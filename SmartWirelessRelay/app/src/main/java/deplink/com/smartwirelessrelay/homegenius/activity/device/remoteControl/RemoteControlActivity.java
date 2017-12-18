package deplink.com.smartwirelessrelay.homegenius.activity.device.remoteControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.GetwaySelectListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl.RemoteControlManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class RemoteControlActivity extends Activity implements View.OnClickListener,RemoteControlListener{
    private static final String TAG="RemoteControlActivity";

    private RemoteControlManager mRemoteControlManager;
    private TextView textview_title;
    private FrameLayout image_back;

    private GetwaySelectListAdapter selectGetwayAdapter;
    private List<Device> mGetways;
    private ListView listview_select_getway;
    private RelativeLayout layout_getway_list;
    private TextView textview_select_getway_name;
    private TextView textview_edit;
    private TextView textview_select_room_name;
    private RelativeLayout layout_getway;
    private RelativeLayout layout_select_room;
    private ImageView imageview_getway_arror_right;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        initViews();
        initDatas();
        initEvents();
    }
    private String selectGetwayName;
    private void initDatas() {
        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        textview_title.setText("万能遥控");
        textview_edit.setText("完成");
        mRemoteControlManager=RemoteControlManager.getInstance();
        mRemoteControlManager.InitRemoteControlManager(this,this);
        mGetways = new ArrayList<>();
        mGetways.addAll(GetwayManager.getInstance().queryAllGetwayDevice());
        selectGetwayAdapter = new GetwaySelectListAdapter(this, mGetways);
        listview_select_getway.setAdapter(selectGetwayAdapter);
        listview_select_getway.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectGetwayName = mGetways.get(position).getName();
                textview_select_getway_name.setText(selectGetwayName);
                layout_getway_list.setVisibility(View.GONE);
                boolean result = mRemoteControlManager.updateSmartDeviceGetway(mGetways.get(position));
                if (!result) {
                    ToastSingleShow.showText(RemoteControlActivity.this, "更新智能设备所属网关失败");
                }
            }
        });
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isOnActivityResult) {
            for(int i=0;i<mRemoteControlManager.getmSelectRemoteControlDevice().getRooms().size();i++){
                Log.i(TAG,mRemoteControlManager.getmSelectRemoteControlDevice().getRooms().get(0).getRoomName());
            }
            if (mRemoteControlManager.getmSelectRemoteControlDevice().getRooms().size() == 1) {
                textview_select_room_name.setText(mRemoteControlManager.getmSelectRemoteControlDevice().getRooms().get(0).getRoomName());
            } else {
                textview_select_room_name.setText("全部");
            }
            SmartDev smartDev = DataSupport.where("Uid=?",mRemoteControlManager.getmSelectRemoteControlDevice().getUid()).findFirst(SmartDev.class, true);
            Device temp = smartDev.getGetwayDevice();
            if (temp == null) {
                textview_select_getway_name.setText("未设置网关");
            } else {
                textview_select_getway_name.setText(smartDev.getGetwayDevice().getName());
            }
        }
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        textview_edit= (TextView) findViewById(R.id.textview_edit);
        textview_select_room_name= (TextView) findViewById(R.id.textview_select_room_name);
        image_back= (FrameLayout) findViewById(R.id.image_back);
        layout_getway_list = (RelativeLayout) findViewById(R.id.layout_getway_list);
        textview_select_getway_name = (TextView) findViewById(R.id.textview_select_getway_name);
        layout_getway = (RelativeLayout) findViewById(R.id.layout_getway);
        layout_select_room = (RelativeLayout) findViewById(R.id.layout_select_room);
        listview_select_getway = (ListView) findViewById(R.id.listview_select_getway);
        imageview_getway_arror_right = (ImageView) findViewById(R.id.imageview_getway_arror_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview_edit:
                onBackPressed();
                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.layout_select_room:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("addDeviceSelectRoom", true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
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
    private boolean isOnActivityResult;
    private boolean isStartFromExperience;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
           isOnActivityResult = true;
            String roomName = data.getStringExtra("roomName");
            Log.i(TAG, "roomName=" + roomName);
            if (!isStartFromExperience) {
                Room room = RoomManager.getInstance().findRoom(roomName, true);
                String deviceUid = DeviceManager.getInstance().getCurrentSelectSmartDevice().getUid();
               mRemoteControlManager.updateSmartDeviceInWhatRoom(room, deviceUid);
            }
            textview_select_room_name.setText(roomName);
        }
    }
    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
    //设置结果:{ "OP": "REPORT", "Method": "Study", "Result": "err" }
    @Override
    public void responseQueryResult(String result) {
        Log.i(TAG,"responseQueryResult :"+result);
        Message msg=Message.obtain();
        msg.obj=result;
        mHandler.sendMessage(msg);
    }
}
