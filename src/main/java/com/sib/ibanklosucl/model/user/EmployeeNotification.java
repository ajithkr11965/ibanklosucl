package com.sib.ibanklosucl.model.user;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "EMPLOYEE_NOTIFICATIONS")
public class EmployeeNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ppcno",length = 10)
    private String ppcNo;
    @Column(name = "solid",length = 10)
    private String solID;

    @Column(name = "message",length = 1000)
    private String message;
    private Boolean seen;
    private Date createdAt;

}
