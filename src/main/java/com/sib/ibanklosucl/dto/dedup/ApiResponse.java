package com.sib.ibanklosucl.dto.dedup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApiResponse {
    @JsonProperty("header")
    private ApiHeader Header;
    @JsonProperty("Status")
    private ApiStatus status;
    @JsonProperty("Body")
    private ApiBody body;
}
