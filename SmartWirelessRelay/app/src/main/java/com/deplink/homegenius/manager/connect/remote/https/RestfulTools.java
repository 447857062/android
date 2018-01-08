package com.deplink.homegenius.manager.connect.remote.https;


import android.content.Context;

import com.deplink.homegenius.Protocol.json.http.QueryBandResponse;
import com.deplink.homegenius.Protocol.json.http.QueryRCCodeResponse;

import java.util.concurrent.TimeUnit;

import com.deplink.homegenius.Protocol.json.http.QueryTestCodeResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestfulTools {
    private volatile static RestfulTools singleton;
    private volatile static RestfulServer apiService;


    private static Context mContext;

    private static final String baseUrl = "http://regsrv1.guogee.com:86";

    /**
     * 假设: Retrofit是线程安全的
     */
    private RestfulTools() {
        Retrofit.Builder builder = null;

        builder = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create());


        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(20 * 1000, TimeUnit.MILLISECONDS);
        OkHttpClient okClient = clientBuilder.build();
        builder.client(okClient);
        Retrofit retrofit = builder.build();
        apiService = retrofit.create(RestfulServer.class);
    }


    public static RestfulTools getSingleton(Context context) {
        mContext = context;
        if (singleton == null) {
            synchronized (RestfulTools.class) {
                if (singleton == null) {
                    singleton = new RestfulTools();
                }
            }
        }
        return singleton;
    }

    /**
     * 获取红外产品品牌
     * @param deviceType
     * @param lan
     * @param cll
     * @return
     */
    public Call<QueryBandResponse> queryBand(String deviceType, String lan, Callback<QueryBandResponse> cll) {

        Call<QueryBandResponse> call = apiService.getIrBrands(baseUrl + "/api/ir/getIrBrandsNew", deviceType, lan);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    /**
     * http://regsrv1.guogee.com:86/api/ir/getIrCodeByBrandNew?deviceType=TV&deviceBrand=长虹&lan=cn
     * 获取品牌下所有测试码
     *
     * @param deviceType
     * @param lan
     * @param cll
     * @return
     */
    public Call<QueryTestCodeResponse> queryTestCode(String deviceType, String deviceBrand, String lan, Callback<QueryTestCodeResponse> cll) {
        Call<QueryTestCodeResponse> call = apiService.getIrCodeByBrand(baseUrl + "/api/ir/getIrCodeByBrandNew", deviceType, deviceBrand, lan);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

    /**
     * 下载码表
     *
     * @param deviceType
     * @param brandId
     * @param controlId
     * @param cll
     * @return
     */
    public Call<QueryRCCodeResponse> downloadIrCode(String deviceType, String brandId, int controlId, Callback<QueryRCCodeResponse> cll) {
        Call<QueryRCCodeResponse> call = apiService.downloadIrCode(baseUrl + "/api/ir/downloadIrCodeNew", deviceType, brandId, controlId);
        if (cll != null) {
            call.enqueue(cll);
        }
        return call;
    }

}
