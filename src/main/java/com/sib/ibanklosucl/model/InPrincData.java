package com.sib.ibanklosucl.model;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;


import java.util.List;
import java.util.Map;

@Data
@Entity
@Audited
@Table(name = "VEHICLE_LOAN_INPRINC")
public class InPrincData {
    @Id
    @Column(name = "winum")
    String wiNum;
    @Column(name = "slno")
    Long slno;
    @Column(name = "dateofform")
    String DateOfForm;
    @Column(name = "applicationnumber")
    String ApplicationNumber;
    @Transient
    List<Map<String, String>> Names;
    @Column(name = "loanamount")
    String LoanAmount;
    @Column(name = "insurance")
    String Insurance;
    @Column(name = "tenor")
    String Tenor;
    @Column(name = "emi")
    String EMI;
    @Column(name = "actualamount")
    String ActualAmount;
    @Column(name = "concessionallowed")
    String ConcessionAllowed;
    @Column(name = "chargebleamount")
    String ChargebleAmount;

    @Column(name = "solid")
    String solid;

}




