package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.RBCPCCheckerSave;
import com.sib.ibanklosucl.dto.WaiverAccessDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.dto.subqueue.LockStatusDTO;
import com.sib.ibanklosucl.dto.subqueue.WaiverHistoryDTO;
import com.sib.ibanklosucl.model.EligibilityDetails;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;
import com.sib.ibanklosucl.model.doc.VehicleLoanChargeWaiver;
import com.sib.ibanklosucl.model.doc.VehicleLoanRoiWaiver;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.VehicleLoanSubqueueTaskRepository;
import com.sib.ibanklosucl.repository.doc.VehicleLoanFeeWaiverRepository;
import com.sib.ibanklosucl.repository.doc.VehicleLoanRoiWaiverRepository;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Optional;

@Service
public class VehicleLoanWaiverService {

    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private VehicleLoanSubqueueTaskRepository vehicleLoanSubqueueTaskRepository;
        @Autowired
    private VehicleLoanRoiWaiverRepository vehicleLoanRoiWaiverRepository;
    @Autowired
    private VehicleLoanFeeWaiverRepository feeWaiverRepository;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanSubqueueTaskService taskService;


    public     List<VehicleLoanChargeWaiver>  chargeWaivers(Long slno){
       return feeWaiverRepository.findBySlnoAndDelFlagOrderByInoDesc(slno,"N");
    }

    @Transactional
    public boolean lockWaiverTasks2(String wiNum, String ppcNo, Long slno) {
        VehicleLoanMaster master = vehicleLoanMasterService.findByWiNum(wiNum);
        if (master == null || !"Y".equals(master.getActiveFlg())) {
            return false;
        }
        WaiverAccessDTO access = vehicleLoanMasterService.findWaiverAccess(ppcNo);
        List<VehicleLoanSubqueueTask> tasks = vehicleLoanSubqueueTaskRepository.findPendingTasksByWiNum(wiNum);
        WaiverAccessDTO accessDTO = vehicleLoanMasterService.findWaiverAccess(ppcNo);
        boolean anyLocked = false;

        for (VehicleLoanSubqueueTask task : tasks) {
            if (hasAccess(task.getTaskType(), access)) {
                if ("N".equals(task.getLockFlg())) {
                    task.setLockFlg("Y");
                    task.setLockedBy(ppcNo);
                    task.setLockedOn(new Timestamp(System.currentTimeMillis()));
                    vehicleLoanSubqueueTaskRepository.save(task);
                    anyLocked = true;
                }
            }
        }
        return anyLocked;
    }
    @Transactional
    public LockStatusDTO lockWaiverTasks(String wiNum, String ppcNo, Long slno) {
        VehicleLoanMaster master = vehicleLoanMasterService.findByWiNum(wiNum);
        if (master == null || !"Y".equals(master.getActiveFlg())) {
            return new LockStatusDTO(false, false, null, null);
        }

        WaiverAccessDTO access = vehicleLoanMasterService.findWaiverAccess(ppcNo);
        List<VehicleLoanSubqueueTask> tasks = vehicleLoanSubqueueTaskRepository.findPendingTasksByWiNum(wiNum);

        boolean roiLocked = false;
        boolean chargeLocked = false;
        String roiLockedBy = null;
        String chargeLockedBy = null;

        for (VehicleLoanSubqueueTask task : tasks) {
            if ("ROI_WAIVER".equals(task.getTaskType()) && access.isHasRoiAccess()) {
                if (!"Y".equals(task.getLockFlg())) {
                    task.setLockFlg("Y");
                    task.setLockedBy(ppcNo);
                    task.setLockedOn(new Timestamp(System.currentTimeMillis()));
                    vehicleLoanSubqueueTaskRepository.save(task);
                }
                roiLocked = true;
                roiLockedBy = task.getLockedBy();
            } else if ("CHARGE_WAIVER".equals(task.getTaskType()) && access.isHasChargeAccess()) {
                if (!"Y".equals(task.getLockFlg())) {
                    task.setLockFlg("Y");
                    task.setLockedBy(ppcNo);
                    task.setLockedOn(new Timestamp(System.currentTimeMillis()));
                    vehicleLoanSubqueueTaskRepository.save(task);
                }
                chargeLocked = true;
                chargeLockedBy = task.getLockedBy();
            }
        }

        return new LockStatusDTO(roiLocked, chargeLocked, roiLockedBy, chargeLockedBy);
    }


