package deplink.com.smartwirelessrelay.homegenius.manager.device.router;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import org.litepal.crud.DataSupport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.toast.ToastSingleShow;
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

    public SmartDev getCurrentSelectedRouter() {
        return currentSelectedRouter;
    }

    public void setCurrentSelectedRouter(SmartDev currentSelectedRouter) {
        Log.i(TAG, "设置当前选中的路由器 uid=" + currentSelectedRouter.getUid());
        this.currentSelectedRouter = currentSelectedRouter;
    }

    public void getRouterAtRooms(final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                SmartDev dev = DataSupport.where("Uid = ?", currentSelectedRouter.getUid()).findFirst(SmartDev.class, true);
                final Room rooms = dev.getRoom();
                mObservable = Observable.create(new ObservableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter e) throws Exception {
                        e.onNext(rooms);
                    }
                });
                mObservable.subscribe(observer);
                Log.i(TAG, "所在房间=" + rooms.getRoomName());
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
        if (manager == null) {
            DeplinkSDK.initSDK(mContext.getApplicationContext(), Perfence.SDK_APP_KEY);
            manager = DeplinkSDK.getSDKManager();
        }
        if (cachedThreadPool == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
        }

    }

    /**
     * 保存路由器到数据库
     *
     * @param dev 路由器
     */
    public void saveRouter(final SmartDev dev, final Observer observer) {
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
                Log.i(TAG, "保存路由器设备=" + success);
            }
        });

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

    public void deleteRouter(final SmartDev dev, final Observer observer) {
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
                Log.i(TAG, "删除路由器设备=" + affectColumn);
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
    public void updateDeviceInWhatRoom(final Room room, final String sn, final String deviceName, final Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "更新路由器设备所在的房间=start");
                //保存所在的房间
                //查询设备
                SmartDev smartDev = DataSupport.where("Uid=?", sn).findFirst(SmartDev.class, true);
                //找到要更行的设备,设置关联的房间
             /*   List<Room> roomList = new ArrayList<>();
                roomList.addAll(smartDev.getRoomList());
                roomList.add(room);*/
                smartDev.setRoom(room);
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
        });

    }

    private RouterDevice routerDevice;
    private SDKManager manager;

    public RouterDevice getRouterDevice() {
        String currentDevcieKey = Perfence.getPerfence(AppConstant.DEVICE.CURRENT_DEVICE_KEY);
        Log.i(TAG, "获取绑定的路由器设备currentDevcieKey=" + currentDevcieKey);
        if (currentDevcieKey.equals("")) {
            if (manager.getDeviceList() != null && manager.getDeviceList().size() != 0) {
                Perfence.setPerfence(AppConstant.DEVICE.CURRENT_DEVICE_KEY, manager.getDeviceList().get(0).getDeviceKey());
            } else {
                ToastSingleShow.showText(mContext, "还没有绑定设备");
            }
        }
        Log.i(TAG, "获取绑定的路由器设备currentDevcieKey=" + Perfence.getPerfence(AppConstant.DEVICE.CURRENT_DEVICE_KEY));
        routerDevice = (RouterDevice) manager.getDevice(Perfence.getPerfence(AppConstant.DEVICE.CURRENT_DEVICE_KEY));
        return routerDevice;
    }
}
