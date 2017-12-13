package deplink.com.smartwirelessrelay.homegenius.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.SmartGetwayActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

public class ManageRoomActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ManageRoomActivity";
    private FrameLayout image_back;
    private Button button_delete_room;
    private TextView textview_room_name;
    private String mRoomName;
    private RelativeLayout layout_room_name;
    private RelativeLayout layout_getway;
    private RoomManager mRoomManager;

    private TextView textview_title;
    private ImageView image_setting;
    private FrameLayout frame_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_room);

        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("编辑");
        image_setting.setImageResource(R.drawable.addicon);
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager();
        mRoomName = mRoomManager.getCurrentSelectedRoom().getRoomName();
        textview_room_name.setText(mRoomName);
        Log.i(TAG, "当前编辑的房间名称= " + mRoomName);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_delete_room.setOnClickListener(this);
        layout_room_name.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
        image_setting.setOnClickListener(this);
    }

    private void initViews() {
        textview_title = (TextView) findViewById(R.id.textview_title);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        image_setting = (ImageView) findViewById(R.id.image_setting);
        button_delete_room = (Button) findViewById(R.id.button_delete_room);
        textview_room_name = (TextView) findViewById(R.id.textview_room_name);
        layout_room_name = (RelativeLayout) findViewById(R.id.layout_room_name);
        layout_getway = (RelativeLayout) findViewById(R.id.layout_getway);
        frame_setting = (FrameLayout) findViewById(R.id.frame_setting);

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {


            case R.id.frame_setting:

                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_delete_room:
                if (mRoomName != null) {
                    int result = mRoomManager.deleteRoom(mRoomName);
                    Log.i(TAG, "删除房间，影响的行数=" + result);
                    if (result == 1) {
                        startActivity(new Intent(ManageRoomActivity.this, RoomActivity.class));
                        Toast.makeText(ManageRoomActivity.this, "删除房间成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ManageRoomActivity.this, "删除房间失败", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.layout_room_name:
                intent = new Intent(this, ModifyRoomNameActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_getway:
                intent = new Intent(this, SmartGetwayActivity.class);
                startActivity(intent);
                break;
        }
    }
}
