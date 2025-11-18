package com.sib.ibanklosucl.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.acopn.LienMarkingRequest;
import com.sib.ibanklosucl.dto.acopn.LienMarkingResult;
import com.sib.ibanklosucl.model.VehicleLoanFD;
import com.sib.ibanklosucl.repository.program.VehicleLoanFDRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class LienMarkingService {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${api.integrator}")
    private String integratorEndpoint;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${esb.ChannelID}")
    private String channelID;
    @Autowired
    private VehicleLoanFDRepository vehicleLoanFDRepository;
    @Transactional
    public List<LienMarkingResult> markLiens(String wiNum) {
        List<VehicleLoanFD> eligibleFDs = vehicleLoanFDRepository.findByWiNumAndEligibleAndDelFlgAndLienStatusIsNull(wiNum, true, "N");
        List<LienMarkingResult> results = new ArrayList<>();

        for (VehicleLoanFD fd : eligibleFDs) {
            try {
                if(fd.getVlfd()==null){
                    continue;
                }
                LienMarkingRequest request = new LienMarkingRequest(fd.getFdaccnum(), fd.getAvailbalance().toString(), "SGMSL", "SGMS Collateral Lien");
                String apiResult = callLienMarkingApi(request,wiNum);
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> response = mapper.readValue(apiResult, Map.class);

                Map<String, Object> responseObj = (Map<String, Object>) response.get("Response");
                Map<String, Object> status = (Map<String, Object>) responseObj.get("Status");
                if ("200".equals(status.get("Code"))) {
                    fd.setLienStatus("MARKED");
                    fd.setLienAmount(fd.getAvailbalance());
                    fd.setLienDate(new Date());
                    vehicleLoanFDRepository.save(fd);
                    results.add(new LienMarkingResult(fd.getFdaccnum(), "Marked", apiResult));
                } else {
                    results.add(new LienMarkingResult(fd.getFdaccnum(), "Failed", "Lien Marking failed: " + status.get("Desc")));
                    throw new RuntimeException("Lien marking failed for FD: " + fd.getFdaccnum());
                }
            } catch (Exception e) {
                results.add(new LienMarkingResult(fd.getFdaccnum(), "Failed", e.getMessage()));
            }
        }

        return results;
    }
    private String callLienMarkingApi(LienMarkingRequest request,String wiNum) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> requestVal = new HashMap<>();
        requestVal.put("merchantCode", merchantCode);
        requestVal.put("merchantName", merchantName);
        requestVal.put("UUID", "INFOBANK-" + System.currentTimeMillis());
        requestVal.put("account_number", request.getAccount_number());
        requestVal.put("reqType", "lienMark");
        requestBody.put("workItemNumber", wiNum);
        requestBody.put("mock", false);
        requestBody.put("apiName", "lienMarking");

        List<Map<String, String>> lienDetails = new ArrayList<>();
        Map<String, String> lienDetail = new HashMap<>();
        lienDetail.put("lien_amt", request.getLien_amt());
        lienDetail.put("lien_reason_code", request.getLien_reason_code());
        lienDetail.put("lien_remarks", request.getLien_remarks());
        lienDetails.add(lienDetail);

        requestVal.put("lienDetails", lienDetails);
        requestBody.put("request", requestVal);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(integratorEndpoint, entity, String.class);
        return response.getBody();
    }

}
