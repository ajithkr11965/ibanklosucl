package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.service.impl.*;
import org.springframework.stereotype.Service;

@Service
public class VLSaveServiceFactory {
    private final GeneralDtSaveImp generalDtSaveImp;
    private final KYCDtSaveImp kycDtSaveImp;
    private final BasicDtSaveImp basicDtSaveImp;
    private final EmpDtSaveImp empDtSaveImp;
    private final CreditDtSaveImp creditDtSaveImp;
    private final ProgramSaveImp programSaveImp;
    private final LoanDtSaveImpl loanDtSave;
    private final BrMakerSaveImpl brMakerSave;
    private final RbcpcCheckerSaveImpl rbcpcCheckerSave;
    private final CRTAmberSaveImpl crtamberSaveImpl;
    private final RepaymentSaveImpl repaymentSave;
    private final WaiverSaveImpl waiverSave;
    private final MaterialListSaveImpl materialListSave;
    private final BOGSaveImpl bogSave;

    private final DocumentaionSaveImpl documentaionSave;

    public VLSaveServiceFactory(GeneralDtSaveImp generalDtSaveImp, KYCDtSaveImp kycDtSaveImp, BasicDtSaveImp basicDtSaveImp, EmpDtSaveImp empDtSaveImp, ProgramSaveImp programSaveImp, CreditDtSaveImp creditDtSaveImp, LoanDtSaveImpl loanDtSave, BrMakerSaveImpl brMakerSave, RbcpcCheckerSaveImpl rbcpcCheckerSave, CRTAmberSaveImpl crtamberSaveImpl, RepaymentSaveImpl repaymentSave, WaiverSaveImpl waiverSave, BOGSaveImpl bogSave,DocumentaionSaveImpl documentaionSave,MaterialListSaveImpl materialListSave) {
        this.generalDtSaveImp = generalDtSaveImp;
        this.kycDtSaveImp = kycDtSaveImp;
        this.basicDtSaveImp = basicDtSaveImp;
        this.empDtSaveImp = empDtSaveImp;
        this.creditDtSaveImp = creditDtSaveImp;
        this.programSaveImp = programSaveImp;
        this.loanDtSave = loanDtSave;
        this.brMakerSave = brMakerSave;
        this.rbcpcCheckerSave = rbcpcCheckerSave;
        this.crtamberSaveImpl = crtamberSaveImpl;
        this.repaymentSave = repaymentSave;
        this.waiverSave = waiverSave;
        this.bogSave = bogSave;
        this.documentaionSave = documentaionSave;
        this.materialListSave = materialListSave;
    }

    public  VlSaveService getService(String type) {
        switch (type) {
            case "GEN":
                return generalDtSaveImp;
            case "KYC":
                return kycDtSaveImp;
            case "BASIC":
                return basicDtSaveImp;
            case "EMP":
                return empDtSaveImp;
            case "PROGRAM":
                return programSaveImp;
            case "CREDIT":
                return creditDtSaveImp;
            case "RBCM":
                return  materialListSave;
            default:
                throw new IllegalArgumentException("Unsupported service type: ");
        }
    }
    public  VlCommonTabService getTabService(String type) {
        switch (type) {
            case "LOAN":
                return loanDtSave;
            case "BRMAKER":
                return brMakerSave;
            case "RBCPCCHECKER":
                return rbcpcCheckerSave;
            case "CRTAMBER":
                return crtamberSaveImpl;
            case "REPAYMENT":
                return repaymentSave;
            case "WAIVER":
                return waiverSave;
            case "DOC":
                return documentaionSave;
            case "BOG":
                return bogSave;
            default:
                throw new IllegalArgumentException("Unsupported service type: ");
        }
    }




}
