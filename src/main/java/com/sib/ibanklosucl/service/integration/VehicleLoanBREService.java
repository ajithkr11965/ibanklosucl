package com.sib.ibanklosucl.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.exception.VehicleLoanBREException;
import com.sib.ibanklosucl.model.VehicleLoanAmber;
import com.sib.ibanklosucl.model.VehicleLoanAmberSub;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.integrations.VehicleLoanBREDetails;
import com.sib.ibanklosucl.repository.VehicleLoanAmberRepository;
import com.sib.ibanklosucl.repository.VehicleLoanAmberSubRepository;
import com.sib.ibanklosucl.repository.integations.VehicleLoanBREDetailsRepository;
import com.sib.ibanklosucl.service.VLBREservice;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
public class VehicleLoanBREService {
    @Autowired
    private VehicleLoanBREDetailsRepository breDetailsRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private VLBREservice vlbrEngineservice;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private VehicleLoanAmberRepository amberRepository;

    @Autowired
    private VehicleLoanAmberSubRepository amberSubRepository;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;
    @Autowired
    private UserSessionData usd;

    @Transactional
    public void processAmberData(String response, String wiNum, String slNo, String reqIpNo) {
        VehicleLoanMaster master = vehicleLoanMasterService.findBySlno(Long.valueOf(slNo));
        Map<String, Object> responseMap;
        try {
            responseMap = objectMapper.readValue(response, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing response JSON", e);
        }

        String eligibilityFlag = (String) responseMap.get("eligibilityFlag");
        Map<String, Object> eligibilityData = null;
        Map<String, Object> breData = null;
        if ("red".equals(eligibilityFlag)) {
            eligibilityData = (Map<String, Object>) responseMap.get("eligibilityData");
        } else if ("green".equals(eligibilityFlag)) {
            breData = (Map<String, Object>) responseMap.get("breData");
        }

        VehicleLoanBREDetails breDetails = new VehicleLoanBREDetails();
        breDetails.setWiNum(wiNum);
        breDetails.setSlno(Long.valueOf(slNo));
        breDetails.setEligibilityFlag(eligibilityFlag);
        breDetails.setBreFlag(eligibilityFlag.equals("green") ? (String) responseMap.get("breFlag") : null);
        breDetails.setCmDate(new Date());
        breDetails.setCmUser(usd.getEmployee().getPpcno());
        breDetails.setDelFlg("N");


        try {
            breDetails.setEligibilityData(objectMapper.writeValueAsString(eligibilityData));
            breDetails.setBreData(objectMapper.writeValueAsString(breData));
        } catch (JsonProcessingException e) {
            log.error("Error in parsing JSON", e);
        }
        breDetails.setVehicleLoanBRE(master);
        breDetailsRepository.save(breDetails);


        if ("green".equals(eligibilityFlag)) {
            insertAmberData(responseMap, wiNum, slNo, reqIpNo);
        } else if ("red".equals(eligibilityFlag)) {
            insertEligibilityData(responseMap, wiNum, slNo, reqIpNo);
        }
    }

    @Transactional
    public void insertAmberData(Map<String, Object> responseMap, String wiNum, String slNo, String reqIpNo) {
        VehicleLoanMaster master = vehicleLoanMasterService.findBySlno(Long.valueOf(slNo));

        amberRepository.updateActiveFlag(wiNum, Long.valueOf(slNo));
        amberSubRepository.updateActiveFlag(wiNum, Long.valueOf(slNo));

        Map<String, Object> breData = (Map<String, Object>) responseMap.get("breData");

        for (Map.Entry<String, Object> entry : breData.entrySet()) {
            Map<String, Object> amberData = (Map<String, Object>) entry.getValue();

            VehicleLoanAmber amber = new VehicleLoanAmber();
            amber.setVlamberKey(master);
            amber.setWiNum(wiNum);
            amber.setSlno(Long.parseLong(slNo));
            amber.setAmberCode((String) amberData.get("breCode"));
            amber.setAmberDesc((String) amberData.get("breDesc"));
            amber.setColour((String) amberData.get("color"));
            amber.setLastModUser(usd.getEmployee().getPpcno());
            amber.setLastModDate(new Date());
            amber.setDelFlg("N");
            amber.setActiveFlg("Y");
            amber.setReqIpAddr(reqIpNo);
            amber.setHomeSol(usd.getEmployee().getJoinedSol());
            VehicleLoanAmber savedAmber = amberRepository.save(amber);

            List<Map<String, Object>> breSub = (List<Map<String, Object>>) amberData.get("breSub");
            if (breSub != null) {
                for (Map<String, Object> subData : breSub) {
                    VehicleLoanAmberSub amberSub = new VehicleLoanAmberSub();
                    amberSub.setVlamberSubKey(master);
                    amberSub.setVehicleLoanAmber(savedAmber);
                    amberSub.setWiNum(wiNum);
                    amberSub.setSlno(Long.parseLong(slNo));
                    amberSub.setAmberCode((String) amberData.get("breCode"));
                    amberSub.setAmberDesc((String) amberData.get("breDesc"));
                    amberSub.setApplicantType((String) subData.get("applicantType"));
                    if (subData.get("applicantId") != null) {
                        amberSub.setApplicantId(Long.valueOf(subData.get("applicantId").toString()));
                    } else {
                        amberSub.setApplicantId(null);
                    }
                    amberSub.setApplicantName(subData.get("applicantName").toString());
                    amberSub.setMasterValue(subData.get("masterValue").toString());
                    amberSub.setCurrentValue(subData.get("currentValue").toString());
                    amberSub.setColour((String) subData.get("color"));
                    amberSub.setLastModUser(usd.getEmployee().getPpcno());
                    amberSub.setLastModDate(new Date());
                    amberSub.setDelFlg("N");
                    amberSub.setActiveFlg("Y");
                    amberSub.setHomeSol(usd.getEmployee().getJoinedSol());
                    amberSub.setReqIpAddr(reqIpNo);
                    VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(amberSub.getApplicantId());
                    amberSub.setVehicleLoanApplicant(applicant);

                    amberSubRepository.save(amberSub);
                }
            }
        }
    }

    private void insertEligibilityData(Map<String, Object> responseMap, String wiNum, String slNo, String reqIpNo) {
        VehicleLoanMaster master = vehicleLoanMasterService.findBySlno(Long.valueOf(slNo));
        amberRepository.updateActiveFlag(wiNum, Long.valueOf(slNo));
        amberSubRepository.updateActiveFlag(wiNum, Long.valueOf(slNo));

        Map<String, Object> eligibilityData = (Map<String, Object>) responseMap.get("eligibilityData");

        for (Map.Entry<String, Object> entry : eligibilityData.entrySet()) {
            if (entry.getKey().startsWith("ELI")) {
                Map<String, Object> eliData = (Map<String, Object>) entry.getValue();

                VehicleLoanAmber amber = new VehicleLoanAmber();
                amber.setVlamberKey(master);
                amber.setWiNum(wiNum);
                amber.setSlno(Long.parseLong(slNo));
                amber.setAmberCode((String) eliData.get("eliCode"));
                amber.setAmberDesc((String) eliData.get("eliDesc"));
                amber.setColour((String) eliData.get("color"));
                amber.setLastModUser(usd.getEmployee().getPpcno());
                amber.setLastModDate(new Date());
                amber.setReqIpAddr(reqIpNo);
                amber.setHomeSol(usd.getEmployee().getJoinedSol());
                amber.setDelFlg("N");
                amber.setActiveFlg("Y");

                VehicleLoanAmber savedAmber = amberRepository.save(amber);

                List<Map<String, Object>> eliSub = (List<Map<String, Object>>) eliData.get("eliSub");
                if (eliSub != null) {
                    for (Map<String, Object> subData : eliSub) {
                        VehicleLoanAmberSub amberSub = new VehicleLoanAmberSub();
                        amberSub.setVlamberSubKey(master);
                        amberSub.setVehicleLoanAmber(savedAmber);
                        amberSub.setWiNum(wiNum);
                        amberSub.setSlno(Long.parseLong(slNo));
                        amberSub.setAmberCode((String) eliData.get("eliCode"));
                        amberSub.setAmberDesc((String) subData.get("Desc"));
                        amberSub.setApplicantType((String) subData.get("applicantType"));
                        if (subData.get("applicantId") != null) {
                            amberSub.setApplicantId(Long.valueOf(subData.get("applicantId").toString()));
                        } else {
                            amberSub.setApplicantId(null);
                        }
                        amberSub.setApplicantName(subData.get("applicantName").toString());
                        if (subData.get("masterValue") != null) {
                             amberSub.setMasterValue(subData.get("masterValue").toString());
                        } else {
                             amberSub.setMasterValue(null);
                        }
                        if (subData.get("currentValue") != null) {
                            amberSub.setCurrentValue(subData.get("currentValue").toString());
                        } else {
                            amberSub.setCurrentValue(null);
                        }


                        amberSub.setHomeSol(usd.getEmployee().getJoinedSol());
                        amberSub.setReqIpAddr(reqIpNo);
                        amberSub.setLastModUser(usd.getEmployee().getPpcno());
                        amberSub.setLastModDate(new Date());
                        amberSub.setColour((String) subData.get("color"));
                        amberSub.setDelFlg("N");
                        amberSub.setActiveFlg("Y");

                        VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(amberSub.getApplicantId());
                        amberSub.setVehicleLoanApplicant(applicant);

                        amberSubRepository.save(amberSub);
                    }
                }
            }
        }
    }
    @Transactional
    public Map<String, Object> getAmberData(String wiNum, String slNo, String reqIpNo) throws Exception {
        try {
        VehicleLoanMaster master = vehicleLoanMasterService.findBySlno(Long.valueOf(slNo));

        log.info("Calling the BRE Master service");
        String response = vlbrEngineservice.getAmberDatas(wiNum, Long.valueOf(slNo));

        if (response == null) {
            return createErrorResponse("No response from BRE service");
        }

        Map<String, Object> responseMap;
        try {
            responseMap = objectMapper.readValue(response, Map.class);
        } catch (JsonProcessingException e) {
            log.error("JSON Parsing exception", e);
            return createErrorResponse("Error parsing BRE service response");
        }

        // Get the current timestamp
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        // Mark existing records as deleted
        breDetailsRepository.updateDelFlgByWiNumAndSlno(wiNum, Long.valueOf(slNo));

        // Process and insert the new data
        processAmberData(response, wiNum, slNo, reqIpNo);

        if ("green".equals(responseMap.get("eligibilityFlag")) && "BCX".equals(master.getQueue())) {
            Map<String, Object> limitedResponse = new HashMap<>();
            limitedResponse.put("status", responseMap.get("status"));
            limitedResponse.put("eligibilityFlag", responseMap.get("eligibilityFlag"));
            limitedResponse.put("breFlag", responseMap.get("breFlag"));
            return limitedResponse;
        }

        vehicleLoanMasterService.updateStatus(Long.valueOf(slNo), "BCDRAFT");
        return responseMap;

    } catch (Exception e) {
        log.error("Error retrieving amber data for wiNum: {} and slNo: {}", wiNum, slNo, e);
        throw new VehicleLoanBREException("Error retrieving amber data", e);
    }


    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "ERROR");
        errorResponse.put("message", message);
        return errorResponse;
    }

