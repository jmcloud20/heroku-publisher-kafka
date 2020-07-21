package com.pccw.cloud.producerapp.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerUpdateEmailDto{
    private String userId;
    private String oldEmail;
    private String newEmail;
    private String source;
    private Long publishTime;

}
