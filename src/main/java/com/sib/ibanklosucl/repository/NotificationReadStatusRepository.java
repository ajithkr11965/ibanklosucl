package com.sib.ibanklosucl.repository;

import com.sib.ibanklosucl.model.notification.NotificationReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationReadStatusRepository extends JpaRepository<NotificationReadStatus, Long> {

    // Check if a notification has been marked as read by a specific ppc
    boolean existsByNotificationIdAndPpc(Long notificationId, String ppc);

    // Fetch all notifications read by a specific ppc
    List<NotificationReadStatus> findByPpc(String ppc);
}
