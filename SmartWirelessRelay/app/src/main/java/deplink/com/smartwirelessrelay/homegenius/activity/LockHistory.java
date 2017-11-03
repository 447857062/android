package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import deplink.com.smartwirelessrelay.homegenius.Devices.ConnectManager;
import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.LockHistorys;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Record;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.RecordListAdapter;
import deplink.com.smartwirelessrelay.homegenius.util.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;
import deplink.com.smartwirelessrelay.homegenius.util.SharedPreference;

public class LockHistory extends Activity {
    private static final String TAG = "LockHistory";
    private ListView dev_list;
    private List<Record> mRecordList;
    private RecordListAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_history);
        initViews();
        initData();
    }

    private void initData() {
        mRecordList = new ArrayList<>();
        recordAdapter = new RecordListAdapter(this, mRecordList);
        dev_list.setAdapter(recordAdapter);
        packet = new GeneralPacket(LockHistory.this);
    }

    private void initViews() {
        dev_list = (ListView) findViewById(R.id.list_lock_histroy);

    }

    private GeneralPacket packet;
    private Thread queryThread;

    @Override
    protected void onResume() {
        super.onResume();
        queryThread =
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        //查询设备
                        //探测设备
                        QueryOptions queryCmd = new QueryOptions();
                        queryCmd.setOP("QUERY");
                        queryCmd.setMethod("SmartLock-HisRecord");
                        String DevUid;
                        SharedPreference sharedPreference = new SharedPreference(LockHistory.this, "smartuid");
                        DevUid = sharedPreference.getString("smartuid");
                        if (DevUid != null && !DevUid.equals("")) {
                            queryCmd.setSmartUid(DevUid);
                        }
                        Gson gson = new Gson();
                        String text = gson.toJson(queryCmd);
                        packet.packQueryRecordListData(null, text.getBytes());
                        if (null==Client_sslSocket) {
                            ConnectManager.getInstance().InitEllESDK(LockHistory.this, null);
                            Client_sslSocket = ConnectManager.getInstance().getClient_sslSocket();
                        }
                        ConnectManager.getInstance().getOut(packet.data);
                        isReceiverHistoryRecord = false;
                        while (!isReceiverHistoryRecord) {
                            getIn(Client_sslSocket);
                        }

                    }
                });
        queryThread.start();
    }

    private SSLSocket Client_sslSocket = null;
    private static final int MSG_GET_HISTORYRECORD = 0x01;
    private static final int MSG_RETURN_ERROR = 0x02;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            switch (msg.what) {
                case MSG_GET_HISTORYRECORD:
                    Gson gson = new Gson();
                    LockHistorys aDeviceList = gson.fromJson(str, LockHistorys.class);
                    mRecordList.clear();
                    if (aDeviceList.getRecord() != null) {
                        mRecordList.addAll(aDeviceList.getRecord());
                        recordAdapter.notifyDataSetChanged();
                    }
                    Log.i(TAG, "历史记录长度=" + aDeviceList.getRecord().size());
                    try {
                        new AlertDialog
                                .Builder(LockHistory.this)
                                .setTitle("设备")
                                .setNegativeButton("确定", null)
                                .setIcon(android.R.drawable.ic_menu_agenda)
                                .setMessage(str)
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_RETURN_ERROR:
                    try {
                        new AlertDialog
                                .Builder(LockHistory.this)
                                .setTitle("错误")
                                .setNegativeButton("确定", null)
                                .setIcon(android.R.drawable.ic_menu_agenda)
                                .setMessage(str)
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    };
    private InputStream input;
    private boolean isReceiverHistoryRecord;

    public void getIn(SSLSocket socket) {
        String str;
        if (null == Client_sslSocket) {
            ConnectManager.getInstance().InitTcpIpConnect(null);
            Client_sslSocket = ConnectManager.getInstance().getClient_sslSocket();
        }
        try {
            input = socket.getInputStream();
            if (input != null) {
                byte[] buf = new byte[1024];
                int len = input.read(buf);
                if (len != -1 && len > AppConstant.BASICLEGTH) {
                    byte[] lengthByte = new byte[AppConstant.PACKET_DATA_LENGTHS_TAKES];
                    System.arraycopy(buf, AppConstant.PACKET_DATA_LENGTH_START_INDEX, lengthByte, 0, 2);
                    int length = DataExchange.bytesToInt(lengthByte, 0, 2);
                    System.out.println("received:" + DataExchange.byteArrayToHexString(buf) + "length=" + length);
                    str = new String(buf, AppConstant.BASICLEGTH, length);
                    System.out.println("received:" + str);
                    Message msg = Message.obtain();
                    msg.obj = str;
                    isReceiverHistoryRecord = true;
                    if (str.contains("SmartLock-HisRecord")) {
                        msg.what = MSG_GET_HISTORYRECORD;
                    } else if (str.contains("Result")) {
                        msg.what = MSG_RETURN_ERROR;
                    }
                    mHandler.sendMessage(msg);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
