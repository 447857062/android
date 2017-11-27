package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.activity.device.AddDeviceActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class RouterSettingActivity extends Activity implements View.OnClickListener {
    private static final String TAG="RouterSettingActivity";
    private ImageView image_back;
    private RelativeLayout layout_router_name_out;
    private RelativeLayout layout_room_select_out;
    private RelativeLayout layout_connect_type_select_out;
    private RelativeLayout layout_wifi_setting_out;
    private RelativeLayout layout_lan_setting_out;
    private RelativeLayout layout_QOS_setting_out;
    private RelativeLayout layout_update_out;
    private RelativeLayout layout_reboot_out;
    private Button buttton_delete_router;
    private RouterManager mRouterManager;
    private TextView textview_room_select_2;
    private TextView textview_route_name_2;

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
                List<Room>rooms=new ArrayList<>();
                rooms.addAll((ArrayList<Room>) o);
                Log.i(TAG,"所在房间列表大小"+((ArrayList<Room>) o).size());
                textview_room_select_2.setText("");
                for(int i=0;i<rooms.size();i++){
                    textview_room_select_2.append("("+rooms.get(i).getRoomName()+")");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void initDatas() {
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
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
        image_back = (ImageView) findViewById(R.id.image_back);
        layout_router_name_out = (RelativeLayout) findViewById(R.id.layout_router_name_out);
        layout_room_select_out = (RelativeLayout) findViewById(R.id.layout_room_select_out);
        layout_connect_type_select_out = (RelativeLayout) findViewById(R.id.layout_connect_type_select_out);
        layout_wifi_setting_out = (RelativeLayout) findViewById(R.id.layout_wifi_setting_out);
        layout_lan_setting_out = (RelativeLayout) findViewById(R.id.layout_lan_setting_out);
        layout_QOS_setting_out = (RelativeLayout) findViewById(R.id.layout_QOS_setting_out);
        layout_update_out = (RelativeLayout) findViewById(R.id.layout_update_out);
        layout_reboot_out = (RelativeLayout) findViewById(R.id.layout_reboot_out);
        buttton_delete_router = (Button) findViewById(R.id.buttton_delete_router);
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
                startActivity(new Intent(this, UpdateActivity.class));
                break;
            case R.id.layout_reboot_out:
                MakeSureDialog rebootRightNow = new MakeSureDialog(this);
                rebootRightNow.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        ToastSingleShow.showText(RouterSettingActivity.this, "已重启设备");
                        //TODO 本地远程重启路由器
                            /*
                                routerDevice.reboot();
                                RebootLocal();*/


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
}
