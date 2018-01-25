package com.deplink.homegenius.util;

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
    /**
     * 使用用户格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return
     */

    public static Date parse(String strDate, String pattern) {

        if (TextUtil.isEmpty(strDate)) {
            return null;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用用户格式格式化日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return
     */

    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }
    public static String format(long date) {
        //时间戳转化为Sting或Date
        SimpleDateFormat format =  new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String d = format.format(date);
        return  d;
    }

}
