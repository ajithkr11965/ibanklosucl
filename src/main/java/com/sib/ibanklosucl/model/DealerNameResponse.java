package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DealerNameResponse {
    //private String dealerCode;
    private List<Map<String, Object>> dstCodes;
    private List<Map<String, Object>> dsaCodes;
    private List<Map<String, Object>> dealerSubCodes;


}
