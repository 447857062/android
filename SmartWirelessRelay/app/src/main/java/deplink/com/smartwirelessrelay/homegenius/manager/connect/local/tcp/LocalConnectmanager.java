package deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import deplink.com.smartwirelessrelay.homegenius.Devices.ConnectionMonitor;
import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.constant.ComandID;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.UdpManager;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.UdpManagerGetIPLintener;
import deplink.com.smartwirelessrelay.homegenius.manager.netStatus.NetStatuChangeReceiver;
import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;

/**
 * Created by Administrator on 2017/11/7.
 * 本地连接管理
 * 1.监听网络状态NetStatuChangeReceiver.onNetStatuschangeListener,只有wifi状态才能建立连接
 * 2.建立长连接后需要启动模拟心跳的线程.
 * 3.本地连接需要在udp广播后获取到连接ip地址后才能运行
 */
public class LocalConnectmanager implements NetStatuChangeReceiver.onNetStatuschangeListener, UdpManagerGetIPLintener {

    private static final String TAG = "LocalConnectmanager";
    /**
     * 这个类设计成单例
     */
    private static LocalConnectmanager instance;
    private LocalConnecteListener mLocalConnecteListener;
    private Context mContext;
    /**
     * 连接线程
     */
    private Thread connectThread;
    /**
     * 心跳包线程
     */
    private Thread monitorThread;
    /**
     * 模拟发送心跳包
     */
    private ConnectionMonitor connectionMonitor;
    /**
     * sslsocket套接字
     */
    private SSLSocket sslSocket;
    private UdpManager mUdpmanager;
    private InetSocketAddress address;
    private NetStatuChangeReceiver mNetStatuChangeReceiver;

    private LocalConnectmanager() {
    }

    public static synchronized LocalConnectmanager getInstance() {
        if (instance == null) {
            instance = new LocalConnectmanager();
        }
        return instance;
    }

    /**
     * 初始化本地连接管理器
     */
    public int InitLocalConnectManager(Context context, LocalConnecteListener listener) {
        this.mContext = context;
        this.mLocalConnecteListener = listener;
        initRegisterNetChangeReceive();
        mUdpmanager = UdpManager.getInstance();
        mUdpmanager.InitUdpConnect(context, this);
        if (listener == null) {
            Log.e(TAG, "LocalConnectManager 没有设置回调 SDK 会出现异常,这里必须设置数据结果回调");
        }
        return 0;
    }

    private void initRegisterNetChangeReceive() {
        mNetStatuChangeReceiver = new NetStatuChangeReceiver();
        mNetStatuChangeReceiver.setmOnNetStatuschangeListener(this);
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        mContext.registerReceiver(mNetStatuChangeReceiver, filter);
    }

    /**
     * 初始化本地连接管理器
     */
    public int InitConnect(String ipAddress) {
        initConnectThread(ipAddress);
        initMonitorThread();
        return 0;
    }

