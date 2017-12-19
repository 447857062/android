package deplink.com.smartwirelessrelay.homegenius.activity.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.GetwaySelectListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.RemoteControlSelectListAdapter;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceType;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.doorbeel.DoorbeelManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartswitch.SmartSwitchManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;
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
    private TextView textview_select_getway_name;
    private RelativeLayout layout_getway_select;
    private RelativeLayout layout_getway_list;
    private RelativeLayout layout_room_select;
    private DoorbeelManager mDoorbeelManager;
    private GetwaySelectListAdapter selectGetwayAdapter;
    private List<Device> mGetways;
    private ListView listview_select_getway;
    private RemoteControlSelectListAdapter selectRemotecontrolAdapter;
    private List<SmartDev> mRemoteControls;
    private ListView listview_select_remotecontrol;
    private ImageView imageview_getway_arror_right;
    private ImageView imageview_remotecontrol_arror_right;
    private TextView textview_select_room_name;
    private TextView textview_select_remotecontrol_name;
    private String deviceName;
    private Room currentSelectedRoom;
    private Device currentSelectGetway;
    private SmartDev currentSelectRemotecontrol;
    private RelativeLayout layout_remotecontrol_select;
    private RelativeLayout layout_remotecontrol_list;

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
        layout_getway_select.setOnClickListener(this);
        layout_room_select.setOnClickListener(this);
        layout_remotecontrol_select.setOnClickListener(this);
    }

    private void initViews() {
        button_add_device_sure = (Button) findViewById(R.id.button_add_device_sure);
        edittext_add_device_input_name = (EditText) findViewById(R.id.edittext_add_device_input_name);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_select_remotecontrol_name = (TextView) findViewById(R.id.textview_select_remotecontrol_name);
        textview_select_room_name = (TextView) findViewById(R.id.textview_select_room_name);
        textview_select_getway_name = (TextView) findViewById(R.id.textview_select_getway_name);
        layout_getway_select = (RelativeLayout) findViewById(R.id.layout_getway_select);
        layout_getway_list = (RelativeLayout) findViewById(R.id.layout_getway_list);
        layout_room_select = (RelativeLayout) findViewById(R.id.layout_room_select);
        layout_remotecontrol_list = (RelativeLayout) findViewById(R.id.layout_remotecontrol_list);
        layout_remotecontrol_select = (RelativeLayout) findViewById(R.id.layout_remotecontrol_select);
        listview_select_getway = (ListView) findViewById(R.id.listview_select_getway);
        listview_select_remotecontrol = (ListView) findViewById(R.id.listview_select_remotecontrol);
        imageview_getway_arror_right = (ImageView) findViewById(R.id.imageview_getway_arror_right);
        imageview_remotecontrol_arror_right = (ImageView) findViewById(R.id.imageview_remotecontrol_arror_right);
    }

    private void initDatas() {
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
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
        if (currentSelectedRoom != null) {
            textview_select_room_name.setText(currentSelectedRoom.getRoomName());
        } else {
            textview_select_room_name.setText("全部");
        }
        switch (deviceType) {
            case "SMART_LOCK":
                edittext_add_device_input_name.setHint("例如:我家的门锁（最多5个字）");
                textview_title.setText("智能门锁");
                break;
            case "IRMOTE_V2":
                edittext_add_device_input_name.setHint("例如:智能遥控（最多10个字）");
                textview_title.setText("智能遥控");
                break;
            case DeviceType.TYPE.TYPE_AIR_REMOTECONTROL:
                edittext_add_device_input_name.setHint("例如:智能空调（最多10个字）");
                textview_title.setText("智能空调遥控器");
                break;
            case DeviceType.TYPE.TYPE_TV_REMOTECONTROL:
                edittext_add_device_input_name.setHint("例如:智能电视（最多10个字）");
                textview_title.setText("智能电视遥控器");
                break;
            case DeviceType.TYPE.TYPE_TVBOX_REMOTECONTROL:
                edittext_add_device_input_name.setHint("例如:智能机顶盒遥控（最多10个字）");
                textview_title.setText("智能机顶盒遥控");
                break;
            case DeviceType.TYPE.TYPE_SWITCH:
                edittext_add_device_input_name.setHint("例如:智能开关（最多10个字）");
                textview_title.setText("智能开关");
                break;
            case DeviceType.TYPE.TYPE_MENLING:
                edittext_add_device_input_name.setHint("例如:智能门铃（最多10个字）");
                textview_title.setText("智能门铃");
                break;
        }
        mRemoteControls = new ArrayList<>();

        mGetways = new ArrayList<>();
        mGetways.addAll(GetwayManager.getInstance().queryAllGetwayDevice());
        selectGetwayAdapter = new GetwaySelectListAdapter(this, mGetways);


        mRemoteControls.addAll(RemoteControlManager.getInstance().queryAllRemotecontrol());
        selectRemotecontrolAdapter = new RemoteControlSelectListAdapter(this, mRemoteControls);
        listview_select_remotecontrol.setAdapter(selectRemotecontrolAdapter);
        listview_select_remotecontrol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectRemotecontrolName = mRemoteControls.get(position).getName();
                textview_select_remotecontrol_name.setText(selectRemotecontrolName);
                layout_remotecontrol_list.setVisibility(View.GONE);
                imageview_remotecontrol_arror_right.setImageResource(R.drawable.directionicon);
                currentSelectRemotecontrol = mRemoteControls.get(position);
            }
        });


        listview_select_getway.setAdapter(selectGetwayAdapter);
        listview_select_getway.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectGetwayName = mGetways.get(position).getName();
                textview_select_getway_name.setText(selectGetwayName);
                layout_getway_list.setVisibility(View.GONE);
                currentSelectGetway = mGetways.get(position);
            }
        });
        showSettinglayout();

    }

    /**
     * 显示当前添加的设备
     * 可以设置的布局
     */
    private void showSettinglayout() {
        if (deviceType.equals(DeviceType.TYPE.TYPE_AIR_REMOTECONTROL) ||
                deviceType.equals(DeviceType.TYPE.TYPE_TV_REMOTECONTROL) ||
                deviceType.equals(DeviceType.TYPE.TYPE_TVBOX_REMOTECONTROL)) {
            layout_remotecontrol_select.setVisibility(View.VISIBLE);
            layout_getway_select.setVisibility(View.GONE);
            if (mRemoteControls.size() > 0) {
                textview_select_remotecontrol_name.setText(mRemoteControls.get(0).getName());
                currentSelectRemotecontrol = mRemoteControls.get(0);
            } else {
                textview_select_remotecontrol_name.setText("未找到遥控器");
            }
        } else {
            layout_remotecontrol_select.setVisibility(View.GONE);
            layout_getway_select.setVisibility(View.VISIBLE);
            if (mGetways.size() > 0) {
                textview_select_getway_name.setText(mGetways.get(0).getName());
                currentSelectGetway = mGetways.get(0);
            } else {
                textview_select_getway_name.setText("未检测到网关");
            }
        }
    }

    private String selectGetwayName;
    private String selectRemotecontrolName;

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


    @Override
    public void responseBindDeviceResult(String result) {
        Gson gson = new Gson();
        final DeviceList aDeviceList = gson.fromJson(result, DeviceList.class);
        boolean success;
        success = isSmartDeviceAddSuccess(aDeviceList);
        mDeviceManager.addDBSmartDevice(device, currentSelectGetway);
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
                for (int i = 0; i < aDeviceList.getSmartDev().size(); i++) {
                    if (aDeviceList.getSmartDev().get(i).getUid().equals(device.getAd())) {
                        mDeviceManager.updateSmartDeviceInWhatRoom(currentSelectedRoom, aDeviceList.getSmartDev().get(i).getUid(), deviceName);
                    }
                }

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
                        Log.i(TAG, "绑定智能设备");
                        device.setName(deviceName);
                        DialogThreeBounce.showLoading(this);
                        Message msg = Message.obtain();
                        msg.what = MSG_HIDE_DIALOG;
                        mHandler.sendMessageDelayed(msg, 3000);
                        mDeviceManager.bindSmartDevList(device);
                        break;
                    case DeviceType.TYPE.TYPE_SWITCH:

                        if (deviceName.equals("")) {
                            deviceName = "智能开关";
                        }
                        //TODO
                        device.setAd(switchqrcode);
                        // device.setAd("智能开关uid");
                        device.setTp(DeviceType.TYPE.TYPE_SWITCH);
                        mSmartSwitchManager.addDBSwitchDevice(device);
                        mDeviceManager.bindSmartDevList(device);
                        break;
                    case "IRMOTE_V2":
                        if (deviceName.equals("")) {
                            deviceName = "智能遥控";
                        }
                        device = gson.fromJson(currentAddDevice, QrcodeSmartDevice.class);
                        device.setName(deviceName);
                        mDeviceManager.bindSmartDevList(device);
                        //TODO 服务没有用调试打开
                        // mDeviceManager.addDBSmartDevice(device, currentSelectGetway);
                        break;
                    case DeviceType.TYPE.TYPE_AIR_REMOTECONTROL:
                        if (deviceName.equals("")) {
                            deviceName = "智能空调";
                        }
                        boolean isRemoteControlAdded = RemoteControlManager.getInstance().judgAirconditionDeviceisAdded(deviceName);
                        if (isRemoteControlAdded) {
                            ToastSingleShow.showText(this, "已存在相同名称的空调遥控器");
                            return;
                        }
                        // 绑定智能遥控,现在智能单个添加，这个不扫码的虚拟设备需要给他一个识别码
                        SmartDev addDevice = new SmartDev();
                        addDevice.setType(DeviceType.TYPE.TYPE_AIR_REMOTECONTROL);
                        addDevice.setUid(DeviceType.TYPE.TYPE_AIR_REMOTECONTROL + deviceName);
                        addDevice.setName(deviceName);
                        addDevice.setRemotecontrolUid(currentSelectRemotecontrol.getUid());
                        boolean addresult = RemoteControlManager.getInstance().addDeviceDbLocal(addDevice, currentSelectedRoom);
                        if (addresult) {
                            startActivity(new Intent(this, DevicesActivity.class));
                        } else {
                            ToastSingleShow.showText(this, "添加空调遥控器失败");
                        }
                        break;
                    case DeviceType.TYPE.TYPE_TV_REMOTECONTROL:
                        if (deviceName.equals("")) {
                            deviceName = "智能电视";
                        }
                        boolean isTvAdded = RemoteControlManager.getInstance().judgTvDeviceisAdded(deviceName);
                        if (isTvAdded) {
                            ToastSingleShow.showText(this, "已存在相同名称的电视遥控器");
                            return;
                        }
                        // 绑定智能遥控,现在智能单个添加，这个不扫码的虚拟设备需要给他一个识别码
                        SmartDev tvDevice = new SmartDev();
                        tvDevice.setType(DeviceType.TYPE.TYPE_TV_REMOTECONTROL);
                        tvDevice.setUid(DeviceType.TYPE.TYPE_TV_REMOTECONTROL + deviceName);
                        tvDevice.setName(deviceName);
                        tvDevice.setRemotecontrolUid(currentSelectRemotecontrol.getUid());
                        boolean addTvresult = RemoteControlManager.getInstance().addDeviceDbLocal(tvDevice, currentSelectedRoom);
                        if (addTvresult) {
                            startActivity(new Intent(this, DevicesActivity.class));
                        } else {
                            ToastSingleShow.showText(this, "添加电视遥控器失败");
                        }
                        break;
                    case DeviceType.TYPE.TYPE_TVBOX_REMOTECONTROL:
                        if (deviceName.equals("")) {
                            deviceName = "智能机顶盒遥控";
                        }
                        boolean isTvBoxAdded = RemoteControlManager.getInstance().judgTvDeviceisAdded(deviceName);
                        if (isTvBoxAdded) {
                            ToastSingleShow.showText(this, "已存在相同名称的电视机顶盒遥控器");
                            return;
                        }
                        // 绑定智能遥控,现在智能单个添加，这个不扫码的虚拟设备需要给他一个识别码
                        SmartDev tvBoxDevice = new SmartDev();
                        tvBoxDevice.setType(DeviceType.TYPE.TYPE_TVBOX_REMOTECONTROL);
                        tvBoxDevice.setUid(DeviceType.TYPE.TYPE_TVBOX_REMOTECONTROL + deviceName);
                        tvBoxDevice.setName(deviceName);
                        tvBoxDevice.setRemotecontrolUid(currentSelectRemotecontrol.getUid());
                        boolean addTvBoxresult = RemoteControlManager.getInstance().addDeviceDbLocal(tvBoxDevice, currentSelectedRoom);
                        if (addTvBoxresult) {
                            startActivity(new Intent(this, DevicesActivity.class));
                        } else {
                            ToastSingleShow.showText(this, "添加电视机顶盒遥控器失败");
                        }
                        break;
                    case DeviceType.TYPE.TYPE_MENLING:
                        //TODO 调试
                        if (device == null) {
                            device = new QrcodeSmartDevice();
                            device.setVer("1");
                        }
                        if (deviceName.equals("")) {
                            deviceName = "智能门铃";
                        }
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
                        break;
                }
                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.layout_room_select:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("addDeviceSelectRoom", true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                break;
            case R.id.layout_getway_select:
                if (layout_getway_list.getVisibility() == View.VISIBLE) {
                    layout_getway_list.setVisibility(View.GONE);
                    imageview_getway_arror_right.setImageResource(R.drawable.directionicon);
                } else {
                    layout_getway_list.setVisibility(View.VISIBLE);
                    imageview_getway_arror_right.setImageResource(R.drawable.nextdirectionicon);
                }
                break;
            case R.id.layout_remotecontrol_select:
                if (layout_remotecontrol_list.getVisibility() == View.VISIBLE) {
                    layout_remotecontrol_list.setVisibility(View.GONE);
                    imageview_remotecontrol_arror_right.setImageResource(R.drawable.directionicon);
                } else {
                    layout_remotecontrol_list.setVisibility(View.VISIBLE);
                    imageview_remotecontrol_arror_right.setImageResource(R.drawable.nextdirectionicon);
                }
                break;
        }
    }

    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;
    private boolean isOnActivityResult;
    private boolean isStartFromExperience;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            isOnActivityResult = true;
            String roomName = data.getStringExtra("roomName");
            Log.i(TAG, "roomName=" + roomName);
            textview_select_room_name.setText(roomName);
        }
    }
}
