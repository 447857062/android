package deplink.com.smartwirelessrelay.homegenius.activity.device.router.wifi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ChannelActivity extends Activity implements View.OnClickListener{
    private static final String TAG="ChannelActivity";
    private RelativeLayout layout_channel_auto;
    private RelativeLayout layout_channel_1;
    private RelativeLayout layout_channel_2;
    private RelativeLayout layout_channel_3;
    private RelativeLayout layout_channel_4;
    private RelativeLayout layout_channel_5;
    private RelativeLayout layout_channel_6;
    private RelativeLayout layout_channel_7;
    private RelativeLayout layout_channel_8;
    private RelativeLayout layout_channel_9;
    private RelativeLayout layout_channel_10;
    private RelativeLayout layout_channel_11;
    private RelativeLayout layout_channel_12;
    private RelativeLayout layout_channel_13;
    private ImageView imageview_channel_auto;
    private ImageView imageview_channel_1;
    private ImageView imageview_channel_2;
    private ImageView imageview_channel_3;
    private ImageView imageview_channel_4;
    private ImageView imageview_channel_5;
    private ImageView imageview_channel_6;
    private ImageView imageview_channel_7;
    private ImageView imageview_channel_8;
    private ImageView imageview_channel_9;
    private ImageView imageview_channel_10;
    private ImageView imageview_channel_11;
    private ImageView imageview_channel_12;
    private ImageView imageview_channel_13;

    private String currentChannel;
   private TextView textview_edit;
    private SDKManager manager;
    private EventCallback ec;
    private MakeSureDialog connectLostDialog;

    private TextView textview_title;
    private ImageView image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        initViews();
        initDatas();
        initEvents();
    }
    private void initDatas() {
        textview_title.setText("信道");
        textview_edit.setText("保存");
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new MakeSureDialog(ChannelActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(ChannelActivity.this, LoginActivity.class));
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
                        if (isSetChannel) {
                            ToastSingleShow.showText(ChannelActivity.this, "设置成功");
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
        currentChannel=getIntent().getStringExtra(AppConstant.WIFISETTING.WIFI_CHANNEL_TYPE);
        Log.i(TAG,"currentChannel="+currentChannel);
        if(currentChannel!=null){
            Log.i(TAG,"currentChannel!=null");
            switch (currentChannel){
                case "0":
                    Log.i(TAG,"currentChannel case 0");
                    setCurrentChannel(R.id.layout_channel_auto);
                    break;
                case "1":
                    setCurrentChannel(R.id.layout_channel_1);
                    break;
                case "2":
                    setCurrentChannel(R.id.layout_channel_2);
                    break;
                case "3":
                    setCurrentChannel(R.id.layout_channel_3);
                    break;
                case "4":
                    setCurrentChannel(R.id.layout_channel_4);
                    break;
                case "5":
                    setCurrentChannel(R.id.layout_channel_5);
                    break;
                case "6":
                    setCurrentChannel(R.id.layout_channel_6);
                    break;
                case "7":
                    setCurrentChannel(R.id.layout_channel_7);
                    break;
                case "8":
                    setCurrentChannel(R.id.layout_channel_8);
                    break;
                case "9":
                    setCurrentChannel(R.id.layout_channel_9);
                    break;
                case "10":
                    setCurrentChannel(R.id.layout_channel_10);
                    break;
                case "11":
                    setCurrentChannel(R.id.layout_channel_11);
                    break;
                case "12":
                    setCurrentChannel(R.id.layout_channel_12);
                    break;
                case "13":
                    setCurrentChannel(R.id.layout_channel_13);
                    break;

            }
        }else{
            Log.i(TAG,"currentChannel=null");
        }
    }
    private  boolean isSetChannel;
    private void initEvents() {
        textview_edit.setOnClickListener(this);
        image_back.setOnClickListener(this);
        layout_channel_auto.setOnClickListener(this);
        layout_channel_1.setOnClickListener(this);
        layout_channel_2.setOnClickListener(this);
        layout_channel_3.setOnClickListener(this);
        layout_channel_4.setOnClickListener(this);
        layout_channel_5.setOnClickListener(this);
        layout_channel_6.setOnClickListener(this);
        layout_channel_7.setOnClickListener(this);
        layout_channel_8.setOnClickListener(this);
        layout_channel_9.setOnClickListener(this);
        layout_channel_10.setOnClickListener(this);
        layout_channel_11.setOnClickListener(this);
        layout_channel_12.setOnClickListener(this);
        layout_channel_13.setOnClickListener(this);
    }

    private void initViews() {
        textview_title= (TextView) findViewById(R.id.textview_title);
        image_back= (ImageView) findViewById(R.id.image_back);
        textview_edit= (TextView) findViewById(R.id.textview_edit);
        layout_channel_auto= (RelativeLayout) findViewById(R.id.layout_channel_auto);
        layout_channel_1= (RelativeLayout) findViewById(R.id.layout_channel_1);
        layout_channel_2= (RelativeLayout) findViewById(R.id.layout_channel_2);
        layout_channel_3= (RelativeLayout) findViewById(R.id.layout_channel_3);
        layout_channel_4= (RelativeLayout) findViewById(R.id.layout_channel_4);
        layout_channel_5= (RelativeLayout) findViewById(R.id.layout_channel_5);
        layout_channel_6= (RelativeLayout) findViewById(R.id.layout_channel_6);
        layout_channel_7= (RelativeLayout) findViewById(R.id.layout_channel_7);
        layout_channel_8= (RelativeLayout) findViewById(R.id.layout_channel_8);
        layout_channel_9= (RelativeLayout) findViewById(R.id.layout_channel_9);
        layout_channel_10= (RelativeLayout) findViewById(R.id.layout_channel_10);
        layout_channel_11= (RelativeLayout) findViewById(R.id.layout_channel_11);
        layout_channel_12= (RelativeLayout) findViewById(R.id.layout_channel_12);
        layout_channel_13= (RelativeLayout) findViewById(R.id.layout_channel_13);
        imageview_channel_auto= (ImageView) findViewById(R.id.imageview_channel_auto);
        imageview_channel_1= (ImageView) findViewById(R.id.imageview_channel_1);
        imageview_channel_2= (ImageView) findViewById(R.id.imageview_channel_2);
        imageview_channel_3= (ImageView) findViewById(R.id.imageview_channel_3);
        imageview_channel_4= (ImageView) findViewById(R.id.imageview_channel_4);
        imageview_channel_5= (ImageView) findViewById(R.id.imageview_channel_5);
        imageview_channel_6= (ImageView) findViewById(R.id.imageview_channel_6);
        imageview_channel_7= (ImageView) findViewById(R.id.imageview_channel_7);
        imageview_channel_8= (ImageView) findViewById(R.id.imageview_channel_8);
        imageview_channel_9= (ImageView) findViewById(R.id.imageview_channel_9);
        imageview_channel_10= (ImageView) findViewById(R.id.imageview_channel_10);
        imageview_channel_11= (ImageView) findViewById(R.id.imageview_channel_11);
        imageview_channel_12= (ImageView) findViewById(R.id.imageview_channel_12);
        imageview_channel_13= (ImageView) findViewById(R.id.imageview_channel_13);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.textview_edit:
                isSetChannel=true;
                if(!currentChannel.equals("")){
                    Intent intent = new Intent();
                    intent.putExtra("channel", currentChannel);
                    setResult(RESULT_OK, intent);
                    this.finish();
                }
                break;
            case R.id.layout_channel_auto:
                Log.i(TAG,"layout_channel_auto click");
                setCurrentChannel(R.id.layout_channel_auto);
                break;
            case R.id.layout_channel_1:
                setCurrentChannel(R.id.layout_channel_1);

                break;
            case R.id.layout_channel_2:
                setCurrentChannel(R.id.layout_channel_2);

                break;
            case R.id.layout_channel_3:
                setCurrentChannel(R.id.layout_channel_3);
                break;
            case R.id.layout_channel_4:
                setCurrentChannel(R.id.layout_channel_4);
                break;
            case R.id.layout_channel_5:
                setCurrentChannel(R.id.layout_channel_5);
                break;
            case R.id.layout_channel_6:
                setCurrentChannel(R.id.layout_channel_6);
                break;
            case R.id.layout_channel_7:
                setCurrentChannel(R.id.layout_channel_7);
                break;
            case R.id.layout_channel_8:
                setCurrentChannel(R.id.layout_channel_8);
                break;
            case R.id.layout_channel_9:
                setCurrentChannel(R.id.layout_channel_9);

                break;
            case R.id.layout_channel_10:
                setCurrentChannel(R.id.layout_channel_10);

                break;
            case R.id.layout_channel_11:
                setCurrentChannel(R.id.layout_channel_11);

                break;
            case R.id.layout_channel_12:
                setCurrentChannel(R.id.layout_channel_12);

                break;
            case R.id.layout_channel_13:
                setCurrentChannel(R.id.layout_channel_13);

                break;


        }
    }

    private void setCurrentChannel(int id) {
        imageview_channel_auto.setImageLevel(0);
        imageview_channel_1.setImageLevel(0);
        imageview_channel_2.setImageLevel(0);
        imageview_channel_3.setImageLevel(0);
        imageview_channel_4.setImageLevel(0);
        imageview_channel_5.setImageLevel(0);
        imageview_channel_6.setImageLevel(0);
        imageview_channel_7.setImageLevel(0);
        imageview_channel_8.setImageLevel(0);
        imageview_channel_9.setImageLevel(0);
        imageview_channel_10.setImageLevel(0);
        imageview_channel_11.setImageLevel(0);
        imageview_channel_12.setImageLevel(0);
        imageview_channel_13.setImageLevel(0);
        switch (id){
            case R.id.layout_channel_auto:
                imageview_channel_auto.setImageLevel(1);
                currentChannel="0";
                break;
            case R.id.layout_channel_1:
                imageview_channel_1.setImageLevel(1);
                currentChannel="1";
                break;
            case R.id.layout_channel_2:
                imageview_channel_2.setImageLevel(1);
                currentChannel="2";
                break;
            case R.id.layout_channel_3:
                imageview_channel_3.setImageLevel(1);
                currentChannel="3";
                break;
            case R.id.layout_channel_4:
                imageview_channel_4.setImageLevel(1);
                currentChannel="4";
                break;
            case R.id.layout_channel_5:
                imageview_channel_5.setImageLevel(1);
                currentChannel="5";
                break;
            case R.id.layout_channel_6:
                imageview_channel_6.setImageLevel(1);
                currentChannel="6";
                break;
            case R.id.layout_channel_7:
                imageview_channel_7.setImageLevel(1);
                currentChannel="7";
                break;
            case R.id.layout_channel_8:
                imageview_channel_8.setImageLevel(1);
                currentChannel="8";
                break;
            case R.id.layout_channel_9:
                imageview_channel_9.setImageLevel(1);
                currentChannel="9";
                break;
            case R.id.layout_channel_10:
                imageview_channel_10.setImageLevel(1);
                currentChannel="10";
                break;
            case R.id.layout_channel_11:
                imageview_channel_11.setImageLevel(1);
                currentChannel="11";
                break;
            case R.id.layout_channel_12:
                imageview_channel_12.setImageLevel(1);
                currentChannel="12";
                break;
            case R.id.layout_channel_13:
                imageview_channel_13.setImageLevel(1);
                currentChannel="13";
                break;
        }

    }
}
