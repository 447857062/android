package deplink.com.smartwirelessrelay.homegenius.activity.device.light;

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

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.adapter.GetwaySelectListAdapter;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceListener;
import deplink.com.smartwirelessrelay.homegenius.manager.device.DeviceManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.getway.GetwayManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.light.SmartLightManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.DeleteDeviceDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;

public class LightEditActivity extends Activity implements View.OnClickListener,DeviceListener{
    private static final String TAG = "LightEditActivity";
    private TextView textview_title;
    private TextView textview_edit;
    private FrameLayout image_back;
    private Button button_delete_device;
    private TextView textview_select_room_name;
    private RelativeLayout layout_select_room;
    private ClearEditText edittext_input_devie_name;
    private DeleteDeviceDialog deleteDialog;
    private GetwaySelectListAdapter selectGetwayAdapter;
    private List<Device> mGetways;
    private ListView listview_select_getway;
    private RelativeLayout layout_getway_list;
    private TextView textview_select_getway_name;
    private RelativeLayout layout_getway;
    private ImageView imageview_getway_arror_right;
    private SmartLightManager mSmartLightManager;
    private DeviceManager mDeviceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_edit);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStartFromExperience =  DeviceManager.getInstance().isStartFromExperience();
        if (isStartFromExperience) {

        } else {
            lightName = mSmartLightManager.getCurrentSelectLight().getName();
            if (lightName != null) {
                edittext_input_devie_name.setText(lightName);
                edittext_input_devie_name.setSelection(lightName.length());
            }

            if (!isOnActivityResult) {
                isOnActivityResult=false;
                if (mSmartLightManager.getCurrentSelectLight().getRooms().size() == 1) {
                    textview_select_room_name.setText(mSmartLightManager.getCurrentSelectLight().getRooms().get(0).getRoomName());
                } else {
                    textview_select_room_name.setText("全部");
                }

                SmartDev smartDev = DataSupport.where("Uid=?",mSmartLightManager.getCurrentSelectLight().getUid()).findFirst(SmartDev.class, true);
                Device temp = smartDev.getGetwayDevice();
                if (temp == null) {
                    textview_select_getway_name.setText("未设置网关");
                } else {
                    textview_select_getway_name.setText(smartDev.getGetwayDevice().getName());
                }
            }
        }
    }
    private String lightName;
    private void initEvents() {
        image_back.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        button_delete_device.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
        layout_getway.setOnClickListener(this);
    }
    private boolean isStartFromExperience;
    private String selectGetwayName;
    private void initDatas() {
        isStartFromExperience =  DeviceManager.getInstance().isStartFromExperience();
        mDeviceManager = DeviceManager.getInstance();
        mSmartLightManager =SmartLightManager.getInstance();

        if (isStartFromExperience) {
            edittext_input_devie_name.setText("我家的智能灯");
            edittext_input_devie_name.setSelection(6);
        } else {
            mDeviceManager.InitDeviceManager(this, this);
            mSmartLightManager.InitSmartLightManager(this);
        }
        textview_title.setText("智能灯泡");
        textview_edit.setText("完成");
        deleteDialog = new DeleteDeviceDialog(this);
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
                boolean result = mSmartLightManager.updateSmartDeviceGetway(mGetways.get(position));
                if (!result) {
                    Toast.makeText(LightEditActivity.this, "更新智能设备所属网关失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initViews() {
        image_back = findViewById(R.id.image_back);
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        textview_select_getway_name = findViewById(R.id.textview_select_getway_name);
        button_delete_device = findViewById(R.id.button_delete_device);
        layout_select_room = findViewById(R.id.layout_select_room);
        textview_select_room_name = findViewById(R.id.textview_select_room_name);
        edittext_input_devie_name = findViewById(R.id.edittext_input_devie_name);
        layout_getway_list = findViewById(R.id.layout_getway_list);
        layout_getway = findViewById(R.id.layout_getway);
        listview_select_getway = findViewById(R.id.listview_select_getway);
        imageview_getway_arror_right = findViewById(R.id.imageview_getway_arror_right);
    }
    private boolean isOnActivityResult;
    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            isOnActivityResult = true;
            String roomName = data.getStringExtra("roomName");
            Log.i(TAG, "roomName=" + roomName);
            if (!isStartFromExperience) {
                Room room = RoomManager.getInstance().findRoom(roomName, true);
                String deviceUid = mDeviceManager.getCurrentSelectSmartDevice().getUid();
                mSmartLightManager.updateSmartDeviceRoom(room, deviceUid);
            }
            textview_select_room_name.setText(roomName);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                onBackPressed();
                break;
            case R.id.layout_select_room:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("addDeviceSelectRoom", true);
                intent.putExtra("isStartFromExperience", isStartFromExperience);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                break;
            case R.id.button_delete_device:
                //删除设备
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        if (!isStartFromExperience) {
                            DialogThreeBounce.showLoading(LightEditActivity.this);
                            mDeviceManager.deleteSmartDevice();
                        } else {
                            startActivity(new Intent(LightEditActivity.this, ExperienceDevicesActivity.class));
                        }
                    }
                });
                deleteDialog.show();
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

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseBindDeviceResult(String result) {

    }

    @Override
    public void responseWifiListResult(List<SSIDList> wifiList) {

    }

    @Override
    public void responseSetWifirelayResult(int result) {

    }
}
