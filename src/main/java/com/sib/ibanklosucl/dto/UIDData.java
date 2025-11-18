package com.sib.ibanklosucl.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UIDData {
    private String uid;
    private String yob;
    private String name;
    private String otp;

}
