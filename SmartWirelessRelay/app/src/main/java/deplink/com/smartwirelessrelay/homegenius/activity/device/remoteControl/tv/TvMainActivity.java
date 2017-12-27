package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.tv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.TvKeyCode;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.TvKeyLearnStatu;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.LearnByHandActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceTypeConstant;
import deplink.com.smartwirelessrelay.homegenius.constant.TvKeyNameConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.KeynotlearnDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.remotecontrol.RemoteControlMenuDialog;

public class TvMainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TvMainActivity";
    private FrameLayout image_back;
    private RelativeLayout layout_control_base;
    private RelativeLayout layout_control_number;
    private TextView textview_title;
    private ImageView image_setting;
    private RelativeLayout layout_title_control_base;
    private RelativeLayout layout_title_control_number;
    private View view_control_base;
    private View view_control_number;
    private TextView textview_control_base;
    private TextView textview_control_number;
    private RemoteControlMenuDialog menu_dialog;
    private FrameLayout frame_setting;
    private RemoteControlManager mRemoteControlManager;
    /**
     * 电视遥控器各个按键的学习状态
     */
    private boolean key_up;
    private boolean key_down;
    private boolean key_left;
    private boolean key_right;
    private boolean key_ok;
    private boolean key_power;
    private boolean key_ch_reduce;
    private boolean key_ch_plus;
    private boolean key_volum_reduce;
    private boolean key_volum_plus;
    private boolean key_mute;
    private boolean key_list;
    private boolean key_return;
    private boolean key_number_0;
    private boolean key_number_1;
    private boolean key_number_2;
    private boolean key_number_3;
    private boolean key_number_4;
    private boolean key_number_5;
    private boolean key_number_6;
    private boolean key_number_7;
    private boolean key_number_8;
    private boolean key_number_9;
    /**
     * 未学习按键的提示
     */
    private KeynotlearnDialog mKeynotlearnDialog;

    private ImageView imageview_power;
    private ImageView imageview_center;
    private ImageView imageview_left;
    private ImageView imageview_right;
    private ImageView imageview_top;
    private ImageView imageview_down;
    private ImageView imageview_ch_reduce;
    private ImageView imageview_ch_add;
    private ImageView imageview_volum_reduce;
    private ImageView imageview_volum_add;
    private ImageView imageview_volume_on_off;
    private ImageView imageview_control_list;
    private ImageView imageview_control_back;
    private ImageView imageview_number_1;
    private ImageView imageview_number_2;
    private ImageView imageview_number_3;
    private ImageView imageview_number_4;
    private ImageView imageview_number_5;
    private ImageView imageview_number_6;
    private ImageView imageview_number_7;
    private ImageView imageview_number_8;
    private ImageView imageview_number_9;
    private ImageView imageview_number_0;
    private TextView textview_cancel;
    private TextView textview_tips;
    private FrameLayout framelayout_center_control;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_main);
        initViews();
        initDatas();
        initEvents();
    }
    private boolean isStartFromExperience;
    @Override
    protected void onResume() {
        super.onResume();
        isStartFromExperience= DeviceManager.getInstance().isStartFromExperience();
        initKeylearnStatus();
        initImageViewKeyBackground();
        initKeyCodeData();
    }



    private TvKeyCode mTvKeyCode;
    private void initKeyCodeData() {
        if(!isStartFromExperience){
            String currentDeviceUid = mRemoteControlManager.getmSelectRemoteControlDevice().getUid();
            mTvKeyCode =
                    DataSupport.where("mAirconditionUid = ?", currentDeviceUid).findFirst(TvKeyCode.class);
            if (mTvKeyCode != null) {
                Log.i(TAG, "mAirconditionKeyCode=" + mTvKeyCode.toString());
            }
        }
    }

    /**
     * 初始化按键的背景，学习过和未学习的按键背景不一样，点击效果也不一样
     */
    private void initImageViewKeyBackground() {
        Log.i(TAG,"key_up learn statu="+key_up);
        Log.i(TAG,"key_power learn statu="+key_power);
        Log.i(TAG,"key_mute learn statu="+key_mute);
        if (key_up) {
            imageview_top.setBackgroundResource(R.drawable.button_click_up_learned);
        } else {
            imageview_top.setBackgroundResource(R.drawable.button_click_up_notlearn);
        }
        if (key_down) {
            imageview_down.setBackgroundResource(R.drawable.button_click_down_learned);

        } else {
            imageview_down.setBackgroundResource(R.drawable.button_click_down_notlearn);
        }
        if (key_left) {
            imageview_left.setBackgroundResource(R.drawable.button_click_left_learned);
        } else {
            imageview_left.setBackgroundResource(R.drawable.button_click_left_notlearn);
        }
        if (key_right) {
            imageview_right.setBackgroundResource(R.drawable.button_click_right_learned);
        } else {
            imageview_right.setBackgroundResource(R.drawable.button_click_right_notlearn);
        }
        if (key_ok) {
            imageview_center.setBackgroundResource(R.drawable.button_ok_learned);
        } else {
            imageview_center.setBackgroundResource(R.drawable.button_ok_notlearn);
        }
        if(key_up && key_down&& key_left&& key_right&& key_ok){
            framelayout_center_control.setBackgroundResource(R.drawable.alllearn);
        }else{
            framelayout_center_control.setBackgroundResource(R.drawable.alldontlearn);
        }
        if (key_power) {
            imageview_power.setBackgroundResource(R.drawable.button_power_learned);
        } else {
            imageview_power.setBackgroundResource(R.drawable.button_power_notlearn);
        }
        if (key_ch_reduce) {
            imageview_ch_reduce.setBackgroundResource(R.drawable.button_learn_ch_reduce_learned);
        } else {
            imageview_ch_reduce.setBackgroundResource(R.drawable.button_learn_ch_reduce_notlearn);
        }
        if (key_ch_plus) {
            imageview_ch_add.setBackgroundResource(R.drawable.button_learn_ch_add_learned);
        } else {
            imageview_ch_add.setBackgroundResource(R.drawable.button_learn_ch_add_notlearn);
        }
        if (key_volum_reduce) {
            imageview_volum_reduce.setBackgroundResource(R.drawable.button_volum_reduce_learned);
        } else {
            imageview_volum_reduce.setBackgroundResource(R.drawable.button_volum_reduce_notlearn);
        }
        if (key_volum_plus) {
            imageview_volum_add.setBackgroundResource(R.drawable.button_volum_add_learned);
        } else {
            imageview_volum_add.setBackgroundResource(R.drawable.button_volum_add_notlearn);
        }
        if (key_mute) {
            imageview_volume_on_off.setBackgroundResource(R.drawable.button_mute_learn);
        } else {
            imageview_volume_on_off.setBackgroundResource(R.drawable.button_mute_notlearn);
        }
        if (key_list) {
            imageview_control_list.setBackgroundResource(R.drawable.button_menu_learned);
        } else {
            imageview_control_list.setBackgroundResource(R.drawable.button_menu_notlearn);
        }
        if (key_return) {
            imageview_control_back.setBackgroundResource(R.drawable.button_back_learned);
        } else {
            imageview_control_back.setBackgroundResource(R.drawable.button_back_notlearn);
        }
        if (key_number_0) {
            imageview_number_0.setBackgroundResource(R.drawable.button_0_learn);
        } else {
            imageview_number_0.setBackgroundResource(R.drawable.button_0_notlearn);
        }
        if (key_number_1) {
            imageview_number_1.setBackgroundResource(R.drawable.button_1_learn);
        } else {
            imageview_number_1.setBackgroundResource(R.drawable.button_1_notlearn);
        }
        if (key_number_2) {
            imageview_number_2.setBackgroundResource(R.drawable.button_2_learned);
        } else {
            imageview_number_2.setBackgroundResource(R.drawable.button_2_notlearn);
        }
        if (key_number_3) {
            imageview_number_3.setBackgroundResource(R.drawable.button_3_learned);
        } else {
            imageview_number_3.setBackgroundResource(R.drawable.button_3_notlearn);
        }
        if (key_number_4) {
            imageview_number_4.setBackgroundResource(R.drawable.button_4_learned);
        } else {
            imageview_number_4.setBackgroundResource(R.drawable.button_4_notlearn);
        }
        if (key_number_5) {
            imageview_number_5.setBackgroundResource(R.drawable.button_5_learned);
        } else {
            imageview_number_5.setBackgroundResource(R.drawable.button_5_notlearn);
        }
        if (key_number_6) {
            imageview_number_6.setBackgroundResource(R.drawable.button_6_learned);
        } else {
            imageview_number_6.setBackgroundResource(R.drawable.button_6_notlearn);
        }
        if (key_number_7) {
            imageview_number_7.setBackgroundResource(R.drawable.button_7_learned);
        } else {
            imageview_number_7.setBackgroundResource(R.drawable.button_7_notlearn);
        }
        if (key_number_8) {
            imageview_number_8.setBackgroundResource(R.drawable.button_8_learned);
        } else {
            imageview_number_8.setBackgroundResource(R.drawable.button_8_notlearn);
        }
        if (key_number_9) {
            imageview_number_9.setBackgroundResource(R.drawable.button_9_learned);
        } else {
            imageview_number_9.setBackgroundResource(R.drawable.button_9_notlearn);
        }
    }

    private void initKeylearnStatus() {
        if(isStartFromExperience){
            key_up = true;
            key_down = true;
            key_left = true;
            key_right = true;
            key_ok = true;
            key_power = true;
            key_ch_reduce = true;
            key_ch_plus = true;
            key_volum_reduce = true;
            key_volum_plus = true;
            key_mute = true;
            key_list = true;
            key_return = true;
            key_number_0 = true;
            key_number_1 = true;
            key_number_2 = true;
            key_number_3 = true;
            key_number_4 = true;
            key_number_5 = true;
            key_number_6 = true;
            key_number_7 = true;
            key_number_8 = true;
            key_number_9 = true;
        }else{
            String currentDeviceUid = mRemoteControlManager.getmSelectRemoteControlDevice().getUid();
            TvKeyLearnStatu mTvKeyLearnStatu = DataSupport.where("mAirconditionUid = ?", currentDeviceUid).findFirst(TvKeyLearnStatu.class);
            if (mTvKeyLearnStatu != null) {
                key_up = mTvKeyLearnStatu.isKey_up();
                key_down = mTvKeyLearnStatu.isKey_down();
                key_left = mTvKeyLearnStatu.isKey_left();
                key_right = mTvKeyLearnStatu.isKey_right();
                key_ok = mTvKeyLearnStatu.isKey_ok();
                key_power = mTvKeyLearnStatu.isKey_power();
                key_ch_reduce = mTvKeyLearnStatu.isKey_ch_reduce();
                key_ch_plus = mTvKeyLearnStatu.isKey_ch_plus();
                key_volum_reduce = mTvKeyLearnStatu.isKey_volum_reduce();
                key_volum_plus = mTvKeyLearnStatu.isKey_volum_plus();
                key_mute = mTvKeyLearnStatu.isKey_mute();
                key_list = mTvKeyLearnStatu.isKey_list();
                key_return = mTvKeyLearnStatu.isKey_return();
                key_number_0 = mTvKeyLearnStatu.isKey_number_0();
                key_number_1 = mTvKeyLearnStatu.isKey_number_1();
                key_number_2 = mTvKeyLearnStatu.isKey_number_2();
                key_number_3 = mTvKeyLearnStatu.isKey_number_3();
                key_number_4 = mTvKeyLearnStatu.isKey_number_4();
                key_number_5 = mTvKeyLearnStatu.isKey_number_5();
                key_number_6 = mTvKeyLearnStatu.isKey_number_6();
                key_number_7 = mTvKeyLearnStatu.isKey_number_7();
                key_number_8 = mTvKeyLearnStatu.isKey_number_8();
                key_number_9 = mTvKeyLearnStatu.isKey_number_9();
            } else {
                key_up = false;
                key_down = false;
                key_left = false;
                key_right = false;
                key_ok = false;
                key_power = false;
                key_ch_reduce = false;
                key_ch_plus = false;
                key_volum_reduce = false;
                key_volum_plus = false;
                key_mute = false;
                key_list = false;
                key_return = false;
                key_number_0 = false;
                key_number_1 = false;
                key_number_2 = false;
                key_number_3 = false;
                key_number_4 = false;
                key_number_5 = false;
                key_number_6 = false;
                key_number_7 = false;
                key_number_8 = false;
                key_number_9 = false;
            }
        }

    }

    private boolean isLearnByHand;

    private void initDatas() {
        mKeynotlearnDialog = new KeynotlearnDialog(this);
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this, null);
        textview_title.setText("电视遥控");
        menu_dialog = new RemoteControlMenuDialog(this, RemoteControlMenuDialog.TYPE_TV);
        menu_dialog.setmLearnHandClickListener(new RemoteControlMenuDialog.onLearnHandClickListener() {
            @Override
            public void onLearnHandBtnClicked() {
                mRemoteControlManager.setCurrentLearnByHandTypeName(DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL);
                isLearnByHand = true;
                textview_cancel.setVisibility(View.VISIBLE);
                image_setting.setVisibility(View.GONE);
                textview_tips.setVisibility(View.VISIBLE);
            }
        });
        image_setting.setImageResource(R.drawable.menuicon);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_cancel.setOnClickListener(this);
        layout_title_control_base.setOnClickListener(this);
        layout_title_control_number.setOnClickListener(this);
        frame_setting.setOnClickListener(this);
        imageview_power.setOnClickListener(this);
        imageview_center.setOnClickListener(this);
        imageview_left.setOnClickListener(this);
        imageview_right.setOnClickListener(this);
        imageview_top.setOnClickListener(this);
        imageview_down.setOnClickListener(this);
        imageview_ch_reduce.setOnClickListener(this);
        imageview_ch_add.setOnClickListener(this);
        imageview_volum_reduce.setOnClickListener(this);
        imageview_volum_add.setOnClickListener(this);
        imageview_volume_on_off.setOnClickListener(this);
        imageview_control_list.setOnClickListener(this);
        imageview_control_back.setOnClickListener(this);
        imageview_number_1.setOnClickListener(this);
        imageview_number_2.setOnClickListener(this);
        imageview_number_3.setOnClickListener(this);
        imageview_number_4.setOnClickListener(this);
        imageview_number_5.setOnClickListener(this);
        imageview_number_6.setOnClickListener(this);
        imageview_number_7.setOnClickListener(this);
        imageview_number_8.setOnClickListener(this);
        imageview_number_9.setOnClickListener(this);
        imageview_number_0.setOnClickListener(this);
    }

    private void initViews() {
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_tips = (TextView) findViewById(R.id.textview_tips);
        textview_cancel = (TextView) findViewById(R.id.textview_cancel);
        image_setting = (ImageView) findViewById(R.id.image_setting);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        layout_title_control_base = (RelativeLayout) findViewById(R.id.layout_title_control_base);
        layout_title_control_number = (RelativeLayout) findViewById(R.id.layout_title_control_number);
        layout_control_base = (RelativeLayout) findViewById(R.id.layout_control_base);
        layout_control_number = (RelativeLayout) findViewById(R.id.layout_control_number);
        view_control_base = findViewById(R.id.view_control_base);
        view_control_number = findViewById(R.id.view_control_number);
        textview_control_base = (TextView) findViewById(R.id.textview_control_base);
        textview_control_number = (TextView) findViewById(R.id.textview_control_number);
        frame_setting = (FrameLayout) findViewById(R.id.frame_setting);
        imageview_power = (ImageView) findViewById(R.id.imageview_power);
        imageview_center = (ImageView) findViewById(R.id.imageview_center);
        imageview_power = (ImageView) findViewById(R.id.imageview_power);
        imageview_left = (ImageView) findViewById(R.id.imageview_left);
        imageview_right = (ImageView) findViewById(R.id.imageview_right);
        imageview_top = (ImageView) findViewById(R.id.imageview_top);
        imageview_down = (ImageView) findViewById(R.id.imageview_down);
        imageview_ch_reduce = (ImageView) findViewById(R.id.imageview_ch_reduce);
        imageview_ch_add = (ImageView) findViewById(R.id.imageview_ch_add);
        imageview_volum_reduce = (ImageView) findViewById(R.id.imageview_volum_reduce);
        imageview_volum_add = (ImageView) findViewById(R.id.imageview_volum_add);
        imageview_volume_on_off = (ImageView) findViewById(R.id.imageview_volume_on_off);
        imageview_control_list = (ImageView) findViewById(R.id.imageview_control_list);
        imageview_control_back = (ImageView) findViewById(R.id.imageview_control_back);
        imageview_number_1 = (ImageView) findViewById(R.id.imageview_number_1);
        imageview_number_2 = (ImageView) findViewById(R.id.imageview_number_2);
        imageview_number_3 = (ImageView) findViewById(R.id.imageview_number_3);
        imageview_number_4 = (ImageView) findViewById(R.id.imageview_number_4);
        imageview_number_5 = (ImageView) findViewById(R.id.imageview_number_5);
        imageview_number_6 = (ImageView) findViewById(R.id.imageview_number_6);
        imageview_number_7 = (ImageView) findViewById(R.id.imageview_number_7);
        imageview_number_8 = (ImageView) findViewById(R.id.imageview_number_8);
        imageview_number_9 = (ImageView) findViewById(R.id.imageview_number_9);
        imageview_number_0 = (ImageView) findViewById(R.id.imageview_number_0);
        framelayout_center_control = (FrameLayout) findViewById(R.id.framelayout_center_control);
    }

    private Intent intent;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frame_setting:
                menu_dialog.show();
                break;
            case R.id.textview_cancel:
                textview_tips.setVisibility(View.GONE);
                textview_cancel.setVisibility(View.GONE);
                image_setting.setVisibility(View.VISIBLE);
                isLearnByHand = false;
                break;
            case R.id.imageview_power:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_POWER);
                    intent = new Intent(this, LearnByHandActivity.class);
                    startActivity(intent);
                } else {
                    if (key_power) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            Log.i(TAG,"电源键:"+mTvKeyCode.getData_key_power());
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_power());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_center:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_SURE);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_ok) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_enter());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_left:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_LEFT);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_left) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_left());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_right:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_RIGHT);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_right) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_right());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_top:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_UP);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_up) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_up());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_down:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_DOWN);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_down) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_down());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_ch_reduce:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_CH_REDUCE);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_ch_reduce) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_ch_reduce());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_ch_add:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_CH_PLUS);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_ch_plus) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_ch_add());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_volum_reduce:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_VOL_REDUCE);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_volum_reduce) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_vol_reduce());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_volum_add:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_VOL_PLUS);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_volum_plus) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_vol_add());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_volume_on_off:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_MUTE);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_mute) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_mute());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_control_list:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_MUTE);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_list) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_mute());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_control_back:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_RETURN);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_return) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_back());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_number_1:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_1);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_number_1) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_1());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_number_2:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_2);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_number_2) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_2());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_number_3:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_3);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_number_3) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_3());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_number_4:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_4);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_number_4) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_4());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_number_5:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_5);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_number_5) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_5());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_number_6:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_6);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_number_6) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_6());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_number_7:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_7);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_number_7) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_7());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_number_8:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_8);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_number_8) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_8());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_number_9:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_9);
                    startActivity(new Intent(this, LearnByHandActivity.class));
                } else {
                    if (key_number_9) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_9());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.imageview_number_0:
                if (isLearnByHand) {
                    mRemoteControlManager.setCurrentLearnByHandKeyName(TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_0);
                    intent = new Intent(this, LearnByHandActivity.class);
                    startActivity(intent);
                } else {
                    if (key_number_0) {
                        if(!isStartFromExperience){
                            if (mTvKeyCode == null) {
                                return;
                            }
                            mRemoteControlManager.sendData(mTvKeyCode.getData_key_0());
                        }

                    } else {
                        mKeynotlearnDialog.show();
                    }
                }

                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.layout_title_control_base:
                layout_control_base.setVisibility(View.VISIBLE);
                layout_control_number.setVisibility(View.GONE);
                view_control_base.setVisibility(View.VISIBLE);
                view_control_number.setVisibility(View.GONE);
                textview_control_base.setTextColor(getResources().getColor(R.color.title_blue_bg));
                textview_control_number.setTextColor(getResources().getColor(R.color.huise));
                break;
            case R.id.layout_title_control_number:
                layout_control_base.setVisibility(View.GONE);
                layout_control_number.setVisibility(View.VISIBLE);
                view_control_base.setVisibility(View.GONE);
                view_control_number.setVisibility(View.VISIBLE);
                textview_control_base.setTextColor(getResources().getColor(R.color.huise));
                textview_control_number.setTextColor(getResources().getColor(R.color.title_blue_bg));
                break;
        }
    }

}
