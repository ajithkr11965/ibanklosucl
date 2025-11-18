package com.sib.ibanklosucl.dto.mssf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MssfDocFetchRequest {
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
        @JsonProperty("UUID")
        private String UUID;
        private String user_type;
        private String doc_type_id;
        private String los_id;
    }
}

