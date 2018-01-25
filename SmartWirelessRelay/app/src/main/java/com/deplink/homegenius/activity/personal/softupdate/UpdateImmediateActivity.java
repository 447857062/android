package com.deplink.homegenius.activity.personal.softupdate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.util.APKVersionCodeUtils;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class UpdateImmediateActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "SoftUpdateImmediately";
    private FrameLayout layout_back;
    private Button button_update;
    private SDKManager manager;
    private TextView textview_version_code;
    private TextView textview_file_size;
    private TextView version_explan;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;
    private TextView textview_title;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_immediate);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("软件升级");
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(UpdateImmediateActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(UpdateImmediateActivity.this, LoginActivity.class));
            }
        });
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
                switch (action) {
                    case APPUPDATE:
                        try {
                            version = manager.getAppUpdateInfo().getVersion();
                            desc = manager.getAppUpdateInfo().getDesc();
                            size = manager.getAppUpdateInfo().getSize();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    textview_version_code.setText("升级版本: " + version);
                                    String fileSizeDots=String.valueOf((size/1024%1024)/1024.0);
                                    Log.i(TAG,"fileSizeDots="+fileSizeDots);
                                    if(fileSizeDots.contains(".") && fileSizeDots.length()>4){
                                        fileSizeDots=fileSizeDots.substring(1,4);
                                    }else{
                                        fileSizeDots=".0";
                                    }
                                    textview_file_size.setText("APK大小: "+size/1024/1024+fileSizeDots+"M");
                                    version_explan.setText("" + desc);

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);

            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {

            }

            @Override
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {

                    ToastSingleShow.showText(UpdateImmediateActivity.this,"已是最新版本");


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

    private String version;
    private String desc;
    private int size;
    private Handler mHandler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        manager.queryAppUpdateInfo(Perfence.SDK_APP_KEY, APKVersionCodeUtils.getVerName(this));
        manager.addEventCallback(ec);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }


    private void initEvents() {
        button_update.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        button_update = findViewById(R.id.button_update);
        textview_version_code = findViewById(R.id.textview_version_code);
        textview_file_size = findViewById(R.id.textview_file_size);
        version_explan = findViewById(R.id.version_explan);
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_update:
                DeleteDeviceDialog dialog = new DeleteDeviceDialog(this);
                dialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        // 获取升级的url，查询版本号,启动服务升级
                        Intent updateIntent=new Intent(UpdateImmediateActivity.this, UpdateProgressActivity.class);
                        updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(updateIntent );
                    }
                });
                dialog.show();
                dialog.setTitleText("软件升级");
                dialog.setContentText("确定进行软件升级?");
                break;
        }
    }
}
