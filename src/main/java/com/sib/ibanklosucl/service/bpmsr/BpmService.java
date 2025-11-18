package com.sib.ibanklosucl.service.bpmsr;

import com.google.gson.Gson;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.*;
import com.sib.ibanklosucl.model.VehicleLoanBpm;
import com.sib.ibanklosucl.model.VehicleLoanKyc;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanBpmRepositoryService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanKycService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BpmService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanKycService vehicleLoanKycService;
    @Autowired
    private VehicleLoanBpmRepositoryService vehicleLoanBpmRepositoryService;

    @Value("${api.bpm.createvl}")
    private String bpmurl;
    @Value("${api.bpm.uploadvl}")
    private String uploadurl;

    @Value("${bpm.domain}")
    private String bpmdomain;
    @Value("${bpm.revdomain}")
    private String bpmrevdomain;

    private static final Gson gson =new Gson();

    public BPMCreateVLResponse BpmParent(String wi,String slno, String child) throws Exception{

        try {
            BPMCreateVL bpmCreateVL=new BPMCreateVL();
            bpmCreateVL.setFolderName(wi);
            bpmCreateVL.setChild(child);
           // bpmCreateVL.setSystemIP(CommonUtils.getIP(request.getHeader("X-Forwarded-For")));
            bpmCreateVL.setSystemIP(usd.getRemoteIP());

            String reqjson = gson.toJson(bpmCreateVL);
            log.info("Bpm request : {}",reqjson);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<String> requestEntity = new HttpEntity<>(reqjson,headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    bpmurl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

           // String responses="{\"parentUrl\":\"VLR_000000566\",\"childUrl\":\"addUrl\",\"childIndex\":\"childIndex\",\"parentIndex\":\"0\",\"status\":\"APPLICANT\"}";

            log.info("Bpm response : {}",response.getBody());
           BPMCreateVLResponse bpmCreateVLResponse=gson.fromJson(response.getBody(),BPMCreateVLResponse.class);
           // BPMCreateVLResponse bpmCreateVLResponse=gson.fromJson(responses,BPMCreateVLResponse.class);
            VehicleLoanBpm vl=vehicleLoanBpmRepositoryService.getById(Long.valueOf(slno),wi);

            if(vl==null){
                vl=new VehicleLoanBpm();
                }
            vl.setWiNum(wi);
            vl.setSlno(Long.valueOf(slno));
            vl.setChildIndex(bpmCreateVLResponse.getChildIndex());
            vl.setParentIndex(bpmCreateVLResponse.getParentIndex());
            vl.setChildUrl(bpmCreateVLResponse.getChildUrl());
            vl.setParentUrl(bpmCreateVLResponse.getParentUrl());
            vl.setStatus(bpmCreateVLResponse.getStatus());
            vl.setProcessID(bpmCreateVL.getProcessID());
            vl.setUserID(bpmCreateVL.getUserID());
            vl.setFolderName(bpmCreateVL.getFolderName());
            vl.setSystemIP(bpmCreateVL.getSystemIP());
            vl.setAddurl(bpmCreateVLResponse.getAddUrl());

            vehicleLoanBpmRepositoryService.save(vl);

            bpmCreateVLResponse.setChildUrl(bpmCreateVLResponse.getChildUrl().replaceAll(bpmdomain,bpmrevdomain));
            bpmCreateVLResponse.setParentUrl(bpmCreateVLResponse.getParentUrl().replaceAll(bpmdomain,bpmrevdomain));
          //  bpmCreateVLResponse.setAddUrl(bpmCreateVLResponse.getAddUrl().replaceAll(bpmdomain,bpmrevdomain));

           return bpmCreateVLResponse;

        }
        catch (Exception e){
           log.error("Unexpected error in BpmParent, returning fallback response: ", e);
            return createFallbackResponse(wi, child);
        }
    }

    public BpmRequest bpmRequest(String winum, String pdf, String child, String childName, String docName,String extention){
        BPMFileUpload bpmFileUpload = new BPMFileUpload();
        bpmFileUpload.setWI_NAME(winum);
        bpmFileUpload.setCHILD(child);
        bpmFileUpload.setCHILD_FOLDER(childName);
        bpmFileUpload.setSystemIP(usd.getRemoteIP());
        List<DOC_ARRAY> docArrayList = new ArrayList<>();
        DOC_ARRAY docArray = new DOC_ARRAY();
        docArray.setDOC_NAME(docName+CommonUtils.getCurrentTimestamp());
        docArray.setDOC_EXT(extention);
        docArray.setDOC_BASE64(pdf);
        docArrayList.add(docArray);
        bpmFileUpload.setDOC_ARRAY(docArrayList);
        BpmRequest bpmRequest = new BpmRequest();
        bpmRequest.setRequest(bpmFileUpload);
        return bpmRequest;
    }
    public TabResponse BpmUpload(BpmRequest bpmRequest){
        String reqjson = gson.toJson(bpmRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> requestEntity = new HttpEntity<>(reqjson,headers);
        ResponseEntity<String> response = restTemplate.exchange(
                uploadurl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        log.info(response.getBody());
        BpmUploadResponse bpmCreateVLResponse=gson.fromJson(response.getBody(),BpmUploadResponse.class);

        if(bpmCreateVLResponse.getResponse().getStatus().equals("Success"))
            return new TabResponse("S",bpmCreateVLResponse.getResponse().getMessage());
        else
            return new TabResponse("F",bpmCreateVLResponse.getResponse().getMessage());

    }


    public TabResponse BpmChildUpload(FormData formData, TypeCount reqtype, HttpServletRequest request, String id){
        try {
//            if(true)
//                return new TabResponse("S","test");
            List<DOC_ARRAY> doc=formData.getDOC_ARRAY();
            String wi = formData.getWinum();
            String tab=formData.getId();
            BPMFileUpload bpmFileUpload=new BPMFileUpload();
            bpmFileUpload.setWI_NAME(wi);
            bpmFileUpload.setCHILD("Y");
            bpmFileUpload.setCHILD_FOLDER(CommonUtils.expandReq(reqtype));
            bpmFileUpload.setSystemIP(CommonUtils.getClientIp(request));
            String appid=formData.getAppid();
            VehicleLoanKyc kyc = null;
            if (appid != null && !appid.trim().isEmpty()) {
                try {
                    kyc = vehicleLoanKycService.findByAppId(Long.valueOf(appid));
                } catch (NumberFormatException e) {
                    log.error("Invalid Application ID format: " + appid, e);
                    throw new IllegalArgumentException("Invalid Application ID format: " + appid, e);
                }
            }

            List<DOC_ARRAY> updatedDocuments = doc.stream()
                    .filter(a -> !a.getDOC_NAME().equalsIgnoreCase("AADHAAR"))
                    .collect(Collectors.toList());

            // Add the new document
            if(tab.equals("KYC") && kyc!=null && kyc.getAadharimg()!=null)
                doc.add(new DOC_ARRAY(kyc.getAadharext(), "AADHAAR",kyc.getAadharimg() ));


            List<DOC_ARRAY> finalDoc=new ArrayList<>();
            for(DOC_ARRAY dto:doc){
                DOC_ARRAY dto_=new DOC_ARRAY();
                dto_.setDOC_NAME(dto.getDOC_NAME()+CommonUtils.getCurrentTimestamp());
                dto_.setDOC_BASE64(dto.getDOC_BASE64());
                dto_.setDOC_EXT(dto.getDOC_EXT());
                finalDoc.add(dto_);
            }

            bpmFileUpload.setDOC_ARRAY(finalDoc);
            BpmRequest bpmRequest=new BpmRequest();
            bpmRequest.setRequest(bpmFileUpload);
            String reqjson = gson.toJson(bpmRequest);
            log.info("*********************************************-------------->"+reqjson);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<String> requestEntity = new HttpEntity<>(reqjson,headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    uploadurl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            log.info(response.getBody());
            BpmUploadResponse bpmCreateVLResponse=gson.fromJson(response.getBody(),BpmUploadResponse.class);

            if(bpmCreateVLResponse.getResponse().getStatus().equalsIgnoreCase("Success"))
                return new TabResponse("S",bpmCreateVLResponse.getResponse().getMessage());
            else
                return new TabResponse("F",bpmCreateVLResponse.getResponse().getMessage());
        }
        catch (Exception e){
            e.printStackTrace();

            return new TabResponse("F", e.getMessage());
        }
    }

    private BPMCreateVLResponse createFallbackResponse(String wi, String child) {
        BPMCreateVLResponse fallback = new BPMCreateVLResponse();
        fallback.setStatus("PENDING"); // Default status
        fallback.setParentUrl(generateFallbackUrl(wi, "parent"));
        fallback.setChildUrl(generateFallbackUrl(wi, "child"));
        fallback.setChildIndex("0");
        fallback.setParentIndex("0");
        return fallback;
    }

    private String generateFallbackUrl(String wi, String type) {
        return String.format("%s/default/%s/%s", bpmrevdomain, type, wi);
    }



}
