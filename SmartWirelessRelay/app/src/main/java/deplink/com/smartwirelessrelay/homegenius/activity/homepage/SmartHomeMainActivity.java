package deplink.com.smartwirelessrelay.homegenius.activity.homepage;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.bean.User;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.ExperienceCenterDevice;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.GetwayDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.SmartLockActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.adapter.ExperienceCenterListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.adapter.HomepageGridViewAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.adapter.HomepageRoomShowTypeChangedViewAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.PersonalCenterActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.DeviceNumberActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.RoomActivity;
import deplink.com.smartwirelessrelay.homegenius.application.AppManager;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectService;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.NonScrollableListView;

/**
 * 智能家居主页
 */
public class SmartHomeMainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "SmartHomeMainActivity";
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;
    private ImageView imageview_devices;
    private ImageView imageview_home_page;
    private ImageView imageview_rooms;
    private ImageView imageview_personal_center;
    private ImageView imageview_setting;

    private List<Room> mRoomList = new ArrayList<>();
    private HomepageGridViewAdapter mAdapter;
    private GridView roomGridView;
    private ListView listview_experience_center;
    private ExperienceCenterListAdapter mExperienceCenterListAdapter;
    private List<ExperienceCenterDevice> mExperienceCenterDeviceList;
    private RelativeLayout layout_experience_center_top;
    private TextView textview_change_show_type;
    private TextView textview_home;
    private TextView textview_device;
    private TextView textview_room;
    private TextView textview_mine;

    private HomepageRoomShowTypeChangedViewAdapter mRoomSelectTypeChangedAdapter;
    private SDKManager manager;
    private EventCallback ec;

    private TextView textview_city;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
           // String addr = location.getAddrStr();    //获取详细地址信息
           // String country = location.getCountry();    //获取国家
           // String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            //String district = location.getDistrict();    //获取区县
           // String street = location.getStreet();    //获取街道信息
            Log.i(TAG, "city=" + city);
            textview_city.setText(city);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home_main);
        initViews();
        initDatas();
        initEvents();
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

       // option.setIsNeedAddress(true);
