package com.sib.ibanklosucl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "VEHICLE_LOAN_PROGRAM_FILE")
@Audited
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class VehicleLoanProgramFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "SLNO", nullable = false)
    private Long slNo;

    @Column(name = "APPLICANT_ID", nullable = false)
    private Long applicantId;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_PATH")
    private String filePath;

    @Column(name = "FILE_TYPE")
    private String fileType;

    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Column(name = "UPLOAD_DATE")
    private Date uploadDate;

    @Column(name = "DEL_FLG")
    private String delFlg;

    @Column(name = "DEL_USER")
    private String delUser;

    @Column(name = "DEL_DATE")
    private Date delDate;

    @Column(name = "CM_USER")
    private String cmUser;

    @Column(name = "CM_DATE")
    private Date cmDate;

    @Column(name = "REQ_IP_ADDR")
    private String reqIpAddr;

    @Column(name = "HOME_SOL")
    private String homeSol;

    @ManyToOne
    @JoinColumn(name = "PROGRAM_ID", insertable = false, updatable = false)
    @JsonBackReference
    private VehicleLoanProgram vehicleLoanProgram;

    @Column(name = "PROGRAM_ID")
    private Long programId;
}
