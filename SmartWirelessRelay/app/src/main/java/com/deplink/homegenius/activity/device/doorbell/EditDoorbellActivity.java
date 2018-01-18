package com.deplink.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class EditDoorbellActivity extends Activity implements View.OnClickListener{
    private TextView button_delete_device;
    private DeleteDeviceDialog deleteDialog;
    private TextView textview_title;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit2);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        button_delete_device.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initDatas() {
        deleteDialog=new DeleteDeviceDialog(this);
        textview_title.setText("智能门铃");
    }

    private void initViews() {
        button_delete_device= findViewById(R.id.button_delete_device);
        textview_title= findViewById(R.id.textview_title);
        image_back= findViewById(R.id.image_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_delete_device:
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {

                    }
                });
                deleteDialog.show();
                break;
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
