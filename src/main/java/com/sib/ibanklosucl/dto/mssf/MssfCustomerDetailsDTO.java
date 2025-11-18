package com.sib.ibanklosucl.dto.mssf;

import lombok.Data;

@Data
public class MssfCustomerDetailsDTO {
    private String refNo;
    private String pdSalutation;
    private String pdFirstName;
    private String pdMiddleName;
    private String pdLastName;
    private String pdGender;
    private String pdDob;
    private String pdMaritalStatus;
    private String pdMotherName;
    private String pdFatherName;
    private Integer pdNumDependent;
    private String pdPan;
    private Long pdMobile;
    private String pdEmail;
    private String dlrCode;
	 private Double laLoanAmt;
    private Double laRoi;
    private Integer laTenure;
    private Double laFeeCharge;
    private Double laDownpayment;
    private Double laEstEmi;
}

