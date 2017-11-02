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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import deplink.com.smartwirelessrelay.homegenius.Devices.EllESDK;
import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.DevListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.SmartDevListAdapter;
import deplink.com.smartwirelessrelay.homegenius.util.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;
import deplink.com.smartwirelessrelay.homegenius.util.SharedPreference;
import deplink.com.smartwirelessrelay.homegenius.util.WifiConnected;


public class DevListActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "DevListActivity";
    private SSLSocket Client_sslSocket = null;
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
    private EditText edittext_manager_password;
    private EditText edittext_auth_time;

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
        edittext_manager_password = (EditText) findViewById(R.id.edittext_manager_password);
        edittext_auth_time = (EditText) findViewById(R.id.edittext_auth_time);
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

    public void getIn(SSLSocket socket) {
        String str;
        if (null != Client_sslSocket) {
            try {
                InputStream input = socket.getInputStream();
                byte[] buf = new byte[1024];
                int len = input.read(buf);
                System.out.println("received:" + "len=" + len);
                if (len != -1 && len > AppConstant.BASICLEGTH) {
                    byte[] lengthByte = new byte[2];
                    System.arraycopy(buf, AppConstant.PACKET_DATA_LENGTH_START_INDEX, lengthByte, 0, 2);
                    int length = DataExchange.bytesToInt(lengthByte, 0, 2);
                    System.out.println("received:" + "length=" + length);
                    str = new String(buf, AppConstant.BASICLEGTH, length);
                    System.out.println("received:" + str);
                    Message msg = Message.obtain();
                    if (str.contains("DevList")) {
                        isReceiverDevList = true;
                        msg.what = MSG_GET_DEVS;
                        msg.obj = str;
                        mHandler.sendMessage(msg);
                    } else {
                        //
                        isReceiverOptionsSet = true;
                        Gson gson = new Gson();
                        QueryOptions optionResult = gson.fromJson(str, QueryOptions.class);
                        String resultStr = optionResult.getResult();
                        msg.what = MSG_GET_SET_RESULT;
                        msg.obj = resultStr;
                        mHandler.sendMessage(msg);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private String managerPassword;
    private String authTime;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (WifiConnected.isNetworkAvailable(DevListActivity.this)) {
                            //查询设备
                            GeneralPacket packet = new GeneralPacket(DevListActivity.this);
                            //探测设备
                            QueryOptions queryCmd = new QueryOptions();
                            queryCmd.setOP("QUERY");
                            queryCmd.setMethod("DevList");
                            Gson gson = new Gson();
                            String text = gson.toJson(queryCmd);
                            packet.packQueryDevListData(null, text.getBytes());
                            if (null == Client_sslSocket) {
                               // EllESDK.getInstance().InitEllESDK(DevListActivity.this,null);
                                EllESDK.getInstance().InitTcpIpConnect(null);
                                Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                            }
                            Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                            isReceiverDevList = false;
                            EllESDK.getInstance().getOut(packet.data);
                            while (!isReceiverDevList) {
                                getIn(Client_sslSocket);
                            }

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
            case R.id.btn_once_auth:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (WifiConnected.isNetworkAvailable(DevListActivity.this)) {
                            //查询设备
                            final GeneralPacket packet = new GeneralPacket(DevListActivity.this);
                            //探测设备
                            QueryOptions queryCmd = new QueryOptions();
                            queryCmd.setOP("SET");
                            queryCmd.setMethod("SmartLock");
                            String smartuid;
                            SharedPreference sharedPreference = new SharedPreference(DevListActivity.this, "smartuid");
                            smartuid = sharedPreference.getString("smartuid");
                            queryCmd.setSmartUid(smartuid);
                            queryCmd.setCommand("Once");

                            queryCmd.setAuthPwd("1111111");
                            queryCmd.setUserID("003");
                            queryCmd.setManagePasswd("123456");
                            managerPassword = edittext_manager_password.getText().toString();
                            authTime = edittext_auth_time.getText().toString();
                            Log.i(TAG, "managerPassword=" + managerPassword + "authTime=" + authTime);
                            if (!managerPassword.equals("") && !authTime.equals("")) {
                                queryCmd.setAuthPwd(managerPassword);
                                queryCmd.setLimitedTime(authTime);
                            } else {
                                queryCmd.setAuthPwd("001");
                                queryCmd.setLimitedTime("1");
                                Message msg = Message.obtain();
                                msg.what = MSG_TOAST;
                                msg.obj = "请输入管理密码，授权时间";
                                mHandler.sendMessage(msg);
                            }
                            Gson gson = new Gson();
                            String text = gson.toJson(queryCmd);
                            packet.packSetSmartLockData(null, text.getBytes());
                            if (Client_sslSocket != null) {

                            } else {
                              //  EllESDK.getInstance().InitEllESDK(DevListActivity.this,null);
                                EllESDK.getInstance().InitTcpIpConnect(null);
                                Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                            }
                            isReceiverOptionsSet = false;
                            EllESDK.getInstance().getOut(packet.data);
                            while (!isReceiverOptionsSet) {
                                getIn(Client_sslSocket);
                            }

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
                        if (WifiConnected.isNetworkAvailable(DevListActivity.this)) {
//查询设备
                            final GeneralPacket packet = new GeneralPacket(DevListActivity.this);
                            //探测设备
                            QueryOptions queryCmd = new QueryOptions();
                            queryCmd.setOP("SET");
                            queryCmd.setMethod("SmartLock");
                            String smartuid;
                            SharedPreference sharedPreference = new SharedPreference(DevListActivity.this, "smartuid");
                            smartuid = sharedPreference.getString("smartuid");
                            queryCmd.setSmartUid(smartuid);
                            queryCmd.setCommand("Time-limited");
                            queryCmd.setUserID("003");
                            queryCmd.setManagePasswd("123456");
                            managerPassword = edittext_manager_password.getText().toString();
                            authTime = edittext_auth_time.getText().toString();
                            if (!managerPassword.equals("") && !authTime.equals("")) {
                                queryCmd.setAuthPwd(managerPassword);
                                queryCmd.setLimitedTime(authTime);
                            } else {
                                queryCmd.setAuthPwd("023456");
                                queryCmd.setLimitedTime("1");
                                Message msg = Message.obtain();

                                msg.obj = "请输入管理密码，授权时间";
                                msg.what = MSG_TOAST;
                                mHandler.sendMessage(msg);
                            }
                            Gson gson = new Gson();
                            String text = gson.toJson(queryCmd);
                            packet.packSetSmartLockData(null, text.getBytes());
                            if (Client_sslSocket != null) {

                            } else {
                              //  EllESDK.getInstance().InitTcpIpConnection(null);
                               // EllESDK.getInstance().InitEllESDK(DevListActivity.this,null);
                                EllESDK.getInstance().InitTcpIpConnect(null);
                                Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                            }
                            isReceiverOptionsSet = false;
                            EllESDK.getInstance().getOut(packet.data);
                            while (!isReceiverOptionsSet) {
                                getIn(Client_sslSocket);
                            }
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
            case R.id.btn_forever_auth:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (WifiConnected.isNetworkAvailable(DevListActivity.this)) {
                            //查询设备
                            final GeneralPacket packet = new GeneralPacket(DevListActivity.this);
                            //探测设备
                            QueryOptions queryCmd = new QueryOptions();
                            queryCmd.setOP("SET");
                            queryCmd.setMethod("SmartLock");
                            String smartuid;
                            SharedPreference sharedPreference = new SharedPreference(DevListActivity.this, "smartuid");
                            smartuid = sharedPreference.getString("smartuid");
                            queryCmd.setSmartUid(smartuid);
                            queryCmd.setCommand("Perpetual");

                            queryCmd.setUserID("003");
                            queryCmd.setManagePasswd("123456");

                            queryCmd.setAuthPwd("002301");
                            managerPassword = edittext_manager_password.getText().toString();
                            authTime = edittext_auth_time.getText().toString();
                            if (!managerPassword.equals("") && !authTime.equals("")) {
                                queryCmd.setAuthPwd(managerPassword);
                                queryCmd.setLimitedTime(authTime);
                            } else {
                                queryCmd.setAuthPwd("001");
                                queryCmd.setLimitedTime("1");
                                Message msg = Message.obtain();
                                msg.what = MSG_TOAST;
                                msg.obj = "请输入管理密码，授权时间";
                                mHandler.sendMessage(msg);
                            }
                            Gson gson = new Gson();
                            String text = gson.toJson(queryCmd);
                            packet.packSetSmartLockData(null, text.getBytes());
                            if (Client_sslSocket != null) {

                            } else {
                               // EllESDK.getInstance().InitTcpIpConnection(null);
                              //  EllESDK.getInstance().InitEllESDK(DevListActivity.this,null);
                                EllESDK.getInstance().InitTcpIpConnect(null);
                                Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                            }
                            isReceiverOptionsSet = false;
                            EllESDK.getInstance().getOut(packet.data);
                            while (!isReceiverOptionsSet) {
                                getIn(Client_sslSocket);
                            }
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
                        if (WifiConnected.isNetworkAvailable(DevListActivity.this)) {
                            //查询设备
                            final GeneralPacket packet = new GeneralPacket(DevListActivity.this);
                            //探测设备
                            QueryOptions queryCmd = new QueryOptions();
                            queryCmd.setOP("SET");
                            queryCmd.setMethod("SmartLock");
                            String smartuid;
                            SharedPreference sharedPreference = new SharedPreference(DevListActivity.this, "smartuid");
                            smartuid = sharedPreference.getString("smartuid");
                            queryCmd.setSmartUid(smartuid);
                            queryCmd.setCommand("Open");
                            queryCmd.setUserID("003");
                            queryCmd.setManagePasswd("123456");
                            queryCmd.setAuthPwd("001");
                            managerPassword = edittext_manager_password.getText().toString();
                            authTime = edittext_auth_time.getText().toString();
                            Log.i(TAG, "managerPassword=" + managerPassword + "authTime=" + authTime);
                            if (!managerPassword.equals("") && !authTime.equals("")) {
                                queryCmd.setAuthPwd(managerPassword);
                                queryCmd.setLimitedTime(authTime);
                            } else {
                                queryCmd.setAuthPwd("001");
                                queryCmd.setLimitedTime("1");
                                Message msg = Message.obtain();
                                msg.what = MSG_TOAST;
                                msg.obj = "请输入管理密码，授权时间";
                                mHandler.sendMessage(msg);
                            }
                            Gson gson = new Gson();
                            String text = gson.toJson(queryCmd);
                            packet.packSetSmartLockData(null, text.getBytes());
                            if (Client_sslSocket != null) {

                            } else {
                               // EllESDK.getInstance().InitTcpIpConnection(null);
                               // EllESDK.getInstance().InitEllESDK(DevListActivity.this,null);
                                EllESDK.getInstance().InitTcpIpConnect(null);
                                Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                            }
                            isReceiverOptionsSet = false;
                            EllESDK.getInstance().getOut(packet.data);
                            while (!isReceiverOptionsSet) {
                                getIn(Client_sslSocket);
                            }
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
        }
    }
}
