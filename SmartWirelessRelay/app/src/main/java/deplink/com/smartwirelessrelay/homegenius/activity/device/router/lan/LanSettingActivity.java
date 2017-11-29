package deplink.com.smartwirelessrelay.homegenius.activity.device.router.lan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.json.Lan;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetUtil;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.util.StringValidatorUtil;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class LanSettingActivity extends Activity implements View.OnClickListener{
    private FrameLayout layout_back;
    private EditText edittext_ip_address;
    private EditText edittext_submask;
    private EditText edittext_ip_addrss_start;
    private EditText edittext_ip_address_end;
    private RelativeLayout layout_ip_addrss_start;
    private RelativeLayout layout_ip_address_end;
    private CheckBox checkbox_dhcp_switch;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private Button button_cancel;
    private Button button_save;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lan_setting);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(LanSettingActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
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
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {

            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_GET_LAN:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Lan lan=routerDevice.getLan();
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
                                    if(lan.getLANIP().length()>0){
                                        edittext_ip_address.setSelection(lan.getLANIP().length());
                                    }
                                    edittext_submask.setText(lan.getNETMASK());
                                    if(lan.getNETMASK().length()>0){
                                        edittext_submask.setSelection(lan.getNETMASK().length());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
                    case RouterDevice.OP_SUCCESS:
                        if (isSetLan) {
                            ToastSingleShow.showText(LanSettingActivity.this, "设置成功");
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
    private boolean isSetLan;
    @Override
    protected void onResume() {
        super.onResume();
        routerDevice=mRouterManager.getRouterDevice();
        manager.addEventCallback(ec);
        if(NetUtil.isNetAvailable(this)){
            try {
                routerDevice.queryLan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            ToastSingleShow.showText(this,"网络连接已断开");
        }


    }
    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initEvents() {
        layout_back.setOnClickListener(this);
        checkbox_dhcp_switch.setOnCheckedChangeListener(dhcpCheckChangeListener);
        button_cancel.setOnClickListener(this);
        button_save.setOnClickListener(this);
    }
    private String dhcpStatus;
    private CompoundButton.OnCheckedChangeListener dhcpCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                dhcpStatus="ON";
            }else{
                dhcpStatus="OFF";
            }
        }
    };

    private void initViews() {
        layout_back = (FrameLayout) findViewById(R.id.layout_back);
        edittext_ip_address = (EditText) findViewById(R.id.edittext_ip_address);
        edittext_submask = (EditText) findViewById(R.id.edittext_submask);
        edittext_ip_addrss_start = (EditText) findViewById(R.id.edittext_ip_addrss_start);
        edittext_ip_address_end = (EditText) findViewById(R.id.edittext_ip_address_end);
        checkbox_dhcp_switch = (CheckBox) findViewById(R.id.checkbox_dhcp_switch);
        layout_ip_addrss_start = (RelativeLayout) findViewById(R.id.layout_ip_addrss_start);
        layout_ip_address_end = (RelativeLayout) findViewById(R.id.layout_ip_address_end);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_save = (Button) findViewById(R.id.button_save);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
            case R.id.layout_back:
                onBackPressed();
                break;
            case R.id.button_save:
                Lan lan=new Lan();
                String lanIP=edittext_ip_address.getText().toString().trim();
                String submask=edittext_submask.getText().toString().trim();
                if(dhcpStatus.equalsIgnoreCase("ON")){

                    if(!StringValidatorUtil.isIPString(lanIP)){
                        ToastSingleShow.showText(this,"IP地址格式不对");
                        return;
                    }
                    if(!StringValidatorUtil.isIPString(submask)){
                        ToastSingleShow.showText(this,"子网掩码格式不对");
                        return;
                    }

                    lan.setLANIP(lanIP);
                    lan.setNETMASK(submask);

                    String ipStart=edittext_ip_addrss_start.getText().toString().trim();
                    String ipEnd=edittext_ip_address_end.getText().toString().trim();
                    if(Integer.parseInt(ipStart)>254 || ipStart.equals("") || Integer.parseInt(ipStart)==0){
                        ToastSingleShow.showText(this,"输入1-254之间的数字");
                        return;
                    }
                    if(Integer.parseInt(ipEnd)>254 || ipStart.equals("") || Integer.parseInt(ipEnd)==0){
                        ToastSingleShow.showText(this,"输入1-254之间的数字");
                        return;
                    }
                    lan.setIpStart(ipStart);
                    lan.setIpOver(ipEnd);
                    lan.setDhcpStatus("ON");
                    if(NetUtil.isNetAvailable(this)){
                        try {
                            boolean isUserLogin;
                            isUserLogin= Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
                            if(isUserLogin){
                                isSetLan=true;
                                routerDevice.setLan(lan);
                            }else{
                                ToastSingleShow.showText(this,"未登录，无法设置LAN,请登录后重试");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        ToastSingleShow.showText(this,"网络连接已断开");
                    }

                }else{
                    lan.setLANIP(lanIP);
                    lan.setNETMASK(submask);
                    lan.setDhcpStatus("OFF");
                    if(NetUtil.isNetAvailable(this)){
                        try {
                            isSetLan=true;
                            routerDevice.setLan(lan);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        ToastSingleShow.showText(this,"网络连接已断开");
                    }

                }
                break;
        }
    }
}