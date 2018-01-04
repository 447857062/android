package deplink.com.smartwirelessrelay.homegenius.activity.device.light;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class LightEditActivity extends Activity implements View.OnClickListener{
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_edit);
        initViews();
        initDatas();
        initEvents();
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("智能灯泡");
        textview_edit.setText("完成");
    }

    private void initViews() {
        image_back = findViewById(R.id.image_back);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                onBackPressed();
                break;
        }
    }
}
