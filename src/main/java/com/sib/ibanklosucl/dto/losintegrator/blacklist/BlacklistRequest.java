package com.sib.ibanklosucl.dto.losintegrator.blacklist;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistRequest {
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
        @JsonProperty("DOB")
        private String dob;
        @JsonProperty("Pan")
        private String pan;
        @JsonProperty("Passport")
        private String passport;
    }
}
