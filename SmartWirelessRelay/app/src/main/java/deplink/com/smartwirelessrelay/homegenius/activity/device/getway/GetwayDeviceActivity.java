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

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;

public class GetwayDeviceActivity extends Activity implements View.OnClickListener, GetwayListener {
    private Button button_delete_device;
    private GetwayManager mGetwayManager;
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
            mGetwayManager=GetwayManager.getInstance();
            mGetwayManager.InitGetwayManager(this,this);
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
                    mGetwayManager.deleteGetwayDevice();
                }

                break;
            case R.id.layout_config_wifi_getway:
                startActivity(new Intent(this, GetwayCheckActivity.class));
                break;
        }
    }


    private static final int MSG_HANDLE_DELETE_DEVICE_RESULT = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HANDLE_DELETE_DEVICE_RESULT:
                    mGetwayManager.deleteDBGetwayDevice(mGetwayManager.getCurrentSelectGetwayDevice().getUid());
                    Toast.makeText(GetwayDeviceActivity.this, "删除设备成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GetwayDeviceActivity.this, DevicesActivity.class));
                    break;
            }
        }
    };


    @Override
    public void responseResult(String result) {
        boolean deleteSuccess = true;
        Gson gson = new Gson();
        DeviceList mDeviceList = gson.fromJson(result, DeviceList.class);
        for (int i = 0; i < mDeviceList.getDevice().size(); i++) {
            if (mDeviceList.getDevice().get(i).getUid().equals(mGetwayManager.getCurrentSelectGetwayDevice().getUid())) {
                deleteSuccess = false;
            }
        }
        if (deleteSuccess) {
            mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_RESULT);
        }
    }
}
