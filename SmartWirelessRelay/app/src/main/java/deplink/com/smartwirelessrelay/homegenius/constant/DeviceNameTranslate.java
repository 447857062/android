package deplink.com.smartwirelessrelay.homegenius.constant;

/**
 * Created by Administrator on 2017/10/31.
 */
public class DeviceNameTranslate {
    private static final String DEV_TYPE_SMARTLOCK = "SMART_LOCK";
    private static final String DEV_TYPE_SMARTLOCK_TRANSLATED_NAME = "智能密码门锁";


    public static String getDeviceTranslatedName(String origName) {
        switch (origName) {
            case DEV_TYPE_SMARTLOCK:
                return DEV_TYPE_SMARTLOCK_TRANSLATED_NAME;
        }
        return "未翻译智能设备";
    }

}
