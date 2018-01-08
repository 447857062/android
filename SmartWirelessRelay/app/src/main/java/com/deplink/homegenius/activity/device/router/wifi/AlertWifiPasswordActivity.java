package com.deplink.homegenius.activity.device.router.wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.json.Wifi;
import com.deplink.sdk.android.sdk.json.Wifi_2G;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.router.RouterManager;

public class AlertWifiPasswordActivity extends Activity implements View.OnClickListener {
    private TextView textview_edit;
    private EditText edittext_password;
    private EditText edittext_password_again;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private String wifiType;
    private ImageView image_input_eye_password;
    private ImageView image_input_eye_password_again;
    private String password;
    private TextView textview_title;
    private FrameLayout image_back;
    /**
     * wifi名称密码设置成功，后面要重启了
     */
    private static final int MSG_LOCAL_OP_RETURN_OK = 1;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_wifi_password);
        initViews();
        initDatas();
        initEvents();

    }

    private void initDatas() {
        textview_title.setText("更改WIFI密码");
        textview_edit.setText("保存");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(AlertWifiPasswordActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(AlertWifiPasswordActivity.this, LoginActivity.class));
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
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_SUCCESS:
                        if (isSetWifiPassword) {
                            ToastSingleShow.showText(AlertWifiPasswordActivity.this, "设置成功");
                        }
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
        wifiType = getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_TYPE);
        password = getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_PASSWORD);
        if (password != null && !password.equals("")) {
            edittext_password.setText(password);
            edittext_password.setSelection(password.length());
        }

    }

    private boolean isSetWifiPassword;

    private void initEvents() {
        textview_edit.setOnClickListener(this);
        image_back.setOnClickListener(this);
        image_input_eye_password.setOnClickListener(this);
        image_input_eye_password_again.setOnClickListener(this);
    }

    private void initViews() {
        textview_edit = findViewById(R.id.textview_edit);
        image_input_eye_password = findViewById(R.id.image_input_eye_password);
        image_input_eye_password_again = findViewById(R.id.image_input_eye_password_again);
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        edittext_password = findViewById(R.id.edittext_password);
        edittext_password_again = findViewById(R.id.edittext_password_again);
    }

    private boolean isLogin;
    private boolean isConnectedLocalRouter;

    @Override
    protected void onResume() {
        super.onResume();
        if( DeviceManager.getInstance().isStartFromExperience()){

        }else{
            manager.addEventCallback(ec);
            isLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
            if (isLogin) {
                routerDevice = (RouterDevice) manager.getDevice(mRouterManager.getRouterDeviceKey());
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                onBackPressed();
                break;
            case R.id.image_input_eye_password:
                if (edittext_password.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    image_input_eye_password.setImageResource(R.drawable.input_eye);
                    edittext_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else if (edittext_password.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                    image_input_eye_password.setImageResource(R.drawable.input_eye_white);
                    edittext_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
                if (edittext_password.getText().toString().trim().length() > 0) {
                    edittext_password.setSelection(edittext_password.getText().toString().trim().length());
                }
                break;
            case R.id.image_input_eye_password_again:
                if (edittext_password_again.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    image_input_eye_password_again.setImageResource(R.drawable.input_eye);
                    edittext_password_again.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else if (edittext_password_again.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                    image_input_eye_password_again.setImageResource(R.drawable.input_eye_white);
                    edittext_password_again.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
                if (edittext_password_again.getText().toString().trim().length() > 0) {
                    edittext_password_again.setSelection(edittext_password_again.getText().toString().trim().length());
                }

                break;
            case R.id.textview_edit:
                String password = edittext_password.getText().toString().trim();
                String passwordAgain = edittext_password_again.getText().toString().trim();
                if (password.length() < 8) {
                    ToastSingleShow.showText(this, "密码长度最小为8");
                    return;
                }
                if (!password.equals(passwordAgain)) {
                    ToastSingleShow.showText(this, "两次输入的密码不一致");
                    return;
                }
                if (isLogin && routerDevice != null) {
                    isSetWifiPassword = true;
                    Wifi wifi = new Wifi();
                    switch (wifiType) {
                        case AppConstant.WIFISETTING.WIFI_TYPE_2G:
                            Wifi_2G wifi_2g = new Wifi_2G();
                            wifi_2g.setWifiPassword(password);
                            wifi.setWifi_2G(wifi_2g);
                            routerDevice.setWifi(wifi);
                            break;
                        case AppConstant.WIFISETTING.WIFI_TYPE_VISITOR:
                            Intent intent = new Intent();
                            intent.putExtra("password", password);
                            setResult(RESULT_OK, intent);
                            break;
                    }
                    this.finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("password", password);
                    setResult(RESULT_OK, intent);
                    this.finish();

                }
                break;
            case R.id.image_back:
                onBackPressed();
                break;

        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOCAL_OP_RETURN_OK:
                    rebootRouterLocal();
                    ToastSingleShow.showText(AlertWifiPasswordActivity.this, "路由器正在重启...");
                    break;
            }
        }
    };

    /**
     * 重启路由器（本地接口）
     */
    private void rebootRouterLocal() {
       /* startActivity(new Intent(AlertWifiPasswordActivity.this, SettingActivity.class));
        RestfulToolsRouter.getSingleton(AlertWifiPasswordActivity.this).rebootRouter(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });*/
    }
}
