package deplink.com.smartwirelessrelay.homegenius.activity.device.router.connectType;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.json.PPPOE;
import com.deplink.sdk.android.sdk.json.Proto;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.deplink.sdk.android.sdk.rest.ErrorResponse;
import com.deplink.sdk.android.sdk.rest.RestfulToolsRouter;
import com.deplink.sdk.android.sdk.rest.RouterResponse;
import com.google.gson.Gson;

import java.io.IOException;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.wifi.WifiSetting24;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetUtil;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialConnectActivity extends Activity implements View.OnClickListener {
    private TextView textview_title;
    private FrameLayout image_back;
    private EditText edittext_account;
    private EditText edittext_password;
    private EditText edittext_dns;
    private EditText edittext_mtu;
    private EditText edittext_mac;
    private SDKManager manager;
    private TextView textview_edit;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial_connect);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("宽带拨号");
        textview_edit.setText("保存");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(DialConnectActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(DialConnectActivity.this, LoginActivity.class));
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (routerDevice != null) {
                                    Proto proto = routerDevice.getProto();
                                    if (proto.getPPPOE() != null) {
                                        ToastSingleShow.showText(DialConnectActivity.this, "拨号上网设置成功");
                                    }
                                }

                            }
                        });
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

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        textview_title = (TextView) findViewById(R.id.textview_title);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        edittext_account = (EditText) findViewById(R.id.edittext_account);
        edittext_password = (EditText) findViewById(R.id.edittext_password);
        edittext_dns = (EditText) findViewById(R.id.edittext_dns);
        edittext_mtu = (EditText) findViewById(R.id.edittext_mtu);
        edittext_mac = (EditText) findViewById(R.id.edittext_mac);
        textview_edit = (TextView) findViewById(R.id.textview_edit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        routerDevice = (RouterDevice) manager.getDevice(mRouterManager.getRouterDeviceKey());
        manager.addEventCallback(ec);
    }


    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private boolean isUserLogin;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                String account = edittext_account.getText().toString().trim();
                String password = edittext_password.getText().toString();
                String dns = edittext_dns.getText().toString();
                String mtu = edittext_mtu.getText().toString().trim();
                String mac = edittext_mac.getText().toString().trim();
                if (NetUtil.isNetAvailable(this)) {
                    if (account.equals("") || password.equals("")) {
                        ToastSingleShow.showText(this, "账号，密码不能为空");
                        return;
                    }
                    Proto proto = new Proto();
                    PPPOE pppoe = new PPPOE();
                    pppoe.setUsername(account);
                    pppoe.setPasswd(password);
                    pppoe.setDNS(dns);
                    pppoe.setMAC(mac);
                    pppoe.setMTU(mtu);
                    proto.setPPPOE(pppoe);
                    if (mRouterManager.getCurrentSelectedRouter().getStatus().equals("在线")) {
                        if (routerDevice != null) {
                            isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
                            if (isUserLogin) {
                                routerDevice.setWan(proto);
                            } else {
                                ToastSingleShow.showText(this, "未登录，无法设置拨号上网,请登录后重试");
                            }

                        }
                    } else {
                        RestfulToolsRouter.getSingleton(this).internetAccess(account, password, new Callback<RouterResponse>() {
                            @Override
                            public void onResponse(Call<RouterResponse> call, Response<RouterResponse> response) {
                                int code = response.code();
                                if (code != 200) {
                                    String errorMsg = "";
                                    try {
                                        String text = response.errorBody().string();
                                        Gson gson = new Gson();
                                        ErrorResponse errorResponse = gson.fromJson(text, ErrorResponse.class);

                                        switch (errorResponse.getErrcode()) {
                                            case AppConstant.ERROR_CODE.OP_ERRCODE_BAD_TOKEN:
                                                ToastSingleShow.showText(DialConnectActivity.this, "登录的Token已过期");
                                                startActivity(new Intent(DialConnectActivity.this, LoginActivity.class));
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
                                    ToastSingleShow.showText(DialConnectActivity.this, errorMsg);
                                } else {
                                    startActivity(new Intent(DialConnectActivity.this, WifiSetting24.class));
                                }
                            }

                            @Override
                            public void onFailure(Call<RouterResponse> call, Throwable t) {
                            }
                        });
                    }

                } else {
                    ToastSingleShow.showText(this, "网络连接已断开");
                }
                break;
        }
    }
}
