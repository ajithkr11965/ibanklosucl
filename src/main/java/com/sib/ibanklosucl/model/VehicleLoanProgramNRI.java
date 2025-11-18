package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "VEHICLE_LOAN_PROGRAM_NRI")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanProgramNRI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @ManyToOne
    @JoinColumn(name = "PROGRAM_INO")
    @JsonBackReference
    private VehicleLoanProgram vlnri;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slNo;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "REMIT_YEAR")
    private Integer remitYear;

    @Column(name = "REMIT_MONTH")
    private Integer remitMonth;

    @Column(name = "TOT_REMITTANCE")
    private Double totRemittance;

    @Column(name = "BULK_REMITTANCE")
    private Double bulkRemittance;

    @Column(name = "NET_REMITTANCE")
    private Double netRemittance;

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
