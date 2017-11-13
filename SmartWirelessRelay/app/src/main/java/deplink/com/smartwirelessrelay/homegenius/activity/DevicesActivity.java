package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.DeviceListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;

public class DevicesActivity extends Activity implements View.OnClickListener,SmartLockListener{
    private static final String TAG="DevicesActivity";
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;

    private SmartLockManager mSmartLockManager;

    private ListView listview_devies;
    private DeviceListAdapter mDeviceAdapter;
    /**
     * 上面半部分列表的数据
     * */
    private List<Device> datasTop;
    /**
     * 下面半部分列表的数据
     * */
    private List<SmartDev> datasBottom;

    private ImageView imageview_add_device;
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

        mSmartLockManager.queryDeviceList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmartLockManager.releaswSmartManager();
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  mSmartLockManager.releaswSmartManager();
    }
    private SQLiteDatabase db;
    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this, this);

        datasTop=new ArrayList<>();
        datasBottom=new ArrayList<>();
        //使用数据库中的数据
         db = Connector.getDatabase();
        datasTop= DataSupport.findAll(Device.class);
        if(datasTop.size()>0){
            Log.i(TAG,"设备界面查询设备"+datasTop.get(0).getStatus());
            Log.i(TAG,"设备界面查询设备"+datasTop.get(0).getUid());
        }else{
            Log.i(TAG,"设备界面查询设备 datasTop size ==0");
        }

        datasBottom= DataSupport.findAll(SmartDev.class);
        if(datasBottom.size()>0){
            Log.i(TAG,"设备界面查询智能设备"+datasBottom.get(0).getStatus());
            Log.i(TAG,"备设备界面查询智能设"+datasBottom.get(0).getType());
        }else{
            Log.i(TAG,"设备界面查询智能设备 datasBottom size ==0");
        }
        mDeviceAdapter = new DeviceListAdapter(this, datasTop, datasBottom);
        listview_devies.setAdapter(mDeviceAdapter);
        listview_devies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DevicesActivity.this,"设备列表点击position="+position,Toast.LENGTH_SHORT).show();
                    if(datasTop.size()<(position+1)){
                        //智能设备
                       String deviceType= datasBottom.get(position - datasTop.size()).getType();
                        Log.i(TAG,"智能设备类型="+deviceType);
                        switch (deviceType){
                            case "SmartLock":
                                startActivity(new Intent(DevicesActivity.this,SmartLockActivity.class));
                                break;
                        }
                    }
            }
        });
    }

    private void initEvents() {
        layout_home_page.setOnClickListener(this);
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
        imageview_add_device.setOnClickListener(this);
    }

    private void initViews() {
        layout_home_page= (LinearLayout) findViewById(R.id.layout_home_page);
        layout_devices= (LinearLayout) findViewById(R.id.layout_devices);
        layout_rooms= (LinearLayout) findViewById(R.id.layout_rooms);
        layout_personal_center= (LinearLayout) findViewById(R.id.layout_personal_center);
        listview_devies= (ListView) findViewById(R.id.listview_devies);
        imageview_add_device= (ImageView) findViewById(R.id.imageview_add_device);
        //TODO 初始化设备列表

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_home_page:
                startActivity(new Intent(this,SmartHomeMainActivity.class));
                break;
            case R.id.layout_devices:
               // startActivity(new Intent(this,DevicesActivity.class));
                break;
            case R.id.layout_rooms:
                startActivity(new Intent(this,RoomActivity.class));
                break;
            case R.id.layout_personal_center:
                startActivity(new Intent(this,PersonalCenterActivity.class));
                break;
            case R.id.imageview_add_device:
                startActivity(new Intent(this,AddDeviceActivity.class));
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
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            switch (msg.what) {

                case MSG_GET_DEVS:
                    Log.i(TAG, "mHandler MSG_GET_DEVS");
                    Gson gson = new Gson();
                    DeviceList aDeviceList = gson.fromJson(str, DeviceList.class);
                    datasTop.clear();
                    datasTop.addAll(aDeviceList.getDevice());
                    datasBottom.clear();
                    datasBottom.addAll(aDeviceList.getSmartDev());

                    mDeviceAdapter.notifyDataSetChanged();
                    Log.i(TAG, "mDeviceList.getDevice().size=" + aDeviceList.getDevice().size());
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
    @Override
    public void responseSetResult(String result) {

    }

    @Override
    public void responseBind(String result) {

    }
}
