package com.sib.ibanklosucl.dto.losintegrator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FIResponse {
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
        @JsonProperty("tran_date")
        private String tran_date;
        @JsonProperty("tran_id")
        private String tran_id;
        @JsonProperty("Description")
        private String Description;
    }
}
