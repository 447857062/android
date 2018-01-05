package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl;

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

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.GetwaySelectListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.DeleteDeviceDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class RemoteControlActivity extends Activity implements View.OnClickListener, RemoteControlListener,DeviceListener {
    private static final String TAG = "RemoteControlActivity";

    private RemoteControlManager mRemoteControlManager;
    private TextView textview_title;
    private FrameLayout image_back;

    private GetwaySelectListAdapter selectGetwayAdapter;
    private List<Device> mGetways;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        initViews();
        initDatas();
        initEvents();
    }

    private String selectGetwayName;

    private void initDatas() {
        deleteDialog = new DeleteDeviceDialog(this);
        mDeviceManager = DeviceManager.getInstance();
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        textview_title.setText("万能遥控");
        textview_edit.setText("完成");
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this, this);
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
                boolean result = mRemoteControlManager.updateSmartDeviceGetway(mGetways.get(position));
                if (!result) {
                    ToastSingleShow.showText(RemoteControlActivity.this, "更新智能设备所属网关失败");
                }
            }
        });
        if (isStartFromExperience) {

        } else {
            mDeviceManager.InitDeviceManager(this, this);

        }
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
        button_delete_device.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isStartFromExperience) {
            if (!isOnActivityResult) {
                if(mRemoteControlManager.getmSelectRemoteControlDevice().getRooms()==null){
                    textview_select_room_name.setText("全部");
                }else{
                    for (int i = 0; i < mRemoteControlManager.getmSelectRemoteControlDevice().getRooms().size(); i++) {
                        Log.i(TAG, mRemoteControlManager.getmSelectRemoteControlDevice().getRooms().get(0).getRoomName());
                    }
                    if (mRemoteControlManager.getmSelectRemoteControlDevice().getRooms().size() == 1) {
                        textview_select_room_name.setText(mRemoteControlManager.getmSelectRemoteControlDevice().getRooms().get(0).getRoomName());
                    } else {
                        textview_select_room_name.setText("全部");
                    }
                }

                SmartDev smartDev = DataSupport.where("Uid=?", mRemoteControlManager.getmSelectRemoteControlDevice().getUid()).findFirst(SmartDev.class, true);
                Device temp = smartDev.getGetwayDevice();
                if (temp == null) {
                    textview_select_getway_name.setText("未设置网关");
                } else {
                    textview_select_getway_name.setText(smartDev.getGetwayDevice().getName());
                }
            }else{
                textview_select_room_name.setText("全部");
                textview_select_getway_name.setText("未设置网关");
            }
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
    }
    private DeleteDeviceDialog deleteDialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_edit:
                onBackPressed();
                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_delete_device:
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        if (!isStartFromExperience) {
                            DialogThreeBounce.showLoading(RemoteControlActivity.this);
                            if(LocalConnectmanager.getInstance().isLocalconnectAvailable()){
                                mDeviceManager.deleteSmartDevice();
                            }else{
                                ToastSingleShow.showText(RemoteControlActivity.this,"无可用的网关");
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
                Room room = RoomManager.getInstance().findRoom(roomName, true);
                String deviceUid = DeviceManager.getInstance().getCurrentSelectSmartDevice().getUid();
                mRemoteControlManager.updateSmartDeviceInWhatRoom(room, deviceUid);
            }
            textview_select_room_name.setText(roomName);
        }
    }

    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;
    //设置结果:{ "OP": "REPORT", "Method": "Study", "Result": "err" }
    @Override
    public void responseQueryResult(String result) {
        Log.i(TAG, "responseQueryResult :" + result);
        Message msg = Message.obtain();
        msg.obj = result;
        mHandler.sendMessage(msg);
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
