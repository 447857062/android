package com.deplink.homegenius.activity.device.router.qos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.remote.HomeGenius;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.util.NetUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.json.PERFORMANCE;
import com.deplink.sdk.android.sdk.json.Qos;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class QosSettingActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "QosSettingActivity";
    private TextView textview_title;
    private FrameLayout image_back;
    private RelativeLayout layout_model_A;
    private RelativeLayout layout_model_B;
    private RelativeLayout layout_model_download;
    private ImageView imageview_model_a;
    private ImageView imageview_model_b;
    private ImageView imageview_model_download;
    private String currentQosMode;
    private TextView textview_edit;
    private SDKManager manager;
    private EventCallback ec;
    private CheckBox checkbox_qos_switch;
    private DeleteDeviceDialog connectLostDialog;
    private RouterManager mRouterManager;
    private Qos qos;
    private boolean isSetQos;
    private HomeGenius mHomeGenius;
    private String channels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qos_setting);
        initViews();
        initDatas();
        initEvents();

    }

    private void initDatas() {
        textview_title.setText("智能分配类型选择");
        textview_edit.setText("保存");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new DeleteDeviceDialog(QosSettingActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(QosSettingActivity.this, LoginActivity.class));
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
            public void notifyHomeGeniusResponse(String result) {
                super.notifyHomeGeniusResponse(result);
                parseDeviceReport(result);
            }

            @Override
            public void deviceOpSuccess(final String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);


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
    }

    private void parseDeviceReport(String xmlStr) {
        String op = "";
        String method;
        Gson gson = new Gson();
        PERFORMANCE content = gson.fromJson(xmlStr, PERFORMANCE.class);
        op = content.getOP();
        method = content.getMethod();
        Log.i(TAG, "op=" + op + "method=" + method + "result=" + content.getResult() + "xmlStr=" + xmlStr);

        if (op == null) {
            if (content.getResult().equalsIgnoreCase("OK")) {
                Log.i(TAG, " mSDKCoordinator.notifyDeviceOpSuccess");

            }
        } else {
            if (op.equalsIgnoreCase("QOS")) {
                if (method.equalsIgnoreCase("REPORT")) {
                    qos = gson.fromJson(xmlStr, Qos.class);
                    if (qos.getSWITCH().equalsIgnoreCase("ON")) {
                        layout_model_A.setVisibility(View.VISIBLE);
                        layout_model_B.setVisibility(View.VISIBLE);
                        layout_model_download.setVisibility(View.VISIBLE);
                        checkbox_qos_switch.setChecked(true);
                        currentQosMode = qos.getCLASSIFY();
                        switch (currentQosMode) {
                            case "1":
                                layout_model_A.callOnClick();
                                break;
                            case "2":
                                layout_model_B.callOnClick();
                                break;
                            case "3":
                                layout_model_download.callOnClick();
                                break;
                        }
                    } else if (qos.getSWITCH().equalsIgnoreCase("OFF")) {
                        checkbox_qos_switch.setChecked(false);
                        layout_model_A.setVisibility(View.GONE);
                        layout_model_B.setVisibility(View.GONE);
                        layout_model_download.setVisibility(View.GONE);
                    }

                }
            }
        }
    }
    private boolean isStartFromExperience;
    @Override
    protected void onResume() {
        super.onResume();
        if (!DeviceManager.getInstance().isStartFromExperience()) {
            mHomeGenius = new HomeGenius();
            channels = mRouterManager.getCurrentSelectedRouter().getRouter().getChannels();
            manager.addEventCallback(ec);
            if (NetUtil.isNetAvailable(this)) {
                if (channels != null) {
                    mHomeGenius.queryQos(channels);
                }
            } else {
                ToastSingleShow.showText(this, "网络连接已断开");
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        layout_model_A.setOnClickListener(this);
        layout_model_B.setOnClickListener(this);
        layout_model_download.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        checkbox_qos_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layout_model_A.setVisibility(View.VISIBLE);
                    layout_model_B.setVisibility(View.VISIBLE);
                    layout_model_download.setVisibility(View.VISIBLE);
                } else {
                    layout_model_A.setVisibility(View.GONE);
                    layout_model_B.setVisibility(View.GONE);
                    layout_model_download.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        layout_model_A = findViewById(R.id.layout_model_A);
        layout_model_B = findViewById(R.id.layout_model_B);
        layout_model_download = findViewById(R.id.layout_model_download);
        imageview_model_a = findViewById(R.id.imageview_model_a);
        imageview_model_b = findViewById(R.id.imageview_model_b);
        imageview_model_download = findViewById(R.id.imageview_model_download);
        textview_edit = findViewById(R.id.textview_edit);
        checkbox_qos_switch = findViewById(R.id.checkbox_qos_switch);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.textview_edit:
                try {
                    qos = new Qos();
                    qos.setCLASSIFY(currentQosMode);
                    if (checkbox_qos_switch.isChecked()) {
                        qos.setSWITCH("ON");
                    } else {
                        qos.setSWITCH("OFF");
                    }
                    if (NetUtil.isNetAvailable(this)) {
                        boolean isUserLogin;
                        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
                        if (isUserLogin) {
                            isSetQos = true;
                            ToastSingleShow.showText(this, "QOS已设置");
                            mHomeGenius.setQos(qos, channels);
                        } else {
                            ToastSingleShow.showText(this, "未登录，无法设置静态上网,请登录后重试");
                        }

                    } else {
                        ToastSingleShow.showText(this, "网络连接已断开");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.layout_model_A:
                imageview_model_a.setImageLevel(1);
                imageview_model_b.setImageLevel(0);
                imageview_model_download.setImageLevel(0);
                currentQosMode = "1";
                break;
            case R.id.layout_model_B:
                imageview_model_a.setImageLevel(0);
                imageview_model_b.setImageLevel(1);
                imageview_model_download.setImageLevel(0);

                currentQosMode = "2";
                break;
            case R.id.layout_model_download:
                imageview_model_a.setImageLevel(0);
                imageview_model_b.setImageLevel(0);
                imageview_model_download.setImageLevel(1);

                currentQosMode = "3";
                break;
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
