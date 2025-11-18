package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_LOCK")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanLock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VLLOCK_SEQ")
    @SequenceGenerator(name = "VLLOCK_SEQ", sequenceName = "IBANKLOSUCL.VLLOCK_SEQ", allocationSize = 1)
    private Long ino;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "LOCKED_BY")
    private String lockedBy;

    @Column(name = "LOCKED_ON")
    private Date lockedOn;

    @Column(name = "LOCK_FLG")
    private String lockFlg;

    @Column(name = "RELEASED_BY")
    private String releasedBy;

    @Column(name = "RELEASED_ON")
    private Date releasedOn;

    @Column(name = "QUEUE")
    private String queue;

    @Column(name = "DEL_FLG")
    private String delFlg;

}

