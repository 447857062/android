package deplink.com.smartwirelessrelay.homegenius.activity.device.getway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.wifi.ScanWifiListActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.DeleteDeviceDialog;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;

public class GetwayDeviceActivity extends Activity implements View.OnClickListener, GetwayListener {
    private static final String TAG = "GetwayDeviceActivity";
    private TextView button_delete_device;
    private GetwayManager mGetwayManager;
    private boolean isStartFromExperience;
    private RelativeLayout layout_config_wifi_getway;
    private RelativeLayout layout_select_room;
    private TextView textview_select_room_name;
    private FrameLayout image_back;
    private ClearEditText edittext_input_devie_name;
    private TextView textview_title;
    private TextView textview_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getway_device);
        initViews();
        initDatas();
        initEvents();
    }

    private boolean needUpdateDeviceName;
    private String currentSelectDeviceName;
    private void initDatas() {
        textview_title.setText("智能网关");
        textview_edit.setText("完成");
        isStartFromExperience = getIntent().getBooleanExtra("isStartFromExperience", false);
        if (isStartFromExperience) {
            edittext_input_devie_name.setText("家里的网关");
            edittext_input_devie_name.setSelection(5);
        } else {
            mGetwayManager = GetwayManager.getInstance();
            mGetwayManager.InitGetwayManager(this, this);
            currentSelectDeviceName=mGetwayManager.getCurrentSelectGetwayDevice().getName();
            edittext_input_devie_name.setText(currentSelectDeviceName);
            edittext_input_devie_name.setSelection(currentSelectDeviceName.length());
            List<Room>rooms=mGetwayManager.getCurrentSelectGetwayDevice().getRoomList();
            if(rooms.size()==1){
                textview_select_room_name.setText(rooms.get(0).getRoomName());
            }else{
                textview_select_room_name.setText("全部");
            }

        }
        deleteDialog = new DeleteDeviceDialog(this);
    }

    private void initEvents() {
        button_delete_device.setOnClickListener(this);
        layout_config_wifi_getway.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private void initViews() {
        button_delete_device = (TextView) findViewById(R.id.button_delete_device);
        layout_config_wifi_getway = (RelativeLayout) findViewById(R.id.layout_config_wifi_getway);
        layout_select_room = (RelativeLayout) findViewById(R.id.layout_select_room);
        textview_select_room_name = (TextView) findViewById(R.id.textview_select_room_name);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        edittext_input_devie_name = (ClearEditText) findViewById(R.id.edittext_input_devie_name);
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_edit = (TextView) findViewById(R.id.textview_edit);
    }

    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;
    private DeleteDeviceDialog deleteDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_delete_device:
                //删除设备
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        if (isStartFromExperience) {
                            Toast.makeText(GetwayDeviceActivity.this, "删除网关设备成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(GetwayDeviceActivity.this, ExperienceDevicesActivity.class));
                        } else {
                            mGetwayManager.deleteGetwayDevice();
                        }
                    }
                });
                deleteDialog.show();


                break;
            case R.id.layout_config_wifi_getway:
                startActivity(new Intent(this, ScanWifiListActivity.class));
                break;
            case R.id.layout_select_room:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("isFromGetwayMainActivity",true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                String inputDeviceName=edittext_input_devie_name.getText().toString();
               if(!inputDeviceName.equals(currentSelectDeviceName)) {
                   mGetwayManager.updateGetwayDeviceName(inputDeviceName);
               }
                this.finish();
                break;
        }
    }
    private static final int MSG_HANDLE_DELETE_DEVICE_RESULT = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HANDLE_DELETE_DEVICE_RESULT:
                    mGetwayManager.deleteDBGetwayDevice(mGetwayManager.getCurrentSelectGetwayDevice().getUid());
                    Toast.makeText(GetwayDeviceActivity.this, "删除设备成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GetwayDeviceActivity.this, DevicesActivity.class));
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            String roomName = data.getStringExtra("roomName");
            Log.i(TAG, "isStartFromExperience=" + isStartFromExperience );
            if (!isStartFromExperience) {
                Room room = RoomManager.getInstance().findRoom(roomName, true);
                String deviceUid = mGetwayManager.getCurrentSelectGetwayDevice().getUid();
                mGetwayManager.updateGetwayDeviceInWhatRoom(room, deviceUid);

            }
            textview_select_room_name.setText(roomName);
        }
    }

    @Override
    public void responseResult(String result) {
        boolean deleteSuccess = true;
        Gson gson = new Gson();
        DeviceList mDeviceList = gson.fromJson(result, DeviceList.class);
        for (int i = 0; i < mDeviceList.getDevice().size(); i++) {
            if (mDeviceList.getDevice().get(i).getUid().equals(mGetwayManager.getCurrentSelectGetwayDevice().getUid())) {
                deleteSuccess = false;
            }
        }
        if (deleteSuccess) {
            mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_RESULT);
        }
    }
}
