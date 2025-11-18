package com.sib.ibanklosucl.model.mssf;

import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MSSF_CUSTOMER_DATA")
@Data
@Audited
@EntityListeners(AuditingEntityListener.class)
public class MSSFCustomerData {
    @Id
    @Column(name = "REF_NO", length = 20)
    private String refNo;

    @Column(name = "PD_SALUTATION", length = 5)
    private String pdSalutation;

    @Column(name = "PD_FIRST_NAME", length = 64)
    private String pdFirstName;

    @Column(name = "PD_MIDDLE_NAME", length = 128)
    private String pdMiddleName;

    @Column(name = "PD_LAST_NAME", length = 64)
    private String pdLastName;

    @Column(name = "PD_GENDER", length = 2)
    private String pdGender;

    @Column(name = "PD_DOB", length = 10)
    private String pdDob;

    @Column(name = "PD_EMPLOYMENT_TYPE")
    private String pdEmploymentType;

    @Column(name = "PD_EDUCATIONAL_QUALIFICATION")
    private String pdEducationalQualification;

    @Column(name = "PD_RESIDENT_TYPE")
    private String pdResidentType;

    @Column(name = "PD_RESIDENT_SINCE")
    private String pdResidentSince;

    @Column(name = "PD_EMPLOYER", length = 256)
    private String pdEmployer;

    @Column(name = "PD_GROSS_SAL_ANNUM")
    private Double pdGrossSalAnnum;

    @Column(name = "PD_NET_SAL_ANNUM")
    private Double pdNetSalAnnum;

    @Column(name = "PD_WORD_EXP_MNTH")
    private Integer pdWordExpMnth;

    @Column(name = "PD_AVG_MON_INC")
    private Double pdAvgMonInc;

    @Column(name = "PD_BUSINESS_NAME", length = 256)
    private String pdBusinessName;

    @Column(name = "PD_PROD_WORK_EXP_YR")
    private Integer pdProdWorkExpYr;

    @Column(name = "PD_PROD_WORK_EXP_MNTH")
    private Integer pdProdWorkExpMnth;

    @Column(name = "PD_TENURE_BUSINESS_YR")
    private Integer pdTenureBusinessYr;

    @Column(name = "PD_TENURE_BUSINESS_MNTH")
    private Integer pdTenureBusinessMnth;

    @Column(name = "PD_NUM_DAIRY_CATTLE")
    private Integer pdNumDairyCattle;

    @Column(name = "PD_NUMBER_OF_CARS_OWNED")
    private Integer pdNumberOfCarsOwned;

    @Column(name = "PD_TOT_AGRI_LAND_AREA")
    private Double pdTotAgriLandArea;

    @Column(name = "PD_CURRENT_EMI_MNTH")
    private Double pdCurrentEmiMnth;

    @Column(name = "PD_MOTHER_NAME", length = 250)
    private String pdMotherName;

    @Column(name = "PD_FATHER_NAME", length = 250)
    private String pdFatherName;

    @Column(name = "PD_NUM_DEPENDENT")
    private Integer pdNumDependent;

    @Column(name = "PD_PAN", length = 10)
    private String pdPan;

    @Column(name = "PD_MOBILE")
    private Long pdMobile;

    @Column(name = "PD_EMAIL", length = 250)
    private String pdEmail;

    @Column(name = "PD_MARITAL_STATUS")
    private String pdMaritalStatus;

    // Present Address
    @Column(name = "PA_ADD_1", length = 45)
    private String paAdd1;

    @Column(name = "PA_ADD_2", length = 45)
    private String paAdd2;

    @Column(name = "PA_LANDMARK", length = 100)
    private String paLandmark;

    @Column(name = "PA_CITY")
    private String paCity;

    @Column(name = "PA_PIN")
    private Integer paPin;

    @Column(name = "PA_STATE")
    private String paState;

    @Column(name = "PA_RESIDING_SINCE")
    private String paResidingSince;

    // Work Address
    @Column(name = "WA_ADD_1", length = 45)
    private String waAdd1;