    public List<VehicleLoanAmber> getAmberDataForLoan(String wiNum, Long slNo) {
        return amberRepository.findActiveByWiNumAndSlno(wiNum, slNo);
    }

    public Optional<VehicleLoanBREDetails> getLatestBREDetails(String wiNum, Long slno) {
        return breDetailsRepository.findTopByWiNumAndSlnoOrderByIdDesc(wiNum, slno);
    }


    public Optional<VehicleLoanAmber> getAmberDataForLoanAndCode(String wiNum, Long slNo, String amberCode) {
        return amberRepository.findActiveByWiNumAndSlnoAndAmberCode(wiNum, slNo, amberCode);
    }

    public List<VehicleLoanAmberSub> getAmberSubDataForLoan(String wiNum, Long slNo) {
        return amberSubRepository.findActiveByWiNumAndSlno(wiNum, slNo);
    }

    public List<VehicleLoanAmber> getAmberColorDataForLoan(String wiNum, Long slNo) {
        List<VehicleLoanAmber> amberList = amberRepository.findActiveByWiNumAndSlnoAndColour(wiNum, slNo);
        amberList.sort((a1, a2) -> {
            boolean isA1UserEntered = a1.getAmberCode().startsWith("RM");
            boolean isA2UserEntered = a2.getAmberCode().startsWith("RM");

            if (isA1UserEntered == isA2UserEntered) {
                return 0;
            } else if (isA1UserEntered) {
                return 1;
            } else {
                return -1;
            }
        });


        return amberList;
    }


    public List<VehicleLoanAmberSub> getAmberSubDataForLoanAndCode(String wiNum, Long slNo, String amberCode) {
        return amberSubRepository.findActiveByWiNumAndSlnoAndAmberCode(wiNum, slNo, amberCode);
    }

    public List<VehicleLoanAmberSub> getAmberSubDataForLoanAndApplicant(String wiNum, Long slNo, Long applicantId) {
        return amberSubRepository.findActiveByWiNumAndSlnoAndApplicantId(wiNum, slNo, applicantId);
    }
}
