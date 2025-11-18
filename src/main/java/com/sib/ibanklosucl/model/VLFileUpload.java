package com.sib.ibanklosucl.model;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Data
@Entity
@Audited
@Table(name = "VL_FILE_UPLOAD")
public class VLFileUpload {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;
    @Column(name = "slno")
    private Long slno;

    @Column(name = "workitem")
    private String workItem;

    @Column(name = "appid")
    private Long appId;

    @Column(name = "apptype")
    private String appType;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "fileextension")
    private String fileExtension;

    @Column(name = "filecode")
    private String fileCode;


}
