package deplink.com.smartwirelessrelay.homegenius.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.GetwayCheckActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.SmartHomeMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.wifi.ScanWifiListActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.RoomActivity;
import deplink.com.smartwirelessrelay.homegenius.application.AppManager;

public class PersonalCenterActivity extends Activity implements View.OnClickListener{
    private RelativeLayout layout_getway_check;
    private RelativeLayout layout_config_wifi_getway;
    private RelativeLayout layout_experience_center;
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        initViews();
        initEvents();
    }

    private void initEvents() {
        AppManager.getAppManager().addActivity(this);
        layout_getway_check.setOnClickListener(this);
        layout_config_wifi_getway.setOnClickListener(this);
        layout_experience_center.setOnClickListener(this);
        layout_home_page.setOnClickListener(this);
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
    }

    private void initViews() {
        layout_getway_check= (RelativeLayout) findViewById(R.id.layout_getway_check);
        layout_config_wifi_getway= (RelativeLayout) findViewById(R.id.layout_config_wifi_getway);
        layout_experience_center= (RelativeLayout) findViewById(R.id.layout_experience_center);
        layout_home_page= (LinearLayout) findViewById(R.id.layout_home_page);
        layout_devices= (LinearLayout) findViewById(R.id.layout_devices);
        layout_rooms= (LinearLayout) findViewById(R.id.layout_rooms);
        layout_personal_center= (LinearLayout) findViewById(R.id.layout_personal_center);
    }
    /**
     * 再按一次退出应用
     */
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().finishAllActivity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_getway_check:
                startActivity(new Intent(PersonalCenterActivity.this, GetwayCheckActivity.class));
                break;
            case R.id.layout_config_wifi_getway:
                startActivity(new Intent(PersonalCenterActivity.this,ScanWifiListActivity.class));
                break;
            case R.id.layout_experience_center:
                break;
            case R.id.layout_home_page:
                startActivity(new Intent(this,SmartHomeMainActivity.class));
                break;
            case R.id.layout_devices:
                startActivity(new Intent(this,DevicesActivity.class));
                break;
            case R.id.layout_rooms:
                startActivity(new Intent(this,RoomActivity.class));
                break;
            case R.id.layout_personal_center:
                startActivity(new Intent(this,ExperienceDevicesActivity.class));
                break;
        }
    }
}
