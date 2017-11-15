package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock.SmartLockManager;

public class SendSmartDevListActivity extends Activity implements View.OnClickListener,SmartLockListener {
    private Button button_send_smart_dev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_smart_dev_list);
        initViews();
    }

    private void initViews() {
        button_send_smart_dev = (Button) findViewById(R.id.button_send_smart_dev);
        button_send_smart_dev.setOnClickListener(this);
    }

    private SmartLockManager mSmartLockManager;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_send_smart_dev:
                mSmartLockManager = SmartLockManager.getInstance();
                mSmartLockManager.InitSmartLockManager(this, this);
                mSmartLockManager.bindSmartDevList();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

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