    /**
     * 初始化sslsocket连接线程
     */
    private void initConnectThread(final String ipAddress) {
        if (connectThread != null) {
            connectThread.run();
        } else {
            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sslSocket(ipAddress);
                }
            });
            connectThread.start();
        }

    }

    /**
     * 初始化发送心跳包的任务
     */
    private void initMonitorThread() {
        if (null != monitorThread) {
            monitorThread.run();
        } else {
            monitorThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    connectionMonitor = new ConnectionMonitor(mContext);
                    connectionMonitor.startTimer();
                }
            });
            monitorThread.start();
        }


    }

    /**
     * 初始化sslsocket
     */
    public void sslSocket(String ipAddress) {
        if (sslSocket != null) {
            try {
                sslSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sslSocket = null;
        }

        try {
            // Loading CAs from an InputStream
            CertificateFactory cf;
            cf = CertificateFactory.getInstance("X.509");

            final X509Certificate server_ca;
            InputStream cert = mContext.getResources().openRawResource(R.raw.server);
            server_ca = (X509Certificate) cf.generateCertificate(cert);
            cert.close();

            // Creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca-certificate", server_ca);

            InputStream pkcs12in = mContext.getResources().openRawResource(R.raw.client);
            KeyStore pKeyStore = KeyStore.getInstance("PKCS12");
            pKeyStore.load(pkcs12in, AppConstant.PASSWORD_FOR_PKCS12.toCharArray());

            // Creating a TrustManager that trusts the CAs in our KeyStore.
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(pKeyStore, null);
            pkcs12in.close();

            // Creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            sslSocket = (SSLSocket) sslContext.getSocketFactory().createSocket();
            sslSocket.setSoTimeout(AppConstant.LOCAL_SERVER_SOCKET_TIMEOUT);
            sslSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                @Override
                public void handshakeCompleted(HandshakeCompletedEvent event) {
                    Log.i(TAG, "ssl握手成功回调");
                    handshakeCompleted = true;
                    mLocalConnecteListener.handshakeCompleted();

                }
            });
            address = new InetSocketAddress(ipAddress, AppConstant.TCP_CONNECT_PORT);
            sslSocket.connect(address, AppConstant.SERVER_CONNECT_TIMEOUT);

            Log.e(TAG, "创建sslsocket success" + address.toString());
           /* GeneralPacket packet = new GeneralPacket(mContext);
            packet.packHeathPacket();
            getOut(packet.data);*/
            while (true){
                getIn();
            }
        } catch (Exception e) {
            //TODO 获取连接异常的ip地址
            handshakeCompleted = false;
            mLocalConnecteListener.createSocketFailed(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 已连接网关
     */
    private boolean handshakeCompleted;
    private OutputStream out;

    /**
     * tcp发送数据
     *
     * @param message
     * @return
     */
    public int getOut(byte[] message) {
        if (sslSocket == null) {
            Log.i(TAG, "socket==null cannot send tcp ip message");
            mLocalConnecteListener.OnFailedgetLocalGW("未连接本地网关");
            return -1;
        }
        Log.i(TAG, "getout HandshakeCompleted=" + handshakeCompleted);
        /*if (!handshakeCompleted) {
            return -1;
        }*/
        try {
            Log.e(TAG, "getOut() send start: ");
            out = sslSocket.getOutputStream();
            out.write(message);
            Log.e(TAG, "getOut() send cuccess: " + DataExchange.byteArrayToHexString(message));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * 获取tcp/ip连接的数据
     */
    public String getIn() {
        if (sslSocket == null) {
            Log.i(TAG, "getIn() socket==null cannot receive message");
            mLocalConnecteListener.OnFailedgetLocalGW("未连接本地网关");
        }
        String str = null;
        try {
            InputStream input = sslSocket.getInputStream();
            byte[] buf = new byte[1024];
            int len = input.read(buf);
            if (len != -1) {
                //读取cmd参数
                int cmd = DataExchange.bytesToInt(buf, 6, 1);

                str = new String(buf, 0, len);
                Log.i(TAG, "cmd=" + cmd);
                System.out.println("received:" + str + "length=" + len);
                System.out.println("received:" + DataExchange.byteArrayToHexString(buf));
                switch (cmd) {
                    case ComandID.CMD_BIND_RESPONSE:
                        byte[] uid = new byte[32];
                        System.arraycopy(buf, 7, uid, 0, 32);
                        str = new String(uid);
                        mLocalConnecteListener.OnGetUid(str);
                        break;
                }
                //获取到数据
                //TODO
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    //接收回调数据区域
    @Override
    public void onNetStatuChange(int netStatu) {
        //TODO
        //wifi没有连接的时候断开socket
        //wifi连接的时候重新连接socket
        Log.i(TAG, "Net status cahnge new Statu=" + netStatu);
    }

    @Override
    public void onGetLocalConnectIp(String ipAddress) {
        InitConnect(ipAddress);
    }
}
