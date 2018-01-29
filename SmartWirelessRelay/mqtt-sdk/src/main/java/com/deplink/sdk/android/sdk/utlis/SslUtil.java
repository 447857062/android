package com.deplink.sdk.android.sdk.utlis;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by billy on 2016/9/26.
 */
public class SslUtil {
    public static SSLSocketFactory getSocketFactory(Context context, int certId) {
        SSLSocketFactory socketFactory = null;
        try {
            // Loading CAs from an InputStream
            CertificateFactory cf = null;
            cf = CertificateFactory.getInstance("X.509");

            X509Certificate ca;
            // I'm using Java7. If you used Java6 close it manually with finally.
            InputStream cert = context.getResources().openRawResource(certId);
            ca = (X509Certificate)cf.generateCertificate(cert);
            cert.close();
            // Creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca-certificate", ca);

            // Creating a TrustManager that trusts the CAs in our KeyStore.
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            socketFactory = sslContext.getSocketFactory();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return socketFactory;
    }

    public static SSLSocketFactory getSocketFactory(String certificate) {
        SSLSocketFactory socketFactory = null;
        try {
            // Loading CAs from an InputStream
            CertificateFactory cf = null;
            cf = CertificateFactory.getInstance("X.509");
            X509Certificate ca;
            InputStream cert = new ByteArrayInputStream(certificate.getBytes());
            ca = (X509Certificate)cf.generateCertificate(cert);
            cert.close();
            // Creating a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca-certificate", ca);
            // Creating a TrustManager that trusts the CAs in our KeyStore.
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            // Creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            socketFactory = sslContext.getSocketFactory();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return socketFactory;
    }
}
