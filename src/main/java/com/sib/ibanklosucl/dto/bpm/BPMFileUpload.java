package com.sib.ibanklosucl.dto.bpm;

import com.sib.ibanklosucl.dto.DOC_ARRAY;
import lombok.Data;

import java.util.List;

@Data
public class BPMFileUpload {
    private String WI_NAME;
    private String CHILD;
    private String CHILD_FOLDER;
    private String systemIP;
    private String ORIGIN="INFOBANK";

    List<DOC_ARRAY> DOC_ARRAY;
}
