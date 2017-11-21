package deplink.com.smartwirelessrelay.homegenius.activity.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.AddDeviceTypeSelectAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.AddGetwaySettingOptionsActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;
import deplink.com.smartwirelessrelay.homegenius.qrcode.qrcodecapture.CaptureActivity;
import deplink.com.smartwirelessrelay.homegenius.view.imageview.CircleImageView;

/**
 * 扫码添加设备
 */
public class AddDeviceQRcodeActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "AddDeviceQRcodeActivity";
    private Bundle mBundle;
    private String mRoomName;
    private GridView mGridView;
    private AddDeviceTypeSelectAdapter mAdapter;
    private SmartLockManager mSmartLockManager;
    private CircleImageView imageview_scan_device;

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
    }

    private void initViews() {
        mGridView = (GridView) findViewById(R.id.gridview_add_device_type);
        imageview_scan_device = (CircleImageView) findViewById(R.id.imageview_scan_device);
    }

    private List<String> mDeviceTypes;

    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this);
        mBundle = getIntent().getExtras();
        Log.i(TAG, "mBundle!=null " + (mBundle != null));
        if (mBundle != null) {
            mRoomName = mBundle.getString("roomName");
            Log.i(TAG, "当前编辑的房间名称= " + mRoomName);
        }
        mDeviceTypes = new ArrayList<>();
        mDeviceTypes.add("智能网关");
        mDeviceTypes.add("路由器");
        mDeviceTypes.add("智能门锁");
        mDeviceTypes.add("红外万能遥控");
        mDeviceTypes.add("智能开关");
        mDeviceTypes.add("智能电视遥控");
        mDeviceTypes.add("智能空调遥控");
        mDeviceTypes.add("智能机顶盒遥控");
        mDeviceTypes.add("智能门铃");
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
            case "智能网关":
                startActivityForResult(intentQrcodeSn, REQUEST_ADD_SMART_GETWAY);
                break;
            default:
                startActivityForResult(intentQrcodeSn, REQUEST_CODE_DEVICE_SN);

                break;
        }


    }

    public final static int REQUEST_CODE_DEVICE_SN = 1;
    public final static int REQUEST_ADD_SMART_GETWAY = 2;

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
        }
    }

    //智能门锁设备扫码返回 {"org":"ismart","tp":"SMART_LOCK","ad":"00-12-4b-00-0b-26-c2-15","ver":"1"}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent(AddDeviceQRcodeActivity.this, AddDeviceNameActivity.class);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_DEVICE_SN:
                    String snCode = data.getStringExtra("deviceSN");
                    if (snCode.contains("SMART_LOCK")) {
                        intent.putExtra("mRoomName", mRoomName);
                        intent.putExtra("currentAddDevice", snCode);
                        intent.putExtra("DeviceType", "SMART_LOCK");
                        startActivity(intent);

                    } else {
                        //TODO
                        intent.putExtra("mRoomName", mRoomName);
                        String uid = "77685180654101946200316696479888";
                        intent.putExtra("currentAddDevice", uid);
                        intent.putExtra("DeviceType", "getway");
                        startActivity(intent);

                    }
                    break;
                case REQUEST_ADD_SMART_GETWAY:
                    //添加智能网关
                   // intent = new Intent(AddDeviceQRcodeActivity.this, GetwayCheckActivity.class);
                    intent = new Intent(AddDeviceQRcodeActivity.this, AddGetwaySettingOptionsActivity.class);
                    String uid = "77685180654101946200316696479888";
                    intent.putExtra("currentAddDevice", uid);
                    intent.putExtra("mRoomName", mRoomName);
                    startActivity(intent);
                    break;
            }


        }
    }
}
