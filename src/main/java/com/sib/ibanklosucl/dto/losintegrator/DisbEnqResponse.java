package com.sib.ibanklosucl.dto.losintegrator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisbEnqResponse {
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
        private String Interface;
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
        @JsonProperty("message")
        private String message;
        @JsonProperty("processedTime")
        private String processedTime;
        @JsonProperty("tranRefNo")
        private String tranRefNo;
        @JsonProperty("cbsRefNo")
        private String cbsRefNo;
        @JsonProperty("disbursementStatus")
        private String disbursementStatus;
    }
}
