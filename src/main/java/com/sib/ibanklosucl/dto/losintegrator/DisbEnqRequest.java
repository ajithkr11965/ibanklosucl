package com.sib.ibanklosucl.dto.losintegrator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisbEnqRequest {

    private Request request;
    private boolean mock;
    private String apiName;
    private String slno;
    private String workItemNumber;
    @Getter
    @Setter
    public static class Request {
        @JsonProperty("merchantCode")private String merchantCode;
        @JsonProperty("apiName")private String apiName;
        @JsonProperty("UUID")private String UUID;
        @JsonProperty("merchantName")private String merchantName;
    }
}