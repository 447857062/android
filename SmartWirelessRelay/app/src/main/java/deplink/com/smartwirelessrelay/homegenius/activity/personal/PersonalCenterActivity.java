package deplink.com.smartwirelessrelay.homegenius.activity.personal;

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

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import java.io.File;
import java.io.FileOutputStream;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.activity.device.DevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.homepage.SmartHomeMainActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.experienceCenter.ExperienceDevicesActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.login.LoginActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.personal.usrinfo.UserinfoActivity;
import deplink.com.smartwirelessrelay.homegenius.activity.room.RoomActivity;
import deplink.com.smartwirelessrelay.homegenius.application.AppManager;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.imageview.CircleImageView;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;

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
    private MakeSureDialog connectLostDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        initViews();
        initDatas();
        initEvents();

    }
    private boolean isUserLogin;
    private boolean hasGetUserImage;
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
        connectLostDialog = new MakeSureDialog(PersonalCenterActivity.this);
        connectLostDialog.setSureBtnClickListener(new MakeSureDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(PersonalCenterActivity.this, LoginActivity.class));
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
            public void onFailure(SDKAction action, Throwable throwable) {

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
                //TODO 退出登录
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.user_head_portrait:
                startActivity(new Intent(this, UserinfoActivity.class));
                break;

        }
    }
}
