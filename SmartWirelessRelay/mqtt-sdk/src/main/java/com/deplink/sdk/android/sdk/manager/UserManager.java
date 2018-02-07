package com.deplink.sdk.android.sdk.manager;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.MqttAction;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.bean.BindingInfo;
import com.deplink.sdk.android.sdk.bean.DeviceInfo;
import com.deplink.sdk.android.sdk.bean.DeviceRoot;
import com.deplink.sdk.android.sdk.bean.User;
import com.deplink.sdk.android.sdk.bean.UserSession;
import com.deplink.sdk.android.sdk.device.router.BaseDevice;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.UserInfoAlertBody;
import com.deplink.sdk.android.sdk.interfaces.MqttListener;
import com.deplink.sdk.android.sdk.interfaces.SDKCoordinator;
import com.deplink.sdk.android.sdk.json.AppUpdateResponse;
import com.deplink.sdk.android.sdk.json.ErrorBody;
import com.deplink.sdk.android.sdk.rest.RestfulTools;
import com.deplink.sdk.android.sdk.rest.RestfulToolsHomeGenius;
import com.deplink.sdk.android.sdk.rest.RestfulToolsHomeGeniusString;
import com.deplink.sdk.android.sdk.rest.RestfulToolsPng;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huqs on 2016/7/12.
 */
public class UserManager implements MqttListener {
    private static final String TAG = "UserManager";
    private User mUserInfo = null;
    private UserSession mUserSession = null;
    private SDKCoordinator mSDKCoordinator = null;

    public UserManager(SDKCoordinator coordinator) {
        mSDKCoordinator = coordinator;
        userImages = new HashMap<>();
    }

    public UserSession getUserSession() {
        return mUserSession;
    }

    public User getUserInfo() {
        return mUserInfo;
    }

    /**
     * MQTT连接建立
     */
    public void onMQTTConnection() {
    }

    /**
     * 用户注册
     *
     * @param username
     * @param password
     * @param verifyCode
     */
    public void userRegister(String username, String password, String verifyCode) {
        final User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setApplication_key(DeplinkSDK.getAppKey());
        user.setVerifyCode(verifyCode);

        RestfulTools.getSingleton().register(user, new Callback<UserSession>() {
            @Override
            public void onResponse(Call<UserSession> call, Response<UserSession> response) {
                switch (response.code()) {
                    case 200:
                        mSDKCoordinator.notifySuccess(SDKAction.REGISTER);
                        break;
                    case 409:
                        String error = "当前账号已存在";
                        mSDKCoordinator.notifyFailure(SDKAction.REGISTER, error);
                        break;
                    default:
                        error = "注册失败";
                        mSDKCoordinator.notifyFailure(SDKAction.REGISTER, error);
                        break;
                }
            }

            @Override
            public void onFailure(Call<UserSession> call, Throwable t) {
                String error = "注册失败";
                mSDKCoordinator.notifyFailure(SDKAction.REGISTER, error);
            }
        });
    }

    /**
     * 用户重置密码
     *
     * @param username
     * @param password
     * @param verifyCode
     */
    public void userResetPassword(String username, String password, String verifyCode) {
        final User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setApplication_key(DeplinkSDK.getAppKey());
        user.setVerifyCode(verifyCode);

        RestfulTools.getSingleton().resetPassword(username, user, new Callback<UserSession>() {
            @Override
            public void onResponse(Call<UserSession> call, Response<UserSession> response) {
                switch (response.code()) {
                    case 200:
                        mSDKCoordinator.notifySuccess(SDKAction.RESET_PASSWORD);
                        break;
                    case 404:
                        String error = "账号不存在";
                        mSDKCoordinator.notifyFailure(SDKAction.REGISTER, error);
                        break;
                    default:
                        error = "重置密码失败";
                        mSDKCoordinator.notifyFailure(SDKAction.RESET_PASSWORD, error);
                        break;
                }
            }

            @Override
            public void onFailure(Call<UserSession> call, Throwable t) {
                String error = "重置密码失败";
                mSDKCoordinator.notifyFailure(SDKAction.RESET_PASSWORD, error);
            }
        });
    }

