package com.sib.ibanklosucl.service.impl;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;
import com.sib.ibanklosucl.dto.experian.DKResponse;
import com.sib.ibanklosucl.dto.experian.ExperianRequest;
import com.sib.ibanklosucl.dto.experian.ExperianResponse;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.PincodeMasterRepository;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.repository.VehicleEligiblityRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanBasicService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;

@Slf4j
@Service
public class ExperianCreditServiceImpl implements CommonService {


    @Autowired
    private VehicleEligiblityRepository vlEligiblityRepository;
    @Autowired
    private VehicleLoanBlockService vehicleLoanBlockService;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanApplicantService repository;
    @Autowired
    private VLCreditService vlCreditservice;
    @Autowired
    private BpmService bpmService;
    @Autowired
    private VehicleLoanDetailsService vehicleLoanDetailsService;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${esb.ChannelID}")
    private String channelID;
    @Value("${experian.id}")
    private String experianID;


    @Autowired
    private VehicleLoanBasicService vehicleLoanBasicService;
    @Autowired
    private PincodeMasterRepository pincodeMasterRepository;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private ValidationRepository validationRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TabResponse processData(CommonDTO requests) {
        boolean insertblock = false;
        CommonDTO.ExperianForm experianForm = requests.getExperianForm();
        VehicleLoanApplicant applicant = repository.getById(Long.valueOf(experianForm.getAppid()));
        ExperianRequest request = createExperianRequest(applicant, experianForm);
        String errorCode="",errorMessage="";

        VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
        vehicleLoanBlock.setWiNum(experianForm.getWinum());
        vehicleLoanBlock.setSlno(Long.parseLong(experianForm.getSlno()));
        vehicleLoanBlock.setApplicantId(applicant.getApplicantId().toString());
        try {


            ExperianResponse response = fetchExperianReport(request);

            if ("SUCCESS".equalsIgnoreCase(response.getStatus())) {
                log.info("API Response ExperianN {}", response);
                ExperianRequest dkrequest = createDkRequest(applicant, response, experianForm.getSlno());
                DKResponse dkResponse = fetchDKReport(dkrequest);
                if (response.getPdf() == null || response.getPdf().isBlank()) {

                    vehicleLoanBlock.setBlockType(VLBlockCodes.EXPERIAN_PDF_FAILED);
                    vehicleLoanBlock.setParticulars(request.getPan());
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    insertblock = true;
                    return new TabResponse("W", objectMapper.writeValueAsString(response));
                }
                if ("SUCCESS".equalsIgnoreCase(dkResponse.getStatus())) {
                    VLCredit vl = vlCreditservice.findByApplicantIdAndDelFlg(applicant.getApplicantId());
                    Long Score = Long.valueOf(response.getScore());
                    String vEp = fetchRepository.getWIProgram(Long.valueOf(experianForm.getSlno()));
                    TypeCount tc = CommonUtils.parseString(experianForm.getReqtype());
                    TabResponse tb1 = bpmService.BpmUpload(bpmRequest(experianForm.getWinum(), response.getPdf(), "Y", CommonUtils.expandReq(tc), "EXPERIAN_REPORT"));
                    if (!"S".equalsIgnoreCase(tb1.getStatus())) {

                        vehicleLoanBlock.setBlockType(VLBlockCodes.EXPERIAN_PDFTRAY_FAILED);
                        vehicleLoanBlock.setParticulars(request.getPan() + "~" + CommonUtils.expandReq(tc));
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                        insertblock = true;
                        return new TabResponse(tb1.getStatus(), tb1.getMsg());
                    }

                    if (vEp.isBlank() || validationRepository.checkExperianEligibleColor(vEp, "", Score)) {
                        if (vl == null) {
                            vl = new VLCredit();
                            vl.setReqIpAddr(usd.getRemoteIP());
                            vl.setSlno(Long.valueOf(experianForm.getSlno()));
                            vl.setWiNum(experianForm.getWinum());
                            vl.setApplicantId(applicant.getApplicantId());
                            vl.setDelFlg("N");
                        }
                        vl.setDkFlag(true);
//                        vl.setDkScore(800L);
                        vl.setDkScore(Long.valueOf(dkResponse.getRaceScore()));
                        vl.setBureauScore(Score);
                        vl.setExperianFlag(true);
                        vl.setTotObligations(null);
                        vl.setLastmodDate(new Date());
                        vl.setLastmodUser(usd.getPPCNo());
                        vl.setHomeSol(usd.getSolid());
                        vl.setExpLoanAmt(experianForm.getExpLoanAmt());
                        vl.setExpTenure(experianForm.getExptenure());
                        vl.setExpFetchDate(CommonUtils.DateFormat(response.getFetchTime()));
//                        TypeCount tc = CommonUtils.parseString(experianForm.getReqtype());
//                        TabResponse tb1 = bpmService.BpmUpload(bpmRequest(experianForm.getWinum(), response.getPdf(), "Y", CommonUtils.expandReq(tc), "EXPERIAN_REPORT"));
//                        if (!"S".equalsIgnoreCase(tb1.getStatus())) {
//                            return new TabResponse(tb1.getStatus(), tb1.getMsg());
//                        }
                        applicant.setCreditComplete("N");
                        vlCreditservice.save(vl);
                        repository.saveApplicant(applicant);
                        JsonObject msg = new JsonObject();
                        msg.addProperty("score", response.getScore());
                        msg.addProperty("fetchTime", response.getFetchTime());
                        if (dkResponse.getLiabilityList().size() > 0)
                            msg.addProperty("liabilityList", objectMapper.writeValueAsString(dkResponse.getLiabilityList()));
                        else
                            msg.addProperty("liabilityList", "");
                        return new TabResponse("S", msg.toString());
                    } else {
                        applicant.setCreditComplete("N");
                        response.setErrorReason("Bureau Score is not in Allowed Range(Refer Pdf in Tray)!!");

                        vehicleLoanBlock.setBlockType(VLBlockCodes.EXPERIAN_SCORE_RANGE);
                        vehicleLoanBlock.setParticulars(request.getPan() + "," + vlEligiblityRepository.findscoredata(vEp).get(0).get("SCOREVIEW"));
                        vehicleLoanBlock.setActualValue(Score.toString());
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                        insertblock = true;
                    }

                    if (!insertblock) {
                        vehicleLoanBlock.setActualValue(Score.toString());
                        vehicleLoanBlock.setBlockType(VLBlockCodes.EXPERIAN_SOMEERROR);
                        vehicleLoanBlock.setParticulars(request.getPan() + "~" + response.getErrorReason());
                        insertblock = true;
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }
                    return new TabResponse("F", response.getErrorReason());
                } else {
                    vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SOME_FAILURE);
                    vehicleLoanBlock.setParticulars(request.getPan() + "," + dkResponse.getErrorreason());
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    insertblock = true;
                    return new TabResponse("F", dkResponse.getErrorreason());
                }
            } else {
                vehicleLoanBlock.setBlockType(VLBlockCodes.EXPERIAN_FAILURE_RESPONSE);
                vehicleLoanBlock.setParticulars(request.getPan() + "~" + response.getErrorReason());
                vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                insertblock = true;
                return new TabResponse("F", response.getErrorReason() != null ?
                    response.getErrorReason() : "Bureau Report Fetch Failed");
            }
        } catch (Exception e) {
            log.error("Error during Report generation Reason ", e);
            vehicleLoanBlock.setBlockType(VLBlockCodes.DK_SOME_FAILURE);
            vehicleLoanBlock.setParticulars(request.getPan() + "," + e.getMessage());
            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
            return new TabResponse("F", e.getMessage());
        }


    }

    @Override
    public ResponseDTO processDataDoc(FileUploadForm request) {
        return null;
    }

    public BpmRequest bpmRequest(String winum, String pdf, String child, String childName, String docName) {
        BPMFileUpload bpmFileUpload = new BPMFileUpload();
        bpmFileUpload.setWI_NAME(winum);
        bpmFileUpload.setCHILD(child);
        bpmFileUpload.setCHILD_FOLDER(childName);
        bpmFileUpload.setSystemIP(usd.getRemoteIP());
        List<DOC_ARRAY> docArrayList = new ArrayList<>();
        DOC_ARRAY docArray = new DOC_ARRAY();
        docArray.setDOC_NAME(docName + CommonUtils.getCurrentTimestamp());
        docArray.setDOC_EXT("pdf");
        docArray.setDOC_BASE64(pdf);
        docArrayList.add(docArray);
        bpmFileUpload.setDOC_ARRAY(docArrayList);
        BpmRequest bpmRequest = new BpmRequest();
        bpmRequest.setRequest(bpmFileUpload);
        return bpmRequest;
    }

    @Value("${api.integrator}")
    private String experianApiUrl;

    public DKResponse fetchDKReport(ExperianRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            log.error("Error serializing request", e);
            throw new RuntimeException("Error preparing request", e);
        }
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        try {
            String responseBody = restTemplate.postForObject(experianApiUrl, entity, String.class);
            DKResponse response = objectMapper.readValue(responseBody, DKResponse.class);
            return response;
        } catch (Exception e) {
            log.error(" ScoreCard report Fetch Failed", e);
            throw new RuntimeException(" ScoreCard report Fetch Failed", e);
        }
    }


    public ExperianResponse fetchExperianReport(ExperianRequest request) {
        log.info("Fetching Experian report for PAN: {}", request.getPan());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(request);
//           requestBody = "{\n" +
//                   "    \"request\": {\n" +
//                   "        \"UUID\": \""+UUID.randomUUID()+"\",\n" +
//                   "        \"merchantCode\": \"INFM\",\n" +
//                   "        \"merchantName\": \"Information_Bank\",\n" +
//                   "        \"experianCCIRReq\": {\n" +
//                   "            \"enqheader\": {\n" +
//                   "                \"reqFormat\": \"JSON\",\n" +
//                   "                \"branchId\": \"\",\n" +
//                   "                \"clientEnquiryRefNumber\": \"\",\n" +
//                   "                \"product\": \"CCIRIN003\",\n" +
//                   "                \"kendraId\": \"\",\n" +
//                   "                \"purpose\": \"19\",\n" +
//                   "                \"searchType\": \"1\",\n" +
//                   "                \"enquiryAmount\": \"11000\",\n" +
//                   "                \"durationofAgreement\": \"1\",\n" +
//                   "                \"enquiryAccountType\": \"47\",\n" +
//                   "                \"frequency\": \"99\",\n" +
//                   "                \"bureauMemberId\": \"1651\",\n" +
//                   "                \"enquiryCreditPurpose\": \"8\",\n" +
//                   "                \"enquiryAmtMonetaryType\": \"INR\",\n" +
//                   "                \"enquiryApplicationType\": \"1\",\n" +
//                   "                \"purposeOfInquiry\": \"2\"\n" +
//                   "            },\n" +
//                   "            \"userpref\": {\n" +
//                   "                \"language\": \"\"\n" +
//                   "            },\n" +
//                   "            \"addlprod\": [\n" +
//                   "                {\n" +
//                   "                    \"enquiryAddOnProduct\": \"\"\n" +
//                   "                }\n" +
//                   "            ],\n" +
//                   "            \"consumerRequest\": {\n" +
//                   "                \"prsnsrch\": [\n" +
//                   "                    {\n" +
//                   "                        \"firstGivenName\": \"Paresh Deshmukh\",\n" +
//                   "                        \"middleName\": \"\",\n" +
//                   "                        \"otherMiddleNames\": \"\",\n" +
//                   "                        \"familyName\": \"Paresh Deshmukh\",\n" +
//                   "                        \"suffix\": \"\",\n" +
//                   "                        \"applicationRole\": \"\",\n" +
//                   "                        \"dateOfBirth\": \"13071989\",\n" +
//                   "                        \"gender\": \"2\",\n" +
//                   "                        \"indiaMiddleName3\": \"\",\n" +
//                   "                        \"indiaNameTitle\": \"\"\n" +
//                   "                    }\n" +
//                   "                ],\n" +
//                   "                \"persalias\": {\n" +
//                   "                    \"aliasName\": \"\",\n" +
//                   "                    \"aliasType\": \"\"\n" +
//                   "                },\n" +
//                   "                \"personid\": [\n" +
//                   "                    {\n" +
//                   "                        \"idNumberType\": \"10\",\n" +
//                   "                        \"idNumber\": \"SZDPD4007L\",\n" +
//                   "                        \"idIssueDate\": \"\",\n" +
//                   "                        \"idExpirationDate\": \"\"\n" +
//                   "                    }\n" +
//                   "                ],\n" +
//                   "                \"personbnk\": {\n" +
//                   "                    \"bankAccountNumber\": \"\"\n" +
//                   "                },\n" +
//                   "                \"persaddr\": [\n" +
//                   "                    {\n" +
//                   "                        \"regionCode\": \"27\",\n" +
//                   "                        \"localityName\": \"MH\",\n" +
//                   "                        \"countryCode\": \"\",\n" +
//                   "                        \"postalCode\": \"400078\",\n" +
//                   "                        \"addressLine1\": \"F-1 1000\",\n" +
//                   "                        \"addressLine2\": \"SOYRA COMPLEX\",\n" +
//                   "                        \"addressLine3\": \"MUMBAI\",\n" +
//                   "                        \"landmark\": \"\",\n" +
//                   "                        \"addrType\": \"\"\n" +
//                   "                    }\n" +
//                   "                ],\n" +
//                   "                \"persphone\": [\n" +
//                   "                    {\n" +
//                   "                        \"phoneNumber\": \"8974432542\",\n" +
//                   "                        \"phoneType\": \"6\",\n" +
//                   "                        \"phoneNumberExtension\": \"\"\n" +
//                   "                    }\n" +
//                   "                ],\n" +
//                   "                \"persemail\": [\n" +
//                   "                    {\n" +
//                   "                        \"webAddrType\": \"\",\n" +
//                   "                        \"webAddr\": \"\"\n" +
//                   "                    }\n" +
//                   "                ],\n" +
//                   "                \"employer\": {\n" +
//                   "                    \"occupationCode\": \"\",\n" +
//                   "                    \"netMontlyIncome\": \"\",\n" +
//                   "                    \"occYearsEmployed\": \"\",\n" +
//                   "                    \"occMonthsEmployed\": \"\"\n" +
//                   "                },\n" +
//                   "                \"persdetail\": {\n" +
//                   "                    \"numberOfCreditCardHeld\": \"\",\n" +
//                   "                    \"maritalStatus\": \"\",\n" +
//                   "                    \"monthlyFamilyExpenseAmt\": \"\",\n" +
//                   "                    \"numberDependents\": \"\",\n" +
//                   "                    \"povertyIndex\": \"\",\n" +
//                   "                    \"assetOwnershipIndicator\": \"\"\n" +
//                   "                },\n" +
//                   "                \"pinid\": [\n" +
//                   "                    {\n" +
//                   "                        \"experianEncryptedPIN\": \"\"\n" +
//                   "                    }\n" +
//                   "                ]\n" +
//                   "            },\n" +
//                   "            \"additionalInfo\": null,\n" +
//                   "            \"addonfields\": {\n" +
//                   "                \"field1\": \"\",\n" +
//                   "                \"field2\": \"\",\n" +
//                   "                \"field3\": \"\",\n" +
//                   "                \"field4\": \"\"\n" +
//                   "            }\n" +
//                   "        }\n" +
//                   "    },\n" +
//                   "    \"mock\": false,\n" +
//                   "    \"apiName\": \"fetchExperian\",\n" +
//                   "    \"userId\": \"12071\",\n" +
//                   "    \"slno\": \"2006\",\n" +
//                   "    \"winum\": \"VLR_0000002006\",\n" +
//                   "    \"appid\": \"2006\",\n" +
//                   "    \"pan\":\"SZDPD4007L\"\n" +
//                   "}";
        } catch (Exception e) {
            log.error("Error serializing request", e);
            throw new RuntimeException("Error preparing request", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        try {
            String responseBody = restTemplate.postForObject(experianApiUrl, entity, String.class);
            ExperianResponse response = objectMapper.readValue(responseBody, ExperianResponse.class);
            if (response.getJsonResponse() != null) {
                JsonNode jsonResponseNode = objectMapper.readTree(response.getJsonResponse());
                JsonNode statusNode = jsonResponseNode
                    .path("Response")
                    .path("Body")
                    .path("experianCCIRRes")
                    .path("status");
                if (statusNode != null && statusNode.has("error_code") && statusNode.has("message")) {
                    String errorCode = statusNode.get("error_code").asText();
                    String errorMessage = statusNode.get("message").asText();
                    response.setErrorReason(String.format("Experian Bureau Fetch failed [Error code :%s] - %s", errorCode, errorMessage));
                }
            }
            log.info("Successfully fetched Experian report for PAN: {}", request.getPan());
            return response;
        } catch (Exception e) {
            log.error("Error fetching Experian report for PAN: {}", request.getPan(), e);
            throw new RuntimeException(" Experian report fetch Failed", e);
        }
    }

    public ExperianRequest createDkRequest(VehicleLoanApplicant data, ExperianResponse experianResponse, String slno) throws JsonProcessingException {
        VehicleLoanBasic vehicleLoanBasic = data.getBasicapplicants();
        ExperianRequest request = new ExperianRequest();
        request.setMock(false);
        request.setApiName("fetchDK");
        request.setUserId(usd.getPPCNo());
        request.setSlno(String.valueOf(data.getSlno()));
        request.setWorkItemNumber(data.getWiNum());
        request.setAppid(String.valueOf(data.getApplicantId()));
        request.setOrigin(String.valueOf(data.getApplicantId()));
        request.setExperian_ino(experianResponse.getExperian_ino());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        ObjectNode requestNode = rootNode.putObject("request");
        requestNode.put("UUID", UUID.randomUUID().toString());
        requestNode.put("merchantCode", merchantCode);
        requestNode.put("merchantName", merchantName);

        // Create score_engine_API_request node
        ObjectNode scoreEngineApiRequestNode = requestNode.putObject("score_engine_API_request");

        // Add customer_details array
        ArrayNode customerDetailsArray = scoreEngineApiRequestNode.putArray("customer_details");
        // Add details to customer_details array
        String currentQueue = fetchRepository.getWIQueue(Long.valueOf(slno));

        ObjectNode customerDetails = customerDetailsArray.addObject();
        customerDetails.put("unique_ref_no", UUID.randomUUID().toString());
        customerDetails.put("Product", vehicleLoanBasicService.isKerala(Long.valueOf(slno)) ? "AL" : "AL_NON_KERALA");
        customerDetails.put("type_borrower", "INDV");
        customerDetails.put("bureau_name", "EXPERIAN");
        customerDetails.put("AssetClassification", "New");
        if ("RM".equals(currentQueue)) {
            customerDetails.put("ScoringRequired", "Y");
        } else {
            customerDetails.put("ScoringRequired", "N");
        }

        customerDetails.put("Bureau_Summary", "Y");
        customerDetails.put("Grade", "Personal");
        customerDetails.put("NewtoCredit", experianResponse.getNewtoCredit());
        customerDetails.put("LTVRequest", "");
        customerDetails.put("Manufacturer", "");
        customerDetails.put("CollateralType", "");
        customerDetails.put("VehicleCategory", "");
        customerDetails.put("FirstName", vehicleLoanBasic.getApplicantName());
        customerDetails.put("LastName", "");
        customerDetails.put("DateOfBirth", CommonUtils.ConvertDate(vehicleLoanBasic.getApplicantDob(), "yyyy-MM-dd").replace("-", ""));
        customerDetails.put("CustomerType", data.getSibCustomer().equalsIgnoreCase("Y") ? "Existing to Bank" : "New to Bank");
        customerDetails.put("CustomerId", data.getSibCustomer().equalsIgnoreCase("Y") ? data.getCifId() : "");


        // Add empty bureau_data array
        ArrayNode bureauData = scoreEngineApiRequestNode.putArray("bureau_data");
        // Add details to customer_details array
        JsonNode bureauDetails = objectMapper.readTree(experianResponse.getExperiaCCIRJsonReport());
        bureauData.add(bureauDetails);

        request.setRequest(rootNode.get("request"));
        return request;
    }

    public ExperianRequest createExperianRequest(VehicleLoanApplicant data, CommonDTO.ExperianForm experianForm) {


        //
        String tenure = experianForm.getExptenure() != null ? experianForm.getExptenure().toString() : "";
        String loanAmt = experianForm.getExpLoanAmt() != null ? experianForm.getExpLoanAmt().toString() : "";
        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(data.getWiNum(), data.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails != null) {
            tenure = String.valueOf(loanDetails.getTenor());
            loanAmt = loanDetails.getLoanAmt() != null ? loanDetails.getLoanAmt().toString() : loanAmt;
        }

        VehicleLoanBasic vehicleLoanBasic = data.getBasicapplicants();
        VehicleLoanKyc kyc = data.getKycapplicants();
        ExperianRequest request = new ExperianRequest();
        request.setMock(false);
        request.setApiName("fetchExperian");
        request.setUserId(usd.getPPCNo());
        request.setSlno(String.valueOf(data.getSlno()));
        request.setWorkItemNumber(data.getWiNum());
        request.setAppid(String.valueOf(data.getApplicantId()));
        request.setOrigin(String.valueOf(data.getApplicantId()));
        request.setPan(kyc.getPanNo());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        ObjectNode requestNode = rootNode.putObject("request");

        requestNode.put("UUID", UUID.randomUUID().toString());
        requestNode.put("merchantCode", merchantCode);
        requestNode.put("merchantName", merchantName);

        ObjectNode experianCCIRReq = requestNode.putObject("experianCCIRReq");
        ObjectNode enqheader = experianCCIRReq.putObject("enqheader");
        enqheader.put("clientEnquiryRefNumber", "");
        enqheader.put("bureauMemberId", experianID);
        enqheader.put("reqFormat", "JSON");
        enqheader.put("purposeOfInquiry", "2");
        enqheader.put("purpose", "19");
        enqheader.put("product", "CCIRIN003");
        enqheader.put("searchType", "1");
        enqheader.put("enquiryApplicationType", "");
        enqheader.put("enquiryAccountType", "47");
        enqheader.put("enquiryAmtMonetaryType", "INR");
        enqheader.put("enquiryAmount", loanAmt);//loanAmt
        enqheader.put("enquiryCreditPurpose", "8");
        enqheader.put("durationofAgreement", tenure);//tenure
        enqheader.put("frequency", "99");
        enqheader.put("kendraId", "");
        enqheader.put("branchId", "");

        experianCCIRReq.putObject("userpref").put("language", "");

        ArrayNode addlprod = experianCCIRReq.putArray("addlprod");
        addlprod.addObject().put("enquiryAddOnProduct", "");

        ObjectNode consumerRequest = experianCCIRReq.putObject("consumerRequest");
        ArrayNode prsnsrch = consumerRequest.putArray("prsnsrch");
        ObjectNode person = prsnsrch.addObject();
        person.put("firstGivenName", data.getApplName());
        person.put("middleName", "");
        person.put("otherMiddleNames", "");
        String familyName = Optional.ofNullable(data.getApplName())
//                .map(name -> {
//                    name = name.trim();
//                    if (name.contains(" ")) {
//                        return name.substring(name.lastIndexOf(" ") + 1);
//                    } else {
//                        return name;
//                    }

//                })
                .orElse("");

        person.put("familyName", familyName);
        person.put("suffix", "");
        person.put("applicationRole", "");
        person.put("dateOfBirth", CommonUtils.getDob(String.valueOf(data.getApplDob())).replace("-", ""));
        String gender = switch (vehicleLoanBasic.getGender()) {
            case "F":
                yield "1";
            case "M":
                yield "2";
            case "T":
                yield "3";
            default:
                yield "";
        };
        person.put("gender", gender);
        person.put("indiaMiddleName3", "");
        person.put("indiaNameTitle", "");

        consumerRequest.putObject("persalias")
                .put("aliasName", "")
                .put("aliasType", "");

        ArrayNode personid = consumerRequest.putArray("personid");
        personid.addObject()
                .put("idNumberType", "10")
                .put("idNumber", kyc.getPanNo())
                .put("idIssueDate", "")
                .put("idExpirationDate", "");
        if (kyc.getPassportNumber() != null) {
            personid.addObject()
                    .put("idNumberType", "4")
                    .put("idNumber", kyc.getPassportNumber())
                    .put("idIssueDate", "")
                    .put("idExpirationDate", String.valueOf(kyc.getPassportExpiryDate()));
        }
//        if( kyc.getAadharRefNum()!=null) {
//            personid.addObject()
//                    .put("idNumberType", "12")
//                    .put("idNumber", kyc.getAadharRefNum())
//                    .put("idIssueDate", "")
//                    .put("idExpirationDate", "");
//        }

        consumerRequest.putObject("personbnk")
                .put("bankAccountNumber", "");

        Optional<ExperianPincodeMasterDTO> code = pincodeMasterRepository.getExpData(vehicleLoanBasic.getState());
        Optional<ExperianPincodeMasterDTO> codeCom = pincodeMasterRepository.getExpData(vehicleLoanBasic.getComState());
        if (code.isEmpty() && codeCom.isEmpty())
            throw new ValidationException(ValidationError.COM001, "State is Not Mapped With Bureau State");
        String adr1 = "", adr2 = "", adr3 = "", pin = "", reg = "", loc = "";
        if (code.isPresent()) {
            adr1 = vehicleLoanBasic.getAddr1();
            adr2 = vehicleLoanBasic.getAddr2();
            adr3 = vehicleLoanBasic.getAddr3();
            pin = vehicleLoanBasic.getPin();
            reg = code.get().getRegionCode();
            loc = code.get().getExperianStateCode();
        } else {
            adr1 = vehicleLoanBasic.getComAddr1();
            adr2 = vehicleLoanBasic.getComAddr2();
            adr3 = vehicleLoanBasic.getComAddr3();
            pin = vehicleLoanBasic.getComPin();
            reg = codeCom.get().getRegionCode();
            loc = codeCom.get().getExperianStateCode();
        }

        ArrayNode persaddr = consumerRequest.putArray("persaddr");
        persaddr.addObject()
                .put("addrType", "")
                .put("localityName", loc)
                .put("regionCode", reg)
                .put("postalCode", pin)
                .put("countryCode", "")
                .put("addressLine1", adr1)
                .put("addressLine2", adr2)
                .put("addressLine3", adr3)
                .put("landmark", "");

        ArrayNode persphone = consumerRequest.putArray("persphone");
        persphone.addObject()
                .put("phoneNumber", vehicleLoanBasic.getMobileNo())
                .put("phoneType", "6")
                .put("phoneNumberExtension", "");

        String theemail = vehicleLoanBasic.getEmailId();
        if (theemail != null) {
            if (theemail.contains("_")) {
                theemail = "";
            }
        }
        ArrayNode persemail = consumerRequest.putArray("persemail");
        persemail.addObject()
                .put("webAddrType", "")
                .put("webAddr", theemail);//vehicleLoanBasic.getEmailId()

        consumerRequest.putObject("employer")
                .put("occupationCode", "")
                .put("netMontlyIncome", "")
                .put("occYearsEmployed", "")
                .put("occMonthsEmployed", "");

        consumerRequest.putObject("persdetail")
                .put("numberOfCreditCardHeld", "")
                .put("maritalStatus", "")
                .put("monthlyFamilyExpenseAmt", "")
                .put("numberDependents", "")
                .put("povertyIndex", "")
                .put("assetOwnershipIndicator", "");

        ArrayNode pinid = consumerRequest.putArray("pinid");
        pinid.addObject().put("experianEncryptedPIN", "");

        experianCCIRReq.putNull("additionalInfo");

        experianCCIRReq.putObject("addonfields")
                .put("field1", "")
                .put("field2", "")
                .put("field3", "")
                .put("field4", "");

        request.setRequest(rootNode.get("request"));

        return request;
    }

}
