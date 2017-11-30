package deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class UpdateSmartLockUserIdActivity extends Activity implements View.OnClickListener{
    private TextView textview_title;
    private TextView textview_edit;
    private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_smart_lock_user_id);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("修改ID名称");

        textview_edit.setText("完成");
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        textview_edit= (TextView) findViewById(R.id.textview_edit);
        image_back= (ImageView) findViewById(R.id.image_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.textview_edit:


                break;


        }
    }
}
