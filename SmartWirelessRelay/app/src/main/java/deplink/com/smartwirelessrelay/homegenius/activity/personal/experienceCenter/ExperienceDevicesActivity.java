package deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.ExperienceCenterDevice;
import deplink.com.smartwirelessrelay.homegenius.activity.device.doorbell.DoorbeelMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.GetwayDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.RemoteControlActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl.AirRemoteControlMianActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.topBox.IptvMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.tv.TvMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.RouterMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch.SwitchOneActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.SmartLockActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.adapter.ExperienceCenterListAdapter;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceType;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;

public class ExperienceDevicesActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "EDActivity";
    private ListView listview_experience_center;
    private List<ExperienceCenterDevice> mExperienceCenterDevices;
    private ExperienceCenterListAdapter mAdapter;
    private FrameLayout imageview_back;
    private TextView textview_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_devices);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        listview_experience_center.setAdapter(mAdapter);
        listview_experience_center.setOnItemClickListener(this);
        imageview_back.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("体验中心");
        mExperienceCenterDevices = new ArrayList<>();
        ExperienceCenterDevice device = new ExperienceCenterDevice();
        device.setDeviceName(DeviceType.TYPE.TYPE_SMART_GETWAY);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(DeviceType.TYPE.TYPE_ROUTER);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(DeviceType.TYPE.TYPE_LOCK);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(DeviceType.TYPE.TYPE_MENLING);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(DeviceType.TYPE.TYPE_SWITCH);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(DeviceType.TYPE.TYPE_REMOTECONTROL);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(DeviceType.TYPE.TYPE_TV_REMOTECONTROL);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(DeviceType.TYPE.TYPE_AIR_REMOTECONTROL);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(DeviceType.TYPE.TYPE_TVBOX_REMOTECONTROL);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        mAdapter = new ExperienceCenterListAdapter(this, mExperienceCenterDevices);
    }

    private void initViews() {
        listview_experience_center = (ListView) findViewById(R.id.listview_experience_center);
        imageview_back = (FrameLayout) findViewById(R.id.image_back);
        textview_title = (TextView) findViewById(R.id.textview_title);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        DeviceManager.getInstance().setStartFromExperience(true);
        switch (mExperienceCenterDevices.get(position).getDeviceName()) {

            case DeviceType.TYPE.TYPE_SMART_GETWAY:
                Intent intentGetwayDevice = new Intent(ExperienceDevicesActivity.this, GetwayDeviceActivity.class);
                startActivity(intentGetwayDevice);
                break;
            case DeviceType.TYPE.TYPE_LOCK:
                intent = new Intent(this, SmartLockActivity.class);
                startActivity(intent);
                break;
            case DeviceType.TYPE.TYPE_ROUTER:
                intent = new Intent(this, RouterMainActivity.class);
                startActivity(intent);
                break;
            case DeviceType.TYPE.TYPE_MENLING:
                intent = new Intent(this, DoorbeelMainActivity.class);
                startActivity(intent);
                break;
            case DeviceType.TYPE.TYPE_SWITCH:
                intent = new Intent(this, SwitchOneActivity.class);
                startActivity(intent);
                break;
            case DeviceType.TYPE.TYPE_REMOTECONTROL:
                intent = new Intent(this, RemoteControlActivity.class);
                startActivity(intent);
                break;
            case DeviceType.TYPE.TYPE_TV_REMOTECONTROL:
                intent = new Intent(this, TvMainActivity.class);

                startActivity(intent);
                break;
            case DeviceType.TYPE.TYPE_AIR_REMOTECONTROL:
                intent = new Intent(this, AirRemoteControlMianActivity.class);
                startActivity(intent);
                break;
            case DeviceType.TYPE.TYPE_TVBOX_REMOTECONTROL:
                intent = new Intent(this, IptvMainActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
