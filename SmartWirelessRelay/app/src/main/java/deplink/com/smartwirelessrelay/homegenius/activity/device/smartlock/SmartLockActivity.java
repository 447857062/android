package deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.ManagerPassword;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.alarmhistory.AlarmHistoryActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.lockhistory.LockHistoryActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.SmartLockConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.AuthoriseDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.PasswordNotsaveDialog;

public class SmartLockActivity extends Activity implements View.OnClickListener, SmartLockListener, AuthoriseDialog.GetDialogAuthtTypeTimeListener {
    private static final String TAG = "SmartLockActivity";
    private RelativeLayout layout_alert_record;
    private RelativeLayout layout_lock_record;
    private RelativeLayout layout_password_not_save;
    private RelativeLayout layout_auth;
    private RelativeLayout layout_option_clear_record;
    private ImageView imageview_unlock;
    private SmartLockManager mSmartLockManager;
    private AuthoriseDialog mAuthoriseDialog;
    private TextView textview_update;
    /**
     * 测试管理密码，使用密码开门，如果返回成功，就是正确的管理密码
     */
    private boolean saveManagetPassword;
    private String savedManagePassword;
    private boolean isStartFromExperience;
    private boolean saveManagetPasswordExperience;
    private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_lock);
        initViews();
        initDatas();
        initEvents();
    }


    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this);
        isStartFromExperience = getIntent().getBooleanExtra("isStartFromExperience", false);
        saveManagetPasswordExperience = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSmartLockManager.addSmartLockListener(this);
        querySavePassword();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSmartLockManager.removeSmartLockListener(this);
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
                savedManagePassword = "";
            } else {
                savedManagePassword = managerPassword.getManagerPassword();
                if (savedManagePassword == null) {
                    savedManagePassword = "";
                }
                Log.i(TAG, "保存的管理密码=" + savedManagePassword);
                saveManagetPassword = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "保存密码=" + saveManagetPassword + "密码=" + savedManagePassword);
    }

    private void initEvents() {
        layout_alert_record.setOnClickListener(this);
        layout_lock_record.setOnClickListener(this);
        layout_password_not_save.setOnClickListener(this);
        layout_auth.setOnClickListener(this);
        layout_option_clear_record.setOnClickListener(this);
        textview_update.setOnClickListener(this);
        imageview_unlock.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        layout_alert_record = (RelativeLayout) findViewById(R.id.layout_alert_record);
        layout_lock_record = (RelativeLayout) findViewById(R.id.layout_lock_record);
        layout_password_not_save = (RelativeLayout) findViewById(R.id.layout_password_not_save);
        layout_auth = (RelativeLayout) findViewById(R.id.layout_auth);
        layout_option_clear_record = (RelativeLayout) findViewById(R.id.layout_open);
        textview_update = (TextView) findViewById(R.id.textview_update);
        imageview_unlock = (ImageView) findViewById(R.id.imageview_unlock);
        image_back = (ImageView) findViewById(R.id.image_back);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_alert_record:
                Intent intentAlarmHistory = new Intent(this, AlarmHistoryActivity.class);
                intentAlarmHistory.putExtra("isStartFromExperience", isStartFromExperience);
                startActivity(intentAlarmHistory);
                break;
            case R.id.layout_lock_record:
                Intent intentLockHistory = new Intent(this, LockHistoryActivity.class);
                intentLockHistory.putExtra("isStartFromExperience", isStartFromExperience);
                startActivity(intentLockHistory);
                break;
            case R.id.layout_password_not_save:
                PasswordNotsaveDialog dialog = new PasswordNotsaveDialog(this);
                dialog.setmOnSureClick(new PasswordNotsaveDialog.PasswordNotsaveSureListener() {
                    @Override
                    public void onSureClick() {
                        if (isStartFromExperience) {
                            saveManagetPasswordExperience = false;
                            mHandler.sendEmptyMessage(MSG_SHOW_NOTSAVE_PASSWORD_DIALOG);

                        } else {
                            ManagerPassword temp = DataSupport.findFirst(ManagerPassword.class);
                            temp.setToDefault("remenbEnable");
                            temp.setToDefault("managerPassword");
                            int affectColumn = temp.updateAll();
                            Log.i(TAG, "密码不保存影响的行数=" + affectColumn);
                        }
                    }
                });
                dialog.show();


                break;
            case R.id.layout_auth:
                mAuthoriseDialog = new AuthoriseDialog(this);
                mAuthoriseDialog.setGetDialogAuthtTypeTimeListener(this);
                mAuthoriseDialog.show();
                break;
            case R.id.image_back:
               onBackPressed();
                break;
            case R.id.imageview_unlock:
                //TODO
                if (isStartFromExperience) {
                    if (saveManagetPasswordExperience) {
                        Toast.makeText(SmartLockActivity.this, "开门成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intentSetLockPwd = new Intent(this, SetLockPwdActivity.class);
                        intentSetLockPwd.putExtra("isStartFromExperience", true);
                        startActivity(intentSetLockPwd);
                    }
                } else {
                    if (saveManagetPassword && !savedManagePassword.equals("")) {
                        savedManagePassword = "123456";
                        mSmartLockManager.setSmartLockParmars(SmartLockConstant.OPEN_LOCK, "003", savedManagePassword, null, null);
                    } else {
                        Intent intentSetLockPwd = new Intent(this, SetLockPwdActivity.class);
                        intentSetLockPwd.putExtra("isStartFromExperience", false);
                        startActivity(intentSetLockPwd);
                    }
                }

                break;
            case R.id.layout_open:
                //TODO


                break;
            case R.id.textview_update:
                Intent intent = new Intent(this, EditSmartLockActivity.class);
                intent.putExtra("isStartFromExperience", isStartFromExperience);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseSetResult(String result) {
        Log.i(TAG, "设置结果=" + result);
        Message msg = Message.obtain();
        msg.what = MSG_SHOW_TOAST;
        msg.obj = result;
        mHandler.sendMessage(msg);
    }

    @Override
    public void responseBind(String result) {

    }

    private static final int MSG_SHOW_TOAST = 1;
    private static final int MSG_SHOW_NOTSAVE_PASSWORD_DIALOG = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SHOW_TOAST:
                    Toast.makeText(SmartLockActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case MSG_SHOW_NOTSAVE_PASSWORD_DIALOG:
                    Toast.makeText(SmartLockActivity.this, "密码不保存", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onGetDialogAuthtTypeTime(String authType, String password, String limitTime) {
        Log.i(TAG, "authType=" + authType);
        if (isStartFromExperience) {
            switch (authType) {
                case SmartLockConstant.AUTH_TYPE_ONCE:
                    Toast.makeText(SmartLockActivity.this, "单次授权成功", Toast.LENGTH_SHORT).show();
                    break;
                case SmartLockConstant.AUTH_TYPE_PERPETUAL:
                    Toast.makeText(SmartLockActivity.this, "永久授权成功", Toast.LENGTH_SHORT).show();
                    break;
                case SmartLockConstant.AUTH_TYPE_TIME_LIMIT:
                    Toast.makeText(SmartLockActivity.this, "限时授权成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            switch (authType) {
                case SmartLockConstant.AUTH_TYPE_ONCE:
                    mSmartLockManager.setSmartLockParmars(SmartLockConstant.AUTH_TYPE_ONCE, "003", "123456", password, null);
                    break;
                case SmartLockConstant.AUTH_TYPE_PERPETUAL:
                    mSmartLockManager.setSmartLockParmars(SmartLockConstant.AUTH_TYPE_PERPETUAL, "003", "123456", password, null);
                    break;
                case SmartLockConstant.AUTH_TYPE_TIME_LIMIT:
                    long hour = Long.valueOf(limitTime);
                    limitTime = String.valueOf(hour * 60 * 1000);
                    Log.i(TAG, "hour=" + hour + "limittime=" + limitTime);
                    mSmartLockManager.setSmartLockParmars(SmartLockConstant.AUTH_TYPE_PERPETUAL, "003", "123456", password, limitTime);
                    //TODO
                    break;
            }
        }

    }
}
