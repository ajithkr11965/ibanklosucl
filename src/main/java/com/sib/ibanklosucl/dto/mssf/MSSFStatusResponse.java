package com.sib.ibanklosucl.dto.mssf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MSSFStatusResponse {

    @JsonProperty("Response")
    private Response response;

    @Data
    public static class Response {
        @JsonProperty("Header")
        private Header header;

        @JsonProperty("Status")
        private Status status;

        @JsonProperty("Body")
        private Body body;
    }

    @Data
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

    @Data
    public static class Status {
        @JsonProperty("Code")
        private String code;

        @JsonProperty("Status")
        private String status;
    }

    @Data
    public static class Body {
        @JsonProperty("UUID")
        private String UUID;

        @JsonProperty("message")
        private Message message;

        @JsonProperty("message1")
        private String message1;
    }

    @Data
    public static class Message {
        @JsonProperty("success")
        private boolean success;

        @JsonProperty("unique_id")
        private String uniqueId;
    }
}

