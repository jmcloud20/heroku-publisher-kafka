package com.pccw.cloud.producerapp.service.heroku.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class DefaultConfigTest {

    @MockBean
    CertificateProvider certificateProvider;

    @Autowired
    DefaultConfig defaultConfig;

    @BeforeEach
    public void setup() {
        final String trustStorePath = "security/truststore.jks";
        final String keystorePath = "security/keystore.jks";
        ClassLoader classLoader = this.getClass().getClassLoader();
        File trustStore = new File(classLoader.getResource(trustStorePath).getFile());
        File keystore = new File(classLoader.getResource(keystorePath).getFile());

        Mockito.when(certificateProvider.getTruststore()).thenReturn(trustStore);
        Mockito.when(certificateProvider.getKeystore()).thenReturn(keystore);
        Mockito.when(certificateProvider.getPassword()).thenReturn("kafkapass");
        Mockito.when(certificateProvider.getDeployEnv()).thenReturn("");

    }

    @Test
    void getProperties() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException {
        Properties properties = this.defaultConfig.getProperties();

        assertThat(properties).isNotNull();
        assertThat(properties.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG)).isEqualTo("SSL");
        assertThat(properties.get("ssl.endpoint.identification.algorithm")).isEqualTo("");
        assertThat(properties.get(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG)).isEqualTo(CertificateProvider.filetype);
        assertThat(properties.get(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG)).isEqualTo(certificateProvider.getTruststore().getAbsolutePath());
        assertThat(properties.get(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG)).isEqualTo(certificateProvider.getPassword());
        assertThat(properties.get(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG)).isEqualTo(CertificateProvider.filetype);
        assertThat(properties.get(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG)).isEqualTo(certificateProvider.getKeystore().getAbsolutePath());
        assertThat(properties.get(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG)).isEqualTo(certificateProvider.getPassword());
        assertThat(properties.get(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG)).isNotNull();
    }

    @TestConfiguration
    static class DefaultConfigTestConfig {
        @Bean
        public DefaultConfig defaultConfig() {
            return new DefaultConfig(new CertificateProvider());
        }
    }


}