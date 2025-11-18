
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
@Table(name = "VEHICLE_LOAN_CIF")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanCIF {
    @ManyToOne
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private VehicleLoanApplicant vehicleLoanCif;

    @ManyToOne
    @JoinColumn(name = "task_id", updatable = false)
    private VehicleLoanSubqueueTask vehicleLoanSubqueueTask;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INO", nullable = false)
    private Long ino;

    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "SLNO", nullable = false)
    private Long slno;

    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;

    @Column(name = "rcredate")
    private Date rcreDate;

    @Column(name = "DECISION", length = 20)
    private String decision;//APPROVE,REJECT,null

    @Column(name = "decisionuser", length = 10)
    private String decisionUser;

    @Column(name = "decisiondate")
    private Date decisiondate;


    @Column(name = "blflag", length = 10)
    private String blFlag;


    @Column(name = "bluser", length = 10)
    private String blUser;

    @Column(name = "bldate")
    private Date blDate;


    @Column(name = "cifid", length = 10)
    private String cifId;

    @Column(name = "cifflag", length = 1)
    private String cifflag;//Y,N


    @Column(name = "cifuser", length = 10)
    private String cifUser;
    @Column(name = "cifdate")
    private Date cifDate;


    @Column(name = "ckycflag", length = 1)
    private String ckycflag;//Y,N
    @Column(name = "ckycuser", length = 10)
    private String ckycUser;
    @Column(name = "ckycdate")
    private Date ckycDate;



    @Column(name = "remarks", length = 300)
    private String remarks;
    @Column(name = "rmkuser", length = 10)
    private String rmkUser;
    @Column(name = "rmkdate")

    private Date rmkDate;
    @Column(name = "delflag", length = 1)
    private String delFlag;//Y,N
    @Column(name = "ipaddress", length = 30)
    private  String  ipaddress;

    @Column(name = "workitemnumber", length = 30)
    private  String  workitemnumber;

}
