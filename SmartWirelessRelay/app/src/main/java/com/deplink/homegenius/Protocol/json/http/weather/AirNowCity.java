package com.deplink.homegenius.Protocol.json.http.weather;

/**
 * Created by ${kelijun} on 2018/1/16.
 */
public class AirNowCity {
    private String pm25;

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    @Override
    public String toString() {
        return "AirNowCity{" +
                "pm25='" + pm25 + '\'' +
                '}';
    }
}
