package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.DeleteDeviceDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class EditRemoteDevicesActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "EditDoorbeelActivity";
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView button_delete_device;
    private DeleteDeviceDialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_remote_devices);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_delete_device.setOnClickListener(this);
    }

    private String deviceType;

    private void initDatas() {
        deviceType = getIntent().getStringExtra("deviceType");
        Log.i(TAG, "initDatas deviceType=" + deviceType);
        textview_title.setText(deviceType);
        deleteDialog = new DeleteDeviceDialog(this);
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
                        if (!DeviceManager.getInstance().isStartFromExperience()) {
                            int result = RemoteControlManager.getInstance().deleteCurrentSelectDevice();
                            if (result > 0) {
                                startActivity(new Intent(EditRemoteDevicesActivity.this, DevicesActivity.class));
                            } else {
                                ToastSingleShow.showText(EditRemoteDevicesActivity.this, "删除" + deviceType + "失败");
                            }
                        } else {
                            startActivity(new Intent(EditRemoteDevicesActivity.this, ExperienceDevicesActivity.class));
                        }
                    }
                });
                deleteDialog.show();
                break;

        }
    }
}
