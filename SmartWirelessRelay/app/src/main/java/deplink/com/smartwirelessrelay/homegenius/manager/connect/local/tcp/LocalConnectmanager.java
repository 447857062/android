package deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
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
import deplink.com.smartwirelessrelay.homegenius.manager.netStatus.NetStatuChangeReceiver;

/**
 * Created by Administrator on 2017/11/7.
 * 本地连接管理
 * 1.监听网络状态NetStatuChangeReceiver.onNetStatuschangeListener,只有wifi状态才能建立连接
 * 2.建立长连接后需要启动模拟心跳的线程.
 * 3.本地连接需要在udp广播后获取到连接ip地址后才能运行
 */
public class LocalConnectmanager implements NetStatuChangeReceiver.onNetStatuschangeListener {

    private static final String TAG = "LocalConnectmanager";
    /**
     * 这个类设计成单例
     */
    private static LocalConnectmanager instance;
    private LocalConnecteDataListener mLocalConnecteDataListener;
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
    public int InitLocalConnectManager(Context context, LocalConnecteDataListener listener) {
        this.mContext = context;
        this.mLocalConnecteDataListener = listener;
        if (listener == null) {
            Log.e(TAG, "没有设置回调 SDK 会出现异常,这里必须设置数据结果回调");
        }
        initConnectThread();
        initMonitorThread();
        return 0;
    }

    /**
     * 初始化sslsocket连接线程
     */
    private void initConnectThread() {
        if (connectThread != null) {
            connectThread.run();
        } else {
            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sslSocket();
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
    public void sslSocket() {
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
                    mLocalConnecteDataListener.handshakeCompleted();
                }
            });
           //TODO 获取连接的ip地址，进行本地连接
           /* if (ipAddress != null && !ipAddress.equals("")) {
                address = new InetSocketAddress(ipAddress, AppConstant.TCP_CONNECT_PORT);
            }
            sslSocket.connect(address, AppConstant.SERVER_CONNECT_TIMEOUT);
            Log.e(TAG, "创建sslsocket success" + address.toString());*/
        } catch (Exception e) {
            //TODO 获取连接异常的ip地址
            // Log.i(TAG,"创建socket失败, ip="+ipAddress);
            e.printStackTrace();
        }

    }

    @Override
    public void onNetStatuChange(int netStatu) {
        //TODO
        //wifi没有连接的时候断开socket
        //wifi连接的时候重新连接socket
    }
}
