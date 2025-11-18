package com.sib.ibanklosucl.service.impl;


import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.model.doc.LegalityInvitees;
import com.sib.ibanklosucl.model.doc.VehicleLoanChargeWaiver;
import com.sib.ibanklosucl.model.doc.VehicleLoanRoiWaiver;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.doc.VehicleLoanFeeWaiverRepository;
import com.sib.ibanklosucl.repository.doc.VehicleLoanRoiWaiverRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.doc.LegalityService;
import com.sib.ibanklosucl.service.doc.ManDocService;
import com.sib.ibanklosucl.service.eligibility.EligibilityDetailsService;
import com.sib.ibanklosucl.service.eligibility.EligibilityHelperService;
import com.sib.ibanklosucl.service.integration.Docservice;
import com.sib.ibanklosucl.service.integration.SMSEmailService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanLockService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanTatService;
import com.sib.ibanklosucl.utilies.AESUtil;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class WaiverSaveImpl implements VlCommonTabService {

    @Autowired
    private VehicleLoanMasterService masterService;

    @Value("${app.mailSmsSend:false}")
    private boolean mailSmsSend;

    @Value("${app.dev-mode:true}")
    private boolean devMode;

    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanSubqueueTaskService taskService;
    @Autowired
    private EligibilityHelperService eligibilityHelperService;
    @Autowired
    private VehicleLoanRoiWaiverRepository vehicleLoanRoiWaiverRepository;
    @Autowired
    private VehicleLoanFeeWaiverRepository feeWaiverRepository;
    @Autowired
    private EligibilityDetailsService eligibilityDetailsService;
    @Autowired
    private VehicleLoanSubqueueTatService subqueueTatService;
    @Autowired
    private VehicleLoanTatService tatService;
    @Autowired
    private ManDocService manDocService;
    @Autowired
    private LegalityService legalityService;
    @Autowired
    private VehicleLoanWaiverService vehicleLoanWaiverService;
    @Value("${helpdeskNo}")
    private String helpdeskNo;
    @Autowired
    private VehicleLoanSanModService vehicleLoanSanModService;
    @Autowired
    private BpmService bpmService;
    @Autowired
    private VehicleLoanQueueDetailsService vehicleLoanQueueDetailsService;
    @Autowired
    private VehicleLoanLockService lockService;
    @Autowired
    private SMSEmailService smsEmailService;
    @Autowired
    private AESUtil aesUtil;
    @Autowired
    private Docservice docservice;
    @Autowired
    private FetchRepository fetchRepository;

    @Override
    public ResponseDTO saveRepayment(RepaymentDTO repaymentDTO) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveWaiver(WaiverDto waiverDto, HttpServletRequest request) throws Exception {
        switch (waiverDto.getWaiverType()) {
            case "ROI":
                return saveRoiWaiver(waiverDto.getRoidto(), request);
            case "CHARGE":
                return saveFeeWaiver(waiverDto.getProcessFeeWaiverDto(), request);
            case "CIF":
                return saveCif(waiverDto.getCifCreationDto(), request);
            case "DOCSAVE":
                return saveDocument(waiverDto.getCompleteDto());
            case "RETURN":
                return saveReturn(waiverDto.getCompleteDto(), request);
            case "RESEND":
                return resendLeegality(waiverDto.getCompleteDto());
            case "SAN_MOD":
                return saveSanTask(waiverDto.getSanModDto(), request);
            default:
                throw new RuntimeException("Invalid Waiver Type");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO resendLeegality(WaiverDto.CompleteDto dto) {
        VehicleLoanMaster vms = masterService.findById(dto.getSlno());
        if (vms.isDocCompleted()) {
            throw new ValidationException(ValidationError.COM001, "The WI has Already Completed  Documentation");
        }


        LegalityInvitees invitees = legalityService.findByUrl(dto.getSlno(), dto.getSignUrl());
        if (invitees == null) {
            throw new ValidationException(ValidationError.COM001, "The Request Seems to Be Invalid");
        }
        if ((invitees.getSigned() != null && invitees.getSigned()) || (invitees.getExpired() != null && invitees.getExpired()) || (invitees.getRejected() != null && invitees.getRejected())) {
            throw new ValidationException(ValidationError.COM001, "The Invitation  Seems to Be Signed/Exprired/Rejected");
        }
        if (!invitees.getActive()) {
            throw new ValidationException(ValidationError.COM001, "The Invitation is Not Active ,Click Status to fetch Latest Data");
        }
        String helpdesk = helpdeskNo;
        String mobile = invitees.getPhone();
        String email = invitees.getEmail();
        String custName = invitees.getName();
        String expiry = CommonUtils.ConvertDate(invitees.getExpiryDate(), "dd-MM-yyyy HH:mm:ss");
        if (invitees.getPhone() != null && false) {
            SMSEmailDTO smdDto = new SMSEmailDTO();
            smdDto.setAlertId("LOSLGL");
            smdDto.setSlno(dto.getSlno());
            smdDto.setWiNum(dto.getWiNum());
            smdDto.setSentUser(usd.getPPCNo());
            //smdDto.setMobile(mobile);
            smdDto.setReqType("S");
            if (mailSmsSend) {
                smdDto.setMobile(mobile);
            } else {
                smdDto.setMobile("918547016003");
            }
            smdDto.setMessage(custName + "|" + invitees.getSignUrl() + "|" + expiry + "|" + helpdesk + "|");
            ResponseDTO sms = smsEmailService.insertSMSEmail(smdDto);
            if (sms.getStatus().equalsIgnoreCase("F")) return sms;
        }
        if (invitees.getEmail() != null) {
            SMSEmailDTO emailDTO = new SMSEmailDTO();
            emailDTO.setSlno(dto.getSlno());
            emailDTO.setWiNum(dto.getWiNum());
            emailDTO.setSentUser(usd.getPPCNo());
            emailDTO.setAlertId("LOSLGL");
            emailDTO.setReqType("E");
            emailDTO.setEmailFrom("sibmailer@sib.bank.in");
            if (!devMode) {
                emailDTO.setEmailTo(invitees.getEmail());
            } else {
                emailDTO.setEmailTo("antonyraj@sib.bank.in");
                // emailDTO.setEmailTo("vigneshpadmanabhan@sib.bank.in");
            }
            emailDTO.setEmailBody("You’ve chosen digital documentation. To complete the process,please click <a href='" + invitees.getSignUrl() + "' target='_blank'>here</a> which is valid until " + expiry + ".");
            emailDTO.setCustName(custName);
            emailDTO.setEmailSubject("Digital Documentation - South Indian Bank");
            ResponseDTO email_ = smsEmailService.insertSMSEmail(emailDTO);
            if (email_.getStatus().equalsIgnoreCase("F"))
                return new ResponseDTO("F", "SMS Sent Successfully ,Email  Failed");
        }
        return new ResponseDTO("S", "Invitation Resented Successfully");
    }

    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveReturn(WaiverDto.CompleteDto dto, HttpServletRequest request) {
        Long Slno = dto.getSlno();
        String toQueue = "", status = "", fromQueue = "BD";
        VehicleLoanMaster vms = masterService.findById(dto.getSlno());
        if (vms.getDocMode() != null) {
            throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for Documentation");
        }
        switch (dto.getDecision()) {
            case "NIL":
                toQueue = dto.getDecision();
                vms.setStatus("BDREJ");
                vms.setActiveFlg("N");
                vms.setRejFlg("Y");
                vms.setRejUser(usd.getPPCNo());
                vms.setRejDate(new Date());
                vms.setRejQueue(fromQueue);
                vms.setSancModRequired(null);
                break;
            case "BS":
                toQueue = dto.getDecision();
                vehicleLoanWaiverService.rejectPendingTasksForWorkItem(dto.getWiNum(), "Rejected as WI sendback");
                vms.setStatus("BDREJ");
                vms.setSanFlg("N");
                vms.setSancModRequired(null);
                break;
            default:
                throw new RuntimeException("Invalid Type");

        }
        masterService.saveLoan(vms);
        vehicleLoanQueueDetailsService.createQueueEntry(dto.getWiNum(), Slno, dto.getDocRemarks(), usd.getPPCNo(), fromQueue, toQueue);
        masterService.updateQueue(Slno, toQueue, status, usd.getPPCNo());
        lockService.ReleaseLock(Slno, usd.getPPCNo());
        tatService.updateTat(Slno, usd.getPPCNo(), dto.getWiNum(), toQueue);
        if ("BS".equals(dto.getDecision())) {
            VehicleLoanSubqueueTask vehicleLoanSubqueueTask = taskService.getSubTaskByTypeAndStatus(vms.getSlno(), "CIF_CREATION", "PENDING");
            if (vehicleLoanSubqueueTask != null) {
                vehicleLoanSubqueueTask.setStatus("RECALLED");
                vehicleLoanSubqueueTask.setCompletedUser(usd.getPPCNo());
                vehicleLoanSubqueueTask.setCompletedDate(new Date());
                taskService.saveSubTask(vehicleLoanSubqueueTask);

                subqueueTatService.updateTat(vehicleLoanSubqueueTask.getTaskId(), usd.getPPCNo(), dto.getWiNum(), "CIF_CREATION", "RECALL", "RECALL", request);
            }
        }
        return new ResponseDTO("S", "Record Saved Successfully (action : " + status + ") & Moved to " + toQueue);
    }


    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveDocument(WaiverDto.CompleteDto dto) throws Exception {
        VehicleLoanMaster master = masterService.findById(dto.getSlno());

        if (!master.isDocCompleted()) {
            throw new ValidationException(ValidationError.COM001, "Kindly Complete Documentation");
        }
        if (master.getQueue().equalsIgnoreCase("AC")) {
            throw new ValidationException(ValidationError.COM001, "Request Already Submitted");
        }
        if (!master.getRepaymentStatus()) {
            throw new ValidationException(ValidationError.COM001, "Kindly Complete Repayment Details");
        }
//        if(master.getRoiRequested() && taskService.isPending(dto.getSlno(),"CHARGE_WAIVER")){
//            throw new ValidationException(ValidationError.COM001,"Processing Fee Waiver Not Completed");
//        }
        if (master.getApplicants().stream().anyMatch(t -> t.getSibCustomer().equalsIgnoreCase("N") && t.getDelFlg().equalsIgnoreCase("N"))) {
            throw new ValidationException(ValidationError.COM001, "Kindly Create Customer ID for All Customers!");
        }
        if (!master.getApplicants().stream().anyMatch(t -> !t.getApplicantType().equalsIgnoreCase("C") && t.getDelFlg().equalsIgnoreCase("N")) && !"001".equalsIgnoreCase(dto.getModeofoper())) {
            throw new ValidationException(ValidationError.COM001, "Kindly Select Mode of Operation As SELF (No Co-Applicants)");
        }
        if (master.getDocMode().equalsIgnoreCase("M") && !manDocService.isDocDate(dto.getSlno(), dto.getDocDate())) {
            throw new ValidationException(ValidationError.COM001, "The Documentation  date should be greater or equal to download date and less than or equal to upload date");
        }
        if (master.getDocMode().equalsIgnoreCase("D")) {
            dto.setDocDate(legalityService.getCompletedDate(dto.getSlno()));
        }
        TabResponse tb = bpmService.BpmUpload(bpmService.bpmRequest(dto.getWiNum(), dto.getDisbursementInst(), "N", "NA", "DISBURSEMENT_INSTRUCTION", "pdf"));
        if (!"S".equalsIgnoreCase(tb.getStatus())) {
            throw new ValidationException(ValidationError.COM001, tb.getMsg());
        }
        if ("Y".equalsIgnoreCase(dto.getMarginreq())) {
            tb = bpmService.BpmUpload(bpmService.bpmRequest(dto.getWiNum(), dto.getMarginfile(), "N", "NA", "MARGIN_RECEIPT", "pdf"));
            if (!"S".equalsIgnoreCase(tb.getStatus())) {
                throw new ValidationException(ValidationError.COM001, tb.getMsg());
            }
        }
        if (dto.getRtoform() != null) {
            tb = bpmService.BpmUpload(bpmService.bpmRequest(dto.getWiNum(), dto.getRtoform(), "N", "NA", "RTO_FORM", "pdf"));
            if (!"S".equalsIgnoreCase(tb.getStatus())) {
                throw new ValidationException(ValidationError.COM001, tb.getMsg());
            }
        }
        DocumentRequest documentRequest = new DocumentRequest();
        documentRequest.setCmUser(usd.getPPCNo());
        documentRequest.setSlNo(String.valueOf(dto.getSlno()));
        documentRequest.setWiNum(dto.getWiNum());
        String pdf = docservice.getSanctionPdf(documentRequest);
        tb = bpmService.BpmUpload(bpmService.bpmRequest(dto.getWiNum(), pdf, "N", "NA", "SANCTION_LETTER", "pdf"));
        if (!"S".equalsIgnoreCase(tb.getStatus())) {
            throw new ValidationException(ValidationError.COM001, tb.getMsg());
        }
        master.setDocCompDate(CommonUtils.DateConvert(dto.getDocDate(), "yyyy-MM-dd"));
        master.setModeOper(dto.getModeofoper());
        master.setMarginReceipt(dto.getMarginreq());
        master.setBrDocCmUser(usd.getPPCNo());
        master.setBrDocCmDate(new Date());
        masterService.saveLoan(master);
        vehicleLoanQueueDetailsService.createQueueEntry(dto.getWiNum(), dto.getSlno(), dto.getDocRemarks(), usd.getPPCNo(), "BD", "ACOPN");
        masterService.updateQueue(dto.getSlno(), "ACOPN", "BD COMPLETED", usd.getPPCNo());
        lockService.ReleaseLock(dto.getSlno(), usd.getPPCNo());
        tatService.updateTat(dto.getSlno(), usd.getPPCNo(), dto.getWiNum(), "ACOPN");
        return new ResponseDTO("S", " Request Saved Successfully");
    }

    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveCif(WaiverDto.CifCreationDto creationDto, HttpServletRequest request) throws Exception {
        VehicleLoanMaster master = masterService.findById(creationDto.getSlno());


        VehicleLoanSubqueueTask task = taskService.getSubTaskByTypeAndStatus(creationDto.getSlno(), "VKYC", "PENDING");
//        if (task == null) {
//            task = taskService.getSubTaskByTypeAndStatus(creationDto.getSlno(), "VKYC", "PENDING");
//        }
        switch (creationDto.getCifwaiveRequired()) {
            case "Y":
                if (task != null && task.isPending()) {
                    throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for CIF ID Creation(PENDING)");
                }
                if (taskService.isPending(creationDto.getSlno(), "CIF_CREATION")) {
                    throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for CIF ID Creation(PENDING)");
                }
                List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();


                if (master != null) {
                    vehicleLoanApplicants = master.getApplicants().stream().filter(t -> t.getSibCustomer().equalsIgnoreCase("N") && t.getDelFlg().equalsIgnoreCase("N")).toList();
                }
                for (VehicleLoanApplicant vlapp : vehicleLoanApplicants) {
                    if (creationDto.getCifMode().equalsIgnoreCase("V")) {
                        String helpdesk = helpdeskNo;
                        String mobile = vlapp.getBasicapplicants().getMobileCntryCode() + vlapp.getBasicapplicants().getMobileNo();
                        String email = vlapp.getBasicapplicants().getEmailId();
                        String custName = vlapp.getBasicapplicants().getApplicantName();
                        LocalDateTime currentDate = LocalDateTime.now();
                        LocalDateTime newDate = currentDate.plusDays(3);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String expiry = newDate.format(formatter);

                        String data = vlapp.getApplicantId() + "|" + expiry;
                        String hashCode = aesUtil.generateUniqueHash(data);
                        String vkycLink = hashCode;
                        // String vkycLink="https://onlineuat.southindianbank.com/losvkyc/"+hashCode;
                        SMSEmailDTO smdDto = new SMSEmailDTO();
                        smdDto.setAlertId("LOSVKYC");
                        smdDto.setSlno(creationDto.getSlno());
                        smdDto.setWiNum(creationDto.getWiNum());
                        smdDto.setSentUser(usd.getPPCNo());
                        ;
                        smdDto.setReqType("S");
                        smdDto.setAppid(vlapp.getApplicantId());
                        smdDto.setHashCode(hashCode);
                        if (!devMode) {
                            smdDto.setMobile(mobile);
                        } else {
                            smdDto.setMobile("918547016003");
                        }
                        smdDto.setMessage(custName + "|" + vkycLink + "|" + helpdesk + "|");
                        ResponseDTO sms = smsEmailService.insertSMSEmail(smdDto);
                        if (sms.getStatus().equalsIgnoreCase("F")) return sms;
                        SMSEmailDTO emailDTO = new SMSEmailDTO();
                        emailDTO.setAlertId("LOSVKYC");
                        emailDTO.setSlno(creationDto.getSlno());
                        emailDTO.setWiNum(creationDto.getWiNum());
                        emailDTO.setSentUser(usd.getPPCNo());
                        ;
                        emailDTO.setReqType("E");
                        emailDTO.setEmailFrom("sibmailer@sib.bank.in");
                        emailDTO.setAppid(vlapp.getApplicantId());
                        emailDTO.setHashCode(hashCode);
                        if (!devMode) {
                            emailDTO.setEmailTo(email);
                            vkycLink = "https://online.southindianbank.com/losvkyc/" + hashCode;
                        } else {
                            emailDTO.setEmailTo("antonyraj@sib.bank.in");
                            vkycLink = "https://onlineuat.southindianbank.com/losvkyc/" + hashCode;
                        }

                        emailDTO.setEmailBody("Your VKYC verification is now Available.<br/>Please click <a href='" + vkycLink + "' target='_blank'>here</a> to proceed");
                        emailDTO.setCustName(custName);
                        emailDTO.setEmailSubject("Message Sent to" + email);
                        ResponseDTO email_ = smsEmailService.insertSMSEmail(emailDTO);
                        if (email_.getStatus().equalsIgnoreCase("F")) return email_;
                    }
//                    else{
//                        if ( taskService.isPending(creationDto.getSlno(),"CIF_CREATION")) {
//                            throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for CIF ID Creation(PENDING)");
//                        }
//                    }

                    task = new VehicleLoanSubqueueTask();
                    task.setSlno(creationDto.getSlno());
                    task.setWiNum(creationDto.getWiNum());
                    task.setCreatedDate(new Date());
                    task.setCreateUser(usd.getPPCNo());
                    task.setCreateSol(usd.getSolid());
                    task.setTaskType(creationDto.getCifMode().equalsIgnoreCase("M") ? "CIF_CREATION" : "VKYC");
                    task.setStatus("PENDING");
                    task.setRemarks(creationDto.getCifwaiverRemarks());
                    task.setCompletedDate(null);
                    task.setCompletedUser(null);
                    task.setApplicant(vlapp);
                    task.setLockFlg("N");
                    taskService.saveSubTask(task);
                    subqueueTatService.initialInsert(task.getTaskId(), usd.getPPCNo(), creationDto.getWiNum(), creationDto.getCifMode().equalsIgnoreCase("M") ? "CIF_CREATION" : "VKYC", request);
                }
                return new ResponseDTO("S", " CIF Create Request Saved Successfully");
            case "RECALL":
                String cifMode = creationDto.getCifMode().equalsIgnoreCase("M") ? "CIF_CREATION" : "VKYC";
                if (!taskService.isPending(creationDto.getSlno(), cifMode)) {
                    //    throw new ValidationException(ValidationError.COM001,"The WI cant be recalled since it is Already COMPLETED/REJECTED/RECALLED");
                    throw new ValidationException(ValidationError.COM001, "The record is not in PENDING status, so recall action is not necessary.");
                } else if (taskService.isPendingAndLocked(creationDto.getSlno(), "CIF_CREATION")) {
                    throw new ValidationException(ValidationError.COM001, "The SubTask cant be Recalled since it's Currently Locked By User :" + task.getLockedBy());
                } else {
                    List<VehicleLoanApplicant> vehicleLoanApplicant1 = new ArrayList<>();


                    if (master != null) {
                        vehicleLoanApplicant1 = master.getApplicants().stream().filter(t -> t.getSibCustomer().equalsIgnoreCase("N") && t.getDelFlg().equalsIgnoreCase("N")).toList();
                    }
                    List<VehicleLoanSubqueueTask> ciftask = taskService.getBySlno(creationDto.getSlno()).stream().filter(t -> t.getTaskType().equalsIgnoreCase(cifMode) && t.isPending()).toList();
                    for (VehicleLoanSubqueueTask sub : ciftask) {
                        sub.setStatus("RECALLED");
                        sub.setCompletedDate(new Date());
                        sub.setCompletedUser(usd.getPPCNo());
                        sub.setRemarks("WI Recalled");
                        taskService.saveSubTask(sub);
                        subqueueTatService.updateTat(sub.getTaskId(), usd.getPPCNo(), creationDto.getWiNum(), cifMode, "RECALL", null, request);
                    }
                    masterService.saveLoan(master);

                    return new ResponseDTO("S", "CIF CREATION  Recalled Successfully");
                }
            default:
                throw new RuntimeException("Invalid Type");
        }
    }


    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveFeeWaiver(WaiverDto.ProcessFeeWaiverDto waiverDto, HttpServletRequest request) {

        VehicleLoanMaster master = masterService.findById(waiverDto.getSlno());
        if (master.getDocMode() != null) {
            throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for Documentation");
        }
        VehicleLoanSubqueueTask task = taskService.getSubTaskByTypeAndStatus(waiverDto.getSlno(), "CHARGE_WAIVER", "PENDING");
        switch (waiverDto.getFeewaiveRequired()) {
            case "Y":
                if (task != null && task.isPending()) {
                    throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for Fee Waiver(PENDING)");
                }
                task = new VehicleLoanSubqueueTask();
                task.setSlno(waiverDto.getSlno());
                task.setWiNum(waiverDto.getWiNum());
                task.setDecision(waiverDto.getDecision());
                task.setTaskType("CHARGE_WAIVER");
                task.setCreatedDate(new Date());
                task.setCreateUser(usd.getPPCNo());
                task.setCreateSol(usd.getSolid());
                task.setStatus("PENDING");
                task.setCompletedDate(null);
                task.setCompletedUser(null);
                task.setLockFlg("N");
                taskService.saveSubTask(task);
                subqueueTatService.initialInsert(task.getTaskId(), usd.getPPCNo(), waiverDto.getWiNum(), "CHARGE_WAIVER", request);
                feeWaiverRepository.updateDelflag(waiverDto.getSlno());
                for (WaiverDto.FeeData feeData : waiverDto.getFeeData()) {
                    VehicleLoanChargeWaiver chg = new VehicleLoanChargeWaiver();
                    chg.setFeeName(feeData.getFeeName());
                    chg.setFeeCode(feeData.getFeeCode());
                    chg.setFeeValueRec(feeData.getFeeValueRec());
                    chg.setFeeValue(feeData.getFeeValue());
                    chg.setTaskId(task.getTaskId());
                    chg.setFeeRemarks(waiverDto.getFeewaiverRemarks());
                    chg.setDecision(waiverDto.getDecision());
                    chg.setSlno(waiverDto.getSlno());
                    chg.setWiNum(waiverDto.getWiNum());
                    chg.setWaiverFlg(feeData.getFeeWaiverFlag());
                    chg.setFinalFee(feeData.getFeeValue());
                    chg.setLastModDate(new Date());
                    chg.setLastModUser(usd.getPPCNo());
                    chg.setLastModSol(usd.getSolid());
                    chg.setFrequency(feeData.getFrequency());
                    chg.setDelFlag("N");
                    feeWaiverRepository.save(chg);
                }
                master.setChargeWaiverRequested(true);
                masterService.saveLoan(master);
                return new ResponseDTO("S", " Waiver Saved Successfully");
            case "N":
                if (task == null || task.isCompleted() || task.isRejected() || task.isRecalled()) {
                    task = new VehicleLoanSubqueueTask();
                    task.setSlno(waiverDto.getSlno());
                    task.setWiNum(waiverDto.getWiNum());
                    task.setDecision(null);
                    task.setTaskType("CHARGE_WAIVER");
                    task.setCreatedDate(new Date());
                    task.setCreateUser(usd.getPPCNo());
                    task.setCreateSol(usd.getSolid());
                    task.setStatus("BRCOMPLETED");
                    task.setRemarks("FEE WAIVER NOT REQUIRED");
                    task.setCompletedDate(new Date());
                    task.setCompletedUser(usd.getPPCNo());
                    task.setLockFlg("N");
                    taskService.saveSubTask(task);
                    feeWaiverRepository.updateDelflag(waiverDto.getSlno());
                    for (WaiverDto.FeeData feeData : waiverDto.getFeeData()) {
                        VehicleLoanChargeWaiver chg = new VehicleLoanChargeWaiver();
                        chg.setFeeName(feeData.getFeeName());
                        chg.setFeeCode(feeData.getFeeCode());
                        chg.setFeeValueRec(null);
                        chg.setFeeValue(feeData.getFeeValue());
                        chg.setTaskId(task.getTaskId());
                        chg.setFeeRemarks(null);
                        chg.setDecision(null);
                        chg.setSlno(waiverDto.getSlno());
                        chg.setWiNum(waiverDto.getWiNum());
                        chg.setWaiverFlg(feeData.getFeeWaiverFlag());
                        chg.setFinalFee(feeData.getFeeValue());
                        chg.setLastModDate(new Date());
                        chg.setLastModUser(usd.getPPCNo());
                        chg.setLastModSol(usd.getSolid());
                        chg.setDelFlag("N");
                        feeWaiverRepository.save(chg);
                    }
                    master.setChargeWaiverRequested(false);
                    masterService.saveLoan(master);
                    return new ResponseDTO("S", "Record Saved Successfully");
                } else {
                    throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for Waiver(PENDING)");
                }
            case "RECALL":
                if (task == null || task.isCompleted() || task.isRejected() || task.isRecalled()) {
                    //    throw new ValidationException(ValidationError.COM001,"The WI cant be recalled since it is Already COMPLETED/REJECTED/RECALLED");
                    throw new ValidationException(ValidationError.COM001, "The record is not in PENDING status, so recall action is not necessary.");
                } else if (task.isLocked()) {
                    throw new ValidationException(ValidationError.COM001, "The SubTask cant be Recalled since it's Currently Locked By User :" + task.getLockedBy());
                } else {
                    task.setStatus("RECALLED");
                    task.setCompletedDate(new Date());
                    task.setCompletedUser(usd.getPPCNo());
                    task.setRemarks("WI Recalled");
                    taskService.saveSubTask(task);
                    subqueueTatService.updateTat(task.getTaskId(), usd.getPPCNo(), waiverDto.getWiNum(), "CHARGE_WAIVER", "RECALL", null, request);
                    master.setChargeWaiverRequested(false);
                    masterService.saveLoan(master);
                    return new ResponseDTO("S", "Waiver Recalled Successfully");
                }
            default:
                throw new RuntimeException("Invalid Type");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveRoiWaiver(WaiverDto.ROIWaiverDto waiverDto, HttpServletRequest request) {
        VehicleLoanMaster master = masterService.findById(waiverDto.getSlno());
        if (master.getDocMode() != null) {
            throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for Documentation");
        }
        VehicleLoanSubqueueTask task = taskService.getSubTaskByTypeAndStatus(waiverDto.getSlno(), "ROI_WAIVER", "PENDING");
        switch (waiverDto.getRoiwaiveRequired()) {
            case "Y":
                VehicleLoanRoiWaiver roiWaiver = null;
                if (task != null && task.isPending()) {
                    throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for ROI Waiver(PENDING)");
                }
                task = new VehicleLoanSubqueueTask();
                task.setSlno(waiverDto.getSlno());
                task.setWiNum(waiverDto.getWiNum());
                roiWaiver = new VehicleLoanRoiWaiver();
                BigDecimal modifiedRoi = waiverDto.getEbr().add(waiverDto.getBaseSpread());
                //BigDecimal modifiedRoi = waiverDto.getOperationalCost().add(waiverDto.getCrp()).add(waiverDto.getEbr()).add(waiverDto.getBaseSpread());
                if (modifiedRoi.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ValidationException(ValidationError.COM001, "The Revised ROI should be greater than ZERO");
                }

                BigDecimal modifiedemi = eligibilityHelperService.calculateEmi(modifiedRoi, waiverDto.getSancAmt(), waiverDto.getSanctenor());
                if (modifiedRoi.compareTo(waiverDto.getSancRoi()) >= 0) {
                    throw new ValidationException(ValidationError.COM001, "The Revised ROI should not be greater than or equal to the sanctioned ROI");
                }
                BigDecimal spread = modifiedRoi.subtract(waiverDto.getSancRoi()).add(waiverDto.getSpread());

                if ("FIXED".equals(waiverDto.getRoiType()) && spread.compareTo(BigDecimal.ZERO) < 0) {
                    throw new ValidationException(ValidationError.COM001, "Value of spread should not be negative For Fixed ROI Type");
                }

                roiWaiver.setRevisedRoi(modifiedRoi);
                roiWaiver.setRevisedEmi(modifiedemi);
                roiWaiver.setSancRoi(waiverDto.getSancRoi());
                roiWaiver.setInitialRoi(waiverDto.getInitialRoi());
                roiWaiver.setSancAmount(waiverDto.getSancAmt());
                roiWaiver.setRoiType(waiverDto.getRoiType());
                roiWaiver.setSanctenor(waiverDto.getSanctenor());
                roiWaiver.setSancemi(waiverDto.getSancemi());
                roiWaiver.setOperationalCost(waiverDto.getOperationalCost());
                roiWaiver.setEbr(waiverDto.getEbr());
                roiWaiver.setCrp(waiverDto.getCrp());
                roiWaiver.setSpread(spread);
                roiWaiver.setBaseSpread(waiverDto.getBaseSpread());
                roiWaiver.setDecision(waiverDto.getDecision());
                roiWaiver.setStp(waiverDto.getStp());
                roiWaiver.setRoiwaiverRemarks(waiverDto.getRoiwaiverRemarks());
                roiWaiver.setLastModDate(new Date());
                roiWaiver.setLastModUser(usd.getPPCNo());
                roiWaiver.setLastModSol(usd.getSolid());
                roiWaiver.setSlno(waiverDto.getSlno());
                roiWaiver.setWiNum(waiverDto.getWiNum());
                task.setCompletedDate(null);
                task.setCompletedUser(null);
                task.setDecision(waiverDto.getDecision());
                task.setTaskType("ROI_WAIVER");
                task.setCreatedDate(new Date());
                task.setCreateUser(usd.getPPCNo());
                task.setCreateSol(usd.getSolid());
                task.setStatus("PENDING");
                task.setLockFlg("N");
                taskService.saveSubTask(task);
                subqueueTatService.initialInsert(task.getTaskId(), usd.getPPCNo(), waiverDto.getWiNum(), "ROI_WAIVER", request);
                roiWaiver.setTaskId(task.getTaskId());
                vehicleLoanRoiWaiverRepository.save(roiWaiver);
                master.setRoiRequested(true);
                masterService.saveLoan(master);
                return new ResponseDTO("S", "ROI Waiver Saved Successfully");
            case "N":
                if (task == null || task.isCompleted() || task.isRejected() || task.isRecalled()) {
                    if (task != null && (task.isCompleted() || task.isRejected() || task.isRecalled()))
                        subqueueTatService.updateTat(task.getTaskId(), usd.getPPCNo(), task.getWiNum(), "ROI_WAIVER", "NA", "ROI_WAIVER", request);
                    task = new VehicleLoanSubqueueTask();
                    task.setSlno(waiverDto.getSlno());
                    task.setWiNum(waiverDto.getWiNum());
                    task.setDecision(null);
                    task.setTaskType("ROI_WAIVER");
                    task.setCreatedDate(new Date());
                    task.setCreateUser(usd.getPPCNo());
                    task.setCreateSol(usd.getSolid());
                    task.setStatus("BRCOMPLETED");
                    task.setRemarks("ROI WAIVER NOT REQUIRED");
                    task.setCompletedDate(new Date());
                    task.setCompletedUser(usd.getPPCNo());
                    task.setLockFlg("N");
                    taskService.saveSubTask(task);
                    if (task == null)
                        subqueueTatService.updateTat(task.getTaskId(), usd.getPPCNo(), task.getWiNum(), "ROI_WAIVER", "NA", "ROI_WAIVER", request);
                    master.setRoiRequested(false);
                    masterService.saveLoan(master);
                    return new ResponseDTO("S", "ROI Waiver Saved Successfully");
                } else {
                    throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for ROI Waiver(PENDING)");
                }
            case "RECALL":
                if (task == null || task.isCompleted() || task.isRejected() || task.isRecalled()) {
                    // throw new ValidationException(ValidationError.COM001,"The WI cant be recalled since it is Already COMPLETED/REJECTED/RECALLED");
                    throw new ValidationException(ValidationError.COM001, "The record is not in PENDING status, so recall action is not necessary.");
                } else if (task.isLocked()) {
                    throw new ValidationException(ValidationError.COM001, "The SubTask cant be Recalled since it's Currently Locked By User :" + task.getLockedBy());
                } else {
                    subqueueTatService.updateTat(task.getTaskId(), usd.getPPCNo(), task.getWiNum(), "ROI_WAIVER", "RECALL", "ROI_WAIVER", request);
                    task.setStatus("RECALLED");
                    task.setCompletedDate(new Date());
                    task.setCompletedUser(usd.getPPCNo());
                    task.setRemarks("WI Recalled");
                    taskService.saveSubTask(task);
                    master.setRoiRequested(false);
                    masterService.saveLoan(master);
                    return new ResponseDTO("S", "ROI Waiver Recalled Successfully");
                }
            default:
                throw new RuntimeException("Invalid Type");
        }


    }

    @Override
    public ResponseDTO updateWaiver(WaiverDto waiverDto, HttpServletRequest request) throws Exception {
        switch (waiverDto.getWaiverType()) {
            case "ROI":
                return updateROIWaiver(waiverDto.getRoidto(), request);
            case "CHARGE":
                return updateChargeWaiver(waiverDto.getProcessFeeWaiverDto(), request);
            default:
                throw new RuntimeException("Invalid Waiver Type");
        }
    }

    @Override
    public ResponseDTO saveDoc(DocumentRequest documentRequest) throws Exception {
        return null;
    }

    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO updateChargeWaiver(WaiverDto.ProcessFeeWaiverDto processFeeWaiverDto, HttpServletRequest request) {
        try {
            VehicleLoanSubqueueTask task = taskService.getSubTaskByTypeAndStatus(processFeeWaiverDto.getSlno(), "CHARGE_WAIVER", "PENDING");
            if (task == null) {
                throw new ValidationException(ValidationError.COM001, "No waiver task found");
            }
            if (processFeeWaiverDto.getDecision().startsWith("PC")) {
                ResponseDTO response = forwardChargeWaiver(task, processFeeWaiverDto);
                subqueueTatService.updateTat(task.getTaskId(), usd.getPPCNo(), task.getWiNum(), "CHARGE_WAIVER", "FORWARD", processFeeWaiverDto.getDecision(), request);
                return response;
            } else if ("SANCTION".equals(processFeeWaiverDto.getDecision())) {
                ResponseDTO response = sanctioChargeWaiver(task, processFeeWaiverDto);
                subqueueTatService.updateTat(task.getTaskId(), usd.getPPCNo(), task.getWiNum(), "CHARGE_WAIVER", "SANCTION", null, request);
                return response;
            } else if ("REJECT".equals(processFeeWaiverDto.getDecision())) {
                ResponseDTO response = rejectChargeWaiver(task, processFeeWaiverDto);
                subqueueTatService.updateTat(task.getTaskId(), usd.getPPCNo(), task.getWiNum(), "CHARGE_WAIVER", "REJECT", null, request);
                return response;
            } else {
                throw new ValidationException(ValidationError.COM001, "Invalid decision");
            }
        } catch (Exception e) {
            log.error("Some exception occured", e);
        }
        return null;
    }

    private ResponseDTO rejectChargeWaiver(VehicleLoanSubqueueTask task, WaiverDto.ProcessFeeWaiverDto processFeeWaiverDto) {
        task.setStatus("REJECTED");
        task.setDecision(processFeeWaiverDto.getDecision());
        task.setCompletedDate(new Date());
        task.setCompletedUser(usd.getPPCNo());
        task.setRemarks(processFeeWaiverDto.getFeewaiverRemarks());
        taskService.saveSubTask(task);
        List<VehicleLoanChargeWaiver> chargeWaivers = feeWaiverRepository.findByTaskId(task.getTaskId());
        for (WaiverDto.FeeData feeData : processFeeWaiverDto.getFeeData()) {
            VehicleLoanChargeWaiver chargeWaiver = chargeWaivers.stream()
                    .filter(cw -> cw.getFeeCode().equals(feeData.getFeeCode()))
                    .findFirst()
                    .orElse(null);
            if (chargeWaiver != null) {
                chargeWaiver.setDecision(processFeeWaiverDto.getDecision());
                chargeWaiver.setFeeWaiverSancRemarks(processFeeWaiverDto.getFeeWaiverSancRemarks());
                chargeWaiver.setLastModDate(new Date());
                chargeWaiver.setFinalFee(chargeWaiver.getFeeValue());
                chargeWaiver.setLastModUser(usd.getPPCNo());
                chargeWaiver.setLastModSol(usd.getSolid());
                feeWaiverRepository.save(chargeWaiver);
            } else {
                log.warn("Charge waiver not found for fee code: {}", feeData.getFeeCode());
            }
        }
        return new ResponseDTO("S", "Waiver Rejected");
    }

    private ResponseDTO sanctioChargeWaiver(VehicleLoanSubqueueTask task, WaiverDto.ProcessFeeWaiverDto processFeeWaiverDto) {
        task.setStatus("COMPLETED");
        task.setDecision(processFeeWaiverDto.getDecision());
        task.setCompletedDate(new Date());
        task.setCompletedUser(usd.getPPCNo());
        task.setRemarks(processFeeWaiverDto.getFeewaiverRemarks());
        taskService.saveSubTask(task);
        List<VehicleLoanChargeWaiver> chargeWaivers = feeWaiverRepository.findByTaskId(task.getTaskId());
        for (WaiverDto.FeeData feeData : processFeeWaiverDto.getFeeData()) {
            VehicleLoanChargeWaiver chargeWaiver = chargeWaivers.stream()
                    .filter(cw -> cw.getFeeCode().equals(feeData.getFeeCode()))
                    .findFirst()
                    .orElse(null);

            if (chargeWaiver != null && "Y".equals(feeData.getFeeWaiverFlag())) {
                chargeWaiver.setFeeWaiverSancRemarks(processFeeWaiverDto.getFeeWaiverSancRemarks());
                chargeWaiver.setDecision(processFeeWaiverDto.getDecision());
                chargeWaiver.setFeeSancValue(feeData.getFeeValueSanc());
                chargeWaiver.setFinalFee(feeData.getFeeValueSanc());
                chargeWaiver.setLastModDate(new Date());
                chargeWaiver.setLastModUser(usd.getPPCNo());
                chargeWaiver.setLastModSol(usd.getSolid());
                feeWaiverRepository.save(chargeWaiver);
            }
        }
        return new ResponseDTO("S", "Waiver forwarded to higher level checker");
    }

    private ResponseDTO forwardChargeWaiver(VehicleLoanSubqueueTask task, WaiverDto.ProcessFeeWaiverDto processFeeWaiverDto) {
        task.setStatus("PENDING");
        task.setDecision(processFeeWaiverDto.getDecision());
        task.setRemarks(processFeeWaiverDto.getFeewaiverRemarks());
        taskService.saveSubTask(task);
        List<VehicleLoanChargeWaiver> chargeWaivers = feeWaiverRepository.findByTaskId(task.getTaskId());

        for (WaiverDto.FeeData feeData : processFeeWaiverDto.getFeeData()) {
            VehicleLoanChargeWaiver chargeWaiver = chargeWaivers.stream()
                    .filter(cw -> cw.getFeeCode().equals(feeData.getFeeCode()))
                    .findFirst()
                    .orElse(null);

            if (chargeWaiver != null) {
                chargeWaiver.setDecision(processFeeWaiverDto.getDecision());
                chargeWaiver.setFeeWaiverSancRemarks(processFeeWaiverDto.getFeeWaiverSancRemarks());
                chargeWaiver.setFeeSancValue(feeData.getFeeValueSanc());
                chargeWaiver.setLastModDate(new Date());
                chargeWaiver.setLastModUser(usd.getPPCNo());
                chargeWaiver.setLastModSol(usd.getSolid());
                feeWaiverRepository.save(chargeWaiver);
            } else {
                log.warn("Charge waiver not found for fee code: {}", feeData.getFeeCode());
            }
        }

        return new ResponseDTO("S", "Waiver forwarded to higher level checker");
    }

    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO updateROIWaiver(WaiverDto.ROIWaiverDto waiverDto, HttpServletRequest request) {
        try {
            VehicleLoanSubqueueTask task = taskService.getSubTaskByTypeAndStatus(waiverDto.getSlno(), "ROI_WAIVER", "PENDING");

            if (task == null) {
                return new ResponseDTO("F", "No waiver task found");
            }

            if (waiverDto.getDecision().startsWith("RI")) {
                ResponseDTO response = forwardWaiver(task, waiverDto);
                if (response.getStatus().equals("S")) {
                    subqueueTatService.updateTat(task.getTaskId(), usd.getPPCNo(), task.getWiNum(), "ROI_WAIVER", "FORWARD", waiverDto.getDecision(), request);
                }
                return response;

            } else if ("SANCTION".equals(waiverDto.getDecision())) {
                ResponseDTO response = sanctionWaiver(task, waiverDto);
                if (response.getStatus().equals("S")) {
                    subqueueTatService.updateTat(task.getTaskId(), usd.getPPCNo(), task.getWiNum(), "ROI_WAIVER", "SANCTION", null, request);
                }
                return response;

            } else if ("REJECT".equals(waiverDto.getDecision())) {
                ResponseDTO response = rejectWaiver(task, waiverDto);
                if (response.getStatus().equals("S")) {
                    subqueueTatService.updateTat(task.getTaskId(), usd.getPPCNo(), task.getWiNum(), "ROI_WAIVER", "REJECT", null, request);
                }
                return response;

            } else {
                throw new ValidationException(ValidationError.COM001, "Invalid decision");
            }
        } catch (Exception e) {
            log.error("Some exception occured", e);
        }
        return null;
    }

    private ResponseDTO rejectWaiver(VehicleLoanSubqueueTask task, WaiverDto.ROIWaiverDto waiverDto) {
        task.setStatus("REJECTED");
        task.setDecision(waiverDto.getDecision());
        task.setCompletedDate(new Date());
        task.setCompletedUser(usd.getPPCNo());
        task.setRemarks(waiverDto.getRoiwaiverRemarks());
        taskService.saveSubTask(task);
        Optional<VehicleLoanRoiWaiver> roiWaiverOpt = vehicleLoanRoiWaiverRepository.findByTaskId(task.getTaskId());
        if (roiWaiverOpt.isPresent()) {
            VehicleLoanRoiWaiver roiWaiver = roiWaiverOpt.get();
            roiWaiver.setDecision(waiverDto.getDecision());
            roiWaiver.setRoiwaiverSancRemarks(waiverDto.getRoiwaiverSancRemarks());
            roiWaiver.setLastModDate(new Date());
            roiWaiver.setLastModUser(usd.getPPCNo());
            roiWaiver.setLastModSol(usd.getSolid());
            vehicleLoanRoiWaiverRepository.save(roiWaiver);
        }
        return new ResponseDTO("S", "Waiver Rejected");
    }

    private ResponseDTO forwardWaiver(VehicleLoanSubqueueTask task, WaiverDto.ROIWaiverDto waiverDto) {
        BigDecimal modifiedRoi = waiverDto.getRevisedRoi();
        if (modifiedRoi.compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseDTO("F", "The Revised ROI should be greater than ZERO");
        }
        validateMclrForRoiWaiver(modifiedRoi, waiverDto.getSanctenor());

        BigDecimal modifiedemi = eligibilityHelperService.calculateEmi(modifiedRoi, waiverDto.getSancAmt(), waiverDto.getSanctenor());
        if (modifiedRoi.compareTo(waiverDto.getInitialRoi()) >= 0) {
            return new ResponseDTO("F", "The Revised ROI should not be greater than or equal to the Initail sanctioned ROI");
        }
        BigDecimal calculatedBaseSpread = waiverDto.getCrp().add(waiverDto.getOperationalCost()).add(waiverDto.getSpread());
        BigDecimal spread = modifiedRoi.subtract(waiverDto.getSancRoi()).add(calculatedBaseSpread);

        if ("FIXED".equals(waiverDto.getRoiType()) && spread.compareTo(BigDecimal.ZERO) < 0) {
            return new ResponseDTO("F", "Value of spread should not be negative For Fixed ROI Type");
        }
        task.setStatus("PENDING");
        task.setDecision(waiverDto.getDecision());
        task.setRemarks(waiverDto.getRoiwaiverRemarks());
        taskService.saveSubTask(task);
        Optional<VehicleLoanRoiWaiver> roiWaiverOpt = vehicleLoanRoiWaiverRepository.findByTaskId(task.getTaskId());
        if (roiWaiverOpt.isPresent()) {
            VehicleLoanRoiWaiver roiWaiver = roiWaiverOpt.get();
            roiWaiver.setDecision(waiverDto.getDecision());
            roiWaiver.setBaseSpread(waiverDto.getBaseSpread());
            roiWaiver.setSancBaseSpread(waiverDto.getSancBaseSpread());
            roiWaiver.setSpread(spread);
            roiWaiver.setRevisedRoi(modifiedRoi);
            roiWaiver.setRevisedEmi(modifiedemi);
            roiWaiver.setRoiwaiverSancRemarks(waiverDto.getRoiwaiverSancRemarks());
            roiWaiver.setLastModDate(new Date());
            roiWaiver.setLastModUser(usd.getPPCNo());
            roiWaiver.setLastModSol(usd.getSolid());
            vehicleLoanRoiWaiverRepository.save(roiWaiver);
        }
        return new ResponseDTO("S", "Waiver forwarded to higher level checker");
    }

    private ResponseDTO sanctionWaiver(VehicleLoanSubqueueTask task, WaiverDto.ROIWaiverDto waiverDto) {

        BigDecimal modifiedRoi = waiverDto.getRevisedRoi();
        if (modifiedRoi.compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseDTO("F", "The Revised ROI should be greater than ZERO");
        }
        validateMclrForRoiWaiver(modifiedRoi, waiverDto.getSanctenor());

        BigDecimal modifiedemi = eligibilityHelperService.calculateEmi(modifiedRoi, waiverDto.getSancAmt(), waiverDto.getSanctenor());
        if (modifiedRoi.compareTo(waiverDto.getInitialRoi()) >= 0) {
            return new ResponseDTO("F", "The Revised ROI should not be greater than or equal to the Initail sanctioned ROI");
        }
        BigDecimal calculatedBaseSpread = waiverDto.getCrp().add(waiverDto.getOperationalCost()).add(waiverDto.getSpread());
        BigDecimal spread = modifiedRoi.subtract(waiverDto.getSancRoi()).add(calculatedBaseSpread);

        if ("FIXED".equals(waiverDto.getRoiType()) && spread.compareTo(BigDecimal.ZERO) < 0) {
            return new ResponseDTO("F", "Value of spread should not be negative For Fixed ROI Type");
        }
        task.setStatus("COMPLETED");
        task.setDecision(waiverDto.getDecision());
        task.setCompletedDate(new Date());
        task.setCompletedUser(usd.getPPCNo());
        task.setRemarks(waiverDto.getRoiwaiverSancRemarks());
        taskService.saveSubTask(task);
        Optional<VehicleLoanRoiWaiver> roiWaiverOpt = vehicleLoanRoiWaiverRepository.findByTaskId(task.getTaskId());
        if (roiWaiverOpt.isPresent()) {
            VehicleLoanRoiWaiver roiWaiver = roiWaiverOpt.get();
            roiWaiver.setBaseSpread(waiverDto.getBaseSpread());
            roiWaiver.setSancBaseSpread(waiverDto.getSancBaseSpread());
            roiWaiver.setSpread(spread);
            roiWaiver.setRevisedRoi(modifiedRoi);
            roiWaiver.setRevisedEmi(modifiedemi);
            roiWaiver.setDecision(waiverDto.getDecision());
            roiWaiver.setRoiwaiverSancRemarks(waiverDto.getRoiwaiverSancRemarks());
            roiWaiver.setLastModDate(new Date());
            roiWaiver.setLastModUser(usd.getPPCNo());
            roiWaiver.setLastModSol(usd.getSolid());
            vehicleLoanRoiWaiverRepository.save(roiWaiver);
        }
        eligibilityDetailsService.updateRoiWaveredSpread(waiverDto.getSlno(), waiverDto.getWiNum(), waiverDto.getRevisedRoi(), waiverDto.getRevisedEmi(), spread);
        return new ResponseDTO("S", "Waiver sanctioned");
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


    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveSanTask(WaiverDto.SanModDto sanModDto, HttpServletRequest request) {
        VehicleLoanMaster master = masterService.findById(sanModDto.getSlno());
        VehicleLoanSubqueueTask task = taskService.getSubTaskByTypeAndStatus(sanModDto.getSlno(), "SAN_MOD", "PENDING");

        if (sanModDto.getSanModRequired() == null || sanModDto.getSanModRequired().trim().isEmpty()) {//sanModDto.getSanModRequired().equals("Y") || sanModDto.getSanModRequired().equals("N")) {
            throw new RuntimeException("Invalid Type");
        } else {

            if (task != null && task.isPending() && !sanModDto.getSanModRequired().equals("RECALL")) {
                throw new ValidationException(ValidationError.COM001, "The WI is Already Submitted for Sanction Modification(PENDING)");
            }

            if (master.getAccNumber() != null) {
                return new ResponseDTO("F", "Sanction modification is not allowed after account opening");
            }

            if (sanModDto.getSanModRequired().equals("Y")) {
                task = new VehicleLoanSubqueueTask();
                task.setStatus("PENDING");
                task.setCompletedDate(null);
                task.setCompletedUser(null);
            } else if (sanModDto.getSanModRequired().equals("N")) {
                task = new VehicleLoanSubqueueTask();
                task.setStatus("COMPLETED");
                task.setCompletedDate(new Date());
                task.setCompletedUser(usd.getPPCNo());
            } else if (sanModDto.getSanModRequired().equals("RECALL")) {
                //dont create new object
                if ("Y".equals(task.getLockFlg())) {
                    return new ResponseDTO("F", "Cannot recall since subtask is locked");
                }
                task.setStatus("RECALLED");
                task.setCompletedDate(new Date());
                task.setCompletedUser(usd.getPPCNo());
            }
            task.setSlno(sanModDto.getSlno());
            task.setWiNum(sanModDto.getWiNum());
            task.setCreatedDate(new Date());
            task.setCreateUser(usd.getPPCNo());
            task.setCreateSol(usd.getSolid());
            task.setTaskType("SAN_MOD");
            task.setRemarks(sanModDto.getSanModRemarks());

            List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
            VehicleLoanApplicant vehicleLoanApplicant = new VehicleLoanApplicant();
            if (master != null) {
                vehicleLoanApplicant = master.getApplicants().stream().filter(t -> t.getApplicantType().equalsIgnoreCase("A")).toList().get(0);
            }
            task.setApplicant(vehicleLoanApplicant);
            task.setLockFlg("N");
            task.setRemarks(sanModDto.getSanModRemarks());

            taskService.saveSubTask(task);
            createSanModRequest(sanModDto, request);
            subqueueTatService.initialInsert(task.getTaskId(), usd.getPPCNo(), sanModDto.getWiNum(), "SAN_MOD", request);
            return new ResponseDTO("S", "Sanction Modification Request Saved Successfully");
        }
    }


    public void createSanModRequest(WaiverDto.SanModDto sanModDto, HttpServletRequest request) {
        VehicleLoanSanMod vehicleLoanSanMod;
        VehicleLoanMaster vehicleLoanMaster = masterService.findById(sanModDto.getSlno());
        if (vehicleLoanMaster.getAccNumber() != null) {
            throw new RuntimeException("Account is already opened; cannot process sanction modification request");
        }
        try {
            if (sanModDto.getSanModRequired().equalsIgnoreCase("RECALL")) {
                //vehicleLoanSubqueueTask=taskService.findByTaskId(Long.parseLong(sanModDto.getTaskId()));
                //VehicleLoanSanMod vehicleLoanSanMod=vehicleLoanSanModService.findBySlno(sanModDto.getSlno());
                vehicleLoanSanMod = vehicleLoanSanModService.findByTaskId(Long.parseLong(sanModDto.getTaskId()));
                vehicleLoanSanMod.setRecallDate(new Date());
                vehicleLoanSanMod.setRecallUser(usd.getPPCNo());
            } else if (sanModDto.getSanModRequired().equalsIgnoreCase("N")) {
                List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks = taskService.getSubTaskByTypeAndStatus2(sanModDto.getSlno(), "SAN_MOD", "COMPLETED");
                VehicleLoanSubqueueTask vehicleLoanSubqueueTask = vehicleLoanSubqueueTasks.get(0);//get the latest task id
                vehicleLoanSanMod = new VehicleLoanSanMod();
                vehicleLoanSanMod.setSlno(sanModDto.getSlno());
                vehicleLoanSanMod.setVlsanmod(vehicleLoanSubqueueTask);
                vehicleLoanSanMod.setWiNum(sanModDto.getWiNum());
                vehicleLoanSanMod.setRcreDate(new Date());
                vehicleLoanSanMod.setRemarks(sanModDto.getSanModRemarks());
                vehicleLoanSanMod.setCmUser(usd.getPPCNo());
                vehicleLoanSanMod.setCmDate(new Date());
                vehicleLoanSanMod.setIpaddress(CommonUtils.getClientIp(request));
                vehicleLoanSanMod.setDelFlag("N");
                vehicleLoanSanMod.setSanModRequired(sanModDto.getSanModRequired());
                vehicleLoanSanMod.setVUser(usd.getPPCNo());
                vehicleLoanSanMod.setVDate(new Date());
            } else {
                VehicleLoanSubqueueTask vehicleLoanSubqueueTask = taskService.getSubTaskByTypeAndStatus(sanModDto.getSlno(), "SAN_MOD", "PENDING");
                vehicleLoanSanMod = new VehicleLoanSanMod();
                vehicleLoanSanMod.setSlno(sanModDto.getSlno());
                vehicleLoanSanMod.setVlsanmod(vehicleLoanSubqueueTask);
                vehicleLoanSanMod.setWiNum(sanModDto.getWiNum());
                vehicleLoanSanMod.setRcreDate(new Date());
                vehicleLoanSanMod.setSanAmt(new BigDecimal(sanModDto.getSancAmt()));
                vehicleLoanSanMod.setReqAmt(new BigDecimal(sanModDto.getReqAmt()));
                vehicleLoanSanMod.setEffRoi(new BigDecimal(sanModDto.getRoi()));
                vehicleLoanSanMod.setLtvAmt(new BigDecimal(sanModDto.getLtvAmount()));
                vehicleLoanSanMod.setTenor(Integer.parseInt(sanModDto.getTenor()));
                vehicleLoanSanMod.setEmi(new BigDecimal(sanModDto.getEmi()));
                vehicleLoanSanMod.setRevisedSanAmt(new BigDecimal(sanModDto.getRec_sanc_amt()));
                vehicleLoanSanMod.setRevisedTenor(new BigDecimal(sanModDto.getRec_tenor()));
                vehicleLoanSanMod.setRevisedEmi(new BigDecimal(sanModDto.getRev_emi_amt()));
                vehicleLoanSanMod.setRemarks(sanModDto.getSanModRemarks());
                vehicleLoanSanMod.setCmUser(usd.getPPCNo());
                vehicleLoanSanMod.setCmDate(new Date());
                vehicleLoanSanMod.setIpaddress(CommonUtils.getClientIp(request));
                vehicleLoanSanMod.setDelFlag("N");
                vehicleLoanSanMod.setSanModRequired(sanModDto.getSanModRequired());
                vehicleLoanSanMod.setEligibleAmt(new BigDecimal(sanModDto.getEligibleAmt()));
            }
            vehicleLoanSanModService.save(vehicleLoanSanMod);
        } catch (Exception e) {
            log.error("Some exception occured in createSanModRequest", e);
            throw e;
        }
    }

    private void validateMclrForRoiWaiver(BigDecimal revisedRoi, Integer tenor) {
        if (tenor != null && tenor >= 12 && tenor <= 36) {
            BigDecimal mclrRate = fetchRepository.getMclrRate();

            if (revisedRoi.compareTo(mclrRate) < 0) {
                throw new ValidationException(ValidationError.COM001,
                        String.format("As per RBI Master Direction, the revised ROI cannot be less than " +
                                "12 months MCLR (%.2f%%) for loans with tenure between 12-36 months. " +
                                "Proposed revised ROI: %.2f%%", mclrRate, revisedRoi));
            }
        }
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
