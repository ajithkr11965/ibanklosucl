package com.sib.ibanklosucl.dto.losintegrator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccOpeningResponse {
    @JsonProperty("Response")
    private Response response;
    @Getter
    @Setter
    public static class Response {
        @JsonProperty("Header")
        private Header header;
        @JsonProperty("Status")
        private Status status;
        @JsonProperty("Body")
        private Body body;
    }
    @Getter
    @Setter
    public static class Header {
        @JsonProperty("Timestamp")
        private String timestamp;
        @JsonProperty("APIName")
        private String apiName;
        @JsonProperty("APIVersion")
        private String apiVersion;
        @JsonProperty("Interface")
        private String interfaceName;
    }
    @Getter
    @Setter
    public static class Status {
        @JsonProperty("Code")
        private String code;
        @JsonProperty("Desc")
        private String desc;
    }
    @Getter
    @Setter
    public static class Body {
        @JsonProperty("UUID")
        private String UUID;
        @JsonProperty("Loan_Account_Number")
        private String Loan_Account_Number;

        @JsonProperty("Status")
        private String Status;
        @JsonProperty("Message")
        private String Message;
    }
}
