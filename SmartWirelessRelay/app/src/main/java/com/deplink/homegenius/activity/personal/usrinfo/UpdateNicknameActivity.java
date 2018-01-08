package com.deplink.homegenius.activity.personal.usrinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import com.deplink.homegenius.view.edittext.ClearEditText;

public class UpdateNicknameActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "UpdateNicknameActivity";
    private ClearEditText edittext_update_nickname;
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);
        initViews();
        initDatas();
        initEvents();
    }
    private String nickName;
    private void initDatas() {
        textview_title.setText("修改昵称");
        textview_edit.setText("完成");
        nickName=getIntent().getStringExtra("nickname");
        if(nickName!=null){
            edittext_update_nickname.setText(nickName);
            edittext_update_nickname.setSelection(nickName.length());
        }

    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        textview_edit= (TextView) findViewById(R.id.textview_edit);
        image_back= (FrameLayout) findViewById(R.id.image_back);

        edittext_update_nickname = (ClearEditText) findViewById(R.id.edittext_update_nickname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_edit:
                onBackPressed();
                break;
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
