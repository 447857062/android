package com.deplink.homegenius.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.manager.room.RoomListener;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.view.edittext.ClearEditText;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class ModifyRoomNameActivity extends Activity implements View.OnClickListener ,RoomListener{
    private static final String TAG="ModifyRoomNameActivity";
    private ClearEditText clearEditText;
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private RoomManager mRoomManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_room_name);
        initViews();
        initDatas();
        initEvents();
    }

    String orgRoomName;

    @Override
    protected void onResume() {
        super.onResume();
        orgRoomName = RoomManager.getInstance().getCurrentSelectedRoom().getRoomName();
        clearEditText.setText(orgRoomName);
        clearEditText.setSelection(orgRoomName.length());
    }

    private void initDatas() {
        textview_title.setText("修改房间名称");
        textview_edit.setText("完成");
        mRoomManager= RoomManager.getInstance();
        mRoomManager.initRoomManager(this,this);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        image_back = findViewById(R.id.image_back);
        clearEditText = findViewById(R.id.clear);
    }
    private String roomName;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.textview_edit:
                 roomName = clearEditText.getText().toString();
                if (!roomName.equalsIgnoreCase("")&& !roomName.equals(orgRoomName)) {
                    String roomUid=mRoomManager.getCurrentSelectedRoom().getUid();
                    Log.i(TAG,"roomUid="+roomUid);
                    mRoomManager.updateRoomNameHttp(roomUid,roomName);

                }else{
                    Toast.makeText(this,"请输入房间名称",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void responseQueryResult(List<Room> result) {

    }

    @Override
    public void responseAddRoomResult(String result) {

    }

    @Override
    public void responseDeleteRoomResult() {

    }

    @Override
    public void responseUpdateRoomNameResult() {
        int result=RoomManager.getInstance().updateRoomName(orgRoomName, roomName);
        if(result!=1){
            Toast.makeText(this,"修改房间名称失败",Toast.LENGTH_SHORT).show();
        }else{
            mRoomManager.getCurrentSelectedRoom().setRoomName(roomName);
            Intent intent=new Intent(ModifyRoomNameActivity.this,ManageRoomActivity.class);
            startActivity(intent);
        }
    }
}
