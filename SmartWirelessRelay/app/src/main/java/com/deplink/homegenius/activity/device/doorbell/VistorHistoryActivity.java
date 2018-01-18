package com.deplink.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class VistorHistoryActivity extends Activity implements View.OnClickListener{
    private TextView textview_title;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vistor_history);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("访客记录");
    }

    private void initViews() {
        textview_title=findViewById(R.id.textview_title);
        image_back=findViewById(R.id.image_back);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
