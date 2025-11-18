package com.sib.ibanklosucl.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sib.ibanklosucl.dto.CommonDTO;
import com.sib.ibanklosucl.dto.VehicleEmpProgram;
import com.sib.ibanklosucl.dto.bre.AmberData;
import com.sib.ibanklosucl.dto.bre.AmberDeviationUpdateRequest;
import com.sib.ibanklosucl.dto.experian.ExperianRequest;
import com.sib.ibanklosucl.dto.experian.ExperianResponse;
import com.sib.ibanklosucl.dto.losintegrator.dk.DKScoreResponse;
import com.sib.ibanklosucl.dto.losintegrator.dk.ScoreInfo;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.service.impl.ExperianCreditServiceImpl;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanBasicService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import com.sib.ibanklosucl.repository.VehicleLoanApplicantRepository;

@Service
public class DKScoreService {
    private static final Logger log = LoggerFactory.getLogger(DKScoreService.class);

    @Autowired
    private VehicleLoanApplicantRepository vehicleLoanApplicantRepository;
    @Autowired
    private VehicleLoanBlockService vehicleLoanBlockService;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;

    @Autowired
    private ExperianCreditServiceImpl experianCreditService;

    @Autowired
    private UserSessionData usd;

    @Autowired
    private VLCreditService vlCreditService;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private ValidationRepository validationRepository;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private VehicleLoanAmberService vehicleLoanAmberService;
    @Autowired
    private VehicleDetailsService vehicleDetailsService;
    @Autowired
    private VehicleLoanBasicService vehicleLoanBasicService;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${esb.ChannelID}")
    private String channelID;
    @Autowired
    private ObjectMapper objectMapper;
    private static final Map<String, String> bodyTypeMappings = new HashMap<>();

    static {
        bodyTypeMappings.put("Wagons", "Sedans");
        bodyTypeMappings.put("Minivans", "Sedans");
        bodyTypeMappings.put("Sport Utilities", "Sedans");
        bodyTypeMappings.put("Coupe", "Hatchback");
        bodyTypeMappings.put("MUV", "Sedans");
        bodyTypeMappings.put("Hatchback", "Hatchback");
        bodyTypeMappings.put("Convertibles", "Sedans");
        bodyTypeMappings.put("Pickup Trucks", "Sedans");
        bodyTypeMappings.put("Sedans", "Sedans");
    }

    public String decodeBodyType(String bodyType) {
        return bodyTypeMappings.getOrDefault(bodyType, "Unknown");
    }


    public DKScoreResponse runDKScoreForAllApplicants(String wiNum, String slno) {
        log.info("Starting DK Score check for wiNum: {}, slno: {}", wiNum, slno);
        List<VehicleLoanApplicant> applicants = vehicleLoanApplicantService.findBySlnoAndDelFlg(Long.valueOf(slno));
        List<DKScoreResponse.DKScoreItem> dkScoreItems = new ArrayList<>();
        String applicantState = "";
        boolean hasRedScore = false;
        boolean hasGreenOrAmberScore = false;

        for (VehicleLoanApplicant applicant : applicants) {
            if (applicant.getApplicantType().equals("A")) {
                applicantState = applicant.getBasicapplicants().getState();
            }
            try {
                DKScoreResponse.DKScoreItem dkScoreItem = processDKScoreForApplicant(applicant, wiNum, slno, applicantState);
                dkScoreItems.add(dkScoreItem);
                if ("red".equalsIgnoreCase(dkScoreItem.getColor())) {
                    hasRedScore = true;
                } else if ("green".equalsIgnoreCase(dkScoreItem.getColor()) || "amber".equalsIgnoreCase(dkScoreItem.getColor())) {
                    hasGreenOrAmberScore = true;
                }
            } catch (Exception e) {
                log.error("Error fetching DK Score for applicant {}: {}", applicant.getApplicantId(), e.getMessage(), e);
                DKScoreResponse.DKScoreItem errorItem = createErrorDKScoreItem(applicant, e.getMessage());
                dkScoreItems.add(errorItem);
            }
        }
                String finalColor = determineMultiApplicantColor(hasRedScore, hasGreenOrAmberScore, applicants.size());

        updateAllVLCredits(applicants, finalColor);

        if (applicants.size() > 1 && hasRedScore && hasGreenOrAmberScore) {
            for (DKScoreResponse.DKScoreItem item : dkScoreItems) {
                if ("red".equalsIgnoreCase(item.getColor())) {
                    item.setColor("amber");
                    item.setScoreRange("Amber (Adjusted)");
                    log.info("Adjusted score for applicant {} from Red to Amber due to multiple applicants rule", item.getApplicantName());
                }
            }

        }

        DKScoreResponse response = new DKScoreResponse();
        response.setMessage("DK Score check completed for all applicants.");
        response.setDkScoreItems(dkScoreItems);
        log.info("Completed DK Score check for wiNum: {}, slno: {}", wiNum, slno);
        return response;
    }

