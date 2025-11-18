package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;
import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanDetails;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.integrations.VLHunterDetails;
import com.sib.ibanklosucl.model.integrations.VehicleLoanBREDetails;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.integations.ExperianHunterResponseRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.integration.Docservice;
import com.sib.ibanklosucl.service.integration.SMSEmailService;
import com.sib.ibanklosucl.service.integration.VehicleLoanBREService;
import com.sib.ibanklosucl.service.mssf.MSSFService;
import com.sib.ibanklosucl.service.vlsr.MisrctService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanLockService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanTatService;
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
import java.util.Optional;

@Service
@Slf4j
public class BrCheckerSaveImpl implements VlCommonTabService {
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
    private BpmService bpmService;
    @Autowired
    private VehicleLoanBREService vehicleLoanBREService;
    @Autowired
    private VehicleLoanAllotmentService allotmentService;
    @Autowired
    private VehicleLoanWaiverService waiverService;
    @Autowired
    private SMSEmailService smsEmailService;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;
    @Autowired
    private MisrctService misrctService;
    @Autowired
    private FetchRepository fetchRepository;
    @Value("${app.dev-mode:true}")
    private boolean devMode;

    @Value("${helpdeskNo}")
    private String helpdeskNo;
      @Autowired
    private ExperianHunterResponseRepository experianHunterResponseRepository;
         @Autowired
    private MSSFService mssfService;
    @Override
    public ResponseEntity<?> saveLoan(VehicleLoanDetails vehicleLoanDetails) {
        return null;
    }

