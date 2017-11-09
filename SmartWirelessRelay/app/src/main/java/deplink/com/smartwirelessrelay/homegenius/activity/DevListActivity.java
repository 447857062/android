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
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.DevListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.SmartDevListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetStatusUtil;


public class DevListActivity extends Activity implements View.OnClickListener, SmartLockListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_list);
        initViews();
        initData();
    }

    private SmartLockManager mSmartLockManager;

    private void initData() {
        mDeviceList = new ArrayList<>();
        mSmartDev = new ArrayList<>();
        devAdapter = new DevListAdapter(this, mDeviceList);
        smartDevListAdapter = new SmartDevListAdapter(this, mSmartDev);
        dev_list.setAdapter(devAdapter);
        smart_dev_list.setAdapter(smartDevListAdapter);
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this, this);
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
                    mDeviceList.clear();
                    mDeviceList.addAll(aDeviceList.getDevice());
                    mSmartDev.clear();
                    mSmartDev.addAll(aDeviceList.getSmartDev());
                    if (aDeviceList.getSmartDev() != null && aDeviceList.getSmartDev().size() > 0) {
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


    private String AuthPwd;
    private String UserID;
    private String ManagePasswd;
    private String LimitedTime;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click:
                mSmartLockManager.queryDeviceList();
                break;
            case R.id.btn_once_auth:
                if (NetStatusUtil.isWiFiActive(DevListActivity.this)) {
                    //查询设备
                    AuthPwd = edittext_AuthPwd.getText().toString();
                    UserID = edittext_UserID.getText().toString();
                    ManagePasswd = edittext_ManagePasswd.getText().toString();
                    LimitedTime = edittext_LimitedTime.getText().toString();
                    if (AuthPwd.equals("")) {
                        AuthPwd = "001";
                    }
                    if (UserID.equals("")) {
                        UserID = "003";
                    }
                    if (ManagePasswd.equals("")) {
                        ManagePasswd = "123456";
                    }
                    if (LimitedTime.equals("")) {
                        LimitedTime = "30";
                    }
                    mSmartLockManager.setSmaertLockParmars("Once", UserID, ManagePasswd, AuthPwd, LimitedTime);


                } else {
                    Message msg = Message.obtain();
                    msg.what = MSG_TOAST;
                    msg.obj = "wifi未连接";
                    mHandler.sendMessage(msg);
                    Log.i(TAG, "wifi未连接");
                }
                break;
            case R.id.btn_time_limit_auth:
                if (NetStatusUtil.isWiFiActive(DevListActivity.this)) {
//查询设备


                    AuthPwd = edittext_AuthPwd.getText().toString();
                    UserID = edittext_UserID.getText().toString();
                    ManagePasswd = edittext_ManagePasswd.getText().toString();
                    LimitedTime = edittext_LimitedTime.getText().toString();
                    if (AuthPwd.equals("")) {
                        AuthPwd = ("001");
                    }
                    if (UserID.equals("")) {
                        UserID = ("003");
                    }
                    if (ManagePasswd.equals("")) {
                        ManagePasswd = ("123456");
                    }
                    if (LimitedTime.equals("")) {
                        LimitedTime = ("30");
                    }

                    mSmartLockManager.setSmaertLockParmars("Time-limited", UserID, ManagePasswd, AuthPwd, LimitedTime);
                } else {
                    Message msg = Message.obtain();
                    msg.what = MSG_TOAST;
                    msg.obj = "wifi未连接";
                    mHandler.sendMessage(msg);
                }
                break;
            case R.id.btn_forever_auth:
                if (NetStatusUtil.isWiFiActive(DevListActivity.this)) {


                    AuthPwd = edittext_AuthPwd.getText().toString();
                    UserID = edittext_UserID.getText().toString();
                    ManagePasswd = edittext_ManagePasswd.getText().toString();
                    LimitedTime = edittext_LimitedTime.getText().toString();
                    if (AuthPwd.equals("")) {
                        AuthPwd = ("001");
                    }
                    if (UserID.equals("")) {
                        UserID = ("003");
                    }
                    if (ManagePasswd.equals("")) {
                        ManagePasswd = ("123456");
                    }
                    if (LimitedTime.equals("")) {
                        LimitedTime = ("30");
                    }
                    mSmartLockManager.setSmaertLockParmars("Perpetual", UserID, ManagePasswd, AuthPwd, LimitedTime);
                } else {
                    Message msg = Message.obtain();
                    msg.what = MSG_TOAST;
                    msg.obj = "wifi未连接";
                    mHandler.sendMessage(msg);
                    Log.i(TAG, "wifi未连接");
                }
                break;
            case R.id.btn_open_door:
                if (NetStatusUtil.isWiFiActive(DevListActivity.this)) {

                    AuthPwd = edittext_AuthPwd.getText().toString();
                    UserID = edittext_UserID.getText().toString();
                    ManagePasswd = edittext_ManagePasswd.getText().toString();
                    LimitedTime = edittext_LimitedTime.getText().toString();
                    if (AuthPwd.equals("")) {
                        AuthPwd = ("001");
                    }
                    if (UserID.equals("")) {
                        UserID = ("003");
                    }
                    if (ManagePasswd.equals("")) {
                        ManagePasswd = ("123456");
                    }
                    if (LimitedTime.equals("")) {
                        LimitedTime = ("30");
                    }
                    mSmartLockManager.setSmaertLockParmars("Open", UserID, ManagePasswd, AuthPwd, LimitedTime);
                } else {
                    Message msg = Message.obtain();
                    msg.what = MSG_TOAST;
                    msg.obj = "wifi未连接";
                    mHandler.sendMessage(msg);
                }
                break;
        }
    }


    @Override
    public void responseQueryResult(String result) {
        if (result.contains("DevList")) {
            Message msg = Message.obtain();
            msg.what = MSG_GET_DEVS;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void responseSetResult(String result) {
        Message msg = Message.obtain();
        msg.what = MSG_GET_SET_RESULT;
        msg.obj = result;
        mHandler.sendMessage(msg);
    }

    @Override
    public void responseBind(String result) {

    }
}
