package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.VehicleLoanSubqueueTat;
import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;
import com.sib.ibanklosucl.repository.VehicleLoanSubqueueTatRepository;
import com.sib.ibanklosucl.repository.VehicleLoanSubqueueTaskRepository;
import com.sib.ibanklosucl.utilies.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class VehicleLoanSubqueueTatService {

    @Autowired
    private VehicleLoanSubqueueTatRepository repository;

    @Autowired
    private VehicleLoanSubqueueTaskRepository taskRepository;

    @Transactional
    public VehicleLoanSubqueueTat saveTat(VehicleLoanSubqueueTat tat) {
        return repository.save(tat);
    }

    public VehicleLoanSubqueueTat getTatByTaskId(Long taskId) {
        return repository.findBySubqueueExitDateIsNullAndTaskIdAndDelFlg(taskId, "N");
    }

    @Transactional
    public void initialInsert(Long taskId, String cmuser, String wiNum, String subqueue, HttpServletRequest request) {
        VehicleLoanSubqueueTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        VehicleLoanSubqueueTat newSubqueueTat = new VehicleLoanSubqueueTat();
        newSubqueueTat.setWiNum(wiNum);
        newSubqueueTat.setTaskId(taskId);
        newSubqueueTat.setTaskType(task.getTaskType());
        newSubqueueTat.setReqIpAddr(CommonUtils.getClientIp(request));
        newSubqueueTat.setSubqueue(subqueue);
        newSubqueueTat.setSubqueueEntryUser(cmuser);
        newSubqueueTat.setSubqueueEntryDate(new Date());
        newSubqueueTat.setDelFlg("N");
        newSubqueueTat.setAction("INITIAL");
        saveTat(newSubqueueTat);
    }

    @Transactional
    public void updateTat(Long taskId, String cmuser, String wiNum, String subqueue, String action, String decision, HttpServletRequest request) {
        VehicleLoanSubqueueTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        VehicleLoanSubqueueTat subqueueTat = getTatByTaskId(taskId);
        if (subqueueTat == null) {
            // If no existing TAT entry, treat this as an initial insert
            initialInsert(taskId, cmuser, wiNum, subqueue, request);
            return;
        }

        subqueueTat.setSubqueueExitUser(cmuser);
        subqueueTat.setSubqueueExitDate(new Date());
        subqueueTat.setAction(action);
        saveTat(subqueueTat);

        if ("FORWARD".equals(action)) {
            VehicleLoanSubqueueTat newSubqueueTat = new VehicleLoanSubqueueTat();
            newSubqueueTat.setWiNum(wiNum);
            newSubqueueTat.setTaskId(taskId);
            newSubqueueTat.setTaskType(task.getTaskType());
            newSubqueueTat.setReqIpAddr(CommonUtils.getClientIp(request));
            newSubqueueTat.setSubqueue(decision);
            newSubqueueTat.setSubqueueEntryUser(cmuser);
            newSubqueueTat.setSubqueueEntryDate(new Date());
            newSubqueueTat.setDelFlg("N");
            newSubqueueTat.setAction(action);
            saveTat(newSubqueueTat);
        }
    }





    public List<VehicleLoanSubqueueTat> getSubqueueHistory(Long taskId) {
        return repository.findByTaskIdOrderBySubqueueEntryDateDesc(taskId);
    }
}
