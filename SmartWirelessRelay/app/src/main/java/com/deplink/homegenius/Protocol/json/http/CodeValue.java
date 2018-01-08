package com.deplink.homegenius.Protocol.json.http;

/**
 * Created by Administrator on 2017/11/22.
 */
public class CodeValue {
    private String code;
    private int group;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "CodeValue{" +
                "code='" + code + '\'' +
                ", group=" + group +
                '}';
    }
}
