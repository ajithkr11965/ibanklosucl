package com.sib.ibanklosucl.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class EligibilityRecommendationRequest {

    @NotNull(message = "wiNum is required")
    private String wiNum;

    @NotNull(message = "slno is required")
    private Long slno;

    @NotNull(message = "loanAmountRecommendedCPC is required")
    @Positive(message = "loanAmountRecommendedCPC must be positive")
    private BigDecimal loanAmountRecommendedCPC;

    @NotNull(message = "roiRecommendedCPC is required")
    @Positive(message = "roiRecommendedCPC must be positive")
    private BigDecimal roiRecommendedCPC;

    @NotNull(message = "emi is required")
    @Positive(message = "emi must be positive")
    private BigDecimal emi;
    @NotNull(message = "vehicleAmt is required")
    @Positive(message = "vehicleAmt must be positive")
    private BigDecimal vehicleAmt;

    @NotNull(message = "ltvAmt is required")
    @Positive(message = "ltvAmt must be positive")
    private BigDecimal ltvAmt;

    @NotNull(message = "loanAmt is required")
    @Positive(message = "loanAmt must be positive")
    private BigDecimal loanAmt;

    @NotNull(message = "ltvPer is required")
    @Positive(message = "ltvPer must be positive")
    private BigDecimal ltvPer;

    @NotNull(message = "ltvType is required")
    private String ltvType;

    @NotNull(message = "tenor is required")
    @Positive(message = "tenor must be positive")
    private Integer tenor;

}
