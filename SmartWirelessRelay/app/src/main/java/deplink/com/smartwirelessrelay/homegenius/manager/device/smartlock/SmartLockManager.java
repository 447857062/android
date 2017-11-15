package deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.OpResult;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.SmartLock;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.alertreport.LOCK_ALARM;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.constant.SmartLockConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;

/**
 * Created by Administrator on 2017/11/9.
 * 智能锁设备管理器
 * 需要context，LocalConnecteListener（非必须）参数
 */
public class SmartLockManager implements LocalConnecteListener {
    private static final String TAG = "SmartLockManager";
    private GeneralPacket packet;
    private Context mContext;
    private LocalConnectmanager mLocalConnectmanager;
    private SmartLockListener mSmartLockListener;
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
    /**
     * 智能设备识别码DevUid
     */
    private String smartUid;

    /**
     * 这个类设计成单例
     */
    private static SmartLockManager instance;
    private SmartLock mSmartLock;
    private SQLiteDatabase db;

    private SmartLockManager() {

    }

    public static synchronized SmartLockManager getInstance() {
        if (instance == null) {
            instance = new SmartLockManager();
        }
        return instance;
    }


    /**
     * 初始化本地连接管理器
     */
    public void InitSmartLockManager(Context context, SmartLockListener listener) {
        this.mContext = context;
        this.mSmartLockListener = listener;
        if (mSmartLockListener == null) {
            Log.i(TAG, "未给智能锁管理器设置数据结果监听");
        }
        if (mLocalConnectmanager == null) {
            mLocalConnectmanager = LocalConnectmanager.getInstance();
            mLocalConnectmanager.InitLocalConnectManager(mContext);

        }
        mLocalConnectmanager.addLocalConnectListener(this);

        //生成数据库
        if (db == null) {
            db = Connector.getDatabase();
        }


        List<SmartDev> smartDevs = DataSupport.findAll(SmartDev.class);
        //当前只有一个智能锁
        if (smartDevs.size() > 0) {
            smartUid = smartDevs.get(0).getDevUid();
        }

        //TODO 创建一个初始化的报警记录
        if (mSmartLock == null) {
            mSmartLock = DataSupport.findFirst(SmartLock.class, true);
            if (mSmartLock == null) {
                mSmartLock = new SmartLock();
                mSmartLock.setDevUid(smartUid);
                mSmartLock.save();
            }
        }
        List<Device> devices = DataSupport.findAll(Device.class);
        if (devices != null && devices.size() > 0) {
            Log.i(TAG, "devices=" + devices.get(0).getUid());
        }


        packet = new GeneralPacket(mContext);

        cachedThreadPool = Executors.newCachedThreadPool();


    }

    public void releaswSmartManager() {
        mLocalConnectmanager.removeLocalConnectListener(this);
    }

