package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.air;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceNameActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.remote.https.RestfulTools;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.remote.https.json.QueryRCCodeResponse;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.remote.https.json.QueryTestCodeResponse;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class addRemoteControlActivity extends Activity implements View.OnClickListener, RemoteControlListener {
    private static final String TAG = "addRCActivity";
    private ImageView imageview_back;
    private TextView textview_show;
    private RelativeLayout layout_device_response;
    private Button button_test;
    private Button button_ng;
    private Button button_ok;
    private RemoteControlManager mRemoteControlManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remote_control);
        initViews();
        initDatas();
        initEvents();
    }

    private List<String> codeDatas;
    private static final int MSG_SHOW_GET_CODE=100;
    private static final int MSG_SEND_CODE=101;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SHOW_GET_CODE:
                    QueryTestCodeResponse code = (QueryTestCodeResponse) msg.obj;
                    textview_show.setText("遥控器型号:" + code.getValue().get(0).getCodeID());
                    controlId = code.getValue().get(0).getCodeID();
                    brandId = code.getValue().get(0).getBrandID();
                    codeDatas.clear();

                    for (int i = 0; i < code.getValue().size(); i++) {
                        codeDatas.add(code.getValue().get(i).getCodeData());
                        textview_show.append("按键编号:" + code.getValue().get(i).getKeyName() + "红外数据:" + code.getValue().get(i).getCodeData() + "\n");
                    }
                    break;
                case MSG_SEND_CODE:
                    if(currentTestCodeIndex<codeDatas.size()){
                        mRemoteControlManager.sendData(codeDatas.get(currentTestCodeIndex));
                        startSend();
                    }
                    break;

            }


        }
    };
    private int controlId;
    private String brandId;

    @Override
    protected void onResume() {
        super.onResume();
        RestfulTools.getSingleton(this).queryTestCode("KT", bandName, "cn", new Callback<QueryTestCodeResponse>() {
            @Override
            public void onResponse(Call<QueryTestCodeResponse> call, Response<QueryTestCodeResponse> response) {
                Message msg = Message.obtain();
                msg.what=MSG_SHOW_GET_CODE;
                msg.obj = response.body();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<QueryTestCodeResponse> call, Throwable t) {

            }
        });
    }

    private void initEvents() {
        imageview_back.setOnClickListener(this);
        button_test.setOnClickListener(this);
        button_ng.setOnClickListener(this);
        button_ok.setOnClickListener(this);
    }

    private void initViews() {
        imageview_back = (ImageView) findViewById(R.id.imageview_back);
        textview_show = (TextView) findViewById(R.id.textview_show);
        layout_device_response = (RelativeLayout) findViewById(R.id.layout_device_response);
        button_test = (Button) findViewById(R.id.button_test);
        button_ng = (Button) findViewById(R.id.button_ng);
        button_ok = (Button) findViewById(R.id.button_ok);
    }

    private String bandName;

    private void initDatas() {
        bandName = getIntent().getStringExtra("bandname");
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this, this);
        codeDatas = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_back:
                onBackPressed();
                break;
            case R.id.button_ng:
                currentTestCodeIndex=0;
                break;
            case R.id.button_ok:
                Log.i(TAG, "下载码表 brandId=" + brandId+"controlId="+controlId);
                RestfulTools.getSingleton(this).downloadIrCode("KT", brandId, controlId, new Callback<QueryRCCodeResponse>() {
                    @Override
                    public void onResponse(Call<QueryRCCodeResponse> call, Response<QueryRCCodeResponse> response) {
                        Log.i(TAG, "下载码表=" + response.body().getValue().getCode());
                        startActivity(new Intent(addRemoteControlActivity.this, AddDeviceNameActivity.class));
                    }

                    @Override
                    public void onFailure(Call<QueryRCCodeResponse> call, Throwable t) {

                    }
                });
                break;
            case R.id.button_test:
                layout_device_response.setVisibility(View.VISIBLE);
                //发送测试码
                startSend();


                break;
        }
    }

    private static final int TIME_DIFFERENCE_BETWEEN_MESSAGE_INTERVALS = 10000;

    int currentTestCodeIndex = 0;

    private void startSend() {
        if (codeDatas.size() > 0) {
            if (currentTestCodeIndex < codeDatas.size()) {
                if (currentTestCodeIndex == 0) {
                    currentTestCodeIndex++;
                    Message msg=Message.obtain();
                    msg.what=MSG_SEND_CODE;
                    mHandler.sendMessage(msg);
                } else {
                    Message msg=Message.obtain();
                    msg.what=MSG_SEND_CODE;
                    mHandler.sendMessageDelayed(msg, TIME_DIFFERENCE_BETWEEN_MESSAGE_INTERVALS);
                }
            }
        }
    }

    @Override
    public void responseQueryResult(String result) {
        Log.i(TAG,"测试按键="+result);
    }
}
