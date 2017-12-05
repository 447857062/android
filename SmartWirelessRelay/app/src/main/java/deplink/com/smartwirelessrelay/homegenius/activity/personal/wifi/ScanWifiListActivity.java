package deplink.com.smartwirelessrelay.homegenius.activity.personal.wifi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.AP_CLIENT;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.wifi.adapter.WifiListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;

/**
 * 配置wifi网关
 */
public class ScanWifiListActivity extends Activity implements DeviceListener, AdapterView.OnItemClickListener,View.OnClickListener {
    private static final String TAG="ScanWifiListActivity";
    private DeviceManager mDeviceManager;
    private ListView listview_wifi_list;
    private WifiListAdapter mWifiListAdapter;
    private ImageView image_back;
    private TextView textview_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wifi_list);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        listview_wifi_list.setOnItemClickListener(this);
        image_back.setOnClickListener(this);
        listview_wifi_list.setAdapter(mWifiListAdapter);
    }

    private void initViews() {
        listview_wifi_list = (ListView) findViewById(R.id.listview_wifi_list);
        textview_title = (TextView) findViewById(R.id.textview_title);
        image_back = (ImageView) findViewById(R.id.image_back);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDeviceManager.queryWifiList();
    }

    private List<SSIDList> mDatas;
    private void initDatas() {
        textview_title.setText("配置WiFi网关");
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this, this);
        mDatas = new ArrayList<>();
        mWifiListAdapter = new WifiListAdapter(this, mDatas);

    }

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseBindDeviceResult(String result) {

    }
    private Handler mhanHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_GET_WIFILIST:
                    mDatas.clear();
                    mDatas.addAll((Collection<? extends SSIDList>) msg.obj);
                    mWifiListAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };
    private static final int MSG_GET_WIFILIST=1;
    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {
        Message msg=Message.obtain();
        msg.what=MSG_GET_WIFILIST;
        msg.obj=wifiList;
        mhanHandler.sendMessage(msg);

    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AP_CLIENT setCmd=new AP_CLIENT();
        String setApCliSsid=mDatas.get(position).getSSID();
        setCmd.setApCliSsid(setApCliSsid);

        String setApCliEncrypType=mDatas.get(position).getEncryption();
        setCmd.setApCliEncrypType(setApCliEncrypType);

        String setApCliAuthMode=mDatas.get(position).getCRYTP();
        setCmd.setApCliAuthMode(setApCliAuthMode);

        String setChannel=mDatas.get(position).getChannel();
        setCmd.setChannel(setChannel);
        //没有密码直接连接
        if(mDatas.get(position).getEncryption().equalsIgnoreCase("none")){
            setCmd.setApCliWPAPSK("");
            mDeviceManager.setWifiRelay(setCmd);
        }else{
            //TODO 弹框提示连接

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
