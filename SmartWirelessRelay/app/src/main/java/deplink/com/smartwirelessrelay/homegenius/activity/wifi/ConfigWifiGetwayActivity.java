package deplink.com.smartwirelessrelay.homegenius.activity.wifi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.AP_CLIENT;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;

public class ConfigWifiGetwayActivity extends Activity implements View.OnClickListener,DeviceListener{
    private static final String TAG="ConfigWifiGetwayActivity";
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
}
