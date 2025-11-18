package com.sib.ibanklosucl.repository;


import com.sib.ibanklosucl.model.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


    // Fetch unread notifications for a specific solId and exclude those marked read by ppc
    @Query("SELECT n FROM Notification n WHERE n.solId = ?1 AND n.delFlg = 'N' AND NOT EXISTS " +
            "(SELECT r FROM NotificationReadStatus r WHERE r.notification.id = n.id AND r.ppc = ?2) ORDER BY n.timestamp DESC")
    Page<Notification> findUnreadNotificationsBySolIdAndPpc(String solId, String ppc, Pageable pageable);

    // Mark all notifications as read by solId
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readTimestamp = ?2 WHERE n.solId = ?1 AND n.isRead = false AND n.delFlg = 'N'")
    int markAllAsReadBySolId(String solId, LocalDateTime readTimestamp);

    // Count unread notifications for a specific solId and exclude those marked read by ppc
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.solId = ?1 AND n.isRead = false AND n.delFlg = 'N' AND NOT EXISTS " +
            "(SELECT r FROM NotificationReadStatus r WHERE r.notification.id = n.id AND r.ppc = ?2)")
    long countUnreadNotificationsBySolIdAndPpc(String solId, String ppc);

    List<Notification> findBySolIdAndIsReadFalseAndDelFlg(String solId, String delFlg);

    List<Notification> findBySolIdAndDelFlg(String solId, String delFlg);

    List<Notification> findBySolIdAndCategoryAndIsReadFalseAndDelFlg(String solId, String category, String delFlg);

    List<Notification> findBySolIdAndPriorityAndIsReadFalseAndDelFlg(String solId, String priority, String delFlg);

    Page<Notification> findBySolIdAndDelFlgOrderByTimestampDesc(String solId, String delFlg, Pageable pageable);

    Optional<Notification> findFirstBySolIdAndTitleAndSubtitleAndUrlAndCategoryAndPriorityAndIsReadFalseAndDelFlgOrderByTimestampDesc(
            String solId, String title, String subtitle, String url, String category, String priority, String delFlg);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readTimestamp = ?2 WHERE n.solId = ?1 AND n.isRead = false AND n.delFlg = 'N'")
    int markAllAsRead(String solId, LocalDateTime readTimestamp);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.solId = ?1 AND n.isRead = false AND n.delFlg = 'N'")
    long countUnreadNotifications(String solId);

    @Modifying
    @Query("UPDATE Notification n SET n.delFlg = 'Y', n.lastUpdateTimestamp = ?2 WHERE n.timestamp < ?1 AND n.delFlg = 'N'")
    int markOldNotificationsAsDeleted(LocalDateTime cutoffDate, LocalDateTime updateTimestamp);
}

