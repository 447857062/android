package deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.ExperienceCenterDevice;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.GetwayDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.SmartLockActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.adapter.ExperienceCenterListAdapter;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;

public class ExperienceDevicesActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "EDActivity";
    private ListView listview_experience_center;
    private List<ExperienceCenterDevice> mExperienceCenterDevices;
    private ExperienceCenterListAdapter mAdapter;

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
    }

    private void initDatas() {
        mExperienceCenterDevices = new ArrayList<>();
        ExperienceCenterDevice device = new ExperienceCenterDevice();
        device.setDeviceName(AppConstant.DEVICES.TYPE_SMART_GETWAY);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(AppConstant.DEVICES.TYPE_ROUTER);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(AppConstant.DEVICES.TYPE_LOCK);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(AppConstant.DEVICES.TYPE_MENLING);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(AppConstant.DEVICES.TYPE_SWITCH);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(AppConstant.DEVICES.TYPE_REMOTECONTROL);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(AppConstant.DEVICES.TYPE_TV_REMOTECONTROL);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(AppConstant.DEVICES.TYPE_AIR_REMOTECONTROL);
        device.setOnline(true);
        mExperienceCenterDevices.add(device);
        device = new ExperienceCenterDevice();
        device.setDeviceName(AppConstant.DEVICES.TYPE_TVBOX_REMOTECONTROL);
        device.setOnline(true);
        mAdapter = new ExperienceCenterListAdapter(this, mExperienceCenterDevices);
    }

    private void initViews() {
        listview_experience_center = (ListView) findViewById(R.id.listview_experience_center);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "position=" + position);
        switch (position) {
            case 0:
                Intent intentGetwayDevice = new Intent(ExperienceDevicesActivity.this, GetwayDeviceActivity.class);
                intentGetwayDevice.putExtra("isStartFromExperience",true);
                startActivity(intentGetwayDevice);
                break;
            case 2:
                Intent intent = new Intent(this, SmartLockActivity.class);
                intent.putExtra("isStartFromExperience",true);
                startActivity(intent);
                break;
        }
    }
}
