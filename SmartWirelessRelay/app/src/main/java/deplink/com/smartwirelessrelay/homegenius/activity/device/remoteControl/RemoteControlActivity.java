package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.remote.https.RestfulTools;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.remote.https.json.QueryBandResponse;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemoteControlActivity extends Activity implements View.OnClickListener,RemoteControlListener{
    private static final String TAG="RemoteControlActivity";
    private TextView textview_show_result;
    private Button button_study;
    private Button button_send_data;
    private Button button_get_band;
    private RemoteControlManager mRemoteControlManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        mRemoteControlManager=RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this,this);
    }

    private void initEvents() {
        button_study.setOnClickListener(this);
        button_send_data.setOnClickListener(this);
        button_get_band.setOnClickListener(this);
    }

    private void initViews() {
        textview_show_result= (TextView) findViewById(R.id.textview_show_result);
        button_study= (Button) findViewById(R.id.button_study);
        button_send_data= (Button) findViewById(R.id.button_send_data);
        button_get_band= (Button) findViewById(R.id.button_get_band);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_study:
                mRemoteControlManager.study();
                break;
            case R.id.button_send_data:
                //发送数据
                mRemoteControlManager.sendData("");
                break;
            case R.id.button_get_band:
                RestfulTools.getSingleton(this).queryBand("TV", "cn", new Callback<QueryBandResponse>() {
                    @Override
                    public void onResponse(Call<QueryBandResponse> call, Response<QueryBandResponse> response) {
                        Log.i(TAG,"response="+response.message());
                        if(response.body().getValue().size()>0){
                            for(int i=0;i<response.body().getValue().size();i++){
                                textview_show_result.append(response.body().getValue().get(i)+"|");
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<QueryBandResponse> call, Throwable t) {

                    }
                });
                break;
        }
    }
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            textview_show_result.setText("返回结果："+(String)msg.obj);
        }
    };
    //设置结果:{ "OP": "REPORT", "Method": "Study", "Result": "err" }
    @Override
    public void responseQueryResult(String result) {
        Log.i(TAG,"responseQueryResult :"+result);
        Message msg=Message.obtain();
        msg.obj=result;
        mHandler.sendMessage(msg);
    }
}
