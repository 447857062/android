package deplink.com.smartwirelessrelay.homegenius.activity.device.router.wifi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.json.Wifi;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class ModeSelectActivity extends Activity implements View.OnClickListener{


    private TextView textview_title;
    private FrameLayout image_back;
    private RelativeLayout layout_model_AC;
    private RelativeLayout layout_model_N;
    private RelativeLayout layout_model_4;
    private RelativeLayout layout_model_7;
    private RelativeLayout layout_model_9;

    private ImageView imageview_model_ac;
    private ImageView imageview_model_n;
    private ImageView imageview_model_4;
    private ImageView imageview_model_7;
    private ImageView imageview_model_9;
    private String currentMode;
   private TextView textview_edit;
    private SDKManager manager;
    private EventCallback ec;
    private String wifiType;
    private MakeSureDialog connectLostDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);
        initViews();
        initDatas();
        initEvents();
    }
    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
    }



    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }
    private void initDatas() {
        textview_title.setText("模式");
        textview_edit.setText("保存");
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(ModeSelectActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(ModeSelectActivity.this, LoginActivity.class));
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
            public void onFailure(SDKAction action, Throwable throwable) {

            }
            @Override
            public void deviceOpSuccess(String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op){
                    case RouterDevice.OP_SUCCESS:
                        if (isSetModel) {
                            ToastSingleShow.showText(ModeSelectActivity.this, "设置成功");
                        }
                        break;
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");

            }
        };
        currentMode=getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_MODE_TYPE);
        wifiType=getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_TYPE);
        if(currentMode!=null){
            switch (currentMode){
                case "0":
                    setCurrentModel(R.id.layout_model_AC);
                    break;
                case "1":
                    setCurrentModel(R.id.layout_model_N);
                    break;
                case "4":
                    setCurrentModel(R.id.layout_model_4);
                    break;
                case "7":
                    setCurrentModel(R.id.layout_model_7);
                    break;
                case "9":
                    setCurrentModel(R.id.layout_model_9);
                    break;
            }
        }
    }
    private boolean isSetModel;
    private void initEvents() {
        image_back.setOnClickListener(this);
        layout_model_AC.setOnClickListener(this);
        layout_model_N.setOnClickListener(this);
        textview_edit.setOnClickListener(this);
        layout_model_4.setOnClickListener(this);
        layout_model_7.setOnClickListener(this);
        layout_model_9.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= findViewById(R.id.textview_title);
        image_back= findViewById(R.id.image_back);
        layout_model_AC= findViewById(R.id.layout_model_AC);
        layout_model_N= findViewById(R.id.layout_model_N);
        layout_model_4= findViewById(R.id.layout_model_4);
        layout_model_7= findViewById(R.id.layout_model_7);
        layout_model_9= findViewById(R.id.layout_model_9);
        imageview_model_ac= findViewById(R.id.imageview_model_ac);
        imageview_model_n= findViewById(R.id.imageview_model_n);
        imageview_model_4= findViewById(R.id.imageview_model_4);
        imageview_model_7= findViewById(R.id.imageview_model_7);
        imageview_model_9= findViewById(R.id.imageview_model_9);
        textview_edit= findViewById(R.id.textview_edit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                if(!currentMode.equals("")){
                    isSetModel=true;
                    Wifi wifi=new Wifi();
                    switch (wifiType){
                        case AppConstant.WIFISETTING.WIFI_TYPE_2G:
                            Intent intent = new Intent();
                            intent.putExtra("model", currentMode);
                            setResult(RESULT_OK, intent);
                            this.finish();
                            break;
                    }

                }
                break;
            case R.id.layout_model_AC:
                setCurrentModel(R.id.layout_model_AC);
                break;
            case R.id.layout_model_N:
                setCurrentModel(R.id.layout_model_N);
                break;
            case R.id.layout_model_4:
                setCurrentModel(R.id.layout_model_4);
                break;
            case R.id.layout_model_7:
                setCurrentModel(R.id.layout_model_7);
                break;
            case R.id.layout_model_9:
                setCurrentModel(R.id.layout_model_9);
                break;

        }
    }

    private void setCurrentModel(int id) {
        switch (id){
            case R.id.layout_model_9:
                imageview_model_ac.setImageLevel(0);
                imageview_model_n.setImageLevel(0);
                imageview_model_4.setImageLevel(0);
                imageview_model_7.setImageLevel(0);
                imageview_model_9.setImageLevel(1);
                currentMode="9";
                break;
            case R.id.layout_model_7:
                imageview_model_ac.setImageLevel(0);
                imageview_model_n.setImageLevel(0);
                imageview_model_4.setImageLevel(0);
                imageview_model_7.setImageLevel(1);
                imageview_model_9.setImageLevel(0);
                currentMode="7";
                break;
            case R.id.layout_model_4:
                imageview_model_ac.setImageLevel(0);
                imageview_model_n.setImageLevel(0);
                imageview_model_4.setImageLevel(1);
                imageview_model_7.setImageLevel(0);
                imageview_model_9.setImageLevel(0);
                currentMode="4";
                break;
            case R.id.layout_model_N:
                imageview_model_ac.setImageLevel(0);
                imageview_model_n.setImageLevel(1);
                imageview_model_4.setImageLevel(0);
                imageview_model_7.setImageLevel(0);
                imageview_model_9.setImageLevel(0);
                currentMode="1";
                break;
            case R.id.layout_model_AC:
                imageview_model_ac.setImageLevel(1);
                imageview_model_n.setImageLevel(0);
                imageview_model_4.setImageLevel(0);
                imageview_model_7.setImageLevel(0);
                imageview_model_9.setImageLevel(0);
                currentMode="0";
                break;
        }
    }
}
