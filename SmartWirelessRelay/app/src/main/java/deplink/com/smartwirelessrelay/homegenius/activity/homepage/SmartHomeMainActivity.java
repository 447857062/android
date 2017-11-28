package deplink.com.smartwirelessrelay.homegenius.activity.homepage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.ExperienceCenterDevice;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.LOCK_ALARM;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.GetwayDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.SmartLockActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.adapter.ExperienceCenterListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.PersonalCenterActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.ManageRoomActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.RoomActivity;
import deplink.com.smartwirelessrelay.homegenius.application.AppManager;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.popmenu.adapter.BaseRecyclerViewAdapter;
import deplink.com.smartwirelessrelay.homegenius.view.popmenu.powerpopmenu.PowerPopMenu;
import deplink.com.smartwirelessrelay.homegenius.view.popmenu.powerpopmenu.PowerPopMenuModel;
import deplink.com.smartwirelessrelay.homegenius.view.popmenu.utils.ToastUtils;

/**
 * 智能家居主页
 */
public class SmartHomeMainActivity extends Activity implements View.OnClickListener, LocalConnecteListener {
    private static final String TAG = "SmartHomeMainActivity";
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;

    private LocalConnectmanager mLocalConnectmanager;
    private ImageView imageview_setting;

    private List<Room> mRoomList = new ArrayList<>();
    private HomepageGridViewAdapter mAdapter;
    private GridView roomGridView;
    private ListView listview_experience_center;
    private ExperienceCenterListAdapter mExperienceCenterListAdapter;
    private List<ExperienceCenterDevice> mExperienceCenterDeviceList;

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
        roomGridView.setHorizontalSpacing(5); // 设置列表项水平间距
        roomGridView.setStretchMode(GridView.NO_STRETCH);
        roomGridView.setNumColumns(size); // 设置列数量=列表集合数
        roomGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void initDatas() {

        mLocalConnectmanager = LocalConnectmanager.getInstance();
        mLocalConnectmanager.InitLocalConnectManager(SmartHomeMainActivity.this, AppConstant.BIND_APP_MAC);
        mLocalConnectmanager.addLocalConnectListener(SmartHomeMainActivity.this);
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager();

        mAdapter = new HomepageGridViewAdapter(SmartHomeMainActivity.this, mRoomList);
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
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
        imageview_setting.setOnClickListener(this);
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

    //TODO
    private Button mUpHBtn;
    private Button mUpVBtn;
    private Button mDownHBtn;
    private Button mDownVBtn;
    private LinearLayout mEmptyView;
    private PowerPopMenu mPowerPopMenu;
    private Context mContext;
    private List<PowerPopMenuModel> mList;
    private class OnItemClickLis implements BaseRecyclerViewAdapter.OnItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            ToastUtils.showMessage(mContext, mList.get(position).text);
            mPowerPopMenu.dismiss();
        }
    }
    //TODO 测试，删除
    private void initViews() {
        layout_home_page = (LinearLayout) findViewById(R.id.layout_home_page);
        layout_devices = (LinearLayout) findViewById(R.id.layout_devices);
        layout_rooms = (LinearLayout) findViewById(R.id.layout_rooms);
        layout_personal_center = (LinearLayout) findViewById(R.id.layout_personal_center);
        imageview_setting = (ImageView) findViewById(R.id.imageview_setting);
        roomGridView = (GridView) findViewById(R.id.grid);
        listview_experience_center = (ListView) findViewById(R.id.listview_experience_center);
        //TODO
        mContext = this;
        mList = new ArrayList<>();
        PowerPopMenuModel item1 = new PowerPopMenuModel();
        item1.text = "aaa";
        item1.resid = R.mipmap.icon1;
        mList.add(item1);
        PowerPopMenuModel item2 = new PowerPopMenuModel();
        item2.text = "bbb";
        item2.resid = R.mipmap.icon2;
        mList.add(item2);
        PowerPopMenuModel item3 = new PowerPopMenuModel();
        item3.text = "ccc";
        mList.add(item3);
        mUpHBtn = (Button) findViewById(R.id.btn_up_h);
        mUpVBtn = (Button) findViewById(R.id.btn_up_v);
        mDownHBtn = (Button) findViewById(R.id.btn_down_h);
        mDownVBtn = (Button) findViewById(R.id.btn_down_v);
        mEmptyView = (LinearLayout) View.inflate(mContext, R.layout.view_empty, null);

        mUpHBtn.setOnClickListener(this);
        mUpVBtn.setOnClickListener(this);
        mDownHBtn.setOnClickListener(this);
        mDownVBtn.setOnClickListener(this);
        //TODO 测试，删除
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_home_page:

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
            //TODO 测试，删除
            case R.id.btn_up_h:
                mPowerPopMenu = new PowerPopMenu(mContext, LinearLayoutManager.HORIZONTAL, PowerPopMenu
                        .POP_UP_TO_DOWN);
                //必须放在setListResource之前
                mPowerPopMenu.setIsShowIcon(true);
                mPowerPopMenu.setListResource(mList);
                mPowerPopMenu.setOnItemClickListener(new OnItemClickLis());
                mPowerPopMenu.addView(mEmptyView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                        .MATCH_PARENT, 50));
                mPowerPopMenu.show();

                break;
            case R.id.btn_up_v:
                mPowerPopMenu = new PowerPopMenu(mContext, LinearLayoutManager.VERTICAL, PowerPopMenu.POP_UP_TO_DOWN);
                mPowerPopMenu.setIsShowIcon(false);
                mPowerPopMenu.setListResource(mList);
                mPowerPopMenu.setOnItemClickListener(new OnItemClickLis());
                mPowerPopMenu.show(v);
                break;
            case R.id.btn_down_h:
                mPowerPopMenu = new PowerPopMenu(mContext, LinearLayoutManager.HORIZONTAL, PowerPopMenu.POP_DOWN_TO_UP);
                mPowerPopMenu.setIsShowIcon(true);
                mPowerPopMenu.setListResource(mList);
                mPowerPopMenu.addView(mEmptyView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                        .MATCH_PARENT, 50));
                mPowerPopMenu.setOnItemClickListener(new OnItemClickLis());
                mPowerPopMenu.show(v);
                break;
            case R.id.btn_down_v:
                mPowerPopMenu = new PowerPopMenu(mContext, LinearLayoutManager.VERTICAL, PowerPopMenu.POP_DOWN_TO_UP);
                mPowerPopMenu.setIsShowIcon(true);
                mPowerPopMenu.setListResource(mList);
                mPowerPopMenu.setOnItemClickListener(new OnItemClickLis());
                mPowerPopMenu.show(v);
                break;
            //TODO 测试，删除
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
    public void onGetalarmRecord(List<LOCK_ALARM> alarmList) {

    }
}
