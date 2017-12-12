package deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.constant.SmartLockConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;
import deplink.com.smartwirelessrelay.homegenius.view.keyboard.KeyboardUtil;

public class SetLockPwdActivity extends Activity implements KeyboardUtil.CancelListener, View.OnClickListener, SmartLockListener {
    private static final String TAG = "SetLockPwdActivity";
    private EditText etPwdOne, etPwdTwo, etPwdThree, etPwdFour, etPwdText;
    private EditText etPwdFive_setLockPwd, etPwdSix_setLockPwd;
    private KeyboardUtil kbUtil;
    private Handler mHandler;
    private ImageView switch_remond_managerpassword;
    private boolean isStartFromExperience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lock_pwd);
        findView();
        setListener();
        initData();
    }

    void findView() {
        etPwdOne = (EditText) findViewById(R.id.etPwdOne_setLockPwd);
        etPwdTwo = (EditText) findViewById(R.id.etPwdTwo_setLockPwd);
        etPwdThree = (EditText) findViewById(R.id.etPwdThree_setLockPwd);
        etPwdFour = (EditText) findViewById(R.id.etPwdFour_setLockPwd);
        etPwdFive_setLockPwd = (EditText) findViewById(R.id.etPwdFive_setLockPwd);
        etPwdSix_setLockPwd = (EditText) findViewById(R.id.etPwdSix_setLockPwd);
        etPwdText = (EditText) findViewById(R.id.etPwdText_setLockPwd);
        switch_remond_managerpassword = (ImageView) findViewById(R.id.switch_remond_managerpassword);
    }

    void setListener() {
        etPwdText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (etPwdFour.getText() != null
                        && etPwdFour.getText().toString().length() >= 1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                Message msg = mHandler.obtainMessage();
                                msg.what = MSG_TOAST;
                                mHandler.sendMessage(msg);
                            }
                        }
                    }).start();
                }
            }
        });
        switch_remond_managerpassword.setOnClickListener(this);
    }


    private void initData() {
        mSmartLockManager = SmartLockManager.getInstance();
        isStartFromExperience = mSmartLockManager.isStartFromExperience();
        if (isStartFromExperience) {

        } else {

            mSmartLockManager.InitSmartLockManager(this);
            mSmartLockManager.addSmartLockListener(this);
        }

        kbUtil = new KeyboardUtil(this);
        ArrayList<EditText> list = new ArrayList<EditText>();
        list.add(etPwdOne);
        list.add(etPwdTwo);
        list.add(etPwdThree);
        list.add(etPwdFour);
        list.add(etPwdFive_setLockPwd);
        list.add(etPwdSix_setLockPwd);
        list.add(etPwdText);
        kbUtil.setListEditText(list);
        etPwdOne.setInputType(InputType.TYPE_NULL);
        etPwdTwo.setInputType(InputType.TYPE_NULL);
        etPwdThree.setInputType(InputType.TYPE_NULL);
        etPwdFour.setInputType(InputType.TYPE_NULL);
        etPwdFive_setLockPwd.setInputType(InputType.TYPE_NULL);
        etPwdSix_setLockPwd.setInputType(InputType.TYPE_NULL);
        MyHandle();
    }

    void backToActivity() {
        Intent mIntent = new Intent(SetLockPwdActivity.this, SmartLockActivity.class);
        startActivity(mIntent);
    }

    private SmartLockManager mSmartLockManager;
    private static final int MSG_TOAST = 1;
    private String currentPassword;

    public void MyHandle() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_TOAST:
                        if (etPwdFour.getText() != null
                                && etPwdFour.getText().toString().length() >= 1) {
                            String strReapt = etPwdText.getText().toString();
                            //TODO 查询管理密码 ，就是使用输入的密码开门，如果返回密码错误，就是错误
                            currentPassword = strReapt;
                            if (isStartFromExperience) {
                                Toast.makeText(SetLockPwdActivity.this, "开门成功", Toast.LENGTH_SHORT).show();
                                SetLockPwdActivity.this.finish();
                            } else {
                                mSmartLockManager.setSmartLockParmars(SmartLockConstant.OPEN_LOCK, "003", strReapt, null, null);
                            }
                            etPwdOne.setText("");
                            etPwdTwo.setText("");
                            etPwdThree.setText("");
                            etPwdFour.setText("");
                            etPwdFive_setLockPwd.setText("");
                            etPwdSix_setLockPwd.setText("");
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onCancelClick() {
        backToActivity();
    }


    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseSetResult(String result) {
        Log.i(TAG, "设置管理密码=" + result);
        //密码正确才能保存，消失界面显示
        // TODO 保存密码
        Log.i(TAG, "result=" + result);
        if ("成功".equals(result)) {
            if (currentImageLevel == 1) {

                mSmartLockManager.getCurrentSelectLock().setLockPassword(currentPassword);
                mSmartLockManager.getCurrentSelectLock().setRemerberPassword(true);
                mSmartLockManager.getCurrentSelectLock().save();
            }
            SetLockPwdActivity.this.finish();
        }
    }

    @Override
    public void responseBind(String result) {

    }

    private int currentImageLevel;

    @Override
    protected void onResume() {
        super.onResume();
        if (mSmartLockManager.getCurrentSelectLock().isRemerberPassword()) {
            switch_remond_managerpassword.setImageLevel(1);
            currentImageLevel = 1;
        } else {
            switch_remond_managerpassword.setImageLevel(0);
            currentImageLevel = 0;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_remond_managerpassword:
                switch (currentImageLevel) {
                    case 0:
                        currentImageLevel = 1;
                        break;
                    case 1:
                        currentImageLevel = 0;
                        break;
                }
                switch_remond_managerpassword.setImageLevel(currentImageLevel);
                ;
                switch (currentImageLevel) {
                    case 0:
                        mSmartLockManager.getCurrentSelectLock().setRemerberPassword(false);
                        break;
                    case 1:
                        mSmartLockManager.getCurrentSelectLock().setRemerberPassword(true);
                        break;
                }
                mSmartLockManager.getCurrentSelectLock().save();
                break;

        }
    }
}