    @Override
    public ResponseDTO saveMaker(String slno, String winum, String vlowner, String vlownerstatus, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveChecker(RBCPCMakerSave rbs, HttpServletRequest request) throws Exception {
        Long slno = Long.valueOf(rbs.getSlno());
        String wiNum = rbs.getWinum();
        String decision = rbs.getDecision();
        String remarks = rbs.getRemarks();
        String msg="Application forwarded successfully.";
        List<VLHunterDetails> allHunterDetails = experianHunterResponseRepository
                .findAllByWiNumAndDelFlgOrderByTimestampDesc(wiNum, "N");

        boolean hunterCheckPerformed = !allHunterDetails.isEmpty();

        boolean hunterMatchFound = false;
        if (hunterCheckPerformed) {
            hunterMatchFound = allHunterDetails.stream()
                    .anyMatch(detail -> detail.getMatches()>0); // Assuming a match > 0 indicates a match
        }
        RBCPCCheckerSave rbcpcCheckerSave = new RBCPCCheckerSave();
        rbcpcCheckerSave.setSlno(String.valueOf(rbs.getSlno()));
        rbcpcCheckerSave.setWinum(rbs.getWinum());
        rbcpcCheckerSave.setRbccremarks(remarks);
        VehicleLoanMaster loanMaster = vehicleLoanMasterService.findById(slno);
        if (loanMaster == null) {
            throw new RuntimeException("Loan application not found");
        }
        String refNo = loanMaster.getRefNo();
        log.info("Saving the workitem in BRCHECKER decision {}",decision);
        if ("FW".equals(decision)) {
            waiverService.calculateProcessingFee(rbcpcCheckerSave);
            Optional<VehicleLoanBREDetails> latestBREDetails = vehicleLoanBREService.getLatestBREDetails(wiNum, Long.valueOf(slno));
            if (!latestBREDetails.isPresent()) {
                throw new RuntimeException("BRE details not found for the application");
            }
            String breFlag = latestBREDetails.get().getBreFlag();
            String newQueue;
            String loanFlowType = "";
            String pdf = "";
            if(hunterMatchFound) {
                breFlag="amber"; //hunter match found
            }
            log.info("Saving the workitem in BRCHECKER huntermatch found -reroute to RBCPC {} COLOR {}",hunterMatchFound,breFlag);
            //waiverService.calculateProcessingFee(rbs);
            if ("green".equalsIgnoreCase(breFlag)) {
                newQueue = "CS"; // CRT Queue
                loanFlowType = "STP";
                try {
                    try {
                        DocumentRequest documentRequest = new DocumentRequest();
                        documentRequest.setCmUser(usd.getPPCNo());
                        documentRequest.setSlNo(String.valueOf(slno));
                        documentRequest.setWiNum(wiNum);
                        pdf = docservice.getSanctionPdf(documentRequest);
                    } catch (ValidationException ve) {
                        log.error("Validation error occurred while generating sanction PDF: {}", ve.getMessage(), ve);
                        throw new RuntimeException("Failed to generate sanction PDF due to validation error", ve);
                    }
                    /*Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(rbs.getWinum(), slno);
                    VehicleLoanMaster vmas = vehicleLoanMasterService.findById(slno);
                    List<VehicleLoanApplicant> vlapp = vmas.getApplicants().stream().filter(t -> t.getDelFlg().equals("N") && t.getApplicantType().equalsIgnoreCase("A")).toList();
                    for (VehicleLoanApplicant vp : vlapp) {
                        String mobile = vp.getBasicapplicants().getMobileCntryCode() + vp.getBasicapplicants().getMobileNo();
                        String email = vp.getBasicapplicants().getEmailId();
                        String salutation = vp.getBasicapplicants().getSalutation();
                        salutation = misrctService.getByCodeValue("TIT", salutation).getCodedesc();
                        String custName = vp.getApplName();
                        String sancAmt = eligibilityDetailsOpt.get().getSancAmountRecommended().toString();
                        String brName = fetchRepository.getSolName(vmas.getSolId());
                        String helpdesk = helpdeskNo;
                        SMSEmailDTO smdDto = new SMSEmailDTO();
                        smdDto.setAlertId("LOSAPPR");
                        smdDto.setSlno(slno);
                        smdDto.setWiNum(rbs.getWinum());
                        smdDto.setSentUser(usd.getPPCNo());
                        smdDto.setReqType("S");
                        if(!devMode) {
                            smdDto.setMobile(mobile);
                        }
                        else{
                            smdDto.setMobile("918547016003");
                        }
                        //smdDto.setMobile(mobile);
                        smdDto.setMessage(salutation + "|" + custName + "|Rs." + sancAmt + "/-|" + smdDto.getWiNum() + "|" + brName + "|" + helpdesk+"|");
                        ResponseDTO sms = smsEmailService.insertSMSEmail(smdDto);
                        if (sms.getStatus().equalsIgnoreCase("F")) return sms;
                        SMSEmailDTO emailDTO = new SMSEmailDTO();
                        emailDTO.setSlno(slno);
                        emailDTO.setWiNum(rbs.getWinum());
                        emailDTO.setSentUser(usd.getPPCNo());
                        emailDTO.setAlertId("LOSAPPR");
                        emailDTO.setReqType("E");
                        emailDTO.setEmailFrom("sibmailer@sib.bank.in");
                        if(!devMode) {
                            emailDTO.setEmailTo(email);
                        }
                        else {
                            emailDTO.setEmailTo("antonyraj@sib.bank.in");
                       //     emailDTO.setEmailTo("vigneshpadmanabhan@sib.bank.in");
                        }
                        emailDTO.setEmailBody("Congratulations! Your Vehicle loan of request of Rs." + sancAmt + "/- vide Appl No-" + rbs.getWinum() + " has been approved. Kindly contact the branch " + brName + " to complete the formalities");
                        emailDTO.setCustName(salutation + " " + custName);
                        emailDTO.setEmailSubject("Your Vehicle Loan Is Approved – South Indian Bank");
                        ResponseDTO email_ = smsEmailService.insertSMSEmail(emailDTO);
                        if (email_.getStatus().equalsIgnoreCase("F"))
                            return new ResponseDTO("F", "SMS Sent Successfully ,Email  Failed");


                    }*/
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                TabResponse tb = bpmService.BpmUpload(bpmRequest(wiNum, pdf, "N", "NA", "SANCTION_LETTER"));
                if (!"S".equalsIgnoreCase(tb.getStatus())) {
                    throw new ValidationException(ValidationError.COM001, tb.getMsg());
                }
            } else if ("amber".equalsIgnoreCase(breFlag)) {
                loanFlowType = "NONSTP";
                if (allotmentService.hasActiveAllotment(wiNum, slno)) {
                    newQueue = "RM"; // RBCPC Maker
                } else {
                    newQueue = "RA"; // RBCPC Allotment
                }
            } else {
                throw new RuntimeException("Invalid BRE flag: " + breFlag);
            }

            loanMaster.setQueue(newQueue);
            loanMaster.setQueueDate(new Date());
            loanMaster.setBrVUser(usd.getEmployee().getPpcno());
            loanMaster.setBrVDate(new Date());
            loanMaster.setStatus("BCCOMPLETE");
            loanMaster.setStp(loanFlowType);
            if(loanMaster.getStp().equals("STP")) {
                loanMaster.setSanUser(usd.getPPCNo());
                loanMaster.setSanDate(new Date());
                if(loanMaster.getSaninitDate()==null){
                    loanMaster.setSaninitDate(new Date());
                }
                loanMaster.setSanFlg("Y");
            }
            vehicleLoanMasterService.saveLoan(loanMaster);
            log.info("Saving the workitem BRCHECKER  queue{} color {} flowtype {} wiNum {}",newQueue,breFlag,loanFlowType,wiNum);
            queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), "BC", newQueue);
            vehicleLoanTatService.updateTat(rbs.getSlno(), usd.getEmployee().getPpcno(), wiNum, newQueue);
            vehicleLoanLockService.ReleaseLock(rbs.getSlno(), usd.getPPCNo());

            if(refNo!=null) {
                if(loanMaster.getStp().equals("STP")) {
                    mssfService.updateApplication(refNo, "340003", "Loan is sanctioned");
                } else {
                    mssfService.updateApplication(refNo, "340002", "Loan is under progress");
                }
            }
        } else if ("BCREJ".equals(decision)) {
            String newQueue = "NIL";
            String currentQueue = loanMaster.getQueue();
            loanMaster.setQueue(newQueue);
            loanMaster.setStatus("BCREJ");
            loanMaster.setRejFlg("Y");
            loanMaster.setRejDate(new Date());
            loanMaster.setRejUser(usd.getEmployee().getPpcno());
            loanMaster.setRejQueue(currentQueue);
            vehicleLoanMasterService.saveLoan(loanMaster);
            queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), currentQueue, newQueue);
            vehicleLoanTatService.updateTat(rbs.getSlno(), usd.getEmployee().getPpcno(), wiNum, newQueue);
            vehicleLoanLockService.ReleaseLock(rbs.getSlno(), usd.getPPCNo());
            VehicleLoanMaster vmas=vehicleLoanMasterService.findById(slno);
            List<VehicleLoanApplicant> vlapp =vmas.getApplicants().stream().filter(t->t.getDelFlg().equals("N") && t.getApplicantType().equalsIgnoreCase("A")).toList();
            for (VehicleLoanApplicant vp:vlapp){
                String mobile=vp.getBasicapplicants().getMobileCntryCode()+vp.getBasicapplicants().getMobileNo();
                String email=vp.getBasicapplicants().getEmailId();
                String salutation=vp.getBasicapplicants().getSalutation();
                salutation=misrctService.getByCodeValue("TIT",salutation).getCodedesc();
                String custName=vp.getApplName();
                String brName=fetchRepository.getSolName(vmas.getSolId());
                String helpdesk=helpdeskNo;
                SMSEmailDTO smdDto = new SMSEmailDTO();
                smdDto.setAlertId("LOSDECN");
                smdDto.setSlno(slno);
                smdDto.setWiNum(rbs.getWinum());
                smdDto.setSentUser(usd.getPPCNo());
                smdDto.setReqType("S");
                if(!devMode) {
                    smdDto.setMobile(mobile);
                }
                else{
                    smdDto.setMobile("918547016003");
                }
                smdDto.setMessage(salutation+"|"+custName  +"|"+ smdDto.getWiNum() +  "|" + helpdesk+"|");
                ResponseDTO sms = smsEmailService.insertSMSEmail(smdDto);
                if (sms.getStatus().equalsIgnoreCase("F")) return sms;
                SMSEmailDTO emailDTO = new SMSEmailDTO();
                emailDTO.setSlno(slno);
                emailDTO.setWiNum(rbs.getWinum());
                emailDTO.setSentUser(usd.getPPCNo());
                emailDTO.setAlertId("LOSDECN");
                emailDTO.setReqType("E");
                emailDTO.setEmailFrom("sibmailer@sib.bank.in");
                if(!devMode) {
                    emailDTO.setEmailTo(email);
                }
                else {
                    emailDTO.setEmailTo("antonyraj@sib.bank.in");
                  //  emailDTO.setEmailTo("vigneshpadmanabhan@sib.bank.in");
                }
                emailDTO.setEmailBody("We regret to inform you that your application "+rbs.getWinum()+" for vehicle loan has been declined. For further details, please contact our customer care  "+helpdesk+" or visit the nearest branch. We appreciate your interest in SIB and look forward to serving you in future");
                emailDTO.setCustName(salutation+" "+custName);
                emailDTO.setEmailSubject("Vehicle Loan Application Rejected – South Indian Bank");
                ResponseDTO email_ = smsEmailService.insertSMSEmail(emailDTO);
                if (email_.getStatus().equalsIgnoreCase("F"))
                    return new ResponseDTO("F", "SMS Sent Successfully ,Email  Failed");


            }
            msg="Application Rejected successfully.";
             if(refNo!=null) {
                mssfService.updateApplication(refNo,"340004","Loan is Rejected");
            }

        } else if ("SB".equals(decision)) {
            String currentQueue = loanMaster.getQueue();
            String newQueue = "BS";
            String chkdecision = "Sendback to Branch";
            loanMaster.setQueue(newQueue);
            loanMaster.setStatus("BCSENDBACK");
            loanMaster.setBrVUser(usd.getEmployee().getPpcno());
            loanMaster.setBrVDate(new Date());
            vehicleLoanMasterService.saveLoan(loanMaster);
            queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), currentQueue, newQueue);
            vehicleLoanTatService.updateTat(rbs.getSlno(), usd.getEmployee().getPpcno(), wiNum, newQueue);
            vehicleLoanLockService.ReleaseLock(rbs.getSlno(), usd.getPPCNo());
            msg="Application Sent Backed successfully.";
        }
        return new ResponseDTO("S",msg);
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
    public ResponseDTO saveDoc(DocumentRequest documentRequest) throws Exception {
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

    public BpmRequest bpmRequest(String winum, String pdf, String child, String childName, String docName) {
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

    @Override
    public ResponseDTO disbursement(Long slno, String winum, HttpServletRequest request) throws Exception {
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
