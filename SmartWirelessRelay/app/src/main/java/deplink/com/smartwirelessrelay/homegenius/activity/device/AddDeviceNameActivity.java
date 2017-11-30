package deplink.com.smartwirelessrelay.homegenius.activity.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

public class AddDeviceNameActivity extends Activity implements DeviceListener, View.OnClickListener {
    private static final String TAG = "AddDeviceNameActivity";
    private String currentAddDevice;
    private DeviceManager mDeviceManager;
    private Button button_add_device_sure;
    private ImageView image_back;
    private EditText edittext_add_device_input_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_name);
        initViews();
        initDatas();
        initEvents();


    }

    private void initEvents() {
        button_add_device_sure.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        button_add_device_sure = (Button) findViewById(R.id.button_add_device_sure);
        edittext_add_device_input_name = (EditText) findViewById(R.id.edittext_add_device_input_name);
        image_back = (ImageView) findViewById(R.id.image_back);
    }

    /**
     * 当前待添加设备
     */
    private QrcodeSmartDevice device;
    private String deviceType;
    private String switchqrcode;

    private void initDatas() {
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this, this);
        //getintent data
        currentAddDevice = getIntent().getStringExtra("currentAddDevice");
        deviceType = getIntent().getStringExtra("DeviceType");
        switchqrcode = getIntent().getStringExtra("switchqrcode");
        //get current room
        currentSelectedRoom = RoomManager.getInstance().getCurrentSelectedRoom();
        switch (deviceType) {
            case "SMART_LOCK":
                edittext_add_device_input_name.setHint("例如:我家的门锁（最多5个字）");
                break;
            case "IRMOTE_V2":
                edittext_add_device_input_name.setHint("例如:智能遥控（最多5个字）");
                break;
            case "智能空调":
                edittext_add_device_input_name.setHint("例如:智能空调（最多5个字）");
                break;
            case "智能电视":
                edittext_add_device_input_name.setHint("例如:智能电视（最多5个字）");
                break;
            case "智能机顶盒遥控":
                edittext_add_device_input_name.setHint("例如:智能机顶盒遥控（最多5个字）");
                break;
            case "智能开关":
                edittext_add_device_input_name.setHint("例如:智能开关（最多5个字）");
                break;
        }

    }

    @Override
    public void responseQueryResult(String result) {

    }

    private static final int MSG_ADD_DEVICE_RESULT = 100;
    private static final int MSG_FINISH_ACTIVITY = 101;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ADD_DEVICE_RESULT:
                    boolean success = (boolean) msg.obj;
                    //TODO 更新房间表
                    if (success) {
                        Toast.makeText(AddDeviceNameActivity.this, "添加设备" + deviceType + "成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddDeviceNameActivity.this, "添加设备" + deviceType + "失败", Toast.LENGTH_SHORT).show();
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_FINISH_ACTIVITY, 1500);
                    break;
                case MSG_FINISH_ACTIVITY:
                    startActivity(new Intent(AddDeviceNameActivity.this, DevicesActivity.class));
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDeviceManager.removeDeviceListener(this);
    }

    private String deviceName;
    private Room currentSelectedRoom;

    @Override
    public void responseBindDeviceResult(String result) {
        Gson gson = new Gson();

        final DeviceList aDeviceList = gson.fromJson(result, DeviceList.class);
        boolean success;
        success = isSmartDeviceAddSuccess(aDeviceList);
        mDeviceManager.addDBSmartDevice(device);
        switch (deviceType) {
            case "SMART_LOCK":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < aDeviceList.getSmartDev().size(); i++) {
                            if (aDeviceList.getSmartDev().get(i).getUid().equals(device.getAd())) {
                                mDeviceManager.updateSmartDeviceInWhatRoom(currentSelectedRoom, aDeviceList.getSmartDev().get(i).getUid(), deviceName);
                            }
                        }
                    }
                });
                break;
            case "IRMOTE_V2":
                // 智能遥控添加结果
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < aDeviceList.getSmartDev().size(); i++) {
                            if (aDeviceList.getSmartDev().get(i).getUid().equals(device.getAd())) {
                                mDeviceManager.updateSmartDeviceInWhatRoom(currentSelectedRoom, aDeviceList.getSmartDev().get(i).getUid(), deviceName);
                            }
                        }
                    }
                });
                break;
            case "智能开关":
                // 智能遥控添加结果
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < aDeviceList.getSmartDev().size(); i++) {
                            if (aDeviceList.getSmartDev().get(i).getUid().equals(device.getAd())) {
                                mDeviceManager.updateSmartDeviceInWhatRoom(currentSelectedRoom, aDeviceList.getSmartDev().get(i).getUid(), deviceName);
                            }
                        }
                    }
                });
                break;

        }
        Message msg = Message.obtain();
        msg.what = MSG_ADD_DEVICE_RESULT;
        msg.obj = success;
        mHandler.sendMessage(msg);
    }

    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {

    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }

    private boolean isDeviceAddSuccess(DeviceList aDeviceList) {
        for (int i = 0; i < aDeviceList.getDevice().size(); i++) {
            if (aDeviceList.getDevice().get(i).getUid().equals(currentAddDevice)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSmartDeviceAddSuccess(DeviceList aDeviceList) {
        for (int i = 0; i < aDeviceList.getSmartDev().size(); i++) {
            if (aDeviceList.getSmartDev().get(i).getUid().equals(device.getAd())) {

                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_device_sure:
                deviceName = edittext_add_device_input_name.getText().toString();
                switch (deviceType) {
                    case "SMART_LOCK":
                        if (deviceName.equals("")) {
                            deviceName = "我家的门锁";
                        }
                        break;
                    case "智能开关":
                        if (deviceName.equals("")) {
                            deviceName = "智能开关";
                        }
                        break;
                    case "IRMOTE_V2":
                        if (deviceName.equals("")) {
                            deviceName = "智能遥控";
                        }
                        break;
                    case "智能空调":
                        if (deviceName.equals("")) {
                            deviceName = "智能空调";
                        }
                        break;
                    case "智能电视":
                        if (deviceName.equals("")) {
                            deviceName = "智能电视";
                        }
                        break;
                    case "智能机顶盒遥控":
                        if (deviceName.equals("")) {
                            deviceName = "智能机顶盒遥控";
                        }
                        break;
                }

                Gson gson = new Gson();
                device = gson.fromJson(currentAddDevice, QrcodeSmartDevice.class);
                Log.i(TAG, "deviceType=" + deviceType + "device=" + (device != null));
                //TODO 调试
                if (device == null) {
                    device = new QrcodeSmartDevice();
                    device.setVer("1");
                }
                switch (deviceType) {
                    case "SMART_LOCK":
                        //TODO 不需要模拟测试
                        Log.i(TAG, "绑定智能设备");
                        mDeviceManager.bindSmartDevList(device);
                        break;
                    case "智能开关":
                        //TODO
                        device.setAd(switchqrcode);
                        mDeviceManager.bindSmartDevList(device);
                        break;
                    case "IRMOTE_V2":
                        device.setAd("智能遥控序列号001");
                        device.setTp("智能遥控");
                        mDeviceManager.bindSmartDevList(device);
                        break;
                    case "智能空调":
                        //TODO
                        // 绑定智能遥控,现在智能单个添加，这个不扫码的虚拟设备需要给他一个识别码
                        device.setAd("智能空调序列号001");
                        device.setTp("智能空调");
                        mDeviceManager.addDBSmartDevice(device);
                        // 智能遥控添加结果
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDeviceManager.updateSmartDeviceInWhatRoom(currentSelectedRoom, "智能空调序列号001", deviceName);
                            }
                        });
                        break;
                    case "智能电视":
                        // 绑定智能遥控
                        //TODO
                        // 绑定智能遥控,现在智能单个添加，这个不扫码的虚拟设备需要给他一个识别码
                        device.setAd("智能电视序列号001");
                        device.setTp("智能电视");
                        mDeviceManager.addDBSmartDevice(device);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDeviceManager.updateSmartDeviceInWhatRoom(currentSelectedRoom, "智能电视序列号001", deviceName);
                            }
                        });
                        break;
                    case "智能机顶盒遥控":
                        // 绑定智能遥控
                        //TODO
                        // 绑定智能遥控,现在智能单个添加，这个不扫码的虚拟设备需要给他一个识别码
                        device.setAd("智能机顶盒遥控序列号001");
                        device.setTp("智能机顶盒遥控");
                        mDeviceManager.addDBSmartDevice(device);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDeviceManager.updateSmartDeviceInWhatRoom(currentSelectedRoom, "智能机顶盒遥控序列号001", deviceName);
                            }
                        });
                        break;
                    default:
                        break;
                }


                break;
            case R.id.image_back:
                onBackPressed();
                break;

        }
    }
}
