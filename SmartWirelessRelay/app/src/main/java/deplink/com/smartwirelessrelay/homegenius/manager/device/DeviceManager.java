package deplink.com.smartwirelessrelay.homegenius.manager.device;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.OpResult;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.QueryWifiList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.QueryWifiListResult;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.LOCK_ALARM;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.AP_CLIENT;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.Proto;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.WifiRelaySet;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;

/**
 * Created by Administrator on 2017/11/9.
 * 使用：
 * private DeviceManager mDeviceManager;
 * mDeviceManager = DeviceManager.getInstance();
 * mDeviceManager.InitDeviceManager(this, null);
 */
public class DeviceManager implements LocalConnecteListener {
    private static final String TAG = "DeviceManager";
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
    /**
     * 这个类设计成单例
     */
    private static DeviceManager instance;
    private LocalConnectmanager mLocalConnectmanager;
    private GeneralPacket packet;
    private Context mContext;
    private List<SmartDev> mSmartDevList;
    private SmartDev currentSelectSmartDevice;

    public static synchronized DeviceManager getInstance() {
        if (instance == null) {
            instance = new DeviceManager();
        }
        return instance;
    }

    private List<DeviceListener> mDeviceListenerList;

    public void addDeviceListener(DeviceListener listener) {
        if (listener != null && !mDeviceListenerList.contains(listener)) {
            Log.i(TAG, "addDeviceListener=" + listener.toString());
            this.mDeviceListenerList.add(listener);
        }
    }

    public void removeDeviceListener(DeviceListener listener) {
        if (listener != null && mDeviceListenerList.contains(listener)) {
            this.mDeviceListenerList.remove(listener);
        }
    }

