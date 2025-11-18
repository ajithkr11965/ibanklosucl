package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reportee {
  private String ppcno;
  private String ppcName;
  private String desigDesc;
  private String solId;
  private String brName;
  private String emailid;
  private String mobno;
  private String section;
  private String subsection;
  private String attendanceFlag;
  private String ipphone;
  private String roleName;
  private String imageUrl;
}
