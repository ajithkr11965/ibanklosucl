package com.sib.ibanklosucl.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_ACCT_LABELS")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanAcctLabels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INO", nullable = false)
    private Long ino;

    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;


    @Column(name = "SLNO", nullable = false)
    private Long slno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SLNO", insertable = false, updatable = false)
    private VehicleLoanMaster vehicleLoanMaster;

    @Column(name = "acct_label", length = 100)
    private String acctLabel;

    @Column(name = "label_text", length = 200)
    private String labeltext;
    @Column(name = "cmuser", length = 10)
    private String cmUser;

    @Column(name = "cmdate")
    private Date cmDate;
}
