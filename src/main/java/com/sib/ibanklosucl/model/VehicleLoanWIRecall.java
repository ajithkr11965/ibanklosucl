package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_RECALL")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehicleLoanWIRecall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "SLNO", nullable = false)
    private Date slno;

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "PREVIOUS_QUEUE", length = 10)
    private String previousQueue;

    @Column(name = "PREVIOUS_STATUS", length = 20)
    private String previousStatus;

    @Column(name = "REMARKS", length = 10)
    private String remarks;

    @Column(name = "CMUSER", length = 10)
    private String cmUser;

    @Column(name = "CMDATE")
    private Date cmDate;

    @Column(name = "HOME_SOL", length = 10)
    private String homeSol;

}
