package com.deplink.homegenius.manager.device.smartlock;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.Protocol.json.OpResult;
import com.deplink.homegenius.Protocol.json.QueryOptions;
import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.Protocol.json.device.lock.Record;
import com.deplink.homegenius.Protocol.json.device.lock.UserIdInfo;
import com.deplink.homegenius.Protocol.json.device.lock.alertreport.Info;
import com.deplink.homegenius.Protocol.packet.GeneralPacket;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.constant.SmartLockConstant;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import com.deplink.homegenius.manager.connect.remote.HomeGenius;
import com.deplink.homegenius.manager.connect.remote.RemoteConnectManager;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.util.Perfence;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.json.homegenius.LockUserId;
import com.deplink.sdk.android.sdk.rest.RestfulToolsHomeGenius;
import com.deplink.sdk.android.sdk.rest.RestfulToolsHomeGeniusString;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private RemoteConnectManager mRemoteConnectManager;
    private HomeGenius mHomeGenius;
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
    /**
     * 这个类设计成单例
     */
    private static SmartLockManager instance;
    private SmartDev currentSelectLock;

    private SmartLockManager() {

    }

    public boolean isEditSmartLock;

    public boolean isEditSmartLock() {
        return isEditSmartLock;
    }

    public void setEditSmartLock(boolean editSmartLock) {
        isEditSmartLock = editSmartLock;
    }

    public SmartDev getCurrentSelectLock() {
        Log.i(TAG, "当前选中的门锁所在房间列表大小=" + currentSelectLock.getRooms().size());
        return currentSelectLock;
    }

    public void setCurrentSelectLock(SmartDev currentSelectLock) {
        Log.i(TAG, "设置当前选中的门锁所在房间列表大小=" + currentSelectLock.getRooms().size() + "currentSelectLock=" + currentSelectLock.getName());
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
        Perfence.setContext(context);
        this.mSmartLockListenerList = new ArrayList<>();
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

    }

    //设置数据返回的接口
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
    //设置数据返回的接口------------------------------------------------------------end

    public void releaswSmartManager() {
        mLocalConnectmanager.removeLocalConnectListener(this);
    }

    //数据库操作函数
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
     * 更新设备所在房间
     */
    public void updateSmartDeviceInWhatRoom(Room room, String deviceUid) {
        Log.i(TAG, "更新智能设备所在的房间=start");
        //保存所在的房间
        //查询设备
        SmartDev smartDev = DataSupport.where("Uid=?", deviceUid).findFirst(SmartDev.class, true);
        //找到要更行的设备,设置关联的房间
        List<Room> rooms = new ArrayList<>();
        if (room != null) {
            rooms.add(room);
        } else {
            rooms.addAll(RoomManager.getInstance().getmRooms());
        }
        currentSelectLock.setRooms(rooms);
        smartDev.setRooms(rooms);
        boolean saveResult = smartDev.save();
        Log.i(TAG, "更新智能设备所在的房间=" + saveResult);
    }

    public boolean updateSmartDeviceGetway(GatwayDevice getwayDevice) {
        Log.i(TAG, "更新智能设备所在的网关=start");
        currentSelectLock.setGetwayDevice(getwayDevice);
        boolean saveResult = currentSelectLock.save();
        Log.i(TAG, "更新智能设备所在的网关=" + saveResult);
        return saveResult;
    }

    public List<SmartDev> getAllLock() {
        List<SmartDev> locklist = DataSupport.where("Type = ?", DeviceTypeConstant.TYPE.TYPE_LOCK).find(SmartDev.class);
        Log.i(TAG, "门锁设备列表大小=" + locklist.size());
        return locklist;
    }
    //数据库操作函数-----------------------------------------------------------end

    /**
     * 查询锁设备uid
     */
    public void queryLockUidHttp(String deviceUid) {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            return;
        }
        RestfulToolsHomeGeniusString.getSingleton().getLockUseId(userName, deviceUid, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "" + response.message());
                if (response.errorBody() != null) {
                    try {
                        Log.i(TAG, "" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.code() == 200) {
                    Log.i(TAG, "response body=" + response.body());
                    //{"selfid":"1000","alluser":[{"userid":1000,"username":"13691876442"}]}
                    if (response.body().contains("selfid")) {
                        Gson gson = new Gson();
                        UserIdInfo info = gson.fromJson(response.body(), UserIdInfo.class);
                        for (int i = 0; i < mSmartLockListenerList.size(); i++) {
                            mSmartLockListenerList.get(i).responseUserIdInfo(info);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    /**
     * 查询设备列表
     */
    public void setLockUidNameHttp(String deviceUid, LockUserId userIdBody) {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            return;
        }
        RestfulToolsHomeGenius.getSingleton().setLockUserIdName(userName, deviceUid, userIdBody, new Callback<DeviceOperationResponse>() {
            @Override
            public void onResponse(Call<DeviceOperationResponse> call, Response<DeviceOperationResponse> response) {
                Log.i(TAG, "" + response.message());
                if (response.errorBody() != null) {
                    try {
                        Log.i(TAG, "" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.code() == 200) {
                    Log.i(TAG, "response body=" + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<DeviceOperationResponse> call, Throwable t) {

            }
        });
    }


    /**
     * 报警记录设备上报，没有查询接口，所以保存在数据库中，需要去数据库获取
     *
     * @return
     */
    public List<Info> getAlarmRecord() {
        return currentSelectLock.getAlarmInfo();
    }

    public boolean clearAlarmRecord() {
        currentSelectLock.setAlarmInfo(null);
        DataSupport.deleteAll(Record.class);
        return currentSelectLock.save();
    }

    /**
     * 查询开锁记录
     */
    public void queryLockHistory(boolean isLocal, int queryNumber, String userId) {
        if (isLocal) {
            QueryOptions queryCmd = new QueryOptions();
            queryCmd.setOP("QUERY");
            queryCmd.setMethod("SmartLock");
            queryCmd.setCommand("HisRecord");
            queryCmd.setUserID(userId);
            queryCmd.setQuery_Num(queryNumber);
            queryCmd.setSmartUid(currentSelectLock.getMac());
            Log.i(TAG, "查询开锁记录设备smartUid=" + currentSelectLock.getMac() + "queryNumber=" + queryNumber);
            queryCmd.setTimestamp();
            Gson gson = new Gson();
            String text = gson.toJson(queryCmd);
            packet.packOpenLockListData(text.getBytes(), currentSelectLock.getUid());
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mLocalConnectmanager.getOut(packet.data);
                }
            });
        } else {
            String uuid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
            GatwayDevice device = currentSelectLock.getGetwayDevice();
            if (device == null) {
                device = DataSupport.where("Status = ?", "在线").findFirst(GatwayDevice.class);
            }
            if(device==null){
                device= DataSupport.findFirst(GatwayDevice.class);
            }
            if (device!=null && device.getTopic() != null && !device.getTopic().equals("")) {
                Log.i(TAG, "device.getTopic()=" + device.getTopic());
                mHomeGenius.queryLockHistory(currentSelectLock, device.getTopic(), uuid, queryNumber, userId);
            }
        }
    }

    public void queryLockStatu() {
        Log.i(TAG, "查询锁设备状态"+mLocalConnectmanager.isLocalconnectAvailable());
        if (mLocalConnectmanager.isLocalconnectAvailable()) {
            QueryOptions queryCmd = new QueryOptions();
            queryCmd.setOP("SET");
            queryCmd.setMethod("SMART_LOCK");
            queryCmd.setCommand("query");
            queryCmd.setSmartUid(currentSelectLock.getMac());
            queryCmd.setTimestamp();
            Gson gson = new Gson();
            String text = gson.toJson(queryCmd);
            packet.packOpenLockListData(text.getBytes(), currentSelectLock.getUid());
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mLocalConnectmanager.getOut(packet.data);
                }
            });
        } else {
            String uuid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
            GatwayDevice device = currentSelectLock.getGetwayDevice();
            if (device == null) {
                device = DataSupport.where("Status = ?", "在线").findFirst(GatwayDevice.class);
            }
            if(device==null){
                device= DataSupport.findFirst(GatwayDevice.class);
            }
            if (device != null) {
                Log.i(TAG, "device.getTopic()=" + device.getTopic());
                if (device.getTopic() != null && !device.getTopic().equals("")) {
                    mHomeGenius.queryLockStatu(currentSelectLock, device.getTopic(), uuid);
                }
            }

        }
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
        Log.i(TAG, "设置智能锁操作 cmd=" + cmd + "userId=" + userId + "本地操作=" + mLocalConnectmanager.isLocalconnectAvailable() +
                "远程操作=" + mRemoteConnectManager.isRemoteConnectAvailable()
                + "managePasswd=" + managePasswd
        );
        if (mLocalConnectmanager.isLocalconnectAvailable()) {
            QueryOptions queryCmd = new QueryOptions();
            queryCmd.setOP("SET");
            queryCmd.setMethod("SmartLock");
            queryCmd.setSmartUid(currentSelectLock.getMac());
            queryCmd.setCommand(cmd);
            queryCmd.setTimestamp();
            if (authPwd != null) {
                queryCmd.setAuthPwd(authPwd);
            } else {
                queryCmd.setAuthPwd("30");
            }
            queryCmd.setUserID(userId);
            queryCmd.setManagePwd(managePasswd);
            if (limitedTime != null) {
                queryCmd.setTime(limitedTime);
            } else {
                queryCmd.setTime("30");
            }
            Gson gson = new Gson();
            String text = gson.toJson(queryCmd);
            packet.packSetCmdData(text.getBytes(), currentSelectLock.getUid());

            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mLocalConnectmanager.getOut(packet.data);
                }
            });
        } else {

            String uuid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
            GatwayDevice device = currentSelectLock.getGetwayDevice();
            if (device == null) {
                device= DataSupport.where("Status = ?", "在线").findFirst(GatwayDevice.class);
            }
            if(device==null){
                device= DataSupport.findFirst(GatwayDevice.class);
            }
            if (device.getTopic() != null && !device.getTopic().equals("")) {
                Log.i(TAG, "device.getTopic()=" + device.getTopic());
                Log.i(TAG, "远程锁操作命令=" + cmd);
                mHomeGenius.setSmartLockParmars(currentSelectLock, device.getTopic(), uuid, cmd, userId, managePasswd, authPwd, limitedTime);
            }

        }
    }

    @Override
    public void OnBindAppResult(String uid) {
        for (int i = 0; i < mSmartLockListenerList.size(); i++) {
            mSmartLockListenerList.get(i).responseBind(uid);
        }
    }

    @Override
    public void OnGetQueryresult(String result) {
        Gson gson = new Gson();
        //SmartLock-HisRecord"
        if (result.contains("HisRecord")) {
            for (int i = 0; i < mSmartLockListenerList.size(); i++) {
                Log.i(TAG, "SmartLockListener ==" + mSmartLockListenerList.get(i).toString());
                mSmartLockListenerList.get(i).responseQueryResult(result);
            }
        }
        OpResult type = gson.fromJson(result, OpResult.class);
        Log.i(TAG, "门锁管理器查询结果返回:" + result);
        if (type != null && type.getOP().equalsIgnoreCase("REPORT") &&
                (type.getMethod().equalsIgnoreCase("SMART_LOCK") || type.getMethod().equalsIgnoreCase("SmartLock"))) {
            switch (type.getCommand()) {
                case SmartLockConstant.CMD.QUERY:
                    for (int i = 0; i < mSmartLockListenerList.size(); i++) {
                        mSmartLockListenerList.get(i).responseLockStatu(type.getRecordNum(), type.getLockStatus());
                    }
                    break;
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
        OpResult type = gson.fromJson(setResult, OpResult.class);
        if (type != null && type.getOP().equals("REPORT") && type.getMethod().equals("SmartLock")) {
            switch (type.getCommand()) {
                case SmartLockConstant.CMD.OPEN:
                    switch (type.getResult()) {
                        case SmartLockConstant.OPENLOCK.TIMEOUT:
                            setResult = "开锁超时";
                            break;
                        case SmartLockConstant.OPENLOCK.SUCCESS:
                            setResult = "开锁成功";
                            break;
                        case SmartLockConstant.OPENLOCK.PASSWORDERROR:
                            setResult = "密码错误";
                            break;
                        case SmartLockConstant.OPENLOCK.FAIL:
                            setResult = "开锁失败";
                            break;
                    }
                    break;
                case SmartLockConstant.CMD.ONCE:
                case SmartLockConstant.CMD.PERMANENT:
                case SmartLockConstant.CMD.TIMELIMIT:
                    switch (type.getResult()) {
                        case SmartLockConstant.AUTH.TIMEOUT:
                            setResult = "录入超时";
                            break;
                        case SmartLockConstant.AUTH.SUCCESS:
                            setResult = "录入成功";
                            break;
                        case SmartLockConstant.AUTH.PASSWORDERROR:
                            setResult = "密码错误";
                            break;
                        case SmartLockConstant.AUTH.FAIL:
                            setResult = "录入失败";
                            break;
                        case SmartLockConstant.AUTH.FORBADE:
                            setResult = "禁止操作";
                            break;
                    }
                    break;

            }
            Log.i(TAG, "设置结果=" + setResult);
            for (int i = 0; i < mSmartLockListenerList.size(); i++) {
                mSmartLockListenerList.get(i).responseSetResult(setResult);
            }

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
        for (int i = 0; i < alarmList.size(); i++) {
            Info alarm;
            alarm = alarmList.get(i);
            Log.i(TAG, alarm.toString());
            alarm.save();
            currentSelectLock.setAlarmInfo(alarmList);
        }
        currentSelectLock.save();
    }


}
