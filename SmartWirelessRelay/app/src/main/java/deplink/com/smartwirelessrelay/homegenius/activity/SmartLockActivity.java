package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.AuthoriseDialog;

public class SmartLockActivity extends Activity implements View.OnClickListener{
    private Button button_alert_record;
    private Button button_open_lock_record;
    private Button button_no_save_password;
    private Button button_authorise;
    private Button button_open;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_lock);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        mAuthoriseDialog=new AuthoriseDialog(this);
    }

    private void initEvents() {
        button_alert_record.setOnClickListener(this);
        button_open_lock_record.setOnClickListener(this);
        button_no_save_password.setOnClickListener(this);
        button_authorise.setOnClickListener(this);
        button_open.setOnClickListener(this);
    }

    private void initViews() {
        button_alert_record= (Button) findViewById(R.id.button_alert_record);
        button_open_lock_record= (Button) findViewById(R.id.button_open_lock_record);
        button_no_save_password= (Button) findViewById(R.id.button_no_save_password);
        button_authorise= (Button) findViewById(R.id.button_authorise);
        button_open= (Button) findViewById(R.id.button_open);
    }
    private AuthoriseDialog mAuthoriseDialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_alert_record:
                break;
            case R.id.button_open_lock_record:
                startActivity(new Intent(SmartLockActivity.this,LockHistory.class));
                break;
            case R.id.button_no_save_password:
                break;
            case R.id.button_authorise:
              mAuthoriseDialog.show();
                break;
            case R.id.button_open:
                break;
        }
    }
}
