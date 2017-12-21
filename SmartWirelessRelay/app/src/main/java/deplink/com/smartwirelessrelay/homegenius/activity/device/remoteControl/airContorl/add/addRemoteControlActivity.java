package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.AirconditionKeyCode;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.AirconditionKeyLearnStatu;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.TvKeyCode;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.TvKeyLearnStatu;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.TvboxKeyCode;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol.TvboxLearnStatu;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.http.QueryRCCodeResponse;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.http.QueryTestCodeResponse;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.http.TestCode;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceNameActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl.AirRemoteControlMianActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.topBox.TvBoxMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.tv.TvMainActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.remote.https.RestfulTools;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRemoteControlActivity extends Activity implements View.OnClickListener, RemoteControlListener {
    private static final String TAG = "addRCActivity";
    private RelativeLayout layout_device_response;
    private Button button_test;
    private Button button_ng;
    private Button button_ok;
    private RemoteControlManager mRemoteControlManager;
    private TextView textview_title;
    private TextView textview_test_press_4;
    private TextView textview_test_press_2;
    private FrameLayout image_back;

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
                    for (int i = 0; i < code.getValue().size(); i++) {
                        Log.i(TAG, "按键名称:" + code.getValue().get(i).getKeyName());
                    }
                    break;
                case MSG_SHOW_GET_TV_CODE:
                    code = (QueryTestCodeResponse) msg.obj;
                    textview_test_press_4.setText("" + code.getValue().size());
                    for (int i = 0; i < code.getValue().size(); i++) {
                        Log.i(TAG, "按键名称:" + code.getValue().get(i).getKeyName());
                    }
                    break;
                case MSG_SHOW_GET_IPTV_CODE:
                    code = (QueryTestCodeResponse) msg.obj;
                    textview_test_press_4.setText("" + code.getValue().size());
                    for (int i = 0; i < code.getValue().size(); i++) {
                        Log.i(TAG, "按键名称:" + code.getValue().get(i).getKeyName());
                    }
                    break;
                case MSG_SEND_CODE:
                    if (currentTestCodeIndex < testCodes.size()) {
                        Log.i(TAG, "发送测试码：第" + currentTestCodeIndex + "个：" +
                                testCodes.get(currentTestCodeIndex).getCodeData() +
                                "按键名称是：" + testCodes.get(currentTestCodeIndex).getKeyName());
                        textview_test_press_2.setText("" + (currentTestCodeIndex + 1));
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

    private void initEvents() {
        button_test.setOnClickListener(this);
        button_ng.setOnClickListener(this);
        button_ok.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        layout_device_response = (RelativeLayout) findViewById(R.id.layout_device_response);
        button_test = (Button) findViewById(R.id.button_test);
        button_ng = (Button) findViewById(R.id.button_ng);
        button_ok = (Button) findViewById(R.id.button_ok);
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_test_press_4 = (TextView) findViewById(R.id.textview_test_press_4);
        textview_test_press_2 = (TextView) findViewById(R.id.textview_test_press_2);
        image_back = (FrameLayout) findViewById(R.id.image_back);
    }

    private String bandName;
    private String type;
    private String currentSelectDeviceUid;

    private void initDatas() {
        textview_title.setText("快速学习");
        bandName = getIntent().getStringExtra("bandname");
        type = getIntent().getStringExtra("type");
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this, this);
        currentSelectDeviceUid = mRemoteControlManager.getmSelectRemoteControlDevice().getUid();
        testCodes = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_ng:
                iscanceled = true;
                currentTestCodeIndex = 0;
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
                                TvKeyCode mTvKeyCode = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvKeyCode.class);
                                if (mTvKeyCode == null) {
                                    mTvKeyCode = new TvKeyCode();
                                }
                                mTvKeyCode.setGroupData(response.body().getValue().getGroup());
                                mTvKeyCode.setKeycode(response.body().getValue().getCode());
                                mTvKeyCode.setmAirconditionUid(currentSelectDeviceUid);
                                mTvKeyCode.save();
                                TvKeyLearnStatu mTvKeyLearnStatu = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvKeyLearnStatu.class);
                                if (mTvKeyLearnStatu == null) {
                                    mTvKeyLearnStatu = new TvKeyLearnStatu();
                                }
                                mTvKeyLearnStatu.seAllKeyLearned();
                                mTvKeyLearnStatu.setmAirconditionUid(currentSelectDeviceUid);
                                mTvKeyLearnStatu.save();
                                if (mRemoteControlManager.isCurrentActionIsAddDevice()) {
                                    Intent intent = new Intent(AddRemoteControlActivity.this, AddDeviceNameActivity.class);
                                    intent.putExtra("DeviceType", "智能电视");
                                    startActivity(intent);
                                } else {
                                    ToastSingleShow.showText(AddRemoteControlActivity.this, "电视遥控器按键已学习");
                                    Intent intent = new Intent(AddRemoteControlActivity.this, TvMainActivity.class);
                                    startActivity(intent);
                                }

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

                                AirconditionKeyCode mAirconditionKeyCode = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(AirconditionKeyCode.class);
                                if (mAirconditionKeyCode == null) {
                                    mAirconditionKeyCode = new AirconditionKeyCode();
                                }
                                mAirconditionKeyCode.setGroupData(response.body().getValue().getGroup());
                                mAirconditionKeyCode.setKeycode(response.body().getValue().getCode());
                                mAirconditionKeyCode.setmAirconditionUid(currentSelectDeviceUid);
                                mAirconditionKeyCode.save();
                                AirconditionKeyLearnStatu mAirconditionKeyLearnStatu = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(AirconditionKeyLearnStatu.class);
                                if (mAirconditionKeyLearnStatu == null) {
                                    mAirconditionKeyLearnStatu = new AirconditionKeyLearnStatu();
                                }
                                mAirconditionKeyLearnStatu.seAllKeyLearned();
                                mAirconditionKeyLearnStatu.setmAirconditionUid(currentSelectDeviceUid);
                                mAirconditionKeyLearnStatu.save();
                                if (mRemoteControlManager.isCurrentActionIsAddDevice()) {
                                    Intent intent = new Intent(AddRemoteControlActivity.this, AddDeviceNameActivity.class);
                                    intent.putExtra("DeviceType", "智能空调");
                                    startActivity(intent);
                                } else {
                                    ToastSingleShow.showText(AddRemoteControlActivity.this, "空调遥控器按键已学习");
                                    Intent intent = new Intent(AddRemoteControlActivity.this, AirRemoteControlMianActivity.class);
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

                                TvboxKeyCode mTvboxKeyCode = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvboxKeyCode.class);
                                if (mTvboxKeyCode == null) {
                                    mTvboxKeyCode = new TvboxKeyCode();
                                }
                                mTvboxKeyCode.setGroupData(response.body().getValue().getGroup());
                                mTvboxKeyCode.setKeycode(response.body().getValue().getCode());
                                mTvboxKeyCode.setmAirconditionUid(currentSelectDeviceUid);
                                mTvboxKeyCode.save();
                                TvboxLearnStatu mTvboxLearnStatu = DataSupport.where("mAirconditionUid = ?", currentSelectDeviceUid).findFirst(TvboxLearnStatu.class);
                                if (mTvboxLearnStatu == null) {
                                    mTvboxLearnStatu = new TvboxLearnStatu();
                                }
                                mTvboxLearnStatu.seAllKeyLearned();
                                mTvboxLearnStatu.setmAirconditionUid(currentSelectDeviceUid);
                                mTvboxLearnStatu.save();
                                if (mRemoteControlManager.isCurrentActionIsAddDevice()) {
                                    Intent intent = new Intent(AddRemoteControlActivity.this, AddDeviceNameActivity.class);
                                    intent.putExtra("DeviceType", "智能机顶盒遥控");
                                    startActivity(intent);
                                } else {
                                    ToastSingleShow.showText(AddRemoteControlActivity.this, "电视机顶盒遥控器按键已学习");
                                    Intent intent = new Intent(AddRemoteControlActivity.this, TvBoxMainActivity.class);
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
            case R.id.button_test:
                //TODO 没有调试的时候注释去掉
                //发送测试码
                // if (LocalConnectmanager.getInstance().isLocalconnectAvailable()) {
                layout_device_response.setVisibility(View.VISIBLE);
                startSend();
                //  } else {
                //      ToastSingleShow.showText(this, "没有活动网关,请检查网络");
                //   }

                break;
        }
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

    @Override
    public void responseQueryResult(String result) {
        Log.i(TAG, "测试按键=" + result);
        //接收到发送红外按键的回应
        if (!iscanceled) {
            currentTestCodeIndex++;
        }
    }
}
