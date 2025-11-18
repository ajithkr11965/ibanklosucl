package com.sib.ibanklosucl.model.notification;

import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
@Entity
@Table(name = "VEHICLE_LOAN_NOTIFICATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String solId;

    @Column(nullable = false)
    private String title;

    @Column
    private String subtitle;

    @Column
    private String url;

    @Column(nullable = false)
    private boolean isRead;

    @Column
    private String category;

    @Column
    private String priority;

    @Column
    private String delFlg;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private LocalDateTime readTimestamp;
    @Column
    private LocalDateTime lastUpdateTimestamp;
}

