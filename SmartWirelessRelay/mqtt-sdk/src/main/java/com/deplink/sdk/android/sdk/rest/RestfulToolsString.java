package com.deplink.sdk.android.sdk.rest;


import android.content.Context;
import android.content.SharedPreferences;

import com.deplink.sdk.android.sdk.rest.ConverterFactory.StringConvertFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class RestfulToolsString {
    private volatile static RestfulToolsString singleton;
    private volatile static RestfulRouterServer apiService;
    private boolean debug = true;
    private static Context mContext;

    /**
     * 假设: Retrofit是线程安全的
     */
    private RestfulToolsString() {
        //service.deplink.net
        //admin.deplink.net
        Retrofit.Builder builder;

        builder = new Retrofit.Builder().baseUrl("http://lkwifi.cn")
                .addConverterFactory(StringConvertFactory.create());
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = new ArrayList<>();
                SharedPreferences sp = mContext.getSharedPreferences("user", Context.MODE_PRIVATE);
                String token = sp.getString("token", null);
                if (token != null) {
                    Cookie cookie = Cookie.parse(url, token);
                    cookies.add(cookie);
                }
                return cookies;
            }
        });
        ;
        clientBuilder.connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS);
        if (debug) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(interceptor);
        }
        OkHttpClient okClient = clientBuilder.build();
        builder.client(okClient);
        Retrofit retrofit = builder.build();
        apiService = retrofit.create(RestfulRouterServer.class);
    }
    public static RestfulToolsString getSingleton(Context context) {
        mContext = context;
        if (singleton == null) {
            synchronized (RestfulToolsString.class) {
                if (singleton == null) {
                    singleton = new RestfulToolsString();
                }
            }
        }
        return singleton;
    }

    public Call<String> WirelessRelayScan(Callback<String> cll) {
        Call<String> call = apiService.WirelessRelayScanString("http://lkwifi.cn" + RestfulToolsRouter.getSingleton(mContext).getLink() + "/admin/home/wan", "wifiscan"/*,"notrestart"*/);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

}
