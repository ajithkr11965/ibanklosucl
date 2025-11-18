package com.sib.ibanklosucl.model;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Audited
@Table(name = "LOS_DEDUP")
public class LosDedupeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @Column(name = "wi_num", length = 13)
    private String wiNum;

    @Column(name = "slno")
    private Long slno;

    @Column(name = "applicant_id")
    private Long applicantId;

//    @ManyToOne
////    @JsonManagedReference
//    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
//    private VehicleLoanApplicant lsapplicant;
    @Column(name = "wi_name")
    private String wiName;
    @Column(name = "cust_name")
    private String custName;
    @Column(name = "loan_type", length = 50)
    private String loanType;
    @Column(name = "wi_status")
    private String wiStatus;
    @Column(name = "app_type")
    private String appType;
    @Column(name = "reject_reason")
    private String rejectReason;
    @Column(name = "do_remarks")
    private String doRemarks;
    @Column(name = "dob")
    private String dob;
    @Column(name = "aadhaar")
    private String aadhaar;
    @Column(name = "pan_no")
    private String panNo;
    @Column(name = "voter_id")
    private String voterID;
    @Column(name = "passport_no")
    private String passportNo;
    @Column(name = "drive_lic")
    private String driveLic;
    @Column(name = "gst_no")
    private String gstNo;
    @Column(name = "corp_id")
    private String corpID;
    @Column(name = "message")
    private String message;
    @Column(name = "dedup_flag")
    private String dedupflag;
    @Column(name = "dedup_msg")
    private String dedupmsg;
    @Column(name = "active_flag")

    private String activeFlag;
    @Column(name = "del_flag")
    private String delFlag;

    @Column(name = "cmuser", length = 10)
    private String cmuser;

    @Column(name = "cmdate")
    private Date cmdate;
    @Column(name = "home_sol", length = 5)
    private String homeSol;
}
