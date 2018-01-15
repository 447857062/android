package com.deplink.homegenius.activity.device.smartSwitch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.activity.device.AddDeviceActivity;
import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.device.adapter.GetwaySelectListAdapter;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.getway.GetwayManager;
import com.deplink.homegenius.manager.device.smartswitch.SmartSwitchManager;
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

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class EditActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "EditDoorbeelActivity";
    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView button_delete_device;
    private DeleteDeviceDialog deleteDialog;
    private SmartSwitchManager mSmartSwitchManager;
    private DeviceManager mDeviceManager;
    private RelativeLayout layout_select_room;
    private TextView textview_select_room_name;
    private ClearEditText edittext_add_device_input_name;
    private RelativeLayout layout_getway_select;
    private RelativeLayout layout_getway_list;
    private ListView listview_select_getway;
    private GetwaySelectListAdapter selectGetwayAdapter;
    private List<GatwayDevice> mGetways;
    private TextView textview_select_getway_name;
    private TextView textview_edit;
    private ImageView imageview_getway_arror_right;
    private String switchType;
    private String selectGetwayName;
    private boolean isStartFromExperience;
    private String deviceName;
    private boolean isOnActivityResult;
    private String action;
    private String deviceUid;
    private Room room;
    private DeviceListener mDeviceListener;
    private boolean isLogin;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;
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
        layout_select_room.setOnClickListener(this);
        layout_getway_select.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private GatwayDevice selectedGatway;
    private void initDatas() {
        textview_edit.setText("完成");
        switchType = getIntent().getStringExtra("switchType");
        Log.i(TAG, "initDatas switchType=" + switchType);
        textview_title.setText(switchType);
        deleteDialog = new DeleteDeviceDialog(this);
        mSmartSwitchManager = SmartSwitchManager.getInstance();
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this);
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
                action="alertgetway";
                selectedGatway=mGetways.get(position);
                deviceUid = mSmartSwitchManager.getCurrentSelectSmartDevice().getUid();
                mDeviceManager.alertDeviceHttp(deviceUid, null, null, selectedGatway.getUid());

            }
        });
        connectLostDialog = new MakeSureDialog(EditActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(EditActivity.this, LoginActivity.class));
            }
        });
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
                isLogin=false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }
        };
        mDeviceListener=new DeviceListener() {
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
            public void responseDeleteDeviceHttpResult(DeviceOperationResponse result) {
                super.responseDeleteDeviceHttpResult(result);
                if (result.getStatus() != null && result.getStatus().equals("ok")) {
                    mDeviceManager.deleteSmartDevice();
                }
            }

            @Override
            public void responseAlertDeviceHttpResult(DeviceOperationResponse result) {
                super.responseAlertDeviceHttpResult(result);
                deviceUid = mSmartSwitchManager.getCurrentSelectSmartDevice().getUid();
                if (action.equals("alertroom")) {
                    mSmartSwitchManager.updateSmartDeviceInWhatRoom(room, deviceUid, deviceName);
                } else if (action.equals("alertname")) {
                    boolean saveResult = mSmartSwitchManager.updateSmartDeviceName(deviceUid, deviceName);
                    if (saveResult) {
                        onBackPressed();
                    }
                }else if(action.equals("alertgetway")){
                    boolean saveDbResult = mSmartSwitchManager.updateSmartDeviceGetway(selectedGatway);
                    if (!saveDbResult) {
                        Toast.makeText(EditActivity.this, "更新智能设备所属网关失败", Toast.LENGTH_SHORT).show();
                    }
                }
                action = "";
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        deviceName = mSmartSwitchManager.getCurrentSelectSmartDevice().getName();
        if (deviceName != null && deviceName.length() > 0) {
            if (deviceName.length() > 10) {
                deviceName = deviceName.substring(0, 10);
            }
            edittext_add_device_input_name.setText(deviceName);
            edittext_add_device_input_name.setSelection(deviceName.length());
        }
        if (!isOnActivityResult) {
            isOnActivityResult = false;
            if (mSmartSwitchManager.getCurrentSelectSmartDevice().getRooms().size() == 1) {
                textview_select_room_name.setText(mSmartSwitchManager.getCurrentSelectSmartDevice().getRooms().get(0).getRoomName());
            } else {
                textview_select_room_name.setText("全部");
            }
        }
        mDeviceManager.addDeviceListener(mDeviceListener);
        manager.addEventCallback(ec);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDeviceManager.removeDeviceListener(mDeviceListener);
        manager.removeEventCallback(ec);
    }

    private void initViews() {
        image_back = findViewById(R.id.image_back);
        textview_title = findViewById(R.id.textview_title);
        button_delete_device = findViewById(R.id.button_delete_device);
        layout_select_room = findViewById(R.id.layout_room_select);
        textview_select_room_name = findViewById(R.id.textview_select_room_name);
        edittext_add_device_input_name = findViewById(R.id.edittext_add_device_input_name);
        layout_getway_list = findViewById(R.id.layout_getway_list);
        layout_getway_select = findViewById(R.id.layout_getway_select);
        listview_select_getway = findViewById(R.id.listview_select_getway);
        textview_select_getway_name = findViewById(R.id.textview_select_getway_name);
        imageview_getway_arror_right = findViewById(R.id.imageview_getway_arror_right);
        textview_edit = findViewById(R.id.textview_edit);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            String roomName = data.getStringExtra("roomName");
            Log.i(TAG, "isStartFromExperience=" + isStartFromExperience);
            isOnActivityResult = true;
            if (!isStartFromExperience) {
                action = "alertroom";
                room = RoomManager.getInstance().findRoom(roomName, true);
                deviceUid = mSmartSwitchManager.getCurrentSelectSmartDevice().getUid();
                deviceName = edittext_add_device_input_name.getText().toString();
                mDeviceManager.alertDeviceHttp(deviceUid, room.getUid(), null, null);
            }
            textview_select_room_name.setText(roomName);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                action = "alertname";
                deviceUid = mSmartSwitchManager.getCurrentSelectSmartDevice().getUid();
                deviceName = edittext_add_device_input_name.getText().toString();
                if(isLogin){
                    mDeviceManager.alertDeviceHttp(deviceUid, null, deviceName, null);
                }else{
                    ToastSingleShow.showText(this,"未登录,登录后操作");
                }

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
            case R.id.layout_room_select:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("addDeviceSelectRoom", true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                break;
            case R.id.button_delete_device:
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        DialogThreeBounce.showLoading(EditActivity.this);
                        mDeviceManager.deleteDeviceHttp();


                    }
                });
                deleteDialog.show();
                break;

        }
    }
}
