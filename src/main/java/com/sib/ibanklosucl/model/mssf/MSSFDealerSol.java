package com.sib.ibanklosucl.model.mssf;

import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "MSSF_DEALER_SOL")
@Data
@Audited
@EntityListeners(AuditingEntityListener.class)
public class MSSFDealerSol {
    @Id
    @Column(name = "DLR_CODE", length = 50)
    private String dlrCode;

    @Column(name = "SOL_ID", length = 10)
    private String solId;
    @Column(name = "DEALER_NAME_CODE", length = 500)
    private String dealerNameCode;
    @Column(name = "DEALER_NAME_MSSF", length = 500)
    private String dealerNameMssf;
    @Column(name = "DEALER_SUB_CODE")
    private String dealerSubCode;
    @Column(name = "DEALER_MAIN_CODE")
    private String dealerMainCode;
    @Column(name = "DEALER_LOCATION")
    private String dealerLocation;

    @Column(name = "DEL_FLG", length = 1)
    private String delFlg;
}
