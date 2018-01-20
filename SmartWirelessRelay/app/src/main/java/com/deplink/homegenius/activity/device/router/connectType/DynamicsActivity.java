package com.deplink.homegenius.activity.device.router.connectType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.remote.HomeGenius;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.util.NetUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.router.RouterDevice;
import com.deplink.sdk.android.sdk.json.DHCP;
import com.deplink.sdk.android.sdk.json.PERFORMANCE;
import com.deplink.sdk.android.sdk.json.Proto;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class DynamicsActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "DynamicsActivity";
    private TextView textview_edit;
    private TextView textview_title;
    private FrameLayout image_back;
    private SDKManager manager;
    private EventCallback ec;
    private EditText edittext_mtu;
    private EditText edittext_dns1;
    private EditText edittext_dns2;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;
    private HomeGenius mHomeGenius;
    private String channels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamics);
        initViews();
        initDatas();
        initEvents();
    }


    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        mHomeGenius = new HomeGenius();
        channels = mRouterManager.getCurrentSelectedRouter().getRouter().getChannels();
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initDatas() {
        textview_title.setText("动态IP");
        textview_edit.setText("保存");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(DynamicsActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(DynamicsActivity.this, LoginActivity.class));
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
            public void notifyHomeGeniusResponse(String result) {
                super.notifyHomeGeniusResponse(result);
                parseDeviceReport(result);
            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_GET_WAN:


                        break;
                }
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {

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

    private void parseDeviceReport(String xmlStr) {
        String op = "";
        String method;
        Gson gson = new Gson();
        PERFORMANCE content = gson.fromJson(xmlStr, PERFORMANCE.class);
        op = content.getOP();
        method = content.getMethod();
        Log.i(TAG, "op=" + op + "method=" + method + "result=" + content.getResult() + "xmlStr=" + xmlStr);
        if (op == null) {

        } else if (op.equalsIgnoreCase("WAN")) {
            if (method.equalsIgnoreCase("REPORT")) {
                PERFORMANCE wan = gson.fromJson(xmlStr, PERFORMANCE.class);
                if (wan.getProto().getDHCP() != null) {
                    ToastSingleShow.showText(DynamicsActivity.this, "动态IP设置成功");
                }
            }
        }
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        textview_edit = findViewById(R.id.textview_edit);
        edittext_mtu = findViewById(R.id.edittext_mtu);
        edittext_dns1 = findViewById(R.id.edittext_dns1);
        edittext_dns2 = findViewById(R.id.edittext_dns2);
    }

    private boolean isUserLogin;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                if (NetUtil.isNetAvailable(this)) {
                    String mtu = edittext_mtu.getText().toString().trim();
                    String dns1 = edittext_dns1.getText().toString().trim();
                    String dns2 = edittext_dns2.getText().toString().trim();
                    Proto proto = new Proto();
                    DHCP dhcp = new DHCP();
                    dhcp.setMTU(mtu);
                    if (!dns1.equals("")) {
                        dhcp.setDNS(dns1);
                    } else if (!dns2.equals("")) {
                        dhcp.setDNS(dns2);
                    }

                    proto.setDHCP(dhcp);
                        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
                        if (isUserLogin) {
                            if(channels!=null){
                                mHomeGenius.setWan(proto,channels);
                            }
                        } else {
                            ToastSingleShow.showText(this, "未登录，无法设置动态上网,请登录后重试");
                        }


                } else {
                    ToastSingleShow.showText(this, "网络连接不可用");
                }

                break;
        }
    }
}
