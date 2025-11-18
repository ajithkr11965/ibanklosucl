package com.sib.ibanklosucl.dto.doc;

import lombok.Data;

@Data
public class SMSEmailDTO {

    private Long slno;
    private String wiNum;
    private String reqType;
    private String alertId;
    private String message;
    private String foracid;
    private String mobile;
    private String hashCode;
    private String emailFrom;
    private String emailTo;
    private String emailCc;
    private String emailBody;
    private String custName;
    private String sentUser;
    private String emailSubject;
    private Long appid;
}
