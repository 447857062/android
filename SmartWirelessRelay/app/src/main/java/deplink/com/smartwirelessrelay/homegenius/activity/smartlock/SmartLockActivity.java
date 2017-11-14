package deplink.com.smartwirelessrelay.homegenius.activity.smartlock;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.ManagerPassword;
import deplink.com.smartwirelessrelay.homegenius.activity.smartlock.alarmhistory.AlarmHistoryActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.smartlock.lockhistory.LockHistoryActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.SmartLockConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.AuthoriseDialog;

public class SmartLockActivity extends Activity implements View.OnClickListener, SmartLockListener, AuthoriseDialog.GetDialogAuthtTypeTimeListener {
    private static final String TAG = "SmartLockActivity";
    private Button button_alert_record;
    private Button button_open_lock_record;
    private Button button_no_save_password;
    private Button button_authorise;
    private Button button_open;

    private SmartLockManager mSmartLockManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_lock);
        initViews();
        initDatas();
        initEvents();
    }

    private boolean saveManagetPassword;
    private String savedManagePassword;

    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this, this);
        if (db == null) {
            db = Connector.getDatabase();
        }
        //TODO
        ManagerPassword managerPassword = null;
        try {
            managerPassword = DataSupport.findFirst(ManagerPassword.class);
            if (!managerPassword.isRemenbEnable()) {
                saveManagetPassword = false;
            } else {
                savedManagePassword = managerPassword.getManagerPassword();
                saveManagetPassword = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initEvents() {
        button_alert_record.setOnClickListener(this);
        button_open_lock_record.setOnClickListener(this);
        button_no_save_password.setOnClickListener(this);
        button_authorise.setOnClickListener(this);
        button_open.setOnClickListener(this);
    }

    private void initViews() {
        button_alert_record = (Button) findViewById(R.id.button_alert_record);
        button_open_lock_record = (Button) findViewById(R.id.button_open_lock_record);
        button_no_save_password = (Button) findViewById(R.id.button_no_save_password);
        button_authorise = (Button) findViewById(R.id.button_authorise);
        button_open = (Button) findViewById(R.id.button_open);
    }

    private AuthoriseDialog mAuthoriseDialog;
    private SQLiteDatabase db;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_alert_record:
                startActivity(new Intent(this, AlarmHistoryActivity.class));
                break;
            case R.id.button_open_lock_record:
                startActivity(new Intent(SmartLockActivity.this, LockHistoryActivity.class));
                break;
            case R.id.button_no_save_password:
                break;
            case R.id.button_authorise:
                mAuthoriseDialog = new AuthoriseDialog(this);
                mAuthoriseDialog.setGetDialogAuthtTypeTimeListener(this);
                mAuthoriseDialog.show();
                break;
            case R.id.button_open:

                //TODO
                //  if(saveManagetPassword){
                savedManagePassword = "123456";
                mSmartLockManager.setSmaertLockParmars(SmartLockConstant.OPEN_LOCK, "003", savedManagePassword, null, null);
                // }else{
                //   startActivity(new Intent(this, SetLockPwdActivity.class));
                // }
                break;
        }
    }

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseSetResult(String result) {
        mHandler.sendEmptyMessage(MSG_SHOW_TOAST);
    }

    @Override
    public void responseBind(String result) {

    }

    private static final int MSG_SHOW_TOAST = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SHOW_TOAST:
                    Toast.makeText(SmartLockActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onGetDialogAuthtTypeTime(String authType, String password, String limitTime) {
        Log.i(TAG, "authType=" + authType);
        switch (authType) {
            case SmartLockConstant.AUTH_TYPE_ONCE:
                mSmartLockManager.setSmaertLockParmars(SmartLockConstant.AUTH_TYPE_ONCE, "003", "123456", password, null);
                break;
            case SmartLockConstant.AUTH_TYPE_PERPETUAL:
                mSmartLockManager.setSmaertLockParmars(SmartLockConstant.AUTH_TYPE_PERPETUAL, "003", "123456", password, null);
                break;
            case SmartLockConstant.AUTH_TYPE_TIME_LIMIT:
                long hour = Long.valueOf(limitTime);
                limitTime = String.valueOf(hour * 60 * 1000);
                Log.i(TAG, "hour=" + hour + "limittime=" + limitTime);
                mSmartLockManager.setSmaertLockParmars(SmartLockConstant.AUTH_TYPE_PERPETUAL, "003", "123456", password, limitTime);
                //TODO
                break;
        }
    }
}
