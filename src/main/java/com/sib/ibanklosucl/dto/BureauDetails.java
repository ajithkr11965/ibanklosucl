package com.sib.ibanklosucl.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BureauDetails {
    private String firstName;
    private String middleName;
    private String lastName;
    private String maskedAadhaar;
    private String name;
    private String status;
    private String aadhaarLinked;  // Changed to String for Y/N
    private String gender;
    private String isPanValid;     // Changed to String for Y/N
    private String panType;
    private String pan;
}
