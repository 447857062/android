package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.RemoteControlMenuDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.aircondition.Aircondition_mode_select_Dialog;

public class AirRemoteControlMianActivity extends Activity implements View.OnClickListener{
    private static final String TAG="ARCMianActivity";
    private FrameLayout image_back;
    private TextView textview_title;
    private ImageView image_setting;
    private ImageView imageview_auto_model;
    private Aircondition_mode_select_Dialog modeDialog;
    private RemoteControlMenuDialog menu_dialog;
    private FrameLayout frame_setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_remote_control);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        modeDialog=new Aircondition_mode_select_Dialog(this);
        menu_dialog=new RemoteControlMenuDialog(this,RemoteControlMenuDialog.TYPE_AIRCONDITION);
        textview_title.setText("智能空调遥控");
        image_setting.setImageResource(R.drawable.menuicon);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        imageview_auto_model.setOnClickListener(this);
        frame_setting.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (FrameLayout) findViewById(R.id.image_back);
        image_setting= (ImageView) findViewById(R.id.image_setting);
        imageview_auto_model= (ImageView) findViewById(R.id.imageview_auto_model);
        frame_setting = (FrameLayout) findViewById(R.id.frame_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.frame_setting:
                    menu_dialog.show();
                break;
            case R.id.imageview_auto_model:
                modeDialog.show();
                break;
        }
    }
}
