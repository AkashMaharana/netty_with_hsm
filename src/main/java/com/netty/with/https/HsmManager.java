package com.netty.with.https;

import com.safenetinc.luna.LunaSlotManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class HsmManager {

    private static final LunaSlotManager slotManager;
    private static KeyStore keyStore;

    static {
        Security.addProvider(new com.safenetinc.luna.provider.LunaProvider());
        slotManager = LunaSlotManager.getInstance();
    }

    public static void login() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        slotManager.login(0, "S@fenet123");
        keyStore = KeyStore.getInstance("Luna");
        keyStore.load(null, null);
        slotManager.setSecretKeysExtractable(false);
    }

    public static void logout() {
        slotManager.logout();
        keyStore = null;
    }

    public static KeyStore getKeyStoreForCertificateAndKey(String alias, char[] password) {
        try {
            Certificate certificates = keyStore.getCertificate(alias);
            if (certificates == null) {
                //return null;
            }

            KeyStore keyStore = KeyStore.getInstance("Luna");
            keyStore.load(null, password);
            Key key = keyStore.getKey("uag_cert_test_private_latest", password);

            keyStore.setKeyEntry(alias, key, password, new Certificate[]{certificates});
            return keyStore;
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException |
                UnrecoverableKeyException e) {
            return null;
        }
    }

    public static KeyStore getKeyStore() throws KeyStoreException {
        return getKeyStoreForCertificateAndKey("uag_cert_esmanager", null);
    }

}
