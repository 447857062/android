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

    /**
     * 假设: Retrofit是线程安全的
     */
    private RestfulToolsWeather() {

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("http://www.weather.com.cn/data/sk/")
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

    public Call<JsonObject> getWeatherInfo(Callback<JsonObject> cll,String citycode) {

        Call<JsonObject> call = apiService.getWeatherInfo(citycode);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }




}
