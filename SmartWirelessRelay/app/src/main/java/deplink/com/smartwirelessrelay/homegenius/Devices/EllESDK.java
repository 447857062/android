package deplink.com.smartwirelessrelay.homegenius.Devices;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.BasicPacket;
import deplink.com.smartwirelessrelay.homegenius.Protocol.UdpNet.UdpPacket;
import deplink.com.smartwirelessrelay.homegenius.util.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;


/**
 * Created by benond on 2017/2/7.
 */

public class EllESDK {//
    private static final String TAG = "EllESDK";
    private static EllE_Listener elleListener;

    private DevStatus devStatus;

    private UdpPacket udpPacket;

    private static EllESDK instance;
    private SSLSocket Client_sslSocket = null;

    private EllESDK() {
    }

    public static synchronized EllESDK getInstance() {
        if (instance == null) {
            instance = new EllESDK();
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
            Log.d("init_sslSocket", "ssl socket already exists");
            return;
        }
        try {
            // Loading CAs from an InputStream
            CertificateFactory cf = null;
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
            Client_sslSocket = (SSLSocket) sslContext.getSocketFactory().createSocket(AppConstant.SERVER_IP, AppConstant.TCP_CONNECT_PORT);
            Client_sslSocket.setKeepAlive(true);

            //  Client_sslSocket.
            //  Client_sslSocket.setSoTimeout(5000);
            Log.e("init_sslSocket", "init_sslSocket ");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Context mContext;
    private ConnectionMonitor connectionMonitor;

    //初始化SDK
    public int InitEllESDK(Context context, EllE_Listener listener) {
        this.mContext = context;
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
        initConnectThread();
        initMonitorThread();
        elleListener = listener;
        if (listener == null)
            Log.e("info", "没有回调 SDK 会出现异常");
        return 0;
    }



    private Thread connectThread;
    private void initConnectThread() {
        if(null==connectThread){
            connectThread= new Thread(new Runnable() {
                @Override
                public void run() {
                    sslSocket2();
                }
            });
            connectThread.start();
        }else{
            connectThread.run();
        }

    }
    private Thread monitorThread;
    private void initMonitorThread() {
        if ( null==monitorThread) {
            monitorThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    connectionMonitor = new ConnectionMonitor(mContext);
                    connectionMonitor.startTimer();
                }
            });
            monitorThread.start();
        }else{
            monitorThread.run();
        }


    }

    public int getOut(byte[] message) {
        if (Client_sslSocket == null) {
            Log.i("kelijun", "cannot send tcp ip message");
            return -1;
        }
      //  if (!connectionMonitor.getServerClose()) {
            try {
                OutputStream out;
                out = Client_sslSocket.getOutputStream();
                out.write(message);
                Log.e(TAG, "ELLESDK sent: "+ DataExchange.byteArrayToHexString(message));
                out.flush();
                out.close();
            } catch (IOException e) {

                e.printStackTrace();
                return -1;
            }
      //  }

        return 0;
    }

    //  private InputStream input;
 /*   public String getIn() {
        String str;
        if (null != Client_sslSocket) {
            try {
                InputStream input = Client_sslSocket.getInputStream();
                byte[] buf = new byte[1024];
                int len = input.read(buf);
                if (len != -1) {
                    str = new String(buf, 0, len);
                    System.out.println("received:" + str + "length=" + len);
                    System.out.println("received:" + DataExchange.byteArrayToHexString(buf));
                    byte[] uid = new byte[32];
                    System.arraycopy(buf, 7, uid, 0, 32);
                    str = new String(uid);
                    Message msg = Message.obtain();
                    msg.obj = str;
                    input.close();
                    //  mHandler.sendMessage(msg);
                    return str;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }*/
    public void setElleListener(EllE_Listener elleListener) {
        EllESDK.elleListener = elleListener;
    }


    public static void getNewPacket(BasicPacket packet) {
        elleListener.onRecvEllEPacket(packet);
    }

    public static void getIp(byte[] ip) {
        //  elleListener.onRecvEllEPacket(packet);
    }

}
