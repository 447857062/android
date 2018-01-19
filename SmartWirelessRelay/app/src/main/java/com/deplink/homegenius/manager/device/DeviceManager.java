package com.deplink.homegenius.manager.device;

import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.Protocol.json.OpResult;
import com.deplink.homegenius.Protocol.json.QueryOptions;
import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.DeviceList;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.Protocol.json.device.lock.alertreport.Info;
import com.deplink.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import com.deplink.homegenius.Protocol.packet.GeneralPacket;
import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import com.deplink.homegenius.manager.connect.local.tcp.LocalConnectmanager;
import com.deplink.homegenius.manager.connect.remote.HomeGenius;
import com.deplink.homegenius.manager.connect.remote.RemoteConnectManager;
import com.deplink.homegenius.manager.room.RoomManager;
import com.deplink.homegenius.util.ParseUtil;
import com.deplink.homegenius.util.Perfence;
import com.deplink.homegenius.view.toast.ToastSingleShow;
import com.deplink.sdk.android.sdk.homegenius.DeviceAddBody;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.Deviceprops;
import com.deplink.sdk.android.sdk.homegenius.VirtualDeviceAddBody;
import com.deplink.sdk.android.sdk.rest.RestfulToolsHomeGenius;
import com.deplink.sdk.android.sdk.rest.RestfulToolsHomeGeniusString;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
    private boolean isStartFromExperience;
    private boolean isStartFromHomePage;
    private boolean isExperCenterStartFromHomePage;
    private RemoteConnectManager mRemoteConnectManager;
    private HomeGenius mHomeGenius;

    public boolean isExperCenterStartFromHomePage() {
        return isExperCenterStartFromHomePage;
    }

    public void setExperCenterStartFromHomePage(boolean experCenterStartFromHomePage) {
        isExperCenterStartFromHomePage = experCenterStartFromHomePage;
    }

    public boolean isStartFromExperience() {
        return isStartFromExperience;
    }

    public void setStartFromExperience(boolean startFromExperience) {
        isStartFromExperience = startFromExperience;
    }

    public boolean isStartFromHomePage() {
        return isStartFromHomePage;
    }

    public void setStartFromHomePage(boolean startFromHomePage) {
        isStartFromHomePage = startFromHomePage;
    }

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
        Log.i(TAG, "本地接口查询设备列表 mLocalConnectmanager.isLocalconnectAvailable():"
                + mLocalConnectmanager.isLocalconnectAvailable() +
                "mRemoteConnectManager.isRemoteConnectAvailable():" + mRemoteConnectManager.isRemoteConnectAvailable()
        );
        if (mLocalConnectmanager.isLocalconnectAvailable()) {
            QueryOptions queryCmd = new QueryOptions();
            queryCmd.setOP("QUERY");
            queryCmd.setMethod("DevList");
            queryCmd.setTimestamp();
            Gson gson = new Gson();
            String text = gson.toJson(queryCmd);
            packet.packQueryDevListData(text.getBytes(), false, null);
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mLocalConnectmanager.getOut(packet.data);
                }
            });
        } else if (mRemoteConnectManager.isRemoteConnectAvailable()) {
            String uuid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
            GatwayDevice device = DataSupport.findFirst(GatwayDevice.class);
            Log.i(TAG, "device.getTopic()=" + device.getTopic());
            if (device.getTopic() != null && !device.getTopic().equals("")) {
                mHomeGenius.queryDeviceList(device.getTopic(), uuid);
            }
        }

    }

    /**
     * 查询设备列表
     */
    public void queryDeviceListHttp() {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            return;
        }
        RestfulToolsHomeGeniusString.getSingleton().getDeviceInfo(userName, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "" + response.code());
                Log.i(TAG, "" + response.message());
                if (response.code() == 200) {
                    ArrayList<Deviceprops> list = ParseUtil.jsonToArrayList(response.body(), Deviceprops.class);
                    for (int i = 0; i < list.size(); i++) {
                        Log.i(TAG, "device=" + list.get(i).toString());
                    }
                    for (int i = 0; i < mDeviceListenerList.size(); i++) {
                        mDeviceListenerList.get(i).responseQueryHttpResult(list);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void addDeviceHttp(DeviceAddBody device) {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            return;
        }
        Log.i(TAG, device.toString());
        RestfulToolsHomeGenius.getSingleton().addDevice(userName, device, new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.i(TAG, "" + response.code());
                Log.i(TAG, "" + response.message());
                if (response.errorBody() != null) {
                    try {
                        Log.i(TAG, "" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                DeviceOperationResponse deviceOperationResponse = null;
                if (response.body() != null) {
                    Gson gson = new Gson();
                    deviceOperationResponse =
                            gson.fromJson(response.body().toString(), DeviceOperationResponse.class);
                }
                if (response.code() == 200) {
                    Log.i(TAG, "" + response.body().toString());
                    for (int i = 0; i < mDeviceListenerList.size(); i++) {
                        mDeviceListenerList.get(i).responseAddDeviceHttpResult(
                                deviceOperationResponse
                        );
                    }
                } else if (response.code() == 403) {
                    int errorcode = deviceOperationResponse.getErrcode();
                    if (errorcode == 100006) {
                        ToastSingleShow.showText(mContext, "没有授权,请让第一次添加此设备的用户给你授权");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i(TAG, "" + t.getMessage());
            }
        });
    }
    public void addVirtualDeviceHttp(VirtualDeviceAddBody device) {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            return;
        }
        Log.i(TAG, device.toString());
        RestfulToolsHomeGenius.getSingleton().addVirtualDevice(userName, device, new Callback<DeviceOperationResponse>() {
            @Override
            public void onResponse(Call<DeviceOperationResponse> call, Response<DeviceOperationResponse> response) {
                Log.i(TAG, "" + response.code());
                Log.i(TAG, "" + response.message());
                if (response.errorBody() != null) {
                    try {
                        Log.i(TAG, "" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.code() == 200) {
                    Log.i(TAG, "" + response.body().toString());
                    for (int i = 0; i < mDeviceListenerList.size(); i++) {
                        mDeviceListenerList.get(i).responseAddVirtualDeviceHttp(
                                response.body()
                        );
                    }
                } else if (response.code() == 403) {

                }
            }

            @Override
            public void onFailure(Call<DeviceOperationResponse> call, Throwable t) {
                Log.i(TAG, "" + t.getMessage());
            }
        });
    }

    public void deleteDeviceHttp() {
        String uid = currentSelectSmartDevice.getUid();
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            return;
        }
        Deviceprops device = new Deviceprops();
        if (uid != null) {
            device.setUid(uid);
        }
        RestfulToolsHomeGenius.getSingleton().deleteDevice(userName, uid, new Callback<DeviceOperationResponse>() {
            @Override
            public void onResponse(Call<DeviceOperationResponse> call, Response<DeviceOperationResponse> response) {
                Log.i(TAG, "" + response.code());
                Log.i(TAG, "" + response.message());
                if (response.code() == 200) {
                    Log.i(TAG, "" + response.body().toString());
                    for (int i = 0; i < mDeviceListenerList.size(); i++) {
                        mDeviceListenerList.get(i).responseDeleteDeviceHttpResult(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<DeviceOperationResponse> call, Throwable t) {
                Log.i(TAG, "" + t.getMessage());
            }
        });
    }

    /**
     * 修改设备属性
     */
    public void alertDeviceHttp(String uid, String room_uid, String device_name, String gw_uid) {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            return;
        }
        Deviceprops device = new Deviceprops();
        device.setUid(uid);
        if (gw_uid != null) {
            device.setGw_uid(gw_uid);
        }
        if (room_uid != null) {
            device.setRoom_uid(room_uid);
        }
        if (device_name != null) {
            device.setDevice_name(device_name);
        }
        Log.i(TAG, "alert device:" + device.toString());
        RestfulToolsHomeGenius.getSingleton().alertDevice(userName, device, new Callback<DeviceOperationResponse>() {
            @Override
            public void onResponse(Call<DeviceOperationResponse> call, Response<DeviceOperationResponse> response) {
                Log.i(TAG, "" + response.code());
                Log.i(TAG, "" + response.message());
                if (response.errorBody() != null) {
                    try {
                        Log.i(TAG, "" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (response.code() == 200) {
                    Log.i(TAG, "" + response.body().toString());
                    for (int i = 0; i < mDeviceListenerList.size(); i++) {
                        mDeviceListenerList.get(i).responseAlertDeviceHttpResult(response.body());
                    }
                }
            }
            @Override
            public void onFailure(Call<DeviceOperationResponse> call, Throwable t) {
                Log.i(TAG, "" + t.getMessage());
            }
        });
    }

    /**
     * 读设备属性
     */
    public void readDeviceInfoHttp(String uid) {
        String userName = Perfence.getPerfence(Perfence.PERFENCE_PHONE);
        if (userName.equals("")) {
            return;
        }
        RestfulToolsHomeGeniusString.getSingleton().readDeviceInfo(userName, uid, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "" + response.code());
                Log.i(TAG, "" + response.message());
                if (response.code() == 200) {
                    Log.i(TAG, "" + response.body());
                    for (int i = 0; i < mDeviceListenerList.size(); i++) {
                        mDeviceListenerList.get(i).responseGetDeviceInfoHttpResult(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG, "" + t.getMessage());
            }
        });
    }

    /**
     * 初始化本地连接管理器
     */
    public void InitDeviceManager(Context context) {
        this.mContext = context;
        this.mDeviceListenerList = new ArrayList<>();
        if (mLocalConnectmanager == null) {
            mLocalConnectmanager = LocalConnectmanager.getInstance();
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
        cachedThreadPool = Executors.newCachedThreadPool();
        //耗时操作新建线程处理
        //数据库查询操作
        mSmartDevList = new ArrayList<>();
        mSmartDevList.clear();
        mSmartDevList.addAll(DataSupport.findAll(SmartDev.class));
    }

    /**
     * 查询wifi列表
     * 返回:{ "OP": "REPORT", "Method": "WIFIRELAY", "SSIDList": [ ] }
     */
    public void queryWifiList() {
        Log.i(TAG, "queryWifiList");
        if (mLocalConnectmanager.isLocalconnectAvailable()) {
            QueryOptions query = new QueryOptions();
            query.setOP("QUERY");
            query.setMethod("WIFIRELAY");
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
        } else {
            if (mRemoteConnectManager.isRemoteConnectAvailable()) {
                String uuid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
                GatwayDevice device = DataSupport.findFirst(GatwayDevice.class);
                Log.i(TAG, "device.getTopic()=" + device.getTopic());
                if (device.getTopic() != null && !device.getTopic().equals("")) {
                    mHomeGenius.queryWifiList(device.getTopic(), uuid);
                }
            }
        }
    }


    /**
     * 绑定智能设备列表
     * {"org":"ismart","tp":"SMART_LOCK","ad":"00-12-4b-00-0b-26-c2-15","ver":"1"}
     *
     * @param smartDevice 智能设备（除去网关设备：中继器）
     */
    public void bindSmartDevList(QrcodeSmartDevice smartDevice) {
        if (mLocalConnectmanager.isLocalconnectAvailable()) {
            QueryOptions queryCmd = new QueryOptions();
            queryCmd.setOP("SET");
            queryCmd.setMethod("DevList");
            queryCmd.setTimestamp();
            List<SmartDev> devs = new ArrayList<>();
            //设备赋值
            SmartDev dev = new SmartDev();
            dev.setSmartUid(smartDevice.getAd());
            dev.setOrg(smartDevice.getOrg());
            Log.i(TAG, "bindSmartDevList type=" + smartDevice.getTp() + "getVer=" + smartDevice.getVer() + "smartDeviceUID=" + smartDevice.getAd());
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
        } else if (mRemoteConnectManager.isRemoteConnectAvailable()) {
            String uuid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
            GatwayDevice device = DataSupport.findFirst(GatwayDevice.class);
            Log.i(TAG, "device.getTopic()=" + device.getTopic());
            if (device.getTopic() != null && !device.getTopic().equals("")) {
                mHomeGenius.bindSmartDevList(device.getTopic(), uuid, smartDevice);
            }
        }

    }

    /**
     * 如果数据库中没有这个设备，需要修改数据库
     * 如果有这个设备就不处理
     * 添加智能设备成功，需要更新数据库
     */
    public boolean addDBSmartDevice(QrcodeSmartDevice device, String uid, GatwayDevice getwayDevice) {
        //查询设备
        SmartDev smartDev = DataSupport.where("Uid=?", uid).findFirst(SmartDev.class);
        if (smartDev == null) {
            smartDev = new SmartDev();
            smartDev.setUid(uid);
            smartDev.setOrg(device.getOrg());
            smartDev.setVer(device.getVer());
            smartDev.setType(device.getTp());
            smartDev.setGetwayDevice(getwayDevice);
            smartDev.setName(device.getName());
            smartDev.setMac(device.getAd().toLowerCase());
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
        queryCmd.setSmartUid(currentSelectSmartDevice.getMac());
        List<SmartDev> devs = new ArrayList<>();
        //设备赋值
        SmartDev dev = new SmartDev();
        dev.setUid(currentSelectSmartDevice.getMac());
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
    public void updateSmartDeviceInWhatRoom(Room room, String deviceUid, String deviceName) {
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

    public void deleteSmartDeviceInWhatRoom(final Room room, final String deviceUid) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                //保存所在的房间
                //查询设备
                SmartDev smartDev = DataSupport.where("Uid=?", deviceUid).findFirst(SmartDev.class, true);
                //找到要更行的设备,设置关联的房间
                smartDev.setRooms(null);
                boolean saveResult = smartDev.save();
                Log.i(TAG, "deleteSmartDeviceInWhatRoom saveResult=" + saveResult);
            }
        });

    }


    @Override
    public void OnBindAppResult(String uid) {

    }

    @Override
    public void OnGetQueryresult(String result) {
        //返回查询结果：开锁记录，设备列表
        Log.i(TAG, "返回查询结果：设备列表=" + result);
        //保存智能锁设备的DevUid
        if (result.contains("DevList")) {
            Gson gson = new Gson();
            DeviceList aDeviceList = gson.fromJson(result, DeviceList.class);
            if (aDeviceList.getSmartDev() != null && aDeviceList.getSmartDev().size() > 0) {
                mSmartDevList.clear();
                mSmartDevList.addAll(DataSupport.findAll(SmartDev.class));
            }
            for (int i = 0; i < mDeviceListenerList.size(); i++) {
                mDeviceListenerList.get(i).responseQueryResult(result);
            }
        }
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
        OpResult wifiList = gson.fromJson(result, OpResult.class);
        if (wifiList.getOP().equalsIgnoreCase("REPORT") && wifiList.getMethod().equalsIgnoreCase("WIFIRELAY")) {
            for (int i = 0; i < mDeviceListenerList.size(); i++) {
                mDeviceListenerList.get(i).responseWifiListResult(wifiList.getSSIDList());
            }
        }
    }

    @Override
    public void onSetWifiRelayResult(String result) {

    }

    @Override
    public void onGetalarmRecord(List<Info> alarmList) {

    }
}
