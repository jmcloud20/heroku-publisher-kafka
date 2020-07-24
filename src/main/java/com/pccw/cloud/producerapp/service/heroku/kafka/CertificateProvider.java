package com.pccw.cloud.producerapp.service.heroku.kafka;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CertificateProvider {

    public static final String filetype = "JKS";
    private final String keystoreJks = "security/keystore.jks";
    private final String truststoreJks = "security/truststore.jks";

    private final String password = "kafkapass";

    private File keystore;
    private File truststore;

    public void init() {
        ClassLoader classLoader = this.getClass().getClassLoader();

        keystore = new File(classLoader.getResource(keystoreJks).getFile());
        truststore = new File(classLoader.getResource(truststoreJks).getFile());
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
}

