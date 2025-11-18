package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sib.ibanklosucl.model.doc.VehicleLoanChargeWaiver;
import com.sib.ibanklosucl.model.doc.VehicleLoanRoiWaiver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "VEHICLE_LOAN_SUBQUEUE_TASK")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanSubqueueTask {
    /*
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VL_SUBQUEUE_SEQ")
    @SequenceGenerator(name = "VL_SUBQUEUE_SEQ", sequenceName = "IBANKLOSUCL.VL_SUBQUEUE_SEQ", allocationSize = 1)
    private Long taskId;
*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SLNO", nullable = false, insertable = false, updatable = false)
    private VehicleLoanMaster vehicleLoanMaster;

/*
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false, insertable = false, updatable = false)
    private VehicleLoanCIF vehicleLoanCIF;
/*
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vlsanmod", insertable = false, updatable = false)
    private VehicleLoanSanMod vlsanmod;
*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICANT_ID", nullable = true)
    private VehicleLoanApplicant applicant;

    @OneToOne(mappedBy = "subTask", fetch = FetchType.LAZY)
    private VehicleLoanRoiWaiver roiWaiver;
    @OneToMany(mappedBy = "feesubTask", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<VehicleLoanChargeWaiver> feeWaiver;


    @Column(name = "TASK_TYPE", nullable = false)
    private String taskType;  // "CHARGE_WAIVER", "ROI_WAIVER", "CIF_CREATION","VKYC","BRCOMPLETED"

    @Column(name = "STATUS", nullable = false)
    private String status;  // "PENDING", "COMPLETED", "REJECTED"
    @Column(name = "slno", nullable = false)
    private Long slno;
    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "CREATED_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "CREATE_SOL")
    private String createSol;
    @Column(name = "decision")
    private String decision;
    @Column(name = "COMPLETED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completedDate;

    @Column(name = "COMPLETED_USER")
    private String completedUser;

    @Column(name = "REMARKS")
    private String remarks;
        @Column(name = "LOCK_FLG")
    private String lockFlg;  // 'Y' for locked, 'N' for unlocked

    @Column(name = "LOCKED_BY")
    private String lockedBy;

    @Column(name = "LOCKED_ON")
    private Timestamp lockedOn;

       public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(this.status) || "BRCOMPLETED".equalsIgnoreCase(this.status) ;
    }
       public boolean isPending() {
        return "PENDING".equalsIgnoreCase(this.status);
    }
       public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(this.status);
    }
       public boolean isRecalled() {
        return "RECALLED".equalsIgnoreCase(this.status);
    }
       public boolean isLocked() {
        return "Y".equalsIgnoreCase(this.lockFlg);
    }

}
