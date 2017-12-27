package com.deplink.sdk.android.sdk.rest;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by huqs on 2016/7/4.
 */

public interface RestfulServerWeather {
    @GET("{citycode}.html")
    Call<JsonObject> getWeatherInfo(@Path("citycode") String citycode);



}