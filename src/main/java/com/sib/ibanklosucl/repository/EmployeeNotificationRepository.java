package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.user.EmployeeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeNotificationRepository extends JpaRepository<EmployeeNotification, Long> {
    EmployeeNotification findByPpcNoAndSeen(String ppcNo,Boolean seen);

}
