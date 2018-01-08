package com.deplink.homegenius.activity.device.smartlock;

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
import com.deplink.homegenius.Protocol.json.device.getway.Device;
import com.deplink.homegenius.manager.device.getway.GetwayManager;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

import com.deplink.homegenius.Protocol.json.device.DeviceList;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.lock.SSIDList;
import com.deplink.homegenius.activity.device.AddDeviceActivity;
import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.device.adapter.GetwaySelectListAdapter;
import com.deplink.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.smartlock.SmartLockManager;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import com.deplink.homegenius.view.edittext.ClearEditText;

public class EditSmartLockActivity extends Activity implements View.OnClickListener, DeviceListener {
    private static final String TAG = "EditSmartLockActivity";
    private FrameLayout image_back;
    private Button button_delete_device;
    private DeviceManager mDeviceManager;
    private TextView textview_select_room_name;
    private RelativeLayout layout_select_room;
    private TextView textview_title;
    private TextView textview_edit;
    private ClearEditText edittext_input_devie_name;
    private DeleteDeviceDialog deleteDialog;
    private SmartLockManager mSmartLockManager;
    private List<Device> mGetways;
    private ListView listview_select_getway;
    private RelativeLayout layout_getway_list;
    private TextView textview_select_getway_name;
    private RelativeLayout layout_getway;
    private ImageView imageview_getway_arror_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_smart_lock);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        button_delete_device.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
    }

    private boolean isStartFromExperience;

    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mDeviceManager = DeviceManager.getInstance();
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        textview_title.setText("编辑");
        textview_edit.setText("完成");
        if (isStartFromExperience) {
            edittext_input_devie_name.setText("我家的门锁");
            edittext_input_devie_name.setSelection(5);
        } else {
            mDeviceManager.InitDeviceManager(this, this);
            mSmartLockManager.InitSmartLockManager(this);
        }
        deleteDialog = new DeleteDeviceDialog(this);

        mGetways = new ArrayList<>();
        mGetways.addAll(GetwayManager.getInstance().getAllGetwayDevice());
        GetwaySelectListAdapter selectGetwayAdapter = new GetwaySelectListAdapter(this, mGetways);
        listview_select_getway.setAdapter(selectGetwayAdapter);
        listview_select_getway.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectGetwayName = mGetways.get(position).getName();
                textview_select_getway_name.setText(selectGetwayName);
                layout_getway_list.setVisibility(View.GONE);
                boolean result = mSmartLockManager.updateSmartDeviceGetway(mGetways.get(position));
                if (!result) {
                    Toast.makeText(EditSmartLockActivity.this, "更新智能设备所属网关失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private String selectGetwayName;

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        textview_select_getway_name = findViewById(R.id.textview_select_getway_name);
        textview_edit = findViewById(R.id.textview_edit);
        image_back = findViewById(R.id.image_back);
        button_delete_device = findViewById(R.id.button_delete_device);
        layout_select_room = findViewById(R.id.layout_select_room);
        textview_select_room_name = findViewById(R.id.textview_select_room_name);
        edittext_input_devie_name = findViewById(R.id.edittext_input_devie_name);
        layout_getway_list = findViewById(R.id.layout_getway_list);
        layout_getway = findViewById(R.id.layout_getway);
        listview_select_getway = findViewById(R.id.listview_select_getway);
        imageview_getway_arror_right = findViewById(R.id.imageview_getway_arror_right);
    }

    private boolean isOnActivityResult;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            isOnActivityResult = true;
            String roomName = data.getStringExtra("roomName");
            Log.i(TAG, "roomName=" + roomName);
            if (!isStartFromExperience) {
                Room room = RoomManager.getInstance().findRoom(roomName, true);
                String deviceUid = mDeviceManager.getCurrentSelectSmartDevice().getUid();
                mSmartLockManager.updateSmartDeviceInWhatRoom(room, deviceUid);
            }
            textview_select_room_name.setText(roomName);
        }
    }

    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_edit:
                String devciename = edittext_input_devie_name.getText().toString();
                mSmartLockManager.updateSmartDeviceName(devciename);
                onBackPressed();
                break;
            case R.id.layout_select_room:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("addDeviceSelectRoom", true);
                intent.putExtra("isStartFromExperience", isStartFromExperience);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                mSmartLockManager.setEditSmartLock(true);
                break;
            case R.id.button_delete_device:
                //删除设备
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        if (!isStartFromExperience) {
                            DialogThreeBounce.showLoading(EditSmartLockActivity.this);
                            mDeviceManager.deleteSmartDevice();
                        } else {
                            startActivity(new Intent(EditSmartLockActivity.this, ExperienceDevicesActivity.class));
                        }
                    }
                });
                deleteDialog.show();
                break;
            case R.id.image_back:
                onBackPressed();
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
    protected void onResume() {
        super.onResume();
        if (isStartFromExperience) {

        } else {
            String lockName = mSmartLockManager.getCurrentSelectLock().getName();
            if (lockName != null) {
                edittext_input_devie_name.setText(lockName);
                Log.i(TAG, "lockName=" + lockName + "lockName.length()=" + lockName.length());
                edittext_input_devie_name.setSelection(lockName.length());
            }
           String roomname= getIntent().getStringExtra("roomName");
            if(roomname!=null){
                textview_select_room_name.setText(roomname);
            }
            else if (!isOnActivityResult) {
                isOnActivityResult = false;
                if (mSmartLockManager.getCurrentSelectLock().getRooms().size() == 1) {
                    textview_select_room_name.setText(mSmartLockManager.getCurrentSelectLock().getRooms().get(0).getRoomName());
                } else {
                    textview_select_room_name.setText("全部");
                }

                SmartDev smartDev = DataSupport.where("Uid=?", mSmartLockManager.getCurrentSelectLock().getUid()).findFirst(SmartDev.class, true);
                Device temp = smartDev.getGetwayDevice();
                if (temp == null) {
                    textview_select_getway_name.setText("未设置网关");
                } else {
                    textview_select_getway_name.setText(smartDev.getGetwayDevice().getName());
                }
            }
        }


    }

    @Override
    public void responseQueryResult(String result) {

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
                    Toast.makeText(EditSmartLockActivity.this, "删除设备成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditSmartLockActivity.this, DevicesActivity.class));
                    break;
                case MSG_HANDLE_DELETE_DEVICE_FAILED:
                    Toast.makeText(EditSmartLockActivity.this, "删除设备失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void responseBindDeviceResult(String result) {

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
    public void responseWifiListResult(List<SSIDList> wifiList) {

    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }
}
