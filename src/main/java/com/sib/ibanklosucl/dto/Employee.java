package com.sib.ibanklosucl.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private String ppcno;
  private String ppcName;
  private String gender;
  private String desig;
  private String desigDesc;
  private String scale;
  private String scaleDesc;
  private String joinedSol;
  private String brName;
  private String regCode;
  private String regName;
  private String offType;
  private String employeeStatus;
  private String employeeStatusDesc;
  private String finacleBlock;
  private String assignedsol;
  private String deputedsol;
  private String busunitsol;
  private String busunitId;
  private String busunitName;
  private String emailid;
  private String mobno;
  private String section;
  private String subsection;
  private String attendanceFlag;
  private String medal;
  private String ipphone;
  private String treeReportPpc;
  private String treeReviewPpc;
  private String treeSupervisorPpc;
  private String jobroleReportPpc;
  private String jobroleReviewPpc;
  private String criticalFlag;
  private String roleFor;
  private String roleName;
  private String roleNature;
  private String roleBusunitId;
  private String roleSolId;
  private String imageUrl;
  private String clusterName;
  private String clusterBaseSol;
  private String clusterHeadPpc;
  private String ppcAvailSol;
  private boolean ppcAvailStatus;
  private boolean superUser;
  private boolean lhUser;

  private boolean rahUser;

}
