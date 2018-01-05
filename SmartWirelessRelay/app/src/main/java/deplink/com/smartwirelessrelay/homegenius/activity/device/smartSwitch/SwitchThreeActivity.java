package deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch;

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
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.OpResult;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartswitch.SmartSwitchListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartswitch.SmartSwitchManager;

public class SwitchThreeActivity extends Activity implements View.OnClickListener, SmartSwitchListener {
    private static final String TAG = "SwitchFourActivity";
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView textview_edit;
    private Button button_switch_left;
    private Button button_switch_middle;
    private Button button_switch_right;
    private SmartSwitchManager mSmartSwitchManager;
    private boolean switch_one_open;
    private boolean switch_two_open;
    private boolean switch_three_open;
    private Button button_all_switch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_three);
        initViews();
        initDatas();
        initEvents();

    }

    @Override
    protected void onResume() {
        super.onResume();
        switch_one_open = mSmartSwitchManager.getCurrentSelectSmartDevice().isSwitch_one_open();
        switch_two_open = mSmartSwitchManager.getCurrentSelectSmartDevice().isSwitch_two_open();
        switch_three_open = mSmartSwitchManager.getCurrentSelectSmartDevice().isSwitch_three_open();
        setSwitchImageviewBackground();
        mSmartSwitchManager.querySwitchStatus("query");
    }

    private void setSwitchImageviewBackground() {
        Log.i(TAG, "switch_one_open=" + switch_one_open);
        Log.i(TAG, "switch_two_open=" + switch_two_open);
        Log.i(TAG, "switch_three_open=" + switch_three_open);
        if (switch_one_open) {
            button_switch_left.setBackgroundResource(R.drawable.threewayswitchlefton);
        } else {
            button_switch_left.setBackgroundResource(R.color.transparent);
        }
        if (switch_two_open) {
            button_switch_middle.setBackgroundResource(R.drawable.threewayswitchcenteron);
        } else {
            button_switch_middle.setBackgroundResource(R.color.transparent);
        }
        if (switch_three_open) {
            button_switch_right.setBackgroundResource(R.drawable.threewayswitchrighton);
        } else {
            button_switch_right.setBackgroundResource(R.color.transparent);
        }

        if(switch_one_open&&switch_two_open&&switch_three_open){
            button_all_switch.setBackgroundResource(R.drawable.noallswitch);
        }else{
            button_all_switch.setBackgroundResource(R.drawable.allswitch);
        }
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        button_switch_left.setOnClickListener(this);
        button_switch_middle.setOnClickListener(this);
        button_switch_right.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("三路开关");
        textview_edit.setText("编辑");
        mSmartSwitchManager = SmartSwitchManager.getInstance();
        mSmartSwitchManager.InitSmartSwitchManager(this);
        mSmartSwitchManager.addSmartSwitchListener(this);
        button_all_switch.setOnClickListener(this);
    }

    private void initViews() {
        image_back = findViewById(R.id.image_back);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        button_switch_left = findViewById(R.id.button_switch_left);
        button_switch_middle = findViewById(R.id.button_switch_middle);
        button_switch_right = findViewById(R.id.button_switch_right);
        button_all_switch = findViewById(R.id.button_all_switch);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmartSwitchManager.removeSmartSwitchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_all_switch:
                if (switch_one_open||switch_two_open||switch_three_open) {
                    mSmartSwitchManager.setSwitchCommand("close_all");
                } else {
                    mSmartSwitchManager.setSwitchCommand("open_all");
                }
                break;
            case R.id.textview_edit:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("switchType", "三路开关");
                startActivity(intent);
                break;
            case R.id.button_switch_left:
                Log.i(TAG, "switch_one_open=" + switch_one_open);
                if (switch_one_open) {
                    mSmartSwitchManager.setSwitchCommand("close1");
                } else {
                    mSmartSwitchManager.setSwitchCommand("open1");
                }
                break;
            case R.id.button_switch_middle:
                if (switch_two_open) {
                    mSmartSwitchManager.setSwitchCommand("close2");
                } else {
                    mSmartSwitchManager.setSwitchCommand("open2");
                }

                break;
            case R.id.button_switch_right:
                if (switch_three_open) {
                    mSmartSwitchManager.setSwitchCommand("close3");
                } else {
                    mSmartSwitchManager.setSwitchCommand("open3");
                }

        }
    }

    private Handler mHandler = new Handler();

    @Override
    public void responseResult(String result) {
        Gson gson = new Gson();
        OpResult mOpResult = gson.fromJson(result, OpResult.class);
        String  mSwitchStatus=mOpResult.getSwitchStatus();
        String[] sourceStrArray = mSwitchStatus.split(" ",3);
        Log.i(TAG,"sourceStrArray[0]"+sourceStrArray[0]);
        Log.i(TAG,"sourceStrArray[1]"+sourceStrArray[1]);
        Log.i(TAG,"sourceStrArray[2]"+sourceStrArray[2]);
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
