package com.sib.ibanklosucl.dto.acopn;

import lombok.Data;

import java.math.BigDecimal;

public class ROIProcFeeDTO {
    private ROIProcFeeDTO.ROIFinalDto roifinaldto;
    private ROIProcFeeDTO.ProcessFeeFinalDto processFeeFinalDto;

    @Data
    public static class ProcessFeeFinalDto{
        private String feeCode;
        private String feeName;
        private String feeInitialValue;
        private String feeFinalValue;
    }
    @Data
    public static class ROIFinalDto {
        private Long slno;
        private String wiNum;
        private String stp;


        private String roiType;

        private BigDecimal sancAmt;

        private BigDecimal sancRoi;
        private int sanctenor;

        private BigDecimal sancemi;

        private BigDecimal ebr;

        private BigDecimal operationalCost;

        private BigDecimal crp;

        private BigDecimal spread;
        private BigDecimal baseSpread;


        private BigDecimal revisedRoi;


        private BigDecimal revisedEmi;

        private String decision;
        private String status;
        private String roiwaiveRequired;
        private String roiwaiverRemarks;


    }
}
