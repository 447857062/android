package deplink.com.smartwirelessrelay.homegenius.activity.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.AddDeviceTypeSelectAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.doorbell.AddDoorbellTipsActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.AddGetwayNotifyActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl.add.ChooseBandActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.topBox.AddTopBoxActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.tv.AddTvDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.AddRouterActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch.SelectSwitchTypeActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;
import deplink.com.smartwirelessrelay.homegenius.qrcode.qrcodecapture.CaptureActivity;

/**
 * 扫码添加设备
 */
public class AddDeviceQRcodeActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "AddDeviceQRcodeActivity";
    private Bundle mBundle;
    private GridView mGridView;
    private AddDeviceTypeSelectAdapter mAdapter;
    private SmartLockManager mSmartLockManager;
    private ImageView imageview_scan_device;
    private ImageView image_back;
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
        mGridView = (GridView) findViewById(R.id.gridview_add_device_type);
        imageview_scan_device = (ImageView) findViewById(R.id.imageview_scan_device);
        image_back = (ImageView) findViewById(R.id.image_back);
    }

    private List<String> mDeviceTypes;

    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this);
        mDeviceTypes = new ArrayList<>();
        mDeviceTypes.add(AppConstant.DEVICES.TYPE_SMART_GETWAY);
        mDeviceTypes.add(AppConstant.DEVICES.TYPE_ROUTER);
        mDeviceTypes.add(AppConstant.DEVICES.TYPE_LOCK);
        mDeviceTypes.add(AppConstant.DEVICES.TYPE_REMOTECONTROL);
        mDeviceTypes.add(AppConstant.DEVICES.TYPE_SWITCH);
        mDeviceTypes.add(AppConstant.DEVICES.TYPE_TV_REMOTECONTROL);
        mDeviceTypes.add(AppConstant.DEVICES.TYPE_AIR_REMOTECONTROL);
        mDeviceTypes.add(AppConstant.DEVICES.TYPE_TVBOX_REMOTECONTROL);
        mDeviceTypes.add(AppConstant.DEVICES.TYPE_MENLING);
        mAdapter = new AddDeviceTypeSelectAdapter(this, mDeviceTypes);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick " + mDeviceTypes.get(position));
        Intent intentQrcodeSn = new Intent();
        intentQrcodeSn.setClass(AddDeviceQRcodeActivity.this, CaptureActivity.class);
        intentQrcodeSn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentQrcodeSn.putExtra("requestType", REQUEST_CODE_DEVICE_SN);
        switch (mDeviceTypes.get(position)) {
            case AppConstant.DEVICES.TYPE_SMART_GETWAY:
                intentQrcodeSn.setClass(AddDeviceQRcodeActivity.this, AddGetwayNotifyActivity.class);
                intentQrcodeSn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentQrcodeSn);
                break;
            case AppConstant.DEVICES.TYPE_REMOTECONTROL:
                startActivityForResult(intentQrcodeSn, REQUEST_ADD_INFRAED_UNIVERSAL_RC);
                break;
            case AppConstant.DEVICES.TYPE_AIR_REMOTECONTROL:
                intentQrcodeSn.setClass(AddDeviceQRcodeActivity.this, ChooseBandActivity.class);
                startActivity(intentQrcodeSn);
                break;
            case AppConstant.DEVICES.TYPE_ROUTER:
                startActivityForResult(intentQrcodeSn, REQUEST_ADD_ROUTER);
                break;
            case AppConstant.DEVICES.TYPE_TV_REMOTECONTROL:
                startActivity(new Intent(AddDeviceQRcodeActivity.this, AddTvDeviceActivity.class));
                break;
            case AppConstant.DEVICES.TYPE_TVBOX_REMOTECONTROL:
                startActivity(new Intent(AddDeviceQRcodeActivity.this, AddTopBoxActivity.class));
                break;
            case AppConstant.DEVICES.TYPE_SWITCH:
                startActivity(new Intent(AddDeviceQRcodeActivity.this, SelectSwitchTypeActivity.class));
                break;
            case AppConstant.DEVICES.TYPE_MENLING:
                startActivity(new Intent(AddDeviceQRcodeActivity.this, AddDoorbellTipsActivity.class));
                break;
            default:
                //智能门锁，等没有在case中的设备
                startActivityForResult(intentQrcodeSn, REQUEST_CODE_DEVICE_SN);
                break;
        }


    }

    public final static int REQUEST_CODE_DEVICE_SN = 1;
    public final static int REQUEST_ADD_SMART_GETWAY = 2;
    /**
     * 红外万能遥控
     */
    public final static int REQUEST_ADD_INFRAED_UNIVERSAL_RC = 3;
    public final static int REQUEST_ADD_ROUTER = 4;

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
                        intent.putExtra("DeviceType", "SMART_LOCK");
                        startActivity(intent);
                    } else {
                        //TODO
                        String uid = "77685180654101946200316696479888";
                        intent.putExtra("currentAddDevice", uid);
                        intent.putExtra("DeviceType", "getway");
                        startActivity(intent);
                    }
                    break;
                case REQUEST_ADD_INFRAED_UNIVERSAL_RC:
                    //{"org":"ismart","tp":"IRMOTE_V2","ad":"00-12-4b-00-08-93-55-bb","ver":"1"}
                    intent.putExtra("currentAddDevice", qrCodeResult);
                    intent.putExtra("DeviceType", "IRMOTE_V2");
                    startActivity(intent);
                    break;
                case REQUEST_ADD_SMART_GETWAY:
                    //添加智能网关
                    intent = new Intent(AddDeviceQRcodeActivity.this, AddGetwayNotifyActivity.class);
                    startActivity(intent);
                    break;
                case REQUEST_ADD_ROUTER:
                    //添加路由器
                    intent = new Intent(AddDeviceQRcodeActivity.this, AddRouterActivity.class);
                    intent.putExtra("routerSN", qrCodeResult);
                    startActivity(intent);
                    break;
            }


        }
    }
}
