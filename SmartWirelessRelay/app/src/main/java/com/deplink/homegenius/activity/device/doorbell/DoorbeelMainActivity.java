package com.deplink.homegenius.activity.device.doorbell;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.lock.UserIdInfo;
import com.deplink.homegenius.Protocol.json.device.lock.UserIdPairs;
import com.deplink.homegenius.broadcast.PushMessage;
import com.deplink.homegenius.constant.SmartLockConstant;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.manager.device.doorbeel.DoorBellListener;
import com.deplink.homegenius.manager.device.doorbeel.DoorbeelManager;
import com.deplink.homegenius.manager.device.smartlock.SmartLockListener;
import com.deplink.homegenius.manager.device.smartlock.SmartLockManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.dialog.doorbeel.Doorbeel_menu_Dialog;
import com.deplink.homegenius.view.toast.ToastSingleShow;

import org.litepal.crud.DataSupport;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DoorbeelMainActivity extends Activity implements View.OnClickListener, SmartLockListener {
    private static final String TAG = "DoorbeelMainActivity";
    private FrameLayout image_back;
    private ImageView image_setting;
    private TextView textview_title;
    private FrameLayout frame_setting;
    private ImageView image_snap;
    private DoorbeelManager mDoorbeelManager;
    private DoorBellListener mDoorBellListener;
    private Button button_opendoor;
    private SmartLockManager mSmartLockManager;
    private ImageView imageview_visitor;
    private RelativeLayout layout_no_vistor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorbeel_main);
        initViews();
        initEvents();
        initDatas();
    }

    private void initEvents() {
        image_back.setOnClickListener(this);
        image_setting.setOnClickListener(this);
        frame_setting.setOnClickListener(this);
        button_opendoor.setOnClickListener(this);
    }

    private String filename;

    private void initDatas() {
        textview_title.setText("智能门铃");
        mDoorbeelManager = DoorbeelManager.getInstance();
        mDoorbeelManager.InitDoorbeelManager(this);
        mSmartLockManager = SmartLockManager.getInstance();
        mSmartLockManager.InitSmartLockManager(this);
        image_setting.setImageResource(R.drawable.menuicon);
        doorbeelMenuDialog = new Doorbeel_menu_Dialog(this);
        filename = getIntent().getStringExtra("file");
        mDoorBellListener = new DoorBellListener() {
            public void responseVisitorImage(final Bitmap bitmap, int count) {
                super.responseVisitorImage(bitmap, count);

            }
        };
        Bundle bundle = getIntent().getBundleExtra("message");
        if (bundle != null) {
            PushMessage mesage = bundle.getParcelable("message");
            filename = mesage.getFile();
            Log.i(TAG, "filename=" + filename);
            if (filename != null && !filename.equalsIgnoreCase("")) {
                String username = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
                String uid = mDoorbeelManager.getCurrentSelectedDoorbeel().getUid();
                mDoorbeelManager.getDoorbellVistorImage(filename, 0);
                imageview_visitor.setVisibility(View.VISIBLE);
                layout_no_vistor.setVisibility(View.GONE);
                String url = "https://api.deplink.net/user/" + username + "/doorbell/" + uid + "/snapshot/" + filename;
                Glide.with(DoorbeelMainActivity.this)
                        .load(url)
                        .bitmapTransform(new RoundedCornersTransformation(this, 8, 0, RoundedCornersTransformation.CornerType.ALL))
                        .crossFade(1000)
                        .into(imageview_visitor);
                //  imageview_visitor.setImageBitmap(imageview_visitor.getRoundBitmap(bitmap, 8));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDoorbeelManager.addDeviceListener(mDoorBellListener);

        isStartFromExperience = DeviceManager.getInstance().isStartFromExperience();
        if (!isStartFromExperience) {
            if (mDoorbeelManager.getCurrentSelectedDoorbeel().getBindLockUid() != null) {
                mSmartLockManager.queryLockUidHttp(mDoorbeelManager.getCurrentSelectedDoorbeel().getBindLockUid());
                String lockuid = mDoorbeelManager.getCurrentSelectedDoorbeel().getBindLockUid();
                lockDevice = DataSupport.where("Uid=?", lockuid).findFirst(SmartDev.class, true);
            }
            if (lockDevice != null && lockDevice.getStatus().equalsIgnoreCase("在线")) {
                button_opendoor.setBackgroundResource(R.drawable.radius22_bg_button_background);
            } else {
                button_opendoor.setBackgroundResource(R.drawable.radius22_bg_6c_background);
            }
        } else {
            button_opendoor.setBackgroundResource(R.drawable.radius22_bg_button_background);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageview_visitor.setVisibility(View.GONE);
        layout_no_vistor.setVisibility(View.VISIBLE);
        mDoorbeelManager.removeDeviceListener(mDoorBellListener);
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        image_back = findViewById(R.id.image_back);
        image_setting = findViewById(R.id.image_setting);
        frame_setting = findViewById(R.id.frame_setting);
        image_snap = findViewById(R.id.image_snap);
        button_opendoor = findViewById(R.id.button_opendoor);
        layout_no_vistor = findViewById(R.id.layout_no_vistor);
        imageview_visitor = findViewById(R.id.imageview_visitor);
    }

    private Doorbeel_menu_Dialog doorbeelMenuDialog;
    private boolean isStartFromExperience;
    private String savedManagePassword;
    private String selfUserId;
    private SmartDev lockDevice;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_setting:
                doorbeelMenuDialog.show();
                break;
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.button_opendoor:
                if (isStartFromExperience) {
                    ToastSingleShow.showText(this, "门锁已开");
                } else {
                    if (lockDevice != null && selfUserId != null) {
                        savedManagePassword = mSmartLockManager.getCurrentSelectLock().getLockPassword();
                        mSmartLockManager.setSmartLockParmars(SmartLockConstant.OPEN_LOCK, selfUserId, savedManagePassword, null, null);
                    }
                }
                break;

        }
    }

    @Override
    public void responseQueryResult(String result) {

    }

    @Override
    public void responseSetResult(String result) {

    }

    @Override
    public void responseBind(String result) {

    }

    @Override
    public void responseLockStatu(int RecondNum, int LockStatus) {

    }

    @Override
    public void responseUserIdInfo(UserIdInfo userIdInfo) {
        List<UserIdPairs> mUserIdPairs = userIdInfo.getAlluser();
        for (int i = 0; i < mUserIdPairs.size(); i++) {
            UserIdPairs tempUserIdPair = DataSupport.where("userid = ?", mUserIdPairs.get(i).getUserid()).findFirst(UserIdPairs.class);
            if (tempUserIdPair != null) {
                tempUserIdPair.setUsername(userIdInfo.getAlluser().get(i).getUsername());
                tempUserIdPair.saveFast();
            } else {
                UserIdPairs addUserIdPair = new UserIdPairs();
                addUserIdPair.setUserid(userIdInfo.getAlluser().get(i).getUserid());
                addUserIdPair.setUsername(userIdInfo.getAlluser().get(i).getUsername());
                addUserIdPair.saveFast();
            }
        }
        selfUserId = userIdInfo.getSelfid();
    }
}
