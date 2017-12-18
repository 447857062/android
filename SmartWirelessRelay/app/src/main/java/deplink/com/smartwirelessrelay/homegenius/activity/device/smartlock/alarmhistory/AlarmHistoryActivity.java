package deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.alarmhistory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.Info;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;

public class AlarmHistoryActivity extends Activity implements View.OnClickListener {
    private ListView list_alart_histroy;
    private AlarmHistoryAdapter mAlarmHistoryAdapter;
    private List<Info> mLockHistory;
    private SmartLockManager mSmartLockManager;
    private boolean isStartFromExperience;
    private TextView textview_title;
    private FrameLayout image_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_history);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStartFromExperience) {
            mLockHistory.clear();
            Info temp = new Info();
            temp.setUserid("003");
            temp.setTime("2017-11-19 12:35:23");
            mLockHistory.add(temp);
            temp = new Info();
            temp.setUserid("004");
            temp.setTime("2017-11-20 12:35:23");
            mLockHistory.add(temp);
            temp = new Info();
            temp.setUserid("004");
            temp.setTime("2017-11-21 12:35:23");
            mLockHistory.add(temp);
            temp = new Info();
            temp.setUserid("005");
            temp.setTime("2017-11-23 12:35:23");
            mLockHistory.add(temp);
            mAlarmHistoryAdapter.notifyDataSetChanged();
        } else {
            mSmartLockManager.InitSmartLockManager(this);
            mLockHistory.clear();
            if (mSmartLockManager.getAlarmRecord() != null) {
                mLockHistory.addAll(mSmartLockManager.getAlarmRecord());
            }
            mAlarmHistoryAdapter.notifyDataSetChanged();
        }


    }

    private void initEvents() {
        list_alart_histroy.setAdapter(mAlarmHistoryAdapter);
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        list_alart_histroy = (ListView) findViewById(R.id.list_alart_histroy);
        textview_title = (TextView) findViewById(R.id.textview_title);
        image_back = (FrameLayout) findViewById(R.id.image_back);
    }

    private void initDatas() {
        textview_title.setText("报警记录");
        isStartFromExperience =  DeviceManager.getInstance().isStartFromExperience();
        mLockHistory = new ArrayList<>();
        mAlarmHistoryAdapter = new AlarmHistoryAdapter(this, mLockHistory);
        if (!isStartFromExperience) {
            mSmartLockManager = SmartLockManager.getInstance();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
        }


    }
}
