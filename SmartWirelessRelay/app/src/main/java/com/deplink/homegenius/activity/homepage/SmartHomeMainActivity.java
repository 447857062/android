package com.deplink.homegenius.activity.homepage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
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
import com.deplink.homegenius.Protocol.json.Pm25Info;
import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.ExperienceCenterDevice;
import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.device.getway.GetwayDeviceActivity;
import com.deplink.homegenius.activity.device.smartlock.SmartLockActivity;
import com.deplink.homegenius.activity.homepage.adapter.ExperienceCenterListAdapter;
import com.deplink.homegenius.activity.homepage.adapter.HomepageGridViewAdapter;
import com.deplink.homegenius.activity.homepage.adapter.HomepageRoomShowTypeChangedViewAdapter;
import com.deplink.homegenius.activity.personal.PersonalCenterActivity;
import com.deplink.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import com.deplink.homegenius.activity.room.DeviceNumberActivity;
import com.deplink.homegenius.activity.room.RoomActivity;
import com.deplink.homegenius.application.AppManager;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectService;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.room.RoomListener;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.util.ParseUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.scrollview.NonScrollableListView;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.bean.User;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.deplink.sdk.android.sdk.rest.RestfulToolsWeather;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 智能家居主页
 */
public class SmartHomeMainActivity extends Activity implements View.OnClickListener,RoomListener {
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
    @Override
    public void responseQueryResultHttps( List<Room> result) {
       Message msg=Message.obtain();
        msg.what=MSG_GET_ROOM;
        msg.obj=result;
        mHandler.sendMessage(msg);
    }

    @Override
    public void responseAddRoomResult(String result) {

    }

    @Override
    public void responseDeleteRoomResult() {

    }

    @Override
    public void responseUpdateRoomNameResult() {

    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            // String addr = location.getAddrStr();    //获取详细地址信息
            // String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            // String street = location.getStreet();    //获取街道信息
            Log.i(TAG, "city=" + city);
            Log.i(TAG, "province=" + province);
            if (city != null && province != null) {
                Log.i(TAG, "city.substring(city.length()-1,city.length())=" + city.substring(city.length() - 1, city.length()));
                if (city.substring(city.length() - 1, city.length()).equals("市")) {
                    city = city.substring(0, city.length() - 1);
                }
                if (province.substring(province.length() - 1, province.length()).equals("省")) {
                    province = province.substring(0, province.length() - 1);
                }
                Log.i(TAG, "city=" + city);
                textview_city.setText(city + "/" + district);
                try {
                    cityCode = ParseUtil.getCityCodeFromCityName(SmartHomeMainActivity.this,province, city);
                    Log.i(TAG, "cityCode=" + cityCode);
                    initWaetherData();
                    sendRequestWithHttpClient(city);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private static final int MSG_GET_ROOM=100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_GET_ROOM:
                    List<Room>result= (List<Room>) msg.obj;
                    mRoomList.clear();
                    mRoomList.addAll(result);
                    setRoomNormalLayout();
                    Log.i(TAG,"mRoomList.size="+mRoomList.size());
                    mAdapter.notifyDataSetChanged();
                    mRoomSelectTypeChangedAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home_main);
        initViews();
        initDatas();
        initEvents();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    private void sendRequestWithHttpClient(final String city) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection;
                try {
                    URL url = new URL("http://www.pm25.in/api/querys/pm2_5.json?city=" + city +
                            "&token=5j1znBVAsnSf5xQyNQyq&stations=no");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    final StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.i("TAG", response.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            parseJSONObjectOrJSONArray(response.toString());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //解析JSON数据
    private void parseJSONObjectOrJSONArray(String jsonData) {
        if (!jsonData.contains("error")) {
            ArrayList<Pm25Info> pm25lists = ParseUtil.jsonToArrayList(jsonData, Pm25Info.class);
            textview_pm25.setText("" + pm25lists.get(0).getPm2_5());
        }
    }
    String cityCode = null;
    public void initWaetherData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "cityCode=" + cityCode);
                RestfulToolsWeather.getSingleton().getWeatherInfo(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.code() == 200) {
                            JsonObject jsonObjectGson = response.body();
                            Log.i(TAG, "cityCode=" + jsonObjectGson.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(jsonObjectGson.toString());
                                JSONObject weatherObject = jsonObject
                                        .getJSONObject("weatherinfo");
                                Message message = new Message();
                                message.obj = weatherObject;
                                handler.sendMessage(message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                }, cityCode);

            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject object = (JSONObject) msg.obj;
            try {
                String tempture = object.getString("temp1");
                tempture = tempture.split("℃")[0];
                Log.i(TAG, "tempture=" + tempture);
                textview_tempature.setText(tempture);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        isLogin=Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        manager.addEventCallback(ec);
        textview_home.setTextColor(getResources().getColor(R.color.title_blue_bg));
        textview_device.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_room.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_mine.setTextColor(getResources().getColor(android.R.color.darker_gray));
        imageview_home_page.setImageResource(R.drawable.checkthehome);
        imageview_devices.setImageResource(R.drawable.nocheckthedevice);
        imageview_rooms.setImageResource(R.drawable.nochecktheroom);
        imageview_personal_center.setImageResource(R.drawable.nocheckthemine);
        if(isLogin){
            mRoomList.clear();
            mRoomList.addAll(mRoomManager.queryRooms());
            mAdapter.notifyDataSetChanged();
            setRoomNormalLayout();
        }

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
        mRoomManager.removeRoomListener(this);
        manager.removeEventCallback(ec);
        manager.onDestroy();
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
        mRoomManager.initRoomManager(this,this);
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
                        Log.i(TAG, "LOGIN success uuid="+manager.getUserInfo().getUuid());
                        Perfence.setPerfence(AppConstant.PERFENCE_BIND_APP_UUID, manager.getUserInfo().getUuid());

                        Log.i(TAG, "LOGIN success uuid read="+Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID));
                        break;
                    case CONNECTED:
                        Log.i(TAG, "CONNECTED mqtt");
                        User user = manager.getUserInfo();
                        Perfence.setPerfence(Perfence.USER_PASSWORD, user.getPassword());
                        Perfence.setPerfence(Perfence.PERFENCE_PHONE, user.getName());
                        Perfence.setPerfence(AppConstant.USER_LOGIN, true);

                        mRoomList.clear();
                        mRoomList.addAll(mRoomManager.queryRooms());
                        mAdapter.notifyDataSetChanged();
                        setRoomNormalLayout();
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
