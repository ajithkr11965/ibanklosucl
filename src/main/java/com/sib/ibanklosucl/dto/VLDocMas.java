package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VLDocMas {

    private String labelcode;
    private String labelname;
    private String filename;
    private String mandatory;
    private String generic;
    private String applicant;
    private String coapplicant;
    private String gurantor;
}