package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;

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



                    }
                }).start();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
