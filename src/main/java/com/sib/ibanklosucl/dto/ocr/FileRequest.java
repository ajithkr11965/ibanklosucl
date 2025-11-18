package com.sib.ibanklosucl.dto.ocr;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileRequest {
    private String fileType;
    private String base64;
    private String password;
   private boolean  dlFlag=false;
   private boolean  maskOnly=false;
   private boolean  secured=false;

   private Document documentMeta;
@Data
    public class Document {
    private String cmUser;
    private String documentType;
    private String applicationType;
    private String workItemNo;
    }

}

