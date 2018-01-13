package com.deplink.homegenius.manager.device.remoteControl;

import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.Protocol.json.QueryOptions;
import com.deplink.homegenius.Protocol.json.RemoteControlOpResult;
import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.Protocol.json.device.lock.alertreport.Info;
import com.deplink.homegenius.Protocol.packet.GeneralPacket;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.constant.DeviceTypeConstant;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.util.Perfence;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/11/22.
 * private RemoteControlManager mRemoteControlManager;
 * mRemoteControlManager=RemoteControlManager.getInstance();
 * mRemoteControlManager.InitRemoteControlManager(this,this);
 */
public class RemoteControlManager implements LocalConnecteListener {
    private static final String TAG = "RemoteControlManager";
    /**
     * 这个类设计成单例
     */
    private static RemoteControlManager instance;
    private LocalConnectmanager mLocalConnectmanager;
    private GeneralPacket packet;
    private Context mContext;
    private List<RemoteControlListener> mRemoteControlListenerList;
    private Gson gson;
    private List<SmartDev> mRemoteControlDeviceList;
    private ExecutorService cachedThreadPool;
    /**
     * 当前选中的遥控设备
     */
    private SmartDev mSelectRemoteControlDevice;
    /**
     * 当前选中的遥控器设备绑定的物理遥控器
     */
    private SmartDev mRealRemoteControlDevice;
    private int currentLearnByHandKeyName;
    private String currentLearnByHandTypeName;

    public String getCurrentLearnByHandTypeName() {
        return currentLearnByHandTypeName;
    }

    public void setCurrentLearnByHandTypeName(String currentLearnByHandTypeName) {
        this.currentLearnByHandTypeName = currentLearnByHandTypeName;
    }

    public int getCurrentLearnByHandKeyName() {
        return currentLearnByHandKeyName;
    }

    public void setCurrentLearnByHandKeyName(int currentLearnByHandKeyName) {
        this.currentLearnByHandKeyName = currentLearnByHandKeyName;
    }

    public SmartDev getmRealRemoteControlDevice() {

        return  findRemotecontrolDevice().get(0);
    }

    public void setmRealRemoteControlDevice(SmartDev mRealRemoteControlDevice) {
        this.mRealRemoteControlDevice = mRealRemoteControlDevice;
    }
    private boolean currentActionIsAddactionQuickLearn;

    public boolean isCurrentActionIsAddactionQuickLearn() {
        return currentActionIsAddactionQuickLearn;
    }

    public void setCurrentActionIsAddactionQuickLearn(boolean currentActionIsAddactionQuickLearn) {
        this.currentActionIsAddactionQuickLearn = currentActionIsAddactionQuickLearn;
    }

    private boolean currentActionIsAddDevice;
    /**
     * 当前是添加设备还是配置遥控器按键列表（快速学习）
     * @return
     */
    public boolean isCurrentActionIsAddDevice() {
        return currentActionIsAddDevice;
    }

    public void setCurrentActionIsAddDevice(boolean currentActionIsAddDevice) {
        this.currentActionIsAddDevice = currentActionIsAddDevice;
    }

    public SmartDev getmSelectRemoteControlDevice() {
        return mSelectRemoteControlDevice;
    }

    public void setmSelectRemoteControlDevice(SmartDev mSelectRemoteControlDevice) {
        Log.i(TAG,"当前选中的遥控器UID="+mSelectRemoteControlDevice.getUid()+"物理遥控器uid="+mSelectRemoteControlDevice.getRemotecontrolUid());
        this.mSelectRemoteControlDevice = mSelectRemoteControlDevice;
    }

