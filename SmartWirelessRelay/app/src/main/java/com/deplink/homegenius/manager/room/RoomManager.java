package com.deplink.homegenius.manager.room;

import android.content.ContentValues;
import android.util.Log;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.getway.Device;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2017/11/13.
 */
public class RoomManager {
    private static final String TAG = "RoomManager";
    /**
     * 这个类设计成单例
     */
    private static RoomManager instance;
    private List<Room> mRooms;
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
    private Room currentSelectedRoom;

    /**
     * 获取房间列表
     *
     * @return
     */
    public List<Room> getmRooms() {

        return mRooms;
    }

    public Room getCurrentSelectedRoom() {
        return currentSelectedRoom;
    }

    public void setCurrentSelectedRoom(Room currentSelectedRoom) {
        if (currentSelectedRoom != null) {
            this.currentSelectedRoom = currentSelectedRoom;
            Log.i(TAG, "设置当前房间=" + currentSelectedRoom.getRoomName());
        } else {
            Log.i(TAG, "设置当前房间=" + tempAddRoom.getRoomName());
            this.currentSelectedRoom = tempAddRoom;
        }

    }
    public void skipSelectedRoom() {
         currentSelectedRoom=null;
    }
    public boolean updateGetway(Device getwayDevice) {
        Log.i(TAG, "更新网关=start");
        List<Device> getways = new ArrayList<>();
        getways.add(getwayDevice);
        currentSelectedRoom.setmGetwayDevices(getways);
        boolean saveResult = currentSelectedRoom.save();
        Log.i(TAG, "更新网关=" + saveResult);
        return saveResult;
    }

    /**
     * 按照序号排序
     */
    public List<Room> sortRooms() {
        Collections.sort(mRooms, new Comparator<Room>() {
            @Override
            public int compare(Room o1, Room o2) {
                //compareTo就是比较两个值，如果前者大于后者，返回1，等于返回0，小于返回-1
                if (o1.getRoomOrdinalNumber() == o2.getRoomOrdinalNumber()) {
                    return 0;
                }
                if (o1.getRoomOrdinalNumber() > o2.getRoomOrdinalNumber()) {
                    return 1;
                }
                if (o1.getRoomOrdinalNumber() < o2.getRoomOrdinalNumber()) {
                    return -1;
                }
                return 0;
            }
        });
        for (int i = 0; i < mRooms.size(); i++) {
            addRoomNames(mRooms.get(i).getRoomName());
        }
        return mRooms;
    }

    private List<String> roomNames;

    public List<String> getRoomNames() {

        return roomNames;
    }

    public void addRoomNames(String roomType) {
        if (!roomNames.contains(roomType)) {
            roomNames.add(roomType);
        }
    }

    private RoomManager() {
    }

    public static synchronized RoomManager getInstance() {
        if (instance == null) {
            instance = new RoomManager();
        }
        return instance;
    }

