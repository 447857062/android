package com.deplink.homegenius.manager.connect.remote;

import android.content.Context;

import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/7.
 */
public class RemoteConnectManager {
    private static final String TAG="RemoteConnectManager";
    private boolean isRemoteConnectAvailable;
    private static RemoteConnectManager instance;
    private  List<GatwayDevice>bindGatways;
    public static synchronized RemoteConnectManager getInstance() {
        if (instance == null) {
            instance = new RemoteConnectManager();
        }
        return instance;
    }
    public void setRemoteConnectAvailable(boolean remoteConnectAvailable) {
        isRemoteConnectAvailable = remoteConnectAvailable;
    }

    public boolean isRemoteConnectAvailable() {
       return isRemoteConnectAvailable;
    }
    public void InitRemoteConnectManager(Context context) {
        if(bindGatways==null){
            bindGatways=new ArrayList<>();
            bindGatways=DataSupport.findAll(GatwayDevice.class);
        }
        isRemoteConnectAvailable=bindGatways.size()>0?true:false;
    }
}
