/**
  * Copyright 2016 aTool.org 
  */
package com.deplink.sdk.android.sdk.bean;

/**
 * Auto-generated: 2016-07-06 12:3:50
 *
 * @author aTool.org (i@aTool.org)
 * @website http://www.atool.org/json2javabean.php
 */
    public class TopicPair {
    private String sub;
    private String pub;
    public void setSub(String sub) {
         this.sub = sub;
     }
     public String getSub() {
         return sub;
     }

    public void setPub(String pub) {
         this.pub = pub;
     }
     public String getPub() {
         return pub;
     }

    @Override
    public String toString() {
        return "{" +
                "\"sub\":" + sub + '\'' +
                ", \"pub\":'" + pub + '\'' +
                '}';
    }
}