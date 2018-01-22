package com.deplink.homegenius.manager.device.doorbeel;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.packet.GeneralPacket;
import com.deplink.homegenius.Protocol.packet.unpacket.UnpacketWiFiConfig;

import org.litepal.crud.DataSupport;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


    public void getDoorbeelAtRooms( Observer observer) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                SmartDev dev = DataSupport.where("Uid = ?", currentSelectedDoorbeel.getUid()).findFirst(SmartDev.class, true);
                final List<Room> rooms = dev.getRooms();

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
    public boolean saveDoorbeel(SmartDev dev) {
        boolean success = dev.save();
        Log.i(TAG, "保存智能门铃设备=" + success);
        return success;
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

    public void deleteDoorbeel( SmartDev dev) {

                 int affectColumn = DataSupport.deleteAll(SmartDev.class, "Uid = ?", dev.getUid());

                Log.i(TAG, "删除智能门铃设备=" + affectColumn);

    }

    /**
     * 更新设备所在房间
     *
     * @param room
     * @param sn
     * @param deviceName
     */
    public boolean updateDeviceInWhatRoom(Room room, String sn, String deviceName) {
        Log.i(TAG, "更新智能门铃设备所在的房间=start");
        //保存所在的房间
        //查询设备
        SmartDev smartDev = DataSupport.where("Uid=?", sn).findFirst(SmartDev.class, true);
        //找到要更行的设备,设置关联的房间
        List<Room> rooms = new ArrayList<>();
        rooms.add(room);
        smartDev.setRooms(rooms);
        smartDev.setName(deviceName);
        boolean saveResult = smartDev.save();
        Log.i(TAG, "更新智能门铃设备所在的房间=" + saveResult);
        return saveResult;
    }
    public static final int LocalConPort = 5880;
    //设置设备的WiFi参数 -- 阻塞方式 -- 外部调用建议使用线程
    public int setDevWiFiConfigWithMac(long mac, byte type, byte ver,String ssid,String pwd) {
        //OneDev dev = DevStatus.getOneDev(mac);
       // int netStatus = dev.getDevStatus(PublicMethod.getTimeMs());
        UnpacketWiFiConfig unpack = new UnpacketWiFiConfig();
        GeneralPacket rebootPacket = null;
      //  Log.i(TAG,"setDevWiFiConfigWithMac netStatus="+netStatus);
       // if (dev != null) {
            //设备存在，并且设备在线，发起读取设备WiFi的任务
            GeneralPacket packet = new GeneralPacket(mContext);
           // if (netStatus == ConnTypeLocal) {
                try {
                    rebootPacket = new GeneralPacket(InetAddress.getByName("255.255.255.255"), LocalConPort, mContext);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                packet.packWiFiConfigPacket(mac, type, ver, true, ssid, pwd, unpack);
                rebootPacket.packRebootWiFiConfigPacket(mac, type, ver, true, unpack);
         //   }
           /* else if (netStatus == ConnTypeRemote) {
                rebootPacket = new GeneralPacket(dev.remoteIP, dev.remotePort, mContext);
                packet.packWiFiConfigPacket(mac, type, ver, false, ssid, pwd, unpack);
                rebootPacket.packRebootWiFiConfigPacket(mac, type, ver, false, unpack);
            } */
           /* else {
                return -1;
            }*/
          //  udp.writeNet(packet);
            boolean isFinish = false;
            int count = 0;
            boolean isSetOk = false;
            while (!isFinish) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                switch (unpack.step) {
                    case 2:
                        isSetOk = false;
                        isFinish = true;
                        return 0;
                    case 4:
                        isFinish = true;
                        isSetOk = true;
                        break;
                    case -1:
                        isSetOk = false;
                        isFinish = true;
                        break;
                    default:
                        break;
                }
                if (count > 50) {
                    isSetOk = false;
                    isFinish = true;
                }
            }
            if (isSetOk) {
              //  udp.writeNet(rebootPacket);
                return 1;
            } else {
                return 0;
            }
      //  }
       // return -1;

    }
}
