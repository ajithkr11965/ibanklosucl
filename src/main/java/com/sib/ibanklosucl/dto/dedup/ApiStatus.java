package com.sib.ibanklosucl.dto.dedup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApiStatus {
    @JsonProperty("Code")
    private String code;
    @JsonProperty("Desc")
    private String desc;
}
