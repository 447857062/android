package com.deplink.homegenius.activity.device.smartlock.lockhistory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.Protocol.json.device.lock.LockHistorys;
import com.deplink.homegenius.Protocol.json.device.lock.Record;
import com.deplink.homegenius.activity.device.smartlock.userid.UpdateSmartLockUserIdActivity;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.smartlock.SmartLockListener;
import com.deplink.homegenius.manager.device.smartlock.SmartLockManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * 开锁记录界面
 */
public class LockHistoryActivity extends Activity implements SmartLockListener, View.OnClickListener {
    private static final String TAG = "LockHistory";
    private ListView dev_list;
    private List<Record> mRecordList;
    private LockHistoryAdapter recordAdapter;
    private SmartLockManager mSmartLockManager;
    private boolean isStartFromExperience;
    private TextView textview_edit;
    private TextView textview_title;
    private TextView textview_empty_record;
    private FrameLayout image_back;
    private ImageView imageview_no_lockhostory;
    private boolean isLogin;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;
    private DeviceManager mDeviceManager;
    private int Query_Num;
    private int Total;
    private ArrayList<String> mRecordListId;
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
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initData() {
        textview_title.setText("开锁记录");
        textview_edit.setText("修改ID名称");
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this);
        isStartFromExperience = mDeviceManager.isStartFromExperience();
        mRecordList = new ArrayList<>();
        recordAdapter = new LockHistoryAdapter(this, mRecordList);
        connectLostDialog = new MakeSureDialog(LockHistoryActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(LockHistoryActivity.this, LoginActivity.class));
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
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }
        };
    }

    private void initViews() {
        dev_list = findViewById(R.id.list_lock_histroy);
        textview_edit = findViewById(R.id.textview_edit);
        textview_title = findViewById(R.id.textview_title);
        textview_empty_record = findViewById(R.id.textview_empty_record);
        image_back = findViewById(R.id.image_back);
        imageview_no_lockhostory = findViewById(R.id.imageview_no_lockhostory);

    }


    @Override
    protected void onResume() {
        super.onResume();
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this);
        mSmartLockManager.addSmartLockListener(this);
        isLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        isStartFromExperience = mDeviceManager.isStartFromExperience();
        manager.addEventCallback(ec);
        if (isStartFromExperience) {
            mRecordList.clear();
            Record temp = new Record();
            temp.setTime("2017-11-23 12:35:23");
            temp.setUserID("001");
            mRecordList.add(temp);
            temp = new Record();
            temp.setTime("2017-11-24 12:35:23");
            temp.setUserID("002");
            mRecordList.add(temp);
            temp = new Record();
            temp.setTime("2017-11-25 12:35:23");
            temp.setUserID("003");
            mRecordList.add(temp);
            temp = new Record();
            temp.setTime("2017-11-26 12:35:23");
            temp.setUserID("004");
            mRecordList.add(temp);
        } else {
            mSmartLockManager.queryLockStatu();
            DialogThreeBounce.setmContext(this);
            DialogThreeBounce.showLoading(this);
        }

    }

    private static final int MSG_GET_HISTORYRECORD = 0x01;
    private static final int MSG_RETURN_ERROR = 0x02;
    private static final int MSG_GET_HISRECORD = 0x03;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            switch (msg.what) {
                case MSG_GET_HISTORYRECORD:
                    Gson gson = new Gson();
                    LockHistorys aDeviceList = gson.fromJson(str, LockHistorys.class);
                    Log.i(TAG, "历史记录长度=" + aDeviceList.getRecord().size());
                    mRecordList.clear();
                    mRecordList.addAll(aDeviceList.getRecord());
                    mRecordListId = new ArrayList<>();
                    for (int i = 0; i < mRecordList.size(); i++) {
                        if (!mRecordListId.contains(mRecordList.get(i).getUserID())) {
                            mRecordListId.add(mRecordList.get(i).getUserID());
                        }
                    }
                    textview_empty_record.setVisibility(View.GONE);
                    imageview_no_lockhostory.setVisibility(View.GONE);
                    Log.i(TAG, "mRecordListId=" + mRecordListId.size());
                    recordAdapter.notifyDataSetChanged();
                    DialogThreeBounce.hideLoading();
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
                case MSG_GET_HISRECORD:
                    if (mRecordList.size() == 0) {
                        imageview_no_lockhostory.setVisibility(View.VISIBLE);
                        textview_empty_record.setVisibility(View.VISIBLE);
                    }
                    DialogThreeBounce.hideLoading();
                    break;
            }
        }
    };

    @Override
    public void responseQueryResult(String result) {
        Message msg = Message.obtain();
        msg.obj = result;
        if (result.contains("HisRecord")) {
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
    public void responseLockStatu(int RecondNum, int LockStatus) {
        if (LocalConnectmanager.getInstance().isLocalconnectAvailable()) {
            Message msg = Message.obtain();
            msg.what = MSG_GET_HISRECORD;
            mHandler.sendMessageDelayed(msg, 3000);
            mSmartLockManager.queryLockHistory(true, RecondNum);
        } else {
            if (isLogin) {
                DialogThreeBounce.setmContext(this);
                DialogThreeBounce.showLoading(this);
                Message msg = Message.obtain();
                msg.what = MSG_GET_HISRECORD;
                mHandler.sendMessageDelayed(msg, 3000);
                mSmartLockManager.queryLockHistory(false, RecondNum);
            } else {
                Toast.makeText(this, "未登录,并且本地网关也不可用", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                if (isStartFromExperience) {
                    Intent intent = new Intent(LockHistoryActivity.this, UpdateSmartLockUserIdActivity.class);
                    startActivity(intent);
                } else {
                    if (mRecordListId != null) {
                        Intent intent = new Intent(LockHistoryActivity.this, UpdateSmartLockUserIdActivity.class);
                        intent.putStringArrayListExtra("recordlistid", mRecordListId);
                        startActivity(intent);
                    } else {
                        ToastSingleShow.showText(this, "未获取到开锁记录");
                    }
                }
                break;
        }
    }
}
