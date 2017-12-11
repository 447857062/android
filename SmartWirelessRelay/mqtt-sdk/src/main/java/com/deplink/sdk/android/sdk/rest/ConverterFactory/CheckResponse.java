package com.deplink.sdk.android.sdk.rest.ConverterFactory;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/31.
 */
public class CheckResponse implements Serializable{
    private String  link;
    private String  router;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRouter() {
        return router;
    }

    public void setRouter(String router) {
        this.router = router;
    }

    @Override
    public String toString() {
        return "CheckResponse{" +
                "link='" + link + '\'' +
                ", router='" + router + '\'' +
                '}';
    }
}
