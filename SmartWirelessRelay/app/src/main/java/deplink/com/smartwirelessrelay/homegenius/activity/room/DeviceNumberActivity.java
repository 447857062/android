package deplink.com.smartwirelessrelay.homegenius.activity.room;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.activity.room.adapter.RoomDevicesListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.room.adapter.RoomGetwayDevicesListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

/**
 * 查看智能设备列表的界面
 */
public class DeviceNumberActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG="DeviceNumberActivity";
    private ImageView image_back;
    private ListView listview_devices;
    private RoomDevicesListAdapter mRoomDevicesAdapter;

    private ListView listview_getway_devices;
    private RoomGetwayDevicesListAdapter mRoomGetwayDevicesListAdapter;
    /**
     * 房间排序号
     */
    private int roomOrdinalNumber;

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
        listview_devices.setOnItemClickListener(this);
    }

    private List<SmartDev> mDevices = new ArrayList<>();
    private RoomManager mRoomManager;
    private List<Device>mGetwayDevices=new ArrayList<>();
    private Room currentRoom;
    private DeviceManager mDeviceManager;
    private GetwayManager mGetwayManager;
    private void initDatas() {
        String hintRoomName = getIntent().getStringExtra("roomname");
        mRoomManager = RoomManager.getInstance();

        mDeviceManager=DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this,null);

        mGetwayManager=GetwayManager.getInstance();
        mGetwayManager.InitGetwayManager(this,null);

        currentRoom=mRoomManager.findRoom(hintRoomName,true);

        mDevices = currentRoom.getmDevices();
        mRoomDevicesAdapter = new RoomDevicesListAdapter(this, mDevices,currentRoom,mDeviceManager);
        listview_devices.setAdapter(mRoomDevicesAdapter);
        mRoomDevicesAdapter.notifyDataSetChanged();

        mGetwayDevices= currentRoom.getmGetwayDevices();
        mRoomGetwayDevicesListAdapter=new RoomGetwayDevicesListAdapter(this,mGetwayDevices,currentRoom,mGetwayManager);
        listview_getway_devices.setAdapter(mRoomGetwayDevicesListAdapter);
        mRoomGetwayDevicesListAdapter.notifyDataSetChanged();
        Log.i(TAG,"初始化设备列表，智能设备="+mDevices.size()+"网关设备="+mGetwayDevices.size());
    }

    private void initViews() {
        image_back = (ImageView) findViewById(R.id.image_back);
        listview_devices = (ListView) findViewById(R.id.listview_devices);
        listview_getway_devices = (ListView) findViewById(R.id.listview_getway_devices);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
