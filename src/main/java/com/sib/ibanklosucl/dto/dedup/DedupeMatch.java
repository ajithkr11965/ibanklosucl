package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

@Data
public class DedupeMatch {
    private String customerId;
    private String phone;
    private String email;
    private String dob;
    private String pan;
    private String name;
    private String voterId;
    private String aadharRefNo;
    private String passport;
}
