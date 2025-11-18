package com.sib.ibanklosucl.dto.mssf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MssfApiBaseRequest {
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
        private String UUID;
    }
}

