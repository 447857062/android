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
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Record;
import deplink.com.smartwirelessrelay.homegenius.activity.adapter.RecordListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;

public class LockHistory extends Activity implements SmartLockListener {
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

    }

    private void initViews() {
        dev_list = (ListView) findViewById(R.id.list_lock_histroy);

    }


    private Thread queryThread;
    private SmartLockManager mSmartLockManager;

    @Override
    protected void onResume() {
        super.onResume();
        mSmartLockManager=SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this,this);
        mSmartLockManager.queryLockHistory();
    }

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
    public void responseQueryResult(String result) {
        Message msg = Message.obtain();
        msg.obj = result;
        if (result.contains("SmartLock-HisRecord")) {
            msg.what = MSG_GET_HISTORYRECORD;
        } else if (result.contains("Result")) {
            msg.what = MSG_RETURN_ERROR;
        }
        mHandler.sendMessage(msg);
    }

    @Override
    public void responseSetResult(String result) {

    }

    @Override
    public void responseBind(String result) {

    }
}
