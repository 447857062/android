package com.deplink.homegenius.activity.homepage;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.ExperienceCenterDevice;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.Protocol.json.device.router.Router;
import com.deplink.homegenius.Protocol.json.http.weather.HeWeather6;
import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.device.getway.GetwayDeviceActivity;
import com.deplink.homegenius.activity.device.smartlock.SmartLockActivity;
import com.deplink.homegenius.activity.homepage.adapter.ExperienceCenterListAdapter;
import com.deplink.homegenius.activity.homepage.adapter.HomepageGridViewAdapter;
import com.deplink.homegenius.activity.homepage.adapter.HomepageRoomShowTypeChangedViewAdapter;
import com.deplink.homegenius.activity.personal.PersonalCenterActivity;
import com.deplink.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.activity.room.DeviceNumberActivity;
import com.deplink.homegenius.activity.room.RoomActivity;
import com.deplink.homegenius.application.AppManager;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectService;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.getway.GetwayManager;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlListener;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlManager;
import com.deplink.homegenius.manager.room.RoomListener;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.scrollview.NonScrollableListView;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.bean.User;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.Deviceprops;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.deplink.sdk.android.sdk.rest.RestfulToolsWeather;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private FrameLayout textview_change_show_type;
    private TextView textview_home;
    private TextView textview_device;
    private TextView textview_room;
    private TextView textview_mine;
    private TextView textview_pm25;
    private HomepageRoomShowTypeChangedViewAdapter mRoomSelectTypeChangedAdapter;
    private SDKManager manager;
    private EventCallback ec;
    private TextView textview_city;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private TextView textview_tempature;
    private RoomManager mRoomManager;
    private HorizontalScrollView layout_roomselect_normal;
    private NonScrollableListView layout_roomselect_changed_ype;
    private ScrollView scrollview_root;
    private boolean isLogin;
    private String province;
    private String city;
    private String district;
    private DeviceListener mDeviceListener;
    private RemoteControlListener mRemoteControlListener;
    private DeleteDeviceDialog connectLostDialog;
    private DeviceManager mDeviceManager;
    private RemoteControlManager mRemoteControlManager;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
        }
    };
    private static final int MSG_GET_ROOM = 100;
    private static final int MSG_QUERY_WEATHER_PM25 = 101;
    private static final int MSG_SHOW_PM25_TEXT = 102;
    private static final int MSG_SHOW_WEATHER_TEXT = 103;
    private static final int MSG_INIT_LOCATIONSERVICE = 104;
    private static final int MSG_GET_DEVS_HTTPS = 105;
    private static final int MSG_GET_VIRTUAL_DEVS_HTTPS = 106;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_GET_ROOM:
                    mDeviceManager.queryDeviceListHttp();
                    break;
                case MSG_QUERY_WEATHER_PM25:
                    if (city.substring(city.length() - 1, city.length()).equals("市")) {
                        city = city.substring(0, city.length() - 1);
                    }
                    if (province.substring(province.length() - 1, province.length()).equals("省")) {
                        province = province.substring(0, province.length() - 1);
                    }
                    textview_city.setText(city + "/" + district);
                    initWaetherData();
                    sendRequestWithHttpClient(city);
                    break;
                case MSG_SHOW_PM25_TEXT:
                    textview_pm25.setText("" + msg.obj);
                    break;
                case MSG_SHOW_WEATHER_TEXT:
                    String temp = (String) msg.obj;
                    try {
                        temp = temp.split("℃")[0];
                        textview_tempature.setText(temp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_INIT_LOCATIONSERVICE:
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            mLocationClient = new LocationClient(getApplicationContext());
                            //声明LocationClient类
                            mLocationClient.registerLocationListener(myListener);
                            //注册监听函数
                            LocationClientOption option = new LocationClientOption();
                            option.setIsNeedAddress(true);
                            //可选，是否需要地址信息，默认为不需要，即参数为false
                            //如果开发者需要获得当前点的地址信息，此处必须为true
                            mLocationClient.setLocOption(option);
                            //mLocationClient为第二步初始化过的LocationClient对象
                            //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
                            //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
                            mLocationClient.start();
                        }
                    }.start();
                    break;
                case MSG_GET_DEVS_HTTPS:
                    mRemoteControlManager.queryVirtualDeviceList();
                    break;
                case MSG_GET_VIRTUAL_DEVS_HTTPS:
                    mRoomList.clear();
                    mRoomList.addAll(mRoomManager.queryRooms());
                    setRoomNormalLayout();
                    Log.i(TAG, "mRoomList.size=" + mRoomList.size());
                    mAdapter.notifyDataSetChanged();
                    mRoomSelectTypeChangedAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };



    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            // String addr = location.getAddrStr();    //获取详细地址信息
            // String country = location.getCountry();    //获取国家
            province = location.getProvince();    //获取省份
            city = location.getCity();    //获取城市
            district = location.getDistrict();    //获取区县
            // String street = location.getStreet();    //获取街道信息
            if (city != null && province != null) {
                Message msg = Message.obtain();
                msg.what = MSG_QUERY_WEATHER_PM25;
                mHandler.sendMessage(msg);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home_main);

        initViews();
        initDatas();
        initEvents();
    }
    /**
     * 获取pm2.5
     *
     * @param city
     */
    private void sendRequestWithHttpClient(final String city) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RestfulToolsWeather.getSingleton().getWeatherPm25(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.code() == 200) {
                            JsonObject jsonObjectGson = response.body();
                            Gson gson = new Gson();
                            Log.i(TAG,"weatherObject="+jsonObjectGson.toString());
                            HeWeather6 weatherObject = gson.fromJson(jsonObjectGson.toString(), HeWeather6.class);
                            Message message = new Message();
                            message.what = MSG_SHOW_PM25_TEXT;
                            try {
                                //{"HeWeather6":[{"status":"no more requests"}]}
                                if(!weatherObject.getInfoList().get(0).getStatus().equalsIgnoreCase("no more requests")){
                                    message.obj = weatherObject.getInfoList().get(0).getAir_now_city().getPm25();
                                    mHandler.sendMessage(message);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                }, city);

            }
        }).start();
    }

    public void initWaetherData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RestfulToolsWeather.getSingleton().getWeatherInfo(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.code() == 200) {
                            JsonObject jsonObjectGson = response.body();
                            Gson gson = new Gson();
                            HeWeather6 weatherObject = gson.fromJson(jsonObjectGson.toString(), HeWeather6.class);
                            Message message = new Message();
                            message.what = MSG_SHOW_WEATHER_TEXT;
                            if(! weatherObject.getInfoList().get(0).getStatus().equalsIgnoreCase("no more requests")){
                                message.obj = weatherObject.getInfoList().get(0).getNow().getTmp();
                                mHandler.sendMessage(message);
                            }

                        }
                    }
                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                }, city);

            }
        }).start();
    }
    private RoomListener mRoomListener;
    @Override
    protected void onResume() {
        super.onResume();
        isLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
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
        mRoomList.addAll(mRoomManager.queryRooms());
        mAdapter.notifyDataSetChanged();
        setRoomNormalLayout();
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
        layout_roomselect_normal.smoothScrollTo(0, 0);
        if (isLogin) {
            mRoomManager.updateRooms();
        }
        mDeviceManager.addDeviceListener(mDeviceListener);
        mRemoteControlManager.addRemoteControlListener(mRemoteControlListener);
        mRoomManager.addRoomListener(mRoomListener);
    }

    private void setRoomNormalLayout() {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.removeEventCallback(ec);
        manager.onDestroy();
    }



    private void initDatas() {
        Intent bindIntent = new Intent(SmartHomeMainActivity.this, LocalConnectService.class);
        startService(bindIntent);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        initManager();
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
        connectLostDialog = new DeleteDeviceDialog(SmartHomeMainActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(SmartHomeMainActivity.this, LoginActivity.class));
            }
        });
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
                switch (action) {
                    case LOGIN:
                        manager.connectMQTT(SmartHomeMainActivity.this);
                        Log.i(TAG, "login mqtt success uuid=" + manager.getUserInfo().getUuid());
                        Perfence.setPerfence(AppConstant.PERFENCE_BIND_APP_UUID, manager.getUserInfo().getUuid());
                        User user = manager.getUserInfo();
                        Perfence.setPerfence(Perfence.USER_PASSWORD, user.getPassword());
                        Perfence.setPerfence(Perfence.PERFENCE_PHONE, user.getName());
                        Perfence.setPerfence(AppConstant.USER_LOGIN, true);
                        mRoomManager.updateRooms();
                        Perfence.setContext(getApplicationContext());
                        break;
                    case CONNECTED:
                        break;
                }
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
                connectLostDialog.setContentText("当前账号已在其它设备上登录,是否重新登录");
            }
        };
        Perfence.setContext(getApplicationContext());
        String phoneNumber = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        String password = Perfence.getPerfence(Perfence.USER_PASSWORD);
        Log.i(TAG, "phoneNumber=" + phoneNumber + "password=" + password);
        if (!password.equals("")) {
            Perfence.setPerfence(AppConstant.USER_LOGIN, false);
            manager.login(phoneNumber, password);
        }
        initListener();
    }

    private void initListener() {
        mDeviceListener = new DeviceListener() {
            @Override
            public void responseQueryHttpResult(List<Deviceprops> devices) {
                super.responseQueryHttpResult(devices);
                //保存设备列表
                List<SmartDev> dbSmartDev = mDeviceManager.findAllSmartDevice();
                for (int i = 0; i < devices.size(); i++) {
                    boolean addToDb = true;
                    if (devices.get(i).getDevice_type().equalsIgnoreCase("LKSGW")
                            ) {
                        addToDb = false;
                    } else {
                        for (int j = 0; j < dbSmartDev.size(); j++) {
                            if (dbSmartDev.get(j).getUid().equals(devices.get(i).getUid())) {
                                addToDb = false;
                            }
                        }
                    }
                    if (addToDb) {
                        Log.i(TAG, "http查询到智能设备,保存下来:");
                        saveSmartDeviceToSqlite(devices, i);
                    }
                }
                List<GatwayDevice> dbGetwayDev = GetwayManager.getInstance().getAllGetwayDevice();
                for (int i = 0; i < devices.size(); i++) {
                    boolean addToDb = true;
                    if (devices.get(i).getDevice_type().equalsIgnoreCase("LKSGW")) {
                        for (int j = 0; j < dbGetwayDev.size(); j++) {
                            if (dbGetwayDev.get(j).getUid().equals(devices.get(i).getUid())) {
                                addToDb = false;
                            }
                        }
                    } else {
                        addToDb = false;
                    }
                    if (addToDb) {
                        saveGetwayDeviceToSqlite(devices, i);
                    }
                }
                mHandler.sendEmptyMessage(MSG_GET_DEVS_HTTPS);
            }
        };
        mRemoteControlListener = new RemoteControlListener() {
            @Override
            public void responseQueryVirtualDevices(List<DeviceOperationResponse> result) {
                super.responseQueryVirtualDevices(result);
                //保存虚拟设备
                for (int i = 0; i < result.size(); i++) {
                    saveVirtualDeviceToSqlite(result, i);
                }
                mHandler.sendEmptyMessage(MSG_GET_VIRTUAL_DEVS_HTTPS);
            }
        };
        mRoomListener=new RoomListener() {
            @Override
            public void responseQueryResultHttps(List<Room> result) {
                super.responseQueryResultHttps(result);
                Log.i(TAG, "主页获取到房间列表=" + result);
                Message msg = Message.obtain();
                msg.what = MSG_GET_ROOM;
                mHandler.sendMessage(msg);
            }
        };
    }

    /**
     * 保存网关设备到本地数据库
     *
     * @param devices
     * @param i
     */
    private void saveGetwayDeviceToSqlite(List<Deviceprops> devices, int i) {
        GatwayDevice dev = new GatwayDevice();
        String deviceType = devices.get(i).getDevice_type();
        dev.setType(deviceType);
        String deviceName = devices.get(i).getDevice_name();
        if (deviceType.equalsIgnoreCase("LKSWG") || deviceType.equalsIgnoreCase("LKSGW")) {
            if (deviceName == null || deviceName.equals("")) {
                dev.setName("中继器");
            } else {
                deviceName = deviceName.replace("/路由器", "");
                dev.setName(deviceName);
            }
            deviceType = DeviceTypeConstant.TYPE.TYPE_SMART_GETWAY;
            dev.setType(deviceType);
        }
        dev.setUid(devices.get(i).getUid());
        dev.setOrg(devices.get(i).getOrg_code());
        dev.setVer(devices.get(i).getVersion());
        dev.setTopic("device/" + devices.get(i).getUid() + "/sub");
        List<Room> rooms = new ArrayList<>();
        Room room = DataSupport.where("Uid=?", devices.get(i).getRoom_uid()).findFirst(Room.class);
        if(room!=null){
            Log.i(TAG, "添加中继器房间是:" + room.toString());
        }
        rooms.add(room);
        dev.setRoomList(rooms);
        boolean success = dev.save();
        Log.i(TAG, "保存设备:" + success + "deviceName=" + deviceName);
    }

    private void saveSmartDeviceToSqlite(List<Deviceprops> devices, int i) {
        SmartDev dev = new SmartDev();
        String deviceType = devices.get(i).getDevice_type();
        dev.setType(deviceType);
        String deviceName = devices.get(i).getDevice_name();
        if (deviceType.equalsIgnoreCase("SMART_LOCK")) {
            deviceType = DeviceTypeConstant.TYPE.TYPE_LOCK;
            dev.setType(deviceType);
            dev.setName(deviceName);
        } else if (deviceType.equalsIgnoreCase("IRMOTE_V2")) {
            deviceType = DeviceTypeConstant.TYPE.TYPE_REMOTECONTROL;
            dev.setType(deviceType);
            dev.setName(deviceName);
        } else if (deviceType.equalsIgnoreCase("SmartWallSwitch1")) {
            deviceType = DeviceTypeConstant.TYPE.TYPE_SWITCH;
            dev.setType(deviceType);
            dev.setName(deviceName);
            dev.setSubType(DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_ONEWAY);
        } else if (deviceType.equalsIgnoreCase("SmartWallSwitch2")) {
            deviceType = DeviceTypeConstant.TYPE.TYPE_SWITCH;
            dev.setType(deviceType);
            dev.setName(deviceName);
            dev.setSubType(DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_TWOWAY);
        } else if (deviceType.equalsIgnoreCase("SmartWallSwitch3")) {
            deviceType = DeviceTypeConstant.TYPE.TYPE_SWITCH;
            dev.setType(deviceType);
            dev.setName(deviceName);
            dev.setSubType(DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_THREEWAY);
        } else if (deviceType.equalsIgnoreCase("SmartWallSwitch4")) {
            deviceType = DeviceTypeConstant.TYPE.TYPE_SWITCH;
            dev.setType(deviceType);
            dev.setName(deviceName);
            dev.setSubType(DeviceTypeConstant.TYPE_SWITCH_SUBTYPE.SUB_TYPE_SWITCH_FOURWAY);
        } else if (deviceType.equalsIgnoreCase("YWLIGHTCONTROL")) {
            deviceType = DeviceTypeConstant.TYPE.TYPE_LIGHT;
            dev.setType(deviceType);
            dev.setName(deviceName);
        } else if (deviceType.equalsIgnoreCase("SMART_BELL")) {
            deviceType = DeviceTypeConstant.TYPE.TYPE_MENLING;
            dev.setType(deviceType);
            dev.setStatus("在线");
            dev.setName(deviceName);
        } else if (deviceType.equalsIgnoreCase("LKRT")) {
            deviceType = DeviceTypeConstant.TYPE.TYPE_ROUTER;
            dev.setType(deviceType);
            Router router = new Router();
            Log.i(TAG, "获取绑定的设备" + manager.getDeviceList().size());
            if (deviceName == null || deviceName.equals("")) {
                dev.setName("路由器");
            } else {
                dev.setName(deviceName);
            }
            router.setSign_seed(devices.get(i).getSign_seed());
            router.setSignature(devices.get(i).getSignature());
            router.setChannels(devices.get(i).getChannels().getSecondary().getSub());
            router.setReceveChannels(devices.get(i).getChannels().getSecondary().getPub());
            router.setSmartDev(dev);
            router.save();
            dev.setRouter(router);
        }
        GatwayDevice addGatwayDevice = null;
        String gw_uid = devices.get(i).getGw_uid();
        if (gw_uid != null) {
            dev.setGetwayDeviceUid(gw_uid);
            addGatwayDevice = DataSupport.where("uid=?", gw_uid).findFirst(GatwayDevice.class);
        }
        Log.i(TAG, "gw_uid=" + gw_uid + "addGatwayDevice" + (addGatwayDevice != null));
        if (addGatwayDevice != null) {
            dev.setGetwayDevice(addGatwayDevice);
        }

        dev.setUid(devices.get(i).getUid());
        dev.setOrg(devices.get(i).getOrg_code());
        dev.setVer(devices.get(i).getVersion());
        dev.setMac(devices.get(i).getMac().toLowerCase());
        List<Room> rooms = new ArrayList<>();
        Room room = DataSupport.where("Uid=?", devices.get(i).getRoom_uid()).findFirst(Room.class);
        if (room != null) {
            rooms.add(room);
            dev.setRooms(rooms);
        } else {
            dev.setRooms(mRoomManager.queryRooms());
        }
        boolean success = dev.save();
    }

    private void saveVirtualDeviceToSqlite(List<DeviceOperationResponse> devices, int i) {
        SmartDev dev = DataSupport.where("Uid=?", devices.get(i).getUid()).findFirst(SmartDev.class);
        if (dev == null) {
            dev = new SmartDev();
        }
        String deviceType = devices.get(i).getDevice_type();
        switch (deviceType) {
            case "IREMOTE_V2_AC":
                deviceType = DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL;
                break;
            case "IREMOTE_V2_TV":
                deviceType = DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL;
                break;
            case "IREMOTE_V2_STB":
                deviceType = DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL;
                break;
        }
        dev.setType(deviceType);
        String deviceName = devices.get(i).getDevice_name();
        dev.setName(deviceName);
        dev.setUid(devices.get(i).getUid());
        SmartDev realRc = DataSupport.where("Uid=?", devices.get(i).getIrmote_uid()).findFirst(SmartDev.class, true);
        if (realRc != null && realRc.getRooms() != null) {
            dev.setRooms(realRc.getRooms());
        }
        dev.setRemotecontrolUid(devices.get(i).getIrmote_uid());
        dev.setMac(devices.get(i).getIrmote_mac());
        String key_codes = devices.get(i).getKey_codes();
        if (key_codes != null) {
            dev.setKey_codes(key_codes);
        }
        dev.save();
    }

    private void initManager() {
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager(this);
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this);
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this);
    }

    private AdapterView.OnItemClickListener mExperienceCenterListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DeviceManager.getInstance().setStartFromExperience(true);
            DeviceManager.getInstance().setStartFromHomePage(true);
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
        Message msg = Message.obtain();
        msg.what = MSG_INIT_LOCATIONSERVICE;
        mHandler.sendMessage(msg);
    }

    private void initViews() {
        layout_home_page = findViewById(R.id.layout_home_page);
        layout_devices = findViewById(R.id.layout_devices);
        layout_rooms = findViewById(R.id.layout_rooms);
        layout_personal_center = findViewById(R.id.layout_personal_center);
        imageview_setting = findViewById(R.id.imageview_setting);
        roomGridView = findViewById(R.id.grid);
        listview_experience_center = findViewById(R.id.listview_experience_center);
        imageview_devices = findViewById(R.id.imageview_devices);
        imageview_home_page = findViewById(R.id.imageview_home_page);
        imageview_rooms = findViewById(R.id.imageview_rooms);
        imageview_personal_center = findViewById(R.id.imageview_personal_center);
        layout_experience_center_top = findViewById(R.id.layout_experience_center_top);
        textview_change_show_type = findViewById(R.id.textview_change_show_type);
        textview_home = findViewById(R.id.textview_home);
        textview_device = findViewById(R.id.textview_device);
        textview_room = findViewById(R.id.textview_room);
        textview_mine = findViewById(R.id.textview_mine);
        textview_city = findViewById(R.id.textview_city);
        textview_tempature = findViewById(R.id.textview_tempature);
        textview_pm25 = findViewById(R.id.textview_pm25);
        layout_roomselect_normal = findViewById(R.id.layout_roomselect_normal);
        layout_roomselect_changed_ype = findViewById(R.id.layout_roomselect_changed_ype);
        scrollview_root = findViewById(R.id.scrollview_root);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRoomManager.removeRoomListener(mRoomListener);
        mDeviceManager.removeDeviceListener(mDeviceListener);
        mRemoteControlManager.removeRemoteControlListener(mRemoteControlListener);
        manager.removeEventCallback(ec);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_home_page:

                break;
            case R.id.layout_experience_center_top:
                DeviceManager.getInstance().setExperCenterStartFromHomePage(true);
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
                    scrollview_root.smoothScrollTo(0, 0);
                } else {
                    layout_roomselect_normal.setVisibility(View.VISIBLE);
                    layout_roomselect_normal.smoothScrollTo(0, 0);
                    layout_roomselect_changed_ype.setVisibility(View.GONE);
                }
                break;

        }
    }
}
