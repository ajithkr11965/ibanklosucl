package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.ExperianData;
import com.sib.ibanklosucl.model.VehicleLoanLock;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;

import javax.transaction.Transactional;
import java.util.List;

public interface VehicleLoanHelpdeskService {
     /**
     * Retrieves work item details
     * @param wiNum Work item number
     * @return VehicleLoanMaster object
     */
    VehicleLoanMaster getWorkItemDetails(String wiNum);


    /**
     * Retrieves work item details
     * @param wiNum Work item number
     * @return experianData object
     */
    ExperianData getExperianWorkItemDetails(String wiNum);

    /**
     * Retrieves work item lock information
     * @param wiNum Work item number
     * @return VehicleLoanLock object or null if no active lock
     */
    VehicleLoanLock getWorkItemLock(String wiNum);

    /**
     * Retrieves list of locked subqueue tasks
     * @param wiNum Work item number
     * @return List of locked VehicleLoanSubqueueTask objects
     */
    List<VehicleLoanSubqueueTask> getSubqueueTasks(String wiNum);

    /**
     * Resets documentation for a work item
     * @param wiNum Work item number
     * @param remarks Action remarks
     * @param userId User performing the action
     * @throws Exception if operation fails
     */
    void resetDocumentation(String wiNum, String remarks, String userId) throws Exception;

    void resetExperian(String wiNum, String panNumber,String remarks, String userId) throws Exception;

    @Transactional
    void manageAcopn(String wiNum, String remarks, String queueAction, String userId) throws Exception;


    @Transactional
    void processCreditComplete(String pan, String wiNum);

    /**
     * Releases main lock on work item
     * @param wiNum Work item number
     * @param remarks Action remarks
     * @param userId User performing the action
     * @throws Exception if operation fails
     */
    void releaseLock(String wiNum, String remarks, String userId) throws Exception;

    /**
     * Releases all child locks for a work item
     * @param wiNum Work item number
     * @param remarks Action remarks
     * @param userId User performing the action
     * @throws Exception if operation fails
     */
    void releaseChildLocks(String wiNum, String remarks, String userId) throws Exception;

    /**
     * Checks if document is signed
     * @param wiNum Work item number
     * @return true if document is signed, false otherwise
     */
    boolean isDocumentSigned(String wiNum);

    /**
     * Gets count of locked tasks
     * @param wiNum Work item number
     * @return number of locked tasks
     */
    int getLockedTasksCount(String wiNum);

    boolean workItemExixts(String wiNum);
}
