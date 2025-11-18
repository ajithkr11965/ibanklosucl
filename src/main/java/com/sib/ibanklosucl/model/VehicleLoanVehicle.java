package com.sib.ibanklosucl.model;

import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_VEHICLE")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vehicle_loan_vehicle_seq")
    @SequenceGenerator(name = "vehicle_loan_vehicle_seq", sequenceName = "IBANKLOSUCL.VLVEHICLE_SEQ", allocationSize = 1)
    private Long ino;
    @NonNull
    private String wiNum;
    @NonNull
    private Long slno;
    @NonNull
    private Long applicantId;
    private String reqIpAddr;
    private String dealerState;
    private String dealerCityId;
    private String dealerCityName;

    private String dealerName;
    private String makeId;
    private String makeName;
    private String modelId;
    private String modelName;
    private String variantId;
    private String variantName;
    private String exshowroomPrice;
    private String extendedWarranty;
    private String rtoPrice;
    private String insurancePrice;

    private Boolean customInsurance;
    private String customInsuranceAmount;
    @Column(name = "CUSTOM_INSURANCE_COMPANY")
    private String customInsuranceRemarks;
    private String otherPrice;
    private String onroadPrice;
    private String discountPrice;

    private String totalInvoicePrice;

    private String invoiceNo;
    private Date invoiceDate;
    private String colour;
    private String cmuser;
    private Date cmdate;
    private String delFlg;
    private String homeSol;

    // New fields
    private String dealerNameRemarks;
    private String dstCode;
    private String dsaCode;
    private String dealerCode;
    private String dealerSubCode;
    private String dealerIfsc;
    private String dealerAccount;
    private String dealerBank;

    @Lob
    @Column(name = "INVOICE_DOC",columnDefinition = "CLOB")
    private String invoiceDoc;

    @Column(name = "INVOICE_EXT", length = 5)
    private String invoiceExt;

    @Column(name = "BODY_TYPE", length = 30)
    private String bodyType;

    @Column(name = "AUTODEALER_SOURCED", length = 1)
    private String autodealerSourced;

    // Getters and Setters
}
