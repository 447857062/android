package com.deplink.homegenius.activity.device.doorbell.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class ApModeActivity extends Activity implements View.OnClickListener{
    private FrameLayout image_back;
    private TextView textview_title;
    private Button button_next_step;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ap_mode);
        initViews();
        initEvents();
        initDatas();
    }

    private void initDatas() {
        textview_title.setText("启动AP模式");
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        button_next_step.setOnClickListener(this);
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        button_next_step = findViewById(R.id.button_next_step);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_next_step:
                startActivity(new Intent(this,AddDoorbellTipsActivity.class));
                break;
        }
    }
}
