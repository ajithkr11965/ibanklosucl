package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;
import com.sib.ibanklosucl.model.doc.VehicleLoanChargeWaiver;
import com.sib.ibanklosucl.model.doc.VehicleLoanRoiWaiver;
import com.sib.ibanklosucl.repository.VehicleLoanSubqueueTaskRepository;
import com.sib.ibanklosucl.repository.doc.VehicleLoanFeeWaiverRepository;
import com.sib.ibanklosucl.repository.doc.VehicleLoanRoiWaiverRepository;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class VehicleLoanSubqueueTaskService {
    @Autowired
    private VehicleLoanSubqueueTaskRepository subqueueTaskRepository;

    @Autowired
    private VehicleLoanMasterService loanMasterService;
        @Autowired
    private VehicleLoanRoiWaiverRepository vehicleLoanRoiWaiverRepository;
    @Autowired
    private VehicleLoanFeeWaiverRepository feeWaiverRepository;

//    public VehicleLoanSubqueueTask getSubTaskByType(Long slno,String type){
//        return subqueueTaskRepository.findBySlnoAndTaskType(slno,type);
//    }

    public VehicleLoanSubqueueTask getSubTaskByTypeAndStatus(Long slno,String type,String status){
        return subqueueTaskRepository.findBySlnoAndTaskTypeAndStatus(slno,type,status);
    }

    public List<VehicleLoanSubqueueTask> getSubTaskByTypeAndStatus2(Long slno,String type,String status){
        return subqueueTaskRepository.findBySlnoAndTaskTypeAndStatus2(slno,type,status);
    }
    public List<VehicleLoanSubqueueTask> getBySlno(Long slno){
      //  return subqueueTaskRepository.findBySlno(slno);
        return subqueueTaskRepository.findBySlnoAndStatus(slno,"PENDING");
    }
    public boolean isPending(Long slno,String taskType){
        return subqueueTaskRepository.countBySlnoAndTaskTypeAndStatus(slno,taskType,"PENDING")>0;
    }
    public boolean isPendingAndLocked(Long slno,String taskType){
        return subqueueTaskRepository.countBySlnoAndTaskTypeAndStatusAndLockFlg(slno,taskType,"PENDING","Y")>0;
    }
    public List<VehicleLoanSubqueueTask> getSubQueueTasks(Long slno,String taskType) {
        List<VehicleLoanSubqueueTask> tasks = subqueueTaskRepository.findBySlnoAndStatus(slno, "PENDING");
        if (tasks.isEmpty()) {
            tasks = subqueueTaskRepository.findTopBySlnoAndTaskTypeOrderByCompletedDateDesc(slno, taskType);
        }
        return tasks;
    }
    public VehicleLoanChargeWaiver getLatestCompletedChargeWaiver(Long taskId) {
        return feeWaiverRepository.findByTaskId(taskId)
                                      .stream()
                                      .findFirst()
                                      .orElse(null);
    }
    public VehicleLoanRoiWaiver getLatestCompletedRoiWaiver(Long taskId) {
        return vehicleLoanRoiWaiverRepository.findByTaskId(taskId)
                                      .stream()
                                      .findFirst()
                                      .orElse(null);
    }

    @Transactional
    public void saveSubTask(VehicleLoanSubqueueTask task){
         subqueueTaskRepository.save(task);
    }


    public VehicleLoanSubqueueTask findByTaskId(Long taskId){
        return subqueueTaskRepository.findByTaskId(taskId);
    }
}
