package com.sib.ibanklosucl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FormBody {
    private String slno;

    private String appid;

    private String winum;
    private String reqtype;
    private String reqcount;

    private String currenttab;
    private TypeCount tc;
    private List<DataItem> data; // List of data items

    private String fileFlag="N";

    @JsonProperty("DOC_ARRAY")
    private List<DOC_ARRAY> DOC_ARRAY; // List of data items

}
