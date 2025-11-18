package com.sib.ibanklosucl.model;

import javax.persistence.*;

import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_NACH_MANDATE")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class NACHMandate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "WI_NUM", nullable = false)
    private String winum;

    @Column(name = "SLNO", nullable = false)
    private Long slno;

    @Column(name = "PAN", nullable = false)
    private String pan;

    @Column(name = "MOBILE", nullable = false)
    private String mobile;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "INSTALMENT_START_DATE", nullable = false)
    private LocalDate instalmentStartDate;

    @Column(name = "INSTALMENT_END_DATE", nullable = false)
    private LocalDate instalmentEndDate;

    @Column(name = "COLLECTION_AMOUNT", nullable = false)
    private Double collectionAmount;

    @Column(name = "TENOR", nullable = false)
    private Integer tenor;

    @Column(name = "REFERENCE_NUMBER")
    private String referenceNumber;
    @Column(name = "REFERENCE")
    private String reference;

    @Column(name = "STATUS", nullable = false)
    private String status;
    @Column(name = "DEL_FLG")
    private String delFlg;
    @Column(name = "DEL_USER")
    private String delUser;
    @Temporal(TemporalType.DATE)
    @Column(name = "DEL_DATE")
    private Date delDate;
    @Column(name = "MNDT_ID")
    private String mndtId;
    @Column(name = "UTILITY_CODE")
    private String utilityCode;
    @Column(name = "BNK_ID")
    private String bnkId;
    @Column(name = "DEBIT_TYPE")
    private String debit_type;
    @Column(name = "FREQUENCY")
    private String frequency;
    @Column(name = "MORE_INFO")
    private String moreInfo;
    @Column(name = "SURL")
    private String surl;
    @Column(name = "TIMESTAMP")
    private LocalDateTime timestamp;
    @Column(name = "MANDATE_MODE")
    private String mandateMode; // Can be 'MANUAL' or 'DIGITAL'

}