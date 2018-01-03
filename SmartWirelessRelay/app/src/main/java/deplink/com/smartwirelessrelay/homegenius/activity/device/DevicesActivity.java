package deplink.com.smartwirelessrelay.homegenius.activity.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.DeviceListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.doorbell.DoorbeelMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.GetwayDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.RemoteControlActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl.AirRemoteControlMianActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.topBox.TvBoxMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.tv.TvMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.RouterMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch.SwitchFourActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch.SwitchOneActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch.SwitchThreeActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch.SwitchTwoActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.SmartLockActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.SmartHomeMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.PersonalCenterActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.RoomActivity;
import deplink.com.smartwirelessrelay.homegenius.application.AppManager;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceTypeConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartswitch.SmartSwitchManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.ListViewLinearLayout;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.devices.DeviceAtRoomDialog;

public class DevicesActivity extends Activity implements View.OnClickListener, DeviceListener {
    private static final String TAG = "DevicesActivity";
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;
    private ListView listview_devies;
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
    private ListViewLinearLayout layout_devices_show;
    private ImageView imageview_empty_device;
    private TextView textview_room_name;
    private ImageView imageview_refresh;
    private ImageView imageview_refresh_complement;
    private FrameLayout frame_refresh;

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
        if (datasTop.size() == 0 && datasBottom.size() == 0) {
            imageview_empty_device.setVisibility(View.VISIBLE);
            listview_devies.setVisibility(View.GONE);
        } else {
            imageview_empty_device.setVisibility(View.GONE);
            listview_devies.setVisibility(View.VISIBLE);
        }
        mDeviceAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDeviceManager.removeDeviceListener(this);
    }

    private List<String> mRooms = new ArrayList<>();

    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(DevicesActivity.this);
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(DevicesActivity.this, DevicesActivity.this);
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager();
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(DevicesActivity.this);

        roomTypeDialog = new DeviceAtRoomDialog(this, mRooms);
        mRooms.addAll(mRoomManager.getRoomNames());
        datasTop = new ArrayList<>();
        datasBottom = new ArrayList<>();
        mDeviceAdapter = new DeviceListAdapter(this, datasTop, datasBottom);
        listview_devies.setAdapter(mDeviceAdapter);
        listview_devies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (datasTop.size() < (position + 1)) {
                    //智能设备
                    String deviceType = datasBottom.get(position - datasTop.size()).getType();
                    String deviceSubType = datasBottom.get(position - datasTop.size()).getSubType();
                    Log.i(TAG, "智能设备类型=" + deviceType);
                    mDeviceManager.setCurrentSelectSmartDevice(datasBottom.get(position - datasTop.size()));
                    mDeviceManager.setStartFromExperience(false);
                    switch (deviceType) {
                        case "SMART_LOCK":
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
                        case "路由器":
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
                                case "一路开关":
                                    startActivity(new Intent(DevicesActivity.this, SwitchOneActivity.class));
                                    break;
                                case "二路开关":
                                    startActivity(new Intent(DevicesActivity.this, SwitchTwoActivity.class));
                                    break;
                                case "三路开关":
                                    startActivity(new Intent(DevicesActivity.this, SwitchThreeActivity.class));
                                    break;
                                case "四路开关":
                                    startActivity(new Intent(DevicesActivity.this, SwitchFourActivity.class));
                                    break;
                            }
                            break;
                        case "SmartWallSwitch4":
                            SmartSwitchManager.getInstance().setCurrentSelectSmartDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, SwitchFourActivity.class));
                            break;
                        case "SmartWallSwitch3":
                            SmartSwitchManager.getInstance().setCurrentSelectSmartDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, SwitchThreeActivity.class));
                            break;
                        case "SmartWallSwitch2":
                            SmartSwitchManager.getInstance().setCurrentSelectSmartDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, SwitchTwoActivity.class));
                            break;
                        case "SmartWallSwitch1":
                            SmartSwitchManager.getInstance().setCurrentSelectSmartDevice(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, SwitchOneActivity.class));
                            break;
                        case "智能门铃":
                            startActivity(new Intent(DevicesActivity.this, DoorbeelMainActivity.class));
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

    // 用GestureDetectorCompat替换GestureDetector,GestureDetectorCompat兼容的版本较广
    private GestureDetectorCompat mDetector;
    public class MytGestureListener extends GestureDetector.SimpleOnGestureListener {
        // 滑动时触发
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i(TAG, "layout_devices_show onScroll");
            RefreshDevicesBackground();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

    }

    private void RefreshDevicesBackground() {
        frame_refresh.setVisibility(View.VISIBLE);
        imageview_refresh.setVisibility(View.VISIBLE);
        imageview_refresh_complement.setVisibility(View.GONE);
        startRefreshAnim();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
               mHandler.sendEmptyMessageDelayed(MSG_HIDE_REFRESH,500);
            }
        }, 1500);
        mDeviceManager.queryDeviceList();
    }
    private void startRefreshAnim() {
        Animation circle_anim = AnimationUtils.loadAnimation(this, R.anim.anim_round_refresh);
        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
        circle_anim.setInterpolator(interpolator);
        imageview_refresh.startAnimation(circle_anim);  //开始动画
    }

    private void initEvents() {
        AppManager.getAppManager().addActivity(this);
        layout_home_page.setOnClickListener(this);
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
        imageview_add_device.setOnClickListener(this);
        layout_select_room_type.setOnClickListener(this);
        mDetector = new GestureDetectorCompat(DevicesActivity.this,
                new MytGestureListener());
        listview_devies.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        });
        layout_devices_show.setmOnRefreshListener(new ListViewLinearLayout.onRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshDevicesBackground();
            }
        });
        layout_devices_show.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(TAG, "layout_devices_show ontouch");
                mDetector.onTouchEvent(event);
                return true;
            }
        });
    }


    private void initViews() {
        layout_home_page = (LinearLayout) findViewById(R.id.layout_home_page);
        layout_devices = (LinearLayout) findViewById(R.id.layout_devices);
        layout_rooms = (LinearLayout) findViewById(R.id.layout_rooms);
        layout_personal_center = (LinearLayout) findViewById(R.id.layout_personal_center);
        listview_devies = (ListView) findViewById(R.id.listview_devies);
        imageview_add_device = (ImageView) findViewById(R.id.imageview_add_device);
        layout_select_room_type = (LinearLayout) findViewById(R.id.layout_select_room_type);
        imageview_devices = (ImageView) findViewById(R.id.imageview_devices);
        imageview_empty_device = (ImageView) findViewById(R.id.imageview_empty_device);
        imageview_home_page = (ImageView) findViewById(R.id.imageview_home_page);
        imageview_rooms = (ImageView) findViewById(R.id.imageview_rooms);
        imageview_personal_center = (ImageView) findViewById(R.id.imageview_personal_center);
        textview_home = (TextView) findViewById(R.id.textview_home);
        textview_device = (TextView) findViewById(R.id.textview_device);
        textview_room = (TextView) findViewById(R.id.textview_room);
        textview_mine = (TextView) findViewById(R.id.textview_mine);
        layout_devices_show = (ListViewLinearLayout) findViewById(R.id.layout_devices_show);
        textview_room_name = (TextView) findViewById(R.id.textview_room_name);
        imageview_refresh = (ImageView) findViewById(R.id.imageview_refresh);
        imageview_refresh_complement = (ImageView) findViewById(R.id.imageview_refresh_complement);
        frame_refresh = (FrameLayout) findViewById(R.id.frame_refresh);
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
                        if (datasTop.size() == 0 && datasBottom.size() == 0) {
                            imageview_empty_device.setVisibility(View.VISIBLE);
                            listview_devies.setVisibility(View.GONE);
                        } else {
                            imageview_empty_device.setVisibility(View.GONE);
                            listview_devies.setVisibility(View.VISIBLE);
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
    private static final int MSG_GET_DEVS = 0x01;
    private static final int MSG_SHOW_REFRESH_COMPLEMENT = 0x02;
    private static final int MSG_HIDE_REFRESH_COMPLEMENT = 0x03;
    private static final int MSG_HIDE_REFRESH = 0x04;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            switch (msg.what) {
                case MSG_SHOW_REFRESH_COMPLEMENT:
                    imageview_refresh_complement.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(MSG_HIDE_REFRESH_COMPLEMENT,500);
                    break;
                case MSG_HIDE_REFRESH:
                    imageview_refresh.setVisibility(View.GONE);
                    mHandler.sendEmptyMessageDelayed(MSG_SHOW_REFRESH_COMPLEMENT,1000);
                    break;
                case MSG_HIDE_REFRESH_COMPLEMENT:
                    frame_refresh.setVisibility(View.GONE);
                    break;
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

                    mDeviceAdapter.setTopList(datasTop);
                    mDeviceAdapter.setBottomList(datasBottom);
                    mDeviceAdapter.notifyDataSetChanged();
                    Log.i(TAG, "设备列表=" + str);
                    break;

            }
        }
    };

}
