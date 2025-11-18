package com.sib.ibanklosucl.service.impl;

import com.google.gson.JsonObject;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.BPMCreateVLResponse;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;
import com.sib.ibanklosucl.model.VLFileUpload;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.service.CommonService;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.doc.ManDocService;
import com.sib.ibanklosucl.service.vlsr.VLFileUploadService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BmDocUploadServiceImpl implements CommonService {

    @Autowired
    private BpmService bpmService;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private VLFileUploadService vlFileUploadService;
    @Autowired
    private ManDocService manDocService;
    @Override
    public TabResponse processData(CommonDTO request) {
        FileUploadForm form=request.getFileUploadForm();
        Model model = null;
        List<FileDetails> applicantFileDetails = processFiles(form.getApplicantFiles(), form.getApplicantFileCodes(), form.getApplicantFileNames(), form.getApplicantType(),form.getApplicantid());

        // Process co-applicant files
        List<FileDetails> coApplicantFileDetails = processFiles(form.getCoApplicantFiles(), form.getCoApplicantFileCodes(), form.getCoApplicantFileNames(), form.getCoApplicantType(),form.getCoApplicantid());

        // Process guarantor files
        List<FileDetails> guarantorFileDetails = processFiles(form.getGuarantorFiles(), form.getGuarantorFileCodes(), form.getGuarantorFileNames(), form.getGuarantorType(),form.getGuarantorid());

        // Process common files
        List<FileDetails> commonFileDetails = processFiles(form.getCommonFiles(), form.getCommonFileCodes(), form.getCommonFileNames(), form.getCommonType(),null);

        return callAPI(form,applicantFileDetails, coApplicantFileDetails, guarantorFileDetails, commonFileDetails);

    };


    private List<FileDetails> processFiles(MultipartFile[] files, String[] fileCodes, String[] fileNames,String[] apptype,String[] appid) {
        if (files == null || fileCodes == null || fileNames == null || apptype==null ) {
            return Collections.emptyList();
        }

        return IntStream.range(0, files.length)
                .filter(i -> !files[i].isEmpty())
                .mapToObj(i -> {
                    String fileCode = fileCodes[i];
                    String fileName = fileNames[i];
                    String app_type = apptype[i];
                    String app_id = appid!=null ? appid[i]:"";
                    String base64Content = convertToBase64(files[i]);
                    String extension = FilenameUtils.getExtension(files[i].getOriginalFilename());
                    return new FileDetails(fileCode, fileName, base64Content, extension,app_type,app_id);
                })
                .collect(Collectors.toList());
    }



    private String convertToBase64(MultipartFile file) {
        try {
            byte[] fileContent = file.getBytes();
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            // Handle file reading error
            return null;
        }
    }

    private TabResponse processAndUploadFiles(FileUploadForm form, List<FileDetails> files,String childflg,List<VLFileUpload> vlFileUploads) {
        if (files == null || files.isEmpty()) return new TabResponse("S","");

        BPMFileUpload bpmFileUpload = new BPMFileUpload();
        bpmFileUpload.setWI_NAME(form.getWinum());
        bpmFileUpload.setCHILD(childflg);
        bpmFileUpload.setSystemIP(form.getReqip());
        List<DOC_ARRAY> docArrayList = new ArrayList<>();

        for (FileDetails fileDetails : files) {
            TypeCount tc = CommonUtils.parseString(fileDetails.getFiletype());
            bpmFileUpload.setCHILD_FOLDER( CommonUtils.expandReq(tc));
            DOC_ARRAY docArray = new DOC_ARRAY();
            docArray.setDOC_NAME(fileDetails.getFilename()+CommonUtils.getCurrentTimestamp());
            docArray.setDOC_EXT(fileDetails.getExtention());
            docArray.setDOC_BASE64(fileDetails.getBase64Content());
            docArrayList.add(docArray);
            if(!vlFileUploads.stream().anyMatch(t-> fileDetails.getFilecode().equals(t.getFileCode()) && fileDetails.getFiletype().equals(t.getAppType()))){
                VLFileUpload vlFileUpload=new VLFileUpload();
                vlFileUpload.setFileCode(fileDetails.getFilecode());
                vlFileUpload.setFileName(fileDetails.getFilename());
                vlFileUpload.setFileExtension(fileDetails.getExtention());
                vlFileUpload.setSlno(Long.valueOf(form.getSlno()));
                vlFileUpload.setWorkItem(form.getWinum());
                vlFileUpload.setAppType(fileDetails.getFiletype());
                vlFileUpload.setAppId(Long.valueOf(fileDetails.getAppid().isEmpty()?"0":fileDetails.getAppid()));
                vlFileUploadService.saveFile(vlFileUpload) ;
            }
        }

        bpmFileUpload.setDOC_ARRAY(docArrayList);
        BpmRequest bpmRequest = new BpmRequest();
        bpmRequest.setRequest(bpmFileUpload);

        return bpmService.BpmUpload(bpmRequest);
    }

    private TabResponse callAPI(FileUploadForm form, List<FileDetails> applicantFiles, List<FileDetails> coApplicantFiles,
                                List<FileDetails> guarantorFiles, List<FileDetails> commonFiles) {

        List<VLFileUpload> vlFileUploads=vlFileUploadService.findFileBySlno(Long.valueOf(form.getSlno()));

        // Process applicant files
        TabResponse isApplicantSuccess = processAndUploadFiles(form, applicantFiles,"Y",vlFileUploads);
        if (!isApplicantSuccess.getStatus().equals("S")) {
           return isApplicantSuccess;
        }
        // Process co-applicant files
        Map<String, List<FileDetails>> coApplicantGroups = coApplicantFiles.stream()
                .collect(Collectors.groupingBy(FileDetails::getFiletype));
        for (Map.Entry<String, List<FileDetails>> entry : coApplicantGroups.entrySet()) {
            TabResponse isCoApplicantSuccess = processAndUploadFiles(form, entry.getValue(),"Y",vlFileUploads);
            if (!isCoApplicantSuccess.getStatus().equals("S")) {
                return isCoApplicantSuccess;
            }
        }

        // Process guarantor files
        TabResponse isGuarantorSuccess = processAndUploadFiles(form, guarantorFiles,"Y",vlFileUploads);
        if (!isGuarantorSuccess.getStatus().equals("S")) {
            return isGuarantorSuccess;
        }

        // Process common files
        TabResponse isCommonSuccess = processAndUploadFiles(form, commonFiles,"N",vlFileUploads);
        if (!isCommonSuccess.getStatus().equals("S")) {
            return isCommonSuccess;
        }

        // If all uploads are successful, return a success response
        return new TabResponse("S", "All files uploaded successfully");
    }

@Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO processDataDoc(FileUploadForm form ){
            VehicleLoanMaster master=vehicleLoanMasterService.findById(Long.valueOf(form.getSlno()));
            if(master.isDocCompleted()){
                return new ResponseDTO("F","Documentation Already Completed !!");
            }
        List<FileDetails> commFiles=processFiles(form.getCommonFiles(),form.getCommonFileCodes(),form.getCommonFileNames());
        // Process applicant files
        TabResponse isApplicantSuccess = processAndUploadFiles(form, commFiles,"Y");
        if (!isApplicantSuccess.getStatus().equals("S")) {
            return new ResponseDTO("F",isApplicantSuccess.getMsg());
        }
        for(FileDetails fileDetails:commFiles){
            manDocService.updateStatus(Long.valueOf(form.getSlno()),form.getCmUser(),fileDetails.getFilecode());
        }

        JsonObject msg=new JsonObject();
          msg.addProperty("completed","N");
        if(manDocService.isAllDocUploaded(Long.valueOf(form.getSlno()))) {
            master.setDocQueueOverallStatus("COMPLETED");
            master.setDocUploadDate(new Date());
            master.setDocUploadUser(form.getCmUser());
            vehicleLoanMasterService.saveLoan(master);
            msg.addProperty("completed","Y");

        }
        msg.addProperty("msg","upload Success");

        return new ResponseDTO("S",msg.toString());
    }

    private List<FileDetails> processFiles(MultipartFile[] files, String[] fileCodes, String[] fileNames) {
        if (files == null || fileCodes == null || fileNames == null ) {
            return Collections.emptyList();
        }

        return IntStream.range(0, files.length)
                .filter(i -> !files[i].isEmpty())
                .mapToObj(i -> {
                    String fileCode = fileCodes[i];
                    String fileName = fileNames[i];
                    String base64Content = convertToBase64(files[i]);
                    String extension = FilenameUtils.getExtension(files[i].getOriginalFilename());
                    return new FileDetails(fileCode, fileName, base64Content, extension,"","");
                })
                .collect(Collectors.toList());
    }

    private TabResponse processAndUploadFiles(FileUploadForm form, List<FileDetails> files,String childflg) {
        if (files == null || files.isEmpty()) return new TabResponse("S","");

        BPMFileUpload bpmFileUpload = new BPMFileUpload();
        bpmFileUpload.setWI_NAME(form.getWinum());
        bpmFileUpload.setCHILD(childflg);
        bpmFileUpload.setSystemIP(form.getReqip());
        List<DOC_ARRAY> docArrayList = new ArrayList<>();
        BPMCreateVLResponse responseDTO=null;
        try {
             responseDTO = bpmService.BpmParent(form.getWinum(), String.valueOf(form.getSlno()), "LOAN_DOCUMENTS");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!responseDTO.getStatus().equals("Success"))
            return new TabResponse("F", responseDTO.getStatus());

        for (FileDetails fileDetails : files) {
            bpmFileUpload.setCHILD_FOLDER( "LOAN_DOCUMENTS");
            DOC_ARRAY docArray = new DOC_ARRAY();
            docArray.setDOC_NAME(fileDetails.getFilename()+CommonUtils.getCurrentTimestamp());
            docArray.setDOC_EXT(fileDetails.getExtention());
            docArray.setDOC_BASE64(fileDetails.getBase64Content());
            docArrayList.add(docArray);
        }
        bpmFileUpload.setDOC_ARRAY(docArrayList);
        BpmRequest bpmRequest = new BpmRequest();
        bpmRequest.setRequest(bpmFileUpload);
        return bpmService.BpmUpload(bpmRequest);
    }

}
