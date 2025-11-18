package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PincodeDTO {
    private String id;
    private String pincode;
    private String district;
    private String stateCode;
    private String cityCode;
}
