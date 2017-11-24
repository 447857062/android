package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

public class AddRouterActivity extends Activity implements View.OnClickListener{
    private static final String TAG="AddRouterActivity";
    private Button button_add_device_sure;
    private RouterManager mRouterManager;
    private EditText edittext_add_device_input_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_router);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        currentAddRoom = getIntent().getStringExtra("roomName");
        routerSN = getIntent().getStringExtra("routerSN");
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
    }

    private void initEvents() {
        button_add_device_sure.setOnClickListener(this);
    }

    private void initViews() {
        button_add_device_sure= (Button) findViewById(R.id.button_add_device_sure);
        edittext_add_device_input_name= (EditText) findViewById(R.id.edittext_add_device_input_name);
    }
    private String currentAddRoom;
    private String routerSN;
    private String routerName;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_add_device_sure:
                //TODO 添加添加路由器
                SmartDev routerDev=new SmartDev();
                routerDev.setUid(routerSN);
                routerDev.setType("路由器");
                mRouterManager.saveRouter(routerDev);
                Log.i(TAG,"添加路由器的房间="+currentAddRoom);
                Room room= RoomManager.getInstance().findRoom(currentAddRoom,true);
                routerName=edittext_add_device_input_name.getText().toString();
                if(routerName.equals("")){
                    routerName="新路由器";
                }
                mRouterManager.updateDeviceInWhatRoom(room,routerSN,routerName);
                break;
        }
    }
}
