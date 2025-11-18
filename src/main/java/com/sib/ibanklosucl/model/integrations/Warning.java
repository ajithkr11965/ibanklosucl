package com.sib.ibanklosucl.model.integrations;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "EXPERIAN_HUNTER_WARNINGS")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Warning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hunter_details_id")
    private VLHunterDetails hunterDetails;

    @Column(name = "message")
    private String message;

    // Getters and setters
}

