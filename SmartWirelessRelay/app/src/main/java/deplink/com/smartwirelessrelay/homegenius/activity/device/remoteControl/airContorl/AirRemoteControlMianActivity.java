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
    private ImageView imageview_power;
    private ImageView imageview_temperature_reduce;
    private ImageView imageview_temperature_plus;
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
                switch (selectMode){
                    case "制热模式":
                    imageview_auto_model.setBackgroundResource(R.drawable.button_aircondition_mode_hot_notlearn);
                    break;
                    case "制冷模式":
                    imageview_auto_model.setBackgroundResource(R.drawable.button_aircondition_mode_cold_notlearn);
                    break;
                    case "除湿模式":
                    imageview_auto_model.setBackgroundResource(R.drawable.button_aircondition_mode_dehumid_notlearn);
                    break;
                    case "送风模式":
                    imageview_auto_model.setBackgroundResource(R.drawable.button_aircondition_mode_wind_notlearn);
                    break;
                    case "自动模式":
                    imageview_auto_model.setBackgroundResource(R.drawable.button_aircondition_mode_auto_notlearn);
                    break;
                }

            }
        });
        windSpeedDialog=new AirconditionWindSpeedSelectDialog(this);
        windSpeedDialog.setmOnModeSelectClickListener(new AirconditionWindSpeedSelectDialog.onModeSelectClickListener() {
            @Override
            public void onModeSelect(String selectMode) {
                textview_wind_speed.setText(selectMode);
                switch (selectMode){
                    case "高风":
                        imageview_auto_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_hight_notlearn);
                        break;
                    case "中风":
                        imageview_auto_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_middle_notlearn);
                        break;
                    case "低风":
                        imageview_auto_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_low_notlearn);
                        break;
                    case "自动模式":
                        imageview_auto_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_auto_notlearn);
                        break;
                }
            }
        });
        windDirectionDialog=new AirconditionWindDirectionSelectDialog(this);
        windDirectionDialog.setmOnModeSelectClickListener(new AirconditionWindDirectionSelectDialog.onModeSelectClickListener() {
            @Override
            public void onModeSelect(String selectMode) {
                textview_wind_center.setText(selectMode);
                switch (selectMode){
                    case "风向向上":
                        imageview_wind_center.setBackgroundResource(R.drawable.button_aircondition_winddirection_up_notlearn);
                        break;
                    case "风向向中":
                        imageview_wind_center.setBackgroundResource(R.drawable.button_aircondition_winddirection_middle_notlearn);
                        break;
                    case "风向向下":
                        imageview_wind_center.setBackgroundResource(R.drawable.button_aircondition_winddirection_down_notlearn);
                        break;
                    case "自动风向":
                        imageview_wind_center.setBackgroundResource(R.drawable.button_aircondition_winddirection_auto_notlearn);
                        break;
                }
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
        imageview_power.setOnClickListener(this);
        imageview_temperature_reduce.setOnClickListener(this);
        imageview_temperature_plus.setOnClickListener(this);
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
        imageview_power= (ImageView) findViewById(R.id.imageview_power);
        imageview_temperature_reduce= (ImageView) findViewById(R.id.imageview_temperature_reduce);
        imageview_temperature_plus= (ImageView) findViewById(R.id.imageview_temperature_plus);
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
