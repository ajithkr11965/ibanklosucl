package com.sib.ibanklosucl.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TabResponse {
    private String status;
    private String msg;
    private String appid;
    public TabResponse(String status,String msg){
        this.status=status;
        this.msg=msg;
    }

}
