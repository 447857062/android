package deplink.com.smartwirelessrelay.homegenius.activity.homepage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.ExperienceCenterDevice;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.Info;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.GetwayDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.SmartLockActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.adapter.ExperienceCenterListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.adapter.HomepageGridViewAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.adapter.HomepageRoomShowTypeChangedViewAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.PersonalCenterActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.ManageRoomActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.RoomActivity;
import deplink.com.smartwirelessrelay.homegenius.application.AppManager;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.NonScrollableListView;

/**
 * 智能家居主页
 */
public class SmartHomeMainActivity extends Activity implements View.OnClickListener, LocalConnecteListener {
    private static final String TAG = "SmartHomeMainActivity";
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;
    private ImageView imageview_devices;
    private ImageView imageview_home_page;
    private ImageView imageview_rooms;
    private ImageView imageview_personal_center;
    private LocalConnectmanager mLocalConnectmanager;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home_main);
        initViews();
        initDatas();
        initEvents();
    }
    private RoomManager mRoomManager;

    @Override
    protected void onResume() {
        super.onResume();
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
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        roomGridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        roomGridView.setColumnWidth(itemWidth); // 设置列表项宽
       // roomGridView.setHorizontalSpacing(5); // 设置列表项水平间距
        roomGridView.setStretchMode(GridView.NO_STRETCH);
        roomGridView.setNumColumns(size); // 设置列数量=列表集合数
        roomGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        layout_roomselect_changed_ype.setAdapter(mRoomSelectTypeChangedAdapter);
        mRoomSelectTypeChangedAdapter.notifyDataSetChanged();
    }

    private void initDatas() {

        mLocalConnectmanager = LocalConnectmanager.getInstance();
        mLocalConnectmanager.InitLocalConnectManager(SmartHomeMainActivity.this, AppConstant.BIND_APP_MAC);
        mLocalConnectmanager.addLocalConnectListener(SmartHomeMainActivity.this);
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager();

        mAdapter = new HomepageGridViewAdapter(SmartHomeMainActivity.this, mRoomList);
        mRoomSelectTypeChangedAdapter=new HomepageRoomShowTypeChangedViewAdapter(this,mRoomList);
        mExperienceCenterDeviceList = new ArrayList<>();
        ExperienceCenterDevice oneDevice = new ExperienceCenterDevice();
        oneDevice.setDeviceName("智能门锁");
        oneDevice.setOnline(true);
        mExperienceCenterDeviceList.add(oneDevice);
        oneDevice = new ExperienceCenterDevice();
        oneDevice.setDeviceName("智能开关");
        oneDevice.setOnline(false);
        mExperienceCenterDeviceList.add(oneDevice);
        oneDevice = new ExperienceCenterDevice();
        oneDevice.setDeviceName("智能网关");
        oneDevice.setOnline(false);
        mExperienceCenterDeviceList.add(oneDevice);
        mExperienceCenterListAdapter = new ExperienceCenterListAdapter(this, mExperienceCenterDeviceList);
        listview_experience_center.setOnItemClickListener(mExperienceCenterListClickListener);
    }

    private AdapterView.OnItemClickListener mExperienceCenterListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (mExperienceCenterDeviceList.get(position).getDeviceName()) {
                case "智能门锁":
                    Intent intent = new Intent(SmartHomeMainActivity.this, SmartLockActivity.class);
                    intent.putExtra("isStartFromExperience", true);
                    startActivity(intent);
                    break;
                case "智能网关":
                    Intent intentGetwayDevice = new Intent(SmartHomeMainActivity.this, GetwayDeviceActivity.class);
                    intentGetwayDevice.putExtra("isStartFromExperience", true);
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
                Bundle bundle = new Bundle();
                bundle.putString("roomName", RoomManager.getInstance().getmRooms().get(position).getRoomName());
                bundle.putInt("roomOrdinalNumber", RoomManager.getInstance().getmRooms().get(position).getRoomOrdinalNumber());
                Intent intent = new Intent(SmartHomeMainActivity.this, ManageRoomActivity.class);
                Log.i(TAG, "传递当前房间名字=" + bundle.get("roomName") + "获取到的名字是=" + RoomManager.getInstance().getmRooms().get(position));
                intent.putExtras(bundle);
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

        layout_roomselect_normal= (HorizontalScrollView) findViewById(R.id.layout_roomselect_normal);
        layout_roomselect_changed_ype= (NonScrollableListView) findViewById(R.id.layout_roomselect_changed_ype);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mLocalConnectmanager.removeLocalConnectListener(this);
        mRoomManager.updateRoomsOrdinalNumber();
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
               if(layout_roomselect_normal.getVisibility()==View.VISIBLE){
                   layout_roomselect_normal.setVisibility(View.GONE);
                   layout_roomselect_changed_ype.setVisibility(View.VISIBLE);
               }else{
                   layout_roomselect_normal.setVisibility(View.VISIBLE);
                   layout_roomselect_changed_ype.setVisibility(View.GONE);
               }
                break;

        }
    }

    @Override
    public void handshakeCompleted() {

    }

    @Override
    public void createSocketFailed(String msg) {

    }

    @Override
    public void OnFailedgetLocalGW(String msg) {

    }

    @Override
    public void OnBindAppResult(String uid) {

    }

    @Override
    public void OnGetQueryresult(String devList) {
        Log.i(TAG, "OnGetQueryresult");
    }

    @Override
    public void OnGetSetresult(String setResult) {

    }

    @Override
    public void OnGetBindresult(String setResult) {

    }


    @Override
    public void getWifiList(String result) {

    }

    @Override
    public void onSetWifiRelayResult(String result) {

    }

    @Override
    public void onGetalarmRecord(List<Info> alarmList) {

    }
}
