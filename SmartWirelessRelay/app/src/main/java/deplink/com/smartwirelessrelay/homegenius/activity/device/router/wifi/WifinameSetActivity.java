package deplink.com.smartwirelessrelay.homegenius.activity.device.router.wifi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class WifinameSetActivity extends Activity implements View.OnClickListener{
    /**
     * wifi名称密码设置成功，后面要重启了
     */
    private static final int MSG_LOCAL_OP_RETURN_OK = 1;
    private TextView textview_title;
    private ImageView image_back;
    private String wifiname;
    private Button button_cancel;
    private Button button_save;
    private EditText edittext_wifi_name;
    private String wifiType;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiname_set);
        initViews();
        initDatas();
        initEvents();
    }
    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        isLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        if (isLogin) {
            routerDevice=mRouterManager.getRouterDevice();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initDatas() {
        textview_title.setText("WIFI名称");
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(WifinameSetActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(WifinameSetActivity.this, LoginActivity.class));
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
                switch (op) {
                    case RouterDevice.OP_SUCCESS:
                        if (isSetWifiname) {
                            ToastSingleShow.showText(WifinameSetActivity.this, "设置成功");
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
        try {
            wifiname = getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_NAME);
            wifiType = getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_TYPE);
            if (wifiname != null) {
                edittext_wifi_name.setText(wifiname);
                edittext_wifi_name.setSelection(wifiname.length());
            } else {
                wifiname = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
        button_save.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (ImageView) findViewById(R.id.image_back);

        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_save = (Button) findViewById(R.id.button_save);
        edittext_wifi_name = (EditText) findViewById(R.id.edittext_wifi_name);

    }

    private boolean isSetWifiname;
    private boolean isLogin;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_cancel:
                onBackPressed();
                break;
            case R.id.button_save:
                wifiname = edittext_wifi_name.getText().toString().trim();
                if (wifiname.equals("")) {
                    ToastSingleShow.showText(this, "还没有输入wifi名称");
                    return;
                }
                if (isLogin && routerDevice != null) {
                    Wifi content;
                    isSetWifiname = true;
                    Intent intent = new Intent();
                    switch (wifiType) {
                        case AppConstant.WIFISETTING.WIFI_TYPE_2G:
                            intent.putExtra("wifiname", wifiname);
                            setResult(RESULT_OK, intent);
                            break;
                        case AppConstant.WIFISETTING.WIFI_TYPE_VISITOR:
                            intent.putExtra("wifiname", wifiname);
                            setResult(RESULT_OK, intent);
                            break;
                    }
                    this.finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("wifiname", wifiname);
                    setResult(RESULT_OK, intent);
                    this.finish();


                }

                break;
        }
    }
}
