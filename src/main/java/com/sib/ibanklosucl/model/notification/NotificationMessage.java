package com.sib.ibanklosucl.model.notification;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessage {
    private String solId;
    private String title;
    private String subtitle;
    private String url;
    private boolean isRead;
    private String category;
    private String priority;
    private LocalDateTime timestamp;
}
