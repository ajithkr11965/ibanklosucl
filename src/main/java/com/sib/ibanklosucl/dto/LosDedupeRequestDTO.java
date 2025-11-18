package com.sib.ibanklosucl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LosDedupeRequestDTO {
    private String merchantCode;
    private String merchantName;
    private String custName;

    @JsonProperty("DOB")
    private String dob;
    private String mobNo;
    private String voterID;
    private String aadharNo;
    private String driverNo;
    private String panNo;
    private String custID;
    private String passport;
    private String gstNo;
    private String corpId;
}