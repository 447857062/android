package deplink.com.smartwirelessrelay.homegenius.activity.device.smartlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

public class EditSmartLockActivity extends Activity implements View.OnClickListener, DeviceListener {

    private ImageView image_back;
    private Button button_delete_device;
    private DeviceManager mDeviceManager;
    private TextView textview_edit_complement;
    private TextView textview_select_room_name;
    private RelativeLayout layout_select_room;
    private TextView textview_title;

    private TextView textview_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_smart_lock);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        button_delete_device.setOnClickListener(this);
        textview_edit_complement.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
    }
    private boolean isStartFromExperience;
    private void initDatas() {
        textview_title.setText("编辑");
        textview_edit.setText("完成");
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this, this);
        isStartFromExperience = getIntent().getBooleanExtra("isStartFromExperience", false);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        textview_edit= (TextView) findViewById(R.id.textview_edit);
        image_back = (ImageView) findViewById(R.id.image_back);
        button_delete_device = (Button) findViewById(R.id.button_delete_device);
        textview_edit_complement = (TextView) findViewById(R.id.textview_edit_complement);
        layout_select_room = (RelativeLayout) findViewById(R.id.layout_select_room);
        textview_select_room_name = (TextView) findViewById(R.id.textview_select_room_name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            String roomName = data.getStringExtra("roomName");
            Room room= RoomManager.getInstance().findRoom(roomName,true);
            String deviceUid=mDeviceManager.getCurrentSelectSmartDevice().getUid();
            String deviceName=mDeviceManager.getCurrentSelectSmartDevice().getName();
            mDeviceManager.updateSmartDeviceInWhatRoom(room,deviceUid,deviceName);
            textview_select_room_name.setText(roomName);
        }
    }

    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_edit:
                onBackPressed();
                break;
            case R.id.layout_select_room:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("EditSmartLockActivity", true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                break;
            case R.id.button_delete_device:
                //删除设备
                if (!isStartFromExperience) {
                    mDeviceManager.deleteSmartDevice();
                } else {
                    startActivity(new Intent(this, DevicesActivity.class));
                }

                break;
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void responseQueryResult(String result) {

    }

    private static final int MSG_HANDLE_DELETE_DEVICE_RESULT = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HANDLE_DELETE_DEVICE_RESULT:
                    mDeviceManager.deleteDBSmartDevice(mDeviceManager.getCurrentSelectSmartDevice().getUid());
                    Toast.makeText(EditSmartLockActivity.this, "删除设备成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditSmartLockActivity.this, DevicesActivity.class));
                    break;
            }
        }
    };

    @Override
    public void responseBindDeviceResult(String result) {
        Gson gson = new Gson();
        boolean deleteSuccess = true;
        DeviceList mDeviceList = gson.fromJson(result, DeviceList.class);
        for (int i = 0; i < mDeviceList.getSmartDev().size(); i++) {
            if (mDeviceList.getSmartDev().get(i).getUid().equals(mDeviceManager.getCurrentSelectSmartDevice().getUid())) {
                deleteSuccess = false;
            }
        }
        if (deleteSuccess) {

            mHandler.sendEmptyMessage(MSG_HANDLE_DELETE_DEVICE_RESULT);
        }
    }

    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {

    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }
}
