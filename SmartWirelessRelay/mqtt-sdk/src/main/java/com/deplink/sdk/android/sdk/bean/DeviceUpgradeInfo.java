package com.deplink.sdk.android.sdk.bean;

/**
 * Created by billy on 2016/8/18.
 */
public class DeviceUpgradeInfo {
    private String product_key;

    private String version;

    private String protocol;

    private String img_url;

    private String bak_protocol;

    private String bak_img_url;

    private long file_len;

    private String file_md5;

    private long upgrade_time;

    private int random_time;

    private String upgrade_state;

    private long finish_time;

    public void setProduct_key(String product_key){
        this.product_key = product_key;
    }
    public String getProduct_key(){
        return this.product_key;
    }
    public void setVersion(String version){
        this.version = version;
    }
    public String getVersion(){
        return this.version;
    }
    public void setProtocol(String protocol){
        this.protocol = protocol;
    }
    public String getProtocol(){
        return this.protocol;
    }
    public void setImg_url(String img_url){
        this.img_url = img_url;
    }
    public String getImg_url(){
        return this.img_url;
    }
    public void setBak_protocol(String bak_protocol){
        this.bak_protocol = bak_protocol;
    }
    public String getBak_protocol(){
        return this.bak_protocol;
    }
    public void setBak_img_url(String bak_img_url){
        this.bak_img_url = bak_img_url;
    }
    public String getBak_img_url(){
        return this.bak_img_url;
    }
    public void setFile_len(long file_len){
        this.file_len = file_len;
    }
    public long getFile_len(){
        return this.file_len;
    }
    public void setFile_md5(String file_md5){
        this.file_md5 = file_md5;
    }
    public String getFile_md5(){
        return this.file_md5;
    }
    public void setUpgrade_time(long update_time) {
        this.upgrade_time = upgrade_time;
    }
    public long getUpgrade_time() {
        return upgrade_time;
    }
    public void setRandom_time(int random_time) {
        this.random_time = random_time;
    }
    public int getRandom_time() {
        return random_time;
    }
    public void setUpgrade_state(String state) {
        upgrade_state = state;
    }
    public String getUpgrade_state() {
        return upgrade_state;
    }
    public void setFinish_time(long time) {
        finish_time = time;
    }
    public long getFinish_time() {
        return finish_time;
    }

    @Override
    public String toString() {
        return "DeviceUpgradeInfo{" +
                "product_key='" + product_key + '\'' +
                ", version='" + version + '\'' +
                ", protocol='" + protocol + '\'' +
                ", img_url='" + img_url + '\'' +
                ", bak_protocol='" + bak_protocol + '\'' +
                ", bak_img_url='" + bak_img_url + '\'' +
                ", file_len=" + file_len +
                ", file_md5='" + file_md5 + '\'' +
                ", upgrade_time=" + upgrade_time +
                ", random_time=" + random_time +
                ", upgrade_state='" + upgrade_state + '\'' +
                ", finish_time=" + finish_time +
                '}';
    }
}
