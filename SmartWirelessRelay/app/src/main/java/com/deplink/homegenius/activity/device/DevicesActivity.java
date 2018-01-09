package com.deplink.homegenius.activity.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.DeviceList;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.Device;
import com.deplink.homegenius.Protocol.json.device.lock.SSIDList;
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
import com.deplink.homegenius.activity.homepage.SmartHomeMainActivity;
import com.deplink.homegenius.activity.personal.PersonalCenterActivity;
import com.deplink.homegenius.activity.room.RoomActivity;
import com.deplink.homegenius.application.AppManager;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.getway.GetwayManager;
import com.deplink.homegenius.manager.device.light.SmartLightManager;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlManager;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.manager.device.smartlock.SmartLockManager;
import com.deplink.homegenius.manager.device.smartswitch.SmartSwitchManager;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.view.dialog.devices.DeviceAtRoomDialog;
import com.deplink.sdk.android.sdk.homegenius.Deviceprops;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class DevicesActivity extends Activity implements View.OnClickListener, DeviceListener {
    private static final String TAG = "DevicesActivity";
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;
    private PullToRefreshListView listview_devies;
    private DeviceListAdapter mDeviceAdapter;
    /**
     * 上面半部分列表的数据
     */
    private List<Device> datasTop;
    /**
     * 下面半部分列表的数据
     */
    private List<SmartDev> datasBottom;
    private ImageView imageview_add_device;
    private DeviceManager mDeviceManager;
    private SmartLockManager mSmartLockManager;
    private RoomManager mRoomManager;
    private LinearLayout layout_select_room_type;
    private ImageView imageview_devices;
    private ImageView imageview_home_page;
    private ImageView imageview_rooms;
    private ImageView imageview_personal_center;
    private TextView textview_home;
    private TextView textview_device;
    private TextView textview_room;
    private TextView textview_mine;
    private DeviceAtRoomDialog roomTypeDialog;
    private RouterManager mRouterManager;
    private TextView textview_room_name;
    private List<String> mRooms = new ArrayList<>();
    private ScrollView layout_empty_view_scroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        initViews();
        initDatas();
        initEvents();
    }
    @Override
    protected void onResume() {
        super.onResume();
        textview_home.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_device.setTextColor(getResources().getColor(R.color.title_blue_bg));
        textview_room.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_mine.setTextColor(getResources().getColor(android.R.color.darker_gray));
        imageview_home_page.setImageResource(R.drawable.nocheckthehome);
        imageview_devices.setImageResource(R.drawable.checkthedevice);
        imageview_rooms.setImageResource(R.drawable.nochecktheroom);
        imageview_personal_center.setImageResource(R.drawable.nocheckthemine);
        mDeviceManager.queryDeviceList();
        datasTop.clear();
        datasBottom.clear();
        datasTop.addAll(GetwayManager.getInstance().getAllGetwayDevice());
        datasBottom.addAll(DataSupport.findAll(SmartDev.class, true));
        mDeviceAdapter.setTopList(datasTop);
        mDeviceAdapter.setBottomList(datasBottom);
        mDeviceAdapter.notifyDataSetChanged();
        listview_devies.setEmptyView(layout_empty_view_scroll);
        mDeviceManager.queryDeviceListHttp();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDeviceManager.removeDeviceListener(this);
    }

    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(DevicesActivity.this);
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(DevicesActivity.this, DevicesActivity.this);
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager(this,null);
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(DevicesActivity.this);
        roomTypeDialog = new DeviceAtRoomDialog(this, mRooms);
        mRooms.addAll(mRoomManager.getRoomNames());
        datasTop = new ArrayList<>();
        datasBottom = new ArrayList<>();
        mDeviceAdapter = new DeviceListAdapter(this, datasTop, datasBottom);
        listview_devies.getRefreshableView().setAdapter(mDeviceAdapter);
        listview_devies.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position-=1;
                if (datasTop.size() < (position + 1)) {
                    //智能设备
                    String deviceType = datasBottom.get(position - datasTop.size()).getType();
                    String deviceSubType = datasBottom.get(position - datasTop.size()).getSubType();
                    Log.i(TAG, "智能设备类型=" + deviceType);
                    mDeviceManager.setCurrentSelectSmartDevice(datasBottom.get(position - datasTop.size()));
                    mDeviceManager.setStartFromExperience(false);
                    switch (deviceType) {
                        case DeviceTypeConstant.TYPE.TYPE_LOCK:
                            //设置当前选中的门锁设备
                            mSmartLockManager.setCurrentSelectLock(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, SmartLockActivity.class));
                            break;
                        case "IRMOTE_V2":
                            RemoteControlManager.getInstance().setmSelectRemoteControlDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, RemoteControlActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL:
                            RemoteControlManager.getInstance().setmSelectRemoteControlDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, AirRemoteControlMianActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_ROUTER:
                            RouterManager.getInstance().setCurrentSelectedRouter(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, RouterMainActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL:
                            RemoteControlManager.getInstance().setmSelectRemoteControlDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, TvMainActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL:
                            RemoteControlManager.getInstance().setmSelectRemoteControlDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, TvBoxMainActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_SWITCH:
                            SmartSwitchManager.getInstance().setCurrentSelectSmartDevice(datasBottom.get(position - datasTop.size()));
                            switch (deviceSubType) {
                                case  DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_ONEWAY:
                                    startActivity(new Intent(DevicesActivity.this, SwitchOneActivity.class));
                                    break;
                                case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_TWOWAY:
                                    startActivity(new Intent(DevicesActivity.this, SwitchTwoActivity.class));
                                    break;
                                case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_THREEWAY:
                                    startActivity(new Intent(DevicesActivity.this, SwitchThreeActivity.class));
                                    break;
                                case DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_FOURWAY:
                                    startActivity(new Intent(DevicesActivity.this, SwitchFourActivity.class));
                                    break;
                            }
                            break;
                        case "智能门铃":
                            startActivity(new Intent(DevicesActivity.this, DoorbeelMainActivity.class));
                            break;
                        case DeviceTypeConstant.TYPE.TYPE_LIGHT:
                            SmartLightManager.getInstance().setCurrentSelectLight(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, LightActivity.class));
                            break;
                    }
                } else {
                    //网关设备
                    GetwayManager.getInstance().setCurrentSelectGetwayDevice(datasTop.get(position));
                    startActivity(new Intent(DevicesActivity.this, GetwayDeviceActivity.class));
                }
            }
        });
    }
    private void initEvents() {
        AppManager.getAppManager().addActivity(this);
        layout_home_page.setOnClickListener(this);
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
        imageview_add_device.setOnClickListener(this);
        layout_select_room_type.setOnClickListener(this);
        listview_devies.getRefreshableView().setSelector(android.R.color.transparent);
        listview_devies.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        // 设置上下刷新 使用 OnRefreshListener2
        listview_devies.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                listview_devies.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceManager.queryDeviceList();
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listview_devies.onRefreshComplete();
                            }
                        },3000);

                    }
                }, 500);
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
    }


    private void initViews() {
        layout_home_page = findViewById(R.id.layout_home_page);
        layout_devices = findViewById(R.id.layout_devices);
        layout_rooms = findViewById(R.id.layout_rooms);
        layout_personal_center = findViewById(R.id.layout_personal_center);
        listview_devies = findViewById(R.id.listview_devies);
        imageview_add_device = findViewById(R.id.imageview_add_device);
        layout_select_room_type = findViewById(R.id.layout_select_room_type);
        imageview_devices = findViewById(R.id.imageview_devices);
        layout_empty_view_scroll = findViewById(R.id.layout_empty_view_scroll);
        imageview_home_page = findViewById(R.id.imageview_home_page);
        imageview_rooms = findViewById(R.id.imageview_rooms);
        imageview_personal_center = findViewById(R.id.imageview_personal_center);
        textview_home = findViewById(R.id.textview_home);
        textview_device = findViewById(R.id.textview_device);
        textview_room = findViewById(R.id.textview_room);
        textview_mine = findViewById(R.id.textview_mine);
        textview_room_name = findViewById(R.id.textview_room_name);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_home_page:
                startActivity(new Intent(this, SmartHomeMainActivity.class));
                break;
            case R.id.layout_select_room_type:
                roomTypeDialog.setRoomTypeItemClickListener(new DeviceAtRoomDialog.onItemClickListener() {
                    @Override
                    public void onItemClicked(int position) {
                        datasTop.clear();
                        datasBottom.clear();
                        if (mRooms.get(position).equals("全部")) {
                            datasTop.addAll(GetwayManager.getInstance().getAllGetwayDevice());
                            datasBottom.addAll(DataSupport.findAll(SmartDev.class, true));
                            mDeviceAdapter.setTopList(datasTop);
                            mDeviceAdapter.setBottomList(datasBottom);
                        } else {
                            Room room = mRoomManager.findRoom(mRooms.get(position), true);
                            //使用数据库中的数据
                            datasTop.addAll(room.getmGetwayDevices());
                            datasBottom.addAll(room.getmDevices());
                            mDeviceAdapter.setTopList(datasTop);
                            mDeviceAdapter.setBottomList(datasBottom);
                        }
                        mDeviceAdapter.notifyDataSetChanged();
                        textview_room_name.setText(mRooms.get(position));
                        roomTypeDialog.dismiss();
                    }
                });
                roomTypeDialog.show();
                break;
            case R.id.layout_rooms:
                startActivity(new Intent(this, RoomActivity.class));
                break;
            case R.id.layout_personal_center:
                startActivity(new Intent(this, PersonalCenterActivity.class));
                break;
            case R.id.imageview_add_device:
                startActivity(new Intent(this, AddDeviceActivity.class));
                break;
        }
    }



    @Override
    public void responseQueryResult(String result) {
        if (result.contains("DevList")) {
            Message msg = Message.obtain();
            msg.what = MSG_GET_DEVS;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void responseBindDeviceResult(String result) {

    }

    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {

    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }

    @Override
    public void responseBindDeviceHttpResult() {

    }

    @Override
    public void responseQueryHttpResult(List<Deviceprops> devices) {

    }

    private static final int MSG_GET_DEVS = 0x01;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            switch (msg.what) {
                case MSG_GET_DEVS:
                    datasTop.clear();
                    datasBottom.clear();
                    datasTop.addAll(GetwayManager.getInstance().getAllGetwayDevice());
                    datasBottom.addAll(DataSupport.findAll(SmartDev.class, true));
                    List<Device> tempDevice = new ArrayList<>();
                    List<SmartDev> tempSmartDevice = new ArrayList<>();
                    Gson gson = new Gson();
                    DeviceList aDeviceList = gson.fromJson(str, DeviceList.class);
                    if (aDeviceList.getSmartDev() != null && aDeviceList.getSmartDev().size() > 0) {
                        tempSmartDevice.addAll(aDeviceList.getSmartDev());
                    }
                    //存储设备列表
                    if (aDeviceList.getDevice() != null && aDeviceList.getDevice().size() > 0) {
                        tempDevice.addAll(aDeviceList.getDevice());
                    }
                    for (int i = 0; i < tempDevice.size(); i++) {
                        for (int j = 0; j < datasTop.size(); j++) {
                            if (datasTop.get(j).getUid().equals(tempDevice.get(i).getUid())) {
                                datasTop.get(j).setStatus(tempDevice.get(i).getStatus());
                            }
                        }
                    }
                    for (int i = 0; i < tempSmartDevice.size(); i++) {
                        for (int j = 0; j < datasBottom.size(); j++) {
                            if (datasBottom.get(j).getUid().equals(tempSmartDevice.get(i).getUid())) {
                                datasBottom.get(j).setStatus(tempSmartDevice.get(i).getStatus());
                            }
                        }
                    }
                    listview_devies.onRefreshComplete();
                    mDeviceAdapter.setTopList(datasTop);
                    mDeviceAdapter.setBottomList(datasBottom);
                    mDeviceAdapter.notifyDataSetChanged();
                    Log.i(TAG, "设备列表=" + str);
                    break;

            }
        }
    };

}
