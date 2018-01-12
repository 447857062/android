package com.deplink.homegenius.activity.device.router;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.Protocol.json.device.lock.SSIDList;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.edittext.ClearEditText;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.Deviceprops;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class RouterNameUpdateActivity extends Activity implements View.OnClickListener, DeviceListener {
    private ClearEditText edittext_router_name;
    private RouterManager mRouterManager;
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private String deviceName;
    private DeviceManager mDeviceManager;
    private boolean isUserLogin;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_name_update);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initDatas() {
        textview_title.setText("修改名称");
        textview_edit.setText("完成");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this, this);
        if (DeviceManager.getInstance().isStartFromExperience()) {
            deviceName = "体验路由器";
        } else {
            deviceName = mRouterManager.getCurrentSelectedRouter().getName();
        }
        edittext_router_name.setText(deviceName);
        edittext_router_name.setSelection(deviceName.length());
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(RouterNameUpdateActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(RouterNameUpdateActivity.this, LoginActivity.class));
            }
        });

        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
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

            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                isUserLogin = false;
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }
        };
    }

    private void initEvents() {
        textview_edit.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        edittext_router_name = findViewById(R.id.edittext_router_name);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        image_back = findViewById(R.id.image_back);
    }

    private String routerName;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.textview_edit:
                routerName = edittext_router_name.getText().toString();
                if (!routerName.equals("")) {
                    if (mDeviceManager.isStartFromExperience()) {

                    } else {
                        if (isUserLogin) {
                            mDeviceManager.alertDeviceHttp(
                                    mRouterManager.getCurrentSelectedRouter().getUid(),
                                    null,
                                    routerName,
                                    null
                            );
                        } else {
                            ToastSingleShow.showText(this, "登录后才能操作");
                        }

                    }

                } else {
                    Toast.makeText(this, "请输入路由器名称", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private static final int MSG_UPDATE_NAME_FAIL = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_NAME_FAIL:
                    Toast.makeText(RouterNameUpdateActivity.this, "更新路由器名称失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseBindDeviceResult(String result) {

    }

    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {

    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }

    @Override
    public void responseAddDeviceHttpResult(DeviceOperationResponse deviceOperationResponse) {

    }

    @Override
    public void responseDeleteDeviceHttpResult(DeviceOperationResponse result) {

    }

    @Override
    public void responseAlertDeviceHttpResult(DeviceOperationResponse result) {
        int saveResult = mRouterManager.updateRouterName(routerName);
        if (saveResult > 0) {
            mRouterManager.getCurrentSelectedRouter().setName(routerName);
            RouterNameUpdateActivity.this.finish();
        } else {
            Message msg = Message.obtain();
            msg.what = MSG_UPDATE_NAME_FAIL;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void responseGetDeviceInfoHttpResult(String result) {

    }

    @Override
    public void responseQueryHttpResult(List<Deviceprops> devices) {

    }
}
