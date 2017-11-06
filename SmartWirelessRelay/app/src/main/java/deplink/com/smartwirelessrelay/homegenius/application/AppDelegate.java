package deplink.com.smartwirelessrelay.homegenius.application;

import android.app.Activity;
import android.app.Application;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import deplink.com.smartwirelessrelay.homegenius.EllESDK.R;
import deplink.com.smartwirelessrelay.homegenius.util.AppConstant;


/**
 * Created by luoxiaoha on 2017/2/6.
 */

public class AppDelegate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Override
    public void onTerminate() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
