package com.pccw.cloud.producerapp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;

@Component
@Slf4j
@Primary
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaProducer<String, String> kafkaProducer;

    public KafkaProducerServiceImpl(KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void produce(String topic, String message) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException {

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        kafkaProducer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                //executes every time record is successfully sent or an exception is thrown.
                if (e == null) {
                    log.info("Received new metadata. ");
                    log.info("Topic: " + recordMetadata.topic());
                    log.info("Partition: " + recordMetadata.partition());
                    log.info("Offset: " + recordMetadata.offset());
                    log.info("Timestamp: " + recordMetadata.timestamp());
                } else {
                    log.error("Error while producing.", e);
                }
            }
        });
        kafkaProducer.flush();
        kafkaProducer.close();

    }

    @Deprecated
    private void produce(String bootstrapServer, String topic, String message) {
        // Create Producer Properties
        Properties properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Create producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // Send data - asynchronous
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                //executes every time record is successfully sent or an exception is thrown.
                if (e == null) {
                    log.info("Received new metadata. ");
                    log.info("Topic: " + recordMetadata.topic());
                    log.info("Partition: " + recordMetadata.partition());
                    log.info("Offset: " + recordMetadata.offset());
                    log.info("Timestamp: " + recordMetadata.timestamp());
                } else {
                    log.error("Error while producing.", e);
                }
            }
        });
        producer.flush();
        producer.close();
    }


}
