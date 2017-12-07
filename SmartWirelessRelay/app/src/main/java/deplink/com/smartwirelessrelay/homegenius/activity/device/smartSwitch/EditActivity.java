package deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class EditActivity extends Activity implements View.OnClickListener{
    private static final String TAG="EditDoorbeelActivity";
    private FrameLayout image_back;
    private TextView textview_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initViews();
        initDatas();
        initEvents();
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
    }
    private String switchType;
    private void initDatas() {
        switchType=getIntent().getStringExtra("switchType");
        Log.i(TAG,"initDatas switchType="+switchType);
        textview_title.setText(switchType);
    }

    private void initViews() {
        image_back= (FrameLayout) findViewById(R.id.image_back);
        textview_title= (TextView) findViewById(R.id.textview_title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;

        }
    }
}
