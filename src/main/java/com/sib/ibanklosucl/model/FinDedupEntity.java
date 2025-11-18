package com.sib.ibanklosucl.model;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Audited
@Table(name = "FINACLE_DEDUP")
public class FinDedupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @Column(name = "wi_num", length = 13)
    private String wiNum;

    @Column(name = "slno")
    private Long slno;

    @Column(name = "applicant_id")
    private Long applicantId;
    @Column(name = "created_channel_id")
    private String createdChannelId;
    @Column(name = "suspend_status")
    private String suspendStatus;
    @Column(name = "customerid")
    private String customerid;
    @Column(name = "emailid")
    private String emailid;
    @Column(name = "mobilephone")
    private String mobilephone;
    @Column(name = "voterid")
    private String voterid;
    @Column(name = "aadhar_ref_no")
    private String aadharRefNo;
    @Column(name = "tds_customerid")
    private String tdsCustomerid;
    @Column(name = "pan")
    private String pan;
    @Column(name = "dob")
    private String dob;
    @Column(name = "name")
    private String name;

    @Column(name = "dedup_flag")
    private String dedupflag;
    @Column(name = "dedup_msg")
    private String dedupmsg;
    @Column(name = "active_flag")
    private String activeFlag;
    @Column(name = "del_flag")
    private String delFlag;
    @Column(name = "cmuser", length = 10)
    private String cmuser;

    @Column(name = "cmdate")
    private Date cmdate;
    @Column(name = "home_sol", length = 5)
    private String homeSol;
}
