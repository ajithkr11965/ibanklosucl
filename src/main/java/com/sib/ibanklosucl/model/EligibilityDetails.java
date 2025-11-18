package com.sib.ibanklosucl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "VEHICLE_LOAN_ELIGIBILITY")
@Getter
@Setter
@ToString
@Audited
@EntityListeners(AuditingEntityListener.class)
public class EligibilityDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eligibility_details_seq")
    @SequenceGenerator(name = "eligibility_details_seq", sequenceName = "VLELIGIBILITYTMP_SEQ", allocationSize = 1)
    private Long ino;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "VEHICLE_AMT")
    private BigDecimal vehicleAmt;

    @Column(name = "LTV_AMT")
    private BigDecimal ltvAmt;

    @Column(name = "LTV_TYPE")
    private String ltvType;

    @Column(name = "LOAN_AMT")
    private BigDecimal loanAmt;

    @Column(name = "PROCEED_FLAG")
    private String proceedFlag;

    @Column(name = "ELIGIBILITY_USER")
    private String eligibilityUser;

    @Column(name = "ELIGIBILITY_DATE")
    private Date eligibilityDate;

    @Column(name = "PROGRAM_ELIGIBLE_AMT")
    private BigDecimal programEligibleAmt;

    @Column(name = "ELIGIBLE_LOAN_AMT")
    private BigDecimal eligibleLoanAmt;

    @Column(name = "LOAN_AMOUNT_RECOMMENDED")
    private BigDecimal loanAmountRecommended;

    @Column(name = "CARD_RATE")
    private BigDecimal cardRate;

    @Column(name = "LOAN_AMOUNT_RECOMMENDED_CPC")
    private BigDecimal loanAmountRecommendedCPC;

    @Column(name = "ROI_RECOMMENDED_CPC")
    private BigDecimal roiRecommendedCPC;

    @Column(name = "CMUSER")
    private String cmuser;

    @Column(name = "CMDATE")
    private Date cmdate;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "HOME_SOL")
    private String homeSol;

    @Column(name = "LTV_PER")
    private BigDecimal ltvPer;

    @Column(name = "ABB")
    private BigDecimal abb;

    @Column(name = "AMI")
    private BigDecimal ami;

    @Column(name = "FOIRBALANCE_PER")
    private BigDecimal foirBalancePer;

    @Column(name = "OBLIGATION")
    private BigDecimal obligation;

    @Column(name = "FD_AVAILABLE_AMT")
    private BigDecimal fdAvailableAmt;

    @Column(name = "EMIMAX")
    private BigDecimal emiMax;

    @Column(name = "TENOR")
    private int tenor;

    @Column(name = "EMI")
    private BigDecimal emi;

    @Column(name = "EBR")
    private BigDecimal ebr;

    @Column(name = "OPCOST")
    private BigDecimal opCost;

    @Column(name = "SPREAD")
    private BigDecimal spread;

    @Column(name = "CRP")
    private BigDecimal crp;

    @Column(name = "INTTBLCODE")
    private String intTblCode;
    @Column(name = "ELIGIBILITY_FLG")
    private String eligibilityFlg;

    @Column(name = "SANC_TENOR")
    private int sancTenor;
    @Column(name = "SANC_CARD_RATE")
    private BigDecimal sancCardRate;
    @Column(name = "SANC_EMI")
    private BigDecimal sancEmi;
    @Column(name = "SANC_AMOUNT_RECOMMENDED")
    private BigDecimal sancAmountRecommended;




    @Column(name = "SM_TENOR")
    private Integer smTenor;
    @Column(name = "SM_CARD_RATE")
    private BigDecimal smCardRate;
    @Column(name = "SM_EMI")
    private BigDecimal smEmi;
    @Column(name = "SM_SANC_AMOUNT")
    private BigDecimal smSancAmount;
    @Column(name = "final_ltv")
    private BigDecimal finalLTV;
    // Getters and setters


    @Column(name = "dealer_amt")
    private BigDecimal dealerAmt;

    @Column(name = "ins_amt")
    private BigDecimal insAmt;

    @Column(name = "add_ltv_amt")
    private BigDecimal addLtvAmt;


}

