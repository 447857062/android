package com.deplink.homegenius.activity.device.remoteControl.realRemoteControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.DeviceList;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.activity.device.AddDeviceActivity;
import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.device.adapter.GetwaySelectListAdapter;
import com.deplink.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.getway.GetwayManager;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlManager;
import com.deplink.homegenius.manager.room.RoomListener;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import com.deplink.homegenius.view.edittext.ClearEditText;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class RemoteControlActivity extends Activity implements View.OnClickListener, RoomListener {
    private static final String TAG = "RemoteControlActivity";
    private RemoteControlManager mRemoteControlManager;
    private TextView textview_title;
    private FrameLayout image_back;
    private GetwaySelectListAdapter selectGetwayAdapter;
    private List<GatwayDevice> mGetways;
    private ListView listview_select_getway;
    private RelativeLayout layout_getway_list;
    private TextView textview_select_getway_name;
    private TextView textview_edit;
    private TextView textview_select_room_name;
    private RelativeLayout layout_getway;
    private RelativeLayout layout_select_room;
    private ImageView imageview_getway_arror_right;
    private TextView button_delete_device;
    private DeviceManager mDeviceManager;
    private boolean isOnActivityResult;
    private boolean isStartFromExperience;
    private ClearEditText edittext_input_devie_name;
    private String deviceName;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;
    private boolean isLogin;
    private RoomManager mRoomManager;
    private String selectGetwayName;
    private DeviceListener mDeviceListener;
    private DeleteDeviceDialog deleteDialog;
    private String deviceUid;
    private Room room;
    private GatwayDevice selectedGatway;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        deleteDialog = new DeleteDeviceDialog(this);
        mDeviceManager = DeviceManager.getInstance();
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager(this, this);
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        textview_title.setText("万能遥控");
        textview_edit.setText("完成");
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this);
        connectLostDialog = new MakeSureDialog(RemoteControlActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(RemoteControlActivity.this, LoginActivity.class));
            }
        });
        mGetways = new ArrayList<>();
        mGetways.addAll(GetwayManager.getInstance().getAllGetwayDevice());
        selectGetwayAdapter = new GetwaySelectListAdapter(this, mGetways);
        listview_select_getway.setAdapter(selectGetwayAdapter);
        listview_select_getway.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectGetwayName = mGetways.get(position).getName();
                textview_select_getway_name.setText(selectGetwayName);
                layout_getway_list.setVisibility(View.GONE);
                if(!isStartFromExperience){
                    action = "alertgetway";
                    selectedGatway = mGetways.get(position);
                    deviceUid = mRemoteControlManager.getmSelectRemoteControlDevice().getUid();
                    mDeviceManager.alertDeviceHttp(deviceUid, null, null, selectedGatway.getUid());
                }
            }
        });
        mDeviceManager.InitDeviceManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {
            }


            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);

            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {

            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                isLogin = false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }
        };
        mDeviceListener = new DeviceListener() {
            @Override
            public void responseBindDeviceResult(String result) {
                super.responseBindDeviceResult(result);
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
                    mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_RESULT);
                } else {
                    mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_FAILED);
                }
            }

            @Override
            public void responseAlertDeviceHttpResult(DeviceOperationResponse result) {
                super.responseAlertDeviceHttpResult(result);
                switch (action) {
                    case "alertroom":
                        mRemoteControlManager.updateSmartDeviceInWhatRoom(room, deviceUid);
                        break;
                    case "alertname":
                        boolean saveNameresult = mRemoteControlManager.saveCurrentSelectDeviceName(deviceName);
                        if (!saveNameresult) {
                            Toast.makeText(RemoteControlActivity.this, "更新智能设备名称失败", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(RemoteControlActivity.this, DevicesActivity.class));
                        }
                        break;
                    case "alertgetway":
                        boolean saveGetwayResult = mRemoteControlManager.updateSmartDeviceGetway(selectedGatway);
                        if (!saveGetwayResult) {
                            ToastSingleShow.showText(RemoteControlActivity.this, "更新智能设备所属网关失败");
                        }
                        break;
                }
                action = "";
            }

            @Override
            public void responseDeleteDeviceHttpResult(DeviceOperationResponse result) {
                super.responseDeleteDeviceHttpResult(result);
                if (LocalConnectmanager.getInstance().isLocalconnectAvailable()) {
                    mDeviceManager.deleteSmartDevice();
                } else {
                    DialogThreeBounce.hideLoading();
                    mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_RESULT);
                    ToastSingleShow.showText(RemoteControlActivity.this, "无可用的网关");
                }
            }
        };
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
        button_delete_device.setOnClickListener(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
        mDeviceManager.removeDeviceListener(mDeviceListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        mDeviceManager.addDeviceListener(mDeviceListener);
        isLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        deviceName = edittext_input_devie_name.getText().toString();
        if (!isStartFromExperience) {
            if (!isOnActivityResult) {
                SmartDev smartDev = DataSupport.where("Uid=?", mRemoteControlManager.getmSelectRemoteControlDevice().getUid()).findFirst(SmartDev.class, true);
                if (smartDev.getRooms() == null) {
                    textview_select_room_name.setText("全部");
                } else {
                    if (smartDev.getRooms().size() == 1) {
                        textview_select_room_name.setText(smartDev.getRooms().get(0).getRoomName());
                    } else {
                        textview_select_room_name.setText("全部");
                    }
                }
                GatwayDevice temp = smartDev.getGetwayDevice();
                if (temp == null) {
                    GatwayDevice localDbGatwayDevice= DataSupport.where("uid=?", smartDev.getGetwayDeviceUid()).findFirst(GatwayDevice.class);
                    if(localDbGatwayDevice!=null){
                        textview_select_getway_name.setText(localDbGatwayDevice.getName());
                    }else{
                        textview_select_getway_name.setText("未设置网关");
                    }
                } else {
                    textview_select_getway_name.setText(smartDev.getGetwayDevice().getName());
                }
            } else {
                isOnActivityResult = false;
                textview_select_getway_name.setText("未设置网关");
            }
            String deviceName = mDeviceManager.getCurrentSelectSmartDevice().getName();
            edittext_input_devie_name.setText(deviceName);
            edittext_input_devie_name.setSelection(deviceName.length());
        } else {
            if (!isOnActivityResult) {
                textview_select_room_name.setText("全部");
            }
            textview_select_getway_name.setText("未设置网关");
            edittext_input_devie_name.setText("我家的遥控器");
            edittext_input_devie_name.setSelection(6);
        }

    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        button_delete_device = findViewById(R.id.button_delete_device);
        textview_select_room_name = findViewById(R.id.textview_select_room_name);
        image_back = findViewById(R.id.image_back);
        layout_getway_list = findViewById(R.id.layout_getway_list);
        textview_select_getway_name = findViewById(R.id.textview_select_getway_name);
        layout_getway = findViewById(R.id.layout_getway);
        layout_select_room = findViewById(R.id.layout_select_room);
        listview_select_getway = findViewById(R.id.listview_select_getway);
        imageview_getway_arror_right = findViewById(R.id.imageview_getway_arror_right);
        edittext_input_devie_name = findViewById(R.id.edittext_input_devie_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_edit:
                action = "alertname";
                String changeDeviceName = edittext_input_devie_name.getText().toString();
                if (changeDeviceName.equals("")) {
                    ToastSingleShow.showText(this, "请输入设备而名称");
                    return;
                }
                if(!isStartFromExperience){
                    if (!changeDeviceName.equals(deviceName)) {
                        mRemoteControlManager.saveCurrentSelectDeviceName(changeDeviceName);
                    }
                    if (isLogin) {
                        deviceUid=DeviceManager.getInstance().getCurrentSelectSmartDevice().getUid();
                        deviceName = changeDeviceName;
                        mDeviceManager.alertDeviceHttp(deviceUid, changeDeviceName, null, null);
                    }
                }else{
                    startActivity(new Intent(this,ExperienceDevicesActivity.class));
                }

                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_delete_device:
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        if (!isStartFromExperience) {
                            if (isLogin) {
                                DialogThreeBounce.showLoading(RemoteControlActivity.this);
                                mDeviceManager.deleteDeviceHttp();
                            } else {
                                ToastSingleShow.showText(RemoteControlActivity.this, "用户未登录");
                            }
                        } else {
                            startActivity(new Intent(RemoteControlActivity.this, ExperienceDevicesActivity.class));
                        }
                    }
                });
                deleteDialog.show();

                break;
            case R.id.layout_select_room:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("addDeviceSelectRoom", true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                break;
            case R.id.layout_getway:
                if (layout_getway_list.getVisibility() == View.VISIBLE) {
                    layout_getway_list.setVisibility(View.GONE);
                    imageview_getway_arror_right.setImageResource(R.drawable.directionicon);
                } else {
                    layout_getway_list.setVisibility(View.VISIBLE);
                    imageview_getway_arror_right.setImageResource(R.drawable.nextdirectionicon);
                }
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            isOnActivityResult = true;
            String roomName = data.getStringExtra("roomName");
            Log.i(TAG, "roomName=" + roomName);
            if (!isStartFromExperience) {
                if (isLogin) {
                    action = "alertroom";
                    room = RoomManager.getInstance().findRoom(roomName, true);
                    deviceUid = DeviceManager.getInstance().getCurrentSelectSmartDevice().getUid();
                    mDeviceManager.alertDeviceHttp(deviceUid, room.getUid(), null, null);
                } else {
                    ToastSingleShow.showText(RemoteControlActivity.this, "未登录登录后操作");
                }
            }
            textview_select_room_name.setText(roomName);
        }
    }

    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;
    private static final int MSG_HANDLE_DELETE_DEVICE_RESULT = 100;
    private static final int MSG_HANDLE_DELETE_DEVICE_FAILED = 101;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HANDLE_DELETE_DEVICE_RESULT:
                    mDeviceManager.deleteDBSmartDevice(mDeviceManager.getCurrentSelectSmartDevice().getUid());
                    Toast.makeText(RemoteControlActivity.this, "删除设备成功", Toast.LENGTH_SHORT).show();
                    mRemoteControlManager.deleteCurrentSelectDevice();
                    startActivity(new Intent(RemoteControlActivity.this, DevicesActivity.class));
                    break;
                case MSG_HANDLE_DELETE_DEVICE_FAILED:
                    Toast.makeText(RemoteControlActivity.this, "删除设备失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void responseQueryResultHttps(List<Room> result) {

    }

    @Override
    public void responseAddRoomResult(String result) {

    }

    @Override
    public void responseDeleteRoomResult() {

    }

    @Override
    public void responseUpdateRoomNameResult() {

    }
}
