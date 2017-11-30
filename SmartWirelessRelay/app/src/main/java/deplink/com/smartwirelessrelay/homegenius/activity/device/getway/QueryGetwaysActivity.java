package deplink.com.smartwirelessrelay.homegenius.activity.device.getway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.wifi.ScanWifiListActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.UdpManager;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.UdpManagerGetIPLintener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;

/**
 * 获取所有的网关
 * 判断当前要添加的网关要不要配置wifi
 */
public class QueryGetwaysActivity extends Activity implements View.OnClickListener,UdpManagerGetIPLintener {
    private static final String TAG = "QueryGetwaysActivity";
    private static final int MSG_CHECK_GETWAY_OK=100;
    private String currentAddDevice;
    private TextView textview_cancel;
    private UdpManager mUdpmanager;

    private TextView textview_title;
    private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_getways);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_cancel.setOnClickListener(this);
    }

    private void initViews() {
        textview_cancel= (TextView) findViewById(R.id.textview_cancel);

        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (ImageView) findViewById(R.id.image_back);
    }

    private void initDatas() {
        textview_title.setText("网关");
        currentAddDevice=getIntent().getStringExtra("currentAddDevice");
        GetwayManager.getInstance().setCurrentAddDevice(currentAddDevice);
        mUdpmanager = UdpManager.getInstance();
        mUdpmanager.InitUdpConnect(this, this);
        mDevices=new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_cancel:
                startActivity(new Intent(this, DevicesActivity.class));
                break;
        }
    }
    private List<Device> mDevices;
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CHECK_GETWAY_OK:
                    Device device=new Device();
                    device.setIpAddress((String) msg.obj);
                    mDevices.add(device);
                    Toast.makeText(QueryGetwaysActivity.this,"检查到IP为:"+ msg.obj+"的网关",Toast.LENGTH_SHORT).show();
                    //TODO 连接tcp/ip
                    Intent intent=new Intent(QueryGetwaysActivity.this,ScanWifiListActivity.class);
                    startActivity(intent);
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
