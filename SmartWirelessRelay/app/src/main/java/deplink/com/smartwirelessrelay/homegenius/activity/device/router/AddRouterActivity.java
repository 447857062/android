package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceTypeConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetUtil;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class AddRouterActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "AddRouterActivity";
    private Button button_add_device_sure;
    private RouterManager mRouterManager;
    private EditText edittext_add_device_input_name;
    private TextView textview_select_room_name;
    private TextView textview_title;
    private FrameLayout image_back;
    private String routerSN;
    private String routerName;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;
    private boolean isBindAction;
    private boolean isUserLogin;
    private RelativeLayout layout_select_room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_router);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private SmartDev currentAddRouter;

    @Override
    protected void onResume() {
        super.onResume();
        if(!isOnActivityResult){
            if (RoomManager.getInstance().getCurrentSelectedRoom() == null) {
                textview_select_room_name.setText("全部");
            } else {
                textview_select_room_name.setText(RoomManager.getInstance().getCurrentSelectedRoom().getRoomName());
            }
        }
        manager.addEventCallback(ec);
    }

    private void initDatas() {
        textview_title.setText("添加路由器");
        routerSN = getIntent().getStringExtra("routerSN");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(AddRouterActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(AddRouterActivity.this, LoginActivity.class));
            }
        });
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
                switch (action) {
                    case GET_BINDING:
                        Log.i(TAG, "status GET_BINDING");
                        if (isBindAction) {
                            isBindAction = false;
                            for (int i = 0; i < manager.getDeviceList().size(); i++) {
                                //查询设备列表，sn和上传时一样才修改名字
                                if (manager.getDeviceList().get(i).getDeviceSN().equals(routerSN)) {
                                    Log.i(TAG, "manager.getDeviceList().get(i).getDeviceSN()=" + manager.getDeviceList().get(i).getDeviceSN() + "bindDeviceSn=" + routerSN + "changename");
                                    currentAddRouter = new SmartDev();
                                    currentAddRouter.setRouterDeviceKey(manager.getDeviceList().get(i).getDeviceKey());
                                    ((RouterDevice) manager.getDeviceList().get(i)).changeName(routerName);
                                }
                            }
                        }
                        break;
                }
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {


            }

            @Override
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

            }

            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_CHANGE_NAME:
                        currentAddRouter.setUid(routerSN);
                        currentAddRouter.setType(DeviceTypeConstant.TYPE.TYPE_ROUTER);
                        boolean saveResult = mRouterManager.saveRouter(currentAddRouter);
                        if (!saveResult) {

                        } else {
                            Room room = RoomManager.getInstance().getCurrentSelectedRoom();
                            Log.i(TAG, "添加设备此处的房间是=" + room.getRoomName());
                            boolean result = mRouterManager.updateDeviceInWhatRoom(room, routerSN, routerName);
                            if (result) {
                                Message msg = Message.obtain();
                                msg.what = MSG_ADD_ROUTER_SUCCESS;
                                mHandler.sendMessage(msg);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
                switch (action) {
                    case GET_BINDING:
                        if (isBindAction) {
                            mHandler.sendEmptyMessage(MSG_ADD_ROUTER_FAIL);
                        }
                        break;

                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                isUserLogin = false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }
        };
    }

    private void initEvents() {
        button_add_device_sure.setOnClickListener(this);
        image_back.setOnClickListener(this);
        layout_select_room.setOnClickListener(this);
    }

    private void initViews() {
        button_add_device_sure = (Button) findViewById(R.id.button_add_device_sure);
        edittext_add_device_input_name = (EditText) findViewById(R.id.edittext_add_device_input_name);
        textview_select_room_name = (TextView) findViewById(R.id.textview_select_room_name);
        textview_title = (TextView) findViewById(R.id.textview_title);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        layout_select_room = (RelativeLayout) findViewById(R.id.layout_select_room);
    }

    public static final int MSG_ADD_ROUTER_FAIL = 100;
    /**
     * 更新路由器所在房间失败
     */
    public static final int MSG_UPDATE_ROOM_FAIL = 101;
    public static final int MSG_ADD_ROUTER_SUCCESS = 102;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ADD_ROUTER_FAIL:
                    Toast.makeText(AddRouterActivity.this, "添加路由器失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_UPDATE_ROOM_FAIL:
                    Toast.makeText(AddRouterActivity.this, "更新路由器所在房间失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_ADD_ROUTER_SUCCESS:
                    startActivity(new Intent(AddRouterActivity.this, DevicesActivity.class));
                    Toast.makeText(AddRouterActivity.this, "添加路由器成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private boolean isOnActivityResult;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            isOnActivityResult = true;
            String roomName = data.getStringExtra("roomName");
            Log.i(TAG, "roomName=" + roomName);
            Room room = RoomManager.getInstance().findRoom(roomName, true);
            RoomManager.getInstance().setCurrentSelectedRoom(room);
            textview_select_room_name.setText(roomName);
        }
    }

    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.layout_select_room:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("addDeviceSelectRoom", true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                break;
            case R.id.button_add_device_sure:
                if (NetUtil.isNetAvailable(AddRouterActivity.this)) {
                    isBindAction = true;
                    routerName = edittext_add_device_input_name.getText().toString();
                    if (routerName.equals("")) {
                        routerName = "家里的路由器";
                    }
                    isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
                    if (isUserLogin) {
                        DialogThreeBounce.showLoading(this);
                        manager.bindDevice(routerSN);
                    } else {
                        ToastSingleShow.showText(AddRouterActivity.this, " 未登录，无法添加路由器");
                    }
                } else {
                    ToastSingleShow.showText(AddRouterActivity.this, " 网络连接不可用，请检查网络连接");
                }
                break;
        }
    }
}
