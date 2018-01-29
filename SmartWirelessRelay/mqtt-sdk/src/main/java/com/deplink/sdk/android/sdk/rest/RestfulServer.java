package com.deplink.sdk.android.sdk.rest;

import android.graphics.Bitmap;

import com.deplink.sdk.android.sdk.bean.BindingInfo;
import com.deplink.sdk.android.sdk.bean.CommonRes;
import com.deplink.sdk.android.sdk.bean.DeviceCookieItem;
import com.deplink.sdk.android.sdk.bean.DeviceCookieRes;
import com.deplink.sdk.android.sdk.bean.DeviceMemberItem;
import com.deplink.sdk.android.sdk.bean.DeviceMemberRes;
import com.deplink.sdk.android.sdk.bean.DeviceProperty;
import com.deplink.sdk.android.sdk.bean.DeviceRoot;
import com.deplink.sdk.android.sdk.bean.DeviceUpgradeRes;
import com.deplink.sdk.android.sdk.bean.User;
import com.deplink.sdk.android.sdk.bean.UserSession;
import com.deplink.sdk.android.sdk.json.AppUpdateResponse;
import com.deplink.sdk.android.sdk.json.Password;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by huqs on 2016/7/4.
 */

public interface RestfulServer {
    //用户注册
    @POST("/user")
    Call<UserSession> register(@Body User user);

    //用户登录
    @PUT("/user/{user_name}/login")
    Call<UserSession> login(@Path("user_name") String user_name, @Body User user);

    //用户登录后修改密码
    @PUT("/user/{user_name}/passwd")
    Call<UserSession> loginedAlertPassword(@Path("user_name") String user_name, @Body Password password, @Header("token") String token);

    //上传头像
    @Multipart
    @POST("/user/{user_name}/avatar")
    Call<UserSession> uploadImage(@Header("token") String token, @Path("user_name") String user_name, @Part MultipartBody.Part file);

    @GET("/user/{user_name}/avatar")
    Call<Bitmap> getImage(@Header("token") String token, @Path("user_name") String user_name);

    @GET("/user/{user_name}/doorbell/{device_uid}/snapshot/{file}")
    Call<Bitmap> getDoorBellVisitorImage(@Header("token") String token,
                                         @Path("user_name") String user_name,
                                         @Path("device_uid") String device_uid,
                                         @Path("file") String file);

    //用户注销
    @PUT("/user/{user_name}/logout")
    Call<UserSession> logout(@Path("user_name") String user_name, @Header("token") String token);

    //用户重置密码
    @PUT("/user/{user_name}")
    Call<UserSession> resetPassword(@Path("user_name") String user_name, @Body User user);

    //验证token是否有效，用来判断用户是否被迫下线
    @GET("/user/{user_name}/session")
    Call<UserSession> session(@Path("user_name") String user_name, @Header("token") String token);

    //读取已绑定设备列表
    @GET("/user/{user_name}/binding")
    Call<DeviceRoot> getBinding(@Path("user_name") String user_name, @Header("token") String token);

    //绑定设备
    @PUT("/user/{user_name}/binding")
    Call<DeviceRoot> bindDevice(@Path("user_name") String user_name, @Header("token") String token, @Body BindingInfo info);

    //解除绑定设备
    @DELETE("/user/{user_name}/binding/{device_key}")
    Call<DeviceRoot> unbindDevice(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token);

    //添加设备cookie数据
    @POST("/user/{user_name}/binding/{device_key}/cookie")
    Call<DeviceCookieRes> addDeviceCookie(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token, @Body DeviceCookieItem item);

    //修改设备cookie数据
    @PUT("/user/{user_name}/binding/{device_key}/cookie")
    Call<CommonRes> updateDeviceCookie(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token, @Body DeviceCookieItem item);

    //查询设备cookie数据
    @GET("/user/{user_name}/binding/{device_key}/cookie")
    Call<DeviceCookieRes> getDeviceCookie(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token, @Query("tag") String tag, @Query("id") Integer id);

    //删除设备cookie数据
    @DELETE("/user/{user_name}/binding/{device_key}/cookie")
    Call<CommonRes> deleteDeviceCookie(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token, @Query("tag") String tag, @Query("id") Integer id);

    //读取设备属性
    @GET("/user/{user_name}/binding/{device_key}/property")
    Call<DeviceProperty> getDeviceProperty(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token);

    //修改设备属性
    @PUT("/user/{user_name}/binding/{device_key}/property")
    Call<CommonRes> updateDeviceProperty(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token, @Body DeviceProperty items);

    //查询设备升级信息
    @GET("/user/{user_name}/binding/{device_key}/upgrade")
    Call<DeviceUpgradeRes> getDeviceUpgradeInfo(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token);

    @POST("/user/{user_name}/binding/{device_key}/member")
    Call<DeviceMemberRes> addDeviceMember(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token, @Body DeviceMemberItem item);


    @PUT("/user/{user_name}/binding/{device_key}/member")
    Call<CommonRes> updateDeviceMember(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token, @Body DeviceMemberItem item);


    @GET("/user/{user_name}/binding/{device_key}/member")
    Call<DeviceMemberRes> getDeviceMember(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token, @Query("id") Integer id);

    //删除设备cookie数据
    @DELETE("/user/{user_name}/binding/{device_key}/member")
    Call<CommonRes> deleteDeviceMember(@Path("user_name") String user_name, @Path("device_key") String device_key, @Header("token") String token, @Query("id") Integer id);
    //APP升级
    @GET("/app/upgrade")
    Call<AppUpdateResponse> getAppUpdateInfo(@Header("app-key") String appkey, @Header("app-version") String appVersion);
}