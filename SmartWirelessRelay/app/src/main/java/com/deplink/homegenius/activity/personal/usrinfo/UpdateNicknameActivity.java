package com.deplink.homegenius.activity.personal.usrinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.edittext.ClearEditText;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.UserInfoAlertBody;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class UpdateNicknameActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "UpdateNicknameActivity";
    private ClearEditText edittext_update_nickname;
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private SDKManager manager;
    private EventCallback ec;
    private DeleteDeviceDialog connectLostDialog;
    private String nickName;
    private boolean isUserLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);
        initViews();
        initDatas();
        initEvents();
    }
    private void initDatas() {
        textview_title.setText("修改昵称");
        textview_edit.setText("完成");
        nickName=getIntent().getStringExtra("nickname");
        if(nickName!=null){
            edittext_update_nickname.setText(nickName);
            edittext_update_nickname.setSelection(nickName.length());
        }
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new DeleteDeviceDialog(UpdateNicknameActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(UpdateNicknameActivity.this, LoginActivity.class));
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
            public void onFailure(SDKAction action, Throwable throwable) {

            }

            @Override
            public void alertUserInfo(DeviceOperationResponse info) {
                super.alertUserInfo(info);
                Log.i(TAG,"alertUserInfo:"+info.toString());
            }
            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                isUserLogin=false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setContentText("当前账号已在其它设备上登录,是否重新登录");
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUserLogin=Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        manager.addEventCallback(ec);
    }
    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= findViewById(R.id.textview_title);
        textview_edit= findViewById(R.id.textview_edit);
        image_back= findViewById(R.id.image_back);
        edittext_update_nickname = findViewById(R.id.edittext_update_nickname);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_edit:
                String nickNameChange=edittext_update_nickname.getText().toString();
                if(isUserLogin){
                    if(!nickNameChange.equalsIgnoreCase(nickName)){
                        String userName=Perfence.getPerfence(Perfence.PERFENCE_PHONE);
                        UserInfoAlertBody body=new UserInfoAlertBody();
                        body.setNickname(nickNameChange);
                        manager.alertUserInfo(userName,body);
                    }
                }else{
                   onBackPressed();
                }
                break;
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
