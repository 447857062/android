package deplink.com.smartwirelessrelay.homegenius.activity.device.getway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;

public class GetwayDeviceActivity extends Activity implements View.OnClickListener, DeviceListener {
    private Button button_delete_device;
    private DeviceManager mDeviceManager;
    private boolean isStartFromExperience;
    private RelativeLayout layout_config_wifi_getway;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getway_device);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        isStartFromExperience = getIntent().getBooleanExtra("isStartFromExperience", false);
        if (isStartFromExperience) {

        } else {
            mDeviceManager = DeviceManager.getInstance();
            mDeviceManager.InitDeviceManager(this, this);
        }

    }

    private void initEvents() {
        button_delete_device.setOnClickListener(this);
        layout_config_wifi_getway.setOnClickListener(this);
    }

    private void initViews() {
        button_delete_device = (Button) findViewById(R.id.button_delete_device);
        layout_config_wifi_getway = (RelativeLayout) findViewById(R.id.layout_config_wifi_getway);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_delete_device:
                //删除设备
                if (isStartFromExperience) {
                    Toast.makeText(this, "删除网关设备成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, DevicesActivity.class));
                } else {
                    mDeviceManager.deleteGetwayDevice();
                }

                break;
            case R.id.layout_config_wifi_getway:
                startActivity(new Intent(this, GetwayCheckActivity.class));
                break;
        }
    }

    @Override
    public void responseQueryResult(String result) {

    }

    private static final int MSG_HANDLE_DELETE_DEVICE_RESULT = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HANDLE_DELETE_DEVICE_RESULT:
                    mDeviceManager.deleteDBGetwayDevice(mDeviceManager.getCurrentSelectGetwayDevice().getUid());
                    Toast.makeText(GetwayDeviceActivity.this, "删除设备成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GetwayDeviceActivity.this, DevicesActivity.class));
                    break;
            }
        }
    };

    @Override
    public void responseBindDeviceResult(String result) {
        boolean deleteSuccess = true;
        Gson gson = new Gson();
        DeviceList mDeviceList = gson.fromJson(result, DeviceList.class);
        for (int i = 0; i < mDeviceList.getDevice().size(); i++) {
            if (mDeviceList.getDevice().get(i).getUid().equals(mDeviceManager.getCurrentSelectGetwayDevice().getUid())) {
                deleteSuccess = false;
            }
        }
        if (deleteSuccess) {
            mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_RESULT);
        }
    }

    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {

    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }
}
