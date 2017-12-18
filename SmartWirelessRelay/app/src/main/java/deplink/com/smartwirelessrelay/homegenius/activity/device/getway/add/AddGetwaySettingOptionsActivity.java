package deplink.com.smartwirelessrelay.homegenius.activity.device.getway.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

/**
 * 添加网关走到添加网关名称，配置wifi网关
 */
public class AddGetwaySettingOptionsActivity extends Activity implements View.OnClickListener, GetwayListener {
    private static final String TAG = "GetwaySettingOptions";
    private Button button_save;
    private EditText edittext_input_devie_name;
    private TextView textview_title;
    private TextView textview_select_room_name;
    private FrameLayout image_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_getway_setting_options);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        button_save.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        button_save = (Button) findViewById(R.id.button_save);
        edittext_input_devie_name = (EditText) findViewById(R.id.edittext_input_devie_name);
        textview_title = (TextView) findViewById(R.id.textview_title);
        textview_select_room_name = (TextView) findViewById(R.id.textview_select_room_name);
        image_back = (FrameLayout) findViewById(R.id.image_back);
    }

    /**
     * 网关设备扫码出来的字符串
     */
    private String currentAddDevice;

    private void initDatas() {
        currentAddDevice = GetwayManager.getInstance().getCurrentAddDevice();
        if (RoomManager.getInstance().getCurrentSelectedRoom() != null) {
            mRoomName = RoomManager.getInstance().getCurrentSelectedRoom().getRoomName();
        } else {
            mRoomName = "全部";
        }
        Log.i(TAG, "mRoomName=" + mRoomName);
        mGetwayManager = GetwayManager.getInstance();
        mGetwayManager.InitGetwayManager(this, this);
        textview_title.setText("网关");
        textview_select_room_name.setText(mRoomName);
    }

    private GetwayManager mGetwayManager;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_save:
                if (currentAddDevice != null) {
                    if (LocalConnectmanager.getInstance().isHandshakeCompleted() && LocalConnectmanager.getInstance().getSslSocket() != null) {
                        Gson gson = new Gson();
                        QrcodeSmartDevice device = gson.fromJson(currentAddDevice, QrcodeSmartDevice.class);
                        mGetwayManager.bindDevice(device);
                    } else {
                        ToastSingleShow.showText(this, "无可用的网关");

                    }

                }
                break;
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }

    private static final int MSG_BIND_DEVICE_RESPONSE = 100;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_BIND_DEVICE_RESPONSE:
                    startActivity(new Intent(AddGetwaySettingOptionsActivity.this, DevicesActivity.class));
                    Toast.makeText(AddGetwaySettingOptionsActivity.this, "绑定网关设备成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private boolean isDeviceAddSuccess(DeviceList aDeviceList, QrcodeSmartDevice tempDevice) {
        for (int i = 0; i < aDeviceList.getDevice().size(); i++) {
            if (aDeviceList.getDevice().get(i).getUid().equals(tempDevice.getSn())) {
                return true;
            }
        }

        return false;
    }

    private String deviceName;
    private String mRoomName;


    @Override
    public void responseResult(String result) {
        Log.i(TAG, "绑定网关设备返回：" + result + "当前要绑定的是：");
        Gson gson = new Gson();
        boolean addDeviceSuccess;
        QrcodeSmartDevice tempDevice = gson.fromJson(currentAddDevice, QrcodeSmartDevice.class);
        DeviceList mDeviceList = gson.fromJson(result, DeviceList.class);
        addDeviceSuccess = isDeviceAddSuccess(mDeviceList, tempDevice);
        deviceName = edittext_input_devie_name.getText().toString();
        if (deviceName.equals("")) {
            deviceName = "家里的网关";
        }
        tempDevice.setName(deviceName);
        mGetwayManager.addDBGetwayDevice(tempDevice);

        for (int i = 0; i < mDeviceList.getDevice().size(); i++) {
            if (mDeviceList.getDevice().get(i).getUid().equals(tempDevice.getSn())) {
                Room room = RoomManager.getInstance().findRoom(mRoomName, true);
                mGetwayManager.updateGetwayDeviceInWhatRoom(room, mDeviceList.getDevice().get(i).getUid());
            }
        }
        if (addDeviceSuccess) {
            mHandler.sendEmptyMessage(MSG_BIND_DEVICE_RESPONSE);
        }
    }
}
