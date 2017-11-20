package deplink.com.smartwirelessrelay.homegenius.activity.smartlock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;

public class EditSmartLockActivity extends Activity implements View.OnClickListener,DeviceListener{

    private ImageView image_back;
    private Button button_delete_device;
    private DeviceManager mDeviceManager;
    private TextView textview_edit_complement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_smart_lock);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {

        image_back.setOnClickListener(this);
        button_delete_device.setOnClickListener(this);
        textview_edit_complement.setOnClickListener(this);
    }

    private void initDatas() {
        mDeviceManager=DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this,this);
    }

    private void initViews() {

        image_back= (ImageView) findViewById(R.id.image_back);
        button_delete_device= (Button) findViewById(R.id.button_delete_device);
        textview_edit_complement= (TextView) findViewById(R.id.textview_edit_complement);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview_edit_complement:
                onBackPressed();
                break;
            case R.id.button_delete_device:
                //TODO 设备的解除绑定
                //查找设备
                mDeviceManager.deleteDevice();
                break;
            case R.id.image_back:
                onBackPressed();
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
