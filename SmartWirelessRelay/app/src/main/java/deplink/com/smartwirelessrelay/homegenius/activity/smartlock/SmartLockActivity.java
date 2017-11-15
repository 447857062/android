package deplink.com.smartwirelessrelay.homegenius.activity.smartlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

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
    private AuthoriseDialog mAuthoriseDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_lock);
        initViews();
        initDatas();
        initEvents();
    }

    /**
     * 测试管理密码，使用密码开门，如果返回成功，就是正确的管理密码
     */
    private boolean saveManagetPassword;
    private String savedManagePassword;

    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        querySavePassword();
    }

    /**
     * 查询记住密码开关，记住的密码字符
     */
    private void querySavePassword() {
        ManagerPassword managerPassword;
        try {
            managerPassword = DataSupport.findFirst(ManagerPassword.class);
            if (!managerPassword.isRemenbEnable()) {
                saveManagetPassword = false;
                savedManagePassword="";
            } else {
                savedManagePassword = managerPassword.getManagerPassword();
                if(savedManagePassword==null){
                    savedManagePassword="";
                }
                Log.i(TAG,"保存的管理密码="+savedManagePassword);
                saveManagetPassword = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG,"保存密码="+saveManagetPassword+"密码="+savedManagePassword);
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
                ManagerPassword temp = DataSupport.findFirst(ManagerPassword.class);
                temp.setToDefault("remenbEnable");
                temp.setToDefault("managerPassword");
                int affectColumn=temp.updateAll();
                Log.i(TAG,"密码不保存影响的行数="+affectColumn);
                break;
            case R.id.button_authorise:
                mAuthoriseDialog = new AuthoriseDialog(this);
                mAuthoriseDialog.setGetDialogAuthtTypeTimeListener(this);
                mAuthoriseDialog.show();
                break;
            case R.id.button_open:
                //TODO
                if (saveManagetPassword && !savedManagePassword.equals("")) {
                    savedManagePassword = "123456";
                    mSmartLockManager.setSmaertLockParmars(SmartLockConstant.OPEN_LOCK, "003", savedManagePassword, null, null);
                } else {
                    startActivity(new Intent(this, SetLockPwdActivity.class));
                }
                break;
        }
    }

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseSetResult(String result) {
        Message msg=Message.obtain();
        msg.what=MSG_SHOW_TOAST;
        msg.obj=result;
        mHandler.sendMessage(msg);
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
                    Toast.makeText(SmartLockActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
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
