package com.sib.ibanklosucl.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "VEHICLE_LOAN_QUEUE_DETAILS")
@Data
@Getter
@Setter

public class VehicleLoanQueueDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "SLNO", nullable = false)
    private Long slno;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @Column(name = "CMUSER", nullable = false)
    private String cmuser;

    @Column(name = "CMDATE", nullable = false)
    @CreationTimestamp
    private LocalDateTime CMDATE;

    @Column(name = "FROM_QUEUE")
    private String fromQueue;

    @Column(name = "TO_QUEUE")
    private String toQueue;
    @Column(name = "ASSIGN_USER")
    private String assignUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SLNO", insertable = false, updatable = false)
    private VehicleLoanMaster vehicleLoanMaster;

}
