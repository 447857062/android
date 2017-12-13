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

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.alarmhistory.AlarmHistoryActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.lockhistory.LockHistoryActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.SmartLockConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.smartlock.AuthoriseDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.smartlock.LockdeviceClearRecordDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.smartlock.PasswordNotsaveDialog;

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
    private LockdeviceClearRecordDialog clearRecordDialog;

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
        isStartFromExperience = mSmartLockManager.isStartFromExperience();
        saveManagetPasswordExperience = true;
        clearRecordDialog = new LockdeviceClearRecordDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isStartFromExperience){

        }else{
            mSmartLockManager.addSmartLockListener(this);
            Log.i(TAG,"当前设备uid="+mSmartLockManager.getCurrentSelectLock().getUid());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSmartLockManager.removeSmartLockListener(this);
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
                // intentLockHistory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                            saveManagetPassword = false;
                            savedManagePassword = "";
                            mSmartLockManager .getCurrentSelectLock().setLockPassword("");
                            boolean saveResult= mSmartLockManager .getCurrentSelectLock().save();
                            Log.i(TAG, "密码不保存=" + saveResult);
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
                saveManagetPassword=(mSmartLockManager.getCurrentSelectLock().isRemerberPassword());
                savedManagePassword=mSmartLockManager.getCurrentSelectLock().getLockPassword();
                if (isStartFromExperience) {
                    if (saveManagetPasswordExperience) {
                        Toast.makeText(SmartLockActivity.this, "开门成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intentSetLockPwd = new Intent(this, SetLockPwdActivity.class);
                        startActivity(intentSetLockPwd);
                    }
                } else {
                    Log.i(TAG,"saveManagetPassword="+saveManagetPassword+"savedManagePassword="+savedManagePassword);
                    if (saveManagetPassword && !savedManagePassword.equals("")) {
                        mSmartLockManager.setSmartLockParmars(SmartLockConstant.OPEN_LOCK, "003", savedManagePassword, null, null);
                    } else {
                        Intent intentSetLockPwd = new Intent(this, SetLockPwdActivity.class);
                        startActivity(intentSetLockPwd);
                    }
                }

                break;
            case R.id.layout_open:
                //TODO
                clearRecordDialog.setSureBtnClickListener(new LockdeviceClearRecordDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {

                    }
                });
                clearRecordDialog.show();

                break;
            case R.id.textview_update:
                Intent intent = new Intent(this, EditSmartLockActivity.class);
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
                    mSmartLockManager.setSmartLockParmars(SmartLockConstant.AUTH_TYPE_ONCE, "003", mSmartLockManager.getCurrentSelectLock().getLockPassword(), password, null);
                    break;
                case SmartLockConstant.AUTH_TYPE_PERPETUAL:
                    mSmartLockManager.setSmartLockParmars(SmartLockConstant.AUTH_TYPE_PERPETUAL, "003",mSmartLockManager.getCurrentSelectLock().getLockPassword(), password, null);
                    break;
                case SmartLockConstant.AUTH_TYPE_TIME_LIMIT:
                    long hour = Long.valueOf(limitTime);
                    limitTime = String.valueOf(hour * 60 * 1000);
                    Log.i(TAG, "hour=" + hour + "limittime=" + limitTime);
                    mSmartLockManager.setSmartLockParmars(SmartLockConstant.AUTH_TYPE_PERPETUAL, "003", mSmartLockManager.getCurrentSelectLock().getLockPassword(), password, limitTime);
                    break;
            }
        }

    }
}
