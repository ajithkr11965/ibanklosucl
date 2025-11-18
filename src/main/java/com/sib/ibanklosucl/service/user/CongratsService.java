package com.sib.ibanklosucl.service.user;

import com.sib.ibanklosucl.model.user.EmployeeNotification;
import com.sib.ibanklosucl.repository.EmployeeNotificationRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class CongratsService {

    @Autowired
    private EmployeeNotificationRepository notificationRepository;
    @Autowired
    private FetchRepository fetchRepository;



    // Method to insert notification when queue is updated to "PD"
    public EmployeeNotification sendDisbursementNotification(String solId,String ppcno) {
        // Fetch all employees for the given solId
        EmployeeNotification notification=null;
        Map<String, Object> disBurseData=fetchRepository.getNotifyData(solId,ppcno);
        if(disBurseData.get("wiNumList")!=null) {
            String workItemNo= (String) disBurseData.get("wiNumList");
            String totalDisbursements = (String) disBurseData.get("count");
            String disbursementMessage = "Great job team! The vehicle loan work item number <a class='fw-bold'>"
                    + workItemNo + "</a> has been successfully disbursed.Your dedication and effort are truly appreciated let's keep up the excellent work! ";
            String milestoneMessage = "In total, you have now reached  <a class='fw-bold'>" + totalDisbursements
                    + "</a> disbursement/s, marking another milestone for our branch!";
             notification = notificationRepository.findByPpcNoAndSeen(ppcno,false);
            if(notification==null){
                notification=new EmployeeNotification();
            }
            notification.setPpcNo(ppcno);
            notification.setMessage(disbursementMessage + "|" + milestoneMessage);
            notification.setSeen(false);
            notification.setCreatedAt(new Date());
            notification.setSolID(solId);
            notificationRepository.save(notification);
        }
        return notification;
    }

    // Method to mark the notification as seen
    public void markNotificationAsSeen(String ppcno) {
        EmployeeNotification notification = notificationRepository.findByPpcNoAndSeen(ppcno,false);
        if (notification!=null) {
            notification.setSeen(true);
            notificationRepository.save(notification);
        }
    }
}
