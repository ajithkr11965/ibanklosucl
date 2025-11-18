package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentRequest {

   private String cmUser;
   private String wiNum;
   private String documentType;
   private String channel="LOS";
   private String slNo;
   private String appId;
   private String legalDocID;
   private String docMode	;
   private MiscData miscData	;
   @Data
   public static class MiscData{
      private String docuCode	;
      private String stampDuty	;
   }


}
