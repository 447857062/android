package com.deplink.sdk.android.sdk.bean;

/**
 * Created by billy on 2016/8/17.
 */
public class DeviceCookieItem {
    private int id;
    private String tag;
    private String content;
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return tag;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }
}