    public ExperianRequest createDkRequest(VehicleLoanApplicant data, ExperianResponse experianResponse) throws JsonProcessingException {
        VehicleLoanVehicle vehicleLoanVehicleOptional = vehicleDetailsService.fetchExistingbySlno(data.getSlno());
        String decodedBodyType = decodeBodyType(fetchRepository.findBodyTypeByMakeAndVariant(vehicleLoanVehicleOptional.getMakeId(),vehicleLoanVehicleOptional.getVariantId()));
        String category = fetchRepository.findCategoryByMakeAndModel(vehicleLoanVehicleOptional.getMakeName());
        String ltvPer = fetchRepository.findVehicleLTV(data.getSlno());
        String customerType = data.getCifId() != null ? "Existing to Bank" : "New to Bank";
        String vehicleCategory = switch (category) {
        case "Mass" -> "A";
        case "Affluent" -> "B";
        case "Luxury" -> "C";
        default -> "D";
    };

        VehicleLoanBasic vehicleLoanBasic = data.getBasicapplicants();
        ExperianRequest request = new ExperianRequest();
        request.setMock(false);
        request.setApiName("fetchDK");
        request.setUserId(usd.getPPCNo());
        request.setSlno(String.valueOf(data.getSlno()));
        request.setWorkItemNumber(data.getWiNum());
        request.setAppid(String.valueOf(data.getApplicantId()));
        request.setExperian_ino(experianResponse.getExperian_ino());
        request.setOrigin(String.valueOf(data.getApplicantId()));

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

        ObjectNode customerDetails = customerDetailsArray.addObject();
        customerDetails.put("unique_ref_no", UUID.randomUUID().toString());
        customerDetails.put("Product", vehicleLoanBasicService.isKerala(data.getSlno())?"AL":"AL_NON_KERALA");
        customerDetails.put("type_borrower", "INDV");
        customerDetails.put("bureau_name", "EXPERIAN");
        customerDetails.put("AssetClassification", "New");
        customerDetails.put("ScoringRequired", "Y");
        customerDetails.put("Bureau_Summary", "Y");
        customerDetails.put("Grade", "Personal");
        customerDetails.put("NewtoCredit", experianResponse.getNewtoCredit());
        customerDetails.put("LTVRequest", ltvPer);
        customerDetails.put("Manufacturer", category);
        customerDetails.put("CollateralType", decodedBodyType);
        customerDetails.put("VehicleCategory", vehicleCategory);
        customerDetails.put("FirstName", vehicleLoanBasic.getApplicantName());
        customerDetails.put("LastName", "");
        customerDetails.put("DateOfBirth", CommonUtils.ConvertDate(vehicleLoanBasic.getApplicantDob(),"yyyy-MM-dd").replace("-",""));
        customerDetails.put("CustomerType", data.getSibCustomer().equalsIgnoreCase("Y")?"Existing to Bank":"New to Bank");
        customerDetails.put("CustomerId", data.getSibCustomer().equalsIgnoreCase("Y")?data.getCifId():"");




        // Add empty bureau_data array
        ArrayNode bureauData = scoreEngineApiRequestNode.putArray("bureau_data");
        // Add details to customer_details array
        JsonNode bureauDetails = objectMapper.readTree(experianResponse.getExperiaCCIRJsonReport());
        bureauData.add(bureauDetails);

        request.setRequest(rootNode.get("request"));
        return request;
    }

