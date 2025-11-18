package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_ALLOTMENT")
@Data
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanAllotment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INO")
    private Long ino;

    @Column(name = "WI_NUM", length = 13)
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "DO_PPC")
    private Integer doPpc;

    @Column(name = "REQ_IP_ADDR", length = 19)
    private String reqIpAddr;

    @Column(name = "DEL_FLG", length = 1)
    private String delFlg;

    @Column(name = "LAST_MOD_USER", length = 10)
    private String lastModUser;

    @Column(name = "LAST_MOD_DATE")
    @Temporal(TemporalType.DATE)
    private Date lastModDate;

    @Column(name = "HOME_SOL", length = 10)
    private String homeSol;

    @Column(name = "DEL_USER", length = 10)
    private String delUser;

    @Column(name = "DEL_DATE")
    @Temporal(TemporalType.DATE)
    private Date delDate;

    @Column(name = "DEL_HOME_SOL", length = 10)
    private String delHomeSol;

    @Column(name = "ACTIVE_FLG", length = 1)
    private String activeFlg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SLNO", insertable = false, updatable = false)
//    @JsonManagedReference("vehicleLoanMaster-allotments")
    @JsonIgnore
    private VehicleLoanMaster vehicleLoanAllot;
}