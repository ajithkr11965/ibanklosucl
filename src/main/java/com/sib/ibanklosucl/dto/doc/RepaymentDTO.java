package com.sib.ibanklosucl.dto.doc;

import lombok.Data;

@Data
public class RepaymentDTO {

    private String wiNum;


    private Long slno;


    private String bankName;

    private String accountNumber;

    private String ifscCode;
    private String borrowerName;
}
