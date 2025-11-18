package com.sib.ibanklosucl.model.integrations;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SMSEmail")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class SMSEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    private Long slno;
    private String wiNum;
    private String reqType;
    private String alertId;
    private String message;
    private String foracid;
    private String mobile;
    private String emailFrom;
    private String emailTo;
    private String emailCc;
    @Lob
    @Column(name = "EMAIL_BODY", columnDefinition = "CLOB")
    private String emailBody;
    private String emailSubject;
    private String hashCode;
    private Long appid;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sentDate;
    private String sentUser;

}
