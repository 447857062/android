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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.doorbeel.DoorbeelManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartswitch.SmartSwitchManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class AddDeviceNameActivity extends Activity implements DeviceListener, View.OnClickListener {
    private static final String TAG = "AddDeviceNameActivity";
    private String currentAddDevice;
    private DeviceManager mDeviceManager;
    private SmartSwitchManager mSmartSwitchManager;
    private Button button_add_device_sure;
    private FrameLayout image_back;
    private EditText edittext_add_device_input_name;
    /**
     * 当前待添加设备
     */
    private QrcodeSmartDevice device;
    private String deviceType;
    private String switchqrcode;
    private TextView textview_title;
    private DoorbeelManager mDoorbeelManager;

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
        image_back = (FrameLayout) findViewById(R.id.image_back);
        textview_title = (TextView) findViewById(R.id.textview_title);
    }


    private void initDatas() {
        mDoorbeelManager = DoorbeelManager.getInstance();
        mDoorbeelManager.InitDoorbeelManager(this);
        mSmartSwitchManager = SmartSwitchManager.getInstance();
        mSmartSwitchManager.InitSmartSwitchManager(this);
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
                textview_title.setText("智能门锁");
                break;
            case "IRMOTE_V2":
                edittext_add_device_input_name.setHint("例如:智能遥控（最多5个字）");
                textview_title.setText("智能遥控");
                break;
            case "智能空调":
                edittext_add_device_input_name.setHint("例如:智能空调（最多5个字）");
                textview_title.setText("智能空调遥控器");
                break;
            case "智能电视":
                edittext_add_device_input_name.setHint("例如:智能电视（最多5个字）");
                textview_title.setText("智能电视遥控器");
                break;
            case "智能机顶盒遥控":
                edittext_add_device_input_name.setHint("例如:智能机顶盒遥控（最多5个字）");
                textview_title.setText("智能机顶盒遥控");
                break;
            case AppConstant.DEVICES.TYPE_SWITCH:
                edittext_add_device_input_name.setHint("例如:智能开关（最多5个字）");
                textview_title.setText("智能开关");
                break;
            case AppConstant.DEVICES.TYPE_MENLING:
                edittext_add_device_input_name.setHint("例如:智能门铃（最多5个字）");
                textview_title.setText("智能门铃");
                break;
        }

    }

    @Override
    public void responseQueryResult(String result) {

    }

    private static final int MSG_ADD_DEVICE_RESULT = 100;
    private static final int MSG_FINISH_ACTIVITY = 101;
    private static final int MSG_UPDATE_ROOM_FAIL = 102;
    private static final int MSG_ADD_DOORBEEL_FAIL = 103;
    private static final int MSG_HIDE_DIALOG = 104;
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
                case MSG_UPDATE_ROOM_FAIL:
                    Toast.makeText(AddDeviceNameActivity.this, "更新智能门铃所在房间失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_ADD_DOORBEEL_FAIL:
                    Toast.makeText(AddDeviceNameActivity.this, "添加智能门铃失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_HIDE_DIALOG:
                   DialogThreeBounce.hideLoading();
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

                for (int i = 0; i < aDeviceList.getSmartDev().size(); i++) {
                    if (aDeviceList.getSmartDev().get(i).getUid().equals(device.getAd())) {
                        mDeviceManager.updateSmartDeviceInWhatRoom(currentSelectedRoom, aDeviceList.getSmartDev().get(i).getUid(), deviceName);
                    }
                }
               DialogThreeBounce.hideLoading();
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
                Gson gson = new Gson();
                switch (deviceType) {
                    case "SMART_LOCK":
                        if (deviceName.equals("")) {
                            deviceName = "我家的门锁";
                        }
                        device = gson.fromJson(currentAddDevice, QrcodeSmartDevice.class);
                        Log.i(TAG, "deviceType=" + deviceType + "device=" + (device != null));
                        break;
                    case AppConstant.DEVICES.TYPE_SWITCH:
                        if (deviceName.equals("")) {
                            deviceName = "智能开关";
                        }
                        break;
                    case "IRMOTE_V2":
                        if (deviceName.equals("")) {
                            deviceName = "智能遥控";
                        }
                        device = gson.fromJson(currentAddDevice, QrcodeSmartDevice.class);
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
                    case AppConstant.DEVICES.TYPE_MENLING:
                        if (deviceName.equals("")) {
                            deviceName = "智能门铃";
                        }
                        break;
                }
                //TODO 调试
                if (device == null) {
                    device = new QrcodeSmartDevice();
                    device.setVer("1");
                }
                switch (deviceType) {
                    case "SMART_LOCK":
                        //TODO 不需要模拟测试
                        Log.i(TAG, "绑定智能设备");
                        device.setName(deviceName);
                        DialogThreeBounce.showLoading(this);
                        Message msg=Message.obtain();
                        msg.what=MSG_HIDE_DIALOG;
                        mHandler.sendMessageDelayed(msg,3000);
                        mDeviceManager.bindSmartDevList(device);
                        break;
                    case AppConstant.DEVICES.TYPE_SWITCH:
                        //TODO
                        device.setAd(switchqrcode);
                        // device.setAd("智能开关uid");
                        device.setTp(AppConstant.DEVICES.TYPE_SWITCH);
                        mSmartSwitchManager.addDBSwitchDevice(device);
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
                    case AppConstant.DEVICES.TYPE_MENLING:
                        //TODO
                        SmartDev doorbeelDev = new SmartDev();
                        doorbeelDev.setUid("testuid智能门铃");
                        doorbeelDev.setType("智能门铃");
                        mDoorbeelManager.saveDoorbeel(doorbeelDev, new Observer() {

                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull Object o) {
                                if ((boolean) o) {
                                    if (deviceName.equals("")) {
                                        deviceName = "智能门铃";
                                    }
                                    mDoorbeelManager.updateDeviceInWhatRoom(currentSelectedRoom, "testuid智能门铃", deviceName, new Observer() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onNext(@NonNull Object o) {
                                            if ((boolean) o) {
                                                startActivity(new Intent(AddDeviceNameActivity.this, DevicesActivity.class));
                                            } else {
                                                Message msg = Message.obtain();
                                                msg.what = MSG_UPDATE_ROOM_FAIL;
                                                mHandler.sendMessage(msg);
                                            }
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                                } else {
                                    Message msg = Message.obtain();
                                    msg.what = MSG_ADD_DOORBEEL_FAIL;
                                    mHandler.sendMessage(msg);
                                }


                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }

                        });
                }
                break;
            case R.id.image_back:
                onBackPressed();
                break;

        }
    }
}
