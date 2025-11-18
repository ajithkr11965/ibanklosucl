package com.sib.ibanklosucl.dto.mssf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MssfDocAckResponse {
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
        @JsonProperty("Desc")
        private String desc;
    }

    @Data
    public static class Body {
        @JsonProperty("UUID")
        private String UUID;
        @JsonProperty("message")
        private Message message;
    }

    @Data
    public static class Message {
        private String unique_id;
        private boolean success;
        private List<ApplicantDocument> applicant_documents;
        private List<ApplicantDocument> co_applicant_documents;
    }

    @Data
    public static class ApplicantDocument {
        private String doc_type_id;
        private String shared_to_financier;
    }
}

