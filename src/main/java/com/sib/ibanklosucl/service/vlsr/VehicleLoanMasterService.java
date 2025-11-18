package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;
import com.sib.ibanklosucl.dto.dashboard.*;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanLock;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.integrations.VehicleLoanBREDetails;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.UIDRepository;
import com.sib.ibanklosucl.repository.VehicleLoanMasterRepository;
import com.sib.ibanklosucl.repository.VehicleLoanMasterRepositoryJdbc;
import com.sib.ibanklosucl.repository.integations.VehicleLoanBREDetailsRepository;
import com.sib.ibanklosucl.service.VehicleLoanAllotmentService;
import com.sib.ibanklosucl.service.VehicleLoanDecisionService;
import com.sib.ibanklosucl.service.VehicleLoanQueueDetailsService;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.integration.Docservice;

import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;

@Service
@Slf4j
public class VehicleLoanMasterService {

    @Autowired
    private VehicleLoanMasterRepository repository;
    @Autowired
    private UIDRepository uidRepository;

    @Autowired
    private VehicleLoanAllotmentService allotmentService;
    @Autowired
    private VehicleLoanBREDetailsRepository breDetailsRepository;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanQueueDetailsService queueDetailsService;

    @Autowired
    private VehicleLoanMasterRepositoryJdbc vehicleLoanMasterRepositoryJdbc;
    @Autowired
    private VehicleLoanDecisionService vehicleLoanDecisionService;
    @Autowired
    private BpmService bpmService;
    @Autowired
    private FetchRepository fetchRepository;
      @Autowired
    private Docservice docservice;
//    private final VehicleLoanWaiverService waiverService;
//
//    public VehicleLoanMasterService(VehicleLoanWaiverService waiverService) {
//        this.waiverService = waiverService;
//    }
//          @Autowired
//    private VehicleLoanWarnService vehicleLoanWarnService;

    public Optional<VehicleLoanMaster> getMasById(Long slno) {
        return repository.findById(slno);
    }

    public VehicleLoanMaster findById(Long slno) {
        return repository.findById(slno).orElseThrow(() -> new RuntimeException("Not Found"));
    }

    public VehicleLoanMaster findByWiNum(String wiNum) {
        return repository.findByWiNumAndActiveFlg(wiNum, "Y").orElseThrow(() -> new RuntimeException("Not Found"));
    }


    public VehicleLoanMaster SearchByWiNum(String wiNum) {
        return repository.findByWiNumAndActiveFlg(wiNum, "Y").orElse(repository.findByWiNumAndQueue(wiNum,"NIL").orElse(null));
    }


    @Transactional
    public VehicleLoanMaster saveLoan(VehicleLoanMaster loan) {
        return repository.save(loan);
    }

    public String getWi_Num(String loan_type) {
        return repository.generateWiNum(loan_type);
    }


    public VehicleLoanMaster findBySlno(Long slno) {
        VehicleLoanMaster vehicleLoanMaster = repository.findBySlnoWithApplicants(slno);
        if (vehicleLoanMaster != null) {
            vehicleLoanMaster.getApplicants().forEach(applicant -> {
                if (applicant.getKycapplicants() != null) {
                    if (applicant.getKycapplicants().getAadharRefNum() != null) {
                        applicant.getKycapplicants().setUidno(uidRepository.getUID(applicant.getKycapplicants().getAadharRefNum()));
                    }
                }
                ;
            });
        }
        return vehicleLoanMaster;
    }

    public VehicleLoanMaster findAppBySlno(Long slno) {
        return repository.findBySlnoWithApplicantsOnly(slno);
    }

