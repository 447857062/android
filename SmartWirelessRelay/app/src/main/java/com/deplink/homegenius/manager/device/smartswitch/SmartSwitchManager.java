package com.deplink.homegenius.manager.device.smartswitch;

import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.Protocol.json.device.lock.alertreport.Info;
import com.deplink.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import com.deplink.homegenius.Protocol.packet.GeneralPacket;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import com.deplink.homegenius.manager.connect.remote.HomeGenius;
import com.deplink.homegenius.manager.connect.remote.RemoteConnectManager;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.util.Perfence;
import com.google.gson.Gson;
import com.deplink.homegenius.Protocol.json.QueryOptions;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/12/7.
 */
public class SmartSwitchManager implements LocalConnecteListener {
    private static final String TAG = "SmartSwitchManager";
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
    /**
     * 这个类设计成单例
     */
    private static SmartSwitchManager instance;
    private LocalConnectmanager mLocalConnectmanager;
    private GeneralPacket packet;
    private Context mContext;
    /**
     * 所有的开关设备
     */
    private List<SmartDev> mSmartSwitchDevList;
    private SmartDev currentSelectSmartDevice;

    public SmartDev getCurrentSelectSmartDevice() {
        return currentSelectSmartDevice;
    }

    private List<SmartSwitchListener> mSmartSwitchListenerList;
    private RemoteConnectManager mRemoteConnectManager;
    private HomeGenius mHomeGenius;

    public void addSmartSwitchListener(SmartSwitchListener listener) {
        if (listener != null && !mSmartSwitchListenerList.contains(listener)) {
            this.mSmartSwitchListenerList.add(listener);
        }
    }

    public void removeSmartSwitchListener(SmartSwitchListener listener) {
        if (listener != null && mSmartSwitchListenerList.contains(listener)) {
            this.mSmartSwitchListenerList.remove(listener);
        }
    }

    public void setCurrentSelectSmartDevice(SmartDev currentSelectSmartDevice) {
        this.currentSelectSmartDevice = currentSelectSmartDevice;
    }

    public static synchronized SmartSwitchManager getInstance() {
        if (instance == null) {
            instance = new SmartSwitchManager();
        }
        return instance;
    }

