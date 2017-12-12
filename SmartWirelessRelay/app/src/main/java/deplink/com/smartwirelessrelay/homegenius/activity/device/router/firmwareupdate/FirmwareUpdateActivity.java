package deplink.com.smartwirelessrelay.homegenius.activity.device.router.firmwareupdate;

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
import com.deplink.sdk.android.sdk.bean.DeviceUpgradeInfo;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class FirmwareUpdateActivity extends Activity implements View.OnClickListener{
    private static final String TAG="FirmwareUpdateActivity";
    private TextView textview_title;
    private FrameLayout image_back;
    private RelativeLayout layout_update_immediately;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private CheckBox checkbox_auto_update;
    private boolean canEnterUpdate=false;
    private TextView textview_show_can_update;
    private TextView  textview_version_code;
    private MakeSureDialog connectLostDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("固件升级");
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(FirmwareUpdateActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(FirmwareUpdateActivity.this, LoginActivity.class));
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
                Log.i(TAG,"设置固件自动升级失败");
            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_CHANGE_AUTO_UPGRADE:
                        Log.i(TAG, "设置固件自动升级成功");
                        break;
                    case RouterDevice.OP_LOAD_UPGRADEINFONULL:
                        textview_show_can_update.setText("已是最新版本");
                        canEnterUpdate = false;
                        break;
                    case RouterDevice.OP_LOAD_UPGRADEINFO:
                        DeviceUpgradeInfo info = routerDevice.getUpgradeInfo();
                        if (info.getUpgrade_state().equalsIgnoreCase("ready")) {
                            textview_show_can_update.setText("立即升级");
                            canEnterUpdate = true;
                        }else{
                            textview_show_can_update.setText("已是最新版本");
                            canEnterUpdate = false;
                        }

                        break;
                    case RouterDevice.OP_GET_REPORT:
                        textview_version_code.setText("当前版本:" + routerDevice.getPerformance().getDevice().getFWVersion());
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
    private RouterManager mRouterManager;
    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        //获取当前连接设备的是否自动升级固件，
        //当前选择的设备判断：没有绑定设备就没有，如果已绑定，或者别人添加管理者，就默认选中这个，
        // 之后用户手动选择路由器才切换
        routerDevice = (RouterDevice) manager.getDevice(mRouterManager.getRouterDeviceKey());
        if(routerDevice!=null){
            routerDevice.retrieveUpgradeInfo();
            routerDevice.getReport();
            boolean autoUpgrade = routerDevice.getAutoUpgrade();
            Log.i(TAG,"autoUpgrade="+ autoUpgrade);
            checkbox_auto_update.setChecked(autoUpgrade);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        layout_update_immediately.setOnClickListener(this);
        checkbox_auto_update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    routerDevice.changeAutoUpgrade(isChecked);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (FrameLayout) findViewById(R.id.image_back);
        layout_update_immediately = (RelativeLayout) findViewById(R.id.layout_update_immediately);
        checkbox_auto_update = (CheckBox) findViewById(R.id.checkbox_auto_update);
        textview_version_code = (TextView) findViewById(R.id.textview_version_code);
        textview_show_can_update = (TextView) findViewById(R.id.textview_show_can_update);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.layout_update_immediately:
                if(canEnterUpdate){
                    startActivity(new Intent(FirmwareUpdateActivity.this,UpdateImmediatelyActivity.class));
                }else{
                    ToastSingleShow.showText(this,"已是最新版本");
                }

                break;
        }
    }
}
