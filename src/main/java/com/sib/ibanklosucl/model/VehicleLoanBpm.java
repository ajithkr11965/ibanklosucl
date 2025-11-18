package com.sib.ibanklosucl.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;

import javax.persistence.*;

@Entity
@Table(name = "VEHICLE_LOAN_BPM_WI")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleLoanBpm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @Column(name = "WI_NUM")
    private String wiNum;

    @Column(name = "SLNO")
    private Long slno;
    @Column(name = "FOLDERNAME")
    private String folderName;

    @Column(name = "PROCESSID")
    private String processID;

    @Column(name = "USERID")
    private String userID;

    @Column(name = "SYSTEMIP")
    private String systemIP;

    @Column(name = "CHILD")
    private String child;

    @Column(name = "PARENTINDEX")
    private String parentIndex;
    @Column(name = "PARENTURL")
    private String parentUrl;
    @Column(name = "CHILDINDEX")
    private String childIndex;
    @Column(name = "CHILDURL")
    private String childUrl;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "ADDURL")
    private String  addurl;


}



