package deplink.com.smartwirelessrelay.homegenius.manager.device.light;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.Info;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceTypeConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

/**
 */
public class SmartLightManager implements LocalConnecteListener {
    private static final String TAG = "SmartLightManager";
    private GeneralPacket packet;
    private Context mContext;
    private LocalConnectmanager mLocalConnectmanager;
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
    /**
     * 这个类设计成单例
     */
    private static SmartLightManager instance;
    private SmartDev currentSelectLight;
    private SmartLightManager() {

    }

    public SmartDev getCurrentSelectLight() {
        return currentSelectLight;
    }

    public void setCurrentSelectLight(SmartDev currentSelectLight) {
        this.currentSelectLight = currentSelectLight;
    }

    public static synchronized SmartLightManager getInstance() {
        if (instance == null) {
            instance = new SmartLightManager();
        }
        return instance;
    }
    private List<SmartLightListener> mSmartLightListenerList;

    /**
     * 初始化本地连接管理器
     */
    public void InitSmartLightManager(Context context) {
        this.mContext = context;
        this.mSmartLightListenerList=new ArrayList<>();
        if (mLocalConnectmanager == null) {
            mLocalConnectmanager = LocalConnectmanager.getInstance();
            mLocalConnectmanager.InitLocalConnectManager(context, AppConstant.BIND_APP_MAC);
        }
        mLocalConnectmanager.addLocalConnectListener(this);
        packet = new GeneralPacket(mContext);
        if (cachedThreadPool == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
        }

    }
    public void addSmartLightListener(SmartLightListener listener) {
        if (listener != null && !mSmartLightListenerList.contains(listener)) {
            this.mSmartLightListenerList.add(listener);
        }
    }
    public void removeSmartLightListener(SmartLightListener listener) {
        if (listener != null && mSmartLightListenerList.contains(listener)) {
            this.mSmartLightListenerList.remove(listener);
        }
    }

    public void releaswSmartManager() {
        mLocalConnectmanager.removeLocalConnectListener(this);
    }
    public void setSmartLightSwitch(String cmd) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("YWLIGHTCONTROL");
        queryCmd.setSmartUid(currentSelectLight.getUid());
        queryCmd.setCommand(cmd);

        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packSetCmdData(text.getBytes(), currentSelectLight.getUid());

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });

    }
    public void setSmartLightParamas(String cmd,int yellow,int white ) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("YWLIGHTCONTROL");
        queryCmd.setSmartUid(currentSelectLight.getUid());
        queryCmd.setCommand(cmd);
        queryCmd.setYellow(yellow);
        queryCmd.setWhite(white);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packSetCmdData(text.getBytes(), currentSelectLight.getUid());

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });

    }
    public void queryLightStatus() {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("YWLIGHTCONTROL");
        queryCmd.setCommand("query");
        queryCmd.setSmartUid(currentSelectLight.getUid());
        queryCmd.setTimestamp();
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packSetCmdData(text.getBytes(), currentSelectLight.getUid());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }
    public boolean addDBSwitchDevice(QrcodeSmartDevice device) {
        //查询设备
        SmartDev smartDev = DataSupport.where("Uid=?", device.getAd()).findFirst(SmartDev.class);
        if (smartDev == null) {
            smartDev = new SmartDev();
            smartDev.setUid(device.getAd());
            smartDev.setOrg(device.getOrg());
            smartDev.setVer(device.getVer());
            smartDev.setType(DeviceTypeConstant.TYPE.TYPE_LIGHT);
            smartDev.setName(device.getName());
            boolean addResult = smartDev.save();
            Log.i(TAG, "向数据库中添加一条智能设备数据=" + addResult);
            return addResult;
        }
        Log.i(TAG, "数据库中已存在相同设备，不必要添加");
        return false;
    }
    public boolean updateSmartDeviceGetway(Device getwayDevice) {
        Log.i(TAG, "更新智能设备所在的网关=start");
        currentSelectLight.setGetwayDevice(getwayDevice);
        boolean saveResult = currentSelectLight.save();
        Log.i(TAG, "更新智能设备所在的网关=" + saveResult);
        return saveResult;
    }
    /**
     * 更新设备所在房间
     */
    public void updateSmartDeviceRoomAndName(Room room, String deviceUid, String deviceName) {
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
    /**
     * 更新设备所在房间
     */
    public void updateSmartDeviceRoom(Room room, String deviceUid) {
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
        boolean saveResult = smartDev.save();
        Log.i(TAG, "更新智能设备所在的房间=" + saveResult);
    }

    @Override
    public void OnBindAppResult(String uid) {

    }

    @Override
    public void OnGetQueryresult(String result) {

    }

    /**
     * 门锁开锁，授权操作结果返回
     * 操作结果解释
     *
     * @param setResult
     */
    @Override
    public void OnGetSetresult(String setResult) {
        Log.i(TAG,"setResult="+setResult);
        for (int i = 0; i < mSmartLightListenerList.size(); i++) {
            mSmartLightListenerList.get(i).responseSetResult(setResult);
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

    /**
     * 获取报警记录
     *
     * @param alarmList
     */
    @Override
    public void onGetalarmRecord(List<Info> alarmList) {

    }
}
