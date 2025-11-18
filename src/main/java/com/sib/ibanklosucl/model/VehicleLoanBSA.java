package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_BSA_DETAILS")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanBSA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @ManyToOne
    @JoinColumn(name = "program_ino")
    @JsonBackReference
    private VehicleLoanProgram vlbsa;
    @Column(name = "wi_num", length = 13)
    private String wiNum;
    @Column(name = "slno")
    private Long slno;
    @Column(name = "applicant_id")
    private Long applicantId;
    @Column(name = "mobile_no")
    private String mobileNo;
    @Column(name = "url")
    private String url;
    @Column(name = "alert_id")
    private String alertId;
    @Column(name = "bsa_mode")
    private String itrMode;
    @Column(name = "txn_Id")
    private String txnId;
    @Column(name = "perfios_transaction_id")
    private String perfiosTransactionId;
    private Date timestamp;
    private Date updated;
    @Column(name = "cmuser", length = 10)
    private String cmUser;
    @Column(name = "cmdate")
    @Temporal(TemporalType.DATE)
    private Date cmDate;
    @Lob
    @Column(name = "fetch_response")
    private String fetchResponse;
    @Lob
    @Column(name = "message")
    private String message;
    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;
    @Column(name = "avg_bank_balance")
    private BigDecimal avgBankBalance;

    @Column(name = "total_inw_bounces")
    private int totalInwBounces;

    @Column(name = "total_outw_bounces")
    private int totalOutwBounces;
    @Column(name = "bank")
    private String bank;
    @Column(name = "accountType")
    private String accountType;
    @Column(name = "accountNo")
    private String accountNo;

    @Column(name = "DEL_FLG")
    private String delFlg;
    @Column(name = "DEL_USER")
    private String delUser;

    @Column(name = "DEL_DATE")
    private Date delDate;

    @Column(name = "DEL_HOME_SOL")
    private String delHomeSol;

    @Column(name = "perfios_status")
    private String perfiosStatus;
    @Column(name = "error_code")
    private String errorCode;
     @Column(name = "statement_type")
    private String statementType;
     @Lob
    @Column(name = "emi_response")
    private String emiResponse;
    @Column(name = "full_month_count")
    private String fullMonthCount;

    @Column(name = "tot_chq_issues")
    private String totChqIssues;



}
