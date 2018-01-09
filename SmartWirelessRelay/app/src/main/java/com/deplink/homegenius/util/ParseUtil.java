package com.deplink.homegenius.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ${kelijun} on 2018/1/9.
 */
public class ParseUtil {
    /**
     * @param json
     * @param clazz
     * @return
     */
    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);
        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }
    public static String getCityCodeFromCityName(Context context,String provinceName, String cityName) throws XmlPullParserException, IOException {
        String cityCode = null;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        //获取XmlPullParser实例
        XmlPullParser pullParser = factory.newPullParser();
        InputStream in = context.getAssets().open("city_code_data.xml");
        pullParser.setInput(in, "UTF-8");
        //开始
        int eventCode = pullParser.getEventType();
        boolean ifExit = false;
        boolean ifProvinceCatched = false;
        boolean ifCityCatched = false;
        while (eventCode != XmlPullParser.END_DOCUMENT) {
            String nodeName = pullParser.getName();
            switch (eventCode) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if ("Province".equals(nodeName)) {
                        int i = 0;
                        while (i < pullParser.getAttributeCount()) {
                            if (ifProvinceCatched)
                                break;
                            String name = pullParser.getAttributeName(i);
                            String value = pullParser.getAttributeValue(i);
                            if (name.equalsIgnoreCase("name"))
                                if (value.equalsIgnoreCase(provinceName)) {
                                    ifProvinceCatched = true;
                                    break;
                                }
                            i++;
                        }
                    } else if ("City".equals(nodeName)) {
                        int i = 0;
                        String tempCityCode = null;
                        while (i < pullParser.getAttributeCount()) {
                            if (!ifProvinceCatched)
                                break;
                            String name = pullParser.getAttributeName(i);
                            String value = pullParser.getAttributeValue(i);
                            if (name.equalsIgnoreCase("ID")) {
                                tempCityCode = pullParser.getAttributeValue(i);
                            }
                            if (name.equalsIgnoreCase("name")) {
                                if (value.equalsIgnoreCase(cityName)) {
                                    ifCityCatched = true;
                                }
                            }
                            if (ifCityCatched) {
                                cityCode = tempCityCode;
                                ifExit = true;
                                break;
                            }
                            i++;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                default:
                    break;
            }
            if (ifExit)
                break;
            eventCode = pullParser.next();
        }
        return cityCode;
    }
}
