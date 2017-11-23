package deplink.com.smartwirelessrelay.homegenius.activity.device.getway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.qrcode.qrcodecapture.CaptureActivity;

public class AddGetwayNotifyActivity extends Activity implements View.OnClickListener{
    private Button button_next_step;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_getway_notify);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        button_next_step.setOnClickListener(this);
    }

    private void initViews() {
        button_next_step= (Button) findViewById(R.id.button_next_step);
    }

    private void initDatas() {
    }
    public final static int REQUEST_CODE_GETWAY = 3;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_next_step:
                Intent intentQrcodeSn = new Intent();
                intentQrcodeSn.setClass(this, CaptureActivity.class);
                intentQrcodeSn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentQrcodeSn.putExtra("requestType", REQUEST_CODE_GETWAY);
                startActivity(intentQrcodeSn);
                break;
        }
    }
}
