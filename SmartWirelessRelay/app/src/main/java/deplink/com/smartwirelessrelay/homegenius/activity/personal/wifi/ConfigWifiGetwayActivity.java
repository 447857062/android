package deplink.com.smartwirelessrelay.homegenius.activity.personal.wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.AP_CLIENT;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.AddGetwaySettingOptionsActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;

public class ConfigWifiGetwayActivity extends Activity implements View.OnClickListener,DeviceListener{
    private static final String TAG="ConfigWifiGetwayActivity";
    private TextView textview_title;
    private ImageView image_back;
    private Button button_connect_right_now;
    private EditText edittext_input_wifi_password;
    private DeviceManager mDeviceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_wifi_getway);
        initViews();
        initDatas();
        initEvents();
    }
    private String setApCliSsid;
    private String setApCliEncrypType;
    private String setApCliAuthMode;
    private String setChannel;
    private void initDatas() {
        textview_title.setText("配置WiFi网关");
        setApCliSsid=getIntent().getStringExtra("setApCliSsid");
        setApCliEncrypType=getIntent().getStringExtra("setApCliEncrypType");
        setApCliAuthMode=getIntent().getStringExtra("setApCliAuthMode");
        setChannel=getIntent().getStringExtra("setChannel");
        mDeviceManager=DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this,this);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_connect_right_now.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (ImageView) findViewById(R.id.image_back);
        button_connect_right_now= (Button) findViewById(R.id.button_connect_right_now);
        edittext_input_wifi_password= (EditText) findViewById(R.id.edittext_input_wifi_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_connect_right_now:
                String password=edittext_input_wifi_password.getText().toString();
                if(password.length()<8){
                    Toast.makeText(this,"wifi密码不能小于8位",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    AP_CLIENT setCmd=new AP_CLIENT();
                    setCmd.setApCliSsid(setApCliSsid);
                    setCmd.setApCliEncrypType(setApCliEncrypType);
                    setCmd.setApCliAuthMode(setApCliAuthMode);
                    setCmd.setChannel(setChannel);
                    setCmd.setApCliWPAPSK(password);
                    mDeviceManager.setWifiRelay(setCmd);

                    Intent intent=new Intent(ConfigWifiGetwayActivity.this,AddGetwaySettingOptionsActivity.class);
                    startActivity(intent);
                }

                break;
        }
    }

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseBindDeviceResult(String result) {

    }

    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {

    }
    private static final int MSG_SET_WIFIRELAY_RESULT=100;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SET_WIFIRELAY_RESULT:
                    Toast.makeText(ConfigWifiGetwayActivity.this,"配置wifi中继返回结果="+msg.arg1,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    public void responseSetWifirelayResult(int result) {
        Message msg=Message.obtain();
        msg.what=MSG_SET_WIFIRELAY_RESULT;
        msg.arg1=result;
        mHandler.sendMessage(msg);
    }
}
