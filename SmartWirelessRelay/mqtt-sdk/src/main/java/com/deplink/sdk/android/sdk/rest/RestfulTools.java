package com.deplink.sdk.android.sdk.rest;

import android.util.Log;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.bean.BindingInfo;
import com.deplink.sdk.android.sdk.bean.CommonRes;
import com.deplink.sdk.android.sdk.bean.DeviceCookieItem;
import com.deplink.sdk.android.sdk.bean.DeviceCookieRes;
import com.deplink.sdk.android.sdk.bean.DeviceMemberItem;
import com.deplink.sdk.android.sdk.bean.DeviceMemberRes;
import com.deplink.sdk.android.sdk.bean.DeviceProperty;
import com.deplink.sdk.android.sdk.bean.DeviceRoot;
import com.deplink.sdk.android.sdk.bean.DeviceUpgradeRes;
import com.deplink.sdk.android.sdk.bean.User;
import com.deplink.sdk.android.sdk.bean.UserSession;
import com.deplink.sdk.android.sdk.json.AppUpdateResponse;
import com.deplink.sdk.android.sdk.json.Password;
import com.deplink.sdk.android.sdk.utlis.SslUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huqs on 2016/7/4.
 */
public class RestfulTools {
    private static final String TAG="RestfulTools";
    private volatile static RestfulTools singleton;
    private volatile static RestfulServer apiService;

    private String username;
    private String token;
    private String errMsg = "请先登录";

    /**
     * 假设: Retrofit是线程安全的
     */
    private RestfulTools() {

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://api.deplink.net")
                .addConverterFactory(GsonConverterFactory.create());
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

    public static RestfulTools getSingleton() {
        if (singleton == null) {
            synchronized (RestfulTools.class) {
                if (singleton == null) {
                    singleton = new RestfulTools();
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

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
    public Call<UserSession> session(Callback<UserSession> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<UserSession> call = apiService.session(username, token);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceRoot> getBinding(Callback<DeviceRoot> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<DeviceRoot> call = apiService.getBinding(username, token);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceRoot> bindDevice(BindingInfo info, Callback<DeviceRoot> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<DeviceRoot> call = apiService.bindDevice(username, token, info);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceRoot> unbindDevice(String device_key, Callback<DeviceRoot> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<DeviceRoot> call = apiService.unbindDevice(username, device_key, token);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<UserSession> register(User user, Callback<UserSession> cll) {
        Call<UserSession> call = apiService.register(user);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<UserSession> loginedAlertPassword(String oldPassword, String newPassword, Callback<UserSession> cll) {
        Password password = new Password();
        password.setPassword_org(oldPassword);
        password.setPassword(newPassword);
        password.setApplication_key(DeplinkSDK.getAppKey());
        password.setTimestamp(System.currentTimeMillis() / 1000);
        Call<UserSession> call = apiService.loginedAlertPassword(username, password,token);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<UserSession> uploadImage( String imagePath, Callback<UserSession> cll) {
        File file = new File(imagePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
        Log.i(TAG,"token="+token);
        Call<UserSession> call = apiService.uploadImage(token,username,body);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<UserSession> login(String username, String password, Callback<UserSession> cll) {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setApplication_key(DeplinkSDK.getAppKey());
        Call<UserSession> call = apiService.login(username, user);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<UserSession> logout(Callback<UserSession> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<UserSession> call = apiService.logout(username, token);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<UserSession> resetPassword(String user_name, User user, Callback<UserSession> cll) {
        Call<UserSession> call = apiService.resetPassword(user_name, user);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceCookieRes> addDeviceCookie(String device_key, DeviceCookieItem item, Callback<DeviceCookieRes> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<DeviceCookieRes> call = apiService.addDeviceCookie(username, device_key, token, item);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<CommonRes> updateDeviceCookie(String device_key, DeviceCookieItem item, Callback<CommonRes> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<CommonRes> call = apiService.updateDeviceCookie(username, device_key, token, item);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceCookieRes> getDeviceCookie(String device_key, String tag, Integer id, Callback<DeviceCookieRes> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<DeviceCookieRes> call = apiService.getDeviceCookie(username, device_key, token, tag, id);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<CommonRes> deleteDeviceCookie(String device_key, String tag, Integer id, Callback<CommonRes> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<CommonRes> call = apiService.deleteDeviceCookie(username, device_key, token, tag, id);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceProperty> getDeviceProperty(String device_key, Callback<DeviceProperty> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<DeviceProperty> call = apiService.getDeviceProperty(username, device_key, token);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<CommonRes> updateDeviceProperty(String device_key, DeviceProperty items, Callback<CommonRes> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<CommonRes> call = apiService.updateDeviceProperty(username, device_key, token, items);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceUpgradeRes> getDeviceUpgradeInfo(String device_key, Callback<DeviceUpgradeRes> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<DeviceUpgradeRes> call = apiService.getDeviceUpgradeInfo(username, device_key, token);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceMemberRes> addDeviceMember(String device_key, DeviceMemberItem item, Callback<DeviceMemberRes> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<DeviceMemberRes> call = apiService.addDeviceMember(username, device_key, token, item);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<CommonRes> updateDeviceMember(String device_key, DeviceMemberItem item, Callback<CommonRes> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<CommonRes> call = apiService.updateDeviceMember(username, device_key, token, item);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceMemberRes> getDeviceMember(String device_key, Integer id, Callback<DeviceMemberRes> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<DeviceMemberRes> call = apiService.getDeviceMember(username, device_key, token, id);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<AppUpdateResponse> getAppUpdateInfo(String appkey, String version, Callback<AppUpdateResponse> cll) {

        Call<AppUpdateResponse> call = apiService.getAppUpdateInfo(appkey,version);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<CommonRes> deleteDeviceMember(String device_key, Integer id, Callback<CommonRes> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Call<CommonRes> call = apiService.deleteDeviceMember(username, device_key, token, id);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }




}
