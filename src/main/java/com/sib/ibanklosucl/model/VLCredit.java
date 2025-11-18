package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "VEHICLE_LOAN_CREDIT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VLCredit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vlcredit_seq")
    @SequenceGenerator(name = "vlcredit_seq", sequenceName = "VLEMPLOYMENT_SEQ", allocationSize = 1)
    private Long ino;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private VehicleLoanApplicant vlcreditappid;
    @OneToMany(mappedBy = "vlliabkey", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VLFinliab> vlLiabList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "vlastkey", fetch = FetchType.LAZY)
    private List<VLFinasset> vlAstList = new ArrayList<>();


    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "BUREAU_SCORE")
    private Long bureauScore;
    @Column(name = "DK_SCORE")
    private Long dkScore;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "LAST_MOD_USER")
    private String lastmodUser;

    @Column(name = "LAST_MOD_DATE")
    private Date lastmodDate;

    @Column(name = "HOME_SOL")
    private String homeSol;

    @Column(name = "DEL_USER")
    private String delUser;

    @Column(name = "DEL_DATE")
    private Date delDate;

    @Column(name = "DEL_HOME_SOL")
    private String delhomeSol;

    @Column(name = "ACTIVE_FLG")
    private String activeFlg;

    @Column(name = "TOT_OBLIGATIONS")
    private Long totObligations;


    @Column(name = "EXPERIANs_FLAG")
    private boolean experianFlag;
    @Column(name = "DK_FLAG")
    private boolean dkFlag;
    @Column(name = "DK_COLOR")
    private String dkColor;

    @Column(name = "EXP_TENURE")
    private BigDecimal expTenure;

    @Column(name = "EXP_LOANAMT")
    private BigDecimal expLoanAmt;
    @Column(name = "EXP_FETCH_DATE")
    private Date expFetchDate;

    @Column(name = "NTC_FLAG")
    private String ntcFlag;
    @Column(name = "PAYSLIP_LIABILITY")
    private Long payslipLiablity;
}
