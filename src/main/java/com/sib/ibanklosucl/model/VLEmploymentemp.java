package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "VEHICLE_LOAN_EMPLOYMENT_EMPLOYER")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VLEmploymentemp {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vlemploymentemp_seq")
    @SequenceGenerator(name = "vlemploymentemp_seq", sequenceName = "VLEMPLOYMENTEMP_SEQ", allocationSize = 1)
    private Long ino;

    @ManyToOne
    @JoinColumn(name = "EMPLOYMENT_INO")
    @JsonBackReference
    @JsonIgnore
    private VLEmployment vlempkey;
    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "EMPLOYER_NAME")
    private String employerName;

    @Column(name = "EMPLOYER_ADDRESS")
    private String employerAddress;

    @Column(name = "WORK_EXPERIENCE")
    private String workExperience;

    @Column(name = "CURRENT_EMPLOYER")
    private String currentEmployer;

    @Column(name = "CMUSER")
    private String cmuser;

    @Column(name = "CMDATE")
    private Date cmdate;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "HOME_SOL")
    private String homeSol;

}
