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
    private static final String TAG = "EllESDK";
    private static SDK_Data_Listener elleListener;
    private DevStatus devStatus;
    private UdpPacket udpPacket;
    private static ConnectManager instance;
    private SSLSocket Client_sslSocket = null;
    private ConnectManager() {
    }

    public static synchronized ConnectManager getInstance() {
        if (instance == null) {
            instance = new ConnectManager();
        }
        return instance;
    }

    public SSLSocket getClient_sslSocket() {
        return Client_sslSocket;
    }

    public void setClient_sslSocket(SSLSocket client_sslSocket) {
        Client_sslSocket = client_sslSocket;
    }

    public void sslSocket2() {
        if (null != Client_sslSocket) {
            Log.i(TAG, "ssl socket already exists");
            return;
        }
        SocketAddress address = null;
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

            Client_sslSocket = (SSLSocket) sslContext.getSocketFactory().createSocket(/*AppConstant.SERVER_IP, AppConstant.TCP_CONNECT_PORT*/);
            Client_sslSocket.setKeepAlive(true);

            if(ipAddress!=null && !ipAddress.equals("")){
                address  = new InetSocketAddress(ipAddress, AppConstant.TCP_CONNECT_PORT);
            }else{
                address  = new InetSocketAddress(AppConstant.SERVER_IP, AppConstant.TCP_CONNECT_PORT);
            }
            Client_sslSocket.connect(address, AppConstant.SERVER_CONNECT_TIMEOUT);
            Log.e(TAG, "init_sslSocket success 创建socket"+address.toString());
        } catch (Exception e) {
            elleListener.onConnectDeviceFail(address.toString());
            e.printStackTrace();
        }

    }
    private String ipAddress;
    private Context mContext;
    private ConnectionMonitor connectionMonitor;

    //初始化SDK
    public int InitEllESDK(Context context, SDK_Data_Listener listener) {
        this.mContext = context;
        this.elleListener = listener;
        if (listener == null){
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
        if(ipAddress!=null){
            this.ipAddress=ipAddress;
        }
        initConnectThread();
        initMonitorThread();
        return 0;
    }

    private Thread connectThread;
    private void initConnectThread() {
        //if (null == connectThread) {
            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sslSocket2();
                }
            });
            connectThread.start();
      //  }

    }
    private Thread monitorThread;
    private void initMonitorThread() {
        if (null == monitorThread) {
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
    private OutputStream out;
    public int getOut(byte[] message) {
        if (Client_sslSocket == null) {
            Log.i("kelijun", "cannot send tcp ip message");
            return -1;
        }
        try {
            Log.e(TAG, "getOut() send start: ");
            out = Client_sslSocket.getOutputStream();
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
            try {
                Client_sslSocket.close();
                Client_sslSocket=null;//重置socket
            } catch (IOException e) {
                e.printStackTrace();
            }
            udpPacket=null;
            Toast.makeText(mContext, "与服务器的连接断开了,重新连接", Toast.LENGTH_SHORT).show();
        }
    };
    public void setElleListener(SDK_Data_Listener elleListener) {
        ConnectManager.elleListener = elleListener;
    }
    public   void getIp(byte[] ip) {
        Log.i(TAG,"EllESDK 已接收到UDP返回数据 中继器ip地址："+DataExchange.byteArrayToHexString(ip));
        try {
            elleListener.onRecvDeviceIp(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
