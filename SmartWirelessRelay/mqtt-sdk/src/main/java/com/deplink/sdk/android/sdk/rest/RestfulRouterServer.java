package com.deplink.sdk.android.sdk.rest;


import com.deplink.sdk.android.sdk.rest.ConverterFactory.CheckResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by huqs on 2016/7/4.
 */

public interface RestfulRouterServer {

    //下面是上网设置的内容
   // 检查内网是否为我们的路由器以及获取stok
    @GET/*("/cgi-bin/luci")*/
    Call<CheckResponse> checkRouter(@Url String url, @Query("islkwifi") String islkwifi, @Query("username") String username, @Query("password") String password);
   // 无线设置 信号强度设置，設置wifi賬號密碼 (/cgi-bin/luci/;stok=1a6483bdfe2278dc35a66f8c1357b48e/admin/home/wifi)
  //  @GET("{link}/admin/home/wifi")
 //  Call<RouterResponse> wifiSignalStrengthSetting(@Path("link")String path, @Query("wifi.option") String set, @Query("wifi.name") String name, @Query("wifi.ssid") String ssid, @Query("wifi.key") String key );
   @GET
   Call<RouterResponse> wifiSignalStrengthSetting(@Url String url, @Query("wifi.option") String set,
                                                  @Query("wifi.name") String name, @Query("wifi.ssid") String ssid,
                                                  @Query("wifi.key") String key, @Query("wifi.notrestart") String notrestart);

   //拨号上网(/cgi-bin/luci/;stok=1a6483bdfe2278dc35a66f8c1357b48e/admin/home/wan)
    @GET
    Call<RouterResponse> internetAccess(@Url String url, @Query("wan.option") String option, @Query("wan.style") String style, @Query("wan.account") String account, @Query("wan.password") String password, @Query("wan.notrestart") String notrestart);
   //静态IP(/cgi-bin/luci/;stok=1a6483bdfe2278dc35a66f8c1357b48e/admin/home/wan)
    @GET
    Call<RouterResponse> staticIp(@Url String url, @Query("wan.option") String option, @Query("wan.style") String style,
                                  @Query("wan.ipaddr") String ipaddr, @Query("wan.netmask") String netmask,
                                  @Query("wan.gateway") String gateway, @Query("wan.dns") String dns,
                                  @Query("wan.notrestart") String notrestart
    );
   //动态IP(/cgi-bin/luci/;stok=1a6483bdfe2278dc35a66f8c1357b48e/admin/home/wan)
    @GET
    Call<RouterResponse> dynamicIp(@Url String url, @Query("wan.option") String option, @Query("wan.style") String style, @Query("wan.notrestart") String notrestart);
   //无线中继(扫描附近wifi)(/cgi-bin/luci/;stok=1a6483bdfe2278dc35a66f8c1357b48e/admin/home/wan)
    @GET
    Call<wifiScanRoot[]> WirelessRelayScan(@Url String url, @Query("wan.option") String option/*,@Query("wan.notrestart") String notrestart*/);
    @GET
    Call<String> rebootRouter(@Url String url, @Query("reboot") int option);
    @GET
    Call<String> WirelessRelayScanString(@Url String url, @Query("wan.option") String option/*,@Query("wan.notrestart") String notrestart*/);
   //无线中继(链接)(/cgi-bin/luci/;stok=1a6483bdfe2278dc35a66f8c1357b48e/admin/home/wan)
    @GET
    Call<RouterResponse> WirelessRelayConnect(@Url String url,
                                              @Query("wan.option") String option, @Query("wan.style") String style,
                                              @Query("wan.crypt") String crypt, @Query("wan.encryption") String encryption,
                                              @Query("wan.channel") int channel, @Query("wan.wifiName") String wifiName,
                                              @Query("wan.password") String password, @Query("wan.notrestart") String notrestart
    );

}