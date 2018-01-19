package com.deplink.sdk.android.sdk.rest;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.deplink.sdk.android.sdk.rest.ConverterFactory.CheckResponse;

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
import retrofit2.converter.gson.GsonConverterFactory;

public class RestfulToolsRouter {
    private volatile static RestfulToolsRouter singleton;
    private volatile static RestfulRouterServer apiService;
    private String link = "";//检查内网是否为我们的路由器以及获取stok这步骤中获取
    private boolean debug = true;
    private static Context mContext;
    private static final String baseUrl = "http://lkwifi.cn";

    /**
     * 假设: Retrofit是线程安全的
     */
    private RestfulToolsRouter() {
        //service.deplink.net
        //admin.deplink.net
        Retrofit.Builder builder = null;

        builder = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create());
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < cookies.size(); i++) {
                    sb.append(cookies.get(i).toString());
                }
                Log.i("saveFromResponse", "" + sb.toString());

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


    public static RestfulToolsRouter getSingleton(Context context) {
        mContext = context;
        if (singleton == null) {
            synchronized (RestfulToolsRouter.class) {
                if (singleton == null) {
                    singleton = new RestfulToolsRouter();
                }
            }
        }
        return singleton;
    }


    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }


    public Call<CheckResponse> checkRouter(String password, Callback<CheckResponse> cll) {
        Call<CheckResponse> call = apiService.checkRouter(baseUrl + "/cgi-bin/luci", "islkwifi", "admin", password);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<RouterResponse> wifiSignalStrengthSetting(String ssid, String key, Callback<RouterResponse> cll) {
        Log.i("wifiSignal", "url=" + baseUrl + getLink() + "/admin/home/wifi?wifi.option=set&wifi.name=2g&wifi.ssid=" + ssid + "&wifi.key=" + key);
        Call<RouterResponse> call = apiService.wifiSignalStrengthSetting(baseUrl + getLink() + "/admin/home/wifi", "set", "2g", ssid, key, "notrestart");
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }


    public Call<RouterResponse> internetAccess(String account, String password, Callback<RouterResponse> cll) {
        Log.i("wifiSignal", "link=" + link + "url=" + baseUrl + link + "/admin/home/wan");

        Call<RouterResponse> call = apiService.internetAccess(baseUrl + link + "/admin/home/wan", "set", "pppoe", account, password, "notrestart");
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<RouterResponse> staticIp(String ipaddr, String netmask, String gateway, String dns, Callback<RouterResponse> cll) {

        Call<RouterResponse> call = apiService.staticIp(baseUrl + link + "/admin/home/wan", "set", "static", ipaddr, netmask, gateway, dns, "notrestart");
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<RouterResponse> dynamicIp(Callback<RouterResponse> cll) {

        Call<RouterResponse> call = apiService.dynamicIp(baseUrl + link + "/admin/home/wan", "set", "dhcp", "notrestart");
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<wifiScanRoot[]> WirelessRelayScan(Callback<wifiScanRoot[]> cll) {

        Call<wifiScanRoot[]> call = apiService.WirelessRelayScan(baseUrl + link + "/admin/home/wan", "wifiscan"/*,"notrestart"*/);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<String> rebootRouter(Callback<String> cll) {

        Call<String> call = apiService.rebootRouter(baseUrl + link + "/admin/system/reboot", 1);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<RouterResponse> WirelessRelayConnect(String crypt, String encryption, int channel, String wifiName, String password, Callback<RouterResponse> cll) {
        Call<RouterResponse> call = apiService.WirelessRelayConnect(baseUrl + link + "/admin/home/wan", "set", "reperter", crypt, encryption, channel, wifiName, password, "notrestart");
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
}
