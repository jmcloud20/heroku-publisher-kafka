package com.pccw.cloud.producerapp.service.heroku.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;

@Configuration
public class BeanConfiguration {

    private final DefaultConfig defaultConfig;

    public BeanConfiguration(DefaultConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    @Bean
    public KafkaProducer<String, String> kafkaProducer() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException {
        Properties properties = defaultConfig.getProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<String, String>(properties);
    }

}
