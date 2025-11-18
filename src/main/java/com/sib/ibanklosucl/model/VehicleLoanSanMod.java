
package com.sib.ibanklosucl.model;

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
@Table(name = "VEHICLE_LOAN_SANCTION_MOD")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanSanMod {
/*
    @OneToOne
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    private VehicleLoanSubqueueTask vlsanmod;
*/

    @OneToOne
    @JoinColumn(name = "task_id", referencedColumnName = "task_id")
    private VehicleLoanSubqueueTask vlsanmod;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INO", nullable = false)
    private Long ino;

    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "SLNO", nullable = false)
    private Long slno;

    @Column(name = "rcredate")
    private Date rcreDate;

    @Column(name = "SAN_AMT")
    private BigDecimal sanAmt;
    @Column(name = "req_amt")
    private BigDecimal reqAmt;
    @Column(name = "eff_roi")
    private BigDecimal effRoi;
    @Column(name = "ltv_amt")
    private BigDecimal ltvAmt;
    @Column(name = "tenor")
    private int tenor;
    @Column(name = "emi")
    private BigDecimal emi;
    @Column(name = "revised_san_amt")
    private BigDecimal revisedSanAmt;
    @Column(name = "revised_tenor")
    private BigDecimal revisedTenor;
    @Column(name = "revised_emi")
    private BigDecimal revisedEmi;

    @Column(name = "remarks", length = 500)
    private String remarks;
    @Column(name = "delflag", length = 1)
    private String delFlag;//Y,N
    @Column(name = "cmuser", length = 10)
    private String cmUser;

    @Column(name = "cmdate")
    private Date cmDate;

    @Column(name = "DECISION", length = 20)
    private String decision;//APPROVE,REJECT,null
    @Column(name = "vremarks", length = 500)
    private String vRemarks;
    @Column(name = "vuser", length = 10)
    private String vUser;
    @Column(name = "vdate")
    private Date vDate;
    @Column(name = "ipaddress", length = 30)
    private  String  ipaddress;
    @Column(name = "recall_user", length = 10)
    private String recallUser;
    @Column(name = "recall_date")
    private Date recallDate;


    @Column(name = "san_mod_required", length = 10)
    private String sanModRequired;


    @Column(name = "ELIGIBLE_AMT")
    private BigDecimal eligibleAmt;
}
