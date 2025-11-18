package com.sib.ibanklosucl.dto.losintegrator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NeftRequest {

    private Request request;
    private boolean mock;
    private String apiName;
    private String slno;
    private String workItemNumber;
    @Getter
    @Setter
    public static class Request {
        @JsonProperty("merchantCode")private String merchantCode;
        @JsonProperty("typeofPayment")private String typeofPayment;
        @JsonProperty("UUID")private String UUID;
        @JsonProperty("merchantName")private String merchantName;
        @JsonProperty("transactionAmount")private String transactionAmount;
        @JsonProperty("remitterAccNo")private String remitterAccNo;
        @JsonProperty("beneficiaryIFSC")private String beneficiaryIFSC;
        @JsonProperty("beneficiaryAccNo")private String beneficiaryAccNo;
        @JsonProperty("beneficiaryAccName")private String beneficiaryAccName;
        @JsonProperty("beneficiaryAccAdd1")private String beneficiaryAccAdd1;
        @JsonProperty("beneficiaryAccAdd2")private String beneficiaryAccAdd2;
        @JsonProperty("beneficiaryAccAdd3")private String beneficiaryAccAdd3;
        @JsonProperty("beneficiaryMob")private String beneficiaryMob;
        @JsonProperty("beneficiaryemail")private String beneficiaryemail;
        @JsonProperty("reportCode")private String reportCode;
    }
}