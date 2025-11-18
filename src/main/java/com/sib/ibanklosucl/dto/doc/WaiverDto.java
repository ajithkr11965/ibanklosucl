package com.sib.ibanklosucl.dto.doc;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WaiverDto {
    Long wiSlno;
    private String waiverType;
    private ROIWaiverDto roidto;
    private ProcessFeeWaiverDto processFeeWaiverDto;
    private CifCreationDto cifCreationDto;

    private CompleteDto	 completeDto	;

    private SanModDto sanModDto;


    @Data
    public static class CompleteDto {
        private Long slno;
        private String wiNum;
        private String docDate;
        private String modeofoper;
        private String docRemarks;
        private String marginreq;
        private String marginfile;
        private String disbursementInst;
        private String rtoform;
        private String decision;
        private String signUrl;

    }
    @Data
    public static class ProcessFeeWaiverDto {
        private Long slno;
        private String wiNum;

        private String chargeName;
        private String chargeCode;
        private String staticFixed;
        private String percantage;
        private String loanAmountPercantage;
        private String vehiclePricePercantage;
        private String othersPercantage;
        private BigDecimal value;
        private String maximumLimit;
        private String feeWaive;
        private String waiver;
        private String decision;
        private String feewaiveRequired;
        private String feewaiverRemarks;
        private String feeWaiverSancRemarks;
        private String feeSancValue;
        private String feeValue;
        private String frequency;
        private List<FeeData> feeData;
        private BigDecimal maximumValue;

    }
    @Data
    public static class FeeData{
        private String feeCode;
        private String feeName;
        private BigDecimal feeValue;
        private BigDecimal feeValueRec;
        private BigDecimal feeValueSanc;
        private String  feeWaiverFlag;
        private String frequency;
    }

    @Data
    public static class CifCreationDto {
        private Long slno;
        private String wiNum;
        private String cifwaiveRequired;
        private String cifMode;
        private String cifwaiverRemarks;
    }

    @Data
    public static class ROIWaiverDto {
        private Long slno;
        private String wiNum;
        private String stp;


        private String roiType;

        private BigDecimal sancAmt;

        private BigDecimal sancRoi;
        private BigDecimal initialRoi;
        private int sanctenor;

        private BigDecimal sancemi;

        private BigDecimal ebr;

        private BigDecimal operationalCost;

        private BigDecimal crp;

        private BigDecimal spread;
        private BigDecimal baseSpread;
        private BigDecimal sancBaseSpread;


        private BigDecimal revisedRoi;


        private BigDecimal revisedEmi;

        private String decision;
        private String status;
        private String roiwaiveRequired;
        private String roiwaiverRemarks;
        private String roiwaiverSancRemarks;


    }

    @Data
    public static class BOGAssetRevisedDto {
        private BigDecimal revisedRoi;
        private BigDecimal revisedEmi;
        private int revisedTenor;
        private BigDecimal revisedAmount;

        private String status;
        private String msg;

        private String slno;
        private String wiNum;

    }


    @Data
    public static class SanModDto {
        private Long slno;
        private String wiNum;
        private String sanModRequired;

        private String taskId;
        private String sancAmt;
        private String eligibleAmt;
        private String reqAmt;
        //private String loanAmt;
        private String roi;
        private String ltvAmount;
        private String tenor;
        private String emi;
        private String rec_sanc_amt;
        private String rec_tenor;
        private String rev_emi_amt;

        private String sanModRemarks;

    }
}