    private DKScoreResponse.DKScoreItem processDKScoreForApplicant(VehicleLoanApplicant applicant, String wiNum, String slno, String applicantState) throws Exception {
        log.info("Processing DK Score for applicant: {}", applicant.getApplicantId());
        DKScoreResponse.DKScoreItem dkScoreItem = new DKScoreResponse.DKScoreItem();

        ExperianRequest experianRequest =experianCreditService.createExperianRequest(applicant,new CommonDTO.ExperianForm());// createExperianRequest(applicant, wiNum, slno);
        ExperianResponse experianResponse = experianCreditService.fetchExperianReport(experianRequest);

        if ("SUCCESS".equalsIgnoreCase(experianResponse.getStatus())) {
            ExperianRequest dkRequest = createDkRequest(applicant, experianResponse);
            com.sib.ibanklosucl.dto.experian.DKResponse dkResponse = experianCreditService.fetchDKReport(dkRequest);

            if ("SUCCESS".equalsIgnoreCase(dkResponse.getStatus())) {

                dkScoreItem = mapToDKScoreItem(applicant, experianResponse, dkResponse, applicantState);
                updateVLCredit(applicant, experianResponse, dkResponse, dkScoreItem.getColor());
                if(!"G".equals(applicant.getApplicantType())) {
                    addRaceDeviation(wiNum, Long.valueOf(slno), dkScoreItem.getColor(), dkScoreItem.getScoreRange(), dkScoreItem.getRaceScore(), applicant);
                }
            } else {
                dkScoreItem = createErrorDKScoreItem(applicant, "DK Score fetch failed: " + dkResponse.getErrorreason());
            }
        } else {
            dkScoreItem = createErrorDKScoreItem(applicant, "Experian report fetch failed: " + experianResponse.getErrorReason());
        }

        return dkScoreItem;
    }

//    private ExperianRequest createExperianRequest(VehicleLoanApplicant applicant, String wiNum, String slno) {
//        ExperianRequest request = new ExperianRequest();
//        request.setMock(false);
//        request.setApiName("fetchDK");
//        request.setUserId(usd.getPPCNo());
//        request.setSlno(slno);
//        request.setWorkItemNumber(wiNum);
//        request.setAppid(String.valueOf(applicant.getApplicantId()));
//        // Set other necessary fields...
//        return request;
//    }
        private void updateAllVLCredits(List<VehicleLoanApplicant> applicants, String finalColor) {
        for (VehicleLoanApplicant applicant : applicants) {
                VLCredit vlCredit = vlCreditService.findByApplicantIdAndDelFlg(applicant.getApplicantId());
                if (vlCredit != null && vlCredit.getDkColor().equals("red")) {
                    vlCredit.setDkColor(finalColor);
                    vlCredit.setLastmodDate(new Date());
                    vlCredit.setLastmodUser(usd.getPPCNo());
                    vlCreditService.save(vlCredit);
                    log.info("Updated VLCredit for applicant: {} with final color: {}", applicant.getApplicantId(), finalColor);
                } else {
                    log.warn("VLCredit not found for applicant: {}", applicant.getApplicantId());
                }
        }
    }


    private void updateVLCredit(VehicleLoanApplicant applicant, ExperianResponse experianResponse, com.sib.ibanklosucl.dto.experian.DKResponse dkResponse, String dkColor) {
        VehicleEmpProgram vEp = fetchRepository.getEmpProgramForApplicant(applicant.getApplicantId());
        Long Score = (long) experianResponse.getScore();
       boolean isRaceOkay = !"red".equalsIgnoreCase(dkColor);
       try {
           if (!isRaceOkay) {
               VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
               vehicleLoanBlock.setWiNum(applicant.getWiNum());
               vehicleLoanBlock.setSlno(applicant.getSlno());
               vehicleLoanBlock.setApplicantId(applicant.getApplicantId().toString());
               vehicleLoanBlock.setBlockType(VLBlockCodes.RACE_RANGE_FAILED);
               vehicleLoanBlock.setActualValue(dkResponse.getRaceScore());

               VehicleLoanApplicant vehicleLoanMainApplicant = vehicleLoanApplicantRepository.findBySlnoAndDelFlgAndApplicantType(applicant.getSlno(), "N", "A");
               String program_code = fetchRepository.getWIProgram(applicant.getSlno());
               String tableName = "VLBUREAURACEBREMAP@mybank";
               String model_type = "NONKERALA";
               model_type = "KL".equals(vehicleLoanMainApplicant.getBasicapplicants().getState()) ? "KERALA" : "NONKERALA";
               String dkNtcFlg = dkResponse.getNtc();
               if ("Y".equals(dkNtcFlg)) {
                   tableName = "VLNTBRACESLABMAS@mybank";
               }
               ScoreInfo scoreInfo = validationRepository.getColorForScoreNew(tableName, Long.valueOf(dkResponse.getRaceScore()), program_code,Score,model_type);
               log.info("Run Race status : colour  is {}  Range is {}", scoreInfo.getColor(), scoreInfo.getScoreRange());
               vehicleLoanBlock.setExpectedValue(scoreInfo.getScoreRange());
               vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
           }
       }catch(Exception e){
           //flow should not break if try block fails
           e.printStackTrace();
       }
        log.info("Credit score eligiblity failed: {} {} {}", applicant.getApplicantId(), Score, isRaceOkay);
        // if () {
        VLCredit vlCredit = vlCreditService.findByApplicantIdAndDelFlg(applicant.getApplicantId());
        if (vlCredit == null) {
            vlCredit = new VLCredit();
            vlCredit.setReqIpAddr(usd.getRemoteIP());
            vlCredit.setSlno(applicant.getSlno());
            vlCredit.setWiNum(applicant.getWiNum());
            vlCredit.setApplicantId(applicant.getApplicantId());
            vlCredit.setDelFlg("N");
        }

        vlCredit.setDkFlag(true);
        vlCredit.setDkScore(Long.valueOf(dkResponse.getRaceScore()));
        vlCredit.setBureauScore(Long.valueOf(experianResponse.getScore()));
        vlCredit.setExperianFlag(true);
        vlCredit.setLastmodDate(new Date());
        vlCredit.setLastmodUser(usd.getPPCNo());
        vlCredit.setHomeSol(usd.getSolid());
        vlCredit.setDkColor(dkColor);
        vlCredit.setNtcFlag(dkResponse.getNtc());


        vlCreditService.save(vlCredit);
        log.info("Updated VLCredit for applicant: {}", applicant.getApplicantId());
//         } else {
//             log.info("Credit score eligiblity failed: {} {}", applicant.getApplicantId(),Score);
//         }
    }

