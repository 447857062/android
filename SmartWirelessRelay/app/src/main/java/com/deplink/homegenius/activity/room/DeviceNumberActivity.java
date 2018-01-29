package com.deplink.homegenius.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.activity.device.adapter.DeviceListAdapter;
import com.deplink.homegenius.activity.device.doorbell.DoorbeelMainActivity;
import com.deplink.homegenius.activity.device.getway.GetwayDeviceActivity;
import com.deplink.homegenius.activity.device.light.LightActivity;
import com.deplink.homegenius.activity.device.remoteControl.airContorl.AirRemoteControlMianActivity;
import com.deplink.homegenius.activity.device.remoteControl.realRemoteControl.RemoteControlActivity;
import com.deplink.homegenius.activity.device.remoteControl.topBox.TvBoxMainActivity;
import com.deplink.homegenius.activity.device.remoteControl.tv.TvMainActivity;
import com.deplink.homegenius.activity.device.router.RouterMainActivity;
import com.deplink.homegenius.activity.device.smartSwitch.SwitchFourActivity;
import com.deplink.homegenius.activity.device.smartSwitch.SwitchOneActivity;
import com.deplink.homegenius.activity.device.smartSwitch.SwitchThreeActivity;
import com.deplink.homegenius.activity.device.smartSwitch.SwitchTwoActivity;
import com.deplink.homegenius.activity.device.smartlock.SmartLockActivity;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.constant.RoomConstant;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.doorbeel.DoorbeelManager;
import com.deplink.homegenius.manager.device.getway.GetwayManager;
import com.deplink.homegenius.manager.device.light.SmartLightManager;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlManager;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.manager.device.smartlock.SmartLockManager;
import com.deplink.homegenius.manager.device.smartswitch.SmartSwitchManager;
import com.deplink.homegenius.manager.room.RoomManager;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * 查看智能设备列表的界面
 */
