package com.deplink.homegenius.activity.device.router.lan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.remote.HomeGenius;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.util.NetUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.util.StringValidatorUtil;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.router.RouterDevice;
import com.deplink.sdk.android.sdk.json.Lan;
import com.deplink.sdk.android.sdk.json.PERFORMANCE;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class LanSettingActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LanSettingActivity";
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private EditText edittext_ip_address;
    private EditText edittext_submask;
    private EditText edittext_ip_addrss_start;
    private EditText edittext_ip_address_end;
    private RelativeLayout layout_ip_addrss_start;
    private RelativeLayout layout_ip_address_end;
    private CheckBox checkbox_dhcp_switch;
    private SDKManager manager;
    private EventCallback ec;

    private DeleteDeviceDialog connectLostDialog;
    private RouterManager mRouterManager;
    private HomeGenius mHomeGenius;
    private String channels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lan_setting);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("LAN设置");
        textview_edit.setText("保存");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new DeleteDeviceDialog(LanSettingActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(LanSettingActivity.this, LoginActivity.class));
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
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_GET_LAN:

                        break;
                    case RouterDevice.OP_SUCCESS:

                        break;
                }
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
                if (isSetLan) {
                    ToastSingleShow.showText(LanSettingActivity.this, "设置成功");
                }
            }
        }  else if (op.equalsIgnoreCase("LAN")) {
            if (method.equalsIgnoreCase("REPORT")) {
                Lan lan = gson.fromJson(xmlStr, Lan.class);
                Log.i(TAG, "get lan =" + lan.toString());
                if (lan.getDhcpStatus().equalsIgnoreCase("ON")) {
                    checkbox_dhcp_switch.setChecked(true);
                    layout_ip_addrss_start.setVisibility(View.VISIBLE);
                    layout_ip_address_end.setVisibility(View.VISIBLE);
                    edittext_ip_addrss_start.setText(lan.getIpStart());
                    edittext_ip_address_end.setText(lan.getIpOver());
                } else if (lan.getDhcpStatus().equalsIgnoreCase("OFF")) {
                    checkbox_dhcp_switch.setChecked(false);
                    layout_ip_addrss_start.setVisibility(View.GONE);
                    layout_ip_address_end.setVisibility(View.GONE);
                }

                edittext_ip_address.setText(lan.getLANIP());
                if (lan.getLANIP().length() > 0) {
                    edittext_ip_address.setSelection(lan.getLANIP().length());
                }
                edittext_submask.setText(lan.getNETMASK());
                if (lan.getNETMASK().length() > 0) {
                    edittext_submask.setSelection(lan.getNETMASK().length());
                }
            }
        }
    }

    private boolean isSetLan;
    private boolean isStartFromExperience;
    @Override
    protected void onResume() {
        super.onResume();
        if (!DeviceManager.getInstance().isStartFromExperience()) {
            mHomeGenius = new HomeGenius();
            isStartFromExperience= DeviceManager.getInstance().isStartFromExperience();
            if(!isStartFromExperience){
                channels = mRouterManager.getCurrentSelectedRouter().getRouter().getChannels();
            }

            manager.addEventCallback(ec);
            if (NetUtil.isNetAvailable(this)) {
                if (channels != null) {
                    mHomeGenius.queryLan(channels);
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
        checkbox_dhcp_switch.setOnCheckedChangeListener(dhcpCheckChangeListener);
        textview_edit.setOnClickListener(this);
    }

    private String dhcpStatus;
    private CompoundButton.OnCheckedChangeListener dhcpCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                dhcpStatus = "ON";
            } else {
                dhcpStatus = "OFF";
            }
        }
    };

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        edittext_ip_address = findViewById(R.id.edittext_ip_address);
        edittext_submask = findViewById(R.id.edittext_submask);
        edittext_ip_addrss_start = findViewById(R.id.edittext_ip_addrss_start);
        edittext_ip_address_end = findViewById(R.id.edittext_ip_address_end);
        checkbox_dhcp_switch = findViewById(R.id.checkbox_dhcp_switch);
        layout_ip_addrss_start = findViewById(R.id.layout_ip_addrss_start);
        layout_ip_address_end = findViewById(R.id.layout_ip_address_end);
        textview_edit = findViewById(R.id.textview_edit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                Lan lan = new Lan();
                String lanIP = edittext_ip_address.getText().toString().trim();
                String submask = edittext_submask.getText().toString().trim();
                if (dhcpStatus.equalsIgnoreCase("ON")) {

                    if (!StringValidatorUtil.isIPString(lanIP)) {
                        ToastSingleShow.showText(this, "IP地址格式不对");
                        return;
                    }
                    if (!StringValidatorUtil.isIPString(submask)) {
                        ToastSingleShow.showText(this, "子网掩码格式不对");
                        return;
                    }

                    lan.setLANIP(lanIP);
                    lan.setNETMASK(submask);

                    String ipStart = edittext_ip_addrss_start.getText().toString().trim();
                    String ipEnd = edittext_ip_address_end.getText().toString().trim();
                    if (Integer.parseInt(ipStart) > 254 || ipStart.equals("") || Integer.parseInt(ipStart) == 0) {
                        ToastSingleShow.showText(this, "输入1-254之间的数字");
                        return;
                    }
                    if (Integer.parseInt(ipEnd) > 254 || ipStart.equals("") || Integer.parseInt(ipEnd) == 0) {
                        ToastSingleShow.showText(this, "输入1-254之间的数字");
                        return;
                    }
                    lan.setIpStart(ipStart);
                    lan.setIpOver(ipEnd);
                    lan.setDhcpStatus("ON");
                    if (NetUtil.isNetAvailable(this)) {
                        try {
                            boolean isUserLogin;
                            isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
                            if (isUserLogin) {
                                isSetLan = true;
                                mHomeGenius.setLan(lan, channels);
                            } else {
                                ToastSingleShow.showText(this, "未登录，无法设置LAN,请登录后重试");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastSingleShow.showText(this, "网络连接已断开");
                    }

                } else {
                    lan.setLANIP(lanIP);
                    lan.setNETMASK(submask);
                    lan.setDhcpStatus("OFF");
                    if (NetUtil.isNetAvailable(this)) {

                        isSetLan = true;
                        mHomeGenius.setLan(lan, channels);

                    } else {
                        ToastSingleShow.showText(this, "网络连接已断开");
                    }

                }
                break;
        }
    }
}
