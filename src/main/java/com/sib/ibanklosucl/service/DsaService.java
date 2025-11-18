package com.sib.ibanklosucl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.dto.experian.DKResponse;
import com.sib.ibanklosucl.dto.experian.ExperianRequest;
import com.sib.ibanklosucl.dto.experian.ExperianResponse;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.*;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.eligibility.EligibilityDetailsService;
import com.sib.ibanklosucl.service.integration.SMSEmailService;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
public class DsaService {
    @Autowired
    private VehicleLoanMasterService vlmasservice;
    @Autowired
    private VehicleLoanApplicantService vlapplicantsservice;
    @Autowired
    private VehicleLoanKycService vlkycservice;
    @Autowired
    private VehicleLoanBasicService vlbasicservice;
    @Autowired
    private VehicleLoanDetailsService vlloandetailsservive;
    @Autowired
    private VehicleDetailsService vlvehicledetailsservice;
    @Autowired
    private VLEmploymentService vlempservice;
    @Autowired
    private VLEmploymentempService vlemploymentempservice;
    @Autowired
    private VLEmploymentoccService vlemploymentoccservice;
    @Autowired
    private EligibilityDetailsService vleligibilityservice;
    @Autowired
    private VehicleLoanProgramService vlprogramservice;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private VLBREparamsRepository vlbreparamsrepository;

    @Autowired
    private VehicleLoanVehicleRepository vehiclerepo;

    @Autowired
    private VehicleLoanTatService vltatservice;

    @Autowired
    private VLDsaStatusViewRepository vldsastatusviewrepository;

    @Autowired
    private VLCreditService vlcreditservice;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ValidationRepository validationRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private VLBREservice vlbrEservice;
    @Autowired
    private VehicleEligiblityRepository vlEligiblityRepository;
    @Autowired
    private PincodeMasterRepository pincodeMasterRepository;
    @Autowired
    private FetchRepository fetchrepo;

    @Autowired
    private UserSessionData usd;

    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${experian.id}")
    private String experianID;

    @Value("${app.dev-mode:true}")
    private boolean devMode;
    @Autowired
    private SMSEmailService mailService;

    @Autowired
    private BpmService bpmService;
    public String getWorkItems(String userid) throws Exception {
        return "";
    }
    public String getMobNo(String wiNum,Long slno,String appid) throws Exception {
        return "";
    }
    public List<VLDsaStatusView> getAllVLDsaStatus(String cmuser) {
        return vldsastatusviewrepository.findByCmuser(cmuser);
    }
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    public String insertData(Map<String, String> incomeDetails, Map<String, String> vehicleDetails, Map<String, String> priceDetails, Map<String, String> loanDetails, Map<String, String> userDetails, Map<String, String> kycDetails, Map<String, String> creditDetails, Map<String, String> addressDetails, String reqIpAddr) throws Exception {

        String winum="";
        try{
            String mob_cntry_code="+91";
            // Long slno = ((BigDecimal) entityManager.createNativeQuery("SELECT admin.VLMAS_SEQ.NEXTVAL FROM dual").getSingleResult()).longValue();
            String wiNum = (String) entityManager.createNativeQuery("SELECT ibanklosucl.GENERATE_WI_NUM('R') FROM dual").getSingleResult();
            String solid = userDetails.get("solid");
            winum = wiNum;
            log.info(" insertData  : wiNum  {}  ",  wiNum);
            log.info(" incomeDetails  : {}    ~ wiNum  {}  ",incomeDetails.toString(),  wiNum);
            log.info(" vehicleDetails  : {}    ~ wiNum  {}  ",vehicleDetails.toString(),  wiNum);
            log.info(" priceDetails  : {}    ~ wiNum  {}  ",priceDetails.toString(),  wiNum);
            log.info(" loanDetails  : {}    ~ wiNum  {}  ",loanDetails.toString(),  wiNum);
            log.info(" kycDetails  : {}    ~ wiNum  {}  ",kycDetails.toString(),  wiNum);
            log.info(" userDetails  : {}    ~ wiNum  {}  ",userDetails.toString(),  wiNum);
            log.info(" creditDetails  : {}    ~ wiNum  {}  ",creditDetails.toString(),  wiNum);
            log.info(" addressDetails  : {}    ~ wiNum  {}  ",addressDetails.toString(),  wiNum);
            VehicleLoanMaster vlmas = new VehicleLoanMaster();
            // vlmas.setSlno(slno);
            vlmas.setWiNum(wiNum);
            vlmas.setReqIpAddr(reqIpAddr);
            vlmas.setRiRcreDate(new Date());
            vlmas.setChannel(userDetails.get("channel")!=null?userDetails.get("channel"):"DSA");
            vlmas.setQueue("BM");
            vlmas.setQueueDate(new Date());
            vlmas.setCustName(kycDetails.get("aadharname"));
            vlmas.setCmUser(userDetails.get("Mobile"));
            vlmas.setCmDate(new Date());
            vlmas.setActiveFlg("Y");
            //vlmas.setSanDate(new Date());
//            if(vlmas.getSaninitDate()==null){
//                vlmas.setSaninitDate(new Date());
//            }
            vlmas.setStatus("BRENTPEND");
            vlmas.setSolId(solid);
            vlmas.setHomeSol(solid);
            vlmas.setNumCoapplicants(kycDetails.get("noofcoapplicants"));
            vlmas.setCurrentTab("A-1");
            vlmasservice.saveLoan(vlmas);
            // Insert into vlapplicants
            VehicleLoanApplicant vlapplicants = new VehicleLoanApplicant();
            vlapplicants.setApplicantType("A");
            vlapplicants.setDelFlg("N");
            vlapplicants.setLastModDate(new Date());
            vlapplicants.setLastModUser(userDetails.get("Mobile"));
            vlapplicants.setHomeSol(solid);
            vlapplicants.setBpmFolderName("APPLICANT");
            vlapplicants.setApplRcreDate(new Date());
            vlapplicants.setApplRcreUser(userDetails.get("Mobile"));
            //   vlapplicants.setApplicantId(((BigDecimal) entityManager.createNativeQuery("SELECT admin.VLAPPLICANTS_SEQ.NEXTVAL FROM dual").getSingleResult()).longValue());
            vlapplicants.setWiNum(vlmas.getWiNum());
            vlapplicants.setSlno(vlmas.getSlno());
            vlapplicants.setReqIpAddr(reqIpAddr);
            vlapplicants.setResidentFlg("R");
            vlapplicants.setSibCustomer("N");
            vlapplicants.setApplName(kycDetails.get("aadharname"));
            vlapplicants.setApplDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("pandob")));

            vlapplicantsservice.saveApplicant(vlapplicants);
            /********* CO Applicant ************/
            if(Integer.parseInt(kycDetails.get("noofcoapplicants"))>0){
                if(kycDetails.containsKey("co1_aadharname")){
                    VehicleLoanApplicant vlapplicants1 = new VehicleLoanApplicant();
                    vlapplicants1.setApplicantType("C");
                    vlapplicants1.setDelFlg("N");
                    vlapplicants1.setLastModDate(new Date());
                    vlapplicants1.setLastModUser(userDetails.get("Mobile"));
                    vlapplicants1.setHomeSol(solid);
                    vlapplicants1.setBpmFolderName("CO_APPLICANT_1");
                    vlapplicants1.setApplRcreDate(new Date());
                    vlapplicants1.setApplRcreUser(userDetails.get("Mobile"));
                    //   vlapplicants.setApplicantId(((BigDecimal) entityManager.createNativeQuery("SELECT admin.VLAPPLICANTS_SEQ.NEXTVAL FROM dual").getSingleResult()).longValue());
                    vlapplicants1.setWiNum(vlmas.getWiNum());
                    vlapplicants1.setSlno(vlmas.getSlno());
                    vlapplicants1.setReqIpAddr(reqIpAddr);
                    vlapplicants1.setResidentFlg("R");
                    vlapplicants1.setSibCustomer("N");
                    vlapplicants1.setApplName(kycDetails.get("co1_aadharname"));
                    vlapplicants1.setApplDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("co1_pandob")));

                    vlapplicantsservice.saveApplicant(vlapplicants1);

