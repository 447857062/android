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
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class EncryptTypeActivity extends Activity implements View.OnClickListener{

    private RelativeLayout layout_type_strong;
    private RelativeLayout layout_type_mix;
    private RelativeLayout layout_type_none;
    private ImageView imageview_strong;
    private ImageView imageview_mix;
    private ImageView imageview_none;
    private String currentSelectEncryptType;
    private TextView textview_edit;
    private TextView textview_title;
    private FrameLayout image_back;
    private String wifiType;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_type);
        initViews();
        initDatas();
        initEvents();
    }

    private void initDatas() {
        textview_title.setText("加密方式");
        textview_edit.setText("保存");
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(EncryptTypeActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(EncryptTypeActivity.this, LoginActivity.class));
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
                        if (isSetEncrypt) {
                            ToastSingleShow.showText(EncryptTypeActivity.this, "设置成功");
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
        currentSelectEncryptType = getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_ENCRYPT_TYPE);
        wifiType = getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_TYPE);
        if (currentSelectEncryptType != null) {
            switch (currentSelectEncryptType) {
                case "psk2":
                    setCurrentEncrytype(R.id.layout_type_strong);
                    break;
                case "psk-mixed":
                    setCurrentEncrytype(R.id.layout_type_mix);
                    break;
                case "none":
                    setCurrentEncrytype(R.id.layout_type_none);
                    break;
            }
        }
    }


    private boolean isSetEncrypt;

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

    private void initEvents() {
        textview_edit.setOnClickListener(this);
        layout_type_strong.setOnClickListener(this);
        layout_type_mix.setOnClickListener(this);
        layout_type_none.setOnClickListener(this);
        image_back.setOnClickListener(this);

    }

    private void initViews() {
        textview_edit = findViewById(R.id.textview_edit);
        layout_type_strong = findViewById(R.id.layout_type_strong);
        layout_type_mix = findViewById(R.id.layout_type_mix);
        layout_type_none = findViewById(R.id.layout_type_none);
        imageview_strong = findViewById(R.id.imageview_strong);
        imageview_mix = findViewById(R.id.imageview_mix);
        imageview_none = findViewById(R.id.imageview_none);
        textview_title= findViewById(R.id.textview_title);
        image_back= findViewById(R.id.image_back);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                if (!currentSelectEncryptType.equals("")) {
                    isSetEncrypt = true;
                    // Wifi wifi = new Wifi();
                    Intent intent = new Intent();
                    try {
                        switch (wifiType) {
                            case AppConstant.WIFISETTING.WIFI_TYPE_2G:

                                intent.putExtra("encryptionType", currentSelectEncryptType);
                                setResult(RESULT_OK, intent);
                                break;
                            case AppConstant.WIFISETTING.WIFI_TYPE_VISITOR:
                                intent.putExtra("encryptionType", currentSelectEncryptType);
                                setResult(RESULT_OK, intent);
                                break;
                        }
                        this.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                break;
            case R.id.layout_type_strong:
                setCurrentEncrytype(R.id.layout_type_strong);
                break;
            case R.id.layout_type_mix:
                setCurrentEncrytype(R.id.layout_type_mix);
                break;
            case R.id.layout_type_none:
                setCurrentEncrytype(R.id.layout_type_none);
                break;

        }
    }

    private void setCurrentEncrytype(int id) {
        switch (id) {
            case R.id.layout_type_none:
                imageview_strong.setImageLevel(0);
                imageview_mix.setImageLevel(0);
                imageview_none.setImageLevel(1);
                currentSelectEncryptType = "none";
                break;
            case R.id.layout_type_mix:
                imageview_strong.setImageLevel(0);
                imageview_mix.setImageLevel(1);
                imageview_none.setImageLevel(0);
                currentSelectEncryptType = "psk-mixed";
                break;
            case R.id.layout_type_strong:
                imageview_strong.setImageLevel(1);
                imageview_mix.setImageLevel(0);
                imageview_none.setImageLevel(0);
                currentSelectEncryptType = "psk2";
                break;
        }

    }

}
