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

import com.deplink.homegenius.Protocol.json.OpResult;
import com.deplink.homegenius.Protocol.json.device.lock.SSIDList;
import com.deplink.homegenius.Protocol.json.wifi.AP_CLIENT;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.activity.personal.wifi.adapter.WifiListAdapter;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.getway.GetwayListener;
import com.deplink.homegenius.manager.device.getway.GetwayManager;
import com.deplink.homegenius.util.NetUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.util.WeakRefHandler;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.WifiRelayInputDialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * 配置wifi网关
 */
public class ScanWifiListActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener, GetwayListener {
    private static final String TAG = "ScanWifiListActivity";
    private DeviceManager mDeviceManager;
    private ListView listview_wifi_list;
    private WifiListAdapter mWifiListAdapter;
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView textview_edit;
    private TextView textview_reload_wifilist;
    private boolean isStartFromExperience;
    private GetwayManager mGetwayManager;
    private boolean isLogin;
    private SDKManager manager;
    private EventCallback ec;
    private DeleteDeviceDialog connectLostDialog;
    private DeviceListener mDeviceListener;
    private TextView textview_wifilist_no;
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
        textview_wifilist_no = findViewById(R.id.textview_wifilist_no);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        manager.addEventCallback(ec);
        mDeviceManager.addDeviceListener(mDeviceListener);
        queryWifiRelayList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
        mDeviceManager.removeDeviceListener(mDeviceListener);
    }

    private void queryWifiRelayList() {
        mDatas.clear();
        if (isStartFromExperience) {
            mDatas.clear();
            List<SSIDList> lists = new ArrayList<>();
            SSIDList ssidList = new SSIDList();
            ssidList.setSSID("wifi列表1");
            ssidList.setQuality("77");
            ssidList.setEncryption("WPA2PSK");
            ssidList.setCRYTP("WPA2PSK");
            lists.add(ssidList);
            ssidList = new SSIDList();
            ssidList.setEncryption("WPA2PSK");
            ssidList.setSSID("wifi列表2");
            ssidList.setQuality("77");
            ssidList.setCRYTP("WPA2PSK");
            lists.add(ssidList);
            ssidList = new SSIDList();
            ssidList.setSSID("wifi列表3");
            ssidList.setEncryption("WPA2PSK");
            ssidList.setQuality("77");
            ssidList.setCRYTP("WPA2PSK");
            lists.add(ssidList);
            mWifiListAdapter.notifyDataSetChanged();
        } else {
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
        mDeviceManager.InitDeviceManager(this);
        mGetwayManager = GetwayManager.getInstance();
        mGetwayManager.InitGetwayManager(this, this);
        mDatas = new ArrayList<>();
        mWifiListAdapter = new WifiListAdapter(this, mDatas);
        wifiRelayDialog = new WifiRelayInputDialog(this);
        isShowSkipOption = getIntent().getBooleanExtra("isShowSkipOption", false);
        if (!isShowSkipOption) {
            textview_edit.setVisibility(View.GONE);
        }
        connectLostDialog = new DeleteDeviceDialog(ScanWifiListActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(ScanWifiListActivity.this, LoginActivity.class));
            }
        });
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {

            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {
            }


            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {

            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                isLogin = false;
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setContentText("当前账号已在其它设备上登录,是否重新登录");
            }
            @Override
            public void notifyHomeGeniusResponse(String result) {
                super.notifyHomeGeniusResponse(result);
                Gson gson = new Gson();
                Log.i(TAG,"notifyHomeGeniusResponse"+result);
                OpResult wifiListResult = gson.fromJson(result, OpResult.class);
                if (wifiListResult.getOP().equalsIgnoreCase("REPORT") && wifiListResult.getMethod().equalsIgnoreCase("WIFIRELAY")) {
                    Message msg = Message.obtain();
                    msg.what = MSG_GET_WIFILIST;
                    msg.obj = wifiListResult.getSSIDList();
                    Log.i(TAG,"wifi列表长度:"+wifiListResult.getSSIDList().size());
                    mHandler.sendMessage(msg);
                }else if(wifiListResult.getOP().equalsIgnoreCase("REPORT")&& wifiListResult.getMethod().equalsIgnoreCase("WIFI")){
                    if(wifiListResult.getResult()!=-1){
                        mHandler.sendEmptyMessage(MSG_GET_WIFILIST_SET_RESULT);
                    }else{
                        ToastSingleShow.showText(ScanWifiListActivity.this,"设置wifi中继失败");
                    }
                }
            }
        };
        mDeviceListener = new DeviceListener() {
            @Override
            public void responseWifiListResult(List<SSIDList> wifiList) {
                super.responseWifiListResult(wifiList);
                Message msg = Message.obtain();
                msg.what = MSG_GET_WIFILIST;
                msg.obj = wifiList;
                mHandler.sendMessage(msg);
            }
        };
    }
    private static final int MSG_GET_WIFILIST = 1;
    private static final int MSG_GET_WIFILIST_SET_RESULT = 2;
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_WIFILIST:
                    //更新一下中继器的在线离线状态,这里如果接受到数据证明是在线状态
                    String statu=mGetwayManager.getCurrentSelectGetwayDevice().getStatus();
                    if(statu==null || statu.equalsIgnoreCase("离线")){
                        mGetwayManager.getCurrentSelectGetwayDevice().setStatus("在线");
                        mGetwayManager.getCurrentSelectGetwayDevice().saveFast();
                    }
                    textview_wifilist_no.setVisibility(View.GONE);
                    mDatas.clear();
                    mDatas.addAll((Collection<? extends SSIDList>) msg.obj);
                    mWifiListAdapter.notifyDataSetChanged();
                    break;
                case MSG_GET_WIFILIST_SET_RESULT:
                    ToastSingleShow.showText(ScanWifiListActivity.this,"设置wifi中继成功");
                    break;
            }
            return true;
        }
    };
    private Handler mHandler = new WeakRefHandler(mCallback);
    @Override
    public void responseSetWifirelayResult(int result) {
        Log.i(TAG, "responseSetWifirelayResult=" + result);
        if(result!=-1){
            mHandler.sendEmptyMessage(MSG_GET_WIFILIST_SET_RESULT);
        }else{
            ToastSingleShow.showText(ScanWifiListActivity.this,"设置wifi中继失败");
        }

    }


    @Override
    public void responseResult(String result) {

    }

    @Override
    public void responseDeleteDeviceHttpResult(DeviceOperationResponse result) {

    }
    private WifiRelayInputDialog wifiRelayDialog;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final AP_CLIENT setCmd = new AP_CLIENT();
        String setApCliSsid = mDatas.get(position).getSSID();
        setCmd.setApCliSsid(setApCliSsid);
        String setApCliEncrypType = mDatas.get(position).getEncryption();
        String setApCliAuthMode = mDatas.get(position).getCRYTP();
        setCmd.setApCliEncrypType(setApCliAuthMode);
        setCmd.setApCliAuthMode(setApCliEncrypType);
        String setChannel = mDatas.get(position).getChannel();
        setCmd.setChannel(setChannel);
        //没有密码直接连接
        if (isStartFromExperience) {
            wifiRelayDialog.setSureBtnClickListener(new WifiRelayInputDialog.onSureBtnClickListener() {
                @Override
                public void onSureBtnClicked(String password) {
                }
            });
            wifiRelayDialog.show();
            wifiRelayDialog.setTitleText(setApCliSsid);
        } else {
            if(NetUtil.isNetAvailable(ScanWifiListActivity.this)){
                if (mDatas.get(position).getEncryption().equalsIgnoreCase("none")) {
                    setCmd.setApCliWPAPSK("");
                    mGetwayManager.setWifiRelay(setCmd);
                } else {
                    wifiRelayDialog.setSureBtnClickListener(new WifiRelayInputDialog.onSureBtnClickListener() {
                        @Override
                        public void onSureBtnClicked(String password) {
                            setCmd.setApCliWPAPSK(password);
                            mGetwayManager.setWifiRelay(setCmd);
                        }
                    });
                    wifiRelayDialog.show();
                    wifiRelayDialog.setTitleText(setApCliSsid);
                }
            }else{
                ToastSingleShow.showText(ScanWifiListActivity.this,"无可用的网络连接");
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
                if(NetUtil.isNetAvailable(this)){
                    queryWifiRelayList();
                }else{
                    ToastSingleShow.showText(this,"无可用的网络连接");
                }

                break;
            case R.id.textview_edit:

                break;
        }
    }
}
