package com.sib.ibanklosucl.service.doc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;
import com.sib.ibanklosucl.dto.doc.AgreementDetailsDTO;
import com.sib.ibanklosucl.dto.doc.LegalityDto;
import com.sib.ibanklosucl.dto.doc.LegalityFetchDTO;
import com.sib.ibanklosucl.dto.doc.LegalityResponse;
import com.sib.ibanklosucl.dto.losintegrator.LosRequest;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.EligibilityDetails;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.doc.LegalityInvitees;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.doc.LegalRepositry;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.integration.Docservice;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class LegalityService {

        int max=38;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FetchRepository fetchRepository;
//        @Value("${app.mailSmsSend:false}")
//        private boolean mailSmsSend;

        @Value("${app.dev-mode:true}")
        private boolean devMode;
        @Autowired
        private EligibilityDetailsRepository eligibilityDetailsRepository;
        @Value("${api.integrator}")
        private String integratorEndpoint;

        @Autowired
        private ObjectMapper objectMapper;
        @Autowired
        private CommonUtils cm;
        @Autowired
        private LegalRepositry legalRepositry;
        @Autowired
        private Docservice docservice;
        @Autowired
        private VehicleLoanMasterService masterService;
        @Autowired
        private BpmService bpmService;
        @Autowired
        private UserSessionData usd;


        public boolean isExpired(Long slno){
                return legalRepositry.isExpired(slno)>0;
        }
        public String getCompletedDate(Long slno){
                return legalRepositry.getCompletedDate(slno);
        }
        public LegalityInvitees findByUrl(Long slno,String url){
                return legalRepositry.findBySlnoAndSignUrl(slno,url);
        }

        @Transactional
        public void saveInvitees(LegalityResponse apiResponse,Long slNo,String wiNum) {
                Optional<List<LegalityInvitees>> del=legalRepositry.findBySlno(slNo);
                if (del.isPresent()) {
                        legalRepositry.deleteAll(del.get());
                }
                if( apiResponse.getData().getInvitations()!=null) {
                        List<LegalityResponse.invitations> invitees = apiResponse.getData().getInvitations().stream()
                                .filter(invitee -> invitee.getName() != null)
                                .toList();
                        for (LegalityResponse.invitations inv : invitees) {
                                LegalityInvitees legalityInvitees = new LegalityInvitees();
                                legalityInvitees.setSlno(slNo);
                                legalityInvitees.setWiNum(wiNum);
                                legalityInvitees.setDocumentId(apiResponse.getData().getDocumentId());
                                legalityInvitees.setIrn(apiResponse.getData().getIrn());
                                if (apiResponse.getMessages().size() > 0) {
                                        legalityInvitees.setMessage(apiResponse.getMessages().get(0).getMessage());
                                        legalityInvitees.setCode(apiResponse.getMessages().get(0).getMessage());
                                } else {
                                        legalityInvitees.setMessage(null);
                                        legalityInvitees.setCode(null);
                                }

                                legalityInvitees.setActive(inv.isActive());
                                legalityInvitees.setEmail(inv.getEmail());
                                legalityInvitees.setPhone(inv.getPhone());
                                legalityInvitees.setName(inv.getName());
                                legalityInvitees.setSignUrl(inv.getSignUrl());
                                legalityInvitees.setExpiryDate(cm.getDate(inv.getExpiryDate()));

                                legalityInvitees.setExpired(inv.isExpired());
                                legalityInvitees.setSigned(inv.isSigned());
                                legalityInvitees.setRejected(inv.isRejected());
                                legalityInvitees.setSignDate(cm.getDate(inv.getSignDate()));
                                legalityInvitees.setCreationDate(cm.getDate(inv.getCreationDate()));

                                legalityInvitees.setCompletionDate(cm.getDate(apiResponse.getData().getCompletionDate()));
                                legalityInvitees.setStatus(apiResponse.getData().getStatus());
                                legalityInvitees.setLastModUser(usd.getPPCNo());
                                legalityInvitees.setLastModDate(new Date());
                                legalRepositry.save(legalityInvitees);
                        }
                }
                if( apiResponse.getData().getInvitees()!=null) {
                        List<LegalityResponse.Invitee> Invite = apiResponse.getData().getInvitees().stream()
                                .filter(invitee -> invitee.getName() != null)
                                .toList();
                        for (LegalityResponse.Invitee inv : Invite) {
                                LegalityInvitees legalityInvitees = new LegalityInvitees();
                                legalityInvitees.setSlno(slNo);
                                legalityInvitees.setWiNum(wiNum);
                                legalityInvitees.setDocumentId(apiResponse.getData().getDocumentId());
                                legalityInvitees.setIrn(apiResponse.getData().getIrn());
                                if (apiResponse.getMessages().size() > 0) {
                                        legalityInvitees.setMessage(apiResponse.getMessages().get(0).getMessage());
                                        legalityInvitees.setCode(apiResponse.getMessages().get(0).getMessage());
                                } else {
                                        legalityInvitees.setMessage(null);
                                        legalityInvitees.setCode(null);
                                }
                                legalityInvitees.setActive(inv.isActive());
                                legalityInvitees.setEmail(inv.getEmail());
                                legalityInvitees.setPhone(inv.getPhone());
                                legalityInvitees.setName(inv.getName());
                                legalityInvitees.setSignUrl(inv.getSignUrl());
                                legalityInvitees.setExpiryDate(cm.getDateIns(inv.getExpiryDate()));
                                legalityInvitees.setStatus("SENT");
                                legalityInvitees.setLastModUser(usd.getPPCNo());
                                legalityInvitees.setLastModDate(new Date());
                                legalRepositry.save(legalityInvitees);
                        }
                }
        }


        public ResponseDTO callStatusApi(Long slNo,String wiNum,String docID) {
                try {
                        LosRequest.LegalityStatusRequest request=new LosRequest.LegalityStatusRequest();
                        request.setSlno(String.valueOf(slNo));
                        request.setWorkItemNumber(wiNum);
                        request.setDocumentId(docID);
                        HttpHeaders headers = new HttpHeaders();
                        headers.set("Content-Type", "application/json");
                        // Create the request entity
                        HttpEntity< LosRequest.LegalityStatusRequest> entity = new HttpEntity<>(request,headers);
                            ResponseEntity<String> response = restTemplate.exchange(
                                    integratorEndpoint,
                                    HttpMethod.POST,
                                    entity,
                                    String.class
                            );
                            if (response.getStatusCode() == HttpStatus.OK) {
                                    LegalityResponse legalityResponse=objectMapper.readValue(response.getBody(), LegalityResponse.class);
                                    if(legalityResponse.getStatus()==1){
                                            List<LegalityResponse.invitations> invitees = legalityResponse.getData().getInvitations().stream()
                                                    .filter(invitee -> invitee.getName() != null)
                                                    .toList();
                                            saveInvitees(legalityResponse,slNo,wiNum);
                                            JsonObject msg=new JsonObject();
                                            msg.addProperty("inv",objectMapper.writeValueAsString(invitees));
                                            msg.addProperty("file",legalityResponse.getData().getFile());
                                            msg.addProperty("completed",false);
                                            msg.addProperty("legalDocID",legalityResponse.getData().getDocumentId());
                                            if(legalityResponse.isCompleted()){
                                                    msg.addProperty("completed",true);
                                                    TabResponse tb=bpmService.BpmUpload(bpmRequest(wiNum,legalityResponse.getData().getFile(),"N","NA","LEEGALITY_SANCTION_LETTER"));
                                                    if(!"S".equalsIgnoreCase(tb.getStatus())){
                                                            throw new ValidationException(ValidationError.COM001,tb.getMsg());
                                                    }
                                                    VehicleLoanMaster master=masterService.findById(slNo);
                                                    master.setDocQueueOverallStatus("COMPLETED");
                                                    master.setDocUploadDate(new Date());
                                                    master.setDocUploadUser(usd.getPPCNo());
                                                    masterService.saveLoan(master);
                                            }
                                            return new ResponseDTO("S",msg.toString());
                                    }
                                    else{
                                            throw new ValidationException(ValidationError.COM001, "Leegality Status Check Failed");
                                    }

                            }
                            else {
                            throw new ValidationException(ValidationError.COM001, response.getBody());
                            }
                } catch (HttpClientErrorException e) {
                        throw new ValidationException(ValidationError.COM001, e.getResponseBodyAsString());
                }catch (Exception e) {
                        throw new ValidationException(ValidationError.COM001, e.getMessage());
                }
        }
        public BpmRequest bpmRequest(String winum, String pdf, String child, String childName, String docName){
                BPMFileUpload bpmFileUpload = new BPMFileUpload();
                bpmFileUpload.setWI_NAME(winum);
                bpmFileUpload.setCHILD(child);
                bpmFileUpload.setCHILD_FOLDER(childName);
                bpmFileUpload.setSystemIP(usd.getRemoteIP());
                List<DOC_ARRAY> docArrayList = new ArrayList<>();
                DOC_ARRAY docArray = new DOC_ARRAY();
                docArray.setDOC_NAME(docName+CommonUtils.getCurrentTimestamp());
                docArray.setDOC_EXT("pdf");
                docArray.setDOC_BASE64(pdf);
                docArrayList.add(docArray);
                bpmFileUpload.setDOC_ARRAY(docArrayList);
                BpmRequest bpmRequest = new BpmRequest();
                bpmRequest.setRequest(bpmFileUpload);
                return bpmRequest;
        }

        public List<LegalityInvitees> findAllBySlno(Long slNo){
                Optional<List<LegalityInvitees>> li=legalRepositry.findBySlno(slNo);
                return li.orElse(null);
        }


    public ResponseDTO callApi(LegalityDto requestDTO,String wiNum,Long slNo) {
            try {
                    System.out.println(objectMapper.writeValueAsString(requestDTO));

                    LosRequest.LegalityRequest lrs=new LosRequest.LegalityRequest();
                    lrs.setRequest(requestDTO);
                    lrs.setWorkItemNumber(wiNum);
                    lrs.setOrigin("LOS");
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Content-Type", "application/json");
                    // Create the request entity
                    HttpEntity< LosRequest.LegalityRequest> entity = new HttpEntity<>(lrs, headers);
                    ResponseEntity<String> response = restTemplate.exchange(
                            integratorEndpoint,
                            HttpMethod.POST,
                            entity,
                            String.class
                    );
                    if (response.getStatusCode() == HttpStatus.OK) {
//                            System.out.println(objectMapper.writeValueAsString(response.getBody()));

//                          String responsssse="{\"data\":{\"documentId\":\"Ooa7weI\",\"irn\":\"VLR_000000398\",\"invitees\":[{\"name\":\"TOM D CHERIYA\",\"email\":\"tomdcheriya@gmail.com\",\"phone\":\"8547016003\",\"signUrl\":\"https://sandbox.leegality.com/nesl/sign/1d5c3073-1de8-4127-962e-2e8d05f1b4ec\",\"active\":true,\"expiryDate\":\"2024-09-01T18:29:59Z\"},{\"name\":null,\"email\":null,\"phone\":null,\"signUrl\":null,\"active\":false,\"expiryDate\":null},{\"name\":null,\"email\":null,\"phone\":null,\"signUrl\":null,\"active\":false,\"expiryDate\":null},{\"name\":null,\"email\":null,\"phone\":null,\"signUrl\":null,\"active\":false,\"expiryDate\":null},{\"name\":null,\"email\":null,\"phone\":null,\"signUrl\":null,\"active\":false,\"expiryDate\":null},{\"name\":\"ARUN THOMAS GEORGE\",\"email\":\"aruntgeorge@sib.bank.in\",\"phone\":\"9446761069\",\"signUrl\":\"https://sandbox.leegality.com/sign/247bd00d-6d5c-48b1-9010-c28c5f927627\",\"active\":false,\"expiryDate\":\"2024-09-01T18:29:59Z\"}]},\"messages\":[{\"code\":\"simpleWorkFlow.success\",\"message\":\"Invitations sent successfully.\"}],\"status\":1}";

                            LegalityResponse legalityResponse=objectMapper.readValue(response.getBody(), LegalityResponse.class);
                            if(legalityResponse.getStatus()==1){

                                    List<LegalityResponse.Invitee> invitees = legalityResponse.getData().getInvitees().stream()
                                            .filter(invitee -> invitee.getName() != null)
                                            .toList();
                                    saveInvitees(legalityResponse,slNo,wiNum);
                                    JsonObject msg=new JsonObject();
                                    msg.addProperty("inv",objectMapper.writeValueAsString(invitees));
                                    msg.addProperty("file","");
                                    msg.addProperty("legalDocID",legalityResponse.getData().getDocumentId());
                                    return new ResponseDTO("S",msg.toString());

                                  //  return callStatusApi(slNo,wiNum,legalityResponse.getData().getDocumentId());

                            }
                            else{
                                    throw new ValidationException(ValidationError.COM001, legalityResponse.getMessages().get(0).getMessage());
                            }

                    } else {
                            throw new ValidationException(ValidationError.COM001, response.getBody());
                    }
            } catch (HttpClientErrorException e) {
                    throw new ValidationException(ValidationError.COM001, e.getResponseBodyAsString());
            }catch (Exception e) {
                    throw new ValidationException(ValidationError.COM001, e.getMessage());
            }
    }

        @Data
        public class LegalData{
                private String artCode;
                BigDecimal artAmt;
        }
    private LegalData getAgrAmount(BigDecimal loanAmt,String fixed,String percantage, String value, BigDecimal minValue, BigDecimal maxValue,String artCode) {
                LegalData data=new LegalData();
//            2024-12-23 11:36:52.611 [http-nio-8081-exec-257] INFO  c.s.i.service.doc.LegalityService - loanAmt 600000,fixed N,percantage Y ,value 500,minValue 500,maxValue 1500000,artCode 1151,data LegalityService.LegalData(artCode=1151, artAmt=1500000)


            log.info("loanAmt {},fixed {},percantage {} ,value {},minValue {},maxValue {},artCode {}", loanAmt, fixed, percantage,  value,  minValue,  maxValue, artCode);
                if(artCode.contains("#")){
                        String finalArtCode = artCode;
                        artCode= Arrays.stream(artCode.split("#"))
                                .map(pair -> pair.split("\\|"))
                                .filter(codeAmount -> codeAmount.length == 2)
                                .filter(codeAmount -> new BigDecimal(codeAmount[1]).compareTo(loanAmt) >= 0)
                                .map(codeAmount -> codeAmount[0])
                                .findFirst()
                                .orElseThrow(()->new RuntimeException("No matching Article Code found for "+ finalArtCode +" : "+loanAmt));
                }
                if(value.contains("#")){
                        String finalValue = value;
                        value= Arrays.stream(value.split("#"))
                                .map(pair -> pair.split("\\|"))
                                .filter(codeAmount -> codeAmount.length == 2)
                                .filter(codeAmount -> new BigDecimal(codeAmount[1]).compareTo(loanAmt) >= 0)
                                .map(codeAmount -> codeAmount[0])
                                .findFirst()
                                .orElseThrow(()->new RuntimeException("No matching Value found for "+ finalValue +" : "+loanAmt));
                }
                 if("Y".equalsIgnoreCase(fixed)){
                        data.setArtCode(artCode);
                        data.setArtAmt(new BigDecimal(value));
                        return data;
                }
                else if(percantage.equalsIgnoreCase("Y")){
                        BigDecimal perAmt=loanAmt.multiply(new BigDecimal(value)).divide(new BigDecimal(100),0, RoundingMode.CEILING);
                        data.setArtCode(artCode);
                        if(perAmt.compareTo(minValue)<0){
                                data.setArtAmt(minValue);
                        }
                        else    if(perAmt.compareTo(maxValue)>0){
                                data.setArtAmt(maxValue);
                        }
                        else {
                                data.setArtAmt(perAmt);
                        }
                         log.info("loanAmt {},fixed {},percantage {} ,value {},minValue {},maxValue {},artCode {},data {}", loanAmt, fixed, percantage,  value,  minValue,  maxValue, artCode,data);
                        return data;

                }
                else {
                        throw new RuntimeException("Invalid Value");
                }

    }

    public ResponseDTO formData(DocumentRequest docRequest, Long slNo,VehicleLoanMaster master) throws Exception{



            //slNo=400L;
            List<LegalityFetchDTO> data=fetchRepository.findAllLegalityData(slNo);
            String appStateCode=data.stream().filter(t-> "A".equalsIgnoreCase(t.getApplicantType())).findFirst().get().getPerState();
            String appStateDesc=data.stream().filter(t-> "A".equalsIgnoreCase(t.getApplicantType())).findFirst().get().getPerStateDesc();
            //for handling the edge case of delhi
            if("NCT OF DELHI".equals(appStateDesc)) {
                    appStateDesc="DELHI";
            }

            //Stamp Charges Calculation
            AgreementDetailsDTO agr=fetchRepository.findAgreementDetailsByStateCode(appStateCode);
            LegalData agreementValue, hypothecationValue,guaranteeValue, arbitrationValue;
            //String arbitrationCode=agr.getArbitrationCode(),hypothecationCode=agr.getHypothecationCode(),agreementCode=agr.getAgreementCode(),guaranteeCode=agr.getGuaranteeCode();
            EligibilityDetails eligDt = eligibilityDetailsRepository.findBySlnoAndDelFlg(slNo, "N").get();
            BigDecimal loanAmt=eligDt.getSancAmountRecommended();
            agreementValue=getAgrAmount(loanAmt,agr.getAgreementFixed(),agr.getAgreementPercentage(),agr.getAgreementValue(),agr.getAgreementMinValue(),agr.getAgreementMaxValue(),agr.getAgreementCode());
            hypothecationValue=getAgrAmount(loanAmt,agr.getHypothecationFixed(),agr.getHypothecationPercentage(),agr.getHypothecationValue(),agr.getHypothecationMinValue(),agr.getHypothecationMaxValue(),agr.getHypothecationCode());
            guaranteeValue=getAgrAmount(loanAmt,agr.getGuaranteeFixed(),agr.getGuaranteePercentage(),agr.getGuaranteeValue(),agr.getGuaranteeMinValue(),agr.getGuaranteeMaxValue(),agr.getGuaranteeCode());
            arbitrationValue=getAgrAmount(loanAmt,agr.getArbitrationFixed(),agr.getArbitrationPercentage(),agr.getArbitrationValue(),agr.getArbitrationMinValue(),agr.getArbitrationMaxValue(),agr.getArbitrationCode());

            log.info("agreementValue {}",agreementValue);
            log.info("hypothecationValue {}",hypothecationValue);
            log.info("guaranteeValue {}",guaranteeValue);
            log.info("arbitrationValue {}",arbitrationValue);
            BigDecimal stampAmt= agreementValue.getArtAmt().add(hypothecationValue.getArtAmt()).add(arbitrationValue.getArtAmt()) ;
            boolean isGuarantor=data.stream().anyMatch(t->t.getApplicantType().equalsIgnoreCase("G"));
            if(isGuarantor){
                    stampAmt=stampAmt.add(guaranteeValue.getArtAmt());
            }
            log.info("stampAmt {}",stampAmt);
            DocumentRequest.MiscData miscData=new   DocumentRequest.MiscData();
            miscData.setDocuCode("D");
            miscData.setStampDuty(stampAmt.toString());
            docRequest.setMiscData(miscData);
            JsonNode doc=docservice.getLegalDoc(docRequest);
            String pdf=doc.path("pdf").asText();
            String profileid=doc.path("profileId").asText();
            String template=doc.path("template").asText();

            LegalityDto requestDTO = new LegalityDto();

            Map<String, String> brDt= fetchRepository.getBranchDetails(slNo);
            // Populate the requestDTO with data
            requestDTO.setProfileId(profileid);

            LegalityDto.FileDTO file = new LegalityDto.FileDTO();
            file.setName(master.getWiNum()+"_"+template+".pdf");
            file.setFile(pdf);
            requestDTO.setFile(file);

            List<LegalityDto.InviteeDTO> invitees = new ArrayList<>();
            LegalityDto.InviteeDTO guarantorInvitee=null;
            String guarantorName="",perStateDesc="";
            LegalityDto.ParticipantDTO guarantorPart=null;
            List<LegalityDto.ParticipantDTO> participants  = new ArrayList<>();

            String appName="",firstPartyPin="",firstPartyOVDType="",firstPartyOVDValue="";
            int count=0,totalCount=5;
            for (LegalityFetchDTO model : data) {
                    System.out.println("______________________"+count);
                    // Populate invitees
                    LegalityDto.InviteeDTO  invitee = new LegalityDto.InviteeDTO();
                    if(!devMode){
                         invitee.setName(getShortName(model.getApplicantName()));
                         invitee.setEmail(model.getEmailId());
                         invitee.setPhone(isIndianMobile(model.getMobileCountryCode())?model.getMobileNo():"");
                    }
                    else{

//                    invitee.setName("ARUN THOMAS GEORGE");
//                    invitee.setEmail("arunitgc1@gmail.com");
//                    invitee.setPhone("9446761069");
                    invitee.setName("ANTONY RAJ G");
                    invitee.setEmail("antonygeorge200@gmail.com");
                    invitee.setPhone("8098153030");
//                            invitee.setName("SUMESH KUMAR PRABHU");
//                            invitee.setEmail("sumesh.prabhus@gmail.com");
//                            invitee.setPhone("8075436361");
//                    invitee.setName("SREELAKSHMI S");
//                    invitee.setEmail("sreelakshmisuresh@sib.bank.in");
//                    invitee.setPhone("9447546163");
                    }



                        // Set participants
                    String relation=switch (model.getApplicantType()){
                            case "A":
                                    appName=getShortName(model.getApplicantName());
                                    firstPartyPin= model.getPin();
                                    firstPartyOVDValue= model.getPanNo();
                                    firstPartyOVDType= "PAN_CARD";
                                    yield "DEBTOR";
                            case "G":
                                    yield "GUARANTOR";
                            case "C":
                                    yield "CO_OBLIGANT";
                            default:
                                    throw new RuntimeException("Tnvalid Type");
                    };

                    LegalityDto.ParticipantDTO participant = new LegalityDto.ParticipantDTO();
                    participant.setContactPersonName(getShortName(model.getApplicantName()));
                    participant.setContactRelation(relation);
                    if(!devMode){
                        participant.setFullName(getShortName(model.getApplicantName()));
                        participant.setEmailId(model.getEmailId());
                        participant.setMobileNumber(isIndianMobile(model.getMobileCountryCode())?model.getMobileNo():"");
                    }
                    else {
                            //                    participant.setFullName("ARUN THOMAS GEORGE");
//                    participant.setEmailId("arunitgc1@gmail.com");
//                    participant.setMobileNumber("9446761069");
                            participant.setFullName("ANTONY RAJ G");
                            participant.setEmailId("antonygeorge200@gmail.com");
                            participant.setMobileNumber("8098153030");
//                            participant.setFullName("SUMESH KUMAR PRABHU");
//                            participant.setEmailId("sumesh.prabhus@gmail.com");
//                            participant.setMobileNumber("8075436361");
//                    participant.setFullName("TOM D CHERIYA");
//                    participant.setEmailId("tomdcheriya@gmail.com");
//                    participant.setMobileNumber("8547016003");
                    }
                    participant.setDob(model.getApplicantDob());
                    participant.setLegalConstitution(model.getResidentFlag().equals("N")?"NON_RESIDENT":"RESIDENT_INDIVIDUAL");
                    participant.setPartyType(model.getResidentFlag().equals("N")?"NRI":"RESIDENT_INDIVIDUAL");
                    participant.setOfficialDocType("PAN_CARD");
                    participant.setOfficialDocId(model.getPanNo());
                    if ("IN".equals(model.getPerCountry()) && model.getResidentFlag().equals("N")) {
                            participant.setRegisteredAddress(cleanAddress(model.getAddr1()));
                            participant.setRegisteredPinCode(model.getPin());
                            participant.setCommunicationAddress(cleanAddress(model.getAddr1()));
                            participant.setCommunicationAddressPinCode(model.getPin());
                    } else if ("IN".equals(model.getComCountry()) && model.getResidentFlag().equals("N")) {
                            participant.setRegisteredAddress(cleanAddress(model.getComAddr1()));
                            participant.setRegisteredPinCode(model.getComPin());
                            participant.setCommunicationAddress(cleanAddress(model.getComAddr1()));
                            participant.setCommunicationAddressPinCode(model.getComPin());
                    } else {
                            participant.setRegisteredAddress(cleanAddress(model.getAddr1()));
                            participant.setRegisteredPinCode(model.getPin());
                            participant.setCommunicationAddress(cleanAddress(model.getComAddr1()));
                            participant.setCommunicationAddressPinCode(model.getComPin());
                    }

                    participant.setAlternateEmailId("");
                    participant.setAlternateMobileNumber("");
                    participant.setDesignation("");
                    participant.setCin("");
                    participant.setKin("");
                    participant.setIsIndividual("");
                    participant.setSignatoryGender("");
                    participant.setBusinessUnit("");
                    participant.setBusinessUnit("");
                    if("G".equalsIgnoreCase(model.getApplicantType())){
                            guarantorInvitee=invitee;
                            guarantorPart=participant;
                            guarantorName=getShortName(guarantorInvitee.getName());
                    }
                    else{
                            invitees.add(invitee);
                            participants.add(participant);
                    }
                    count++;
            }

            while (count<totalCount){
                    LegalityDto.InviteeDTO  invitee = new LegalityDto.InviteeDTO();
                    invitee.setName("");
                    invitee.setEmail("");
                    invitee.setPhone("");
                    invitees.add(invitee);
                    LegalityDto.ParticipantDTO participant = new LegalityDto.ParticipantDTO();
                    participant.setFullName("");
                    participant.setContactPersonName("");
                    participant.setContactRelation("");
                    participant.setEmailId("");
                    participant.setMobileNumber("");
                    participant.setDob("");
                    participant.setLegalConstitution("");
                    participant.setPartyType("");
                    participant.setOfficialDocType("");
                    participant.setOfficialDocId("");
                    participant.setRegisteredAddress("");
                    participant.setRegisteredPinCode("");
                    participant.setCommunicationAddress("");
                    participant.setCommunicationAddressPinCode("");
                    participant.setAlternateEmailId("");
                    participant.setAlternateMobileNumber("");
                    participant.setDesignation("");
                    participant.setCin("");
                    participant.setKin("");
                    participant.setIsIndividual("");
                    participant.setSignatoryGender("");
                    participant.setBusinessUnit("");
                    participant.setBusinessUnit("");
                    participants.add(participant);
                    count++ ;
            }
            if(guarantorInvitee!=null){
                    invitees.add(guarantorInvitee);
                    participants.add(guarantorPart);
            }
                //STAFF
            LegalityDto.InviteeDTO  staffinvitee=null;
            if(!devMode){
                        Map<String, String> staffDt= fetchRepository.fetchStaff(slNo);
                            staffinvitee = new LegalityDto.InviteeDTO();
                        staffinvitee.setName(staffDt.get("PPC_NAME"));
                        staffinvitee.setEmail(staffDt.get("EMAILID"));
                        staffinvitee.setPhone(cleanMobile(staffDt.get("MOBNO")));
            }
                else{
                    //        staffinvitee = new LegalityDto.InviteeDTO();
//            staffinvitee.setName("JAYAKRISHNAN U M");
//            staffinvitee.setEmail("jayakrishnan.u@sib.bank.in");
//            staffinvitee.setPhone("9809772576");
                     staffinvitee = new LegalityDto.InviteeDTO();
                    staffinvitee.setName("G ANTONY RAJ");
                    staffinvitee.setEmail("infobanksib@gmail.com");
                    staffinvitee.setPhone("9061281503");
//                    staffinvitee.setName("VIGNESH PADMANABHAN IYER");
//                    staffinvitee.setEmail("Vigneshpadmanabhan@sib.bank.in");
//                    staffinvitee.setPhone("9605964544");
//                     staffinvitee = new LegalityDto.InviteeDTO();
//                    staffinvitee.setName("ANJANA R");
//                    staffinvitee.setEmail("anjanar2323@gmail.com");
//                    staffinvitee.setPhone("9526744481");
//            staffinvitee.setName("TOM D CHERIYA");
//            staffinvitee.setEmail("antonyraj@sib.bank.in");
//            staffinvitee.setPhone("8547016003");
            }


            invitees.add(staffinvitee);
            requestDTO.setInvitees(invitees);

            // Set neslData
            LegalityDto.NeslDataDTO neslData = new LegalityDto.NeslDataDTO();

            // Set documentDetail
            LegalityDto.DocumentDetailDTO documentDetail = new LegalityDto.DocumentDetailDTO();
            documentDetail.setLoanNumber(eligDt.getWiNum().replaceAll("_",""));
            documentDetail.setSanctionNumber(eligDt.getWiNum().replaceAll("_",""));
            documentDetail.setRegistrationType("INDIVIDUAL_LOAN");
            if(!devMode){
                    documentDetail.setState(appStateDesc);
            }
            else {
                    documentDetail.setState(appStateDesc);
                    //documentDetail.setState("TAMIL NADU");
            }
            documentDetail.setBranchName(brDt.get("BR_NAME"));
            documentDetail.setBranchAddress(cleanAddress(brDt.get("BR_ADR")));
            documentDetail.setDateOfSanction(brDt.get("SAN_DATE"));
            documentDetail.setEmiAmount(eligDt.getSancEmi().toString());
            documentDetail.setRateOfInterest(eligDt.getSancCardRate().toString());
            documentDetail.setSanctionAmount(eligDt.getSancAmountRecommended().toString());
            documentDetail.setTenure(String.valueOf(eligDt.getSancTenor()));
            documentDetail.setTypeOfDebt("FINANCIAL");
            documentDetail.setAccountClosedFlag("NO");
            documentDetail.setFundType("FUNDED");
            documentDetail.setSanctionCurrency("INR");
            documentDetail.setCreditSubtype("CREDIT_FACILITY");
            documentDetail.setFacilityName("VEHICLE LOAN");
            documentDetail.setCreditorBusinessUnit("Retail");
            documentDetail.setAmountOverdue("");
            documentDetail.setOtherChargeAmount("");
            documentDetail.setDebtStartDate("");
            documentDetail.setInterestAmount("");
            documentDetail.setOldDebtRefNo("");
            documentDetail.setPrincipalOutstanding("");
            documentDetail.setLoanRemark("");
            documentDetail.setTotalOutstandingAmount("");
            documentDetail.setDrawingPower("");
            documentDetail.setDaysPastDue("");
            documentDetail.setDocRefNo("");
            documentDetail.setEvent("");
            documentDetail.setExpiryDateEbg("");
            documentDetail.setCurrencyOfDebt("");
            documentDetail.setClaimExpiryDate("");
            documentDetail.setContractRefNo("");
            documentDetail.setVendorCode("");
            documentDetail.setPortalID("");


            // Set neslData document detail
            neslData.setDocumentDetail(documentDetail);
            neslData.setNeslSecurities(new ArrayList<>());
            neslData.setNeslParties(new ArrayList<>());

            // Set participants in neslData
            neslData.setParticipants(participants);

            // Set stampData
            LegalityDto.StampDataDTO stampData = new LegalityDto.StampDataDTO();
            if(!devMode){
                    stampData.setFirstParty(appName);
            }
            else {
                    //stampData.setFirstParty("ARUN THOMAS GEORGE");
                      stampData.setFirstParty("ANTONY RAJ G");
                 //   stampData.setFirstParty("SUMESH KUMAR PRABHU");
            }
            stampData.setSecondParty("THE South Indian Bank LIMITED");
            stampData.setStampDutyAmount(agreementValue.getArtAmt().toString());
            stampData.setConsiderationPrice(eligDt.getSancAmountRecommended().setScale(0, RoundingMode.HALF_UP).toString());

//            System.out.println(eligDt.getSancAmountRecommended().setScale(0, RoundingMode.HALF_UP).toString());
//            if(true){
//                    throw new Exception("fdsf");
//            }
            stampData.setDescriptionOfDocument("Loan documents to be stamped Hypothecation agreement Guarantee agreement");
            stampData.setStampDutyPaidBy("THE South Indian Bank LIMITED");
            stampData.setArticleCode(agreementValue.getArtCode());

            stampData.setFirstPartyPin(firstPartyPin);
            stampData.setFirstPartyOVDValue(firstPartyOVDValue);
            stampData.setFirstPartyOVDType(firstPartyOVDType);
            if ("WB".equals(appStateCode)||"MP".equals(appStateCode)) {
                    stampData.setSecondPartyPin(brDt.get("PIN_CODE"));
                    stampData.setSecondPartyOVDType("PAN_CARD");
                    stampData.setSecondPartyOVDValue("AABCT0022F");
            } else {
                    stampData.setSecondPartyPin("");
                    stampData.setSecondPartyOVDType("");
                    stampData.setSecondPartyOVDValue("");
            }


            // Set stampData
            LegalityDto.StampDataDTO stampData2 = new LegalityDto.StampDataDTO();

            if(!devMode){
                    stampData2.setFirstParty(appName);
            }
            else {
                    //stampData.setFirstParty("ARUN THOMAS GEORGE");
                    stampData2.setFirstParty("ANTONY RAJ G");
                    //   stampData.setFirstParty("SUMESH KUMAR PRABHU");
            }
            stampData2.setSecondParty("THE South Indian Bank LIMITED");
            stampData2.setStampDutyAmount(hypothecationValue.getArtAmt().toString());
            stampData2.setConsiderationPrice(eligDt.getSancAmountRecommended().setScale(0, RoundingMode.HALF_UP).toString());
            stampData2.setDescriptionOfDocument("Loan documents to be stamped Hypothecation agreement Guarantee agreement");
            stampData2.setStampDutyPaidBy("THE South Indian Bank LIMITED");
            stampData2.setArticleCode(hypothecationValue.getArtCode());
            stampData2.setFirstPartyPin(firstPartyPin);
            stampData2.setFirstPartyOVDValue(firstPartyOVDValue);
            stampData2.setFirstPartyOVDType(firstPartyOVDType);
            //stampData2.setSecondPartyPin("");
            if ("WB".equals(appStateCode)||"MP".equals(appStateCode)) {
                    stampData2.setSecondPartyPin(brDt.get("PIN_CODE"));
                    stampData2.setSecondPartyOVDType("PAN_CARD");
                    stampData2.setSecondPartyOVDValue("AABCT0022F");
            } else {
                    stampData2.setSecondPartyPin("");
                    stampData2.setSecondPartyOVDType("");
                    stampData2.setSecondPartyOVDValue("");
            }

            LegalityDto.StampDataDTO stampData4 = new LegalityDto.StampDataDTO();

            if(!devMode){
                    stampData4.setFirstParty(appName);
            }
            else {
                    //stampData.setFirstParty("ARUN THOMAS GEORGE");
                    //stampData2.setFirstParty("TOM D CHERIYA");
                    stampData4.setFirstParty("ANTONY RAJ G");
            }
            stampData4.setSecondParty("THE South Indian Bank LIMITED");
            stampData4.setStampDutyAmount(arbitrationValue.getArtAmt().toString());
            stampData4.setConsiderationPrice(eligDt.getSancAmountRecommended().setScale(0, RoundingMode.HALF_UP).toString());
            stampData4.setDescriptionOfDocument("Loan documents to be stamped Hypothecation agreement Guarantee agreement");
            stampData4.setStampDutyPaidBy("THE South Indian Bank LIMITED");
            stampData4.setArticleCode(arbitrationValue.getArtCode());
            stampData4.setFirstPartyPin(firstPartyPin);
            stampData4.setFirstPartyOVDValue(firstPartyOVDValue);
            stampData4.setFirstPartyOVDType(firstPartyOVDType);
            //stampData4.setSecondPartyPin("");
            if ("WB".equals(appStateCode)||"MP".equals(appStateCode)) {
                    stampData4.setSecondPartyPin(brDt.get("PIN_CODE"));
                    stampData4.setSecondPartyOVDType("PAN_CARD");
                    stampData4.setSecondPartyOVDValue("AABCT0022F");
            } else {
                    stampData4.setSecondPartyPin("");
                    stampData4.setSecondPartyOVDType("");
                    stampData4.setSecondPartyOVDValue("");
            }

            // Set stampData
            LegalityDto.StampDataDTO stampData3 = new LegalityDto.StampDataDTO();
            if(!devMode){
                    stampData3.setFirstParty(guarantorName);
            }
            else {
                    //stampData.setFirstParty("ARUN THOMAS GEORGE");
                    stampData3.setFirstParty("ANTONY RAJ G");
                    //stampData.setFirstParty(appName);
            }
            stampData3.setSecondParty("THE South Indian Bank LIMITED");
            stampData3.setStampDutyAmount(guaranteeValue.getArtAmt().toString());
            stampData3.setConsiderationPrice(eligDt.getSancAmountRecommended().setScale(0, RoundingMode.HALF_UP).toString());
            stampData3.setDescriptionOfDocument("Loan documents to be stamped Hypothecation agreement Guarantee agreement");
            stampData3.setStampDutyPaidBy("THE South Indian Bank LIMITED");
            stampData3.setArticleCode(guaranteeValue.getArtCode());
            stampData3.setFirstPartyPin(firstPartyPin);
            stampData3.setFirstPartyOVDValue(firstPartyOVDValue);
            stampData3.setFirstPartyOVDType(firstPartyOVDType);
            //stampData3.setSecondPartyPin("");
            if ("WB".equals(appStateCode)||"MP".equals(appStateCode)) {
                    stampData3.setSecondPartyPin(brDt.get("PIN_CODE"));
                    stampData3.setSecondPartyOVDType("PAN_CARD");
                    stampData3.setSecondPartyOVDValue("AABCT0022F");
            } else {
                    stampData3.setSecondPartyPin("");
                    stampData3.setSecondPartyOVDType("");
                    stampData3.setSecondPartyOVDValue("");
            }



            List<LegalityDto.StampDataDTO> st=new ArrayList<>();
            st.add(stampData);
            st.add(stampData2);
            st.add(stampData4);
            if(guarantorInvitee!=null){
                    st.add(stampData3);
            }
            master.setStampAmt(stampAmt);

            // Set stamp data in neslData
            neslData.setStampData(st);

            // Set neslData to the requestDTO
            requestDTO.setNeslData(neslData);

            // Set irn (assuming it's an empty string as per your provided JSON)
            requestDTO.setIrn(master.getWiNum()+"_"+template);



            // Call the API using the service
            return callApi(requestDTO,eligDt.getWiNum(),slNo);

        }

        private String cleanMobile(String str){
            if(str==null){
                    str="";
            }
            else if(str.startsWith("+91")){
                    str=str.replaceAll("\\+91","");
            }
            if(str.length()!=10){
                    str="";
            }
            return str;
        }
        private String cleanAddress(String str){
            if(str==null){
                    str="";
            }
           else {
                    str = str.replaceAll("[^,a-zA-Z0-9 ]", " ");
                    if (str.length() > 50) {
                            str = str.substring(0, 50);

                    }
                    if (str.contains(",") && (str.lastIndexOf(",") > str.lastIndexOf(" "))) {
                            str = str.substring(0, str.lastIndexOf(",") + 1);
                    } else if (str.contains(" ")) {
                            str = str.substring(0, str.lastIndexOf(" ") + 1);
                    }
            }
                    return str;
        }


        public String getShortName(String str) {
                str= cleanName(str);
                return  str.substring(0, Math.min(str.length(), 39));
        }    public static String cleanName(String name) {
                return name.replaceAll("[^a-zA-Z ]", "");
        }

        private boolean isIndianMobile(String str){
                return "91".equals(str);
        }
}
