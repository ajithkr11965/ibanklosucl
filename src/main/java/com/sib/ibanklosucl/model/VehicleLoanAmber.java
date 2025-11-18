package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "VEHICLE_LOAN_AMBER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanAmber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "slno", insertable = false, updatable = false)
    private VehicleLoanMaster vlamberKey;

    @OneToMany(mappedBy = "vehicleLoanAmber", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VehicleLoanAmberSub> amberSubList;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "AMBER_CODE")
    private String amberCode;

    @Column(name = "AMBER_DESC",length = 2000)
    private String amberDesc;

    @Column(name = "DEVIATION_TYPE")
    private String deviationType;

    @Column(name = "APPROVING_AUTH")
    private String approvingAuth;

    @Column(name = "DO_REMARKS",length = 2000)
    private String doRemarks;

    @Column(name = "APPR_AUTH_ACTION")
    private String apprAuthAction;

    @Column(name = "APPR_AUTH_REMARKS",length = 2000)
    private String apprAuthRemarks;

    @Column(name = "APPR_AUTH_USER")
    private String apprAuthUser;

    @Column(name = "APPR_AUTH_DATE")
    private Date apprAuthDate;

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
}
