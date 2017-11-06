package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import deplink.com.smartwirelessrelay.homegenius.Devices.ConnectManager;
import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.util.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;

public class SendSmartDevListActivity extends Activity implements View.OnClickListener{
    private Button button_send_smart_dev;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_smart_dev_list);
        initViews();
    }

    private void initViews() {
        button_send_smart_dev= (Button) findViewById(R.id.button_send_smart_dev);
        button_send_smart_dev.setOnClickListener(this);
        packet=new GeneralPacket(this);
    }
    private GeneralPacket packet;
    private SSLSocket Client_sslSocket;
    private boolean isReceiverSendSmartDev;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_send_smart_dev:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        //查询设备
                        //探测设备
                        QueryOptions queryCmd = new QueryOptions();
                        queryCmd.setOP("SET");
                        queryCmd.setMethod("SmartDev-List");
                        List<SmartDev>devs=new ArrayList<>();
                        SmartDev dev=new SmartDev();
                        dev.setDevUid("00-12-4b-00-0b-26-c2-15");
                        dev.setOrg("ismart");
                        devs.add(dev);
                        queryCmd.setSmartDev(devs);

                        Gson gson = new Gson();
                        String text = gson.toJson(queryCmd);
                        packet.packSendSmartDevsData(null, text.getBytes());
                        if (null == Client_sslSocket) {
                            ConnectManager.getInstance().InitConnectManager(SendSmartDevListActivity.this, null);
                            Client_sslSocket = ConnectManager.getInstance().getSslSocket();

                        }
                        Client_sslSocket = ConnectManager.getInstance().getSslSocket();
                        ConnectManager.getInstance().getOut(packet.data);
                        isReceiverSendSmartDev = false;
                       while (!isReceiverSendSmartDev) {
                            getIn(Client_sslSocket);
                        }

                    }
                }).start();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isReceiverSendSmartDev=true;
    }

    private InputStream input;
    public void getIn(SSLSocket socket) {
        String str;
        if (null == Client_sslSocket) {
            ConnectManager.getInstance().InitTcpIpConnect(null);
            Client_sslSocket = ConnectManager.getInstance().getSslSocket();
        }
        try {
            input = socket.getInputStream();
            if (input != null) {
                byte[] buf = new byte[1024];
                int len = input.read(buf);
                if (len != -1 && len > AppConstant.BASICLEGTH) {
                    byte[] lengthByte = new byte[AppConstant.PACKET_DATA_LENGTHS_TAKES];
                    System.arraycopy(buf, AppConstant.PACKET_DATA_LENGTH_START_INDEX, lengthByte, 0, 2);
                    int length = DataExchange.bytesToInt(lengthByte, 0, 2);
                    System.out.println("received:" + DataExchange.byteArrayToHexString(buf) + "length=" + length);
                    str = new String(buf, AppConstant.BASICLEGTH, length);
                    System.out.println("received:" + str);
                    Message msg = Message.obtain();
                    msg.obj = str;
                    isReceiverSendSmartDev = true;
                    if (str.contains("SmartLock-HisRecord")) {
                       // msg.what = MSG_GET_HISTORYRECORD;
                    } else if (str.contains("Result")) {
                      //  msg.what = MSG_RETURN_ERROR;
                    }
                  //  mHandler.sendMessage(msg);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
