package com.pccw.cloud.producerapp.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptDto {
    private String userId;
    private Integer mobile;
    private String email;
    private String hkId;
    private String brand;
    private boolean optOut;
    private String source;
    private Long publishTime;
}
