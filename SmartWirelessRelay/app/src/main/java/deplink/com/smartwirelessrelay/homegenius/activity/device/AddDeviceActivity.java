package deplink.com.smartwirelessrelay.homegenius.activity.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock.EditSmartLockActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.adapter.GridViewAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.gridview.DragGridView;

public class AddDeviceActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RoomActivity";
    private ImageView image_back;

    private DragGridView mDragGridView;
    private GridViewAdapter mRoomsAdapter;
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

    private void initDatas() {
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager();
        isFromEditSmartLockActivity = getIntent().getBooleanExtra("EditSmartLockActivity", false);
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

        mDragGridView.setOnChangeListener(new DragGridView.OnChanageListener() {

            @Override
            public void onChange(int from, int to) {
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
                //最大值，最后一个，添加房间

                if (isFromEditSmartLockActivity) {
                    Intent intentSeleteedRoom = new Intent(AddDeviceActivity.this, EditSmartLockActivity.class);
                    intentSeleteedRoom.putExtra("roomName", RoomManager.getInstance().getmRooms().get(position).getRoomName());
                    AddDeviceActivity.this.setResult(RESULT_OK, intentSeleteedRoom);
                    finish();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("roomName", RoomManager.getInstance().getmRooms().get(position).getRoomName());
                    bundle.putInt("roomOrdinalNumber", RoomManager.getInstance().getmRooms().get(position).getRoomOrdinalNumber());
                    Intent intent = new Intent(AddDeviceActivity.this, AddDeviceQRcodeActivity.class);
                    Log.i(TAG, "传递当前房间名字=" + bundle.get("roomName") + "获取到的名字是=" + RoomManager.getInstance().getmRooms().get(position));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }


                Toast.makeText(AddDeviceActivity.this, "onclick position=" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        image_back = (ImageView) findViewById(R.id.image_back);
        mDragGridView = (DragGridView) findViewById(R.id.dragGridView);
        textview_show_select_room = (TextView) findViewById(R.id.textview_show_select_room);
        textview_skip_this_option = (TextView) findViewById(R.id.textview_skip_this_option);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRoomManager.updateRoomsOrdinalNumber();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRooms = mRoomManager.getDatabaseRooms();
        mRoomsAdapter = new GridViewAdapter(this, mRooms);
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
