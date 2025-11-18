package com.sib.ibanklosucl.dto.bpm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BPMCreateVLResponse {

    private String parentUrl;
    private String childUrl;
    private String addUrl;
    private String childIndex;
    private String parentIndex;
    private String status;

}
