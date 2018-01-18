package com.deplink.homegenius.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.homepage.SmartHomeMainActivity;
import com.deplink.homegenius.activity.personal.PersonalCenterActivity;
import com.deplink.homegenius.activity.room.adapter.GridViewAdapter;
import com.deplink.homegenius.application.AppManager;
import com.deplink.homegenius.manager.room.RoomListener;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.view.gridview.DragGridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class RoomActivity extends Activity implements View.OnClickListener, RoomListener {
    private static final String TAG = "RoomActivity";
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;
    private DragGridView mDragGridView;
    private GridViewAdapter mRoomsAdapter;
    private RoomManager mRoomManager;
    private List<Room> mRooms = new ArrayList<>();
    private ImageView imageview_addroom;
    private ImageView imageview_devices;
    private ImageView imageview_home_page;
    private ImageView imageview_rooms;
    private ImageView imageview_personal_center;
    private TextView textview_home;
    private TextView textview_device;
    private TextView textview_room;
    private TextView textview_mine;
    private static final int MSG_UPDATE_ROOM = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_ROOM:
                    mRooms.clear();
                    mRooms.addAll(mRoomManager.queryRooms());
                    Log.i(TAG, "handler界面显示房间的列表大小" + mRooms.size());
                    mRoomsAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isRoomOrdinalNumberChanged){
            mRoomManager.updateRoomsOrdinalNumber(mRooms);
        }
    }

    private void initDatas() {
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        textview_home.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_device.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_room.setTextColor(getResources().getColor(R.color.title_blue_bg));
        textview_mine.setTextColor(getResources().getColor(android.R.color.darker_gray));
        imageview_home_page.setImageResource(R.drawable.nocheckthehome);
        imageview_devices.setImageResource(R.drawable.nocheckthedevice);
        imageview_rooms.setImageResource(R.drawable.checktheroom);
        imageview_personal_center.setImageResource(R.drawable.nocheckthemine);
        mRooms = mRoomManager.queryRooms();
        mRoomsAdapter = new GridViewAdapter(this, mRooms);
        //房间适配器
        mDragGridView.setAdapter(mRoomsAdapter);
        mRoomManager.updateRooms();
    }
    private boolean isRoomOrdinalNumberChanged;
    private void initEvents() {
        AppManager.getAppManager().addActivity(this);
        layout_home_page.setOnClickListener(this);
        imageview_addroom.setOnClickListener(this);
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
        mDragGridView.setOnChangeListener(new DragGridView.OnChanageListener() {

            @Override
            public void onChange(int from, int to) {
                isRoomOrdinalNumberChanged=true;
                Room temp = mRooms.get(from);
                //这里的处理需要注意下
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(mRooms, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(mRooms, i, i - 1);
                    }
                }
                mRooms.set(to, temp);
                mRoomsAdapter.notifyDataSetChanged();
            }
        });
        mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRoomManager.setCurrentSelectedRoom(mRoomManager.getmRooms().get(position));
                Intent intent = new Intent(RoomActivity.this, DeviceNumberActivity.class);
                startActivity(intent);

            }
        });
    }

    private void initViews() {
        textview_home = findViewById(R.id.textview_home);
        textview_device = findViewById(R.id.textview_device);
        textview_room = findViewById(R.id.textview_room);
        textview_mine = findViewById(R.id.textview_mine);
        layout_home_page = findViewById(R.id.layout_home_page);
        layout_devices = findViewById(R.id.layout_devices);
        layout_rooms = findViewById(R.id.layout_rooms);
        layout_personal_center = findViewById(R.id.layout_personal_center);
        mDragGridView = findViewById(R.id.dragGridView);
        imageview_addroom = findViewById(R.id.imageview_addroom);
        imageview_devices = findViewById(R.id.imageview_devices);
        imageview_home_page = findViewById(R.id.imageview_home_page);
        imageview_rooms = findViewById(R.id.imageview_rooms);
        imageview_personal_center = findViewById(R.id.imageview_personal_center);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_home_page:
                startActivity(new Intent(this, SmartHomeMainActivity.class));
                break;
            case R.id.layout_devices:
                startActivity(new Intent(this, DevicesActivity.class));
                break;
            case R.id.layout_rooms:
                //  startActivity(new Intent(this,RoomActivity.class));
                break;
            case R.id.layout_personal_center:
                startActivity(new Intent(this, PersonalCenterActivity.class));
                break;
            case R.id.imageview_addroom:
                startActivity(new Intent(RoomActivity.this, AddRommActivity.class));
                break;
        }
    }

    @Override
    public void responseQueryResultHttps(List<Room> result) {
        Log.i(TAG, "更新房间排序:" + result.size());
        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_ROOM;
        mHandler.sendMessage(msg);
    }

    @Override
    public void responseAddRoomResult(String result) {

    }

    @Override
    public void responseDeleteRoomResult() {

    }

    @Override
    public void responseUpdateRoomNameResult() {

    }
}
