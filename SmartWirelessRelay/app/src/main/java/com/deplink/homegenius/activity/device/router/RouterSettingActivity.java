package com.deplink.homegenius.activity.device.router;

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

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.activity.device.AddDeviceActivity;
import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.device.router.firmwareupdate.FirmwareUpdateActivity;
import com.deplink.homegenius.activity.device.router.lan.LanSettingActivity;
import com.deplink.homegenius.activity.device.router.qos.QosSettingActivity;
import com.deplink.homegenius.activity.device.router.wifi.WiFiSettingActivity;
import com.deplink.homegenius.activity.device.router.wifi.WifiSetting24;
import com.deplink.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.remote.HomeGenius;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.router.RouterManager;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.util.NetUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.ConnectTypeLocalDialog;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.MakeSureDialog;
import com.deplink.homegenius.view.dialog.SelectConnectTypeLocalDialog;
import com.deplink.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.router.RouterDevice;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.deplink.sdk.android.sdk.rest.ErrorResponse;
import com.deplink.sdk.android.sdk.rest.RestfulToolsRouter;
import com.deplink.sdk.android.sdk.rest.RouterResponse;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouterSettingActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RouterSettingActivity";
    private FrameLayout image_back;
    private RelativeLayout layout_router_name_out;
    private RelativeLayout layout_room_select_out;
    private RelativeLayout layout_connect_type_select_out;
    private RelativeLayout layout_wifi_setting_out;
    private RelativeLayout layout_reboot_out;
    private TextView buttton_delete_router;
    private RouterManager mRouterManager;
    private TextView textview_room_select_2;
    private TextView textview_route_name_2;
    private boolean isUserLogin;
    private MakeSureDialog connectLostDialog;
    private SDKManager manager;
    private EventCallback ec;
    private boolean deviceOnline;
    private TextView textview_title;
    private DeleteDeviceDialog deleteDialog;
    private DeleteDeviceDialog rebootDialog;
    private RelativeLayout layout_lan_setting_out;
    private RelativeLayout layout_update_out;
    private RelativeLayout layout_QOS_setting_out;
    private DeviceManager mDeviceManager;
    private DeviceListener mDeviceListener;
    private HomeGenius mHomeGenius;
    private String channels;
    private boolean isStartFromExperience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_setting);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStartFromExperience = mDeviceManager.isStartFromExperience();
        if (!isStartFromExperience) {
            textview_route_name_2.setText(mRouterManager.getCurrentSelectedRouter().getName());
            if (mRouterManager.getCurrentSelectedRouter().getStatus().equals("离线")) {
                layout_lan_setting_out.setVisibility(View.GONE);
                layout_update_out.setVisibility(View.GONE);
                layout_QOS_setting_out.setVisibility(View.GONE);
            }
            List<Room> rooms = mRouterManager.getRouterAtRooms();
            ;
            if (rooms.size() == 1) {
                textview_room_select_2.setText(rooms.get(0).getRoomName());
            } else {
                textview_room_select_2.setText("全部");
            }
            manager.addEventCallback(ec);
            mDeviceManager.addDeviceListener(mDeviceListener);
            isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);

        } else {
            textview_route_name_2.setText("体验路由器");
        }
        mHomeGenius = new HomeGenius();
        channels = mRouterManager.getCurrentSelectedRouter().getRouter().getChannels();
    }



    private void initDatas() {
        textview_title.setText("路由器设置");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(RouterSettingActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(RouterSettingActivity.this, LoginActivity.class));
            }
        });
        selectConnectTypeDialog = new ConnectTypeLocalDialog(RouterSettingActivity.this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {

            @Override
            public void onSuccess(SDKAction action) {
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {

            }


            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_REBOOT:
                        ToastSingleShow.showText(RouterSettingActivity.this, "重启设备成功");
                        break;
                }
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                isUserLogin = false;
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");
            }
        };
        mDeviceListener = new DeviceListener() {
            @Override
            public void responseAlertDeviceHttpResult(DeviceOperationResponse result) {
                super.responseAlertDeviceHttpResult(result);
                if (action.equals("alertroom")) {
                    String deviceName = mRouterManager.getCurrentSelectedRouter().getName();
                    boolean saveResult = mRouterManager.updateDeviceInWhatRoom(room, deviceUid, deviceName);
                    if (saveResult) {
                        textview_room_select_2.setText(roomName);
                    } else {
                        Message msg = Message.obtain();
                        msg.what = MSG_UPDATE_ROOM_FAIL;
                        mHandler.sendMessage(msg);
                        action = "";
                    }
                } else if (action.equalsIgnoreCase("alertname")) {

                }
            }

            @Override
            public void responseDeleteDeviceHttpResult(DeviceOperationResponse result) {
                super.responseDeleteDeviceHttpResult(result);
                int affectColumn = DataSupport.deleteAll(SmartDev.class, "Uid = ?", mRouterManager.getCurrentSelectedRouter().getUid());
                Log.i(TAG, "删除路由器设备=" + affectColumn);
                ToastSingleShow.showText(RouterSettingActivity.this, "解除绑定成功");
                RouterSettingActivity.this.startActivity(new Intent(RouterSettingActivity.this, DevicesActivity.class));
            }
        };
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        layout_router_name_out.setOnClickListener(this);
        layout_room_select_out.setOnClickListener(this);
        layout_connect_type_select_out.setOnClickListener(this);
        layout_wifi_setting_out.setOnClickListener(this);
        layout_lan_setting_out.setOnClickListener(this);
        layout_QOS_setting_out.setOnClickListener(this);
        layout_update_out.setOnClickListener(this);
        layout_reboot_out.setOnClickListener(this);
        buttton_delete_router.setOnClickListener(this);
    }

    private void initViews() {
        deleteDialog = new DeleteDeviceDialog(this);
        rebootDialog = new DeleteDeviceDialog(this);
        textview_title = findViewById(R.id.textview_title);
        buttton_delete_router = findViewById(R.id.buttton_delete_router);
        image_back = findViewById(R.id.image_back);
        layout_router_name_out = findViewById(R.id.layout_router_name_out);
        layout_room_select_out = findViewById(R.id.layout_room_select_out);
        layout_connect_type_select_out = findViewById(R.id.layout_connect_type_select_out);
        layout_wifi_setting_out = findViewById(R.id.layout_wifi_setting_out);
        layout_lan_setting_out = findViewById(R.id.layout_lan_setting_out);
        layout_QOS_setting_out = findViewById(R.id.layout_QOS_setting_out);
        layout_update_out = findViewById(R.id.layout_update_out);
        layout_reboot_out = findViewById(R.id.layout_reboot_out);
        textview_room_select_2 = findViewById(R.id.textview_room_select_2);
        textview_route_name_2 = findViewById(R.id.textview_route_name_2);
    }

    private String action;
    private String roomName;
    private Room room;
    private String deviceUid;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            if (mDeviceManager.isStartFromExperience()) {

            } else {
                roomName = data.getStringExtra("roomName");
                room = RoomManager.getInstance().findRoom(roomName, true);
                deviceUid = mRouterManager.getCurrentSelectedRouter().getUid();
                action = "alertroom";
                mDeviceManager.alertDeviceHttp(deviceUid, room.getUid(), null, null);

            }
        }
    }

    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;
    private ConnectTypeLocalDialog selectConnectTypeDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;

            case R.id.layout_router_name_out:
                startActivity(new Intent(this, RouterNameUpdateActivity.class));
                break;
            case R.id.layout_room_select_out:
                Intent intent = new Intent(this, AddDeviceActivity.class);
                intent.putExtra("addDeviceSelectRoom", true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                break;
            case R.id.layout_connect_type_select_out:
                if (DeviceManager.getInstance().isStartFromExperience()) {
                    selectConnectTypeDialog.setmOnConnectTypeSlected(new ConnectTypeLocalDialog.onConnectTypeSlected() {
                        @Override
                        public void onConnectTypeSelect(int type) {
                            switch (type) {
                                case SelectConnectTypeLocalDialog.CONNECTTYPE_DYNAMICS:
                                    selectConnectType();
                                    break;
                            }
                        }
                    });
                    selectConnectTypeDialog.show();
                } else {
                    if (mRouterManager.getCurrentSelectedRouter().getStatus().equals("在线")) {
                        startActivity(new Intent(this, ConnectSettingActivity.class));
                    } else {
                        selectConnectTypeDialog.setmOnConnectTypeSlected(new ConnectTypeLocalDialog.onConnectTypeSlected() {
                            @Override
                            public void onConnectTypeSelect(int type) {
                                switch (type) {
                                    case SelectConnectTypeLocalDialog.CONNECTTYPE_DYNAMICS:
                                        selectConnectType();
                                        break;
                                }
                            }
                        });
                        selectConnectTypeDialog.show();
                    }
                }


                break;
            case R.id.layout_wifi_setting_out:
                startActivity(new Intent(this, WiFiSettingActivity.class));
                break;
            case R.id.layout_lan_setting_out:
                startActivity(new Intent(this, LanSettingActivity.class));
                break;
            case R.id.layout_QOS_setting_out:
                startActivity(new Intent(this, QosSettingActivity.class));
                break;
            case R.id.layout_update_out:
                startActivity(new Intent(this, FirmwareUpdateActivity.class));
                break;
            case R.id.layout_reboot_out:
                rebootDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        ToastSingleShow.showText(RouterSettingActivity.this, "已重启设备");
                        if (!NetUtil.isNetAvailable(RouterSettingActivity.this)) {
                            ToastSingleShow.showText(RouterSettingActivity.this, "网络连接已断开");
                        } else {
                            if (isUserLogin && deviceOnline) {
                                if(channels!=null){
                                    mHomeGenius.reboot(channels);
                                }
                            } else {
                                RebootLocal();
                            }
                        }
                    }
                });
                rebootDialog.show();
                rebootDialog.setTitleText("重启设备");
                rebootDialog.setContentText("确定立即重启?");

                break;
            case R.id.buttton_delete_router:
                deleteDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        if (DeviceManager.getInstance().isStartFromExperience()) {
                            startActivity(new Intent(RouterSettingActivity.this, ExperienceDevicesActivity.class));
                        } else {
                            if (NetUtil.isNetAvailable(RouterSettingActivity.this)) {
                                if (isUserLogin) {
                                    DialogThreeBounce.showLoading(RouterSettingActivity.this);
                                    mDeviceManager.deleteDeviceHttp();
                                } else {
                                    ToastSingleShow.showText(RouterSettingActivity.this, "用户已离线，登录后使用");
                                }

                            } else {
                                ToastSingleShow.showText(RouterSettingActivity.this, "网络连接不可用");
                            }
                        }


                    }
                });
                deleteDialog.show();

                break;
        }
    }

    /**
     * 重启，使用本地接口
     */
    private void RebootLocal() {

        RestfulToolsRouter.getSingleton(RouterSettingActivity.this).rebootRouter(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "response.code=" + response.code());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
        startActivity(new Intent(RouterSettingActivity.this, DevicesActivity.class));
    }

    /**
     * （成功连接本地路由器后）选择上网方式
     */
    private void selectConnectType() {
        RestfulToolsRouter.getSingleton(RouterSettingActivity.this).dynamicIp(new Callback<RouterResponse>() {
            @Override
            public void onResponse(Call<RouterResponse> call, Response<RouterResponse> response) {
                int code = response.code();
                if (code != 200) {
                    String errorMsg = "";
                    try {
                        String text = response.errorBody().string();
                        Gson gson = new Gson();
                        ErrorResponse errorResponse;
                        errorResponse = gson.fromJson(text, ErrorResponse.class);
                        switch (errorResponse.getErrcode()) {
                            case AppConstant.ERROR_CODE.OP_ERRCODE_BAD_TOKEN:
                                text = AppConstant.ERROR_MSG.OP_ERRCODE_BAD_TOKEN;
                                ToastSingleShow.showText(RouterSettingActivity.this, "登录已失效 :" + text);
                                startActivity(new Intent(RouterSettingActivity.this, LoginActivity.class));
                                return;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_BAD_ACCOUNT:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_BAD_ACCOUNT;
                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_LOGIN_FAIL:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_LOGIN_FAIL;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_NOT_FOUND:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_NOT_FOUND;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_LOGIN_FAIL_MAX:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_LOGIN_FAIL_MAX;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_CAPTCHA_INCORRECT:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_CAPTCHA_INCORRECT;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_PASSWORD_INCORRECT:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_PASSWORD_INCORRECT;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_PASSWORD_SHORT:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_PASSWORD_SHORT;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_BAD_ACCOUNT_INFO:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_BAD_ACCOUNT_INFO;

                                break;
                            case AppConstant.ERROR_CODE.OP_ERRCODE_DB_TRANSACTION_ERROR:
                                errorMsg = AppConstant.ERROR_MSG.OP_ERRCODE_DB_TRANSACTION_ERROR;
                                break;
                            default:
                                errorMsg = errorResponse.getMsg();
                                break;

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ToastSingleShow.showText(RouterSettingActivity.this, errorMsg);
                } else {
                    ToastSingleShow.showText(RouterSettingActivity.this, "动态IP设置成功，请设置wifi名字密码");
                    Intent intentWifiSetting = new Intent(RouterSettingActivity.this, WifiSetting24.class);
                    intentWifiSetting.putExtra(AppConstant.OPERATION_TYPE, AppConstant.OPERATION_TYPE_LOCAL);
                    startActivity(intentWifiSetting);
                }
            }

            @Override
            public void onFailure(Call<RouterResponse> call, Throwable t) {

            }
        });
    }

    private static final int MSG_DELETE_ROUTER_FAIL = 100;
    /**
     * 更新路由器所在房间失败
     */
    private static final int MSG_UPDATE_ROOM_FAIL = 101;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DELETE_ROUTER_FAIL:
                    Toast.makeText(RouterSettingActivity.this, "删除路由器失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_UPDATE_ROOM_FAIL:
                    Toast.makeText(RouterSettingActivity.this, "更新路由器所在房间失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (!isStartFromExperience) {
            mDeviceManager.removeDeviceListener(mDeviceListener);
            manager.removeEventCallback(ec);
        }

    }
}
