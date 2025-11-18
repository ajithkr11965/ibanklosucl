package com.sib.ibanklosucl.model;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_WARN")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Audited
public class VehicleLoanWarn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @Column(name = "wi_num", length = 13)
    private String wiNum;

    @Column(name = "slno")
    private Long slno;


    @Column(name = "APPLICANT_TYPE", length = 100)

    private String applicantType;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "WARN_CODE", length = 10)
    private String warnCode;

    @Column(name = "WARN_desc", length = 100)
    private String warnDesc;
    @Column(name = "severity", length = 100)
    private String severity;

    @Column(name = "CBS_VALUE", length = 500)
    private String cbsValue;

    @Column(name = "WI_VALUE", length = 500)
    private String wiValue;

    @Column(name = "QUEUE", length = 5)
    private String queue;

    @Column(name = "req_ip_addr", length = 19)
    private String reqIpAddr;

    @Column(name = "del_flg", length = 1)
    private String delFlg;

    @Column(name = "last_mod_user", length = 10)
    private String lastModUser;

    @Column(name = "last_mod_date")
    private Date lastModDate;

    @Column(name = "home_sol", length = 10)
    private String homeSol;

    @Column(name = "del_user", length = 10)
    private String delUser;

    @Column(name = "del_date")
    private Date delDate;

    @Column(name = "del_home_sol", length = 10)
    private String delHomeSol;

    @Column(name = "active_flg", length = 1)
    private String activeFlg;

    @Column(name = "MAILSENTFLG", length = 1)
    private String mailSentFlg;

    @Column(name = "MAILDATE")
    private Date mailDate;

    @Column(name = "MAILID_FROM", length = 200)
    private String mailIdFrom;

    @Column(name = "MAILID_TO", length = 200)
    private String mailIdTo;

    @Column(name = "MAILID_CC", length = 200)
    private String mailIdCc;

}

