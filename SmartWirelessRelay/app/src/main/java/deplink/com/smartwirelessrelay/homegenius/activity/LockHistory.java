package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.LockHistorys;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Record;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.RecordListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import deplink.com.smartwirelessrelay.homegenius.util.SharedPreference;

public class LockHistory extends Activity implements LocalConnecteListener{
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
        localConnectmanager=LocalConnectmanager.getInstance();
        localConnectmanager.InitLocalConnectManager(this, this);
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
                       localConnectmanager.getOut(packet.data);
                    }
                });
        queryThread.start();
    }
    private LocalConnectmanager localConnectmanager;
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
        Message msg = Message.obtain();
        msg.obj = devList;
        if (devList.contains("SmartLock-HisRecord")) {
            msg.what = MSG_GET_HISTORYRECORD;
        } else if (devList.contains("Result")) {
            msg.what = MSG_RETURN_ERROR;
        }
        mHandler.sendMessage(msg);
    }

    @Override
    public void OnGetSetresult(String setResult) {

    }

    @Override
    public void wifiConnectUnReachable() {

    }
}
