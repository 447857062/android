package com.deplink.homegenius.manager.room;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.getway.Device;
import com.deplink.homegenius.util.CharSetUtil;
import com.deplink.homegenius.util.NetUtil;
import com.deplink.homegenius.util.ParseUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.RoomUpdateName;
import com.deplink.sdk.android.sdk.rest.RestfulToolsHomeGenius;
import com.deplink.sdk.android.sdk.rest.RestfulToolsHomeGeniusString;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private Context mContext;

    /**
     * 获取房间列表
     *
     * @return
     */
    public List<Room> getmRooms() {

        return mRooms;
    }

    private List<RoomListener> mRoomListenerList;

    public void addRoomListener(RoomListener listener) {
        if (listener != null && !mRoomListenerList.contains(listener)) {
            this.mRoomListenerList.add(listener);
        }
    }

    public void removeRoomListener(RoomListener listener) {
        if (listener != null && mRoomListenerList.contains(listener)) {
            this.mRoomListenerList.remove(listener);
        }
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
        currentSelectedRoom = null;
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
     * 查询房间列表
     */
    public void queryRoomListHttp() {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (!NetUtil.isNetAvailable(mContext)) {
            ToastSingleShow.showText(mContext, "网络连接不正常");
            return;
        }
        if (userName.equals("")) {
            ToastSingleShow.showText(mContext, "用户未登录");
            return;
        }
        RestfulToolsHomeGeniusString.getSingleton(mContext).getRoomInfo(userName, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "" + response.code());
                Log.i(TAG, "" + response.message());
                Log.i(TAG, "" + response.body());
                if (response.code() == 400) {
                    //Bad Request
                } else {
                    if (!response.body().contains("errcode")) {
                        ArrayList<com.deplink.sdk.android.sdk.homegenius.Room> list = ParseUtil.jsonToArrayList(response.body(), com.deplink.sdk.android.sdk.homegenius.Room.class);
                        Room temp;

                        for (int i = 0; i < list.size(); i++) {
                            Log.i(TAG, "roomname=" + CharSetUtil.decodeUnicode(list.get(i).getRoom_name()));
                            Log.i(TAG, "roomtype=" + CharSetUtil.decodeUnicode(list.get(i).getRoom_type()));
                            temp = new Room();
                            boolean addToDb = true;
                            for (int j = 0; j < mRooms.size(); j++) {
                                if (list.get(i).getUid().equalsIgnoreCase(mRooms.get(j).getUid())) {
                                    addToDb = false;
                                }
                            }

                            if (addToDb) {
                                temp.setRoomName(CharSetUtil.decodeUnicode(list.get(i).getRoom_name()));
                                temp.setRoomOrdinalNumber(i);
                                temp.setRoomType(CharSetUtil.decodeUnicode(list.get(i).getRoom_type()));
                                temp.setUid(list.get(i).getUid());
                                temp.save();
                                mRooms.add(temp);
                            }

                        }
                        List<Room> rooms = sortRooms();
                        for (int i = 0; i < mRoomListenerList.size(); i++) {
                            mRoomListenerList.get(i).responseQueryResult(rooms);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG, "" + t.getMessage() + t.toString());
            }
        });
    }

    /**
     * 添加房间
     */
    public void addRoomHttp(String roomName, String roomType) {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            ToastSingleShow.showText(mContext, "用户未登录");
            return;
        }
        com.deplink.sdk.android.sdk.homegenius.Room room = new com.deplink.sdk.android.sdk.homegenius.Room();
        room.setRoom_name(roomName);
        room.setRoom_type(roomType);
        RestfulToolsHomeGenius.getSingleton(mContext).addRomm(userName, room, new Callback<DeviceOperationResponse>() {
            @Override
            public void onResponse(Call<DeviceOperationResponse> call, Response<DeviceOperationResponse> response) {
                Log.i(TAG, "" + response.code());
                if (response.code() == 200) {
                    Log.i(TAG, "" + response.message());
                    Log.i(TAG, "" + response.body());
                    DeviceOperationResponse result = response.body();
                    if (result.getStatus().equalsIgnoreCase("ok")) {
                        for (int i = 0; i < mRoomListenerList.size(); i++) {
                            mRoomListenerList.get(i).responseAddRoomResult(result.getUid());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DeviceOperationResponse> call, Throwable t) {
                Log.i(TAG, "" + t.getMessage() + t.toString());
            }
        });
    }

    /**
     * 删除房间
     */
    public void deleteRoomHttp(String roomUid) {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            ToastSingleShow.showText(mContext, "用户未登录");
            return;
        }
        RestfulToolsHomeGenius.getSingleton(mContext).deleteRomm(userName, roomUid, new Callback<DeviceOperationResponse>() {
            @Override
            public void onResponse(Call<DeviceOperationResponse> call, Response<DeviceOperationResponse> response) {
                Log.i(TAG, "" + response.code());
                Log.i(TAG, "" + response.message());
                Log.i(TAG, "" + response.body());
                if (response.code() == 200) {
                    for (int i = 0; i < mRoomListenerList.size(); i++) {
                        mRoomListenerList.get(i).responseDeleteRoomResult();
                    }
                }
            }

            @Override
            public void onFailure(Call<DeviceOperationResponse> call, Throwable t) {
                Log.i(TAG, "" + t.getMessage() + t.toString());
            }
        });
    }
    /**
     * 删除房间
     */
    public void updateRoomNameHttp(String roomUid,String roomName,int sort_num) {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            ToastSingleShow.showText(mContext, "用户未登录");
            return;
        }
        RoomUpdateName roomUpdateName=new RoomUpdateName();
        roomUpdateName.setRoom_uid(roomUid);
        roomUpdateName.setRoom_name(roomName);
        roomUpdateName.setSort_num(sort_num);
        RestfulToolsHomeGenius.getSingleton(mContext).updateRoomName(userName, roomUpdateName, new Callback<DeviceOperationResponse>() {
            @Override
            public void onResponse(Call<DeviceOperationResponse> call, Response<DeviceOperationResponse> response) {
                Log.i(TAG, "" + response.code());
                Log.i(TAG, "" + response.message());
                if(response.errorBody()!=null){
                    try {
                        Log.i(TAG, "" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.code() == 200) {
                    Log.i(TAG, "" + response.body());
                    for (int i = 0; i < mRoomListenerList.size(); i++) {
                        mRoomListenerList.get(i).responseUpdateRoomNameResult();
                    }
                }
            }

            @Override
            public void onFailure(Call<DeviceOperationResponse> call, Throwable t) {
                Log.i(TAG, "" + t.getMessage() + t.toString());
            }
        });
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
    public void initRoomManager(Context context, RoomListener listener) {
        cachedThreadPool = Executors.newCachedThreadPool();
        this.mContext = context;
        this.mRoomListenerList = new ArrayList<>();
        if (listener != null) {
            addRoomListener(listener);
        }
        if (roomNames == null) {
            roomNames = new ArrayList<>();
            roomNames.add("全部");
        }
    }

    /**
     * 查询数据库获取房间列表
     */
    public List<Room> queryRooms() {
        mRooms = DataSupport.findAll(Room.class, true);
        queryRoomListHttp();
        return mRooms;
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
        queryRooms();
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
        queryRooms();
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
    public boolean addRoom(String roomType, String roomName, String roomUid, Device gewayDevice) {
        tempAddRoom = new Room();
        tempAddRoom.setRoomName(roomName);
        tempAddRoom.setRoomType(roomType);
        tempAddRoom.setUid(roomUid);
        tempAddRoom.setRoomOrdinalNumber(mRooms.size() + 1);
        if (gewayDevice != null) {
            List<Device> devices = new ArrayList<>();
            devices.add(gewayDevice);
            tempAddRoom.setmGetwayDevices(devices);
        }
        final boolean optionResult;
        optionResult = tempAddRoom.save();
        mRooms.add(tempAddRoom);
        queryRooms();
        Log.i(TAG, "添加房间=" + optionResult);
        return optionResult;
    }

}
