package deplink.com.smartwirelessrelay.homegenius.activity.device;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.DeviceListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.doorbell.DoorbeelMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.GetwayDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.RemoteControlActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl.AirRemoteControlMianActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.topBox.IptvMainActivity;
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
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
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
        datasTop.addAll(GetwayManager.getInstance().queryAllGetwayDevice());
        datasBottom.addAll(DataSupport.findAll(SmartDev.class));
        //  mDeviceAdapter.notifyDataSetChanged();
        mDeviceAdapter = new DeviceListAdapter(this, datasTop, datasBottom);
        listview_devies.setAdapter(mDeviceAdapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDeviceManager.removeDeviceListener(this);
    }

    private List<String> mRoomTypes = new ArrayList<>();

    private void initDatas() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSmartLockManager = SmartLockManager.getInstance();
                mSmartLockManager.InitSmartLockManager(DevicesActivity.this);
                mDeviceManager = DeviceManager.getInstance();
                mDeviceManager.InitDeviceManager(DevicesActivity.this, DevicesActivity.this);
                mRoomManager = RoomManager.getInstance();
                mRoomManager.initRoomManager();

            }
        });
        roomTypeDialog = new DeviceAtRoomDialog(this, mRoomTypes);
        mRoomTypes.addAll(mRoomManager.getRoomTypes());
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
                    switch (deviceType) {
                        case "SMART_LOCK":
                            //设置当前选中的门锁设备
                            mSmartLockManager.setCurrentSelectLock(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, SmartLockActivity.class));
                            break;
                        case "IRMOTE_V2":
                            startActivity(new Intent(DevicesActivity.this, RemoteControlActivity.class));
                            break;
                        case "智能空调":
                            startActivity(new Intent(DevicesActivity.this, AirRemoteControlMianActivity.class));
                            break;
                        case "路由器":
                            RouterManager.getInstance().setCurrentSelectedRouter(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DevicesActivity.this, RouterMainActivity.class));
                            break;
                        case "智能电视":
                            startActivity(new Intent(DevicesActivity.this, TvMainActivity.class));
                            break;
                        case "智能机顶盒遥控":
                            startActivity(new Intent(DevicesActivity.this, IptvMainActivity.class));
                            break;
                        case AppConstant.DEVICES.TYPE_SWITCH:
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

    private void initEvents() {
        AppManager.getAppManager().addActivity(this);
        layout_home_page.setOnClickListener(this);
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
        imageview_add_device.setOnClickListener(this);
        layout_select_room_type.setOnClickListener(this);
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
        imageview_home_page = (ImageView) findViewById(R.id.imageview_home_page);
        imageview_rooms = (ImageView) findViewById(R.id.imageview_rooms);
        imageview_personal_center = (ImageView) findViewById(R.id.imageview_personal_center);
        textview_home = (TextView) findViewById(R.id.textview_home);
        textview_device = (TextView) findViewById(R.id.textview_device);
        textview_room = (TextView) findViewById(R.id.textview_room);
        textview_mine = (TextView) findViewById(R.id.textview_mine);
        //TODO 初始化设备列表

    }

    private DeviceAtRoomDialog roomTypeDialog;

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
                        if (mRoomTypes.get(position).equals("全部")) {
                            datasTop.addAll(GetwayManager.getInstance().queryAllGetwayDevice());
                            datasBottom.addAll(DataSupport.findAll(SmartDev.class));

                        } else {
                            Room room = mRoomManager.findRoomByType(mRoomTypes.get(position), true);
                            //使用数据库中的数据
                            datasTop.addAll(room.getmGetwayDevices());
                            datasBottom.addAll(room.getmDevices());

                        }
                        mDeviceAdapter.notifyDataSetChanged();
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

    private static final int MSG_GET_DEVS = 0x01;

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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            switch (msg.what) {
                case MSG_GET_DEVS:
                    try {
                        new AlertDialog
                                .Builder(DevicesActivity.this)
                                .setTitle("设备")
                                .setNegativeButton("确定", null)
                                .setIcon(android.R.drawable.ic_menu_agenda)
                                .setMessage(str)
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }


        }
    };

}
