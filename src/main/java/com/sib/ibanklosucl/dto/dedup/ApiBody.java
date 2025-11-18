package com.sib.ibanklosucl.dto.dedup;

import lombok.Data;

import java.util.List;

@Data
public class ApiBody {
    private String merchant_code;
    private String merchant_name;
    private String servicename;
    private String message;
    private List<CustomerData> customer;

}