    private boolean hasAccess(String taskType, WaiverAccessDTO access) {
        switch (taskType) {
            case "ROI_WAIVER":
                return access.isHasRoiAccess();
            case "CHARGE_WAIVER":
                return access.isHasChargeAccess();
            default:
                return false;
        }
    }

    public LockStatusDTO checkWaiverLocks(String wiNum, String ppcNo) {
        WaiverAccessDTO access = vehicleLoanMasterService.findWaiverAccess(ppcNo);
        List<VehicleLoanSubqueueTask> tasks = vehicleLoanSubqueueTaskRepository.findPendingTasksByWiNum(wiNum);

        boolean roiLocked = false;
        boolean chargeLocked = false;
        String roiLockedBy = null;
        String chargeLockedBy = null;

        for (VehicleLoanSubqueueTask task : tasks) {
            if ("ROI_WAIVER".equals(task.getTaskType()) && access.isHasRoiAccess()) {
                if ("Y".equals(task.getLockFlg())) {
                    roiLocked = true;
                    roiLockedBy = task.getLockedBy();
                }
            } else if ("CHARGE_WAIVER".equals(task.getTaskType()) && access.isHasChargeAccess()) {
                if ("Y".equals(task.getLockFlg())) {
                    chargeLocked = true;
                    chargeLockedBy = task.getLockedBy();
                }
            }
        }

        return new LockStatusDTO(roiLocked, chargeLocked, roiLockedBy, chargeLockedBy);
    }
    @Transactional
    public boolean releaseSubQueueLocks(Long slno, String ppcNo) {
        List<VehicleLoanSubqueueTask> tasks = vehicleLoanSubqueueTaskRepository.findLockedTasksByWiNumAndPpcNo(slno, ppcNo);
        boolean anyUnlocked = false;

        for (VehicleLoanSubqueueTask task : tasks) {
            task.setLockFlg("N");
            task.setLockedBy(null);
            task.setLockedOn(null);
            vehicleLoanSubqueueTaskRepository.save(task);
            anyUnlocked = true;
        }

        return anyUnlocked;
    }
    @Transactional
    public boolean releaseSubQueueLocksByTask(Long slno, String ppcNo,String taskType) {
        List<VehicleLoanSubqueueTask> tasks = vehicleLoanSubqueueTaskRepository.findLockedTasksByWiNumAndPpcNoAndTaskType(slno, ppcNo,taskType);
        boolean anyUnlocked = false;

        for (VehicleLoanSubqueueTask task : tasks) {
            task.setLockFlg("N");
            task.setLockedBy(null);
            task.setLockedOn(null);
            vehicleLoanSubqueueTaskRepository.save(task);
            anyUnlocked = true;
        }

        return anyUnlocked;
    }
    public List<WaiverHistoryDTO> getWaiverHistory(Long slno, String waiverType) {
        List<WaiverHistoryDTO> history = new ArrayList<>();

        if ("ROI".equals(waiverType)) {
            List<VehicleLoanRoiWaiver> roiWaivers = vehicleLoanRoiWaiverRepository.findBySlnoOrderByInoDesc(slno);
            for (VehicleLoanRoiWaiver waiver : roiWaivers) {
                WaiverHistoryDTO dto = new WaiverHistoryDTO();
               dto.setDecision(decodeDecision(waiver.getDecision()));
                dto.setRequestedSpread(getValueOrDefault(waiver.getBaseSpread(), BigDecimal.ZERO));
                dto.setSanctionedSpread(getValueOrDefault(waiver.getSancBaseSpread(), BigDecimal.ZERO));
                dto.setRevisedRoi(getValueOrDefault(waiver.getRevisedRoi(), BigDecimal.ZERO));
                dto.setRevisedEmi(getValueOrDefault(waiver.getRevisedEmi(), BigDecimal.ZERO));
                dto.setRemarks(getValueOrDefault(waiver.getRoiwaiverRemarks(), ""));
                dto.setSanctionRemarks(getValueOrDefault(waiver.getRoiwaiverSancRemarks(), ""));
                dto.setLastModDate(waiver.getLastModDate());
                dto.setLastModUser(getValueOrDefault(waiver.getLastModUser(), ""));
                setSubqueueTaskDetails(dto, waiver.getSubTask());
                history.add(dto);
            }
        } else if ("CHARGE".equals(waiverType)) {
            List<VehicleLoanChargeWaiver> chargeWaivers = feeWaiverRepository.findBySlnoOrderByInoDesc(slno);
            for (VehicleLoanChargeWaiver waiver : chargeWaivers) {
                WaiverHistoryDTO dto = new WaiverHistoryDTO();
                dto.setDecision(decodeDecision(waiver.getDecision()).equals("Unknown")?"WAIVER NOT REQUIRED":waiver.getDecision());
                dto.setFeeCode(getValueOrDefault(waiver.getFeeCode(), ""));
                dto.setFeeName(getValueOrDefault(waiver.getFeeName(), ""));
                dto.setFeeValue(getValueOrDefault(waiver.getFeeValue(), BigDecimal.ZERO));
                dto.setFeeValueRec(getValueOrDefault(waiver.getFeeValueRec(), BigDecimal.ZERO));
                dto.setFeeSancValue(getValueOrDefault(waiver.getFeeSancValue(), BigDecimal.ZERO));
                dto.setFinalFee(getValueOrDefault(waiver.getFinalFee(), BigDecimal.ZERO));
                dto.setRemarks(getValueOrDefault(waiver.getFeeRemarks(), ""));
                dto.setSanctionRemarks(getValueOrDefault(waiver.getFeeWaiverSancRemarks(), ""));
                dto.setLastModDate(waiver.getLastModDate());
                dto.setLastModUser(getValueOrDefault(waiver.getLastModUser(), ""));
                dto.setWaiverFlag(getValueOrDefault(waiver.getWaiverFlg(), ""));
                setSubqueueTaskDetails(dto, waiver.getFeesubTask());

                history.add(dto);
            }
        }

        return history;
    }
    private String decodeDecision(String decision) {
        if (decision == null) return "Unknown";
        switch (decision.toUpperCase()) {
            case "RI1":
                return "Forwarded to L1";
            case "RI2":
                return "Forwarded to L2";
            case "RI3":
                return "Forwarded to L3";
            case "RI4":
                return "Forwarded to L4";
            default:
                return decision;
        }
    }
     private <T> T getValueOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    private void setSubqueueTaskDetails(WaiverHistoryDTO dto, VehicleLoanSubqueueTask subTask) {
        if (subTask != null) {
            dto.setCreateUser(getValueOrDefault(subTask.getCreateUser(), ""));
            dto.setCreatedDate(formatDate(subTask.getCreatedDate()));
            dto.setCompletedUser(getValueOrDefault(subTask.getCompletedUser(), ""));
            dto.setCompletedDate(formatDate(subTask.getCompletedDate()));
            dto.setTaskStatus(getValueOrDefault(subTask.getStatus(), ""));
            dto.setTaskId(String.valueOf(subTask.getTaskId()));
        }
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    @Transactional
    public void lockPendingSubTasks(String wiNum, String ppcNo, Long slno, String taskType) {
        List<VehicleLoanSubqueueTask> tasks = vehicleLoanSubqueueTaskRepository.findPendingTasksByWiNum(wiNum);
        for(VehicleLoanSubqueueTask vehicleLoanSubqueueTask : tasks){
            if (vehicleLoanSubqueueTask.getTaskType().equals(taskType) && vehicleLoanSubqueueTask.getStatus().equals("PENDING") ) {
                vehicleLoanSubqueueTask.setLockedOn(new Timestamp(System.currentTimeMillis()));
                vehicleLoanSubqueueTask.setLockedBy(ppcNo);
                vehicleLoanSubqueueTask.setLockFlg("Y");
                vehicleLoanSubqueueTaskRepository.save(vehicleLoanSubqueueTask);
            }
        }
    }

    /*
    @Transactional
    public void lockCifCreationTasks(String wiNum, String ppcNo, Long slno) {
        List<VehicleLoanSubqueueTask> tasks = vehicleLoanSubqueueTaskRepository.findPendingTasksByWiNum(wiNum);
        for(VehicleLoanSubqueueTask vehicleLoanSubqueueTask : tasks){
            if (vehicleLoanSubqueueTask.getTaskType().equals("CIF_CREATION") && vehicleLoanSubqueueTask.getStatus().equals("PENDING") ) {
                vehicleLoanSubqueueTask.setLockedOn(new Timestamp(System.currentTimeMillis()));
                vehicleLoanSubqueueTask.setLockedBy(ppcNo);
                vehicleLoanSubqueueTask.setLockFlg("Y");
                vehicleLoanSubqueueTaskRepository.save(vehicleLoanSubqueueTask);
            }
        }
    }
    */

    public List<VehicleLoanChargeWaiver> calculateProcessingFee(String slno,BigDecimal loanAmt,BigDecimal vehicleAmt,Boolean saveData,String wiNum){
        BigDecimal Processfee;
        List<VehicleLoanChargeWaiver> chgList=new ArrayList<>();
        VehicleLoanSubqueueTask task=null;
        if(saveData) {
             task = new VehicleLoanSubqueueTask();
            task.setSlno(Long.valueOf(slno));
            task.setWiNum(wiNum);
            task.setDecision(null);
            task.setTaskType("CHARGE_WAIVER");
            task.setCreatedDate(new Date());
            task.setCreateUser(usd.getPPCNo());
            task.setCreateSol(usd.getSolid());
            task.setStatus("BRCOMPLETED");
            task.setRemarks("SANCTIONED DATA");
            task.setCompletedDate(new Date());
            task.setCompletedUser(usd.getPPCNo());
            task.setLockFlg("N");
            taskService.saveSubTask(task);
            feeWaiverRepository.updateDelflag(Long.valueOf(slno));
        }
        List<WaiverDto.ProcessFeeWaiverDto> data=fetchRepository.fetchProccessingFee(slno);
        for (WaiverDto.ProcessFeeWaiverDto feeData :data) {
            Processfee=BigDecimal.ZERO;
            if("Y".equalsIgnoreCase(feeData.getStaticFixed())){
                Processfee=feeData.getValue();
            }
            if("Y".equalsIgnoreCase(feeData.getPercantage())){
                if("Y".equalsIgnoreCase(feeData.getLoanAmountPercantage())){
                    Processfee=loanAmt.multiply(feeData.getValue()).divide(new BigDecimal(100),2, RoundingMode.HALF_EVEN);
                }
                if("Y".equalsIgnoreCase(feeData.getVehiclePricePercantage())){
                    Processfee=vehicleAmt.multiply(feeData.getValue()).divide(new BigDecimal(100),2, RoundingMode.HALF_EVEN);
                }
            }
            if("Y".equalsIgnoreCase(feeData.getMaximumLimit())){
                Processfee= Processfee.compareTo(feeData.getMaximumValue())>0?feeData.getMaximumValue():Processfee;
            }
            VehicleLoanChargeWaiver chg = new VehicleLoanChargeWaiver();
            chg.setFeeName(feeData.getChargeName());

            chg.setFinalFee(Processfee);
            if(saveData) {
            chg.setFeeCode(feeData.getChargeCode());
            chg.setFeeValueRec(null);
            chg.setFeeValue(Processfee);
            chg.setTaskId(task.getTaskId());
            chg.setFeeRemarks(null);
            chg.setDecision(null);
            chg.setSlno(Long.valueOf(slno));
            chg.setWiNum(wiNum);
            chg.setWaiverFlg(feeData.getWaiver());
            chg.setLastModDate(new Date());
            chg.setLastModUser(usd.getPPCNo());
            chg.setLastModSol(usd.getSolid());
            chg.setFrequency(feeData.getFrequency());
            chg.setDelFlag("N");

                feeWaiverRepository.save(chg);
            }
            chgList.add(chg);
        }
        return chgList;
    }


@Transactional(rollbackOn = Exception.class)
    public void calculateProcessingFee(RBCPCCheckerSave master)
    {
        VehicleLoanSubqueueTask task = new VehicleLoanSubqueueTask();
        task.setSlno(Long.valueOf(master.getSlno()));
        task.setWiNum(master.getWinum());
        task.setDecision(null);
        task.setTaskType("CHARGE_WAIVER");
        task.setCreatedDate(new Date());
        task.setCreateUser(usd.getPPCNo());
        task.setCreateSol(usd.getSolid());
        task.setStatus("BRCOMPLETED");
        task.setRemarks("SANCTIONED DATA");
        task.setCompletedDate(new Date());
        task.setCompletedUser(usd.getPPCNo());
        task.setLockFlg("N");
        taskService.saveSubTask(task);
        feeWaiverRepository.updateDelflag(Long.valueOf(master.getSlno()));
        List<WaiverDto.ProcessFeeWaiverDto> data=fetchRepository.fetchProccessingFee(master.getSlno());
        BigDecimal Processfee;
        EligibilityDetails eligibilityDetails=null;
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(master.getWinum(), Long.valueOf(master.getSlno()));
        if (eligibilityDetailsOpt.isPresent())  {
             eligibilityDetails = eligibilityDetailsOpt.get();
        }
        for (WaiverDto.ProcessFeeWaiverDto feeData :data) {
            Processfee=BigDecimal.ZERO;
            if("Y".equalsIgnoreCase(feeData.getStaticFixed())){
                Processfee=feeData.getValue();
            }
            if("Y".equalsIgnoreCase(feeData.getPercantage())){
                if("Y".equalsIgnoreCase(feeData.getLoanAmountPercantage())){
                    Processfee=eligibilityDetails.getSancAmountRecommended().multiply(feeData.getValue()).divide(new BigDecimal(100),2, RoundingMode.HALF_EVEN);
                }
                if("Y".equalsIgnoreCase(feeData.getVehiclePricePercantage())){
                    Processfee=eligibilityDetails.getVehicleAmt().multiply(feeData.getValue()).divide(new BigDecimal(100),2, RoundingMode.HALF_EVEN);
                }
            }
            if("Y".equalsIgnoreCase(feeData.getMaximumLimit())){
                Processfee= Processfee.compareTo(feeData.getMaximumValue())>0?feeData.getMaximumValue():Processfee;
            }
            VehicleLoanChargeWaiver chg = new VehicleLoanChargeWaiver();
            chg.setFeeName(feeData.getChargeName());
            chg.setFeeCode(feeData.getChargeCode());
            chg.setFeeValueRec(null);
            chg.setFeeValue(Processfee);
            chg.setTaskId(task.getTaskId());
            chg.setFeeRemarks(null);
            chg.setDecision(null);
            chg.setSlno(Long.valueOf(master.getSlno()));
            chg.setWiNum(master.getWinum());
            chg.setWaiverFlg(feeData.getWaiver());
            chg.setFinalFee(Processfee);
            chg.setLastModDate(new Date());
            chg.setLastModUser(usd.getPPCNo());
            chg.setLastModSol(usd.getSolid());
            chg.setFrequency(feeData.getFrequency());
            chg.setDelFlag("N");
            feeWaiverRepository.save(chg);
        }
    }

    @Transactional
    public boolean rejectPendingTasksForWorkItem(String wiNum, String remarks) {
        // Find all pending tasks for the work item
        List<VehicleLoanSubqueueTask> pendingTasks = vehicleLoanSubqueueTaskRepository.findPendingTasksByWiNum(wiNum);

        if (pendingTasks.isEmpty()) {
            return false;
        }

        Date currentDate = new Date();
        String currentUser = usd.getPPCNo();

        // Update all pending tasks to rejected status
        for (VehicleLoanSubqueueTask task : pendingTasks) {
            // Update the task status to REJECTED
            task.setStatus("REJECTED");
            task.setRemarks(remarks);
            task.setCompletedDate(currentDate);
            task.setCompletedUser(currentUser);

            // Release any locks if present
            task.setLockFlg("N");
            task.setLockedBy(null);
            task.setLockedOn(null);

            vehicleLoanSubqueueTaskRepository.save(task);
        }

        return true;
    }
}
