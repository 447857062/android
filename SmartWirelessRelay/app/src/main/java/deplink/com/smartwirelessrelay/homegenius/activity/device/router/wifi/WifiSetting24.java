package deplink.com.smartwirelessrelay.homegenius.activity.device.router.wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.json.Wifi;
import com.deplink.sdk.android.sdk.json.Wifi_2G;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetUtil;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class WifiSetting24 extends Activity implements View.OnClickListener{
    private static final String TAG = "WifiSetting24";
    private RelativeLayout layout_wireless_wifi;
    private RelativeLayout layout_hide_net;
    private RelativeLayout layout_encryption;
    private RelativeLayout layout_mode;
    private RelativeLayout layout_channel;
    private RelativeLayout layout_bandwidth;

    private TextView textview_encryption;
    private RelativeLayout layout_password;
    private TextView textview_mode;
    private TextView textview_channel;
    private TextView textview_bandwidth;
    private TextView textview_title;
    private ImageView image_back;
    private CheckBox checkbox_wireless_wifi;
    private CheckBox checkbox_hide_net;
    private boolean isUserLogin;
    private RelativeLayout layout_wifiname_setting;
    private TextView textview_wifi_name;
    private String wifiname;
    private String wifi_password ;
    private String encryptionType;
    private String mode;
    private String channel;
    private String bandwidth;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private Wifi_2G wifiParams;
    /**
     * wifi名称密码设置成功，后面要重启了
     */
    private static final int MSG_LOCAL_OP_RETURN_OK = 1;
    private Button button_cancel;
    private Button button_save;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_setting24);
        initViews();
        initDatas();
        initEvent();

    }

    private void initDatas() {
        textview_title.setText("2.4G WIFI设置");
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(WifiSetting24.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(WifiSetting24.this, LoginActivity.class));
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
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    wifiParams = routerDevice.getWifi().getWifi_2G();
                                    if(!isActivityResultSetWifiPassword){
                                        wifi_password=wifiParams.getWifiPassword();
                                    }
                                    if (wifiParams.getWifiStatus().equalsIgnoreCase("ON")) {
                                        checkbox_wireless_wifi.setChecked(true);
                                    } else {
                                        checkbox_wireless_wifi.setChecked(false);
                                    }
                                    if (wifiParams.getHIDDEN().equalsIgnoreCase("OFF")) {
                                        checkbox_hide_net.setChecked(false);
                                    } else {
                                        checkbox_hide_net.setChecked(true);
                                    }

                                    if (!isActivityResultSetWifiname) {
                                        wifiname = wifiParams.getWifiSSID();
                                    }
                                    Log.i(TAG,"isActivityResultSetWifiname="+isActivityResultSetWifiname+"wifiname="+wifiname);
                                    textview_wifi_name.setText(wifiname);
                                    if (!isActivityResultSetEncryptType) {
                                        encryptionType = wifiParams.getEncryption();
                                    }

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
                                    if (!isActivityResultSetModel) {
                                        mode = wifiParams.getHWMODE();
                                    }
                                    if (mode.equals("")) {

                                        textview_mode.setText("--");
                                    }
                                    switch (mode) {
                                        case "0":
                                            textview_mode.setText("802.11b/g");
                                            break;
                                        case "1":
                                            textview_mode.setText("802.11b");
                                            break;
                                        case "4":
                                            textview_mode.setText("802.11g");
                                            break;
                                        case "7":
                                            textview_mode.setText("802.11g/n");
                                            break;
                                        case "9":
                                            textview_mode.setText("802.11b/g/n");
                                            break;
                                    }
                                    if (!isActivityResultSetChannel) {
                                        channel = wifiParams.getCHANNEL();
                                    }

                                    if (channel.equals("")) {
                                        channel = "--";
                                    }
                                    if (channel.equals("0")) {
                                        textview_channel.setText("自动");
                                    } else {
                                        textview_channel.setText(channel);
                                    }
                                    if (!isActivityResultSetBandwidth) {
                                        bandwidth = wifiParams.getHTMODE();
                                    }

                                    if (bandwidth.equals("")) {
                                        bandwidth = "--";
                                    }
                                    switch (bandwidth) {
                                        case "0":
                                            textview_bandwidth.setText("20M模式");
                                            break;
                                        case "1":
                                            textview_bandwidth.setText("40M模式");
                                            break;

                                    }

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case RouterDevice.OP_SUCCESS:
                        if (isSetWifi2G) {
                            ToastSingleShow.showText(WifiSetting24.this, "设置成功");
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
        routerDevice=mRouterManager.getRouterDevice();
        manager.addEventCallback(ec);
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        // setWifiNameText();
        if (isUserLogin && routerDevice != null && routerDevice.getOnline()) {
            if (NetUtil.isNetAvailable(this)) {
                routerDevice.queryWifi();
            } else {
                ToastSingleShow.showText(this, "网络连接已断开");
            }
            layout_wireless_wifi.setVisibility(View.VISIBLE);
            layout_hide_net.setVisibility(View.VISIBLE);
            layout_encryption.setVisibility(View.VISIBLE);
            layout_mode.setVisibility(View.VISIBLE);
            layout_channel.setVisibility(View.VISIBLE);
            layout_bandwidth.setVisibility(View.VISIBLE);
        } else {
            layout_wireless_wifi.setVisibility(View.GONE);
            layout_hide_net.setVisibility(View.GONE);
            layout_encryption.setVisibility(View.GONE);
            layout_mode.setVisibility(View.GONE);
            layout_channel.setVisibility(View.GONE);
            layout_bandwidth.setVisibility(View.GONE);
            if (!isActivityResultSetWifiname) {
                wifiname= deplink.com.smartwirelessrelay.homegenius.util.Wifi.getConnectedWifiName(WifiSetting24.this);
                wifi_password="12345678";
            }
            textview_wifi_name.setText(wifiname);
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
        layout_mode.setOnClickListener(this);
        layout_channel.setOnClickListener(this);
        layout_bandwidth.setOnClickListener(this);
        image_back.setOnClickListener(this);
        layout_wifiname_setting.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
        button_save.setOnClickListener(this);
        button_save.setText("设置");
    }



    private void initViews() {
        layout_wifiname_setting = (RelativeLayout) findViewById(R.id.layout_wifiname_setting);
        layout_encryption = (RelativeLayout) findViewById(R.id.layout_encryption);
        textview_encryption = (TextView) findViewById(R.id.textview_encryption);
        layout_password = (RelativeLayout) findViewById(R.id.layout_password);
        layout_mode = (RelativeLayout) findViewById(R.id.layout_mode);
        textview_mode = (TextView) findViewById(R.id.textview_mode);
        layout_channel = (RelativeLayout) findViewById(R.id.layout_channel);
        textview_channel = (TextView) findViewById(R.id.textview_channel);
        layout_bandwidth = (RelativeLayout) findViewById(R.id.layout_bandwidth);
        textview_bandwidth = (TextView) findViewById(R.id.textview_bandwidth);
        textview_wifi_name = (TextView) findViewById(R.id.textview_wifi_name);
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (ImageView) findViewById(R.id.image_back);
        checkbox_wireless_wifi = (CheckBox) findViewById(R.id.checkbox_wireless_wifi);
        checkbox_hide_net = (CheckBox) findViewById(R.id.checkbox_hide_net);
        layout_wireless_wifi = (RelativeLayout) findViewById(R.id.layout_wireless_wifi);
        layout_hide_net = (RelativeLayout) findViewById(R.id.layout_hide_net);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_save = (Button) findViewById(R.id.button_save);
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOCAL_OP_RETURN_OK:
                   /* ToastSingleShow.showText(WifiSetting24.this,"已重启路由器");
                    Log.i(TAG, "路由器本地设置成功，重启步骤");
                    RestfulToolsRouter.getSingleton(WifiSetting24.this).rebootRouter(new retrofit2.Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Log.i(TAG, "response.code=" + response.code());
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                    startActivity(new Intent(WifiSetting24.this, SettingActivity.class));*/
                    break;

            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_cancel:
                onBackPressed();
                break;
            case R.id.button_save:
                if (isUserLogin && routerDevice != null && routerDevice.getOnline()) {
                    isSetWifi2G = true;
                    Wifi_2G wifi_2g=new Wifi_2G();
                    Wifi content;
                    if (!wifiname.equals("")) {
                        wifi_2g.setWifiSSID(wifiname);
                    }
                    if (!mode.equals("")) {
                        wifi_2g.setHWMODE(mode);
                    }
                    if (!channel.equals("")) {
                        wifi_2g.setCHANNEL(channel);
                    }
                    if (!bandwidth.equals("")) {
                        wifi_2g.setHTMODE(bandwidth);
                    }
                    if (!wifi_password.equals("")) {
                        wifi_2g.setWifiPassword(wifi_password);
                    }
                    if (!encryptionType.equals("")) {
                        wifi_2g.setEncryption(encryptionType);
                    }
                    if(checkbox_wireless_wifi.isChecked()){
                        wifi_2g.setWifiStatus("ON");
                    }else{
                        wifi_2g.setWifiStatus("OFF");
                    }
                    if(checkbox_hide_net.isChecked()){
                        wifi_2g.setHIDDEN("ON");
                    }else{
                        wifi_2g.setHIDDEN("OFF");
                    }
                    content = new Wifi();
                    content.setWifi_2G(wifi_2g);
                    routerDevice.setWifi(content);

                }else{
                    //本地接口
                  /*  RestfulToolsRouter.getSingleton(this).wifiSignalStrengthSetting(
                            wifiname,
                            wifi_password,
                            new Callback<RouterResponse>() {
                                @Override
                                public void onResponse(Call<RouterResponse> call, Response<RouterResponse> response) {
                                    int code = response.code();
                                    if (code == 200) {
                                        showRebootConfirmDialog();
                                    }
                                }

                                @Override
                                public void onFailure(Call<RouterResponse> call, Throwable t) {
                                    //返回空的对象，获取不到code
                                }
                            });*/
                }
                break;
            case R.id.layout_encryption:

              Intent intent = new Intent(WifiSetting24.this, EncryptTypeActivity.class);
                intent.putExtra(AppConstant.WIFISETTING.WIFI_ENCRYPT_TYPE, encryptionType);
                intent.putExtra(AppConstant.WIFISETTING.WIFI_TYPE, AppConstant.WIFISETTING.WIFI_TYPE_2G);
                startActivityForResult(intent, REQUEST_ENCRYPTION);
                break;
            case R.id.layout_mode:

               Intent intentModeSelect = new Intent(WifiSetting24.this, ModeSelectActivity.class);
                intentModeSelect.putExtra(AppConstant.WIFISETTING.WIFI_MODE_TYPE, mode);
                intentModeSelect.putExtra(AppConstant.WIFISETTING.WIFI_TYPE, AppConstant.WIFISETTING.WIFI_TYPE_2G);
                startActivityForResult(intentModeSelect, REQUEST_MODEL);
                break;
            case R.id.layout_channel:

              Intent intentChannel = new Intent(WifiSetting24.this, ChannelActivity.class);
                intentChannel.putExtra(AppConstant.WIFISETTING.WIFI_CHANNEL_TYPE, channel);
                intentChannel.putExtra(AppConstant.WIFISETTING.WIFI_TYPE, AppConstant.WIFISETTING.WIFI_TYPE_2G);
                startActivityForResult(intentChannel, REQUEST_CHANNEL);
                break;
            case R.id.layout_bandwidth:

               Intent intentBandwidth = new Intent(WifiSetting24.this, BandwidthActivity.class);
                intentBandwidth.putExtra(AppConstant.WIFISETTING.WIFI_BANDWIDTH, bandwidth);
                intentBandwidth.putExtra(AppConstant.WIFISETTING.WIFI_TYPE, AppConstant.WIFISETTING.WIFI_TYPE_2G);
                startActivityForResult(intentBandwidth, REQUEST_BANDWIDTH);
                break;
            case R.id.layout_password:

               Intent intentAlertPassword = new Intent(WifiSetting24.this, AlertWifiPasswordActivity.class);
                intentAlertPassword.putExtra(AppConstant.WIFISETTING.WIFI_TYPE, AppConstant.WIFISETTING.WIFI_TYPE_2G);
                startActivityForResult(intentAlertPassword, REQUEST_WIFIPASSWORD);
                break;
            case R.id.layout_wifiname_setting:

             Intent intentWifiname = new Intent(WifiSetting24.this, WifinameSetActivity.class);
                intentWifiname.putExtra(AppConstant.WIFISETTING.WIFI_NAME, wifiname);
                intentWifiname.putExtra(AppConstant.WIFISETTING.WIFI_TYPE, AppConstant.WIFISETTING.WIFI_TYPE_2G);
                startActivityForResult(intentWifiname, REQUEST_WIFINAME);
                break;
        }
    }
    private void showRebootConfirmDialog() {
        //重启
        MakeSureDialog dialog = new MakeSureDialog(WifiSetting24.this);
        dialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                Message msg = Message.obtain();
                msg.what = MSG_LOCAL_OP_RETURN_OK;
                mHandler.sendMessage(msg);
            }
        });
        dialog.show();
        dialog.setTitleText("设置WIFI成功");
        dialog.setMsg("需要重启设备才能使设置生效，现在重启吗？");

    }
    private static final int REQUEST_WIFINAME = 100;
    private static final int REQUEST_ENCRYPTION = 101;
    private static final int REQUEST_WIFIPASSWORD = 102;
    private static final int REQUEST_MODEL = 103;
    private static final int REQUEST_CHANNEL = 104;
    private static final int REQUEST_BANDWIDTH = 105;
    /**
     * startactivityforresult 返回就不显示查询到的参数，显示用户修改的参数.
     */
    private boolean isActivityResultSetWifiname;
    private boolean isActivityResultSetEncryptType;
    private boolean isActivityResultSetWifiPassword;
    private boolean isActivityResultSetModel;
    private boolean isActivityResultSetChannel;
    private boolean isActivityResultSetBandwidth;
    private boolean isSetWifi2G;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_WIFINAME:
                    isActivityResultSetWifiname = true;
                    wifiname = data.getExtras().getString("wifiname");
                    Log.i(TAG,"onActivityResult wifiname="+wifiname);
                    break;
                case REQUEST_ENCRYPTION:
                    isActivityResultSetEncryptType = true;
                    encryptionType = data.getExtras().getString("encryptionType");
                    break;
                case REQUEST_WIFIPASSWORD:
                    isActivityResultSetWifiPassword = true;
                    wifi_password = data.getExtras().getString("password");
                    break;
                case REQUEST_MODEL:

                    isActivityResultSetModel = true;
                    mode = data.getExtras().getString("model");
                    break;
                case REQUEST_CHANNEL:
                    isActivityResultSetChannel = true;
                    channel = data.getExtras().getString("channel");
                    break;
                case REQUEST_BANDWIDTH:
                    isActivityResultSetBandwidth = true;
                    bandwidth = data.getExtras().getString("bandwidth");
                    break;
            }
        }
    }

}
