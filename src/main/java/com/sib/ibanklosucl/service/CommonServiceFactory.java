package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.service.impl.BmDocUploadServiceImpl;
import com.sib.ibanklosucl.service.impl.CustDocSaveImpl;
import com.sib.ibanklosucl.service.impl.ExperianCreditServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceFactory {

    private final BmDocUploadServiceImpl bmDocUploadService;
    private final ExperianCreditServiceImpl exPerianService;
    private final CustDocSaveImpl custdocSave;


    public CommonServiceFactory(BmDocUploadServiceImpl bmDocUploadService, ExperianCreditServiceImpl exPerianService, CustDocSaveImpl custdocSave) {
        this.bmDocUploadService = bmDocUploadService;
        this.exPerianService = exPerianService;
        this.custdocSave = custdocSave;
    }

    public  CommonService getService(String type) {
        switch (type) {
            case "BMUPLOAD":
                return bmDocUploadService;
            case "EXPERIAN":
                return exPerianService;
            case "CUSTDOC":
                return custdocSave;
            default:
                throw new IllegalArgumentException("Unsupported service type: ");
        }
    }
}
