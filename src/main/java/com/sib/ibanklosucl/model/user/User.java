package com.sib.ibanklosucl.model.user;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "USERS")
@Data
@Audited
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "last_login_time")
    private Date lastLoginTime;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "ip")
    private String ip;
    @Column(name = "sol_id")
    private String solID;
}

