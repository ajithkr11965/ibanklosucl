package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Getter @Setter
public class LoanEligibilityDetailsDTO {
    private boolean isLTVManual;
    private BigDecimal vlamount;
    private BigDecimal ltvpercentage;
    private BigDecimal ltvAmount;
    private BigDecimal requestLoanAmount;
    private BigDecimal tenor;

    private BigDecimal addltvAmount;
}

