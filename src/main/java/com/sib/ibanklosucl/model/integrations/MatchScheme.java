package com.sib.ibanklosucl.model.integrations;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "EXPERIAN_HUNTER_MATCH_SCHEMES")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class MatchScheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hunter_details_id")
    private VLHunterDetails hunterDetails;

    @Column(name = "score")
    private Integer score;

    @Column(name = "scheme_id")
    private Integer schemeId;

    // Getters and setters
}
