package com.pccw.cloud.producerapp.service.heroku.kafka;

import com.github.jkutner.EnvKeyStore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@Component
@ConfigurationProperties(value = "com.pccw.cloud.producerapp")
public class CertificateProvider {

    public static final String filetype = "JKS";
    private final String keystoreJks = "security/keystore.jks";
    private final String truststoreJks = "security/truststore.jks";
    private final String password = "kafkapass";
    private String deployEnv = "";

    private EnvKeyStore envKeyStore;
    private EnvKeyStore envTrustStore;

    private File keystore;
    private File truststore;

    public void init() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        if (!deployEnv.equalsIgnoreCase("cloud")) {
            keystore = new File(classLoader.getResource(keystoreJks).getFile());
            truststore = new File(classLoader.getResource(truststoreJks).getFile());
        } else {
            envTrustStore = EnvKeyStore.createWithRandomPassword("KAFKA_TRUSTED_CERT");
            envKeyStore = EnvKeyStore.createWithRandomPassword("KAFKA_CLIENT_CERT_KEY", "KAFKA_CLIENT_CERT");

            this.truststore = envTrustStore.storeTemp();
            this.keystore = envKeyStore.storeTemp();
        }
    }

    public File getKeystore() {
        return keystore;
    }

    public File getTruststore() {
        return truststore;
    }

    public String getPassword() {
        return password;
    }

    public String getDeployEnv() {
        return this.deployEnv;
    }

    public void setDeployEnv(String deployEnv) {
        this.deployEnv = deployEnv;
    }

    public EnvKeyStore getEnvKeyStore() {
        return envKeyStore;
    }

    public EnvKeyStore getEnvTrustStore() {
        return envTrustStore;
    }
}

