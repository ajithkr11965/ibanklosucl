package com.sib.ibanklosucl.dto.doc;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgreementDetailsDTO {
    private String stateCode;
    private String stateName;
    private String agreementCode;
    private String agreementFixed;
    private String agreementPercentage;
    private String agreementValue;
    private BigDecimal agreementMinValue;
    private BigDecimal agreementMaxValue;
    private String hypothecationCode;
    private String hypothecationFixed;
    private String hypothecationPercentage;
    private String hypothecationValue;
    private BigDecimal hypothecationMinValue;
    private BigDecimal hypothecationMaxValue;
    private String guaranteeCode;
    private String guaranteeFixed;
    private String guaranteePercentage;
    private String guaranteeValue;
    private BigDecimal guaranteeMinValue;
    private BigDecimal guaranteeMaxValue;
    private String arbitrationCode;
    private String arbitrationFixed;
    private String arbitrationPercentage;
    private String arbitrationValue;
    private BigDecimal arbitrationMinValue;
    private BigDecimal arbitrationMaxValue;

    // Getters and Setters
}
