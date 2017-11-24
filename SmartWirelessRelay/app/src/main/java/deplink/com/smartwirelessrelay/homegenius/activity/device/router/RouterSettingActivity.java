package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class RouterSettingActivity extends Activity implements View.OnClickListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_setting);
        initViews();
        initDatas();
        initEvents();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.layout_router_name_out:
                break;
            case R.id.layout_room_select_out:
                break;
            case R.id.layout_connect_type_select_out:
                break;
            case R.id.layout_wifi_setting_out:
                break;
            case R.id.layout_lan_setting_out:
                break;
            case R.id.layout_QOS_setting_out:
                break;
            case R.id.layout_update_out:
                break;
            case R.id.layout_reboot_out:
                break;
            case R.id.buttton_delete_router:
                mRouterManager.deleteRouter(mRouterManager.getCurrentSelectedRouter(), new Observer() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                        int affectColumn= (int) o;
                        if(affectColumn>0){
                            startActivity(new Intent(RouterSettingActivity.this, DevicesActivity.class));
                        }else{
                            Message msg=Message.obtain();
                            msg.what=MSG_DELETE_ROUTER_FAIL;
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
    private static final int MSG_DELETE_ROUTER_FAIL=100;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_DELETE_ROUTER_FAIL:
                    Toast.makeText(RouterSettingActivity.this,"删除路由器失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
