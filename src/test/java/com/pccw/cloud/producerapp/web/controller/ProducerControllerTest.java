package com.pccw.cloud.producerapp.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pccw.cloud.producerapp.web.model.CustomerUpdateEmailDto;
import com.pccw.cloud.producerapp.web.model.MessageDto;
import com.pccw.cloud.producerapp.web.model.OptDto;
import com.pccw.cloud.producerapp.web.model.ProductOfferDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProducerController.class)
class ProducerControllerTest {
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

        performMock(messageDto, "/api/v1/producer/CUST_optOut_optIn");
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