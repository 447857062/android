package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.TvKeyCode;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.TvKeyLearnStatu;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.TvboxKeyCode;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.TvboxLearnStatu;
import deplink.com.smartwirelessrelay.homegenius.constant.AirKeyNameConstant;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceTypeConstant;
import deplink.com.smartwirelessrelay.homegenius.constant.TvBoxNameConstant;
import deplink.com.smartwirelessrelay.homegenius.constant.TvKeyNameConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class LearnByHandActivity extends Activity implements View.OnClickListener, RemoteControlListener {
    private static final String TAG = "LearnByHandActivity";
    private Button button_cancel;
    private RemoteControlManager mRemoteControlManager;
    private String currentSelectDeviceUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_by_hand);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this, this);
        currentSelectDeviceUid = mRemoteControlManager.getmSelectRemoteControlDevice().getUid();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRemoteControlManager.study();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRemoteControlManager.removeRemoteControlListener(this);
    }

    private void initEvents() {
        button_cancel.setOnClickListener(this);
    }

    private void initViews() {
        button_cancel = (Button) findViewById(R.id.button_cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                this.finish();
                break;
        }
    }
    private Handler mHandler=new Handler();
    @Override
    public void responseQueryResult(String result) {
        Log.i(TAG, "学习结果=" + result);
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
                    switch (currentLearnByHand) {
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_ADD:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_TEMPTURE_REDUCE:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_POWER_OPEN:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_AUTO:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_DOWN:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_MIDDLE:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_DIRECTION_UP:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_AUTO:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_LOW:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_MIDDLE:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_WIND_HIGH:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_MODE_HOT:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_MODE_COLD:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_MODE_DEHUMIT:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_MODE_WIND:
                            break;
                        case AirKeyNameConstant.KEYNAME.KEYNAME_MODE_AUTO:
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
                            /*mTvKeyLearnStatu.setKey_h(true);
                            mTvKeyLearnStatu.saveFast();*/
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
}
