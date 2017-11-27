package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class AirRemoteControlMianActivity extends Activity implements View.OnClickListener{
    private static final String TAG="ARCMianActivity";
    private ImageView image_back;
    private ImageView imageview_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_remote_control);
        initViews();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        imageview_list.setOnClickListener(this);
    }

    private void initViews() {
        image_back= (ImageView) findViewById(R.id.image_back);
        imageview_list= (ImageView) findViewById(R.id.imageview_list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
