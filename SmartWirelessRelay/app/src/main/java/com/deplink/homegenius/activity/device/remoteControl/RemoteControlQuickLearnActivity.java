package com.deplink.homegenius.activity.device.remoteControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.device.remotecontrol.AirconditionKeyCode;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.AirconditionKeyLearnStatu;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.TvKeyCode;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.TvKeyLearnStatu;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.TvboxKeyCode;
import com.deplink.homegenius.Protocol.json.device.remotecontrol.TvboxLearnStatu;
import com.deplink.homegenius.Protocol.json.http.QueryRCCodeResponse;
import com.deplink.homegenius.Protocol.json.http.QueryTestCodeResponse;
import com.deplink.homegenius.Protocol.json.http.TestCode;
import com.deplink.homegenius.activity.device.AddDeviceNameActivity;
import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.device.remoteControl.airContorl.AirRemoteControlMianActivity;
import com.deplink.homegenius.activity.device.remoteControl.topBox.TvBoxMainActivity;
import com.deplink.homegenius.activity.device.remoteControl.tv.TvMainActivity;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import com.deplink.homegenius.manager.connect.remote.https.RestfulTools;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlListener;
import com.deplink.homegenius.manager.device.remoteControl.RemoteControlManager;
import com.deplink.homegenius.util.DataExchange;
import com.deplink.homegenius.view.toast.ToastSingleShow;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteControlQuickLearnActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "addRCActivity";
    private RelativeLayout layout_device_response;
    private Button button_test;
    private Button button_ng;
    private Button button_ok;
    private RemoteControlManager mRemoteControlManager;
    private RemoteControlListener mRemoteControlListener;
    private TextView textview_title;
    private TextView textview_test_press_4;
    private TextView textview_test_press_2;
    private FrameLayout image_back;
    private Button button_test_consecutively;
    private ImageView imageview_left;
    private ImageView imageview_right;
    private TextView textview_key_name;
    private String bandName;
    private String type;
    private String currentSelectDeviceUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remote_control);
        initViews();
        initDatas();
        initEvents();
    }
    private static final int MSG_SHOW_GET_KT_CODE = 100;
    private static final int MSG_SEND_CODE = 101;
    private static final int MSG_SHOW_GET_TV_CODE = 102;
    private static final int MSG_SHOW_GET_IPTV_CODE = 103;
    private int testCodeNumber;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            QueryTestCodeResponse code;
            switch (msg.what) {
                case MSG_SHOW_GET_KT_CODE:
                    code = (QueryTestCodeResponse) msg.obj;
                    testCodeNumber = code.getValue().size();
                    textview_test_press_4.setText("" + testCodeNumber);
                    textview_key_name.setText("开关");
                    break;
                case MSG_SHOW_GET_TV_CODE:
                    code = (QueryTestCodeResponse) msg.obj;
                    testCodeNumber = code.getValue().size();
                    textview_test_press_4.setText("" + testCodeNumber);
                    break;
                case MSG_SHOW_GET_IPTV_CODE:
                    code = (QueryTestCodeResponse) msg.obj;
                    testCodeNumber = code.getValue().size();
                    textview_test_press_4.setText("" + testCodeNumber);

                    break;
                case MSG_SEND_CODE:
                    if (currentTestCodeIndex > testCodes.size()) {
                        currentTestCodeIndex = 0;
                    } else if (currentTestCodeIndex < 0) {
                        currentTestCodeIndex = testCodes.size();
                    }
                    Log.i(TAG, "当前发送的遥控器型号下标=" + currentTestCodeIndex);
                    if (currentTestCodeIndex <= testCodes.size() && currentTestCodeIndex >= 0) {
                        Log.i(TAG, "发送测试码：第" + currentTestCodeIndex + "个：" +
                                testCodes.get(currentTestCodeIndex).getCodeData() +
                                "按键名称是：" + testCodes.get(currentTestCodeIndex).getKeyName());
                        String keyName = testCodes.get(currentTestCodeIndex).getKeyName();
                        textview_test_press_2.setText("" + (currentTestCodeIndex + 1));
                        switch (type) {
                            case "TV":
                                switch (keyName) {
                                    case "100":
                                        button_test.setBackgroundResource(R.drawable.tv100testswitch);
                                        textview_key_name.setText("开关");
                                        break;
                                    case "101":
                                        button_test.setBackgroundResource(R.drawable.tv101testmute);
                                        textview_key_name.setText("静音");
                                        break;
                                    case "102":
                                        button_test.setBackgroundResource(R.drawable.tv102teston);
                                        textview_key_name.setText("上");
                                        break;
                                    case "103":
                                        button_test.setBackgroundResource(R.drawable.tv103testleft);
                                        textview_key_name.setText("左");
                                        break;
                                    case "104":
                                        button_test.setBackgroundResource(R.drawable.tv104testsure);
                                        textview_key_name.setText("OK");
                                        break;
                                    case "105":
                                        button_test.setBackgroundResource(R.drawable.tv105testright);
                                        textview_key_name.setText("右");
                                        break;
                                    case "106":
                                        button_test.setBackgroundResource(R.drawable.tv106testnext);
                                        textview_key_name.setText("下");
                                        break;
                                    case "107":
                                        button_test.setBackgroundResource(R.drawable.tv107testvolumeless);
                                        textview_key_name.setText("音量减");
                                        break;
                                    case "108":
                                        button_test.setBackgroundResource(R.drawable.tv108testvolumeadd);
                                        textview_key_name.setText("音量加");
                                        break;
                                    case "109":
                                        button_test.setBackgroundResource(R.drawable.tv109testchannelless);
                                        textview_key_name.setText("频道减");
                                        break;
                                    case "110":
                                        button_test.setBackgroundResource(R.drawable.tv110testchanneladd);
                                        textview_key_name.setText("频道加");
                                        break;
                                    case "111":
                                        button_test.setBackgroundResource(R.drawable.tv111testmenu);
                                        textview_key_name.setText("菜单");
                                        break;
                                    case "115":
                                        button_test.setBackgroundResource(R.drawable.tv115testreturn);
                                        textview_key_name.setText("返回");
                                        break;
                                    case "116":
                                        button_test.setBackgroundResource(R.drawable.tv116testhome);
                                        textview_key_name.setText("主页");
                                        break;
                                }

                                break;
                            case "智能机顶盒遥控":
                                switch (keyName) {
                                    case "100":
                                        button_test.setBackgroundResource(R.drawable.jdh100testswitch);
                                        textview_key_name.setText("开关");
                                        break;
                                    case "101":
                                        button_test.setBackgroundResource(R.drawable.jdh101testreturn);
                                        textview_key_name.setText("返回");
                                        break;
                                    case "102":
                                        button_test.setBackgroundResource(R.drawable.jdh102teston);
                                        textview_key_name.setText("上");
                                        break;
                                    case "103":
                                        button_test.setBackgroundResource(R.drawable.jdh103testleft);
                                        textview_key_name.setText("左");
                                        break;
                                    case "104":
                                        button_test.setBackgroundResource(R.drawable.jdh104testsure);
                                        textview_key_name.setText("OK");
                                        break;
                                    case "105":
                                        button_test.setBackgroundResource(R.drawable.jdh105testright);
                                        textview_key_name.setText("右");
                                        break;
                                    case "106":
                                        button_test.setBackgroundResource(R.drawable.jdh106testnext);
                                        textview_key_name.setText("下");
                                        break;
                                    case "107":
                                        button_test.setBackgroundResource(R.drawable.jdh107testvolumeless);
                                        textview_key_name.setText("音量减");
                                        break;
                                    case "108":
                                        button_test.setBackgroundResource(R.drawable.jdh108testvolumeadd);
                                        textview_key_name.setText("音量加");
                                        break;
                                    case "109":
                                        button_test.setBackgroundResource(R.drawable.jdh109testchannelless);
                                        textview_key_name.setText("频道减");
                                        break;
                                    case "110":
                                        button_test.setBackgroundResource(R.drawable.jdh110testchanneladd);
                                        textview_key_name.setText("频道加");
                                        break;
                                    case "111":
                                        button_test.setBackgroundResource(R.drawable.jdh111testmenu);
                                        textview_key_name.setText("菜单");
                                        break;
                                    case "113":
                                        button_test.setBackgroundResource(R.drawable.jdh113testguide);
                                        textview_key_name.setText("高级");
                                        break;
                                    case "115":
                                        button_test.setBackgroundResource(R.drawable.jdh115testhome);
                                        textview_key_name.setText("返回");
                                        break;

                                }
                                break;
                        }
                        mRemoteControlManager.sendData(testCodes.get(currentTestCodeIndex).getCodeData());
                        startSend();
                    }
                    break;

            }


        }
    };
    private int controlId;
    private String brandId;
    private List<TestCode> testCodes;

    @Override
    protected void onResume() {
        super.onResume();
        mRemoteControlManager.addRemoteControlListener(mRemoteControlListener);
        isSendFirst = true;
        Log.i(TAG, "onResume type=" + type);
        switch (type) {
            case "KT":
                RestfulTools.getSingleton(this).queryTestCode("KT", bandName, "cn", new Callback<QueryTestCodeResponse>() {
                    @Override
                    public void onResponse(Call<QueryTestCodeResponse> call, Response<QueryTestCodeResponse> response) {
                        Message msg = Message.obtain();
                        msg.what = MSG_SHOW_GET_KT_CODE;
                        msg.obj = response.body();
                        Log.i(TAG, "测试码列表大小:" + response.body().getValue().size());
                        testCodes.clear();
                        testCodes.addAll(response.body().getValue());
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Call<QueryTestCodeResponse> call, Throwable t) {

                    }
                });
                break;
            case "TV":
                RestfulTools.getSingleton(this).queryTestCode("TV", bandName, "cn", new Callback<QueryTestCodeResponse>() {
                    @Override
                    public void onResponse(Call<QueryTestCodeResponse> call, Response<QueryTestCodeResponse> response) {
                        Message msg = Message.obtain();
                        msg.what = MSG_SHOW_GET_TV_CODE;
                        msg.obj = response.body();
                        testCodes.clear();
                        testCodes.addAll(response.body().getValue());
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Call<QueryTestCodeResponse> call, Throwable t) {

                    }
                });
                break;
            case "智能机顶盒遥控":
                RestfulTools.getSingleton(this).queryTestCode("STB", bandName, "cn", new Callback<QueryTestCodeResponse>() {
                    @Override
                    public void onResponse(Call<QueryTestCodeResponse> call, Response<QueryTestCodeResponse> response) {
                        Message msg = Message.obtain();
                        msg.what = MSG_SHOW_GET_IPTV_CODE;
                        msg.obj = response.body();
                        testCodes.clear();
                        testCodes.addAll(response.body().getValue());
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Call<QueryTestCodeResponse> call, Throwable t) {

                    }
                });
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mRemoteControlManager.removeRemoteControlListener(mRemoteControlListener);
    }

    private void initEvents() {
        button_ng.setOnClickListener(this);
        button_ok.setOnClickListener(this);
        image_back.setOnClickListener(this);
        button_test.setOnClickListener(this);
        imageview_left.setOnClickListener(this);
        imageview_right.setOnClickListener(this);
        button_test_consecutively.setOnClickListener(this);
        switch (type) {
            case "KT":
                button_test.setBackgroundResource(R.drawable.air99100testswitch);
                break;
            case "TV":
                break;
            case "智能机顶盒遥控":
                break;
        }
    }

    private void initViews() {
        layout_device_response = findViewById(R.id.layout_device_response);
        button_test = findViewById(R.id.button_test);
        button_ng = findViewById(R.id.button_ng);
        button_ok = findViewById(R.id.button_ok);
        textview_title = findViewById(R.id.textview_title);
        textview_test_press_4 = findViewById(R.id.textview_test_press_4);
        textview_test_press_2 = findViewById(R.id.textview_test_press_2);
        image_back = findViewById(R.id.image_back);
        button_test_consecutively = findViewById(R.id.button_test_consecutively);
        imageview_right = findViewById(R.id.imageview_right);
        imageview_left = findViewById(R.id.imageview_left);
        textview_key_name = findViewById(R.id.textview_key_name);
    }
    private void initDatas() {
        textview_title.setText("快速学习");
        bandName = getIntent().getStringExtra("bandname");
        type = getIntent().getStringExtra("type");
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this);
        currentSelectDeviceUid = mRemoteControlManager.getmSelectRemoteControlDevice().getUid();
        testCodes = new ArrayList<>();
        mRemoteControlListener=new RemoteControlListener() {
            @Override
            public void responseQueryResult(String result) {
                super.responseQueryResult(result);
                Log.i(TAG, "测试按键=" + result);
                //接收到发送红外按键的回应
                if (!iscanceled) {
                    currentTestCodeIndex++;
                }
            }
        };
    }

    private int currentButtonStage = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.imageview_right:
                currentTestCodeIndex++;
                if (currentTestCodeIndex > testCodes.size()) {
                    currentTestCodeIndex = 0;
                }
                textview_test_press_2.setText("" + currentTestCodeIndex);
                break;
            case R.id.imageview_left:
                currentTestCodeIndex--;
                if (currentTestCodeIndex < 0) {
                    currentTestCodeIndex = testCodes.size();
                }
                textview_test_press_2.setText("" + currentTestCodeIndex);
                break;
            case R.id.button_test:
                startSend();
                layout_device_response.setVisibility(View.VISIBLE);
                break;
            case R.id.button_test_consecutively:
                //TODO 没有调试的时候注释去掉
                //发送测试码
                if (LocalConnectmanager.getInstance().isLocalconnectAvailable()) {
                    if (currentButtonStage == 1) {
                        currentButtonStage = 2;
                        button_test_consecutively.setText("暂停测试");
                        button_test_consecutively.setBackgroundResource(R.drawable.radius22_bg_red_background);

                        startSend();
                    } else if (currentButtonStage == 2) {
                        currentButtonStage = 3;
                        iscanceled = true;
                        layout_device_response.setVisibility(View.VISIBLE);
                        button_test_consecutively.setText("继续测试");
                        button_test_consecutively.setBackgroundResource(R.drawable.radius22_bg_button_background);
                    } else if (currentButtonStage == 3) {
                        //TODO 继续测试
                        iscanceled = false;
                        layout_device_response.setVisibility(View.GONE);
                    }
                } else {
                    ToastSingleShow.showText(this, "没有活动网关,请检查网络");
                }

                break;
            case R.id.button_ng:
                iscanceled = true;
                layout_device_response.setVisibility(View.GONE);
                break;
            case R.id.button_ok:
                iscanceled = true;
                TestCode selectedCode = testCodes.get(currentTestCodeIndex);
                brandId = selectedCode.getBrandID();
                controlId = selectedCode.getCodeID();
                Log.i(TAG, "下载码表 type=" + type + "brandId=" + brandId + "controlId=" + controlId);
                switch (type) {
                    case "TV":
                        RestfulTools.getSingleton(this).downloadIrCode("TV", brandId, controlId, new Callback<QueryRCCodeResponse>() {
                            @Override
                            public void onResponse(Call<QueryRCCodeResponse> call, Response<QueryRCCodeResponse> response) {
                                Log.i(TAG, "下载电视码表=" + response.body().getValue().getCode() + "组号：" + response.body().getValue().getGroup());
                                saveTvKeyCode(response);
                                saveTvKeyLearnStatu();
                                switchActivity();
                            }
                            @Override
                            public void onFailure(Call<QueryRCCodeResponse> call, Throwable t) {

                            }
                        });
                        break;
                    case "KT":
                        RestfulTools.getSingleton(this).downloadIrCode("KT", selectedCode.getBrandID(), selectedCode.getCodeID(), new Callback<QueryRCCodeResponse>() {
                            @Override
                            public void onResponse(Call<QueryRCCodeResponse> call, Response<QueryRCCodeResponse> response) {
                                Log.i(TAG, "下载空调码表=" + response.body().getValue().getCode() + "组号：" + response.body().getValue().getGroup());
                                saveAirConditionKeycode(response);
                                saveAirRemoteControlLearnStatus();
                                if (mRemoteControlManager.isCurrentActionIsAddDevice()) {
                                    if(mRemoteControlManager.isCurrentActionIsAddactionQuickLearn()){
                                        mRemoteControlManager.setCurrentActionIsAddactionQuickLearn(false);
                                        Intent intent = new Intent(RemoteControlQuickLearnActivity.this, DevicesActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(RemoteControlQuickLearnActivity.this, AddDeviceNameActivity.class);
                                        intent.putExtra("DeviceType", "智能空调");
                                        startActivity(intent);
                                    }

                                } else {
                                    ToastSingleShow.showText(RemoteControlQuickLearnActivity.this, "空调遥控器按键已学习");
                                    Intent intent = new Intent(RemoteControlQuickLearnActivity.this, AirRemoteControlMianActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onFailure(Call<QueryRCCodeResponse> call, Throwable t) {

                            }
                        });
                        break;
                    case "智能机顶盒遥控":
                        RestfulTools.getSingleton(this).downloadIrCode("STB", brandId, controlId, new Callback<QueryRCCodeResponse>() {
                            @Override
                            public void onResponse(Call<QueryRCCodeResponse> call, Response<QueryRCCodeResponse> response) {
                                Log.i(TAG, "下载码表 code=" + response.body().getValue().getCode() + "group=" + response.body().getValue().getGroup());
                                saveTvBoxKeycode(response);
                                saveTvBoxKeyLearnStatus();
                                if (mRemoteControlManager.isCurrentActionIsAddDevice()) {
                                    if(mRemoteControlManager.isCurrentActionIsAddactionQuickLearn()){
                                        mRemoteControlManager.setCurrentActionIsAddactionQuickLearn(false);
                                        Intent intent = new Intent(RemoteControlQuickLearnActivity.this, DevicesActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Intent intent = new Intent(RemoteControlQuickLearnActivity.this, AddDeviceNameActivity.class);
                                        intent.putExtra("DeviceType", "智能机顶盒遥控");
                                        startActivity(intent);
                                    }

                                } else {
                                    ToastSingleShow.showText(RemoteControlQuickLearnActivity.this, "电视机顶盒遥控器按键已学习");
                                    Intent intent = new Intent(RemoteControlQuickLearnActivity.this, TvBoxMainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onFailure(Call<QueryRCCodeResponse> call, Throwable t) {

                            }
                        });
                        break;
                }

                break;


        }
    }

    private void saveAirConditionKeycode(Response<QueryRCCodeResponse> response) {
        AirconditionKeyCode mAirconditionKeyCode = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(AirconditionKeyCode.class);
        if (mAirconditionKeyCode == null) {
            mAirconditionKeyCode = new AirconditionKeyCode();
        }
        mAirconditionKeyCode.setGroupData(response.body().getValue().getGroup());
        mAirconditionKeyCode.setKeycode(response.body().getValue().getCode());
        mAirconditionKeyCode.setmAirconditionUid(currentSelectDeviceUid);
        mAirconditionKeyCode.save();
    }

    private void saveAirRemoteControlLearnStatus() {
        AirconditionKeyLearnStatu mAirconditionKeyLearnStatu = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(AirconditionKeyLearnStatu.class);
        if (mAirconditionKeyLearnStatu == null) {
            mAirconditionKeyLearnStatu = new AirconditionKeyLearnStatu();
        }
        mAirconditionKeyLearnStatu.seAllKeyLearned();
        mAirconditionKeyLearnStatu.setmAirconditionUid(currentSelectDeviceUid);
        mAirconditionKeyLearnStatu.save();
    }

    private void saveTvBoxKeyLearnStatus() {
        TvboxLearnStatu mTvboxLearnStatu = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvboxLearnStatu.class);
        if (mTvboxLearnStatu == null) {
            mTvboxLearnStatu = new TvboxLearnStatu();
        }
        mTvboxLearnStatu.seAllKeyLearned();
        mTvboxLearnStatu.setmAirconditionUid(currentSelectDeviceUid);
        mTvboxLearnStatu.save();
    }
    private String data_tvboxkey_up;
    private String data_tvboxkey_down;
    private String data_tvboxkey_left;
    private String data_tvboxkey_right;
    private String data_tvboxkey_ok;
    private String data_tvboxkey_power;
    private String data_tvboxkey_ch_reduce;
    private String data_tvboxkey_ch_plus;
    private String data_tvboxkey_volum_reduce;
    private String data_tvboxkey_volum_plus;
    private String data_tvboxkey_navi;
    private String data_tvboxkey_list;
    private String data_tvboxkey_return;
    private String data_tvboxkey_number_0;
    private String data_tvboxkey_number_1;
    private String data_tvboxkey_number_2;
    private String data_tvboxkey_number_3;
    private String data_tvboxkey_number_4;
    private String data_tvboxkey_number_5;
    private String data_tvboxkey_number_6;
    private String data_tvboxkey_number_7;
    private String data_tvboxkey_number_8;
    private String data_tvboxkey_number_9;
    /**
     * 按键对应的byte数据
     */
    private byte[] data_bytes_tvbox_key_up = new byte[2];
    private byte[] data_bytes_tvbox_key_down = new byte[2];
    private byte[] data_bytes_tvbox_key_left = new byte[2];
    private byte[] data_bytes_tvbox_key_right = new byte[2];
    private byte[] data_bytes_tvbox_key_ok = new byte[2];
    private byte[] data_bytes_tvbox_key_power = new byte[2];
    private byte[] data_bytes_tvbox_key_ch_reduce = new byte[2];
    private byte[] data_bytes_tvbox_key_ch_plus = new byte[2];
    private byte[] data_bytes_tvbox_key_volum_reduce = new byte[2];
    private byte[] data_bytes_tvbox_key_volum_plus = new byte[2];
    private byte[] data_bytes_tvbox_key_navi = new byte[2];
    private byte[] data_bytes_tvbox_key_list = new byte[2];
    private byte[] data_bytes_tvbox_key_return = new byte[2];
    private byte[] data_bytes_tvbox_key_number_0 = new byte[2];
    private byte[] data_bytes_tvbox_key_number_1 = new byte[2];
    private byte[] data_bytes_tvbox_key_number_2 = new byte[2];
    private byte[] data_bytes_tvbox_key_number_3 = new byte[2];
    private byte[] data_bytes_tvbox_key_number_4 = new byte[2];
    private byte[] data_bytes_tvbox_key_number_5 = new byte[2];
    private byte[] data_bytes_tvbox_key_number_6 = new byte[2];
    private byte[] data_bytes_tvbox_key_number_7 = new byte[2];
    private byte[] data_bytes_tvbox_key_number_8 = new byte[2];
    private byte[] data_bytes_tvbox_key_number_9 = new byte[2];
    private String mTvBoxKeyCodeString;

    private void initTvBoxKeyCodeBytesData() {
        Log.i(TAG, "mTvBoxKeyCodeString=" + mTvBoxKeyCodeString);
        byte[] codeByte = DataExchange.dbString_ToBytes(mTvBoxKeyCodeString);
        System.arraycopy(codeByte, 1, data_bytes_tvbox_key_power, 0, 2);

        System.arraycopy(codeByte, 3, data_bytes_tvbox_key_number_1, 0, 2);

        System.arraycopy(codeByte, 5, data_bytes_tvbox_key_number_2, 0, 2);

        System.arraycopy(codeByte, 7, data_bytes_tvbox_key_number_3, 0, 2);

        System.arraycopy(codeByte, 9, data_bytes_tvbox_key_number_4, 0, 2);

        System.arraycopy(codeByte, 11, data_bytes_tvbox_key_number_5, 0, 2);

        System.arraycopy(codeByte, 13, data_bytes_tvbox_key_number_6, 0, 2);

        System.arraycopy(codeByte, 15, data_bytes_tvbox_key_number_7, 0, 2);

        System.arraycopy(codeByte, 17, data_bytes_tvbox_key_number_8, 0, 2);

        System.arraycopy(codeByte, 19, data_bytes_tvbox_key_number_9, 0, 2);

        System.arraycopy(codeByte, 21, data_bytes_tvbox_key_navi, 0, 2);

        System.arraycopy(codeByte, 23, data_bytes_tvbox_key_number_0, 0, 2);

        System.arraycopy(codeByte, 25, data_bytes_tvbox_key_return, 0, 2);

        System.arraycopy(codeByte, 27, data_bytes_tvbox_key_up, 0, 2);

        System.arraycopy(codeByte, 29, data_bytes_tvbox_key_left, 0, 2);

        System.arraycopy(codeByte, 31, data_bytes_tvbox_key_ok, 0, 2);

        System.arraycopy(codeByte, 33, data_bytes_tvbox_key_right, 0, 2);

        System.arraycopy(codeByte, 35, data_bytes_tvbox_key_down, 0, 2);

        System.arraycopy(codeByte, 37, data_bytes_tvbox_key_volum_plus, 0, 2);

        System.arraycopy(codeByte, 39, data_bytes_tvbox_key_volum_reduce, 0, 2);

        System.arraycopy(codeByte, 41, data_bytes_tvbox_key_ch_plus, 0, 2);

        System.arraycopy(codeByte, 43, data_bytes_tvbox_key_ch_reduce, 0, 2);

        System.arraycopy(codeByte, 45, data_bytes_tvbox_key_list, 0, 2);

    }

    private byte[] packTvBoxData(byte func[]) {
        data = new byte[10];
        int len = 0;
        byte[] codeByte = DataExchange.dbString_ToBytes(mTvBoxKeyCodeString);
        data[len++] = (byte) 0x30;
        data[len++] = (byte) 0x00;
        data[len++] = codeByte[0];
        System.arraycopy(func, 0, data, len, 2);
        len += 2;
        byte[] last4CodeBytes = new byte[4];
        System.arraycopy(codeByte, codeByte.length - 4, last4CodeBytes, 0, 4);
        Log.i(TAG, "最后4个字节=" + DataExchange.byteArrayToHexString(last4CodeBytes));
        System.arraycopy(last4CodeBytes, 0, data, len, 4);
        len += 4;
        byte crc = 0;
        for (int i = 0; i < 9; i++) {
            crc += data[i];
        }
        data[len] = (byte) (crc & 0xff);//最后一个检验位
        Log.i(TAG, "打包机顶盒控制数据dbBytesToString=" + DataExchange.dbBytesToString(data));
        return data;
    }

    private void saveTvBoxKeycode(Response<QueryRCCodeResponse> response) {
        TvboxKeyCode mTvboxKeyCode = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvboxKeyCode.class);
        if (mTvboxKeyCode == null) {
            mTvboxKeyCode = new TvboxKeyCode();
        }
        mTvBoxKeyCodeString = response.body().getValue().getCode();
        initTvBoxKeyCodeBytesData();
        mTvboxKeyCode.setGroupData(response.body().getValue().getGroup());
        mTvboxKeyCode.setKeycode(response.body().getValue().getCode());
        mTvboxKeyCode.setmAirconditionUid(currentSelectDeviceUid);
        //保存按键的字符串
        data = packTvBoxData(data_bytes_tvbox_key_up);
        data_tvboxkey_up = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_up(data_tvboxkey_up);
        Log.i(TAG, "data_tvboxkey_up=" + data_tvboxkey_up);

        data = packTvBoxData(data_bytes_tvbox_key_down);
        data_tvboxkey_down = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_down(data_tvboxkey_down);
        Log.i(TAG, "data_tvboxkey_down=" + data_tvboxkey_down);

        data = packTvBoxData(data_bytes_tvbox_key_left);
        data_tvboxkey_left = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_left(data_tvboxkey_left);
        Log.i(TAG, "data_tvboxkey_left=" + data_tvboxkey_left);

        data = packTvBoxData(data_bytes_tvbox_key_right);
        data_tvboxkey_right = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_right(data_tvboxkey_right);
        Log.i(TAG, "data_tvboxkey_right=" + data_tvboxkey_right);

        data = packTvBoxData(data_bytes_tvbox_key_ok);
        data_tvboxkey_ok = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_ok(data_tvboxkey_ok);
        Log.i(TAG, "data_tvboxkey_ok=" + data_tvboxkey_ok);

        data = packTvBoxData(data_bytes_tvbox_key_power);
        data_tvboxkey_power = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_power(data_tvboxkey_power);
        Log.i(TAG, "data_tvboxkey_power=" + data_tvboxkey_power);

        data = packTvBoxData(data_bytes_tvbox_key_ch_reduce);
        data_tvboxkey_ch_reduce = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_ch_reduce(data_tvboxkey_ch_reduce);
        Log.i(TAG, "data_tvboxkey_ch_reduce=" + data_tvboxkey_ch_reduce);

        data = packTvBoxData(data_bytes_tvbox_key_ch_plus);
        data_tvboxkey_ch_plus = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_ch_plus(data_tvboxkey_ch_plus);
        Log.i(TAG, "data_tvboxkey_ch_plus=" + data_tvboxkey_ch_plus);

        data = packTvBoxData(data_bytes_tvbox_key_volum_reduce);
        data_tvboxkey_volum_reduce = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_volum_reduce(data_tvboxkey_volum_reduce);
        Log.i(TAG, "data_tvboxkey_volum_reduce=" + data_tvboxkey_volum_reduce);

        data = packTvBoxData(data_bytes_tvbox_key_volum_plus);
        data_tvboxkey_volum_plus = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_volum_plus(data_tvboxkey_volum_plus);
        Log.i(TAG, "data_tvboxkey_volum_plus=" + data_tvboxkey_volum_plus);

        data = packTvBoxData(data_bytes_tvbox_key_navi);
        data_tvboxkey_navi = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_navi(data_tvboxkey_navi);
        Log.i(TAG, "data_tvboxkey_navi=" + data_tvboxkey_navi);

        data = packTvBoxData(data_bytes_tvbox_key_list);
        data_tvboxkey_list = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_list(data_tvboxkey_list);
        Log.i(TAG, "data_tvboxkey_list=" + data_tvboxkey_list);

        data = packTvBoxData(data_bytes_tvbox_key_return);
        data_tvboxkey_return = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_return(data_tvboxkey_return);
        Log.i(TAG, "data_tvboxkey_return=" + data_tvboxkey_return);

        data = packTvBoxData(data_bytes_tvbox_key_number_0);
        data_tvboxkey_number_0 = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_number_0(data_tvboxkey_number_0);
        Log.i(TAG, "data_tvboxkey_number_0=" + data_tvboxkey_number_0);

        data = packTvBoxData(data_bytes_tvbox_key_number_1);
        data_tvboxkey_number_1 = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_number_1(data_tvboxkey_number_1);
        Log.i(TAG, "data_tvboxkey_number_1=" + data_tvboxkey_number_1);

        data = packTvBoxData(data_bytes_tvbox_key_number_2);
        data_tvboxkey_number_2 = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_number_2(data_tvboxkey_number_2);
        Log.i(TAG, "data_tvboxkey_number_2=" + data_tvboxkey_number_2);

        data = packTvBoxData(data_bytes_tvbox_key_number_3);
        data_tvboxkey_number_3 = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_number_3(data_tvboxkey_number_3);
        Log.i(TAG, "data_tvboxkey_number_3=" + data_tvboxkey_number_3);

        data = packTvBoxData(data_bytes_tvbox_key_number_4);
        data_tvboxkey_number_4 = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_number_4(data_tvboxkey_number_4);
        Log.i(TAG, "data_tvboxkey_number_4=" + data_tvboxkey_number_4);

        data = packTvBoxData(data_bytes_tvbox_key_number_5);
        data_tvboxkey_number_5 = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_number_5(data_tvboxkey_number_5);
        Log.i(TAG, "data_tvboxkey_number_5=" + data_tvboxkey_number_5);

        data = packTvBoxData(data_bytes_tvbox_key_number_6);
        data_tvboxkey_number_6 = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_number_6(data_tvboxkey_number_6);
        Log.i(TAG, "data_tvboxkey_number_6=" + data_tvboxkey_number_6);

        data = packTvBoxData(data_bytes_tvbox_key_number_7);
        data_tvboxkey_number_7 = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_number_7(data_tvboxkey_number_7);
        Log.i(TAG, "data_tvboxkey_number_7=" + data_tvboxkey_number_7);

        data = packTvBoxData(data_bytes_tvbox_key_number_8);
        data_tvboxkey_number_8 = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_number_8(data_tvboxkey_number_8);
        Log.i(TAG, "data_tvboxkey_number_8=" + data_tvboxkey_number_8);

        data = packTvBoxData(data_bytes_tvbox_key_number_9);
        data_tvboxkey_number_9 = DataExchange.dbBytesToString(data);
        mTvboxKeyCode.setKey_number_9(data_tvboxkey_number_9);
        Log.i(TAG, "data_tvboxkey_number_9=" + data_tvboxkey_number_9);
        mTvboxKeyCode.save();
    }


    private void switchActivity() {
        if (mRemoteControlManager.isCurrentActionIsAddDevice()) {
            if(mRemoteControlManager.isCurrentActionIsAddactionQuickLearn()){
                mRemoteControlManager.setCurrentActionIsAddactionQuickLearn(false);
                Intent intent = new Intent(RemoteControlQuickLearnActivity.this, DevicesActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(RemoteControlQuickLearnActivity.this, AddDeviceNameActivity.class);
                intent.putExtra("DeviceType", "智能电视");
                startActivity(intent);
            }

        } else {
            ToastSingleShow.showText(RemoteControlQuickLearnActivity.this, "电视遥控器按键已学习");
            Intent intent = new Intent(RemoteControlQuickLearnActivity.this, TvMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void saveTvKeyLearnStatu() {
        TvKeyLearnStatu mTvKeyLearnStatu = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvKeyLearnStatu.class);
        if (mTvKeyLearnStatu == null) {
            mTvKeyLearnStatu = new TvKeyLearnStatu();
        }
        mTvKeyLearnStatu.seAllKeyLearned();
        mTvKeyLearnStatu.setmAirconditionUid(currentSelectDeviceUid);
        mTvKeyLearnStatu.save();
    }


    private String data_key_vol_reduce;
    private String data_key_ch_add;
    private String data_key_menu;
    private String data_key_ch_reduce;
    private String data_key_vol_add;
    private String data_key_power;
    private String data_key_mute;
    private String data_key_1;
    private String data_key_2;
    private String data_key_3;
    private String data_key_4;
    private String data_key_5;
    private String data_key_6;
    private String data_key_7;
    private String data_key_8;
    private String data_key_9;
    private String data_key_0;
    private String data_key_enter;//--/-按键
    private String data_key_avtv;
    private String data_key_back;
    private String data_key_sure;
    private String data_key_up;
    private String data_key_down;
    private String data_key_left;
    private String data_key_right;
    /**
     * 按键对应的byte数据
     */
    private byte[] data_bytes_key_vol_reduce = new byte[2];
    private byte[] data_bytes_key_vol_add = new byte[2];
    private byte[] data_bytes_key_ch_reduce = new byte[2];
    private byte[] data_bytes_key_ch_add = new byte[2];
    private byte[] data_bytes_key_menu = new byte[2];
    private byte[] data_bytes_key_power = new byte[2];
    private byte[] data_bytes_key_mute = new byte[2];
    private byte[] data_bytes_key_1 = new byte[2];
    private byte[] data_bytes_key_2 = new byte[2];
    private byte[] data_bytes_key_3 = new byte[2];
    private byte[] data_bytes_key_4 = new byte[2];
    private byte[] data_bytes_key_5 = new byte[2];
    private byte[] data_bytes_key_6 = new byte[2];
    private byte[] data_bytes_key_7 = new byte[2];
    private byte[] data_bytes_key_8 = new byte[2];
    private byte[] data_bytes_key_9 = new byte[2];
    private byte[] data_bytes_key_0 = new byte[2];
    private byte[] data_bytes_key_enter = new byte[2];
    private byte[] data_bytes_key_avtv = new byte[2];
    private byte[] data_bytes_key_back = new byte[2];
    private byte[] data_bytes_key_sure = new byte[2];
    private byte[] data_bytes_key_up = new byte[2];
    private byte[] data_bytes_key_down = new byte[2];
    private byte[] data_bytes_key_left = new byte[2];
    private byte[] data_bytes_key_right = new byte[2];
    private byte[] data;
    private String mTvKeyCodeString;

    private byte[] packTvData(byte func[]) {
        data = new byte[10];
        int len = 0;
        byte[] codeByte = DataExchange.dbString_ToBytes(mTvKeyCodeString);
        data[len++] = (byte) 0x30;
        data[len++] = (byte) 0x00;
        data[len++] = codeByte[0];
        System.arraycopy(func, 0, data, len, 2);
        len += 2;
        byte[] last4CodeBytes = new byte[4];
        System.arraycopy(codeByte, codeByte.length - 4, last4CodeBytes, 0, 4);
        Log.i(TAG, "最后4个字节=" + DataExchange.byteArrayToHexString(last4CodeBytes));
        System.arraycopy(last4CodeBytes, 0, data, len, 4);
        len += 4;
        byte crc = 0;
        for (int i = 0; i < 9; i++) {
            crc += data[i];
        }
        data[len] = (byte) (crc & 0xff);//最后一个检验位
        Log.i(TAG, "打包电视控制数据dbBytesToString=" + DataExchange.dbBytesToString(data));
        return data;
    }

    private void initTvKeyCodeBytesData() {
        byte[] codeByte = DataExchange.dbString_ToBytes(mTvKeyCodeString);
        System.arraycopy(codeByte, 1, data_bytes_key_vol_reduce, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_vol_reduce));

        System.arraycopy(codeByte, 3, data_bytes_key_ch_add, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_ch_add));

        System.arraycopy(codeByte, 5, data_bytes_key_menu, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_menu));

        System.arraycopy(codeByte, 7, data_bytes_key_ch_reduce, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_ch_reduce));

        System.arraycopy(codeByte, 9, data_bytes_key_vol_add, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_vol_add));

        System.arraycopy(codeByte, 11, data_bytes_key_power, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_power));

        System.arraycopy(codeByte, 13, data_bytes_key_mute, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_mute));

        System.arraycopy(codeByte, 15, data_bytes_key_1, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_1));

        System.arraycopy(codeByte, 17, data_bytes_key_2, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_2));

        System.arraycopy(codeByte, 19, data_bytes_key_3, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_3));

        System.arraycopy(codeByte, 21, data_bytes_key_4, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_4));

        System.arraycopy(codeByte, 23, data_bytes_key_5, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_5));

        System.arraycopy(codeByte, 25, data_bytes_key_6, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_6));

        System.arraycopy(codeByte, 27, data_bytes_key_7, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_7));

        System.arraycopy(codeByte, 29, data_bytes_key_8, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_8));

        System.arraycopy(codeByte, 31, data_bytes_key_9, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_9));

        System.arraycopy(codeByte, 33, data_bytes_key_enter, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_enter));

        System.arraycopy(codeByte, 35, data_bytes_key_0, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_0));

        System.arraycopy(codeByte, 37, data_bytes_key_avtv, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_avtv));

        System.arraycopy(codeByte, 39, data_bytes_key_back, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_back));

        System.arraycopy(codeByte, 41, data_bytes_key_sure, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_sure));

        System.arraycopy(codeByte, 43, data_bytes_key_up, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_up));

        System.arraycopy(codeByte, 45, data_bytes_key_down, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_down));

        System.arraycopy(codeByte, 47, data_bytes_key_left, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_left));

        System.arraycopy(codeByte, 49, data_bytes_key_right, 0, 2);
        Log.i(TAG, "codeByte=" + "data_key_vol_reduce=" + DataExchange.byteArrayToHexString(data_bytes_key_right));

    }

    private void saveTvKeyCode(Response<QueryRCCodeResponse> response) {
        TvKeyCode mTvKeyCode = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvKeyCode.class);
        if (mTvKeyCode == null) {
            mTvKeyCode = new TvKeyCode();
        }
        mTvKeyCodeString = response.body().getValue().getCode();
        initTvKeyCodeBytesData();
        mTvKeyCode.setGroupData(response.body().getValue().getGroup());
        mTvKeyCode.setKeycode(response.body().getValue().getCode());
        //上传给服务器keycode
        mTvKeyCode.setmAirconditionUid(currentSelectDeviceUid);
        //保存按键的字符串
        data = packTvData(data_bytes_key_vol_reduce);
        data_key_vol_reduce = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_vol_reduce(data_key_vol_reduce);
        Log.i(TAG, "data_key_vol_reduce=" + data_key_vol_reduce);
        //保存按键的字符串
        data = packTvData(data_bytes_key_ch_add);
        data_key_ch_add = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_ch_add(data_key_ch_add);
        Log.i(TAG, "data_key_ch_add=" + data_key_ch_add);
        //保存按键的字符串
        data = packTvData(data_bytes_key_menu);
        data_key_menu = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_menu(data_key_menu);
        Log.i(TAG, "data_key_menu=" + data_key_menu);
        //保存按键的字符串
        data = packTvData(data_bytes_key_ch_reduce);
        data_key_ch_reduce = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_ch_reduce(data_key_ch_reduce);
        Log.i(TAG, "data_key_ch_reduce=" + data_key_ch_reduce);
        //保存按键的字符串
        data = packTvData(data_bytes_key_vol_add);
        data_key_vol_add = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_vol_add(data_key_vol_add);
        Log.i(TAG, "data_key_vol_add=" + data_key_vol_add);
        //保存按键的字符串
        data = packTvData(data_bytes_key_power);
        data_key_power = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_power(data_key_power);
        Log.i(TAG, "data_key_power=" + data_key_power);
        //保存按键的字符串
        data = packTvData(data_bytes_key_mute);
        data_key_mute = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_mute(data_key_mute);
        Log.i(TAG, "data_key_mute=" + data_key_mute);
        //保存按键的字符串
        data = packTvData(data_bytes_key_1);
        data_key_1 = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_1(data_key_1);
        Log.i(TAG, "data_key_1=" + data_key_1);
        //保存按键的字符串
        data = packTvData(data_bytes_key_2);
        data_key_2 = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_2(data_key_2);
        Log.i(TAG, "data_key_2=" + data_key_2);
        //保存按键的字符串
        data = packTvData(data_bytes_key_3);
        data_key_3 = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_3(data_key_3);
        Log.i(TAG, "data_key_3=" + data_key_3);
        //保存按键的字符串
        data = packTvData(data_bytes_key_4);
        data_key_4 = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_4(data_key_4);
        Log.i(TAG, "data_key_4=" + data_key_4);
        //保存按键的字符串
        data = packTvData(data_bytes_key_5);
        data_key_5 = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_5(data_key_5);
        Log.i(TAG, "data_key_5=" + data_key_5);
        //保存按键的字符串
        data = packTvData(data_bytes_key_6);
        data_key_6 = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_6(data_key_6);
        Log.i(TAG, "data_key_6=" + data_key_6);
        //保存按键的字符串
        data = packTvData(data_bytes_key_7);
        data_key_7 = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_7(data_key_7);
        Log.i(TAG, "data_key_7=" + data_key_7);
        //保存按键的字符串
        data = packTvData(data_bytes_key_8);
        data_key_8 = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_8(data_key_8);
        Log.i(TAG, "data_key_8=" + data_key_8);
        //保存按键的字符串
        data = packTvData(data_bytes_key_9);
        data_key_9 = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_9(data_key_9);
        Log.i(TAG, "data_key_9=" + data_key_9);
        //保存按键的字符串
        data = packTvData(data_bytes_key_0);
        data_key_0 = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_0(data_key_0);
        Log.i(TAG, "data_key_0=" + data_key_0);
        //保存按键的字符串
        data = packTvData(data_bytes_key_enter);
        data_key_enter = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_enter(data_key_enter);
        Log.i(TAG, "data_key_enter=" + data_key_enter);
        //保存按键的字符串
        data = packTvData(data_bytes_key_avtv);
        data_key_avtv = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_avtv(data_key_avtv);
        Log.i(TAG, "data_key_avtv=" + data_key_avtv);
        //保存按键的字符串
        data = packTvData(data_bytes_key_back);
        data_key_back = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_back(data_key_back);
        Log.i(TAG, "data_key_back=" + data_key_back);
        //保存按键的字符串
        data = packTvData(data_bytes_key_sure);
        data_key_sure = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_sure(data_key_sure);
        Log.i(TAG, "data_key_sure=" + data_key_sure);
        //保存按键的字符串
        data = packTvData(data_bytes_key_up);
        data_key_up = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_up(data_key_up);
        Log.i(TAG, "data_key_up=" + data_key_up);
        //保存按键的字符串
        data = packTvData(data_bytes_key_down);
        data_key_down = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_down(data_key_down);
        Log.i(TAG, "data_key_down=" + data_key_down);
        //保存按键的字符串
        data = packTvData(data_bytes_key_left);
        data_key_left = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_left(data_key_left);
        Log.i(TAG, "data_key_left=" + data_key_left);
        //保存按键的字符串
        data = packTvData(data_bytes_key_right);
        data_key_right = DataExchange.dbBytesToString(data);
        mTvKeyCode.setData_key_right(data_key_right);
        Log.i(TAG, "data_key_right=" + data_key_right);

        mTvKeyCode.save();
    }


    private static final int TIME_DIFFERENCE_BETWEEN_MESSAGE_INTERVALS = 5000;
    int currentTestCodeIndex = 0;
    private boolean isSendFirst;

    private void startSend() {
        if (!iscanceled) {
            if (testCodes.size() > 0) {
                if (currentTestCodeIndex < testCodes.size()) {
                    Message msg = Message.obtain();
                    msg.what = MSG_SEND_CODE;
                    if (isSendFirst) {
                        isSendFirst = false;
                        mHandler.sendMessage(msg);
                    } else {
                        mHandler.sendMessageDelayed(msg, TIME_DIFFERENCE_BETWEEN_MESSAGE_INTERVALS);
                    }
                }
            }
        }
    }

    private boolean iscanceled;


}
