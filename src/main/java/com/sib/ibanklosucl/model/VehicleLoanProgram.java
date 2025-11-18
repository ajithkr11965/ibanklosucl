package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "VEHICLE_LOAN_PROGRAM")
@Audited
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class VehicleLoanProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @OneToOne
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    @JsonBackReference
    private VehicleLoanApplicant vlprogram;
    @OneToMany(mappedBy = "vlfd", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<VehicleLoanFD> vehicleLoanFDList = new ArrayList<>();
    @OneToMany(mappedBy = "vlitr", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<VehicleLoanITR> vehicleLoanITRList = new ArrayList<>();
    @OneToMany(mappedBy = "vlbsa", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<VehicleLoanBSA> vehicleLoanBSAList = new ArrayList<>();
    @OneToMany(mappedBy = "vlnri", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<VehicleLoanProgramNRI> vehicleLoanProgramNRIList = new ArrayList<>();
    @OneToMany(mappedBy = "vlprogramSal", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<VehicleLoanProgramSalary> vehicleLoanProgramSalaryList = new ArrayList<>();


    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "SLNO", nullable = false)
    private Long slNo;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "INCOME_CONSIDERED")
    private String incomeConsidered;

    @Column(name = "LOAN_PROGRAM")
    private String loanProgram;

    @Column(name = "DOCTYPE")
    private String doctype;

    @Column(name = "DOB")
    private Date dob;

    @Column(name = "PAN")
    private String pan;

    @Column(name = "ITR_FLG")
    private String itrFlg;
    @Column(name = "FORM16_FLG")
    private String form16Flg;

    @Column(name = "SAL_SLIP_MONTHS")
    private String salSlipMonths;

    @Column(name = "ACCT_STMT_MONTHS")
    private String acctStmtMonths;

    @Column(name = "ITR_MONTHS")
    private String itrMonths;

    @Column(name = "NUM_SAL_SLIP_FILES")
    private String numSalSlipFiles;

    @Column(name = "NUM_ACCT_STMT_FILES")
    private String numAcctStmtFiles;

    @Column(name = "NUM_ITR_FILES")
    private String numItrFiles;

    @Column(name = "RESIDENT_TYPE")
    private String residentType;

    @Column(name = "AVG_SAL")
    private BigDecimal avgSal;

    @Column(name = "ABB")
    private BigDecimal abb;
    @Column(name="DESIRED_INCOME")
    private BigDecimal desiredIncome;

    @Column(name = "SANCTION_DATE")
    private Date sanctionDate;

    @Column(name = "DEP_AMT")
    private BigDecimal depAmt;

    @Column(name = "CMUSER")
    private String cmUser;

    @Column(name = "CMDATE")
    private Date cmDate;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "HOME_SOL")
    private String homeSol;
    @Column(name = "foir_rate")
    private BigDecimal foirRate;
    @Column(name = "nri_net_salary")
    private BigDecimal nriNetSalary;
	@Column(name = "avg_total_remittance")
    private BigDecimal avgTotalRemittance;
	@Column(name = "avg_bulk_remittance")
    private BigDecimal avgBulkRemittance;
	@Column(name = "avg_net_remittance")
    private BigDecimal avgNetRemittance;

}
