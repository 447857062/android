package com.deplink.homegenius.Protocol.json.http.weather;

/**
 * Created by ${kelijun} on 2018/1/16.
 */
public class WeatherInfo {
    private Now now;
    private AirNowCity air_now_city;
    private String status;
    public AirNowCity getAir_now_city() {
        return air_now_city;
    }

    public void setAir_now_city(AirNowCity air_now_city) {
        this.air_now_city = air_now_city;
    }

    public Now getNow() {
        return now;
    }

    public void setNow(Now now) {
        this.now = now;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
