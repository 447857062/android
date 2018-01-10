package com.deplink.homegenius.activity.device.router.connectType;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.homegenius.activity.device.router.wifi.WifiSetting24;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.dialog.MakeSureWithInputDialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.router.RouterDevice;
import com.deplink.sdk.android.sdk.json.Proto;
import com.deplink.sdk.android.sdk.json.STATIC;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.deplink.sdk.android.sdk.rest.ErrorResponse;
import com.deplink.sdk.android.sdk.rest.RestfulToolsRouter;
import com.deplink.sdk.android.sdk.rest.RouterResponse;
import com.google.gson.Gson;

import java.io.IOException;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.util.NetUtil;
import com.deplink.homegenius.util.StringValidatorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaticConnectActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "StaticConnectActivity";
    private TextView textview_title;
    private FrameLayout image_back;
    private TextView textview_edit;
    private EditText ip_address;
    private EditText edittext_submask;
    private EditText edittext_getway;
    private EditText edittext_dns1;
    private EditText edittext_dns2;

    private String op_type;
    private EditText edittext_mtu;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private boolean isSetStaticConnect;
    //本地设置需要的参数
    private String ipaddress;
    private String submask;
    private String getway;
    private String dns1;
    private String mtu;
    private MakeSureDialog connectLostDialog;
    //本地设置需要的参数 end
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_connect);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("静态IP");
        textview_edit.setText("保存");
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(StaticConnectActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(StaticConnectActivity.this, LoginActivity.class));
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
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_GET_WAN:
                        try {
                            if (routerDevice.getProto().getSTATIC() != null) {
                                ToastSingleShow.showText(StaticConnectActivity.this, "动态IP设置成功");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    case RouterDevice.OP_SUCCESS:
                        if (isSetStaticConnect) {
                            ToastSingleShow.showText(StaticConnectActivity.this, "设置成功");
                        }
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

    private boolean isUserLogin;
    @Override
    protected void onResume() {
        super.onResume();
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        if(isUserLogin){
            getRouterDevice();
        }

        manager.addEventCallback(ec);

    }

    private void getRouterDevice() {
        String currentDevcieKey = Perfence.getPerfence(AppConstant.DEVICE.CURRENT_DEVICE_KEY);
        if (currentDevcieKey.equals("")) {
            if (manager.getDeviceList() != null && manager.getDeviceList().size() > 0) {
                Perfence.setPerfence(AppConstant.DEVICE.CURRENT_DEVICE_KEY, manager.getDeviceList().get(0).getDeviceKey());
            } else {
                ToastSingleShow.showText(this, "还没有绑定设备");
            }
        }
        routerDevice = (RouterDevice) manager.getDevice(Perfence.getPerfence(AppConstant.DEVICE.CURRENT_DEVICE_KEY));
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
        op_type = getIntent().getStringExtra(AppConstant.OPERATION_TYPE);
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (FrameLayout) findViewById(R.id.image_back);
        textview_edit = (TextView) findViewById(R.id.textview_edit);
        ip_address = (EditText) findViewById(R.id.edittext_ip_address);
        edittext_submask = (EditText) findViewById(R.id.edittext_submask);
        edittext_getway = (EditText) findViewById(R.id.edittext_getway);
        RelativeLayout layout_dns2 = (RelativeLayout) findViewById(R.id.layout_dns2);
        edittext_mtu = (EditText) findViewById(R.id.edittext_mtu);
        edittext_dns1 = (EditText) findViewById(R.id.edittext_dns1);
        edittext_dns2 = (EditText) findViewById(R.id.edittext_dns2);
        if (op_type != null && op_type.equals(AppConstant.OPERATION_TYPE_LOCAL)) {
            layout_dns2.setVisibility(View.GONE);
        } else {
            layout_dns2.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.textview_edit:
                ipaddress = ip_address.getText().toString().trim();
                submask = edittext_submask.getText().toString().trim();
                getway = edittext_getway.getText().toString().trim();
                dns1 = edittext_dns1.getText().toString().trim();

                mtu = edittext_mtu.getText().toString().trim();
                if (mtu.equals("1500(默认)")) {
                    mtu = "1500";
                }
                String dns2 = edittext_dns2.getText().toString().trim();
                if (!StringValidatorUtil.isIPString(ipaddress)) {
                    ToastSingleShow.showText(StaticConnectActivity.this, "输入的ip地址格式不正确");
                    return;
                }
                if (!StringValidatorUtil.isIPString(submask)) {
                    ToastSingleShow.showText(StaticConnectActivity.this, "输入的子网掩码格式不正确");
                    return;
                }
                if (!StringValidatorUtil.isIPString(getway)) {
                    ToastSingleShow.showText(StaticConnectActivity.this, "输入的默认网关地址格式不正确");
                    return;
                }
                Log.i(TAG, "ipaddress=" + ipaddress + "submask=" + submask + "getway=" + getway + "dns1=" + dns1);
                if (op_type != null && op_type.equals(AppConstant.OPERATION_TYPE_LOCAL)) {
                    if (NetUtil.isNetAvailable(StaticConnectActivity.this)) {
                        setStaticConnectLocal(ipaddress, submask, getway, dns1);
                    } else {
                        ToastSingleShow.showText(StaticConnectActivity.this, "请确保连接上想配置路由器的wifi");
                    }

                } else {
                    //MQTT接口
                    if (NetUtil.isNetAvailable(StaticConnectActivity.this)) {
                        if (routerDevice != null) {
                            isSetStaticConnect = true;
                            Proto proto = new Proto();
                            STATIC static_ = new STATIC();
                            static_.setIPADDR(ipaddress);
                            static_.setNETMASK(submask);
                            static_.setGATEWAY(getway);
                            static_.setMTU(mtu);
                            if (!StringValidatorUtil.isIPString(dns1)) {
                                if (!dns1.equals("")) {
                                    ToastSingleShow.showText(StaticConnectActivity.this, "DNS1 不是有效的Ip地址,已忽略DNS1");
                                }
                                //return;
                            } else if (!StringValidatorUtil.isIPString(dns2)) {
                                if (!dns2.equals("")) {
                                    ToastSingleShow.showText(StaticConnectActivity.this, "DNS1 不是有效的Ip地址,已忽略DNS1");
                                }
                            } else {
                                if (!dns1.equals("")) {
                                    static_.setDNS(dns1);
                                } else if (!dns2.equals("")) {
                                    static_.setDNS(dns2);
                                }

                            }
                            proto.setSTATIC(static_);
                            boolean isUserLogin;
                            isUserLogin= Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
                            if(isUserLogin){
                                routerDevice.setWan(proto);
                            }else{
                                ToastSingleShow.showText(this,"未登录，无法设置静态上网,请登录后重试");
                            }

                        }

                    } else {
                        ToastSingleShow.showText(StaticConnectActivity.this, "网络连接已断开");
                    }

                }
                break;
        }
    }

    /**
     * 检查使用本地接口的本地路由器连接情况
     */
    private boolean isConnectLocalRouter = false;
    private Handler mHandler = new Handler();
    private Runnable connectStatus = new Runnable() {
        @Override
        public void run() {
            if (!isConnectLocalRouter) {
                MakeSureWithInputDialog dialog = new MakeSureWithInputDialog(StaticConnectActivity.this, MakeSureWithInputDialog.DIALOG_TYPE_WIFI_CONNECTED);
                dialog.setSureBtnClickListener(new MakeSureWithInputDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked(String password) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                dialog.show();
            }
        }
    };




    private void setStaticConnectLocal(String ipaddress, String submask, String getway, String dns1) {

       RestfulToolsRouter.getSingleton(StaticConnectActivity.this).staticIp(ipaddress, submask, getway, dns1, new Callback<RouterResponse>() {
            @Override
            public void onResponse(Call<RouterResponse> call, Response<RouterResponse> response) {
                int code = response.code();
                Log.i(TAG, "getSingleton code=" + code);
                RouterResponse result = response.body();
                Log.i(TAG, "getSingleton RouterResponse=" + result.toString());
                if (code != 200) {
                    String errorMsg = "";
                    try {
                        String text = response.errorBody().string();
                        Gson gson = new Gson();
                        ErrorResponse errorResponse = gson.fromJson(text, ErrorResponse.class);
                        switch (errorResponse.getErrcode()) {
                            case AppConstant.ERROR_CODE.OP_ERRCODE_BAD_TOKEN:
                                text = AppConstant.ERROR_MSG.OP_ERRCODE_BAD_TOKEN;
                                ToastSingleShow.showText(StaticConnectActivity.this, "登录失效 :" + text);
                                startActivity(new Intent(StaticConnectActivity.this, LoginActivity.class));
                                return;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_BAD_ACCOUNT:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_BAD_ACCOUNT;
                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_LOGIN_FAIL:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_LOGIN_FAIL;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_NOT_FOUND:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_NOT_FOUND;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_LOGIN_FAIL_MAX:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_LOGIN_FAIL_MAX;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_CAPTCHA_INCORRECT:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_CAPTCHA_INCORRECT;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_PASSWORD_INCORRECT:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_PASSWORD_INCORRECT;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_PASSWORD_SHORT:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_PASSWORD_SHORT;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_BAD_ACCOUNT_INFO:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_BAD_ACCOUNT_INFO;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_DB_TRANSACTION_ERROR:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_DB_TRANSACTION_ERROR;

                                break;
                            default:
                                errorMsg = errorResponse.getMsg();
                                break;

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ToastSingleShow.showText(StaticConnectActivity.this, errorMsg);
                } else {
                    ToastSingleShow.showText(StaticConnectActivity.this, "静态IP设置成功，请设置wifi名字密码");
                    Intent intentWifiSetting = new Intent(StaticConnectActivity.this, WifiSetting24.class);
                    intentWifiSetting.putExtra(AppConstant.OPERATION_TYPE, AppConstant.OPERATION_TYPE_LOCAL);
                    startActivity(intentWifiSetting);
                }

            }

            @Override
            public void onFailure(Call<RouterResponse> call, Throwable t) {

            }
        });
    }
}
