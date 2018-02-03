package com.deplink.homegenius.Protocol.json.http.weather;

import java.util.List;

/**
 * Created by ${kelijun} on 2018/1/16.
 */
public class HeWeather6 {
    private List<WeatherInfo>HeWeather6;
    public List<WeatherInfo> getInfoList() {
        return HeWeather6;
    }

    public void setInfoList(List<WeatherInfo> infoList) {
        this.HeWeather6 = infoList;
    }

    @Override
    public String toString() {
        return "HeWeather6{" +
                "HeWeather6=" + HeWeather6 +
                '}';
    }
}
