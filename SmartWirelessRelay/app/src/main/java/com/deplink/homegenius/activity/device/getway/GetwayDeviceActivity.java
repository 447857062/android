package com.deplink.homegenius.activity.device.getway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.DeviceList;
import com.deplink.homegenius.activity.device.AddDeviceActivity;
import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.activity.personal.wifi.ScanWifiListActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.getway.GetwayListener;
import com.deplink.homegenius.manager.device.getway.GetwayManager;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.edittext.ClearEditText;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class GetwayDeviceActivity extends Activity implements View.OnClickListener, GetwayListener {
    private static final String TAG = "GetwayDeviceActivity";
    private TextView button_delete_device;
    private GetwayManager mGetwayManager;
    private boolean isStartFromExperience;
    private RelativeLayout layout_config_wifi_getway;
    private RelativeLayout layout_select_room;
    private TextView textview_select_room_name;
    private FrameLayout image_back;
    private ClearEditText edittext_input_devie_name;
    private TextView textview_title;
    private TextView textview_edit;
    private String currentSelectDeviceName;
    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;
    private DeleteDeviceDialog deleteDialog;
    private boolean isUserLogin;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;
    private DeviceManager mDeviceManager;
    private String inputDeviceName;
    private DeviceListener mDeviceListener;
    private String deviceUid;
    private Room room;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getway_device);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("智能网关");
        textview_edit.setText("完成");
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        connectLostDialog = new MakeSureDialog(GetwayDeviceActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(GetwayDeviceActivity.this, LoginActivity.class));
            }
        });
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {

            }


            @Override
            public void deviceOpSuccess(String op, final String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                isUserLogin = false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
            }
        };
        mDeviceListener = new DeviceListener() {
            @Override
            public void responseAlertDeviceHttpResult(DeviceOperationResponse result) {
                super.responseAlertDeviceHttpResult(result);
                Log.i(TAG, "修改设备属性:" + result.toString());
                if (action.equalsIgnoreCase("alertroom")) {
                    mGetwayManager.updateGetwayDeviceInWhatRoom(room, deviceUid);
                } else if (action.equalsIgnoreCase("alertname")) {
                    Message msg = Message.obtain();
                    msg.what = MSG_ALERT_DEVICENAME_RESULT;
                    mHandler.sendMessage(msg);
                }
                action = "";
            }
        };
        mGetwayManager = GetwayManager.getInstance();
        mGetwayManager.InitGetwayManager(this, this);
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this);
        if (isStartFromExperience) {
            edittext_input_devie_name.setText("家里的网关");
            edittext_input_devie_name.setSelection(5);
            edittext_input_devie_name.clearFocus();
            textview_select_room_name.setText("全部");
        } else {
            currentSelectDeviceName = mGetwayManager.getCurrentSelectGetwayDevice().getName();
            edittext_input_devie_name.setText(currentSelectDeviceName);
            edittext_input_devie_name.setSelection(currentSelectDeviceName.length());
            edittext_input_devie_name.clearFocus();
            List<Room> rooms = mGetwayManager.getCurrentSelectGetwayDevice().getRoomList();
            if (rooms.size() == 1) {
                textview_select_room_name.setText(rooms.get(0).getRoomName());
            } else {
                textview_select_room_name.setText("全部");
            }
        }
        deleteDialog = new DeleteDeviceDialog(this);
    }

    private void initEvents() {
        button_delete_device.setOnClickListener(this);
        layout_config_wifi_getway.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        button_delete_device = findViewById(R.id.button_delete_device);
        layout_config_wifi_getway = findViewById(R.id.layout_config_wifi_getway);
        layout_select_room = findViewById(R.id.layout_select_room);
        textview_select_room_name = findViewById(R.id.textview_select_room_name);
        image_back = findViewById(R.id.image_back);
        edittext_input_devie_name = findViewById(R.id.edittext_input_devie_name);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        mDeviceManager.addDeviceListener(mDeviceListener);
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDeviceManager.removeDeviceListener(mDeviceListener);
        manager.removeEventCallback(ec);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_delete_device:
                //删除设备
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        if (isStartFromExperience) {
                            Toast.makeText(GetwayDeviceActivity.this, "删除网关设备成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(GetwayDeviceActivity.this, ExperienceDevicesActivity.class));
                        } else {
                            if (isUserLogin) {
                                mGetwayManager.deleteDeviceHttp();
                            } else {
                                ToastSingleShow.showText(GetwayDeviceActivity.this, "未登录,登录后才能操作");
                            }
                        }
                    }
                });
                deleteDialog.show();
                break;
            case R.id.layout_config_wifi_getway:
                Intent inent = new Intent(this, ScanWifiListActivity.class);
                inent.putExtra("isShowSkipOption", false);
                startActivity(inent);
                break;
            case R.id.layout_select_room:
                if (isStartFromExperience) {
                    Intent intent = new Intent(this, AddDeviceActivity.class);
                    intent.putExtra("addDeviceSelectRoom", true);
                    startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                } else {
                    if (isUserLogin) {
                        Intent intent = new Intent(this, AddDeviceActivity.class);
                        intent.putExtra("addDeviceSelectRoom", true);
                        startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                    }
                }
                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                if (isStartFromExperience) {
                    startActivity(new Intent(this,ExperienceDevicesActivity.class));
                } else {
                    if (isUserLogin) {
                        inputDeviceName = edittext_input_devie_name.getText().toString();
                        if (!inputDeviceName.equals(currentSelectDeviceName)) {
                            String deviceUid = mGetwayManager.getCurrentSelectGetwayDevice().getUid();
                            mDeviceManager.alertDeviceHttp(deviceUid, null, inputDeviceName, null);
                            action = "alertname";
                        }
                    } else {
                        ToastSingleShow.showText(this, "未登录,登录后才能操作");
                    }
                }
                break;
        }
    }

    private static final int MSG_HANDLE_DELETE_DEVICE_RESULT = 100;
    private static final int MSG_ALERT_DEVICENAME_RESULT = 101;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HANDLE_DELETE_DEVICE_RESULT:
                    Log.i(TAG, "删除getway设备uid " + mGetwayManager.getCurrentSelectGetwayDevice().getUid());
                    mGetwayManager.deleteDBGetwayDevice(mGetwayManager.getCurrentSelectGetwayDevice().getUid());
                    Toast.makeText(GetwayDeviceActivity.this, "删除设备成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GetwayDeviceActivity.this, DevicesActivity.class));
                    break;
                case MSG_ALERT_DEVICENAME_RESULT:
                    Log.i(TAG, "修改设备名称 handler msg");
                    mGetwayManager.updateGetwayDeviceName(inputDeviceName);
                    startActivity(new Intent(GetwayDeviceActivity.this, DevicesActivity.class));
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            String roomName = data.getStringExtra("roomName");
            Log.i(TAG, "isStartFromExperience=" + isStartFromExperience);
            if (!isStartFromExperience) {
                room = RoomManager.getInstance().findRoom(roomName, true);
                deviceUid = mGetwayManager.getCurrentSelectGetwayDevice().getUid();
                mDeviceManager.alertDeviceHttp(deviceUid, room.getUid(), null, null);
                action = "alertroom";
            }
            textview_select_room_name.setText(roomName);
        }
    }

    @Override
    public void responseResult(String result) {
        boolean deleteSuccess = true;
        Gson gson = new Gson();
        DeviceList mDeviceList = gson.fromJson(result, DeviceList.class);
        for (int i = 0; i < mDeviceList.getDevice().size(); i++) {
            if (mDeviceList.getDevice().get(i).getUid().equals(mGetwayManager.getCurrentSelectGetwayDevice().getUid())) {
                deleteSuccess = false;
            }
        }
        if (deleteSuccess) {
            mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_RESULT);
        }
    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }

    @Override
    public void responseDeleteDeviceHttpResult(DeviceOperationResponse result) {
        if (result.getStatus() != null && result.getStatus().equals("ok")) {
            if (LocalConnectmanager.getInstance().isLocalconnectAvailable()) {
                mGetwayManager.deleteGetwayDevice();
            } else {
                mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_RESULT);
            }

        }
    }

}
