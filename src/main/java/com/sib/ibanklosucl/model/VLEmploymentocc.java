package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "VEHICLE_LOAN_EMPLOYMENT_OCCUPATION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VLEmploymentocc {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vlemploymentocc_seq")
    @SequenceGenerator(name = "vlemploymentocc_seq", sequenceName = "VLEMPLOYMENTOCC_SEQ", allocationSize = 1)
    private Long ino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLOYMENT_INO")
    @JsonBackReference
    @JsonIgnore
    private VLEmployment vlEmployment;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "OCCUPATION_TYPE")
    private String occupationType;

    @Column(name = "OCCUPATION_CODE")
    private String occupationCode;

    @Column(name = "PROFESSION")
    private String profession;

    @Column(name = "EMPLOYER_NAME")
    private String employerName;

    @Column(name = "BUSINESS_EXPERIENCE")
    private String businessExperience;


    @Column(name = "CMUSER")
    private String cmuser;

    @Column(name = "CMDATE")
    private Date cmdate;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "HOME_SOL")
    private String homeSol;

}
