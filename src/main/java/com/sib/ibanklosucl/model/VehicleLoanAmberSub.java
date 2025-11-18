package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_AMBER_SUB")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanAmberSub {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VLAMBER_SUB_SEQ")
    @SequenceGenerator(name = "VLAMBER_SUB_SEQ", sequenceName = "IBANKLOSUCL.VLAMBER_SUB_SEQ", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "slno", insertable = false, updatable = false)
    private VehicleLoanMaster vlamberSubKey;

    @ManyToOne
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private VehicleLoanApplicant vehicleLoanApplicant;

    @ManyToOne
    @JoinColumn(name = "amber_id")
    private VehicleLoanAmber vehicleLoanAmber;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "AMBER_CODE")
    private String amberCode;

    @Column(name = "AMBER_DESC")
    private String amberDesc;

    @Column(name = "APPLICANT_TYPE")
    private String applicantType;

    @Column(name = "APPLICANT_NAME")
    private String applicantName;

    @Column(name = "APPLICANT_ID")
    private Long applicantId;

    @Column(name = "COLOUR")
    private String colour;

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "LAST_MOD_USER")
    private String lastModUser;

    @Column(name = "LAST_MOD_DATE")
    private Date lastModDate;

    @Column(name = "HOME_SOL")
    private String homeSol;

    @Column(name = "DEL_USER")
    private String delUser;

    @Column(name = "DEL_DATE")
    private Date delDate;

    @Column(name = "DEL_HOME_SOL")
    private String delHomeSol;

    @Column(name = "ACTIVE_FLG")
    private String activeFlg;
      @Column(name = "MASTER_VALUE")
    private String masterValue;
     @Column(name = "CURRENT_VALUE")
    private String currentValue;
}