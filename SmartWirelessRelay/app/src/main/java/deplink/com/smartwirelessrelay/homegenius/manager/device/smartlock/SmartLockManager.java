package deplink.com.smartwirelessrelay.homegenius.manager.device.smartlock;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.OpResult;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.ResultType;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.DeviceList;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.ManagerPassword;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SmartLock;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.Info;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.LOCK_ALARM;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.constant.SmartLockConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;

/**
 * Created by Administrator on 2017/11/9.
 * 智能锁设备管理器
 * 需要context，LocalConnecteListener（非必须）参数
 * 使用：
 * <p/>
 * private SmartLockManager mSmartLockManager;
 * mSmartLockManager = SmartLockManager.getInstance();
 * mSmartLockManager.InitSmartLockManager(this);
 * mSmartLockManager.addSmartLockListener
 */
public class SmartLockManager implements LocalConnecteListener {
    private static final String TAG = "SmartLockManager";
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
    private static SmartLockManager instance;
    private SmartLock mSmartLock;
    private String smartUid = "00-12-4b-00-0b-26-c2-15";
    private SmartDev currentSelectLock;
    private SmartLockManager() {

    }

    public SmartDev getCurrentSelectLock() {
        return currentSelectLock;
    }

    public void setCurrentSelectLock(SmartDev currentSelectLock) {
        this.currentSelectLock = currentSelectLock;
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
    public void InitSmartLockManager(Context context) {
        this.mContext = context;
        this.mSmartLockListenerList = new ArrayList<>();
        if (mLocalConnectmanager == null) {
            mLocalConnectmanager = LocalConnectmanager.getInstance();
            mLocalConnectmanager.InitLocalConnectManager(mContext, AppConstant.BIND_APP_MAC);
        }
        mLocalConnectmanager.addLocalConnectListener(this);

        ManagerPassword managerPassword = DataSupport.findFirst(ManagerPassword.class);
        Log.i(TAG, "managerPassword!=null" + (managerPassword != null));
        if (managerPassword == null) {
            managerPassword = new ManagerPassword();
            managerPassword.save();
        }
        packet = new GeneralPacket(mContext);
        if (cachedThreadPool == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
        }

    }

    private List<SmartLockListener> mSmartLockListenerList;

    public void addSmartLockListener(SmartLockListener listener) {

        if (listener != null && !mSmartLockListenerList.contains(listener)) {
            Log.i(TAG, "addSmartLockListener=" + listener.toString());
            this.mSmartLockListenerList.add(listener);
        }
    }

    public void removeSmartLockListener(SmartLockListener listener) {
        if (listener != null && mSmartLockListenerList.contains(listener)) {
            this.mSmartLockListenerList.remove(listener);
        }

    }

    public void releaswSmartManager() {
        mLocalConnectmanager.removeLocalConnectListener(this);
    }


    /**
     * 查询开锁记录
     */
    public void queryLockHistory() {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("QUERY");
        queryCmd.setMethod("SmartLock");
        queryCmd.setCommand("HisRecord");
        queryCmd.setUserID("1001");
        Log.i(TAG, "查询开锁记录设备smartUid=" + smartUid);
        smartUid = "00-12-4b-00-0b-26-c2-15";
        queryCmd.setSmartUid(smartUid);
        queryCmd.setTimestamp();
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packOpenLockListData(text.getBytes());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }

    public void updateSmartDeviceName(final String deviceName) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put("name", deviceName);
                int effectColumn = DataSupport.updateAll(SmartDev.class, values, "Uid = ?", currentSelectLock.getUid());

                Log.i(TAG, "修改智能门锁设备名字=" + effectColumn);
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
    public void setSmartLockParmars(String cmd, String userId, String managePasswd, String authPwd, String limitedTime) {
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
        queryCmd.setManagePwd(managePasswd);
        if (limitedTime != null) {
            queryCmd.setTime(limitedTime);
        } else {
            queryCmd.setTime("0");
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
    public void OnBindAppResult(String uid) {
        for (int i = 0; i < mSmartLockListenerList.size(); i++) {
            mSmartLockListenerList.get(i).responseBind(uid);
        }
    }

    @Override
    public void OnGetQueryresult(String result) {
        //TODO
        Log.i(TAG, "OnGetQueryresult=" + smartUid);
        Gson gson = new Gson();

        OpResult queryResult = gson.fromJson(result, OpResult.class);
        if (result.contains("DevList")) {
            DeviceList aDeviceList = gson.fromJson(result, DeviceList.class);
            if (aDeviceList.getSmartDev() != null && aDeviceList.getSmartDev().size() > 0) {
                smartUid = aDeviceList.getSmartDev().get(0).getUid();
                Log.i(TAG, "保存智能设备uid=" + smartUid);
            }

        }
        //SmartLock-HisRecord"
        if (result.contains("HisRecord")) {
            for (int i = 0; i < mSmartLockListenerList.size(); i++) {
                Log.i(TAG, "SmartLockListener ==" + mSmartLockListenerList.get(i).toString());
                mSmartLockListenerList.get(i).responseQueryResult(result);
            }
        }
    }

    /**
     * 门锁开锁，授权操作结果返回
     * 操作结果解释
     *
     * @param setResult
     */
    @Override
    public void OnGetSetresult(String setResult) {
        Gson gson = new Gson();
        ResultType type = gson.fromJson(setResult, ResultType.class);
        if (type != null && type.getOP().equals("REPORT") && type.getMethod().equals("SmartLock")) {
            OpResult result = gson.fromJson(setResult, OpResult.class);
            switch (result.getCommand()) {
                case SmartLockConstant.CMD.OPEN:
                    switch (result.getResult()) {
                        case SmartLockConstant.OPENLOCK.TIMEOUT:
                            setResult = "超时";
                            break;
                        case SmartLockConstant.OPENLOCK.SUCCESS:
                            setResult = "成功";
                            break;
                        case SmartLockConstant.OPENLOCK.PASSWORDERROR:
                            setResult = "密码错误";
                            break;
                        case SmartLockConstant.OPENLOCK.FAIL:
                            setResult = "失败";
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
        }
        Log.i(TAG, "设置结果=" + setResult);
        for (int i = 0; i < mSmartLockListenerList.size(); i++) {
            mSmartLockListenerList.get(i).responseSetResult(setResult);
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
        //TODO 这里还要写入devuid，不然会报错，因为这个devuid是不为空的
        mSmartLock = DataSupport.findFirst(SmartLock.class);
        for (int i = 0; i < alarmList.size(); i++) {
            Info alarm;
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
        //TODO
        smartUid = "00-12-4b-00-0b-26-c2-15";
        List<SmartLock> mSmartDevices = DataSupport.where("Uid = ?", smartUid).find(SmartLock.class, true);
        if (mSmartDevices.size() > 0) {
            List<Info> newsList = mSmartDevices.get(0).getAlarmList();
        }

        return null;
    }
}
