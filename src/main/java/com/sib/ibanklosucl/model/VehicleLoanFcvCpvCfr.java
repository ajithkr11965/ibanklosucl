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
@Table(name = "VEHICLE_LOAN_FCV_CPV_CFR_DETAILS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanFcvCpvCfr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @ManyToOne
    @JoinColumn(name = "SLNO", insertable = false, updatable = false)
    private VehicleLoanMaster fcvcpvcfrkey;

    @Column(name = "WI_NUM", length = 13)
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "FCV_STATUS", length = 50)
    private String fcvStatus;

    @Column(name = "CPV_STATUS", length = 50)
    private String CpvStatus;

    @Column(name = "CFR_STATUS", length = 5)
    private String cfrStatus;

    @Column(name = "DEL_FLG", length = 1)
    private String delFlg;

    @Column(name = "FCV_FILE_UPLOADED")
    private Boolean fcvFileUploaded;

    @Column(name = "CPV_FILE_UPLOADED")
    private Boolean cpvFileUploaded;
    @Column(name = "CFR_FILE_UPLOADED")
    private Boolean cfrFileUploaded;

    @Column(name = "CMUSER", length = 10)
    private String cmuser;

    @Column(name = "CMDATE")
    @Temporal(TemporalType.DATE)
    private Date cmdate;


}