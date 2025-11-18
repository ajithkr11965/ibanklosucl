package com.sib.ibanklosucl.model.doc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_REPAYMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanRepayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "BANK_NAME",length = 10)
    private String bankName;
    @Column(name = "ACCOUNT_NUMBER",length = 50)
    private String accountNumber;

    @Column(name = "IFSC_CODE",length = 11)
    private String ifscCode;
    @Column(name = "BORROWER_NAME",length = 50)
    private String borrowerName;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "LAST_MOD_USER")
    private String lastModUser;
    @Column(name = "LAST_MOD_DATE")
    private Date lastModDate;

    @Column(name = "HOME_SOL")
    private String homeSol;

}
