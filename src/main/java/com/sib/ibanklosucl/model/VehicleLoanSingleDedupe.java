package com.sib.ibanklosucl.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_DEDUPE_DETAILS")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanSingleDedupe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @ManyToOne
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private VehicleLoanApplicant vldedupe;

    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "SLNO", nullable = false)
    private Long slNo;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @Column(name = "DOB")
    private String dob;

    @Column(name = "PAN")
    private String pan;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VOTER_ID")
    private String voterId;

    @Column(name = "AADHAR_REF_NO")
    private String aadharRefNo;

    @Column(name = "PASSPORT")
    private String passport;

    @Column(name = "RELATION")
    private String relation;

    @Column(name = "CHECK_DATE")
    private Date checkDate;

    @Lob
    @Column(name = "fetch_response")
    private String fetchResponse;

    @Column(name = "CHECK_RESULT")
    private String checkResult;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name ="DEL_FLG")
    private String delFlg;

    @Column(name = "cmuser", length = 10)
    private String cmUser;

    @Column(name = "cmdate")
    @Temporal(TemporalType.DATE)
    private Date cmDate;
}
