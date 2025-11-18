package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;

import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.integration.Docservice;
import com.sib.ibanklosucl.service.integration.SMSEmailService;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
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
public class RbcpcCheckerSaveImpl implements VlCommonTabService {
    @Autowired
    private VehicleLoanQueueDetailsService vehicleLoanQueueDetailsService;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;

    @Autowired
    private VehicleLoanLockService vehicleLoanLockService;
    @Autowired
    private VehicleLoanTatService loanTatService;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanAmberService loanAmberService;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private MisrctService misrctService;
    @Autowired
    private VehicleLoanDecisionService vehicleLoanDecisionService;
    @Autowired
    private VehicleLoanWarnService VehicleLoanWarnService;
    @Autowired
    private Docservice docservice;
    @Autowired
    private BpmService bpmService;
    @Autowired
    private VehicleLoanWaiverService waiverService;
    @Autowired
    private SMSEmailService smsEmailService;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;

    @Value("${app.dev-mode:true}")
    private boolean devMode;
    @Value("${helpdeskNo}")
    private String helpdeskNo;

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
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveRBCChecker(RBCPCCheckerSave rbs, HttpServletRequest request) throws Exception {
        Long Slno= Long.valueOf(rbs.getSlno());
        List<VehicleLoanAmber> vlAmbers=loanAmberService.getAmberDeviationsByWiNumAndSlno(rbs.getWinum(),Slno);
        String fromQueue=fetchRepository.getUserRole(usd.getPPCNo());
        String toQueue="", status="";
        List<Misrct> dec= misrctService.getCodeValuesByTypeOrdered(fromQueue);
        Long userlevel= Long.valueOf(fromQueue.substring(fromQueue.length()-1));
        String rbcDecision=rbs.getRbcdecision();
        Long currentLevel= 0L;
        if (rbcDecision.startsWith("RCL")){
            currentLevel= Long.valueOf(rbcDecision.substring(rbcDecision.length()-1));
        }

        switch (rbcDecision){
            case "SANC":
                if(!fetchRepository.isvalidVehicleAccount(rbs.getSlno())){
                    throw new ValidationException(ValidationError.COM001,"Dealer Account Missing in Vehicle Details !!");
                }
                if(vlAmbers.stream().filter(t->!t.getApprovingAuth().equalsIgnoreCase("NA")).anyMatch(t-> (t.getApprAuthAction()==null  ||  !"APPROVE".equalsIgnoreCase(t.getApprAuthAction()))))
                    throw new ValidationException(ValidationError.COM001,"All the Deviations must be Approved to Sanction the WI");
                if(VehicleLoanWarnService.countOfWarn(Slno)>0)
                    toQueue="CA";
                else
                    toQueue="BD";

                status=getStatus(dec,rbcDecision);
                VehicleLoanMaster vmss=vehicleLoanMasterService.findById(Slno);
                vmss.setSanFlg("Y");
                vmss.setSanUser(usd.getPPCNo());
                vmss.setSanDate(new Date());
                if(vmss.getSaninitDate()==null){
                    vmss.setSaninitDate(new Date());
                }
                vehicleLoanMasterService.saveLoan(vmss);
                DocumentRequest documentRequest=new DocumentRequest();
                documentRequest.setCmUser(usd.getPPCNo());
                documentRequest.setSlNo(String.valueOf(Slno));
                documentRequest.setWiNum(rbs.getWinum());
                String pdf=docservice.getSanctionPdf(documentRequest);
                waiverService.calculateProcessingFee(rbs);
                TabResponse tb=bpmService.BpmUpload(bpmRequest(rbs.getWinum(),pdf,"N","NA","SANCTION_LETTER"));
                if(!"S".equalsIgnoreCase(tb.getStatus())){
                    throw new ValidationException(ValidationError.COM001,tb.getMsg());
                }

                break;
            case "NIL":
                toQueue=rbcDecision;
                VehicleLoanMaster vms=vehicleLoanMasterService.findById(Slno);
                vms.setStatus("RCREJ");
                vms.setActiveFlg("N");
                vms.setRejFlg("Y");
                vms.setRejUser(usd.getPPCNo());
                vms.setRejDate(new Date());
                vms.setRejQueue(fromQueue);
                vehicleLoanMasterService.saveLoan(vms);
                break;
            default:
                if(!fetchRepository.isvalidVehicleAccount(rbs.getSlno())){
                    throw new ValidationException(ValidationError.COM001,"Dealer Account Missing in Vehicle Details !!");
                }
                if(userlevel<=currentLevel && vlAmbers.stream().anyMatch(t->(t.getApprAuthAction()==null || !"APPROVE".equalsIgnoreCase(t.getApprAuthAction())) && fromQueue.equalsIgnoreCase(t.getApprovingAuth())))
                    throw new ValidationException(ValidationError.COM001,"All the Decisions at Current Level must be Approved.");
                toQueue=rbcDecision;
                VehicleLoanMaster vmas=vehicleLoanMasterService.findById(Slno);
                vmas.setRbcpcCheckerUser(rbs.getRbcpcCheckerUser());
                status=getStatus(dec,rbcDecision);

        }
        vehicleLoanDecisionService.saveDecision(rbs.getWinum(),Slno,fromQueue,rbcDecision,request);
        if(rbs.getRbcdecision().startsWith("RC")) {
            vehicleLoanQueueDetailsService.createQueueWithAssignUserEntry(rbs.getWinum(), Slno, rbs.getRbccremarks(), usd.getPPCNo(), fromQueue, toQueue,rbs.getRbcpcCheckerUser());
        } else {
            vehicleLoanQueueDetailsService.createQueueEntry(rbs.getWinum(), Slno, rbs.getRbccremarks(), usd.getPPCNo(), fromQueue, toQueue);
        }
        vehicleLoanMasterService.updateQueue(Slno, toQueue, status, usd.getPPCNo());
        vehicleLoanLockService.ReleaseLock(Slno, usd.getPPCNo());
        loanTatService.updateTat(Slno,usd.getPPCNo(), rbs.getWinum(),toQueue );

        if("SANC".equalsIgnoreCase(rbcDecision)){
            Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(rbs.getWinum(), Slno);
            VehicleLoanMaster vmas=vehicleLoanMasterService.findById(Slno);
            List<VehicleLoanApplicant> vlapp =vmas.getApplicants().stream().filter(t->t.getDelFlg().equals("N") && t.getApplicantType().equalsIgnoreCase("A")).toList();
            for (VehicleLoanApplicant vp:vlapp){
                String mobile=vp.getBasicapplicants().getMobileCntryCode()+vp.getBasicapplicants().getMobileNo();
                String email=vp.getBasicapplicants().getEmailId();
                String salutation=vp.getBasicapplicants().getSalutation();
                salutation=misrctService.getByCodeValue("TIT",salutation).getCodedesc();
                String custName=vp.getApplName();
                String sancAmt=eligibilityDetailsOpt.get().getSancAmountRecommended().toString();
                String brName=fetchRepository.getSolName(vmas.getSolId());
                String helpdesk=helpdeskNo;
                SMSEmailDTO smdDto = new SMSEmailDTO();
                smdDto.setAlertId("LOSAPPR");
                smdDto.setSlno(Slno);
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
                smdDto.setMessage(salutation+"|"+custName + "|Rs."+sancAmt +"/-|"+ smdDto.getWiNum() + "|" + brName + "|" + helpdesk+"|");
                if(!devMode) {
                    ResponseDTO sms = smsEmailService.insertSMSEmail(smdDto);
                    if (sms.getStatus().equalsIgnoreCase("F")) return sms;
                }
                SMSEmailDTO emailDTO = new SMSEmailDTO();
                emailDTO.setSlno(Slno);
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
             //       emailDTO.setEmailTo("vigneshpadmanabhan@sib.bank.in");
                }
                // emailDTO.setEmailTo(email);
                emailDTO.setEmailBody("Congratulations! Your Vehicle loan of request of Rs."+sancAmt+"/- vide Appl No-"+rbs.getWinum()+" has been approved. Kindly contact the branch "+brName+" to complete the formalities");
                emailDTO.setCustName(salutation+" "+custName);
                emailDTO.setEmailSubject("Your Vehicle Loan Is Approved – South Indian Bank");
                ResponseDTO email_ = smsEmailService.insertSMSEmail(emailDTO);
                if (email_.getStatus().equalsIgnoreCase("F"))
                    return new ResponseDTO("F", "SMS Sent Successfully ,Email  Failed");


            }
            }
        else   if("NIL".equalsIgnoreCase(rbcDecision)){
            VehicleLoanMaster vmas=vehicleLoanMasterService.findById(Slno);
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
                smdDto.setSlno(Slno);
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
                smdDto.setMessage(salutation+"|"+custName  +"|"+ smdDto.getWiNum() +  "|" + helpdesk+"|");
                ResponseDTO sms = smsEmailService.insertSMSEmail(smdDto);
                if (sms.getStatus().equalsIgnoreCase("F")) return sms;
                SMSEmailDTO emailDTO = new SMSEmailDTO();
                emailDTO.setSlno(Slno);
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
                   // emailDTO.setEmailTo("vigneshpadmanabhan@sib.bank.in");
                }
                emailDTO.setEmailBody("We regret to inform you that your application "+rbs.getWinum()+" for vehicle loan has been declined. For further details, please contact our customer care  "+helpdesk+" or visit the nearest branch. We appreciate your interest in SIB and look forward to serving you in future");
                emailDTO.setCustName(salutation+" "+custName);
                emailDTO.setEmailSubject("Vehicle Loan Application Rejected – South Indian Bank");
                ResponseDTO email_ = smsEmailService.insertSMSEmail(emailDTO);
                if (email_.getStatus().equalsIgnoreCase("F"))
                    return new ResponseDTO("F", "SMS Sent Successfully ,Email  Failed");


            }
        }
        return new ResponseDTO("S","Record Saved Successfully (action : "+status+") & Moved to "+toQueue);
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
    public ResponseDTO updateWaiver(WaiverDto waiverDto, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveDoc(DocumentRequest documentRequest) throws Exception {
        return null;
    }

    public String getStatus(List<Misrct> dec ,String decision){
        Optional<String> status = dec.stream()
                .filter(t -> t.getCodevalue().equalsIgnoreCase(decision))
                .map(t -> t.getCodedesc())
                .findFirst();
        return status.orElse(""); // Replace "Default Description" with a suitable default value
    }

    public BpmRequest bpmRequest(String winum, String pdf, String child, String childName, String docName){
        BPMFileUpload bpmFileUpload = new BPMFileUpload();
        bpmFileUpload.setWI_NAME(winum);
        bpmFileUpload.setCHILD(child);
        bpmFileUpload.setCHILD_FOLDER(childName);
        bpmFileUpload.setSystemIP(usd.getRemoteIP());
        List<DOC_ARRAY> docArrayList = new ArrayList<>();
        DOC_ARRAY docArray = new DOC_ARRAY();
        docArray.setDOC_NAME(docName + CommonUtils.getCurrentTimestamp());
        docArray.setDOC_EXT("pdf");
        docArray.setDOC_BASE64(pdf);
        docArrayList.add(docArray);
        bpmFileUpload.setDOC_ARRAY(docArrayList);
        BpmRequest bpmRequest = new BpmRequest();
        bpmRequest.setRequest(bpmFileUpload);
        return bpmRequest;
    }

    @Override
    public ResponseDTO saveBOG(Long slno, String winum, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }

//    @Override
//    public ResponseDTO saveCustDoc(String winum, String foldername, String filename, String remarks, HttpServletRequest request) throws Exception {
//        return null;
//    }

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
    public ResponseDTO performInsFiTrn(Long slno,String winum) throws Exception {
        return null;
    }
}
