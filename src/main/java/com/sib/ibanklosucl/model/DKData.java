package com.sib.ibanklosucl.model;

import lombok.*;

import javax.persistence.*;

import java.util.Date;


@Data
@Entity
@Table(name = "DK_DATA")
@Getter
@Setter
public class DKData {
    @Id
    @Column(name = "ino")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @Column(name = "score")
    private String ScoreValue;
    @Column(name = "racescore")
    private String raceScoreValue;
    @Column(name = "delinquencyAnalysis")
    @Lob
    private String delinquencyAnalysis;

    @Column(name = "liabilityAnalysis")
    @Lob
    private String liablityAnalysis;

    @Column(name = "liabilityList")
    @Lob
    private String liablityList;
    @Column(name = "wi_num")
    private String winumber;
    @Column(name = "slno")
    private String slno;
    @Column(name = "appid")
    private String appid;
    @Column(name = "experian_ino")
    private String experian_ino;
    @Column(name = "cmuser")
    private String cmuser;
    @Column(name = "cmdate")
    private Date cmdate;
    @Column(name = "active_flg")
    private String activeFlg;

    @Column(name = "success_flg")
    private String successFlg;
}
