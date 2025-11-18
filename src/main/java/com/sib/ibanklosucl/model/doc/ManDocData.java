package com.sib.ibanklosucl.model.doc;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Audited
@Data
@Entity
@Table(name = "MAN_DOC_DATA")
public class ManDocData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SLNO")
    private Long slno;

    @Column(name = "WI_NUM")
    private String wiNum;
    @Column(name = "DOC_NAME")
    private String docName;
    @Column(name = "DOC_DESC")
    private String docDesc;

    private Boolean uploadFlg;

    @Column(name = "last_Mod_User")
    private String lastModUser;
    @Column(name = "LAST_MOD_DATE")
    private Date lastModDate;
    @Column(name = "UPLOAD_USER")
    private String uploadUser;
    @Column(name = "UPLOAD_DATE")
    private Date uploadDate;
}
