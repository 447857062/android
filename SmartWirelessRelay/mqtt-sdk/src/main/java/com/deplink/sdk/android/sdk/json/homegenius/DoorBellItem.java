package com.deplink.sdk.android.sdk.json.homegenius;

/**
 * Created by ${kelijun} on 2018/1/16.
 */
public class DoorBellItem {
    private long timestamp;
    private String  file;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
