package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class AirRemoteControlMianActivity extends Activity implements View.OnClickListener{
    private static final String TAG="ARCMianActivity";
    private ImageView image_back;
    private TextView textview_title;
    private ImageView image_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_remote_control);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("智能空调遥控");
        image_setting.setImageResource(R.drawable.menuicon);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        image_setting.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (ImageView) findViewById(R.id.image_back);
        image_setting= (ImageView) findViewById(R.id.image_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.image_setting:
                break;
        }
    }
}
