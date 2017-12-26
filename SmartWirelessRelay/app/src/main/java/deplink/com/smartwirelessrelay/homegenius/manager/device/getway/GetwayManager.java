package deplink.com.smartwirelessrelay.homegenius.manager.device.getway;

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
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.Info;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import deplink.com.smartwirelessrelay.homegenius.manager.room.RoomManager;

/**
 * Created by Administrator on 2017/11/22.
 */
public class GetwayManager implements LocalConnecteListener {
    private static final String TAG = "GetwayManager";
    /**
     * 这个类设计成单例
     */
    private static GetwayManager instance;
    private Context mContext;
    /**
     * 当前要添加设备的识别码，二维码扫码出来的
     */
    private String currentAddDevice;
    private GeneralPacket packet;
    private LocalConnectmanager mLocalConnectmanager;
    private Device currentSelectGetwayDevice;
    private Device currentAddGetwayDevice;
    private ExecutorService cachedThreadPool;
    public String getCurrentAddDevice() {
        Log.i(TAG, "获取当前添加设备：" + currentAddDevice);
        return currentAddDevice;
    }

    public void setCurrentAddDevice(String currentAddDevice) {
        this.currentAddDevice = currentAddDevice;
    }


    public static synchronized GetwayManager getInstance() {
        if (instance == null) {
            instance = new GetwayManager();
        }
        return instance;
    }

    public void InitGetwayManager(Context context, GetwayListener listener) {
        this.mContext = context;
        mGetwayListenerList = new ArrayList<>();
        if (mLocalConnectmanager == null) {
            mLocalConnectmanager = LocalConnectmanager.getInstance();
            // mLocalConnectmanager.InitLocalConnectManager(mContext, AppConstant.BIND_APP_MAC);
        }
        mLocalConnectmanager.addLocalConnectListener(this);
        packet = new GeneralPacket(mContext);
        if (cachedThreadPool == null) {
            cachedThreadPool = Executors.newCachedThreadPool();
        }
        addGetwayListener(listener);
    }

    private List<GetwayListener> mGetwayListenerList;

    public void addGetwayListener(GetwayListener listener) {
        if (listener != null && !mGetwayListenerList.contains(listener)) {
            this.mGetwayListenerList.add(listener);
        }
    }
    public void removeGetwayListener(GetwayListener listener) {
        if (listener != null && mGetwayListenerList.contains(listener)) {
            this.mGetwayListenerList.remove(listener);
        }
    }

    /**
     * 删除数据库中的一个网关设备
     */
    public int deleteDBGetwayDevice(String uid) {
        int affectcolumn = DataSupport.deleteAll(Device.class, "Uid=?", uid);
        Log.i(TAG, "删除一个网关设备，删除影响的行数=" + affectcolumn);
        return affectcolumn;
    }



    public List<Device> getAllGetwayDevice() {
        List<Device> list = DataSupport.findAll(Device.class, true);
        if (list.size() > 0) {
            Log.i(TAG, "查询到的网关设备个数=" + list.size() + list.get(0).getUid());
        }
        return list;
    }


    public Device getCurrentSelectGetwayDevice() {
        return currentSelectGetwayDevice;
    }

    public void setCurrentSelectGetwayDevice(Device currentSelectGetwayDevice) {
        this.currentSelectGetwayDevice = currentSelectGetwayDevice;
    }

    public void deleteGetwayDevice() {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("DELETE");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        List<Device> devs = new ArrayList<>();
        //设备赋值
        Device dev = new Device();
        dev.setUid(currentSelectGetwayDevice.getUid());
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

    public boolean addDBGetwayDevice(QrcodeSmartDevice device) {
        //查询设备
        currentAddGetwayDevice = new Device();
        currentAddGetwayDevice.setUid(device.getSn());
        currentAddGetwayDevice.setName(device.getName());
        boolean addResult = currentAddGetwayDevice.save();
        Log.i(TAG, "向数据库中添加一条网关设备数据=" + addResult);
        if (!addResult) {
            Log.i(TAG, "数据库中已存在相同网关设备，不必要添加");
        }
        return addResult;
    }

    /**
     * @param room      更新房间
     * @param deviceUid 当前网关设备
     */
    public void updateGetwayDeviceInWhatRoom(Room room, String deviceUid) {

        //保存所在的房间
        //查询设备
        Device getwayDevice = DataSupport.where("Uid=?", deviceUid).findFirst(Device.class, true);
        //找到要更行的设备,设置关联的房间
        List<Room> roomList = new ArrayList<>();
        if (room != null) {
            roomList.add(room);
        } else {
            roomList.addAll(RoomManager.getInstance().getmRooms());
        }
        getwayDevice.setRoomList(roomList);
        boolean result = getwayDevice.save();
        Log.i(TAG, "更新网关所在房间" + result);

    }

    public void updateGetwayDeviceName(String name) {
        currentSelectGetwayDevice.setName(name);
        boolean result = currentSelectGetwayDevice.save();
        Log.i(TAG, "更新网关名称" + result);
    }

    /**
     * 绑定网关，中继器
     */
    public void bindDevice(QrcodeSmartDevice device) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        List<Device> devs = new ArrayList<>();
        //设备赋值
        Device dev = new Device();
        //调试  uid 77685180654101946200316696479888
        dev.setUid(device.getSn());
        dev.setMac(device.getAd());
        dev.setType(device.getTp());
        devs.add(dev);
        queryCmd.setDevice(devs);
        Gson gson = new Gson();
        Log.i(TAG,"绑定网关:"+queryCmd.toString());
        String text = gson.toJson(queryCmd);
        packet.packSendDevsData(text.getBytes());
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }

    public void deleteGetwayDeviceInWhatRoom(final Room room, final String deviceUid) {


        //保存所在的房间
        //查询设备
        Device getwayDevice = DataSupport.where("Uid=?", deviceUid).findFirst(Device.class, true);
        //找到要更行的设备,设置关联的房间
        List<Room> roomList = new ArrayList<>();
        roomList.addAll(getwayDevice.getRoomList());
        for (int i = 0; i < roomList.size(); i++) {
            if (roomList.get(i).getRoomName().equals(room.getRoomName())) {
                roomList.remove(i);
            }
        }
        getwayDevice.setRoomList(roomList);
        boolean saveResult = getwayDevice.save();
        Log.i(TAG, "deleteGetwayDeviceInWhatRoom saveResult=" + saveResult);


    }


    @Override
    public void OnBindAppResult(String uid) {

    }

    @Override
    public void OnGetQueryresult(String devList) {

    }

    @Override
    public void OnGetSetresult(String setResult) {

    }

    @Override
    public void OnGetBindresult(String setResult) {
        for (int i = 0; i < mGetwayListenerList.size(); i++) {
            mGetwayListenerList.get(i).responseResult(setResult);
        }
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
