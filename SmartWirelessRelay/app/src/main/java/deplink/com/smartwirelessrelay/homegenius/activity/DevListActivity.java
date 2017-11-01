package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;
import deplink.com.smartwirelessrelay.homegenius.util.SharedPreference;


public class DevListActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "DevListActivity";

    private SSLSocket Client_sslSocket = null;
    private boolean close = false; // 关闭连接标志位，true表示关闭，false表示开启

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
        // expan_listview = (ExpandableListView) findViewById(R.id.expandablelv);
        btn.setOnClickListener(this);
        btn_once_auth.setOnClickListener(this);
        btn_time_limit_auth.setOnClickListener(this);
        btn_forever_auth.setOnClickListener(this);
        btn_open_door.setOnClickListener(this);
    }

    private Button btn;

    @Override
    protected void onResume() {
        super.onResume();


    }


    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    public void getOut(SSLSocket socket, byte[] message) {
        OutputStream out;
        Log.i(TAG, "message=" + DataExchange.byteArrayToHexString(message));
        try {
            out = socket.getOutputStream();
            out.write(message);
            Log.e("kelijun", "getOut sent: alert");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int MSG_GET_DEVS = 0x01;
    private static final int MSG_GET_SET_RESULT = 0x02;
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

            }


        }
    };

    public void getIn(SSLSocket socket) {
        String str;
        if (null != Client_sslSocket) {
            try {
                InputStream input = socket.getInputStream();
                byte[] buf = new byte[1024];
                int len = input.read(buf);
                if (len != -1 && len > 84) {
                    byte[] lengthByte = new byte[2];
                    System.arraycopy(buf, 78, lengthByte, 0, 2);
                    int length = DataExchange.bytesToInt(lengthByte, 0, 2);
                    System.out.println("received:" + "length=" + length);
                    str = new String(buf, 84, length);
                    System.out.println("received:" + str);
                    Message msg = Message.obtain();
                    if (str.contains("DevList")) {

                        msg.what = MSG_GET_DEVS;
                        msg.obj = str;
                        mHandler.sendMessage(msg);
                    } else {
                        //
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
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
                            EllESDK.getInstance().InitEllESDK(DevListActivity.this, null);
                            Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                        }
                        Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();

                        EllESDK.getInstance().getOut(packet.data);
                        getIn(Client_sslSocket);
                    }
                }).start();
                break;
            case R.id.btn_once_auth:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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
                        queryCmd.setUserID("12345");
                        queryCmd.setManagePasswd("1");
                        queryCmd.setAuthPwd("001");
                        queryCmd.setLimitedTime("1");
                        Gson gson = new Gson();
                        String text = gson.toJson(queryCmd);
                        packet.packSetSmartLockData(null, text.getBytes());
                        if (Client_sslSocket != null) {

                        } else {
                            EllESDK.getInstance().InitEllESDK(DevListActivity.this, null);
                            Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                        }
                        EllESDK.getInstance().getOut(packet.data);
                        getIn(Client_sslSocket);

                    }
                }).start();
                break;
            case R.id.btn_time_limit_auth:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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

                        queryCmd.setUserID("12345");
                        queryCmd.setManagePasswd("1");


                        queryCmd.setAuthPwd("001");
                        queryCmd.setLimitedTime("1");

                        Gson gson = new Gson();
                        String text = gson.toJson(queryCmd);
                        packet.packSetSmartLockData(null, text.getBytes());
                        if (Client_sslSocket != null) {

                        } else {
                            EllESDK.getInstance().InitEllESDK(DevListActivity.this, null);
                            Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                        }
                        EllESDK.getInstance().getOut(packet.data);
                        getIn(Client_sslSocket);

                    }
                }).start();
                break;
            case R.id.btn_forever_auth:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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

                        queryCmd.setUserID("12345");
                        queryCmd.setManagePasswd("1");


                        queryCmd.setAuthPwd("001");
                        queryCmd.setLimitedTime("1");

                        Gson gson = new Gson();
                        String text = gson.toJson(queryCmd);
                        packet.packSetSmartLockData(null, text.getBytes());
                        if (Client_sslSocket != null) {

                        } else {
                            EllESDK.getInstance().InitEllESDK(DevListActivity.this, null);
                            Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                        }
                        EllESDK.getInstance().getOut(packet.data);
                        getIn(Client_sslSocket);

                    }
                }).start();
                break;
            case R.id.btn_open_door:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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

                        queryCmd.setUserID("12345");
                        queryCmd.setManagePasswd("1");


                        queryCmd.setAuthPwd("001");
                        queryCmd.setLimitedTime("1");

                        Gson gson = new Gson();
                        String text = gson.toJson(queryCmd);
                        packet.packSetSmartLockData(null, text.getBytes());
                        if (Client_sslSocket != null) {

                        } else {
                            EllESDK.getInstance().InitEllESDK(DevListActivity.this, null);
                            Client_sslSocket = EllESDK.getInstance().getClient_sslSocket();
                        }
                        EllESDK.getInstance().getOut(packet.data);
                        getIn(Client_sslSocket);

                    }
                }).start();
                break;
        }
    }

  /*  @Override
    protected void onPause() {
        super.onPause();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Client_sslSocket != null) {
                        Client_sslSocket.close();
                        Client_sslSocket = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }*/
}
