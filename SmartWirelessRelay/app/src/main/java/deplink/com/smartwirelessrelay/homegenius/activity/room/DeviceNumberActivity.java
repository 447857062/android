package deplink.com.smartwirelessrelay.homegenius.activity.room;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.activity.room.adapter.RoomDevicesListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

public class DeviceNumberActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView image_back;
    private ListView listview_devices;
    private RoomDevicesListAdapter mRoomDevicesAdapter;
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

    private void initDatas() {
        String hintRoomName = getIntent().getStringExtra("roomname");
        mRoomManager = RoomManager.getInstance();

        mDevices = mRoomManager.findRoom(hintRoomName,true).getmDevices();

        mRoomDevicesAdapter = new RoomDevicesListAdapter(this, mDevices);
        listview_devices.setAdapter(mRoomDevicesAdapter);
        mRoomDevicesAdapter.notifyDataSetChanged();
    }

    private void initViews() {
        image_back = (ImageView) findViewById(R.id.image_back);
        listview_devices = (ListView) findViewById(R.id.listview_devices);
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
