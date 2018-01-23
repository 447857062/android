package com.deplink.homegenius.activity.device.doorbell.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.packet.ellisdk.BasicPacket;
import com.deplink.homegenius.Protocol.packet.ellisdk.EllESDK;
import com.deplink.homegenius.Protocol.packet.ellisdk.EllE_Listener;
import com.deplink.homegenius.Protocol.packet.ellisdk.Handler_Background;
import com.deplink.homegenius.Protocol.packet.ellisdk.Handler_UiThread;
import com.deplink.homegenius.Protocol.packet.ellisdk.WIFIData;
import com.deplink.homegenius.activity.device.AddDeviceNameActivity;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.manager.device.doorbeel.DoorbeelManager;
import com.deplink.homegenius.util.DataExchange;
import com.deplink.homegenius.view.toast.ToastSingleShow;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class AddDoorbellTipsActivity extends Activity implements View.OnClickListener, EllE_Listener {
    private static final String TAG = "AddDoorbellTipsActivity";
    private FrameLayout image_back;
    private TextView textview_title;
    private Button button_next_step;
    private DoorbeelManager mDoorbeelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doorbell_tips);
        initViews();
        initEvents();
        initDatas();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_next_step.setOnClickListener(this);
    }

    EllESDK ellESDK;

    private void initDatas() {
        textview_title.setText("连接门铃热点");
        mDoorbeelManager = DoorbeelManager.getInstance();
        mDoorbeelManager.InitDoorbeelManager(this);
        ellESDK = EllESDK.getInstance();
        ellESDK.InitEllESDK(this, this);
        account = mDoorbeelManager.getSsid();
        password = mDoorbeelManager.getPassword();
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        button_next_step = findViewById(R.id.button_next_step);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_next_step:
                ellESDK.startSearchDevs();
                break;
        }
    }

    String account;
    String password;

    @Override
    protected void onDestroy() {
        EllESDK.getInstance().stopSearchDevs();
        super.onDestroy();
    }

    @Override
    public void onRecvEllEPacket(BasicPacket packet) {

    }

    public void onDoneClick(final long mac, final byte type, final byte ver) {
        if (account.length() > 0 && password.length() > 0) {
            Log.i(TAG, "onDoneClick setDevWiFiConfigWithMac mac=" + mac + "type=" + type + "ver=" + ver);
            //设置wifi
            Handler_Background.execute(new Runnable() {
                @Override
                public void run() {
                    WIFIData wifiData = new WIFIData(account, password);

                    int setresult = EllESDK.getInstance().setDevWiFiConfigWithMac(mac, type, ver, wifiData);
                    Log.i(TAG, "配置wifi结果是=" + setresult);
                    if (setresult == 1) {
                        Handler_UiThread.runTask("", new Runnable() {
                            @Override
                            public void run() {
                                ToastSingleShow.showText(AddDoorbellTipsActivity.this, "门铃网络已配置,现在重启门邻设备,等手机连上网络后进行设备添加");
                                Intent intent = new Intent(AddDoorbellTipsActivity.this, AddDeviceNameActivity.class);
                                intent.putExtra("DeviceType", DeviceTypeConstant.TYPE.TYPE_MENLING);
                                startActivity(intent);
                            }
                        }, 0);

                    }
                }
            });

        }
    }

    @Override
    public void searchDevCBS(long mac, byte type, byte ver) {
        Log.e(TAG, "mac:" + mac + "type:" + type + "ver:" + ver);
        String macS=DataExchange.byteArrayToHexString( DataExchange.longToEightByte(mac));
        macS=macS.replaceAll("0x","").trim();
        macS=macS.replaceAll(" ","-");
        Log.i(TAG,"mac="+macS);
        Log.i(TAG,"savemac="+macS);
        mDoorbeelManager.setMac(macS);
        onDoneClick(mac, type, ver);
    }
}
