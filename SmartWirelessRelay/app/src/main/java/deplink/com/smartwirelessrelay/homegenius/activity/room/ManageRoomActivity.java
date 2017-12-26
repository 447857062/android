package deplink.com.smartwirelessrelay.homegenius.activity.room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.GetwaySelectListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

public class ManageRoomActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ManageRoomActivity";
    private FrameLayout image_back;
    private Button button_delete_room;
    private TextView textview_room_name;
    private String mRoomName;
    private RelativeLayout layout_room_name;

    private RoomManager mRoomManager;

    private TextView textview_title;

    private TextView textview_edit;
    private RelativeLayout layout_getway;
    private ListView listview_select_getway;
    private RelativeLayout layout_getway_list;
    private TextView textview_select_getway_name;
    private GetwaySelectListAdapter selectGetwayAdapter;
    private List<Device> mGetways;
    private String selectGetwayName;
    private ImageView imageview_getway_arror_right;
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
        textview_edit.setText("完成");
        mRoomManager = RoomManager.getInstance();
        mRoomManager.initRoomManager();
        mRoomName = mRoomManager.getCurrentSelectedRoom().getRoomName();
        textview_room_name.setText(mRoomName);
        Log.i(TAG, "当前编辑的房间名称= " + mRoomName);
        mGetways = new ArrayList<>();
        mGetways.addAll(GetwayManager.getInstance().getAllGetwayDevice());
        selectGetwayAdapter = new GetwaySelectListAdapter(this, mGetways);
        listview_select_getway.setAdapter(selectGetwayAdapter);
        listview_select_getway.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectGetwayName = mGetways.get(position).getName();
                textview_select_getway_name.setText(selectGetwayName);
                layout_getway_list.setVisibility(View.GONE);
                boolean result = mRoomManager.updateGetway(mGetways.get(position));
                if (!result) {
                    Toast.makeText(ManageRoomActivity.this, "更新网关失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_delete_room.setOnClickListener(this);
        layout_room_name.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_edit = (TextView) findViewById(R.id.textview_edit);
        textview_select_getway_name = (TextView) findViewById(R.id.textview_select_getway_name);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        button_delete_room = (Button) findViewById(R.id.button_delete_room);
        textview_room_name = (TextView) findViewById(R.id.textview_room_name);
        layout_room_name = (RelativeLayout) findViewById(R.id.layout_room_name);
        layout_getway = (RelativeLayout) findViewById(R.id.layout_getway);
        layout_getway_list = (RelativeLayout) findViewById(R.id.layout_getway_list);
        listview_select_getway = (ListView) findViewById(R.id.listview_select_getway);
        imageview_getway_arror_right = (ImageView) findViewById(R.id.imageview_getway_arror_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Device> mGetways = mRoomManager.getCurrentSelectedRoom().getmGetwayDevices();
        if (mGetways == null || mGetways.size()==0) {
            textview_select_getway_name.setText("未设置网关");
        } else {
            textview_select_getway_name.setText(mGetways.get(0).getName());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {


            case R.id.textview_edit:
                onBackPressed();
                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_delete_room:
                if (mRoomName != null) {
                    int result = mRoomManager.deleteRoom(mRoomName);
                    Log.i(TAG, "删除房间，影响的行数=" + result);
                    if (result > 0) {
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
                if (layout_getway_list.getVisibility() == View.VISIBLE) {
                    layout_getway_list.setVisibility(View.GONE);
                    imageview_getway_arror_right.setImageResource(R.drawable.directionicon);
                } else {
                    layout_getway_list.setVisibility(View.VISIBLE);
                    imageview_getway_arror_right.setImageResource(R.drawable.nextdirectionicon);
                }

                break;
        }
    }
}
