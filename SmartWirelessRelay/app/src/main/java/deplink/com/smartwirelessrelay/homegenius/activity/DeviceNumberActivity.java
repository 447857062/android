package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.litepal.tablemanager.Connector;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.RoomDevicesListAdapter;

public class DeviceNumberActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
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
    private List<SmartDev>mDevices;
    private SQLiteDatabase db;
    private void initDatas() {
        db = Connector.getDatabase();

        //TODO
     //   mDevices= DataSupport.findAll(Room.class);
        mRoomDevicesAdapter=new RoomDevicesListAdapter(this,mDevices);
    }

    private void initViews() {
        image_back= (ImageView) findViewById(R.id.image_back);
        listview_devices= (ListView) findViewById(R.id.listview_devices);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
