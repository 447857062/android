package deplink.com.smartwirelessrelay.homegenius.activity.device.smartSwitch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.GetwaySelectListAdapter;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.smartswitch.SmartSwitchManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.DeleteDeviceDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class EditActivity extends Activity implements View.OnClickListener, DeviceListener {
    private static final String TAG = "EditDoorbeelActivity";
    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;
    private FrameLayout image_back;
    private TextView textview_title;
    private TextView button_delete_device;
    private DeleteDeviceDialog deleteDialog;
    private SmartSwitchManager mSmartSwitchManager;
    private DeviceManager mDeviceManager;
    private RelativeLayout layout_select_room;
    private TextView textview_select_room_name;
    private ClearEditText edittext_add_device_input_name;
    private RelativeLayout layout_getway_select;
    private RelativeLayout layout_getway_list;
    private ListView listview_select_getway;
    private GetwaySelectListAdapter selectGetwayAdapter;
    private List<Device> mGetways;
    private TextView textview_select_getway_name;
    private TextView textview_edit;
    private ImageView imageview_getway_arror_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initViews();
        initDatas();
        initEvents();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_delete_device.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
        layout_getway_select.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
    }

    private String switchType;
    private String selectGetwayName;

    private void initDatas() {
        textview_edit.setText("完成");
        switchType = getIntent().getStringExtra("switchType");
        Log.i(TAG, "initDatas switchType=" + switchType);
        textview_title.setText(switchType);
        deleteDialog = new DeleteDeviceDialog(this);
        mSmartSwitchManager = SmartSwitchManager.getInstance();
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this, this);
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
                boolean result = mSmartSwitchManager.updateSmartDeviceGetway(mGetways.get(position));
                if (!result) {
                    Toast.makeText(EditActivity.this, "更新智能设备所属网关失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceName = mSmartSwitchManager.getCurrentSelectSmartDevice().getName();
        if (deviceName != null && deviceName.length() > 0) {
            edittext_add_device_input_name.setText(deviceName);
            edittext_add_device_input_name.setSelection(deviceName.length());
        }
        if (!isOnActivityResult) {
            isOnActivityResult = false;
            if (mSmartSwitchManager.getCurrentSelectSmartDevice().getRooms().size() == 1) {
                textview_select_room_name.setText(mSmartSwitchManager.getCurrentSelectSmartDevice().getRooms().get(0).getRoomName());
            } else {
                textview_select_room_name.setText("全部");
            }
        }

    }

    private void initViews() {
        image_back = (FrameLayout) findViewById(R.id.image_back);
        textview_title = (TextView) findViewById(R.id.textview_title);
        button_delete_device = (TextView) findViewById(R.id.button_delete_device);
        layout_select_room = (RelativeLayout) findViewById(R.id.layout_room_select);
        textview_select_room_name = (TextView) findViewById(R.id.textview_select_room_name);
        edittext_add_device_input_name = (ClearEditText) findViewById(R.id.edittext_add_device_input_name);
        layout_getway_list = (RelativeLayout) findViewById(R.id.layout_getway_list);
        layout_getway_select = (RelativeLayout) findViewById(R.id.layout_getway_select);
        listview_select_getway = (ListView) findViewById(R.id.listview_select_getway);
        textview_select_getway_name = (TextView) findViewById(R.id.textview_select_getway_name);
        imageview_getway_arror_right = (ImageView) findViewById(R.id.imageview_getway_arror_right);
        textview_edit = (TextView) findViewById(R.id.textview_edit);
    }

    private boolean isStartFromExperience;
    private String deviceName;
    private boolean isOnActivityResult;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            String roomName = data.getStringExtra("roomName");
            Log.i(TAG, "isStartFromExperience=" + isStartFromExperience);
            isOnActivityResult = true;
            if (!isStartFromExperience) {
                Room room = RoomManager.getInstance().findRoom(roomName, true);
                String deviceUid = mSmartSwitchManager.getCurrentSelectSmartDevice().getUid();
                deviceName = edittext_add_device_input_name.getText().toString();
                mSmartSwitchManager.updateSmartDeviceInWhatRoom(room, deviceUid, deviceName);
            }
            textview_select_room_name.setText(roomName);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                onBackPressed();
                break;
            case R.id.layout_getway_select:
                if (layout_getway_list.getVisibility() == View.VISIBLE) {
                    layout_getway_list.setVisibility(View.GONE);
                    imageview_getway_arror_right.setImageResource(R.drawable.directionicon);
                } else {
                    layout_getway_list.setVisibility(View.VISIBLE);
                    imageview_getway_arror_right.setImageResource(R.drawable.nextdirectionicon);
                }

                break;
            case R.id.layout_room_select:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("addDeviceSelectRoom", true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                break;
            case R.id.button_delete_device:
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        DialogThreeBounce.showLoading(EditActivity.this);
                        mDeviceManager.deleteSmartDevice();

                    }
                });
                deleteDialog.show();
                break;

        }
    }

    @Override
    public void responseQueryResult(String result) {

    }

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
        DialogThreeBounce.hideLoading();
        if (deleteSuccess) {
            int deleteResult = mSmartSwitchManager.deleteDBSmartDevice(mSmartSwitchManager.getCurrentSelectSmartDevice().getUid());
            if (deleteResult > 0) {
                startActivity(new Intent(EditActivity.this, DevicesActivity.class));
            } else {
                ToastSingleShow.showText(EditActivity.this, "删除开关设备失败");
            }
        } else {
            ToastSingleShow.showText(EditActivity.this, "删除开关设备失败");
        }
    }

    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {

    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }
}
