package com.deplink.homegenius.activity.personal.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.Protocol.json.device.lock.Record;
import com.deplink.homegenius.Protocol.json.device.router.Router;
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

import org.litepal.crud.DataSupport;

import java.util.Timer;
import java.util.TimerTask;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class LoginActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener {
    private static final String TAG = "LoginActivity";
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
    private FrameLayout layout_eye;
    private ImageView imageview_eye;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initDatas();
        initEvents();
    }

    private void showInputmothed() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {

                               InputMethodManager inputManager =
                                       (InputMethodManager) edittext_input_phone_number.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(edittext_input_phone_number, 0);
                           }
                       },
                500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        showInputmothed();
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
                        User user = manager.getUserInfo();
                        //清除本地数据库
                        String lastLoginUser = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
                        Log.i(TAG,"lastLoginUser="+lastLoginUser);
                        if (lastLoginUser != null && !lastLoginUser.equalsIgnoreCase(user.getName())) {
                            DataSupport.deleteAll(SmartDev.class);
                            DataSupport.deleteAll(GatwayDevice.class);
                            DataSupport.deleteAll(Room.class);
                            DataSupport.deleteAll(Record.class);
                            DataSupport.deleteAll(Router.class);
                        }
                        Perfence.setPerfence(Perfence.USER_PASSWORD, user.getPassword());
                        Perfence.setPerfence(Perfence.PERFENCE_PHONE, user.getName());
                        Perfence.setPerfence(AppConstant.USER_LOGIN, true);
                        startActivity(new Intent(LoginActivity.this, SmartHomeMainActivity.class));
                        DialogLoading.hideLoading();
                        LoginActivity.this.finish();
                        Log.i(TAG, "onSuccess login");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {


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
        layout_eye.setOnClickListener(this);
    }

    private void initViews() {
        imageview_delete = findViewById(R.id.imageview_delete);
        textview_forget_password = findViewById(R.id.textview_forget_password);
        textview_regist_now = findViewById(R.id.textview_regist_now);
        button_login = findViewById(R.id.button_login);
        edittext_input_password = findViewById(R.id.edittext_input_password);
        edittext_input_phone_number = findViewById(R.id.edittext_input_phone_number);
        view_phonenumber_dirverline = findViewById(R.id.view_phonenumber_dirverline);
        view_password_dirverline = findViewById(R.id.view_password_dirverline);
        layout_eye = findViewById(R.id.layout_eye);
        imageview_eye = findViewById(R.id.imageview_eye);
    }

    /**
     * 设置密文明文之间切换
     */
    private void changeInputCipher() {
        if (edittext_input_password.getTransformationMethod() instanceof PasswordTransformationMethod) {
            imageview_eye.setImageResource(R.drawable.displayicon);
            edittext_input_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

        } else if (edittext_input_password.getTransformationMethod() instanceof HideReturnsTransformationMethod) {

            imageview_eye.setImageResource(R.drawable.hideicon);
            edittext_input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        }
        int length = edittext_input_password.getText().toString().trim().length();
        if (length != 0) {
            edittext_input_password.setSelection(length);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_delete:
                this.finish();
                break;
            case R.id.layout_eye:
                changeInputCipher();
                break;
            case R.id.button_login:
                final String phoneNumber = edittext_input_phone_number.getText().toString().trim();
                boolean isValidatorPhone = StringValidatorUtil.isMobileNO(phoneNumber);
                if (!isValidatorPhone) {
                    Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = edittext_input_password.getText().toString().trim();
                if (password.length() < 6) {
                    Toast.makeText(this, "密码位数6-20", Toast.LENGTH_SHORT).show();
                    return;
                }
                DialogLoading.showLoading(this);
                manager.login(phoneNumber, password);
                break;
            case R.id.textview_forget_password:
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                break;
            case R.id.textview_regist_now:
                startActivity(new Intent(LoginActivity.this, RegistActivity.class));
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
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
