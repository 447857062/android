package com.deplink.homegenius.activity.device.router;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.view.edittext.ClearEditText;

public class RouterNameUpdateActivity extends Activity implements View.OnClickListener {
    private ClearEditText edittext_router_name;
    private RouterManager mRouterManager;
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_name_update);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("修改名称");
        textview_edit.setText("完成");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        if ( DeviceManager.getInstance().isStartFromExperience()) {
            deviceName = "体验路由器";
        } else {
            deviceName = mRouterManager.getCurrentSelectedRouter().getName();
        }
        edittext_router_name.setText(deviceName);
        edittext_router_name.setSelection(deviceName.length());
    }

    private void initEvents() {
        textview_edit.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        edittext_router_name = findViewById(R.id.edittext_router_name);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        image_back = findViewById(R.id.image_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.textview_edit:
                final String routerName = edittext_router_name.getText().toString();
                if (!routerName.equals("")) {
                    if ( DeviceManager.getInstance().isStartFromExperience()) {

                    } else {
                       int result= mRouterManager.updateRouterName(routerName);
                        if (result > 0) {
                            mRouterManager.getCurrentSelectedRouter().setName(routerName);
                            RouterNameUpdateActivity.this.finish();
                        } else {
                            Message msg = Message.obtain();
                            msg.what = MSG_UPDATE_NAME_FAIL;
                            mHandler.sendMessage(msg);
                        }
                    }

                } else {
                    Toast.makeText(this, "请输入路由器名称", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }

    private static final int MSG_UPDATE_NAME_FAIL = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_NAME_FAIL:
                    Toast.makeText(RouterNameUpdateActivity.this, "更新路由器名称失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}