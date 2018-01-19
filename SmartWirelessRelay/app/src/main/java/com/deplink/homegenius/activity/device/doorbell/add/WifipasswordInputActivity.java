package com.deplink.homegenius.activity.device.doorbell.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.homegenius.util.Wifi;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class WifipasswordInputActivity extends Activity implements View.OnClickListener{
    private FrameLayout image_back;
    private TextView textview_title;
    private Button button_next_step;
    private TextView textview_wifi_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifipassword_input);
        initViews();
        initDatas();
        initEvents();

    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        button_next_step.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("输入WIFI信息");
       String wifiName= Wifi.getConnectedWifiName(this);
        if(wifiName!=null&&!wifiName.equals("")){
            textview_wifi_name.setText(wifiName);
        }

    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        button_next_step = findViewById(R.id.button_next_step);
        textview_wifi_name = findViewById(R.id.textview_wifi_name);

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
