package com.sib.ibanklosucl.service.impl;


import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.EligibilityDetails;
import com.sib.ibanklosucl.model.ErrorResponse;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanDetails;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.service.VehicleLoanDetailsService;
import com.sib.ibanklosucl.service.VlCommonTabService;
import com.sib.ibanklosucl.service.iBankService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LoanDtSaveImpl  implements VlCommonTabService {
    @Autowired
    private VehicleLoanApplicantService applicantService;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private iBankService iBankService;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;
    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private VehicleLoanDetailsService loanDetailsService;
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<?> saveLoan(VehicleLoanDetails loanDetails_) {
        VehicleLoanDetails loanDetails= loanDetailsService.findBySlnoAndDelFlg(loanDetails_.getSlno());
        if(loanDetails==null){
            loanDetails=new VehicleLoanDetails();
        }
        loanDetails.setLoanAmt(loanDetails_.getLoanAmt());
        loanDetails.setVehicleAmt(loanDetails_.getVehicleAmt());
        loanDetails.setFoirType(loanDetails_.getFoirType());
        loanDetails.setRoiType(loanDetails_.getRoiType());
        loanDetails.setTenor(loanDetails_.getTenor());
        loanDetails.setSlno(loanDetails_.getSlno());
        loanDetails.setWiNum(loanDetails_.getWiNum());
        loanDetails.setApplicantId(loanDetails_.getApplicantId());
        loanDetails.setInsVal(loanDetails_.getInsVal());
        if("Y".equalsIgnoreCase(loanDetails_.getInsVal())) {
            loanDetails.setInsAmt(loanDetails_.getInsAmt());
            loanDetails.setInsVal(loanDetails_.getInsVal());
            if(loanDetails_.getInsAmt().compareTo(BigDecimal.ZERO)<=0){
                throw new ValidationException(ValidationError.COM001,"Please Enter Valid Insurance Amount");
            }
        }
        else{
            loanDetails.setInsAmt(null);
            loanDetails.setInsType("");
        }
        loanDetails.setInsType(loanDetails_.getInsType());
        loanDetails.setReqIpAddr(usd.getRemoteIP());
        loanDetails.setCmdate(new Date());
        loanDetails.setDelFlg("N");
        loanDetails.setCmuser(usd.getPPCNo());
        loanDetails.setHomeSol(usd.getSolid());
        String tenor = iBankService.getMisPRM("FOIRTENOR").getPVALUE();
        Long minFixedTenor = Long.valueOf(iBankService.getMisPRM("MINFIXEDTENOR").getPVALUE());
        String loanamt = iBankService.getMisPRM("FOIRLOANAMT").getPVALUE();
        Long foirbureau = Long.valueOf(iBankService.getMisPRM("FOIRBUREAU").getPVALUE());
        List<VehicleEmpProgram> empPrograms=fetchRepository.getEmpProgram(loanDetails.getSlno());
        boolean isNRIExistAndProgramNotNone =empPrograms.stream()
                .anyMatch(applicant ->  "N".equals(applicant.getResidentFlg()) && !"NONE".equals(applicant.getLoanProgram()));
        try {
            //Requested Loan amount should be less than or equal to vehicle amount
            if (loanDetails.getVehicleAmt().compareTo(loanDetails.getLoanAmt())<0) {
                throw new ValidationException(ValidationError.ERRO05);
            }
            if (loanDetails.getInsVal().equalsIgnoreCase("Y") &&  (loanDetails.getVehicleAmt().compareTo(loanDetails.getInsAmt())<0 ||loanDetails.getLoanAmt().compareTo(loanDetails.getInsAmt())<0 ) ) {
                throw new ValidationException(ValidationError.ERRO15);
            }
            //check whether all tabs completed
            validationRepository.isAllTabCompleted(loanDetails.getSlno());
            //Validating program
            String program= validationRepository.validateLoanPrograms(empPrograms);
            //-	Minimum value is 1 lakh and maximum values is 10 cr,and it will be made menu driven
            validationRepository.checkLoanAmount(empPrograms.stream().filter(e-> ("A".equals(e.getApplicantType()) || "C".equals(e.getApplicantType()) )).toList(),loanDetails.getLoanAmt(),program,"LOAN");
            //-	Minimum is 12 months and max is 84 months. This will be menu driven.
            validationRepository.checkLoanTenor(empPrograms,loanDetails.getTenor());
            //NON-FOIR
            if("N".equals(loanDetails.getFoirType()) && program.equals("INCOME")){
                //If an NRI/PIO/OCI is applicant/co-applicant but income considered = No else,
                    if (isNRIExistAndProgramNotNone){
                        throw new ValidationException(ValidationError.ERRO12);
                    }
                        //o	Tenor is 5 years or less,
                    if(loanDetails.getTenor()> Integer.parseInt(tenor) || loanDetails.getLoanAmt().compareTo(new BigDecimal(loanamt))>0){
                        throw new ValidationException(ValidationError.ERRO06,tenor,loanamt);
                    }
                    //	Bureau score of all applicants/co-applicants whose income is considered is 750 or above
                    if(empPrograms.stream().anyMatch(a-> a.getLoanProgram().equals("INCOME") && foirbureau>=Long.valueOf(a.getBureauScore()))){
                        throw new ValidationException(ValidationError.ERRO07,foirbureau);
                    }
            }
            //-	For selecting fixed type, the loan tenor should be greater than or equal to 36 months.
            if(loanDetails.getTenor()<minFixedTenor && "FIXED".equalsIgnoreCase(loanDetails.getRoiType())){
                throw new ValidationException(ValidationError.ERRO08,minFixedTenor);
            }

        } catch (ValidationException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getError().name(), e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsRepository.findBySlnoAndDelFlg(loanDetails.getSlno(), "N");
        EligibilityDetails eligibilityDetails =null;
        if (eligibilityDetails_.isPresent()) {
            eligibilityDetails=eligibilityDetails_.get();
            eligibilityDetails.setEligibilityFlg("N");
            eligibilityDetails.setProceedFlag("N");
            eligibilityDetailsRepository.save(eligibilityDetails);
        }
        VehicleLoanApplicant applicant = applicantService.findByApplicantIdAndDelFlg(loanDetails.getApplicantId());
        applicant.setLoanComplete("Y");
        applicantService.saveApplicant(applicant);
        VehicleLoanDetails savedLoanDetails = loanDetailsService.save(loanDetails);
        return ResponseEntity.ok(savedLoanDetails);
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
    public ResponseDTO saveCRTAmber(String slno, String winum, String declaration, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }


    @Override
    public ResponseDTO saveWIRecall(String wiNum, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveRepayment(RepaymentDTO repaymentDTO) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveWaiver(WaiverDto waiverDto, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveBOG(Long slno, String winum, String remarks, HttpServletRequest request) throws Exception {
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
    public ResponseDTO savewism(Long slno, String winum, String remarks, String action, HttpServletRequest request) throws Exception {
        return null;
    }
    @Override
    public ResponseDTO acctlabelsave(AcctLabelDTO acctLabelDTO, HttpServletRequest request) throws Exception {
        return null;
    }
    @Override
    public ResponseDTO performAccOpening(Long slno, String winum,  HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO disbursement(Long slno, String winum,  HttpServletRequest request) throws Exception {
        return null;
    }
    @Override
    public ResponseDTO performDisbStatusEnquiry(Long slno, String winum,  HttpServletRequest request) throws Exception {
        return null;
    }
    public ResponseDTO performNeft(Long slno, String winum, String beneficiaryType, String dneftamt,String mneftamt,String accnum, String ifsc,
                                   String accname,  String manufMobile,String disbType, String add1, String add2, String add3, HttpServletRequest request) throws Exception {
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



