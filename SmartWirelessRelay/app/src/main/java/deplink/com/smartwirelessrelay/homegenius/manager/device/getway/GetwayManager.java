package deplink.com.smartwirelessrelay.homegenius.manager.device.getway;

import android.util.Log;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.concurrent.ExecutorService;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway.Device;

/**
 * Created by Administrator on 2017/11/22.
 */
public class GetwayManager {
    private static final String TAG = "GetwayManager";
    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    private ExecutorService cachedThreadPool;
    /**
     * 这个类设计成单例
     */
    private static GetwayManager instance;
    public static synchronized GetwayManager getInstance() {
        if (instance == null) {
            instance = new GetwayManager();
        }
        return instance;
    }
    public List<Device>queryAllGetwayDevice(){
        List<Device>list=DataSupport.findAll(Device.class);
        Log.i(TAG,"查询到的网关设备个数="+list.size());
        return list;
    }
}
