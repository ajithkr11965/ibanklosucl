package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "VEHICLE_LOAN_FINANCIAL_ASSET")
@Getter
@Setter
@ToString(exclude = "vlastkey")
@AllArgsConstructor
@NoArgsConstructor
public class VLFinasset {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vlfinasset_seq")
    @SequenceGenerator(name = "vlfinasset_seq", sequenceName = "VLFINASSET_SEQ", allocationSize = 1)
    private Long ino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VLFIN_INO")
    @JsonBackReference
    @JsonIgnore
    private VLCredit vlastkey;



    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "ASSETTYPE")
    private String assetType;

    @Column(name = "ASSETVAL")
    private Long assetVal;


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


}
