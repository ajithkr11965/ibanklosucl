package com.sib.ibanklosucl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SanctionChargeDTO {
    @JsonProperty("CHARGE_CODE")
    private String chargeCode;
    @JsonProperty("CHARGE_NAME")
    private String chargeName;
    @JsonProperty("STATIC_FIXED")
    private String staticFixed;
    @JsonProperty("PERCENTAGE")
    private String percentage;
    @JsonProperty("LOAN_AMOUNT_PERCENTAGE")
    private String loanAmountPercentage;
    @JsonProperty("VEHICLE_PRICE_PERCENTAGE")
    private String vehiclePricePercentage;
    @JsonProperty("OTHERS_PERCENTAGE")
    private String othersPercentage;
    @JsonProperty("value")
    private Double value;
    @JsonProperty("MAXIMUM_LIMIT")
    private String maximumLimit;
    @JsonProperty("MAXIMUM_VALUE")
    private Long maximumValue;
    @JsonProperty("FREQUENCY")
    private String frequency;
}
