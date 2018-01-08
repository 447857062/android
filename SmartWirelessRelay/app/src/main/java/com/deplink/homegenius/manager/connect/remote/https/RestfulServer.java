package com.deplink.homegenius.manager.connect.remote.https;


import com.deplink.homegenius.Protocol.json.http.QueryBandResponse;
import com.deplink.homegenius.Protocol.json.http.QueryRCCodeResponse;
import com.deplink.homegenius.Protocol.json.http.QueryTestCodeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by huqs on 2016/7/4.
 */

public interface RestfulServer {

    @GET
    Call<QueryBandResponse> getIrBrands(@Url String url, @Query("deviceType") String deviceType, @Query("lan") String lan);

    @GET
    Call<QueryTestCodeResponse> getIrCodeByBrand(@Url String url, @Query("deviceType") String deviceType, @Query("deviceBrand") String deviceBrand, @Query("lan") String lan);

    //获取遥控器码库
    @GET
    Call<QueryRCCodeResponse> downloadIrCode(@Url String url, @Query("deviceType") String deviceType, @Query("brandId") String brandId, @Query("controlId") int controlId);
}