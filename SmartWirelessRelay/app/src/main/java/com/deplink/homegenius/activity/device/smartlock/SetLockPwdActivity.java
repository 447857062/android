package com.deplink.homegenius.activity.device.smartlock;

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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.deplink.homegenius.Protocol.json.OpResult;
import com.deplink.homegenius.Protocol.json.device.lock.UserIdInfo;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.constant.SmartLockConstant;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.smartlock.SmartLockListener;
import com.deplink.homegenius.manager.device.smartlock.SmartLockManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.keyboard.KeyboardUtil;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import java.util.ArrayList;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class SetLockPwdActivity extends Activity implements KeyboardUtil.CancelListener, View.OnClickListener, SmartLockListener {
    private static final String TAG = "SetLockPwdActivity";
    private EditText etPwdOne, etPwdTwo, etPwdThree, etPwdFour, etPwdText;
    private EditText etPwdFive_setLockPwd, etPwdSix_setLockPwd;
    private KeyboardUtil kbUtil;
    private Handler mHandler;
    private ImageView switch_remond_managerpassword;
    private boolean isStartFromExperience;
    private RelativeLayout layout_save_password;
    private boolean isLogin;
    private SDKManager manager;
    private EventCallback ec;
    private DeleteDeviceDialog connectLostDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lock_pwd);
        findView();
        setListener();
        initData();
    }

    void findView() {
        etPwdOne = findViewById(R.id.etPwdOne_setLockPwd);
        etPwdTwo = findViewById(R.id.etPwdTwo_setLockPwd);
        etPwdThree = findViewById(R.id.etPwdThree_setLockPwd);
        etPwdFour = findViewById(R.id.etPwdFour_setLockPwd);
        etPwdFive_setLockPwd = findViewById(R.id.etPwdFive_setLockPwd);
        etPwdSix_setLockPwd = findViewById(R.id.etPwdSix_setLockPwd);
        etPwdText = findViewById(R.id.etPwdText_setLockPwd);
        switch_remond_managerpassword = findViewById(R.id.switch_remond_managerpassword);
        layout_save_password = findViewById(R.id.layout_save_password);
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
        layout_save_password.setOnClickListener(this);
    }


    private void initData() {
        mSmartLockManager = SmartLockManager.getInstance();
        isStartFromExperience =  DeviceManager.getInstance().isStartFromExperience();
        if (!isStartFromExperience) {
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
        connectLostDialog = new DeleteDeviceDialog(SetLockPwdActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(SetLockPwdActivity.this, LoginActivity.class));
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
            public void notifyHomeGeniusResponse(String setResult) {
                super.notifyHomeGeniusResponse(setResult);
                Gson gson = new Gson();
                OpResult type = gson.fromJson(setResult, OpResult.class);
                Log.i(TAG,"智能门锁接收远程门锁设置结果返回:"+setResult);
                if (type != null && type.getOP().equalsIgnoreCase("REPORT") && type.getMethod().equalsIgnoreCase("SmartLock")) {
                    switch (type.getCommand()) {
                        case SmartLockConstant.CMD.OPEN:
                            switch (type.getResult()) {
                                case SmartLockConstant.OPENLOCK.TIMEOUT:
                                    setResult = "开锁超时";
                                    break;
                                case SmartLockConstant.OPENLOCK.SUCCESS:
                                    setResult = "开锁成功";
                                    mSmartLockManager.getCurrentSelectLock().setLockPassword(currentPassword);
                                    mSmartLockManager.getCurrentSelectLock().setRemerberPassword(true);
                                    mSmartLockManager.getCurrentSelectLock().save();
                                    break;
                                case SmartLockConstant.OPENLOCK.PASSWORDERROR:
                                    setResult = "密码错误";
                                    break;
                                case SmartLockConstant.OPENLOCK.FAIL:
                                    setResult = "开锁失败";
                                    break;
                            }
                            break;
                    }
                }
                Message msg = Message.obtain();
                msg.what = MSG_SHOW_TOAST;
                if(setResult.length()==4){
                    msg.obj = setResult;
                    mHandler.sendMessage(msg);
                }

            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                isLogin = false;
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setContentText("当前账号已在其它设备上登录,是否重新登录");
            }
        };
    }

    void backToActivity() {
        Intent mIntent = new Intent(SetLockPwdActivity.this, SmartLockActivity.class);
        startActivity(mIntent);
    }

    private SmartLockManager mSmartLockManager;
    private static final int MSG_TOAST = 1;
    private static final int MSG_SHOW_TOAST = 2;
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
                                //做延时处理,没有反应就隐藏界面
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        SetLockPwdActivity.this.finish();
                                    }
                                },3000);
                                String userId= Perfence.getPerfence(AppConstant.PERFENCE_LOCK_SELF_USERID);
                                mSmartLockManager.setSmartLockParmars(SmartLockConstant.OPEN_LOCK, userId, strReapt, null, null);
                            }
                            etPwdOne.setText("");
                            etPwdTwo.setText("");
                            etPwdThree.setText("");
                            etPwdFour.setText("");
                            etPwdFive_setLockPwd.setText("");
                            etPwdSix_setLockPwd.setText("");
                        }
                        break;
                    case MSG_SHOW_TOAST:
                        Toast.makeText(SetLockPwdActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        SetLockPwdActivity.this.finish();
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
        if ("开锁成功".equals(result)) {
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

    @Override
    public void responseLockStatu(int RecondNum, int LockStatus) {

    }

    @Override
    public void responseUserIdInfo(UserIdInfo userIdInfo) {

    }

    private int currentImageLevel;
    @Override
    protected void onResume() {
        super.onResume();
        if(! DeviceManager.getInstance().isStartFromExperience()){
            if (mSmartLockManager.getCurrentSelectLock().isRemerberPassword()) {
                switch_remond_managerpassword.setImageLevel(1);
                currentImageLevel = 1;
            } else {
                switch_remond_managerpassword.setImageLevel(0);
                currentImageLevel = 0;
            }
        }
        manager.addEventCallback(ec);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_save_password:
                switch (currentImageLevel) {
                    case 0:
                        currentImageLevel = 1;
                        if(!isStartFromExperience){
                            mSmartLockManager.getCurrentSelectLock().setRemerberPassword(true);
                        }
                        break;
                    case 1:
                        currentImageLevel = 0;
                        if(!isStartFromExperience){
                            mSmartLockManager.getCurrentSelectLock().setRemerberPassword(false);
                        }
                        break;
                }
                switch_remond_managerpassword.setImageLevel(currentImageLevel);
                if(!isStartFromExperience){
                    mSmartLockManager.getCurrentSelectLock().save();
                }
                break;

        }
    }
}
