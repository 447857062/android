package deplink.com.smartwirelessrelay.homegenius.activity.device.router.wifi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.json.VISITOR;
import com.deplink.sdk.android.sdk.json.Wifi;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetUtil;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class WifiSettingCustom extends Activity implements View.OnClickListener{
    private static final String TAG = "WifiSettingCustom";
    private RelativeLayout layout_encryption;
    private RelativeLayout layout_password;
    private RelativeLayout layout_wifiname_setting;
    private TextView textview_encryption;
    private TextView textview_wifi_name;
    private TextView textview_password;
    private TextView textview_title;
    private FrameLayout image_back;
    private String encryptionType;
    private String password;
    private String wifiName;
    private String wifiSwitch;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private CheckBox checkbox_wifi_switch;
  private TextView textview_edit;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_setting_custom);
        initViews();
        initDatas();
        initEvent();


    }

    private void initDatas() {
        textview_title.setText("访客WIFI设置");
        textview_edit.setText("保存");
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(WifiSettingCustom.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(WifiSettingCustom.this, LoginActivity.class));
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
                    case RouterDevice.OP_GET_WIFI:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isActivityResultSetWifiname) {
                                    wifiName = routerDevice.getWifi().getVISITOR().getWifiSSID();
                                }
                                if (!isActivityResultSetEncryptType) {
                                    encryptionType = routerDevice.getWifi().getVISITOR().getEncryption();
                                }

                                textview_wifi_name.setText(wifiName);

                                if (encryptionType.equals("")) {
                                    encryptionType = "--";
                                }
                                switch (encryptionType) {
                                    case "none":
                                        textview_encryption.setText("无加密");
                                        break;
                                    case "psk-mixed":
                                        textview_encryption.setText("混合加密");
                                        break;
                                    case "psk2":
                                        textview_encryption.setText("强加密");
                                        break;
                                }
                                if(encryptionType.equalsIgnoreCase("none")){
                                    password="";
                                    textview_password.setText("");
                                }else{
                                    if (!isActivityResultSetWifiPassword) {
                                        password = routerDevice.getWifi().getVISITOR().getWifiPassword();
                                    }
                                    textview_password.setText(password);
                                }


                                wifiSwitch = routerDevice.getWifi().getVISITOR().getWifiStatus();
                                Log.i(TAG, "wifiSwitch=" + wifiSwitch);
                                if (wifiSwitch.equalsIgnoreCase("ON")) {
                                    checkbox_wifi_switch.setChecked(true);
                                } else {
                                    checkbox_wifi_switch.setChecked(false);
                                }
                            }
                        });
                        break;
                    case RouterDevice.OP_SUCCESS:
                        if (isSetWifiVisitor) {
                            ToastSingleShow.showText(WifiSettingCustom.this, "设置成功");
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mRouterManager.isStartFromExperience()){

        }else{
            routerDevice = (RouterDevice) manager.getDevice(mRouterManager.getRouterDeviceKey());
            manager.addEventCallback(ec);
            if (NetUtil.isNetAvailable(this)) {
                try {
                    Log.i(TAG, "onResume queryWifi");
                    routerDevice.queryWifi();
                } catch (Exception e) {
                    e.printStackTrace();
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

    private void initEvent() {
        layout_encryption.setOnClickListener(this);
        layout_password.setOnClickListener(this);
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);


        layout_wifiname_setting.setOnClickListener(this);
        checkbox_wifi_switch.setOnCheckedChangeListener(wifiSwitchCheckChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener wifiSwitchCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            VISITOR visitor = new VISITOR();
            if (isChecked) {
                visitor.setWifiStatus("ON");
            } else {
                visitor.setWifiStatus("OFF");
            }
        }
    };

    private void initViews() {
        layout_encryption = (RelativeLayout) findViewById(R.id.layout_encryption);
        layout_password = (RelativeLayout) findViewById(R.id.layout_password);
        layout_wifiname_setting = (RelativeLayout) findViewById(R.id.layout_wifiname_setting);
        textview_encryption = (TextView) findViewById(R.id.textview_encryption);
        textview_wifi_name = (TextView) findViewById(R.id.textview_wifi_name);
        textview_password = (TextView) findViewById(R.id.textview_password);
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (FrameLayout) findViewById(R.id.image_back);
        checkbox_wifi_switch = (CheckBox) findViewById(R.id.checkbox_wifi_switch);
        textview_edit = (TextView) findViewById(R.id.textview_edit);
    }

    /**
     * startactivityforresult 返回就不显示查询到的参数，显示用户修改的参数.
     */
    private boolean isActivityResultSetWifiname;
    private boolean isActivityResultSetEncryptType;
    private boolean isActivityResultSetWifiPassword;
    private static final int REQUESTCODE_SET_WIFI_NAME = 100;
    private static final int REQUESTCODE_SET_ENCRYPT_TYPE = 101;
    private static final int REQUESTCODE_SET_WIFI_PASSWORD = 102;
    private boolean isSetWifiVisitor;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.textview_edit:
                VISITOR visitor = new VISITOR();
                Wifi content;
                if (!wifiName.equals("")) {
                    visitor.setWifiSSID(wifiName);
                }
                if (!wifiSwitch.equals("")) {
                    visitor.setWifiStatus(wifiSwitch);
                }
                if (!password.equals("")) {
                    visitor.setWifiPassword(password);
                }
                if( checkbox_wifi_switch.isChecked()){
                    visitor.setWifiStatus("ON");
                }else{
                    visitor.setWifiStatus("OFF");
                }

                if (!encryptionType.equals("")) {
                    visitor.setEncryption(encryptionType);
                }
                content = new Wifi();
                content.setVISITOR(visitor);
                if (routerDevice != null) {
                    isSetWifiVisitor = true;
                    routerDevice.setWifi(content);
                }


                break;
            case R.id.layout_encryption:
                if (wifiSwitch != null) {
                    if (wifiSwitch.equals("ON")) {
                        Intent intent = new Intent(WifiSettingCustom.this, EncryptTypeActivity.class);
                        intent.putExtra(AppConstant.WIFISETTING.WIFI_ENCRYPT_TYPE, encryptionType);
                        intent.putExtra(AppConstant.WIFISETTING.WIFI_TYPE, AppConstant.WIFISETTING.WIFI_TYPE_VISITOR);
                        startActivityForResult(intent, REQUESTCODE_SET_ENCRYPT_TYPE);
                    } else {
                        ToastSingleShow.showText(this, "访客wifi未打开");
                    }
                } else {
                    ToastSingleShow.showText(this, "未获取到访客wifi状态");
                }


                break;
            case R.id.layout_wifiname_setting:
                if (wifiSwitch != null) {
                    if (wifiSwitch.equals("ON")) {
                        Intent intentWifiname = new Intent(WifiSettingCustom.this, WifinameSetActivity.class);
                        intentWifiname.putExtra(AppConstant.WIFISETTING.WIFI_NAME, wifiName);
                        intentWifiname.putExtra(AppConstant.WIFISETTING.WIFI_TYPE, AppConstant.WIFISETTING.WIFI_TYPE_VISITOR);
                        startActivityForResult(intentWifiname, REQUESTCODE_SET_WIFI_NAME);
                    } else {
                        ToastSingleShow.showText(this, "访客wifi未打开");
                    }
                } else {
                    ToastSingleShow.showText(this, "还未获取到访客wifi状态");
                }


                break;
            case R.id.layout_password:
                if (wifiSwitch != null) {
                    if (wifiSwitch.equals("ON")) {
                        Intent intentAlertPassword = new Intent(WifiSettingCustom.this, AlertWifiPasswordActivity.class);
                        intentAlertPassword.putExtra(AppConstant.WIFISETTING.WIFI_TYPE, AppConstant.WIFISETTING.WIFI_TYPE_VISITOR);
                        intentAlertPassword.putExtra(AppConstant.WIFISETTING.WIFI_PASSWORD, password);
                        startActivityForResult(intentAlertPassword, REQUESTCODE_SET_WIFI_PASSWORD);
                    } else {
                        ToastSingleShow.showText(this, "访客wifi未打开");
                    }
                } else {
                    ToastSingleShow.showText(this, "还未获取到访客wifi状态");
                }


                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_SET_WIFI_NAME:
                    isActivityResultSetWifiname = true;
                    wifiName = data.getExtras().getString("wifiname");
                    Log.i(TAG, "onActivityResult wifiname=" + wifiName);
                    break;
                case REQUESTCODE_SET_WIFI_PASSWORD:
                    isActivityResultSetWifiPassword = true;
                    password = data.getExtras().getString("password");
                    Log.i(TAG, "onActivityResult password=" + password);
                    break;
                case REQUESTCODE_SET_ENCRYPT_TYPE:
                    isActivityResultSetEncryptType = true;
                    encryptionType = data.getExtras().getString("encryptionType");
                    Log.i(TAG, "onActivityResult encryptionType=" + encryptionType);
                    break;
            }
        }
    }
}
