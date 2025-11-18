package com.sib.ibanklosucl.dto.dedup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DedupeRequestBody {
    @JsonProperty("merchant_code")
   private String merchantCode;
   @JsonProperty("merchant_name")
    private String merchantName;
    private String mobileNumber; // Updated field name
    private String email;

}
