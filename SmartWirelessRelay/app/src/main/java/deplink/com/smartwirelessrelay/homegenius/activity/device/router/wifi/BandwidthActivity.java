package deplink.com.smartwirelessrelay.homegenius.activity.device.router.wifi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

public class BandwidthActivity extends Activity implements View.OnClickListener{
    private static final String TAG="BandwidthActivity";
    private FrameLayout layout_back;
    private Button button_cancel;
    private Button button_save;
    private RelativeLayout layout_bandwidth_20;
    private RelativeLayout layout_bandwidth_40;
    private ImageView imageview_bandwidth_20;
    private ImageView imageview_bandwidth_40;
    private String currentBandwidth;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandwidth);
        try {
            initViews();
            initDatas();
            initEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initDatas() {
        connectLostDialog = new MakeSureDialog(BandwidthActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(BandwidthActivity.this, LoginActivity.class));
            }
        });
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
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
                        if (isSetBandWidth) {
                            ToastSingleShow.showText(BandwidthActivity.this, "设置成功");
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
        currentBandwidth = getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_BANDWIDTH);
        if (currentBandwidth != null) {
            switch (currentBandwidth) {
                case "0":
                    Log.i(TAG,"currentBandwidth="+currentBandwidth);
                    setCurrentBandWidth(R.id.layout_bandwidth_20);
                    break;
                case "1":
                    setCurrentBandWidth(R.id.layout_bandwidth_40);
                    break;


            }
        }
    }
    private boolean isSetBandWidth;
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
        button_cancel.setOnClickListener(this);
        button_save.setOnClickListener(this);
        layout_bandwidth_20.setOnClickListener(this);
        layout_bandwidth_40.setOnClickListener(this);
        layout_back.setOnClickListener(this);
    }

    private void initViews() {
        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_save = (Button) findViewById(R.id.button_save);
        layout_bandwidth_20 = (RelativeLayout) findViewById(R.id.layout_bandwidth_20);
        layout_bandwidth_40 = (RelativeLayout) findViewById(R.id.layout_bandwidth_40);
        imageview_bandwidth_20 = (ImageView) findViewById(R.id.imageview_bandwidth_20);
        imageview_bandwidth_40 = (ImageView) findViewById(R.id.imageview_bandwidth_40);
        layout_back = (FrameLayout) findViewById(R.id.layout_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                onBackPressed();
                break;
            case R.id.button_cancel:
                onBackPressed();
                break;
            case R.id.button_save:

                if (!currentBandwidth.equals("")) {
                    Intent intent = new Intent();
                    intent.putExtra("bandwidth", currentBandwidth);
                    setResult(RESULT_OK, intent);
                    this.finish();
                }
                break;
            case R.id.layout_bandwidth_20:

                setCurrentBandWidth(R.id.layout_bandwidth_20);
                break;
            case R.id.layout_bandwidth_40:
                setCurrentBandWidth(R.id.layout_bandwidth_40);

                break;
        }
    }

    private void setCurrentBandWidth(int id) {
        switch (id){
            case R.id.layout_bandwidth_20:
                imageview_bandwidth_20.setImageLevel(1);
                imageview_bandwidth_40.setImageLevel(0);
                currentBandwidth = "0";
                break;
            case R.id.layout_bandwidth_40:
                imageview_bandwidth_20.setImageLevel(0);
                imageview_bandwidth_40.setImageLevel(1);
                currentBandwidth = "1";
                break;
        }

    }
}
