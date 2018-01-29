package com.deplink.sdk.android.sdk.rest;


import android.util.Log;

import com.deplink.sdk.android.sdk.homegenius.DeviceAddBody;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.Deviceprops;
import com.deplink.sdk.android.sdk.homegenius.Room;
import com.deplink.sdk.android.sdk.homegenius.RoomUpdateName;
import com.deplink.sdk.android.sdk.homegenius.ShareDeviceBody;
import com.deplink.sdk.android.sdk.homegenius.UserInfoAlertBody;
import com.deplink.sdk.android.sdk.homegenius.VirtualDeviceAddBody;
import com.deplink.sdk.android.sdk.homegenius.VirtualDeviceAlertBody;
import com.deplink.sdk.android.sdk.json.homegenius.LockUserId;
import com.deplink.sdk.android.sdk.utlis.SslUtil;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestfulToolsHomeGenius {
    private static final String TAG = "RestfulToolsHomeGenius";
    private volatile static RestfulToolsHomeGenius singleton;
    private volatile static RestfulHomeGeniusServer apiService;
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
        HttpLoggingInterceptor.Level level= HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("OkHttpClient","OkHttpMessage:"+message);
            }
        });
        loggingInterceptor.setLevel(level);
        builder = new Retrofit.Builder().baseUrl(baseUrl).
                addConverterFactory(GsonConverterFactory.create());
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(loggingInterceptor);
        clientBuilder.connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .sslSocketFactory(SslUtil.getSocketFactory(ca))
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS);
        OkHttpClient okClient = clientBuilder.build();
        builder.client(okClient);
        Retrofit retrofit = builder.build();
        apiService = retrofit.create(RestfulHomeGeniusServer.class);
    }

    public static RestfulToolsHomeGenius getSingleton() {
        if (singleton == null) {
            synchronized (RestfulToolsHomeGenius.class) {
                if (singleton == null) {
                    singleton = new RestfulToolsHomeGenius();
                }
            }
        }
        return singleton;
    }

    public Call<JsonObject> addDevice(String username, DeviceAddBody deviceAddBody, Callback<JsonObject> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }

        Call<JsonObject> call = apiService.addDevice(username, deviceAddBody, RestfulTools.getSingleton().getToken());
        Log.i(TAG, "addDevice:" + username);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<DeviceOperationResponse> addVirtualDevice(String username, VirtualDeviceAddBody deviceAddBody, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "addDevice:" + username);
        Call<DeviceOperationResponse> call = apiService.addVirtualDevice(username, deviceAddBody, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<DeviceOperationResponse> cancelDeviceShare(String username, String  device_uid, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        ShareDeviceBody body=new ShareDeviceBody();
        body.setUser_name(username);
        body.setDevice_uid(device_uid);
        Log.i(TAG, "cancelDeviceShare:" + username);
        Call<DeviceOperationResponse> call = apiService.cancelDeviceShare(username, body, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceOperationResponse> deleteDevice(String username, String uid, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "deleteDevice:" + username);
        Call<DeviceOperationResponse> call = apiService.deleteDevice(username, uid, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<DeviceOperationResponse> deleteDoorBellVisitor(String username, String uid,String file, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "deleteDevice:" + username+"file name="+file);
        Call<DeviceOperationResponse> call = apiService.deleteDoorBellVisitorImage(
                username,
                uid,
                file,
                RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    public Call<DeviceOperationResponse> deleteVirtualDevice(String username, String uid, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "deleteVirtualDevice:" + username);
        Call<DeviceOperationResponse> call = apiService.deleteVirtualDevice(username, uid, RestfulTools.getSingleton().getToken());
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
    public Call<DeviceOperationResponse> alertVirtualDevice(String username, VirtualDeviceAlertBody deviceprops, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "alertDevice:" + username);
        Call<DeviceOperationResponse> call = apiService.alertVirtualDevice(username, deviceprops, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<DeviceOperationResponse> alertUserInfo(String username, UserInfoAlertBody body, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "alertUserInfo:" + username);
        Call<DeviceOperationResponse> call = apiService.alertUserInfo(username, body, RestfulTools.getSingleton().getToken());
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

    public Call<DeviceOperationResponse> deleteRomm(String username, String uid, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "deleteRoom:" + username);
        Call<DeviceOperationResponse> call = apiService.deleteRoom(username, uid, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<DeviceOperationResponse> updateRoomName(String username, RoomUpdateName roomUpdateName, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "updateRoomName:" + username);
        Call<DeviceOperationResponse> call = apiService.updateRoomName(username, roomUpdateName, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
    public Call<DeviceOperationResponse> setLockUserIdName(String username, String device_uid, LockUserId userIdBody, Callback<DeviceOperationResponse> cll) {
        if (null == username) {
            if (cll != null) {
                cll.onFailure(null, new Throwable(errMsg));
            }
            return null;
        }
        Log.i(TAG, "setLockUserIdName:" + username);
        Call<DeviceOperationResponse> call = apiService.setLockUserIdName(username, device_uid,userIdBody, RestfulTools.getSingleton().getToken());
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }
}
