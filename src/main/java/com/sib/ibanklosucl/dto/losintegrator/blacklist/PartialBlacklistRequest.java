package com.sib.ibanklosucl.dto.losintegrator.blacklist;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartialBlacklistRequest {
    private Request request;
    private boolean mock;
    private String apiName;
    private String workItemNumber;
    private String origin;
    private String slno;
    @Getter
    @Setter
    public static class Request {
        @JsonProperty("merchant_code")
        private String merchantCode;
        @JsonProperty("merchant_name")
        private String merchantName;
        @JsonProperty("CustName")
        private String custname;
        @JsonProperty("FirstName")
        private String firstName;
        @JsonProperty("LastName")
        private String lastname;
        @JsonProperty("Gender")
        private String gender;
        @JsonProperty("Country")
        private String country;
    }
}
