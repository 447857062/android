package com.deplink.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.OpResult;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.lock.UserIdInfo;
import com.deplink.homegenius.Protocol.json.device.lock.UserIdPairs;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.broadcast.PushMessage;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.constant.SmartLockConstant;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.doorbeel.DoorBellListener;
import com.deplink.homegenius.manager.device.doorbeel.DoorbeelManager;
import com.deplink.homegenius.manager.device.smartlock.SmartLockListener;
import com.deplink.homegenius.manager.device.smartlock.SmartLockManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.util.WeakRefHandler;
import com.deplink.homegenius.util.bitmap.BitmapHandler;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.doorbeel.Doorbeel_menu_Dialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.json.PERFORMANCE;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.litepal.crud.DataSupport;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class DoorbeelMainActivity extends Activity implements View.OnClickListener, SmartLockListener {
    private static final String TAG = "DoorbeelMainActivity";
    private FrameLayout image_back;
    private ImageView image_setting;
    private TextView textview_title;
    private FrameLayout frame_setting;
    private DoorbeelManager mDoorbeelManager;
    private DoorBellListener mDoorBellListener;
    private Button button_opendoor;
    private SmartLockManager mSmartLockManager;
    private ImageView imageview_visitor;
    private RelativeLayout layout_no_vistor;
    private SDKManager manager;
    private EventCallback ec;
    private DeviceListener mDeviceListener;
    private DeleteDeviceDialog connectLostDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorbeel_main);
        initViews();
        initEvents();
        initDatas();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        image_setting.setOnClickListener(this);
        frame_setting.setOnClickListener(this);
        button_opendoor.setOnClickListener(this);
    }

    private String filename;

    private void initDatas() {
        textview_title.setText("智能门铃");
        mDoorbeelManager = DoorbeelManager.getInstance();
        mDoorbeelManager.InitDoorbeelManager(this);
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this);
        image_setting.setImageResource(R.drawable.menuicon);
        doorbeelMenuDialog = new Doorbeel_menu_Dialog(this);
        mDoorBellListener = new DoorBellListener() {
            public void responseVisitorImage(Bitmap bitmap, int count) {
                super.responseVisitorImage(bitmap, count);
                Log.i(TAG,"bitmap !=null"+(bitmap!=null));
                BitmapDrawable bbb = new BitmapDrawable(BitmapHandler.toRoundCorner(bitmap, 30));
                imageview_visitor.setBackgroundDrawable(bbb);
            }
        };
        initMqttCallback();
    }
    private boolean isUserLogin;
    private void initMqttCallback() {
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new DeleteDeviceDialog(DoorbeelMainActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(DoorbeelMainActivity.this, LoginActivity.class));
            }
        });
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {
            }

            @Override
            public void notifyHomeGeniusResponse(String result) {
                super.notifyHomeGeniusResponse(result);
                Gson gson = new Gson();
                PERFORMANCE content = null;
                try {
                    content = gson.fromJson(result, PERFORMANCE.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                if (content != null) {
                    if (content.getOP() != null && content.getOP().equalsIgnoreCase("REPORT")) {
                        if (content.getMethod().equalsIgnoreCase("SmartLock")) {
                            OpResult type = gson.fromJson(result, OpResult.class);
                            if (type != null && type.getOP().equals("REPORT") && type.getMethod().equals("SmartLock")) {
                                switch (type.getCommand()) {
                                    case SmartLockConstant.CMD.OPEN:
                                        switch (type.getResult()) {
                                            case SmartLockConstant.OPENLOCK.TIMEOUT:
                                                result = "开锁超时";
                                                break;
                                            case SmartLockConstant.OPENLOCK.SUCCESS:
                                                result = "开锁成功";
                                                break;
                                            case SmartLockConstant.OPENLOCK.PASSWORDERROR:
                                                result = "密码错误";
                                                break;
                                            case SmartLockConstant.OPENLOCK.FAIL:
                                                result = "开锁失败";
                                                break;
                                        }
                                        break;
                                    case SmartLockConstant.CMD.ONCE:
                                    case SmartLockConstant.CMD.PERMANENT:
                                    case SmartLockConstant.CMD.TIMELIMIT:
                                        switch (type.getResult()) {
                                            case SmartLockConstant.AUTH.TIMEOUT:
                                                result = "录入超时";
                                                break;
                                            case SmartLockConstant.AUTH.SUCCESS:
                                                result = "录入成功";
                                                break;
                                            case SmartLockConstant.AUTH.PASSWORDERROR:
                                                result = "密码错误";
                                                break;
                                            case SmartLockConstant.AUTH.FAIL:
                                                result = "录入失败";
                                                break;
                                            case SmartLockConstant.AUTH.FORBADE:
                                                result = "禁止操作";
                                                break;
                                        }
                                        break;

                                }
                                if(result!=null){
                                    Message msg=Message.obtain();
                                    msg.what=MSG_SHOW_OPENLOCK_RESULT;
                                    msg.obj=result;
                                    mHandler.sendMessage(msg);
                                }
                            }
                        }
                    }
                }
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
                isUserLogin = false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setContentText("当前账号已在其它设备上登录,是否重新登录");
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        if(mDoorbeelManager!=null){
            mDoorbeelManager = DoorbeelManager.getInstance();
            mDoorbeelManager.InitDoorbeelManager(this);
        }
        if(mDoorBellListener!=null){
            mDoorbeelManager.addDeviceListener(mDoorBellListener);
        }
        if(mSmartLockManager!=null){
            mSmartLockManager.addSmartLockListener(this);
        }
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        XGPushClickedResult clickedResult = XGPushManager.onActivityStarted(this);
        Log.i(TAG,"clickedResult="+(clickedResult!=null));
        if (clickedResult != null) { // 判断是否来自信鸽的打开方式
            String  customContent= clickedResult.getCustomContent();
            isStartFromExperience=false;
            Log.i(TAG,"customContent="+customContent);
            Gson gson = new Gson();
            pushMessage = gson.fromJson(customContent, PushMessage.class);
            if (pushMessage != null) {

                if(pushMessage.getFile()!=null && !pushMessage.getFile().equalsIgnoreCase("")){
                    SmartDev dbSmartDev = DataSupport.where("Uid = ?", pushMessage.getBell_uid()).findFirst(SmartDev.class,true);
                    if(dbSmartDev!=null){
                        mDoorbeelManager.setCurrentSelectedDoorbeel(dbSmartDev);
                    }
                    filename=pushMessage.getFile();
                    Log.i(TAG, "filename=" + filename);
                    if (filename != null && !filename.equalsIgnoreCase("")) {
                        mDoorbeelManager.getDoorbellVistorImage(filename, 0);
                        imageview_visitor.setVisibility(View.VISIBLE);
                        layout_no_vistor.setVisibility(View.GONE);
                    }
                }
            }
        }
        if (!isStartFromExperience) {
            if (mDoorbeelManager.getCurrentSelectedDoorbeel().getBindLockUid() != null) {
                mSmartLockManager.queryLockUidHttp(mDoorbeelManager.getCurrentSelectedDoorbeel().getBindLockUid());
                String lockuid = mDoorbeelManager.getCurrentSelectedDoorbeel().getBindLockUid();
                lockDevice = DataSupport.where("Uid=?", lockuid).findFirst(SmartDev.class, true);
            }

            if (lockDevice != null && (lockDevice.getStatus().equalsIgnoreCase("在线")|| lockDevice.getStatus().equalsIgnoreCase("ON"))) {
                Log.i(TAG,"lockDevice="+lockDevice.toString());
                button_opendoor.setBackgroundResource(R.drawable.radius22_bg_button_background);
            } else {
                button_opendoor.setBackgroundResource(R.drawable.radius22_bg_6c_background);
            }
        } else {
            button_opendoor.setBackgroundResource(R.drawable.radius22_bg_button_background);
        }

    }
    private PushMessage pushMessage;
    @Override
    protected void onPause() {
        super.onPause();
        imageview_visitor.setVisibility(View.GONE);
        layout_no_vistor.setVisibility(View.VISIBLE);
        mDoorbeelManager.removeDeviceListener(mDoorBellListener);
        mSmartLockManager.removeSmartLockListener(this);
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        image_setting = findViewById(R.id.image_setting);
        frame_setting = findViewById(R.id.frame_setting);
        button_opendoor = findViewById(R.id.button_opendoor);
        layout_no_vistor = findViewById(R.id.layout_no_vistor);
        imageview_visitor = findViewById(R.id.imageview_visitor);
    }

    private Doorbeel_menu_Dialog doorbeelMenuDialog;
    private boolean isStartFromExperience;
    private String savedManagePassword;
    private String selfUserId;
    private SmartDev lockDevice;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_setting:
                doorbeelMenuDialog.show();
                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_opendoor:
                if (isStartFromExperience) {
                    ToastSingleShow.showText(this, "门锁已开");
                } else {
                    if (lockDevice != null && selfUserId != null) {
                        Log.i(TAG,"lockDevice="+lockDevice.toString()+"selfUserId="+selfUserId);
                        savedManagePassword = lockDevice.getLockPassword();
                        mSmartLockManager.setCurrentSelectLock(lockDevice);
                        mSmartLockManager.setSmartLockParmars(SmartLockConstant.OPEN_LOCK, selfUserId, savedManagePassword, null, null);
                    }else{
                        ToastSingleShow.showText(this,"未绑定门锁,无法开门");
                    }
                }
                break;

        }
    }
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case MSG_SHOW_OPENLOCK_RESULT:
                    ToastSingleShow.showText(DoorbeelMainActivity.this,""+msg.obj);
                    break;
            }
            return true;
        }
    };
    private Handler mHandler = new WeakRefHandler(mCallback);
    private static final int MSG_SHOW_OPENLOCK_RESULT=100;
    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseSetResult(String result) {
        Message msg=Message.obtain();
        msg.what=MSG_SHOW_OPENLOCK_RESULT;
        if(result!=null){
            msg.obj=result;
            mHandler.sendMessage(msg);
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
        Log.i(TAG,"userIdInfo="+userIdInfo.toString());
        List<UserIdPairs> mUserIdPairs = userIdInfo.getAlluser();
        for (int i = 0; i < mUserIdPairs.size(); i++) {
            UserIdPairs tempUserIdPair = DataSupport.where("userid = ?", mUserIdPairs.get(i).getUserid()).findFirst(UserIdPairs.class);
            if (tempUserIdPair != null) {
                tempUserIdPair.setUsername(userIdInfo.getAlluser().get(i).getUsername());
                tempUserIdPair.saveFast();
            } else {
                UserIdPairs addUserIdPair = new UserIdPairs();
                addUserIdPair.setUserid(userIdInfo.getAlluser().get(i).getUserid());
                addUserIdPair.setUsername(userIdInfo.getAlluser().get(i).getUsername());
                addUserIdPair.saveFast();
            }
        }
        selfUserId = userIdInfo.getSelfid();
    }
}
