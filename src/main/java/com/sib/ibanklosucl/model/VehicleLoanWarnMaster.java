package com.sib.ibanklosucl.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VEHICLE_LOAN_WARN_MASTER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VehicleLoanWarnMaster {

        @Id
        private Long ino;

        @Column(name = "WARN_CODE")
        private String warnCode;

        @Column(name = "WARN_DESC")
        private String warnDesc;

        @Column(name = "SEVERITY")
        private String severity;

        @Column(name = "REQ_IP_ADDR")
        private String reqIpAddr;
        @Column(name = "LAST_MOD_USER")
        private String lastModUser;
        @Column(name = "LAST_MOD_DATE")
        private Date lastModDate;

        @Column(name = "HOME_SOL")
        private String homeSol;
        @Column(name = "DEL_USER")
        private String delUser;
        @Column(name = "DEL_DATE")
        private String delDate;
        @Column(name = "DEL_FLG")
        private String delFlg;


}
