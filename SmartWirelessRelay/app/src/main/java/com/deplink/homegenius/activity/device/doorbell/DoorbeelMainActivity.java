package com.deplink.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.packet.ellisdk.Handler_UiThread;
import com.deplink.homegenius.manager.device.doorbeel.DoorBellListener;
import com.deplink.homegenius.manager.device.doorbeel.DoorbeelManager;
import com.deplink.homegenius.view.dialog.doorbeel.Doorbeel_menu_Dialog;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class DoorbeelMainActivity extends Activity implements View.OnClickListener{
    private FrameLayout image_back;
    private ImageView image_setting;
    private TextView textview_title;
    private FrameLayout frame_setting;
    private ImageView image_snap;
    private DoorbeelManager mDoorbeelManager;
    private DoorBellListener mDoorBellListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorbeel_main);
        initViews();
        initEvents();
        initDatas();
    }
    private void initEvents() {
        image_back.setOnClickListener(this);
        image_setting.setOnClickListener(this);
        frame_setting.setOnClickListener(this);
    }
    private String filename;
    private void initDatas() {
        textview_title.setText("智能门铃");
        mDoorbeelManager = DoorbeelManager.getInstance();
        mDoorbeelManager.InitDoorbeelManager(this);
        image_setting.setImageResource(R.drawable.menuicon);
        doorbeelMenuDialog=new Doorbeel_menu_Dialog(this);
        filename=getIntent().getStringExtra("file");
        mDoorBellListener = new DoorBellListener() {


            @Override
            public void responseVisitorImage(final Bitmap bitmap, int count) {
                super.responseVisitorImage(bitmap, count);
                Handler_UiThread.runTask("", new Runnable() {
                    @Override
                    public void run() {
                        image_snap.setImageBitmap(bitmap);
                    }
                },0);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDoorbeelManager.addDeviceListener(mDoorBellListener);
        mDoorbeelManager.getDoorbellVistorImage(filename, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDoorbeelManager.removeDeviceListener(mDoorBellListener);
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        image_setting = findViewById(R.id.image_setting);
        frame_setting = findViewById(R.id.frame_setting);
        image_snap = findViewById(R.id.image_snap);
    }
    private Doorbeel_menu_Dialog doorbeelMenuDialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_setting:
                doorbeelMenuDialog.show();
                break;
            case R.id.image_back:
                onBackPressed();
                break;

        }
    }
}
