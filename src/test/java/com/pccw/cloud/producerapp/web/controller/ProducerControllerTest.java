package com.pccw.cloud.producerapp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pccw.cloud.producerapp.service.KafkaProducerService;
import com.pccw.cloud.producerapp.web.model.CustomerUpdateEmailDto;
import com.pccw.cloud.producerapp.web.model.MessageDto;
import com.pccw.cloud.producerapp.web.model.OptDto;
import com.pccw.cloud.producerapp.web.model.ProductOfferDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Date;

import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProducerController.class)
class ProducerControllerTest {

    @MockBean
    KafkaProducerService kafkaProducerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void customerOpt() throws Exception {
        OptDto optDto = OptDto.builder()
                .userId("U2313532432")
                .mobile(98989898)
                .email("ewjilkfw@netvigator.com")
                .hkId("1234567890")
                .brand("netvigator")
                .source("cloud")
                .publishTime(new Date().getTime())
                .build();

        MessageDto messageDto = MessageDto.builder()
                .topic("CUST_optOut_optIn")
                .desc("Customer request opt-out/opt-in")
                .message(optDto)
                .build();

        String messageDtoJson = objectMapper.writeValueAsString(messageDto);

        this.performMock(messageDto, "/api/v1/producer/CUST_optOut_optIn");

        then(kafkaProducerService).should().produce(messageDto.getTopic(), messageDtoJson);
    }

    @Test
    public void testRandom() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        ClassLoader classLoader = new ProducerControllerTest().getClass().getClassLoader();
        String filename = "security/keystore.jks";
        File file = new File(classLoader.getResource(filename).getFile());

        Assert.notNull(file, "keystore file does not exist.");
//        EnvKeyStore envTrustStore = EnvKeyStore.createWithRandomPassword("KAFKA_TRUSTED_CERT");
//        EnvKeyStore envKeyStore = EnvKeyStore.createWithRandomPassword("KAFKA_CLIENT_CERT","KAFKA_CLIENT_CERT_KEY");

//        Reader reader = new StringReader(System.getenv("KAFKA_TRUSTED_CERT"));
//        Reader reader = new FileReader(System.getenv("KAFKA_CLIENT_CERT_KEY"));
//        PEMParser pemParser = new PEMParser(reader);
//        X509Certificate certHolder = (X509Certificate) pemParser.readObject();
//        PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
//        JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
//        StringReader certReader = new StringReader(System.getenv("KAFKA_CLIENT_CERT"));
    }

    @Test
    void updateEmail() throws Exception {
        CustomerUpdateEmailDto customerUpdateEmailDto = CustomerUpdateEmailDto.builder()
                .userId("U231353674")
                .oldEmail("ewjilkfw@netvigator.com")
                .newEmail("abcdefg@netvigator.com")
                .source("cloud")
                .publishTime(new Date().getTime())
                .build();
        MessageDto messageDto = MessageDto.builder()
                .topic("CUST_optOut_optIn")
                .desc("Customer request opt-out/opt-in")
                .message(customerUpdateEmailDto)
                .build();

        performMock(messageDto, "/api/v1/producer/cust_update_email");


    }

    @Test
    void productOffer() throws Exception {
        ProductOfferDto productOfferDto = ProductOfferDto.builder()
                .offerId("O19840327565")
                .brand("1010")
                .type("update")
                .build();

        MessageDto messageDto = MessageDto.builder()
                .topic("PROD_offer")
                .desc("New/update product offer")
                .message(productOfferDto)
                .build();

        performMock(messageDto, "/api/v1/producer/cust_update_email");
    }

    private void performMock(MessageDto messageDto, String urlTemplate) throws Exception {
        String messageDtoJson = objectMapper.writeValueAsString(messageDto);

        mockMvc.perform(post(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(messageDtoJson))
                .andExpect(status().isCreated());
    }

}