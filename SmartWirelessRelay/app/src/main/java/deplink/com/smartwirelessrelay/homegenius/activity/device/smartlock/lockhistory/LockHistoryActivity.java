package deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.lockhistory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.LockHistorys;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.UpdateSmartLockUserIdActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;

/**
 * 开锁记录界面
 */
public class LockHistoryActivity extends Activity implements SmartLockListener,View.OnClickListener{
    private static final String TAG = "LockHistory";
    private ListView dev_list;
    private List<deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.LockHistory> mRecordList;
    private LockHistoryAdapter recordAdapter;
    private SmartLockManager mSmartLockManager;
    private ImageView imageview_back;
    private TextView textview_update_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_history);
        initViews();
        initData();
        initEvents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSmartLockManager.removeSmartLockListener(this);
    }

    private void initEvents() {
        dev_list.setAdapter(recordAdapter);
        textview_update_id.setOnClickListener(this);
        imageview_back.setOnClickListener(this);
    }

    private void initData() {
        mRecordList = new ArrayList<>();
        recordAdapter = new LockHistoryAdapter(this, mRecordList);
    }

    private void initViews() {
        dev_list = (ListView) findViewById(R.id.list_lock_histroy);
        imageview_back = (ImageView) findViewById(R.id.imageview_back);
        textview_update_id = (TextView) findViewById(R.id.textview_update_id);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this);
        mSmartLockManager.addSmartLockListener(this);
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
                        Log.i(TAG, "历史记录长度=" + aDeviceList.getRecord().size());
                        mRecordList.addAll(aDeviceList.getRecord());
                        recordAdapter.notifyDataSetChanged();
                    }

                    try {
                        new AlertDialog
                                .Builder(LockHistoryActivity.this)
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
                                .Builder(LockHistoryActivity.this)
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageview_back:
                onBackPressed();
                break;
            case R.id.textview_update_id:
                startActivity(new Intent(LockHistoryActivity.this,UpdateSmartLockUserIdActivity.class));
                break;
        }
    }
}
