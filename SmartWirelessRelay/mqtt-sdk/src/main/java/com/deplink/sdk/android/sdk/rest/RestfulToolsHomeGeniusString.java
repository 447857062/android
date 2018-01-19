package com.deplink.sdk.android.sdk.rest;


import android.util.Log;

import com.deplink.sdk.android.sdk.rest.ConverterFactory.StringConvertFactory;
import com.deplink.sdk.android.sdk.utlis.SslUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class RestfulToolsHomeGeniusString {
    private static final String TAG = "RestfulToolsHomeGenius";
    private volatile static RestfulToolsHomeGeniusString singleton;
    private volatile static RestfulHomeGeniusServer apiService;
    private static final String baseUrl = "https://api.deplink.net";
    private String errMsg = "请先登录";

    /**
     * 假设: Retrofit是线程安全的
     */
    private RestfulToolsHomeGeniusString() {
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
                addConverterFactory(StringConvertFactory.create());
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .sslSocketFactory(SslUtil.getSocketFactory(ca))
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS);
        OkHttpClient okClient = clientBuilder.build();
        builder.client(okClient);
        Retrofit retrofit = builder.build();
        apiService = retrofit.create(RestfulHomeGeniusServer.class);
    }

    public static RestfulToolsHomeGeniusString getSingleton() {
        if (singleton == null) {
            synchronized (RestfulToolsHomeGeniusString.class) {
                if (singleton == null) {
                    singleton = new RestfulToolsHomeGeniusString();
                }
            }
        }
        return singleton;
    }

    public Call<String> getRoomInfo(String username, Callback<String> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "getRoomInfo:" + username);
        Call<String> call = apiService.getRoomInfo(username, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<String> getDeviceInfo(String username, Callback<String> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "getDeviceInfo:" + username);
        Call<String> call = apiService.getDeviceInfo(username, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<String> readDeviceInfo(String username, String uid, Callback<String> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "readDeviceInfo:" + username);
        Call<String> call = apiService.readDeviceInfo(username, uid, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<String> readVirtualDevices(String username, Callback<String> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "readDeviceInfo:" + username);
        Call<String> call = apiService.readVirtualDevices(username, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<String> readUserInfo(String username, Callback<String> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "readUserInfo:" + username);
        Call<String> call = apiService.readUserInfo(username, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<String> getLockUseId(String username,String deviceUid, Callback<String> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "getLockUseId:" + username+"token="+RestfulTools.getSingleton().getToken());
        Call<String> call = apiService.getLockUseId(username,deviceUid, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
}
