package deplink.com.smartwirelessrelay.homegenius.manager.device.smartswitch;

import android.content.Context;
import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.Info;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnectmanager;

/**
 * Created by Administrator on 2017/12/7.
 */
public class SmartSwitchManager implements LocalConnecteListener{
    private static final String TAG = "SmartSwitchManager";
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
    /**
     * 这个类设计成单例
     */
    private static SmartSwitchManager instance;
    private LocalConnectmanager mLocalConnectmanager;
    private GeneralPacket packet;
    private Context mContext;
    /**
     * 所有的开关设备
     */
    private List<SmartDev> mSmartSwitchDevList;
    private SmartDev currentSelectSmartDevice;

    public static synchronized SmartSwitchManager getInstance() {
        if (instance == null) {
            instance = new SmartSwitchManager();
        }
        return instance;
    }
    /**
     * 初始化本地连接管理器
     */
    public void InitSmartSwitchManager(Context context) {
        this.mContext = context;
        if (mLocalConnectmanager == null) {
            mLocalConnectmanager = LocalConnectmanager.getInstance();
            mLocalConnectmanager.InitLocalConnectManager(mContext, AppConstant.BIND_APP_MAC);
        }
        mLocalConnectmanager.addLocalConnectListener(this);
        packet = new GeneralPacket(mContext);
        if(cachedThreadPool==null){
            cachedThreadPool = Executors.newCachedThreadPool();
        }
        //耗时操作新建线程处理
        //数据库查询操作
        if(mSmartSwitchDevList==null){
            cachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    mSmartSwitchDevList = new ArrayList<>();
                    mSmartSwitchDevList.clear();
                    mSmartSwitchDevList.addAll( DataSupport.where("Type = ?", AppConstant.DEVICES.TYPE_SWITCH).find(SmartDev.class)  );
                }
            });
        }
    }

    /**
     * 添加开关时指定当前添加开关的类型
     */
    private String currentAddSwitchSubType;
    /**
     * 设备列表界面中，当前选中的开关设备
     */
    private String currentSelectSwitchSubType;

    public String getCurrentSelectSwitchSubType() {
        return currentSelectSwitchSubType;
    }

    public void setCurrentSelectSwitchSubType(String currentSelectSwitchSubType) {
        this.currentSelectSwitchSubType = currentSelectSwitchSubType;
    }

    public String getCurrentAddSwitchSubType() {
        return currentAddSwitchSubType;
    }

    public void setCurrentAddSwitchSubType(String currentAddSwitchSubType) {
        this.currentAddSwitchSubType = currentAddSwitchSubType;
    }


    public boolean addDBSwitchDevice(QrcodeSmartDevice device) {
        //查询设备
        SmartDev smartDev = DataSupport.where("Uid=?", device.getAd()).findFirst(SmartDev.class);
        if (smartDev == null) {
            smartDev = new SmartDev();
            smartDev.setUid(device.getAd());
            smartDev.setOrg(device.getOrg());
            smartDev.setVer(device.getVer());
            smartDev.setType(device.getTp());
            smartDev.setName(device.getName());
            smartDev.setSubType(currentAddSwitchSubType);
            boolean addResult = smartDev.save();
            Log.i(TAG, "向数据库中添加一条智能设备数据=" + addResult);
            return addResult;
        }
        Log.i(TAG, "数据库中已存在相同设备，不必要添加");
        return false;
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