    public List<SmartDev> findAllRemotecontrolDevice() {
        List<SmartDev> newsList = DataSupport.where("Type = ?", "IRMOTE_V2").find(SmartDev.class);
        Log.i(TAG, "查找所有的智能设备,设备个数=" + newsList.size());
        return newsList;
    }
    public List<SmartDev> queryAllRemotecontrol() {
        List<SmartDev>smartDevs=new ArrayList<>();
        smartDevs.addAll(DataSupport.where("Type= ? ", "IRMOTE_V2").find(SmartDev.class));
        Log.i(TAG,"遥控器列表大小="+smartDevs.size());
        return smartDevs;
    }
    /**
     * 找到遥控器挂载在哪个物理遥控器下面
     * @return
     */
    public List<SmartDev> findRemotecontrolDevice() {
        Log.i(TAG, "查找绑定的物理遥控器,uid=" + mSelectRemoteControlDevice.getRemotecontrolUid());
        Log.i(TAG, "查找绑定的物理遥控器,uid=" + mSelectRemoteControlDevice.getUid());
        return DataSupport.where("Uid = ?", mSelectRemoteControlDevice.getRemotecontrolUid()).find(SmartDev.class);
    }
    public static synchronized RemoteControlManager getInstance() {
        if (instance == null) {
            instance = new RemoteControlManager();
        }
        return instance;
    }

    public void InitRemoteControlManager(Context context, RemoteControlListener listener) {
        this.mContext = context;
        if (mLocalConnectmanager == null) {
            mLocalConnectmanager = LocalConnectmanager.getInstance();
            String uuid= Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
            mLocalConnectmanager.InitLocalConnectManager(context, uuid);
        }
        mLocalConnectmanager.addLocalConnectListener(this);
        packet = new GeneralPacket(mContext);
        mRemoteControlListenerList = new ArrayList<>();
        if(listener!=null){
            addRemoteControlListener(listener);
        }
        gson = new Gson();
        mRemoteControlDeviceList = new ArrayList<>();
        mRemoteControlDeviceList.addAll(DataSupport.where("Type=?", "IRMOTE_V2").find(SmartDev.class));
        //TODO 当前选中的遥控器
       // if (mRemoteControlDeviceList.size() > 0) {
        //    mSelectRemoteControlDevice = mRemoteControlDeviceList.get(0);
     //   }
        if (cachedThreadPool == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
        }
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
        mSelectRemoteControlDevice.setRooms(rooms);
        smartDev.setRooms(rooms);
        boolean saveResult = smartDev.save();
        Log.i(TAG, "更新智能设备所在的房间=" + saveResult);


    }
    /**
     * 更新设备所在房间
     */
    public void updateSelectedDeviceInWhatRoom(Room room) {
        Log.i(TAG, "更新智能设备所在的房间=start");
        //保存所在的房间
        //查询设备
        //找到要更行的设备,设置关联的房间
        List<Room> rooms = new ArrayList<>();
        if (room != null) {
            rooms.add(room);
        } else {
            rooms.addAll(RoomManager.getInstance().getmRooms());
        }
        SmartDev smartDev = DataSupport.where("Uid=?", mSelectRemoteControlDevice.getUid()).findFirst(SmartDev.class, true);
        mSelectRemoteControlDevice.setRooms(rooms);
        smartDev.setRooms(rooms);
        boolean saveResult = smartDev.save();
        Log.i(TAG, "更新智能设备所在的房间=" + saveResult);
    }

