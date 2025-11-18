package com.sib.ibanklosucl.model.doc;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Audited
@Data
@Entity
@Table(name = "LEGALITY_INVITEES")
public class LegalityInvitees {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(name = "SLNO")
        private Long slno;

        @Column(name = "WI_NUM")
        private String wiNum;

        @Column(name = "DOCUMENT_ID")
        private String documentId;
        @Column(name = "MESSAGE")
        private String message;
        @Column(name = "code")
        private String code;
        @Column(name = "irn")
        private String irn;
        @Column(name = "NAME")
        private String name;

        @Column(name = "EMAIL")
        private String email;

        @Column(name = "PHONE")
        private String phone;

        @Column(name = "SIGN_URL")
        private String signUrl;

        @Column(name = "ACTIVE")
        private Boolean active;

        private Boolean signed;
        private Boolean rejected;
        private Boolean expired;

        @Column(name = "EXPIRY_DATE")
        private Date expiryDate;
        @Column(name = "CREATION_DATE")
        private Date creationDate;
        @Column(name = "COMPLETION_DATE")
        private Date completionDate;

        @Column(name = "SIGN_DATE")
        private Date signDate;
        @Column(name = "STATUS")
        private String status;
        @Column(name = "last_Mod_User")
        private String lastModUser;
        @Column(name = "LAST_MOD_DATE")
        private Date lastModDate;




//        @Column(name = "DEL_FLAG")
//        private String delFlg;

}
