package com.sib.ibanklosucl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpreadDTO {
    @JsonProperty("EBR")
    private BigDecimal ebr;
    @JsonProperty("CPR")
    private BigDecimal cpr;
    @JsonProperty("CARDRATE")
    private BigDecimal cardrate;
    @JsonProperty("OPCOST")
    private BigDecimal opcost;
    @JsonProperty("INTTBLCODE")
    private String inttblcode;
    @JsonProperty("SPREAD")
    private BigDecimal spread;
    @JsonProperty("status")
    private String status;
    @JsonProperty("REASON")
    private String reason;

}
