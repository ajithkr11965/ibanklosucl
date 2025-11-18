package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface VehicleLoanSubqueueTaskRepository extends JpaRepository<VehicleLoanSubqueueTask, Long> {

    List<VehicleLoanSubqueueTask> findByVehicleLoanMasterAndStatus(VehicleLoanMaster loanMaster, String status);

    List<VehicleLoanSubqueueTask> findByApplicantAndTaskType(Long applicantId, String taskType);


    VehicleLoanSubqueueTask findBySlnoAndTaskTypeAndApplicantAndStatusNot(Long slno, String taskType, VehicleLoanApplicant applicant, String status);

    VehicleLoanSubqueueTask findBySlnoAndTaskTypeAndStatus(Long slno,String taskType,String status);
    //Long countBySlnoAndTaskTypeAndStatusAndApplicant(Long slno,String taskType,String status,Long appid);
    @Query("SELECT t FROM VehicleLoanSubqueueTask t WHERE t.slno = :slno and t.taskType=:taskType AND t.status = :status order by t.taskId desc")
    List<VehicleLoanSubqueueTask> findBySlnoAndTaskTypeAndStatus2(Long slno, String taskType, String status);



    VehicleLoanSubqueueTask findByTaskId(Long taskId);
    List<VehicleLoanSubqueueTask> findBySlno(Long slno);
    List<VehicleLoanSubqueueTask> findBySlnoAndStatus(Long slno,String status);

    Long countBySlnoAndTaskTypeAndStatus(Long slno,String taskType,String status);
    Long countBySlnoAndTaskTypeAndStatusAndLockFlg(Long slno,String taskType,String status,String lock);

    @Modifying
    @Transactional
    @Query("UPDATE VehicleLoanSubqueueTask t SET t.status = :status WHERE t.taskId = :taskId")
    void updateTaskStatus(@Param("taskId") Long taskId, @Param("status") String status);

    @Modifying
    @Transactional
    @Query("UPDATE VehicleLoanSubqueueTask t SET t.completedDate = :completedDate WHERE t.taskId = :taskId")
    void updateCompletedDate(@Param("taskId") Long taskId, @Param("completedDate") Date completedDate);

    List<VehicleLoanSubqueueTask> findByVehicleLoanMaster(VehicleLoanMaster loanMaster);
    @Query("SELECT t FROM VehicleLoanSubqueueTask t WHERE t.wiNum = :wiNum AND t.status = 'PENDING'")
    List<VehicleLoanSubqueueTask> findPendingTasksByWiNum(@Param("wiNum") String wiNum);

    @Query("SELECT t FROM VehicleLoanSubqueueTask t WHERE t.slno = :slno AND t.lockedBy = :ppcNo AND t.lockFlg = 'Y'")
    List<VehicleLoanSubqueueTask> findLockedTasksByWiNumAndPpcNo(@Param("slno") Long slno, @Param("ppcNo") String ppcNo);

    @Query("SELECT t FROM VehicleLoanSubqueueTask t WHERE t.wiNum = :wiNum AND t.lockFlg = 'Y'")
    List<VehicleLoanSubqueueTask> findLockedTasksByWiNum(@Param("wiNum") String wiNum);
     @Query("SELECT count(*) FROM VehicleLoanSubqueueTask t WHERE t.wiNum = :wiNum AND t.lockFlg = :lockFlg")
    int countByWiNumAndLockFlg(@Param("wiNum") String wiNum,@Param("lockFlg") String lockFlg);

    @Query("SELECT t FROM VehicleLoanSubqueueTask t WHERE t.wiNum = :wiNum")
    List<VehicleLoanSubqueueTask> findTasksByWiNum(@Param("wiNum") String wiNum);
    @Query("SELECT t FROM VehicleLoanSubqueueTask t WHERE t.slno = :slno AND t.lockedBy = :ppcNo AND t.lockFlg = 'Y' AND t.taskType=:taskType")
    List<VehicleLoanSubqueueTask> findLockedTasksByWiNumAndPpcNoAndTaskType(@Param("slno") Long slno, @Param("ppcNo") String ppcNo, @Param("taskType") String taskType);

@Query("SELECT t FROM VehicleLoanSubqueueTask t WHERE t.slno = :slno AND t.taskType = :taskType AND t.status = 'COMPLETED' ORDER BY t.completedDate DESC")
List<VehicleLoanSubqueueTask> findTopBySlnoAndTaskTypeOrderByCompletedDateDesc(@Param("slno") Long slno, String taskType);
}
