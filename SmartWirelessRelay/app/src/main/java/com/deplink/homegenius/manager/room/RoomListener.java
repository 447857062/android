package com.deplink.homegenius.manager.room;

import com.deplink.homegenius.Protocol.json.Room;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */
public  abstract class RoomListener {
    /**
     *返回查询结果
     */
    public void responseQueryResultHttps(List<Room> result){

    };
    public void responseAddRoomResult(String result){

    };
    public void responseDeleteRoomResult(){

    };
    public void responseUpdateRoomNameResult(){

    };
}
