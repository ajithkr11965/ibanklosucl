package com.sib.ibanklosucl.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import javax.validation.constraints.NotNull;
@Getter
@Setter
public class FormDataFCV {
    @NotNull(message = "ID is required")
    private String id;

    @NotNull(message = "Serial number is required")
    private String slno;
    @NotNull(message = "winum is required")
    private String winum;

    private String appid;

    private String fcvstatus;

    private String cpvstatus;

    private String cfrstatus;

    @NotNull(message = "Request Type is required")
    private String reqtype;

    private List<DataItem> data; // List of data items

    @JsonProperty("DOC_ARRAY")
    private List<DOC_ARRAY> DOC_ARRAY; // List of data items

}
