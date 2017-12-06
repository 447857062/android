package deplink.com.smartwirelessrelay.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.doorbeel.Doorbeel_menu_Dialog;

public class DoorbeelMainActivity extends Activity implements View.OnClickListener{
    private ImageView image_back;
    private ImageView image_setting;
    private TextView textview_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorbeel_main);
        initViews();
        initEvents();
        initDatas();
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        image_setting.setOnClickListener(this);
    }

    private void initDatas() {
        textview_title.setText("智能门铃");
        image_setting.setImageResource(R.drawable.menuicon);
        doorbeelMenuDialog=new Doorbeel_menu_Dialog(this);
    }

    private void initViews() {
        textview_title = (TextView) findViewById(R.id.textview_title);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_setting = (ImageView) findViewById(R.id.image_setting);
    }
    private Doorbeel_menu_Dialog doorbeelMenuDialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.image_setting:
                doorbeelMenuDialog.show();
                break;

        }
    }
}
