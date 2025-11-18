package com.sib.ibanklosucl.model;

import javax.persistence.*;
import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Data
@Entity
@Table(name = "MATERIAL_LIST_DATA")
@Getter
@Setter
@Audited
public class MaterialListData {
    @Id
    @Column(name = "ino")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    @Column(name = "slno")
    private Long slno;

    @Column(name = "wi_num",length = 13)
    private String winum;
    @Column( name="LISTID",length = 10)
    private String listID;
    @Column( name="LISTDESC",length = 300)
    private String listDesc;
    @Column( name="LISTCONDITION",length = 300)
    private String listCondition;
    @Column(name = "COMPLAINCE_DATE")
    private String complainceDate;
    @Column(name = "CMUSER", length = 10)
    private String cmuser;
    @Column(name = "sol_id", length = 10)
    private String solId;

    @Column(name = "CMDATE")
    @Temporal(TemporalType.DATE)
    private Date cmdate;

}
