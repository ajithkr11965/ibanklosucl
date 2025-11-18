package com.sib.ibanklosucl.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExperianPincodeMasterDTO {

    private String pincode;

    private String district;

    private String stateName;

    private String stateCode;

    private String finacleCity;

    private String finacleCityCode;

    private String finacleState;

    private String finacleStateCode;

    private String experianState;

    private String experianStateCode;

    private String regionCode;

    public ExperianPincodeMasterDTO(String experianStateCode,String regionCode){
        this.experianStateCode=experianStateCode;
        this.regionCode=regionCode;
    }

}
