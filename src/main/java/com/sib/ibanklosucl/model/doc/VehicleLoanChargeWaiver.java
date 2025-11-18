package com.sib.ibanklosucl.model.doc;

import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_CHARGE_WAIVER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanChargeWaiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;
    private Long taskId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskId", nullable = true, insertable = false, updatable = false)
    private VehicleLoanSubqueueTask feesubTask;


    @Column(name = "fee_code", nullable = false)
    private String feeCode;

    @Column(name = "fee_name", nullable = false)
    private String feeName;

    @Column(name = "fee_value", nullable = false)
    private BigDecimal feeValue;
    @Column(name = "fee_sanc_value")
    private BigDecimal feeSancValue;

    @Column(name = "fee_value_rec")
    private BigDecimal feeValueRec;
    @Column(name = "final_fee", nullable = false)
    private BigDecimal finalFee;
    @Column(name = "waiver_flg", nullable = false)
    private String waiverFlg;

    private String feeRemarks;
    private String decision;
    @Column(name = "FEE_WAIVER_SANC_REMARKS",length = 1500)
    private String feeWaiverSancRemarks;

    @Column(name = "del_flag", nullable = false)
    private String delFlag;
    @Column(name = "LAST_MOD_USER")
    private String lastModUser;

    @Column(name = "LAST_MOD_DATE")
    private Date lastModDate;
    @Column(name = "LAST_MOD_SOL")
    private String lastModSol;
    @Column(name = "FREQUENCY")
    private String frequency;
}
