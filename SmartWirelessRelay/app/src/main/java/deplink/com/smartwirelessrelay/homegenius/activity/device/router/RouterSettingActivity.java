package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.firmwareupdate.FirmwareUpdateActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.lan.LanSettingActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.qos.QosSettingActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.router.wifi.WiFiSettingActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetUtil;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class RouterSettingActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "RouterSettingActivity";
    private FrameLayout image_back;
    private RelativeLayout layout_router_name_out;
    private RelativeLayout layout_room_select_out;
    private RelativeLayout layout_connect_type_select_out;
    private RelativeLayout layout_wifi_setting_out;
    private RelativeLayout layout_lan_setting_out;
    private RelativeLayout layout_QOS_setting_out;
    private RelativeLayout layout_update_out;
    private RelativeLayout layout_reboot_out;
    private TextView buttton_delete_router;
    private RouterManager mRouterManager;
    private TextView textview_room_select_2;
    private TextView textview_route_name_2;
    private RouterDevice routerDevice;
    private boolean isUserLogin;
    private MakeSureDialog connectLostDialog;
    private SDKManager manager;
    private EventCallback ec;
    private boolean deviceOnline;
    private TextView textview_title;
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
        textview_route_name_2.setText(mRouterManager.getCurrentSelectedRouter().getName());

        mRouterManager.getRouterAtRooms(new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Object o) {
                //所在房间
                List<Room> rooms = new ArrayList<>();
                rooms.addAll((ArrayList<Room>) o);
                Log.i(TAG, "所在房间列表大小" + ((ArrayList<Room>) o).size());
                textview_room_select_2.setText("");
                for (int i = 0; i < rooms.size(); i++) {
                    textview_room_select_2.append("(" + rooms.get(i).getRoomName() + ")");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        manager.addEventCallback(ec);
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        getCurrentSelectedDevice();
    }

    /**
     * 获取当前选种的路由器
     */
    private void getCurrentSelectedDevice() {
        if (isUserLogin) {
            routerDevice = mRouterManager.getRouterDevice();
            if (routerDevice != null) {
                deviceOnline = routerDevice.getOnline();
            }
        }

    }

    private void initDatas() {
        textview_title.setText("路由器设置");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(RouterSettingActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(RouterSettingActivity.this, LoginActivity.class));
            }
        });
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {

            @Override
            public void onSuccess(SDKAction action) {

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
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back = (FrameLayout) findViewById(R.id.image_back);
        layout_router_name_out = (RelativeLayout) findViewById(R.id.layout_router_name_out);
        layout_room_select_out = (RelativeLayout) findViewById(R.id.layout_room_select_out);
        layout_connect_type_select_out = (RelativeLayout) findViewById(R.id.layout_connect_type_select_out);
        layout_wifi_setting_out = (RelativeLayout) findViewById(R.id.layout_wifi_setting_out);
        layout_lan_setting_out = (RelativeLayout) findViewById(R.id.layout_lan_setting_out);
        layout_QOS_setting_out = (RelativeLayout) findViewById(R.id.layout_QOS_setting_out);
        layout_update_out = (RelativeLayout) findViewById(R.id.layout_update_out);
        layout_reboot_out = (RelativeLayout) findViewById(R.id.layout_reboot_out);
        buttton_delete_router = (TextView) findViewById(R.id.buttton_delete_router);
        textview_room_select_2 = (TextView) findViewById(R.id.textview_room_select_2);
        textview_route_name_2 = (TextView) findViewById(R.id.textview_route_name_2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM && resultCode == RESULT_OK) {
            final String roomName = data.getStringExtra("roomName");
            Room room = RoomManager.getInstance().findRoom(roomName, true);
            String deviceUid = mRouterManager.getCurrentSelectedRouter().getUid();
            String deviceName = mRouterManager.getCurrentSelectedRouter().getName();
            mRouterManager.updateDeviceInWhatRoom(room, deviceUid, deviceName, new Observer() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(@NonNull Object o) {
                    if ((boolean) o) {
                        textview_room_select_2.setText(roomName);
                    } else {
                        Message msg = Message.obtain();
                        msg.what = MSG_UPDATE_ROOM_FAIL;
                        mHandler.sendMessage(msg);
                    }
                    ;
                }

                @Override
                public void onError(@NonNull Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });

        }
    }

    private static final int REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM = 100;

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
                intent.putExtra("EditSmartLockActivity", true);
                startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE_IN_WHAT_ROOM);
                break;
            case R.id.layout_connect_type_select_out:
                startActivity(new Intent(this, ConnectSettingActivity.class));
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
                MakeSureDialog rebootRightNow = new MakeSureDialog(this);
                rebootRightNow.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        ToastSingleShow.showText(RouterSettingActivity.this, "已重启设备");
                        //TODO 本地远程重启路由器
                        ToastSingleShow.showText(RouterSettingActivity.this, "已重启设备");
                        if (!NetUtil.isNetAvailable(RouterSettingActivity.this)) {
                            ToastSingleShow.showText(RouterSettingActivity.this, "网络连接已断开");
                        } else {
                            if (isUserLogin && deviceOnline) {
                                routerDevice.reboot();
                            } else {
                                //RebootLocal();
                            }
                        }


                    }
                });
                rebootRightNow.show();
                rebootRightNow.setTitleText("确定立即重启");
                break;
            case R.id.buttton_delete_router:
                mRouterManager.deleteRouter(mRouterManager.getCurrentSelectedRouter(), new Observer() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                        int affectColumn = (int) o;
                        if (affectColumn > 0) {
                            startActivity(new Intent(RouterSettingActivity.this, DevicesActivity.class));
                        } else {
                            Message msg = Message.obtain();
                            msg.what = MSG_DELETE_ROUTER_FAIL;
                            mHandler.sendMessage(msg);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
                break;
        }
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
        manager.removeEventCallback(ec);
    }
}
