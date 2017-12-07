package deplink.com.smartwirelessrelay.homegenius.activity.device.getway.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.adapter.GetwayListDevicesAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.wifi.ScanWifiListActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.UdpManager;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.UdpManagerGetIPLintener;

/**
 * 查询附近所有的wifi
 */
public class GetwayCheckActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener,UdpManagerGetIPLintener {
    private static final String TAG = "GetwayCheckActivity";

    private TextView textview_title;
    private FrameLayout image_back;
    private UdpManager mUdpmanager;
    private ListView listview_getway_devices;
    private List<Device>mDevices;
    private GetwayListDevicesAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getway_check_list);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("网关检测");
        mUdpmanager = UdpManager.getInstance();
        mUdpmanager.InitUdpConnect(this, this);
        mDevices=new ArrayList<>();
        mAdapter=new GetwayListDevicesAdapter(this,mDevices);

    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        listview_getway_devices.setAdapter(mAdapter);
        listview_getway_devices.setOnItemClickListener(this);
    }

    private void initViews() {
        image_back = (FrameLayout) findViewById(R.id.image_back);
        textview_title= (TextView) findViewById(R.id.textview_title);
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
    private static final int MSG_CHECK_GETWAY_OK=100;
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CHECK_GETWAY_OK:
                    Device device=new Device();
                    device.setIpAddress((String) msg.obj);
                    mDevices.add(device);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(GetwayCheckActivity.this,"检查到IP为:"+ msg.obj+"的网关",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    public void onGetLocalConnectIp(String ipAddress) {
        Log.i(TAG, "检查网关，获取到IP地址=" + ipAddress);
        Message msg=Message.obtain();
        msg.what=MSG_CHECK_GETWAY_OK;
        msg.obj=ipAddress;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(GetwayCheckActivity.this,ScanWifiListActivity.class));
    }
}
