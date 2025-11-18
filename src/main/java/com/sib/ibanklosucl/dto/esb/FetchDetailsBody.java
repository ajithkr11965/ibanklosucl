package com.sib.ibanklosucl.dto.esb;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchDetailsBody {
    @JsonProperty("UUID")
    private String uuid;
    @JsonProperty("CustId")
    private String custId;

}