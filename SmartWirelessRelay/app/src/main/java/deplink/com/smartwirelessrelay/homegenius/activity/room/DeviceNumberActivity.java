package deplink.com.smartwirelessrelay.homegenius.activity.room;

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

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.DeviceListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.GetwayDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.RemoteControlActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl.AirRemoteControlMianActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.topBox.IptvMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.tv.TvMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.RouterMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.SmartLockActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

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
    private List<Device> datasTop;
    /**
     * 下面半部分列表的数据
     */
    private List<SmartDev> datasBottom;

    private ListView listview_devies;
    private RelativeLayout layout_device_number_root;
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private RoomManager mRoomManager;

    private Room currentRoom;
    private DeviceManager mDeviceManager;
    private GetwayManager mGetwayManager;
    private void initDatas() {
        mRoomManager = RoomManager.getInstance();
        mDeviceManager=DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this,null);
        mGetwayManager=GetwayManager.getInstance();
        mGetwayManager.InitGetwayManager(this,null);
        String hintRoomName = mRoomManager.getCurrentSelectedRoom().getRoomName();
        currentRoom=mRoomManager.getCurrentSelectedRoom();
        String roomType=currentRoom.getRoomType();
        switch (roomType){
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_LIVING:
                layout_device_number_root.setBackgroundResource(R.drawable.livingroombackground);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_BED:
                layout_device_number_root.setBackgroundResource(R.drawable.bedroombackground);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_KITCHEN:
                layout_device_number_root.setBackgroundResource(R.drawable.kitchenbackground);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_STUDY:
                layout_device_number_root.setBackgroundResource(R.drawable.studybackground);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_STORAGE:
                layout_device_number_root.setBackgroundResource(R.drawable.storageroom);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_TOILET:
                layout_device_number_root.setBackgroundResource(R.drawable.toiletbackground);
                break;
            case deplink.com.smartwirelessrelay.homegenius.constant.Room.ROOMTYPE.TYPE_DINING:
                layout_device_number_root.setBackgroundResource(R.drawable.diningroombackground);
                break;
        }
        datasTop = new ArrayList<>();
        datasBottom = new ArrayList<>();
        //使用数据库中的数据
        datasTop.addAll(currentRoom.getmGetwayDevices());
        datasBottom .addAll(currentRoom.getmDevices());

        mDeviceAdapter = new DeviceListAdapter(this, datasTop, datasBottom);
        listview_devies.setAdapter(mDeviceAdapter);
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
                        case "SMART_LOCK":
                            SmartLockManager.getInstance().setCurrentSelectLock(datasBottom.get(position - datasTop.size()));
                            startActivity(new Intent(DeviceNumberActivity.this, SmartLockActivity.class));
                            break;
                        case "IRMOTE_V2":
                            startActivity(new Intent(DeviceNumberActivity.this, RemoteControlActivity.class));
                            break;
                        case "智能空调":
                            startActivity(new Intent(DeviceNumberActivity.this, AirRemoteControlMianActivity.class));
                            break;
                        case "路由器":
                            RouterManager.getInstance().setCurrentSelectedRouter(datasBottom.get(position-datasTop.size()));
                            startActivity(new Intent(DeviceNumberActivity.this, RouterMainActivity.class));
                            break;
                        case "智能电视":
                            startActivity(new Intent(DeviceNumberActivity.this, TvMainActivity.class));
                            break;
                        case "智能机顶盒遥控":
                            startActivity(new Intent(DeviceNumberActivity.this, IptvMainActivity.class));
                            break;
                        case "智能开关":
                            // startActivity(new Intent(DevicesActivity.this, SelectSwitchTypeActivity.class));
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

    private void initViews() {
        image_back = (FrameLayout) findViewById(R.id.image_back);

        textview_edit = (TextView) findViewById(R.id.textview_edit);
        listview_devies=(ListView) findViewById(R.id.listview_devies);
        layout_device_number_root=(RelativeLayout) findViewById(R.id.layout_device_number_root);
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
