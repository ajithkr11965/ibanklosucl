package com.sib.ibanklosucl.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "VEHICLE_LOAN_KYC")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanKyc {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vlkyc_seq")
    @SequenceGenerator(name = "vlkyc_seq", sequenceName = "IBANKLOSUCL.VLKYC_SEQ", allocationSize = 1)
    private Long ino;

    @Column(name = "wi_num", length = 13)
    private String wiNum;

    @Column(name = "slno")
    private Long slno;

    @Column(name = "applicant_id")
    private Long applicantId;

    @OneToOne
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private VehicleLoanApplicant applicant;


    @Column(name = "req_ip_addr", length = 19)
    private String reqIpAddr;

    @Column(name = "panfilename_bpm", length = 200)
    private String panFilenameBpm;

    @Column(name = "pan_no", length = 10)
    private String panNo;

    @Column(name = "pan_dob")
    @Temporal(TemporalType.DATE)
    private Date panDob;

    @Column(name = "pan_name", length = 80)
    private String panName;

    @Column(name = "ocr_pan_name", length = 80)
    private String ocrPanName;

    @Column(name = "ocr_pan_number", length = 10)
    private String ocrPanNumber;

    @Column(name = "ocr_pan_dob")
    @Temporal(TemporalType.DATE)
    private Date ocrPanDob;

    @Column(name = "pan_img",columnDefinition = "CLOB")
    private String panimg;
    @Column(name = "pan_ext", length = 5)
    private String panext;

    @Column(name = "pan_dob_nsdl_valid", length = 1)
    private String panDobNsdlValid;

    @Column(name = "aadhar_mode", length = 10)
    private String aadharMode;

    @Column(name = "aadhar_ref_num", length = 20)
    private String aadharRefNum;

    @Column(name = "aadhar_name", length = 80)
    private String aadharName;

//    @Column(name = "aadhar_dob")
//    @Temporal(TemporalType.DATE)
//    private Date aadharDob;

    @Column(name = "AADHAR_YOB", length = 4)
    private String aadharYob;
    @Column(name = "aadhar_otp_sent", length = 1)
    private String aadharOtpSent;

    @Column(name = "aadhar_otp_validated", length = 1)
    private String aadharOtpValidated;

    @Column(name = "ocr_aadhar_num", length = 12)
    private String ocrAadharNum;
    @Column(name = "ocr_aadhar_name", length = 100)
    private String ocrAadharName;
    @Column(name = "ocr_aadhar_yob", length = 4)
    private String ocrAadharYob;

    @Column(name = "cbs_aadhar_ref_num", length = 20)
    private String cbsAadharRefNum;


    @Column(name = "aadhar_img",columnDefinition = "CLOB")
    private String aadharimg;
    @Column(name = "aadhar_ext", length = 5)
    private String aadharext;

    @Column(name = "passport_number", length = 20)
    private String passportNumber;
    @Column(name = "passport_name", length = 100)
    private String passportName;

    @Column(name = "passport_expiry_date")
    @Temporal(TemporalType.DATE)
    private Date passportExpiryDate;
    @Column(name = "passport_img",columnDefinition = "CLOB")
    private String passportimg;
    @Column(name = "passport_ext", length = 5)
    private String passportext;
    @Column(name = "visa_oci_type", length = 5)
    private String visaOciType;

    @Column(name = "visa_oci_number", length = 30)
    private String visaOciNumber;

    @Column(name = "passport_filename_bpm", length = 200)
    private String passportFilenameBpm;

    @Column(name = "visa_filename_bpm", length = 200)
    private String visaFilenameBpm;

    @Column(name = "cbs_passport_num", length = 20)
    private String cbsPassportNum;


    @Column(name = "visa_img",columnDefinition = "CLOB")
    private String visaimg;
    @Column(name = "visa_ext", length = 5)
    private String visaext;
    @Column(name = "cbs_visa_num", length = 30)
    private String cbsVisaNum;
    @Column(name = "CBS_PASSPORT_NAME", length = 30)
    private String cbsPassportName;

    @Column(name = "cbs_passport_expiry")
    @Temporal(TemporalType.DATE)
    private Date cbsPassportExpiry;
    @Column(name = "cbs_visa_exp")
    @Temporal(TemporalType.DATE)
    private Date cbVisaExpiry;
    @Column(name = "visa_exp")
    @Temporal(TemporalType.DATE)
    private Date visaExpiry;

    @Column(name = "cbs_oci_number", length = 30)
    private String cbsOciNumber;

    @Column(name = "ocr_passport_num", length = 20)
    private String ocrPassportNum;
    @Column(name = "ocr_passport_name", length = 100)
    private String ocrPassportName;

    @Column(name = "ocr_passport_expiry")
    @Temporal(TemporalType.DATE)
    private Date ocrpassportexpiry;

    @Column(name = "cmuser", length = 10)
    private String cmUser;

    @Column(name = "cmdate")
    @Temporal(TemporalType.DATE)
    private Date cmDate;

    @Column(name = "del_flg", length = 1)
    private String delFlg;

    @Column(name = "home_sol", length = 5)
    private String homeSol;


    @Transient
    private String uidno;

    @Column(name = "photo",columnDefinition = "CLOB")
    private String photo;
    @Column(name = "photo_ext", length = 5)
    private String photoext;


    @Column(name = "consentimg",columnDefinition = "CLOB")
    private String consentimg;
    @Column(name = "consentimg_ext", length = 5)
    private String consentimgext;

    @Column(name = "original_seen_certificate",columnDefinition = "CLOB")
    private String originalSeenCertificate;
    @Column(name = "original_seen_certificate_ext", length = 5)
    private String originalSeenCertificateExt;
    @Column(name = "cust_sig",columnDefinition = "CLOB")
    private String custSig;
    @Column(name = "cust_sig_ext", length = 5)
    private String custSigExt;
    @Column(name = "PAN_UID_LINK", length = 2)
    private String panUidLink;
    @Column(name = "PAN_STATUS", length = 2)
    private String panStatus;
    @Column(name = "PAN_NAME_STATUS", length = 2)
    private String panNameStatus;
    @Column(name = "PAN_DOB_STATUS", length = 2)
    private String panDobStatus;
    @Column(name = "CONSENT_TYPE", length = 8)
    private String consentType;
    @Column(name = "DIGITAL_REF_SLNO", length = 80)
    private String digitalRefSlno;

}
