package com.sib.ibanklosucl.dto;

import lombok.Data;

@Data
public class CustDedup {
    private String merchant_code;
    private String merchant_name;
    private String name;
    private String dateofBirth;
    private String email;
    private String landlineNumber;
    private String mobileNumber;
    private String voterId;

    private String aadhar;
    private String drivingLicense;
    private String pan;
    private String passport;
}
