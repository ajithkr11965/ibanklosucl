package com.sib.ibanklosucl.service.esbsr;

import com.google.gson.Gson;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.losintegrator.LosRequest;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.service.vlsr.FinacleLosDedupeService;
import com.sib.ibanklosucl.service.vlsr.LosDedupeService;
import com.sib.ibanklosucl.utilies.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DedupService {
    @Value("${api.integrator}")
    private String apiUrl;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ValidationRepository validationRepository;
    @Autowired
    private LosDedupeService losDedupeService;
    @Autowired
    private FinacleLosDedupeService finacleLosDedupeService;

    @Value("${esb.MerchantCode}")
    private String  ChannelID;
    @Value("${esb.MerchantName}")
    private String  ChannelName;

    private static final Gson gson=new Gson();

    @Transactional
    public ResponseDTO callLosDedupeApi(DedupRequestDTO tab) {
        try {
            LosDedupeRequestDTO losDedupeRequestDTO=validationRepository.getApplicantDetails(tab.getAppid(),"/");
            losDedupeRequestDTO.setMerchantCode(ChannelID);
            losDedupeRequestDTO.setMerchantName(ChannelName);
            losDedupeRequestDTO.setMobNo(tab.getMobno());
            losDedupeRequestDTO.setVoterID("");
            losDedupeRequestDTO.setGstNo("");
            losDedupeRequestDTO.setDriverNo("");
            losDedupeRequestDTO.setCorpId("");
            LosRequest.LOSDedup dedup=new LosRequest.LOSDedup();
            dedup.setRequest(losDedupeRequestDTO);
            dedup.setMock(false);
            dedup.setOrigin(tab.getAppid());
            dedup.setWorkItemNumber(tab.getWinum());
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            // Create the HTTP entity
            HttpEntity<LosRequest.LOSDedup> entity = new HttpEntity<>(dedup, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );


            LosApiResponse apiResponse = gson.fromJson(response.getBody(), LosApiResponse.class);
//             String responses ="{\"Response\":{\"Header\":{\"Timestamp\":\"20240808114421\",\"APIName\":\"bpm-dedupe-api\",\"APIVersion\":\"1.0.0\",\"Interface\":\"BPM_Dedupe_API\"},\"Status\":{\"Code\":\"406\",\"Desc\":\"Failure\"},\"Body\":{\"bpmdedupeData\":[{\"message\":\"No Data Found\"}]}}}";
//            LosApiResponse apiResponse = gson.fromJson(responses, LosApiResponse.class);

            losDedupeService.updateActiveFlag(tab.getAppid());
            if ("406".equals(apiResponse.getResponse().getStatus().getCode())) {
                String message=apiResponse.getResponse().getBody().getBpmdedupeData().get(0).getMessage();
                losDedupeService.saveMsg(message,tab);
                return new ResponseDTO("Y",message);
            }
            List<LosApiResponse.BpmdedupeData> dataList=(apiResponse.getResponse().getBody().getBpmdedupeData());
            losDedupeService.saveAll(dataList,tab);
            return new ResponseDTO("S",gson.toJson(dataList));

        } catch (Exception ex) {
            ex.printStackTrace();
            //return new ResponseDTO("F", "Error Occurred During LOS Fetch (Reason : "+ex.getMessage()+" )");
            return new ResponseDTO("F", "The service failed. Please recheck the details entered or try again after some time.");
        }
    }

    @Transactional
    public ResponseDTO callFinacleDedupeApi(DedupRequestDTO tab) {
        try {
            LosDedupeRequestDTO losDedupeRequestDTO=validationRepository.getApplicantDetails(tab.getAppid(),"-");
            CustDedup custDedup=new CustDedup();
            custDedup.setMerchant_code(ChannelID);
            custDedup.setMerchant_name(ChannelName);
            custDedup.setMobileNumber(tab.getMobno());
            custDedup.setEmail(tab.getEmail());
            custDedup.setAadhar(losDedupeRequestDTO.getAadharNo());
            custDedup.setPan(losDedupeRequestDTO.getPanNo());
//            custDedup.setPan("CWHPK6368Q");
            custDedup.setPassport(losDedupeRequestDTO.getPassport());
            custDedup.setDateofBirth(losDedupeRequestDTO.getDob());
            custDedup.setName(losDedupeRequestDTO.getCustName());
            custDedup.setVoterId("");
            custDedup.setLandlineNumber("");
            custDedup.setDrivingLicense("");

            LosRequest.FinacleDedup dedup=new LosRequest.FinacleDedup();
            dedup.setRequest(custDedup);
            dedup.setMock(false);
            dedup.setOrigin(tab.getAppid());
            dedup.setWorkItemNumber(tab.getWinum());
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            // Create the HTTP entity
            HttpEntity<LosRequest.FinacleDedup> entity = new HttpEntity<>(dedup, headers);

            // Call the OCR API
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            LosApiResponse apiResponse = gson.fromJson(response.getBody(), LosApiResponse.class);
           finacleLosDedupeService.updateActiveFlag(tab.getAppid());
//             String responses="{\"Response\":{\"Header\":{\"Timestamp\":\"2024-08-06T23:21:10\",\"APIName\":\"dedupe-service-api\",\"APIVersion\":\"1.0.0\",\"Interface\":\"DedupeService\"},\"Status\":{\"Code\":201,\"Desc\":\"NO DATA\"},\"Body\":{\"merchant_code\":\"INFM\",\"merchant_name\":\"Information_Bank\",\"servicename\":\"Dedupe\",\"message\":\"NO DATA\"}}}";

   //          LosApiResponse apiResponse = gson.fromJson(responses, LosApiResponse.class);
            if ("201".equals(apiResponse.getResponse().getStatus().getCode())) {
                String message=apiResponse.getResponse().getBody().getMessage();
                if(message.equals("SUCCESS")){
                    List<LosApiResponse.customer> dataList=(apiResponse.getResponse().getBody().getCustomer());
                    finacleLosDedupeService.saveAll(dataList,tab);
                    return new ResponseDTO("S",gson.toJson(dataList));
                }
                else {
                    finacleLosDedupeService.saveMsg(message, tab);
                    return new ResponseDTO("Y", message);
                }
            }
            else {
                return new ResponseDTO("F", Constants.Messages.SOMETHING_ERROR);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
          //  return new ResponseDTO("F", "Error Occurred During Customer Dedupe (Reason : "+ex.getMessage()+" )");
            return new ResponseDTO("F", "The Finacle Dedupe service failed. Please recheck the details entered or try again after some time.");
        }
    }

}
