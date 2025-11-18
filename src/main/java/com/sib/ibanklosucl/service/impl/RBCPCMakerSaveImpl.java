package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;
import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.dto.losintegrator.HunterReviewItem;
import com.sib.ibanklosucl.dto.losintegrator.HunterReviewRequest;
import com.sib.ibanklosucl.model.VehicleLoanDetails;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.integrations.VLHunterDetails;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.integations.ExperianHunterResponseRepository;
import com.sib.ibanklosucl.service.VehicleLoanDecisionService;
import com.sib.ibanklosucl.service.VehicleLoanQueueDetailsService;
import com.sib.ibanklosucl.service.VlCommonTabService;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.integration.Docservice;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanLockService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanTatService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanWarnService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RBCPCMakerSaveImpl implements VlCommonTabService {
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private VehicleLoanDecisionService vehicleLoanDecisionService;
    @Autowired
    private VehicleLoanQueueDetailsService queueDetailsService;
    @Autowired
    private VehicleLoanLockService vehicleLoanLockService;
    @Autowired
    private VehicleLoanTatService vehicleLoanTatService;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private Docservice docservice;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private BpmService bpmService;
    @Autowired
    private VehicleLoanWarnService vehicleLoanWarnService;
         @Autowired
    private ExperianHunterResponseRepository experianHunterResponseRepository;
    @Value("${app.dev-mode:true}")
    private boolean devMode;

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
    @Transactional
    public ResponseDTO saveRBCMaker(RBCPCMakerSave rbs, HttpServletRequest request) throws Exception {
        Long slno = Long.valueOf(rbs.getSlno());
        String wiNum = rbs.getWinum();
        String decision = rbs.getDecision();
        String remarks = rbs.getRemarks();
        String preDisbursementCondition = rbs.getPreDisbursementCondition();
        DocumentRequest documentRequest=new DocumentRequest();
        documentRequest.setCmUser(usd.getPPCNo());
        documentRequest.setSlNo(String.valueOf(slno));
        documentRequest.setWiNum(wiNum);
//        String camPdf = docservice.getCamRbcpc(documentRequest);
//        TabResponse tb=bpmService.BpmUpload(bpmRequest(wiNum,camPdf,"N","NA","CAMRBCPC"));
//        if(!"S".equalsIgnoreCase(tb.getStatus())){
//            return new ResponseDTO(tb.getStatus(), tb.getMsg());
//        }
        if ("SB".equals(decision)) {
            if (remarks == null || remarks.trim().isEmpty()) {
                throw new IllegalArgumentException("Remarks are mandatory for sendback");
            }
            vehicleLoanMasterService.sendbackApplication(slno, wiNum, remarks, request);
            vehicleLoanTatService.updateTat(slno, usd.getEmployee().getPpcno(), wiNum, "BS");
            return new ResponseDTO("S", "Application sent back successfully.");
        } else if ("HU".equals(decision)) {

            VehicleLoanMaster loanMaster = vehicleLoanMasterService.findById(slno);
            if (loanMaster == null) {
                throw new RuntimeException("Loan application not found");
            }
            String currentQueue = loanMaster.getQueue();
            String newQueue = "HU";
            String chkdecision = "Forwarded for Hunter Approval";
            loanMaster.setQueue(newQueue);
            loanMaster.setStatus("HUPENDING");
//            loanMaster.setBrVUser(usd.getEmployee().getPpcno());
//            loanMaster.setBrVDate(new Date());
            vehicleLoanMasterService.saveLoan(loanMaster);
            queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), currentQueue, newQueue);
            vehicleLoanTatService.updateTat(rbs.getSlno(), usd.getEmployee().getPpcno(), wiNum, newQueue);
            vehicleLoanLockService.ReleaseLock(rbs.getSlno(), usd.getPPCNo());
            return new ResponseDTO("S", "Application forwarded successfully to " + newQueue);
        } else {
            if(!fetchRepository.isvalidVehicleAccount(String.valueOf(rbs.getSlno()))){
                throw new IllegalArgumentException("Dealer Account Missing in Vehicle Details !!");
            }
            ResponseDTO responseDTO = vehicleLoanWarnService.sendWarnEmail(Long.valueOf(slno));
            if (!responseDTO.getStatus().equals("S"))
                return responseDTO;
            if (preDisbursementCondition == null || preDisbursementCondition.trim().isEmpty()) {
                throw new IllegalArgumentException("Pre-disbursement condition is mandatory for forwarding");
            }

            VehicleLoanMaster loanMaster = vehicleLoanMasterService.findById(slno);
            if (loanMaster == null) {
                throw new RuntimeException("Loan application not found");
            }

            String newQueue = decision;
            String newStatus = "RMCOMPLETE";

            // Update VehicleLoanMaster
            loanMaster.setQueue(newQueue);
            loanMaster.setQueueDate(new Date());
            loanMaster.setStatus(newStatus);
            loanMaster.setRbcpcMakerDecision(decision);
            loanMaster.setRbcpcCheckerUser(rbs.getRbcpcCheckerUser());
            loanMaster.setPreDisbCondition(preDisbursementCondition);
            loanMaster.setRbcpcCmUser(usd.getEmployee().getPpcno());
            loanMaster.setRbcpcCmDate(new Date());

            if (devMode) {
                        String camPdf = docservice.getCamRbcpc(documentRequest);
        TabResponse tb=bpmService.BpmUpload(bpmRequest(wiNum,camPdf,"N","NA","CAMRBCPC"));
        if(!"S".equalsIgnoreCase(tb.getStatus())){
            return new ResponseDTO(tb.getStatus(), tb.getMsg());
        }
            }
            vehicleLoanMasterService.saveLoan(loanMaster);

            // Save the decision
            vehicleLoanDecisionService.saveDecision(wiNum, slno, newQueue, decision, request);

            //queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), loanMaster.getQueue(), newQueue);
            queueDetailsService.createQueueWithAssignUserEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), "RM", newQueue,rbs.getRbcpcCheckerUser());

            // Release the lock
            vehicleLoanLockService.ReleaseLock(slno, usd.getPPCNo());

            // Update TAT
            vehicleLoanTatService.updateTat(slno, usd.getEmployee().getPpcno(), wiNum, newQueue);

            return new ResponseDTO("S", "Application forwarded successfully to " + newQueue);
        }
    }


    private BpmRequest bpmRequest(String winum, String pdf, String child, String childName, String docName) {
        BPMFileUpload bpmFileUpload = new BPMFileUpload();
        bpmFileUpload.setWI_NAME(winum);
        bpmFileUpload.setCHILD(child);
        bpmFileUpload.setCHILD_FOLDER(childName);
        bpmFileUpload.setSystemIP(usd.getRemoteIP());
        List<DOC_ARRAY> docArrayList = new ArrayList<>();
        DOC_ARRAY docArray = new DOC_ARRAY();
        docArray.setDOC_NAME(docName+ CommonUtils.getCurrentTimestamp());
        docArray.setDOC_EXT("pdf");
        docArray.setDOC_BASE64(pdf);
        docArrayList.add(docArray);
        bpmFileUpload.setDOC_ARRAY(docArrayList);
        BpmRequest bpmRequest = new BpmRequest();
        bpmRequest.setRequest(bpmFileUpload);
        return bpmRequest;
    }
    public ResponseDTO saveHunterApproval(HunterReviewRequest hunterReviewRequest, HttpServletRequest request) throws Exception {
       try {
        Long slno = hunterReviewRequest.getSlno();
        String wiNum = hunterReviewRequest.getWiNum();
        String reviewUser = hunterReviewRequest.getReviewUser();
        Date reviewDate = new Date(); // Use the current date

        // Update Hunter details
        for (HunterReviewItem review : hunterReviewRequest.getHunterReviews()) {
            VLHunterDetails hunterDetails = experianHunterResponseRepository
                .findTopByApplicantIdAndDelFlgOrderByTimestampDesc(review.getApplicantId(), "N")
                .orElseThrow(() -> new RuntimeException("Hunter details not found for applicant " + review.getApplicantId()));

            hunterDetails.setHunterUserRemarks(review.getRemarks());
            hunterDetails.setReviewUser(reviewUser);
            hunterDetails.setReviewDate(reviewDate);

            experianHunterResponseRepository.save(hunterDetails);
        }

        // Update VehicleLoanMaster
        VehicleLoanMaster loanMaster = vehicleLoanMasterService.findById(slno);
        if (loanMaster == null) {
            throw new RuntimeException("Loan application not found");
        }

        String currentQueue = loanMaster.getQueue();
        String newQueue = "RM";
        String newStatus = "RMPENDING";

        loanMaster.setQueue(newQueue);
        loanMaster.setQueueDate(new Date());
        loanMaster.setStatus(newStatus);

        vehicleLoanMasterService.saveLoan(loanMaster);

        // Create queue entry
        queueDetailsService.createQueueEntry(wiNum, slno, "Hunter review completed", reviewUser, currentQueue, newQueue);

        // Update TAT
        vehicleLoanTatService.updateTat(slno, reviewUser, wiNum, newQueue);

        // Release lock
        vehicleLoanLockService.ReleaseLock(slno, reviewUser);

        return new ResponseDTO("S", "Hunter review saved and application moved to RM queue.");
    } catch (Exception e) {
        log.error("Error in saveHunterReview", e);
        return new ResponseDTO("F", "Error: " + e.getMessage());
    }

    }


    @Override
    public ResponseDTO saveCRTAmber(String slno, String winum, String remarks, String decision, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveBOG(Long slno, String winum, String remarks, HttpServletRequest request) throws Exception {
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
