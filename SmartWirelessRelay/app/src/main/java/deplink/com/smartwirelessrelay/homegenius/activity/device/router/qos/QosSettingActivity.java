package deplink.com.smartwirelessrelay.homegenius.activity.device.router.qos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.json.Qos;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.device.router.RouterManager;
import deplink.com.smartwirelessrelay.homegenius.util.NetUtil;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

public class QosSettingActivity extends Activity implements View.OnClickListener{
    private TextView textview_title;
    private ImageView image_back;
    private RelativeLayout layout_model_A;
    private RelativeLayout layout_model_B;
    private RelativeLayout layout_model_download;
    private ImageView imageview_model_a;
    private ImageView imageview_model_b;
    private ImageView imageview_model_download;
    private String currentQosMode;
    private Button button_cancel;
    private Button button_save;
    private SDKManager manager;
    private EventCallback ec;
    private RouterDevice routerDevice;
    private CheckBox checkbox_qos_switch;
    private MakeSureDialog connectLostDialog;
    private RouterManager mRouterManager;
    private Qos qos;
    private boolean isSetQos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qos_setting);
        initViews();
        initDatas();
        initEvents();

    }

    private void initDatas() {
        textview_title.setText("智能分配类型选择");
        mRouterManager=RouterManager.getInstance();
        mRouterManager.InitRouterManager(this);
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(QosSettingActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(QosSettingActivity.this, LoginActivity.class));
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
            public void deviceOpSuccess(final String op, String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (op) {
                            case RouterDevice.OP_GET_QOS:
                                try {
                                    qos = routerDevice.getQos();
                                    if (qos.getSWITCH().equalsIgnoreCase("ON")) {
                                        layout_model_A.setVisibility(View.VISIBLE);
                                        layout_model_B.setVisibility(View.VISIBLE);
                                        layout_model_download.setVisibility(View.VISIBLE);
                                        checkbox_qos_switch.setChecked(true);
                                        currentQosMode = qos.getCLASSIFY();
                                        switch (currentQosMode) {
                                            case "1":
                                                layout_model_A.callOnClick();
                                                break;
                                            case "2":
                                                layout_model_B.callOnClick();
                                                break;
                                            case "3":
                                                layout_model_download.callOnClick();
                                                break;
                                        }
                                    } else if (qos.getSWITCH().equalsIgnoreCase("OFF")) {
                                        checkbox_qos_switch.setChecked(false);
                                        layout_model_A.setVisibility(View.GONE);
                                        layout_model_B.setVisibility(View.GONE);
                                        layout_model_download.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case RouterDevice.OP_SUCCESS:
                                if (isSetQos) {
                                    ToastSingleShow.showText(QosSettingActivity.this, "设置成功");
                                }
                                break;
                        }
                    }
                });

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
    }



    @Override
    protected void onResume() {
        super.onResume();
        routerDevice=mRouterManager.getRouterDevice();
        manager.addEventCallback(ec);
        if (NetUtil.isNetAvailable(this)) {
            if(routerDevice!=null){
                routerDevice.queryQos();
            }
        } else {
            ToastSingleShow.showText(this, "网络连接已断开");
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        layout_model_A.setOnClickListener(this);
        layout_model_B.setOnClickListener(this);
        layout_model_download.setOnClickListener(this);
        button_cancel.setOnClickListener(this);
        button_save.setOnClickListener(this);
        checkbox_qos_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    layout_model_A.setVisibility(View.VISIBLE);
                    layout_model_B.setVisibility(View.VISIBLE);
                    layout_model_download.setVisibility(View.VISIBLE);
                }else{
                    layout_model_A.setVisibility(View.GONE);
                    layout_model_B.setVisibility(View.GONE);
                    layout_model_download.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (ImageView) findViewById(R.id.image_back);
        layout_model_A = (RelativeLayout) findViewById(R.id.layout_model_A);
        layout_model_B = (RelativeLayout) findViewById(R.id.layout_model_B);
        layout_model_download = (RelativeLayout) findViewById(R.id.layout_model_download);
        imageview_model_a = (ImageView) findViewById(R.id.imageview_model_a);
        imageview_model_b = (ImageView) findViewById(R.id.imageview_model_b);
        imageview_model_download = (ImageView) findViewById(R.id.imageview_model_download);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_save = (Button) findViewById(R.id.button_save);
        checkbox_qos_switch = (CheckBox) findViewById(R.id.checkbox_qos_switch);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                onBackPressed();
                break;
            case R.id.button_save:
                try {
                    qos = new Qos();
                    qos.setCLASSIFY(currentQosMode);
                    if (checkbox_qos_switch.isChecked()) {
                        qos.setSWITCH("ON");
                    } else {
                        qos.setSWITCH("OFF");
                    }
                    if (NetUtil.isNetAvailable(this)) {
                        boolean isUserLogin;
                        isUserLogin= Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
                        if(isUserLogin){
                            isSetQos = true;
                            ToastSingleShow.showText(this, "QOS已设置");
                            routerDevice.setQos(qos);
                        }else{
                            ToastSingleShow.showText(this,"未登录，无法设置静态上网,请登录后重试");
                        }

                    } else {
                        ToastSingleShow.showText(this, "网络连接已断开");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.layout_model_A:
                imageview_model_a.setImageLevel(1);
                imageview_model_b.setImageLevel(0);
                imageview_model_download.setImageLevel(0);
                currentQosMode = "1";
                break;
            case R.id.layout_model_B:
                imageview_model_a.setImageLevel(0);
                imageview_model_b.setImageLevel(1);
                imageview_model_download.setImageLevel(0);

                currentQosMode = "2";
                break;
            case R.id.layout_model_download:
                imageview_model_a.setImageLevel(0);
                imageview_model_b.setImageLevel(0);
                imageview_model_download.setImageLevel(1);

                currentQosMode = "3";
                break;
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
