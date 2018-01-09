package com.deplink.homegenius.activity.personal.wifi;

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

import com.deplink.homegenius.Protocol.json.device.lock.SSIDList;
import com.deplink.homegenius.Protocol.json.wifi.AP_CLIENT;
import com.deplink.homegenius.activity.device.getway.add.AddGetwaySettingOptionsActivity;
import com.deplink.homegenius.activity.personal.wifi.adapter.WifiListAdapter;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.view.dialog.WifiRelayInputDialog;
import com.deplink.homegenius.view.dialog.loadingdialog.DialogThreeBounce;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * 配置wifi网关
 */
public class ScanWifiListActivity extends Activity implements DeviceListener, AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "ScanWifiListActivity";
    private DeviceManager mDeviceManager;
    private ListView listview_wifi_list;
    private WifiListAdapter mWifiListAdapter;
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView textview_edit;
    private TextView textview_reload_wifilist;
    private boolean isStartFromExperience;
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
        textview_edit.setOnClickListener(this);
        textview_reload_wifilist.setOnClickListener(this);
        listview_wifi_list.setAdapter(mWifiListAdapter);
    }

    private void initViews() {
        listview_wifi_list = findViewById(R.id.listview_wifi_list);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        image_back = findViewById(R.id.image_back);
        textview_reload_wifilist = findViewById(R.id.textview_reload_wifilist);
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryWifiRelayList();
    }

    private void queryWifiRelayList() {
        DialogThreeBounce.showLoading(this);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"mDatas.size()="+mDatas.size());
                DialogThreeBounce.hideLoading();

            }
        }, 3000);
        mDatas.clear();
        if(isStartFromExperience){
            mDatas.clear();
            List<SSIDList>lists=new ArrayList<>();
            SSIDList ssidList=new SSIDList();
            ssidList.setSSID("wifi列表1");
            ssidList.setQuality("77");
            ssidList.setEncryption("WPA2PSK");
            ssidList.setCRYTP("WPA2PSK");
            lists.add(ssidList);
             ssidList=new SSIDList();
            ssidList.setEncryption("WPA2PSK");
            ssidList.setSSID("wifi列表2");
            ssidList.setQuality("77");
            ssidList.setCRYTP("WPA2PSK");
            lists.add(ssidList);
             ssidList=new SSIDList();
            ssidList.setSSID("wifi列表3");
            ssidList.setEncryption("WPA2PSK");
            ssidList.setQuality("77");
            ssidList.setCRYTP("WPA2PSK");
            lists.add(ssidList);
            mDatas.addAll(lists);
            mWifiListAdapter.notifyDataSetChanged();
        }else{
            mDeviceManager.queryWifiList();
        }

    }

    private boolean isShowSkipOption;
    private List<SSIDList> mDatas;

    private void initDatas() {
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        textview_title.setText("配置WiFi网关");
        textview_edit.setText("跳过");
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this, this);
        mDatas = new ArrayList<>();
        mWifiListAdapter = new WifiListAdapter(this, mDatas);
        wifiRelayDialog = new WifiRelayInputDialog(this);
        isShowSkipOption = getIntent().getBooleanExtra("isShowSkipOption", false);
        if (!isShowSkipOption) {
            textview_edit.setVisibility(View.GONE);
        }
    }

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseBindDeviceResult(String result) {

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_GET_WIFILIST:
                    mDatas.clear();
                    mDatas.addAll((Collection<? extends SSIDList>) msg.obj);
                    mWifiListAdapter.notifyDataSetChanged();

                    break;
            }

        }
    };
    private static final int MSG_GET_WIFILIST = 1;

    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {
        Message msg = Message.obtain();
        msg.what = MSG_GET_WIFILIST;
        msg.obj = wifiList;
        mHandler.sendMessage(msg);

    }

    @Override
    public void responseSetWifirelayResult(int result) {
        Log.i(TAG, "responseSetWifirelayResult=" + result);
    }

    @Override
    public void responseBindDeviceHttpResult() {

    }

    private WifiRelayInputDialog wifiRelayDialog;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final AP_CLIENT setCmd = new AP_CLIENT();
        String setApCliSsid = mDatas.get(position).getSSID();
        setCmd.setApCliSsid(setApCliSsid);
        String setApCliEncrypType = mDatas.get(position).getEncryption();
        setCmd.setApCliEncrypType(setApCliEncrypType);
        String setApCliAuthMode = mDatas.get(position).getCRYTP();
        setCmd.setApCliAuthMode(setApCliAuthMode);
        String setChannel = mDatas.get(position).getChannel();
        setCmd.setChannel(setChannel);
        //没有密码直接连接
        if(isStartFromExperience){
            wifiRelayDialog.setSureBtnClickListener(new WifiRelayInputDialog.onSureBtnClickListener() {
                @Override
                public void onSureBtnClicked(String password) {
                }
            });
            wifiRelayDialog.show();
            wifiRelayDialog.setTitleText(setApCliSsid);
        }else{
            if (mDatas.get(position).getEncryption().equalsIgnoreCase("none")) {
                setCmd.setApCliWPAPSK("");
                mDeviceManager.setWifiRelay(setCmd);
            } else {
                wifiRelayDialog.setSureBtnClickListener(new WifiRelayInputDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked(String password) {
                        setCmd.setApCliWPAPSK(password);
                        mDeviceManager.setWifiRelay(setCmd);
                    }
                });
                wifiRelayDialog.show();
                wifiRelayDialog.setTitleText(setApCliSsid);
            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_reload_wifilist:
                queryWifiRelayList();
                break;
            case R.id.textview_edit:
                startActivity(new Intent(this, AddGetwaySettingOptionsActivity.class));
                break;
        }
    }
}
