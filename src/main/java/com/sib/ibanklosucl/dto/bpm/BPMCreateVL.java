package com.sib.ibanklosucl.dto.bpm;

import lombok.Data;

@Data
public class BPMCreateVL {

    private String folderName;
    private String processID="NEW_VL";
    private String userID="docutility";
    private String systemIP;
    private String child;


}
