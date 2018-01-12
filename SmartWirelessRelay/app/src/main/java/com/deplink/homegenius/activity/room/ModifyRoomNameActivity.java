package com.deplink.homegenius.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.room.RoomListener;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.edittext.ClearEditText;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class ModifyRoomNameActivity extends Activity implements View.OnClickListener, RoomListener {
    private static final String TAG = "ModifyRoomNameActivity";
    private ClearEditText clearEditText;
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private RoomManager mRoomManager;
    private String orgRoomName;
    private boolean isLogin;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_room_name);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        orgRoomName = RoomManager.getInstance().getCurrentSelectedRoom().getRoomName();
        clearEditText.setText(orgRoomName);
        clearEditText.setSelection(orgRoomName.length());
        manager.addEventCallback(ec);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initDatas() {
        textview_title.setText("修改房间名称");
        textview_edit.setText("完成");
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager(this, this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        connectLostDialog = new MakeSureDialog(ModifyRoomNameActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(ModifyRoomNameActivity.this, LoginActivity.class));
            }
        });
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {

            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {
            }

            @Override
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {
            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {

            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                isLogin = false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }
        };
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
                if (isLogin) {
                    if (!roomName.equalsIgnoreCase("")  ) {
                        if(!roomName.equals(orgRoomName)){
                            String roomUid = mRoomManager.getCurrentSelectedRoom().getUid();
                            int roomOrdinalNumber=mRoomManager.getCurrentSelectedRoom().getRoomOrdinalNumber();
                            Log.i(TAG, "roomUid=" + roomUid);
                            mRoomManager.updateRoomNameHttp(roomUid, roomName,roomOrdinalNumber);
                        }else{
                            this.finish();
                        }
                    } else {
                        Toast.makeText(this, "请输入房间名称", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ToastSingleShow.showText(this, "未登陆，请先登陆");
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
        int result = RoomManager.getInstance().updateRoomName(orgRoomName, roomName);
        if (result != 1) {
            Toast.makeText(this, "修改房间名称失败", Toast.LENGTH_SHORT).show();
        } else {
            mRoomManager.getCurrentSelectedRoom().setRoomName(roomName);
            Intent intent = new Intent(ModifyRoomNameActivity.this, ManageRoomActivity.class);
            startActivity(intent);
        }
    }
}
