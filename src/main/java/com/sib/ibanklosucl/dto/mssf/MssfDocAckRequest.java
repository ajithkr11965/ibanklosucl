package com.sib.ibanklosucl.dto.mssf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MssfDocAckRequest {
    private Request request;
    private boolean mock;
    private String apiName;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String merchantCode;
        private String merchantName;
        private String reqType;
        @JsonProperty ("UUID")
        private String UUID;
        private String los_id;
        private List<Document> documents;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        private String user_type;
        private String doc_type_id;
        private boolean success;
    }
}


