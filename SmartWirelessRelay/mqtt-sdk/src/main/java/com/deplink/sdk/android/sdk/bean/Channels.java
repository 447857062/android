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
public class Channels {

    private TopicPair common;

    private TopicPair primary;

    private TopicPair secondary;

    @Override
    public String toString() {
        return "{" +
                "\"common\":{" + common +
                "}, \"secondary\":{" + secondary +
                '}';
    }

    public void setCommon(TopicPair common) {
         this.common = common;
     }
     public TopicPair getCommon() {
         return common;
     }

    public void setPrimary(TopicPair primary) {
        this.primary = primary;
    }
    public TopicPair getPrimary() {
        return primary;
    }

    public void setSecondary(TopicPair secondary) {
         this.secondary = secondary;
     }
     public TopicPair getSecondary() {
         return secondary;
     }
}