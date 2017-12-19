package deplink.com.smartwirelessrelay.homegenius.manager.device.remoteControl;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.RemoteControlOpResult;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.Info;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.constant.DeviceType;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

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

    public SmartDev getmSelectRemoteControlDevice() {
        return mSelectRemoteControlDevice;
    }

    public void setmSelectRemoteControlDevice(SmartDev mSelectRemoteControlDevice) {
        this.mSelectRemoteControlDevice = mSelectRemoteControlDevice;
    }

    public List<SmartDev> findAllRemotecontrolDevice() {
        List<SmartDev> newsList = DataSupport.where("Type = ?", "IRMOTE_V2").find(SmartDev.class);
        Log.i(TAG, "查找所有的智能设备,设备个数=" + newsList.size());
        return newsList;
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
            // mLocalConnectmanager.InitLocalConnectManager(mContext, AppConstant.BIND_APP_MAC);
        }
        mLocalConnectmanager.addLocalConnectListener(this);
        packet = new GeneralPacket(mContext);
        mRemoteControlListenerList = new ArrayList<>();
        addRemoteControlListener(listener);
        gson = new Gson();
        mRemoteControlDeviceList = new ArrayList<>();
        mRemoteControlDeviceList.addAll(DataSupport.where("Type=?", "IRMOTE_V2").find(SmartDev.class));
        //TODO 当前选中的遥控器
        if (mRemoteControlDeviceList.size() > 0) {
            mSelectRemoteControlDevice = mRemoteControlDeviceList.get(0);
        }
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

    public void study() {

        QueryOptions cmd = new QueryOptions();
        cmd.setOP("SET");
        cmd.setMethod("IrmoteV2");
        cmd.setTimestamp();
        cmd.setSmartUid(mSelectRemoteControlDevice.getUid());
        cmd.setCommand("Study");
        String text = gson.toJson(cmd);
        packet.packRemoteControlData(text.getBytes(), null);
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });

    }

    public void sendData(final String data) {
        QueryOptions cmd = new QueryOptions();
        cmd.setOP("SET");
        cmd.setMethod("IrmoteV2");
        cmd.setTimestamp();
        cmd.setSmartUid(mSelectRemoteControlDevice.getUid());
        cmd.setCommand("Send");
        cmd.setData(data);
        String text = gson.toJson(cmd);
        packet.packRemoteControlData(text.getBytes(), mSelectRemoteControlDevice.getUid());
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
                DeviceType.TYPE.TYPE_AIR_REMOTECONTROL,name).find(SmartDev.class);
        return smartDevs.size() > 0;
    }
    public boolean judgTvDeviceisAdded(String name) {
        //sql 多条件查询
        List<SmartDev> smartDevs = DataSupport.where("Type= ? and name= ? ",
                DeviceType.TYPE.TYPE_TV_REMOTECONTROL,name).find(SmartDev.class);
        return smartDevs.size() > 0;
    }
    public boolean judgTvBoxDeviceisAdded(String name) {
        //sql 多条件查询
        List<SmartDev> smartDevs = DataSupport.where("Type= ? and name= ? ",
                DeviceType.TYPE.TYPE_TVBOX_REMOTECONTROL,name).find(SmartDev.class);
        return smartDevs.size() > 0;
    }
    public List<SmartDev> queryAllRemotecontrol() {
        List<SmartDev>smartDevs=new ArrayList<>();
        smartDevs.addAll(DataSupport.where("Type= ? ", "IRMOTE_V2"/*DeviceType.TYPE.TYPE_REMOTECONTROL*/).find(SmartDev.class));
        Log.i(TAG,"遥控器列表大小="+smartDevs.size());
        return smartDevs;
    }
    public boolean updateSmartDeviceGetway(Device getwayDevice) {
        Log.i(TAG, "更新智能设备所在的网关=start");
        mSelectRemoteControlDevice.setGetwayDevice(getwayDevice);
        boolean saveResult = mSelectRemoteControlDevice.save();
        Log.i(TAG, "更新智能设备所在的网关=" + saveResult);
        return saveResult;
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
            for (int i = 0; i < mRemoteControlListenerList.size(); i++) {
                mRemoteControlListenerList.get(i).responseQueryResult(result.getCommand() + result.getResult());
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
