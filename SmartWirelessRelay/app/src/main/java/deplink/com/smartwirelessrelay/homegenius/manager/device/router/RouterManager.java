package deplink.com.smartwirelessrelay.homegenius.manager.device.router;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.deplink.sdk.android.sdk.device.RouterDevice;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceTypeConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;

/**
 * Created by Administrator on 2017/11/22.
 * 路由器管理
 * private RouterManager mRouterManager;
 * mRouterManager=RouterManager.getInstance();
 * mRouterManager.InitRouterManager(this);
 */
public class RouterManager {
    private static final String TAG = "RouterManager";
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
    /**
     * 这个类设计成单例
     */
    private static RouterManager instance;
    private Context mContext;
    private SmartDev currentSelectedRouter;

    private RouterDevice routerDevice;

    public SmartDev getCurrentSelectedRouter() {
        return currentSelectedRouter;
    }

    public void setCurrentSelectedRouter(SmartDev currentSelectedRouter) {
        Log.i(TAG, "设置当前选中的路由器 uid=" + currentSelectedRouter.getUid());
        this.currentSelectedRouter = currentSelectedRouter;
        this.currentSelectedRouter.setStatus("在线");

    }

    public void getRouterAtRooms(final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                SmartDev dev = DataSupport.where("Uid = ?", currentSelectedRouter.getUid()).findFirst(SmartDev.class, true);
                final List<Room> rooms = dev.getRooms();
                mObservable = Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(rooms);
                    }
                });
                mObservable.subscribe(observer);
                Log.i(TAG, "所在房间有几个=" + rooms.size());
            }
        });


    }

    public static synchronized RouterManager getInstance() {
        if (instance == null) {
            instance = new RouterManager();
        }
        return instance;
    }

    public void InitRouterManager(Context context) {
        this.mContext = context;
        if (cachedThreadPool == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
        }
    }


    /**
     * 保存路由器到数据库
     *
     * @param dev 路由器
     */
    public boolean saveRouter(SmartDev dev) {
        boolean success = dev.save();
        Log.i(TAG, "保存路由器设备=" + success);
        return success;
    }


    public void updateRouterName(final String name, final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put("name", name);
                final int affectColumn = DataSupport.updateAll(SmartDev.class, values, "Uid=?", currentSelectedRouter.getUid());
                mObservable = Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(affectColumn);
                    }
                });
                mObservable.subscribe(observer);
                Log.i(TAG, "更新路由器名称=" + affectColumn);
            }
        });

    }

    private Observable mObservable;


    /**
     * 更新设备所在房间
     *
     * @param room
     * @param sn
     * @param deviceName
     */
    public void updateDeviceInWhatRoom(Room room, String sn, String deviceName, Observer observer) {

        Log.i(TAG, "更新路由器设备所在的房间=start");
        SmartDev smartDev = DataSupport.where("Uid=?", sn).findFirst(SmartDev.class, true);
        List<Room> rooms = new ArrayList<Room>();
        rooms.clear();
        rooms.add(room);
        smartDev.setRooms(rooms);
        smartDev.setName(deviceName);
        final boolean saveResult = smartDev.save();
        mObservable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                e.onNext(saveResult);
            }
        });
        mObservable.subscribe(observer);
        Log.i(TAG, "更新路由器设备所在的房间=" + saveResult);
    }

    public boolean updateDeviceInWhatRoom(Room room, String sn, String deviceName) {
        Log.i(TAG, "更新路由器设备所在的房间=start");
        SmartDev smartDev = DataSupport.where("Uid=?", sn).findFirst(SmartDev.class, true);
        List<Room> rooms = new ArrayList<>();
        rooms.clear();
        if (room != null) {
            rooms.add(room);
        } else {
            rooms.addAll(RoomManager.getInstance().getmRooms());
        }
        smartDev.setRooms(rooms);
        smartDev.setName(deviceName);
         boolean saveResult = smartDev.save();
        Log.i(TAG, "更新路由器设备所在的房间=" + saveResult);
        return saveResult;
    }


    public String getRouterDeviceKey() {
        String currentDevcieKey = currentSelectedRouter.getRouter().getRouterDeviceKey();
        Log.i(TAG, "获取绑定的路由器设备currentDevcieKey=" + currentDevcieKey);
        return currentDevcieKey;
    }
    public List<SmartDev> getAllRouterDevice() {
        return DataSupport.where("Type = ?", DeviceTypeConstant.TYPE.TYPE_ROUTER).find(SmartDev.class);
    }
}
