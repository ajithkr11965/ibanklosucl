package com.sib.ibanklosucl.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "VEHICLE_LOAN_FINANCIAL")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanFinancial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ino;
    @Column(name = "APPLICANT_ID", nullable = false)
    private Long applicantId;
    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;
    @Column(name = "SLNO", nullable = false)
    private Long slno;
    @Column(name = "TOT_OBLIGATIONS")
    private Double totObligations;
    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;
    @Column(name = "DEL_FLG")
    private String delFlg;
    @Column(name = "LAST_MOD_USER")
    private String lastModUser;
    @Column(name = "LAST_MOD_DATE")
    private Date lastModDate;
    @Column(name = "HOME_SOL")
    private String homeSol;
    @Column(name = "DEL_USER")
    private String delUser;
    @Column(name = "DEL_DATE")
    private Date delDate;
    @Column(name = "DEL_HOME_SOL")
    private String delHomeSol;
}
