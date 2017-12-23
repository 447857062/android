package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.AirconditionInitKeyValue;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.AirconditionKeyCode;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.AirconditionKeyLearnStatu;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.LearnByHandActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AirKeyNameConstant;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceTypeConstant;
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
    private ImageView imageview_model;
    private ImageView imageview_wind_speed;
    private ImageView imageview_wind_direction;
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
    private int group;
    private String code;
    private TextView textview_cancel;
    private TextView textview_tips;
    private TextView textview_temperature;
    private SeekBar progressBar;
    private int temptureProgress;

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
        initImageViewKeyBackground();
        initKeyCodeData();
    }

    private int currentMode;
    private int wind;
    private int directionHand;
    private int directionAuto;
    private int tempature;
    private int power;

    /**
     * 初始化按键的背景，学习过和未学习的按键背景不一样，点击效果也不一样
     */
    private void initImageViewKeyBackground() {
        currentMode = mAirconditionInitKeyValue.getMode();
        wind = mAirconditionInitKeyValue.getWind();
        directionHand = mAirconditionInitKeyValue.getDirectionHand();
        directionAuto = mAirconditionInitKeyValue.getDirectionAuto();
        tempature = mAirconditionInitKeyValue.getTempature();
        power = mAirconditionInitKeyValue.getKeyPower();
        textview_temperature.setText("" + tempature);
        temptureProgress = (int) (((tempature - 16) / 15.0) * 100);
        progressBar.setProgress(temptureProgress);

        if (key_power) {
            imageview_power.setBackgroundResource(R.drawable.button_power_learned);
        } else {
            imageview_power.setBackgroundResource(R.drawable.button_power_notlearn);
        }
        switch (currentMode) {
            case 0x01:
                if (key_mode_cold) {
                    imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_cold_learned);
                } else {
                    imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_cold_notlearn);
                }
                break;
            case 0x02:
                if (key_mode_dehumit) {
                    imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_dehumid_learned);
                } else {
                    imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_dehumid_notlearn);
                }
                break;
            case 0x03:
                if (key_mode_wind) {
                    imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_wind_learned);
                } else {
                    imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_wind_notlearn);
                }
                break;
            case 0x04:
                if (key_mode_hot) {
                    imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_hot_learned);
                } else {
                    imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_hot_notlearn);
                }
                break;
            case 0x05:
                if (key_mode_auto) {
                    imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_auto_learned);
                } else {
                    imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_auto_notlearn);
                }
                break;
        }
        switch (wind) {
            case 0x01:
                if (key_windspeed_low) {
                    imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_low_learned);
                } else {
                    imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_low_notlearn);
                }
                break;
            case 0x02:
                if (key_windspeed_middle) {
                    imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_middle_learned);
                } else {
                    imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_middle_notlearn);
                }
                break;
            case 0x03:
                if (key_windspeed_hight) {
                    imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_hight_learned);
                } else {
                    imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_hight_notlearn);
                }
                break;
            case 0x04:
                if (key_windspeed_auto) {
                    imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_auto_learned);
                } else {
                    imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_auto_notlearn);
                }
                break;
        }
        switch (directionAuto) {
            case 0x00:
                switch (directionHand) {
                    case 0x01:
                        if (key_winddirection_up) {
                            imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_up_learned);
                        } else {
                            imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_up_notlearn);
                        }
                        break;
                    case 0x02:
                        if (key_winddirection_middle) {
                            imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_middle_learned);
                        } else {
                            imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_middle_notlearn);
                        }
                        break;
                    case 0x03:
                        if (key_winddirection_down) {
                            imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_down_learned);
                        } else {
                            imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_down_notlearn);
                        }
                        break;
                }
                break;
            case 0x01:
                if (key_winddirection_auto) {
                    imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_auto_learned);
                } else {
                    imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_auto_notlearn);
                }

                break;
        }
        if (key_tempature_reduce) {
            imageview_temperature_reduce.setBackgroundResource(R.drawable.button_temp_reduce_learned);
        } else {
            imageview_temperature_reduce.setBackgroundResource(R.drawable.button_temp_reduce_notlearn);
        }
        if (key_tempature_plus) {
            imageview_temperature_plus.setBackgroundResource(R.drawable.button_temp_plus_learned);
        } else {
            imageview_temperature_plus.setBackgroundResource(R.drawable.button_temp_plus_notlearn);
        }

    }


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

    private AirconditionInitKeyValue mAirconditionInitKeyValue;

    private void initDatas() {
        mAirconditionInitKeyValue = DataSupport.findFirst(AirconditionInitKeyValue.class);
        if (mAirconditionInitKeyValue == null) {
            mAirconditionInitKeyValue = new AirconditionInitKeyValue();
            //设置默认值
            mAirconditionInitKeyValue.setTempature(0x19);
            mAirconditionInitKeyValue.setWind(0x01);
            mAirconditionInitKeyValue.setDirectionHand(0x02);
            mAirconditionInitKeyValue.setDirectionAuto(0x01);
            mAirconditionInitKeyValue.setMode(0x01);
            mAirconditionInitKeyValue.save();
        }
        Log.i(TAG, "mAirconditionInitKeyValue!=null" + (mAirconditionInitKeyValue != null));
        func[0] = (byte) mAirconditionInitKeyValue.getTempature();
        func[1] = (byte) mAirconditionInitKeyValue.getDirectionHand();
        func[2] = (byte) mAirconditionInitKeyValue.getDirectionAuto();
        func[3] = (byte) mAirconditionInitKeyValue.getKeyPower();
        // func[4] =  mAirconditionInitKeyValue.get;
        func[5] = (byte) mAirconditionInitKeyValue.getWind();
        func[6] = (byte) mAirconditionInitKeyValue.getMode();
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
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_MODE_HOT);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_mode_hot) {
                                imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_hot_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x02;
                                func[6] = (byte) 0x04;
                                currentMode = 0x04;
                                mAirconditionInitKeyValue.setMode(currentMode);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_hot_notlearn);
                                mKeynotlearnDialog.show();
                            }
                        }

                        break;
                    case "制冷模式":
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_MODE_COLD);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_mode_cold) {
                                imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_cold_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x02;
                                func[6] = (byte) 0x01;
                                currentMode = 0x01;
                                mAirconditionInitKeyValue.setMode(currentMode);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_cold_notlearn);
                                mKeynotlearnDialog.show();
                            }
                        }


                        break;
                    case "除湿模式":
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_MODE_DEHUMIT);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_mode_dehumit) {
                                imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_dehumid_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x02;
                                func[6] = (byte) 0x02;
                                currentMode = 0x02;
                                mAirconditionInitKeyValue.setMode(currentMode);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_dehumid_notlearn);
                                mKeynotlearnDialog.show();
                            }
                        }


                        break;
                    case "送风模式":
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_MODE_WIND);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_mode_wind) {
                                imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_wind_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x02;
                                func[6] = (byte) 0x03;
                                currentMode = 0x03;
                                mAirconditionInitKeyValue.setMode(currentMode);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_wind_notlearn);
                                mKeynotlearnDialog.show();
                            }
                        }


                        break;
                    case "自动模式":
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_MODE_AUTO);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_mode_auto) {
                                imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_auto_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x02;
                                func[6] = (byte) 0x05;
                                currentMode = 0x05;
                                mAirconditionInitKeyValue.setMode(currentMode);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_model.setBackgroundResource(R.drawable.button_aircondition_mode_auto_notlearn);
                                mKeynotlearnDialog.show();
                            }
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
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_WIND_HIGH);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_windspeed_hight) {
                                imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_hight_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x02;
                                func[1] = (byte) 0x04;
                                wind = 0x04;
                                mAirconditionInitKeyValue.setWind(wind);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_hight_notlearn);
                                mKeynotlearnDialog.show();
                            }
                        }


                        break;
                    case "中风":
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_WIND_MIDDLE);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_windspeed_middle) {
                                imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_middle_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x02;
                                func[1] = (byte) 0x03;
                                wind = 0x03;
                                mAirconditionInitKeyValue.setWind(wind);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_middle_notlearn);
                                mKeynotlearnDialog.show();
                            }
                        }


                        break;
                    case "低风":
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_WIND_LOW);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_windspeed_low) {
                                imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_low_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x02;
                                func[1] = (byte) 0x02;
                                wind = 0x02;
                                mAirconditionInitKeyValue.setWind(wind);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_low_notlearn);
                                mKeynotlearnDialog.show();
                            }
                        }


                        break;
                    case "自动风速":
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_WIND_AUTO);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_windspeed_auto) {
                                imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_auto_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x02;
                                func[1] = (byte) 0x01;
                                wind = 0x01;
                                mAirconditionInitKeyValue.setWind(wind);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_wind_speed.setBackgroundResource(R.drawable.button_aircondition_windspeed_auto_notlearn);
                                mKeynotlearnDialog.show();
                            }
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
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_UP);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_winddirection_up) {
                                imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_up_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x03;
                                func[2] = (byte) 0x01;
                                directionHand = 0x01;
                                mAirconditionInitKeyValue.setDirectionHand(directionHand);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_up_notlearn);
                                mKeynotlearnDialog.show();
                            }
                        }


                        break;
                    case "风向居中":
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_MIDDLE);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_winddirection_middle) {
                                imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_middle_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x03;
                                func[2] = (byte) 0x02;
                                directionHand = 0x02;
                                mAirconditionInitKeyValue.setDirectionHand(directionHand);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_middle_notlearn);
                                mKeynotlearnDialog.show();
                            }
                        }


                        break;
                    case "风向向下":
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_DOWN);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_winddirection_down) {
                                imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_down_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x03;
                                func[2] = (byte) 0x03;
                                directionHand = 0x03;
                                mAirconditionInitKeyValue.setDirectionHand(directionHand);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_down_notlearn);
                                mKeynotlearnDialog.show();
                            }
                        }


                        break;
                    case "自动风向":
                        if (isLearnByHand) {
                            mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_AUTO);
                            startActivity(new Intent(AirRemoteControlMianActivity.this, LearnByHandActivity.class));
                        } else {
                            if (key_winddirection_auto) {
                                imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_auto_learned);
                                if (code == null) {
                                    return;
                                }
                                func[5] = (byte) 0x04;
                                func[3] = (byte) 0x01;
                                directionAuto = 0x01;
                                mAirconditionInitKeyValue.setDirectionAuto(directionAuto);
                                mAirconditionInitKeyValue.save();

                                data = packData();
                                mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                            } else {
                                imageview_wind_direction.setBackgroundResource(R.drawable.button_aircondition_winddirection_auto_notlearn);
                                mKeynotlearnDialog.show();
                            }

                        }

                        break;
                }
            }
        });
        menu_dialog = new RemoteControlMenuDialog(this, RemoteControlMenuDialog.TYPE_AIRCONDITION);
        menu_dialog.setmLearnHandClickListener(new RemoteControlMenuDialog.onLearnHandClickListener() {
            @Override
            public void onLearnHandBtnClicked() {
                mRemoteControlManager.setCurrentLearnByHandTypeName(DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL);
                isLearnByHand = true;
                textview_cancel.setVisibility(View.VISIBLE);
                image_setting.setVisibility(View.GONE);
                textview_tips.setVisibility(View.VISIBLE);
            }
        });
        textview_title.setText("智能空调遥控");
        image_setting.setImageResource(R.drawable.menuicon);
    }

    private boolean isLearnByHand;

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
        imageview_model.setOnClickListener(this);
        frame_setting.setOnClickListener(this);
        imageview_wind_speed.setOnClickListener(this);
        imageview_wind_direction.setOnClickListener(this);
        imageview_power.setOnClickListener(this);
        imageview_temperature_reduce.setOnClickListener(this);
        imageview_temperature_plus.setOnClickListener(this);
        textview_cancel.setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged=" + progress);
                float temptureTemp = (float) (progress / 100.0) * 15 + 16;
                Log.i(TAG, "temptureTemp=" + temptureTemp);
                temptureProgress = progress;
                tempature = (int) temptureTemp;
                textview_temperature.setText("" + tempature);
                mAirconditionInitKeyValue.setTempature(tempature);
                mAirconditionInitKeyValue.save();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initViews() {
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_model = (TextView) findViewById(R.id.textview_model);
        textview_temperature = (TextView) findViewById(R.id.textview_temperature);
        textview_tips = (TextView) findViewById(R.id.textview_tips);
        textview_cancel = (TextView) findViewById(R.id.textview_cancel);
        textview_wind_center = (TextView) findViewById(R.id.textview_wind_center);
        textview_wind_speed = (TextView) findViewById(R.id.textview_wind_speed);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        image_setting = (ImageView) findViewById(R.id.image_setting);
        imageview_model = (ImageView) findViewById(R.id.imageview_auto_model);
        imageview_wind_speed = (ImageView) findViewById(R.id.imageview_auto_wind_speed);
        imageview_wind_direction = (ImageView) findViewById(R.id.imageview_wind_center);
        imageview_power = (ImageView) findViewById(R.id.imageview_power);
        imageview_temperature_reduce = (ImageView) findViewById(R.id.imageview_temperature_reduce);
        imageview_temperature_plus = (ImageView) findViewById(R.id.imageview_temperature_plus);
        frame_setting = (FrameLayout) findViewById(R.id.frame_setting);
        layout_top_content = (RelativeLayout) findViewById(R.id.layout_top_content);
        progressBar = (SeekBar) findViewById(R.id.progressBar);
    }

    private byte[] data;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_cancel:
                textview_tips.setVisibility(View.GONE);
                textview_cancel.setVisibility(View.GONE);
                image_setting.setVisibility(View.VISIBLE);
                isLearnByHand = false;
                break;
            case R.id.imageview_temperature_reduce:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_REDUCE);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_tempature_reduce) {
                        if (code == null) {
                            return;
                        }
                        Log.i(TAG, "mAirconditionInitKeyValue!=null" + (mAirconditionInitKeyValue != null));
                        if (tempature > 19) {
                            func[0] = (byte) (tempature--);
                            func[5] = (byte) (0x07);
                            textview_temperature.setText("" + tempature);
                            mAirconditionInitKeyValue.setTempature(tempature);
                            mAirconditionInitKeyValue.save();
                        }
                        data = packData();
                        mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                        temptureProgress -= 7;
                        progressBar.setProgress(temptureProgress);
                    } else {
                        mKeynotlearnDialog.show();
                    }
                }
                break;
            case R.id.imageview_temperature_plus:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_ADD);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_tempature_plus) {
                        if (code == null) {
                            return;
                        }
                        Log.i(TAG, "mAirconditionInitKeyValue!=null" + (mAirconditionInitKeyValue != null));
                        if (tempature < 30) {
                            func[0] = (byte) (tempature++);
                            func[5] = (byte) (0x06);
                            textview_temperature.setText("" + tempature);
                            mAirconditionInitKeyValue.setTempature(tempature);
                            mAirconditionInitKeyValue.save();
                        }
                        data = packData();
                        mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                        temptureProgress += 7;
                        progressBar.setProgress(temptureProgress);
                    } else {
                        mKeynotlearnDialog.show();
                    }
                }
                break;
            case R.id.frame_setting:
                menu_dialog.show();
                break;
            case R.id.imageview_auto_model:
                if (isLearnByHand) {
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_mode_hot || key_mode_cold || key_mode_dehumit || key_mode_wind || key_mode_auto) {
                        modeDialog.show();
                    } else {
                        mKeynotlearnDialog.show();
                    }
                }
                break;
            case R.id.imageview_auto_wind_speed:
                if (isLearnByHand) {
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_windspeed_hight || key_windspeed_middle || key_windspeed_low || key_windspeed_auto) {
                        windSpeedDialog.show();
                    } else {
                        mKeynotlearnDialog.show();
                    }
                }
                break;
            case R.id.imageview_wind_center:
                if (isLearnByHand) {
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_winddirection_up || key_winddirection_middle || key_winddirection_down || key_winddirection_auto) {
                        windDirectionDialog.show();
                    } else {
                        mKeynotlearnDialog.show();
                    }
                }
                break;
            case R.id.imageview_power:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(AirKeyNameConstant.KEYNAME.KEYNAME_POWER_OPEN);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_power) {
                        Log.i(TAG, "code=" + code + "bytelength=" + code.getBytes().length + "length2=" + DataExchange.dbString_ToBytes(code).length);
                        if (code == null) {
                            return;
                        }
                        Log.i(TAG, "mAirconditionInitKeyValue!=null" + (mAirconditionInitKeyValue != null));
                        func[4] = (byte) 0x01;
                        func[5] = (byte) (0x01);
                        if (power == 0x00) {
                            power = (byte) 0x01;
                            mAirconditionInitKeyValue.setKeyPower(0x01);
                            mAirconditionInitKeyValue.save();
                            layout_top_content.setBackgroundResource(R.drawable.airconditioningoff);
                        } else if (power == 0x01) {
                            power = (byte) 0x0;
                            mAirconditionInitKeyValue.setKeyPower(0x00);
                            mAirconditionInitKeyValue.save();
                            layout_top_content.setBackgroundResource(R.drawable.airconditioningon);
                        }
                        data = packData();
                        mRemoteControlManager.sendData(DataExchange.dbBytesToString(data));
                        layout_top_content.setBackgroundResource(R.drawable.airconditioningon);
                    } else {
                        mKeynotlearnDialog.show();
                    }
                }


                break;
        }
    }

    byte[] func = new byte[7];

    /**
     * 包装空调数据
     *
     * @return
     */
    public byte[] packData() {
        int len = 0;
        byte[] codeByte = DataExchange.dbString_ToBytes(code);
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
        Log.i(TAG, "打包空调控制数据dbBytesToString=" + DataExchange.dbBytesToString(data));
        return data;
    }
}
