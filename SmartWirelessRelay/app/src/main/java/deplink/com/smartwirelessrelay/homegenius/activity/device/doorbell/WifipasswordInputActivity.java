package deplink.com.smartwirelessrelay.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class WifipasswordInputActivity extends Activity implements View.OnClickListener{
    private ImageView image_back;
    private TextView textview_title;
    private Button button_next_step;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifipassword_input);
        initViews();
        initEvents();
        initDatas();
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        button_next_step.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("输入WIFI信息");
    }

    private void initViews() {
        textview_title = (TextView) findViewById(R.id.textview_title);
        image_back = (ImageView) findViewById(R.id.image_back);
        button_next_step = (Button) findViewById(R.id.button_next_step);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_next_step:
                startActivity(new Intent(this,ApModeActivity.class));
                break;
        }
    }
}
