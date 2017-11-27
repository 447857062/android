package deplink.com.smartwirelessrelay.homegenius.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/7/21.
 */
public class Perfence extends Application {
    private static final String TAG="Perfence";
    public static String PERFENCE_NAME = "PERFENCE_NAME";
    public static String SDK_APP_KEY = "6bbe96ccf8a013eeac65beb76c8b6949";
    //电话
    public static String PERFENCE_PHONE = "PERFENCE_PHONE";
    public static String USER_PASSWORD = "USER_PASSWORD";
    private static SharedPreferences sp;
    private static Context c;
    public static Handler handler;
    public static void setContext(Context context){
        c = context;
        sp = context.getSharedPreferences(PERFENCE_NAME, Activity.MODE_PRIVATE);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(c, msg.obj.toString(), Toast.LENGTH_LONG).show();
            }
        };
    }


    public static float dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }
    public static float px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue -0.5f) /scale;
    }


    public static String getPerfenceDef(String key, String defValue){
        return sp.getString(key, defValue);
    }
    public static String getPerfence(String key){
        return sp.getString(key, "");
    }
    public static boolean getBooleanPerfence(String key){

        return sp.getBoolean(key, false);
    }
    public static int getIntPerfence(String key){

        return sp.getInt(key, -1);
    }
    public static void setPerfence(String key, String value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);

        editor.apply();
        Log.i(TAG,"setPerfence key="+key+"value="+value);
    }
    public static void setPerfence(String key, boolean value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public static void setPerfence(String key, int value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.apply();
    }


}
