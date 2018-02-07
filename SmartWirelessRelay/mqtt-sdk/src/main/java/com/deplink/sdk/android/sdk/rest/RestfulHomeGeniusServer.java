package com.deplink.sdk.android.sdk.rest;


import com.deplink.sdk.android.sdk.homegenius.DeviceAddBody;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.Deviceprops;
import com.deplink.sdk.android.sdk.homegenius.Room;
import com.deplink.sdk.android.sdk.homegenius.RoomUpdateName;
import com.deplink.sdk.android.sdk.homegenius.ShareDeviceBody;
import com.deplink.sdk.android.sdk.homegenius.UserInfoAlertBody;
import com.deplink.sdk.android.sdk.homegenius.VirtualDeviceAddBody;
import com.deplink.sdk.android.sdk.homegenius.VirtualDeviceAlertBody;
import com.deplink.sdk.android.sdk.json.homegenius.LockUserId;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by kelijun
 */

public interface RestfulHomeGeniusServer {
    //修改用户个人信息
    @PUT("/user/{user_name}/profile")
    Call<DeviceOperationResponse> alertUserInfo(@Path("user_name") String user_name, @Body UserInfoAlertBody body, @Header("token") String token);
    //获取房间信息
    @GET("/user/{user_name}/rooms")
    Call<String> getRoomInfo(@Path("user_name") String user_name, @Header("token") String token);
    //读绑定设备信息
    @GET("/user/{user_name}/devices")
    Call<String> getDeviceInfo(@Path("user_name") String user_name, @Header("token") String token);
    //读绑定设备信息
    @GET("/user/{user_name}/smartlock/{device_uid}")
    Call<String> getLockUseId(@Path("user_name") String user_name,@Path("device_uid") String device_uid, @Header("token") String token);
  //添加设备
    @PUT("/user/{user_name}/devices")
    Call<JsonObject> addDevice(@Path("user_name") String user_name, @Body DeviceAddBody deviceAddBody, @Header("token") String token);
  //添加虚拟设备
    @POST("/user/{user_name}/irdevices")
    Call<DeviceOperationResponse> addVirtualDevice(@Path("user_name") String user_name, @Body VirtualDeviceAddBody deviceAddBody, @Header("token") String token);
  //取消设备分享
    @DELETE("/user/{user_name}/deviceshare/{shareid}")
    Call<DeviceOperationResponse> cancelDeviceShare(@Path("user_name") String user_name,@Path("shareid") int shareid, @Header("token") String token);

    //修改设备属性
    @PUT("/user/{user_name}/deviceprops")
    Call<DeviceOperationResponse> alertDevice(@Path("user_name") String user_name, @Body Deviceprops deviceprops, @Header("token") String token);
    //修改虚拟设备属性
    @PUT("/user/{user_name}/irdevices")
    Call<DeviceOperationResponse> alertVirtualDevice(@Path("user_name") String user_name, @Body VirtualDeviceAlertBody deviceprops, @Header("token") String token);
    //读设备属性
    @GET("/user/{user_name}/deviceprops/{uid}")
    Call<String> readDeviceInfo(@Path("user_name") String user_name,@Path("uid") String uid, @Header("token") String token);
    //查询虚拟设备
    @GET("/user/{user_name}/irdevices")
    Call<String> readVirtualDevices(@Path("user_name") String user_name, @Header("token") String token);
    //读用户个人信息
    @GET("/user/{user_name}/profile")
    Call<String> readUserInfo(@Path("user_name") String user_name, @Header("token") String token);
    //读门铃记录
    @GET("/user/{user_name}/doorbell/visits/{device_uid}")
    Call<String> readDoorBeelVisitorInfo(@Path("user_name") String user_name, @Path("device_uid") String device_uid, @Header("token") String token);
    //分享设备
    @PUT("/user/{user_name}/deviceshare/{device_uid}")
    Call<DeviceOperationResponse> shareDevice(@Path("user_name") String user_name,@Path("device_uid") String device_uid, @Body ShareDeviceBody shareDeviceBody, @Header("token") String token);
    //获取设备分享信息
    @GET("/user/{user_name}/deviceshare/{device_uid}")
    Call<String> getDeviceShareInfo(@Path("user_name") String user_name, @Path("device_uid") String device_uid, @Header("token") String token);
    //删除设备
    @DELETE("/user/{user_name}/devices/{uid}")
    Call<DeviceOperationResponse> deleteDevice(@Path("user_name") String user_name,@Path("uid") String uid ,@Header("token") String token);
    @DELETE("/user/{user_name}/doorbell/{device_uid}/snapshot/{file}")
    Call<DeviceOperationResponse> deleteDoorBellVisitorImage(
            @Path("user_name") String user_name,
            @Path("device_uid") String device_uid ,
            @Path("file") String file ,
            @Header("token") String token);
    //删除虚拟设备
    @DELETE("/user/{user_name}/irdevices/{uid}")
    Call<DeviceOperationResponse> deleteVirtualDevice(@Path("user_name") String user_name,@Path("uid") String uid ,@Header("token") String token);
    //添加房间
    @POST("/user/{user_name}/rooms")
    Call<DeviceOperationResponse> addRoom(@Path("user_name") String user_name, @Body Room room, @Header("token") String token);
    //删除房间
    @DELETE("/user/{user_name}/rooms/{uid}")
    Call<DeviceOperationResponse> deleteRoom(@Path("user_name") String user_name, @Path("uid") String uid, @Header("token") String token);
    //更新房间名称
    @PUT("/user/{user_name}/rooms")
    Call<DeviceOperationResponse> updateRoomName(@Path("user_name") String user_name, @Body RoomUpdateName roomUpdateName, @Header("token") String token);
    //更新房间名称
    @PUT("/user/{user_name}/smartlock/{device_uid}")
    Call<DeviceOperationResponse> setLockUserIdName(@Path("user_name") String user_name, @Path("device_uid") String device_uid, @Body LockUserId userIdBody, @Header("token") String token);

}