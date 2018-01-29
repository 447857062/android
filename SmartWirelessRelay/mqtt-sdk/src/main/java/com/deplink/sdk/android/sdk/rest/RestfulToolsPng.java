package com.deplink.sdk.android.sdk.rest;

import android.graphics.Bitmap;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.rest.ConverterFactory.PngConvertFactory;
import com.deplink.sdk.android.sdk.utlis.SslUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by huqs on 2016/7/4.
 */
public class RestfulToolsPng {
    private volatile static RestfulToolsPng singleton;
    private volatile static RestfulServer apiService;
    private String username;

    /**
     * 假设: Retrofit是线程安全的
     */
    private RestfulToolsPng() {

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://api.deplink.net")
                .addConverterFactory(PngConvertFactory.create());

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
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS)
                .sslSocketFactory(SslUtil.getSocketFactory(ca));
        if (DeplinkSDK.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(interceptor);
        }
        OkHttpClient okClient = clientBuilder.build();

        builder.client(okClient);
        Retrofit retrofit = builder.build();
        apiService = retrofit.create(RestfulServer.class);
    }

    public static RestfulToolsPng getSingleton() {
        if (singleton == null) {
            synchronized (RestfulToolsPng.class) {
                if (singleton == null) {
                    singleton = new RestfulToolsPng();
                }
            }
        }
        return singleton;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }



    public Call<Bitmap> getImage(String username, Callback<Bitmap> cll) {
        Call<Bitmap> call = apiService.getImage(RestfulTools.getSingleton().getToken(),username);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<Bitmap> getDoorBellImage(String username,String device_uid,String file, Callback<Bitmap> cll) {
        Call<Bitmap> call = apiService.getDoorBellVisitorImage(RestfulTools.getSingleton().getToken(),username,device_uid,file);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }


}
