package com.deplink.homegenius.activity.device.getway.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.homegenius.util.qrcode.qrcodecapture.CaptureActivity;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class AddGetwayNotifyActivity extends Activity implements View.OnClickListener{
    private Button button_next_step;
    private TextView textview_title;
    private FrameLayout image_back;
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
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        button_next_step= findViewById(R.id.button_next_step);
        textview_title= findViewById(R.id.textview_title);
        image_back= findViewById(R.id.image_back);
    }

    private void initDatas() {
        textview_title.setText("网关");
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
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
