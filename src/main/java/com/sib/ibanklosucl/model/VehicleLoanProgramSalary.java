package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_PROGRAM_SALARY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanProgramSalary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @ManyToOne
    @JoinColumn(name = "PROGRAM_INO")
    @JsonBackReference
    private VehicleLoanProgram vlprogramSal;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "SAL_MONTH")
    private Integer salMonth;

    @Column(name = "SAL_YEAR")
    private Integer salYear;

    @Column(name = "SAL_AMOUNT")
    private java.math.BigDecimal salAmount;

    @Column(name = "SALARY_DOC")
    private String salaryDoc;

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
