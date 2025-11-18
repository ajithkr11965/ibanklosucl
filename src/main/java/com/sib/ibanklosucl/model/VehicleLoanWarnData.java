package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_WARN_DATA")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanWarnData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "SLNO", nullable = false)
    private Long slNo;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "WARN_CODE", length = 10)
    private String warnCode;
    @Column(name = "SEVERITY", length = 10)
    private String severity;
    @Column(name = "SEVERITY_DESC", length = 20)
    private String severityDesc;

    @Column(name = "WARN_desc", length = 100)
    private String warnDesc;

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
}
