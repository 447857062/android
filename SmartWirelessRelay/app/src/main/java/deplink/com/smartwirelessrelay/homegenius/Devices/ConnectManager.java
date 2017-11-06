package deplink.com.smartwirelessrelay.homegenius.Devices;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.UdpNet.UdpPacket;
import deplink.com.smartwirelessrelay.homegenius.util.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;


/**
 * Created by benond on 2017/2/7.
 */

public class ConnectManager {//
    private static final String TAG = "ConnectManager";
    private SDK_Data_Listener dataListener;
    private DevStatus devStatus;
    private UdpPacket udpPacket;
    private static ConnectManager instance;
    private SSLSocket sslSocket = null;
    private String ipAddress;
    private Context mContext;
    private ConnectionMonitor connectionMonitor;
    private Thread connectThread;
    private Thread monitorThread;
    private OutputStream out;
    private ConnectManager() {
    }

    public static synchronized ConnectManager getInstance() {
        if (instance == null) {
            instance = new ConnectManager();
        }
        return instance;
    }

    public SSLSocket getSslSocket() {
        if(sslSocket==null){
            Log.i(TAG,"ConnectManager 还没有获取到socket连接");
        }
        return sslSocket;
    }

    SocketAddress address = null;
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

            sslSocket = (SSLSocket) sslContext.getSocketFactory().createSocket(/*AppConstant.SERVER_IP, AppConstant.TCP_CONNECT_PORT*/);
            sslSocket.setSoTimeout(5000);
            sslSocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                @Override
                public void handshakeCompleted(HandshakeCompletedEvent event) {
                    Log.i(TAG,"ssl握手成功回调");
                }
            });

            if (ipAddress != null && !ipAddress.equals("")) {
                address = new InetSocketAddress(ipAddress, AppConstant.TCP_CONNECT_PORT);
            } else {
                address = new InetSocketAddress(AppConstant.SERVER_IP, AppConstant.TCP_CONNECT_PORT);
            }
            sslSocket.connect(address, AppConstant.SERVER_CONNECT_TIMEOUT);

            Log.e(TAG, "init sslSocket success 创建socket" + address.toString());
        } catch (Exception e) {
            if(ipAddress!=null){
                Log.i(TAG,"连接的ip不可用, ip="+ipAddress);
                dataListener.onConnectDeviceFail(ipAddress);
            }
            e.printStackTrace();
        }

    }



    //初始化SDK
    public int InitConnectManager(Context context, SDK_Data_Listener listener) {
        this.mContext = context;
        this.dataListener = listener;
        if (listener == null) {
            Log.e(TAG, "没有回调 SDK 会出现异常");
        }

        //发送任务队列
        if (udpPacket == null) {
            udpPacket = new UdpPacket(context);
            udpPacket.start();
        }
        //启动状态查询任务
        if (devStatus == null) {
            devStatus = new DevStatus(context, udpPacket);
            devStatus.open();
        }

        return 0;
    }

    //初始化tcp/ip连接，和设备的连接
    public int InitTcpIpConnect(String ipAddress) {
        if (ipAddress != null) {
            this.ipAddress = ipAddress;
        }
        initConnectThread();
        initMonitorThread();
        return 0;
    }



    private void initConnectThread() {
        if(connectThread!=null){
            connectThread.run();
        }else{
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
        }else{
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



    public int getOut(byte[] message) {
        if (sslSocket == null) {
            Log.i(TAG, "socket==null cannot send tcp ip message");
            return -1;
        }
        try {
            Log.e(TAG, "getOut() send start: ");
            out = sslSocket.getOutputStream();
            out.write(message);
            Log.e(TAG, "getOut() send cuccess: " + DataExchange.byteArrayToHexString(message));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(0);
            return -1;
        }
        return 0;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        if (sslSocket != null) {
                            sslSocket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sslSocket = null;//重置socket
                    udpPacket = null;
                }
            }.start();
            Toast.makeText(mContext, "与服务器的连接断开了,重新连接", Toast.LENGTH_SHORT).show();
        }
    };

    public void setElleListener(SDK_Data_Listener elleListener) {
        this.dataListener = elleListener;
    }

    public void getIp(byte[] ip) {
        Log.i(TAG, "SDK 已接收到UDP返回数据 中继器ip地址：" + DataExchange.byteArrayToHexString(ip));
        try {
            dataListener.onRecvDeviceIp(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
