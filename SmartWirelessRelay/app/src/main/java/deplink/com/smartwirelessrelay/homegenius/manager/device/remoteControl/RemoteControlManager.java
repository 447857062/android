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
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.LOCK_ALARM;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;

/**
 * Created by Administrator on 2017/11/22.
 * private RemoteControlManager mRemoteControlManager;
 * mRemoteControlManager=RemoteControlManager.getInstance();
 * mRemoteControlManager.InitRemoteControlManager(this,this);
 */
public class RemoteControlManager implements LocalConnecteListener {
    private static final String TAG = "RemoteControlManager";
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
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
    /**
     * 当前选中的遥控设备
     */
    private SmartDev mRemoteControlDevice;

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
            mLocalConnectmanager.InitLocalConnectManager(mContext, AppConstant.BIND_APP_MAC);
        }
        mLocalConnectmanager.addLocalConnectListener(this);
        packet = new GeneralPacket(mContext);
        cachedThreadPool = Executors.newCachedThreadPool();
        mRemoteControlListenerList = new ArrayList<>();
        addRemoteControlListener(listener);
        gson = new Gson();
        mRemoteControlDeviceList = new ArrayList<>();
        mRemoteControlDeviceList.addAll(DataSupport.where("Type=?", "IRMOTE_V2").find(SmartDev.class));
        //TODO 当前选中的遥控器
        if(mRemoteControlDeviceList.size()>0){
            mRemoteControlDevice = mRemoteControlDeviceList.get(0);
        }
    }

    public void study() {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                QueryOptions cmd = new QueryOptions();
                cmd.setOP("SET");
                cmd.setMethod("IrmoteV2");
                cmd.setTimestamp();
                cmd.setSmartUid(mRemoteControlDevice.getUid());
                cmd.setCommand("Study");
                String text = gson.toJson(cmd);
                packet.packRemoteControlData(text.getBytes());
                mLocalConnectmanager.getOut(packet.data);
            }
        });
    }

    public void sendData(final String data) {
        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                QueryOptions cmd = new QueryOptions();
                cmd.setOP("SET");
                cmd.setMethod("IrmoteV2");
                cmd.setTimestamp();
                cmd.setSmartUid(mRemoteControlDevice.getUid());
                cmd.setCommand("Send");
                cmd.setData(data);
                String text = gson.toJson(cmd);
                packet.packRemoteControlData(text.getBytes());
                mLocalConnectmanager.getOut(packet.data);
            }
        });
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
    public void onGetalarmRecord(List<LOCK_ALARM> alarmList) {

    }
}