    /**
     * 更新房间的排列顺序
     * 拖动排序的表格布局中，如果拖动了就要使用这个方法，重新为gridview按照设备的序号排列一下
     */
    public void updateRoomsOrdinalNumber() {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mRooms.size(); i++) {
                    mRooms.get(i).setRoomOrdinalNumber(i);
                    //如果对象是持久化的，执行save操作就相当于更新这条数据，如：
                    //如果一个对象是没有持久化的，执行save操作相当于新增一条数据
                    mRooms.get(i).save();
                }
            }
        });


    }

    /**
     * 初始化本地连接管理器
     */
    public void initRoomManager() {
        cachedThreadPool = Executors.newCachedThreadPool();
        if (roomNames == null) {
            roomNames = new ArrayList<>();
            roomNames.add("全部");
        }
    }

    /**
     * 查询数据库获取房间列表
     */
    public List<Room> getDatabaseRooms() {
        mRooms = DataSupport.findAll(Room.class, true);
        if (mRooms.size() == 0) {
            Room temp = new Room();
            temp.setRoomName("客厅");
            temp.setRoomOrdinalNumber(0);
            temp.setRoomType("客厅");
            temp.save();
            mRooms.add(temp);

            temp = new Room();
            temp.setRoomName("卧室");
            temp.setRoomType("卧室");
            temp.setRoomOrdinalNumber(1);
            temp.save();
            mRooms.add(temp);

            temp = new Room();
            temp.setRoomName("厨房");
            temp.setRoomType("厨房");
            temp.setRoomOrdinalNumber(2);
            temp.save();
            mRooms.add(temp);
        }
        return sortRooms();
    }

    /**
     * 根据房间排序号查询房间
     *
     * @param roomIndex
     * @return
     */
    public Room findRoom(int roomIndex) {
        Room room = DataSupport.find(Room.class, roomIndex);
        if (room != null) {
            Log.i(TAG, "查找房间 :" + room.toString());
        } else {
            Log.i(TAG, "查找房间 : 未找到");
        }

        return room;
    }

    /**
     * 按照房间名字插叙房间
     * 关联表中数据是无法查到的，因为LitePal默认的模式就是懒查询，当然这也是推荐的查询方式。
     * 那么，如果你真的非常想要一次性将关联表中的数据也一起查询出来，当然也是可以的，
     * LitePal中也支持激进查询的
     *
     * @param roomName
     * @param queryRelativeTable
     * @return
     */
    public Room findRoom(String roomName, boolean queryRelativeTable) {
        List<Room> rooms = DataSupport.where("roomName = ?", roomName).find(Room.class, queryRelativeTable);
        Room room = null;
        if (rooms.size() > 0) {
            room = rooms.get(0);
            Log.i(TAG, "根据名字查询房间,查到房间" + room.toString());
        }
        return room;
    }

    public Room findRoomByType(String typeName, boolean queryRelativeTable) {
        List<Room> rooms = DataSupport.where("roomType = ?", typeName).find(Room.class, queryRelativeTable);
        Room room = null;
        if (rooms.size() > 0) {
            room = rooms.get(0);
        }
        Log.i(TAG, "根据类型查询房间,查到房间" + room.toString());
        return room;
    }

    private Observable mObservable;

    /**
     * 根据房间名称
     * 删除房间
     * 使用RXjava框架
     */
    public int deleteRoom(String roomName) {
        int affectColumn = DataSupport.deleteAll(Room.class, "roomName = ? ", roomName);
        getDatabaseRooms();
        Log.i(TAG, "根据房间名称删除房间=" + affectColumn);
        return affectColumn;
    }

    public int updateRoomName(String oriName, String roomName) {
        ContentValues values = new ContentValues();
        values.put("roomName", roomName);
        return DataSupport.updateAll(Room.class, values, "roomName = ?", oriName);
    }

    /**
     * 按照房间排序删除房间
     * 序号是唯一的，不为空
     */
    public int deleteRoom(int roomOrdinalNumber) {
        int optionResult;
        optionResult = DataSupport.delete(Room.class, roomOrdinalNumber);
        getDatabaseRooms();
        Log.i(TAG, "按照房间排序删除房间=" + optionResult);
        return optionResult;
    }


    private Room tempAddRoom;

    /**
     * 添加房间
     * 新加的房间排序序号都是当前房间加一，默认排在最后面
     *
     * @param roomName
     * @return
     */
    public boolean addRoom(String roomType, String roomName, Device gewayDevice) {
        tempAddRoom = new Room();
        tempAddRoom.setRoomName(roomName);
        tempAddRoom.setRoomType(roomType);
        tempAddRoom.setRoomOrdinalNumber(mRooms.size() + 1);
        if(gewayDevice!=null){
            List<Device> devices = new ArrayList<>();
            devices.add(gewayDevice);
            tempAddRoom.setmGetwayDevices(devices);
        }
        final boolean optionResult;
        optionResult = tempAddRoom.save();
        mRooms.add(tempAddRoom);
        getDatabaseRooms();
        Log.i(TAG, "添加房间=" + optionResult);
        return optionResult;
    }

}
