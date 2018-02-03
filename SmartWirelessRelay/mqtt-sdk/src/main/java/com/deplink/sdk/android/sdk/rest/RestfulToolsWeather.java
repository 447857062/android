package com.deplink.sdk.android.sdk.rest;

import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huqs on 2016/7/4.
 */
public class RestfulToolsWeather {
    private static final String TAG="RestfulToolsWeather";
    private volatile static RestfulToolsWeather singleton;
    private volatile static RestfulServerWeather apiService;
   // private static final String APIKEY ="884fce7c4f484fcab3b32fec1447f01f";
    private static final String APIKEY ="230fce30ce304b1ea5d964d5b854212d";

    /**
     * 假设: Retrofit是线程安全的
     */
    private RestfulToolsWeather() {

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://free-api.heweather.com/")
                .addConverterFactory(GsonConverterFactory.create());


        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS);
        OkHttpClient okClient = clientBuilder.build();

        builder.client(okClient);
        Retrofit retrofit = builder.build();
        apiService = retrofit.create(RestfulServerWeather.class);
    }

    public static RestfulToolsWeather getSingleton() {
        if (singleton == null) {
            synchronized (RestfulToolsWeather.class) {
                if (singleton == null) {
                    singleton = new RestfulToolsWeather();
                }
            }
        }
        return singleton;
    }

    public Call<JsonObject> getWeatherInfo(Callback<JsonObject> cll,String city) {

        Call<JsonObject> call = apiService.getWeatherInfo("https://free-api.heweather.com/s6/weather/now?",city, APIKEY);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<JsonObject> getWeatherPm25(Callback<JsonObject> cll,String city) {

        Call<JsonObject> call = apiService.getWeatherInfo("https://free-api.heweather.com/s6/air/now?",city, APIKEY);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

}
