package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehicleDTO {
    private String winum;
    private String applid;
    private String dealer_state;
    private String dealer_city;
    private String manufacturer;
    private String model;
    private String variant;
    private String exshowroom;
    private String rto;
    private String insurance;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal tot_price;
    private String cmuser;
    private String cmdate;
    private String homesol;
    private String delflag;
}
