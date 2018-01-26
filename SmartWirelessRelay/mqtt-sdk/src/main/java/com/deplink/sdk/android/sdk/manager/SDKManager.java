package com.deplink.sdk.android.sdk.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.MqttConfig;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.bean.User;
import com.deplink.sdk.android.sdk.bean.UserSession;
import com.deplink.sdk.android.sdk.device.router.BaseDevice;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.UserInfoAlertBody;
import com.deplink.sdk.android.sdk.interfaces.SDKCoordinator;
import com.deplink.sdk.android.sdk.json.AppUpdateResponse;
import com.deplink.sdk.android.sdk.mqtt.MQTTController;
import com.deplink.sdk.android.sdk.rest.RestfulTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huqs on 2016/7/4.
 */
public class SDKManager {
    private static final String TAG = "SDKManager";
    private DeviceManager mDeviceManager = null;
    private SDKCoordinator mSDKCoordinator = null;
    private List<EventCallback> eventCallbackList = new ArrayList<>();
    private UserManager mUserManager;
    public SDKManager() {
        mSDKCoordinator = new Coordinator();
        mUserManager = new UserManager(mSDKCoordinator);
        mDeviceManager = new DeviceManager(mSDKCoordinator);
    }
    /**
     * 添加回调事件
     *
     * @param callback
     */
    public void addEventCallback(EventCallback callback) {
        //用户添加的回调对象不能为空并且不能重复添加
        if (callback != null && !eventCallbackList.contains(callback)) {
            eventCallbackList.add(callback);
        }
    }

    /**
     * 移除回调事件
     *
     * @param callback
     */
    public void removeEventCallback(EventCallback callback) {
        if (callback != null) {
            eventCallbackList.remove(callback);
        }
    }

    /**
     * 用户注册
     *
     * @param username
     * @param password
     * @param verifyCode
     */
    public void register(String username, String password, String verifyCode) {
        mUserManager.userRegister(username, password, verifyCode);
    }

    /**
     * 用户登录
     *
     * @param username
     * @param password
     */
    public void login(String username, String password) {
        mUserManager.login(username, password);
    }
    /**
     * 用户登录后修改密码
     *
     * @param password
     * @param passwordNew
     */
    public void loginedAlertPassword(String password, String passwordNew) {
        mUserManager.loginedAlertPassword(password, passwordNew);
    }

    /**
     * 上传头像
     *
     * @param imagePath
     */
    public void uploadImage(String imagePath) {
        mUserManager.uploadImage(imagePath);
    }

    /**
     * 获取头像
     */
    public void getImage(String username) {
        mUserManager.getImage(username);
    }

    public String getBindedDeviceName() {
        if (mUserManager != null) {
            return mUserManager.getBindedDeviceName();
        }
        return "";
    }

    /**
     * 获取APP升级信息
     */
    public void queryAppUpdateInfo(String appKey, String version) {
        mUserManager.getAppUpdateInfo(appKey, version);
    }

    /**
     * 用户注销
     */
    public void logout() {
        mUserManager.logout();
    }

    /**
     * 当前登录的用户信息
     *
     * @return
     */
    public User getUserInfo() {
        return mSDKCoordinator.getUserInfo();
    }

    /**
     * 用户密码重置
     *
     * @param username
     * @param password
     * @param verifyCode
     */
    public void resetPassword(String username, String password, String verifyCode) {
        mUserManager.userResetPassword(username, password, verifyCode);
    }



    /**
     * 绑定设备
     *
     * @param deviceSn
     */
    public void bindDevice(String deviceSn) {
        mUserManager.bindDevice(deviceSn);
    }

    /**
     * 解除设备绑定
     *
     * @param device
     */
    public void unbindDevice(BaseDevice device) {
        mUserManager.unbindDevice(device);
    }

    /**
     * 获取绑定的设备列表
     *
     * @return
     */
    public List<BaseDevice> getDeviceList() {
        if (mDeviceManager != null) {
            return mDeviceManager.getDeviceList();
        }
        return null;
    }
    public DeviceManager getmDeviceManager() {
        if (mDeviceManager != null) {
            return mDeviceManager;
        }
        return null;
    }
    /**
     * 获取App升级信息
     *
     * @return
     */
    public AppUpdateResponse getAppUpdateInfo() {
        if (mUserManager != null) {
            return mUserManager.getAppUpdateInfo();
        }
        return null;
    }

    /**
     * 获取绑定的单个设备
     *
     * @param deviceKey
     * @return
     */
    public BaseDevice getDevice(String deviceKey) {
        if (mDeviceManager != null) {
            return mDeviceManager.getDevice(deviceKey);
        }
        return null;
    }

