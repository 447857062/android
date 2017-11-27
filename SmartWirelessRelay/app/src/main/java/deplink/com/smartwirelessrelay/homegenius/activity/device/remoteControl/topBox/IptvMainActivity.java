package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.topBox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class IptvMainActivity extends Activity implements View.OnClickListener{
    private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iptv_main);
        initViews();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        image_back= (ImageView) findViewById(R.id.image_back);
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
