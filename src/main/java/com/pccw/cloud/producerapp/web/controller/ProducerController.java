package com.pccw.cloud.producerapp.web.controller;

import com.pccw.cloud.producerapp.web.model.MessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/producer")
@RestController
@Slf4j
public class ProducerController {

    @PostMapping("/CUST_optOut_optIn")
    @ResponseStatus(HttpStatus.CREATED)
    public void customerOpt(@RequestBody MessageDto messageDto){
        log.info("Message Received: "+messageDto);
    }

    @PostMapping("/cust_update_email")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateEmail(@RequestBody MessageDto messageDto){
        log.info("Message Received: "+messageDto);
    }

    @PostMapping("/prod_offer")
    @ResponseStatus(HttpStatus.CREATED)
    public void productOffer(@RequestBody MessageDto messageDto){
        log.info("Message Received: "+messageDto);
    }
}
