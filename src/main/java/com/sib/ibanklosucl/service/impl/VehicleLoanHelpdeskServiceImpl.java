package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.ExperianDataHistRepository;
import com.sib.ibanklosucl.repository.VehicleLoanLockRepository;
import com.sib.ibanklosucl.repository.VehicleLoanMasterRepository;
import com.sib.ibanklosucl.repository.VehicleLoanSubqueueTaskRepository;
import com.sib.ibanklosucl.repository.doc.LegalRepositry;
import com.sib.ibanklosucl.repository.program.ExperianDataRepository;
import com.sib.ibanklosucl.service.HelpdeskAuditService;
import com.sib.ibanklosucl.service.VehicleLoanHelpdeskService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanTatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class VehicleLoanHelpdeskServiceImpl implements VehicleLoanHelpdeskService {

    @Autowired
    private VehicleLoanMasterRepository vehicleLoanMasterRepository;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private VehicleLoanLockRepository vehicleLoanLockRepository;

    @Autowired
    private ExperianDataRepository experianDataRepository;

    @Autowired
    private ExperianDataHistRepository experianDataHistRepository;

    @Autowired
    private LegalRepositry legalRepositry;

    @Autowired
    private VehicleLoanSubqueueTaskRepository subqueueTaskRepository;

    @Autowired
    private HelpdeskAuditService auditService;

    @Autowired
    private VehicleLoanTatService tatService;

    @Value("${application.lock.timeout-minutes:30}")
    private int lockTimeoutMinutes;

    @Override
    @Transactional
    public VehicleLoanMaster getWorkItemDetails(String wiNum) {
        log.info("Fetching work item details for: {}", wiNum);
        try {
            return vehicleLoanMasterRepository.findByWiNumAndActiveFlg(wiNum,"Y")
                .orElseThrow(() -> new EntityNotFoundException("Work item not found: " + wiNum));
        } catch (Exception e) {
            log.error("Error fetching work item details for {}: ", wiNum, e);
            throw new RuntimeException("Failed to fetch work item details", e);
        }
    }

    @Override
    public ExperianData getExperianWorkItemDetails(String wiNum) {
        log.info("Fetching Experian work item details for: {}", wiNum);
        try {
            return experianDataRepository.findExperianByWiNumAndActiveFlg(wiNum,"Y")
                    //.orElseThrow(() -> new EntityNotFoundException("Experian Work item not found: " + wiNum));
                    .orElse(null);
        } catch (Exception e) {
            log.error("Error fetching work item details for {}: ", wiNum, e);
            throw new RuntimeException("Failed to fetch Experian work item details", e);
        }

    }

    @Override
    @Transactional
    public VehicleLoanLock getWorkItemLock(String wiNum) {
        log.debug("Fetching lock information for work item: {}", wiNum);
        try {
            return vehicleLoanLockRepository.findByWiNumAndDelFlgAndLockFlg(wiNum, "N", "Y")
                .orElse(null);
        } catch (Exception e) {
            log.error("Error fetching lock information for {}: ", wiNum, e);
            throw new RuntimeException("Failed to fetch lock information", e);
        }
    }

    @Override
    @Transactional
    public List<VehicleLoanSubqueueTask> getSubqueueTasks(String wiNum) {
        log.debug("Fetching subqueue tasks for work item: {}", wiNum);
        try {
            return subqueueTaskRepository.findTasksByWiNum(wiNum);
        } catch (Exception e) {
            log.error("Error fetching subqueue tasks for {}: ", wiNum, e);
            throw new RuntimeException("Failed to fetch subqueue tasks", e);
        }
    }

    @Override
    @Transactional
    public void resetDocumentation(String wiNum, String remarks, String userId) throws Exception {
        log.info("Resetting documentation for work item: {}", wiNum);

        try {
            // Validate work item exists and check queue
            VehicleLoanMaster master = getWorkItemDetails(wiNum);

            if (master == null) {
                throw new EntityNotFoundException("Work item not found: " + wiNum);
            }

            if (!"BD".equals(master.getQueue())) {
                throw new IllegalStateException("Documentation reset is only allowed for BD queue");
            }

            // Check if there are any active locks
            VehicleLoanLock activeLock = getWorkItemLock(wiNum);
            if (activeLock != null) {
                // Check if lock has timed out
                if (isLockTimedOut(activeLock.getLockedOn())) {
                    log.info("Found timed out lock, releasing it first");
                    releaseLock(wiNum, "Auto-released due to timeout", "SYSTEM");
                } else {
                    throw new IllegalStateException(
                        String.format("Work item is locked by %s since %s",
                            activeLock.getLockedBy(),
                            activeLock.getLockedOn())
                    );
                }
            }

            // Backup current state
            String currentDocMode = master.getDocMode();
            boolean isDocSigned = isDocumentSigned(wiNum);

            // Perform reset operations
            master.setDocMode("");

            master.setDocQueueOverallStatus(null);
            vehicleLoanMasterRepository.save(master);

            // Delete legality invitees
            deleteLegalityInvitees(wiNum);

            // Log additional details about the reset
            String additionalInfo = String.format(
                "Previous doc mode: %s, Document was %s before reset",
                currentDocMode,
                isDocSigned ? "signed" : "not signed"
            );

            log.info("Documentation reset completed for work item: {}, {}", wiNum, additionalInfo);

        } catch (Exception e) {
            log.error("Error resetting documentation for {}: ", wiNum, e);
            throw e;
        }
    }
    @Transactional
    @Override
    public void resetExperian(String wiNum, String panNumber, String remarks, String userId) throws Exception {
        log.info("Resetting experian for work item: {}", wiNum);
        try{
            List<ExperianData> dataList = experianDataRepository.findByWiNumAndPan(wiNum,panNumber);
            // Check if there are any active locks
            VehicleLoanLock activeLock = getWorkItemLock(wiNum);
            if (activeLock != null) {
                // Check if lock has timed out
                if (isLockTimedOut(activeLock.getLockedOn())) {
                    log.info("Found timed out lock, releasing it first");
                    releaseLock(wiNum, "Auto-released due to timeout", "SYSTEM");
                } else {
                    throw new IllegalStateException(
                            String.format("Work item is locked by %s since %s",
                                    activeLock.getLockedBy(),
                                    activeLock.getLockedOn())
                    );
                }
            }

            // Perform reset operations
           if(!dataList.isEmpty()){
                List<ExperianDataHist> audList = dataList.stream().map(data -> {
                    ExperianDataHist audit = new ExperianDataHist();
                    audit.setWiNum((data.getWiNum()));
                    audit.setPan((data.getPan()));
                    audit.setActiveFlg(data.getActiveFlg());
                    audit.setAppId((data.getAppId()));
                    audit.setCmDate(data.getCmDate());
                    audit.setCmDate(data.getCmDate());
                    audit.setIno(data.getIno());
                    audit.setCmUser(data.getCmUser());
                    audit.setExpJpdRep(data.getExpJpdRep());
                    audit.setExpJpdRep(data.getExpJpdRep());
                    audit.setOrderId(data.getOrderId());
                    audit.setSlNo(data.getSlNo());
                    audit.setSucFlg(data.getSucFlg());
                    return audit;
                }).toList();

               experianDataHistRepository.saveAll(audList);
            experianDataRepository.deleteByWiNumAndPan(wiNum,panNumber);
            } else {
                throw new RuntimeException("Pan number for this workItem doesn't Exist");
            }

        }catch (Exception e) {
            log.error("Error resetting documentation for {}: ", wiNum, e);
            throw e;
        }

    }

    private ExperianData getExperianDataDetails(String wiNum,String panNumber) {
        log.info("Fetching work item details for: {}", wiNum);
        try {
            return experianDataRepository.findExperianByWiNumAndActiveFlg(wiNum,"Y")
                    .orElseThrow(() -> new EntityNotFoundException("Work item not found: " + wiNum));
        } catch (Exception e) {
            log.error("Error fetching work item details for {}: ", wiNum, e);
            throw new RuntimeException("Failed to fetch work item details", e);
        }
    }


    @Override
    @Transactional
    public void manageAcopn(String wiNum, String remarks, String queueAction, String userId) throws Exception {
        log.info("Managing ACOPN for WI: {}, action: {}", wiNum, queueAction);

        // 1. Fetch and validate the master record
        VehicleLoanMaster master = getWorkItemDetails(wiNum);
        if (master == null) {
            throw new EntityNotFoundException("Work item not found: " + wiNum);
        }

        if (!"ACOPN".equalsIgnoreCase(master.getQueue())) {
            throw new IllegalStateException("Only allowed for work items in ACOPN queue.");
        }

        // 2. Check if account opening has already happened
        if ("Y".equalsIgnoreCase(master.getAccOpened()) && !queueAction.equals("PD")) {
            throw new IllegalStateException("Account opening is already completed. Action not allowed.");
        }

        // 3. Check for active lock
        VehicleLoanLock lock = getWorkItemLock(wiNum);
        if (lock != null && !isLockTimedOut(lock.getLockedOn())) {
            throw new IllegalStateException(String.format(
                    "Work item is locked by %s since %s", lock.getLockedBy(), lock.getLockedOn()
            ));
        }
        // Optionally auto-release lock if timed out
        if (lock != null && isLockTimedOut(lock.getLockedOn())) {
            releaseLock(wiNum, "Auto-released due to timeout", "SYSTEM");
        }

        // 4. Perform action based on queueAction
        switch (queueAction) {
            case "REJ":
                // REJECT
                master.setRejFlg("Y");
                master.setRejDate(new Date());
                master.setRejUser(userId);
                master.setRejQueue(master.getQueue());
                master.setQueue("NIL"); // or whatever final queue

                // Example: also update queue date here
                master.setQueueDate(new Date());

                // Update TAT or other logs
                tatService.updateTat(master.getSlno(), userId, master.getWiNum(), "NIL");
                vehicleLoanMasterRepository.save(master);
                break;

            case "PD":
                // MARK PD
                master.setQueue("PD");
                master.setPdUser("SYSTEM");
                master.setPdDate(new Date());
                master.setQueueDate(new Date()); // if you want to track queue date changes

                tatService.updateTat(master.getSlno(), userId, master.getWiNum(), "PD");
                vehicleLoanMasterRepository.save(master);
                break;

            case "REV":
                // REVOKE to previous revision (for example, revert to BD revision)
                Long bdRev = vehicleLoanMasterRepository.findLastRevisionForQueue(master.getWiNum(), "BD");
                if (bdRev == null) {
                    throw new IllegalStateException("No BD revision found. Cannot revert to BD queue state.");
                }

                // 1) Revert live table to that revision
                revertMasterToRevision(master.getSlno(), bdRev);
                entityManager.flush();
                entityManager.clear();

                // 2) Re-fetch the updated entity to set queueDate = new Date()
                VehicleLoanMaster updatedMaster = vehicleLoanMasterRepository
                        .findById(master.getSlno())
                        .orElseThrow(() -> new EntityNotFoundException("VehicleLoanMaster not found after revert: " + master.getSlno()));

                // E.g., set queue date (system date) so that a new Envers revision is created
                updatedMaster.setQueueDate(new Date());

                // 3) Save, triggers new Envers revision
                vehicleLoanMasterRepository.save(updatedMaster);

                // 4) TAT update or other logs
                tatService.updateTat(master.getSlno(), userId, master.getWiNum(), "BD");
                break;

            default:
                throw new IllegalArgumentException("Invalid queueAction: " + queueAction + " (only REJ, REV, PD are allowed)");
        }

        log.info("manageAcopn completed for WI: {}, action: {}", wiNum, queueAction);
    }

    @Transactional
    public void revertMasterToRevision(Long slno, Long rev) {
        // 1) Run the custom native query to copy from AUD -> MASTER
        vehicleLoanMasterRepository.revertMasterToRevision(slno, rev);


        log.info("Successfully reverted SLNO {} to revision {}", slno, rev);
    }


    @Override
    @Transactional
    public void releaseLock(String wiNum, String remarks, String userId) throws Exception {
        log.info("Releasing lock for work item: {}", wiNum);

        try {
            VehicleLoanLock lock = vehicleLoanLockRepository
                .findByWiNumAndDelFlgAndLockFlg(wiNum, "N", "Y")
                .orElseThrow(() -> new IllegalStateException("No active lock found"));

            // Backup lock details for audit
            String previousLockedBy = lock.getLockedBy();
            Date previousLockedOn = lock.getLockedOn();

            // Release lock
            lock.setLockFlg("N");
            lock.setReleasedBy(userId);
            lock.setReleasedOn(new Date());
            vehicleLoanLockRepository.save(lock);

            log.info("Lock released for work item: {}, Previously locked by: {} since: {}",
                wiNum, previousLockedBy, previousLockedOn);

        } catch (Exception e) {
            log.error("Error releasing lock for {}: ", wiNum, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void releaseChildLocks(String wiNum, String remarks, String userId) throws Exception {
        log.info("Releasing child locks for work item: {}", wiNum);

        try {
            // Validate work item exists and check queue
            VehicleLoanMaster master = getWorkItemDetails(wiNum);
            if (!"BD".equals(master.getQueue())) {
                throw new IllegalStateException("Child lock release is only allowed for BD queue");
            }

            List<VehicleLoanSubqueueTask> lockedTasks =
                subqueueTaskRepository.findLockedTasksByWiNum(wiNum);

            if (lockedTasks.isEmpty()) {
                throw new IllegalStateException("No locked child tasks found");
            }

            // Backup locked tasks info for audit
            Map<String, String> previousLockInfo = lockedTasks.stream()
                .collect(Collectors.toMap(
                    VehicleLoanSubqueueTask::getTaskType,
                    task -> String.format("Locked by %s since %s",
                        task.getLockedBy(),
                        task.getLockedOn())
                ));

            // Release all locks
            for (VehicleLoanSubqueueTask task : lockedTasks) {
                task.setLockFlg("N");
            }

            subqueueTaskRepository.saveAll(lockedTasks);

            log.info("Released {} child locks for work item: {}, Previous lock info: {}",
                lockedTasks.size(), wiNum, previousLockInfo);

        } catch (Exception e) {
            log.error("Error releasing child locks for {}: ", wiNum, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean isDocumentSigned(String wiNum) {
        try {
            // Implement your logic to check if document is signed
            // This is a placeholder implementation
            VehicleLoanMaster master = getWorkItemDetails(wiNum);
            return master != null && "SIGNED".equals(master.getDocMode());
        } catch (Exception e) {
            log.error("Error checking document signed status for {}: ", wiNum, e);
            return false;
        }
    }

    @Override
    @Transactional
    public int getLockedTasksCount(String wiNum) {
        try {
            return subqueueTaskRepository.countByWiNumAndLockFlg(wiNum, "Y");
        } catch (Exception e) {
            log.error("Error counting locked tasks for {}: ", wiNum, e);
            return 0;
        }
    }


    public boolean workItemExixts(String wiNum) {
        return experianDataRepository.countByWiNum(wiNum) >0;
    }

    private boolean isLockTimedOut(Date lockedOn) {
        if (lockedOn == null) return false;

        LocalDateTime lockTime = lockedOn.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        return lockTime.plusMinutes(lockTimeoutMinutes)
            .isBefore(LocalDateTime.now());
    }

    private void deleteLegalityInvitees(String wiNum) {
        legalRepositry.deleteByWiNum(wiNum);
        log.info("Deleting legality invitees for work item: {}", wiNum);
        // Add your implementation here
    }

    @Override
    public void processCreditComplete(String pan, String wiNum) {
        // Step 1: Select APPID from EXPERIAN_DATA


        String appIdSql = "SELECT DISTINCT APPID FROM EXPERIAN_DATA WHERE PAN = ? AND WI_NUM = ?";
        try {
        String appId = jdbcTemplate.queryForObject(appIdSql, String.class, pan, wiNum);


        // If no APPID is found, handle the case (e.g., throw an exception, log it)
       /* if (appId == null) {
            // Log or handle the case where no APPID exists for the given PAN and WI_NUM.

        }*/
        String creditCompleteSql = "SELECT CREDIT_COMPLETE FROM VEHICLE_LOAN_APPLICANTS WHERE APPLICANT_ID = ? AND DEL_FLG = 'N'";
        String creditComplete = null;
        // Step 2: Select CREDIT_COMPLETE from VEHICLE_LOAN_APPLICANTS
        try {


          creditComplete = jdbcTemplate.queryForObject(creditCompleteSql, String.class, appId);
        }catch(Exception e) {

            System.out.println("No applicant found with APPID: " + appId);
           throw new IllegalArgumentException("No application found for the given PAN and WI_NUM."); // Or handle as per your business logic
        }
        // Step 3: Conditionally update based on the result
        if ("Y".equalsIgnoreCase(creditComplete)) {
            String updateSql = "UPDATE VEHICLE_LOAN_APPLICANTS SET CREDIT_COMPLETE = 'N' WHERE APPLICANT_ID = ?";
            jdbcTemplate.update(updateSql, appId);
        }
        }catch(Exception e) {
            throw new IllegalArgumentException("No application found for the given PAN and Work Item.");
        }
    }


}
