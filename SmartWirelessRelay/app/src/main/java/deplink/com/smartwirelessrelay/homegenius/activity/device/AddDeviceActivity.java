package deplink.com.smartwirelessrelay.homegenius.activity.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.AddDeviceGridViewAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.EditSmartLockActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.AddRommActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

public class AddDeviceActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RoomActivity";
    private FrameLayout image_back;
    private GridView mDragGridView;
    private AddDeviceGridViewAdapter mRoomsAdapter;
    private RoomManager mRoomManager;
    private List<Room> mRooms = new ArrayList<>();
    private TextView textview_show_select_room;
    private TextView textview_skip_this_option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        initViews();
        initDatas();
        initEvents();
    }

    private boolean isFromEditSmartLockActivity;
    private boolean isStartFromExperience;

    private void initDatas() {
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager();
        isFromEditSmartLockActivity = getIntent().getBooleanExtra("EditSmartLockActivity", false);
        isStartFromExperience = getIntent().getBooleanExtra("isStartFromExperience", false);
        if (isFromEditSmartLockActivity) {
            textview_show_select_room.setText("请选择设备所在的房间");
            textview_skip_this_option.setVisibility(View.GONE);
        } else {
            textview_show_select_room.setText("请选择设备所在的房间,跳过设备默认分类为全部");
            textview_skip_this_option.setVisibility(View.VISIBLE);
        }
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_skip_this_option.setOnClickListener(this);
        mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //最大值，最后一个，添加房间

                if (position == mRoomsAdapter.getCount() - 1) {
                    Intent intent = new Intent(AddDeviceActivity.this, AddRommActivity.class);
                    intent.putExtra("fromAddDevice", true);
                    startActivity(intent);
                } else {
                    Room currentSelectedRoom = RoomManager.getInstance().getmRooms().get(position);
                    String currentAddRomm = currentSelectedRoom.getRoomName();
                    RoomManager.getInstance().setCurrentSelectedRoom(currentSelectedRoom);
                    if (isStartFromExperience) {
                        Intent mIntent = new Intent();
                        mIntent.putExtra("roomName", currentAddRomm);
                        // 设置结果，并进行传送
                        setResult(RESULT_OK, mIntent);
                        finish();
                    } else {
                        if (isFromEditSmartLockActivity) {
                            Intent intentSeleteedRoom = new Intent(AddDeviceActivity.this, EditSmartLockActivity.class);
                            intentSeleteedRoom.putExtra("roomName", currentAddRomm);
                            AddDeviceActivity.this.setResult(RESULT_OK, intentSeleteedRoom);
                            finish();
                        } else {
                            Intent intent = new Intent(AddDeviceActivity.this, AddDeviceQRcodeActivity.class);
                            startActivity(intent);
                        }
                    }
                }


            }
        });
    }

    private void initViews() {
        image_back = (FrameLayout) findViewById(R.id.image_back);
        mDragGridView = (GridView) findViewById(R.id.dragGridView);
        textview_show_select_room = (TextView) findViewById(R.id.textview_show_select_room);
        textview_skip_this_option = (TextView) findViewById(R.id.textview_skip_this_option);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isStartFromExperience) {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRooms = mRoomManager.getDatabaseRooms();
        mRoomsAdapter = new AddDeviceGridViewAdapter(this, mRooms);
        //房间适配器
        mDragGridView.setAdapter(mRoomsAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_skip_this_option:
                startActivity(new Intent(this, AddDeviceQRcodeActivity.class));
                break;
        }
    }
}
