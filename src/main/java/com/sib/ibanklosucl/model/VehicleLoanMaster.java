package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sib.ibanklosucl.model.integrations.VehicleLoanBREDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "VEHICLE_LOAN_MASTER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VLMAS_SEQ")
    @SequenceGenerator(name = "VLMAS_SEQ", sequenceName = "IBANKLOSUCL.VLMAS_SEQ", allocationSize = 1)
    private Long slno;

    @OneToMany(mappedBy = "vehicleLoanMaster", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<VehicleLoanApplicant> applicants;

    @OneToMany(mappedBy = "vlamberKey", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<VehicleLoanAmber> amberList;

    @OneToMany(mappedBy = "vehicleLoanBRE", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<VehicleLoanBREDetails> vehicleLoanBREDetailsList;

    @OneToMany(mappedBy = "vlamberSubKey", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<VehicleLoanAmberSub> amberSubList;
    @OneToMany(mappedBy = "vehicleLoanAllot", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<VehicleLoanAllotment> vehicleLoanAllotments;

    @OneToMany(mappedBy = "fcvcpvcfrkey", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<VehicleLoanFcvCpvCfr> vehicleLoanFcvCpvCfr;
    @OneToMany(mappedBy = "vehicleLoanMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<VehicleLoanSubqueueTask> subqueueTasks = new ArrayList<>();


    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "RI_RCRE_DATE")
    private Date riRcreDate;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "QUEUE")
    private String queue;

    @Column(name = "QUEUE_DATE")
    private Date queueDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "SOL_ID")
    private String solId;

    @Column(name = "CUST_NAME")
    private String custName;

    @Column(name = "STP")
    private String stp;

    @Column(name = "CMUSER")
    private String cmUser;

    @Column(name = "CMDATE")
    private Date cmDate;

    @Column(name = "HOME_SOL")
    private String homeSol;

    @Column(name = "BUSUNIT_ID")
    private String busUnitId;

    @Column(name = "NUM_COAPPLICANTS")
    private String numCoapplicants;

    @Column(name = "NUM_GUARANTORS")
    private String numGuarantors;

    @Column(name = "SAN_FLG")
    private String sanFlg;

    @Column(name = "SAN_DATE")
    private Date sanDate;
    @Column(name = "SAN_INIT_DATE")
    private Date saninitDate;

    @Column(name = "SAN_USER")
    private String sanUser;

    @Column(name = "BR_VUSER")
    private String brVUser;

    @Column(name = "BR_VDATE")
    private Date brVDate;

    @Column(name = "RBCPC_CMUSER")
    private String rbcpcCmUser;

    @Column(name = "RBCPC_CMDATE")
    private Date rbcpcCmDate;

    @Column(name = "RBCPC_HOME_SOL")
    private String rbcpcHomeSol;

    @Column(name = "RBCPC_VUSER")
    private String rbcpcVUser;

    @Column(name = "RBCPC_VDATE")
    private Date rbcpcVDate;

    @Column(name = "CRT_CMUSER")
    private String crtCmUser;

    @Column(name = "CRT_CMDATE")
    private Date crtCmDate;

    @Column(name = "BR_DOC_CMUSER")
    private String brDocCmUser;

    @Column(name = "BR_DOC_CMDATE")
    private Date brDocCmDate;

    @Column(name = "ACC_OPENED")
    private String accOpened;

    @Column(name = "ACC_NUMBER")
    private String accNumber;

    @Column(name = "ACC_OPEN_DATE")
    private Date accOpenDate;

    @Column(name = "DISB_FLG")
    private String disbFlg;

    @Column(name = "DISB_DATE")
    private Date disbDate;

    @Column(name = "DISB_AMT")
    private String disbAmt;

    @Column(name = "RESUBMIT_FLG")
    private String resubmitFlg;

    @Column(name = "ACTIVE_FLG")
    private String activeFlg;
    @Column(name = "currenttab")
    private String currentTab;

    @Lob
    @Column(name = "DSA_SANC_DOC", columnDefinition = "CLOB")
    private String dsaSancDoc;

    @Column(name = "OWNER_APPLICANT_ID")
    private Long ownerApplicantId;

    @Column(name = "FIRST_TIME_BUYER")
    private String firstTimeBuyer;
    @Column(name = "REJ_FLG")
    private String rejFlg;
    @Column(name = "REJ_DATE")
    private Date rejDate;
    @Column(name = "REJ_USER")
    private String rejUser;
    @Column(name = "REJ_QUEUE")
    private String rejQueue;
    @Lob
    @Column(name = "PRE_DISB_CONDITION", columnDefinition = "CLOB")
    private String preDisbCondition;
    @Column(name = "RBCPC_MAKER_DECISION")
    private String rbcpcMakerDecision;
    @Column(name = "RBCPC_CHECKER_DECISION")
    private String rbcpcCheckerDecision;
     @Column(name = "CHARGE_WAIVER_REQUESTED")
    private Boolean chargeWaiverRequested;

    @Column(name = "ROI_REQUESTED")
    private Boolean roiRequested;
    @Column(name = "SANC_MOD_REQUIRED")
    private Boolean sancModRequired;
    @Column(name = "REPAYMENT_STATUS")
    private Boolean repaymentStatus;
    @Column(name = "DOC_MODE")
    private String docMode;

    @Column(name = "DOC_QUEUE_OVERALL_STATUS")
    private String docQueueOverallStatus;

    @Column(name = "DOC_UPLOAD_USER")
    private String docUploadUser;
    @Column(name = "DOC_UPLOAD_DATE")
    private Date docUploadDate;

    @Column(name = "DOC_COMP_DATE")
    private Date docCompDate;

    @Column(name = "MARGIN_RECEIPT")
    private String marginReceipt;

    @Column(name = "MODE_OPER")
    private String modeOper;
    @Column(name = "STAMP_AMT")
    private BigDecimal stampAmt;


    @Column(name = "PD_USER")
    private String pdUser;

    @Column(name = "PD_DATE")
    private Date pdDate;

    @Column(name = "REF_NO")
    private String refNo;

    @Column(name = "RBCPC_CHECKER_USER")
    private String rbcpcCheckerUser;

  public void addSubqueueTask(VehicleLoanSubqueueTask task) {
        subqueueTasks.add(task);
        task.setVehicleLoanMaster(this);
    }

    public void removeSubqueueTask(VehicleLoanSubqueueTask task) {
        subqueueTasks.remove(task);
        task.setVehicleLoanMaster(null);
    }


    @OneToMany(mappedBy = "vehicleLoanMaster")
    private List<VehicleLoanAcctLabels> vehicleLoanAcctLabels;


    public boolean isDocCompleted(){
        return this.getDocQueueOverallStatus()!=null && "COMPLETED".equalsIgnoreCase(this.getDocQueueOverallStatus());
    }
}
