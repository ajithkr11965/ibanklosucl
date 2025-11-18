package com.sib.ibanklosucl.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_SUBQUEUE_TAT")
@Getter
@Setter
@Audited
@AllArgsConstructor
@NoArgsConstructor
public class VehicleLoanSubqueueTat {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vlSubqueueTatSeqGen")
    @SequenceGenerator(name = "vlSubqueueTatSeqGen", sequenceName = "IBANKLOSUCL.VL_SUBQUEUE_TAT_SEQ", allocationSize = 1)
    private Long ino;
    @Column(name = "wi_num")
    private String wiNum;
    @Column(name = "slno")
    private Long slno;
   @Column(name = "task_id")
    private Long taskId;

    @Column(name = "task_type")
    private String taskType;

    @Column(name = "req_ip_addr")
    private String reqIpAddr;
    @Column(name = "subqueue")
    private String subqueue;
    @Column(name = "action")
    private String action;

    @Column(name = "subqueue_entry_user")
    private String subqueueEntryUser;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subqueue_entry_date")
    private Date subqueueEntryDate;
    @Column(name = "subqueue_exit_user")
    private String subqueueExitUser;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subqueue_exit_date")
    private Date subqueueExitDate;
    @Column(name = "wi_sol")
    private String wiSol;
    @Column(name = "del_flg")
    private String delFlg;
}
