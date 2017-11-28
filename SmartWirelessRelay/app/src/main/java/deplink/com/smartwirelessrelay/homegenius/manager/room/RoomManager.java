package deplink.com.smartwirelessrelay.homegenius.manager.room;

import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;

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
        this.currentSelectedRoom = currentSelectedRoom;
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
            Log.i(TAG, "房间" + i + "是：" + mRooms.get(i).toString()+"房间类型="+mRooms.get(i).getRoomType());
            addRoomTypes(mRooms.get(i).getRoomType());
        }
        return mRooms;
    }
    private List<String>roomTypes;

    public List<String> getRoomTypes() {
        Log.i(TAG,"获取房间类型种类="+roomTypes.size());
        for(int i=0;i<roomTypes.size();i++){
            Log.i(TAG,"获取房间类型种类="+roomTypes.get(i));
        }
        return roomTypes;
    }

    public void addRoomTypes(String roomType) {
        if( roomType!=null&& ! roomType.equals("null") && !roomTypes.contains(roomType)){
            roomTypes.add(roomType);
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
        if(roomTypes==null){
            roomTypes=new ArrayList<>();
            roomTypes.add("全部");
        }
    }

    /**
     * 查询数据库获取房间列表
     */
    public List<Room> getDatabaseRooms() {
        mRooms = DataSupport.findAll(Room.class,true);
        if (mRooms.size() == 0) {
            Room temp = new Room();
            temp.setRoomName("客厅的房间");
            temp.setRoomOrdinalNumber(0);
            temp.setRoomType("客厅");
            mRooms.add(temp);

            temp = new Room();
            temp.setRoomName("卧室的房间");
            temp.setRoomType("卧室");
            temp.setRoomOrdinalNumber(1);
            temp.save();
            mRooms.add(temp);

            temp = new Room();
            temp.setRoomName("厨房的房间");
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
     * @param roomName
     * @param  queryRelativeTable
     * @return
     */
    public Room findRoom(String roomName,boolean queryRelativeTable) {
        Room room =DataSupport.where("roomName = ?", roomName).find(Room.class,queryRelativeTable).get(0);
        Log.i(TAG,"根据名字查询房间,查到房间"+room.toString());
        return room;
    }
    private Observable mObservable;
    /**
     * 根据房间名称
     * 删除房间
     * 使用RXjava框架
     */
    public void deleteRoom(final String roomName, final io.reactivex.Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                final int affectColumn = DataSupport.deleteAll(Room.class, "roomName = ? ", roomName);
                mObservable=Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(affectColumn);
                    }
                });
                mObservable.subscribe(observer);
                getDatabaseRooms();
                Log.i(TAG, "根据房间名称删除房间=" +affectColumn);
            }
        });
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
    public void addRoom(final String roomType,final String roomName, final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Room temp = new Room();
                temp.setRoomName(roomName);
                temp.setRoomType(roomType);
                temp.setRoomOrdinalNumber(mRooms.size() + 1);
                final boolean optionResult;
                optionResult =  temp.save();
                mRooms.add(temp);
                mObservable=Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(optionResult);
                        e.onComplete();
                    }
                });
                mObservable.subscribe(observer);
                getDatabaseRooms();
                Log.i(TAG, "添加房间=" +optionResult);
            }
        });
    }
}
