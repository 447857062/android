package com.deplink.homegenius.activity.personal.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.activity.homepage.SmartHomeMainActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.util.StringValidatorUtil;
import com.deplink.homegenius.view.dialog.loadingdialog.DialogLoading;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.bean.User;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class LoginActivity extends Activity implements View.OnClickListener,View.OnFocusChangeListener{
    private static final String TAG="LoginActivity";
    private ImageView imageview_delete;
    private TextView textview_forget_password;
    private TextView textview_regist_now;
    private Button button_login;
    private EditText edittext_input_phone_number;
    private EditText edittext_input_password;
    private SDKManager manager;
    private EventCallback ec;
    private View view_phonenumber_dirverline;
    private View view_password_dirverline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
    }
    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initDatas() {
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
                switch (action) {
                    case LOGIN:
                        Perfence.setPerfence(AppConstant.PERFENCE_BIND_APP_UUID, manager.getUserInfo().getUuid());
                        manager.connectMQTT(getApplicationContext());
                        Log.i(TAG, "onSuccess login");
                        break;
                    case CONNECTED:
                        User user = manager.getUserInfo();
                        Perfence.setPerfence(Perfence.USER_PASSWORD, user.getPassword());
                        Perfence.setPerfence(Perfence.PERFENCE_PHONE, user.getName());
                        Perfence.setPerfence(AppConstant.USER_LOGIN, true);
                        startActivity(new Intent(LoginActivity.this, SmartHomeMainActivity.class));
                        DialogLoading.hideLoading();
                        LoginActivity.this.finish();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {


            }

            @Override
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
                switch (action) {
                    case LOGIN:
                        Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                        DialogLoading.hideLoading();
                        ToastSingleShow.showText(LoginActivity.this, throwable.getMessage());
                        break;
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                ToastSingleShow.showText(LoginActivity.this, "当前账号已在其它设备上登录");
            }
        };
    }

    private void initEvents() {
        imageview_delete.setOnClickListener(this);
        textview_forget_password.setOnClickListener(this);
        textview_regist_now.setOnClickListener(this);
        button_login.setOnClickListener(this);
        edittext_input_phone_number.setOnFocusChangeListener(this);
        edittext_input_password.setOnFocusChangeListener(this);
    }

    private void initViews() {
        imageview_delete= findViewById(R.id.imageview_delete);
        textview_forget_password= findViewById(R.id.textview_forget_password);
        textview_regist_now= findViewById(R.id.textview_regist_now);
        button_login= findViewById(R.id.button_login);
        edittext_input_password= findViewById(R.id.edittext_input_password);
        edittext_input_phone_number= findViewById(R.id.edittext_input_phone_number);
        view_phonenumber_dirverline=  findViewById(R.id.view_phonenumber_dirverline);
        view_password_dirverline=  findViewById(R.id.view_password_dirverline);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageview_delete:
                this.finish();
                break;
            case R.id.button_login:
                final String phoneNumber = edittext_input_phone_number.getText().toString().trim();
                boolean isValidatorPhone = StringValidatorUtil.isMobileNO(phoneNumber);
                if (!isValidatorPhone) {
                    Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                Perfence.setPerfence(Perfence.PERFENCE_PHONE, phoneNumber);
                String password = edittext_input_password.getText().toString().trim();
                if (password.length() < 6) {
                    Toast.makeText(this, "密码位数6-20", Toast.LENGTH_SHORT).show();
                    return;
                }

                DialogLoading.showLoading(this);
                    manager.login(phoneNumber, password);
                break;
            case R.id.textview_forget_password:
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
                break;
            case R.id.textview_regist_now:
                startActivity(new Intent(LoginActivity.this,RegistActivity.class));
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.edittext_input_phone_number:
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    view_phonenumber_dirverline.setBackgroundResource(R.color.white);
                } else {
                    // 此处为失去焦点时的处理内容
                    view_phonenumber_dirverline.setBackgroundResource(R.color.white_alpha60);
                }
                break;
            case R.id.edittext_input_password:
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    view_password_dirverline.setBackgroundResource(R.color.white);
                } else {
                    // 此处为失去焦点时的处理内容
                    view_password_dirverline.setBackgroundResource(R.color.white_alpha60);
                }
                break;
        }
    }
}
