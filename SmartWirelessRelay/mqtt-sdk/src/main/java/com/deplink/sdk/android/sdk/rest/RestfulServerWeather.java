package com.deplink.sdk.android.sdk.rest;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by huqs on 2016/7/4.
 */

public interface RestfulServerWeather {
    @GET
    Call<JsonObject> getWeatherInfo(@Url String url, @Query("location")String location, @Query("key")String key);
}