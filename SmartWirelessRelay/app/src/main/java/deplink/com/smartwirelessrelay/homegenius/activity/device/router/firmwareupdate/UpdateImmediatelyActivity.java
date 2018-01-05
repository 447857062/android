package deplink.com.smartwirelessrelay.homegenius.activity.device.router.firmwareupdate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.bean.DeviceUpgradeInfo;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;

public class UpdateImmediatelyActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "UpdateImmediately";
    private Button button_cancel;
    private Button button_update;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private TextView textview_version_code;
    private TextView textview_file_size;
    private TextView textview_update_what;
    private MakeSureDialog connectLostDialog;


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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "已获取固件升级信息");
                                DeviceUpgradeInfo info = routerDevice.getUpgradeInfo();
                                textview_version_code.setText("联客路由固件 " + info.getVersion());
                                String fileSizeDots = String.valueOf((info.getFile_len() / 1024 % 1024) / 1024.0);
                                if (fileSizeDots.contains(".") && fileSizeDots.length() > 4) {
                                    Log.i(TAG, "fileSizeDots=" + fileSizeDots);
                                    fileSizeDots = fileSizeDots.substring(1, 4);
                                } else {
                                    fileSizeDots = ".0";
                                }
                                textview_file_size.setText(info.getFile_len() / 1024 / 1024 + fileSizeDots + "M");
                                textview_update_what.setText("联客路由固件 " + info.getVersion() + " 包含问题修复以及对路 由器安全性的改进。");

                            }
                        });
                        break;
                }
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {

            }

            @Override
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

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

    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        routerDevice = (RouterDevice) manager.getDevice(mRouterManager.getRouterDeviceKey());
        try {
            routerDevice.retrieveUpgradeInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        try {
                            routerDevice.startUpgrade();
                            startActivity(new Intent(UpdateImmediatelyActivity.this, UpdateStatusActivity.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
                dialog.setTitleText("确定进行固件升级");

                break;
        }
    }
}
