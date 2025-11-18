package com.sib.ibanklosucl.dto.experian;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ExperianRequest {
    private JsonNode request;
    private boolean mock;
    private String apiName;
    private String userId;
    private String origin;
    private String slno;
    private String workItemNumber;
    private String appid;
    private String pan;

    @Schema(description = "experian_ino")
    @JsonProperty("experian_ino")
    private String experian_ino;
}


