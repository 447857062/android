package com.deplink.homegenius.activity.device.smartlock.userid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.deplink.homegenius.manager.device.DeviceManager;

import java.util.ArrayList;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class UpdateSmartLockUserIdActivity extends Activity implements View.OnClickListener {
    private static final String TAG="UserIdActivity";
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private ListView listview_update_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_smart_lock_user_id);
        initViews();
        initDatas();
        initEvents();
    }

    private UserIdAdapter mUserIdAdapter;
    private ArrayList<String> mIds;

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        listview_update_ids.setAdapter(mUserIdAdapter);
    }

    private boolean isStartFromExperience;

    private void initDatas() {
        textview_title.setText("修改ID名称");
        textview_edit.setText("完成");
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        mIds = new ArrayList<>();
        if (isStartFromExperience) {
            mIds.add("001");
            mIds.add("002");
            mIds.add("003");
        } else {
            mIds = getIntent().getStringArrayListExtra("recordlistid");
        }
        Log.i(TAG,"isStartFromExperience="+isStartFromExperience);
        Log.i(TAG,"mIds="+mIds.size());
        mUserIdAdapter = new UserIdAdapter(this, mIds);
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        image_back = findViewById(R.id.image_back);
        listview_update_ids = findViewById(R.id.listview_update_ids);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.textview_edit:
                if(isStartFromExperience){

                }else{
                    for(int i=0;i<mIds.size();i++){
                        //TODO
                    }
                }
                finish();
                break;


        }
    }
}
