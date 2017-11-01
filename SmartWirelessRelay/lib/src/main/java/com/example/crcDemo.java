package com.example;

/**
 * Created by Administrator on 2017/10/31.
 */
public class crcDemo {
    public static void main(String[] args) {
        String testStr = "{\"log\":{\"content\":\"2\",\"time\":\"2016-01-05 10:17:24\",\"type\":1001,\"version\":\"[5.0.8.12]\"},\"pcInfo\":{\"ip\":\"192.168.118.57\",\"mac\":\"94-DE-80-A8-E6-EC\",\"onlyId\":\"7CE81DDBF7D05F6AD89CD7D79FAA5905\"},\"user\":{\"name\":\"CFM\"}}";
        java.util.zip.CRC32 jdkCrc32 = new java.util.zip.CRC32();
        jdkCrc32.update(testStr.getBytes());
        System.out.println("jdk  crc32: " + jdkCrc32.getValue());
        System.out.println("test crc32: " + CustomerCRC32.getCrc32(testStr.getBytes()));
        //使用jdk的左移32位
    }
}
