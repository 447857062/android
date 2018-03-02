package com.deplink.homegenius.activity.device.getway.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.personal.wifi.ScanWifiListActivity;
import com.deplink.homegenius.manager.connect.local.udp.UdpManager;
import com.deplink.homegenius.manager.connect.local.udp.interfaces.UdpManagerGetIPLintener;
import com.deplink.homegenius.manager.device.getway.GetwayManager;
import com.deplink.homegenius.util.WeakRefHandler;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

/**
 * 获取所有的网关
 * 判断当前要添加的网关要不要配置wifi
 */
public class QueryGetwaysActivity extends Activity implements View.OnClickListener, UdpManagerGetIPLintener {
    private static final String TAG = "QueryGetwaysActivity";
    private static final int MSG_CHECK_GETWAY_OK = 100;
    private String currentAddDevice;
    private Button textview_cancel;
    private UdpManager mUdpmanager;
    private TextView textview_title;
    private FrameLayout image_back;

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
        textview_cancel = findViewById(R.id.textview_cancel);

        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
    }

    private void initDatas() {
        textview_title.setText("网关");
        currentAddDevice = getIntent().getStringExtra("currentAddDevice");
        GetwayManager.getInstance().setCurrentAddDevice(currentAddDevice);
        mUdpmanager = UdpManager.getInstance();
        mUdpmanager.InitUdpConnect(this, this);
        mDevices = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUdpmanager.registerNetBroadcast(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUdpmanager.unRegisterNetBroadcast(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_cancel:
                startActivity(new Intent(this, DevicesActivity.class));
                break;
        }
    }

    private List<GatwayDevice> mDevices;
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CHECK_GETWAY_OK:
                    GatwayDevice device = new GatwayDevice();
                    device.setIpAddress((String) msg.obj);
                    mDevices.add(device);
                    Toast.makeText(QueryGetwaysActivity.this, "检查到IP为:" + msg.obj + "的网关", Toast.LENGTH_SHORT).show();
                    //TODO 连接tcp/ip
                    Intent intent = new Intent(QueryGetwaysActivity.this, ScanWifiListActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        }
    };
    private Handler mHandler = new WeakRefHandler(mCallback);
    @Override
    public void onGetLocalConnectIp(String ipAddress, String uid) {
        Log.i(TAG, "检查网关，获取到IP地址=" + ipAddress);
        Message msg = Message.obtain();
        msg.what = MSG_CHECK_GETWAY_OK;
        msg.obj = ipAddress;
        mHandler.sendMessage(msg);
    }
}
