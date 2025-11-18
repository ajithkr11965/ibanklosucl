package com.sib.ibanklosucl.dto.doc;

import lombok.Data;

@Data
public class LegalityFetchDTO {
    private String applicantName;
    private String applicantId;
    private String applicantType;
    private String residentFlag;
    private String perState;
    private String perStateDesc;
    private String mobileCountryCode;
    private String mobileNo;
    private String emailId;
    private String applicantDob;
    private String panNo;
    private String addr1;
    private String pin;
    private String comAddr1;
    private String comPin;
    private String perCountry;
    private String comCountry;

}
