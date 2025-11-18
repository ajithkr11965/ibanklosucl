package com.sib.ibanklosucl.model;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CUSTOMER_DETAILS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class CustomerDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "WI_NUM", length = 13)
    private String wiNum;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "UUID", length = 50)
    private String uuid;

    @Column(name = "CUST_ID", length = 10)
    private String custId;

    @Column(name = "VALIDFLG", length = 1)
    private String validFlg;

    @Column(name = "ERRORMSG", length = 100)
    private String errorMsg;

    @Column(name = "AADHAAR_REF_NO", length = 20)
    private String aadhaarRefNo;

    @Column(name = "BRANCH_NAME", length = 50)
    private String branchName;

    @Column(name = "CELL_PHONE", length = 50)
    private String cellPhone;

    @Column(name = "COMM_EMAIL", length = 50)
    private String commEmail;
    @Column(name = "KYC_COMPLIED", length = 1)
    private String kycComplied;

    @Column(name = "COMMUNICATION_ADDRESS1", length = 100)
    private String communicationAddress1;

    @Column(name = "COMMUNICATION_ADDRESS2", length = 100)
    private String communicationAddress2;

    @Column(name = "COMMUNICATION_ADDRESS3", length = 100)
    private String communicationAddress3;

    @Column(name = "COMMUNICATION_CITY", length = 100)
    private String communicationCity;

    @Column(name = "COMMUNICATION_CITY_CODE", length = 10)
    private String communicationCityCode;

    @Column(name = "COMMUNICATION_COUNTRY", length = 100)
    private String communicationCountry;

    @Column(name = "COMMUNICATION_COUNTRY_CODE", length = 10)
    private String communicationCountryCode;

    @Column(name = "COMMUNICATION_PIN", length = 10)
    private String communicationPin;

    @Column(name = "COMMUNICATION_STATE", length = 100)
    private String communicationState;

    @Column(name = "COMMUNICATION_STATE_CODE", length = 10)
    private String communicationStateCode;

    @Column(name = "CONSTITUTION_CODE", length = 10)
    private String constitutionCode;

    @Column(name = "CUST_DOB", length = 10)
    private String custDob;

    @Column(name = "CUST_TITLE", length = 10)
    private String custTitle;

    @Column(name = "CUSTOMER_NAME", length = 100)
    private String customerName;

    @Column(name = "DEL_FLG", length = 1)
    private String delFlg;

    @Column(name = "FATHERS_NAME", length = 100)
    private String fathersName;

    @Column(name = "GENDER", length = 1)
    private String gender;

    @Column(name = "MARITAL_STATUS", length = 10)
    private String maritalStatus;

    @Column(name = "MINOR_FLAG", length = 1)
    private String minorFlag;

    @Column(name = "MONTHLY_INC", length = 10)
    private String monthlyInc;

    @Column(name = "MOTHERS_NAME", length = 100)
    private String mothersName;

    @Column(name = "NRE_FLAG", length = 1)
    private String nreFlag;

    @Column(name = "OCCUPATION", length = 10)
    private String occupation;

    @Column(name = "PAN", length = 10)
    private String pan;

    @Column(name = "PASSPORT", length = 50)
    private String passport;
    @Column(name = "VISA", length = 50)
    private String visa;
    @Column(name = "DRIVING_LICENCE", length = 50)
    private String drivingLicence;
    @Column(name = "OCI_CARD", length = 50)
    private String  ociCard;
    @Column(name = "OVERSEAS_ID_CARD", length = 50)
    private String  overseasIdCard;
    @Column(name = "CDN_NO", length = 50)
    private String  cdnNo;
    @Column(name = "VOTER_ID", length = 50)
    private String  voterid;

    @Column(name = "PERMANENT_ADDRESS1", length = 100)
    private String permanentAddress1;

    @Column(name = "PERMANENT_ADDRESS2", length = 100)
    private String permanentAddress2;

    @Column(name = "PERMANENT_ADDRESS3", length = 100)
    private String permanentAddress3;

    @Column(name = "PERMANENT_CITY", length = 100)
    private String permanentCity;

    @Column(name = "PERMANENT_CITY_CODE", length = 10)
    private String permanentCityCode;

    @Column(name = "PERMANENT_COUNTRY", length = 100)
    private String permanentCountry;

    @Column(name = "PERMANENT_COUNTRY_CODE", length = 10)
    private String permanentCountryCode;

    @Column(name = "PERMANENT_PIN", length = 10)
    private String permanentPin;

    @Column(name = "PERMANENT_STATE", length = 100)
    private String permanentState;

    @Column(name = "PERMANENT_STATE_CODE", length = 10)
    private String permanentStateCode;

    @Column(name = "PRIMARY_SOL_ID", length = 255)
    private String primarySolId;

    @Column(name = "REGION_NAME", length = 50)
    private String regionName;

    @Column(name = "REQ_IP_ADDR", length = 19)
    private String reqIpAddr;

    @Temporal(TemporalType.DATE)
    @Column(name = "CMDATE")
    private Date cmDate;

    @Column(name = "CMUSER", length = 10)
    private String cmUser;

    @Column(name = "HOME_SOL", length = 5)
    private String homeSol;
    @Column(name = "residentialStatus", length = 1)
    private String residentialStatus;



}