    public void study() {
        QueryOptions cmd = new QueryOptions();
        cmd.setOP("SET");
        cmd.setMethod("IrmoteV2");
        cmd.setTimestamp();
        cmd.setSmartUid(mSelectRemoteControlDevice.getRemotecontrolUid());
        cmd.setCommand("Study");
        String text = gson.toJson(cmd);
        packet.packRemoteControlData(text.getBytes(), mSelectRemoteControlDevice.getRemotecontrolUid());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });

    }

    public void sendData(String data) {
        //TODO 当前选中的遥控器和当前选中的物理遥控器混乱
        Log.i(TAG,"mSelectRemoteControlDevice="+mSelectRemoteControlDevice.getRemotecontrolUid());
        String controlUid=mSelectRemoteControlDevice.getRemotecontrolUid();
        if(mSelectRemoteControlDevice.getRemotecontrolUid()==null){
            controlUid=mSelectRemoteControlDevice.getUid();
        }
        QueryOptions cmd = new QueryOptions();
        cmd.setOP("SET");
        cmd.setMethod("IrmoteV2");
        cmd.setTimestamp();
        cmd.setSmartUid(controlUid);
        cmd.setCommand("Send");
        cmd.setData(data);
        String text = gson.toJson(cmd);
        Log.i(TAG,"mSelectRemoteControlDevice!=null"+(mSelectRemoteControlDevice!=null));
        Log.i(TAG,""+text+" mSelectRemoteControlDevice.getRemotecontrolUid()!=null"+( mSelectRemoteControlDevice.getRemotecontrolUid()));
        packet.packRemoteControlData(text.getBytes(), controlUid);
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
    public boolean addDeviceDbLocal(SmartDev device, Room atRoom) {
        List<Room> rooms = new ArrayList<>();
        if (atRoom == null) {
            rooms.addAll(RoomManager.getInstance().getmRooms());
        } else {
            rooms.add(atRoom);
        }
        device.setRooms(rooms);
        device.setStatus("在线");
        boolean addResult = device.save();
        if (!addResult) {
            Log.i(TAG, "数据库中已存在相同设备，不必要添加");
        }
        Log.i(TAG, "向数据库中添加一条智能设备数据=" + addResult);
        return addResult;
    }

    public boolean judgAirconditionDeviceisAdded(String name) {
        //sql 多条件查询
        List<SmartDev> smartDevs = DataSupport.where("Type= ? and name= ? ",
                DeviceTypeConstant.TYPE.TYPE_AIR_REMOTECONTROL,name).find(SmartDev.class);
        return smartDevs.size() > 0;
    }
    public boolean judgTvDeviceisAdded(String name) {
        //sql 多条件查询
        List<SmartDev> smartDevs = DataSupport.where("Type= ? and name= ? ",
                DeviceTypeConstant.TYPE.TYPE_TV_REMOTECONTROL,name).find(SmartDev.class);
        return smartDevs.size() > 0;
    }
    public boolean judgTvBoxDeviceisAdded(String name) {
        //sql 多条件查询
        List<SmartDev> smartDevs = DataSupport.where("Type= ? and name= ? ",
                DeviceTypeConstant.TYPE.TYPE_TVBOX_REMOTECONTROL,name).find(SmartDev.class);
        return smartDevs.size() > 0;
    }

    public boolean updateSmartDeviceGetway(GatwayDevice getwayDevice) {
        Log.i(TAG, "更新智能设备所在的网关=start");
        mSelectRemoteControlDevice.setGetwayDevice(getwayDevice);
        boolean saveResult = mSelectRemoteControlDevice.save();
        Log.i(TAG, "更新智能设备所在的网关=" + saveResult);
        return saveResult;
    }
    public int deleteCurrentSelectDevice() {
        int affectcolumn = DataSupport.deleteAll(SmartDev.class, "Uid=?",mSelectRemoteControlDevice.getUid());
        Log.i(TAG, "删除一个智能设备，删除影响的行数=" + affectcolumn);
        return affectcolumn;
    }
    public boolean saveCurrentSelectDeviceName(String name) {
        mSelectRemoteControlDevice.setName(name);
        boolean result=mSelectRemoteControlDevice.save();
        Log.i(TAG, "修改名称=" + result);
        return result;
    }

    public void addRemoteControlListener(RemoteControlListener listener) {
        if (listener != null && !mRemoteControlListenerList.contains(listener)) {
            this.mRemoteControlListenerList.add(listener);
        }
    }

    public void removeRemoteControlListener(RemoteControlListener listener) {
        if (listener != null && mRemoteControlListenerList.contains(listener)) {
            this.mRemoteControlListenerList.remove(listener);
        }
    }


    @Override
    public void OnBindAppResult(String uid) {

    }

    @Override
    public void OnGetQueryresult(String devList) {

    }

    @Override
    public void OnGetSetresult(String setResult) {
        RemoteControlOpResult result = gson.fromJson(setResult, RemoteControlOpResult.class);
        Log.i(TAG, TAG + ":获取设置结果setResult=" + setResult);
        if (result != null) {
            if(setResult.contains("Study")){
                for (int i = 0; i < mRemoteControlListenerList.size(); i++) {
                    mRemoteControlListenerList.get(i).responseQueryResult(setResult);
                }
            }else{
                for (int i = 0; i < mRemoteControlListenerList.size(); i++) {
                    mRemoteControlListenerList.get(i).responseQueryResult(result.getCommand() + result.getResult());
                }
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

    @Override
    public void onGetalarmRecord(List<Info> alarmList) {

    }

}
