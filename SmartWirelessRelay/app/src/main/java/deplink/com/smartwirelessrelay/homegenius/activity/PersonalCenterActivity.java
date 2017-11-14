package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.RoomActivity;

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
        layout_config_wifi_getway= (RelativeLayout) findViewById(R.id.layout_getway_check);
        layout_experience_center= (RelativeLayout) findViewById(R.id.layout_getway_check);
        layout_home_page= (LinearLayout) findViewById(R.id.layout_home_page);
        layout_devices= (LinearLayout) findViewById(R.id.layout_devices);
        layout_rooms= (LinearLayout) findViewById(R.id.layout_rooms);
        layout_personal_center= (LinearLayout) findViewById(R.id.layout_personal_center);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_getway_check:
                break;
            case R.id.layout_config_wifi_getway:
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
               // startActivity(new Intent(this,PersonalCenterActivity.class));
                break;
        }
    }
}
