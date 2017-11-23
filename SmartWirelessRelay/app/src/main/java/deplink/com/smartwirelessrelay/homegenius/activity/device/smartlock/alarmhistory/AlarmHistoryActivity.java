package deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.alarmhistory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.LOCK_ALARM;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;

public class AlarmHistoryActivity extends Activity {
    private ListView list_alart_histroy;
    private AlarmHistoryAdapter mAlarmHistoryAdapter;
    private List<LOCK_ALARM> mLockHistory;
    private SmartLockManager mSmartLockManager;
    private boolean isStartFromExperience;

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
            LOCK_ALARM temp = new LOCK_ALARM();
            temp.setUserid("003");
            temp.setTime("2017-11-19 12:35:23");
            mLockHistory.add(temp);
            temp = new LOCK_ALARM();
            temp.setUserid("004");
            temp.setTime("2017-11-20 12:35:23");
            mLockHistory.add(temp);
            temp = new LOCK_ALARM();
            temp.setUserid("004");
            temp.setTime("2017-11-21 12:35:23");
            mLockHistory.add(temp);
            temp = new LOCK_ALARM();
            temp.setUserid("005");
            temp.setTime("2017-11-23 12:35:23");
            mLockHistory.add(temp);
            mAlarmHistoryAdapter.notifyDataSetChanged();
        } else {
            mSmartLockManager.InitSmartLockManager(this);
            mLockHistory.clear();
            //TODO
            if (mSmartLockManager.getAlarmRecord("") != null) {
                mLockHistory.addAll(mSmartLockManager.getAlarmRecord(""));
            }
            mAlarmHistoryAdapter.notifyDataSetChanged();
        }


    }

    private void initEvents() {
        list_alart_histroy.setAdapter(mAlarmHistoryAdapter);
    }

    private void initViews() {
        list_alart_histroy = (ListView) findViewById(R.id.list_alart_histroy);
    }

    private void initDatas() {
        isStartFromExperience = getIntent().getBooleanExtra("isStartFromExperience", false);
        mLockHistory = new ArrayList<>();
        mAlarmHistoryAdapter = new AlarmHistoryAdapter(this, mLockHistory);
        if (!isStartFromExperience) {
            mSmartLockManager = SmartLockManager.getInstance();
        }
    }


}
