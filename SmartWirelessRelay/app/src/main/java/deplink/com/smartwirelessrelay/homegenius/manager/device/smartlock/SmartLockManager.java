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
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
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

    private SmartLockManager() {
    }

    public static synchronized SmartLockManager getInstance() {
        if (instance == null) {
            instance = new SmartLockManager();
        }
        return instance;
    }

    private SQLiteDatabase db;

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
        if(smartDevs.size()>0){
            smartUid = smartDevs.get(0).getDevUid();
        }


        List<Device> devices = DataSupport.findAll(Device.class);
        if (devices != null && devices.size() > 0) {
            Log.i(TAG, "devices=" + devices.get(0).getUid());
        }


        packet = new GeneralPacket(mContext);
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public  void releaswSmartManager(){
        mLocalConnectmanager.removeLocalConnectListener(this);
    }
    /**
     * 查询设备列表
     */
    public void queryDeviceList() {
        Log.i(TAG,"查询设备列表");
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("QUERY");
        queryCmd.setMethod("DevList");
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        packet.packQueryDevListData(text.getBytes());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"cachedThreadPool execute queryDeviceList");
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }

    /**
     * 绑定设备
     */
    public void bindSmartLock() {
        packet.packBindPacket();
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }

    /**
     * 下发智能设备列表
     */
    public void sendSmartDevList() {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SmartDev-List");
        List<SmartDev> devs = new ArrayList<>();
        SmartDev dev = new SmartDev();
        dev.setDevUid("00-12-4b-00-0b-26-c2-15");
        dev.setOrg("ismart");
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
     * @param userId 注册app,服务器统一分配一个userid
     * @param managePasswd 管理密码，第一次由用户自己输入
     * @param authPwd 授权密码
     * @param limitedTime 授权时限
     */
    public void setSmaertLockParmars(String cmd, String userId, String managePasswd, String authPwd, String limitedTime) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SmartLock");
        queryCmd.setSmartUid(smartUid);
        queryCmd.setCommand(cmd);
        if(authPwd!=null){
            queryCmd.setAuthPwd(authPwd);
        }else{
            queryCmd.setAuthPwd("0");
        }

        queryCmd.setUserID(userId);
        queryCmd.setManagePasswd(managePasswd);
        if(limitedTime!=null){
            queryCmd.setLimitedTime(limitedTime);
        }else{
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
    }

    @Override
    public void OnGetQueryresult(String result) {
        //返回查询结果：开锁记录，设备列表
        Log.i(TAG,"回调设备列表数据 result="+result);
        //保存智能锁设备的DevUid
        if (result.contains("DevList")) {
            Gson gson = new Gson();
            DeviceList aDeviceList = gson.fromJson(result, DeviceList.class);
            if (aDeviceList.getSmartDev() != null && aDeviceList.getSmartDev().size() > 0) {
                for (int i = 0; i < aDeviceList.getSmartDev().size(); i++) {
                    //uid
                    Log.i(TAG, "智能设备type=" + aDeviceList.getSmartDev().get(i).getType());
                    if ("SmartLock".equalsIgnoreCase(aDeviceList.getSmartDev().get(i).getType())) {
                        //查询数据库
                        List<SmartDev> smartDevs = DataSupport.findAll(SmartDev.class);
                        //对比数据库和本地查询到的智能锁设备，如果数据库没有就添加到数据库中去
                        Log.i(TAG, "查询智能设备smartDevs=" + smartDevs.size());
                        if(smartDevs.size()>0){
                            for (int devindex = 0; devindex < smartDevs.size(); devindex++) {
                                if (!smartDevs.get(devindex).getDevUid().equals(aDeviceList.getSmartDev().get(i).getDevUid())) {
                                    SmartDev dev = new SmartDev();
                                    smartUid = aDeviceList.getSmartDev().get(i).getDevUid();
                                    dev.setDevUid(smartUid);
                                    dev.setCtrUid(aDeviceList.getSmartDev().get(i).getCtrUid());
                                    dev.setStatus(aDeviceList.getSmartDev().get(i).getStatus());
                                    dev.setOrg(aDeviceList.getSmartDev().get(i).getOrg());
                                    dev.setType(aDeviceList.getSmartDev().get(i).getType());
                                    boolean success = dev.save();
                                    Log.i(TAG, "保存智能锁设备=" + success);
                                }
                            }
                        }else{
                            //如果表没有数据也要保存
                            SmartDev dev = new SmartDev();
                            smartUid = aDeviceList.getSmartDev().get(i).getDevUid();
                            dev.setDevUid(smartUid);
                            dev.setCtrUid(aDeviceList.getSmartDev().get(i).getCtrUid());
                            dev.setStatus(aDeviceList.getSmartDev().get(i).getStatus());
                            dev.setOrg(aDeviceList.getSmartDev().get(i).getOrg());
                            dev.setType(aDeviceList.getSmartDev().get(i).getType());
                            boolean success = dev.save();
                            Log.i(TAG, "保存智能锁设备=" + success);
                        }
                    }
                }
            }

            //存储设备列表
            if (aDeviceList.getDevice() != null && aDeviceList.getDevice().size() > 0) {
                for (int i = 0; i < aDeviceList.getDevice().size(); i++) {
                    //查询数据库
                    List<Device> devices = DataSupport.findAll(Device.class);
                    if (devices.size() == 0) {
                        Device dev = new Device();
                        dev.setStatus(aDeviceList.getDevice().get(i).getStatus());
                        dev.setIp(aDeviceList.getDevice().get(i).getIp());
                        dev.setUid(aDeviceList.getDevice().get(i).getUid());
                        boolean success = dev.save();
                        Log.i(TAG, "保存设备=" + success);
                    }else{
                        for (int devindex = 0; devindex < devices.size(); devindex++) {
                            if (!devices.get(devindex).getUid().equals(aDeviceList.getDevice().get(i).getUid())) {
                                Device dev = new Device();
                                dev.setStatus(aDeviceList.getDevice().get(i).getStatus());
                                dev.setIp(aDeviceList.getDevice().get(i).getIp());
                                dev.setUid(aDeviceList.getDevice().get(i).getUid());
                                boolean success = dev.save();
                                Log.i(TAG, "保存设备=" + success);
                            }
                        }
                    }
                    //对比数据库和本地查询到的智能锁设备，如果数据库没有就添加到数据库中去


                }
            }
        }

        mSmartLockListener.responseQueryResult(result);
    }

    @Override
    public void OnGetSetresult(String setResult) {
        mSmartLockListener.responseSetResult(setResult);
    }

    @Override
    public void wifiConnectUnReachable() {

    }
}
