package com.sib.ibanklosucl.dto.acopn;

import lombok.Data;

@Data
public class SanctionDetailsDTO {

    private String   sancAmountRecommended;
    private String sancCardRate;
    private String sancEmi;
    private String sancTenor;

    private String ltvAmount;
    private String ltvPercentage;

    private String loanAmt;
    private String eligibleLoanAmt;
}
