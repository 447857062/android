package com.deplink.homegenius.activity.device.remoteControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.deplink.homegenius.Protocol.json.OpResult;
import com.deplink.homegenius.Protocol.json.QueryOptions;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.AirconditionKeyCode;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.AirconditionKeyLearnStatu;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.TvKeyCode;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.TvKeyLearnStatu;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.TvboxKeyCode;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.TvboxLearnStatu;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AirKeyNameConstant;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.constant.TvBoxNameConstant;
import com.deplink.homegenius.constant.TvKeyNameConstant;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlListener;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.util.WeakRefHandler;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class LearnByHandActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LearnByHandActivity";
    private Button button_cancel;
    private RemoteControlManager mRemoteControlManager;
    private String currentSelectDeviceUid;
    private RemoteControlListener mRemoteControlListener;
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            return true;
        }
    };
    private Handler mHandler = new WeakRefHandler(mCallback);
    private SDKManager manager;
    private EventCallback ec;
    private boolean isUserLogin;
    private DeleteDeviceDialog connectLostDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_by_hand);
        initViews();
        initDatas();
        initEvents();
    }
    private boolean isStartFromExperience;
    private DeviceManager mDeviceManager;
    private void initDatas() {
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this);
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this);
        isStartFromExperience = mDeviceManager.isStartFromExperience();
        if(!isStartFromExperience){
            currentSelectDeviceUid = mRemoteControlManager.getmSelectRemoteControlDevice().getUid();
        }
        mRemoteControlListener=new RemoteControlListener() {
            @Override
            public void responseQueryResult(String result) {
                super.responseQueryResult(result);
                Log.i(TAG, "学习结果=" + result);
                responseLearnResult(result);
            }
        };
        initMqttCallback();
    }

    private void responseLearnResult(String result) {
        if (result.contains("Study")) {
            //学习成功
            String currentLearnByHandType = mRemoteControlManager.getCurrentLearnByHandTypeName();
            int currentLearnByHand = mRemoteControlManager.getCurrentLearnByHandKeyName();
            Gson gson = new Gson();
            QueryOptions resultQueryOptions = gson.fromJson(result, QueryOptions.class);
            String codeData = resultQueryOptions.getData();
            Log.i(TAG, "学习结果=" + codeData);
            switch (currentLearnByHandType) {
                case DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL:
                    AirconditionKeyCode mAirconditionKeyCode = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(AirconditionKeyCode.class);
                    if (mAirconditionKeyCode == null) {
                        mAirconditionKeyCode = new AirconditionKeyCode();
                    }
                    AirconditionKeyLearnStatu mAirconditionKeyLearnStatu = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(AirconditionKeyLearnStatu.class);
                    if (mAirconditionKeyLearnStatu == null) {
                        mAirconditionKeyLearnStatu = new AirconditionKeyLearnStatu();
                    }
                    switch (currentLearnByHand) {
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_16:
                            mAirconditionKeyCode.setKey_tempature_hot_16(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_17:
                            mAirconditionKeyCode.setKey_tempature_hot_17(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_18:
                            mAirconditionKeyCode.setKey_tempature_hot_18(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_19:
                            mAirconditionKeyCode.setKey_tempature_hot_19(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_20:
                            mAirconditionKeyCode.setKey_tempature_hot_20(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_21:
                            mAirconditionKeyCode.setKey_tempature_hot_21(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_22:
                            mAirconditionKeyCode.setKey_tempature_hot_22(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_23:
                            mAirconditionKeyCode.setKey_tempature_hot_23(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_24:
                            mAirconditionKeyCode.setKey_tempature_hot_24(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_25:
                            mAirconditionKeyCode.setKey_tempature_hot_25(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_26:
                            mAirconditionKeyCode.setKey_tempature_hot_26(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_27:
                            mAirconditionKeyCode.setKey_tempature_hot_27(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_28:
                            mAirconditionKeyCode.setKey_tempature_hot_28(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_29:
                            mAirconditionKeyCode.setKey_tempature_hot_29(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_HOT_30:
                            mAirconditionKeyCode.setKey_tempature_hot_30(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_16:
                            mAirconditionKeyCode.setKey_tempature_cold_16(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_17:
                            mAirconditionKeyCode.setKey_tempature_cold_17(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_18:
                            mAirconditionKeyCode.setKey_tempature_cold_18(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_19:
                            mAirconditionKeyCode.setKey_tempature_cold_19(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_20:
                            mAirconditionKeyCode.setKey_tempature_cold_20(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_21:
                            mAirconditionKeyCode.setKey_tempature_cold_21(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_22:
                            mAirconditionKeyCode.setKey_tempature_cold_22(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_23:
                            mAirconditionKeyCode.setKey_tempature_cold_23(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_24:
                            mAirconditionKeyCode.setKey_tempature_cold_24(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_25:
                            mAirconditionKeyCode.setKey_tempature_cold_25(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_26:
                            mAirconditionKeyCode.setKey_tempature_cold_26(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_27:
                            mAirconditionKeyCode.setKey_tempature_cold_27(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_28:
                            mAirconditionKeyCode.setKey_tempature_cold_28(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_29:
                            mAirconditionKeyCode.setKey_tempature_cold_29(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_COLD_30:
                            mAirconditionKeyCode.setKey_tempature_cold_30(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_tempature_plus(true);
                            mAirconditionKeyLearnStatu.setKey_tempature_reduce(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键已学习");
                                }
                            });
                            break;


                        case AirKeyNameConstant.KEYNAME.KEYNAME_POWER_OPEN:
                            mAirconditionKeyCode.setKey_power(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_power(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"开关已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_AUTO:
                            mAirconditionKeyCode.setKey_winddirection_auto(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_winddirection_auto(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"自动风向已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_DOWN:
                            mAirconditionKeyCode.setKey_winddirection_down(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_winddirection_down(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"风向下已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_MIDDLE:
                            mAirconditionKeyCode.setKey_winddirection_middle(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_winddirection_middle(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"风向中已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_UP:
                            mAirconditionKeyCode.setKey_winddirection_up(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_winddirection_up(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"风向上已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_AUTO:
                            mAirconditionKeyCode.setKey_windspeed_auto(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_windspeed_auto(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"自动风量已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_LOW:
                            mAirconditionKeyCode.setKey_windspeed_low(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_windspeed_low(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"低风已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_MIDDLE:
                            mAirconditionKeyCode.setKey_windspeed_middle(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_windspeed_middle(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"中风已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_HIGH:
                            mAirconditionKeyCode.setKey_windspeed_hight(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_windspeed_hight(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"高风已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_MODE_HOT:
                            mAirconditionKeyCode.setKey_mode_hot(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_mode_hot(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"制热模式已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_MODE_COLD:
                            mAirconditionKeyCode.setKey_mode_cold(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_mode_cold(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"制冷模式已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_MODE_DEHUMIT:
                            mAirconditionKeyCode.setKey_mode_dehumit(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_mode_dehumit(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"除湿模式已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_MODE_WIND:
                            mAirconditionKeyCode.setKey_mode_wind(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_mode_wind(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"送风模式已学习");
                                }
                            });
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_MODE_AUTO:
                            mAirconditionKeyCode.setKey_mode_auto(codeData);
                            mAirconditionKeyCode.save();
                            mAirconditionKeyLearnStatu.setKey_mode_auto(true);
                            mAirconditionKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"自动模式已学习");
                                }
                            });
                            break;

                    }
                    break;
                case DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL:
                    TvKeyCode mTvKeyCode = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvKeyCode.class);
                    if (mTvKeyCode == null) {
                        mTvKeyCode = new TvKeyCode();
                    }
                    TvKeyLearnStatu mTvKeyLearnStatu = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvKeyLearnStatu.class);
                    if (mTvKeyLearnStatu == null) {
                        mTvKeyLearnStatu = new TvKeyLearnStatu();
                    }
                    switch (currentLearnByHand) {
                        case TvKeyNameConstant.KEYNAME.KEYNAME_CH_PLUS:
                            mTvKeyCode.setData_key_ch_add(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_ch_plus(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键频道加已学习");
                                }
                            });

                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_CH_REDUCE:
                            mTvKeyCode.setData_key_ch_reduce(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_ch_reduce(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键频道减已学习");
                                }
                            });

                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_DOWN:
                            mTvKeyCode.setData_key_down(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_down(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键下已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_HOME:
                            mTvKeyCode.setData_key_home(codeData);
                            mTvKeyCode.save();
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_LEFT:
                            mTvKeyCode.setData_key_left(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_left(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键左已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_RIGHT:
                            mTvKeyCode.setData_key_right(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_right(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键右已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_MUTE:
                            mTvKeyCode.setData_key_mute(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_mute(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键静音已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_VOL_PLUS:
                            mTvKeyCode.setData_key_vol_add(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_volum_plus(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键音量加已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_VOL_REDUCE:
                            mTvKeyCode.setData_key_vol_reduce(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_volum_reduce(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键音量减已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_POWER:
                            Log.i(TAG,"学习电源键:"+codeData);
                            mTvKeyCode.setData_key_power(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_power(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键开关已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_RETURN:
                            mTvKeyCode.setData_key_back(codeData);
                            mTvKeyCode.save();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键返回已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_SURE:
                            mTvKeyCode.setData_key_sure(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_ok(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键确认已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_0:
                            mTvKeyCode.setData_key_0(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_0(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键0已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_1:
                            mTvKeyCode.setData_key_1(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_1(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键1已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_2:
                            mTvKeyCode.setData_key_2(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_2(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键2已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_3:
                            mTvKeyCode.setData_key_3(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_3(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键3已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_4:
                            mTvKeyCode.setData_key_4(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_4(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键4已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_5:
                            mTvKeyCode.setData_key_5(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_5(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键5已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_6:
                            mTvKeyCode.setData_key_6(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_6(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键6已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_7:
                            mTvKeyCode.setData_key_7(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_7(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键7已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_8:
                            mTvKeyCode.setData_key_8(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_8(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键8已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_9:
                            mTvKeyCode.setData_key_9(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_9(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键9已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_:
                            mTvKeyCode.setData_key_9(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_left(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键-/--已学习");
                                }
                            });
                            break;
                        case TvKeyNameConstant.KEYNAME.KEYNAME_NUMBER_AVTV:
                            mTvKeyCode.setData_key_avtv(codeData);
                            mTvKeyCode.save();
                            mTvKeyLearnStatu.setKey_number_9(true);
                            mTvKeyLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键AV/TV已学习");
                                }
                            });
                            break;
                    }
                    break;
                case DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL:
                    TvboxKeyCode mTvboxKeyCode = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvboxKeyCode.class);
                    if (mTvboxKeyCode == null) {
                        mTvboxKeyCode = new TvboxKeyCode();
                    }
                    TvboxLearnStatu mTvboxLearnStatu = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvboxLearnStatu.class);
                    if (mTvboxLearnStatu == null) {
                        mTvboxLearnStatu = new TvboxLearnStatu();
                    }
                    switch (currentLearnByHand) {
                        case TvBoxNameConstant.KEYNAME.KEYNAME_POWER:
                            mTvboxKeyCode.setKey_power(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_power(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键开关已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_UP:
                            mTvboxKeyCode.setKey_up(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_up(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键上已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_DOWN:
                            mTvboxKeyCode.setKey_down(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_down(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键下已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_LEFT:
                            mTvboxKeyCode.setKey_left(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_left(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键左已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_RIGHT:
                            mTvboxKeyCode.setKey_right(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_right(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键右已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_SURE:
                            mTvboxKeyCode.setKey_ok(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_ok(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键确定已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_VOL_REDUCE:
                            mTvboxKeyCode.setKey_volum_reduce(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_volum_reduce(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键音量减已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_VOL_PLUS:
                            mTvboxKeyCode.setKey_volum_plus(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_volum_plus(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键音量加已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_CH_REDUCE:
                            mTvboxKeyCode.setKey_ch_reduce(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_ch_reduce(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键频道减已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_MENU:
                            mTvboxKeyCode.setKey_list(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_list(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键菜单已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_RETURN:
                            mTvboxKeyCode.setKey_return(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_return(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键返回已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NAVI:
                            mTvboxKeyCode.setKey_navi(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_navi(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键导航已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NUMBER_0:
                            mTvboxKeyCode.setKey_number_0(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_number_0(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键0已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NUMBER_1:
                            mTvboxKeyCode.setKey_number_1(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_number_1(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键1已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NUMBER_2:
                            mTvboxKeyCode.setKey_number_2(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_number_2(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键2已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NUMBER_3:
                            mTvboxKeyCode.setKey_number_3(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_number_3(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键3已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NUMBER_4:
                            mTvboxKeyCode.setKey_number_4(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_number_4(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键4已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NUMBER_5:
                            mTvboxKeyCode.setKey_number_5(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_number_5(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键5已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NUMBER_6:
                            mTvboxKeyCode.setKey_number_6(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_number_6(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键6已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NUMBER_7:
                            mTvboxKeyCode.setKey_number_7(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_number_7(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键7已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NUMBER_8:
                            mTvboxKeyCode.setKey_number_8(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_number_8(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键8已学习");
                                }
                            });
                            break;
                        case TvBoxNameConstant.KEYNAME.KEYNAME_NUMBER_9:
                            mTvboxKeyCode.setKey_number_9(codeData);
                            mTvboxKeyCode.saveFast();
                            mTvboxLearnStatu.setKey_number_9(true);
                            mTvboxLearnStatu.saveFast();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastSingleShow.showText(LearnByHandActivity.this,"按键9已学习");
                                }
                            });
                            break;
                    }
                    break;
            }
            LearnByHandActivity.this.finish();
        }
    }

    private void initMqttCallback() {
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new DeleteDeviceDialog(LearnByHandActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(LearnByHandActivity.this, LoginActivity.class));
            }
        });
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {
            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);

            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {

            }

            @Override
            public void notifyHomeGeniusResponse(String result) {
                super.notifyHomeGeniusResponse(result);
                Gson gson=new Gson();
                OpResult result1=gson.fromJson(result,OpResult.class);
                if(result1.getOP().equalsIgnoreCase("REPORT")
                        && result1.getMethod().equalsIgnoreCase("IrmoteV2")
                        ){
                    if(result1.getCommand().equalsIgnoreCase("Study")){
                       responseLearnResult(result);
                    }
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);

                isUserLogin = false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setContentText("当前账号已在其它设备上登录,是否重新登录");
            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();
        isStartFromExperience = mDeviceManager.isStartFromExperience();
        manager.addEventCallback(ec);
        mRemoteControlManager.addRemoteControlListener(mRemoteControlListener);
        if(!isStartFromExperience){
            mRemoteControlManager.study();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
        if(!isStartFromExperience){
            mRemoteControlManager.stopStudy();
        }
        mRemoteControlManager.removeRemoteControlListener(mRemoteControlListener);
    }
    private void initEvents() {
        button_cancel.setOnClickListener(this);
    }

    private void initViews() {
        button_cancel = findViewById(R.id.button_cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                this.finish();
                break;
        }
    }

}