    private DKScoreResponse.DKScoreItem mapToDKScoreItem(VehicleLoanApplicant applicant, ExperianResponse experianResponse, com.sib.ibanklosucl.dto.experian.DKResponse dkResponse, String applicantState) {
        String program_code = fetchRepository.getWIProgram(applicant.getSlno());


        //String tableName = "KL".equals(applicantState) ? "VLKERALARACESLABMAS@mybank" : "VLNONKERALARACESLABMAS@mybank";
        Long Score = (long) experianResponse.getScore();
        String tableName = "VLBUREAURACEBREMAP@mybank";
        String model_type = "NONKERALA";
        model_type = "KL".equals(applicantState) ? "KERALA" : "NONKERALA";
        String dkNtcFlg = dkResponse.getNtc();
        if("Y".equals(dkNtcFlg)) {
            tableName="VLNTBRACESLABMAS@mybank";
        }
        ScoreInfo scoreInfo = validationRepository.getColorForScoreNew(tableName, Long.valueOf(dkResponse.getRaceScore()), program_code,Score,model_type);
        log.info("Run Race status : colour  is {}  Range is {}", scoreInfo.getColor(), scoreInfo.getScoreRange());
        DKScoreResponse.DKScoreItem item = new DKScoreResponse.DKScoreItem();
        item.setApplicantType(getApplicantType(applicant.getApplicantType()));
        item.setApplicantName(applicant.getApplName());
        item.setRunDate(new Date()); // Or use a specific date from the response if available
        item.setStatus("SUCCESS");
        item.setScore(String.valueOf(experianResponse.getScore()));
        item.setRaceScore(dkResponse.getRaceScore());
        item.setColor(scoreInfo.getColor());
        item.setScoreRange(scoreInfo.getScoreRange());
        return item;
    }

    private DKScoreResponse.DKScoreItem createErrorDKScoreItem(VehicleLoanApplicant applicant, String errorReason) {
        DKScoreResponse.DKScoreItem item = new DKScoreResponse.DKScoreItem();
        item.setApplicantType(getApplicantType(applicant.getApplicantType()));
        item.setApplicantName(applicant.getApplName());
        item.setRunDate(new Date());
        item.setStatus("ERROR");
        item.setErrorReason(errorReason);
        return item;
    }

    private String getApplicantType(String type) {
        switch (type) {
            case "A":
                return "Applicant";
            case "C":
                return "Co-Applicant";
            case "G":
                return "Guarantor";
            default:
                return "Unknown";
        }
    }

