package com.deplink.homegenius.activity.device;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.deplink.homegenius.Protocol.json.device.share.UserShareInfo;
import com.deplink.homegenius.activity.device.adapter.ShareDeviceListAdapter;
import com.deplink.homegenius.activity.personal.login.LoginActivity;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.device.DeviceListener;
import com.deplink.homegenius.manager.device.DeviceManager;
import com.deplink.homegenius.util.ParseUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.util.StringValidatorUtil;
import com.deplink.homegenius.util.WeakRefHandler;
import com.deplink.homegenius.view.dialog.DeleteDeviceDialog;
import com.deplink.homegenius.view.dialog.DialogWithInput;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.ShareDeviceBody;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;

public class ShareDeviceActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ShareDeviceActivity";
    private TextView textview_title;
    private TextView textview_edit;
    private ListView listview_share_user;
    private DeviceListener mDeviceListener;
    private DeviceManager mDeviceManager;
    private boolean isStartFromExperience;
    private boolean isUserLogin;
    private SDKManager manager;
    private EventCallback ec;
    private DeleteDeviceDialog connectLostDialog;
    private String deviceuid;
    private ShareDeviceListAdapter mAdapter;
    private List<UserShareInfo> userInfos;
    private FrameLayout image_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_device);
        initViews();
        initDatas();
        initEvents();
    }
    private void initEvents() {
        textview_edit.setOnClickListener(this);
        image_back.setOnClickListener(this);
        listview_share_user.setAdapter(mAdapter);
        listview_share_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                final DeleteDeviceDialog dialog = new DeleteDeviceDialog(ShareDeviceActivity.this);
                dialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked() {
                        if (userInfos.get(position).getStatus() == 2) {
                            //该用户绑定
                            action = "setmanager";
                            if (!isStartFromExperience) {
                                if (isUserLogin) {
                                    ShareDeviceBody body = new ShareDeviceBody();
                                    body.setUser_name(userInfos.get(position).getUsername());
                                    body.setAssuper(1);
                                    mDeviceManager.shareDevice(deviceuid, body);
                                } else {
                                    ToastSingleShow.showText(ShareDeviceActivity.this, "未登录,登录后操作");
                                }
                            }
                        } else if (userInfos.get(position).getStatus() == 1) {
                            if (!isStartFromExperience) {
                                if (isUserLogin) {
                                    mDeviceManager.cancelDeviceShare(deviceuid, userInfos.get(position).getShareid());
                                } else {
                                    ToastSingleShow.showText(ShareDeviceActivity.this, "未登录,登录后操作");
                                }
                            }
                        }
                    }
                });
                String selfusername = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
                boolean selfIsManager = false;
                for (int i = 0; i < userInfos.size(); i++) {
                    if (userInfos.get(i).getUsername().equalsIgnoreCase(selfusername) && userInfos.get(i).getIssuper() == 0) {
                        //自己不是管理员
                        selfIsManager = false;
                    } else if (userInfos.get(i).getUsername().equalsIgnoreCase(selfusername) && userInfos.get(i).getIssuper() == 1) {
                        selfIsManager = true;
                    }
                }
                if (selfIsManager) {
                    if (userInfos.get(position).getIssuper() == 1
                            ) {
                    } else {
                        if (userInfos.get(position).getStatus() == 2) {
                            //该用户绑定
                            dialog.show();
                            action = "setmanager";
                            dialog.setTitleText("设备管理");
                            dialog.setContentText("确定将" + userInfos.get(position).getUsername() + "设为管理员");

                        } else if (userInfos.get(position).getStatus() == 1) {
                            dialog.show();
                            dialog.setTitleText("删除用户");
                            dialog.setContentText("确定删除" + userInfos.get(position).getUsername());
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStartFromExperience = mDeviceManager.isStartFromExperience();
        isUserLogin = Perfence.getBooleanPerfence(AppConstant.USER_LOGIN);
        mDeviceManager.addDeviceListener(mDeviceListener);
        manager.addEventCallback(ec);
        if (!isStartFromExperience) {
            if (isUserLogin) {
                mDeviceManager.readDeviceShareInfo(deviceuid);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDeviceManager.removeDeviceListener(mDeviceListener);
        manager.removeEventCallback(ec);
        userInfos.clear();
    }
    private void initDatas() {
        textview_title.setText("设备分享");
        textview_edit.setText("添加");
        mDeviceManager = DeviceManager.getInstance();
        mDeviceManager.InitDeviceManager(this);
        mDeviceListener = new DeviceListener() {
            @Override
            public void responseGetDeviceShareInfo(String result) {
                super.responseGetDeviceShareInfo(result);
                if (result.contains("errcode")) {

                } else {
                    List<UserShareInfo> response = ParseUtil.jsonToArrayList(result, UserShareInfo.class);
                    if (response != null) {
                        userInfos.clear();
                        Log.i(TAG, "分享的用户列表长度:" + response.size());
                        for (int i = 0; i < response.size(); i++) {
                            String username = response.get(i).getUsername();
                            manager.getImage(username);
                        }
                        Collections.sort(response, new Comparator<UserShareInfo>() {
                            @Override
                            public int compare(UserShareInfo o1, UserShareInfo o2) {
                                //compareTo就是比较两个值，如果前者大于后者，返回1，等于返回0，小于返回-1
                                //重小到大排序的相反
                                String selfusername = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
                                if (o1.getUsername().equalsIgnoreCase(selfusername) &&
                                        o2.getUsername().equalsIgnoreCase(selfusername)) {
                                    return 0;
                                }
                                if (o1.getUsername().equalsIgnoreCase(selfusername) &&
                                        !o2.getUsername().equalsIgnoreCase(selfusername)) {
                                    return -1;
                                }
                                if (!o1.getUsername().equalsIgnoreCase(selfusername) &&
                                        o2.getUsername().equalsIgnoreCase(selfusername)) {
                                    return 1;
                                }
                                if (o1.getIssuper() == o2.getIssuper()) {
                                    if (o1.getStatus() == o2.getStatus()) {
                                        return 0;
                                    }
                                    if (o1.getStatus() < o2.getStatus()) {
                                        return 1;
                                    }
                                    if (o1.getStatus() > o2.getStatus()) {
                                        return -1;
                                    }
                                }
                                if (o1.getIssuper() < o2.getIssuper()) {
                                    return 1;
                                }
                                if (o1.getIssuper() > o2.getIssuper()) {
                                    return -1;
                                }
                                return 0;
                            }
                        });
                        userInfos.addAll(response);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void responseDeviceShareResult(DeviceOperationResponse result) {
                super.responseDeviceShareResult(result);
                Message msg = Message.obtain();
                msg.what = MSG_SHOW_SHARE_OPTION_RESULT;
                msg.obj = result;
                mHandler.sendMessage(msg);

            }

            @Override
            public void responseCancelDeviceShare(DeviceOperationResponse result) {
                super.responseCancelDeviceShare(result);
                Message msg = Message.obtain();
                msg.what = MSG_SHOW_CANCEL_SHARE_RESULT;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        initMqtt();
        if (getIntent().getStringExtra("deviceuid") != null) {
            deviceuid = getIntent().getStringExtra("deviceuid");
        }
        dialogWithInput = new DialogWithInput(this);
        userInfos = new ArrayList<>();
        userImage = new HashMap<>();
        mAdapter = new ShareDeviceListAdapter(this, userInfos, userImage);
    }

    private static final int MSG_SHOW_SHARE_OPTION_RESULT = 100;
    private static final int MSG_SHOW_CANCEL_SHARE_RESULT = 101;
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            DeviceOperationResponse result = (DeviceOperationResponse) msg.obj;
            switch (msg.what) {
                case MSG_SHOW_SHARE_OPTION_RESULT:
                    switch (action) {
                        case "share":
                            if (result.getStatus().equalsIgnoreCase("ok")) {
                                ToastSingleShow.showText(ShareDeviceActivity.this, "分享设备成功");
                                mDeviceManager.readDeviceShareInfo(deviceuid);
                            } else {
                                ToastSingleShow.showText(ShareDeviceActivity.this, "分享设备失败");
                            }
                            break;
                        case "setmanager":
                            if (result.getStatus().equalsIgnoreCase("ok")) {
                                mDeviceManager.readDeviceShareInfo(deviceuid);
                                ToastSingleShow.showText(ShareDeviceActivity.this, "设置管理员成功");
                            } else {
                                ToastSingleShow.showText(ShareDeviceActivity.this, "设置管理员失败");
                            }
                            break;
                    }
                    break;
                case MSG_SHOW_CANCEL_SHARE_RESULT:
                    if (result.getStatus().equalsIgnoreCase("ok")) {
                        mDeviceManager.readDeviceShareInfo(deviceuid);
                        ToastSingleShow.showText(ShareDeviceActivity.this, "取消分享成功");
                    } else {
                        ToastSingleShow.showText(ShareDeviceActivity.this, "取消分享失败");
                    }
                    break;
            }
            return true;
        }
    };
    private Handler mHandler = new WeakRefHandler(mCallback);
    private HashMap<String, Bitmap> userImage;
    private String action;

    private void initMqtt() {
        DeplinkSDK.initSDK(getApplicationContext(), Perfence.SDK_APP_KEY);
        manager = DeplinkSDK.getSDKManager();
        connectLostDialog = new DeleteDeviceDialog(ShareDeviceActivity.this);
        connectLostDialog.setSureBtnClickListener(new DeleteDeviceDialog.onSureBtnClickListener() {
            @Override
            public void onSureBtnClicked() {
                startActivity(new Intent(ShareDeviceActivity.this, LoginActivity.class));
            }
        });
        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
            }

            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {

            }

            @Override
            public void deviceOpSuccess(String op, final String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
            }

            @Override
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {
                userImage.clear();
                userImage.putAll(manager.getUserImage());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                isUserLogin = false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setContentText("当前账号已在其它设备上登录,是否重新登录");
            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
            }
        };
    }

    private void initViews() {
        textview_title = findViewById(R.id.textview_title);
        textview_edit = findViewById(R.id.textview_edit);
        listview_share_user = findViewById(R.id.listview_share_user);
        image_back = findViewById(R.id.image_back);
    }

    private DialogWithInput dialogWithInput;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textview_edit:
                dialogWithInput.setSureBtnClickListener(new DialogWithInput.onSureBtnClickListener() {
                    @Override
                    public void onSureBtnClicked(String password) {
                        action = "share";
                        ShareDeviceBody body = new ShareDeviceBody();
                        if (StringValidatorUtil.isMobileNO(password)) {
                            body.setUser_name(password);
                            body.setAssuper(0);
                            if (!isStartFromExperience) {
                                if (isUserLogin) {
                                    mDeviceManager.shareDevice(deviceuid, body);
                                } else {
                                    ToastSingleShow.showText(ShareDeviceActivity.this, "未登录,登录后操作");
                                }
                            }
                            dialogWithInput.dismiss();
                        } else {
                            ToastSingleShow.showText(ShareDeviceActivity.this, "请输入想要分享该设备的用户名");
                        }
                    }
                });
                String selfusername = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
                boolean selfIsManager = false;
                for (int i = 0; i < userInfos.size(); i++) {
                    if (userInfos.get(i).getUsername().equalsIgnoreCase(selfusername) && userInfos.get(i).getIssuper() == 0) {
                        //自己不是管理员
                        selfIsManager = false;
                    } else if (userInfos.get(i).getUsername().equalsIgnoreCase(selfusername) && userInfos.get(i).getIssuper() == 1) {
                        selfIsManager = true;
                    }
                }
                if(selfIsManager){
                    dialogWithInput.show();
                }else{
                    ToastSingleShow.showText(ShareDeviceActivity.this,"自己不是管理员,无法分享此设备");
                }

                break;
            case R.id.image_back:
                onBackPressed();
                break;
        }
    }
}
