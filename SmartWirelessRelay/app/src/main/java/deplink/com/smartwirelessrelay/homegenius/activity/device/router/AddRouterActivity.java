package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManagerListener;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetUtil;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class AddRouterActivity extends Activity implements View.OnClickListener, RouterManagerListener {
    private static final String TAG = "AddRouterActivity";
    private Button button_add_device_sure;
    private RouterManager mRouterManager;
    private EditText edittext_add_device_input_name;
    private TextView textview_select_room_name;
    private TextView textview_title;
    private FrameLayout image_back;
    private String routerSN;
    private String routerName;

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
        mRouterManager.removeSmartLockListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRouterManager.addSmartLockListener(this);
        if (RoomManager.getInstance().getCurrentSelectedRoom() == null) {
            textview_select_room_name.setText("全部");
        } else {
            textview_select_room_name.setText(RoomManager.getInstance().getCurrentSelectedRoom().getRoomName());
        }

    }


    private void initDatas() {
        textview_title.setText("添加路由器");
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
        textview_title = (TextView) findViewById(R.id.textview_title);
        image_back = (FrameLayout) findViewById(R.id.image_back);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_add_device_sure:
                if (NetUtil.isNetAvailable(AddRouterActivity.this)) {
                    mRouterManager.setUserBindAction(true);
                    mRouterManager.setRouterSN(routerSN);
                    routerName = edittext_add_device_input_name.getText().toString();
                    if (routerName.equals("")) {
                        routerName = "家里的路由器";
                    }
                    mRouterManager.setRouterName(routerName);
                    mRouterManager.bindDevice(routerSN);

                } else {
                    ToastSingleShow.showText(AddRouterActivity.this, " 网络连接不可用，请检查网络连接");
                }
                break;
        }
    }

    @Override
    public void responseAddyResult(int result) {
        mHandler.sendEmptyMessage(result);
    }
}
