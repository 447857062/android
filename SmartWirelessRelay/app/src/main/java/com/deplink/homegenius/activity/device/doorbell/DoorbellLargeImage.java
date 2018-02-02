package com.deplink.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.deplink.homegenius.manager.device.doorbeel.DoorBellListener;
import com.deplink.homegenius.manager.device.doorbeel.DoorbeelManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class DoorbellLargeImage extends Activity implements View.OnClickListener{
    private ImageView image_snap;
    private RelativeLayout layout_root;
    private DoorbeelManager mDoorbeelManager;
    private DoorBellListener mDoorBellListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorbell_large_image);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
    }

    private void initDatas() {
        mDoorbeelManager = DoorbeelManager.getInstance();
        mDoorbeelManager.InitDoorbeelManager(this);
        mDoorBellListener = new DoorBellListener() {
            public void responseVisitorImage(Bitmap bitmap, int count) {
                super.responseVisitorImage(bitmap, count);
                BitmapDrawable bbb = new BitmapDrawable(bitmap);
                image_snap.setBackgroundDrawable(bbb);
            }
        };
        if(getIntent().getStringExtra("file")!=null){
            String filename=getIntent().getStringExtra("file");
            mDoorbeelManager.getDoorbellVistorImage(filename, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDoorbeelManager.addDeviceListener(mDoorBellListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mDoorbeelManager.removeDeviceListener(mDoorBellListener);
    }
    private void initViews() {
        image_snap=findViewById(R.id.image_snap);
        layout_root=findViewById(R.id.layout_root);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_root:
                onBackPressed();
                break;
        }
    }
}
