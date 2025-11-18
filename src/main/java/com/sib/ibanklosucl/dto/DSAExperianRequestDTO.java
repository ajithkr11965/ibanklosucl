package com.sib.ibanklosucl.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class DSAExperianRequestDTO {
    @Column(name = "userid")
    private String userid;
    @Column(name = "panno")
    private String panno;
    @Column(name = "name")
    private String name;
    @Column(name = "dob")
    private String dob;
//    @Column(name = "gender")
//    private String gender;
    @Column(name = "aadharrefnum")
    private String aadharrefnum;
    @Column(name = "mobilenum")
    private String mobilenum;
    @Column(name = "program")
    private String program;
    @Column(name = "emptype")
    private String emptype;
    @Column(name = "tenure")
    private String tenure;
    @Column(name = "loanamount")
    private String loanamount;
    @Column(name = "slno")
    private String slno;


    @Column(name = "pin")
    private String pin;
    @Column(name = "addr1")
    private String addr1;
    @Column(name = "addr2")
    private String addr2;
    @Column(name = "statecode")
    private String statecode;
    @Column(name = "state")
    private String state;
    @Column(name = "citycode")
    private String citycode;

    @Column(name = "city")
    private String city;

    @Column(name = "regioncode")
    private String regioncode;

    @Column(name = "ltv")
    private String ltv;
    @Column(name = "make")
    private String make;

    @Column(name = "bodytype")
    private String bodytype;

    @Column(name = "vehicleCategory")
    private String vehicleCategory;

    @Column(name = "gender")
    private String gender;

}
