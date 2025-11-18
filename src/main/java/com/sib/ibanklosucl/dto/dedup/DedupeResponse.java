package com.sib.ibanklosucl.dto.dedup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DedupeResponse {
    @JsonProperty("Response")
    private ApiResponse response;
}