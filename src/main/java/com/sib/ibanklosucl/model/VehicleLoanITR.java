package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_ITR_DETAILS")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanITR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @ManyToOne
    @JoinColumn(name ="program_ino")
    @JsonBackReference
    private  VehicleLoanProgram vlitr;
	@Column(name = "wi_num", length = 13)
    private String wiNum;
    @Column(name = "slno")
    private Long slno;
    @Column(name = "applicant_id")
    private Long applicantId;
    @Column(name = "client_txn_id")
    private String clientTxnId;
	@Column(name = "mobile_no")
    private String mobileNo;
	@Column(name = "url")
    private String url;
	@Column(name = "alert_id")
    private String alertId;
	@Column(name = "itr_mode")
    private String itrMode;
	@Column(name = "generate_link_id")
	private String generateLinkId;
	@Column(name = "perfios_transaction_id")
	private String perfiosTransactionId;
    @Column(name = "perfios_status")
	private String perfiosStatus;
	private Date timestamp;
    private Date updated;
	@Column(name = "cmuser", length = 10)
    private String cmUser;
    @Column(name = "cmdate")
    @Temporal(TemporalType.DATE)
    private Date cmDate;
    @Lob
    @Column(name = "fetch_response")
    private String fetchResponse;
    @Lob
    @Column(name = "message")
    private String message;
    @Column(name = "MONTHLY_GROSS_INCOME")
    private BigDecimal monthlyGrossIncome;
    @Column(name = "MONTHLY_TOTAL_INCOME")
    private BigDecimal monthlyTotalIncome;
    @Column(name = "DEL_FLG")
    private String delFlg;
    @Column(name = "DEL_USER")
    private String delUser;
    @Column(name = "DEL_DATE")
    private Date delDate;
}
