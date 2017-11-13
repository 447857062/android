package deplink.com.smartwirelessrelay.homegenius.manager.room;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;

/**
 * Created by Administrator on 2017/11/13.
 */
public class RoomManager {
    private static final String TAG = "RoomManager";
    /**
     * 这个类设计成单例
     */
    private static RoomManager instance;
    private SQLiteDatabase db;
    private List<Room> mRooms;

    /**
     * 获取按照排序号的房间列表
     *
     * @return
     */
    public List<Room> getmRooms() {
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
        return mRooms;
    }

    /**
     * 拖拽排序后更新房间的排序下标
     *
     * @param from
     * @param to
     * @return
     */
    public boolean sortRooms(int from, int to) {
        Room temp1 = DataSupport.find(Room.class, from);
        Room temp2 = DataSupport.find(Room.class, to);
        //先删除，再交换
        int deleteTo=DataSupport.delete(Room.class, to);
        temp1.setRoomOrdinalNumber(to);
        // rowsAffected
        boolean saveFrom=temp1.save();
        temp2.setRoomOrdinalNumber(from);
        boolean saveTo=temp2.save();
        Log.i(TAG,"deleteTo="+deleteTo+"saveFrom="+saveFrom+"saveTo="+saveTo);
        getmRooms();
        //TODO 返回值
        return false;
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
     * 初始化本地连接管理器
     */
    public void InitRoomManager() {
        //生成数据库
        if (db == null) {
            db = Connector.getDatabase();
        }

    }

    /**
     * 查询数据库获取房间列表
     */
    public void getDatabaseRooms() {
        mRooms = DataSupport.findAll(Room.class);
        if (mRooms.size() == 0) {
            Room temp = new Room();
            temp.setRoomName("客厅");
            temp.setRoomOrdinalNumber(1);
            temp.save();
            mRooms.add(temp);

            temp = new Room();
            temp.setRoomName("卧室");
            temp.setRoomOrdinalNumber(2);
            temp.save();
            mRooms.add(temp);

            temp = new Room();
            temp.setRoomName("厨房");
            temp.setRoomOrdinalNumber(3);
            temp.save();
            mRooms.add(temp);
        }
    }

    /**
     * 根据房间排序号查询房间
     * @param roomIndex
     * @return
     */
    public Room findRoom(int  roomIndex) {
      return DataSupport.find(Room.class,roomIndex);
    }
    /**
     * 根据房间名称
     * 删除房间
     */
    public int deleteRoom(String roomName) {
        int optionResult;
        optionResult = DataSupport.deleteAll(Room.class, "roomName = ? ", roomName);
        getDatabaseRooms();
        Log.i(TAG, "根据房间名称删除房间=" + optionResult);
        return optionResult;
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

    /**
     * 添加房间
     * 新加的房间排序序号都是当前房间加一，默认排在最后面
     *
     * @param roomName
     * @return
     */
    public boolean addRoom(String roomName) {
        Room temp = new Room();
        temp.setRoomName(roomName);

        temp.setRoomOrdinalNumber(mRooms.size() + 1);
        temp.save();
        boolean optionResult;
        optionResult = mRooms.add(temp);
        return optionResult;
    }
}