    /**
     * 查询设备列表
     */
    public void queryDeviceList() {
        Log.i(TAG, "查询设备列表");
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("QUERY");
        queryCmd.setMethod("DevList");
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
     * 绑定智能设备列表
     */
    public void bindSmartDevList() {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SetDevList");
        List<SmartDev> devs = new ArrayList<>();
        //设备赋值
        SmartDev dev = new SmartDev();
        dev.setDevUid("00-12-4b-00-0b-26-c2-15");
        dev.setOrg("ismart");
        dev.setType("");
        dev.setVer("");

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
    public void bindDevList() {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SetDevList");
        List<Device> devs = new ArrayList<>();
        //设备赋值
        Device dev = new Device();
        //调试  uid 77685180654101946200316696479888
        dev.setUid("77685180654101946200316696479888");


        devs.add(dev);
        queryCmd.setDevice(devs);
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
     * 查询开锁记录
     */
    public void queryLockHistory() {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("QUERY");
        queryCmd.setMethod("SmartLock-HisRecord");

        queryCmd.setSmartUid(smartUid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packQueryDevListData(text.getBytes());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }

    /**
     * 设置SamrtLock参数
     *
     * @param cmd
     * @param userId       注册app,服务器统一分配一个userid
     * @param managePasswd 管理密码，第一次由用户自己输入
     * @param authPwd      授权密码
     * @param limitedTime  授权时限
     */
    public void setSmaertLockParmars(String cmd, String userId, String managePasswd, String authPwd, String limitedTime) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SmartLock");
        queryCmd.setSmartUid(smartUid);


        queryCmd.setCommand(cmd);
        if (authPwd != null) {
            queryCmd.setAuthPwd(authPwd);
        } else {
            queryCmd.setAuthPwd("0");
        }

        queryCmd.setUserID(userId);
        queryCmd.setManagePasswd(managePasswd);
        if (limitedTime != null) {
            queryCmd.setLimitedTime(limitedTime);
        } else {
            queryCmd.setLimitedTime("0");
        }


        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packSetSmartLockData(text.getBytes());

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
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
    public void OnGetUid(String uid) {
        mSmartLockListener.responseBind(uid);

        Log.i(TAG,"");

    }

    @Override
    public void OnGetQueryresult(String result) {
        //返回查询结果：开锁记录，设备列表
        Log.i(TAG, "回调设备列表数据 result=" + result);
        //保存智能锁设备的DevUid
        if (result.contains("DevList")) {
            Gson gson = new Gson();
            DeviceList aDeviceList = gson.fromJson(result, DeviceList.class);

            if (aDeviceList.getSmartDev() != null && aDeviceList.getSmartDev().size() > 0) {
                handleSmartDeviceList(aDeviceList);
            }

            //存储设备列表
            if (aDeviceList.getDevice() != null && aDeviceList.getDevice().size() > 0) {
                handleNormalDeviceList(aDeviceList);
            }
        }

        mSmartLockListener.responseQueryResult(result);
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

    private void saveDeviceToSqlite(DeviceList aDeviceList, int i) {
        Device dev = new Device();
        dev.setStatus(aDeviceList.getDevice().get(i).getStatus());
        dev.setUid(aDeviceList.getDevice().get(i).getUid());
        boolean success = dev.save();
        Log.i(TAG, "保存设备=" + success);
    }

    /**
     * 处理智能设备列表
     *
     * @param aDeviceList
     */
    private void handleSmartDeviceList(DeviceList aDeviceList) {
        for (int i = 0; i < aDeviceList.getSmartDev().size(); i++) {
            Log.i(TAG, "智能设备type=" + aDeviceList.getSmartDev().get(i).getType());
            switch (aDeviceList.getSmartDev().get(i).getType()) {
                case "SmartLock":
                    //查询数据库
                    List<SmartDev> smartDevs = DataSupport.findAll(SmartDev.class);
                    //对比数据库和本地查询到的智能锁设备，如果数据库没有就添加到数据库中去
                    Log.i(TAG, "查询智能设备smartDevs=" + smartDevs.size());
                    //保存智能锁的devUid
                    smartUid = aDeviceList.getSmartDev().get(i).getDevUid();
                    mSmartLock.setDevUid(smartUid);
                    mSmartLock.save();
                    if (smartDevs.size() > 0) {
                        for (int devindex = 0; devindex < smartDevs.size(); devindex++) {
                            if (!smartDevs.get(devindex).getDevUid().equals(aDeviceList.getSmartDev().get(i).getDevUid())) {
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

    private void saveSmartDeviceToSqlite(DeviceList aDeviceList, int i) {
        SmartDev dev = new SmartDev();
        dev.setDevUid(smartUid);
        dev.setCtrUid(aDeviceList.getSmartDev().get(i).getCtrUid());
        dev.setStatus(aDeviceList.getSmartDev().get(i).getStatus());
        dev.setOrg(aDeviceList.getSmartDev().get(i).getOrg());
        dev.setType(aDeviceList.getSmartDev().get(i).getType());
        boolean success = dev.save();
        Log.i(TAG, "保存智能锁设备=" + success);
    }

    @Override
    public void OnGetSetresult(String setResult) {
        //TODO 操作结果需要加上
        Gson gson = new Gson();
        OpResult result = gson.fromJson(setResult, OpResult.class);
        switch (result.getCmd()) {
            case SmartLockConstant.CMD.OPEN:
                switch (result.getResult()) {
                    case SmartLockConstant.OPENLOCK.TIMEOUT:
                        setResult = "超时";
                        break;
                    case SmartLockConstant.OPENLOCK.SUCCESS:
                        setResult = "超时";
                        break;
                    case SmartLockConstant.OPENLOCK.PASSWORDERROR:
                        setResult = "超时";
                        break;
                    case SmartLockConstant.OPENLOCK.FAIL:
                        setResult = "超时";
                        break;
                }
                break;
            case SmartLockConstant.CMD.ONCE:
            case SmartLockConstant.CMD.PERMANENT:
            case SmartLockConstant.CMD.TIMELIMIT:
                switch (result.getResult()) {
                    case SmartLockConstant.AUTH.TIMEOUT:
                        setResult = "超时";
                        break;
                    case SmartLockConstant.AUTH.SUCCESS:
                        setResult = "成功";
                        break;
                    case SmartLockConstant.AUTH.PASSWORDERROR:
                        setResult = "密码错误";
                        break;
                    case SmartLockConstant.AUTH.FAIL:
                        setResult = "失败";
                        break;
                    case SmartLockConstant.AUTH.FORBADE:
                        setResult = "禁止";
                        break;
                }
                break;
        }
        mSmartLockListener.responseSetResult(setResult);
    }

    @Override
    public void wifiConnectUnReachable() {

    }

    /**
     * 获取报警记录
     *
     * @param alarmList
     */
    @Override
    public void onGetalarmRecord(List<LOCK_ALARM> alarmList) {
        //TODO 这里还要写入devuid，不然会报错，因为这个devuid是不为空的
        mSmartLock = DataSupport.findFirst(SmartLock.class);
        for (int i = 0; i < alarmList.size(); i++) {
            LOCK_ALARM alarm;
            alarm = alarmList.get(i);
            alarm.save();

            mSmartLock.getAlarmList().add(alarm);
        }
        mSmartLock.save();
        //TODO
    }

    /**
     * 报警记录设备上报，没有查询接口，所以保存在数据库中，需要去数据库获取
     *
     * @return
     */
    public List<LOCK_ALARM> getAlarmRecord(String devUid) {
        List<LOCK_ALARM> newsList = DataSupport.where("DevUid = ?", smartUid).findFirst(SmartLock.class, true).getAlarmList();
        return newsList;
    }
}