    /**
     * 连接MQTT server
     *
     * @param context
     * @return
     */
    public boolean connectMQTT(Context context) {
        UserSession session = mUserManager.getUserSession();
        if (null == session) {
            Log.i(TAG,"connectMQTT (null == session");
            return false;
        }
        MqttConfig config = new MqttConfig();
        config.setServers(session.getServers());
        config.setClientid(session.getClientid());
        config.setUsername(session.getUsername());
        config.setPassword(session.getPassword());
        MQTTController.getSingleton().init(context, config, mSDKCoordinator);
        MQTTController.getSingleton().connect();
        return true;
    }

    /**
     * 清理
     */
    public void onDestroy() {
        Log.d(DeplinkSDK.SDK_TAG, "--->SDKManager.onDestroy");
        MQTTController.getSingleton().onDestroy();
    }

    public HashMap<String, Bitmap> getUserImage() {
        if (mUserManager != null) {
            return mUserManager.getUserImages();
        }
        return null;
    }
    public void getUserInfo(String username) {
        if (mUserManager != null) {
             mUserManager.getUserInfo(username );
        }
    }
    public void alertUserInfo(String username, UserInfoAlertBody body) {
        if (mUserManager != null) {
             mUserManager.alertUserInfo(username,body );
        }
    }

    private class Coordinator implements SDKCoordinator {
        @Override
        public void afterLogin() {
          //  mDeviceManager.getDeviceBinding();
        }

        @Override
        public void afterLogout() {
            MQTTController.getSingleton().onDestroy();
            mDeviceManager.cleanup();
        }

        @Override
        public void afterDeviceBinding() {
          //  mDeviceManager.getDeviceBinding();
        }

        @Override
        public void afterDeviceUnbinding() {
          //  mDeviceManager.getDeviceBinding();
        }

        @Override
        public UserSession getUserSession() {
            if (mUserManager != null) {
                return mUserManager.getUserSession();
            }
            return null;
        }

        @Override
        public User getUserInfo() {
            if (mUserManager != null) {
                return mUserManager.getUserInfo();
            }
            return null;
        }


        @Override
        public void MQTTConnectionLost(Throwable cause) {
            RestfulTools.getSingleton().session(new Callback<UserSession>() {
                @Override
                public void onResponse(Call<UserSession> call, Response<UserSession> response) {
                    if (response.code() == 403) {
                        String error = "当前账号已在其它设备上登录";
                        for (EventCallback callback : eventCallbackList) {
                            if (callback == null) continue;
                            callback.connectionLost(new Throwable(error));
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserSession> call, Throwable t) {

                }
            });
        }

        @Override
        public void MQTTReconnectionFailed() {
        }

        @Override
        public void MQTTConnected() {
            if (mDeviceManager != null) {
                mDeviceManager.onMQTTConnection();
            }
            if (mUserManager != null) {
                mUserManager.onMQTTConnection();
            }
        }

        @Override
        public void notifySuccess(SDKAction action) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;
                callback.onSuccess(action);
            }
        }

        @Override
        public void homeGeniusGetUserInfo(String info) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;
                callback.onGetUserInfouccess(info);
            }
        }

        @Override
        public void alertUserInfo(DeviceOperationResponse info) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;
                callback.alertUserInfo(info);
            }
        }

        @Override
        public void notifyGetImageSuccess(SDKAction action, Bitmap bm) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;
                callback.onGetImageSuccess(action, bm);
            }
        }

        @Override
        public void notifyBindSuccess(SDKAction action, String deviceKey) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;

                callback.onBindSuccess(action, deviceKey);
            }
        }

        @Override
        public void notifyFailure(SDKAction action, String errMsg) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;
                callback.onFailure(action, new Throwable(errMsg));
            }
        }
        @Override
        public void notifyDeviceDataUpdate(String deviceKey, int msgType) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;
                Log.i(TAG, "notifyDeviceDataUpdate msgType=" + msgType);
                callback.notifyDeviceDataChanged(deviceKey, msgType);
            }
        }

        @Override
        public void notifyDeviceUpgrade(String deviceKey) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;
                callback.notifyDeviceUpgrade(deviceKey);
            }
        }

        @Override
        public void notifyDeviceOpSuccess(String op, String deviceKey) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;
                callback.deviceOpSuccess(op, deviceKey);
            }
        }

        @Override
        public void notifyDeviceOpFailure(String op, String deviceKey, Throwable exception) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;
                callback.deviceOpFailure(op, deviceKey, exception);
            }
        }

        @Override
        public void notifyHomeGeniusResult(String result) {
            for (EventCallback callback : eventCallbackList) {
                if (callback == null) continue;
                callback.notifyHomeGeniusResponse(result);
            }
        }

    }
}
