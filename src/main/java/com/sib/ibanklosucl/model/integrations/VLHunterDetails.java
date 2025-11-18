package com.sib.ibanklosucl.model.integrations;

import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "VEHICLE_LOAN_HUNTER_DETAILS")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VLHunterDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private VehicleLoanApplicant vlHunterlist;

    @Column(name = "wi_num", length = 13)
    private String wiNum;

    @Column(name = "slno")
    private Long slno;

    @Column(name = "applicant_id")
    private Long applicantId;

    @Column(name = "interface")
    private String interfaceName;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "status_desc")
    private String statusDesc;

    @Column(name = "sequence_id")
    private String sequenceId;

    @Column(name = "decision_source")
    private String decisionSource;

    @Column(name = "decision")
    private String decision;

    @Column(name = "score")
    private Integer score;

    @Column(name = "decision_text")
    private String decisionText;

    @Column(name = "next_action")
    private String nextAction;

    @Column(name = "app_reference")
    private String appReference;

    @Column(name = "normalized_score")
    private Integer normalizedScore;

    @Column(name = "total_match_score")
    private String totalMatchScore;

    @Column(name = "matches")
    private Integer matches;

    @Column(name = "error_count")
    private Integer errorCount;

    @Column(name = "warning_count")
    private Integer warningCount;

    @OneToMany(mappedBy = "hunterDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Warning> warnings = new ArrayList<>();

    @OneToMany(mappedBy = "hunterDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchScheme> matchSchemes = new ArrayList<>();

    @OneToMany(mappedBy = "hunterDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rule> rules = new ArrayList<>();

    @Column(name = "response_type")
    private String responseType;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "response_message")
    private String responseMessage;

    @Lob
    @Column(name = "full_response")
    private String fullResponse;

    @Column(name = "timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(name = "cmuser", length = 10)
    private String cmUser;

    @Column(name = "cmdate")
    @Temporal(TemporalType.DATE)
    private Date cmDate;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "DEL_USER")
    private String delUser;

    @Column(name = "DEL_DATE")
    @Temporal(TemporalType.DATE)
    private Date delDate;

    @Column(name = "DEL_HOME_SOL")
    private String delHomeSol;

    @Lob
    @Column(name = "hunter_user_remarks")
    private String hunterUserRemarks;

    @Column(name = "REVIEW_USER")
    private String reviewUser;

    @Column(name = "REVIEW_DATE")
    @Temporal(TemporalType.DATE)
    private Date reviewDate;

    // Constructors
    public VLHunterDetails() {
    }

    // Getters and Setters
    // ... (include getters and setters for all fields)

    // Helper methods for managing relationships
    public void addWarning(Warning warning) {
        warnings.add(warning);
        warning.setHunterDetails(this);
    }

    public void removeWarning(Warning warning) {
        warnings.remove(warning);
        warning.setHunterDetails(null);
    }

    public void addMatchScheme(MatchScheme matchScheme) {
        matchSchemes.add(matchScheme);
        matchScheme.setHunterDetails(this);
    }

    public void removeMatchScheme(MatchScheme matchScheme) {
        matchSchemes.remove(matchScheme);
        matchScheme.setHunterDetails(null);
    }

    public void addRule(Rule rule) {
        rules.add(rule);
        rule.setHunterDetails(this);
    }

    public void removeRule(Rule rule) {
        rules.remove(rule);
        rule.setHunterDetails(null);
    }

    // You might want to override equals() and hashCode() methods
    // based on the business key of this entity

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        // Logic to handle before an update, if needed
    }
}
