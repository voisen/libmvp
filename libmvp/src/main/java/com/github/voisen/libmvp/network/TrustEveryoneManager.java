package com.github.voisen.libmvp.network;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustEveryoneManager implements X509TrustManager {
    private SSLContext mSSLContext = null;

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    private synchronized SSLContext getSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        if (mSSLContext == null){
            mSSLContext = SSLContext.getInstance("TLS");
            mSSLContext.init(null, new TrustManager[]{this}, null);
        }
        return mSSLContext;
    }

    public SSLSocketFactory getSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        return getSslContext().getSocketFactory();
    }

    public X509TrustManager getX509TrustManager(){
        return this;
    }
}
