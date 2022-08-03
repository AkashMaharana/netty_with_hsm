package com.netty.with.https;

import io.netty.handler.ssl.SslHandler;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import org.apache.log4j.Logger;

public class SslHandlerProvider {

    private static final Logger logger = Logger.getLogger(SslHandlerProvider.class);

    private static SSLContext sslContext = null;

    public static SslHandler getSSLHandler(){
        SSLEngine sslEngine=null;
        if(sslContext ==null){
            logger.error("Server SSL context is null");
            System.exit(-1);
        }else{
            sslEngine = sslContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(false);

        }
        return new SslHandler(sslEngine);
    }

    public static void initializeSSLContext () throws NoSuchAlgorithmException, IOException, KeyStoreException, UnrecoverableKeyException, KeyManagementException, CertificateException {
        logger.info("Initiating SSL context");
        String algorithm = "SunX509";

        KeyStore keyStore = HsmManager.getKeyStore();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
        kmf.init(keyStore, null);

        KeyManager[] keyManagers = kmf.getKeyManagers();
        TrustManager[] trustManagers = null;

        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
    }
}
