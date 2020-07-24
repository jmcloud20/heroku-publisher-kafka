package com.pccw.cloud.producerapp.service.heroku.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class DefaultConfig {

    @Value("${KAFKA_URL}")
    @NotNull
    private final String kafkaURL;

    private final String plainText;

    private final CertificateProvider certificateProvider;

    public DefaultConfig(CertificateProvider certificateProvider) {
        this.certificateProvider = certificateProvider;

        Map<String, String> systemVariables = System.getenv();

        this.kafkaURL = System.getenv("KAFKA_URL");
        this.plainText = System.getenv("PLAINTEXT");
    }


    public Properties getProperties() throws URISyntaxException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        final String KAFKA = "kafka";
        final String KAFKA_SSL = "kafka+ssl";
        final String PLAIN_TEXT = "PLAINTEXT";
        this.certificateProvider.init();

        List<String> hostPorts = new ArrayList<String>();
        Properties properties = new Properties();

        for (String url : kafkaURL.split(",")) {
            URI uri = new URI(url);
            hostPorts.add(String.format("%s:%d", uri.getHost(), uri.getPort()));
            String scheme = uri.getScheme();

            switch (scheme) {
                case KAFKA:
                    properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, PLAIN_TEXT);
                    break;
                case KAFKA_SSL:
                    this.generateSSLProperties(properties);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("unknown scheme; %s", uri.getScheme()));
            }

        }
        String bootstrapServers = String.join(",", hostPorts);
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return properties;
    }

    private void generateSSLProperties(Properties properties) {

        final String SSL = "SSL";
        final String SSL_ALGORITHM = "ssl.endpoint.identification.algorithm";

        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SSL);
        properties.put(SSL_ALGORITHM, "");

        String deployEnv = certificateProvider.getDeployEnv();

        File trustStore = certificateProvider.getTruststore();
        File keyStore = certificateProvider.getKeystore();

        properties.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, CertificateProvider.filetype);
        properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStore.getAbsolutePath());
        properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, deployEnv.equalsIgnoreCase("cloud") ?
                certificateProvider.getEnvTrustStore().password() : certificateProvider.getPassword());
        properties.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, CertificateProvider.filetype);
        properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keyStore.getAbsolutePath());
        properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, deployEnv.equalsIgnoreCase("cloud") ?
                certificateProvider.getEnvKeyStore().password() : certificateProvider.getPassword());
    }


}
