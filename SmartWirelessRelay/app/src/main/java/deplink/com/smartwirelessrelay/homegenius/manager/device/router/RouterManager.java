package deplink.com.smartwirelessrelay.homegenius.manager.device.router;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;

/**
 * Created by Administrator on 2017/11/22.
 */
public class RouterManager  {
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
    /**
     * 添加设备时候，要往那个房间添加
     */
    private  String currentAddRoom;
    private SmartDev currentSelectedRouter;
    public String getCurrentAddRoom() {
        Log.i(TAG,"getCurrentAddRoom:"+currentAddRoom);
        return currentAddRoom;
    }

    public void setCurrentAddRoom(String currentAddRoom) {
        Log.i(TAG,"setCurrentAddRoom:"+currentAddRoom);
        this.currentAddRoom = currentAddRoom;
    }

    public SmartDev getCurrentSelectedRouter() {
        return currentSelectedRouter;
    }

    public void setCurrentSelectedRouter(SmartDev currentSelectedRouter) {
        Log.i(TAG,"设置当前选中的路由器 uid="+currentSelectedRouter.getUid());
        this.currentSelectedRouter = currentSelectedRouter;
    }

    public static synchronized RouterManager getInstance() {
        if (instance == null) {
            instance = new RouterManager();
        }
        return instance;
    }
    public void InitRouterManager(Context context) {
        this.mContext = context;

        if(cachedThreadPool==null){
            cachedThreadPool = Executors.newCachedThreadPool();
        }

    }

    /**
     * 保存路由器到数据库
     * @param dev 路由器
     */
    public void saveRouter(final SmartDev dev,final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                final boolean success = dev.save();
                mObservable=Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(success);
                    }
                });
                mObservable.subscribe(observer);
                Log.i(TAG, "保存路由器设备=" + success);
            }
        });

    }
    public void updateRouterName(final String name, final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ContentValues values=new ContentValues();
                values.put("name",name);
                final int affectColumn=DataSupport.updateAll(SmartDev.class,values,"Uid=?",currentSelectedRouter.getUid());
                mObservable=Observable.create(new ObservableOnSubscribe() {
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
    public void deleteRouter(final SmartDev dev, final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                final int affectColumn=DataSupport.deleteAll(SmartDev.class, "Uid = ?", dev.getUid());
                mObservable=Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(affectColumn);
                    }
                });
                mObservable.subscribe(observer);
                Log.i(TAG, "删除路由器设备=" + affectColumn);
            }
        });
    }

    /**
     * 更新设备所在房间
     * @param room
     * @param sn
     * @param deviceName
     */
    public void updateDeviceInWhatRoom(final Room room, final String sn, final String deviceName,final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "更新路由器设备所在的房间=start");
                //保存所在的房间
                //查询设备
                SmartDev smartDev = DataSupport.where("Uid=?", sn).findFirst(SmartDev.class, true);
                //找到要更行的设备,设置关联的房间
                List<Room> roomList = new ArrayList<>();
                roomList.addAll(smartDev.getRoomList());
                roomList.add(room);
                smartDev.setRoomList(roomList);
                smartDev.setName(deviceName);
                final boolean saveResult = smartDev.save();
                mObservable=Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(saveResult);
                    }
                });
                mObservable.subscribe(observer);
                Log.i(TAG, "更新路由器设备所在的房间=" + saveResult);
            }
        });

    }
}
