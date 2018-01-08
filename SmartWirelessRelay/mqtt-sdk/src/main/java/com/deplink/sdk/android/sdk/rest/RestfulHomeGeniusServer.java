package com.deplink.sdk.android.sdk.rest;


import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.DeviceResponse;
import com.deplink.sdk.android.sdk.homegenius.Deviceprops;
import com.deplink.sdk.android.sdk.homegenius.Room;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by kelijun
 */

public interface RestfulHomeGeniusServer {
    //获取房间信息
    @GET("/user/{user_name}/rooms")
    Call<String> getRoomInfo(@Path("user_name") String user_name, @Header("token") String token);
    //读绑定设备信息
    @GET("/user/{user_name}/devices")
    Call<DeviceResponse> getDeviceInfo(@Path("user_name") String user_name, @Header("token") String token);
    //添加设备
    @PUT("/user/{user_name}/devices")
    Call<DeviceOperationResponse> addDevice(@Path("user_name") String user_name, @Body Deviceprops deviceprops, @Header("token") String token);
    //修改设备属性
    @PUT("/user/{user_name}/deviceprops")
    Call<DeviceOperationResponse> alertDevice(@Path("user_name") String user_name, @Body Deviceprops deviceprops, @Header("token") String token);
    //删除设备
    @DELETE("/user/{user_name}/devices")
    Call<DeviceOperationResponse> deleteDevice(@Path("user_name") String user_name, @Body Deviceprops deviceprops,@Header("token") String token);
    //添加房间
    @PUT("/user/{user_name}/rooms")
    Call<DeviceOperationResponse> addRoom(@Path("user_name") String user_name, @Body Room room, @Header("token") String token);
    //删除房间
    @PUT("/user/{user_name}/rooms")
    Call<DeviceOperationResponse> deleteRoom(@Path("user_name") String user_name, @Body Room room, @Header("token") String token);

}