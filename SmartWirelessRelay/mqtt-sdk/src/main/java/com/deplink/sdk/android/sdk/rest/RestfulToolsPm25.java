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
public class RestfulToolsPm25 {
    private static final String TAG="RestfulToolsWeather";
    private volatile static RestfulToolsPm25 singleton;
    private volatile static RestfulServerPm25 apiService;

    /**
     * 假设: Retrofit是线程安全的
     */
    private RestfulToolsPm25() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://www.pm25.in/api/querys/")
                .addConverterFactory(GsonConverterFactory.create());
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS);
        OkHttpClient okClient = clientBuilder.build();

        builder.client(okClient);
        Retrofit retrofit = builder.build();
        apiService = retrofit.create(RestfulServerPm25.class);
    }

    public static RestfulToolsPm25 getSingleton() {
        if (singleton == null) {
            synchronized (RestfulToolsPm25.class) {
                if (singleton == null) {
                    singleton = new RestfulToolsPm25();
                }
            }
        }
        return singleton;
    }

    public Call<JsonObject> getPm25(Callback<JsonObject> cll,String city) {

        Call<JsonObject> call = apiService.getPm25(city,"5j1znBVAsnSf5xQyNQyq");
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }




}
