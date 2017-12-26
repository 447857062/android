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

public class SwitchTwoActivity extends Activity implements View.OnClickListener, SmartSwitchListener {
    private static final String TAG = "SwitchTwoActivity";
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView textview_edit;
    private Button button_switch_left;
    private Button button_switch_right;
    private boolean switch_one_open;
    private boolean switch_two_open;
    private SmartSwitchManager mSmartSwitchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_two);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch_one_open = mSmartSwitchManager.getCurrentSelectSmartDevice().isSwitch_one_open();
        switch_two_open = mSmartSwitchManager.getCurrentSelectSmartDevice().isSwitch_two_open();
        setSwitchImageviewBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmartSwitchManager.removeSmartSwitchListener(this);
    }

    private void setSwitchImageviewBackground() {
        Log.i(TAG, "switch_one_open=" + switch_one_open);
        Log.i(TAG, "switch_two_open=" + switch_two_open);
        if (switch_one_open) {
            button_switch_left.setBackgroundResource(R.drawable.roadswitchlefton);
        } else {
            button_switch_left.setBackgroundResource(R.color.transparent);
        }
        if (switch_two_open) {
            button_switch_right.setBackgroundResource(R.drawable.roadswitchrighton);
        } else {
            button_switch_right.setBackgroundResource(R.color.transparent);
        }
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        button_switch_right.setOnClickListener(this);
        button_switch_left.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("二路开关");
        textview_edit.setText("编辑");
        mSmartSwitchManager = SmartSwitchManager.getInstance();
        mSmartSwitchManager.InitSmartSwitchManager(this);
        mSmartSwitchManager.addSmartSwitchListener(this);
    }

    private void initViews() {
        image_back = (FrameLayout) findViewById(R.id.image_back);
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_edit = (TextView) findViewById(R.id.textview_edit);
        button_switch_left = (Button) findViewById(R.id.button_switch_left);
        button_switch_right = (Button) findViewById(R.id.button_switch_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("switchType", "二路开关");
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
            case R.id.button_switch_right:
                if (switch_two_open) {
                    mSmartSwitchManager.setSwitchCommand("close2");
                } else {
                    mSmartSwitchManager.setSwitchCommand("open2");
                }

                break;
        }
    }

    private Handler mHandler = new Handler();

    @Override
    public void responseResult(String result) {
        Gson gson = new Gson();
        OpResult mOpResult = gson.fromJson(result, OpResult.class);
        if (mOpResult != null) {
            switch (mOpResult.getCommand()) {
                case "close1":
                    switch_one_open = false;
                    mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
                    break;
                case "close2":
                    switch_two_open = false;
                    mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_two_open(switch_two_open);
                    break;

                case "open1":
                    switch_one_open = true;
                    mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_one_open(switch_one_open);
                    break;
                case "open2":
                    switch_two_open = true;
                    mSmartSwitchManager.getCurrentSelectSmartDevice().setSwitch_two_open(switch_two_open);
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
}
