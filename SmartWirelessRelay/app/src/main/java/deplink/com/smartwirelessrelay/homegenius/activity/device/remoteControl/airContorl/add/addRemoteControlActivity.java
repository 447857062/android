package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl.airContorl.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.http.QueryRCCodeResponse;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.http.QueryTestCodeResponse;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceNameActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.remote.https.RestfulTools;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class addRemoteControlActivity extends Activity implements View.OnClickListener, RemoteControlListener {
    private static final String TAG = "addRCActivity";
    private RelativeLayout layout_device_response;
    private Button button_test;
    private Button button_ng;
    private Button button_ok;
    private RemoteControlManager mRemoteControlManager;
    private TextView textview_title;
    private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_add_remote_control);
        initViews();
        initDatas();
        initEvents();
    }

    private List<String> codeDatas;
    private static final int MSG_SHOW_GET_KT_CODE =100;
    private static final int MSG_SEND_CODE=101;
    private static final int MSG_SHOW_GET_TV_CODE =102;
    private static final int MSG_SHOW_GET_IPTV_CODE =103;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            QueryTestCodeResponse code;
            switch (msg.what){
                case MSG_SHOW_GET_KT_CODE:
                     code = (QueryTestCodeResponse) msg.obj;

                    controlId = code.getValue().get(0).getCodeID();
                    brandId = code.getValue().get(0).getBrandID();
                    codeDatas.clear();


                    break;
                case MSG_SHOW_GET_TV_CODE:
                     code = (QueryTestCodeResponse) msg.obj;

                    controlId = code.getValue().get(0).getCodeID();
                    brandId = code.getValue().get(0).getBrandID();
                    codeDatas.clear();

                    break;
                case MSG_SHOW_GET_IPTV_CODE:
                     code = (QueryTestCodeResponse) msg.obj;

                    controlId = code.getValue().get(0).getCodeID();
                    brandId = code.getValue().get(0).getBrandID();
                    codeDatas.clear();

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
        switch (type){
            case "KT":
                RestfulTools.getSingleton(this).queryTestCode("KT", bandName, "cn", new Callback<QueryTestCodeResponse>() {
                    @Override
                    public void onResponse(Call<QueryTestCodeResponse> call, Response<QueryTestCodeResponse> response) {
                        Message msg = Message.obtain();
                        msg.what= MSG_SHOW_GET_KT_CODE;
                        msg.obj = response.body();
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
                        msg.what= MSG_SHOW_GET_TV_CODE;
                        msg.obj = response.body();
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Call<QueryTestCodeResponse> call, Throwable t) {

                    }
                });
                break;
            case "智能机顶盒遥控":
                RestfulTools.getSingleton(this).queryTestCode("IPTV", bandName, "cn", new Callback<QueryTestCodeResponse>() {
                    @Override
                    public void onResponse(Call<QueryTestCodeResponse> call, Response<QueryTestCodeResponse> response) {
                        Message msg = Message.obtain();
                        msg.what= MSG_SHOW_GET_IPTV_CODE;
                        msg.obj = response.body();
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
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (ImageView) findViewById(R.id.image_back);
    }

    private String bandName;
    private String type;

    private void initDatas() {
        textview_title.setText("添加空调遥控器");
        bandName = getIntent().getStringExtra("bandname");
        type = getIntent().getStringExtra("type");
        mRemoteControlManager = RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this, this);
        codeDatas = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_ng:
                currentTestCodeIndex=0;
                layout_device_response.setVisibility(View.GONE);
                break;
            case R.id.button_ok:
                Log.i(TAG, "下载码表 type="+type+"brandId=" + brandId+"controlId="+controlId);
                switch (type){
                    case "TV":
                        RestfulTools.getSingleton(this).downloadIrCode("TV", brandId, controlId, new Callback<QueryRCCodeResponse>() {
                            @Override
                            public void onResponse(Call<QueryRCCodeResponse> call, Response<QueryRCCodeResponse> response) {
                                Log.i(TAG, "下载码表=" + response.body().getValue().getCode());
                                Intent intent=new Intent(addRemoteControlActivity.this, AddDeviceNameActivity.class);
                                intent.putExtra("DeviceType","智能电视");
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Call<QueryRCCodeResponse> call, Throwable t) {

                            }
                        });
                        break;
                    case "KT":
                        RestfulTools.getSingleton(this).downloadIrCode("KT", brandId, controlId, new Callback<QueryRCCodeResponse>() {
                            @Override
                            public void onResponse(Call<QueryRCCodeResponse> call, Response<QueryRCCodeResponse> response) {
                                Log.i(TAG, "下载码表=" + response.body().getValue().getCode());
                                Intent intent=new Intent(addRemoteControlActivity.this, AddDeviceNameActivity.class);
                                intent.putExtra("DeviceType","智能空调");
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Call<QueryRCCodeResponse> call, Throwable t) {

                            }
                        });
                        break;
                    case "智能机顶盒遥控":
                        RestfulTools.getSingleton(this).downloadIrCode("IPTV", brandId, controlId, new Callback<QueryRCCodeResponse>() {
                            @Override
                            public void onResponse(Call<QueryRCCodeResponse> call, Response<QueryRCCodeResponse> response) {
                                Log.i(TAG, "下载码表=" + response.body().getValue().getCode());
                                Intent intent=new Intent(addRemoteControlActivity.this, AddDeviceNameActivity.class);
                                intent.putExtra("DeviceType","智能机顶盒遥控");
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Call<QueryRCCodeResponse> call, Throwable t) {

                            }
                        });
                        break;
                }

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
