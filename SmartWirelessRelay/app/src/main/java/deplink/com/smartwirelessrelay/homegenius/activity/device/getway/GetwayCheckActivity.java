package deplink.com.smartwirelessrelay.homegenius.activity.device.getway;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.UdpManager;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.UdpManagerGetIPLintener;

public class GetwayCheckActivity extends Activity implements View.OnClickListener, UdpManagerGetIPLintener {
    private static final String TAG = "GetwayCheckActivity";
    private Button button_next_step;
    private ImageView image_back;
    private UdpManager mUdpmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getway_check);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        mUdpmanager = UdpManager.getInstance();
        mUdpmanager.InitUdpConnect(this, this);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_next_step.setOnClickListener(this);
    }

    private void initViews() {
        image_back = (ImageView) findViewById(R.id.image_back);
        button_next_step = (Button) findViewById(R.id.button_next_step);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
    private static final int MSG_CHECK_GETWAY_OK=100;
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CHECK_GETWAY_OK:
                    Toast.makeText(GetwayCheckActivity.this,"检查到IP为:"+ msg.obj+"的网关",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    public void onGetLocalConnectIp(String ipAddress) {
        Log.i(TAG, "检查网关，获取到IP地址=" + ipAddress);
        Message msg=Message.obtain();
        msg.what=MSG_CHECK_GETWAY_OK;
        msg.obj=ipAddress;
        mHandler.sendMessage(msg);
    }
}