    /**
     * 用户登录
     *
     * @param username
     * @param password
     */
    public void login(String username, String password) {
        mUserInfo = new User();
        mUserInfo.setName(username);
        mUserInfo.setPassword(password);
        mUserInfo.setApplication_key(DeplinkSDK.getAppKey());
        RestfulTools.getSingleton().login(username, password, new Callback<UserSession>() {
            @Override
            public void onResponse(Call<UserSession> call, Response<UserSession> response) {
                int code;
                code = response.code();
                Log.i(TAG, "登录  code=" + code);
                String error = "";
                if (code == 200) { //登录成功
                    mUserSession = response.body();
                    Log.i(TAG,"mUserSession:"+mUserSession.toString());
                    mUserInfo.setAvatar(mUserSession.getAvatar());
                    mUserInfo.setUuid(mUserSession.getUuid());
                    RestfulTools.getSingleton().setUsername(mUserInfo.getName());
                    RestfulTools.getSingleton().setToken(mUserSession.getToken());
                    mSDKCoordinator.notifySuccess(SDKAction.LOGIN);
                    mSDKCoordinator.afterLogin();
                    return;
                } else if (code == 403) {//登录失败，用户密码错误
                    error = "用户名或密码错误";
                } else if (code == 400) {//登录失败，请求服务器失败
                    error = "参数错误";

                } else if (code == 404) {//登录失败，请求服务器失败
                    error = "参数错误";
                } else if (code == 500) {//登录失败，请求服务器失败
                    error = "服务器内部错误";
                }
                mSDKCoordinator.notifyFailure(SDKAction.LOGIN, error);
                if (response.errorBody() != null) {
                    try {
                        Log.i(TAG, "登录参数错误" + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserSession> call, Throwable t) {
                String error = "无法访问网络";
                Log.i(TAG, "登录失败" + Arrays.toString(t.getStackTrace()));
                mSDKCoordinator.notifyFailure(SDKAction.LOGIN, error);
            }
        });
    }

    /**
     * 用户登录后修改密码
     *
     * @param username
     * @param password
     */
    public void loginedAlertPassword(String username, String password) {
        RestfulTools.getSingleton().loginedAlertPassword(username, password, new Callback<UserSession>() {
            @Override
            public void onResponse(Call<UserSession> call, Response<UserSession> response) {
                int code = 0;
                code = response.code();
                String error = "";
                Log.i(TAG, "code=" + code);
                if (code == 200) { //登录成功
                    mSDKCoordinator.notifySuccess(SDKAction.ALERTPASSWORD);
                    return;
                } else if (code == 403) {//登录失败，用户密码错误
                    error = "用户名或密码错误";
                } else if (code == 400) {//登录失败，请求服务器失败
                    error = "参数错误";
                } else if (code == 404) {//登录失败，请求服务器失败
                    error = "参数错误";
                } else if (code == 500) {//登录失败，请求服务器失败
                    error = "服务器内部错误";
                }
                if (response.errorBody() != null) {
                    try {
                        Log.i(TAG, "code=" + response.errorBody().string());
                        mSDKCoordinator.notifyFailure(SDKAction.ALERTPASSWORD, response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mSDKCoordinator.notifyFailure(SDKAction.ALERTPASSWORD, error);
                }

            }

            @Override
            public void onFailure(Call<UserSession> call, Throwable t) {
                String error = "无法访问网络";
                mSDKCoordinator.notifyFailure(SDKAction.ALERTPASSWORD, error);
            }
        });
    }

    private AppUpdateResponse AppUpdateInfo;

    public AppUpdateResponse getAppUpdateInfo() {
        return AppUpdateInfo;
    }

    /**
     * 获取app升级信息
     */
    public void getAppUpdateInfo(String appKey, String version) {
        Log.i(TAG, "getAppUpdateInfo 获取app升级信息");
        RestfulTools.getSingleton().getAppUpdateInfo(appKey, version, new Callback<AppUpdateResponse>() {
            @Override
            public void onResponse(Call<AppUpdateResponse> call, Response<AppUpdateResponse> response) {
                int code = response.code();
                Log.i(TAG, "code=" + code);
                if (code == 200) {
                    Log.i(TAG, response.body().toString());
                    AppUpdateInfo = response.body();
                    mSDKCoordinator.notifySuccess(SDKAction.APPUPDATE);
                } else if (code == 204) {
                    //没有新版本
                    mSDKCoordinator.notifyFailure(SDKAction.APPUPDATE, "没有升级信息");
                } else {
                    try {
                        Log.i(TAG, response.errorBody().string());
                        mSDKCoordinator.notifyFailure(SDKAction.APPUPDATE, response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AppUpdateResponse> call, Throwable t) {
                //  String error = "加载成员失败";
                //   notifyFailure(OP_GET_MEMBER, error);
                Log.i(TAG, "getAppUpdateInfo 获取app升级信息 error=" + t.getMessage());
                mSDKCoordinator.notifyFailure(SDKAction.APPUPDATE, t.toString());
            }
        });
    }

    /**
     * 用户上传头像
     *
     * @param imagePath
     */
    public void uploadImage(String imagePath) {
        Log.i(TAG, "uploadImage:" + imagePath);
        RestfulTools.getSingleton().uploadImage(imagePath, new Callback<UserSession>() {
            @Override
            public void onResponse(Call<UserSession> call, Response<UserSession> response) {
                int code;
                code = response.code();
                String error = "";
                Log.i(TAG, "code=" + code);
                if (code == 200) {
                    mSDKCoordinator.notifySuccess(SDKAction.UPLOADIMAGE);
                    return;
                } else if (code == 403) {//登录失败，用户密码错误
                    error = "用户名或密码错误";
                } else if (code == 400) {//登录失败，请求服务器失败

                    error = "参数错误";
                } else if (code == 404) {//登录失败，请求服务器失败
                    error = "参数错误";
                } else if (code == 500) {//登录失败，请求服务器失败
                    error = "服务器内部错误";
                }
                if (response.errorBody() != null) {

                    try {
                        Gson gson = new Gson();
                        Log.i(TAG, "code=" + code + response.message() + response.errorBody().string());
                        ErrorBody errorbody = gson.fromJson(response.errorBody().string(), ErrorBody.class);
                        mSDKCoordinator.notifyFailure(SDKAction.UPLOADIMAGE, errorbody.getMsg());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mSDKCoordinator.notifyFailure(SDKAction.UPLOADIMAGE, error);
                }
            }

            @Override
            public void onFailure(Call<UserSession> call, Throwable t) {
                String error = "无法访问网络";
                mSDKCoordinator.notifyFailure(SDKAction.UPLOADIMAGE, error);
            }
        });
    }

    private HashMap<String, Bitmap> userImages;

    public HashMap<String, Bitmap> getUserImages() {
        return userImages;
    }

    /**
     * 用户获取头像
     */
    public void getImage(final String username) {
        RestfulToolsPng.getSingleton().getImage(username, new Callback<Bitmap>() {
            @Override
            public void onResponse(Call<Bitmap> call, Response<Bitmap> response) {
                int code;
                code = response.code();
                String error = "";
                if (code == 200) {
                    Log.i(TAG,"获取用户头像放到头像bitmap中:"+username);
                    userImages.put(username, response.body());
                    mSDKCoordinator.notifyGetImageSuccess(SDKAction.GETIMAGE, response.body());
                    return;
                } else if (code == 403) {//登录失败，用户密码错误
                    error = "用户名或密码错误";
                } else if (code == 400) {//登录失败，请求服务器失败
                    error = "参数错误";
                } else if (code == 404) {//登录失败，请求服务器失败
                    error = "参数错误";
                } else if (code == 500) {//登录失败，请求服务器失败
                    error = "服务器内部错误";
                }
                if (response.errorBody() != null) {
                    Log.i(TAG, "" + response.message());
                    try {
                        Gson gson = new Gson();
                        ErrorBody errorbody = gson.fromJson(response.errorBody().string(), ErrorBody.class);
                        mSDKCoordinator.notifyFailure(SDKAction.GETIMAGE, errorbody.getMsg());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mSDKCoordinator.notifyFailure(SDKAction.GETIMAGE, error);
                }
            }

            @Override
            public void onFailure(Call<Bitmap> call, Throwable t) {
                String error = "无法访问网络";
                mSDKCoordinator.notifyFailure(SDKAction.GETIMAGE, error);
            }
        });
    }
    public void getUserInfo( String username) {
        Log.i(TAG, "getUserInfo");
        RestfulToolsHomeGeniusString.getSingleton().readUserInfo(username, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                int responseCode=response.code();
                Log.i(TAG,"code="+responseCode);
                if(response.errorBody()!=null){
                    try {
                        Log.i(TAG,"error="+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(responseCode==200){
                    Log.i(TAG,"getUserInfo="+response.body());
                    mSDKCoordinator.homeGeniusGetUserInfo(response.body());
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
    public void alertUserInfo(String username, UserInfoAlertBody body) {
        Log.i(TAG, "alertUserInfo");
        RestfulToolsHomeGenius.getSingleton().alertUserInfo(username, body, new Callback<DeviceOperationResponse>() {
            @Override
            public void onResponse(Call<DeviceOperationResponse> call, Response<DeviceOperationResponse> response) {
                int responseCode=response.code();
                Log.i(TAG,"code="+responseCode);
                if(response.errorBody()!=null){
                    try {
                        Log.i(TAG,"error="+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(responseCode==200){
                    Log.i(TAG,"alertUserInfo="+response.body());
                    mSDKCoordinator.alertUserInfo(response.body());
                }
            }

            @Override
            public void onFailure(Call<DeviceOperationResponse> call, Throwable t) {

            }
        });
    }

    /**
     * 注销登录
     */
    public void logout() {
        if (mUserSession == null || mUserInfo == null) {
            String error = "尚未登录";
            mSDKCoordinator.notifyFailure(SDKAction.LOGOUT, error);
            return;
        }
        RestfulTools.getSingleton().logout(new Callback<UserSession>() {
            @Override
            public void onResponse(Call<UserSession> call, Response<UserSession> response) {
                int code = response.code();
                if (code == 200) {
                    RestfulTools.getSingleton().setUsername(null);
                    RestfulTools.getSingleton().setToken(null);
                    mSDKCoordinator.afterLogout();
                    mUserSession = null;//用户注销成功后清除用户的个人信息
                    mSDKCoordinator.notifySuccess(SDKAction.LOGOUT);
                } else if (code == 400) {
                    String error = "服务器内部错误";
                    mSDKCoordinator.notifyFailure(SDKAction.LOGOUT, error);
                } else if (code == 403) {
                    mSDKCoordinator.notifyFailure(SDKAction.LOGOUT, "当前账号已在其它设备上登录");
                }
            }

            @Override
            public void onFailure(Call<UserSession> call, Throwable t) {
                String error = "无法访问网络";
                mSDKCoordinator.notifyFailure(SDKAction.LOGOUT, error);
            }
        });
    }


    private String bindedDeviceName;

    public String getBindedDeviceName() {
        return bindedDeviceName;
    }

    /**
     * 绑定设备
     *
     * @param DeviceSn
     */
    public void bindDevice(String DeviceSn) {
        if (TextUtils.isEmpty(DeviceSn)) {
            String var0 = "DeviceSn can't be null.";
            throw new NullPointerException(var0);
        }
        if (mUserSession == null) {
            String error = "请先登录";
            mSDKCoordinator.notifyFailure(SDKAction.BIND, error);
            return;
        }
        final BindingInfo info = new BindingInfo();
        info.device_list = new String[]{DeviceSn};
        Log.i(TAG, "绑定设备");
        RestfulTools.getSingleton().bindDevice(info, new Callback<DeviceRoot>() {
            @Override
            public void onResponse(Call<DeviceRoot> call, Response<DeviceRoot> response) {
                DeviceRoot deviceRoot = response.body();

                String error;
                Log.i(TAG, "绑定设备" + response.code());
                switch (response.code()) {

                    case 200:
                        //设备绑定成功
                        List<DeviceInfo> deviceList = deviceRoot.getDevice_list();
                        Log.i(TAG, "binddevice info=" + deviceList.size());
                        for (int i = 0; i < deviceList.size(); i++) {
                            Log.i(TAG, "binddevice info=" + deviceList.get(i).getChannels().toString());
                            if (deviceList.get(i).getDeviceSn().equals(info.device_list[0])) {
                                //设备的SN相同
                                bindedDeviceName = deviceList.get(i).getDeviceName();
                            }
                        }
                        if (deviceList.size() > 0) {
                            mSDKCoordinator.afterDeviceBinding();
                            mSDKCoordinator.notifyBindSuccess(SDKAction.BIND, deviceList.get(0).getDevice_key());
                        } else {//绑定失败，设备序列号不存在
                            error = "设备不存在，或者您尚未被添加为可绑定该设备的成员";
                            mSDKCoordinator.notifyFailure(SDKAction.BIND, error);
                        }
                        break;
                    case 403:
                        error = "请先登录";
                        mSDKCoordinator.notifyFailure(SDKAction.BIND, error);
                        break;
                    default:
                        error = "绑定设备失败";
                        mSDKCoordinator.notifyFailure(SDKAction.BIND, error);
                }
            }

            @Override
            public void onFailure(Call<DeviceRoot> call, Throwable t) {
                String error = "绑定设备失败";
                mSDKCoordinator.notifyFailure(SDKAction.BIND, error);
            }
        });
    }

    /**
     * 解除设备绑定
     *
     * @param device
     */
    public void unbindDevice(final BaseDevice device) {
        if (device == null) return;
        String deviceKey = device.getDeviceKey();
        if (TextUtils.isEmpty(deviceKey)) return;

        RestfulTools.getSingleton().unbindDevice(deviceKey, new Callback<DeviceRoot>() {

            @Override
            public void onResponse(Call<DeviceRoot> call, Response<DeviceRoot> response) {
                switch (response.code()) {
                    case 200:
                        mSDKCoordinator.notifySuccess(SDKAction.UNBIND);
                        mSDKCoordinator.afterDeviceUnbinding();
                        break;

                    default:
                        String error = "解除绑定设备失败";
                        mSDKCoordinator.notifyFailure(SDKAction.UNBIND, error);
                        break;
                }
            }

            @Override
            public void onFailure(Call<DeviceRoot> call, Throwable t) {
                String error = "解除绑定设备失败";
                mSDKCoordinator.notifyFailure(SDKAction.UNBIND, error);
            }
        });
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken, MqttAction cation, String clientHandle, String topic) {

    }

    @Override
    public void onFailure(IMqttToken token, Throwable exception, MqttAction cation, String clientHandle, String topic) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
