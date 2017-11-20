package deplink.com.smartwirelessrelay.homegenius.manager.device;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.OpResult;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.QueryWifiList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.QueryWifiListResult;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.alertreport.LOCK_ALARM;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.AP_CLIENT;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.Proto;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.WifiRelaySet;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
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
            mLocalConnectmanager.InitLocalConnectManager(mContext);
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
     * 绑定网关，中继器
     */
    public void bindDevice(String uid) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        List<Device> devs = new ArrayList<>();
        //设备赋值
        Device dev = new Device();
        //调试  uid 77685180654101946200316696479888
        dev.setUid(uid);
        devs.add(dev);
        queryCmd.setDevice(devs);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packSendDevsData(text.getBytes());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }


    public SmartDev getCurrentSelectSmartDevice() {
        return currentSelectSmartDevice;
    }

    public void setCurrentSelectSmartDevice(SmartDev currentSelectSmartDevice) {
        this.currentSelectSmartDevice = currentSelectSmartDevice;
    }

    /**
     * 解除绑定
     * 解除绑定后根据返回结果更新数据库
     */
    public void deleteDevice() {
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
    public void updateDeviceInWhatRoom(final Room room, final String deviceUid, final String deviceName) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                //保存所在的房间
                //查询设备
                SmartDev smartDev = DataSupport.where("Uid=?", deviceUid).findFirst(SmartDev.class);
                //找到要更行的设备,设置关联的房间
                List<Room> roomList = new ArrayList<>();
                roomList.add(room);
                smartDev.setRoomList(roomList);
                smartDev.setName(deviceName);
                smartDev.save();
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
            switch (aDeviceList.getSmartDev().get(i).getType()) {
                case "SMART_LOCK":
                    //查询数据库
                    List<SmartDev> smartDevs = DataSupport.findAll(SmartDev.class);
                    //对比数据库和本地查询到的智能锁设备，如果数据库没有就添加到数据库中去
                    Log.i(TAG, "查询智能设备smartDevs=" + smartDevs.size());
                    if (smartDevs.size() > 0) {
                        for (int devindex = 0; devindex < smartDevs.size(); devindex++) {
                            if (!smartDevs.get(devindex).getUid().equals(aDeviceList.getSmartDev().get(i).getUid())) {
                                saveSmartDeviceToSqlite(aDeviceList, i);
                            }
                        }
                    } else {
                        //数据库中没有就要新建数据
                        saveSmartDeviceToSqlite(aDeviceList, i);
                    }
                    break;
            }

        }
    }

    private void handleNormalDeviceList(DeviceList aDeviceList) {
        for (int i = 0; i < aDeviceList.getDevice().size(); i++) {
            //查询数据库
            List<Device> devices = DataSupport.findAll(Device.class);
            if (devices.size() > 0) {
                //有设备，要判断devUID如果有了就不能保存了
                for (int devindex = 0; devindex < devices.size(); devindex++) {
                    if (!devices.get(devindex).getUid().equals(aDeviceList.getDevice().get(i).getUid())) {
                        saveDeviceToSqlite(aDeviceList, i);
                    }
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
    public void wifiConnectUnReachable() {

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
