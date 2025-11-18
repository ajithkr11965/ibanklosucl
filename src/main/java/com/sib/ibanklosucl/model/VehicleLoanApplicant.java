package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sib.ibanklosucl.model.integrations.VLBlackList;
import com.sib.ibanklosucl.model.integrations.VLHunterDetails;
import com.sib.ibanklosucl.model.integrations.VLPartialBlackList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "VEHICLE_LOAN_APPLICANTS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanApplicant {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vlapplicants_seq")
    @SequenceGenerator(name = "vlapplicants_seq", sequenceName = "IBANKLOSUCL.VLAPPLICANTS_SEQ", allocationSize = 1)
    private Long applicantId;


    @ManyToOne
//    @JsonManagedReference
    @JoinColumn(name = "slno", insertable = false, updatable = false)
    private VehicleLoanMaster vehicleLoanMaster;

    @OneToOne(mappedBy = "applicant", fetch = FetchType.LAZY)
    private VehicleLoanKyc kycapplicants;

    @OneToOne(mappedBy = "bsapplicant", fetch = FetchType.LAZY)
    private VehicleLoanBasic basicapplicants;
    @OneToOne(mappedBy = "vlprogram", fetch = FetchType.LAZY)
    @JsonManagedReference
    private VehicleLoanProgram vlProgram;
    @OneToOne(mappedBy = "vlemployeement", fetch = FetchType.LAZY)
    @JsonManagedReference
    private VLEmployment vlEmployment;

    @OneToOne(mappedBy = "vlcreditappid", fetch = FetchType.LAZY)
    @JsonManagedReference
    private VLCredit vlcredit;
    @OneToMany(mappedBy = "vlblacklist", fetch = FetchType.LAZY)
    private List<VLBlackList> vlBlackList = new ArrayList<>();

    @OneToMany(mappedBy = "vlpartialblacklist", fetch = FetchType.LAZY)
    private List<VLPartialBlackList> vlPartialBlackList = new ArrayList<>();

    @OneToMany(mappedBy = "vehicleLoanCif", fetch = FetchType.LAZY)
    private List<VehicleLoanCIF> vehicleLoanCif = new ArrayList<>();

    @OneToMany(mappedBy = "vehicleLoanApplicant", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<VehicleLoanAmberSub> amberSubList = new ArrayList<>();


    @OneToMany(mappedBy = "applicant", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTaskList = new ArrayList<>();
    @OneToMany(mappedBy = "vlHunterlist", fetch = FetchType.LAZY)
     @JsonManagedReference
    private List<VLHunterDetails> vlHunterDetailsList = new ArrayList<>();

    @OneToMany(mappedBy = "vldedupe", cascade = CascadeType.ALL)
    private List<VehicleLoanSingleDedupe> vlDedupe = new ArrayList<>();


//    @OneToMany(mappedBy = "lsapplicant",fetch = FetchType.LAZY)
//    @LazyCollection(LazyCollectionOption.FALSE)
//    private List<LosDedupeEntity> losdeduplist;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "APPLICANT_TYPE")
    private String applicantType;

    @Column(name = "RESIDENT_FLG")
    private String residentFlg;

    @Column(name = "SIB_CUSTOMER")
    private String sibCustomer;

    @Column(name = "CIF_ID")
    private String cifId;

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

    @Column(name = "APPL_NAME")
    private String applName;

    @Column(name = "APPL_DOB")
    private Date applDob;

    @Column(name = "BPM_FOLDER_NAME")
    private String bpmFolderName;

    @Column(name = "BPM_INDEX_NO")
    private String bpmIndexNo;

    @Column(name = "RELATION_WITH_APPLICANT")
    private String relationWithApplicant;
    @Column(name = "canvassed_ppc")
    private String canvassedppc;
    @Column(name = "canvassed_ppc_name")
    private String canvassedppcname;
    @Column(name = "rsm_ppc")
    private String rsmppc;
    @Column(name = "rsm_ppc_name")
    private String rsmppcname;
    @Column(name = "rsm_sol")
    private String rsmsol;
    @Column(name = "rsm_sol_name")
    private String rsmsolname;

    @Column(name = "GEN_COMPLETE")
    private String genComplete;

    @Column(name = "KYC_COMPLETE")
    private String kycComplete;

    @Column(name = "BASIC_COMPLETE")
    private String basicComplete;

    @Column(name = "EMPLOYMENT_COMPLETE")
    private String employmentComplete;

    @Column(name = "INCOME_COMPLETE")
    private String incomeComplete;

    @Column(name = "CREDIT_COMPLETE")
    private String creditComplete;

    @Column(name = "FINANCIAL_COMPLETE")
    private String financialComplete;

    @Column(name = "VEHICLE_COMPLETE")
    private String vehicleComplete;

    @Column(name = "INSURANCE_COMPLETE")
    private String insuranceComplete;

    @Column(name = "LOAN_COMPLETE")
    private String loanComplete;

    @Column(name = "BLACKLIST_CHECK")
    private String blacklistCheck;

    @Column(name = "HUNTER_CHECK")
    private String hunterCheck;

    @Column(name = "RACE_SCORE")
    private String raceScore;

    @Column(name = "RACE_DK_SLNO")
    private String raceDKSlno;

//    @Column(name = "BSA_PERFIOS_TXN_ID")
//    private String bsaPerfiosTxnId;
//
//    @Column(name = "ITR_PERFIOS_TXN_ID")
//    private String itrPerfiosTxnId;

    @Column(name = "APPL_RCRE_USER")
    private String applRcreUser;

    @Column(name = "APPL_RCRE_DATE")
    private Date applRcreDate;
    @Column(name = "cif_Creation_Mode")
    private String cifCreationMode;


    @Column(name = "lh_ppcno")
    private String lhppcno;
    @Column(name = "lh_sol")
    private String lhsol;
    @Column(name = "lh_sol_name")
    private String lhsolname;

    @Column(name = "rah_ppcno")
    private String rahppcno;
    @Column(name = "rah_sol")
    private String rahsol;
    @Column(name = "rah_sol_name")
    private String rahsolname;


}
