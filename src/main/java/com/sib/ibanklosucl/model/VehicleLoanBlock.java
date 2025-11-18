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
@Table(name = "VEHICLE_LOAN_BLOCK")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INO", nullable = false)
    private Long ino;
    @Column(name = "SLNO", nullable = false)
    private Long slno;

    @Column(name = "WI_NUM")
    private String wiNum;
    @Column(name = "QUEUE")
    private String queue;
    @Column(name = "APPLICANT_ID")
    private String applicantId;



    @Column(name = "BLOCK_TYPE", length = 50)
    private String blockType;

    @Column(name = "PARTICULARS", length = 2000)
    private String particulars;

    @Column(name = "ACTUAL_VALUE", length = 500)
    private String actualValue;

    @Column(name = "EXPECTED_VALUE", length = 500)
    private String expectedValue;

    @Column(name = "active", length = 1)
    private String active;



    @Column(name = "CMUSER", length = 20)
    private String cmUser;

    @Column(name = "CMDATE")
    private Date cmDate;
    @Column(name = "DEL_FLAG", length = 1)
    private String delFlag;
}
