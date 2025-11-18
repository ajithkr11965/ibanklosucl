package com.sib.ibanklosucl.model.mssf;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "MSSF_LOCK")
@Data
public class MSSFLock {
    @Id
    @Column(name = "REF_NO", length = 20)
    private String refNo;

    @Column(name = "LOCK_FLG", length = 1)
    private String lockFlg;

    @Column(name = "LOCKED_BY", length = 50)
    private String lockedBy;

    @Column(name = "LOCKED_ON")
    private LocalDateTime lockedOn;
}
