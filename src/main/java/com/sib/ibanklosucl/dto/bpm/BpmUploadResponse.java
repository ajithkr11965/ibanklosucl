package com.sib.ibanklosucl.dto.bpm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BpmUploadResponse {
    @JsonProperty("Response")
    private Response Response;

    @Data
    public class DOCS {
        @JsonProperty("Status")
        private String Status;

        @JsonProperty("FileName")
        private String FileName;
        @JsonProperty("Index")
        private String Index;
    }
    @Data
    public class Response {
        @JsonProperty("Status")
        private String Status;
        @JsonProperty("Message")
        private String Message;
        @JsonProperty("DOCS")
        List<DOCS> DOCS;
    }

}
