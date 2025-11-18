package com.sib.ibanklosucl.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_TAT")
@Getter
@Setter
@Audited
public class VehicleLoanTat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vltatSeqGen")
    @SequenceGenerator(name = "vltatSeqGen", sequenceName = "IBANKLOSUCL.VLTAT_SEQ", allocationSize = 1)
    private Long ino;

    @Column(name = "wi_num")
    private String wiNum;

    @Column(name = "slno")
    private Long slno;

    @Column(name = "req_ip_addr")
    private String reqIpAddr;

    @Column(name = "queue")
    private String queue;

    @Column(name = "queue_entry_user")
    private String queueEntryUser;

    @Temporal(TemporalType.DATE)
    @Column(name = "queue_entry_date")
    private Date queueEntryDate;

    @Column(name = "queue_exit_user")
    private String queueExitUser;

    @Temporal(TemporalType.DATE)
    @Column(name = "queue_exit_date")
    private Date queueExitDate;

    @Column(name = "wi_sol")
    private String wiSol;

    @Column(name = "program")
    private String program;

    @Column(name = "loan_amt")
    private String loanAmt;

    @Column(name = "sanctioned_amt")
    private String sanctionedAmt;

    @Column(name = "del_flg")
    private String delFlg;

    // Getters and setters
}
