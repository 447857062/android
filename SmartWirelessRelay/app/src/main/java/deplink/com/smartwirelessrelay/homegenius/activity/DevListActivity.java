package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.DevListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.SmartDevListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import deplink.com.smartwirelessrelay.homegenius.util.NetStatusUtil;
import deplink.com.smartwirelessrelay.homegenius.util.SharedPreference;


public class DevListActivity extends Activity implements View.OnClickListener, LocalConnecteListener {
    private static final String TAG = "DevListActivity";
    private ListView dev_list;
    private List<Device> mDeviceList;
    private ListView smart_dev_list;
    private List<SmartDev> mSmartDev;
    private DevListAdapter devAdapter;
    private SmartDevListAdapter smartDevListAdapter;
    private Button btn_once_auth;
    private Button btn_time_limit_auth;
    private Button btn_forever_auth;
    private Button btn_open_door;


    private EditText edittext_AuthPwd;
    private EditText edittext_UserID;
    private EditText edittext_ManagePasswd;
    private EditText edittext_LimitedTime;
    GeneralPacket packet;
    private LocalConnectmanager localConnectmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_list);
        initViews();
        initData();
    }

    private void initData() {
        mDeviceList = new ArrayList<>();
        mSmartDev = new ArrayList<>();
        devAdapter = new DevListAdapter(this, mDeviceList);
        smartDevListAdapter = new SmartDevListAdapter(this, mSmartDev);
        dev_list.setAdapter(devAdapter);
        smart_dev_list.setAdapter(smartDevListAdapter);
        localConnectmanager=LocalConnectmanager.getInstance();
        localConnectmanager.InitLocalConnectManager(this, this);
    }

    private void initViews() {
        dev_list = (ListView) findViewById(R.id.dev_list);
        smart_dev_list = (ListView) findViewById(R.id.smart_dev_list);
        btn = (Button) findViewById(R.id.click);
        btn_once_auth = (Button) findViewById(R.id.btn_once_auth);
        btn_time_limit_auth = (Button) findViewById(R.id.btn_time_limit_auth);
        btn_forever_auth = (Button) findViewById(R.id.btn_forever_auth);
        btn_open_door = (Button) findViewById(R.id.btn_open_door);
        btn.setOnClickListener(this);
        btn_once_auth.setOnClickListener(this);
        btn_time_limit_auth.setOnClickListener(this);
        btn_forever_auth.setOnClickListener(this);
        btn_open_door.setOnClickListener(this);
        edittext_AuthPwd = (EditText) findViewById(R.id.edittext_AuthPwd);
        edittext_UserID = (EditText) findViewById(R.id.edittext_UserID);
        edittext_ManagePasswd = (EditText) findViewById(R.id.edittext_ManagePasswd);
        edittext_LimitedTime = (EditText) findViewById(R.id.edittext_LimitedTime);
    }

    private Button btn;


    private static final int MSG_GET_DEVS = 0x01;
    private static final int MSG_GET_SET_RESULT = 0x02;
    private static final int MSG_TOAST = 0x03;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            switch (msg.what) {

                case MSG_GET_DEVS:
                    Log.i(TAG, "mHandler MSG_GET_DEVS");
                    Gson gson = new Gson();
                    DeviceList aDeviceList = gson.fromJson(str, DeviceList.class);
                    //mDeviceList=aDeviceList.getDevice();
                    mDeviceList.clear();
                    mDeviceList.addAll(aDeviceList.getDevice());
                    mSmartDev.clear();
                    mSmartDev.addAll(aDeviceList.getSmartDev());
                    if (aDeviceList.getSmartDev() != null && aDeviceList.getSmartDev().size() > 0) {
                        for (int i = 0; i < aDeviceList.getSmartDev().size(); i++) {
                            //uid
                            String smartuid;
                            smartuid = mSmartDev.get(i).getDevUid();
                            SharedPreference sharedPreference = new SharedPreference(DevListActivity.this, "smartuid");
                            sharedPreference.saveString("smartuid", smartuid);
                            Log.i(TAG, "sharedPreference save smartuid=" + smartuid);
                        }
                        smartDevListAdapter.notifyDataSetChanged();
                    }
                    devAdapter.notifyDataSetChanged();
                    Log.i(TAG, "mDeviceList.getDevice().size=" + aDeviceList.getDevice().size());
                    try {
                        new AlertDialog
                                .Builder(DevListActivity.this)
                                .setTitle("设备")
                                .setNegativeButton("确定", null)
                                .setIcon(android.R.drawable.ic_menu_agenda)
                                .setMessage(str)
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_GET_SET_RESULT:
                    try {
                        new AlertDialog
                                .Builder(DevListActivity.this)
                                .setTitle("设置结果")
                                .setNegativeButton("确定", null)
                                .setIcon(android.R.drawable.ic_menu_agenda)
                                .setMessage("" + str)
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_TOAST:
                    Toast.makeText(DevListActivity.this, str, Toast.LENGTH_SHORT).show();
                    break;

            }


        }
    };
    private boolean isReceiverDevList;
    private boolean isReceiverOptionsSet;


    private String AuthPwd;
    private String UserID;
    private String ManagePasswd;
    private String LimitedTime;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (NetStatusUtil.isWiFiActive(DevListActivity.this)) {
                            //查询设备
                            packet = new GeneralPacket(DevListActivity.this);
                            //探测设备
                            QueryOptions queryCmd = new QueryOptions();
                            queryCmd.setOP("QUERY");
                            queryCmd.setMethod("DevList");
                            Gson gson = new Gson();
                            String text = gson.toJson(queryCmd);
                            packet.packQueryDevListData(null, text.getBytes());

                            isReceiverDevList = false;
                            localConnectmanager.getOut(packet.data);

                        } else {
                            Message msg = Message.obtain();
                            msg.what = MSG_TOAST;
                            msg.obj = "wifi未连接";
                            mHandler.sendMessage(msg);
                        }

                    }
                }).start();
                break;
            case R.id.btn_once_auth:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetStatusUtil.isWiFiActive(DevListActivity.this)) {
                            //查询设备
                            packet = new GeneralPacket(DevListActivity.this);
                            //探测设备
                            QueryOptions queryCmd = new QueryOptions();
                            queryCmd.setOP("SET");
                            queryCmd.setMethod("SmartLock");
                            String smartuid;
                            SharedPreference sharedPreference = new SharedPreference(DevListActivity.this, "smartuid");
                            smartuid = sharedPreference.getString("smartuid");
                            queryCmd.setSmartUid(smartuid);
                            queryCmd.setCommand("Once");
                            AuthPwd = edittext_AuthPwd.getText().toString();
                            UserID = edittext_UserID.getText().toString();
                            ManagePasswd = edittext_ManagePasswd.getText().toString();
                            LimitedTime = edittext_LimitedTime.getText().toString();
                            if (AuthPwd.equals("")) {
                                queryCmd.setAuthPwd("001");
                            } else {
                                queryCmd.setAuthPwd(AuthPwd);
                            }
                            if (UserID.equals("")) {
                                queryCmd.setUserID("003");
                            } else {
                                queryCmd.setUserID(UserID);
                            }
                            if (ManagePasswd.equals("")) {
                                queryCmd.setManagePasswd("123456");
                            } else {
                                queryCmd.setManagePasswd(ManagePasswd);
                            }
                            if (LimitedTime.equals("")) {
                                queryCmd.setLimitedTime("30");
                            } else {
                                queryCmd.setLimitedTime(LimitedTime);
                            }

                            Gson gson = new Gson();
                            String text = gson.toJson(queryCmd);
                            packet.packSetSmartLockData(null, text.getBytes());
                            localConnectmanager.getOut(packet.data);
                           /* while (!isReceiverOptionsSet) {
                                getIn();
                            }
*/
                        } else {
                            Message msg = Message.obtain();
                            msg.what = MSG_TOAST;
                            msg.obj = "wifi未连接";
                            mHandler.sendMessage(msg);
                            Log.i(TAG, "wifi未连接");
                        }


                    }
                }).start();
                break;
            case R.id.btn_time_limit_auth:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetStatusUtil.isWiFiActive(DevListActivity.this)) {
//查询设备
                            packet = new GeneralPacket(DevListActivity.this);
                            //探测设备
                            QueryOptions queryCmd = new QueryOptions();
                            queryCmd.setOP("SET");
                            queryCmd.setMethod("SmartLock");
                            String smartuid;
                            SharedPreference sharedPreference = new SharedPreference(DevListActivity.this, "smartuid");
                            smartuid = sharedPreference.getString("smartuid");
                            queryCmd.setSmartUid(smartuid);
                            queryCmd.setCommand("Time-limited");
                            AuthPwd = edittext_AuthPwd.getText().toString();
                            UserID = edittext_UserID.getText().toString();
                            ManagePasswd = edittext_ManagePasswd.getText().toString();
                            LimitedTime = edittext_LimitedTime.getText().toString();
                            if (AuthPwd.equals("")) {
                                queryCmd.setAuthPwd("001");
                            } else {
                                queryCmd.setAuthPwd(AuthPwd);
                            }
                            if (UserID.equals("")) {
                                queryCmd.setUserID("003");
                            } else {
                                queryCmd.setUserID(UserID);
                            }
                            if (ManagePasswd.equals("")) {
                                queryCmd.setManagePasswd("123456");
                            } else {
                                queryCmd.setManagePasswd(ManagePasswd);
                            }
                            if (LimitedTime.equals("")) {
                                queryCmd.setLimitedTime("30");
                            } else {
                                queryCmd.setLimitedTime(LimitedTime);
                            }
                            Gson gson = new Gson();
                            String text = gson.toJson(queryCmd);
                            packet.packSetSmartLockData(null, text.getBytes());
                            localConnectmanager.getOut(packet.data);
                        } else {
                            Message msg = Message.obtain();
                            msg.what = MSG_TOAST;
                            msg.obj = "wifi未连接";
                            mHandler.sendMessage(msg);
                        }


                    }
                }).start();
                break;
            case R.id.btn_forever_auth:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetStatusUtil.isWiFiActive(DevListActivity.this)) {
                            //查询设备
                            packet = new GeneralPacket(DevListActivity.this);
                            //探测设备
                            QueryOptions queryCmd = new QueryOptions();
                            queryCmd.setOP("SET");
                            queryCmd.setMethod("SmartLock");
                            String smartuid;
                            SharedPreference sharedPreference = new SharedPreference(DevListActivity.this, "smartuid");
                            smartuid = sharedPreference.getString("smartuid");
                            queryCmd.setSmartUid(smartuid);
                            queryCmd.setCommand("Perpetual");

                            AuthPwd = edittext_AuthPwd.getText().toString();
                            UserID = edittext_UserID.getText().toString();
                            ManagePasswd = edittext_ManagePasswd.getText().toString();
                            LimitedTime = edittext_LimitedTime.getText().toString();
                            if (AuthPwd.equals("")) {
                                queryCmd.setAuthPwd("001");
                            } else {
                                queryCmd.setAuthPwd(AuthPwd);
                            }
                            if (UserID.equals("")) {
                                queryCmd.setUserID("003");
                            } else {
                                queryCmd.setUserID(UserID);
                            }
                            if (ManagePasswd.equals("")) {
                                queryCmd.setManagePasswd("123456");
                            } else {
                                queryCmd.setManagePasswd(ManagePasswd);
                            }
                            if (LimitedTime.equals("")) {
                                queryCmd.setLimitedTime("30");
                            } else {
                                queryCmd.setLimitedTime(LimitedTime);
                            }
                            Gson gson = new Gson();
                            String text = gson.toJson(queryCmd);
                            packet.packSetSmartLockData(null, text.getBytes());
                            localConnectmanager.getOut(packet.data);
                        } else {
                            Message msg = Message.obtain();
                            msg.what = MSG_TOAST;
                            msg.obj = "wifi未连接";
                            mHandler.sendMessage(msg);
                            Log.i(TAG, "wifi未连接");
                        }


                    }
                }).start();
                break;
            case R.id.btn_open_door:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (NetStatusUtil.isWiFiActive(DevListActivity.this)) {
                            //查询设备
                            packet = new GeneralPacket(DevListActivity.this);
                            //探测设备
                            QueryOptions queryCmd = new QueryOptions();
                            queryCmd.setOP("SET");
                            queryCmd.setMethod("SmartLock");
                            String smartuid;
                            SharedPreference sharedPreference = new SharedPreference(DevListActivity.this, "smartuid");
                            smartuid = sharedPreference.getString("smartuid");
                            queryCmd.setSmartUid(smartuid);
                            queryCmd.setCommand("Open");
                            AuthPwd = edittext_AuthPwd.getText().toString();
                            UserID = edittext_UserID.getText().toString();
                            ManagePasswd = edittext_ManagePasswd.getText().toString();
                            LimitedTime = edittext_LimitedTime.getText().toString();
                            if (AuthPwd.equals("")) {
                                queryCmd.setAuthPwd("001");
                            } else {
                                queryCmd.setAuthPwd(AuthPwd);
                            }
                            if (UserID.equals("")) {
                                queryCmd.setUserID("003");
                            } else {
                                queryCmd.setUserID(UserID);
                            }
                            if (ManagePasswd.equals("")) {
                                queryCmd.setManagePasswd("123456");
                            } else {
                                queryCmd.setManagePasswd(ManagePasswd);
                            }
                            if (LimitedTime.equals("")) {
                                queryCmd.setLimitedTime("30");
                            } else {
                                queryCmd.setLimitedTime(LimitedTime);
                            }
                            Gson gson = new Gson();
                            String text = gson.toJson(queryCmd);
                            packet.packSetSmartLockData(null, text.getBytes());
                            localConnectmanager.getOut(packet.data);
                        } else {
                            Message msg = Message.obtain();
                            msg.what = MSG_TOAST;
                            msg.obj = "wifi未连接";
                            mHandler.sendMessage(msg);
                        }


                    }
                }).start();
                break;
        }
    }


    @Override
    public void onReciveLocalConnectePacket(byte[] packet) {

    }

    @Override
    public void handshakeCompleted() {

    }

    @Override
    public void createSocketFailed(String msg) {

    }

    @Override
    public void OnFailedgetLocalGW(String msg) {

    }

    @Override
    public void OnGetUid(String uid) {

    }

    @Override
    public void OnGetQueryresult(String devList) {
        if(devList.contains("DevList")){
            Message msg = Message.obtain();
            msg.what = MSG_GET_DEVS;
            msg.obj = devList;
            System.out.println("mHandler.sendMessage(isReceiverDevList)");
            mHandler.sendMessage(msg);
        }

    }

    @Override
    public void OnGetSetresult(String setResult) {
        Message msg = Message.obtain();
        msg.what = MSG_GET_SET_RESULT;
        msg.obj = setResult;
        mHandler.sendMessage(msg);
    }



    @Override
    public void wifiConnectUnReachable() {
        Message msg = Message.obtain();
        msg.what = MSG_GET_SET_RESULT;
        msg.obj = "WiFi连接不可用";
        mHandler.sendMessage(msg);
    }
}
