package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import deplink.com.smartwirelessrelay.homegenius.Devices.EllESDK;
import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.LockHistorys;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Record;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.RecordListAdapter;
import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;
import deplink.com.smartwirelessrelay.homegenius.util.SharedPreference;

public class LockHistory extends Activity {
    private static final String TAG = "LockHistory";
    private ListView dev_list;
    private List<Record> mRecordList;
    private RecordListAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_history);
        initViews();
        initData();
    }

    private void initData() {
        mRecordList = new ArrayList<>();
        recordAdapter = new RecordListAdapter(this, mRecordList);
        dev_list.setAdapter(recordAdapter);
    }

    private void initViews() {
        dev_list = (ListView) findViewById(R.id.list_lock_histroy);

    }


    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {

            @Override
            public void run() {

                //查询设备
                GeneralPacket packet = new GeneralPacket(LockHistory.this);
                //探测设备
                QueryOptions queryCmd = new QueryOptions();
                queryCmd.setOP("QUERY");
                queryCmd.setMethod("SmartLock-HisRecord");
                String DevUid;
                SharedPreference sharedPreference = new SharedPreference(LockHistory.this, "smartuid");
                DevUid = sharedPreference.getString("smartuid");
                if (DevUid != null && !DevUid.equals("")) {
                    queryCmd.setSmartUid(DevUid);
                }
                Gson gson = new Gson();
                String text = gson.toJson(queryCmd);
                packet.packQueryRecordListData(null, text.getBytes());
                if (Client_sslSocket != null) {
                } else {
                    EllESDK.getInstance().InitEllESDK(LockHistory.this, null);
                    Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                }
                EllESDK.getInstance().getOut(packet.data);
               // getOut(Client_sslSocket, packet.data);
                getIn(Client_sslSocket);
            }
        }).start();
    }

    private SSLSocket Client_sslSocket = null;
    String PASSWORD_FOR_PKCS12 = "1234567890";
    // private static final int SERVER_PORT = 443;//端口号
    private static final int SERVER_PORT = 9999;//端口号
    private static final String SERVER_IP = "192.168.68.205";//连接IP
    private static final String ENCONDING = "utf-8";//字符集
    private boolean close = false; // 关闭连接标志位，true表示关闭，false表示开启

   /* public void sslSocket2() {
        if (null != Client_sslSocket) {
            Log.d("init_sslSocket", "ssl socket already exists");
            return;
        }
        try {
            // Loading CAs from an InputStream
            CertificateFactory cf = null;
            cf = CertificateFactory.getInstance("X.509");

            final X509Certificate server_ca;
            InputStream cert = this.getResources().openRawResource(R.raw.server);
            server_ca = (X509Certificate) cf.generateCertificate(cert);
            cert.close();

            // Creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca-certificate", server_ca);

            InputStream pkcs12in = this.getResources().openRawResource(R.raw.client);
            KeyStore pKeyStore = KeyStore.getInstance("PKCS12");
            pKeyStore.load(pkcs12in, PASSWORD_FOR_PKCS12.toCharArray());

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
            Client_sslSocket = (SSLSocket) sslContext.getSocketFactory().createSocket(SERVER_IP, SERVER_PORT);
            //
            Log.e("init_sslSocket", "send hello");

            //查询设备
            GeneralPacket packet = new GeneralPacket(this);
            //探测设备
            QueryOptions queryCmd = new QueryOptions();
            queryCmd.setOP("QUERY");
            queryCmd.setMethod("SmartLock-HisRecord");
            String DevUid;
            SharedPreference sharedPreference = new SharedPreference(LockHistory.this, "smartuid");
            DevUid = sharedPreference.getString("smartuid");
            if (DevUid != null && !DevUid.equals("")) {
                queryCmd.setSmartUid(DevUid);
            }
            Gson gson = new Gson();
            String text = gson.toJson(queryCmd);
            packet.packQueryRecordListData(null, text.getBytes());
            getOut(Client_sslSocket, packet.data);

        } catch (Exception e) {
            //   Log.e("init_sslSocket", e.getMessage());
            e.printStackTrace();
        }

    }
*/
    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }
    public void getOut(SSLSocket socket, byte[] message) {
        OutputStream out;
        Log.i(TAG, "message=" + DataExchange.byteArrayToHexString(message));
        try {
            out = socket.getOutputStream();
            out.write(message);
            Log.e("kelijun", "getOut sent: alert");
            out.flush();
            // out.println(Arrays.toString(message));
            // out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            Gson gson = new Gson();
            LockHistorys aDeviceList = gson.fromJson(str, LockHistorys.class);
            mRecordList.clear();
            if(aDeviceList.getRecord()!=null){
                mRecordList.addAll(aDeviceList.getRecord());
                recordAdapter.notifyDataSetChanged();
            }

            Log.i(TAG, "mDeviceList.getDevice().size=" + aDeviceList.getRecord().size());
            try {
                new AlertDialog
                        .Builder(LockHistory.this)
                        .setTitle("设备")
                        .setNegativeButton("确定", null)
                        .setIcon(android.R.drawable.ic_menu_agenda)
                        .setMessage(str)
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void getIn(SSLSocket socket) {
        String str;
        if (null != Client_sslSocket) {
            try {
                InputStream input = socket.getInputStream();
                byte[] buf = new byte[1024];
                int len = input.read(buf);
                if (len != -1 && len > 84) {
                    byte[] lengthByte = new byte[2];
                    System.arraycopy(buf, 78, lengthByte, 0, 2);
                    int length = DataExchange.bytesToInt(lengthByte, 0, 2);
                    System.out.println("received:" + DataExchange.byteArrayToHexString(buf) + "length=" + length);
                    System.out.println("received:" + "length=" + length);
                    str = new String(buf, 84, length);
                    System.out.println("received:" + str);
                    Message msg = Message.obtain();
                    msg.obj = str;
                    if(str.contains("SmartLock-HisRecord")){
                        mHandler.sendMessage(msg);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }


    }


}
