package com.deplink.homegenius.activity.device.router.firmwareupdate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.remote.HomeGenius;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.bean.DeviceUpgradeInfo;
import com.deplink.sdk.android.sdk.bean.DeviceUpgradeRes;
import com.deplink.sdk.android.sdk.device.router.RouterDevice;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.deplink.sdk.android.sdk.rest.RestfulTools;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateImmediatelyActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "UpdateImmediately";
    private Button button_cancel;
    private Button button_update;
    private SDKManager manager;
    private EventCallback ec;
    private TextView textview_version_code;
    private TextView textview_file_size;
    private TextView textview_update_what;
    private MakeSureDialog connectLostDialog;
    private HomeGenius mHomeGenius;
    private String channels;
    private TextView textview_title;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_immediately);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("固件升级");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(UpdateImmediatelyActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(UpdateImmediatelyActivity.this, LoginActivity.class));
            }
        });
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {

            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_LOAD_UPGRADEINFO:

                        break;
                }
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {

            }



            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
                Log.i(TAG, "设置固件自动升级失败");
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
    private boolean isStartFromExperience;
    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        mHomeGenius = new HomeGenius();
        isStartFromExperience= DeviceManager.getInstance().isStartFromExperience();
        if(!isStartFromExperience){
            channels = mRouterManager.getCurrentSelectedRouter().getRouter().getChannels();
        }

        retrieveUpgradeInfo();

    }
    private DeviceUpgradeInfo deviceUpgradeInfo;
    /**
     * 获取升级信息
     */
    public void retrieveUpgradeInfo() {
        RestfulTools.getSingleton().getDeviceUpgradeInfo(mRouterManager.getCurrentSelectedRouter().getUid(), new Callback<DeviceUpgradeRes>() {
            @Override
            public void onResponse(Call<DeviceUpgradeRes> call, final Response<DeviceUpgradeRes> response) {
                switch (response.code()) {
                    case 200:
                        Log.i(TAG, "retrieveUpgradeInfo=" + response.body().toString() + "response.message()=" + response.message());
                        if (null != response.body().getUpgrade_info()) {
                            Log.i(TAG, "已获取版本升级信息");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, "已获取固件升级信息");
                                    deviceUpgradeInfo = response.body().getUpgrade_info();
                                    textview_version_code.setText("联客路由固件 " + deviceUpgradeInfo.getVersion());
                                    String fileSizeDots = String.valueOf((deviceUpgradeInfo.getFile_len() / 1024 % 1024) / 1024.0);
                                    if (fileSizeDots.contains(".") && fileSizeDots.length() > 4) {
                                        Log.i(TAG, "fileSizeDots=" + fileSizeDots);
                                        fileSizeDots = fileSizeDots.substring(1, 4);
                                    } else {
                                        fileSizeDots = ".0";
                                    }
                                    textview_file_size.setText(deviceUpgradeInfo.getFile_len() / 1024 / 1024 + fileSizeDots + "M");
                                    textview_update_what.setText("联客路由固件 " + deviceUpgradeInfo.getVersion() + " 包含问题修复以及对路 由器安全性的改进。");

                                }
                            });
                        } else {
                            Log.i(TAG, "版本升级信息为空");
                          //  notifySuccess(OP_LOAD_UPGRADEINFONULL);
                        }
                        break;
                }
            }
            @Override
            public void onFailure(Call<DeviceUpgradeRes> call, Throwable t) {
                String error = "读取设备升级信息名称失败";
                Log.i(TAG, "读取设备升级信息名称失败 " + t.getMessage());
             //   notifyFailure(OP_LOAD_UPGRADEINFO, error);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);

    }

    private RouterManager mRouterManager;

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
        button_update.setOnClickListener(this);
    }

    private void initViews() {
        button_cancel = findViewById(R.id.button_cancel);
        button_update = findViewById(R.id.button_update);
        textview_title= findViewById(R.id.textview_title);
        image_back= findViewById(R.id.image_back);
        textview_version_code = findViewById(R.id.textview_version_code);
        textview_file_size = findViewById(R.id.textview_file_size);
        textview_update_what = findViewById(R.id.textview_update_what);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.button_cancel:
                onBackPressed();
                break;
            case R.id.button_update:
                MakeSureDialog dialog = new MakeSureDialog(this);
                dialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {

                            if(channels!=null){
                                mHomeGenius.startUpgrade(deviceUpgradeInfo,channels);
                                startActivity(new Intent(UpdateImmediatelyActivity.this, UpdateStatusActivity.class));
                            }

                    }
                });
                dialog.show();
                dialog.setTitleText("确定进行固件升级");

                break;
        }
    }
}
