package com.deplink.homegenius.util;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/17.
 *字符串验证
 */
public class StringValidatorUtil {
    private static final String TAG="StringValidatorUtil";
    /**
     * 是否是手机号码
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        //Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0,6,7,8])|(14[5,7]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    /**
     * 该方法主要使用正则表达式来判断字符串中是否包含字母
     * @author fenggaopan 2015年7月21日 上午9:49:40
     * @param cardNum 待检验的原始卡号
     * @return 返回是否包含
     */
    public static boolean judgeContainsStr(String cardNum) {
        String regex=".*[a-zA-Z]+.*";
        Matcher m=Pattern.compile(regex).matcher(cardNum);
        return m.matches();
    }
    public static boolean isNumeric(String str){
        for (int i = 0; i < str.length(); i++){
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
    /**
     * 是否是邮箱
     * @param mobiles
     * @return
     */
    public static boolean isEmialString(String mobiles) {
        String regex = "\\w+@\\w+(\\.\\w+)+";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    /**
     * Java中字符串中子串的查找共有四种方法，如下：
     1、int indexOf(String str) ：返回第一次出现的指定子字符串在此字符串中的索引。
     2、int indexOf(String str, int startIndex)：从指定的索引处开始，返回第一次出现的指定子字符串在此字符串中的索引。
     3、int lastIndexOf(String str) ：返回在此字符串中最右边出现的指定子字符串的索引。
     4、int lastIndexOf(String str, int startIndex) ：从指定的索引处开始向后搜索，返回在此字符串中最后一次出现的指定子字符串的索引。
     *
     * 是否是IP地址
     * @param ipString
     * @return
     */
    public static boolean isIPString(String ipString) {
        if (appearNumber(ipString, ".") != 3) {
            return false;
        }
        String text1 = ipString.substring(0, ipString.indexOf("."));

        Log.i(TAG, text1);
        if (text1.length() > 3) {
            return false;
        }
        if (text1.equals("") || Integer.valueOf(text1) > 255 ) {
            return false;
        }
        //008 08 这些情况也判定为不合格的地址
        if(Integer.valueOf(text1).toString().length()!=text1.length()){
            return false;
        }
        String cuttedString2 = ipString.substring(ipString.indexOf(".") + 1, ipString.length());
        Log.i(TAG, "cuttedString2=" + cuttedString2);
        String text2 = cuttedString2.substring(0, cuttedString2.indexOf("."));
        Log.i(TAG, text2);
        if (text2.length() > 3) {
            return false;
        }
        if (text2.equals("") || Integer.valueOf(text2) > 255) {
            return false;
        }
        if(Integer.valueOf(text2).toString().length()!=text2.length()){
            return false;
        }
        String cuttedString3 = cuttedString2.substring(cuttedString2.indexOf(".") + 1, cuttedString2.length());
        String text3 = cuttedString3.substring(0, cuttedString3.indexOf("."));
        Log.i(TAG, text3);
        if (text3.length() > 3) {
            return false;
        }
        if (text3.equals("") || Integer.valueOf(text3) > 255) {
            return false;
        }
        if(Integer.valueOf(text3).toString().length()!=text3.length()){
            return false;
        }
        String text4 = ipString.substring(ipString.lastIndexOf(".") + 1, ipString.length());
        Log.i(TAG, text4);
        if(Integer.valueOf(text4).toString().length()!=text4.length()){
            return false;
        }
        return text4.length() <= 3 && !(text4.equals("") || Integer.valueOf(text4) > 255);
    }

    /**
     * public int indexOf(int ch, int fromIndex)
     * 返回在此字符串中第一次出现指定字符处的索引，从指定的索引开始搜索
     *
     * @param srcText
     * @param findText
     * @return
     */
    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        Log.i(TAG,"ip地址字符串出现的.的次数="+count);
        return count;
    }

}
