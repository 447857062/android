package deplink.com.smartwirelessrelay.homegenius.manager.device.doorbeel;

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
 * Created by Administrator on 2017/12/6.
 * * private DoorbeelManager mDoorbeelManager;
 * mDoorbeelManager=DoorbeelManager.getInstance();
 * mDoorbeelManager.InitRouterManager(this);
 */
public class DoorbeelManager {
    private static final String TAG = "DoorbeelManager";
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
    /**
     * 这个类设计成单例
     */
    private static DoorbeelManager instance;
    private Context mContext;
    private SmartDev currentSelectedDoorbeel;

    public SmartDev getCurrentSelectedDoorbeel() {
        return currentSelectedDoorbeel;
    }

    public void setCurrentSelectedDoorbeel(SmartDev currentSelectedDoorbeel) {
        this.currentSelectedDoorbeel = currentSelectedDoorbeel;
    }



    public void getDoorbeelAtRooms(final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                SmartDev dev = DataSupport.where("Uid = ?", currentSelectedDoorbeel.getUid()).findFirst(SmartDev.class, true);
                final List<Room> rooms = dev.getRooms();
                mObservable = Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(rooms);
                    }
                });
                mObservable.subscribe(observer);
                Log.i(TAG, "所在房间=" + rooms.size());
            }
        });


    }

    public static synchronized DoorbeelManager getInstance() {
        if (instance == null) {
            instance = new DoorbeelManager();
        }
        return instance;
    }

    public void InitDoorbeelManager(Context context) {
        this.mContext = context;

        if (cachedThreadPool == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
        }

    }

    /**
     * 保存智能门铃到数据库
     *
     * @param dev 路由器
     */
    public void saveDoorbeel(final SmartDev dev, final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                final boolean success = dev.save();
                mObservable = Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(success);
                    }
                });
                mObservable.subscribe(observer);
                Log.i(TAG, "保存智能门铃设备=" + success);
            }
        });

    }

    public void updateDoorbeelName(final String name, final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put("name", name);
                final int affectColumn = DataSupport.updateAll(SmartDev.class, values, "Uid=?", currentSelectedDoorbeel.getUid());
                mObservable = Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(affectColumn);
                    }
                });
                mObservable.subscribe(observer);
                Log.i(TAG, "更新智能门铃名称=" + affectColumn);
            }
        });

    }

    private Observable mObservable;

    public void deleteDoorbeel(final SmartDev dev, final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                final int affectColumn = DataSupport.deleteAll(SmartDev.class, "Uid = ?", dev.getUid());
                mObservable = Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(affectColumn);
                    }
                });
                mObservable.subscribe(observer);
                Log.i(TAG, "删除智能门铃设备=" + affectColumn);
            }
        });
    }

    /**
     * 更新设备所在房间
     *
     * @param room
     * @param sn
     * @param deviceName
     */
    public void updateDeviceInWhatRoom( Room room,  String sn,  String deviceName,  Observer observer) {
                Log.i(TAG, "更新智能门铃设备所在的房间=start");
                //保存所在的房间
                //查询设备
                SmartDev smartDev = DataSupport.where("Uid=?", sn).findFirst(SmartDev.class, true);
                //找到要更行的设备,设置关联的房间
               /* List<Room> roomList = new ArrayList<>();
                roomList.addAll(smartDev.getRoomList());
                roomList.add(room);*/
                List<Room>rooms=new ArrayList<Room>();
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
                Log.i(TAG, "更新智能门铃设备所在的房间=" + saveResult);


    }

}
