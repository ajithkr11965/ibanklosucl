package com.sib.ibanklosucl.dto.acopn;

import lombok.Data;

@Data
public class RepayAcctDTO {
    private String  bankName;
    private String  bankId;
    private String accountNumber;
    private String ifscCode;
    private String borrowerName;
}
