package com.sib.ibanklosucl.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String mobile;
    private String officeCode;
    private String mail;
    private String officeName;
    private String ppcno;
    private String designation;
    private String description;
    private String ipPhone;
    private String userName;
    private List<String> roles;



}
