package com.sib.ibanklosucl.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "VEHICLE_LOAN_NEFT_INPUTS")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanNeftInputs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INO", nullable = false)
    private Long ino;
    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;
    @Column(name = "SLNO", nullable = false)
    private Long slno;

    @Column(name = "disb_type", length = 1)
    private String disbType;//single/multiple
    @Column(name = "dealer_name", length = 300)
    String dealerName;
    @Column(name = "dealer_name_rmk", length = 300)
    String dealerNameRmk;
    @Column(name = "dst_code", length = 300)
    String dstCode;
    @Column(name = "dsa_code", length = 300)
    String dsaCode;
    @Column(name = "dealer_code", length = 300)
    String dealerCode;
    @Column(name = "dealer_sub_code", length = 300)
    String dealerSubCode;
    @Column(name = "dealer_acc", length = 300)
    String dealerAcc;
    @Column(name = "dealer_ifsc", length = 300)
    String dealerIfsc;
    @Column(name = "dealer_amount")
    BigDecimal dealerAmount;



    @Column(name = "man_make", length = 300)
    String manMake;
    @Column(name = "man_mobile", length = 10)
    String manMobile;
    @Column(name = "man_acc", length = 30)
    String manAcc;
    @Column(name = "man_confirm_acc", length = 30)
    String manConfirmAcc;
    @Column(name = "man_ifsc", length = 20)
    String manIfsc;
    @Column(name = "man_acc_name", length = 100)
    String manAccName;
    @Column(name = "add1", length = 100)
    private String add1;
    @Column(name = "add2", length = 100)
    private String add2;
    @Column(name = "add3", length = 100)
    private String add3;
    @Column(name = "man_amount")
    BigDecimal manAmount;

    @Column(name = "cmuser", length = 10)
    private String cmUser;
    @Column(name = "cmdate")
    private Date cmDate;
    @Column(name = "ipaddress", length = 10)
    private String ipaddress;
    @Column(name = "delflag", length = 10)
    private String delflag;
}
