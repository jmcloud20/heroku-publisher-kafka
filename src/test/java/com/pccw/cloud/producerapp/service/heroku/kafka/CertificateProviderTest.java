package com.pccw.cloud.producerapp.service.heroku.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class CertificateProviderTest {

    @Autowired
    CertificateProvider certificateProvider;
    String allowablePassword = "kafkapass";

    @BeforeEach
    public void setup() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        certificateProvider.setDeployEnv("");
        certificateProvider.init();
    }

    @Test
    void checkInit() {
        File keystore = certificateProvider.getKeystore();
        File truststore = certificateProvider.getTruststore();

        assertThat(certificateProvider.getPassword()).isEqualTo(allowablePassword);
        assertThat(keystore).isNotNull();
        assertThat(truststore).isNotNull();


        String dotRegex = "\\.";

        String[] keystoreFileName = keystore.getName().split(dotRegex);
        assertThat(keystoreFileName[keystoreFileName.length - 1].toUpperCase()).isEqualTo(CertificateProvider.filetype);

        String[] truststoreFileName = truststore.getName().split(dotRegex);
        assertThat(truststoreFileName[truststoreFileName.length - 1].toUpperCase()).isEqualTo(CertificateProvider.filetype);

    }

    // Create an instance of the component
    @TestConfiguration
    static class CertProviderTestConfig {
        @Bean
        public CertificateProvider certificateProvider() {
            return new CertificateProvider();
        }
    }
}