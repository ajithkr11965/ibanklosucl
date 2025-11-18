package com.sib.ibanklosucl.model.menuaccess;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "QUEUE_VARIATION")
@Getter
@Setter
@Entity
@ToString
public class QueueVariation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String solId;

    private String menuId;

    private int previousCount;

    private LocalDateTime timestamp;

    // Getters and Setters
}


