package com.sib.ibanklosucl.service.dashboard.notification;
import com.sib.ibanklosucl.model.notification.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendPushNotification(String solId, String title, String subtitle, String url, String category, String priority) {
        try {
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setSolId(solId);
            notificationMessage.setTitle(title);
            notificationMessage.setSubtitle(subtitle);
            notificationMessage.setUrl(url);
            notificationMessage.setRead(false);
            notificationMessage.setCategory(category);
            notificationMessage.setPriority(priority);
            notificationMessage.setTimestamp(LocalDateTime.now());

            messagingTemplate.convertAndSend("/topic/sol/" + solId, notificationMessage);
            log.info("Push notification sent to solId: {}, title: {}", solId, title);
        } catch (Exception e) {
            log.error("Error sending push notification to solId: {}", solId, e);
            // Consider implementing a retry mechanism or fallback here
            // For now, we'll just log the error and continue
        }
    }
}