public class DeviceNumberActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG="DeviceNumberActivity";
    private FrameLayout image_back;
    private TextView textview_edit;
    private DeviceListAdapter mDeviceAdapter;
    /**
     * 上面半部分列表的数据
     */
    private List<GatwayDevice> datasTop;
    /**
     * 下面半部分列表的数据
     */
    private List<SmartDev> datasBottom;

    private ListView listview_devies;
    private RelativeLayout layout_device_number_root;
    private RoomManager mRoomManager;
    private Room currentRoom;
    private DeviceManager mDeviceManager;
    private GetwayManager mGetwayManager;
    private TextView textview_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_number);
        initViews();
        initDatas();
        initEvents();
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }
    private boolean isStartFromExperience;
    @Override
    protected void onResume() {
        super.onResume();
        //使用数据库中的数据
        datasTop.clear();
        datasBottom.clear();
        datasTop.addAll(currentRoom.getmGetwayDevices());
        datasBottom .addAll(currentRoom.getmDevices());
        mDeviceAdapter.setTopList(datasTop);
        mDeviceAdapter.setBottomList(datasBottom);
        listview_devies.setAdapter(mDeviceAdapter);
        isStartFromExperience = mDeviceManager.isStartFromExperience();
    }
    private void initDatas() {
        mRoomManager = RoomManager.getInstance();
        mDeviceManager=DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this);
        mGetwayManager=GetwayManager.getInstance();
        mGetwayManager.InitGetwayManager(this,null);
        currentRoom=mRoomManager.getCurrentSelectedRoom();

        if(!isStartFromExperience){
            textview_title.setText(currentRoom.getRoomName());
        }
        setRoomBg();
        datasTop = new ArrayList<>();
        datasBottom = new ArrayList<>();
        mDeviceAdapter = new DeviceListAdapter(this, datasTop, datasBottom);
        listview_devies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceManager.getInstance().setStartFromExperience(false);
                if (datasTop.size() < (position + 1)) {
                    //智能设备
                    String deviceType = datasBottom.get(position - datasTop.size()).getType();
                    Log.i(TAG, "智能设备类型=" + deviceType);
                    mDeviceManager.setCurrentSelectSmartDevice(datasBottom.get(position - datasTop.size()));
                    switch (deviceType) {
                        case DeviceTypeConstant.TYPE.TYPE_LOCK:
                            SmartLockManager.getInstance().setCurrentSelectLock(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DeviceNumberActivity.this, SmartLockActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_MENLING:
                            DoorbeelManager.getInstance().setCurrentSelectedDoorbeel(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DeviceNumberActivity.this, DoorbeelMainActivity.class));
                            break;
                        case "IRMOTE_V2":
                        case DeviceTypeConstant.TYPE.TYPE_REMOTECONTROL:
                            RemoteControlManager.getInstance().setmSelectRemoteControlDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DeviceNumberActivity.this, RemoteControlActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL:
                            RemoteControlManager.getInstance().setmSelectRemoteControlDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DeviceNumberActivity.this, AirRemoteControlMianActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_ROUTER:
                            RouterManager.getInstance().setCurrentSelectedRouter(datasBottom.get(position-datasTop.size()));
                            startActivity(new Intent(DeviceNumberActivity.this, RouterMainActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL:
                            RemoteControlManager.getInstance().setmSelectRemoteControlDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DeviceNumberActivity.this, TvMainActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL:
                            RemoteControlManager.getInstance().setmSelectRemoteControlDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DeviceNumberActivity.this, TvBoxMainActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_LIGHT:
                            SmartLightManager.getInstance().setCurrentSelectLight(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DeviceNumberActivity.this, LightActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_SWITCH:
                            SmartSwitchManager.getInstance().setCurrentSelectSmartDevice(datasBottom.get(position - datasTop.size()));
                            String deviceSubType = datasBottom.get(position - datasTop.size()).getSubType();
                            switch (deviceSubType) {
                                case  DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_ONEWAY:
                                    startActivity(new Intent(DeviceNumberActivity.this, SwitchOneActivity.class));
                                    break;
                                case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_TWOWAY:
                                    startActivity(new Intent(DeviceNumberActivity.this, SwitchTwoActivity.class));
                                    break;
                                case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_THREEWAY:
                                    startActivity(new Intent(DeviceNumberActivity.this, SwitchThreeActivity.class));
                                    break;
                                case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_FOURWAY:
                                    startActivity(new Intent(DeviceNumberActivity.this, SwitchFourActivity.class));
                                    break;
                            }
                            break;
                    }
                } else {
                    //网关设备
                    GetwayManager.getInstance().setCurrentSelectGetwayDevice(datasTop.get(position));
                    startActivity(new Intent(DeviceNumberActivity.this, GetwayDeviceActivity.class));
                }
            }
        });
    }
    private RelativeLayout layout_title;
    private void setRoomBg() {
        String roomType=currentRoom.getRoomType();
        switch (roomType){
            case RoomConstant.ROOMTYPE.TYPE_LIVING:
                layout_device_number_root.setBackgroundResource(R.drawable.livingroombackground);
                layout_title.setBackgroundResource(R.color.title_blue_bg);
                layout_title.setAlpha((float) 0.9);
                break;
            case RoomConstant.ROOMTYPE.TYPE_BED:
                layout_device_number_root.setBackgroundResource(R.drawable.bedroombackground);
                layout_title.setBackgroundResource(R.color.title_blue_bg);
                layout_title.setAlpha((float) 0.9);
                break;
            case RoomConstant.ROOMTYPE.TYPE_KITCHEN:
                layout_device_number_root.setBackgroundResource(R.drawable.kitchenbackground);
                layout_title.setBackgroundResource(R.color.title_blue_bg);
                layout_title.setAlpha((float) 0.9);
                break;
            case RoomConstant.ROOMTYPE.TYPE_STUDY:
                layout_device_number_root.setBackgroundResource(R.drawable.studybackground);
                layout_title.setBackgroundResource(R.color.title_blue_bg);
                layout_title.setAlpha((float) 0.9);
                break;
            case RoomConstant.ROOMTYPE.TYPE_STORAGE:
                layout_device_number_root.setBackgroundResource(R.drawable.storageroom);
                layout_title.setBackgroundResource(R.color.title_blue_bg);
                layout_title.setAlpha((float) 0.9);
                break;
            case RoomConstant.ROOMTYPE.TYPE_TOILET:
                layout_device_number_root.setBackgroundResource(R.drawable.toiletbackground);
                layout_title.setBackgroundResource(R.color.title_blue_bg);
                layout_title.setAlpha((float) 0.9);
                break;
            case RoomConstant.ROOMTYPE.TYPE_DINING:
                layout_device_number_root.setBackgroundResource(R.drawable.diningroombackground);
                layout_title.setBackgroundResource(R.color.title_blue_bg);
                layout_title.setAlpha((float) 0.9);
                break;
        }
    }

    private void initViews() {
        image_back = findViewById(R.id.image_back);
        textview_edit = findViewById(R.id.textview_edit);
        listview_devies= findViewById(R.id.listview_devies);
        layout_device_number_root= findViewById(R.id.layout_device_number_root);
        textview_title= findViewById(R.id.textview_title);
        layout_title= findViewById(R.id.layout_title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
               startActivity(new Intent(this,ManageRoomActivity.class));
                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
