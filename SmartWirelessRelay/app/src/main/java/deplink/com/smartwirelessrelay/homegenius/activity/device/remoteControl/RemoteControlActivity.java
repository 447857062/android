package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;

public class RemoteControlActivity extends Activity implements View.OnClickListener,RemoteControlListener{
    private static final String TAG="RemoteControlActivity";

    private RemoteControlManager mRemoteControlManager;
    private TextView textview_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("万能遥控");
        mRemoteControlManager=RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this,this);
    }

    private void initEvents() {

    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


        }
    }
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

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
