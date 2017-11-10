package deplink.com.smartwirelessrelay.homegenius.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/11/10.
 * 日期格式转换
 */
public class DateUtil {
    /**
     *把yyyy-MM-dd HH:mm:ss
     * 格式的字符串转换成日期data对象
     * @param dataString
     * @return
     */
    public static Date transStringTodata(String dataString) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
             date=sdf.parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     *
     * @param date
     * @return
     */
    public static String getYearMothDayStringFromData(Date date) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String datestr=sdf.format(date);
        return datestr;
    }
    /**
     *
     * @param date
     * @return
     */
    public static String getHourMinuteSecondStringFromData(Date date) {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        String datestr=sdf.format(date);
        return datestr;
    }
}
