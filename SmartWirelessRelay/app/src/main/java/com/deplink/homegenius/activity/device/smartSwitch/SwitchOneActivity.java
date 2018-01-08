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

import com.google.gson.Gson;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import com.deplink.homegenius.Protocol.json.OpResult;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.smartswitch.SmartSwitchListener;
import com.deplink.homegenius.manager.device.smartswitch.SmartSwitchManager;

public class SwitchOneActivity extends Activity implements View.OnClickListener ,SmartSwitchListener{
    private static final String TAG = "SwitchTwoActivity";
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView textview_edit;
    private Button button_switch;
    private SmartSwitchManager mSmartSwitchManager;
    private boolean switch_one_open;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_one);
        initViews();
        initDatas();
        initEvents();
    }
    private boolean isStartFromExperience;
    @Override
    protected void onResume() {
        super.onResume();
        isStartFromExperience= DeviceManager.getInstance().isStartFromExperience();
        if(isStartFromExperience){
            switch_one_open=true;
        }else{
            switch_one_open = mSmartSwitchManager.getCurrentSelectSmartDevice().isSwitch_one_open();
            mSmartSwitchManager.querySwitchStatus("query");
        }
        setSwitchImageviewBackground();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmartSwitchManager.removeSmartSwitchListener(this);
    }

    private void setSwitchImageviewBackground() {
        Log.i(TAG, "switch_one_open=" + switch_one_open);
        if (switch_one_open) {
            button_switch.setBackgroundResource(R.drawable.switchallthewayon);
        } else {
            button_switch.setBackgroundResource(R.drawable.switchallthewayoff);
        }
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        button_switch.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("一路开关");
        textview_edit.setText("编辑");
        mSmartSwitchManager = SmartSwitchManager.getInstance();
        mSmartSwitchManager.InitSmartSwitchManager(this);
        mSmartSwitchManager.addSmartSwitchListener(this);
    }

    private void initViews() {
        image_back = findViewById(R.id.image_back);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        button_switch = findViewById(R.id.button_switch);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_switch:
                Log.i(TAG, "switch_one_open=" + switch_one_open);
                if(isStartFromExperience){

                    if (switch_one_open) {
                        switch_one_open=false;
                    } else {
                        switch_one_open=true;
                    }
                    setSwitchImageviewBackground();
                }else{
                    if (switch_one_open) {
                        mSmartSwitchManager.setSwitchCommand("close1");
                    } else {
                        mSmartSwitchManager.setSwitchCommand("open1");
                    }
                }

                break;
            case R.id.textview_edit:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("switchType", "一路开关");
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
        String[] sourceStrArray = mSwitchStatus.split(" ",1);
        Log.i(TAG,"sourceStrArray[0]"+sourceStrArray[0]);
        if(sourceStrArray[0].equals("01")){
            switch_one_open=true;
        }else if(sourceStrArray[0].equals("02")){
            switch_one_open=false;
        }
        mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
        switch (mOpResult.getCommand()) {
            case "close1":
                switch_one_open = false;
                mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
                break;
            case "open1":
                switch_one_open = true;
                mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
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
