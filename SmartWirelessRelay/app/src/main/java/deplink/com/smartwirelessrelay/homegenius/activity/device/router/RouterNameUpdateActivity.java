package deplink.com.smartwirelessrelay.homegenius.activity.device.router;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.view.edittext.ClearEditText;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class RouterNameUpdateActivity extends Activity implements View.OnClickListener{
    private ClearEditText edittext_router_name;
    private TextView textview_complement;
    private RouterManager mRouterManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_name_update);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
    }

    private void initEvents() {
        textview_complement.setOnClickListener(this);
    }

    private void initViews() {
        edittext_router_name= (ClearEditText) findViewById(R.id.edittext_router_name);
        textview_complement= (TextView) findViewById(R.id.textview_complement);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview_complement:
                final String routerName=edittext_router_name.getText().toString();
                if(!routerName.equals("")){
                    mRouterManager.updateRouterName(routerName, new Observer() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Object o) {
                                if((int)o>0){
                                    mRouterManager.getCurrentSelectedRouter().setName(routerName);
                                RouterNameUpdateActivity.this.finish();
                                }else{
                                    Message msg=Message.obtain();
                                    msg.what=MSG_UPDATE_NAME_FAIL;
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
                }

                break;
        }
    }
    private static final int MSG_UPDATE_NAME_FAIL=100;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_NAME_FAIL:
                    Toast.makeText(RouterNameUpdateActivity.this,"更新路由器名称失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}