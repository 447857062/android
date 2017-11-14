package deplink.com.smartwirelessrelay.homegenius.activity.device;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.AddDeviceTypeSelectAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

/**
 * 扫码添加设备
 */
public class AddDeviceQRcodeActivity extends Activity implements AdapterView.OnItemClickListener{
    private static final String TAG = "AddDeviceQRcodeActivity";
    private Bundle mBundle;
    private String mRoomName;
    private RoomManager mRoomManager;
    private GridView mGridView;
    private AddDeviceTypeSelectAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_qrcode);
        initViews();
        initDatas();
    }

    private void initViews() {
        mGridView= (GridView) findViewById(R.id.gridview_add_device_type);
    }
    private List<String>mDeviceTypes;
    private void initDatas() {
        mBundle = getIntent().getExtras();
        Log.i(TAG, "mBundle!=null " + (mBundle != null));
        if (mBundle != null) {
            mRoomName = mBundle.getString("roomName");
            Log.i(TAG, "当前编辑的房间名称= " + mRoomName);
        }
        mRoomManager = RoomManager.getInstance();
        mDeviceTypes=new ArrayList<>();
        mDeviceTypes.add("智能门锁");
        mDeviceTypes.add("红外万能遥控");
        mDeviceTypes.add("智能开关");
        mDeviceTypes.add("智能电视遥控");
        mDeviceTypes.add("智能空调遥控");
        mDeviceTypes.add("智能机顶盒遥控");
        mDeviceTypes.add("智能门铃");
        mAdapter=new AddDeviceTypeSelectAdapter(this,mDeviceTypes);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG,"onItemClick "+mDeviceTypes.get(position));
    }
}
