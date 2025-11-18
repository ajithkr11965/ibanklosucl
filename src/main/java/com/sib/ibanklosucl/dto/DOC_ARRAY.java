package com.sib.ibanklosucl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DOC_ARRAY {
    @JsonProperty("DOC_EXT")
    private String  DOC_EXT;
    @JsonProperty("DOC_NAME")
    private String  DOC_NAME;
    @JsonProperty("DOC_BASE64")
    private String  DOC_BASE64;

}
