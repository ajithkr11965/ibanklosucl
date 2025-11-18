package com.sib.ibanklosucl.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormSave {
    private String loanType="VL";

    private String tabType;

    private FormBody body; // List of data items

    private String reqip;
}
