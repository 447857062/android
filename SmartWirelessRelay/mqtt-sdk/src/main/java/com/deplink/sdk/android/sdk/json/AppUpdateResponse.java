package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/18.
 */
public class AppUpdateResponse implements Serializable{
    private String version;
    private String desc;
    private String protocol;
    private String download_url;
    private String protocol2;
    private String download_url2;
    private int size;
    private String md5;
    private String create_time;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getProtocol2() {
        return protocol2;
    }

    public void setProtocol2(String protocol2) {
        this.protocol2 = protocol2;
    }

    public String getDownload_url2() {
        return download_url2;
    }

    public void setDownload_url2(String download_url2) {
        this.download_url2 = download_url2;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    @Override
    public String toString() {
        return "AppUpdateResponse{" +
                "version='" + version + '\'' +
                ", desc='" + desc + '\'' +
                ", protocol='" + protocol + '\'' +
                ", download_url='" + download_url + '\'' +
                ", protocol2='" + protocol2 + '\'' +
                ", download_url2='" + download_url2 + '\'' +
                ", size=" + size +
                ", md5='" + md5 + '\'' +
                ", create_time='" + create_time + '\'' +
                '}';
    }
}
