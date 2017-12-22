package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.AirconditionKeyCode;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.AirconditionKeyLearnStatu;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.KeynotlearnDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.RemoteControlMenuDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.aircondition.AirconditionWindDirectionSelectDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.aircondition.AirconditionWindSpeedSelectDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.aircondition.Aircondition_mode_select_Dialog;

public class AirRemoteControlMianActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ARCMianActivity";
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
    private RelativeLayout layout_top_content;
    /**
     * 空调各个按键的学习状态
     */
    private boolean key_tempature_reduce;
    private boolean key_tempature_plus;
    private boolean key_power;
    private boolean key_mode_hot;
    private boolean key_mode_cold;
    private boolean key_mode_dehumit;
    private boolean key_mode_wind;
    private boolean key_mode_auto;
    private boolean key_windspeed_hight;
    private boolean key_windspeed_middle;
    private boolean key_windspeed_low;
    private boolean key_windspeed_auto;
    private boolean key_winddirection_up;
    private boolean key_winddirection_middle;
    private boolean key_winddirection_down;
    private boolean key_winddirection_auto;
    /**
     * 未学习按键的提示
     */
    private KeynotlearnDialog mKeynotlearnDialog;
    private RemoteControlManager mRemoteControlManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_remote_control);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initKeylearnStatus();
        initKeyCodeData();
    }

    private int group;
    private String code;

    private void initKeyCodeData() {
        String currentDeviceUid = mRemoteControlManager.getmSelectRemoteControlDevice().getUid();
        AirconditionKeyCode mAirconditionKeyCode =
                DataSupport.where("mAirconditionUid = ?", currentDeviceUid).findFirst(AirconditionKeyCode.class);
        if (mAirconditionKeyCode != null) {
            Log.i(TAG, "mAirconditionKeyCode=" + mAirconditionKeyCode.toString());
            group = mAirconditionKeyCode.getGroupData();
            code = mAirconditionKeyCode.getKeycode();

        }

    }

    private void initDatas() {

        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this, null);
        mKeynotlearnDialog = new KeynotlearnDialog(this);
        modeDialog = new Aircondition_mode_select_Dialog(this);
        modeDialog.setmOnModeSelectClickListener(new Aircondition_mode_select_Dialog.onModeSelectClickListener() {
            @Override
            public void onModeSelect(String selectMode) {
                textview_model.setText(selectMode);
                switch (selectMode) {
                    case "制热模式":
                        if (key_mode_hot) {
                            imageview_auto_model.setBackgroundResource(R.drawable.button_aircondition_mode_hot_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }
                        break;
                    case "制冷模式":
                        if (key_mode_cold) {
                            imageview_auto_model.setBackgroundResource(R.drawable.button_aircondition_mode_cold_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                    case "除湿模式":
                        if (key_mode_dehumit) {
                            imageview_auto_model.setBackgroundResource(R.drawable.button_aircondition_mode_dehumid_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                    case "送风模式":
                        if (key_mode_wind) {
                            imageview_auto_model.setBackgroundResource(R.drawable.button_aircondition_mode_wind_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                    case "自动模式":
                        if (key_mode_auto) {
                            imageview_auto_model.setBackgroundResource(R.drawable.button_aircondition_mode_auto_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                }

            }
        });
        windSpeedDialog = new AirconditionWindSpeedSelectDialog(this);
        windSpeedDialog.setmOnModeSelectClickListener(new AirconditionWindSpeedSelectDialog.onModeSelectClickListener() {
            @Override
            public void onModeSelect(String selectMode) {
                textview_wind_speed.setText(selectMode);
                switch (selectMode) {
                    case "高风":
                        if (key_windspeed_hight) {
                            imageview_auto_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_hight_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                    case "中风":
                        if (key_windspeed_middle) {
                            imageview_auto_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_middle_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                    case "低风":
                        if (key_windspeed_low) {
                            imageview_auto_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_low_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                    case "自动风速":
                        if (key_windspeed_auto) {
                            imageview_auto_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_auto_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                }
            }
        });
        windDirectionDialog = new AirconditionWindDirectionSelectDialog(this);
        windDirectionDialog.setmOnModeSelectClickListener(new AirconditionWindDirectionSelectDialog.onModeSelectClickListener() {
            @Override
            public void onModeSelect(String selectMode) {
                textview_wind_center.setText(selectMode);
                switch (selectMode) {
                    case "风向向上":
                        if (key_winddirection_up) {
                            imageview_wind_center.setBackgroundResource(R.drawable.button_aircondition_winddirection_up_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }


                        break;
                    case "风向居中":
                        if (key_winddirection_middle) {
                            imageview_wind_center.setBackgroundResource(R.drawable.button_aircondition_winddirection_middle_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                    case "风向向下":
                        if (key_winddirection_down) {
                            imageview_wind_center.setBackgroundResource(R.drawable.button_aircondition_winddirection_down_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                    case "自动风向":
                        if (key_winddirection_auto) {
                            imageview_wind_center.setBackgroundResource(R.drawable.button_aircondition_winddirection_auto_notlearn);
                        } else {
                            mKeynotlearnDialog.show();
                        }

                        break;
                }
            }
        });
        menu_dialog = new RemoteControlMenuDialog(this, RemoteControlMenuDialog.TYPE_AIRCONDITION);
        textview_title.setText("智能空调遥控");
        image_setting.setImageResource(R.drawable.menuicon);
    }

    private void initKeylearnStatus() {
        String currentDeviceUid = mRemoteControlManager.getmSelectRemoteControlDevice().getUid();
        AirconditionKeyLearnStatu mAirconditionKeyLearnStatu = DataSupport.where("mAirconditionUid = ?", currentDeviceUid).findFirst(AirconditionKeyLearnStatu.class);
        if (mAirconditionKeyLearnStatu != null) {
            key_tempature_reduce = mAirconditionKeyLearnStatu.isKey_tempature_reduce();
            key_tempature_plus = mAirconditionKeyLearnStatu.isKey_tempature_plus();
            key_power = mAirconditionKeyLearnStatu.isKey_power();
            key_mode_hot = mAirconditionKeyLearnStatu.isKey_mode_hot();
            key_mode_cold = mAirconditionKeyLearnStatu.isKey_mode_cold();
            key_mode_dehumit = mAirconditionKeyLearnStatu.isKey_mode_dehumit();
            key_mode_wind = mAirconditionKeyLearnStatu.isKey_mode_wind();
            key_mode_auto = mAirconditionKeyLearnStatu.isKey_mode_auto();
            key_windspeed_hight = mAirconditionKeyLearnStatu.isKey_windspeed_hight();
            key_windspeed_middle = mAirconditionKeyLearnStatu.isKey_windspeed_middle();
            key_windspeed_low = mAirconditionKeyLearnStatu.isKey_windspeed_low();
            key_windspeed_auto = mAirconditionKeyLearnStatu.isKey_windspeed_auto();
            key_winddirection_up = mAirconditionKeyLearnStatu.isKey_winddirection_up();
            key_winddirection_middle = mAirconditionKeyLearnStatu.isKey_winddirection_middle();
            key_winddirection_down = mAirconditionKeyLearnStatu.isKey_winddirection_down();
            key_winddirection_auto = mAirconditionKeyLearnStatu.isKey_winddirection_auto();
        } else {
            key_tempature_reduce = false;
            key_tempature_plus = false;
            key_power = false;
            key_mode_hot = false;
            key_mode_cold = false;
            key_mode_dehumit = false;
            key_mode_wind = false;
            key_mode_auto = false;
            key_windspeed_hight = false;
            key_windspeed_middle = false;
            key_windspeed_low = false;
            key_windspeed_auto = false;
            key_winddirection_up = false;
            key_winddirection_middle = false;
            key_winddirection_down = false;
            key_winddirection_auto = false;
        }

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
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_model = (TextView) findViewById(R.id.textview_model);
        textview_wind_center = (TextView) findViewById(R.id.textview_wind_center);
        textview_wind_speed = (TextView) findViewById(R.id.textview_wind_speed);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        image_setting = (ImageView) findViewById(R.id.image_setting);
        imageview_auto_model = (ImageView) findViewById(R.id.imageview_auto_model);
        imageview_auto_wind_speed = (ImageView) findViewById(R.id.imageview_auto_wind_speed);
        imageview_wind_center = (ImageView) findViewById(R.id.imageview_wind_center);
        imageview_power = (ImageView) findViewById(R.id.imageview_power);
        imageview_temperature_reduce = (ImageView) findViewById(R.id.imageview_temperature_reduce);
        imageview_temperature_plus = (ImageView) findViewById(R.id.imageview_temperature_plus);
        frame_setting = (FrameLayout) findViewById(R.id.frame_setting);
        layout_top_content = (RelativeLayout) findViewById(R.id.layout_top_content);
    }

    private byte[] data;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.imageview_temperature_reduce:
                if (key_tempature_reduce) {
                    //TODO
                } else {
                    mKeynotlearnDialog.show();
                }
                break;
            case R.id.imageview_temperature_plus:
                if (key_tempature_plus) {
                    //TODO
                } else {
                    mKeynotlearnDialog.show();
                }
                break;
            case R.id.frame_setting:
                menu_dialog.show();
                break;
            case R.id.imageview_auto_model:
                if (key_mode_hot || key_mode_cold || key_mode_dehumit || key_mode_wind || key_mode_auto) {
                    modeDialog.show();
                } else {
                    mKeynotlearnDialog.show();
                }
                break;
            case R.id.imageview_auto_wind_speed:
                if (key_windspeed_hight || key_windspeed_middle || key_windspeed_low || key_windspeed_auto) {
                    windSpeedDialog.show();
                } else {
                    mKeynotlearnDialog.show();
                }
                break;
            case R.id.imageview_wind_center:
                if (key_winddirection_up || key_winddirection_middle || key_winddirection_down || key_winddirection_auto) {
                    windDirectionDialog.show();
                } else {
                    mKeynotlearnDialog.show();
                }
                break;
            case R.id.imageview_power:
                if (key_power) {
                    Log.i(TAG, "code=" + code + "bytelength=" + code.getBytes().length + "length2=" + DataExchange.dbString_ToBytes(code).length);
                    if (code == null) {
                        return;
                    }
                    data = packData();
                    mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                    layout_top_content.setBackgroundResource(R.drawable.airconditioningon);
                } else {
                    mKeynotlearnDialog.show();
                }
                break;
        }
    }

    /**
     * 包装空调数据
     * @return
     */
    public byte[] packData() {
        int len = 0;
        byte[] codeByte = DataExchange.dbString_ToBytes(code);
        byte[] func = new byte[7];
        func[0] = (byte) 0x13;
        func[1] = (byte) 0x1;
        func[2] = (byte) 0x2;
        func[3] = (byte) 0x1;
        func[4] = (byte) 0x1;
        func[5] = (byte) 0x1;
        func[6] = (byte) 0x1;
        data = new byte[13 + codeByte.length];
        data[len++] = (byte) 0x30;
        data[len++] = (byte) 0x01;
        byte[] groupByte = DataExchange.intToTwoByte(group);
        System.arraycopy(groupByte, 0, data, len, 2);
        len += 2;
        System.arraycopy(func, 0, data, len, 7);
        len += 7;
        data[len++] = (byte) (codeByte[0] + 1);
        System.arraycopy(codeByte, 1, data, len, codeByte.length - 1);
        len += codeByte.length - 1;
        data[len++] = (byte) 0xff;
        byte crc = 0;
        for (int i = 0; i < len - 1; i++) {
            crc += data[i];
        }
        data[len] = (byte) (crc & 0xff);//最后一个检验位
        Log.i(TAG, "打包空调控制数据=" + DataExchange.byteArrayToHexString(data));
        Log.i(TAG, "打包空调控制数据dbBytesToString=" + DataExchange.dbBytesToString(data));
        return data;
    }
}
