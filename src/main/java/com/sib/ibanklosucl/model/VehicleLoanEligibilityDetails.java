package com.sib.ibanklosucl.model;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_ELIGIBILITY_DETAILS")
@ToString
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanEligibilityDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INO")
    private Long ino;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "QUEUE")
    private String queue;

    @Column(name = "ELIGIBILITY_FLAG")
    private String eligibilityFlag;

    @Lob
    @Column(name = "ELIGIBILITY_REQUEST")
    private String eligibilityRequest;

    @Lob
    @Column(name = "ELIGIBILITY_RESPONSE")
    private String eligibilityResponse;
    @Column(name = "CMDATE")
    @Temporal(TemporalType.DATE)
    private Date cmDate;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "DEL_DATE")
    @Temporal(TemporalType.DATE)
    private Date delDate;

}
