package com.sib.ibanklosucl.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VKYCResponse {
    private String status;
    private String msg;
    private String uniqueid;

}
