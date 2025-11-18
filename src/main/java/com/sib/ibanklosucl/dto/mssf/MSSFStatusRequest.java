package com.sib.ibanklosucl.dto.mssf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MSSFStatusRequest {

    @JsonProperty("request")
    private Request request;

    @JsonProperty("mock")
    private boolean mock;

    @JsonProperty("apiName")
    private String apiName;

    @Data
    public static class Request {
        @JsonProperty("new_queue")
        private String newQueue;

        @JsonProperty("wi_num")
        private String wiNum;

        @JsonProperty("los_id")
        private String losId;
    }
}
