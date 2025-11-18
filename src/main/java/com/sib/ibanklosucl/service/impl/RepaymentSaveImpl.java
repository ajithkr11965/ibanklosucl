package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.VehicleLoanDetails;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.doc.VehicleLoanRepayment;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.service.VlCommonTabService;
import com.sib.ibanklosucl.service.doc.RepaymentService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;

@Service
public class RepaymentSaveImpl implements VlCommonTabService {

    @Autowired
    private ValidationRepository validationRepository;
    @Autowired
    private VehicleLoanMasterService vlmassr;
    @Autowired
    private RepaymentService repaymentService;
    @Autowired
    private UserSessionData usd;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveRepayment(RepaymentDTO repaymentDTO) throws Exception {
        VehicleLoanMaster master = vlmassr.findById(repaymentDTO.getSlno());

        if (master.getDocMode() != null) {
            throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for Documentation");
        }
        //-	If bank name selected is SIB, during account number entry, system should ensure that entered account number belongs to any one of the CIF ID of applicant/co-applicant, only if any one of them is an SIB customer
        if("SIBL".equals(repaymentDTO.getBankName())){
            if(!validationRepository.checkSibAccount(repaymentDTO.getAccountNumber(),repaymentDTO.getSlno())){
                throw new ValidationException(ValidationError.COM001,"The entered Account Number must be an Operative Account and belongs to the CIF ID of Applicant/Co-Applicant") ;
            }
        }

        VehicleLoanRepayment vlr = repaymentService.getRepaymentDetails(repaymentDTO.getSlno());

        if (vlr == null) {
            vlr = new VehicleLoanRepayment();
            vlr.setSlno(repaymentDTO.getSlno());
            vlr.setWiNum(repaymentDTO.getWiNum());
        }
        vlr.setIfscCode(repaymentDTO.getIfscCode().trim());
        vlr.setBankName(repaymentDTO.getBankName());
        vlr.setBorrowerName(repaymentDTO.getBorrowerName());
        vlr.setLastModUser(usd.getPPCNo());
        vlr.setHomeSol(usd.getSolid());
        vlr.setAccountNumber(repaymentDTO.getAccountNumber().trim());
        vlr.setLastModDate(new Date());
        vlr.setDelFlg("N");
        repaymentService.save(vlr);
        master.setRepaymentStatus(true);
        vlmassr.saveLoan(master);
        return new ResponseDTO("S", "Repayment Details Saved!");
    }

    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveRepaymentForBD(RepaymentDTO repaymentDTO) throws Exception {
        VehicleLoanMaster master = vlmassr.findById(repaymentDTO.getSlno());

        // Check if loan account is created
        if (master.getAccNumber() != null) {
            throw new ValidationException(ValidationError.COM001, "After loan account creation, repayment details cannot be modified");
        }
        // Get existing repayment details
        VehicleLoanRepayment existingRepayment = repaymentService.getRepaymentDetails(repaymentDTO.getSlno());

        if ("SIBL".equals(repaymentDTO.getBankName())) {
        // Check documentation reset requirement
        if (master.getDocMode() != null) {
            throw new ValidationException(ValidationError.COM001,
                "When changing to SIB bank, documentation must be reset first as the new account details need to be captured in documentation. " +
                "Please use the 'Reset Documentation' option first and then update repayment details.");
        }
        // Validate SIB account
        if (!validationRepository.checkSibAccount(repaymentDTO.getAccountNumber(), repaymentDTO.getSlno())) {
            throw new ValidationException(ValidationError.COM001,
                "The entered Account Number should belong to the CIF ID of Applicant/Co-Applicant, " +
                "Also Account should be of Scheme Type SBA/CAA and not Debit Freezed");
        }
    } else {
            // Check for existing SIBL bank
            if (existingRepayment != null && "SIBL".equals(existingRepayment.getBankName())) {
                throw new ValidationException(ValidationError.COM001,
                        "Since existing bank is SIB, documentation must be reset before changing repayment details. " +
                                "Please use the 'Reset Documentation' option first and then update repayment details.");
            }
        }
        // Special handling for SIBL bank change
        if (existingRepayment != null && "SIBL".equals(existingRepayment.getBankName())) {
            throw new ValidationException(ValidationError.COM001,
                    "Since existing bank is SIB, documentation must be reset before changing repayment details. " +
                            "Please use the 'Reset Documentation' option first and then update repayment details.");
        }

        // Create or update repayment details
        VehicleLoanRepayment vlr = existingRepayment;
        if (vlr == null) {
            vlr = new VehicleLoanRepayment();
            vlr.setSlno(repaymentDTO.getSlno());
            vlr.setWiNum(repaymentDTO.getWiNum());
        }

        // Update repayment details
        vlr.setIfscCode(repaymentDTO.getIfscCode());
        vlr.setBankName(repaymentDTO.getBankName());
        vlr.setBorrowerName(repaymentDTO.getBorrowerName());
        vlr.setLastModUser(usd.getPPCNo());
        vlr.setHomeSol(usd.getSolid());
        vlr.setAccountNumber(repaymentDTO.getAccountNumber().trim());
        vlr.setLastModDate(new Date());
        vlr.setDelFlg("N");

        // Save repayment details
        repaymentService.save(vlr);

        // Update master status
        master.setRepaymentStatus(true);
        vlmassr.saveLoan(master);

        return new ResponseDTO("S", "Repayment Details Saved!");
    }

    @Override
    public ResponseDTO saveWaiver(WaiverDto waiverDto, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO updateWaiver(WaiverDto waiverDto, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveDoc(DocumentRequest documentRequest) throws Exception {
        return null;
    }


    @Override
    public ResponseEntity<?> saveLoan(VehicleLoanDetails vehicleLoanDetails) {
        return null;
    }

    @Override
    public ResponseDTO saveMaker(String slno, String winum, String vlowner, String vlownerstatus, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveChecker(RBCPCMakerSave rbs, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveRBCChecker(RBCPCCheckerSave rbs, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveRBCMaker(RBCPCMakerSave rbs, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveCRTAmber(String slno, String winum, String remarks, String decision, HttpServletRequest request) throws Exception {
        return null;
    }


    @Override
    public ResponseDTO saveWIRecall(String wiNum, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveBOG(Long slno, String winum, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO savewism(Long slno, String winum, String remarks, String action, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO acctlabelsave(AcctLabelDTO acctLabelDTO, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO performAccOpening(Long slno, String winum, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO disbursement(Long slno, String winum, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO performDisbStatusEnquiry(Long slno, String winum, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO performNeft(Long slno, String winum, String beneficiaryType, String dneftamt, String mneftamt, String accnum, String ifsc,
                                   String accname, String manufMobile, String disbType, String add1, String add2, String add3, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO bmDel(Long slno, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO performInsFiTrn(Long slno, String winum) throws Exception {
        return null;
    }
}
