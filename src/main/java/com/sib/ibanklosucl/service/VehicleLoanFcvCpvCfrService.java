package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;
import com.sib.ibanklosucl.dto.bre.AmberData;
import com.sib.ibanklosucl.dto.bre.AmberDeviationUpdateRequest;
import com.sib.ibanklosucl.model.VLFileUpload;
import com.sib.ibanklosucl.model.VehicleLoanFcvCpvCfr;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.repository.VehicleLoanFcvCpvCfrRepository;
import com.sib.ibanklosucl.repository.VehicleLoanMasterRepository;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.integration.VehicleLoanBREService;
import com.sib.ibanklosucl.service.vlsr.VLFileUploadService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class VehicleLoanFcvCpvCfrService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleLoanFcvCpvCfrService.class);

    @Autowired
    private VehicleLoanFcvCpvCfrRepository fcvCpvCfrRepository;

    @Autowired
    private VehicleLoanMasterRepository repository;
    @Autowired
    private BpmService bpmService;
    @Autowired
    private VLFileUploadService vlFileUploadService;
    @Autowired
    private UserSessionData userSessionData;
    @Autowired
    private VehicleLoanAmberService vehicleLoanAmberService;

    @Autowired
    private VehicleLoanBREService vlbre;

    public String validate(FormDataFCV details, HttpServletRequest request) throws Exception {

        if (details.getFcvstatus() == null || details.getFcvstatus().isEmpty() || details.getCpvstatus() == null || details.getCpvstatus().isEmpty()) {
            logger.error("FCV/CPV status is required for WI_NUM: {}", details.getWinum());
            return "FCV/CPV status is required";
        }

        if (details.getCfrstatus() == null || details.getCfrstatus().isEmpty()) {
            logger.error("CFR status is required for WI_NUM: {}", details.getWinum());
            return "CFR status is required";
        }
        boolean fcvfound=false;
        boolean cpvfound=false;

        JSONArray jsonArray= new JSONArray(details.getDOC_ARRAY());
        for (int i=0;i<jsonArray.length();i++){
            JSONObject jsonobj = jsonArray.getJSONObject(i);
            if(jsonobj.getString("DOC_NAME").equals("FCV")){
                fcvfound=true;
            }
            if(jsonobj.getString("DOC_NAME").equals("CPV")){
                cpvfound=true;
            }
        }

//        if ("PASS".equals(details.getFcvstatus())) {
//            if(!fcvfound){
//                return "FCV Document is mandatory if FCV status is PASS";
//            }
//        } else {
//            if(fcvfound){
//                return "FCV Document is only needed if FCV status is PASS";
//            }
//        }
//        if ("PASS".equals(details.getCpvstatus())) {
//            if(!cpvfound){
//                return "CPV Document is mandatory if CPV status is PASS";
//            }
//        }else{
//            if(cpvfound){
//                return "CPV Document is only needed if CPV status is PASS";
//            }
//        }
        return "valid";
    }

    @Transactional
    public VehicleLoanFcvCpvCfr save(VehicleLoanFcvCpvCfr details, HttpServletRequest request, MultipartFile fcvFile, MultipartFile cpvFile, MultipartFile cfrFile) throws Exception {
        logger.info("Attempting to save FCV/CPV/CFR details for WI_NUM: {}", details.getWiNum());

        validateDetails(details);

        VehicleLoanMaster master = repository.findBySlnoWithApplicantsOnly(details.getSlno());
        Optional<VehicleLoanFcvCpvCfr> existingRecord = fcvCpvCfrRepository.findBySlnoAndDelFlg(details.getSlno(), "N");
        existingRecord.ifPresent(record -> {
            record.setDelFlg("Y");
            fcvCpvCfrRepository.save(record);
        });

        details.setFcvcpvcfrkey(master);
        details.setCmdate(new Date());
        details.setCmuser(userSessionData.getEmployee().getPpcno());
        details.setDelFlg("N");
        details.setFcvFileUploaded(fcvFile != null && !fcvFile.isEmpty());
        details.setCpvFileUploaded(cpvFile != null && !cpvFile.isEmpty());
        details.setCfrFileUploaded(cfrFile != null && !cfrFile.isEmpty());
        if (!"NA".equals(details.getFcvStatus()) && (fcvFile == null || fcvFile.isEmpty())) {
            throw new IllegalArgumentException("FCV file is required when FCV status is not NA");
        }

        if (!"NA".equals(details.getCpvStatus()) && (cpvFile == null || cpvFile.isEmpty())) {
            throw new IllegalArgumentException("CPV file is required when CPV status is not NA");
        }

        if (fcvFile != null && !fcvFile.isEmpty()) {
            handleFileUpload(fcvFile, details, master, request, "FCV");
        }

        if (cpvFile != null && !cpvFile.isEmpty()) {
            handleFileUpload(cpvFile, details, master, request, "CPV");
        }

        if (cfrFile != null && !cfrFile.isEmpty()) {
            handleFileUpload(cfrFile, details, master, request, "CFR");
        }
        VehicleLoanFcvCpvCfr savedDetails = fcvCpvCfrRepository.save(details);

        logger.info("FCV/CPV/CFR details successfully saved for WI_NUM: {}", details.getWiNum());
        addCfrDeviation(details.getWiNum(), details.getSlno(), details.getCfrStatus());
        addCpvDeviation(details.getWiNum(), details.getSlno(), details.getCpvStatus());
        addFcvDeviation(details.getWiNum(), details.getSlno(), details.getFcvStatus());
        return savedDetails;
    }

    public Optional<VehicleLoanFcvCpvCfr> findByWiNum(String wiNum) {
        logger.info("Attempting to find FCV/CPV/CFR details for WI_NUM: {}", wiNum);
        return fcvCpvCfrRepository.findByWiNum(wiNum);
    }

    public Optional<VehicleLoanFcvCpvCfr> findBySlno(Long slno) {
        logger.info("Attempting to find FCV/CPV/CFR details for SLNO: {}", slno);
        return fcvCpvCfrRepository.findBySlnoAndDelFlg(slno, "N");
    }

    @Transactional
    public void addCfrDeviation(String wiNum, Long slno, String cfrStatus) {

        logger.info("Attempting to add CFR deviation for WI_NUM: {}", wiNum);
        String color = "Yes".equals(cfrStatus) ? "amber" : "green";
        String amberDesc = "Yes".equals(cfrStatus) ? "CFR match found" : "No CFR match found";
        String cfrCode = "AMB011";

        AmberDeviationUpdateRequest request = new AmberDeviationUpdateRequest();
        request.setWiNum(wiNum);
        request.setSlno(slno.toString());

        AmberData amberData = new AmberData();
        amberData.setAmberDesc(amberDesc);
        amberData.setColor(color);
        amberData.setDeviationType("");
        amberData.setAmberCode(cfrCode);
        amberData.setDoRemarks("CFR deviation added automatically");


        try {
            vehicleLoanAmberService.updateFcvCpvCfrDeviation(wiNum,slno,amberData);
            logger.info("CFR deviation added successfully for WI_NUM: {}", wiNum);
        } catch (Exception e) {
            logger.error("Error adding CFR deviation for WI_NUM: {}", wiNum, e);
            throw new RuntimeException("Failed to add CFR deviation", e);
        }
    }

    @Transactional
    public void addCpvDeviation(String wiNum, Long slno, String cpvStatus) {

        logger.info("Attempting to add CPV deviation for WI_NUM: {}", wiNum);
        String color = "Refer to credit".equals(cpvStatus) ? "amber" : "Negative".equals(cpvStatus)? "red" : "green";
        String amberDesc = "Refer to credit".equals(cpvStatus) ? "CPV is marked ‘Refer to credit’" :  "Negative".equals(cpvStatus) ? "CPV is marked ‘Negative’" : "CPV is not marked ‘Refer to credit’";
        String cpvCode = "AMB010";

        AmberDeviationUpdateRequest request = new AmberDeviationUpdateRequest();
        request.setWiNum(wiNum);
        request.setSlno(slno.toString());

        AmberData amberData = new AmberData();
        amberData.setAmberDesc(amberDesc);
        amberData.setColor(color);
        amberData.setDeviationType("");
        amberData.setAmberCode(cpvCode);
        amberData.setDoRemarks("CPV deviation added automatically");


        try {
            vehicleLoanAmberService.updateFcvCpvCfrDeviation(wiNum,slno,amberData);
            logger.info("CFR deviation added successfully for WI_NUM: {}", wiNum);
        } catch (Exception e) {
            logger.error("Error adding CFR deviation for WI_NUM: {}", wiNum, e);
            throw new RuntimeException("Failed to add CFR deviation", e);
        }
    }

    @Transactional
    public void addFcvDeviation(String wiNum, Long slno, String fcvStatus) {

        logger.info("Attempting to add FCV deviation for WI_NUM: {}", wiNum);
        String color = "Refer to credit".equals(fcvStatus) ? "amber" : "Negative".equals(fcvStatus)? "red" : "green";
        String amberDesc = "Refer to credit".equals(fcvStatus) ? "FCV is marked ‘Refer to credit’" : "Negative".equals(fcvStatus) ? "FCV is marked ‘Negative’" : "FCV is not marked ‘Refer to credit’";
        String fcvCode = "AMB009";
        AmberDeviationUpdateRequest request = new AmberDeviationUpdateRequest();
        request.setWiNum(wiNum);
        request.setSlno(slno.toString());

        AmberData amberData = new AmberData();
        amberData.setAmberDesc(amberDesc);
        amberData.setColor(color);
        amberData.setDeviationType("");
        amberData.setAmberCode(fcvCode);
        amberData.setDoRemarks("FCV deviation added automatically");

        request.setAmberData(Collections.singletonList(amberData));

        try {
            vehicleLoanAmberService.updateFcvCpvCfrDeviation(wiNum,slno,amberData);
            logger.info("CFR deviation added successfully for WI_NUM: {}", wiNum);
        } catch (Exception e) {
            logger.error("Error adding CFR deviation for WI_NUM: {}", wiNum, e);
            throw new RuntimeException("Failed to add CFR deviation", e);
        }
    }


    private void validateDetails(VehicleLoanFcvCpvCfr details) {
        if (details.getFcvStatus() == null || details.getFcvStatus().isEmpty() || details.getCpvStatus() == null || details.getCpvStatus().isEmpty()) {
            logger.error("FCV/CPV status is required for WI_NUM: {}", details.getWiNum());
            throw new IllegalArgumentException("FCV/CPV status is required");
        }

        if (details.getCfrStatus() == null || details.getCfrStatus().isEmpty()) {
            logger.error("CFR status is required for WI_NUM: {}", details.getWiNum());
            throw new IllegalArgumentException("CFR status is required");
        }
    }


    private void handleFileUpload(MultipartFile file, VehicleLoanFcvCpvCfr details, VehicleLoanMaster master, HttpServletRequest request, String fileType) throws Exception {
        logger.info("Handling {} file upload: {} for WI_NUM: {}", fileType, file.getOriginalFilename(), details.getWiNum());

        FileUploadForm form = new FileUploadForm();
        form.setWinum(details.getWiNum());
        form.setSlno(master.getSlno().toString());
        form.setReqip(CommonUtils.getClientIp(request));

        FileDetails fileDetails = new FileDetails(
                fileType,
                fileType,
                convertToBase64(file),
                FilenameUtils.getExtension(file.getOriginalFilename()),
                fileType,
                master.getSlno().toString()
        );

        List<FileDetails> files = Collections.singletonList(fileDetails);
        List<VLFileUpload> vlFileUploads = vlFileUploadService.findFileBySlno(master.getSlno());// May remove this later
        TabResponse response = processAndUploadFiles(form, files, "N", vlFileUploads);
        if (!"S".equals(response.getStatus())) {
            throw new Exception("File upload failed: ");
        }


    }

    private TabResponse processAndUploadFiles(FileUploadForm form, List<FileDetails> files, String childFlg, List<VLFileUpload> vlFileUploads) {
        if (files == null || files.isEmpty()) return new TabResponse("S", "");

        BPMFileUpload bpmFileUpload = new BPMFileUpload();
        bpmFileUpload.setWI_NAME(form.getWinum());
        bpmFileUpload.setCHILD(childFlg);
        bpmFileUpload.setSystemIP(form.getReqip());
        List<DOC_ARRAY> docArrayList = new ArrayList<>();

        for (FileDetails fileDetails : files) {
            TypeCount tc = CommonUtils.parseString(fileDetails.getFiletype());
            bpmFileUpload.setCHILD_FOLDER(CommonUtils.expandReq(tc));
            DOC_ARRAY docArray = new DOC_ARRAY();
            docArray.setDOC_NAME(fileDetails.getFilename()+CommonUtils.getCurrentTimestamp());
            docArray.setDOC_EXT(fileDetails.getExtention());
            docArray.setDOC_BASE64(fileDetails.getBase64Content());
            docArrayList.add(docArray);

            if (vlFileUploads.stream().noneMatch(t -> fileDetails.getFilecode().equals(t.getFileCode()) && fileDetails.getFiletype().equals(t.getAppType()))) {
                VLFileUpload vlFileUpload = new VLFileUpload();
                vlFileUpload.setFileCode(fileDetails.getFilecode());
                vlFileUpload.setFileName(fileDetails.getFilename());
                vlFileUpload.setFileExtension(fileDetails.getExtention());
                vlFileUpload.setSlno(Long.valueOf(form.getSlno()));
                vlFileUpload.setWorkItem(form.getWinum());
                vlFileUpload.setAppType(fileDetails.getFiletype());
                vlFileUpload.setAppId(Long.valueOf(fileDetails.getAppid().isEmpty() ? "0" : fileDetails.getAppid()));
                vlFileUploadService.saveFile(vlFileUpload);
            }
        }

        bpmFileUpload.setDOC_ARRAY(docArrayList);
        BpmRequest bpmRequest = new BpmRequest();
        bpmRequest.setRequest(bpmFileUpload);

        return bpmService.BpmUpload(bpmRequest);
    }

    private String convertToBase64(MultipartFile file) {
        try {
            byte[] fileContent = file.getBytes();
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (Exception e) {
            logger.error("Error converting file to Base64", e);
            return null;
        }
    }


}
