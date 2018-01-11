package com.deplink.sdk.android.sdk.bean;

import java.io.Serializable;

/**
 * Created by huqs on 2016/6/8.
 */
public class User implements Serializable {

        private String token=null;
        private String application_key=null;
        private String name=null;
        private String password=null;
        private String verify_code=null;
        private String avatar=null;
        private String uuid=null;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVerifyCode() {
            return verify_code;
        }

        public void setVerifyCode(String verify_code) {
            this.verify_code = verify_code;
        }

        public String getApplication_key() {
            return application_key;
        }

        public void setApplication_key(String application_key) {
            this.application_key = application_key;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "User{" +
                "token='" + token + '\'' +
                ", application_key='" + application_key + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", verify_code='" + verify_code + '\'' +
                ", avatar='" + avatar + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
