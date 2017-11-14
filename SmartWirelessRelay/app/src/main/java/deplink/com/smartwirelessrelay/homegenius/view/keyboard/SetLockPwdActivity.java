package deplink.com.smartwirelessrelay.homegenius.view.keyboard;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import org.litepal.tablemanager.Connector;

import java.util.ArrayList;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.ManagerPassword;
import deplink.com.smartwirelessrelay.homegenius.activity.smartlock.SmartLockActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.SmartLockConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;

public class SetLockPwdActivity extends Activity implements KeyboardUtil.CancelListener,CompoundButton.OnCheckedChangeListener,SmartLockListener{
    private static final String TAG="SetLockPwdActivity";
    private View backView;
    private EditText etPwdOne, etPwdTwo, etPwdThree, etPwdFour, etPwdText;
    private EditText etPwdFive_setLockPwd,etPwdSix_setLockPwd;
    private KeyboardUtil kbUtil;
    public String strLockPwdOne;
    private Handler mHandler;
    private Switch switch_remond_managerpassword;
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
        switch_remond_managerpassword = (Switch) findViewById(R.id.switch_remond_managerpassword);
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
        switch_remond_managerpassword.setOnCheckedChangeListener(this);
    }
    private SQLiteDatabase db;
    void initData() {
        //生成数据库
        if (db == null) {
            db = Connector.getDatabase();
        }
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this, this);

        kbUtil = new KeyboardUtil(this/*,this*/);
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
    private static final int MSG_TOAST=1;
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
                            mSmartLockManager.setSmaertLockParmars(SmartLockConstant.OPEN_LOCK, "003", strReapt, null, null);
                               /* if (strReapt.equals(strLockPwdOne)) {
                                    Toast.makeText(SetLockPwdActivity.this,
                                            "解锁密码设置成功",Toast.LENGTH_SHORT).show();
                                    backToActivity();
                                } else {
                                    Toast.makeText(SetLockPwdActivity.this,
                                            "解锁密码设置失败",Toast.LENGTH_SHORT).show();

                                }
*/
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ManagerPassword managerPassword=new ManagerPassword();
        managerPassword.setRemenbEnable(isChecked);
       /* if(isChecked){
            //TODO 密码对才能记住密码

            return;
        }*/

       boolean saveResult= managerPassword.save();
        Log.i(TAG,"saveResult记住密码="+saveResult);
    }

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseSetResult(String result) {

    }

    @Override
    public void responseBind(String result) {

    }
}
