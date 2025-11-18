package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "VEHICLE_LOAN_EMPLOYMENT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VLEmployment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vlemployment_seq")
    @SequenceGenerator(name = "vlemployment_seq", sequenceName = "VLEMPLOYMENT_SEQ", allocationSize = 1)
    private Long ino;

    @Column(name = "WI_NUM")
    private String wiNum;

    @OneToOne
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    @JsonBackReference
    private VehicleLoanApplicant vlemployeement;
    @OneToMany(mappedBy = "vlEmployment",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<VLEmploymentocc> vlEmploymentoccList = new ArrayList<>();

    @OneToMany(mappedBy = "vlempkey",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<VLEmploymentemp> vlEmploymentempList = new ArrayList<>();

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "RETIREMENT_AGE")
    private String retirement_age;

    @Column(name = "EMPLOYMENT_TYPE")
    private String employment_type;

    @Column(name = "TOTAL_EXPERIENCE")
    private String total_experience;

    @Column(name = "CMUSER")
    private String cmuser;

    @Column(name = "CMDATE")
    private Date cmdate;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "HOME_SOL")
    private String homeSol;

}
