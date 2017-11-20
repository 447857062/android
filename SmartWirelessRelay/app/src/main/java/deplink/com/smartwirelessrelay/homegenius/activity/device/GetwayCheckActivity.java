package deplink.com.smartwirelessrelay.homegenius.activity.device;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.UdpManager;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.UdpManagerGetIPLintener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;

public class GetwayCheckActivity extends Activity implements View.OnClickListener,UdpManagerGetIPLintener{
    private static final String TAG="GetwayCheckActivity";
    private Button button_next_step;
    private ImageView image_back;

    private UdpManager mUdpmanager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getway_check);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        mUdpmanager = UdpManager.getInstance();
        mUdpmanager.InitUdpConnect(this, this);
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this, null);
        currentAddDevice = getIntent().getStringExtra("currentAddDevice");
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_next_step.setOnClickListener(this);
    }

    private void initViews() {
        image_back= (ImageView) findViewById(R.id.image_back);
        button_next_step= (Button) findViewById(R.id.button_next_step);
    }
    private DeviceManager mDeviceManager;
    private String currentAddDevice;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }


    @Override
    public void onGetLocalConnectIp(String ipAddress) {
        Log.i(TAG,"检查网关，获取到IP地址="+ipAddress);
        mDeviceManager.bindDevice(currentAddDevice);
    }
}
