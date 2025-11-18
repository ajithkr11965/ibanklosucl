package com.sib.ibanklosucl.model.integrations;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "EXPERIAN_HUNTER_RULES")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hunter_details_id")
    private VLHunterDetails hunterDetails;

    @Column(name = "score")
    private Integer score;

    @Column(name = "rule_count")
    private Integer ruleCount;

    @Column(name = "rule_id")
    private String ruleId;

    // Getters and setters
}
