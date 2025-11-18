package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationDTO {
    private String stateCode;
    private String stateName;
    private String finacleCityCode;
    private String finacleCity;
    private String district;
}
