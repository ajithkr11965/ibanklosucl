package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "VEHICLE_LOAN_FINANCIAL_LIABILITIES")
@Getter
@Setter
@ToString(exclude = "vlliabkey")
@AllArgsConstructor
@NoArgsConstructor
public class VLFinliab {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vlfinliab_seq")
    @SequenceGenerator(name = "vlfinliab_seq", sequenceName = "VLFINLIAB_SEQ", allocationSize = 1)
    private Long ino;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VLFIN_INO")
    @JsonBackReference
    @JsonIgnore
    private VLCredit vlliabkey;


    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "BANKNAME")
    private String bankName;

    @Column(name = "NATURELIM")
    private String natureLim;

    @Column(name = "LIMIT")
    private Long limit;

    @Column(name = "OUTSTANDING")
    private Long outStanding;

    @Column(name = "emi")
    private Long emi;


    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;


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
    @Column(name = "MODIFIED_EMI")
    private Long modifiedEmi;


}