//可选，是否需要地址信息，默认为不需要，即参数为false
//如果开发者需要获得当前点的地址信息，此处必须为true
        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.start();
    }

    private RoomManager mRoomManager;

    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        textview_home.setTextColor(getResources().getColor(R.color.title_blue_bg));
        textview_device.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_room.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_mine.setTextColor(getResources().getColor(android.R.color.darker_gray));

        imageview_home_page.setImageResource(R.drawable.checkthehome);
        imageview_devices.setImageResource(R.drawable.nocheckthedevice);
        imageview_rooms.setImageResource(R.drawable.nochecktheroom);
        imageview_personal_center.setImageResource(R.drawable.nocheckthemine);
        mRoomList.clear();
        mRoomList.addAll(mRoomManager.getDatabaseRooms());
        int size = mRoomList.size();
        int length = 92;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 1) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        roomGridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        roomGridView.setColumnWidth(itemWidth); // 设置列表项宽
        roomGridView.setStretchMode(GridView.NO_STRETCH);
        roomGridView.setNumColumns(size); // 设置列数量=列表集合数
        roomGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        layout_roomselect_changed_ype.setAdapter(mRoomSelectTypeChangedAdapter);
        layout_roomselect_changed_ype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRoomManager.setCurrentSelectedRoom(mRoomManager.getmRooms().get(position));
                Intent intent = new Intent(SmartHomeMainActivity.this, DeviceNumberActivity.class);
                startActivity(intent);
            }
        });
        mRoomSelectTypeChangedAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.removeEventCallback(ec);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");

        }
    };

    private void initDatas() {
        Intent bindIntent = new Intent(this, LocalConnectService.class);
        startService(bindIntent);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager();
        mAdapter = new HomepageGridViewAdapter(SmartHomeMainActivity.this, mRoomList);
        mRoomSelectTypeChangedAdapter = new HomepageRoomShowTypeChangedViewAdapter(this, mRoomList);
        mExperienceCenterDeviceList = new ArrayList<>();
        ExperienceCenterDevice oneDevice = new ExperienceCenterDevice();
        oneDevice.setDeviceName("智能门锁");
        oneDevice.setOnline(true);
        mExperienceCenterDeviceList.add(oneDevice);
        oneDevice = new ExperienceCenterDevice();
        oneDevice.setDeviceName("智能网关");
        oneDevice.setOnline(true);
        mExperienceCenterDeviceList.add(oneDevice);
        mExperienceCenterListAdapter = new ExperienceCenterListAdapter(this, mExperienceCenterDeviceList);
        listview_experience_center.setOnItemClickListener(mExperienceCenterListClickListener);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
                switch (action) {
                    case LOGIN:
                        manager.connectMQTT(SmartHomeMainActivity.this);
                        Log.i(TAG, "LOGIN success");
                        break;
                    case CONNECTED:
                        Log.i(TAG, "CONNECTED mqtt");
                        User user = manager.getUserInfo();
                        Perfence.setPerfence(Perfence.USER_PASSWORD, user.getPassword());
                        Perfence.setPerfence(Perfence.PERFENCE_PHONE, user.getName());
                        Perfence.setPerfence(AppConstant.USER_LOGIN, true);
                        break;
                }

            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {


            }

            @Override
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

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

            }
        };
        String phoneNumber = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        String password = Perfence.getPerfence(Perfence.USER_PASSWORD);
        Log.i(TAG, "phoneNumber=" + phoneNumber + "password=" + password);
        if (!password.equals("")) {
            Perfence.setPerfence(AppConstant.USER_LOGIN, false);
            manager.login(phoneNumber, password);
        }
    }

    private AdapterView.OnItemClickListener mExperienceCenterListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DeviceManager.getInstance().setStartFromExperience(true);
            switch (mExperienceCenterDeviceList.get(position).getDeviceName()) {
                case "智能门锁":
                    Intent intent = new Intent(SmartHomeMainActivity.this, SmartLockActivity.class);

                    startActivity(intent);
                    break;
                case "智能网关":
                    Intent intentGetwayDevice = new Intent(SmartHomeMainActivity.this, GetwayDeviceActivity.class);
                    startActivity(intentGetwayDevice);
                    break;
            }
        }
    };

    private void initEvents() {
        AppManager.getAppManager().addActivity(this);
        layout_home_page.setOnClickListener(this);
        layout_experience_center_top.setOnClickListener(this);
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
        imageview_setting.setOnClickListener(this);
        textview_change_show_type.setOnClickListener(this);
        roomGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mRoomManager.setCurrentSelectedRoom(mRoomManager.getmRooms().get(position));
                Intent intent = new Intent(SmartHomeMainActivity.this, DeviceNumberActivity.class);
                startActivity(intent);
            }
        });
        listview_experience_center.setAdapter(mExperienceCenterListAdapter);

    }


    private void initViews() {
        layout_home_page = (LinearLayout) findViewById(R.id.layout_home_page);
        layout_devices = (LinearLayout) findViewById(R.id.layout_devices);
        layout_rooms = (LinearLayout) findViewById(R.id.layout_rooms);
        layout_personal_center = (LinearLayout) findViewById(R.id.layout_personal_center);
        imageview_setting = (ImageView) findViewById(R.id.imageview_setting);
        roomGridView = (GridView) findViewById(R.id.grid);
        listview_experience_center = (ListView) findViewById(R.id.listview_experience_center);
        imageview_devices = (ImageView) findViewById(R.id.imageview_devices);
        imageview_home_page = (ImageView) findViewById(R.id.imageview_home_page);
        imageview_rooms = (ImageView) findViewById(R.id.imageview_rooms);
        imageview_personal_center = (ImageView) findViewById(R.id.imageview_personal_center);
        layout_experience_center_top = (RelativeLayout) findViewById(R.id.layout_experience_center_top);
        textview_change_show_type = (TextView) findViewById(R.id.textview_change_show_type);
        textview_home = (TextView) findViewById(R.id.textview_home);
        textview_device = (TextView) findViewById(R.id.textview_device);
        textview_room = (TextView) findViewById(R.id.textview_room);
        textview_mine = (TextView) findViewById(R.id.textview_mine);
        textview_city = (TextView) findViewById(R.id.textview_city);

        layout_roomselect_normal = (HorizontalScrollView) findViewById(R.id.layout_roomselect_normal);
        layout_roomselect_changed_ype = (NonScrollableListView) findViewById(R.id.layout_roomselect_changed_ype);
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    /**
     * 再按一次退出应用
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().finishAllActivity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private HorizontalScrollView layout_roomselect_normal;
    private NonScrollableListView layout_roomselect_changed_ype;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_home_page:

                break;
            case R.id.layout_experience_center_top:
                startActivity(new Intent(this, ExperienceDevicesActivity.class));
                break;
            case R.id.imageview_setting:
                startActivity(new Intent(SmartHomeMainActivity.this, HomePageSettingActivity.class));
                break;
            case R.id.layout_devices:

                startActivity(new Intent(this, DevicesActivity.class));
                break;
            case R.id.layout_rooms:
                startActivity(new Intent(this, RoomActivity.class));
                break;
            case R.id.layout_personal_center:
                startActivity(new Intent(this, PersonalCenterActivity.class));
                break;
            case R.id.textview_change_show_type:
                if (layout_roomselect_normal.getVisibility() == View.VISIBLE) {
                    layout_roomselect_normal.setVisibility(View.GONE);
                    layout_roomselect_changed_ype.setVisibility(View.VISIBLE);
                } else {
                    layout_roomselect_normal.setVisibility(View.VISIBLE);
                    layout_roomselect_changed_ype.setVisibility(View.GONE);
                }
                break;

        }
    }
}