    public QueueCountDTO getStatusCountsBySolId(String solId) {
        List<Object[]> results = new ArrayList<>();
        if (!usd.getEmployee().getOffType().equalsIgnoreCase("BRANCH"))
        {
            results = repository.getStatusCountsBySolId();

        }
        else
            results = repository.getStatusCountsBySolId(solId);


        QueueCountDTO statusCountDTO = new QueueCountDTO();
        for (Object[] result : results) {
            String status = (String) result[0];
            Long actcount = (Long) result[1];
            Integer count = Math.toIntExact(actcount);
            switch (status) {
                case "BM":
                    log.info("setEntryPendingCount:"+count);
                    statusCountDTO.setEntryPendingCount(count);
                    break;
                case "BC":
                    log.info("setVerificationPendingCount:"+count);
                    statusCountDTO.setVerificationPendingCount(count);
                    break;
                case "CS","CRTC":
                    count=getCRTQueue(usd.getSolid()).size();
                    log.info("setCrtCount:"+count);
                    statusCountDTO.setCrtCount(count);
                    break;
                case "CA":
                    count=getCRTAmberQueue().size();
                    log.info("setCrtAmberCount:"+count);
                    statusCountDTO.setCrtAmberCount(count);
                case "RA","ALLOT","ALL":
                    count=allotmentService.getAllotment("RA").size();
                    log.info("setRbcpcAllotmentCount:"+count);
                    statusCountDTO.setRbcpcAllotmentCount(count);
                    break;
                case "RM","RBCM":
                    count=getRMQueue().size();
                    log.info("setRbcpcMakerCount:"+count);
                    statusCountDTO.setRbcpcMakerCount(count);
                    break;
                case "DP":
                    log.info("setDeviationCount:"+count);
                    statusCountDTO.setDeviationCount(count);
                    break;
                case "RC","RBCC":
                    count= getRBCQueue(usd.getPPCNo()).size();
                    log.info("setCrtCount:"+count);
                    statusCountDTO.setRbcpcCheckerCount(count);
                    break;
                case "BD","DQ":
                    log.info("setBrDocumentationCount:"+count);
                    statusCountDTO.setBrDocumentationCount(count);
                    break;
                case "BG","BOGQUEUE":
                    count= repository.findByQueueWithSubqueue("BD","CIF_CREATION").size();
                    log.info("setBogQueueCount:"+count);
                    statusCountDTO.setBogQueueCount(count);
                    break;
                case "ACOPN":
                    count= getAcctOpenQueue().size();
                    log.info("setAccountOpening:"+count);
                    statusCountDTO.setAccountOpening(count);
                    break;
                case "WAIVE":
                    count= findByWaiverSubtask(usd.getPPCNo()).size();
                    log.info("setWaiverList:"+count);
                    statusCountDTO.setWaiverList(count);
                    break;
                case "SM":
                    log.info("setCrtCount:"+count);
                    statusCountDTO.setBogQueueCount(count);
                    break;
                case "NIL":
                    log.info("setCrtCount:"+count);
                    statusCountDTO.setRejectedCount(count);
                    break;
                case "HUNTER":
                    log.info("setHunterCount:"+getHunterQueue().size());
                    statusCountDTO.setHunterCount(getHunterQueue().size());
                    break;
            }
        }

        return statusCountDTO;
    }

