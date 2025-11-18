package com.sib.ibanklosucl.dto.mssf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sib.ibanklosucl.utilies.Base64Deserializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class MssfDocFetchResponse {
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
    public static class Error {
        @JsonProperty("error_code")
        private Integer errorCode;
        @JsonProperty("error_message")
        private String errorMessage;
    }
    @Data
    public static class Message {
        private String status;
        private String unique_id;
        private String los_id;
        private List<Document> documents;
        @JsonProperty("success")
        private boolean success;
        @JsonProperty("errors")
        private List<Error> errors;

    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Document {
        @JsonProperty("proof_type_name")
        private String proof_type_name;

        @JsonProperty("proof_type_id")
        private String proof_type_id;

        @JsonProperty("doc_type_id")
        private String doc_type_id;

        @JsonProperty("file_name")
        private String file_name;

        @JsonProperty("doc_type_name")
        private String doc_type_name;

        @JsonProperty("is_document_reupload")
        private boolean is_document_reupload;

        @JsonProperty("s3_url")
        private String s3_url;

        @JsonProperty("user_type")
        private String user_type;

        @JsonProperty("doc_data")
        @JsonDeserialize(using = Base64Deserializer.class)
        private byte[] doc_data;

        @JsonProperty("financier_doc_type_id")
        private String financier_doc_type_id;

        @JsonProperty("file_size")
        private int file_size;

        @JsonProperty("is_failed")
        private boolean is_failed;

        @JsonProperty("ocr_enable")
        private boolean ocr_enable;

        @JsonProperty("is_merged")
        private boolean is_merged;

    }
}
