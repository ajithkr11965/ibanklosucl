package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_FD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanFD {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @ManyToOne
    @JoinColumn(name = "program_ino")
    @JsonBackReference
    private VehicleLoanProgram vlfd;
    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "FDACCNUM")
    private String fdaccnum;

    @Column(name = "FDACCNAME")
    private String fdaccname;

    @Column(name = "AVAILBALANCE")
    private BigDecimal availbalance; // (FD_BAL_AMOUNT-FSLD_ADJ_AMOUNT)

    @Column(name = "CIFID")
    private String cifid;

    @Column(name = "SINGLE_JOINT")
    private String singleJoint;

    @Column(name = "FD_STATUS")
    private String fdStatus;

    @Column(name = "LAST_MOD_DATE")
    private Date lastModDate;

    @Column(name = "ACCOUNT_OPEN_DATE")
    private Date accountOpenDate;

    @Column(name = "MATURITY_DATE")
    private Date maturityDate;

    @Column(name = "DEPOSIT_AMOUNT")
    private BigDecimal depositAmount;

    @Column(name = "MATURITY_AMOUNT")
    private BigDecimal maturityAmount;
    @Column(name = "eligible")
    private boolean eligible;
    @Column(name = "FD_BAL_AMOUNT")
    private BigDecimal fdBalAmount;// After subtracting the lien and other factors as in the FD query
    @Column(name = "FSLD_ADJ_AMOUNT")
    private BigDecimal fsldAdjAmount; //FSLD adjustment values

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "LAST_MOD_USER")
    private String lastModUser;

    @Column(name = "HOME_SOL")
    private String homeSol;

    @Column(name = "DEL_USER")
    private String delUser;

    @Column(name = "DEL_DATE")
    private Date delDate;

    @Column(name = "DEL_HOME_SOL")
    private String delHomeSol;
    @Column(name = "ACID")
    private String acid;
    @Column(name = "LIEN_STATUS")
    private String lienStatus;

    @Column(name = "LIEN_AMOUNT")
    private BigDecimal lienAmount;

    @Column(name = "LIEN_DATE")
    private Date lienDate;
}
