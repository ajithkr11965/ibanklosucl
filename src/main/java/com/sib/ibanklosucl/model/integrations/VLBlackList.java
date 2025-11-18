package com.sib.ibanklosucl.model.integrations;

import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_BLACKLIST_DETAILS")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VLBlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @ManyToOne
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private VehicleLoanApplicant vlblacklist;
    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "SLNO", nullable = false)
    private Long slNo;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;
    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;
    @Column(name = "DOB")
    private String dob;
    @Column(name = "PAN")
    private String pan;
    @Column(name = "PASSPORT")
    private String passport;
    @Column(name = "CUSTNAME")
    private String custName;
    @Column(name = "BL_CHECK_DATE")
    private Date blCheckDate;
    @Lob
    @Column(name = "fetch_response")
    private String fetchResponse;
    @Column(name = "BL_CHECK_RESULT")
    private String blCheckResult;
    @Column(name ="DEL_FLG")
    private String delFlg;
    @Column(name = "cmuser", length = 10)
    private String cmUser;
    @Column(name = "cmdate")
    @Temporal(TemporalType.DATE)
    private Date cmDate;
}
