package com.pccw.cloud.producerapp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pccw.cloud.producerapp.service.KafkaProducerService;
import com.pccw.cloud.producerapp.web.model.CustomerUpdateEmailDto;
import com.pccw.cloud.producerapp.web.model.MessageDto;
import com.pccw.cloud.producerapp.web.model.OptDto;
import com.pccw.cloud.producerapp.web.model.ProductOfferDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Date;

@RequestMapping("/api/v1/producer")
@RestController
@Slf4j
public class ProducerController {

    @Qualifier("kafkaProducerServiceImpl")
    private final KafkaProducerService kafkaProducerService;
    @Autowired
    private ObjectMapper objectMapper;

    public ProducerController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }


    @PostMapping("/CUST_optOut_optIn")
    @ResponseStatus(HttpStatus.CREATED)
    public void customerOpt(@RequestBody MessageDto messageDto) throws IOException, URISyntaxException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        OptDto optDto = (OptDto) messageDto.getMessage();
        optDto.setPublishTime(new Date().getTime());
        this.produce(messageDto.getTopic(), messageDto);
        log.info("Message Received: " + messageDto);
    }

    @PostMapping("/cust_update_email")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateEmail(@RequestBody MessageDto messageDto) throws IOException, URISyntaxException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        CustomerUpdateEmailDto customerUpdateEmailDto = (CustomerUpdateEmailDto) messageDto.getMessage();
        customerUpdateEmailDto.setPublishTime(new Date().getTime());
        this.produce(messageDto.getTopic(), messageDto);
        log.info("Message Received: " + messageDto);
    }

    @PostMapping("/prod_offer")
    @ResponseStatus(HttpStatus.CREATED)
    public void productOffer(@RequestBody MessageDto messageDto) throws URISyntaxException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        ProductOfferDto productOfferDto = (ProductOfferDto) messageDto.getMessage();
        productOfferDto.setPublishTime(new Date().getTime());
        this.produce(messageDto.getTopic(), messageDto);
        log.info("Message Received: " + messageDto);
    }

    private void produce(String topic, MessageDto messageDto) throws IOException, URISyntaxException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        String messageDtoJson = objectMapper.writeValueAsString(messageDto);
        kafkaProducerService.produce(topic, messageDtoJson);
    }
}
