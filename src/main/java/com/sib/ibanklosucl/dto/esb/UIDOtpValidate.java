package com.sib.ibanklosucl.dto.esb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UIDOtpValidate {
    private String channel_id;
    private String uid;
    private String clientId;
    private String securityToken;
    private String otp;
    private String uIDAITxn;
    private String requestPrintFormatPdfFromUIDA;
}

