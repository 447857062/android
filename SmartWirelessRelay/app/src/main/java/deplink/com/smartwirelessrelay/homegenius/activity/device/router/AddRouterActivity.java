package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

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

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class AddRouterActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "AddRouterActivity";
    private Button button_add_device_sure;
    private RouterManager mRouterManager;
    private EditText edittext_add_device_input_name;
    private TextView textview_select_room_name;
    private TextView textview_title;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_router);
        initViews();
        initDatas();
        initEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        textview_select_room_name.setText(RoomManager.getInstance().getCurrentSelectedRoom().getRoomName());
    }

    private void initDatas() {
        textview_title.setText("添加路由器");
        currentAddRoom = getIntent().getStringExtra("roomName");
        routerSN = getIntent().getStringExtra("routerSN");
        mRouterManager = RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
    }

    private void initEvents() {
        button_add_device_sure.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initViews() {
        button_add_device_sure = (Button) findViewById(R.id.button_add_device_sure);
        edittext_add_device_input_name = (EditText) findViewById(R.id.edittext_add_device_input_name);
        textview_select_room_name = (TextView) findViewById(R.id.textview_select_room_name);
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (FrameLayout) findViewById(R.id.image_back);
    }

    private static final int MSG_ADD_ROUTER_FAIL = 100;
    /**
     * 更新路由器所在房间失败
     */
    private static final int MSG_UPDATE_ROOM_FAIL = 101;
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
            }
        }
    };
    private String currentAddRoom;
    private String routerSN;
    private String routerName;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_add_device_sure:
                //TODO 添加添加路由器
                SmartDev routerDev = new SmartDev();
                routerDev.setUid(routerSN);
                routerDev.setType("路由器");
                mRouterManager.saveRouter(routerDev, new Observer() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                        if ((boolean) o) {
                            Log.i(TAG, "添加路由器的房间=" + currentAddRoom);
                            Room room = RoomManager.getInstance().findRoom(currentAddRoom, true);
                            routerName = edittext_add_device_input_name.getText().toString();
                            if (routerName.equals("")) {
                                routerName = "新路由器";
                            }
                            mRouterManager.updateDeviceInWhatRoom(room, routerSN, routerName, new Observer() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull Object o) {
                                    if ((boolean) o) {
                                        startActivity(new Intent(AddRouterActivity.this, DevicesActivity.class));
                                    }else{
                                        Message msg = Message.obtain();
                                        msg.what = MSG_UPDATE_ROOM_FAIL;
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
                        } else {
                            Message msg = Message.obtain();
                            msg.what = MSG_ADD_ROUTER_FAIL;
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
}
