package deplink.com.smartwirelessrelay.homegenius.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;

/**
 * 智能网关界面
 */
public class SmartGetwayActivity extends Activity implements View.OnClickListener{
    private Button button_sure;
    private ClearEditText clearEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_getway);
        initViews();
        initEvents();
    }

    private void initEvents() {
        button_sure.setOnClickListener(this);

    }

    private void initViews() {
        button_sure= (Button) findViewById(R.id.button_sure);
        clearEditText = (ClearEditText) findViewById(R.id.clear);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_sure:

                break;
        }
    }
}
