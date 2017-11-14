package deplink.com.smartwirelessrelay.homegenius.activity.smartlock.alarmhistory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.alertreport.LOCK_ALARM;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;

public class AlarmHistoryActivity extends Activity   {
    private ListView list_alart_histroy;
    private AlarmHistoryAdapter mAlarmHistoryAdapter;
    private List<LOCK_ALARM>mLockHistory;
    private SmartLockManager mSmartLockManager;
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

        mSmartLockManager.InitSmartLockManager(this, null);

        mLockHistory.clear();
        mLockHistory.addAll(mSmartLockManager.getAlarmRecord(""));
        mAlarmHistoryAdapter.notifyDataSetChanged();

    }

    private void initEvents() {
        list_alart_histroy.setAdapter(mAlarmHistoryAdapter);
    }

    private void initViews() {
        list_alart_histroy= (ListView) findViewById(R.id.list_alart_histroy);
    }

    private void initDatas() {
        mSmartLockManager = SmartLockManager.getInstance();
        mLockHistory=new ArrayList<>();
        mAlarmHistoryAdapter=new AlarmHistoryAdapter(this,mLockHistory);
    }


}
