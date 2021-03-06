package com.deplink.homegenius.activity.device.router.wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.remote.HomeGenius;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.json.PERFORMANCE;
import com.deplink.sdk.android.sdk.json.Wifi;
import com.deplink.sdk.android.sdk.json.Wifi_2G;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.deplink.sdk.android.sdk.rest.RestfulToolsRouter;
import com.google.gson.Gson;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertWifiPasswordActivity extends Activity implements View.OnClickListener {
    private static final String TAG="AlertWifiPassword";
    private TextView textview_edit;
    private EditText edittext_password;
    private EditText edittext_password_again;
    private SDKManager manager;
    private EventCallback ec;
    private String wifiType;
    private ImageView image_input_eye_password;
    private FrameLayout framelayout_input_eye_password;
    private ImageView image_input_eye_password_again;
    private FrameLayout framelayout_nput_eye_password_again;
    private String password;
    private TextView textview_title;
    private FrameLayout image_back;
    /**
     * wifi名称密码设置成功，后面要重启了
     */
    private static final int MSG_LOCAL_OP_RETURN_OK = 1;
    private DeleteDeviceDialog connectLostDialog;
    private RouterManager mRouterManager;
    private HomeGenius mHomeGenius;
    private String channels;
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
        connectLostDialog = new DeleteDeviceDialog(AlertWifiPasswordActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
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
            public void onFailure(SDKAction action, Throwable throwable) {

            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
            }

            @Override
            public void notifyHomeGeniusResponse(String result) {
                super.notifyHomeGeniusResponse(result);
                parseDeviceReport(result);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);

                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setContentText("当前账号已在其它设备上登录,是否重新登录");
            }
        };
        wifiType = getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_TYPE);
        password = getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_PASSWORD);
        if (password != null && !password.equals("")) {
            edittext_password.setText(password);
            edittext_password.setSelection(password.length());
        }

    }
    private void parseDeviceReport(String xmlStr) {
        String op;
        String method;
        Gson gson = new Gson();
        PERFORMANCE content = gson.fromJson(xmlStr, PERFORMANCE.class);
        op = content.getOP();
        method = content.getMethod();
        Log.i(TAG, "op=" + op + "method=" + method + "result=" + content.getResult() + "xmlStr=" + xmlStr);
        if (op == null) {
            if (content.getResult().equalsIgnoreCase("OK")) {
                Log.i(TAG," mSDKCoordinator.notifyDeviceOpSuccess");
                if (isSetWifiPassword) {
                    ToastSingleShow.showText(AlertWifiPasswordActivity.this, "设置成功");
                }
            }
        }
    }
    private boolean isSetWifiPassword;

    private void initEvents() {
        textview_edit.setOnClickListener(this);
        image_back.setOnClickListener(this);
        framelayout_input_eye_password.setOnClickListener(this);
        framelayout_nput_eye_password_again.setOnClickListener(this);
    }

    private void initViews() {
        textview_edit = findViewById(R.id.textview_edit);
        image_input_eye_password = findViewById(R.id.image_input_eye_password);
        framelayout_input_eye_password = findViewById(R.id.framelayout_input_eye_password);
        image_input_eye_password_again = findViewById(R.id.image_input_eye_password_again);
        framelayout_nput_eye_password_again = findViewById(R.id.framelayout_nput_eye_password_again);
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        edittext_password = findViewById(R.id.edittext_password);
        edittext_password_again = findViewById(R.id.edittext_password_again);
    }
    private boolean isLogin;
    @Override
    protected void onResume() {
        super.onResume();
        if(! DeviceManager.getInstance().isStartFromExperience()){
            manager.addEventCallback(ec);
            isLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
            mHomeGenius = new HomeGenius();
            channels = mRouterManager.getCurrentSelectedRouter().getRouter().getChannels();
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
            case R.id.framelayout_input_eye_password:
                if (edittext_password.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    image_input_eye_password.setImageResource(R.drawable.blueeye);
                    edittext_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else if (edittext_password.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                    image_input_eye_password.setImageResource(R.drawable.grayeye);
                    edittext_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
                if (edittext_password.getText().toString().trim().length() > 0) {
                    edittext_password.setSelection(edittext_password.getText().toString().trim().length());
                }
                break;
            case R.id.framelayout_nput_eye_password_again:
                if (edittext_password_again.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    image_input_eye_password_again.setImageResource(R.drawable.blueeye);
                    edittext_password_again.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else if (edittext_password_again.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                    image_input_eye_password_again.setImageResource(R.drawable.grayeye);
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
                if (isLogin && channels != null) {
                    isSetWifiPassword = true;
                    Wifi wifi = new Wifi();
                    switch (wifiType) {
                        case AppConstant.WIFISETTING.WIFI_TYPE_2G:
                            Wifi_2G wifi_2g = new Wifi_2G();
                            wifi_2g.setWifiPassword(password);
                            wifi.setWifi_2G(wifi_2g);
                            mHomeGenius.setWifi(wifi,channels);
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
       startActivity(new Intent(AlertWifiPasswordActivity.this, DevicesActivity.class));
        RestfulToolsRouter.getSingleton(AlertWifiPasswordActivity.this).rebootRouter(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
