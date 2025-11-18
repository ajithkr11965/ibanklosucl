package com.sib.ibanklosucl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadForm {
    private String slno;
    private String winum;
    private String cmUser;
    private MultipartFile[] applicantFiles;
    private String[] applicantFileCodes;
    private String[] applicantFileNames;
    private String[] applicantType;
    private String[] applicantid;
    private MultipartFile[] coApplicantFiles;
    private String[] coApplicantFileCodes;
    private String[] coApplicantFileNames;
    private String[] coApplicantType;
    private String[] coApplicantid;
    private MultipartFile[] guarantorFiles;
    private String[] guarantorFileCodes;
    private String[] guarantorFileNames;
    private String[] guarantorid;
    private String[] guarantorType;
    private MultipartFile[] commonFiles;
    private String[] commonFileCodes;
    private String[] commonFileNames;
    private String[] commonType;
    private String reqip;

}
