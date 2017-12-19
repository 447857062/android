package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.RemoteControlMenuDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.aircondition.AirconditionWindDirectionSelectDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.aircondition.AirconditionWindSpeedSelectDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.aircondition.Aircondition_mode_select_Dialog;

public class AirRemoteControlMianActivity extends Activity implements View.OnClickListener{
    private static final String TAG="ARCMianActivity";
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView textview_model;
    private TextView textview_wind_speed;
    private TextView textview_wind_center;
    private ImageView image_setting;
    private ImageView imageview_auto_model;
    private ImageView imageview_auto_wind_speed;
    private ImageView imageview_wind_center;
    private Aircondition_mode_select_Dialog modeDialog;
    private AirconditionWindSpeedSelectDialog windSpeedDialog;
    private AirconditionWindDirectionSelectDialog windDirectionDialog;
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
        modeDialog.setmOnModeSelectClickListener(new Aircondition_mode_select_Dialog.onModeSelectClickListener() {
            @Override
            public void onModeSelect(String selectMode) {
                textview_model.setText(selectMode);
            }
        });
        windSpeedDialog=new AirconditionWindSpeedSelectDialog(this);
        windSpeedDialog.setmOnModeSelectClickListener(new AirconditionWindSpeedSelectDialog.onModeSelectClickListener() {
            @Override
            public void onModeSelect(String selectMode) {
                textview_wind_speed.setText(selectMode);
            }
        });
        windDirectionDialog=new AirconditionWindDirectionSelectDialog(this);
        windDirectionDialog.setmOnModeSelectClickListener(new AirconditionWindDirectionSelectDialog.onModeSelectClickListener() {
            @Override
            public void onModeSelect(String selectMode) {
                textview_wind_center.setText(selectMode);
            }
        });
        menu_dialog=new RemoteControlMenuDialog(this,RemoteControlMenuDialog.TYPE_AIRCONDITION);
        textview_title.setText("智能空调遥控");
        image_setting.setImageResource(R.drawable.menuicon);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        imageview_auto_model.setOnClickListener(this);
        frame_setting.setOnClickListener(this);
        imageview_auto_wind_speed.setOnClickListener(this);
        imageview_wind_center.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        textview_model= (TextView) findViewById(R.id.textview_model);
        textview_wind_center= (TextView) findViewById(R.id.textview_wind_center);
        textview_wind_speed= (TextView) findViewById(R.id.textview_wind_speed);
        image_back= (FrameLayout) findViewById(R.id.image_back);
        image_setting= (ImageView) findViewById(R.id.image_setting);
        imageview_auto_model= (ImageView) findViewById(R.id.imageview_auto_model);
        imageview_auto_wind_speed= (ImageView) findViewById(R.id.imageview_auto_wind_speed);
        imageview_wind_center= (ImageView) findViewById(R.id.imageview_wind_center);
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
            case R.id.imageview_auto_wind_speed:
                windSpeedDialog.show();
                break;
            case R.id.imageview_wind_center:
                windDirectionDialog.show();
                break;
        }
    }
}