    public List<VehicleLoanMasterDTO> getEntryQueue(String solId) {
        List<String> solid=new ArrayList<>();
        if(fetchRepository.isLMPPC(usd.getPPCNo())) {
            solid= fetchRepository.getLHSols(usd.getPPCNo());
        }else  if(fetchRepository.isRAHPPC(usd.getPPCNo())) {
            solid= fetchRepository.getRAHSols(usd.getPPCNo());
        }
        else if(usd.getSolid().equalsIgnoreCase("8032"))
            solid= fetchRepository.getRsmSols(usd.getPPCNo());
        else
            solid.add(solId);
        List<Object[]> entryQueue = repository.findByQueueAndSol("BM", solid);
        return entryQueue.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> getBsQueue(String solId) {
    List<String> solid=new ArrayList<>();
        if(fetchRepository.isLMPPC(usd.getPPCNo())) {
            solid= fetchRepository.getLHSols(usd.getPPCNo());
        }else  if(fetchRepository.isRAHPPC(usd.getPPCNo())) {
            solid= fetchRepository.getRAHSols(usd.getPPCNo());
        }
        else if(usd.getSolid().equalsIgnoreCase("8032"))
            solid= fetchRepository.getRsmSols(usd.getPPCNo());
        else
            solid.add(solId);
        List<Map<String, String>> vcs = vehicleLoanMasterRepositoryJdbc.getBsQueue("BS", solid);
        return vcs;
    }
    public List<VehicleLoanMasterDTO> getHunterQueue() {

        List<Object[]> vcs = repository.findByHunterQueue("HU");
        return vcs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public boolean updateQueue(Long slno, String queue, String status, String cmuser) {
        VehicleLoanMaster vehicleLoanMasterDTO = findById(slno);
        vehicleLoanMasterDTO.setQueue(queue);
        vehicleLoanMasterDTO.setQueueDate(new Date());
        vehicleLoanMasterDTO.setStatus(status);
//        vehicleLoanMasterDTO.setCmUser(cmuser);
//        vehicleLoanMasterDTO.setCmDate(new Date());
        saveLoan(vehicleLoanMasterDTO);
        return true;
    }

    @Transactional
    public boolean updateRecallQueue(Long slno) {
        VehicleLoanMaster vehicleLoanMasterDTO = findById(slno);
        vehicleLoanMasterDTO.setQueue("RM");
        vehicleLoanMasterDTO.setQueueDate(new Date());
        vehicleLoanMasterDTO.setStatus("RECALLED");
        saveLoan(vehicleLoanMasterDTO);
        return true;
    }

    @Transactional
    public boolean updateCrtAmberQueue(Long slno, String queue, String status, String cmuser) {
        VehicleLoanMaster vehicleLoanMasterDTO = findById(slno);
        vehicleLoanMasterDTO.setQueue(queue);
        vehicleLoanMasterDTO.setQueueDate(new Date());
        vehicleLoanMasterDTO.setStatus(status);
        vehicleLoanMasterDTO.setCrtCmUser(cmuser);
        vehicleLoanMasterDTO.setCrtCmDate(new Date());
        if (status.equalsIgnoreCase("CAREJECT")) {
            vehicleLoanMasterDTO.setRejQueue("CA");
            vehicleLoanMasterDTO.setRejUser(cmuser);
            vehicleLoanMasterDTO.setRejFlg("Y");
            vehicleLoanMasterDTO.setRejDate(new Date());
            vehicleLoanMasterDTO.setActiveFlg("N");
        } else if (status.equalsIgnoreCase("CASENDBACK")) {
            vehicleLoanMasterDTO.setSanDate(null);
            vehicleLoanMasterDTO.setSanFlg("N");
            vehicleLoanMasterDTO.setSanUser(null);
        }
        saveLoan(vehicleLoanMasterDTO);
        return true;
    }

    @Transactional
    public boolean updateStatus(Long slno, String status) {
        VehicleLoanMaster vehicleLoanMasterDTO = findById(slno);
        vehicleLoanMasterDTO.setStatus(status);
        saveLoan(vehicleLoanMasterDTO);
        return true;
    }

    @Transactional
    public boolean updateVehicleOwner(Long slno, String vlownerstatus, String vlowner, String currentTab) {
        VehicleLoanMaster vehicleLoanMasterDTO = findById(slno);
        vehicleLoanMasterDTO.setFirstTimeBuyer(vlownerstatus);
        vehicleLoanMasterDTO.setCurrentTab(currentTab);
        vehicleLoanMasterDTO.setOwnerApplicantId(Long.valueOf(vlowner));
        saveLoan(vehicleLoanMasterDTO);
        return true;
    }

    public List<VehicleLoanMasterDTO> getBCQueue(String solId) {
        List<String> solid=new ArrayList<>();
        if(fetchRepository.isLMPPC(usd.getPPCNo())) {
            solid= fetchRepository.getLHSols(usd.getPPCNo());
        }else  if(fetchRepository.isRAHPPC(usd.getPPCNo())) {
            solid= fetchRepository.getRAHSols(usd.getPPCNo());
        }
        else if(usd.getSolid().equalsIgnoreCase("8032"))
            solid= fetchRepository.getRsmSols(usd.getPPCNo());
        else
            solid.add(solId);
        List<Object[]> entryQueue = repository.findByQueueAndSol("BC", solid);
        return entryQueue.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<VehicleLoanMasterDTO> getDOCQueue(String solId) {
        List<String> solid=new ArrayList<>();

//        if(usd.getSolid().equalsIgnoreCase("8032"))
//            solid= fetchRepository.getRsmSols(usd.getPPCNo());
//        else
        if(fetchRepository.isRAHPPC(usd.getPPCNo())) {
            solid= fetchRepository.getRAHSols(usd.getPPCNo());
        }else
            solid.add(solId);
        List<Object[]> entryQueue = repository.findByQueueAndSol("BD", solid);
        return entryQueue.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<VehicleLoanRBCPCDTO> getRBCQueue(String ppcNO) {

        List<Object[]> queue = repository.findByRBCQueue(ppcNO);
        //return queue.stream().map(this::convertToResDTO).collect(Collectors.toList());
        List<VehicleLoanRBCPCDTO> vehicleLoanMasterDTOList=queue.stream().map(this::convertToResDTO).collect(Collectors.toList());
        for(VehicleLoanRBCPCDTO vehicleLoanRBCPCDTO:vehicleLoanMasterDTOList){
            String getprocesseduser="";
            Optional<String> getprocesseduseropt= fetchRepository.getprocesseduser(vehicleLoanRBCPCDTO.getWiNum(),"RC");
            if(getprocesseduseropt.isPresent()){
                getprocesseduser=getprocesseduseropt.get();

                if(getprocesseduser!=null && !getprocesseduser.trim().isEmpty()){
                    vehicleLoanRBCPCDTO.setLastLockUser(getprocesseduser.split("~")[0]);
                    vehicleLoanRBCPCDTO.setLastLockDate(getprocesseduser.split("~")[1]);
                }
            }
        }
        return vehicleLoanMasterDTOList;
    }

    public List<VehicleLoanMasterDTO> getCRTQueue(String solId) {
//        List<String> solid=new ArrayList<>();
//        if(usd.getSolid().equalsIgnoreCase("8032"))
//            solid= fetchRepository.getRsmSols(usd.getPPCNo());
//        else
//            solid.add(solId);
        List<Object[]> entryQueue = repository.findByQueueforCRT("CS");
        return entryQueue.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<VehicleLoanMasterDTO> getCRTAmberQueue() {
        List<Object[]> entryQueue = repository.findByQueueWithLock("CA");
        return entryQueue.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private VehicleLoanRBCPCDTO convertToResDTO(Object[] result) {
        VehicleLoanRBCPCDTO dto = new VehicleLoanRBCPCDTO();
        dto.setWiNum((String) result[0]);
        dto.setSlNo(result[1].toString());
        dto.setCustName((String) result[2]);
        dto.setRcrDate((Date) result[3]);
        dto.setChannel((String) result[4]);
        dto.setLockedFlag(result[5] != null ? String.valueOf(result[5]) : "");
        dto.setLockedBy(result[6] != null ? (String) result[6] : "");
        return dto;
    }


    private VehicleLoanMasterDTO convertToDTO(Object[] result) {
        if (result.length == 0) {
            return null; // or throw an appropriate exception
        }
        VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) result[0];
        VehicleLoanMasterDTO dto = new VehicleLoanMasterDTO();
        dto.setWiNum(vehicleLoanMaster.getWiNum());
        dto.setSlno(vehicleLoanMaster.getSlno());
        dto.setSolId(vehicleLoanMaster.getSolId());
        String pattern = "dd MMM yyyy, HH:mm a";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        String formattedDate = formatter.format(vehicleLoanMaster.getRiRcreDate());
        String formattedQueueDate = formatter.format(vehicleLoanMaster.getQueueDate());
        dto.setRiRcreDate(formattedDate);
        dto.setChannel(vehicleLoanMaster.getChannel());
        dto.setCustName(vehicleLoanMaster.getCustName());
        dto.setQueueDate(formattedQueueDate);
        if (result.length > 1 && result[1] instanceof VehicleLoanLock) {
            VehicleLoanLock vehicleLoanLock = (VehicleLoanLock) result[1];
            VehicleLoanLockDTO lockDTO = convertToLockDTO(vehicleLoanLock);
            dto.setVehicleLoanLock(lockDTO);
        }
        return dto;
    }

    private VehicleLoanLockDTO convertToLockDTO(VehicleLoanLock vehicleLoanLock) {
        VehicleLoanLockDTO dto = new VehicleLoanLockDTO();
        dto.setLockedBy(vehicleLoanLock.getLockedBy());
        dto.setLockedOn(vehicleLoanLock.getLockedOn());
        dto.setLockFlg(vehicleLoanLock.getLockFlg());
        return dto;

    }


    private VehicleLoanApplicantDTO convertToApplicantDTO(VehicleLoanApplicant applicant) {
        VehicleLoanApplicantDTO dto = new VehicleLoanApplicantDTO();
        dto.setApplicantId(applicant.getApplicantId());
        dto.setApplicantType(applicant.getApplicantType());
        dto.setApplName(applicant.getApplName());
        dto.setApplDob(applicant.getApplDob());

        int completedStages = 0;
        int totalStages = 10;

        dto.setGenComplete(getStageStatus(applicant.getGenComplete()));
        if (applicant.getGenComplete() != null && applicant.getGenComplete().equals("Y")) completedStages++;

        dto.setKycComplete(getStageStatus(applicant.getKycComplete()));
        if (applicant.getKycComplete() != null && applicant.getKycComplete().equals("Y")) completedStages++;

        dto.setBasicComplete(getStageStatus(applicant.getBasicComplete()));
        if (applicant.getBasicComplete() != null && applicant.getBasicComplete().equals("Y")) completedStages++;

        dto.setEmploymentComplete(getStageStatus(applicant.getEmploymentComplete()));
        if (applicant.getEmploymentComplete() != null && applicant.getEmploymentComplete().equals("Y")) completedStages++;

        dto.setIncomeComplete(getStageStatus(applicant.getIncomeComplete()));
        if (applicant.getIncomeComplete() != null && applicant.getIncomeComplete().equals("Y")) completedStages++;

        dto.setCreditComplete(getStageStatus(applicant.getCreditComplete()));
        if (applicant.getCreditComplete() != null && applicant.getCreditComplete().equals("Y")) completedStages++;

        dto.setFinancialComplete(getStageStatus(applicant.getFinancialComplete()));
        if (applicant.getFinancialComplete() != null && applicant.getFinancialComplete().equals("Y")) completedStages++;

        dto.setVehicleComplete(getStageStatus(applicant.getVehicleComplete()));
        if (applicant.getVehicleComplete() != null && applicant.getVehicleComplete().equals("Y")) completedStages++;

        dto.setInsuranceComplete(getStageStatus(applicant.getInsuranceComplete()));
        if (applicant.getInsuranceComplete() != null && applicant.getInsuranceComplete().equals("Y")) completedStages++;

        dto.setLoanComplete(getStageStatus(applicant.getLoanComplete()));
        if (applicant.getLoanComplete() != null && applicant.getLoanComplete().equals("Y")) completedStages++;

        double progress = (completedStages * 100.0) / totalStages;
        dto.setProgress(progress);

        return dto;
    }


    private String getStageStatus(String status) {
        return status != null && status.equals("Y") ? "Completed" : "Pending";
    }

    @Transactional
    public String forwardApplication(Long slno, String wiNum, String remarks) {
        VehicleLoanMaster loanMaster = repository.findBySlnoWithApplicants(slno);
        RBCPCCheckerSave rbs = new RBCPCCheckerSave();
        rbs.setSlno(String.valueOf(slno));
        rbs.setWinum(wiNum);
        rbs.setRbccremarks(remarks);

        if (loanMaster == null) {
            throw new RuntimeException("Loan application not found");
        }
        Optional<VehicleLoanBREDetails> latestBREDetails = breDetailsRepository.findTopByWiNumAndSlnoOrderByIdDesc(wiNum, slno);
        if (!latestBREDetails.isPresent()) {
            throw new RuntimeException("BRE details not found for the application");
        }

        String breFlag = latestBREDetails.get().getBreFlag();
        String newQueue;
        String loanFlowType = "";
        String pdf="";
        //waiverService.calculateProcessingFee(rbs);
        if ("green".equalsIgnoreCase(breFlag)) {
             loanMaster.setSanUser(usd.getPPCNo());
            loanMaster.setSanDate(new Date());
            loanMaster.setSanFlg("Y");

            newQueue = "CS"; // CRT Queue
            loanFlowType = "STP";
            try {
                DocumentRequest documentRequest=new DocumentRequest();
                documentRequest.setCmUser(usd.getPPCNo());
                documentRequest.setSlNo(String.valueOf(slno));
                documentRequest.setWiNum(wiNum);
                 pdf=docservice.getSanctionPdf(documentRequest);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            TabResponse tb=bpmService.BpmUpload(bpmRequest(wiNum,pdf,"N","NA","SANCTION_LETTER"));
                if(!"S".equalsIgnoreCase(tb.getStatus())){
                    throw new ValidationException(ValidationError.COM001,tb.getMsg());
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
        queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), loanMaster.getQueue(), newQueue);

        // Update VehicleLoanMaster
        loanMaster.setQueue(newQueue);
        loanMaster.setQueueDate(new Date());
        loanMaster.setBrVUser(usd.getEmployee().getPpcno());
        loanMaster.setBrVDate(new Date());
        loanMaster.setStatus("BCCOMPLETE");
        loanMaster.setStp(loanFlowType);
        repository.save(loanMaster);
        return newQueue;
    }

    @Transactional
    public void rejectApplication(Long slno, String wiNum, String remarks) {
        VehicleLoanMaster loanMaster = repository.findBySlnoWithApplicants(slno);
        if (loanMaster == null) {
            throw new RuntimeException("Loan application not found");
        }
        String newQueue = "NIL";
        String currentQueue = loanMaster.getQueue();
        loanMaster.setQueue(newQueue);
        loanMaster.setStatus("BCREJ");
        loanMaster.setRejFlg("Y");
        loanMaster.setRejDate(new Date());
        loanMaster.setRejUser(usd.getEmployee().getPpcno());
        loanMaster.setRejQueue(currentQueue);
        repository.save(loanMaster);
        queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), currentQueue, newQueue);
    }

    @Transactional
    public void sendbackApplication(Long slno, String wiNum, String remarks, HttpServletRequest request) {
        VehicleLoanMaster loanMaster = repository.findBySlnoWithApplicants(slno);
        if (loanMaster == null) {
            throw new RuntimeException("Loan application not found");
        }

        String currentQueue = loanMaster.getQueue();
        String newQueue = "BS";
        String decision = "Sendback to Branch";

        // Update VehicleLoanMaster for sendback
        loanMaster.setQueue(newQueue);
        loanMaster.setStatus("BCSENDBACK");
        loanMaster.setBrVUser(usd.getEmployee().getPpcno());
        loanMaster.setBrVDate(new Date());
        if ("RM".equals(currentQueue)) {
            loanMaster.setRbcpcCmUser(usd.getEmployee().getPpcno());
            loanMaster.setRbcpcCmDate(new Date());
            loanMaster.setRbcpcMakerDecision(decision);
            loanMaster.setStatus("RMSENDBACK");
        }

        repository.save(loanMaster);
        queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), currentQueue, newQueue);
    }

    public List<VehicleLoanMasterDTO> getRMQueue() {
        Integer employeePpcno = Integer.valueOf(usd.getEmployee().getPpcno());
        List<Object[]> entryQueue = repository.findByQueue("RM");
        return entryQueue.stream()
                .filter(result -> {
                    if (result.length > 0 && result[0] instanceof VehicleLoanMaster) {
                        VehicleLoanMaster master = (VehicleLoanMaster) result[0];
                        return allotmentService.isAllotedToEmployee(master.getWiNum(), master.getSlno(), employeePpcno);
                    }
                    return false;
                })
                .map(this::convertToDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }


    @Transactional
    public String forwardApplicationRBCPCMaker(Long slno, String wiNum, String decision, String preDisbursementCondition, HttpServletRequest request) {
        VehicleLoanMaster loanMaster = repository.findBySlnoWithApplicants(slno);
        if (loanMaster == null) {
            throw new RuntimeException("Loan application not found");
        }

        // Validate mandatory fields
        if (decision == null || decision.trim().isEmpty()) {
            throw new IllegalArgumentException("Decision is mandatory");
        }
        if (preDisbursementCondition == null || preDisbursementCondition.trim().isEmpty()) {
            throw new IllegalArgumentException("Pre-disbursement condition is mandatory");
        }
        ResponseDTO responseDTO= null;
//        try {
//            responseDTO = vehicleLoanWarnService.sendWarnEmail(Long.valueOf(slno));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }


        String newQueue = decision;
        String newStatus = "RMCOMPLETE";

        // Update VehicleLoanMaster
        loanMaster.setQueue(newQueue);
        loanMaster.setQueueDate(new Date());
        loanMaster.setStatus(newStatus);
        loanMaster.setRbcpcMakerDecision(decision);
        loanMaster.setPreDisbCondition(preDisbursementCondition);
        loanMaster.setRbcpcCmUser(usd.getEmployee().getPpcno());
        loanMaster.setRbcpcCmDate(new Date());

        repository.save(loanMaster);

        // Save the decision
        vehicleLoanDecisionService.saveDecision(wiNum, slno, newQueue, decision, request);

        queueDetailsService.createQueueEntry(wiNum, slno, preDisbursementCondition, usd.getEmployee().getPpcno(), loanMaster.getQueue(), newQueue);

        return newQueue;
    }
    public BpmRequest bpmRequest(String winum, String pdf, String child, String childName, String docName){
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


    public List<VehicleLoanMasterDTO> getBOGQueue() {
        List<Object[]> entryQueue = repository.findByQueueWithSubqueue("BD","CIF_CREATION");


        List<VehicleLoanMasterDTO> vehicleLoanMasterDTOList= entryQueue.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        for(VehicleLoanMasterDTO vehicleLoanMasterDTO:vehicleLoanMasterDTOList){
            Long slno=vehicleLoanMasterDTO.getSlno();
            //List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTaskList = vehicleLoanSubqueueTaskService.getBySlno(slno);//ideally all tasks in an slno will be locked by the same PPC
            VehicleLoanMaster vehicleLoanMaster = findBySlno(slno);
            List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTaskList =vehicleLoanMaster.getSubqueueTasks();
            for(VehicleLoanSubqueueTask vehicleLoanSubqueueTask:vehicleLoanSubqueueTaskList) {
                if (vehicleLoanSubqueueTask.getTaskType().equals("CIF_CREATION") && vehicleLoanSubqueueTask.getStatus().equals("PENDING") && vehicleLoanSubqueueTask.getLockFlg() != null && vehicleLoanSubqueueTask.getLockFlg().equals("Y")) {
                    VehicleLoanLockDTO vehicleLoanLockDTO = new VehicleLoanLockDTO();
                    vehicleLoanLockDTO.setLockedBy(vehicleLoanSubqueueTask.getLockedBy());
                    vehicleLoanLockDTO.setLockedOn(vehicleLoanSubqueueTask.getLockedOn());
                    vehicleLoanLockDTO.setLockFlg("Y");
                    vehicleLoanMasterDTO.setVehicleLoanLock(vehicleLoanLockDTO);
                    break;
                }
            }
        }
        return vehicleLoanMasterDTOList;
    }

    public List<VehicleLoanMasterDTO> getAcctOpenQueue() {
        List<Object[]> entryQueue = repository.findByQueueWithLock("ACOPN");
        //return
        List<VehicleLoanMasterDTO> vehicleLoanMasterDTOList = entryQueue.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        for(VehicleLoanMasterDTO vehicleLoanMasterDTO:vehicleLoanMasterDTOList){
            vehicleLoanMasterDTO.setBrName(fetchRepository.getSolName(vehicleLoanMasterDTO.getSolId()));
            vehicleLoanMasterDTO.setRoName(fetchRepository.getROName(vehicleLoanMasterDTO.getSolId()));
            Optional<String> getprocesseduseropt=fetchRepository.getprocesseduser(vehicleLoanMasterDTO.getWiNum(),"ACOPN");
            String getprocesseduser="";
            if(getprocesseduseropt.isPresent()){
                getprocesseduser=getprocesseduseropt.get();

                if(getprocesseduser!=null && !getprocesseduser.trim().isEmpty()){
                    vehicleLoanMasterDTO.setStatus("RUNNING");
                    vehicleLoanMasterDTO.setLastLockUser(getprocesseduser.split("~")[0]);
                    vehicleLoanMasterDTO.setLastLockDate(getprocesseduser.split("~")[1]);
                }else {
                    vehicleLoanMasterDTO.setStatus("NOT STARTED");
                }
            }else{
                vehicleLoanMasterDTO.setStatus("NOT STARTED");
            }
        }
        return vehicleLoanMasterDTOList;
    }
    public List<VehicleLoanMasterDTO> getInsData(String solId) {
        List<Object[]> entryQueue = repository.findByInsList(solId);
        return entryQueue.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public WaiverAccessDTO findWaiverAccess(String ppcNo) {
        List<Object[]> accessResults = repository.findWaiverAccess(ppcNo);
        boolean hasRoiAccess = false;
        boolean hasChargeAccess = false;

        for (Object[] result : accessResults) {
            String waiverType = (String) result[0];
            if ("ROI_WAIVER".equals(waiverType)) {
                hasRoiAccess = true;
            } else if ("CHARGE_WAIVER".equals(waiverType)) {
                hasChargeAccess = true;
            }
        }

        return new WaiverAccessDTO(hasRoiAccess, hasChargeAccess);
    }

     public List<WaiverSubtaskDTO> findByWaiverSubtask(String ppcNo) {
        try {
            WaiverAccessDTO access = findWaiverAccess(ppcNo);
            if (!access.isHasRoiAccess() && !access.isHasChargeAccess()) {
                return new ArrayList<>();
            }
            String hasRoiAccess = access.isHasRoiAccess() ? "Y" : "N";
            String hasChargeAccess = access.isHasChargeAccess() ? "Y" : "N";

            List<Object[]> results = repository.findWaiverSubtasks(ppcNo, hasRoiAccess, hasChargeAccess);
            List<WaiverSubtaskDTO> waiverTasks = new ArrayList<>();

            for (Object[] result : results) {
                try {
                    String wiNum = convertToString(result[0]);
                    Long slno = convertToLong(result[1]);
                    String custName = convertToString(result[2]);
                    Date riRcreDate = convertToDate(result[3]);
                    String channel = convertToString(result[4]);
                    String lockFlg = "";//convertToString(result[5]);
                    String lockedBy = "";//convertToString(result[6]);
                    String status = convertToString(result[5]);
                    String taskType = convertToString(result[6]);

                    WaiverSubtaskDTO dto = new WaiverSubtaskDTO(wiNum, slno, custName, riRcreDate, channel, lockFlg, lockedBy, status, taskType);
                    waiverTasks.add(dto);
                } catch (Exception e) {
                    log.warn("Error processing result row: " + Arrays.toString(result), e);
                    // Continue processing other rows
                }
            }

            return waiverTasks;
        } catch (Exception e) {
            log.error("Error in findByWaiverSubtask", e);
            return new ArrayList<>(); // Return empty list in case of error
        }
    }

    private String convertToString(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        if (obj instanceof Character) return String.valueOf(obj);
        return obj.toString();
    }

    private Long convertToLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            log.warn("Failed to convert to Long: " + obj, e);
            return null;
        }
    }

    private Date convertToDate(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Date) return (Date) obj;
        if (obj instanceof java.sql.Date) return new Date(((java.sql.Date) obj).getTime());
        if (obj instanceof java.sql.Timestamp) return new Date(((java.sql.Timestamp) obj).getTime());
        log.warn("Unexpected date type: " + obj.getClass());
        return null;
    }


    public List<VehicleLoanMasterDTO> getSanModQueue() {
        List<Object[]> entryQueue = repository.findByQueueWithSubqueue("ACOPN","SAN_MOD");


        List<VehicleLoanMasterDTO> vehicleLoanMasterDTOList= entryQueue.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        for(VehicleLoanMasterDTO vehicleLoanMasterDTO:vehicleLoanMasterDTOList){
            Long slno=vehicleLoanMasterDTO.getSlno();
            //List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTaskList = vehicleLoanSubqueueTaskService.getBySlno(slno);//ideally all tasks in an slno will be locked by the same PPC
            VehicleLoanMaster vehicleLoanMaster = findBySlno(slno);
            List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTaskList =vehicleLoanMaster.getSubqueueTasks();
            for(VehicleLoanSubqueueTask vehicleLoanSubqueueTask:vehicleLoanSubqueueTaskList) {
                if (vehicleLoanSubqueueTask.getTaskType().equals("SAN_MOD") && vehicleLoanSubqueueTask.getStatus().equals("PENDING") && vehicleLoanSubqueueTask.getLockFlg() != null && vehicleLoanSubqueueTask.getLockFlg().equals("Y")) {
                    VehicleLoanLockDTO vehicleLoanLockDTO = new VehicleLoanLockDTO();
                    vehicleLoanLockDTO.setLockedBy(vehicleLoanSubqueueTask.getLockedBy());
                    vehicleLoanLockDTO.setLockedOn(vehicleLoanSubqueueTask.getLockedOn());
                    vehicleLoanLockDTO.setLockFlg("Y");
                    vehicleLoanMasterDTO.setVehicleLoanLock(vehicleLoanLockDTO);
                    break;
                }
            }
        }
        return vehicleLoanMasterDTOList;
    }
}
