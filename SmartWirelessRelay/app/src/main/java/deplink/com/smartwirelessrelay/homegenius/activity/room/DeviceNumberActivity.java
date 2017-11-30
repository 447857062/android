package deplink.com.smartwirelessrelay.homegenius.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    private TextView textview_edit;
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
        textview_edit.setOnClickListener(this);
    }

    private List<SmartDev> mDevices = new ArrayList<>();
    private RoomManager mRoomManager;
    private List<Device>mGetwayDevices=new ArrayList<>();
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
        textview_edit = (TextView) findViewById(R.id.textview_edit);
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
