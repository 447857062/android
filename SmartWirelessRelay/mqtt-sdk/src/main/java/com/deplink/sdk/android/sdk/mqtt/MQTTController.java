package com.deplink.sdk.android.sdk.mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.MqttAction;
import com.deplink.sdk.android.sdk.MqttConfig;
import com.deplink.sdk.android.sdk.bean.UserSession;
import com.deplink.sdk.android.sdk.interfaces.MqttListener;
import com.deplink.sdk.android.sdk.interfaces.SDKCoordinator;
import com.deplink.sdk.android.sdk.mqtt.service.MqttClientAndroidService;
import com.deplink.sdk.android.sdk.rest.RestfulTools;
import com.deplink.sdk.android.sdk.utlis.SslUtil;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huqs on 2016/6/29.
 */
public class MQTTController implements MqttListener {
    private static final String TAG = "MQTTController";
    private volatile static MQTTController singleton;
    private ConnectionMonitor monitor = new ConnectionMonitor();
    private NetBroadCast broadCast = null;
    private Context mContext = null;
    private MqttConfig mConfig = null;
    private Connection mConnection = null;
    private SDKCoordinator mSDKCoordinator = null;
    LinkedHashMap<String, MqttListener> mTopicListeners = new LinkedHashMap<>();

    private MQTTController() {

    }
    public void init(Context context, MqttConfig config, SDKCoordinator coordinator) {
        if (config == null || context == null || coordinator == null) {
            throw new NullPointerException("Invalid params");
        }
        mConfig = config;
        mSDKCoordinator = coordinator;
        if (mContext != context) {
            if (null != mContext) {
                mContext.unregisterReceiver(broadCast);
            }
            broadCast = new NetBroadCast();
            IntentFilter filter = new IntentFilter();
            filter.addAction(broadCast.ACTION);
            context.registerReceiver(broadCast, filter);
            mContext = context;
        }
    }
    public static MQTTController getSingleton() {
        if (singleton == null) {
            synchronized (MQTTController.class) {
                if (singleton == null) {
                    singleton = new MQTTController();
                }
            }
        }
        return singleton;
    }

    public void connect() {
        //如果当前没有连接服务器,开启新的连接，如果已经有连接到服务器（有正在连接中或者已经断开连接两种情况）
        if (mConnection == null || mConnection.getClient() == null) {
            this.connectAction(mContext, mConfig);
        } else {
            this.executionReconnect(mConnection.handle());
        }
    }

    public void disconnect() {
        if (mConnection != null && mConnection.getClient() != null) {
            this.executionDisconnect(mConnection.handle());
            mConnection = null;
        }
    }

    public void onDestroy() {
        if (null != mContext) {
            mContext.unregisterReceiver(broadCast);
            mContext = null;
        }
        if (null != mConnection && null != mConnection.getClient()) {
            mConnection.getClient().stopService();
            mConnection = null;
        }
        monitor.stopTimer();
    }

