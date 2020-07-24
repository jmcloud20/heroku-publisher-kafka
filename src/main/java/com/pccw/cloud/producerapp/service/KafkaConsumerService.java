package com.pccw.cloud.producerapp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Deprecated
@Slf4j
public class KafkaConsumerService {

    public void consume() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String bootstrapServer = null;
        String groupId = null;
        String topic = null;
        Runnable consumerRunnable = new ConsumerRunnable(countDownLatch, bootstrapServer, groupId, topic);

        Thread consumerThread = new Thread(consumerRunnable);
        consumerThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ((ConsumerRunnable) consumerRunnable).shutdown();
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public class ConsumerRunnable implements Runnable {

        private final CountDownLatch countDownLatch;
        private final String boostrapServers;
        private final String groupId;
        private final String topic;
        private KafkaConsumer<String, String> consumer;
        private Properties properties;

        public ConsumerRunnable(CountDownLatch countDownLatch, String boostrapServers, String groupId, String topic) {
            this.countDownLatch = countDownLatch;
            this.boostrapServers = boostrapServers;
            this.groupId = groupId;
            this.topic = topic;
        }

        private void createProperties() {
            properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.boostrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
            String offSetResetConfig = "latest";//earliest, latest, none
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offSetResetConfig);

        }

        @Override
        public void run() {

            this.createProperties();

            // Create consumer
            consumer = new KafkaConsumer<String, String>(properties);

            //subscribe consumer to topic.
            consumer.subscribe(Arrays.asList(topic));

            try {
                long timeout = 1000;
                //poll for new data
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(timeout));
                    records.forEach(record -> {
                        log.info("Key: " + record.key());
                        log.info("Value: " + record.value());
                        log.info("Partition: " + record.partition());
                        log.info("Offset: " + record.offset());
                    });
                }
            } catch (WakeupException e) {
                log.info("Received shutdown signal.");
            } finally {
                consumer.close();
                this.countDownLatch.countDown();
            }

        }

        public void shutdown() {
            consumer.wakeup();
        }

    }

}
