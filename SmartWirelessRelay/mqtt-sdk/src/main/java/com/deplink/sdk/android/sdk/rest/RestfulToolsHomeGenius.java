package com.deplink.sdk.android.sdk.rest;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.DeviceResponse;
import com.deplink.sdk.android.sdk.homegenius.Deviceprops;
import com.deplink.sdk.android.sdk.homegenius.Room;
import com.deplink.sdk.android.sdk.homegenius.RoomResponse;
import com.deplink.sdk.android.sdk.utlis.SslUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestfulToolsHomeGenius {
    private static final String TAG = "RestfulToolsHomeGenius";
    private volatile static RestfulToolsHomeGenius singleton;
    private volatile static RestfulHomeGeniusServer apiService;
    private static Context mContext;
    private static final String baseUrl = "https://api.deplink.net";
    private String errMsg = "请先登录";

    /**
     * 假设: Retrofit是线程安全的
     */
    private RestfulToolsHomeGenius() {
        //service.deplink.net
        //admin.deplink.net
        Retrofit.Builder builder;
        String ca = "-----BEGIN CERTIFICATE-----\n" +
                "MIICMTCCAZoCCQCBJHhUa4Yq3jANBgkqhkiG9w0BAQsFADBdMQswCQYDVQQGEwJD\n" +
                "TjETMBEGA1UECAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50ZXJuZXQgV2lkZ2l0\n" +
                "cyBQdHkgTHRkMRYwFAYDVQQDDA0qLmRlcGxpbmsubmV0MB4XDTE2MDgzMTA0NDMy\n" +
                "NloXDTQ0MDExNzA0NDMyNlowXTELMAkGA1UEBhMCQ04xEzARBgNVBAgMClNvbWUt\n" +
                "U3RhdGUxITAfBgNVBAoMGEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZDEWMBQGA1UE\n" +
                "AwwNKi5kZXBsaW5rLm5ldDCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA2cld\n" +
                "OVOuLXpJEJnzmvS40HYT8mNaqbJI/lsQVZKVx+rOa9ZyNZPkg1kZouqxgZJRhQWD\n" +
                "Oq0CDkqVUyEUQwG1SkPu/GM8DFuRPLYyyPL/YaygYdgSCBAkinFeawtI2phbzQhM\n" +
                "CysMBpXHCl6tEepV/816/hLJorbRj6+NyjYdi28CAwEAATANBgkqhkiG9w0BAQsF\n" +
                "AAOBgQAYerSstTX5WVsDNtxmu42GIOuHgCSuw+EbKSuhwye8LVjkfj1UGC5zav91\n" +
                "gtPeEexrQAoohDEi0FgAEoMS7OlCvRRVBXZ66VkA6yH2uvr9G5qmEBbMOCpq/z+J\n" +
                "NkX8gffeUmw2VqA/7adjNLdZg3Zs8rJncgz9ooXcpdXL/+tbuQ==\n" +
                "-----END CERTIFICATE-----";
        builder = new Retrofit.Builder().baseUrl(baseUrl).
                addConverterFactory(GsonConverterFactory.create());
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
                .sslSocketFactory(SslUtil.getSocketFactory(ca))
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS);
        OkHttpClient okClient = clientBuilder.build();
        builder.client(okClient);
        Retrofit retrofit = builder.build();
        apiService = retrofit.create(RestfulHomeGeniusServer.class);
    }

    public static RestfulToolsHomeGenius getSingleton(Context context) {
        mContext = context;
        if (singleton == null) {
            synchronized (RestfulToolsHomeGenius.class) {
                if (singleton == null) {
                    singleton = new RestfulToolsHomeGenius();
                }
            }
        }
        return singleton;
    }

    public Call<RoomResponse> getRoomInfo(String username, Callback<RoomResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "getRoomInfo:" + username);
        Call<RoomResponse> call = apiService.getRoomInfo(username, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceResponse> getDeviceInfo(String username, Callback<DeviceResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "getDeviceInfo:" + username);
        Call<DeviceResponse> call = apiService.getDeviceInfo(username, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceOperationResponse> addDevice(String username, Deviceprops deviceprops, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "addDevice:" + username);
        Call<DeviceOperationResponse> call = apiService.addDevice(username, deviceprops, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceOperationResponse> deleteDevice(String username, Deviceprops deviceprops, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "addDevice:" + username);
        Call<DeviceOperationResponse> call = apiService.deleteDevice(username, deviceprops, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceOperationResponse> alertDevice(String username, Deviceprops deviceprops, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "alertDevice:" + username);
        Call<DeviceOperationResponse> call = apiService.alertDevice(username, deviceprops, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceOperationResponse> addRomm(String username, Room room, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "addRoom:" + username);
        Call<DeviceOperationResponse> call = apiService.addRoom(username, room, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceOperationResponse> deleteRomm(String username, Room room, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "deleteRoom:" + username);
        Call<DeviceOperationResponse> call = apiService.deleteRoom(username, room, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
}
