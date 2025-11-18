package com.sib.ibanklosucl.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "STATE_CODE_EXPERIAN")
@Data
public class StateCodeExperian {
    @Id
    @Column(name = "INO", nullable = false)
    private Long ino;

    @Column(name = "Finacle_State", length = 50)
    private String finacleState;

    @Column(name = "Finacle_State_Code", length = 2)
    private String finacleStateCode;
    @Column(name = "Exp_State_Name", length = 50)
    private String expStateName;

    @Column(name = "Exp_State_Code", length = 2)
    private String expStateCode;

    @Column(name = "REGION_CODE", length = 2)
    private String regionCode;

}
