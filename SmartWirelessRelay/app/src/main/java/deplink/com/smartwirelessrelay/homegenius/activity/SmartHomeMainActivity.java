package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * 智能家居主页
 */
public class SmartHomeMainActivity extends Activity implements View.OnClickListener{
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home_main);
        initViews();
        initEvents();
    }

    private void initEvents() {
        layout_home_page.setOnClickListener(this);
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
    }

    private void initViews() {
        layout_home_page= (LinearLayout) findViewById(R.id.layout_home_page);
        layout_devices= (LinearLayout) findViewById(R.id.layout_devices);
        layout_rooms= (LinearLayout) findViewById(R.id.layout_rooms);
        layout_personal_center= (LinearLayout) findViewById(R.id.layout_personal_center);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_home_page:

                break;
            case R.id.layout_devices:
                startActivity(new Intent(this,DevicesActivity.class));
                break;
            case R.id.layout_rooms:
                startActivity(new Intent(this,RoomActivity.class));
                break;
            case R.id.layout_personal_center:
                startActivity(new Intent(this,PersonalCenterActivity.class));
                break;
        }
    }
}
