package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.DocumentRequest;
import com.sib.ibanklosucl.dto.RBCPCCheckerSave;
import com.sib.ibanklosucl.dto.RBCPCMakerSave;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanDetails;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.VehicleLoanProgram;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.service.VehicleLoanSubqueueTaskService;
import com.sib.ibanklosucl.service.VlCommonTabService;
import com.sib.ibanklosucl.service.doc.LegalityService;
import com.sib.ibanklosucl.service.doc.ManDoc;
import com.sib.ibanklosucl.service.doc.ManDocService;
import com.sib.ibanklosucl.service.integration.Docservice;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class DocumentaionSaveImpl implements VlCommonTabService {
    @Autowired
    private Docservice docservice;
    @Autowired
    private VehicleLoanMasterService masterService;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private LegalityService legalityService;
    @Autowired
    private ManDocService manDocService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private VehicleLoanSubqueueTaskService taskService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveDoc(DocumentRequest docRequest) throws Exception {
        VehicleLoanMaster master=masterService.findById(Long.valueOf(docRequest.getSlNo()));
        if(!fetchRepository.isvalidVehicleAccount(docRequest.getSlNo())){
            throw new ValidationException(ValidationError.COM001,"Dealer Account Missing in Vehicle Details !!");
        }
        else if(master.isDocCompleted()){
            throw new ValidationException(ValidationError.COM001,"Documentation Already Completed !!");
        }
        else if(master.getRepaymentStatus()==null || !master.getRepaymentStatus()){
            throw new ValidationException(ValidationError.COM001,"Kindly Complete Repayment Tab !!");
        }
        else if(master.getRoiRequested()==null){
            throw new ValidationException(ValidationError.COM001,"Kindly Complete ROI Waiver Tab !!");
        }
        else if(master.getChargeWaiverRequested()==null){
            throw new ValidationException(ValidationError.COM001,"Kindly Complete  Fee/Charge Waiver Tab !!");
        }
        else if(master.getRoiRequested()!=null && master.getRoiRequested() && taskService.isPending(Long.valueOf(docRequest.getSlNo()),"ROI_WAIVER")){
            throw new ValidationException(ValidationError.COM001,"Request Pending For ROI Waiver !!");
        }
        else if(master.getChargeWaiverRequested()!=null && master.getChargeWaiverRequested() && taskService.isPending(Long.valueOf(docRequest.getSlNo()),"CHARGE_WAIVER")){
            throw new ValidationException(ValidationError.COM001,"Request Pending For Fee/Charge Waiver !!");
        }
        else if(master.getApplicants().stream().anyMatch(t -> t.getSibCustomer().equalsIgnoreCase("N") && t.getDelFlg().equalsIgnoreCase("N"))){
            throw new ValidationException(ValidationError.COM001,"Kindly Create Customer ID for All Customers!");
        }
        switch (docRequest.getDocMode()){
            case "D":
                if(legalityService.isExpired(Long.valueOf(docRequest.getSlNo()))){
                    throw new ValidationException(ValidationError.COM001,"Please try resending your request, as the existing link is still active and has not expired.");
                }
                List<VehicleLoanApplicant> vlapps=master.getApplicants().stream().filter(t-> t.getDelFlg().equalsIgnoreCase("N") && !t.getApplicantType().equalsIgnoreCase("G")).toList();
                long AppCoAppCount= vlapps.size();
                long prgcount=0;boolean loanfd=false;
                for(VehicleLoanApplicant vlapp: vlapps) {
                    VehicleLoanProgram prg=vlapp.getVlProgram();
                    if(prg.getLoanProgram().equalsIgnoreCase("LOANFD")){
                        prgcount++;
                    }
                }
                if(loanfd && AppCoAppCount!=prgcount){
                    throw new ValidationException(ValidationError.COM001, "The WI cant be sent for Digital Documentation Since for Program FD All Applicants & Co/Applicants should have program FD");
                }
                docRequest.setCmUser(usd.getPPCNo());
//                String pdf="dsd";
//                String profileId="gGeA0tz";
                master.setDocMode("D");
                master.setStampAmt(null);
                ResponseDTO responseDTO=legalityService.formData(docRequest, Long.valueOf(docRequest.getSlNo()),master);
                masterService.saveLoan(master);
               return responseDTO;
            case "LS":
                return legalityService.callStatusApi(master.getSlno(), master.getWiNum(), docRequest.getLegalDocID());
            case "M":
                docRequest.setCmUser(usd.getPPCNo());
                ManDoc doc1=docservice.getAllPdf(docRequest);
                manDocService.saveManDoc(doc1,master.getSlno(),master.getWiNum());
                master.setDocMode("M");
                master.setStampAmt(null);
                masterService.saveLoan(master);
                return new ResponseDTO("S", manDocService.generateHTML(doc1));
            default:
                throw new RuntimeException("Invalid DOC Code");

        }

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
    public ResponseDTO saveRepayment(RepaymentDTO repaymentDTO) throws Exception {
        return null;
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
    @Override
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
