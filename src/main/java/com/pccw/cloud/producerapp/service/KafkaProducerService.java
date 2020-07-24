package com.pccw.cloud.producerapp.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public interface KafkaProducerService {

    void produce(String topic, String message) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, URISyntaxException;

}
