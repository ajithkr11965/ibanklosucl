package com.sib.ibanklosucl.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter @Setter
public class CheckEligibilityRequest {
    private String wiNum;
    private String ino;
    private Long slno;
    private BigDecimal tenor;
    private String cardRate;
    private String loanAmt;
    private BigDecimal ltvAmt;
    private Long applicantId;
    private String vehicleAmt;
    private String ltvType;
    private BigDecimal ltvPer;

@Data
    public static class LoanEligibleDto{
        private BigDecimal abb;

        private BigDecimal ami;
        private BigDecimal emi;

        private BigDecimal foirBalancePer;

        private BigDecimal obligation;

        private BigDecimal fdAvailableAmt;
        private BigDecimal eligibleAmt;
        private String loanProgram;
    }
}
