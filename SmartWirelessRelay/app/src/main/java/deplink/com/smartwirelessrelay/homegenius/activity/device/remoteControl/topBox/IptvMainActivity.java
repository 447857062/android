package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.topBox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class IptvMainActivity extends Activity implements View.OnClickListener{
    private ImageView image_back;
    private Button button_control_base;
    private Button button_control_number;
    private RelativeLayout layout_control_base;
    private RelativeLayout layout_control_number;
    private TextView textview_title;
    private ImageView image_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iptv_main);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("机顶盒遥控");
        image_setting.setImageResource(R.drawable.menuicon);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_control_base.setOnClickListener(this);
        button_control_number.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_setting= (ImageView) findViewById(R.id.image_setting);
        image_back= (ImageView) findViewById(R.id.image_back);
        button_control_base = (Button) findViewById(R.id.button_control_base);
        button_control_number = (Button) findViewById(R.id.button_control_number);
        layout_control_base = (RelativeLayout) findViewById(R.id.layout_control_base);
        layout_control_number = (RelativeLayout) findViewById(R.id.layout_control_number);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_setting:
                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_control_base:
                layout_control_base.setVisibility(View.VISIBLE);
                layout_control_number.setVisibility(View.GONE);
                break;
            case R.id.button_control_number:
                layout_control_base.setVisibility(View.GONE);
                layout_control_number.setVisibility(View.VISIBLE);
                break;
        }
    }
}