                    VehicleLoanKyc vlkyc1 = new VehicleLoanKyc();
                    vlkyc1.setWiNum(vlmas.getWiNum());
                    vlkyc1.setSlno(vlmas.getSlno());
                    vlkyc1.setApplicantId(vlapplicants1.getApplicantId());
                    vlkyc1.setReqIpAddr(reqIpAddr);
                    vlkyc1.setPanNo(kycDetails.get("co1_panno"));
                    vlkyc1.setPanDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("co1_pandob")));
                    vlkyc1.setPanName(kycDetails.get("co1_panname"));
                    vlkyc1.setPanDobNsdlValid("Y");
                    vlkyc1.setAadharRefNum(kycDetails.get("co1_aadharrefno"));
                    vlkyc1.setAadharName(kycDetails.get("co1_aadharname"));
                    vlkyc1.setAadharYob(kycDetails.get("co1_aadharyob"));
                    vlkyc1.setCmUser(userDetails.get("Mobile"));
                    vlkyc1.setCmDate(new Date());
                    vlkyc1.setAadharMode("M");
                    vlkyc1.setPanFilenameBpm("PAN");
                    vlkyc1.setHomeSol(solid);
                    vlkyc1.setDelFlg("N");
                    if(kycDetails.containsKey("co1_aadharfile")){
                        vlkyc1.setAadharimg(kycDetails.get("co1_aadharfile"));
                        vlkyc1.setAadharext(kycDetails.get("co1_aadharext"));
                    }
                    if(kycDetails.containsKey("co1_panfile")){
                        vlkyc1.setPanimg(kycDetails.get("co1_panfile"));
                        vlkyc1.setPanext(kycDetails.get("co1_panext"));
                    }
                    if(kycDetails.containsKey("co1_passportfile")){
                        vlkyc1.setPassportimg(kycDetails.get("co1_passportfile"));
                        vlkyc1.setPassportext(kycDetails.get("co1_passportext"));
                    }
                    if(kycDetails.containsKey("co1_visafile")){
                        vlkyc1.setVisaimg(kycDetails.get("co1_visafile"));
                        vlkyc1.setVisaext(kycDetails.get("co1_visaext"));
                    }
                    if(kycDetails.containsKey("co1_custphotofile")){
                        vlkyc1.setPhoto(kycDetails.get("co1_custphotofile"));
                        vlkyc1.setPhotoext(kycDetails.get("co1_customerphotoext"));
                    }
                    if(kycDetails.containsKey("co1_consentfile")) {
                        vlkyc1.setConsentimg(kycDetails.get("co1_consentfile"));
                        vlkyc1.setConsentimgext(kycDetails.get("co1_consentext"));
                    }
                    if(kycDetails.containsKey("co1_originalseenfile")) {
                        vlkyc1.setOriginalSeenCertificate(kycDetails.get("co1_originalseenfile"));
                        vlkyc1.setOriginalSeenCertificateExt(kycDetails.get("co1_originalseenext"));
                    }

                    if(kycDetails.containsKey("co1_consenttype")) {
                        vlkyc1.setConsentType(kycDetails.get("co1_consenttype"));
                        if(kycDetails.get("co1_consenttype").equals("digital")){
                            if(kycDetails.containsKey("co1_signature")) {
                                String extension1 = extractFileExtension(kycDetails.get("co1_signature"));
                                String base64Data1 = extractBase64Data(kycDetails.get("co1_signature"));
                                vlkyc1.setCustSig(base64Data1);
                                vlkyc1.setCustSigExt(extension1);
                            }
                            if(kycDetails.containsKey("co1_refslno")) {
                                vlkyc1.setDigitalRefSlno(kycDetails.get("co1_refslno"));
                            }
                        }
                    }

                    vlkycservice.save(vlkyc1);

                    VehicleLoanBasic vlbasic1 = new VehicleLoanBasic();
                    vlbasic1.setApplicantId(vlapplicants1.getApplicantId());
                    vlbasic1.setApplicantName(vlapplicants1.getApplName());
                    vlbasic1.setSlno(vlmas.getSlno());
                    vlbasic1.setApplicantDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("co1_pandob")));
                    vlbasic1.setCmuser(userDetails.get("Mobile"));
                    vlbasic1.setCmdate(new Date());
                    vlbasic1.setMobileNo(kycDetails.get("co1_mobile"));
                    vlbasic1.setReqIpAddr(reqIpAddr);
                    vlbasic1.setWiNum(vlmas.getWiNum());
                    vlbasic1.setMobileCntryCode(mob_cntry_code);
                    vlbasic1.setDelFlg("N");
                    vlbasic1.setHomeSol(solid);
                    vlbasic1.setAddr1(addressDetails.get("co1_addr1"));
                    vlbasic1.setAddr2(addressDetails.get("co1_addr2"));
                    vlbasic1.setCity(addressDetails.get("co1_city"));
                    vlbasic1.setCitydesc(addressDetails.get("co1_citydesc"));
                    vlbasic1.setState(addressDetails.get("co1_state"));
                    vlbasic1.setStatedesc(addressDetails.get("co1_statedesc"));
                    vlbasic1.setCountry(addressDetails.get("co1_country"));
                    vlbasic1.setCountrydesc(addressDetails.get("co1_countrydesc"));
                    vlbasic1.setPin(addressDetails.get("co1_pin"));
                    vlbasicservice.save(vlbasic1);

                    VLEmployment vlemployment1 = new VLEmployment();
                    vlemployment1.setWiNum(vlmas.getWiNum());
                    vlemployment1.setSlno(vlmas.getSlno());
                    vlemployment1.setApplicantId(vlapplicants1.getApplicantId());
                    vlemployment1.setDelFlg("N");
                    vlemployment1.setEmployment_type(incomeDetails.get("co1_emptype"));
                    vlemployment1.setReqIpAddr(reqIpAddr);
                    vlemployment1.setCmuser(userDetails.get("Mobile"));
                    vlemployment1.setCmdate(new Date());
                    vlemployment1.setHomeSol(solid);
                    vlempservice.save(vlemployment1);
                    if(vlemployment1.getEmployment_type().equalsIgnoreCase("SALARIED")) {
                        VLEmploymentemp vlemploymentemp1 = new VLEmploymentemp();
                        vlemploymentemp1.setDelFlg("N");
                        vlemploymentemp1.setCurrentEmployer("Y");
                        vlemploymentemp1.setApplicantId(vlapplicants1.getApplicantId());
                        vlemploymentemp1.setEmployerName(incomeDetails.get("co1_companyname"));
                        vlemploymentemp1.setCmuser(userDetails.get("Mobile"));
                        vlemploymentemp1.setCmdate(new Date());
                        vlemploymentemp1.setHomeSol(solid);
                        vlemploymentemp1.setSlno(vlmas.getSlno());
                        vlemploymentemp1.setWiNum(vlmas.getWiNum());
                        vlemploymentemp1.setVlempkey(vlemployment1);
                        vlemploymentempservice.save(vlemploymentemp1);
                    }else{
                        VLEmploymentocc vlemploymentocc1 = new VLEmploymentocc();
                        vlemploymentocc1.setDelFlg("N");
                        vlemploymentocc1.setApplicantId(vlapplicants1.getApplicantId());
                        vlemploymentocc1.setEmployerName(incomeDetails.get("co1_companyname"));
                        vlemploymentocc1.setCmuser(userDetails.get("Mobile"));
                        vlemploymentocc1.setCmdate(new Date());
                        vlemploymentocc1.setHomeSol(solid);
                        vlemploymentocc1.setSlno(vlmas.getSlno());
                        vlemploymentocc1.setWiNum(vlmas.getWiNum());
                        vlemploymentocc1.setVlEmployment(vlemployment1);
                        vlemploymentoccservice.save(vlemploymentocc1);
                    }
                    VehicleLoanProgram vlprogram1  = new VehicleLoanProgram();
                    vlprogram1.setHomeSol(solid);
                    vlprogram1.setAvgSal(BigDecimal.valueOf(Long.parseLong(incomeDetails.get("co1_income"))));
                    vlprogram1.setDelFlg("N");
                    vlprogram1.setDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("co1_pandob")));
                    vlprogram1.setApplicantId(vlapplicants1.getApplicantId());
                    vlprogram1.setCmDate(new Date());
                    vlprogram1.setCmUser(userDetails.get("channel")!=null?userDetails.get("channel"):"DSA");
                    vlprogram1.setIncomeConsidered("Y");
                    vlprogram1.setLoanProgram("INCOME");
                    vlprogram1.setPan(kycDetails.get("co1_panno"));
                    vlprogram1.setWiNum(vlmas.getWiNum());
                    vlprogram1.setResidentType("R");
                    vlprogram1.setReqIpAddr(reqIpAddr);
                    vlprogram1.setSlNo(vlmas.getSlno());
                    vlprogram1.setItrFlg("Y");
                    vlprogram1.setItrMonths("2");
                    vlprogramservice.insertVehicleLoanProgram(vlprogram1);
                    VLCredit vlcredit1 = new VLCredit();
                    vlcredit1.setActiveFlg("Y");
                    vlcredit1.setApplicantId(vlapplicants1.getApplicantId());
                    vlcredit1.setBureauScore(Long.valueOf(creditDetails.get("co1_bureauscore")));
                    vlcredit1.setDelFlg("N");
                    vlcredit1.setHomeSol(solid);
                    vlcredit1.setReqIpAddr(reqIpAddr);
                    vlcredit1.setSlno(vlmas.getSlno());
                    vlcredit1.setTotObligations(Math.round(Double.valueOf(creditDetails.get("co1_obligation"))));
                    vlcredit1.setWiNum(vlmas.getWiNum());
                    vlcreditservice.save(vlcredit1);
                }
                if(kycDetails.containsKey("co2_aadharname")){
                    VehicleLoanApplicant vlapplicants2 = new VehicleLoanApplicant();
                    vlapplicants2.setApplicantType("C");
                    vlapplicants2.setDelFlg("N");
                    vlapplicants2.setLastModDate(new Date());
                    vlapplicants2.setLastModUser(userDetails.get("Mobile"));
                    vlapplicants2.setHomeSol(solid);
                    vlapplicants2.setBpmFolderName("CO_APPLICANT_2");
                    vlapplicants2.setApplRcreDate(new Date());
                    vlapplicants2.setApplRcreUser(userDetails.get("Mobile"));
                    //   vlapplicants.setApplicantId(((BigDecimal) entityManager.createNativeQuery("SELECT admin.VLAPPLICANTS_SEQ.NEXTVAL FROM dual").getSingleResult()).longValue());
                    vlapplicants2.setWiNum(vlmas.getWiNum());
                    vlapplicants2.setSlno(vlmas.getSlno());
                    vlapplicants2.setReqIpAddr(reqIpAddr);
                    vlapplicants2.setResidentFlg("R");
                    vlapplicants2.setSibCustomer("N");
                    vlapplicants2.setApplName(kycDetails.get("co2_aadharname"));
                    vlapplicants2.setApplDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("co2_pandob")));

                    vlapplicantsservice.saveApplicant(vlapplicants2);

                    VehicleLoanKyc vlkyc2 = new VehicleLoanKyc();
                    vlkyc2.setWiNum(vlmas.getWiNum());
                    vlkyc2.setSlno(vlmas.getSlno());
                    vlkyc2.setApplicantId(vlapplicants2.getApplicantId());
                    vlkyc2.setReqIpAddr(reqIpAddr);
                    vlkyc2.setPanNo(kycDetails.get("co2_panno"));
                    vlkyc2.setPanDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("co2_pandob")));
                    vlkyc2.setPanName(kycDetails.get("co2_panname"));
                    vlkyc2.setPanDobNsdlValid("Y");
                    vlkyc2.setAadharRefNum(kycDetails.get("co2_aadharrefno"));
                    vlkyc2.setAadharName(kycDetails.get("co2_aadharname"));
                    vlkyc2.setAadharYob(kycDetails.get("co2_aadharyob"));
                    vlkyc2.setCmUser(userDetails.get("Mobile"));
                    vlkyc2.setCmDate(new Date());
                    vlkyc2.setAadharMode("M");
                    vlkyc2.setPanFilenameBpm("PAN");
                    vlkyc2.setHomeSol(solid);
                    vlkyc2.setDelFlg("N");
                    if(kycDetails.containsKey("co2_aadharfile")){
                        vlkyc2.setAadharimg(kycDetails.get("co2_aadharfile"));
                        vlkyc2.setAadharext(kycDetails.get("co2_aadharext"));
                    }
                    if(kycDetails.containsKey("co2_panfile")){
                        vlkyc2.setPanimg(kycDetails.get("co2_panfile"));
                        vlkyc2.setPanext(kycDetails.get("co2_panext"));
                    }
                    if(kycDetails.containsKey("co2_passportfile")){
                        vlkyc2.setPassportimg(kycDetails.get("co2_passportfile"));
                        vlkyc2.setPassportext(kycDetails.get("co2_passportext"));
                    }
                    if(kycDetails.containsKey("co2_visafile")){
                        vlkyc2.setVisaimg(kycDetails.get("co2_visafile"));
                        vlkyc2.setVisaext(kycDetails.get("co2_visaext"));
                    }
                    if(kycDetails.containsKey("co2_custphotofile")){
                        vlkyc2.setPhoto(kycDetails.get("co2_custphotofile"));
                        vlkyc2.setPhotoext(kycDetails.get("co2_customerphotoext"));
                    }
                    if(kycDetails.containsKey("co2_consentfile")) {
                        vlkyc2.setConsentimg(kycDetails.get("co2_consentfile"));
                        vlkyc2.setConsentimgext(kycDetails.get("co2_consentext"));
                    }
                    if(kycDetails.containsKey("co2_originalseenfile")) {
                        vlkyc2.setOriginalSeenCertificate(kycDetails.get("co2_originalseenfile"));
                        vlkyc2.setOriginalSeenCertificateExt(kycDetails.get("co2_originalseenext"));
                    }

                    if(kycDetails.containsKey("co2_consenttype")) {
                        vlkyc2.setConsentType(kycDetails.get("co2_consenttype"));
                        if(kycDetails.get("co2_consenttype").equals("digital")){
                            if(kycDetails.containsKey("co2_signature")) {
                                String extension1 = extractFileExtension(kycDetails.get("co2_signature"));
                                String base64Data1 = extractBase64Data(kycDetails.get("co2_signature"));
                                vlkyc2.setCustSig(base64Data1);
                                vlkyc2.setCustSigExt(extension1);
                            }
                            if(kycDetails.containsKey("co2_refslno")) {
                                vlkyc2.setDigitalRefSlno(kycDetails.get("co2_refslno"));
                            }
                        }
                    }
                    vlkycservice.save(vlkyc2);

                    VehicleLoanBasic vlbasic2 = new VehicleLoanBasic();
                    vlbasic2.setApplicantId(vlapplicants2.getApplicantId());
                    vlbasic2.setApplicantName(vlapplicants2.getApplName());
                    vlbasic2.setSlno(vlmas.getSlno());
                    vlbasic2.setApplicantDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("co2_pandob")));
                    vlbasic2.setCmuser(userDetails.get("Mobile"));
                    vlbasic2.setCmdate(new Date());
                    vlbasic2.setMobileNo(kycDetails.get("co2_mobile"));
                    vlbasic2.setReqIpAddr(reqIpAddr);
                    vlbasic2.setWiNum(vlmas.getWiNum());
                    vlbasic2.setMobileCntryCode(mob_cntry_code);
                    vlbasic2.setDelFlg("N");
                    vlbasic2.setHomeSol(solid);
                    vlbasic2.setAddr1(addressDetails.get("co2_addr1"));
                    vlbasic2.setAddr2(addressDetails.get("co2_addr2"));
                    vlbasic2.setCity(addressDetails.get("co2_city"));
                    vlbasic2.setCitydesc(addressDetails.get("co2_citydesc"));
                    vlbasic2.setState(addressDetails.get("co2_state"));
                    vlbasic2.setStatedesc(addressDetails.get("co2_statedesc"));
                    vlbasic2.setCountry(addressDetails.get("co2_country"));
                    vlbasic2.setCountrydesc(addressDetails.get("co2_countrydesc"));
                    vlbasic2.setPin(addressDetails.get("co2_pin"));
                    vlbasicservice.save(vlbasic2);

                    VLEmployment vlemployment2 = new VLEmployment();
                    vlemployment2.setWiNum(vlmas.getWiNum());
                    vlemployment2.setSlno(vlmas.getSlno());
                    vlemployment2.setApplicantId(vlapplicants2.getApplicantId());
                    vlemployment2.setDelFlg("N");
                    vlemployment2.setEmployment_type(incomeDetails.get("co2_emptype"));
                    vlemployment2.setReqIpAddr(reqIpAddr);
                    vlemployment2.setCmuser(userDetails.get("Mobile"));
                    vlemployment2.setCmdate(new Date());
                    vlemployment2.setHomeSol(solid);
                    vlempservice.save(vlemployment2);
                    if(vlemployment2.getEmployment_type().equalsIgnoreCase("SALARIED")) {
                        VLEmploymentemp vlemploymentemp2 = new VLEmploymentemp();
                        vlemploymentemp2.setDelFlg("N");
                        vlemploymentemp2.setCurrentEmployer("Y");
                        vlemploymentemp2.setApplicantId(vlapplicants2.getApplicantId());
                        vlemploymentemp2.setEmployerName(incomeDetails.get("co2_companyname"));
                        vlemploymentemp2.setCmuser(userDetails.get("Mobile"));
                        vlemploymentemp2.setCmdate(new Date());
                        vlemploymentemp2.setHomeSol(solid);
                        vlemploymentemp2.setSlno(vlmas.getSlno());
                        vlemploymentemp2.setWiNum(vlmas.getWiNum());
                        vlemploymentemp2.setVlempkey(vlemployment2);
                        vlemploymentempservice.save(vlemploymentemp2);
                    }else{
                        VLEmploymentocc vlemploymentocc2 = new VLEmploymentocc();
                        vlemploymentocc2.setDelFlg("N");
                        vlemploymentocc2.setApplicantId(vlapplicants2.getApplicantId());
                        vlemploymentocc2.setEmployerName(incomeDetails.get("co2_companyname"));
                        vlemploymentocc2.setCmuser(userDetails.get("Mobile"));
                        vlemploymentocc2.setCmdate(new Date());
                        vlemploymentocc2.setHomeSol(solid);
                        vlemploymentocc2.setSlno(vlmas.getSlno());
                        vlemploymentocc2.setWiNum(vlmas.getWiNum());
                        vlemploymentocc2.setVlEmployment(vlemployment2);
                        vlemploymentoccservice.save(vlemploymentocc2);
                    }
                    VehicleLoanProgram vlprogram2  = new VehicleLoanProgram();
                    vlprogram2.setHomeSol(solid);
                    vlprogram2.setAvgSal(BigDecimal.valueOf(Long.parseLong(incomeDetails.get("co2_income"))));
                    vlprogram2.setDelFlg("N");
                    vlprogram2.setDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("co2_pandob")));
                    vlprogram2.setApplicantId(vlapplicants2.getApplicantId());
                    vlprogram2.setCmDate(new Date());
                    vlprogram2.setCmUser(userDetails.get("channel")!=null?userDetails.get("channel"):"DSA");
                    vlprogram2.setIncomeConsidered("Y");
                    vlprogram2.setLoanProgram("INCOME");
                    vlprogram2.setPan(kycDetails.get("co2_panno"));
                    vlprogram2.setWiNum(vlmas.getWiNum());
                    vlprogram2.setResidentType("R");
                    vlprogram2.setReqIpAddr(reqIpAddr);
                    vlprogram2.setSlNo(vlmas.getSlno());
                    vlprogram2.setItrFlg("Y");
                    vlprogram2.setItrMonths("2");
                    vlprogramservice.insertVehicleLoanProgram(vlprogram2);
                    VLCredit vlcredit2 = new VLCredit();
                    vlcredit2.setActiveFlg("Y");
                    vlcredit2.setApplicantId(vlapplicants2.getApplicantId());
                    vlcredit2.setBureauScore(Long.valueOf(creditDetails.get("co2_bureauscore")));
                    vlcredit2.setDelFlg("N");
                    vlcredit2.setHomeSol(solid);
                    vlcredit2.setReqIpAddr(reqIpAddr);
                    vlcredit2.setSlno(vlmas.getSlno());
                    vlcredit2.setTotObligations(Math.round(Double.valueOf(creditDetails.get("co2_obligation"))));
                    vlcredit2.setWiNum(vlmas.getWiNum());
                    vlcreditservice.save(vlcredit2);
                }
            }
            /*********************************/
            VehicleLoanKyc vlkyc = new VehicleLoanKyc();
            vlkyc.setWiNum(vlmas.getWiNum());
            vlkyc.setSlno(vlmas.getSlno());
            vlkyc.setApplicantId(vlapplicants.getApplicantId());
            vlkyc.setReqIpAddr(reqIpAddr);
            vlkyc.setPanNo(kycDetails.get("panno"));
            vlkyc.setPanDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("pandob")));
            vlkyc.setPanName(kycDetails.get("panname"));
            vlkyc.setPanDobNsdlValid("Y");
            vlkyc.setAadharRefNum(kycDetails.get("aadharrefno"));
            vlkyc.setAadharName(kycDetails.get("aadharname"));
            vlkyc.setAadharYob(kycDetails.get("aadharyob"));
            vlkyc.setCmUser(userDetails.get("Mobile"));
            vlkyc.setCmDate(new Date());
            vlkyc.setAadharMode("M");
            vlkyc.setPanFilenameBpm("PAN");
            vlkyc.setHomeSol(solid);
            vlkyc.setDelFlg("N");
            if(kycDetails.containsKey("aadharfile")){
                vlkyc.setAadharimg(kycDetails.get("aadharfile"));
                vlkyc.setAadharext(kycDetails.get("aadharext"));
            }
            if(kycDetails.containsKey("panfile")){
                vlkyc.setPanimg(kycDetails.get("panfile"));
                vlkyc.setPanext(kycDetails.get("panext"));
            }
            if(kycDetails.containsKey("passportfile")){
                vlkyc.setPassportimg(kycDetails.get("passportfile"));
                vlkyc.setPassportext(kycDetails.get("passportext"));
            }
            if(kycDetails.containsKey("visafile")){
                vlkyc.setVisaimg(kycDetails.get("visafile"));
                vlkyc.setVisaext(kycDetails.get("visaext"));
            }
            if(kycDetails.containsKey("custphotofile")){
                vlkyc.setPhoto(kycDetails.get("custphotofile"));
                vlkyc.setPhotoext(kycDetails.get("customerphotoext"));
            }
            if(kycDetails.containsKey("consentfile")) {
                vlkyc.setConsentimg(kycDetails.get("consentfile"));
                vlkyc.setConsentimgext(kycDetails.get("consentext"));
            }
            if(kycDetails.containsKey("originalseenfile")) {
                vlkyc.setOriginalSeenCertificate(kycDetails.get("originalseenfile"));
                vlkyc.setOriginalSeenCertificateExt(kycDetails.get("originalseenext"));
            }

            if(kycDetails.containsKey("consenttype")) {
                vlkyc.setConsentType(kycDetails.get("consenttype"));
                if(kycDetails.get("consenttype").equals("digital")){
                    if(kycDetails.containsKey("signature")) {
                        String extension1 = extractFileExtension(kycDetails.get("signature"));
                        String base64Data1 = extractBase64Data(kycDetails.get("signature"));
                        vlkyc.setCustSig(base64Data1);
                        vlkyc.setCustSigExt(extension1);
                    }
                    if(kycDetails.containsKey("refslno")) {
                        vlkyc.setDigitalRefSlno(kycDetails.get("refslno"));
                    }
                }
            }
            vlkycservice.save(vlkyc);
            VehicleLoanBasic vlbasic = new VehicleLoanBasic();
            vlbasic.setApplicantId(vlapplicants.getApplicantId());
            vlbasic.setApplicantName(vlapplicants.getApplName());
            vlbasic.setSlno(vlmas.getSlno());
            vlbasic.setApplicantDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("pandob")));
            vlbasic.setCmuser(userDetails.get("Mobile"));
            vlbasic.setCmdate(new Date());
            vlbasic.setMobileNo(kycDetails.get("mobile"));
            vlbasic.setReqIpAddr(reqIpAddr);
            vlbasic.setWiNum(vlmas.getWiNum());
            vlbasic.setMobileCntryCode(mob_cntry_code);
            vlbasic.setDelFlg("N");
            vlbasic.setHomeSol(solid);
            vlbasic.setAddr1(addressDetails.get("addr1"));
            vlbasic.setAddr2(addressDetails.get("addr2"));
            vlbasic.setCity(addressDetails.get("city"));
            vlbasic.setCitydesc(addressDetails.get("citydesc"));
            vlbasic.setState(addressDetails.get("state"));
            vlbasic.setStatedesc(addressDetails.get("statedesc"));
            vlbasic.setCountry(addressDetails.get("country"));
            vlbasic.setCountrydesc(addressDetails.get("countrydesc"));
            vlbasic.setPin(addressDetails.get("pin"));
            vlbasicservice.save(vlbasic);
            VehicleLoanDetails vlloandetails = new VehicleLoanDetails();
            vlloandetails.setWiNum(vlmas.getWiNum());
            vlloandetails.setSlno(vlmas.getSlno());
            vlloandetails.setApplicantId(vlapplicants.getApplicantId());
            vlloandetails.setFoirType("Y");
            vlloandetails.setRoiType("FIXED");
            vlloandetails.setReqIpAddr(reqIpAddr);
            vlloandetails.setTenor(Integer.parseInt(loanDetails.get("tenure")));
            vlloandetails.setLoanAmt(new BigDecimal(loanDetails.get("eligibleamount")));
            if(priceDetails.get("ismodified").equals("Y"))
                vlloandetails.setVehicleAmt(new BigDecimal(priceDetails.get("modifiedonroad")));
            else
                vlloandetails.setVehicleAmt(new BigDecimal(priceDetails.get("onroad")));
            vlloandetails.setCmuser(userDetails.get("Mobile"));
            vlloandetails.setCmdate(new Date());
            vlloandetails.setHomeSol(solid);
            vlloandetails.setDelFlg("N");
            vlloandetailsservive.save(vlloandetails);
            VehicleLoanVehicle vlvehicledetails = new VehicleLoanVehicle();
            vlvehicledetails.setWiNum(vlmas.getWiNum());
            vlvehicledetails.setSlno(vlmas.getSlno());
            vlvehicledetails.setApplicantId(vlapplicants.getApplicantId());
            vlvehicledetails.setDelFlg("N");
            vlvehicledetails.setDealerCityId(vehicleDetails.get("city"));
            vlvehicledetails.setDealerCityName(vehicleDetails.get("cityname"));
            vlvehicledetails.setDealerState(vehicleDetails.get("state"));
            if(priceDetails.get("ismodified").equals("Y"))
                vlvehicledetails.setExshowroomPrice(priceDetails.get("modifiedshowroom"));
            else
                vlvehicledetails.setExshowroomPrice(priceDetails.get("showroom"));
            if(priceDetails.get("ismodified").equals("Y"))
                vlvehicledetails.setInsurancePrice(priceDetails.get("modifiedinsurance"));
            else
                vlvehicledetails.setInsurancePrice(priceDetails.get("insurance"));
            vlvehicledetails.setMakeId(vehicleDetails.get("make"));
            vlvehicledetails.setMakeName(vehicleDetails.get("makename"));
            vlvehicledetails.setVariantId(vehicleDetails.get("variant"));
            vlvehicledetails.setVariantName(vehicleDetails.get("variantname"));
            vlvehicledetails.setModelId(vehicleDetails.get("model"));
            vlvehicledetails.setModelName(vehicleDetails.get("modelname"));
            if(priceDetails.get("ismodified").equals("Y"))
                vlvehicledetails.setRtoPrice(priceDetails.get("modifiedrto"));
            else
                vlvehicledetails.setRtoPrice(priceDetails.get("rto"));
            if(priceDetails.get("ismodified").equals("Y"))
                vlvehicledetails.setOnroadPrice(priceDetails.get("modifiedonroad"));
            else
                vlvehicledetails.setOnroadPrice(priceDetails.get("onroad"));
            if(priceDetails.get("ismodified").equals("Y"))
                vlvehicledetails.setOtherPrice(priceDetails.get("modifiedother"));
            else
                vlvehicledetails.setOtherPrice(priceDetails.get("otherprice"));

            vlvehicledetails.setExtendedWarranty("0");
            vlvehicledetails.setReqIpAddr(reqIpAddr);
            vlvehicledetails.setCmuser(userDetails.get("Mobile"));
            vlvehicledetails.setCmdate(new Date());
            vlvehicledetails.setHomeSol(solid);
            vlvehicledetails.setDealerCode(vehicleDetails.get("dealercode"));
            vlvehicledetails.setDealerName(vehicleDetails.get("dealername"));
            vlvehicledetails.setDealerSubCode(vehicleDetails.get("dealersubcode"));
            vlvehicledetails.setDstCode(vehicleDetails.get("dstcode"));
            vlvehicledetails.setDsaCode(vehicleDetails.get("dsacode"));
            vlvehicledetails.setBodyType(vehicleDetails.get("bodytype"));
            //vlvehicledetails.setDs(vehicleDetails.get("dsasubcode"));
            vlvehicledetailsservice.checkAndInsertOrUpdate(vlvehicledetails);
            VLEmployment vlemployment = new VLEmployment();
            vlemployment.setWiNum(vlmas.getWiNum());
            vlemployment.setSlno(vlmas.getSlno());
            vlemployment.setApplicantId(vlapplicants.getApplicantId());
            vlemployment.setDelFlg("N");
            vlemployment.setRetirement_age("");
            vlemployment.setEmployment_type(incomeDetails.get("emptype"));
            vlemployment.setReqIpAddr(reqIpAddr);
            vlemployment.setCmuser(userDetails.get("Mobile"));
            vlemployment.setCmdate(new Date());
            vlemployment.setHomeSol(solid);
            vlempservice.save(vlemployment);
            if(vlemployment.getEmployment_type().equalsIgnoreCase("SALARIED")) {
                VLEmploymentemp vlemploymentemp = new VLEmploymentemp();
                vlemploymentemp.setDelFlg("N");
                vlemploymentemp.setCurrentEmployer("Y");
                vlemploymentemp.setApplicantId(vlapplicants.getApplicantId());
                vlemploymentemp.setEmployerName(incomeDetails.get("companyname"));
                vlemploymentemp.setCmuser(userDetails.get("Mobile"));
                vlemploymentemp.setCmdate(new Date());
                vlemploymentemp.setHomeSol(solid);
                vlemploymentemp.setSlno(vlmas.getSlno());
                vlemploymentemp.setWiNum(vlmas.getWiNum());
                vlemploymentemp.setVlempkey(vlemployment);
                vlemploymentempservice.save(vlemploymentemp);
            }else{
                VLEmploymentocc vlemploymentocc = new VLEmploymentocc();
                vlemploymentocc.setDelFlg("N");
                vlemploymentocc.setApplicantId(vlapplicants.getApplicantId());
                vlemploymentocc.setEmployerName(incomeDetails.get("companyname"));
                vlemploymentocc.setCmuser(userDetails.get("Mobile"));
                vlemploymentocc.setCmdate(new Date());
                vlemploymentocc.setHomeSol(solid);
                vlemploymentocc.setSlno(vlmas.getSlno());
                vlemploymentocc.setWiNum(vlmas.getWiNum());
                vlemploymentocc.setVlEmployment(vlemployment);
                vlemploymentoccservice.save(vlemploymentocc);
            }
            EligibilityDetails vleligibility = new EligibilityDetails();
            vleligibility.setWiNum(vlmas.getWiNum());
            vleligibility.setSlno(vlmas.getSlno());
            vleligibility.setApplicantId(vlapplicants.getApplicantId());
            vleligibility.setDelFlg("N");
            vleligibility.setReqIpAddr(reqIpAddr);
            if(priceDetails.get("ismodified").equals("Y"))
                vleligibility.setVehicleAmt(new BigDecimal(priceDetails.get("modifiedonroad")));
            else
                vleligibility.setVehicleAmt(new BigDecimal(priceDetails.get("onroad")));
            vleligibility.setLtvAmt(new BigDecimal(loanDetails.get("ltvamt")));
            vleligibility.setLoanAmt(new BigDecimal(loanDetails.get("requestedamount")));
            vleligibility.setEligibilityUser(userDetails.get("channel")!=null?userDetails.get("channel"):"DSA");
            vleligibility.setEligibleLoanAmt(new BigDecimal(loanDetails.get("eligibleamount")));
            vleligibility.setEligibilityDate(new Date());
            vleligibility.setProgramEligibleAmt(new BigDecimal(loanDetails.get("pgmeligibleamount")));
            vleligibility.setCardRate(new BigDecimal("10"));
            vleligibility.setCmuser(userDetails.get("Mobile"));
            vleligibility.setCmdate(new Date());
            vleligibility.setDelFlg("N");
            vleligibility.setLtvPer(new BigDecimal(loanDetails.get("ltvper")));
            vleligibility.setAmi(new BigDecimal(incomeDetails.get("income")));
            vleligibility.setFoirBalancePer(new BigDecimal(loanDetails.get("foir")));
            vleligibility.setObligation(new BigDecimal(loanDetails.get("obligation")));
            vleligibility.setEmiMax(new BigDecimal(loanDetails.get("emimax")));
            vleligibility.setEmi(new BigDecimal(loanDetails.get("emi")));
            vleligibility.setTenor(Integer.parseInt(loanDetails.get("tenure")));
            vleligibility.setHomeSol(solid);
            vleligibilityservice.DsaSave(vleligibility);
            VehicleLoanProgram vlprogram  = new VehicleLoanProgram();
            vlprogram.setHomeSol(solid);
            vlprogram.setAvgSal(BigDecimal.valueOf(Long.parseLong(incomeDetails.get("income"))));
            vlprogram.setDelFlg("N");
            vlprogram.setDob(new SimpleDateFormat("dd/MM/yyyy").parse(kycDetails.get("pandob")));
            vlprogram.setApplicantId(vlapplicants.getApplicantId());
            vlprogram.setCmDate(new Date());
            vlprogram.setCmUser(userDetails.get("channel")!=null?userDetails.get("channel"):"DSA");
            vlprogram.setIncomeConsidered("Y");
            vlprogram.setLoanProgram("INCOME");
            vlprogram.setPan(kycDetails.get("panno"));
            vlprogram.setWiNum(vlmas.getWiNum());
            vlprogram.setResidentType("R");
            vlprogram.setReqIpAddr(reqIpAddr);
            vlprogram.setSlNo(vlmas.getSlno());
            vlprogram.setItrFlg("Y");
            vlprogram.setItrMonths("2");
            vlprogramservice.insertVehicleLoanProgram(vlprogram);
            VLCredit vlcredit = new VLCredit();
            vlcredit.setActiveFlg("Y");
            vlcredit.setApplicantId(vlapplicants.getApplicantId());
            vlcredit.setBureauScore(Long.valueOf(creditDetails.get("bureauscore")));
            vlcredit.setDelFlg("N");
            vlcredit.setHomeSol(solid);
            vlcredit.setReqIpAddr(reqIpAddr);
            vlcredit.setSlno(vlmas.getSlno());
            vlcredit.setTotObligations(Math.round(Double.valueOf(creditDetails.get("obligation"))));
            vlcredit.setWiNum(vlmas.getWiNum());
            if(incomeDetails.containsKey("requestedamount")) {
                vlcredit.setExpLoanAmt(new BigDecimal(incomeDetails.get("requestedamount")));
            }
            if(incomeDetails.containsKey("requestedtenure")) {
                vlcredit.setExpTenure(new BigDecimal(loanDetails.get("requestedtenure")));
            }
            vlcredit.setWiNum(vlmas.getWiNum());
            vlcreditservice.save(vlcredit);
            VehicleLoanTat vehicleLoanTat_ = new VehicleLoanTat();
            vehicleLoanTat_.setWiNum(vlmas.getWiNum());
            vehicleLoanTat_.setSlno(vlmas.getSlno());
            vehicleLoanTat_.setReqIpAddr(reqIpAddr);
            vehicleLoanTat_.setQueue("BM");
            vehicleLoanTat_.setQueueEntryUser(userDetails.get("channel")!=null?userDetails.get("channel"):"DSA");
            vehicleLoanTat_.setQueueEntryDate(new Date());
            vehicleLoanTat_.setDelFlg("N");
            vltatservice.saveTat(vehicleLoanTat_);
            log.info("success : winum ~ {}",winum);



            usd.setRemoteIP(reqIpAddr);

            try {
                String content = winum+ "-  has been submitted from "+userDetails.get("channel")!=null?userDetails.get("channel"):"DSA"+".\n";
                SMSEmailDTO emailDTO = new SMSEmailDTO();
                emailDTO.setSlno(Long.valueOf(vlmas.getSlno()));
                emailDTO.setWiNum(winum);
                emailDTO.setSentUser("system");
                emailDTO.setAlertId("DSA_WI_ALERT");
                emailDTO.setReqType("E");
                emailDTO.setEmailFrom("sibmailer@sib.bank.in");
                List<Map<String, String>> mailcc = fetchrepo.getRSMEmails(solid);
                String pipeSeparatedEmails="";
                if(mailcc != null && !mailcc.isEmpty()){
                    pipeSeparatedEmails = mailcc.stream().map(map -> map.get("email")).filter(Objects::nonNull).filter(email -> !email.isEmpty()).collect(Collectors.joining("|"));
                }
                emailDTO.setEmailCc(pipeSeparatedEmails);
                if (!devMode) {
                    emailDTO.setEmailTo(String.format("br%s@sib.bank.in",solid));
                } else {
                    emailDTO.setEmailTo("infobanksib@gmail.com");
                }
                emailDTO.setEmailBody(content);
                emailDTO.setCustName(vlapplicants.getApplName());
                emailDTO.setEmailSubject("Power Drive - Work Item : " + winum );
                Gson gson= new Gson();
                String emailreq =  gson.toJson(emailDTO);
                log.info("Email request : {}",emailreq);
                if (!devMode) {
                    ResponseDTO email_ = mailService.insertSMSEmail(emailDTO);
                    String emailresp = gson.toJson(email_);
                    log.info("Email response : {}", emailresp);
                }
                for(int i=1;i<=Integer.parseInt(kycDetails.get("noofcoapplicants"));i++){
                    bpmService.BpmParent(vlmas.getWiNum(), String.valueOf(vlmas.getSlno()), "CO_APPLICANT_"+i);
                }
            }catch (Exception e){
                log.info("Error Occurred : {}",e);
                e.printStackTrace();
            }




            return "success~"+winum+"~"+vlmas.getSlno();
        } catch (Exception e) {
            log.info("Exception :",e);
            e.printStackTrace();
            return "error~"+winum+"~";
        }

    }

    public static String extractFileExtension(String dataUri) {
        // Find the start of the MIME type (after "image/")
        int mimeTypeStartIndex = dataUri.indexOf("image/") + 6;

        // Find the end of the MIME type (before ";base64,")
        int mimeTypeEndIndex = dataUri.indexOf(";base64,");

        // Extract the MIME type (e.g., "png" or "jpeg")
        String mimeType = dataUri.substring(mimeTypeStartIndex, mimeTypeEndIndex);

        return mimeType;
    }

    public static String extractBase64Data(String dataUri) {
        // Find the start of the Base64 encoded data (after ";base64,")
        int base64StartIndex = dataUri.indexOf(";base64,") + 8;

        // Extract the Base64 encoded data
        String base64Data = dataUri.substring(base64StartIndex);

        return base64Data;
    }


    @Transactional
    public String uploadInvoice(String wino,String slno,String invoice,String invoiceext,String invoicedate,String invoiceprice,String invoicenumber,String mainappid,String consent,String osc,String co1appid,String co1consent,String co1osc,String co2appid,String co2consent,String co2osc,String consentext,String oscext,String co1consentext,String co1oscext,String co2consentext,String co2oscext) throws Exception {

        try{
            log.info("uploadInvoice : winum ~ {} slno ~ {} invoice ~ {} ",wino,slno,invoice);
            if(invoicedate!=null && !invoicedate.equals("") && invoice!=null && !invoice.equals("") && invoiceprice!=null && !invoiceprice.equals("") && invoicenumber!=null && !invoicenumber.equals("")){
                Date parseddate=new SimpleDateFormat("dd-MM-yyyy").parse(invoicedate);
                int updated=vehiclerepo.updateDsaInvoiceBywiNumAndSlno(wino, Long.valueOf(slno),invoice,invoiceext,parseddate,invoiceprice,invoicenumber);
                log.info("updated : {} ~ {} ",updated,slno);
            }
            if(mainappid==null || mainappid.equals("")){
                mainappid="0";
            }
            if(co1appid==null || co1appid.equals("")){
                co1appid="0";
            }
            if(co2appid==null || co2appid.equals("")){
                co2appid="0";
            }


            long appid= Long.parseLong(mainappid);
            long appidco1= Long.parseLong(co1appid);
            long appidco2= Long.parseLong(co2appid);
            if(appid!=0){
                if((consent!=null && !consent.equals("")) || (osc!=null && !osc.equals(""))){
                    VehicleLoanKyc vlkyc = new VehicleLoanKyc();
                    vlkyc = vlkycservice.findByAppId(appid);
                    if(consent!=null && !consent.equals("")){
                        vlkyc.setConsentimg(consent);
                        vlkyc.setConsentimgext(consentext);
                        vlkyc.setConsentType("manual");
                    }
                    if(osc!=null && !osc.equals("")){
                        vlkyc.setOriginalSeenCertificate(osc);
                        vlkyc.setOriginalSeenCertificateExt(oscext);
                    }
                    vlkycservice.save(vlkyc);
                }

            }
            if(appidco1!=0){
                if((co1consent!=null && !co1consent.equals("")) || (co1osc!=null && !co1osc.equals(""))){
                    VehicleLoanKyc vlkyc = new VehicleLoanKyc();
                    vlkyc = vlkycservice.findByAppId(appidco1);
                    if(co1consent!=null && !co1consent.equals("")) {
                        vlkyc.setConsentimg(co1consent);
                        vlkyc.setConsentimgext(co1consentext);
                        vlkyc.setConsentType("manual");
                    }
                    if(co1osc!=null && !co1osc.equals("")) {
                        vlkyc.setOriginalSeenCertificate(co1osc);
                        vlkyc.setOriginalSeenCertificateExt(co1oscext);
                    }
                    vlkycservice.save(vlkyc);
                }
            }

            if(appidco2!=0){
                if((co2consent!=null && !co2consent.equals("")) || (co2osc!=null && !co2osc.equals(""))) {
                    VehicleLoanKyc vlkyc = new VehicleLoanKyc();
                    vlkyc = vlkycservice.findByAppId(appidco2);
                    if(co2consent!=null && !co2consent.equals("")) {
                        vlkyc.setConsentimg(co2consent);
                        vlkyc.setConsentimgext(co2consentext);
                        vlkyc.setConsentType("manual");
                    }
                    if(co2osc!=null && !co2osc.equals("")) {
                        vlkyc.setOriginalSeenCertificate(co2osc);
                        vlkyc.setOriginalSeenCertificateExt(co2oscext);
                    }
                    vlkycservice.save(vlkyc);
                }
            }


            return "success";


        }catch(Exception e) {
            log.info("Exception : {} ",e);
            e.printStackTrace();
            return "error";
        }
    }

    public DSAExperianReponseDTO processDataDSA(DSAExperianRequestDTO exprequest) {
        try {
            log.info("Entered Experian Check from DSA");
            ExperianRequest request = createExperianRequest1(exprequest);
            log.info("Experian Req : {} ",request.toString());
            ExperianResponse response = fetchExperianReport(request);
            log.info(" Experian Resp {} ",response.toString());
            if ("SUCCESS".equalsIgnoreCase(response.getStatus())) {
                log.info("Experian Success. Entered DK1");
                Long Score = Long.valueOf(response.getScore());
                log.info("Score : {} ",Score);
                log.info("Experian Success. Entered DK2");
                if(!exprequest.getProgram().isBlank()){
                    if (validationRepository.checkExperianEligibleColor(exprequest.getProgram(), "", Long.valueOf(response.getScore()))) {
                        ExperianRequest dkrequest = createDkRequest(exprequest, response);
                        log.info("DK request {} ",dkrequest);
                        DKResponse dkResponse = fetchDKReport(dkrequest);
                        log.info("DK response {}  ",dkResponse.toString());

                        if ("SUCCESS".equalsIgnoreCase(dkResponse.getStatus())){
                            log.info("DK Success");
                            List<Map<String, String>> getDPD = vlEligiblityRepository.finddpddata(exprequest.getProgram(),exprequest.getEmptype(),"BM");
                            log.info("DPD Data {} ", getDPD.toString() );
                            int duration=0;int dpdDays=0;
                            Boolean stat=false;
                            if (!getDPD.isEmpty() && getDPD.size() >= 1) {
                                for (Map<String, String> dpdval : getDPD) {
                                    duration = Integer.parseInt(dpdval.get("DURATION"));
                                    dpdDays = Integer.parseInt(dpdval.get("MAX_DPD_DAYS"));
                                    log.info("duration  {}" , duration);
                                    log.info("dpdDays {} " , dpdDays);
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    stat = vlbrEservice.validateDpd(duration, dpdDays, String.valueOf(formatter.format(new Date())), dkResponse.getDelinquencyAnalysis(),"dsa","0","","BM","INCOME");
                                    log.info("stat {}" ,stat);
                                    if(stat)
                                        break;
                                }
                            }else{
                                 stat = vlbrEservice.validateDpdNew(dkResponse.getDelinquencyAnalysis(),"BM","INCOME","dsa","0","");
                            }
                            BigDecimal totalemiamount= BigDecimal.ZERO;
                            for(DKResponse.Liability liability: dkResponse.getLiabilityList()){
                                if(liability.getEmiAmount()!=null){
                                    totalemiamount=totalemiamount.add(liability.getEmiAmount());
                                }
                            }
                            if(stat==false){
                                DSAExperianReponseDTO expdto = new DSAExperianReponseDTO();
                                log.info("DPD Check is Passed");
                                expdto.setStatus("success");
                                expdto.setMessage("Bureau Score Successfully Fetched");
                                expdto.setBureauScore(Score);
                                expdto.setEligibility("eligible");
                                expdto.setLiabilityList(dkResponse.getLiabilityList());
                                expdto.setTotObligations(totalemiamount);
                                expdto.setDpddays(dkResponse.getDelinquencyAnalysis());
                                log.info("dto : {}",expdto.toString());
                                return expdto;
                            }else{
                                log.info("DPD Check is Failed");
                                DSAExperianReponseDTO expdto = new DSAExperianReponseDTO();
                                expdto.setStatus("failed");
                                expdto.setMessage("DPD Days Eligibility Failed");
                                expdto.setBureauScore(Score);
                                expdto.setEligibility("Not eligible");
                                expdto.setDpddays(dkResponse.getDelinquencyAnalysis());
                                expdto.setLiabilityList(dkResponse.getLiabilityList());
                                expdto.setTotObligations(totalemiamount);
                                log.info("Failed 2: {}",dkResponse.getErrorreason());
                                return expdto;
                            }


                        }else{
                            log.info("DK Call is Failed");
                            DSAExperianReponseDTO expdto = new DSAExperianReponseDTO();
                            expdto.setStatus("failed");
                            expdto.setMessage("DPD Analysis  is failed.Please Retry");
                            expdto.setBureauScore(Score);
                            log.info("Failed 2: {} ",dkResponse.getErrorreason());
                            return expdto;
                        }

                    }else {
                        log.info("Experian  score data  is Failed with master");
                        DSAExperianReponseDTO expdto = new DSAExperianReponseDTO();
                        expdto.setStatus("failed");
                        expdto.setMessage("Bureau Eligibility Not Met");
                        expdto.setBureauScore(Score);
                        return expdto;
                    }
                }else{
                    log.info("Experian   is Failed ");
                    DSAExperianReponseDTO expdto = new DSAExperianReponseDTO();
                    expdto.setStatus("failed");
                    expdto.setMessage("Income tpye passed is null");
                    return expdto;
                }
            }else{
                String jsonResponse = response.getJsonResponse();
                JSONObject jsonObj = new JSONObject(jsonResponse);
                String returnmsg="";
                if (jsonObj.has("Response") && jsonObj.getJSONObject("Response").has("Body")) {
                    JSONObject body = jsonObj.getJSONObject("Response").getJSONObject("Body");
                    if (body.has("experianCCIRRes") && body.getJSONObject("experianCCIRRes").has("status")) {
                        JSONObject status = body.getJSONObject("experianCCIRRes").getJSONObject("status");
                        if (status.has("message") && !status.isNull("message")) {
                            returnmsg = "Bureau Score Fetch failed : "+ status.getString("message");
                        }
                    }
                }
                log.info("Experian  Score fetch is Failed ");
                DSAExperianReponseDTO expdto = new DSAExperianReponseDTO();
                expdto.setStatus("failed");
                if(!returnmsg.equals("")){
                    expdto.setMessage(returnmsg);
                }else{
                    expdto.setMessage("Bureau Score Fetch failed.Please Retry");
                }

                return expdto;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception Failed 4: {} ",e.getMessage());
            log.info("Error during Report generation Reason:  {}",e.getMessage());
            DSAExperianReponseDTO expdto = new DSAExperianReponseDTO();
            expdto.setStatus("failed");
            expdto.setMessage("Unexpected Error during Report generation Reason.Please Retry");
            return expdto;
        }


    }

    public ExperianResponse fetchExperianReport(ExperianRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(request);
            log.info("DSA Req {}",requestBody);

//            requestBody = "{\n" +
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


            log.info("DUmmy req {}",requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception 4:" ,e.getMessage());
            throw new RuntimeException("Error preparing request", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        try {
            log.info("Exp experianApiUrl {}",experianApiUrl);
            String responseBody = restTemplate.postForObject(experianApiUrl, entity, String.class);
            log.info("Exp responseBody : {} ",responseBody);
            ExperianResponse response = objectMapper.readValue(responseBody, ExperianResponse.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            log.info(" Exception Failed 4: {} ",e.getMessage());
            throw new RuntimeException(" Experian report fetch Failed", e);
        }
    }

    @Value("${api.integrator}")
    private String experianApiUrl;

    public DKResponse fetchDKReport(ExperianRequest request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(request);
        }
        catch (Exception e) {
            throw new RuntimeException("Error preparing request", e);
        }
        log.info("fetchDKReport requ {} ",requestBody);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        try {
            String responseBody = restTemplate.postForObject(experianApiUrl, entity, String.class);
            DKResponse response = objectMapper.readValue(responseBody, DKResponse.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            log.info(" Exception {}",e.getMessage());
            throw new RuntimeException(" ScoreCard report Fetch Failed", e);
        }
    }


    private  ExperianRequest createDkRequest(DSAExperianRequestDTO exprequest,ExperianResponse experianResponse) throws JsonProcessingException {
        ExperianRequest request = new ExperianRequest();
        request.setMock(false);
        request.setApiName("fetchDK");
        request.setUserId(exprequest.getUserid());
        request.setSlno(exprequest.getSlno());
        request.setWorkItemNumber(exprequest.getSlno());
        request.setAppid("");
        request.setOrigin("DSA");
        request.setExperian_ino(experianResponse.getExperian_ino());

        String dob=exprequest.getDob();
        DateTimeFormatter inputformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter outputformatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        LocalDate formattedate = LocalDate.parse(dob, inputformatter);
        String resultDob = formattedate.format(outputformatter);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        ObjectNode requestNode = rootNode.putObject("request");
        requestNode.put("UUID", UUID.randomUUID().toString());
        requestNode.put("merchantCode", merchantCode);
        requestNode.put("merchantName",merchantName);

        // Create score_engine_API_request node
        ObjectNode scoreEngineApiRequestNode = requestNode.putObject("score_engine_API_request");

        // Add customer_details array
        ArrayNode customerDetailsArray = scoreEngineApiRequestNode.putArray("customer_details");
        // Add details to customer_details array
        ObjectNode customerDetails = customerDetailsArray.addObject();
        String newtocredit="Y";
        if(experianResponse.getNewtoCredit()!=null && !experianResponse.getNewtoCredit().isBlank()){
            newtocredit = experianResponse.getNewtoCredit();
        }

        customerDetails.put("FirstName", exprequest.getName());
        customerDetails.put("LastName", "");
        customerDetails.put("DateOfBirth", resultDob.replace("-","").replace("/",""));
        customerDetails.put("LTVRequest", exprequest.getLtv());
        customerDetails.put("Manufacturer", exprequest.getMake());
        customerDetails.put("CollateralType", exprequest.getBodytype());
        customerDetails.put("CustomerType", newtocredit.equals("Y")?"New to Bank":"Existing to Bank");
        customerDetails.put("Grade", "Personal");
        customerDetails.put("VehicleCategory", exprequest.getVehicleCategory());

        customerDetails.put("CustomerId", "");

        customerDetails.put("unique_ref_no", UUID.randomUUID().toString());
        if(exprequest.getStatecode().equals("KL")){
            customerDetails.put("Product", "AL");
        }else{
            customerDetails.put("Product", "AL_NON_KERALA");
        }

        customerDetails.put("type_borrower", "INDV");
        customerDetails.put("bureau_name", "EXPERIAN");
        customerDetails.put("AssetClassification", "New");
        customerDetails.put("ScoringRequired", "N");
        customerDetails.put("Bureau_Summary", "Y");
        customerDetails.put("NewtoCredit", newtocredit);
        // Add empty bureau_data array
        ArrayNode bureauData = scoreEngineApiRequestNode.putArray("bureau_data");
        // Add details to customer_details array
        if(!newtocredit.equals("Y")){
            JsonNode bureauDetails=objectMapper.readTree(experianResponse.getExperiaCCIRJsonReport());
            bureauData.add(bureauDetails);
        }


        request.setRequest(rootNode.get("request"));
        return request;
    }



    public ExperianRequest createExperianRequest1(DSAExperianRequestDTO exprequest) {


        ExperianRequest request = new ExperianRequest();
        request.setMock(false);
        request.setApiName("fetchExperian");
        request.setUserId(exprequest.getUserid());
        request.setSlno(exprequest.getSlno());
        request.setWorkItemNumber(exprequest.getSlno());
        request.setAppid("");
        request.setOrigin("DSA");
        request.setPan(exprequest.getPanno());

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
        enqheader.put("enquiryAmount", exprequest.getLoanamount());//loanAmt
        enqheader.put("enquiryCreditPurpose", "8");
        enqheader.put("durationofAgreement", exprequest.getTenure());//tenure
        enqheader.put("frequency", "99");
        enqheader.put("kendraId", "");
        enqheader.put("branchId", "");

        experianCCIRReq.putObject("userpref").put("language", "");

        ArrayNode addlprod = experianCCIRReq.putArray("addlprod");
        addlprod.addObject().put("enquiryAddOnProduct", "");

        ObjectNode consumerRequest = experianCCIRReq.putObject("consumerRequest");
        ArrayNode prsnsrch = consumerRequest.putArray("prsnsrch");
        ObjectNode person = prsnsrch.addObject();
        person.put("firstGivenName", exprequest.getName());
        person.put("middleName", "");
        person.put("otherMiddleNames", "");
//        String familyName=Optional.ofNullable(data.getApplName())
//                .map(name -> {
//                    name = name.trim();
//                    if (name.contains(" ")) {
//                        return name.substring(name.lastIndexOf(" ") + 1);
//                    } else {
//                        return name;
//                    }

//                })
//                .orElse("");

        person.put("familyName", exprequest.getName());
        person.put("suffix", "");
        person.put("applicationRole", "");
        person.put("dateOfBirth", exprequest.getDob().replace("-","").replace("/",""));
//        String gender = switch (exprequest.getGender()){
//            case "M" : yield "1";
//            case "F" : yield "2";
//            case "T" : yield "3";
//            default:  yield "";
//        };
//        person.put("gender", gender);
        person.put("indiaMiddleName3", "");
        person.put("indiaNameTitle", "");

        consumerRequest.putObject("persalias")
                .put("aliasName", "")
                .put("aliasType", "");

        ArrayNode personid = consumerRequest.putArray("personid");
        personid.addObject()
                .put("idNumberType", "10")
                .put("idNumber", exprequest.getPanno())
                .put("idIssueDate", "")
                .put("idExpirationDate", "");
//        if( kyc.getPassportNumber()!=null) {
//            personid.addObject()
//                    .put("idNumberType", "4")
//                    .put("idNumber", kyc.getPassportNumber())
//                    .put("idIssueDate", "")
//                    .put("idExpirationDate", String.valueOf(kyc.getPassportExpiryDate()));
//        }
//        if( kyc.getAadharRefNum()!=null) {
//            personid.addObject()
//                    .put("idNumberType", "12")
//                    .put("idNumber", kyc.getAadharRefNum())
//                    .put("idIssueDate", "")
//                    .put("idExpirationDate", "");
//        }

        consumerRequest.putObject("personbnk")
                .put("bankAccountNumber", "");

        Optional<ExperianPincodeMasterDTO> code=pincodeMasterRepository.getExpData(exprequest.getStatecode());
        if(code.isEmpty())
            throw new ValidationException(ValidationError.COM001,"State is Not Mapped With Bureau State");
        String adr1="",adr2="",adr3="",pin="",reg="",loc="";
        if(code.isPresent())   {
            adr1=exprequest.getAddr1();
            adr2=exprequest.getAddr2();
            adr3=exprequest.getCity();
            pin=exprequest.getPin();
            reg=code.get().getRegionCode();
            loc=code.get().getExperianStateCode();
        }

        ArrayNode persaddr = consumerRequest.putArray("persaddr");
        persaddr.addObject()
                .put("addrType", "")
                .put("localityName",  loc.replace("_"," ").replace("("," ").replace(")"," "))
                .put("regionCode", reg)
                .put("postalCode",pin)
                .put("countryCode", "")
                .put("addressLine1", adr1.replace("_"," ").replace("("," ").replace(")"," "))
                .put("addressLine2", adr2.replace("_"," ").replace("("," ").replace(")"," "))
                .put("addressLine3",  adr3.replace("_"," ").replace("("," ").replace(")"," "))
                .put("landmark", "");

        ArrayNode persphone = consumerRequest.putArray("persphone");
        persphone.addObject()
                .put("phoneNumber", exprequest.getMobilenum())
                .put("phoneType", "6")
                .put("phoneNumberExtension", "");

        ArrayNode persemail = consumerRequest.putArray("persemail");
        persemail.addObject()
                .put("webAddrType", "")
                .put("webAddr", "");

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
