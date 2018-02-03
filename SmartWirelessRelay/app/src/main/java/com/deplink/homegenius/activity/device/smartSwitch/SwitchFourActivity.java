package com.deplink.homegenius.activity.device.smartSwitch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.OpResult;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.smartswitch.SmartSwitchListener;
import com.deplink.homegenius.manager.device.smartswitch.SmartSwitchManager;
import com.deplink.homegenius.util.NetUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class SwitchFourActivity extends Activity implements View.OnClickListener, SmartSwitchListener {
    private static final String TAG = "SwitchFourActivity";
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView textview_edit;
    private Button button_switch_left;
    private Button button_switch_2;
    private Button button_switch_3;
    private Button button_switch_right;
    private SmartSwitchManager mSmartSwitchManager;
    private Button button_all_switch;
    private DeviceManager mDeviceManager;
    private SDKManager manager;
    private EventCallback ec;
    private boolean isUserLogin;
    private DeleteDeviceDialog connectLostDialog;
    private boolean switch_one_open;
    private boolean switch_two_open;
    private boolean switch_three_open;
    private boolean switch_four_open;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_four);
        initViews();
        initDatas();
        initEvents();
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        button_switch_left.setOnClickListener(this);
        button_switch_2.setOnClickListener(this);
        button_switch_3.setOnClickListener(this);
        button_switch_right.setOnClickListener(this);
        button_all_switch.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        isUserLogin=Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        switch_one_open = mSmartSwitchManager.getCurrentSelectSmartDevice().isSwitch_one_open();
        switch_two_open = mSmartSwitchManager.getCurrentSelectSmartDevice().isSwitch_two_open();
        switch_three_open = mSmartSwitchManager.getCurrentSelectSmartDevice().isSwitch_three_open();
        switch_four_open = mSmartSwitchManager.getCurrentSelectSmartDevice().isSwitch_four_open();
        setSwitchImageviewBackground();
        mSmartSwitchManager.querySwitchStatus("query");
        mDeviceManager.readDeviceInfoHttp(mDeviceManager.getCurrentSelectSmartDevice().getUid());
        manager.addEventCallback(ec);
    }
    private void setSwitchImageviewBackground() {
        Log.i(TAG, "switch_one_open=" + switch_one_open);
        Log.i(TAG, "switch_two_open=" + switch_two_open);
        Log.i(TAG, "switch_three_open=" + switch_three_open);
        Log.i(TAG, "switch_four_open=" + switch_four_open);
        if (switch_one_open) {
            button_switch_left.setBackgroundResource(R.drawable.fourwayswitchlefton);
        } else {
            button_switch_left.setBackgroundResource(R.color.transparent);
        }
        if (switch_two_open) {
            button_switch_2.setBackgroundResource(R.drawable.fourwayswitchrighton);
        } else {
            button_switch_2.setBackgroundResource(R.color.transparent);
        }
        if (switch_three_open) {
            button_switch_3.setBackgroundResource(R.drawable.fourwayswitchleftnext);
        } else {
            button_switch_3.setBackgroundResource(R.color.transparent);
        }
        if (switch_four_open) {
            button_switch_right.setBackgroundResource(R.drawable.fourwayswitchrightnext);
        } else {
            button_switch_right.setBackgroundResource(R.color.transparent);
        }
        if (switch_one_open && switch_two_open && switch_three_open && switch_four_open) {
            button_all_switch.setBackgroundResource(R.drawable.noallswitch);
        } else {
            button_all_switch.setBackgroundResource(R.drawable.allswitch);
        }
    }
    private void initDatas() {
        textview_title.setText("四路开关");
        textview_edit.setText("编辑");
        mSmartSwitchManager = SmartSwitchManager.getInstance();
        mSmartSwitchManager.InitSmartSwitchManager(this);
        mSmartSwitchManager.addSmartSwitchListener(this);
        mDeviceManager=DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new DeleteDeviceDialog(SwitchFourActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(SwitchFourActivity.this, LoginActivity.class));
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
                Log.i(TAG, "设备列表界面收到回调的mqtt消息=" + result);
                Gson gson = new Gson();
                OpResult mOpResult = gson.fromJson(result, OpResult.class);
                if (mOpResult.getOP().equalsIgnoreCase("REPORT")&& mOpResult.getMethod().equalsIgnoreCase("SmartWallSwitch")) {
                    String  mSwitchStatus=mOpResult.getSwitchStatus();
                    String[] sourceStrArray = mSwitchStatus.split(" ",4);
                    if(sourceStrArray[0].equals("01")){
                        switch_one_open=true;
                    }else if(sourceStrArray[0].equals("02")){
                        switch_one_open=false;
                    }
                    mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
                    if(sourceStrArray[1].equals("01")){
                        switch_two_open=true;
                    }else if(sourceStrArray[1].equals("02")){
                        switch_two_open=false;
                    }
                    mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_two_open(switch_two_open);
                    if(sourceStrArray[2].equals("01")){
                        switch_three_open=true;
                    }else if(sourceStrArray[2].equals("02")){
                        switch_three_open=false;
                    }
                    mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_three_open(switch_three_open);
                    if(sourceStrArray[3].equals("01")){
                        switch_four_open=true;
                    }else if(sourceStrArray[3].equals("02")){
                        switch_four_open=false;
                    }
                    mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_four_open(switch_four_open);
                    switch (mOpResult.getCommand()) {
                        case "close1":
                            switch_one_open = false;
                            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
                            break;
                        case "close2":
                            switch_two_open = false;
                            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_two_open(switch_two_open);
                            break;
                        case "close3":
                            switch_three_open = false;
                            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_three_open(switch_three_open);
                            break;
                        case "close4":
                            switch_four_open = false;
                            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_four_open(switch_four_open);
                            break;
                        case "open1":
                            switch_one_open = true;
                            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
                            break;
                        case "open2":
                            switch_two_open = true;
                            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_two_open(switch_two_open);
                            break;
                        case "open3":
                            switch_three_open = true;
                            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_three_open(switch_three_open);
                            break;
                        case "open4":
                            switch_four_open = true;
                            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_four_open(switch_four_open);
                            break;
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setSwitchImageviewBackground();
                            mSmartSwitchManager.getCurrentSelectSmartDevice().saveFast();
                        }
                    });
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
    private void initViews() {
        image_back = findViewById(R.id.image_back);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        button_switch_left = findViewById(R.id.button_switch_left);
        button_switch_2 = findViewById(R.id.button_switch_2);
        button_switch_3 = findViewById(R.id.button_switch_3);
        button_switch_right = findViewById(R.id.button_switch_right);
        button_all_switch = findViewById(R.id.button_all_switch);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mSmartSwitchManager.removeSmartSwitchListener(this);
        manager.removeEventCallback(ec);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_all_switch:
                if(NetUtil.isNetAvailable(this)){
                    if(!isUserLogin && !LocalConnectmanager.getInstance().isLocalconnectAvailable()){
                        ToastSingleShow.showText(this,"本地连接不可用,需要登录后才能操作");
                    }else{
                        if (switch_one_open || switch_two_open || switch_three_open || switch_four_open) {
                            mSmartSwitchManager.setSwitchCommand("close_all");
                        } else {
                            mSmartSwitchManager.setSwitchCommand("open_all");
                        }
                    }
                }else{
                    ToastSingleShow.showText(this,"网络连接不正常");
                }

                break;
            case R.id.button_switch_left:
                if(NetUtil.isNetAvailable(this)){
                    if(!isUserLogin && !LocalConnectmanager.getInstance().isLocalconnectAvailable()){
                        ToastSingleShow.showText(this,"本地连接不可用,需要登录后才能操作");
                    }else{
                        Log.i(TAG, "switch_one_open=" + switch_one_open);
                        if (switch_one_open) {
                            mSmartSwitchManager.setSwitchCommand("close1");
                        } else {
                            mSmartSwitchManager.setSwitchCommand("open1");
                        }
                    }
                }
                else{
                    ToastSingleShow.showText(this,"网络连接不正常");
                }

                break;
            case R.id.button_switch_2:
                if(NetUtil.isNetAvailable(this)){
                    if(!isUserLogin && !LocalConnectmanager.getInstance().isLocalconnectAvailable()){
                        ToastSingleShow.showText(this,"本地连接不可用,需要登录后才能操作");
                    }else{
                        if (switch_two_open) {
                            mSmartSwitchManager.setSwitchCommand("close2");
                        } else {
                            mSmartSwitchManager.setSwitchCommand("open2");
                        }
                    }
                }
                else{
                    ToastSingleShow.showText(this,"网络连接不正常");
                }


                break;
            case R.id.button_switch_3:
                if(NetUtil.isNetAvailable(this)){
                    if(!isUserLogin && !LocalConnectmanager.getInstance().isLocalconnectAvailable()){
                        ToastSingleShow.showText(this,"本地连接不可用,需要登录后才能操作");
                    }else{
                        if (switch_three_open) {
                            mSmartSwitchManager.setSwitchCommand("close3");
                        } else {
                            mSmartSwitchManager.setSwitchCommand("open3");
                        }
                    }
                }
                else{
                    ToastSingleShow.showText(this,"网络连接不正常");
                }


                break;
            case R.id.button_switch_right:
                if(NetUtil.isNetAvailable(this)){
                    if(!isUserLogin && !LocalConnectmanager.getInstance().isLocalconnectAvailable()){
                        ToastSingleShow.showText(this,"本地连接不可用,需要登录后才能操作");
                    }else{
                        if (switch_four_open) {
                            mSmartSwitchManager.setSwitchCommand("close4");
                        } else {
                            mSmartSwitchManager.setSwitchCommand("open4");
                        }
                    }
                }
                else{
                    ToastSingleShow.showText(this,"网络连接不正常");
                }
                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("switchType", "四路开关");
                startActivity(intent);
                break;
        }
    }

    private Handler mHandler = new Handler();

    @Override
    public void responseResult(String result) {
        Gson gson = new Gson();
        OpResult mOpResult = gson.fromJson(result, OpResult.class);
        String  mSwitchStatus=mOpResult.getSwitchStatus();
        String[] sourceStrArray = mSwitchStatus.split(" ",4);
        Log.i(TAG,"sourceStrArray[0]"+sourceStrArray[0]);
        Log.i(TAG,"sourceStrArray[1]"+sourceStrArray[1]);
        Log.i(TAG,"sourceStrArray[2]"+sourceStrArray[2]);
        Log.i(TAG,"sourceStrArray[3]"+sourceStrArray[3]);
            if(sourceStrArray[0].equals("01")){
                switch_one_open=true;
            }else if(sourceStrArray[0].equals("02")){
                switch_one_open=false;
            }
            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
            if(sourceStrArray[1].equals("01")){
                switch_two_open=true;
            }else if(sourceStrArray[1].equals("02")){
                switch_two_open=false;
            }
            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_two_open(switch_two_open);
            if(sourceStrArray[2].equals("01")){
                switch_three_open=true;
            }else if(sourceStrArray[2].equals("02")){
                switch_three_open=false;
            }
            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_three_open(switch_three_open);
            if(sourceStrArray[3].equals("01")){
                switch_four_open=true;
            }else if(sourceStrArray[3].equals("02")){
                switch_four_open=false;
            }
            mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_four_open(switch_four_open);
        switch (mOpResult.getCommand()) {
            case "close1":
                switch_one_open = false;
                mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
                break;
            case "close2":
                switch_two_open = false;
                mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_two_open(switch_two_open);
                break;
            case "close3":
                switch_three_open = false;
                mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_three_open(switch_three_open);
                break;
            case "close4":
                switch_four_open = false;
                mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_four_open(switch_four_open);
                break;
            case "open1":
                switch_one_open = true;
                mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
                break;
            case "open2":
                switch_two_open = true;
                mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_two_open(switch_two_open);
                break;
            case "open3":
                switch_three_open = true;
                mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_three_open(switch_three_open);
                break;
            case "open4":
                switch_four_open = true;
                mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_four_open(switch_four_open);
                break;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setSwitchImageviewBackground();
                mSmartSwitchManager.getCurrentSelectSmartDevice().saveFast();
            }
        });

    }

}
