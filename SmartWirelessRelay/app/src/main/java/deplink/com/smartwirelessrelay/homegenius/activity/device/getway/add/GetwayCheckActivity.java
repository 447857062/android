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

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.activity.device.getway.adapter.GetwayListDevicesAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.wifi.ScanWifiListActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.UdpManager;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.UdpManagerGetIPLintener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;

/**
 * 查询附近所有的wifi
 */
public class GetwayCheckActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, UdpManagerGetIPLintener {
    private static final String TAG = "GetwayCheckActivity";

    private TextView textview_title;
    private FrameLayout image_back;
    private UdpManager mUdpmanager;
    private ListView listview_getway_devices;
    private List<Device> mDevices;
    private GetwayListDevicesAdapter mAdapter;
    private GetwayManager getwayManager;
    private List<Device> mBindGetway;
    private TextView textview_no_getway;
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
        getwayManager = GetwayManager.getInstance();
        mBindGetway = new ArrayList<>();
        mBindGetway = getwayManager.queryAllGetwayDevice();
        mDevices = new ArrayList<>();
        mAdapter = new GetwayListDevicesAdapter(this, mDevices, mBindGetway);

    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        listview_getway_devices.setAdapter(mAdapter);
        listview_getway_devices.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUdpmanager.registerNetBroadcast(this);
        DialogThreeBounce.showLoading(this);
        Message msg = Message.obtain();
        msg.what = MSG_DISMISS_DIALOG;
        mHandler.sendMessageDelayed(msg, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUdpmanager.unRegisterNetBroadcast(this);
    }

    private void initViews() {
        image_back = (FrameLayout) findViewById(R.id.image_back);
        textview_title = (TextView) findViewById(R.id.textview_title);
        listview_getway_devices = (ListView) findViewById(R.id.listview_getway_devices);
        textview_no_getway = (TextView) findViewById(R.id.textview_no_getway);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }

    private static final int MSG_CHECK_GETWAY_OK = 100;
    private static final int MSG_DISMISS_DIALOG = 101;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CHECK_GETWAY_OK:
                    Device device = (Device) msg.obj;
                    //如果存在相同ip就不添加
                    boolean addToDevices = true;
                    for (int i = 0; i < mDevices.size(); i++) {
                        if (mDevices.get(i).getIpAddress().equals(device.getIpAddress())) {
                            addToDevices = false;
                        }
                    }
                    if (addToDevices) {
                        mDevices.add(device);
                        textview_no_getway.setVisibility(View.GONE);
                        mBindGetway = getwayManager.queryAllGetwayDevice();
                        mAdapter.notifyDataSetChanged();
                    }

                    break;
                case MSG_DISMISS_DIALOG:
                    if(mDevices.size()==0){
                        textview_no_getway.setVisibility(View.VISIBLE);
                    }else{
                        textview_no_getway.setVisibility(View.GONE);
                    }
                    DialogThreeBounce.hideLoading();
                    break;
            }
        }
    };

    @Override
    public void onGetLocalConnectIp(String ipAddress, String uid) {
        Log.i(TAG, "检查网关，获取到IP地址=" + ipAddress);
        Message msg = Message.obtain();
        msg.what = MSG_CHECK_GETWAY_OK;
        Device device = new Device();
        device.setIpAddress(ipAddress);
        device.setUid(uid);
        msg.obj = device;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent inent=new Intent(this, ScanWifiListActivity.class);
        inent.putExtra("isShowSkipOption",false);
        startActivity(inent);
    }
}
