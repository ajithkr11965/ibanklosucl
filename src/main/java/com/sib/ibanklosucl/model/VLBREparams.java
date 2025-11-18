package com.sib.ibanklosucl.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "VEHICLE_LOAN_BRE_PARAMS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VLBREparams {


    @Id
    Long id;

    @Column(name = "winum")
    String winum;
    @Column(name = "slno")
    Long slno;

    @Column(name = "queue")
    String queue;

    @Column(name = "ownerapplicantid")
    Long ownerapplicantid;
    @Column(name = "firsttimebuyer")
    String firsttimebuyer;
    @Column(name = "loanamount")
    String loanamount;
    @Column(name = "tenor")
    String tenor;
    @Column(name = "emptype")
    String emptype;
    @Column(name = "retirementage")
    String retirementage;
    @Column(name = "loanprogram")
    String loanprogram;
    @Column(name = "applicanttype")
    String applicanttype;
    @Column(name = "totalexperience")
    String totalexperience;
    @Column(name = "currentexperience")
    String currentexperience;
    @Column(name = "currentbusinessexperience")
    String currentbusinessexperience;
    @Column(name = "stayduration")
    Long stayduration;
    @Column(name = "applicantdob")
    Date applicantdob;

    @Column(name = "bureauscore")
    Long bureauscore;

    @Column(name = "racescore")
    Long racescore;

    @Column(name = "ntcflag")
    String ntcflag;
    @Column(name = "ltvtype")
    String ltvtype;

    @Column(name = "Loanrecomentedamount")
    String Loanrecomentedamount;

    @Column(name = "eligibleloanamt")
    String eligibleloanamt;

    @Column(name = "doctype")
    String doctype;
    @Column(name = "incomeconsidered")
    String incomeconsidered;
    @Column(name = "abb")
    String abb;
    @Column(name = "avgsal")
    String avgsal;
    @Column(name = "depamt")
    String depamt;

    @Column(name = "sibcustomer")
    String sibcustomer;
    @Column(name = "residentflg")
    String residentflg;
    @Column(name = "applname")
    String applname;

    @Column(name = "recloanamt")
    private BigDecimal recLoanAmt;

    @Column(name = "roitype")
    private String roiyype;

    @Column(name = "foirtype")
    private String foirtype;

    @Column(name = "gencomplete")
    private String gencomplete;

    @Column(name = "kyccomplete")
    private String kyccomplete;

    @Column(name = "basiccomplete")
    private String basiccomplete;
    @Column(name = "employmentcomplete")
    private String employmentcomplete;
    @Column(name = "incomecomplete")
    private String incomecomplete;
    @Column(name = "creditcomplete")
    private String creditcomplete;

    @Column(name = "loancomplete")
    private String loancomplete;
    @Column(name = "vehiclecomplete")
    private String vehiclecomplete;

    @Column(name = "cifcreationmode")
    private String cifcreationmode;

    @Column(name = "cifid")
    private String cifid;
    @Column(name = "relationwithapplicant")
    private String relationwithapplicant;

    @Column(name = "maritalstatus")
    private String maritalstatus;
    @Column(name = "panno")
    private String panno;
    @Column(name = "aadharrefnum")
    private String aadharrefnum;

    @Column(name = "passportnumber")
    private String passportnumber;
    @Column(name = "visaocinumber")
    private String visaocinumber;

    @Column(name = "hunterscore")
    private String hunterscore;
    @Column(name = "decision")
    private String decision;

    @Column(name = "politicallyexposed")
    private String politicallyexposed;

    @Column(name = "state")
    private String state;

    @Column(name = "comresidencetype")
    private String comresidencetype;


}
