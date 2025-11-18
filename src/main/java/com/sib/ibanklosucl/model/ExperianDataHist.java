package com.sib.ibanklosucl.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "EXPERIAN_DATA_HIST")
@ToString
@Getter
@Setter
public class ExperianDataHist {
    @Id

    @Column(name = "INO")
    private Long ino;
    @Column(name = "ACTIVE_FLG")
    private String activeFlg;

    @Column(name = "CMDATE")
    @Temporal(TemporalType.DATE)
    private Date cmDate;

    @Column(name = "CMUSER")
    private String cmUser;

    @Lob
    @Column(name = "EXPERIACCIRJSONREPORT", columnDefinition = "CLOB")
    private String expJsonRep;

    @Column(name = "MODELSCOREVALUE")
    private Long modScore;

    @Column(name = "ORDERID")
    private String orderId;

    @Column(name = "SLNO")
    private String slNo;

    @Column(name = "SUCCESS_FLG")
    private String sucFlg;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "APPID")
    private String appId;
    @Lob
    @Column(name = "EXPERIANCCIRJPDFREPORT", columnDefinition = "CLOB")
    private String expJpdRep;
    @Column(name = "PAN")
    private String pan;
    @Column(name = "AUDIT_TIMESTAMP")
    @Temporal(TemporalType.DATE)
    private Date audTimeStamp;
}
