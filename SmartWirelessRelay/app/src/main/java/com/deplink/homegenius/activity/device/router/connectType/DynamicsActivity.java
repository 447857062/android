package com.deplink.homegenius.activity.device.router.connectType;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
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
import com.deplink.sdk.android.sdk.json.Proto;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class DynamicsActivity extends Activity implements View.OnClickListener{
    private static final  String TAG="DynamicsActivity";
    private TextView textview_edit;
    private TextView textview_title;
    private FrameLayout image_back;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private EditText edittext_mtu;
    private EditText edittext_dns1;
    private EditText edittext_dns2;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;
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
        routerDevice = (RouterDevice) manager.getDevice(mRouterManager.getRouterDeviceKey());
        manager.addEventCallback(ec);
    }
    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initDatas() {
        textview_title.setText("动态IP");
        textview_edit.setText("保存");
        mRouterManager=RouterManager.getInstance();
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
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op){
                    case RouterDevice.OP_GET_WAN:
                        try {
                            if( routerDevice.getProto().getDHCP()!=null){
                                ToastSingleShow.showText(DynamicsActivity.this,"动态IP设置成功");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (FrameLayout) findViewById(R.id.image_back);
        textview_edit = (TextView) findViewById(R.id.textview_edit);
        edittext_mtu = (EditText) findViewById(R.id.edittext_mtu);
        edittext_dns1 = (EditText) findViewById(R.id.edittext_dns1);
        edittext_dns2 = (EditText) findViewById(R.id.edittext_dns2);
    }
    private boolean isUserLogin;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                if(NetUtil.isNetAvailable(this)){
                    String mtu=edittext_mtu.getText().toString().trim();
                    String dns1=edittext_dns1.getText().toString().trim();
                    String dns2=edittext_dns2.getText().toString().trim();
                    Proto proto=new Proto();
                    DHCP dhcp=new DHCP();
                    dhcp.setMTU(mtu);
                    if(!dns1.equals("")){
                        dhcp.setDNS(dns1);
                    }else  if(!dns2.equals("")){
                        dhcp.setDNS(dns2);
                    }

                    proto.setDHCP(dhcp);
                    if(routerDevice!=null){
                        isUserLogin= Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
                        if(isUserLogin){
                            routerDevice.setWan(proto);
                        }else{
                            ToastSingleShow.showText(this,"未登录，无法设置动态上网,请登录后重试");
                        }

                    }

                }else{
                    ToastSingleShow.showText(this,"网络连接不可用");
                }

                break;
        }
    }
}
