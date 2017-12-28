package com.deplink.sdk.android.sdk.rest;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by huqs on 2016/7/4.
 */

public interface RestfulServerPm25 {
    @GET("pm2_5.json")
    Call<JsonObject> getPm25( @Query("city") String id, @Query("token") String token);
}