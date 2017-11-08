package deplink.com.smartwirelessrelay.homegenius.manager.netStatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import deplink.com.smartwirelessrelay.homegenius.util.NetStatusUtil;
import deplink.com.smartwirelessrelay.homegenius.util.PublicMethod;

/**
 * Created by Administrator on 2017/11/7.
 * 处理网络情况变化的广播,静态广播，程序启动后一直监听.
 * 把网络状态转发出去
 * 使用：
 *只能动态注册，静态广播是没法处理回调的
 * 新建接口onNetStatuschangeListener，或者实现onNetStatuschangeListener接口，然后设置监听.
 */
public class NetStatuChangeReceiver extends BroadcastReceiver{
    private static  final String TAG="NetStatuChangeReceiver";
    public static final int NET_TYPE_WIFI_CONNECTED =0;
    public static final int NET_TYPE_PHONE=1;
    /**
     * 不考虑这种情况，同时开启wifi，手机数据流量，会默认关闭手机流量.
     */
    public static final int NET_TYPE_PHONE_AND_WIFI=2;
    /**
     * 无连接
     */
    public static final int NET_TYPE_NONE=3;
    /**
     * WIFI不可用
     */
    public static final int NET_TYPE_WIFI_DISCONNECTED=4;
    /**
     * 当前的网络情况
     */
    private int currentNetStatu;
    /**
     * 网络状态监听
     */
    private onNetStatuschangeListener mOnNetStatuschangeListener;

    public onNetStatuschangeListener getmOnNetStatuschangeListener() {
        return mOnNetStatuschangeListener;
    }

    public void setmOnNetStatuschangeListener(onNetStatuschangeListener listener) {
        this.mOnNetStatuschangeListener = listener;
        Log.i(TAG,"localconnectmanager set net connect change listener mOnNetStatuschangeListener!=null"
                +(mOnNetStatuschangeListener!=null));
    }
    /**
     * 网络状态监听接口
     */
    public interface onNetStatuschangeListener {
         void onNetStatuChange(int netStatu);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"net status change on receive ");
        String action=intent.getAction();
       if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
           //只监听了网络状态的广播，就不用过滤acton了
           Log.i(TAG,"wifi连接操作 wifi开关="+ NetStatusUtil.isWiFiActive(context)+"手机卡网络="+NetStatusUtil.isNetTypePhoneAvailable(context));
           if(NetStatusUtil.isWiFiActive(context)){
                if(PublicMethod.isNetworkOnline()){
               currentNetStatu= NET_TYPE_WIFI_CONNECTED;
                 }else{
                     currentNetStatu= NET_TYPE_WIFI_DISCONNECTED;
                 }
           }else {
               if(NetStatusUtil.isNetTypePhoneAvailable(context)){
                   currentNetStatu=NET_TYPE_PHONE;
               }else{
                   currentNetStatu=NET_TYPE_NONE;
               }
           }

           if(this.mOnNetStatuschangeListener!=null){
               Log.i(TAG, String.valueOf(PublicMethod.isNetworkOnline()));
               this.mOnNetStatuschangeListener.onNetStatuChange(currentNetStatu);
           }
       }

    }
}
