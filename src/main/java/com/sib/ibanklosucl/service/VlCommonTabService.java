package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.model.VehicleLoanDetails;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public interface VlCommonTabService {
    ResponseEntity<?> saveLoan(VehicleLoanDetails vehicleLoanDetails);
    ResponseDTO saveMaker(String slno, String winum, String vlowner, String vlownerstatus, String remarks, HttpServletRequest request) throws Exception;

    ResponseDTO saveChecker(RBCPCMakerSave rbs, HttpServletRequest request) throws Exception ;
     ResponseDTO saveRBCChecker(RBCPCCheckerSave rbs, HttpServletRequest request) throws Exception ;
     ResponseDTO saveRBCMaker(RBCPCMakerSave rbs, HttpServletRequest request) throws Exception ;

    ResponseDTO saveCRTAmber(String slno, String winum, String remarks, String decision, HttpServletRequest request) throws Exception;



    ResponseDTO saveWIRecall(String wiNum, String remarks,  HttpServletRequest request) throws Exception;
    ResponseDTO saveRepayment(RepaymentDTO repaymentDTO) throws Exception;
    ResponseDTO saveWaiver(WaiverDto waiverDto, HttpServletRequest request) throws Exception;
    ResponseDTO updateWaiver(WaiverDto waiverDto, HttpServletRequest request) throws Exception;
    ResponseDTO saveDoc(DocumentRequest documentRequest) throws Exception;

    ResponseDTO saveBOG(Long slno, String winum, String remarks, HttpServletRequest request) throws Exception;

//    ResponseDTO saveCustDoc(String winum, String foldername, String filename, String remarks, HttpServletRequest request) throws Exception;

    ResponseDTO savewism(Long slno, String winum, String remarks, String action, HttpServletRequest request) throws Exception;
    ResponseDTO acctlabelsave(AcctLabelDTO acctLabelDTO, HttpServletRequest request) throws Exception;
    ResponseDTO performAccOpening(Long slno, String winum, HttpServletRequest request) throws Exception;
    ResponseDTO disbursement(Long slno, String winum, HttpServletRequest request) throws Exception;
    ResponseDTO performDisbStatusEnquiry(Long slno, String winum, HttpServletRequest request) throws Exception;

    ResponseDTO performNeft(Long slno, String winum, String beneficiaryType, String dneftamt,String mneftamt,String accnum,
                            String ifsc, String accname, String manufmobile,String disbType, String add1, String add2, String add3, HttpServletRequest request
                            ) throws Exception ;
    ResponseDTO bmDel(Long slno, HttpServletRequest request) throws Exception;

    ResponseDTO performInsFiTrn(Long slno,String winum) throws Exception;
}