    /**
     * Subscribe to a topic that the user has specified
     */
    public void subscribe(String topic, MqttListener listener) {
        if (null == mConnection) return;
        try {
            String[] topics = new String[1];
            topics[0] = topic;

            if (listener != null) {
                mTopicListeners.put(topic, listener);
            }
            Connections.getInstance().getConnection(mConnection.handle()).getClient()
                    .subscribe(topic, 0, null, new ActionListener(MqttAction.SUBSCRIBE, mConnection.handle(), this, topic));
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + mConnection.handle(), e);
        }
    }

    public void unsubscribe(String topic) {
        if (null == mConnection) return;
        try {
            mTopicListeners.remove(topic);
            Connections.getInstance().getConnection(mConnection.handle()).getClient()
                    .unsubscribe(topic);
        } catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to unsubscribe to" + topic + " the client with the handle " + mConnection.handle(), e);
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + mConnection.handle(), e);
        }
    }

    /**
     * Publish the message the user has specified
     */
    public void publish(String topic, String message) {
        if (null == mConnection) return;
        int qos = ActivityConstants.defaultQos;
        String[] args = new String[2];
        args[0] = message;
        args[1] = topic;
        try {
            Connections.getInstance().getConnection(mConnection.handle()).getClient().publish(topic, message.getBytes(), qos, false, null, new ActionListener(MqttAction.PUBLISH, mConnection.handle(), this, topic));
        } catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle " + mConnection.handle(), e);
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + mConnection.handle(), e);
        }
    }

    /**
     * Publish the message the user has specified
     */
    public void publish(String topic, String message, IMqttActionListener listener) {
        if (null == mConnection) return;
        int qos = ActivityConstants.defaultQos;
        String[] args = new String[2];
        args[0] = message;
        args[1] = topic;
        try {
            Log.i(TAG, "send message=" + message);
            Connections.getInstance().getConnection(mConnection.handle()).getClient().publish(topic, message.getBytes(), qos, false, null, listener);
        } catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to publish a messged from the client with the handle " + mConnection.handle(), e);
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to subscribe to" + topic + " the client with the handle " + mConnection.handle(), e);
        }
    }

    /**
     * Process data from the connect action 封装数据进行MQTT连接操作
     *
     * @param config
     */
    private void connectAction(Context context, MqttConfig config) {
        String uri;
        String clientId = config.getClientid();
        List<String> servers = config.getServers();
        int serverIndex = 0;
        for (; serverIndex < servers.size(); serverIndex++) {
            if (servers.get(serverIndex).endsWith(":8883")) {
                break;
            }
        }
        if (serverIndex >= servers.size()) {
            serverIndex = 0;
        }
        String server = config.getServers().get(serverIndex);
        String username = config.getUsername();
        String password = config.getPassword();
        String host = server.split(":")[0];
        String port = server.split(":")[1];
        if ("8883".equals(port)) {
            uri = "ssl://" + server;
        } else {
            uri = "tcp://" + server;
        }
        MqttClientAndroidService client;
        //创建一个client对象，此时还没有进行MQTT连接
        client = Connections.getInstance().createClient(context, uri, clientId);

        // create a client handle
        String clientHandle = uri + clientId;
        int timeout = 30;
        int keepAlive = 10;
        mConnection = new Connection(clientHandle, clientId, host, context, client);
        Connections.getInstance().addConnection(mConnection);
        // connect client
        String[] actionArgs = new String[1];
        actionArgs[0] = clientId;
        mConnection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);
        //设置MQTT的高级选项
        MqttConnectOptions conOpt = new MqttConnectOptions();
        //设置清理会话
        conOpt.setCleanSession(true);
        //设置连接超时时间
        conOpt.setConnectionTimeout(timeout);
        //设置保持活力的时间
        conOpt.setKeepAliveInterval(keepAlive);
        //设置用户名
        conOpt.setUserName(username);
        //设置用户密码
        conOpt.setPassword(password.toCharArray());
        //设置ssl socket
        if (uri.startsWith("ssl")) {
            String ca = "-----BEGIN CERTIFICATE-----\n" +
                    "MIICrDCCAZQCCQDY8fYhlcIOdDANBgkqhkiG9w0BAQsFADAYMRYwFAYDVQQDDA0q\n" +
                    "LmRlcGxpbmsubmV0MB4XDTE2MDgzMTAzNDgxMVoXDTQ0MDExNzAzNDgxMVowGDEW\n" +
                    "MBQGA1UEAwwNKi5kZXBsaW5rLm5ldDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCC\n" +
                    "AQoCggEBAN3QgEOkr+Ly2aRKVSVd01hJuIZ9FWGLcyO4EnZ9tHbhn8h5q3ilhuzD\n" +
                    "zU3aGdyrUuCNukRcqOgDEnI3iG+hVZxiEdOYkfqqOc/mSjaJX936dTXDn+Lb4rFi\n" +
                    "0QyE0hz5bYolBPs7ZMXMWT+6z/ZnQ3RRt/wL+0TPN31rZ/0w4jb2vLEBTvhP8Y/9\n" +
                    "YMmgC/ngldEtgRaWBvyychhSt8pZx+Caqz5YuZueZ2WTqbdkas8hFh4WLFfDE0ES\n" +
                    "zOb1d0nnbaMJYHhTdiDi2sHz2+gtzeBifHXKOHqCZf6WLO6XoK/a6EI9Aq48FjA3\n" +
                    "S/oQIImQWMaSjVPwyZr86mqD7DjZa6sCAwEAATANBgkqhkiG9w0BAQsFAAOCAQEA\n" +
                    "pId+1vC+KmDHBFpGgMuug+CFRkDJI71M/Y5D4AizIjQkPOwx6l2smUr/6CxScZu8\n" +
                    "Vu56RTG+sevmNbeEenMtbOf9m8oXacbSJi+NHITWL8hhVUbqRWkWaLOaM/Hws3Vp\n" +
                    "UTWhY+h0Xxw9X8+03oNwK4HC4fWLYC3c2W7y0RWPg+tiMAcnt04Lw6leCmeWYZGP\n" +
                    "ntgwuwsOkj/VL4oIJZQ+3BGKKpRDtWAJyK8V3mj4PqmGZGaKzTHQ4+pggHfEA6tU\n" +
                    "3ocri7SWqfH1dhbqnDF8LPRU2psD2WnvpNnKALyH+54jxUlyCNFaaOmqB1v/3G+K\n" +
                    "NHaoWOMlgm60gGbfS1caOA==\n" +
                    "-----END CERTIFICATE-----";
            conOpt.setSocketFactory(SslUtil.getSocketFactory(ca));
        }

        final ActionListener callback = new ActionListener(MqttAction.CONNECT, clientHandle, this, "");

        //设置消息回调
        client.setCallback(new MqttCallbackHandler(context, clientHandle, this));

        mConnection.addConnectionOptions(conOpt);
        try {
            client.connect(conOpt, null, callback);
        } catch (MqttException e) {
            Log.d(DeplinkSDK.SDK_TAG, "client.connect----------------->" + e.getMessage());
            Log.e(this.getClass().getCanonicalName(), "MqttException Occured", e);
        }

        //启动连接监控
        monitor.startTimer();
        Log.d(DeplinkSDK.SDK_TAG, "===>MQTT connect");
    }

    /**
     * Disconnect the client
     * 断开客户端的连接
     */
    private void executionDisconnect(String clientHandle) {
        Connection c = Connections.getInstance().getConnection(clientHandle);
        try {
            c.getClient().disconnect(null, new ActionListener(MqttAction.DISCONNECT, clientHandle, this, ""));
        } catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to disconnect the client with the handle " + clientHandle, e);
            c.addAction("Client failed to disconnect");
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to disconnect the client with the handle " + clientHandle, e);
        }

        //停止连接监控
        monitor.stopTimer();
    }

    /**
     * Reconnect the selected client
     * 重新连接客户端
     */
    private void executionReconnect(String clientHandle) {
        Connections.getInstance().getConnection(clientHandle).changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);
        Connection c = Connections.getInstance().getConnection(clientHandle);
        try {
            c.getClient().connect(c.getConnectionOptions(), null, new ActionListener(MqttAction.RECONNECT, clientHandle, this, ""));
        } catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client with the handle " + clientHandle, e);
            c.addAction("Client failed to connect");
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Failed to reconnect the client with the handle " + clientHandle, e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(DeplinkSDK.SDK_TAG, "MQTT connection lost, cause: " + cause);
        mSDKCoordinator.MQTTConnectionLost(cause);
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken, MqttAction cation, String clientHandle, String topic) {
        Log.i(DeplinkSDK.SDK_TAG, "onSuccess----action:" + cation);
        switch (cation) {
            case CONNECT:
                mSDKCoordinator.MQTTConnected();
                break;
            case DISCONNECT:
                break;
            case SUBSCRIBE:
                break;
            case PUBLISH:
                break;
            case RECONNECT:
                mSDKCoordinator.MQTTConnected();
                Log.d(DeplinkSDK.SDK_TAG, "onSuccess------恢复连接成功--------action:" + cation);
                break;
        }
    }

    @Override
    public void onFailure(IMqttToken token, Throwable exception, MqttAction cation, String clientHandle, String topic) {
        Log.d(DeplinkSDK.SDK_TAG, "onFailure--------------action:" + cation + "----------" + exception.getMessage());
        switch (cation) {
            case CONNECT://MQTT连接失败
                break;
            case DISCONNECT:
                break;
            case SUBSCRIBE://订阅用户通道失败
                break;
            case PUBLISH:
                break;
            case RECONNECT:
                mSDKCoordinator.MQTTReconnectionFailed();
                break;
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        MqttListener listener = mTopicListeners.get(topic);
        if (listener != null) {
            listener.messageArrived(topic, message);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
    /**
     * 网络切换时立即检查是否需要重连
     */
    class NetBroadCast extends BroadcastReceiver {
        public final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
        private boolean isInit = false;
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION.equals(intent.getAction())) {
                if (isInit) {
                    monitor.checkConnectionHealth();
                }
                isInit = true;
            }
        }
    }

    /**
     * 定时检查连接状态并决定是否需要重连
     */
    class ConnectionMonitor implements Callback<UserSession> {
        private Timer monitorTimer = null;
        private TimerTask monitorTask = null;

        public void startTimer() {
            if (monitorTimer == null) {
                monitorTimer = new Timer();
            }
            if (monitorTask == null) {
                monitorTask = new TimerTask() {
                    @Override
                    public void run() {
                        checkConnectionHealth();
                    }
                };
            }
            if (monitorTimer != null) {
                monitorTimer.schedule(monitorTask, 7000, 5000);
            }
            Log.d(DeplinkSDK.SDK_TAG, "===>monitor started");
        }

        public void stopTimer() {
            if (monitorTimer != null) {
                monitorTimer.cancel();
                monitorTimer = null;
            }
            if (monitorTask != null) {
                monitorTask.cancel();
                monitorTask = null;
            }
            Log.d(DeplinkSDK.SDK_TAG, "===>monitor stopped");
        }

        /**
         * 检查
         */
        public void checkConnectionHealth() {
            if (mConnection != null && mConnection.getClient() != null && mContext != null) {
                if (!isConnected() && isNetworkAvailable(mContext) && isUserSessionExist()) {
                    Log.d(DeplinkSDK.SDK_TAG, "===>check session");
                    RestfulTools.getSingleton().session(this);
                }
            }
        }

        @Override
        public void onResponse(Call<UserSession> call, Response<UserSession> response) {
            switch (response.code()) {
                case 200://token验证成功，可以进行重复连接MQTT操作
                    Log.d(DeplinkSDK.SDK_TAG, "===>connect mqtt after checked session");
                    connect();
                    break;
            }
        }

        @Override
        public void onFailure(Call<UserSession> call, Throwable t) {

        }
    }

    private boolean isNetworkAvailable(Context context) {
        boolean available = false;
        //获取手机的连接服务管理器，这里是连接管理器类
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            for (Network network : networks) {
                NetworkInfo networkInfo = cm.getNetworkInfo(network);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED
                        && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                    available = true;
                    break;
                }
            }
        } else {
            NetworkInfo.State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            NetworkInfo.State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            available = (wifiState == NetworkInfo.State.CONNECTED || mobileState == NetworkInfo.State.CONNECTED);
        }
        return available;
    }

    private boolean isUserSessionExist() {
        return (mSDKCoordinator.getUserSession() != null && mSDKCoordinator.getUserInfo() != null);
    }

    private boolean isConnected() {
        try {
            return (null != mConnection && null != mConnection.getClient() && mConnection.getClient().isConnected());
        } catch (Exception e) {
            Log.e(this.getClass().getCanonicalName(), "Failed calling isConnected", e);
        }

        return false;
    }
}
