package com.sib.ibanklosucl.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_DSA_STATUS_VIEW")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VLDsaStatusView {
    @Id
    @Column(name = "SLNO")
    private Long slno;
    @Column(name = "WI_NUM")
    private String wiNum;
    @Column(name = "QUEUE")
    private String queue;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "SOL_ID")
    private String solId;
    @Column(name = "CMUSER")
    private String cmuser;
    @Column(name = "HOME_SOL")
    private String homeSol;
    @Column(name = "NUM_COAPPLICANTS")
    private String numCoapplicants;
    @Column(name = "SAN_FLG")
    private String sanFlg;
    @Column(name = "SAN_DATE")
    @Temporal(TemporalType.DATE)
    private Date sanDate;
    @Column(name = "SAN_USER")
    private String sanUser;
    @Column(name = "ACTIVE_FLG")
    private String activeFlg;
    @Column(name = "CUST_NAME")
    private String custName;
    @Column(name = "INO")
    private Long ino;
    @Column(name = "DEALER_CITY_NAME")
    private String dealerCityName;
    @Column(name = "DEALER_NAME")
    private String dealerName;
    @Column(name = "DEALER_CODE")
    private String dealerCode;
    @Column(name = "DEALER_STATE")
    private String dealerState;
    @Column(name = "DEALER_SUB_CODE")
    private String dealerSubCode;
    @Column(name = "MAKE_NAME")
    private String makeName;
    @Column(name = "MODEL_NAME")
    private String modelName;
    @Column(name = "VARIANT_NAME")
    private String variantName;
    @Column(name = "EXSHOWROOM_PRICE")
    private String exshowroomPrice;
    @Column(name = "RTO_PRICE")
    private String rtoPrice;
    @Column(name = "INSURANCE_PRICE")
    private String insurancePrice;
    @Column(name = "OTHER_PRICE")
    private String otherPrice;
    @Column(name = "DISCOUNT_PRICE")
    private String discountPrice;
    @Column(name = "COLOUR")
    private String colour;
    @Column(name = "PRICE")
    private String price;
    @Column(name = "MAIN_APPLICANT_NAME")
    private String mainApplicantName;
    @Column(name = "MAIN_APPLICANT_ID")
    private Long mainApplicantId;
    @Column(name = "CO_APPLICANT_NAMES")
    private String coApplicantNames;
    @Column(name = "ABB")
    private Double abb;
    @Column(name = "AMI")
    private Double ami;
    @Column(name = "ELIGIBLE_LOAN_AMT")
    private String eligibleLoanAmt;
    @Column(name = "EMI")
    private Double emi;
    @Column(name = "LOAN_AMT")
    private String loanAmt;
    @Column(name = "TENOR")
    private Double tenor;
    @Column(name = "COAPPLICANT_DETAILS")
    private String coapplicantDetails;
    @Column(name = "CMDATE")
    private String cmdate;

    @Column(name = "invoice_date")
    private String invoicedate;
    @Column(name = "INVOICE_NO")
    private String invoiceno;
    @Column(name = "TOTAL_INVOICE_PRICE")
    private String invoiceprice;
    @Column(name = "consentimg_ext")
    private String consentext;

    @Column(name = "original_seen_certificate_ext")
    private String originalSeenCertificateExt;
}
