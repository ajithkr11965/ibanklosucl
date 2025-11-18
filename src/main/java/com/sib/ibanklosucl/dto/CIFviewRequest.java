package com.sib.ibanklosucl.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CIFviewRequest {

    @NotNull(message = "Serial number is required")
    private String slno;
    @NotNull(message = "winum is required")
    private String winum;

    private String appid;
    private String custID;
}
