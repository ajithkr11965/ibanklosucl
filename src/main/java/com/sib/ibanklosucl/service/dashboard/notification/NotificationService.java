package com.sib.ibanklosucl.service.dashboard.notification;

import com.sib.ibanklosucl.model.notification.Notification;
import com.sib.ibanklosucl.model.notification.NotificationReadStatus;
import com.sib.ibanklosucl.repository.NotificationReadStatusRepository;
import com.sib.ibanklosucl.repository.NotificationRepository;
import com.sib.ibanklosucl.exception.NotificationFetchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private NotificationReadStatusRepository notificationReadStatusRepository;


    @Transactional
    public void createNotification(String solId, String title, String subtitle, String url, String category, String priority) {
        try {
            Optional<Notification> existingNotification = notificationRepository
                    .findFirstBySolIdAndTitleAndSubtitleAndUrlAndCategoryAndPriorityAndIsReadFalseAndDelFlgOrderByTimestampDesc(
                            solId, title, subtitle, url, category, priority, "N");

            if (existingNotification.isPresent() &&
                    ChronoUnit.MINUTES.between(existingNotification.get().getTimestamp(), LocalDateTime.now()) < 5) {
                log.info("Duplicate notification prevented for solId: {}, title: {}", solId, title);
                return;
            }

            Notification notification = new Notification();
            notification.setSolId(solId);
            notification.setTitle(title);
            notification.setSubtitle(subtitle);
            notification.setUrl(url);
            notification.setRead(false);
            notification.setCategory(category);
            notification.setPriority(priority);
            notification.setTimestamp(LocalDateTime.now());
            notification.setDelFlg("N");

            notificationRepository.save(notification);
            log.info("New notification created for solId: {}, title: {}", solId, title);

            pushNotificationService.sendPushNotification(solId, title, subtitle, url, category, priority);
        } catch (Exception e) {
            log.error("Error creating notification for solId: {}", solId, e);
            throw new RuntimeException("Failed to create notification", e);
        }
    }

    @Transactional
    public void markNotificationsAsRead(String solId) {
        try {
            LocalDateTime now = LocalDateTime.now();
            int updatedCount = notificationRepository.markAllAsRead(solId, now);
            log.info("Marked {} notifications as read for solId: {}", updatedCount, solId);
        } catch (Exception e) {
            log.error("Error marking notifications as read for solId: {}", solId, e);
            throw new RuntimeException("Failed to mark notifications as read", e);
        }
    }

    // Fetch paginated notifications filtered by solId and ppc
    public Page<Notification> getUnreadNotifications(String solId, String ppc, Pageable pageable) {
        try {
            return notificationRepository.findUnreadNotificationsBySolIdAndPpc(solId, ppc, pageable);
        } catch (Exception e) {
            log.error("Error fetching unread notifications for solId: {}, ppc: {}", solId, ppc, e);
            throw new NotificationFetchException("Failed to fetch notifications", e);
        }
    }

    // Mark notifications as read for a specific solId and ppc
    @Transactional
    public void markNotificationsAsRead(String solId, String ppc) {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Notification> notifications = notificationRepository.findBySolIdAndIsReadFalseAndDelFlg(solId, "N");

            for (Notification notification : notifications) {
                if (!notificationReadStatusRepository.existsByNotificationIdAndPpc(notification.getId(), ppc)) {
                    NotificationReadStatus readStatus = new NotificationReadStatus();
                    readStatus.setNotification(notification);
                    readStatus.setPpc(ppc);
                    readStatus.setReadTimestamp(now);

                    notificationReadStatusRepository.save(readStatus);
                }
            }
            log.info("Marked notifications as read for solId: {}, ppc: {}", solId, ppc);
        } catch (Exception e) {
            log.error("Error marking notifications as read for solId: {}, ppc: {}", solId, ppc, e);
            throw new RuntimeException("Failed to mark notifications as read", e);
        }
    }

    // Count unread notifications for solId and ppc
    public long getUnreadNotificationCount(String solId, String ppc) {
        try {
            return notificationRepository.countUnreadNotificationsBySolIdAndPpc(solId, ppc);
        } catch (Exception e) {
            log.error("Error counting unread notifications for solId: {}, ppc: {}", solId, ppc, e);
            throw new NotificationFetchException("Failed to count unread notifications", e);
        }
    }

    public List<Notification> getFilteredNotifications(String solId, String category, String priority) {
        try {
            if (category != null && !category.isEmpty()) {
                return notificationRepository.findBySolIdAndCategoryAndIsReadFalseAndDelFlg(solId, category, "N");
            } else if (priority != null && !priority.isEmpty()) {
                return notificationRepository.findBySolIdAndPriorityAndIsReadFalseAndDelFlg(solId, priority, "N");
            }
            return notificationRepository.findBySolIdAndIsReadFalseAndDelFlg(solId, "N");
        } catch (Exception e) {
            log.error("Error fetching filtered notifications for solId: {}", solId, e);
            throw new NotificationFetchException("Failed to fetch notifications", e);
        }
    }

    public List<Notification> getUnreadNotifications(String solId) {
        try {
            return notificationRepository.findBySolIdAndIsReadFalseAndDelFlg(solId, "N");
        } catch (Exception e) {
            log.error("Error fetching unread notifications for solId: {}", solId, e);
            throw new NotificationFetchException("Failed to fetch unread notifications", e);
        }
    }

    public Page<Notification> getNotificationsWithPagination(String solId, Pageable pageable) {
        try {
            return notificationRepository.findBySolIdAndDelFlgOrderByTimestampDesc(solId, "N", pageable);
        } catch (Exception e) {
            log.error("Error fetching paginated notifications for solId: {}", solId, e);
            throw new NotificationFetchException("Failed to fetch paginated notifications", e);
        }
    }

    @Transactional
    public void cleanupOldNotifications(LocalDateTime cutoffDate) {
        try {
            int deletedCount = notificationRepository.markOldNotificationsAsDeleted(cutoffDate, LocalDateTime.now());
            log.info("Marked {} old notifications as deleted", deletedCount);
        } catch (Exception e) {
            log.error("Error cleaning up old notifications", e);
            throw new RuntimeException("Failed to clean up old notifications", e);
        }
    }

    public long getUnreadNotificationCount(String solId) {
        try {
            return notificationRepository.countUnreadNotifications(solId);
        } catch (Exception e) {
            log.error("Error counting unread notifications for solId: {}", solId, e);
            throw new NotificationFetchException("Failed to count unread notifications", e);
        }
    }
}
