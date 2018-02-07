package com.deplink.homegenius.activity.personal.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

import com.deplink.homegenius.activity.homepage.SmartHomeMainActivity;
import com.deplink.homegenius.util.NetUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.util.StringValidatorUtil;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class RegistActivity extends Activity implements View.OnClickListener, View.OnFocusChangeListener {
    private static final String TAG = "RegistActivity";
    private TextView textview_title;
    private FrameLayout image_back;
    private View view_phonenumber_dirverline;
    private View view_password_dirverline;
    private View view_yanzhen_dirverline;
    private EditText edittext_input_phone_number;
    private EditText edittext_verification_code;
    private EditText edittext_input_password;
    private FrameLayout layout_eye;
    private ImageView imageview_eye;
    private EventHandler eh;
    private ArrayList<HashMap<String, Object>> alhmCountries;
    private String simCountryCode = "86";
    private TextView button_SMS_verification_code;
    private int time = Perfence.VERIFYCODE_TIME;
    private SDKManager manager;
    private EventCallback ec;
    private Button button_regist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initViews();
        initDatas();
        initEvents();
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ToastSingleShow.showText(RegistActivity.this, (String) msg.obj);
        }
    };
    String verifycode;
    String phoneNumber;
    String password;
    private boolean isGetCaptche;
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
        edittext_input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        showInputmothed();
    }

    private String username;

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void getPhoneCaptcha() {
        if (!NetUtil.isNetAvailable(this)) {
            Toast.makeText(RegistActivity.this, "无法访问网络", Toast.LENGTH_SHORT).show();
            return;
        }

        username = edittext_input_phone_number.getText().toString();
        if (username.equals("")) {
            Toast.makeText(RegistActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean support = false;
        for (int i = 0; i < alhmCountries.size(); i++) {
            HashMap<String, Object> hashMap = alhmCountries.get(i);
            if (simCountryCode.equalsIgnoreCase(hashMap.get("zone").toString()) && username.matches(hashMap.get("rule").toString()))
                support = true;
        }
        if (support) {
            isGetCaptche = true;
            SMSSDK.getVerificationCode(simCountryCode, username);
            Toast.makeText(RegistActivity.this, "验证码已发送", Toast.LENGTH_SHORT).show();
            button_SMS_verification_code.setEnabled(false);
            time = Perfence.VERIFYCODE_TIME;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    button_SMS_verification_code.setText(String.format(getString(R.string.register_verifycode_time), time));
                    if (time > 0)
                        handler.postDelayed(this, Perfence.DELAY_VERIFYCODE);
                    else {
                        button_SMS_verification_code.setEnabled(true);
                        button_SMS_verification_code.setText(getResources().getString(R.string.get_sms_verification));
                    }
                    time--;
                }
            }, 0);
        } else {
            Toast.makeText(RegistActivity.this, "手机号码不支持", Toast.LENGTH_SHORT).show();
        }
    }
    private void initDatas() {
        textview_title.setText("注册");
        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Log.i(TAG, "event:" + event + ", result: " + result + ", data:" + data.toString());
                if (result == SMSSDK.RESULT_COMPLETE) {
                    switch (event) {
                        case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                            manager.register(phoneNumber, password, verifycode);
                            break;
                        case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                            break;
                        case SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES:
                            Perfence.alhmCountries = alhmCountries = (ArrayList<HashMap<String, Object>>) data;
                            break;
                        default:
                            break;
                    }
                } else {
                    String msg = ((Throwable) data).getMessage();
                    Log.i(TAG, msg);
                    try {
                        JSONObject object = new JSONObject(msg);
                        String des = object.optString("detail");
                        int status = object.optInt("status");
                        if (status > 0 && !TextUtils.isEmpty(des)) {
                            // Perfence.showCallBack(des);
                            Message message = Message.obtain();
                            message.what = 1;
                            message.obj = des;
                            mhandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
      //  SMSSDK.initSDK(getApplicationContext(), Perfence.SMSSDK_APPKEY, Perfence.SMSSDK_APPSECRET);
        SMSSDK.registerEventHandler(eh);
        if (Perfence.alhmCountries == null || Perfence.alhmCountries.size() == 0)
            SMSSDK.getSupportedCountries();
        else {
            alhmCountries = Perfence.alhmCountries;
        }
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
                switch (action) {
                    case REGISTER:
                        ToastSingleShow.showText(RegistActivity.this, "注册成功");
                        Intent intent=new Intent(RegistActivity.this, SmartHomeMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
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
                    case REGISTER:
                        try {
                            ToastSingleShow.showText(RegistActivity.this, "注册失败:" + throwable.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }


        };
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        edittext_input_phone_number.setOnFocusChangeListener(this);
        edittext_verification_code.setOnFocusChangeListener(this);
        edittext_input_password.setOnFocusChangeListener(this);
        layout_eye.setOnClickListener(this);
        button_regist.setOnClickListener(this);
        button_SMS_verification_code.setOnClickListener(this);
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        view_phonenumber_dirverline = findViewById(R.id.view_phonenumber_dirverline);
        view_password_dirverline = findViewById(R.id.view_password_dirverline);
        view_yanzhen_dirverline = findViewById(R.id.view_yanzhen_dirverline);
        edittext_input_phone_number = findViewById(R.id.edittext_input_phone_number);
        edittext_verification_code = findViewById(R.id.edittext_verification_code);
        edittext_input_password = findViewById(R.id.edittext_input_password);
        layout_eye = findViewById(R.id.layout_eye);
        imageview_eye = findViewById(R.id.imageview_eye);
        button_regist = findViewById(R.id.button_regist);
        button_SMS_verification_code = findViewById(R.id.buton_get_verification_code);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.buton_get_verification_code:
                getPhoneCaptcha();
                break;
            case R.id.button_regist:
                phoneNumber = edittext_input_phone_number.getText().toString().trim();
                Log.i(TAG, "phoneNumber=" + phoneNumber);
                boolean isPhoneNumber = StringValidatorUtil.isMobileNO(phoneNumber);
                if (!isPhoneNumber) {
                    Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                password = edittext_input_password.getText().toString().trim();
                if (password.equals("")) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                }
                verifycode = edittext_verification_code.getText().toString().trim();
                if (verifycode.length() < 6) {
                    ToastSingleShow.showText(this, "需要校验的验证码错误");
                    return;
                }
                //注册
                if (!isGetCaptche) {
                    ToastSingleShow.showText(this, "需要校验的验证码错误");
                    return;
                } else {
                    SMSSDK.submitVerificationCode(simCountryCode, username, verifycode);
                }
                break;
            case R.id.layout_eye:
                changeInputCipher();
                break;
        }
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
            case R.id.edittext_verification_code:
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    view_yanzhen_dirverline.setBackgroundResource(R.color.white);
                } else {
                    // 此处为失去焦点时的处理内容
                    view_yanzhen_dirverline.setBackgroundResource(R.color.white_alpha60);
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