    //    public List<Map<String, Object>> fetchDKScoreDataForJSP(Long slno) {
//        List<VehicleLoanApplicant> applicants = vehicleLoanApplicantService.findBySlnoAndDelFlg(slno);
//        return applicants.stream()
//            .map(applicant -> {
//                VLCredit vlCredit = vlCreditService.findByApplicantIdAndDelFlg(applicant.getApplicantId());
//                Map<String, Object> data = Map.of(
//                    "applicantId", applicant.getApplicantId(),
//                    "applicantType", getApplicantType(applicant.getApplicantType()),
//                    "applicantName", applicant.getApplName(),
//                    "runDate", vlCredit != null ? vlCredit.getLastmodDate() : null,
//                    "bureauScore", vlCredit != null ? vlCredit.getBureauScore() : null,
//                    "raceScore", vlCredit != null ? vlCredit.getDkScore() : null,
//                    "status", vlCredit != null && vlCredit.getDkScore()!=null ? "Success" : "Not Performed"
//                );
//                return data;
//            })
//            .collect(Collectors.toList());
//    }
    public List<Map<String, Object>> fetchDKScoreDataForJSP(Long slno) {
        List<VehicleLoanApplicant> applicants = vehicleLoanApplicantService.findBySlnoAndDelFlg(slno);
        return applicants.stream()
                .map(applicant -> {
                    VLCredit vlCredit = vlCreditService.findByApplicantIdAndDelFlg(applicant.getApplicantId());
                    Map<String, Object> data = new HashMap<>();
                    data.put("applicantId", applicant.getApplicantId());
                    data.put("applicantType", getApplicantType(applicant.getApplicantType()));
                    data.put("applicantName", applicant.getApplName());
                    data.put("runDate", vlCredit != null ? vlCredit.getLastmodDate() : null);
                    data.put("bureauScore", vlCredit != null ? vlCredit.getBureauScore() : null);
                    data.put("raceScore", vlCredit != null ? vlCredit.getDkScore() : null);
                    data.put("status", vlCredit != null && vlCredit.getDkScore() != null ? "Success" : "Not Performed");
                    data.put("color", vlCredit != null && vlCredit.getDkColor() != null ? vlCredit.getDkColor() : "");
                    return data;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> fetchDKScoreDataConsideringQueueDate(Long slno) {
        log.info("Fetching DK Score data considering queue date for slno: {}", slno);

        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(slno);

        Date queueDate = vehicleLoanMaster.getQueueDate();
        if (queueDate == null) {
            log.warn("Queue date is null for slno: {}. Returning empty list.", slno);
            return Collections.emptyList();
        }

        List<VehicleLoanApplicant> applicants = vehicleLoanApplicantService.findBySlnoAndDelFlg(slno);
        return applicants.stream()
                .map(applicant -> {
                    VLCredit vlCredit = vlCreditService.findByApplicantIdAndDelFlg(applicant.getApplicantId());
                    if (vlCredit != null && vlCredit.getLastmodDate() != null && vlCredit.getLastmodDate().after(queueDate)) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("applicantId", applicant.getApplicantId());
                        data.put("applicantType", getApplicantType(applicant.getApplicantType()));
                        data.put("applicantName", applicant.getApplName());
                        data.put("runDate", vlCredit != null ? vlCredit.getLastmodDate() : null);
                        data.put("bureauScore", vlCredit != null ? vlCredit.getBureauScore() : null);
                        data.put("raceScore", vlCredit != null ? vlCredit.getDkScore() : null);
                        data.put("status", vlCredit != null && vlCredit.getDkScore() != null ? "Success" : "Not Performed");
                        data.put("color", vlCredit != null && vlCredit.getDkColor() != null ? vlCredit.getDkColor() : "");
                        return data;
                    } else {
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void addRaceDeviation(String wiNum, Long slno, String color, String range, String value, VehicleLoanApplicant applicant) {

        log.info("Attempting to add RACE deviation for WI_NUM: {}", wiNum);
        String amberDesc = "Race Score is " + color;
        String ambCode = "AMB006";
        AmberDeviationUpdateRequest request = new AmberDeviationUpdateRequest();
        request.setWiNum(wiNum);
        request.setSlno(slno.toString());

        AmberData amberData = new AmberData();
        amberData.setApplicantId(String.valueOf(applicant.getApplicantId()));
        amberData.setApplicantType(applicant.getApplicantType());
        amberData.setApplicantName(applicant.getApplName());
        amberData.setAmberDesc(amberDesc);
        amberData.setColor(color);
        amberData.setAmberCode(ambCode);
        amberData.setParameterRange(range);
        amberData.setParameterValue(value);
        amberData.setDoRemarks("RACE Score deviation");
        try {
            vehicleLoanAmberService.updateRaceScoreDeviation(wiNum, slno, amberData);
            log.info("RACE deviation added successfully for WI_NUM: {}", wiNum);
        } catch (Exception e) {
            log.error("Error adding RACE deviation for WI_NUM: {}", wiNum, e);
            throw new RuntimeException("Failed to add RACE deviation", e);
        }
    }
    private String determineMultiApplicantColor(boolean hasRedScore, boolean hasGreenOrAmberScore, int applicantCount) {
        if (applicantCount > 1 && hasRedScore && hasGreenOrAmberScore) {
            return "amber";
        } else if (hasRedScore) {
            return "red";
        } else {
            return "amber";
        }
    }



}