package com.deplink.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class EditDoorbellActivity extends Activity implements View.OnClickListener{
    private TextView button_delete_device;
    private DeleteDeviceDialog deleteDialog;
    private TextView textview_title;
    private FrameLayout image_back;
    private DeviceManager mDeviceManager;
    private DeviceListener mDeviceListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit2);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDeviceManager.addDeviceListener(mDeviceListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDeviceManager.removeDeviceListener(mDeviceListener);
    }

    private void initEvents() {
        button_delete_device.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initDatas() {
        deleteDialog=new DeleteDeviceDialog(this);
        textview_title.setText("智能门铃");
        mDeviceManager=DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this);
        mDeviceListener=new DeviceListener() {
            @Override
            public void responseDeleteDeviceHttpResult(DeviceOperationResponse result) {
                super.responseDeleteDeviceHttpResult(result);
                DialogThreeBounce.hideLoading();
                mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_RESULT);
            }
        };
    }
    private static final int MSG_HANDLE_DELETE_DEVICE_RESULT = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HANDLE_DELETE_DEVICE_RESULT:
                    mDeviceManager.deleteDBSmartDevice(mDeviceManager.getCurrentSelectSmartDevice().getUid());
                    Toast.makeText(EditDoorbellActivity.this, "删除设备成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditDoorbellActivity.this, DevicesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;

            }
        }
    };
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
                    mDeviceManager.deleteDeviceHttp();
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
