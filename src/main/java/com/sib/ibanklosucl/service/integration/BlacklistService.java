package com.sib.ibanklosucl.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.losintegrator.blacklist.*;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.integrations.VLBlackList;
import com.sib.ibanklosucl.model.integrations.VLPartialBlackList;
import com.sib.ibanklosucl.repository.integations.VLBlackListRepository;
import com.sib.ibanklosucl.repository.integations.VLPartialBlacklistRepository;
import com.sib.ibanklosucl.service.VehicleLoanCifService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class BlacklistService {

    private final VLBlackListRepository vlBlackListRepository;
    private final BlacklistApiClient blacklistApiClient;
    private final UserSessionData usd;
    private final ObjectMapper objectMapper;
    private final VehicleLoanApplicantService vehicleLoanApplicantService;
    private final VLPartialBlacklistRepository vlPartialBlacklistRepository;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${esb.ChannelID}")
    private String channelID;
    private final VehicleLoanCifService vehicleLoanCifService;

    @Autowired
    public BlacklistService(VLBlackListRepository vlBlackListRepository,
                            BlacklistApiClient blacklistApiClient,
                            UserSessionData usd,
                            ObjectMapper objectMapper,
                            VehicleLoanApplicantService vehicleLoanApplicantService,
                            VLPartialBlacklistRepository vlPartialBlacklistRepository,
                            VehicleLoanCifService vehicleLoanCifService1) {
        this.vlBlackListRepository = vlBlackListRepository;
        this.blacklistApiClient = blacklistApiClient;
        this.usd = usd;
        this.objectMapper = objectMapper;
        this.vehicleLoanApplicantService = vehicleLoanApplicantService;
        this.vlPartialBlacklistRepository = vlPartialBlacklistRepository;
        this.vehicleLoanCifService = vehicleLoanCifService1;
    }

    public Optional<VLBlackList> findByApplicantIdAndDelFlg(Long applicantId, String delFlg) {
        if (applicantId == null || !StringUtils.hasText(delFlg)) {
            log.warn("Invalid input for findByApplicantIdAndDelFlg: applicantId={}, delFlg={}", applicantId, delFlg);
            return Optional.empty();
        }
        log.debug("Searching for blacklist entry: applicantId={}, delFlg={}", applicantId, delFlg);
        return vlBlackListRepository.findByApplicantIdAndDelFlg(applicantId, delFlg);
    }

    @Transactional
    public BlacklistResult performBlacklistCheck(BlacklistRequest blacklistRequest, String reqIP) {
        log.info("Performing blacklist check for applicant ID: {}", blacklistRequest.getOrigin());
        blacklistRequest.getRequest().setMerchantCode(merchantCode);
        blacklistRequest.getRequest().setMerchantName(merchantName);
        validateBlacklistRequest(blacklistRequest);

        try {
            BlacklistResponse blacklistResponse = blacklistApiClient.performBlacklistCheck(blacklistRequest);
            BlacklistResult blacklistResult = processBlacklistResponse(blacklistResponse);
            updateExistingBlacklist(blacklistRequest.getOrigin());
            saveNewBlacklist(blacklistRequest, blacklistResponse, blacklistResult, reqIP);
            return blacklistResult;
        } catch (Exception e) {
            log.error("Error during blacklist check for applicant ID: {}", blacklistRequest.getOrigin(), e);
            throw new RuntimeException("Error performing blacklist check", e);
        }
    }



    private void validateBlacklistRequest(BlacklistRequest blacklistRequest) {
        if (blacklistRequest == null || blacklistRequest.getOrigin() == null ||
            blacklistRequest.getWorkItemNumber() == null || blacklistRequest.getRequest() == null ||
            blacklistRequest.getRequest().getDob() == null || blacklistRequest.getRequest().getPan() == null) {
            log.error("Invalid BlacklistRequest: {}", blacklistRequest);
            throw new IllegalArgumentException("All parameters are required for Blacklist API check");
        }
    }

    @Transactional
    public PartialBlacklistResult performPartialBlacklistCheck(PartialBlacklistRequest blacklistRequest, String reqIP) {
        log.info("Performing partial blacklist check for applicant ID: {}", blacklistRequest.getOrigin());

        String custfullname=blacklistRequest.getRequest().getCustname();
        String firstName="", lastName="";
        if(custfullname!=null && custfullname.contains(" ")){
            firstName=custfullname.split(" ")[0];
            lastName = custfullname.substring(custfullname.indexOf(' ') + 1);
        }else{
            firstName=custfullname;
            lastName="";
        }
        blacklistRequest.getRequest().setFirstName(firstName);
        blacklistRequest.getRequest().setLastname(lastName);

        blacklistRequest.getRequest().setMerchantCode(merchantCode);
        blacklistRequest.getRequest().setMerchantName(merchantName);
        if(blacklistRequest.getRequest().getCountry().equals("IN")){
            blacklistRequest.getRequest().setCountry("INDIA");
        }
        validatePartialBlacklistRequest(blacklistRequest);

        try {
            PartialBlacklistResponse blacklistResponse = blacklistApiClient.performPartialBlacklistCheck(blacklistRequest);
            PartialBlacklistResult blacklistResult = processPartialBlacklistResponse(blacklistResponse);
            updateExistingPartialBlacklist(blacklistRequest.getOrigin());
            saveNewPartialBlacklist(blacklistRequest, blacklistResponse, blacklistResult, reqIP);
            vehicleLoanCifService.updateVLCifBlacklist(Long.valueOf(blacklistRequest.getOrigin()), reqIP);
            return blacklistResult;
        } catch (Exception e) {
            log.error("Error during blacklist check for applicant ID: {}", blacklistRequest.getOrigin(), e);
            throw new RuntimeException("Error performing blacklist check", e);
        }
    }

    private void validatePartialBlacklistRequest(PartialBlacklistRequest blacklistRequest) {

        if (blacklistRequest == null || blacklistRequest.getOrigin() == null ||
                blacklistRequest.getWorkItemNumber() == null || blacklistRequest.getRequest() == null ||
                blacklistRequest.getRequest().getCustname() == null || blacklistRequest.getRequest().getLastname() == null ||
                blacklistRequest.getRequest().getCountry() == null || blacklistRequest.getRequest().getGender() == null ||
                (blacklistRequest.getRequest().getFirstName()==null && blacklistRequest.getRequest().getLastname()==null)
        ) {
            log.error("Invalid PartialBlacklistRequest: {}", blacklistRequest);
            throw new IllegalArgumentException("All parameters are required for Partial Blacklist API check");
        }
    }

    private BlacklistResult processBlacklistResponse(BlacklistResponse blacklistResponse) {
        BlacklistResult blacklistResult = new BlacklistResult();
        String statusCode = blacklistResponse.getResponse().getStatus().getCode();
        log.debug("Blacklist API response status code: {}", statusCode);

        switch (statusCode) {
            case "200":
                blacklistResult.setBlacklisted(true);
                blacklistResult.setBlacklistReasons(blacklistResponse.getResponse().getBody().getBlacklistcheck().get(0).getFirstname());
                break;
            case "406":
                blacklistResult.setBlacklisted(false);
                blacklistResult.setBlacklistReasons("No records found");
                break;
            default:
                blacklistResult.setBlacklisted(false);
                blacklistResult.setBlacklistReasons("Error occurred");
                log.warn("Unexpected status code from Blacklist API: {}", statusCode);
        }
        return blacklistResult;
    }

    private PartialBlacklistResult processPartialBlacklistResponse(PartialBlacklistResponse blacklistResponse) {
        PartialBlacklistResult blacklistResult = new PartialBlacklistResult();
        String statusCode = blacklistResponse.getResponse().getStatus().getCode();
        log.debug("partial Blacklist API response status code: {}", statusCode);

        switch (statusCode) {
            case "200":
                blacklistResult.setStatusCode(statusCode);
                blacklistResult.setBody(blacklistResponse.getResponse().getBody().getBlacklistcheck());
                break;
            case "406":
                blacklistResult.setStatusCode(statusCode);
                blacklistResult.setBody(null);
                break;
            default:
                blacklistResult.setStatusCode("-1");
                blacklistResult.setBody(null);
                log.warn("Unexpected status code from Partial Blacklist API: {}", statusCode);
        }
        return blacklistResult;
    }
    private void updateExistingBlacklist(String origin) {
        VLBlackList existingBlacklist = vlBlackListRepository.findByApplicantIdAndDelFlg(Long.valueOf(origin), "N")
                .orElse(null);
        if (existingBlacklist != null) {
            existingBlacklist.setDelFlg("Y");
            vlBlackListRepository.save(existingBlacklist);
            log.debug("Updated existing blacklist entry to deleted for applicant ID: {}", origin);
        }
    }

    private void saveNewBlacklist(BlacklistRequest blacklistRequest, BlacklistResponse blacklistResponse,
                                  BlacklistResult blacklistResult, String reqIP) {
        try {
            VLBlackList newBlacklist = createNewBlacklistEntry(blacklistRequest, blacklistResponse, blacklistResult, reqIP);
            vlBlackListRepository.save(newBlacklist);
            log.info("Saved new blacklist entry for applicant ID: {}", blacklistRequest.getOrigin());
        } catch (JsonProcessingException e) {
            log.error("Error converting BlacklistResponse to JSON for applicant ID: {}", blacklistRequest.getOrigin(), e);
            throw new RuntimeException("Error saving blacklist entry", e);
        }
    }

    private VLBlackList createNewBlacklistEntry(BlacklistRequest blacklistRequest, BlacklistResponse blacklistResponse,
                                                BlacklistResult blacklistResult, String reqIP) throws JsonProcessingException {
        VLBlackList newBlacklist = new VLBlackList();
        newBlacklist.setApplicantId(Long.valueOf(blacklistRequest.getOrigin()));
        VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(Long.valueOf(blacklistRequest.getOrigin()));
        newBlacklist.setWiNum(blacklistRequest.getWorkItemNumber());
        newBlacklist.setSlNo(Long.valueOf(blacklistRequest.getSlno()));
        newBlacklist.setDob(blacklistRequest.getRequest().getDob());
        newBlacklist.setPan(blacklistRequest.getRequest().getPan());
        newBlacklist.setPassport(blacklistRequest.getRequest().getPassport());
        newBlacklist.setBlCheckDate(new Date());
        newBlacklist.setFetchResponse(objectMapper.writeValueAsString(blacklistResponse));
        newBlacklist.setBlCheckResult(blacklistResult.isBlacklisted() ? "Blacklisted" : "Not Blacklisted");
        newBlacklist.setCmUser(usd.getEmployee().getPpcno());
        newBlacklist.setCmDate(new Date());
        newBlacklist.setReqIpAddr(reqIP);
        newBlacklist.setVlblacklist(applicant);
        newBlacklist.setDelFlg("N");
        return newBlacklist;
    }


    private void updateExistingPartialBlacklist(String origin) {
        VLPartialBlackList existingBlacklist = vlPartialBlacklistRepository.findByApplicantIdAndDelFlg(Long.valueOf(origin), "N")
                .orElse(null);
        if (existingBlacklist != null) {
            existingBlacklist.setDelFlg("Y");
            vlPartialBlacklistRepository.save(existingBlacklist);
            log.debug("Updated existing partial blacklist entry to deleted for applicant ID: {}", origin);
        }
    }

    private void saveNewPartialBlacklist(PartialBlacklistRequest blacklistRequest, PartialBlacklistResponse blacklistResponse,
                                         PartialBlacklistResult blacklistResult, String reqIP) {
        try {
            VLPartialBlackList newBlacklist = createNewPartialBlacklistEntry(blacklistRequest, blacklistResponse, blacklistResult, reqIP);
            vlPartialBlacklistRepository.save(newBlacklist);
            log.info("Saved new partial blacklist entry for applicant ID: {}", blacklistRequest.getOrigin());
        } catch (JsonProcessingException e) {
            log.error("Error converting partialBlacklistResponse to JSON for applicant ID: {}", blacklistRequest.getOrigin(), e);
            throw new RuntimeException("Error saving partial blacklist entry", e);
        }
    }

    private VLPartialBlackList createNewPartialBlacklistEntry(PartialBlacklistRequest blacklistRequest, PartialBlacklistResponse blacklistResponse,
                                                              PartialBlacklistResult blacklistResult, String reqIP) throws JsonProcessingException {
        VLPartialBlackList newBlacklist = new VLPartialBlackList();
        newBlacklist.setApplicantId(Long.valueOf(blacklistRequest.getOrigin()));
        VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(Long.valueOf(blacklistRequest.getOrigin()));
        newBlacklist.setWiNum(blacklistRequest.getWorkItemNumber());
        newBlacklist.setSlNo(Long.valueOf(blacklistRequest.getSlno()));
        newBlacklist.setBlCheckDate(new Date());
        newBlacklist.setFetchResponse(objectMapper.writeValueAsString(blacklistResponse));
        newBlacklist.setCmUser(usd.getEmployee().getPpcno());
        newBlacklist.setCmDate(new Date());
        newBlacklist.setReqIpAddr(reqIP);
        newBlacklist.setVlpartialblacklist(applicant);
        newBlacklist.setDelFlg("N");
        newBlacklist.setCustName(blacklistRequest.getRequest().getCustname());
        newBlacklist.setLastName(blacklistRequest.getRequest().getLastname());
        newBlacklist.setGender(blacklistRequest.getRequest().getGender());
        newBlacklist.setCountry(blacklistRequest.getRequest().getCountry());
        return newBlacklist;
    }
}