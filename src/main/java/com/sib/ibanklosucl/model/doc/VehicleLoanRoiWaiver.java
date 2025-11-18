package com.sib.ibanklosucl.model.doc;

import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_ROIWAIVER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanRoiWaiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    private Long slno;

    private String wiNum;

    private Long taskId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskId", nullable = true, insertable = false, updatable = false)
    private VehicleLoanSubqueueTask subTask;


    private String stp;

    @Column(name = "ROI_TYPE")
    private String roiType;

    @Column(name = "SANC_AMOUNT")
    private BigDecimal sancAmount;

    @Column(name = "SANC_ROI")
    private BigDecimal sancRoi;

    private Integer sanctenor;

    private BigDecimal sancemi;

    private BigDecimal ebr;

    @Column(name = "OPERATIONAL_COST")
    private BigDecimal operationalCost;

    private BigDecimal crp;

    private BigDecimal spread; //actual spread
    private BigDecimal baseSpread;//requested spread
    private BigDecimal sancBaseSpread;//sanctioned spread

    @Column(name = "REVISED_ROI")
    private BigDecimal revisedRoi;
    @Column(name = "INITIAL_ROI")
    private BigDecimal initialRoi;

    @Column(name = "REVISED_EMI")
    private BigDecimal revisedEmi;
    @Column(name = "ROIWAIVER_REMARKS")
    private String roiwaiverRemarks;
    @Column(name = "ROIWAIVER_SANC_REMARKS", length = 1500)
    private String roiwaiverSancRemarks;

    private String decision;

    private String lastModUser;

    private Date lastModDate;

    private String lastModSol;

}
