package com.sib.ibanklosucl.dto;

import com.sib.ibanklosucl.dto.experian.DKResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class DSAExperianReponseDTO {
    @Column(name = "STATUS")
    private String status;

    @Column(name = "MSG")
    private String message;
    @Column(name = "BUREAU_SCORE")
    private Long bureauScore;
    @Column(name = "TOT_OBLIGATIONS")
    private BigDecimal totObligations;

    @Column(name = "ELIGIBILITY")
    private String eligibility;

    @Column(name = "DPD_DAYS")
    private String dpddays;
    private List<DKResponse.Liability> liabilityList;

    @Data
    public static class Liability{
        private String bankName;
        private BigDecimal limit;
        private BigDecimal emiAmount;
        private String natureOfLimit;
        private BigDecimal outStandingBal;

    }
}