    /**
     * 查询设备列表
     */
    public void queryDeviceList() {
        Log.i(TAG, "查询设备列表");
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("QUERY");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packQueryDevListData(text.getBytes());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "cachedThreadPool execute queryDeviceList");
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }

    /**
     * 初始化本地连接管理器
     */
    public void InitDeviceManager(Context context, DeviceListener listener) {
        this.mContext = context;
        this.mDeviceListenerList = new ArrayList<>();
        if (mLocalConnectmanager == null) {
            mLocalConnectmanager = LocalConnectmanager.getInstance();
            mLocalConnectmanager.InitLocalConnectManager(mContext, AppConstant.BIND_APP_MAC);
        }
        mLocalConnectmanager.addLocalConnectListener(this);
        packet = new GeneralPacket(mContext);
        cachedThreadPool = Executors.newCachedThreadPool();
        addDeviceListener(listener);
        //耗时操作新建线程处理
        //数据库查询操作
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mSmartDevList = new ArrayList<>();
                mSmartDevList.clear();
                mSmartDevList.addAll(DataSupport.findAll(SmartDev.class));
            }
        });

    }

    /**
     * 查询wifi列表
     * 返回:{ "OP": "REPORT", "Method": "WIFIRELAY", "SSIDList": [ ] }
     */
    public void queryWifiList() {
        QueryWifiList query = new QueryWifiList();
        query.setTimestamp();
        Gson gson = new Gson();
        String text = gson.toJson(query);
        packet.packQueryWifiListData(text.getBytes());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }

    /**
     * 中继连接
     */
    public void setWifiRelay(AP_CLIENT paramas) {
        Log.i(TAG,"setWifiRelay");
        WifiRelaySet setCmd = new WifiRelaySet();
        setCmd.setTimestamp();
        Proto proto = new Proto();
        proto.setAP_CLIENT(paramas);
        setCmd.setProto(proto);
        Gson gson = new Gson();
        String text = gson.toJson(setCmd);
        packet.packSetWifiListData(text.getBytes());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }

    /**
     * 绑定智能设备列表
     * {"org":"ismart","tp":"SMART_LOCK","ad":"00-12-4b-00-0b-26-c2-15","ver":"1"}
     *
     * @param smartDevice 智能设备（除去网关设备：中继器）
     */
    public void bindSmartDevList(QrcodeSmartDevice smartDevice) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        List<SmartDev> devs = new ArrayList<>();
        //设备赋值
        SmartDev dev = new SmartDev();
        dev.setUid(smartDevice.getAd());
        dev.setOrg(smartDevice.getOrg());
        dev.setType(smartDevice.getTp());
        dev.setVer(smartDevice.getVer());
        //设备列表添加一个设备
        devs.add(dev);
        queryCmd.setSmartDev(devs);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packSendSmartDevsData(text.getBytes());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }


    /**
     * 如果数据库中没有这个设备，需要修改数据库
     * 如果有这个设备就不处理
     * 添加智能设备成功，需要更新数据库
     */
    public boolean addDBSmartDevice(QrcodeSmartDevice device) {
        //查询设备
        SmartDev smartDev = DataSupport.where("Uid=?", device.getAd()).findFirst(SmartDev.class);
        if (smartDev == null) {
            smartDev = new SmartDev();
            smartDev.setUid(device.getAd());
            smartDev.setOrg(device.getOrg());
            smartDev.setVer(device.getVer());
            smartDev.setType(device.getTp());

            boolean addResult = smartDev.save();
            Log.i(TAG, "向数据库中添加一条智能设备数据=" + addResult);
            return addResult;
        }
        Log.i(TAG, "数据库中已存在相同设备，不必要添加");
        return false;
    }

    public SmartDev getCurrentSelectSmartDevice() {
        return currentSelectSmartDevice;
    }

    public void setCurrentSelectSmartDevice(SmartDev currentSelectSmartDevice) {
        this.currentSelectSmartDevice = currentSelectSmartDevice;
    }

    /**
     * 查找所有的智能设备
     */
    public List<SmartDev> findAllSmartDevice() {
        List<SmartDev> smartDevices = DataSupport.findAll(SmartDev.class);
        Log.i(TAG, "查找所有的智能设备,设备个数=" + smartDevices.size());
        return smartDevices;
    }

    /**
     * 删除数据库中的一个智能设备
     */
    public int deleteDBSmartDevice(String uid) {
        int affectcolumn = DataSupport.deleteAll(SmartDev.class, "Uid=?", uid);
        Log.i(TAG, "删除一个智能设备，删除影响的行数=" + affectcolumn);
        return affectcolumn;
    }

    /**
     * 解除绑定
     * 解除绑定后根据返回结果更新数据库
     */
    public void deleteSmartDevice() {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("DELETE");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        List<SmartDev> devs = new ArrayList<>();
        //设备赋值
        SmartDev dev = new SmartDev();
        dev.setUid(currentSelectSmartDevice.getUid());
        dev.setOrg(currentSelectSmartDevice.getOrg());
        dev.setType(currentSelectSmartDevice.getType());
        dev.setVer(currentSelectSmartDevice.getVer());
        devs.add(dev);
        queryCmd.setSmartDev(devs);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packSendSmartDevsData(text.getBytes());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }

    /**
     * 更新设备所在房间
     */
    public void updateSmartDeviceInWhatRoom(final Room room, final String deviceUid, final String deviceName) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "更新智能设备所在的房间=start");
                //保存所在的房间
                //查询设备
                SmartDev smartDev = DataSupport.where("Uid=?", deviceUid).findFirst(SmartDev.class, true);
                //找到要更行的设备,设置关联的房间
                List<Room> roomList = new ArrayList<>();
                roomList.addAll(smartDev.getRoomList());
                roomList.add(room);
                smartDev.setRoomList(roomList);
                smartDev.setName(deviceName);
                boolean saveResult = smartDev.save();
                Log.i(TAG, "更新智能设备所在的房间=" + saveResult);
            }
        });

    }

    public void deleteSmartDeviceInWhatRoom(final Room room, final String deviceUid) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                //保存所在的房间
                //查询设备
                SmartDev smartDev = DataSupport.where("Uid=?", deviceUid).findFirst(SmartDev.class, true);
                //找到要更行的设备,设置关联的房间
                List<Room> roomList = new ArrayList<>();
                roomList.addAll(smartDev.getRoomList());
                for (int i = 0; i < roomList.size(); i++) {
                    if (roomList.get(i).getRoomName().equals(room.getRoomName())) {
                        roomList.remove(i);
                    }
                }
                smartDev.setRoomList(roomList);
                boolean saveResult = smartDev.save();
                Log.i(TAG, "deleteSmartDeviceInWhatRoom saveResult=" + saveResult);
            }
        });

    }


    @Override
    public void handshakeCompleted() {

    }

    @Override
    public void createSocketFailed(String msg) {

    }

    @Override
    public void OnFailedgetLocalGW(String msg) {

    }

    @Override
    public void OnBindAppResult(String uid) {

    }

    @Override
    public void OnGetQueryresult(String result) {
        //返回查询结果：开锁记录，设备列表
        Log.i(TAG, "返回查询结果：开锁记录，设备列表=" + result);
        //保存智能锁设备的DevUid
        if (result.contains("DevList")) {
            Gson gson = new Gson();
            DeviceList aDeviceList = gson.fromJson(result, DeviceList.class);
            if (aDeviceList.getSmartDev() != null && aDeviceList.getSmartDev().size() > 0) {
                mSmartDevList.clear();
                mSmartDevList.addAll(DataSupport.findAll(SmartDev.class));
                handleSmartDeviceList(aDeviceList);
            }
            //存储设备列表
            if (aDeviceList.getDevice() != null && aDeviceList.getDevice().size() > 0) {
                handleNormalDeviceList(aDeviceList);
            }
            for (int i = 0; i < mDeviceListenerList.size(); i++) {
                mDeviceListenerList.get(i).responseQueryResult(result);
            }
        }

    }

    /**
     * 处理智能设备列表
     *
     * @param aDeviceList 接收到的包含网关设备和智能设备的设备列表对象
     */
    private void handleSmartDeviceList(DeviceList aDeviceList) {
        for (int i = 0; i < aDeviceList.getSmartDev().size(); i++) {
            Log.i(TAG, "智能设备type=" + aDeviceList.getSmartDev().get(i).getType());
            List<SmartDev> smartDevs = DataSupport.findAll(SmartDev.class);
            Log.i(TAG, "查询智能设备smartDevs=" + smartDevs.size());
            if (smartDevs.size() > 0) {
                switch (aDeviceList.getSmartDev().get(i).getType()) {
                    case "SMART_LOCK":
                    case "IRMOTE_V2":
                        //查询数据库
                        boolean addToDb = true;
                        for (int devindex = 0; devindex < smartDevs.size(); devindex++) {
                            //数据库遍历。查找需要插入的智能设备（如果数据库中存在这个设备就不能插入）
                            //所有数据库中的设备中都没有和当前循环选中的设备uid相同，添加设备
                            if (smartDevs.get(devindex).getUid().equals(aDeviceList.getSmartDev().get(i).getUid())) {
                                addToDb = false;
                            }
                        }
                        if (addToDb) {
                            saveSmartDeviceToSqlite(aDeviceList, i);
                        }
                        //对比数据库和本地查询到的智能锁设备，如果数据库没有就添加到数据库中去
                        break;
                    default:

                        break;
                }
            } else {
                //数据库中没有就要新建数据
                saveSmartDeviceToSqlite(aDeviceList, i);
            }
        }

    }

    private void handleNormalDeviceList(DeviceList aDeviceList) {
        for (int i = 0; i < aDeviceList.getDevice().size(); i++) {
            //查询数据库
            List<Device> devices = DataSupport.findAll(Device.class);
            if (devices.size() > 0) {
                //有设备，要判断devUID如果有了就不能保存了
                boolean addToDb = true;
                for (int devindex = 0; devindex < devices.size(); devindex++) {
                    if (devices.get(devindex).getUid().equals(aDeviceList.getDevice().get(i).getUid())) {
                        addToDb = false;
                    }
                }
                if (addToDb) {
                    saveDeviceToSqlite(aDeviceList, i);
                }
            } else {
                saveDeviceToSqlite(aDeviceList, i);
            }
            //对比数据库和本地查询到的智能锁设备，如果数据库没有就添加到数据库中去
        }
    }

    private void saveDeviceToSqlite(final DeviceList aDeviceList, final int i) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Device dev = new Device();
                dev.setStatus(aDeviceList.getDevice().get(i).getStatus());
                dev.setUid(aDeviceList.getDevice().get(i).getUid());
                boolean success = dev.save();
                Log.i(TAG, "保存设备=" + success);
            }
        });
    }

    private void saveSmartDeviceToSqlite(final DeviceList aDeviceList, final int i) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                SmartDev dev = new SmartDev();
                dev.setUid(aDeviceList.getSmartDev().get(i).getUid());
                dev.setCtrUid(aDeviceList.getSmartDev().get(i).getCtrUid());
                dev.setStatus(aDeviceList.getSmartDev().get(i).getStatus());
                dev.setOrg(aDeviceList.getSmartDev().get(i).getOrg());
                dev.setType(aDeviceList.getSmartDev().get(i).getType());
                boolean success = dev.save();
                Log.i(TAG, "保存智能锁设备=" + success);
            }
        });

    }

    @Override
    public void OnGetSetresult(String setResult) {

    }

    @Override
    public void OnGetBindresult(String result) {
        for (int i = 0; i < mDeviceListenerList.size(); i++) {
            mDeviceListenerList.get(i).responseBindDeviceResult(result);
        }
    }


    @Override
    public void getWifiList(String result) {
        Gson gson = new Gson();
        QueryWifiListResult wifiList = gson.fromJson(result, QueryWifiListResult.class);
        for (int i = 0; i < mDeviceListenerList.size(); i++) {
            mDeviceListenerList.get(i).responseWifiListResult(wifiList.getSSIDList());
        }
    }

    @Override
    public void onSetWifiRelayResult(String result) {
        Log.i(TAG,"onSetWifiRelayResult="+result);
        Gson gson = new Gson();
        OpResult opResult = gson.fromJson(result, OpResult.class);
        if (opResult.getOP().equals("REPORT") && opResult.getMethod().equals("WIFI"))
            for (int i = 0; i < mDeviceListenerList.size(); i++) {
                mDeviceListenerList.get(i).responseSetWifirelayResult(opResult.getResult());
            }
    }

    @Override
    public void onGetalarmRecord(List<LOCK_ALARM> alarmList) {

    }
}
