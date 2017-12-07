package deplink.com.smartwirelessrelay.homegenius.activity.personal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;

/**
 * 智能网关界面
 */
public class SmartGetwayActivity extends Activity implements View.OnClickListener{
    private Button button_sure;
    private ClearEditText clearEditText;
    private TextView textview_title;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_getway);
        initViews();
        initDatyas();
        initEvents();
    }

    private void initDatyas() {
        textview_title.setText("智能网关");
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_sure.setOnClickListener(this);

    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (FrameLayout) findViewById(R.id.image_back);
        button_sure= (Button) findViewById(R.id.button_sure);
        clearEditText = (ClearEditText) findViewById(R.id.clear);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_sure:

                break;
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
