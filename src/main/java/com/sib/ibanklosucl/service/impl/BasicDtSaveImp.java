package com.sib.ibanklosucl.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.service.VlSaveService;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.HtmlTableGenerator;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BasicDtSaveImp implements VlSaveService {
    @Autowired
    private VehicleLoanMasterService masterService;
    @Autowired
    private VehicleLoanApplicantService repository;
    @Autowired
    private VehicleLoanBasicService repo;

    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    private VehicleLoanWarnService vehicleLoanWarnService;
    @Autowired
    private FinacleLosDedupeService dedupeService;
    @Autowired
    private MisrctService misrctService;
    @Autowired
    private ValidationRepository validationRepository;
    @Autowired
    private HtmlTableGenerator htmlTableGenerator;
    @Autowired
    private CommonUtils cm;
    @Autowired
    private PincodeMasterService pincodeMasterService;
    @Autowired
    private VehicleLoanKycService kycrepository;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;
    @Autowired
    private UserSessionData usd;
    @Override
    @Transactional(rollbackOn = Exception.class)
    public TabResponse executeSave(FormSave fs){
        String slno = fs.getBody().getSlno();
        String appid = fs.getBody().getAppid();
        VehicleLoanApplicant applicant = repository.getById(Long.valueOf(appid));
        VehicleLoanBasic vl=repo.findByAppId(applicant.getApplicantId());
//        VehicleLoanAddress adr_per=vladr.findByAppId(applicant.getApplicantId(),"PERM");
//        VehicleLoanAddress adr_com=vladr.findByAppId(applicant.getApplicantId(),"COMM");
        if (vl == null) {
            vl=new VehicleLoanBasic();
            vl.setReqIpAddr(CommonUtils.getIP(fs.getReqip()));
            vl.setSlno(Long.valueOf(slno));
            vl.setWiNum(fs.getBody().getWinum());
            vl.setApplicantId(applicant.getApplicantId());
            vl.setDelFlg("N");
        }
        vl.setCmdate(new Date());
        vl.setCmuser(usd.getPPCNo());
        vl.setHomeSol(usd.getSolid());
        applicant.setBasicComplete("Y");
        vl.setApplicantName(applicant.getApplName());
        vl.setApplicantDob(applicant.getApplDob());
        vl.setAge(cm.getAge(applicant.getApplDob()));

        VehicleLoanMaster master=masterService.findById(Long.valueOf(slno));
        master.setCurrentTab(fs.getBody().getCurrenttab());
        masterService.saveLoan(master);
        if(fs.getBody().getFileFlag().equals("Y")){
            for (DOC_ARRAY data : fs.getBody().getDOC_ARRAY()) {
                switch (data.getDOC_NAME().trim().toUpperCase()) {
                    case "COM_ADR_PROOF":
                        vl.setAddrdocnameBpm(data.getDOC_NAME().trim().toUpperCase());
                        break;
                    default:
                        // Handle unknown keys if needed
                        break;
                }
            }
        }
        String dedupcifid="";
        if (fs.getBody().getFileFlag().equals("Y")) {
            for (DOC_ARRAY data : fs.getBody().getDOC_ARRAY()) {

                switch (data.getDOC_NAME().trim().toUpperCase()) {
                    case "COM_ADR_PROOF":
                        vl.setCpoaDoc(data.getDOC_BASE64());
                        vl.setCpoaExt(data.getDOC_EXT());
                        break;
                    default:
                        // Handle unknown keys if needed
                        break;
                }
            }
        }
        // Map data fields from request to entity
        for (DataItem data : fs.getBody().getData()) {
            switch (data.getKey().trim()) {
//                case "basic_name":
//                    vl.setApplicantName(applicant.getValue());
//                    break;
                case "basic_gender":
                    vl.setGender(data.getValue());
                    break;
//                case "basic_dob":
//                    vl.setApplicantDob(CommonUtils.DateConvert(data.getValue(), "yyyy-MM-dd"));
//                    break;
                case "basic_ftname":
                    vl.setFatherName(data.getValue());
                    break;
                case "basic_mtname":
                    vl.setMotherName(data.getValue());
                    break;
                case "basic_ms":
                    vl.setMaritalStatus(data.getValue());
                    break;
                case "basic_crmlead":
                 //   vl.setCR(data.getValue().equals("true")?"Y":"N");
                    break;
                case "basic_saltutation":
                    vl.setSalutation(data.getValue());
                    break;
                case "basic_occupation":
                    vl.setOccupation(data.getValue());
                    break;
                case "basic_spname":
                    vl.setSpouseName(data.getValue());
                    break;
                case "basic_mob":
                    vl.setMobileNo(data.getValue());
                    break;
                case "basic_mobcode":
                    vl.setMobileCntryCode(data.getValue());
                    break;
                case "basic_email":
                    vl.setEmailId(data.getValue());
                    break;
                    //ADDRESS
                case "permanentAddress1":
                    vl.setAddr1(data.getValue());
                    break;
                case "permanentAddress2":
                    vl.setAddr2(data.getValue());
                    break;
                case "permanentAddress3":
                    vl.setAddr3(data.getValue());
                    break;
                case "permanentCity":
                    vl.setCity(data.getValue());
                    vl.setCitydesc(misrctService.getByCodeValue("01",data.getValue()).getCodedesc());
                    break;
                case "permanentState":
                    vl.setState(data.getValue());
                    vl.setStatedesc(misrctService.getByCodeValue("02",data.getValue()).getCodedesc());
                    break;
                case "permanentCountry":
                    vl.setCountry(data.getValue());
                    vl.setCountrydesc(misrctService.getByCodeValue("03",data.getValue()).getCodedesc());
                    break;
                case "permanentPin":
                    vl.setPin(data.getValue());
                    break;
                case "permanentDurationOfStay":
                    vl.setDurationStay(Integer.valueOf(data.getValue()));
                    break;
                case "permanentResidenceType":
                    vl.setResidenceType(data.getValue());
                    break;
                case "sameAsper_flg":
                    vl.setSameAsPer(data.getValue());
                    break;
                case "presentAddress1":
                    vl.setComAddr1(data.getValue());
                    break;
                case "presentAddress2":
                    vl.setComAddr2(data.getValue());
                    break;
                case "presentAddress3":
                    vl.setComAddr3(data.getValue());
                    break;
                case "presentCity":
                    vl.setComCity(data.getValue());
                    vl.setComCityedesc(misrctService.getByCodeValue("01",data.getValue()).getCodedesc());
                    break;
                case "presentState":
                    vl.setComState(data.getValue());
                    vl.setComStatedesc(misrctService.getByCodeValue("02",data.getValue()).getCodedesc());
                    break;
                case "presentCountry":
                    vl.setComCountry(data.getValue());
                    vl.setComCountrydesc(misrctService.getByCodeValue("03",data.getValue()).getCodedesc());
                    break;
                case "presentPin":
                    vl.setComPin(data.getValue());
                    break;
                case "presentDurationOfStay":
                    vl.setComDurationStay(Integer.valueOf(data.getValue()));
                    break;
                case "presentdistanceFromBranch":
                    vl.setCommdistanceFromBranch(Integer.valueOf(data.getValue()));
                    break;
                case "permanentdistanceFromBranch":
                    vl.setDistanceFromBranch(Integer.valueOf(data.getValue()));
                    break;
                case "presentResidenceType":
                    vl.setComResidenceType(data.getValue());
                    break;
                case "preferred_flag":
                    vl.setPreferredFlag(data.getValue());
                    break;
                case "current_residence_flag":
                    vl.setCurrentResidenceFlag(data.getValue());
                    break;
                case "loscount":
                    vl.setLosDedupCount(Integer.parseInt(data.getValue()));
                    break;
                case "fincount":
                    vl.setFinacleDedupCount(Integer.parseInt(data.getValue()));
                    break;
                case "dedupcustid":
                    dedupcifid=data.getValue();
                    break;
                case "basic_edu":
                    vl.setEducation(data.getValue());
                    break;
                case "basic_pep":
                    vl.setPoliticallyExposed(data.getValue());
                    break;
                case "basic_annualincome":
                    vl.setAnnualIncome(data.getValue());
                    break;
                case "basic_cpoa":
                    vl.setCpoa(data.getValue());
                    break;
                default:
                    // Handle unknown keys if needed
                    break;

            }
        }

        CustomerDetails cd = null, cdold = null;


        if(applicant.getSibCustomer().equals("Y")){
             cd = customerDetailsService.findByAppId(applicant.getApplicantId());
            if(cd==null){
                return new TabResponse("F","Kindly click Fetch From CBS to proceed!!");
            }
            vl.setAddr1(cd.getPermanentAddress1());
            vl.setAddr2(cd.getPermanentAddress2());
            vl.setAddr3(cd.getPermanentAddress3());
            vl.setCity(cd.getPermanentCityCode());
            vl.setCitydesc(cd.getPermanentCity());
            vl.setState(cd.getPermanentStateCode());
            vl.setStatedesc(cd.getPermanentState());
            vl.setCountry(cd.getPermanentCountryCode());
            vl.setCountrydesc(cd.getPermanentCountry());
            vl.setPin(cd.getPermanentPin());

            if(cm.isEmpty(cd.getPermanentAddress1())|| cm.isEmpty(cd.getPermanentAddress2()) || cm.isEmpty(cd.getPermanentCityCode()) || cm.isEmpty(cd.getPermanentStateCode()) || cm.isEmpty(cd.getPermanentCountryCode())|| cm.isEmpty(cd.getPermanentPin())){
                if(applicant.getSibCustomer().equalsIgnoreCase("Y")){
                    throw new ValidationException(ValidationError.COM001, "Kindly Update the Permanent address details in CBS ,mandatory fields missing !!");
                }
                throw new ValidationException(ValidationError.COM001, "Kindly complete Permanent Address!!");
            }

        }

        if(vl.getSameAsPer().equals("Y"))
        {
            vl.setComAddr1(vl.getAddr1());
            vl.setComAddr2(vl.getAddr2());
            vl.setComAddr3(vl.getAddr3());
            vl.setComCity(vl.getCity());
            vl.setComCityedesc(vl.getCitydesc());
            vl.setComState(vl.getState());
            vl.setComStatedesc(vl.getStatedesc());
            vl.setComCountry(vl.getCountry());
            vl.setComCountrydesc(vl.getCountrydesc());
            vl.setComPin(vl.getPin());
            vl.setComResidenceType(vl.getResidenceType());
            vl.setComDurationStay(vl.getDurationStay());
        }
        //Address Validation
        if("IN".equalsIgnoreCase(vl.getCountry())  &&  "N".equalsIgnoreCase(applicant.getSibCustomer()) && !pincodeMasterService.isValidIndianPincode(vl.getPin(), vl.getState(),vl.getCity())) {
            throw new ValidationException(ValidationError.COM001, "The  Country, State, City, Pin code Combination in Permanent Address is not Valid!!");
        }
        if("IN".equalsIgnoreCase(vl.getComCountry()) &&  "N".equalsIgnoreCase(applicant.getSibCustomer())  && !pincodeMasterService.isValidIndianPincode(vl.getComPin(), vl.getComState(),vl.getComCity())) {
            throw new ValidationException(ValidationError.COM001, "The  Country, State, City, Pin code Combination in Communication Address is not valid!!");
        }

        String table="";


        List<FinDedupEntity> findedup = dedupeService.getFinDupByAppID(Long.valueOf(slno), Long.valueOf(appid));
        if(findedup.size() == 0){
            throw new ValidationException(ValidationError.COM001, "Kindly Complete Finacle Dedupe!!");
        }

        if(vl.getMobileCntryCode().equalsIgnoreCase("91") && vl.getMobileNo().length()!=10){
            throw new ValidationException(ValidationError.COM001, "Mobile Number Should be 10 digits for +91 Mobile Code!!");
        }


        JsonObject dedupDt=new JsonObject();
        dedupDt.addProperty("isDedup",false);
        String exist_cifid = applicant.getCifId();

        if (findedup.size() > 0) {
            if (exist_cifid != null && !exist_cifid.equalsIgnoreCase(dedupcifid) && findedup.stream().anyMatch(t-> t.getDedupflag().equalsIgnoreCase("N"))) {
                throw new ValidationException(ValidationError.COM001, "The selected Customer ID doesnt match with the Cust ID selected in general details. Please modify the general, KYC, and basic details as per the deduped Customer ID to continue");
            }
            if ((exist_cifid == null || !exist_cifid.equalsIgnoreCase(dedupcifid)) && findedup.stream().anyMatch(t-> t.getDedupflag().equalsIgnoreCase("Y"))) {
                throw new ValidationException(ValidationError.COM001, "The mentioned details match with the customer information in Finacle Dedupe. Please modify the general, KYC, and basic details as per the deduped Customer ID to continue");
            }
            if (findedup.size() > 1) {
                String finalDedupcifid = dedupcifid;
                // Count customers with no TDS Customer ID other than the selected customer
                long invalidTdsCount = findedup.stream()
                        .filter(t -> !exist_cifid.equalsIgnoreCase(finalDedupcifid) && (t.getTdsCustomerid() == null || t.getTdsCustomerid().isBlank()))
                        .count();
                if (invalidTdsCount > 0) {
                    throw new ValidationException(ValidationError.COM001, "Multiple Customers Found during Finacle Dedupe!!");
                }
                long validTdsCount = findedup.stream()
                        .filter(t ->  t.getTdsCustomerid()!=null && t.getTdsCustomerid().equalsIgnoreCase(finalDedupcifid))
                        .count();
                if (validTdsCount == 0 || validTdsCount != findedup.size() - 1) {
                    throw new ValidationException(ValidationError.COM001, "Multiple Customers Found during Finacle Dedupe!!");
                }
            }


//            String exist_cifid = applicant.getCifId();
//            if (exist_cifid == null || !exist_cifid.equalsIgnoreCase(dedupcifid)) {
//
//                cdold = customerDetailsService.findByAppId(applicant.getApplicantId());
//                if (cdold != null) {
//                    cdold.setDelFlg("Y");
//                    customerDetailsService.saveCustomerDetails(cdold);
//                }
//                cd = new CustomerDetails();
//                cd.setSlno(Long.valueOf(slno));
//                cd.setWiNum(fs.getBody().getWinum());
//                cd.setApplicantId(applicant.getApplicantId());
//                cd.setCustId(dedupcifid);
//                cd.setReqIpAddr(CommonUtils.getIP(fs.getReqip()));
//                cd.setDelFlg("N");
//                cd.setCmDate(new Date());
//                cd.setCmUser(usd.getPPCNo());
//                cd.setHomeSol(usd.getSolid());
//                try {
//                    cd = cifViewService.fetchAndProcessCustomerData(cd);
//                    customerDetailsService.saveCustomerDetails(cd);
//                    if (!cd.getValidFlg().equals("Y"))
//                        throw new ValidationException(ValidationError.COM001, cd.getErrorMsg());
//                } catch (Exception e) {
//                    e.printStackTrace();//Constants.Messages.CIF_ERROR
//                    throw new ValidationException(ValidationError.COM001, e.getMessage());
//                }
//            }
//
//            applicant.setCifId(dedupcifid);
//            applicant.setSibCustomer("Y");
//            applicant.setResidentFlg(cd.getResidentialStatus().equals("Y")?"R":"N");
//
//            dedupDt.addProperty("isDedup",true);
//            dedupDt.addProperty("cifid",dedupcifid);
//            dedupDt.addProperty("sibCust",applicant.getSibCustomer());
//            dedupDt.addProperty("resFlag",applicant.getResidentFlg());

        }
        if("Y".equals(applicant.getSibCustomer())) {
            //WARNING VALIDATIONS
            List<VehicleLoanWarnMaster> loanWarnMaster = vehicleLoanWarnService.getWarnMaster();
            VehicleLoanApplicant finalApplicant = applicant;
            VehicleLoanKyc finalKyc = kycrepository.findByAppId(applicant.getApplicantId());
            String finalDob =CommonUtils.ConvertDate(vl.getApplicantDob(),"yyyy-MM-dd");
            CustomerDetails finalCd = cd;
            VehicleLoanBasic finalBasicVl = vl;
            Map<String, List<String>> validationMessages = loanWarnMaster.stream().flatMap(mas -> {
                Map<String, List<String>> messages = new HashMap<>();
                boolean warn = false;
                String cbsValue = null, wiValue = null;
                //DOB mismatch between application vs CBS
                if ("WAR001".equals(mas.getWarnCode()) && (finalCd.getCustDob() == null || !finalCd.getCustDob().equalsIgnoreCase(finalDob))) {
                    cbsValue = finalCd.getCustDob();
                    wiValue = finalDob;
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                //PAN is not available in CBS
                if ("WAR002".equals(mas.getWarnCode()) && (finalCd.getPan() == null || finalCd.getPan().isBlank())) {
                    cbsValue = finalCd.getPan();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                //PAN Mismatch
                if ("WAR003".equals(mas.getWarnCode()) && !finalCd.getPan().equalsIgnoreCase(finalKyc.getPanNo())) {
                    cbsValue = finalCd.getPan();
                    wiValue = finalKyc.getPanNo();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                //Aadhaar is not available in CBS
                if ("WAR004".equals(mas.getWarnCode()) && (!cm.isEmpty(finalKyc.getAadharRefNum()) && cm.isEmpty(finalCd.getAadhaarRefNo()))) {
                    if (cm.isEmpty(finalCd.getPassport()) || cm.isEmpty(finalCd.getVoterid()) || cm.isEmpty(finalCd.getDrivingLicence())) {
                        mas.setSeverity("Low");
                    }
                    cbsValue = finalCd.getAadhaarRefNo();
                    wiValue = finalKyc.getAadharRefNum();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                //Aadhaar Mismatch
                if ("WAR005".equals(mas.getWarnCode()) && (!cm.isEmpty(finalKyc.getAadharRefNum()) && !cm.isEmpty(finalCd.getAadhaarRefNo()) && !finalCd.getAadhaarRefNo().equalsIgnoreCase(finalKyc.getAadharRefNum()))) {
                    cbsValue = finalCd.getAadhaarRefNo();
                    wiValue = finalKyc.getAadharRefNum();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }

                //Passport is not available in CBS for NRI
                if ("WAR006".equals(mas.getWarnCode()) && ("N".equals(finalApplicant.getResidentFlg()) && cm.isEmpty(finalCd.getPassport()))) {
                    cbsValue = finalCd.getPassport();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                //Passport number mismatch between application vs CBS for NRI
                if ("WAR007".equals(mas.getWarnCode()) && ("N".equals(finalApplicant.getResidentFlg()) && !cm.isEmpty(finalCd.getPassport())  && !finalCd.getPassport().equalsIgnoreCase(finalKyc.getPassportNumber()))) {
                    cbsValue = finalCd.getPassport();
                    wiValue = finalKyc.getPassportNumber();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                //Resident status mismatch between application vs CBS
                if ("WAR008".equals(mas.getWarnCode()) &&  (finalCd.getResidentialStatus() == null || !finalCd.getResidentialStatus().equals(finalApplicant.getResidentFlg().equals("N")?"N":"Y"))) {
                    cbsValue=finalCd.getResidentialStatus();
                    wiValue=finalApplicant.getResidentFlg();
                    messages = cm.assignWarn(messages, mas);
                    warn=true;
                }
                //CIF is minor in CBS
                if ("WAR009".equals(mas.getWarnCode()) &&  (finalCd.getMinorFlag() == null || finalCd.getMinorFlag().equals("Y"))) {
                    cbsValue=finalCd.getMinorFlag();
                    messages = cm.assignWarn(messages, mas);
                    warn=true;
                }
                //Visa mismatch between application vs CBS
                if ("WAR0010".equals(mas.getWarnCode()) && ("N".equals(finalApplicant.getResidentFlg()) && "V".equals(finalKyc.getVisaOciType()) && finalCd.getVisa() != null  && !finalCd.getVisa().equalsIgnoreCase(finalKyc.getVisaOciNumber()))) {
                    cbsValue = finalCd.getVisa();
                    wiValue = finalKyc.getVisaOciNumber();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                if ("WAR0010".equals(mas.getWarnCode()) &&  ("N".equals(finalApplicant.getResidentFlg()) && "C".equals(finalKyc.getVisaOciType()) && finalCd.getCdnNo() != null && !finalCd.getCdnNo().equalsIgnoreCase(finalKyc.getVisaOciNumber()))) {
                    cbsValue=finalCd.getCdnNo();
                    wiValue= finalKyc.getVisaOciNumber();
                    messages = cm.assignWarn(messages, mas);
                    warn=true;
                }
                if ("WAR0010".equals(mas.getWarnCode()) &&  ("N".equals(finalApplicant.getResidentFlg()) && "O".equals(finalKyc.getVisaOciType()) && finalCd.getOciCard() != null && !finalCd.getOciCard().equalsIgnoreCase(finalKyc.getVisaOciNumber()))) {
                    cbsValue=finalCd.getOciCard();
                    wiValue= finalKyc.getVisaOciNumber();
                    messages = cm.assignWarn(messages, mas);
                    warn=true;
                }
                //Visa is not available in CBS
                if ("WAR0011".equals(mas.getWarnCode())  && "V".equals(finalKyc.getVisaOciType()) && ("N".equals(finalApplicant.getResidentFlg()) && cm.isEmpty(finalCd.getVisa()))) {
                    cbsValue = finalCd.getVisa();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                if ("WAR0011".equals(mas.getWarnCode()) &&   ("N".equals(finalApplicant.getResidentFlg()) &&  "C".equals(finalKyc.getVisaOciType()) && cm.isEmpty(finalCd.getCdnNo()))) {
                    cbsValue=finalCd.getCdnNo();
                    messages = cm.assignWarn(messages, mas);
                    warn=true;
                }
                if ("WAR0011".equals(mas.getWarnCode()) &&   ("N".equals(finalApplicant.getResidentFlg()) &&  "O".equals(finalKyc.getVisaOciType()) && cm.isEmpty(finalCd.getOciCard()))) {
                    cbsValue=finalCd.getOciCard();
                    messages = cm.assignWarn(messages, mas);
                    warn=true;
                }
                //PAN vs Aadhaar name mismatch
                if ("WAR0012".equals(mas.getWarnCode()) && !cm.isEmpty(finalKyc.getAadharName()) && !finalKyc.getPanName().equalsIgnoreCase(finalKyc.getAadharName())) {
                    cbsValue = "";
                    wiValue = finalKyc.getPanName() + "|" + finalKyc.getAadharName();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                //Name mismatch between application vs CBS
                if ("WAR0013".equals(mas.getWarnCode()) &&  !finalApplicant.getApplName().trim().equalsIgnoreCase(finalCd.getCustomerName().trim())) {
                    cbsValue = finalCd.getCustomerName();
                    wiValue = finalApplicant.getApplName();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                //KYC Complied
                if ("WAR0015".equals(mas.getWarnCode()) && (finalCd.getKycComplied() == null || !finalCd.getKycComplied().equals("Y"))) {
                    cbsValue = finalCd.getKycComplied();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }


                //Phone number mismatch between application vs CBS
                if ("WAR0017".equals(mas.getWarnCode()) && !cm.isEmpty(finalCd.getCellPhone()) && !(finalBasicVl.getMobileCntryCode()+finalBasicVl.getMobileNo()).equalsIgnoreCase(finalCd.getCellPhone())) {
                    cbsValue = finalCd.getCellPhone();
                    wiValue = finalBasicVl.getMobileNo();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                //Phone number is not available in CBS
                if ("WAR0018".equals(mas.getWarnCode()) && cm.isEmpty(finalCd.getCellPhone())) {
                    cbsValue = finalCd.getCellPhone();
                    wiValue = finalBasicVl.getMobileNo();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                //Email mismatch between application vs CBS
                if ("WAR0019".equals(mas.getWarnCode()) && !cm.isEmpty(finalCd.getCommEmail()) && !finalBasicVl.getEmailId().equalsIgnoreCase(finalCd.getCommEmail())) {
                    cbsValue = finalCd.getCommEmail();
                    wiValue = finalBasicVl.getEmailId();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }

                //Customer ID of STAFF is  not Allowed.
                if ("WAR0020".equals(mas.getWarnCode()) && validationRepository.checkWhetherStaff(finalApplicant.getCifId())) {
                    wiValue = finalApplicant.getCifId();
                    messages = cm.assignWarn(messages, mas);
                    warn = true;
                }
                if (warn) {
                    VehicleLoanWarnData vehicleLoanWarnData = new VehicleLoanWarnData();
                    vehicleLoanWarnData.setApplicantId(finalApplicant.getApplicantId());
                    vehicleLoanWarnData.setSlNo(finalApplicant.getSlno());
                    vehicleLoanWarnData.setWiNum(finalApplicant.getWiNum());
                    vehicleLoanWarnData.setWarnCode(mas.getWarnCode());
                    vehicleLoanWarnData.setWarnDesc(mas.getWarnDesc());
                    vehicleLoanWarnData.setCbsValue(cbsValue);
                    vehicleLoanWarnData.setWiValue(wiValue);
                    vehicleLoanWarnData.setSeverity(mas.getSeverity());
                    vehicleLoanWarnData.setSeverityDesc(mas.getSeverity().equals("High") ? "Blocker" : "Non-Blocker");
                    vehicleLoanWarnData.setDelFlg("N");
                    vehicleLoanWarnData.setReqIpAddr(usd.getRemoteIP());
                    vehicleLoanWarnData.setLastModDate(new Date());
                    vehicleLoanWarnData.setLastModUser(usd.getPPCNo());
                    vehicleLoanWarnData.setQueue("BM");
                    vehicleLoanWarnService.saveWarn(vehicleLoanWarnData);
                }
                return messages.entrySet().stream();
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existingValue, newValue) -> {
                existingValue.addAll(newValue);
                return existingValue;
            }));


            if (!validationMessages.isEmpty()) {

                table = htmlTableGenerator.generateTable(validationMessages);

                if (validationMessages.get("High") != null && !validationMessages.get("High").isEmpty()) {
                    throw new ValidationException(ValidationError.COM001, table);
                }
            }
        }


        repository.resetLoanFlg(Long.valueOf(slno));
        Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsRepository.findBySlnoAndDelFlg(applicant.getSlno(), "N");
        EligibilityDetails eligibilityDetails =null;
        if (eligibilityDetails_.isPresent()) {
            eligibilityDetails=eligibilityDetails_.get();
            eligibilityDetails.setEligibilityFlg("N");
            eligibilityDetails.setProceedFlag("N");
            eligibilityDetailsRepository.save(eligibilityDetails);
        }

        repo.save(vl);
        repository.saveApplicant(applicant);


//        JsonObject msg=new JsonObject();
//        msg.addProperty("warn",table);
//        msg.addProperty("dedupDt",dedupDt.toString());
        if (!table.isBlank())
            return new TabResponse("W", table.toString(), applicant.getApplicantId().toString());
        else
            return new TabResponse("S", "", applicant.getApplicantId().toString());

    }

    @Override
    public TabResponse fetchData(FormData fs){
        try {
            Long appid=Long.valueOf(fs.getAppid());
            ObjectMapper mapper = new ObjectMapper();
            VehicleLoanApplicant applicant = repository.getById(appid);
            String msg="";
            if(applicant.getBasicComplete()!=null && applicant.getBasicComplete().equals("Y")){
                VehicleLoanBasic vl = repo.findByAppId(appid);
                msg=mapper.writeValueAsString(vl);
            }
            if(applicant.getSibCustomer().equals("Y")){
                CustomerDetails customerDetails = customerDetailsService.findByAppId(appid);
                msg=mapper.writeValueAsString(customerDetails);
            }

            return new TabResponse("S", msg);
        }
        catch (Exception e){
            return new TabResponse("F", e.getMessage());
        }
    }


}
