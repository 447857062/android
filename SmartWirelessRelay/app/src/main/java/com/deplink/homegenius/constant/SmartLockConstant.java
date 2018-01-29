package com.deplink.homegenius.constant;

/**
 * Created by Administrator on 2017/10/31.
 */
public  class SmartLockConstant {
    /**
     * 智能锁uid
     */
    public static final String DEV_UID = "smartuid";
    /**
     *单次授权
     */
    public static final String AUTH_TYPE_ONCE = "Once";
    /**
     *永久授权
     */
    public static final String AUTH_TYPE_PERPETUAL = "Perpetual";
    /**
     *期限授权
     */
    public static final String AUTH_TYPE_TIME_LIMIT = "Time-limited";
    /**
     *开锁
     */
    public static final String OPEN_LOCK = "Open";
    public interface AUTH{
        int TIMEOUT =- 1;
        int SUCCESS = 1;
        int FAIL = 2;
        int FORBADE = 3;
        int PASSWORDERROR = 4;
    }
    public interface OPENLOCK{
        int TIMEOUT =- 1;
        int SUCCESS = 1;
        int PASSWORDERROR = 2;
        int FAIL = 3;

    }
    public interface CMD{
        String OPEN = "Open";
        String ONCE = "Once";
        String PERMANENT = "Perpetual";
        String TIMELIMIT = "Time-limited";
        String QUERY = "query";

    }
}
