package com.deplink.homegenius.activity.device.router.firmwareupdate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import com.deplink.homegenius.activity.personal.PersonalCenterActivity;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.MakeSureDialog;

public class UpdateStatusActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "UpdateStatusActivity";
    private Button button_sure;
    private TextView textview_updateing;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;
    private TextView textview_title;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_status);
        initViews();
        initDatas();
        initEvents();
        manager.addEventCallback(ec);

    }

    private void initEvents() {
        button_sure.setOnClickListener(this);
        try {
            routerDevice.startUpgrade();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDatas() {
        textview_title.setText("固件升级");
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        connectLostDialog = new MakeSureDialog(UpdateStatusActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(UpdateStatusActivity.this, LoginActivity.class));
            }
        });
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {

            @Override
            public void onSuccess(SDKAction action) {
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {

            }

            @Override
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
            }


            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
            }

            @Override
            public void deviceOpFailure(String op, String deviceKey, Throwable throwable) {
            }

            @Override
            public void notifyDeviceDataChanged(String deviceKey, int type) {
                Log.i(TAG, "notifyDeviceDataChanged type=" + type);
                switch (type) {
                    case RouterDevice.MSG_REPORT_START:
                        textview_updateing.setText("开始升级固件...");
                        break;
                    case RouterDevice.MSG_REPORT_DOWNLOAD:
                        textview_updateing.setText("开始下载固件...");
                        break;
                    case RouterDevice.MSG_REPORT_ERROR:
                        textview_updateing.setText("升级固件出错...");
                        break;
                    case RouterDevice.MSG_REPORT_FINSH:
                        textview_updateing.setText("固件升级完成...");
                        button_sure.setVisibility(View.VISIBLE);
                        break;
                    case RouterDevice.MSG_REPORT_WRITE:
                        textview_updateing.setText("开始烧写固件...");
                        break;
                }
            }


            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }
        };
    }
    private boolean isUserLogin;
    @Override
    protected void onResume() {
        super.onResume();
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        routerDevice = (RouterDevice) manager.getDevice(mRouterManager.getRouterDeviceKey());
    }


    private void initViews() {
        textview_title= findViewById(R.id.textview_title);
        button_sure = findViewById(R.id.button_sure);
        textview_updateing = findViewById(R.id.textview_updateing);
        image_back = findViewById(R.id.image_back);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sure:
                startActivity(new Intent(this, PersonalCenterActivity.class));
                finish();
                break;
            case R.id.image_back:
               onBackPressed();
                break;
        }
    }
}
