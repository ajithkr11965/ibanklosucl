package com.sib.ibanklosucl.model;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_DETAILS")
@ToString
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_details_seq")
    @SequenceGenerator(name = "loan_details_seq", sequenceName = "VLLOANDETSEQ", allocationSize = 1)
    private Long ino;

    private String wiNum;
    private Long slno;
    private BigDecimal vehicleAmt;
    private Long applicantId;
    private String reqIpAddr;
    private BigDecimal loanAmt;
    private BigDecimal recLoanAmt;
    private int tenor;
    private String roiType;
    private String foirType;
    private String insVal;
    private String insType;
    private BigDecimal insAmt;
    private String cmuser;
    private Date cmdate;
    private String delFlg;
    private String homeSol;


    private String insTranID;
    private java.time.LocalDateTime insTranDate;
    private String insCreditAcc;

    @Column(name = "ficmuser", length = 10)
    private String ficmuser;
    @Column(name = "ficmdate")
    private Date ficmdate;

}
