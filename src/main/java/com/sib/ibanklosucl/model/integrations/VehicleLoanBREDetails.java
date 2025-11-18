package com.sib.ibanklosucl.model.integrations;

import com.sib.ibanklosucl.model.VehicleLoanMaster;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_BRE_DETAILS")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanBREDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @ManyToOne
    @JoinColumn(name = "slno", insertable = false, updatable = false)
    private VehicleLoanMaster vehicleLoanBRE;

     @Column(name = "SLNO")
    private Long slno;

    @Column(name = "wi_num")
    private String wiNum;

    @Column(name = "eligibility_flag")
    private String eligibilityFlag;

    @Column(name = "bre_flag")
    private String breFlag;
    @Lob
    @Column(name = "eligibility_data")
    private String eligibilityData;
    @Lob
    @Column(name = "bre_data")
    private String breData;
    @Column(name = "DEL_FLG")
    private String delFlg;
    @Column(name = "cmuser", length = 10)
    private String cmUser;
    @Column(name = "cmdate")
    @Temporal(TemporalType.DATE)
    private Date cmDate;
}
