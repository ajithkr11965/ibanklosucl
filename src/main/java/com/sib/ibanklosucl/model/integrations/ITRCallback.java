package com.sib.ibanklosucl.model.integrations;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "itr_callback")
public class ITRCallback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "client_transaction_id")
    private String clientTransactionId;

    private String message;

    @Column(name = "perfios_transaction_id")
    private String perfiosTransactionId;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
