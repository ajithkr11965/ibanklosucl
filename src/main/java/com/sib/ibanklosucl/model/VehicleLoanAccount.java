package com.sib.ibanklosucl.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.math.BigDecimal;

@Entity
@Table(name = "VEHICLE_LOAN_ACCOUNT")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INO", nullable = false)
    private Long ino;

    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "SLNO", nullable = false)
    private Long slno;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SLNO", insertable = false, updatable = false)
    private VehicleLoanMaster vehicleLoanMaster;
    @Column(name = "cmuser", length = 10)
    private String cmUser;

    @Column(name = "cmdate")
    private Date cmDate;
    @Column(name = "ipaddress", length = 10)
    private String ipaddress;
    @Column(name = "delflag", length = 10)
    private String delflag;
    @Column(name = "accopenapiuuid", length = 100)
    private String accopenapiuuid;//account opeing esb request uuid


    @Column(name = "disbapiuuid", length = 100)
    private String disbapiuuid;//account opeing esb request uuid

    @Column(name = "disbflag", length = 10)
    private String disbflag;//Initiated,success,failed

    @Column(name = "disbdate", length = 100)
    private Date disbdate;//will be available only for success cases.

    @Column(name = "tranrefno", length = 100)
    private String tranRefNo;

    @Column(name = "cbsrefno", length = 100)
    private String cbsRefNo;

    @Column(name = "processedtime")
    private Date processedTime;


    @Column(name = "disbcmuser", length = 10)
    private String disbcmuser;

    @Column(name = "disbcmdate")
    private Date disbcmdate;
    @Column(name = "disbenqcmuser", length = 10)
    private String disbenqcmuser;

    @Column(name = "disbenqcmdate")
    private Date disbenqcmdate;

    @Column(name = "disbursedamount")
    private BigDecimal disbursedAmount;




    @Column(name = "neftcmuserdealer", length = 10)
    private String neftcmuserdealer;

    @Column(name = "neftcmdatedealer")
    private Date neftcmdatedealer;
    @Column(name = "utrnodealer", length = 100)
    private String utrnodealer;
    @Column(name = "neftflagdealer", length = 100)
    private String neftflagdealer;
    @Column(name = "neftamountdealer")
    private BigDecimal neftamountdealer;
    @Column(name = "dealeraccnum", length = 100)
    private String dealeraccnum;
    @Column(name = "dealerifsc", length = 100)
    private String dealerifsc;
    @Column(name = "dealermob", length = 100)
    private String dealermob;
    @Column(name = "dealername", length = 100)
    private String dealername;
    @Column(name = "neftcmusermanuf", length = 10)
    private String neftcmusermanuf;

    @Column(name = "neftcmdatemanuf")
    private Date neftcmdatemanuf;
    @Column(name = "utrnomanuf", length = 100)
    private String utrnomanuf;
    @Column(name = "neftflagmanuf", length = 100)
    private String neftflagmanuf;
    @Column(name = "neftamountmanuf")
    private BigDecimal neftamountmanuf;
    @Column(name = "manufacc", length = 100)
    private String manufacc;
    @Column(name = "manufifsc", length = 100)
    private String manufifsc;
    @Column(name = "manumob", length = 100)
    private String manumob;
    @Column(name = "manuname", length = 100)
    private String manuname;


    @Column(name = "fiflag_dealer", length = 100)
    private String fiflag_dealer;//SUCCESS,FAILED

    @Column(name = "ficmuser_dealer", length = 10)
    private String ficmuser_dealer;
    @Column(name = "ficmdate_dealer")
    private Date ficmdate_dealer;
    @Column(name = "fitranid_dealer", length = 100)
    private String fitranid_dealer;
    @Column(name = "fitrandate_dealer")
    private java.time.LocalDateTime fitrandate_dealer;


    @Column(name = "fiflag_manu", length = 100)
    private String fiflag_manu;//SUCCESS,FAILED

    @Column(name = "ficmuser_manu", length = 10)
    private String ficmuser_manu;
    @Column(name = "ficmdate_manu")
    private Date ficmdate_manu;
    @Column(name = "fitranid_manu", length = 100)
    private String fitranid_manu;
    @Column(name = "fitrandate_manu")
    private java.time.LocalDateTime fitrandate_manu;
    @Column(name = "disb_type", length = 1)
    private String disbType;//single/multiple
    @Column(name = "add1", length = 100)
    private String add1;
    @Column(name = "add2", length = 100)
    private String add2;
    @Column(name = "add3", length = 100)
    private String add3;
}
