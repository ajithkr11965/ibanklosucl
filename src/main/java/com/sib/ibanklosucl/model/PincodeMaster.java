package com.sib.ibanklosucl.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PIN_CODE_MASTER")
@Data
public class PincodeMaster {

    @Id
    @Column(name = "Pincode", length = 6, nullable = false)
    private String pincode;

    @Column(name = "DISTRICT", length = 50)
    private String district;

    @Column(name = "STATE_NAME", length = 50)
    private String stateName;

    @Column(name = "STATE_CODE", length = 2)
    private String stateCode;

    @Column(name = "Finacle_City", length = 50)
    private String finacleCity;

    @Column(name = "Finacle_City_Code", length = 5)
    private String finacleCityCode;

    @Column(name = "Finacle_State", length = 50)
    private String finacleState;

    @Column(name = "Finacle_State_Code", length = 2)
    private String finacleStateCode;

}