    @Column(name = "WA_ADD_2", length = 45)
    private String waAdd2;

    @Column(name = "WA_LANDMARK", length = 100)
    private String waLandmark;

    @Column(name = "WA_CITY")
    private String waCity;

    @Column(name = "WA_PIN")
    private Integer waPin;

    @Column(name = "WA_STATE")
    private String waState;

    // Permanent Address
    @Column(name = "PER_ADD_1", length = 45)
    private String perAdd1;

    @Column(name = "PER_ADD_2", length = 45)
    private String perAdd2;

    @Column(name = "PER_LANDMARK", length = 100)
    private String perLandmark;

    @Column(name = "PER_CITY")
    private String perCity;

    @Column(name = "PER_PIN")
    private Integer perPin;

    @Column(name = "PER_STATE")
    private String perState;

    @Column(name = "PER_RESIDING_SINCE")
    private Integer perResidingSince;

    // Reference 1
    @Column(name = "RF1_SALUTATION")
    private String rf1Salutation;

    @Column(name = "RF1_FIRST_NAME", length = 64)
    private String rf1FirstName;

    @Column(name = "RF1_LAST_NAME", length = 128)
    private String rf1LastName;

    @Column(name = "RF1_MOBILE")
    private Long rf1Mobile;

    @Column(name = "RF1_RELATION", length = 128)
    private String rf1Relation;

    // Reference 2
    @Column(name = "RF2_SALUTATION")
    private String rf2Salutation;

    @Column(name = "RF2_FIRST_NAME", length = 64)
    private String rf2FirstName;

    @Column(name = "RF2_LAST_NAME", length = 128)
    private String rf2LastName;

    @Column(name = "RF2_MOBILE")
    private Long rf2Mobile;

    @Column(name = "RF2_RELATION", length = 128)
    private String rf2Relation;

    // Dealer Details
    @Column(name = "DLR_STATE", length = 50)
    private String dlrState;

    @Column(name = "DLR_PIN")
    private Integer dlrPin;

    @Column(name = "DLR_CODE", length = 50)
    private String dlrCode;

    @Column(name = "DLR_CITY", length = 50)
    private String dlrCity;

    // Loan Details
    @Column(name = "LA_LOAN_AMT")
    private Double laLoanAmt;

    @Column(name = "LA_ROI")
    private Double laRoi;

    @Column(name = "LA_TENURE")
    private Integer laTenure;

    @Column(name = "LA_FEE_CHARGE")
    private Double laFeeCharge;

    @Column(name = "LA_DOWNPAYMENT")
    private Double laDownpayment;

    @Column(name = "LA_EST_EMI")
    private Double laEstEmi;

    // Additional Fields
    @Column(name = "ADDTNL_1", length = 100)
    private String addtnl1;

    @Column(name = "ADDTNL_2", length = 100)
    private String addtnl2;

    @Column(name = "ADDTNL_3", length = 100)
    private String addtnl3;

    @Column(name = "ADDTNL_4", length = 100)
    private String addtnl4;

    @Column(name = "ADDTNL_5", length = 100)
    private String addtnl5;

    @Column(name = "ADDTNL_6", length = 100)
    private String addtnl6;

    @Column(name = "ADDTNL_7", length = 100)
    private String addtnl7;

    @Column(name = "ADDTNL_8", length = 100)
    private String addtnl8;

    @Column(name = "ADDTNL_9", length = 100)
    private String addtnl9;

    @Column(name = "ADDTNL_10", length = 100)
    private String addtnl10;

    @Column(name = "ADDTNL_11", length = 100)
    private String addtnl11;

    @Column(name = "ADDTNL_12", length = 100)
    private String addtnl12;

    // Audit Fields
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @Column(name = "CREATED_BY", length = 50)
    private String createdBy;

    @Column(name = "MODIFIED_BY", length = 50)
    private String modifiedBy;
    @Column(name = "STATUS", length = 50)
    private String status;
}
