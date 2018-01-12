package com.deplink.homegenius.manager.room;

import com.deplink.homegenius.Protocol.json.Room;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */
public interface RoomListener {
    /**
     *返回查询结果
     */
    void responseQueryResultHttps(List<Room> result);
    void responseAddRoomResult(String result);
    void responseDeleteRoomResult();
    void responseUpdateRoomNameResult();
}
