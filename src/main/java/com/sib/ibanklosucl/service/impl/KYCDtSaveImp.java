package com.sib.ibanklosucl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.UIDRepository;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.service.VLBlockCodes;
import com.sib.ibanklosucl.service.VLCreditService;
import com.sib.ibanklosucl.service.VehicleLoanBlockService;
import com.sib.ibanklosucl.service.VlSaveService;
import com.sib.ibanklosucl.service.integration.BureauService;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.HtmlTableGenerator;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
    public class KYCDtSaveImp implements VlSaveService {

        @Autowired
        private VehicleLoanApplicantService repository;
        @Autowired
        private CustomerDetailsService customerDetailsService;
        @Autowired private VehicleLoanMasterService mrepo;
    @Autowired
    private VehicleLoanBlockService vehicleLoanBlockService;

    @Value("${app.dev-mode:true}")
    private boolean devMode;
        @Autowired
        private VehicleLoanKycService kycrepository;
        @Autowired
        private FetchRepository fetchRepository;
        @Autowired
        private CommonUtils cm;
        @Autowired
        private VehicleLoanWarnService vehicleLoanWarnService;
        @Autowired
        private UserSessionData usd;
        @Autowired
        private UIDRepository uidRepository;
        @Autowired
        private ValidationRepository validationRepository;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;
    @Autowired
    private HtmlTableGenerator htmlTableGenerator;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private BureauService bureauService;
    @Autowired
    private VLCreditService vlCreditservice;
    @Autowired
    private VehicleLoanMasterService masterService;
    @Override
    public TabResponse fetchData(FormData fs){
        return new TabResponse();
    }
    @Override
    @Transactional(rollbackOn = Exception.class)
    public TabResponse executeSave(FormSave fs) throws JsonProcessingException {
      //  try {
            String slno = fs.getBody().getSlno();
            String appid = fs.getBody().getAppid();
            boolean isPanChanged=false;
            VehicleLoanApplicant applicant = repository.getById(Long.valueOf(appid));
            VehicleLoanMaster vehicleLoanMaster = mrepo.findById(Long.valueOf(slno));
            VehicleLoanKyc kyc = kycrepository.findByAppId(applicant.getApplicantId());
            if (kyc == null) {
                kyc = new VehicleLoanKyc();
                kyc.setReqIpAddr(CommonUtils.getIP(fs.getReqip()));
                kyc.setSlno(Long.valueOf(slno));
                kyc.setWiNum(fs.getBody().getWinum());
                kyc.setApplicantId(applicant.getApplicantId());
                kyc.setDelFlg("N");
            }
            kyc.setCmDate(new Date());
            kyc.setCmUser(usd.getPPCNo());
            kyc.setHomeSol(usd.getSolid());
            applicant.setKycComplete("Y");
            applicant.setBasicComplete("N");
            String aadharMasked="";
            repository.resetLoanFlg(Long.valueOf(slno));
            String res_status = applicant.getResidentFlg(),dob="";

            if (fs.getBody().getFileFlag().equals("Y")) {
                for (DOC_ARRAY data : fs.getBody().getDOC_ARRAY()) {

                    switch (data.getDOC_NAME().trim().toUpperCase()) {
                        case "PAN":
                            kyc.setPanimg(data.getDOC_BASE64());
                            kyc.setPanext(data.getDOC_EXT());
                            kyc.setPanFilenameBpm(data.getDOC_NAME());
                            break;
                        case "AADHAAR":
                            if ("R".equals(res_status)) {
                                kyc.setAadharimg(data.getDOC_BASE64());
                                kyc.setAadharext(data.getDOC_EXT());
                            }
                            //  kyc.setAadharFilenameBpm(data.getDOC_NAME());
                            break;
                        case "PASSPORT":
                            if ("N".equals(res_status)) {
                                kyc.setPassportimg(data.getDOC_BASE64());
                                kyc.setPassportext(data.getDOC_EXT());
                                kyc.setPassportFilenameBpm(data.getDOC_NAME());
                            }
                            break;
                        case "VISA_OCI":
                            if ("N".equals(res_status)) {
                                kyc.setVisaimg(data.getDOC_BASE64());
                                kyc.setVisaext(data.getDOC_EXT());
                                kyc.setVisaFilenameBpm(data.getDOC_NAME());
                            }
                            break;
                        case "PHOTO":
                                kyc.setPhoto(data.getDOC_BASE64());
                                kyc.setPhotoext(data.getDOC_EXT());
                            break;
                        case "CONSENT_FORM":
                                kyc.setConsentimg(data.getDOC_BASE64());
                                kyc.setConsentimgext(data.getDOC_EXT());
                            break;
                        case "ORIGINAL_SEEN_VERIFIED":
                                kyc.setOriginalSeenCertificate(data.getDOC_BASE64());
                                kyc.setOriginalSeenCertificateExt(data.getDOC_EXT());
                            break;
                        case "CUSTOMER_SIGNATURE":
                                kyc.setCustSig(data.getDOC_BASE64());
                                kyc.setCustSigExt(data.getDOC_EXT());
                            break;
                        default:
                            // Handle unknown keys if needed
                            break;
                    }
                }
            }

            //ensure aadhar image is present
            if((kyc.getAadharimg()==null || kyc.getAadharimg().isEmpty()) && "R".equals(res_status)){
                return new TabResponse("F", "Masked Image of Aadhaar is  missing ,Please check");
            }

            // Map data fields from request to entity
            for (DataItem data : fs.getBody().getData()) {
                switch (data.getKey().trim()) {
                    case "pan":
                        if(kyc.getPanNo()!=null && !kyc.getPanNo().equalsIgnoreCase(data.getValue())){
                            applicant.setCreditComplete("N");
                            VLCredit vlCredit = vlCreditservice.findByApplicantIdAndDelFlg(applicant.getApplicantId());
                            if (vlCredit != null) {
                                vlCredit.setExperianFlag(false);
                                vlCreditservice.save(vlCredit);
                            }
                        }
                        kyc.setPanNo(data.getValue());
                        break;
                    case "ocr_pan":
                        kyc.setOcrPanNumber(data.getValue());
                        break;
                    case "ocr_pandob":
                        kyc.setOcrPanDob(CommonUtils.DateConvert(data.getValue(), "yyyy-MM-dd"));
                        break;
                    case "ocr_panname":
                        kyc.setOcrPanName(data.getValue());
                        break;
                    case "pandob":
                        dob=data.getValue();
                        kyc.setPanDob(CommonUtils.DateConvert(data.getValue(), "yyyy-MM-dd"));
                        break;
                    case "panname":
                        kyc.setPanName(data.getValue());
                        break;
                    case "pan-validated":
                        kyc.setPanDobNsdlValid(data.getValue().equals("true") ? "Y" : "N");
                        break;
                    case "uidmode":
                            kyc.setAadharMode(data.getValue());
                            kyc.setAadharOtpSent(data.getValue().equals("O") ? "Y" : "N");
                        break;
                    case "uid":
                            aadharMasked=data.getValue();
                            kyc.setAadharRefNum(uidRepository.getUIDRefNo(data.getValue()));
                        break;
                    case "uiddob":
                            kyc.setAadharYob(data.getValue());
                        break;
                    case "uidname":
                            kyc.setAadharName(data.getValue());
                        break;
                    case "uid-validated":
                            kyc.setAadharOtpValidated(data.getValue().equals("true") ? "Y" : "N");
                        break;
                    case "uidotpval":
                            kyc.setAadharOtpSent(data.getValue().length() == 6 ? "Y" : "N");
                        break;
                    case "passport":
                        if ("N".equals(res_status)) {
                            kyc.setPassportNumber(data.getValue());
                        }
                        break;
                    case "passportname":
                        if ("N".equals(res_status)) {
                            kyc.setPassportName(data.getValue());
                        }
                        break;
                    case "ocr_passport":
                        if ("N".equals(res_status)) {
                            kyc.setOcrPassportNum(data.getValue());
                        }
                        break;
                    case "passportexp":
                        if ("N".equals(res_status)) {
                            kyc.setPassportExpiryDate(CommonUtils.DateConvert(data.getValue(), "yyyy-MM-dd"));
                        }
                        break;
                    case "ocr_passportexp":
                        if ("N".equals(res_status)) {
                            kyc.setOcrpassportexpiry(CommonUtils.DateConvert(data.getValue(), "yyyy-MM-dd"));
                        }
                        break;
                    case "visaocimode":
                        if ("N".equals(res_status)) {
                            kyc.setVisaOciType(data.getValue());
                        }
                        break;
                    case "visa":
                        if ("N".equals(res_status)) {
                            kyc.setVisaOciNumber(data.getValue());
                        }
                        break;
                    case "visa_exp":
                        if ("N".equals(res_status)) {
                            kyc.setVisaExpiry(CommonUtils.DateConvert(data.getValue(), "yyyy-MM-dd"));
                        }
                        break;
                    case "cif_mode":
                        if ("N".equals(applicant.getSibCustomer())) {
                            if(cm.isEmpty(data.getValue()))
                                return new TabResponse("F", "Kindly Select CIF Creation Mode");
                            else if("M".equals(data.getValue()) && cm.isEmpty(kyc.getConsentimg()))
                                return new TabResponse("F", "Consent Form is mandatory if CIF creation Mode is Manual");
                            applicant.setCifCreationMode(data.getValue());
                        }
                        else{
                            applicant.setCifCreationMode("s");
                        }
                        break;
                    default:
                        // Handle unknown keys if needed
                        break;

                }
            }


            if ("R".equals(res_status)) {
                kyc.setPassportNumber(null);
                kyc.setPassportName(null);
                kyc.setOcrPassportNum(null);
                kyc.setPassportExpiryDate(null);
                kyc.setOcrpassportexpiry(null);
                kyc.setVisaOciType(null);
                kyc.setVisaOciNumber(null);
                kyc.setVisaExpiry(null);
            } else if ("N".equals(res_status)) {
//                kyc.setAadharMode(null);
//                kyc.setAadharOtpSent(null);
//                kyc.setAadharRefNum(null);
//                kyc.setAadharYob(null);
//                kyc.setAadharName(null);
//                kyc.setAadharOtpValidated(null);
//                kyc.setAadharOtpSent(null);
            }

            String name = "",table="";
            if (applicant.getResidentFlg().equals("N"))
                name = kyc.getPassportName();
            else
                name = kyc.getAadharName();



            if ("Y".equals(applicant.getSibCustomer())) {
                CustomerDetails cd = customerDetailsService.findByAppId(applicant.getApplicantId());
                if(cd==null){
                    return new TabResponse("F","Kindly click Fetch From CBS to proceed!!");
                }
                //WARNING VALIDATIONS
                List<VehicleLoanWarnMaster> loanWarnMaster = vehicleLoanWarnService.getWarnMaster();
                VehicleLoanApplicant finalApplicant = applicant;
                VehicleLoanKyc finalKyc = kyc;
                String finalDob = dob;
                Map<String, List<String>> validationMessages = loanWarnMaster.stream().flatMap(mas -> {
                    Map<String, List<String>> messages = new HashMap<>();
                    boolean warn=false;
                    String cbsValue=null,wiValue=null;
                    //DOB mismatch between application vs CBS

                    if ("WAR001".equals(mas.getWarnCode()) && (cd.getCustDob() == null || !cd.getCustDob().equalsIgnoreCase(finalDob))) {
                        cbsValue=cd.getCustDob();
                        wiValue= finalDob;

                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
                    //PAN is not available in CBS
                    if ("WAR002".equals(mas.getWarnCode()) && (cd.getPan() == null || cd.getPan().isBlank())) {
                        cbsValue=cd.getPan();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;


                        VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
                        vehicleLoanBlock.setSlno(Long.parseLong(slno));
                        vehicleLoanBlock.setWiNum(fs.getBody().getWinum());
                        vehicleLoanBlock.setApplicantId(finalApplicant.getApplicantId().toString());
                        vehicleLoanBlock.setBlockType(VLBlockCodes.PAN_CBS_MISSING);
                        vehicleLoanBlock.setActualValue(finalKyc.getPanNo());
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }
                    //PAN Mismatch
                    if ("WAR003".equals(mas.getWarnCode()) && !cd.getPan().equalsIgnoreCase(finalKyc.getPanNo())) {
                        cbsValue=cd.getPan();
                        wiValue=finalKyc.getPanNo();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;

                        VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
                        vehicleLoanBlock.setSlno(Long.parseLong(slno));
                        vehicleLoanBlock.setWiNum(fs.getBody().getWinum());
                        vehicleLoanBlock.setApplicantId(finalApplicant.getApplicantId().toString());
                        vehicleLoanBlock.setBlockType(VLBlockCodes.PAN_CBS_MISMATCH);
                        vehicleLoanBlock.setExpectedValue(cd.getPan());
                        vehicleLoanBlock.setActualValue(finalKyc.getPanNo());
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }
                    //Aadhaar is not available in CBS
                    if ("WAR004".equals(mas.getWarnCode()) && (!cm.isEmpty(finalKyc.getAadharRefNum()) && cm.isEmpty(cd.getAadhaarRefNo()))) {
                        if(cm.isEmpty(cd.getPassport()) || cm.isEmpty(cd.getVoterid()) || cm.isEmpty(cd.getDrivingLicence())){
                            mas.setSeverity("Low");
                        }
                        cbsValue=cd.getAadhaarRefNo();
                        wiValue=finalKyc.getAadharRefNum();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
                    //Aadhaar Mismatch
                    if ("WAR005".equals(mas.getWarnCode()) && (!cm.isEmpty(finalKyc.getAadharRefNum()) && !cm.isEmpty(cd.getAadhaarRefNo()) && !cd.getAadhaarRefNo().equalsIgnoreCase(finalKyc.getAadharRefNum()))) {
                        cbsValue=cd.getAadhaarRefNo();
                        wiValue=finalKyc.getAadharRefNum();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;

                        VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
                        vehicleLoanBlock.setSlno(Long.parseLong(slno));
                        vehicleLoanBlock.setWiNum(fs.getBody().getWinum());
                        vehicleLoanBlock.setApplicantId(finalApplicant.getApplicantId().toString());
                        vehicleLoanBlock.setBlockType(VLBlockCodes.AADHAAR_CBS_MISMATCH);
                        vehicleLoanBlock.setExpectedValue(cd.getAadhaarRefNo());
                        vehicleLoanBlock.setActualValue(finalKyc.getAadharRefNum());
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }

                    //Passport is not available in CBS for NRI
                    if ("WAR006".equals(mas.getWarnCode()) && ("N".equals(finalApplicant.getResidentFlg()) && cm.isEmpty(cd.getPassport()))) {
                        cbsValue=cd.getPassport();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
                    //Passport number mismatch between application vs CBS for NRI
                    if ("WAR007".equals(mas.getWarnCode()) && ("N".equals(finalApplicant.getResidentFlg()) && !cm.isEmpty(cd.getPassport()) && !cd.getPassport().equalsIgnoreCase(finalKyc.getPassportNumber()))) {
                        cbsValue=cd.getPassport();
                        wiValue=finalKyc.getPassportNumber();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;

                        VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
                        vehicleLoanBlock.setSlno(Long.parseLong(slno));
                        vehicleLoanBlock.setWiNum(fs.getBody().getWinum());
                        vehicleLoanBlock.setApplicantId(finalApplicant.getApplicantId().toString());
                        vehicleLoanBlock.setBlockType(VLBlockCodes.PASSPORT_CBS_MISMATCH);
                        vehicleLoanBlock.setExpectedValue(cd.getPassport());
                        vehicleLoanBlock.setActualValue(finalKyc.getPassportNumber());
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                    }

                    //Resident status mismatch between application vs CBS
                    if ("WAR008".equals(mas.getWarnCode()) &&  (cd.getResidentialStatus() == null || !cd.getResidentialStatus().equals(finalApplicant.getResidentFlg().equals("N")?"N":"Y"))) {
                        cbsValue=cd.getResidentialStatus();
                        wiValue=finalApplicant.getResidentFlg();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
                    //CIF is minor in CBS
                    if ("WAR009".equals(mas.getWarnCode()) &&  (cd.getMinorFlag() == null || cd.getMinorFlag().equals("Y"))) {
                        cbsValue=cd.getMinorFlag();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
                    //Visa/OCI/CDC mismatch between application vs CBS
                    if ("WAR0010".equals(mas.getWarnCode()) &&  ("N".equals(finalApplicant.getResidentFlg()) && "V".equals(finalKyc.getVisaOciType()) && cd.getVisa() != null && !cd.getVisa().equalsIgnoreCase(finalKyc.getVisaOciNumber()))) {
                        cbsValue=cd.getVisa();
                        wiValue= finalKyc.getVisaOciNumber();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
                    if ("WAR0010".equals(mas.getWarnCode()) &&  ("N".equals(finalApplicant.getResidentFlg()) && "C".equals(finalKyc.getVisaOciType()) && cd.getCdnNo() != null && !cd.getCdnNo().equalsIgnoreCase(finalKyc.getVisaOciNumber()))) {
                        cbsValue=cd.getCdnNo();
                        wiValue= finalKyc.getVisaOciNumber();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
                    if ("WAR0010".equals(mas.getWarnCode()) &&  ("N".equals(finalApplicant.getResidentFlg()) && "O".equals(finalKyc.getVisaOciType()) && cd.getOciCard() != null && !cd.getOciCard().equalsIgnoreCase(finalKyc.getVisaOciNumber()))) {
                        cbsValue=cd.getOciCard();
                        wiValue= finalKyc.getVisaOciNumber();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }


                    //Visa/OCI/CDC  is not available in CBS
                    if ("WAR0011".equals(mas.getWarnCode()) &&   ("N".equals(finalApplicant.getResidentFlg()) &&  "V".equals(finalKyc.getVisaOciType()) && cm.isEmpty(cd.getVisa()))) {
                        cbsValue=cd.getVisa();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
                    if ("WAR0011".equals(mas.getWarnCode()) &&   ("N".equals(finalApplicant.getResidentFlg()) &&  "C".equals(finalKyc.getVisaOciType()) && cm.isEmpty(cd.getCdnNo()))) {
                        cbsValue=cd.getCdnNo();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
                    if ("WAR0011".equals(mas.getWarnCode()) &&   ("N".equals(finalApplicant.getResidentFlg()) &&  "O".equals(finalKyc.getVisaOciType()) && cm.isEmpty(cd.getOciCard()))) {
                        cbsValue=cd.getOciCard();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
                    //PAN vs Aadhaar name mismatch
                    if ("WAR0012".equals(mas.getWarnCode()) && !cm.isEmpty(finalKyc.getAadharName()) && !finalKyc.getPanName().equalsIgnoreCase(finalKyc.getAadharName())) {
                        cbsValue="";
                        wiValue= finalKyc.getPanName()+"|"+finalKyc.getAadharName();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }

                    //KYC Complied
                    if ("WAR0015".equals(mas.getWarnCode()) &&  (cd.getKycComplied() == null || !cd.getKycComplied().equals("Y"))) {
                        cbsValue=cd.getKycComplied();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;
                    }
//                    //Aadhaar seeding not proper
//                    if ("WAR0016".equals(mas.getWarnCode()) ) {
//                        cbsValue=cd.getKycComplied();
//                        messages = cm.assignWarn(messages, mas);
//                        warn=true;
//                    }
                    //Customer ID of STAFF is  not Allowed.
                    if ("WAR0020".equals(mas.getWarnCode()) && validationRepository.checkWhetherStaff(finalApplicant.getCifId())) {
                        wiValue=finalApplicant.getCifId();
                        messages = cm.assignWarn(messages, mas);
                        warn=true;

                        /*
                        VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
                        vehicleLoanBlock.setSlno(Long.parseLong(slno));
                        vehicleLoanBlock.setWiNum(fs.getBody().getWinum());
                        vehicleLoanBlock.setApplicantId(finalApplicant.getApplicantId().toString());
                        vehicleLoanBlock.setBlockType(VLBlockCodes.PASSPORT_CBS_MISMATCH);
                        vehicleLoanBlock.setExpectedValue(cd.getPassport());
                        vehicleLoanBlock.setActualValue(finalKyc.getPassportNumber());
                        vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                        */
                    }
                    if(warn){
                        VehicleLoanWarnData vehicleLoanWarnData=new VehicleLoanWarnData();
                        vehicleLoanWarnData.setApplicantId(finalApplicant.getApplicantId());
                        vehicleLoanWarnData.setSlNo(finalApplicant.getSlno());
                        vehicleLoanWarnData.setWiNum(finalApplicant.getWiNum());
                        vehicleLoanWarnData.setWarnCode(mas.getWarnCode());
                        vehicleLoanWarnData.setWarnDesc(mas.getWarnDesc());
                        vehicleLoanWarnData.setCbsValue(cbsValue);
                        vehicleLoanWarnData.setWiValue(wiValue);
                        vehicleLoanWarnData.setSeverity(mas.getSeverity());
                        vehicleLoanWarnData.setSeverityDesc(mas.getSeverity().equals("High")?"Blocker":"Non-Blocker");
                        vehicleLoanWarnData.setDelFlg("N");
                        vehicleLoanWarnData.setReqIpAddr(usd.getRemoteIP());
                        vehicleLoanWarnData.setLastModDate(new Date());
                        vehicleLoanWarnData.setLastModUser(usd.getPPCNo());
                        vehicleLoanWarnData.setQueue("BM");
                        vehicleLoanWarnService.saveWarn(vehicleLoanWarnData);
                    }
                    return messages.entrySet().stream();
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(existingValue, newValue)->{
                    existingValue.addAll(newValue);
                    return existingValue;
                }));


                if (!validationMessages.isEmpty()) {
                     table = htmlTableGenerator.generateTable(validationMessages);

                    if (validationMessages.get("High")!=null && !validationMessages.get("High").isEmpty()) {
                        throw new ValidationException(ValidationError.COM001,table);
                    }
                }

            }
            if(usd.getData(appid,"PAN_NSDL")==null){
               return new TabResponse("F","Kindly Revalidate Pan ");
            }
            JsonNode Panjson=mapper.readTree(usd.getData(appid,"PAN_NSDL"));


            //SAVE PAN DATA
            String panseeding=Panjson.path("seeding_status").asText();
            String panStatus=Panjson.path("pan_status").asText();
            String panName=Panjson.path("name").asText();
            String panDob=Panjson.path("dob").asText();
            kyc.setPanUidLink(panseeding);
            kyc.setPanStatus(panStatus);
            kyc.setPanNameStatus(panName);
            kyc.setPanDobStatus(panDob);
            if(!devMode) {
                bureauService.BureauCheck(Panjson, kyc.getPanName(), kyc.getPanNo(), aadharMasked, String.valueOf(kyc.getSlno()), kyc.getWiNum(), appid, applicant.getResidentFlg());
            }
            //AGE ELIGIBILITY
            VehicleEmpProgram vpl=fetchRepository.getEmpProgramforApplicant(applicant.getApplicantId());
            String program="NONE",employee="";
            if(vpl!=null){
                if(!cm.isEmpty(vpl.getLoanProgram()))
                    program=vpl.getLoanProgram();
                if(!cm.isEmpty(vpl.getEmploymentType()))
                    employee=vpl.getEmploymentType();
            }
            int age=CommonUtils.calculateAge(kyc.getPanDob().toString());
            if(!validationRepository.checkAge(program,employee,age)){
                throw new ValidationException(ValidationError.KYCO01,kyc.getPanName());
            }


            if (applicant.getApplicantType().equals("A"))
                vehicleLoanMaster.setCustName(name);
            applicant.setApplName(name.toUpperCase().trim());
            applicant.setApplDob(kyc.getPanDob());
            VehicleLoanMaster master = masterService.findById(Long.valueOf(slno));
            master.setCurrentTab(fs.getBody().getCurrenttab());
            masterService.saveLoan(master);
            kycrepository.save(kyc);
            repository.saveApplicant(applicant);
            Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsRepository.findBySlnoAndDelFlg(applicant.getSlno(), "N");
            EligibilityDetails eligibilityDetails =null;
            if (eligibilityDetails_.isPresent()) {
                eligibilityDetails=eligibilityDetails_.get();
                eligibilityDetails.setEligibilityFlg("N");
                eligibilityDetails.setProceedFlag("N");
                eligibilityDetailsRepository.save(eligibilityDetails);
            }
            mrepo.saveLoan(vehicleLoanMaster);
            JsonObject msg=new JsonObject();
            msg.addProperty("warn",table);
            msg.addProperty("name",name);
            msg.addProperty("age",cm.getAge(kyc.getPanDob()));
            if (!table.isBlank())
                return new TabResponse("W", msg.toString(), applicant.getApplicantId().toString());
            else
                return new TabResponse("S", msg.toString(), applicant.getApplicantId().toString());

//        }
//        catch (Exception e){
//            e.printStackTrace();
//            return new TabResponse("F", e.getMessage());
//        }
    }




    }
