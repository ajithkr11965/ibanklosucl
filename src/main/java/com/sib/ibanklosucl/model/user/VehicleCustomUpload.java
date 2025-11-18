package com.sib.ibanklosucl.model.user;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_CUSTOM_UPLOAD")
@Getter
@Setter
@Audited
@EntityListeners(AuditingEntityListener.class)
public class VehicleCustomUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INO", nullable = false)
    private Long ino;

    @Column(name = "WI_NUM", nullable = false)
    private String wiNum;

    @Column(name = "cmuser", length = 10)
    private String cmUser;
    @Column(name = "file_name", length = 40)
    private String fileName;
    @Column(name = "folder_name", length = 40)
    private String folderName;
    @Column(name = "child_flg", length = 1)
    private String childFlg;
    @Column(name = "file_ext", length = 10)
    private String fileExt;
    @Column(name = "remarks", length = 300)
    private String remarks;

    @Column(name = "cmdate")
    private Date cmDate;
    @Column(name = "solid")
    private String solID;
    @Column(name = "ipaddress", length = 20)
    private String ipaddress;


}
