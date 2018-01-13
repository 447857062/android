package com.deplink.homegenius.activity.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.activity.device.adapter.AddDeviceTypeSelectAdapter;
import com.deplink.homegenius.activity.device.doorbell.add.AddDoorbellTipsActivity;
import com.deplink.homegenius.activity.device.smartSwitch.add.SelectSwitchTypeActivity;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlManager;
import com.deplink.homegenius.manager.device.smartlock.SmartLockManager;
import com.deplink.homegenius.util.qrcode.qrcodecapture.CaptureActivity;
import com.deplink.homegenius.view.toast.ToastSingleShow;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * 扫码添加设备
 */
public class AddDeviceQRcodeActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "AddDeviceQRcodeActivity";
    private GridView mGridView;
    private AddDeviceTypeSelectAdapter mAdapter;
    private SmartLockManager mSmartLockManager;
    private ImageView imageview_scan_device;
    private FrameLayout image_back;
    private List<String> mDeviceTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_qrcode);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        imageview_scan_device.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        mGridView = findViewById(R.id.gridview_add_device_type);
        imageview_scan_device = findViewById(R.id.imageview_scan_device);
        image_back = findViewById(R.id.image_back);
    }
    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this);
        mDeviceTypes = new ArrayList<>();
        mDeviceTypes.add(DeviceTypeConstant.TYPE.TYPE_SMART_GETWAY);
        mDeviceTypes.add(DeviceTypeConstant.TYPE.TYPE_ROUTER);
        mDeviceTypes.add(DeviceTypeConstant.TYPE.TYPE_LOCK);
        mDeviceTypes.add(DeviceTypeConstant.TYPE.TYPE_REMOTECONTROL);
        mDeviceTypes.add(DeviceTypeConstant.TYPE.TYPE_SWITCH);
        mDeviceTypes.add(DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL);
        mDeviceTypes.add(DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL);
        mDeviceTypes.add(DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL);
        mDeviceTypes.add(DeviceTypeConstant.TYPE.TYPE_MENLING);
        mDeviceTypes.add(DeviceTypeConstant.TYPE.TYPE_LIGHT);
        mAdapter = new AddDeviceTypeSelectAdapter(this, mDeviceTypes);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick " + mDeviceTypes.get(position));
        Intent intentQrcodeSn = new Intent(AddDeviceQRcodeActivity.this, CaptureActivity.class);
        intentQrcodeSn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentQrcodeSn.putExtra("requestType", REQUEST_CODE_DEVICE_SN);
        Intent intentEditDeviceMessage = new Intent(AddDeviceQRcodeActivity.this, AddDeviceNameActivity.class);
        List<SmartDev> mRemotecontrol = new ArrayList<>();
        switch (mDeviceTypes.get(position)) {
            case DeviceTypeConstant.TYPE.TYPE_SMART_GETWAY:
                startActivityForResult(intentQrcodeSn, REQUEST_ADD_GETWAY);
                break;
            case DeviceTypeConstant.TYPE.TYPE_REMOTECONTROL:
                startActivityForResult(intentQrcodeSn, REQUEST_ADD_INFRAED_UNIVERSAL_RC);
                break;
            case DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL:
                RemoteControlManager.getInstance().setCurrentActionIsAddDevice(true);
                mRemotecontrol.addAll(RemoteControlManager.getInstance().findAllRemotecontrolDevice());
                if (mRemotecontrol.size() == 0) {
                    ToastSingleShow.showText(this, "未添加智能遥控，无法添加设备");
                } else {
                    RemoteControlManager.getInstance().setmSelectRemoteControlDevice(mRemotecontrol.get(0));
                    intentEditDeviceMessage.putExtra("DeviceType", DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL);
                    startActivity(intentEditDeviceMessage);
                }
                break;
            case DeviceTypeConstant.TYPE.TYPE_ROUTER:
                startActivityForResult(intentQrcodeSn, REQUEST_ADD_ROUTER);
                break;
            case DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL:
                RemoteControlManager.getInstance().setCurrentActionIsAddDevice(true);
                mRemotecontrol.addAll(RemoteControlManager.getInstance().findAllRemotecontrolDevice());
                if (mRemotecontrol.size() == 0) {
                    ToastSingleShow.showText(this, "未添加智能遥控，无法添加设备");
                } else {
                    RemoteControlManager.getInstance().setmSelectRemoteControlDevice(mRemotecontrol.get(0));
                    intentEditDeviceMessage.putExtra("DeviceType", DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL);
                    startActivity(intentEditDeviceMessage);
                }

                break;
            case DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL:
                RemoteControlManager.getInstance().setCurrentActionIsAddDevice(true);
                mRemotecontrol.addAll(RemoteControlManager.getInstance().findAllRemotecontrolDevice());
                if (mRemotecontrol.size() == 0) {
                    ToastSingleShow.showText(this, "未添加智能遥控，无法添加设备");
                } else {
                    RemoteControlManager.getInstance().setmSelectRemoteControlDevice(mRemotecontrol.get(0));
                    intentEditDeviceMessage.putExtra("DeviceType", DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL);
                    startActivity(intentEditDeviceMessage);
                }

                break;
            case DeviceTypeConstant.TYPE.TYPE_SWITCH:
                startActivity(new Intent(AddDeviceQRcodeActivity.this, SelectSwitchTypeActivity.class));
                break;
            case DeviceTypeConstant.TYPE.TYPE_MENLING:
                startActivity(new Intent(AddDeviceQRcodeActivity.this, AddDoorbellTipsActivity.class));
                break;
            default:
                //智能门锁，等没有在case中的设备
                startActivityForResult(intentQrcodeSn, REQUEST_CODE_DEVICE_SN);
                break;
        }


    }

    public final static int REQUEST_CODE_DEVICE_SN = 1;
    /**
     * 红外万能遥控
     */
    public final static int REQUEST_ADD_INFRAED_UNIVERSAL_RC = 3;
    public final static int REQUEST_ADD_ROUTER = 4;
    public final static int REQUEST_ADD_GETWAY = 5;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_scan_device:
                Intent intentQrcodeSn = new Intent();
                intentQrcodeSn.setClass(AddDeviceQRcodeActivity.this, CaptureActivity.class);
                intentQrcodeSn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intentQrcodeSn.putExtra("requestType", REQUEST_CODE_DEVICE_SN);
                startActivityForResult(intentQrcodeSn, REQUEST_CODE_DEVICE_SN);
                break;
            case R.id.image_back:
                onBackPressed();
                break;

        }
    }

    //智能门锁设备扫码返回 {"org":"ismart","tp":"SMART_LOCK","ad":"00-12-4b-00-0b-26-c2-15","ver":"1"}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(AddDeviceQRcodeActivity.this, AddDeviceNameActivity.class);
        if (resultCode == RESULT_OK) {
            String qrCodeResult = data.getStringExtra("deviceSN");
            Log.i(TAG, "二维码扫码结果=" + qrCodeResult);
            switch (requestCode) {
                case REQUEST_CODE_DEVICE_SN:
                    if (qrCodeResult.contains("SMART_LOCK")) {
                        intent.putExtra("currentAddDevice", qrCodeResult);
                        intent.putExtra("DeviceType", DeviceTypeConstant.TYPE.TYPE_LOCK);
                        startActivity(intent);
                    } else if (qrCodeResult.length()==12) {//网关,路由器
                        intent.putExtra("currentAddDevice", qrCodeResult);
                        intent.putExtra("DeviceType", DeviceTypeConstant.TYPE.TYPE_SMART_GETWAY);
                        startActivity(intent);
                    } else if (qrCodeResult.contains("YWLIGHTCONTROL")) {
                        intent.putExtra("currentAddDevice", qrCodeResult);
                        intent.putExtra("DeviceType", DeviceTypeConstant.TYPE.TYPE_LIGHT);
                        startActivity(intent);
                    } else {

                    }
                    break;
                case REQUEST_ADD_INFRAED_UNIVERSAL_RC:
                    //{"org":"ismart","tp":"IRMOTE_V2","ad":"00-12-4b-00-08-93-55-bb","ver":"1"}
                    intent.putExtra("currentAddDevice", qrCodeResult);
                    intent.putExtra("DeviceType", "IRMOTE_V2");
                    startActivity(intent);
                    break;
                case REQUEST_ADD_ROUTER:
                    //添加路由器
                    intent.putExtra("currentAddDevice", qrCodeResult);
                    intent.putExtra("DeviceType", DeviceTypeConstant.TYPE.TYPE_SMART_GETWAY);
                    startActivity(intent);
                    break;
                case REQUEST_ADD_GETWAY:
                    intent.putExtra("currentAddDevice", qrCodeResult);
                    intent.putExtra("DeviceType", DeviceTypeConstant.TYPE.TYPE_SMART_GETWAY);
                    startActivity(intent);
                    break;
            }
        }
    }
}
