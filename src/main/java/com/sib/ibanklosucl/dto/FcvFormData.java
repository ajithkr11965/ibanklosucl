package com.sib.ibanklosucl.dto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter
public class FcvFormData {

    @NotNull(message = "Serial number is required")
    private Long slno;
    @NotNull(message = "winum is required")
    private String wiNum;
    private Long applicantId;
    private String delflg;
    @NotNull(message = "FCV status is required")
    private String fcvStatus;
    @NotNull(message = "CPV status is required")
    private String cpvStatus;
    @NotNull(message = "CFR status is required")
    private String cfrStatus;
    private String fcvdoc;
    private String cpvdoc;
    private String fcvDocExt;
    private String cpvDocExt;


}
