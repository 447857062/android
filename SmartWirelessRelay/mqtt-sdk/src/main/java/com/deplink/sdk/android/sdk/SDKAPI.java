package com.deplink.sdk.android.sdk;

import com.deplink.sdk.android.sdk.bean.User;
import com.deplink.sdk.android.sdk.bean.UserSession;
import com.deplink.sdk.android.sdk.device.BaseDevice;

import java.util.List;

/**
 * Created by huqs on 2016/6/29.
 */
public interface SDKAPI {
    List<BaseDevice> getDeviceList();

    void login(String username,String password);

    void logout();

    User getUserInfo();
}
