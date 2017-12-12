package deplink.com.smartwirelessrelay.homegenius.manager.device.router;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.EventCallback;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.device.BaseDevice;
import com.deplink.sdk.android.sdk.device.RouterDevice;
import com.deplink.sdk.android.sdk.manager.SDKManager;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.MakeSureDialog;
import deplink.com.smartwirelessrelay.homegenius.view.dialog.loadingdialog.DialogThreeBounce;
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
    private SmartDev currentAddRouter;
    private RouterDevice routerDevice;
    private SDKManager manager;
    private EventCallback ec;
    private boolean isUserLogin;
    private boolean isConnectedMqtt;
    private boolean isUserBindAction = false;
    //add router
    private String routerSN;
    private String routerName;
    private MakeSureDialog connectLostDialog;
    private List<RouterManagerListener> mRouterManagerListenerList;
    private boolean isStartFromExperience;

    public boolean isStartFromExperience() {
        return isStartFromExperience;
    }

    public void setStartFromExperience(boolean startFromExperience) {
        isStartFromExperience = startFromExperience;
    }

    public void addSmartLockListener(RouterManagerListener listener) {

        if (listener != null && !mRouterManagerListenerList.contains(listener)) {
            Log.i(TAG, "addRouterManagerListener=" + listener.toString());
            this.mRouterManagerListenerList.add(listener);
        }
    }

    public void removeSmartLockListener(RouterManagerListener listener) {
        if (listener != null && mRouterManagerListenerList.contains(listener)) {
            this.mRouterManagerListenerList.remove(listener);
        }

    }

    public boolean isUserLogin() {
        return isUserLogin;
    }

    public void setUserLogin(boolean userLogin) {
        isUserLogin = userLogin;
    }

    public boolean isConnectedMqtt() {
        return isConnectedMqtt;
    }

    public boolean isUserBindAction() {
        return isUserBindAction;
    }

    public void setUserBindAction(boolean userBindAction) {
        isUserBindAction = userBindAction;
    }

    public String getRouterSN() {
        return routerSN;
    }

    public void setRouterSN(String routerSN) {
        this.routerSN = routerSN;
    }

    public String getRouterName() {
        return routerName;
    }

    public void setRouterName(String routerName) {
        this.routerName = routerName;
    }

    public void setConnectedMqtt(boolean connectedMqtt) {
        isConnectedMqtt = connectedMqtt;
    }

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

    public void bindDevice(String routerSN) {
        if (manager != null) {
            manager.bindDevice(routerSN);
        }

    }

    public static synchronized RouterManager getInstance() {
        if (instance == null) {
            instance = new RouterManager();
        }
        return instance;
    }

    public void InitRouterManager(Context context) {
        this.mContext = context;
        this.mRouterManagerListenerList = new ArrayList<>();
        connectLostDialog = new MakeSureDialog(mContext);
        DeplinkSDK.initSDK(mContext.getApplicationContext(), Perfence.SDK_APP_KEY);

        ec = new EventCallback() {
            @Override
            public void onSuccess(SDKAction action) {
                switch (action) {
                    case GET_BINDING:
                        Log.i(TAG, "status GET_BINDING");
                        if (isUserBindAction) {
                            isUserBindAction = false;
                            for (int i = 0; i < manager.getDeviceList().size(); i++) {
                                //查询设备列表，sn和上传时一样才修改名字
                                if (manager.getDeviceList().get(i).getDeviceSN().equals(routerSN)) {
                                    Log.i(TAG, "manager.getDeviceList().get(i).getDeviceSN()=" + manager.getDeviceList().get(i).getDeviceSN() + "bindDeviceSn=" + routerSN + "changename");
                                    currentAddRouter = new SmartDev();
                                    currentAddRouter.setRouterDeviceKey(manager.getDeviceList().get(i).getDeviceKey());
                                    ((RouterDevice) manager.getDeviceList().get(i)).changeName(routerName);
                                }
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
            @Override
            public void onBindSuccess(SDKAction action, String devicekey) {
            }
            @Override
            public void onGetImageSuccess(SDKAction action, Bitmap bm) {

            }

            @Override
            public void deviceOpSuccess(String op, final String deviceKey) {
                super.deviceOpSuccess(op, deviceKey);
                switch (op) {
                    case RouterDevice.OP_CHANGE_NAME:
                        currentAddRouter.setUid(routerSN);
                        currentAddRouter.setType("路由器");
                        boolean saveResult = saveRouter(currentAddRouter);
                        if (!saveResult) {
                            for (int i = 0; i < mRouterManagerListenerList.size(); i++) {
                                mRouterManagerListenerList.get(i).responseAddyResult(100);//MSG_ADD_ROUTER_FAILED
                            }
                        } else {
                            Room room = RoomManager.getInstance().getCurrentSelectedRoom();

                            Log.i(TAG, "添加设备此处的房间是=" + room.getRoomName());
                            updateDeviceInWhatRoom(room, routerSN, routerName);


                        }

                        break;

                    case RouterDevice.OP_GET_REPORT:

                        break;

                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
                super.connectionLost(throwable);
                isConnectedMqtt = false;
                isUserLogin = false;
                Perfence.setPerfence(AppConstant.USER_LOGIN, false);
                connectLostDialog.show();
                connectLostDialog.setTitleText("账号异地登录");
                connectLostDialog.setMsg("当前账号已在其它设备上登录,是否重新登录");

            }

            @Override
            public void onFailure(SDKAction action, Throwable throwable) {
                switch (action) {
                    case GET_BINDING:
                        if (isUserBindAction) {
                            for (int i = 0; i < mRouterManagerListenerList.size(); i++) {
                                mRouterManagerListenerList.get(i).responseAddyResult(100);//MSG_ADD_ROUTER_FAIL
                            }
                        }
                        break;

                }
            }
        };
        if(manager==null){
            manager = DeplinkSDK.getSDKManager();
            Log.i(TAG, "InitRouterManager ,init sdk manager"); manager.addEventCallback(ec);
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
    public boolean saveRouter(SmartDev dev) {
        boolean success = dev.save();
        Log.i(TAG, "保存路由器设备=" + success);
        return success;
    }

    public void onDestroy() {
        DeplinkSDK.getSDKManager().onDestroy();
        manager = null;
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

    public void deleteRouter() {
        DialogThreeBounce.showLoading(mContext);
        BaseDevice unbindDevice = manager.getDevice(currentSelectedRouter.getRouterDeviceKey());
        manager.unbindDevice(unbindDevice);
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
        });

    }

    public void updateDeviceInWhatRoom(final Room room, final String sn, final String deviceName) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "更新路由器设备所在的房间=start");
                SmartDev smartDev = DataSupport.where("Uid=?", sn).findFirst(SmartDev.class, true);
                List<Room> rooms = new ArrayList<Room>();
                rooms.clear();
                if (room != null) {
                    rooms.add(room);
                } else {
                    //
                    rooms.addAll(RoomManager.getInstance().getmRooms());
                }
                smartDev.setRooms(rooms);
                smartDev.setName(deviceName);
                final boolean saveResult = smartDev.save();
                for (int i = 0; i < mRouterManagerListenerList.size(); i++) {
                    DialogThreeBounce.hideLoading();
                    mRouterManagerListenerList.get(i).responseAddyResult(102);//MSG_ADD_ROUTER_SUCCESS
                }
                Log.i(TAG, "更新路由器设备所在的房间=" + saveResult);
            }
        });

    }


    public RouterDevice getRouterDevice() {
        String currentDevcieKey = currentSelectedRouter.getRouterDeviceKey();
        Log.i(TAG, "获取绑定的路由器设备currentDevcieKey=" + currentDevcieKey);
        routerDevice = (RouterDevice) manager.getDevice(currentSelectedRouter.getRouterDeviceKey());
        if (routerDevice == null) {
            Log.i(TAG, "设备离线了");
            routerDevice.setOnline(false);
            ContentValues values = new ContentValues();
            values.put("Status", "离线");
            final int affectColumn = DataSupport.updateAll(SmartDev.class, values, "Uid=?", currentSelectedRouter.getUid());
            Log.i(TAG, "更新设备在线状态 :离线 affectColumn=" + affectColumn);
        }
        return routerDevice;
    }
}
