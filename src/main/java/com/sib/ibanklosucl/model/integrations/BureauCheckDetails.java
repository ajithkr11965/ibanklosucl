package com.sib.ibanklosucl.model.integrations;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "BUREAU_CHECK_DETAILS")
@ToString
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class BureauCheckDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bureau_check_seq")
    @SequenceGenerator(name = "bureau_check_seq", sequenceName = "BUREAUCHECKSEQ", allocationSize = 1)
    private Long ino;

    private String wiNum;
    private Long slno;
    private Long applicantId;

    private String firstName;
    private String middleName;
    private String lastName;
    private String maskedAadhaar;
    private String name;
    private String status;
    private Boolean aadhaarLinked;
    private String gender;
    private Boolean isPanValid;
    private String panType;
    private String pan;

    private String delFlg;
    private String cmuser;

    @Temporal(TemporalType.TIMESTAMP)
    private Date cmdate;
}
