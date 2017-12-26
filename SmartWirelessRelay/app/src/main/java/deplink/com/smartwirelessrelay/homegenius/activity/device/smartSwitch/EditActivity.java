package deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartswitch.SmartSwitchManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.DeleteDeviceDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class EditActivity extends Activity implements View.OnClickListener, DeviceListener {
    private static final String TAG = "EditDoorbeelActivity";
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView button_delete_device;
    private DeleteDeviceDialog deleteDialog;
    private SmartSwitchManager mSmartSwitchManager;
    private DeviceManager mDeviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_delete_device.setOnClickListener(this);
    }

    private String switchType;

    private void initDatas() {
        switchType = getIntent().getStringExtra("switchType");
        Log.i(TAG, "initDatas switchType=" + switchType);
        textview_title.setText(switchType);
        deleteDialog = new DeleteDeviceDialog(this);
        mSmartSwitchManager = SmartSwitchManager.getInstance();
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this, this);
    }

    private void initViews() {
        image_back = (FrameLayout) findViewById(R.id.image_back);
        textview_title = (TextView) findViewById(R.id.textview_title);
        button_delete_device = (TextView) findViewById(R.id.button_delete_device);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_delete_device:
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        DialogThreeBounce.showLoading(EditActivity.this);
                        mDeviceManager.deleteSmartDevice();

                    }
                });
                deleteDialog.show();
                break;

        }
    }

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseBindDeviceResult(String result) {


        Gson gson = new Gson();
        boolean deleteSuccess = true;
        DeviceList mDeviceList = gson.fromJson(result, DeviceList.class);
        for (int i = 0; i < mDeviceList.getSmartDev().size(); i++) {
            if (mDeviceList.getSmartDev().get(i).getUid().equals(mDeviceManager.getCurrentSelectSmartDevice().getUid())) {
                deleteSuccess = false;
            }
        }
        DialogThreeBounce.hideLoading();
        if (deleteSuccess) {
            int deleteResult = mSmartSwitchManager.deleteDBSmartDevice(mSmartSwitchManager.getCurrentSelectSmartDevice().getUid());
            if (deleteResult > 0) {
                startActivity(new Intent(EditActivity.this, DevicesActivity.class));
            } else {
                ToastSingleShow.showText(EditActivity.this, "删除开关设备失败");
            }
        } else {
            ToastSingleShow.showText(EditActivity.this, "删除开关设备失败");
        }
    }

    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {

    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }
}
