package com.deplink.homegenius.activity.personal.softupdate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deplink.homegenius.activity.device.DevicesActivity;
import com.deplink.homegenius.activity.homepage.SmartHomeMainActivity;
import com.deplink.homegenius.activity.personal.HomeNetWorkActivity;
import com.deplink.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.activity.personal.usrinfo.UserinfoActivity;
import com.deplink.homegenius.activity.room.RoomActivity;
import com.deplink.homegenius.application.AppManager;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.util.APKVersionCodeUtils;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.ConfirmDialog;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.imageview.CircleImageView;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.homegenius.UserInfoAlertBody;
import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class PersonalCenterActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "PersonalCenterActivity";
    private RelativeLayout layout_getway_check;
    private RelativeLayout layout_experience_center;
    private LinearLayout layout_home_page;
    private LinearLayout layout_devices;
    private LinearLayout layout_rooms;
    private LinearLayout layout_personal_center;
    private TextView button_logout;
    private CircleImageView user_head_portrait;
    private ImageView imageview_devices;
    private ImageView imageview_home_page;
    private ImageView imageview_rooms;
    private ImageView imageview_personal_center;
    private TextView textview_home;
    private TextView textview_device;
    private TextView textview_room;
    private TextView textview_mine;
    private SDKManager manager;
    private EventCallback ec;
    private DeleteDeviceDialog connectLostDialog;
    private boolean isUserLogin;
    private boolean hasGetUserImage;
    private ConfirmDialog mLogoutDialog;
    private TextView user_nickname;
    private TextView textview_update_now;
    private boolean isAppUpdate = false;
    private boolean isClickUpdate;
    private RelativeLayout layout_update_soft;
    private TextView textview_current_version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        initViews();
        initDatas();
        initEvents();

    }
    @Override
    protected void onResume() {
        super.onResume();
        manager.addEventCallback(ec);
        textview_home.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_device.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_room.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textview_mine.setTextColor(getResources().getColor(R.color.title_blue_bg));
        imageview_home_page.setImageResource(R.drawable.nocheckthehome);
        imageview_devices.setImageResource(R.drawable.nocheckthedevice);
        imageview_rooms.setImageResource(R.drawable.nochecktheroom);
        imageview_personal_center.setImageResource(R.drawable.checkthemine);
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        if (isUserLogin) {
             hasGetUserImage = Perfence.getBooleanPerfence(AppConstant.USER.USER_GETIMAGE_FROM_SERVICE);
            if (!hasGetUserImage) {
                Perfence.setPerfence(AppConstant.USER.USER_GETIMAGE_FROM_SERVICE, true);
                manager.getImage(Perfence.getPerfence(Perfence.PERFENCE_PHONE));
            } else {
                setLocalImage(user_head_portrait);
            }
            button_logout.setText("退出登录");
            String userName=Perfence.getPerfence(Perfence.PERFENCE_PHONE);
            manager.getUserInfo(userName);
            manager.queryAppUpdateInfo(Perfence.SDK_APP_KEY, APKVersionCodeUtils.getVerName(this));
            if( manager.getAppUpdateInfo()!=null){
                String version = manager.getAppUpdateInfo().getVersion();
                int oldVersion=Integer.valueOf(APKVersionCodeUtils.getVerName(PersonalCenterActivity.this).replace(".",""));
                int newVersion=Integer.valueOf(version.replace(".",""));
                isAppUpdate = !APKVersionCodeUtils.getVerName(PersonalCenterActivity.this).equals(version) && (newVersion > oldVersion);
            }else{
                isAppUpdate = false;
                textview_update_now.setText("已是最新版本");
            }
            textview_current_version.setText("当前版本:"+APKVersionCodeUtils.getVerName(this));
        }else{
            button_logout.setText("登录");
        }

    }
    private void setLocalImage(CircleImageView user_head_portrait) {
        boolean isSdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
        if (isSdCardExist) {
            String path = this.getFilesDir().getAbsolutePath();
            path = path + File.separator + "userIcon" + "userIcon.png";
            File file = new File(path);
            if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(file.getPath());
                // 将图片显示到ImageView中
                user_head_portrait.setImageBitmap(bm);
            } else {
                user_head_portrait.setImageDrawable(getResources().getDrawable(R.drawable.defaultavatar));
            }
        } else {
            ToastSingleShow.showText(this, "sd卡不存在！");
        }
    }
    private void initDatas() {
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        connectLostDialog = new DeleteDeviceDialog(PersonalCenterActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(PersonalCenterActivity.this, LoginActivity.class));
            }
        });
        mLogoutDialog=new ConfirmDialog(this);
        manager = DeplinkSDK.getSDKManager();
        ec = new EventCallback() {

            @Override
            public void onSuccess(SDKAction action) {
                switch (action){
                    case LOGOUT:

                        Perfence.setPerfence(AppConstant.USER_LOGIN,false);
                        startActivity(new Intent(PersonalCenterActivity.this, LoginActivity.class));
                        break;
                    case APPUPDATE:
                            String version = manager.getAppUpdateInfo().getVersion();
                            int oldVersion=Integer.valueOf(APKVersionCodeUtils.getVerName(PersonalCenterActivity.this).replace(".",""));
                            int newVersion=Integer.valueOf(version.replace(".",""));
                            if (!APKVersionCodeUtils.getVerName(PersonalCenterActivity.this).equals(version) && (newVersion>oldVersion)) {
                                isAppUpdate = true;
                                textview_update_now.setText("立即升级");
                            } else {
                                textview_update_now.setText("已是最新版本");
                                isAppUpdate = false;
                            }
                            if(isClickUpdate){
                                isClickUpdate=false;
                                if(isAppUpdate){
                                    startActivity(new Intent(PersonalCenterActivity.this, UpdateImmediateActivity.class));
                                }else{
                                    ToastSingleShow.showText(PersonalCenterActivity.this,"已是最新版本");
                                }
                            }
                        break;
                }
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {


            }

            @Override
            public void onGetImageSuccess(SDKAction action, final Bitmap bm) {
                //保存到本地
                try {
                    Log.i(TAG,"onGetImageSuccess");
                    user_head_portrait.setImageBitmap(bm);
                    saveToSDCard(bm);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onGetUserInfouccess(String info) {
                super.onGetUserInfouccess(info);
                Gson gson = new Gson();
                if(!info.equalsIgnoreCase("[]")){
                    UserInfoAlertBody responseInfo = gson.fromJson(info, UserInfoAlertBody.class);
                    user_nickname.setText(responseInfo.getNickname());
                }
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
                switch (action){
                    case LOGOUT:
                        Log.i(TAG, "退出登录失败");
                        ToastSingleShow.showText(PersonalCenterActivity.this, "退出登录失败，请检查网络连接");
                        break;
                }

            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setContentText("当前账号已在其它设备上登录,是否重新登录");
            }
        };
    }
    private void saveToSDCard(Bitmap bitmap) {
        String path = this.getFilesDir().getAbsolutePath();
        path = path + File.separator + "userIcon" + "userIcon.png";
        File dest = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initEvents() {
        AppManager.getAppManager().addActivity(this);
        layout_getway_check.setOnClickListener(this);
        layout_experience_center.setOnClickListener(this);
        layout_home_page.setOnClickListener(this);
        layout_devices.setOnClickListener(this);
        layout_rooms.setOnClickListener(this);
        layout_personal_center.setOnClickListener(this);
        button_logout.setOnClickListener(this);
        user_head_portrait.setOnClickListener(this);
        layout_update_soft.setOnClickListener(this);
    }

    private void initViews() {

        textview_home = findViewById(R.id.textview_home);
        textview_device = findViewById(R.id.textview_device);
        textview_room = findViewById(R.id.textview_room);
        textview_mine = findViewById(R.id.textview_mine);
        imageview_devices = findViewById(R.id.imageview_devices);
        imageview_home_page = findViewById(R.id.imageview_home_page);
        imageview_rooms = findViewById(R.id.imageview_rooms);
        imageview_personal_center = findViewById(R.id.imageview_personal_center);
        layout_getway_check = findViewById(R.id.layout_getway_check);
        layout_experience_center = findViewById(R.id.layout_experience_center);
        layout_home_page = findViewById(R.id.layout_home_page);
        layout_devices = findViewById(R.id.layout_devices);
        layout_rooms = findViewById(R.id.layout_rooms);
        layout_personal_center = findViewById(R.id.layout_personal_center);
        button_logout = findViewById(R.id.button_logout);
        user_head_portrait = findViewById(R.id.user_head_portrait);
        user_nickname = findViewById(R.id.user_nickname);
        textview_update_now = findViewById(R.id.textview_update_now);
        layout_update_soft = findViewById(R.id.layout_update_soft);
        textview_current_version = findViewById(R.id.textview_current_version);
    }

    /**
     * 再按一次退出应用
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().finishAllActivity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.removeEventCallback(ec);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_getway_check:
                startActivity(new Intent(PersonalCenterActivity.this, HomeNetWorkActivity.class));
                break;
            case R.id.layout_experience_center:
                DeviceManager.getInstance().setExperCenterStartFromHomePage(false);
                startActivity(new Intent(this, ExperienceDevicesActivity.class));
                break;
            case R.id.layout_home_page:
                startActivity(new Intent(this, SmartHomeMainActivity.class));
                break;
            case R.id.layout_devices:
                startActivity(new Intent(this, DevicesActivity.class));
                break;
            case R.id.layout_rooms:
                startActivity(new Intent(this, RoomActivity.class));
                break;
            case R.id.button_logout:
                if(isUserLogin){
                    mLogoutDialog.setSureBtnClickListener(new ConfirmDialog.onSureBtnClickListener() {
                        @Override
                        public void onSureBtnClicked() {
                            manager.logout();

                        }
                    });
                    mLogoutDialog.show();
                    mLogoutDialog.setDialogTitleText("退出登录");
                    mLogoutDialog.setDialogMsgText("确定退出登录?");
                }else{
                    startActivity(new Intent(PersonalCenterActivity.this, LoginActivity.class));
                }

                break;
            case R.id.user_head_portrait:
                startActivity(new Intent(this, UserinfoActivity.class));
                break;
            case R.id.layout_update_soft:
                manager.queryAppUpdateInfo(Perfence.SDK_APP_KEY, APKVersionCodeUtils.getVerName(this));
                isClickUpdate=true;
                break;

        }
    }
}