    /**
     * 初始化本地连接管理器
     */
    public void InitSmartSwitchManager(Context context) {
        this.mContext = context;
        this.mSmartSwitchListenerList = new ArrayList<>();
        if (mLocalConnectmanager == null) {
            mLocalConnectmanager = LocalConnectmanager.getInstance();
            String uuid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
            mLocalConnectmanager.InitLocalConnectManager(context, uuid);
        }
        if (mRemoteConnectManager == null) {
            mRemoteConnectManager = RemoteConnectManager.getInstance();
            mRemoteConnectManager.InitRemoteConnectManager(mContext);
        }

        if (mHomeGenius == null) {
            mHomeGenius = new HomeGenius();
        }
        mLocalConnectmanager.addLocalConnectListener(this);
        packet = new GeneralPacket(mContext);
        if (cachedThreadPool == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
        }
        //数据库查询操作
        if (mSmartSwitchDevList == null) {
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mSmartSwitchDevList = new ArrayList<>();
                    mSmartSwitchDevList.clear();
                    mSmartSwitchDevList.addAll(DataSupport.where("Type = ?", DeviceTypeConstant.TYPE.TYPE_SWITCH).find(SmartDev.class));
                }
            });
        }
    }

    /**
     *
     */
    public void setSwitchCommand(String cmd) {
        if (mLocalConnectmanager.isLocalconnectAvailable()) {
            QueryOptions queryCmd = new QueryOptions();
            queryCmd.setOP("SET");
            queryCmd.setMethod("SmartWallSwitch");
            queryCmd.setCommand(cmd);
            queryCmd.setSmartUid(currentSelectSmartDevice.getMac());
            Log.i(TAG, "设置开关smartUid=" + currentSelectSmartDevice.getUid());
            queryCmd.setTimestamp();
            Gson gson = new Gson();
            String text = gson.toJson(queryCmd);
            packet.packSetCmdData(text.getBytes(), currentSelectSmartDevice.getUid());
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mLocalConnectmanager.getOut(packet.data);
                }
            });
        } else {
            String uuid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
            GatwayDevice device = currentSelectSmartDevice.getGetwayDevice();
            if (device == null) {
                device= DataSupport.where("Status = ?", "在线").findFirst(GatwayDevice.class);
            }
            if(device==null){
                device= DataSupport.findFirst(GatwayDevice.class);
            }
            Log.i(TAG, "device.getTopic()=" + device.getTopic());
            if (device.getTopic() != null && !device.getTopic().equals("")) {
                mHomeGenius.setSwitchCommand(currentSelectSmartDevice, device.getTopic(), uuid, cmd);
            }

        }
    }

    public void querySwitchStatus(String cmd) {
        if (mLocalConnectmanager.isLocalconnectAvailable()) {
            QueryOptions queryCmd = new QueryOptions();
            queryCmd.setOP("SET");
            queryCmd.setMethod("SmartWallSwitch");
            queryCmd.setCommand(cmd);
            queryCmd.setSmartUid(currentSelectSmartDevice.getMac());
            queryCmd.setTimestamp();
            Gson gson = new Gson();
            String text = gson.toJson(queryCmd);
            packet.packSetCmdData(text.getBytes(), currentSelectSmartDevice.getUid());
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mLocalConnectmanager.getOut(packet.data);
                }
            });
        } else {
            String uuid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
            GatwayDevice device = currentSelectSmartDevice.getGetwayDevice();
            if (device == null) {
                device= DataSupport.where("Status = ?", "在线").findFirst(GatwayDevice.class);
            }
            if(device==null){
                device= DataSupport.findFirst(GatwayDevice.class);
            }
            if (device != null) {
                Log.i(TAG, "device.getTopic()=" + device.getTopic());
                if (device.getTopic() != null && !device.getTopic().equals("")) {
                    mHomeGenius.querySwitchStatus(currentSelectSmartDevice, device.getTopic(), uuid, cmd);
                }
            }


        }

    }

    public boolean updateSmartDeviceGetway(GatwayDevice getwayDevice) {
        Log.i(TAG, "更新智能设备所在的网关=start");
        currentSelectSmartDevice.setGetwayDevice(getwayDevice);
        currentSelectSmartDevice.setGetwayDeviceUid(getwayDevice.getUid());
        boolean saveResult = currentSelectSmartDevice.saveFast();
        Log.i(TAG, "更新智能设备所在的网关=" + saveResult);
        return saveResult;
    }

    /**
     * 添加开关时指定当前添加开关的类型
     */
    private String currentAddSwitchSubType;
    /**
     * 设备列表界面中，当前选中的开关设备
     */
    private String currentSelectSwitchSubType;

    public String getCurrentSelectSwitchSubType() {
        return currentSelectSwitchSubType;
    }

    public void setCurrentSelectSwitchSubType(String currentSelectSwitchSubType) {
        this.currentSelectSwitchSubType = currentSelectSwitchSubType;
    }

    public String getCurrentAddSwitchSubType() {
        return currentAddSwitchSubType;
    }

    public void setCurrentAddSwitchSubType(String currentAddSwitchSubType) {
        this.currentAddSwitchSubType = currentAddSwitchSubType;
    }

    public boolean addDBSwitchDevice(QrcodeSmartDevice device, String uid) {
        //查询设备
        Log.i(TAG, "当前添加的开关子类型" + currentAddSwitchSubType);
        SmartDev smartDev = DataSupport.where("Uid=?", device.getAd()).findFirst(SmartDev.class);
        if (smartDev == null) {
            smartDev = new SmartDev();
            smartDev.setUid(uid);
            smartDev.setOrg(device.getOrg());
            smartDev.setVer(device.getVer());
            smartDev.setMac(device.getAd().toLowerCase());
            smartDev.setType(DeviceTypeConstant.TYPE.TYPE_SWITCH);
            smartDev.setName(device.getName());
            smartDev.setSubType(currentAddSwitchSubType);
            boolean addResult = smartDev.save();
            Log.i(TAG, "向数据库中添加一条智能设备数据=" + addResult);
            return addResult;
        }
        Log.i(TAG, "数据库中已存在相同设备，不必要添加");
        return false;
    }

    /**
     * 更新设备所在房间
     */
    public void updateSmartDeviceInWhatRoom(Room room, String deviceUid, String deviceName) {
        Log.i(TAG, "更新智能设备所在的房间=start" + "room=" + (room != null));
        //保存所在的房间
        //查询设备
        SmartDev smartDev = DataSupport.where("Uid=?", deviceUid).findFirst(SmartDev.class, true);
        //找到要更行的设备,设置关联的房间
        List<Room> rooms = new ArrayList<>();
        if (room != null) {
            rooms.add(room);
        } else {
            rooms.addAll(RoomManager.getInstance().getmRooms());
            Log.i(TAG, "房间列表大小" + rooms.size());
        }
        smartDev.setRooms(rooms);
        smartDev.setName(deviceName);
        boolean saveResult = smartDev.save();
        Log.i(TAG, "更新智能设备所在的房间=" + saveResult);
    }

    public boolean updateSmartDeviceName(String deviceUid, String deviceName) {
        //保存所在的房间
        //查询设备
        SmartDev smartDev = DataSupport.where("Uid=?", deviceUid).findFirst(SmartDev.class, true);
        setCurrentSelectSmartDevice(smartDev);
        smartDev.setName(deviceName);
        return smartDev.save();
    }

    /**
     * 删除数据库中的一个智能设备
     */
    public int deleteDBSmartDevice(String uid) {
        int affectcolumn = DataSupport.deleteAll(SmartDev.class, "Uid=?", uid);
        Log.i(TAG, "删除一个智能设备，删除影响的行数=" + affectcolumn);
        return affectcolumn;
    }

    @Override
    public void OnBindAppResult(String uid) {

    }

    @Override
    public void OnGetQueryresult(String devList) {

    }

    @Override
    public void OnGetSetresult(String setResult) {
        Log.i(TAG, "开关控制结果返回=" + setResult);
        for (int i = 0; i < mSmartSwitchListenerList.size(); i++) {
            mSmartSwitchListenerList.get(i).responseResult(setResult);
        }
    }

    @Override
    public void OnGetBindresult(String setResult) {

    }

    @Override
    public void getWifiList(String result) {

    }

    @Override
    public void onSetWifiRelayResult(String result) {

    }

    @Override
    public void onGetalarmRecord(List<Info> alarmList) {

    }

}
