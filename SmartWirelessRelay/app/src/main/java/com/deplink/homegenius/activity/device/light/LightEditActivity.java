package com.deplink.homegenius.activity.device.light;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.deplink.homegenius.manager.device.light.SmartLightManager;
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

public class LightEditActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LightEditActivity";
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private Button button_delete_device;
    private TextView textview_select_room_name;
    private RelativeLayout layout_select_room;
    private ClearEditText edittext_input_devie_name;
    private DeleteDeviceDialog deleteDialog;
    private GetwaySelectListAdapter selectGetwayAdapter;
    private List<GatwayDevice> mGetways;
    private ListView listview_select_getway;
    private RelativeLayout layout_getway_list;
    private TextView textview_select_getway_name;
    private RelativeLayout layout_getway;
    private ImageView imageview_getway_arror_right;
    private SmartLightManager mSmartLightManager;
    private DeviceManager mDeviceManager;
    private DeviceListener mDeviceListener;
    private GatwayDevice selectedGatway;
    private String action;
    private String deviceUid;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;
    private boolean isLogin;
    private String lightName;
    private boolean isStartFromExperience;
    private String selectGetwayName;
    /**
     * onactivityresult设置完房间名称后onresume就不能设置
     */
    private boolean isOnActivityResult;
    private Room changeRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_edit);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDeviceManager.addDeviceListener(mDeviceListener);
        manager.addEventCallback(ec);
        isLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        if (isStartFromExperience) {
            textview_select_room_name.setText("全部");
            textview_select_getway_name.setText("未设置网关");
        } else {
            lightName = mSmartLightManager.getCurrentSelectLight().getName();
            if (lightName != null) {
                edittext_input_devie_name.setText(lightName);
                edittext_input_devie_name.setSelection(lightName.length());
            }
            SmartDev smartDev = DataSupport.where("Uid=?", mSmartLightManager.getCurrentSelectLight().getUid()).findFirst(SmartDev.class, true);
            if (!isOnActivityResult) {
                if (mSmartLightManager.getCurrentSelectLight().getRooms().size() == 1) {
                    textview_select_room_name.setText(smartDev.getRooms().get(0).getRoomName());
                } else {
                    textview_select_room_name.setText("全部");
                }
            }
            GatwayDevice temp = smartDev.getGetwayDevice();
            if (temp == null) {
                textview_select_getway_name.setText("未设置网关");
            } else {
                textview_select_getway_name.setText(smartDev.getGetwayDevice().getName());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnActivityResult=false;
        mDeviceManager.removeDeviceListener(mDeviceListener);
        manager.removeEventCallback(ec);
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        button_delete_device.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
    }

    private void initDatas() {
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        mDeviceManager = DeviceManager.getInstance();
        mSmartLightManager = SmartLightManager.getInstance();
        if (isStartFromExperience) {
            edittext_input_devie_name.setText("我家的智能灯");
            edittext_input_devie_name.setSelection(6);
        }
        mDeviceManager.InitDeviceManager(this);
        mSmartLightManager.InitSmartLightManager(this);
        textview_title.setText("智能灯泡");
        textview_edit.setText("完成");
        deleteDialog = new DeleteDeviceDialog(this);
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
                if (!isStartFromExperience) {
                    action = "alertgetway";
                    selectedGatway = mGetways.get(position);
                    deviceUid = mSmartLightManager.getCurrentSelectLight().getUid();
                    mDeviceManager.alertDeviceHttp(deviceUid, null, null, selectedGatway.getUid());
                }
            }
        });
        connectLostDialog = new MakeSureDialog(LightEditActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(LightEditActivity.this, LoginActivity.class));
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
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                isLogin = false;
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }
        };
        mDeviceListener = new DeviceListener() {
            @Override
            public void responseDeleteDeviceHttpResult(DeviceOperationResponse result) {
                super.responseDeleteDeviceHttpResult(result);
                if (LocalConnectmanager.getInstance().isLocalconnectAvailable()) {
                    mDeviceManager.deleteSmartDevice();
                } else {
                    DialogThreeBounce.hideLoading();
                    mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_RESULT);
                }
            }

            @Override
            public void responseAlertDeviceHttpResult(DeviceOperationResponse result) {
                super.responseAlertDeviceHttpResult(result);
                switch (action) {
                    case "alertroom":
                        mSmartLightManager.updateSmartDeviceRoom(changeRoom, deviceUid);
                        break;
                    case "alertname":
                        boolean saveNameresult = mSmartLightManager.updateSmartDeviceName(mSmartLightManager.getCurrentSelectLight().getUid(), lightName);
                        if (!saveNameresult) {
                            Toast.makeText(LightEditActivity.this, "更新智能设备名称失败", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(LightEditActivity.this, LightActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case "alertgetway":
                        boolean saveTbResult = mSmartLightManager.updateSmartDeviceGetway(selectedGatway);
                        if (!saveTbResult) {
                            Toast.makeText(LightEditActivity.this, "更新智能设备所属网关失败", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                action = "";
            }

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
        };
        if(getIntent().getBooleanExtra("isupdateroom",false)){
            isOnActivityResult = true;
            String roomName = getIntent().getStringExtra("roomName");
            Log.i(TAG, "roomName=" + roomName);
            if (!isStartFromExperience) {
                action = "alertroom";
                changeRoom = RoomManager.getInstance().findRoom(roomName, true);
                deviceUid = mDeviceManager.getCurrentSelectSmartDevice().getUid();
                mDeviceManager.alertDeviceHttp(deviceUid, changeRoom.getUid(), null, null);
            }
            textview_select_room_name.setText(roomName);
        }
    }

    private void initViews() {
        image_back = findViewById(R.id.image_back);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        textview_select_getway_name = findViewById(R.id.textview_select_getway_name);
        button_delete_device = findViewById(R.id.button_delete_device);
        layout_select_room = findViewById(R.id.layout_select_room);
        textview_select_room_name = findViewById(R.id.textview_select_room_name);
        edittext_input_devie_name = findViewById(R.id.edittext_input_devie_name);
        layout_getway_list = findViewById(R.id.layout_getway_list);
        layout_getway = findViewById(R.id.layout_getway);
        listview_select_getway = findViewById(R.id.listview_select_getway);
        imageview_getway_arror_right = findViewById(R.id.imageview_getway_arror_right);
    }



    private static final int MSG_HANDLE_DELETE_DEVICE_RESULT = 100;
    private static final int MSG_HANDLE_DELETE_DEVICE_FAILED = 101;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HANDLE_DELETE_DEVICE_RESULT:
                    mDeviceManager.deleteDBSmartDevice(mDeviceManager.getCurrentSelectSmartDevice().getUid());
                    Toast.makeText(LightEditActivity.this, "删除设备成功", Toast.LENGTH_SHORT).show();
                    mDeviceManager.deleteDBSmartDevice(mDeviceManager.getCurrentSelectSmartDevice().getUid());
                    startActivity(new Intent(LightEditActivity.this, DevicesActivity.class));
                    break;
                case MSG_HANDLE_DELETE_DEVICE_FAILED:
                    Toast.makeText(LightEditActivity.this, "删除设备失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                if (isStartFromExperience) {
                    onBackPressed();
                } else {
                    if (isLogin) {
                        String lightnameChange = edittext_input_devie_name.getText().toString();
                        if (lightName.equals("")) {
                            ToastSingleShow.showText(this, "请输入设备名称");
                            return;
                        }
                        if (lightnameChange.equals(lightName)) {
                            onBackPressed();
                        } else {
                            action = "alertname";
                            lightName = lightnameChange;
                            deviceUid = mSmartLightManager.getCurrentSelectLight().getUid();
                            mDeviceManager.alertDeviceHttp(deviceUid, null, lightName, null);
                        }
                    } else {
                        LightEditActivity.this.finish();
                    }
                }

                break;
            case R.id.layout_select_room:
                mSmartLightManager.setEditSmartLight(true);
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("addDeviceSelectRoom", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.button_delete_device:
                //删除设备
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        if (!isStartFromExperience) {
                            if (isLogin) {
                                DialogThreeBounce.showLoading(LightEditActivity.this);
                                mDeviceManager.deleteDeviceHttp();
                            } else {
                                ToastSingleShow.showText(LightEditActivity.this, "用户未登录");
                            }
                        } else {
                            startActivity(new Intent(LightEditActivity.this, ExperienceDevicesActivity.class));
                        }
                    }
                });
                deleteDialog.show();
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
}
