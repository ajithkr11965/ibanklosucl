package com.sib.ibanklosucl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.sib.ibanklosucl.dto.DataItem;
import com.sib.ibanklosucl.dto.FormData;
import com.sib.ibanklosucl.dto.FormSave;
import com.sib.ibanklosucl.dto.TabResponse;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.service.VLBlockCodes;
import com.sib.ibanklosucl.service.VehicleLoanBlockService;
import com.sib.ibanklosucl.service.esbsr.CIFViewService;
import com.sib.ibanklosucl.service.mssf.MSSFService;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.service.VlSaveService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.Constants;
import com.sib.ibanklosucl.utilies.HtmlTableGenerator;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeneralDtSaveImp implements VlSaveService {

    @Autowired
    private VehicleLoanBlockService vehicleLoanBlockService;

    @Autowired
    private VehicleLoanApplicantService repository;

    @Autowired
    private ValidationRepository validationRepository;
    @Autowired
    private CommonUtils cm;
    @Autowired
    private VehicleLoanMasterService masterService;
    @Autowired
    private VehicleLoanWarnService vehicleLoanWarnService;
    @Autowired
    private VehicleLoanKycService kycrepository;
    @Autowired
    private CIFViewService cifViewService;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private HtmlTableGenerator htmlTableGenerator;
    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;
    @Autowired
    private VehicleLoanProgramIntegrationService vehicleLoanProgramIntegrationService;
    @Autowired
    private MSSFService mssfService;


    @Override
    public TabResponse fetchData(FormData fs){
        return null;
    }
    @Override
    @Transactional(rollbackOn = Exception.class)
    public TabResponse executeSave(FormSave fs) {

            String slno = fs.getBody().getSlno();
            String appid = fs.getBody().getAppid();
            VehicleLoanApplicant applicant = null;
            VehicleLoanKyc kyc = null;
            String exist_custid="",exist_res="",exist_sib="";
            if (appid != null && !appid.isEmpty()) {
                applicant = repository.getById(Long.valueOf(appid));
                exist_custid=applicant.getCifId()==null?"":applicant.getCifId();
                exist_res=applicant.getResidentFlg();
                exist_sib=applicant.getSibCustomer();
            } else {
                applicant = new VehicleLoanApplicant();
                applicant.setSlno(Long.valueOf(slno));
                applicant.setWiNum(fs.getBody().getWinum());
                repository.saveApplicant(applicant);
            }
            applicant.setReqIpAddr(CommonUtils.getIP(fs.getReqip()));
            applicant.setApplicantType(fs.getBody().getReqtype());
            applicant.setGenComplete("Y");
            applicant.setDelFlg("N");
            applicant.setBpmFolderName(CommonUtils.expandReq(fs.getBody().getTc()));
            applicant.setLastModDate(new Date());
            applicant.setLastModUser(usd.getPPCNo());
            applicant.setHomeSol(usd.getSolid());

            VehicleLoanMaster master = masterService.findById(Long.valueOf(slno));
            String refNo = master.getRefNo();
            if(refNo!=null) {
                mssfService.updateApplication(refNo,"340001","Loan is applied");
            }
            master.setCurrentTab(fs.getBody().getCurrenttab());


            // Map data fields from request to entity
            for (DataItem data : fs.getBody().getData()) {
                switch (data.getKey().trim()) {
                    case "residentialStatus":
                         applicant.setResidentFlg(data.getValue());
                        break;
                    case "custID":
                        List<VehicleLoanApplicant> vlapp=master.getApplicants().stream().filter(t->t.getDelFlg().equalsIgnoreCase("N")).toList();
                        if(!cm.isEmpty(fs.getBody().getAppid())){
                            if (!cm.isEmpty(data.getValue()) && vlapp.stream().anyMatch(t -> (t.getApplicantId() != Long.parseLong(fs.getBody().getAppid()) && t.getCifId()!=null && t.getCifId().equalsIgnoreCase(data.getValue())))) {
                                throw new ValidationException(ValidationError.COM001, Constants.GeneralTabMessages.CUST_ID_DUP);
                            }
                        }
                        else{
                            if (!cm.isEmpty(data.getValue()) && vlapp.stream().filter(t-> "Y".equalsIgnoreCase(t.getSibCustomer())).anyMatch(t -> (t.getCifId().equalsIgnoreCase(data.getValue())))) {
                                throw new ValidationException(ValidationError.COM001, Constants.GeneralTabMessages.CUST_ID_DUP);
                            }
                        }
                        applicant.setCifId(data.getValue());
                        break;
                    case "sibCustomer":
                        applicant.setSibCustomer(data.getValue());
                        break;
                    case "canvassed_ppc":
                        applicant.setCanvassedppc(data.getValue());
                        applicant.setCanvassedppcname(fetchRepository.getPPCName(data.getValue()));
                        break;
                    case "relation":
                        applicant.setRelationWithApplicant(data.getValue());
                        break;
                    case "rsm_ppc":
                        if(data.getValue()!=null && !data.getValue().isEmpty()) {
                            applicant.setRsmppc(data.getValue());
                            applicant.setRsmppcname(fetchRepository.getPPCName(data.getValue()));
                        }
                        break;
                    case "rsm_sol":
                        applicant.setRsmsol(data.getValue());
                        applicant.setRsmsolname(fetchRepository.getSolName(data.getValue()));
                        if(usd.getSolid().equals("8032"))
                        {
                            master.setSolId(applicant.getRsmsol());
                        }
                        break;
                    case "lh_sol":
                        applicant.setLhsol(data.getValue());
                        applicant.setLhsolname(fetchRepository.getSolName(data.getValue()));
                        if(usd.getEmployee().isLhUser())
                        {
                            applicant.setLhppcno(usd.getPPCNo());
                            master.setSolId(applicant.getLhsol());
                        }
                        break;
                    case "rah_sol":
                        applicant.setRahsol(data.getValue());
                        applicant.setRahsolname(fetchRepository.getSolName(data.getValue()));
                        if(usd.getEmployee().isRahUser())
                        {
                            applicant.setRahppcno(usd.getPPCNo());
                            master.setSolId(applicant.getRahsol());
                        }
                        break;
                }
            }
            String custChanged="N";

            if (appid != null && !appid.isEmpty()) {
                if(!exist_custid.equalsIgnoreCase(applicant.getCifId()))
                    custChanged="Y";
//                    throw new ValidationException(ValidationError.COM001, Constants.GeneralTabMessages.PARAM_CHANGE);
//                if(!exist_res.equalsIgnoreCase(applicant.getResidentFlg()))
//                    throw new ValidationException(ValidationError.COM001, Constants.GeneralTabMessages.PARAM_CHANGE);
//                if(!exist_sib.equalsIgnoreCase(applicant.getSibCustomer()))
//                  throw new ValidationException(ValidationError.COM001, Constants.GeneralTabMessages.PARAM_CHANGE);

            }


            if(applicant.getResidentFlg().equals("N") || applicant.getSibCustomer().equals("Y")){
                if(applicant.getCifId()==null ||applicant.getCifId().isBlank() ){
                    throw new ValidationException(ValidationError.COM001, Constants.GeneralTabMessages.CUST_ERROR);
                }
            }

            CustomerDetails cd = null,cdold=null;
            JsonObject msg=new JsonObject();
        if (applicant.getSibCustomer().equals("Y") || applicant.getResidentFlg().equalsIgnoreCase("N")) {
            cdold = customerDetailsService.findByAppId(applicant.getApplicantId());
            if (cdold != null) {
                cdold.setDelFlg("Y");
                customerDetailsService.saveCustomerDetails(cdold);
            }
            cd = new CustomerDetails();
            cd.setSlno(Long.valueOf(slno));
            cd.setWiNum(fs.getBody().getWinum());
            cd.setApplicantId(applicant.getApplicantId());
            cd.setCustId(applicant.getCifId());
            cd.setReqIpAddr(CommonUtils.getIP(fs.getReqip()));
            cd.setDelFlg("N");
            cd.setCmDate(new Date());
            cd.setCmUser(usd.getPPCNo());
            cd.setHomeSol(usd.getSolid());
            try {
                cd = cifViewService.fetchAndProcessCustomerData(cd);
                customerDetailsService.saveCustomerDetails(cd);
                if (!cd.getValidFlg().equals("Y"))
                    throw new ValidationException(ValidationError.COM001, cd.getErrorMsg());
            } catch (Exception e) {
                e.printStackTrace();//Constants.Messages.CIF_ERROR
                throw new ValidationException(ValidationError.COM001,e.getMessage() );
            }

            //WARNING VALIDATIONS
            List<VehicleLoanWarnMaster> loanWarnMaster = vehicleLoanWarnService.getWarnMaster();
            VehicleLoanApplicant finalApplicant = applicant;
            CustomerDetails finalCd = cd;
            Map<String, List<String>> validationMessages = loanWarnMaster.stream().flatMap(mas -> {
                Map<String, List<String>> messages = new HashMap<>();
                boolean warn=false;
                String cbsValue=null,wiValue=null;
                //Customer ID of STAFF is  not Allowed.
                if ("WAR0020".equals(mas.getWarnCode()) && validationRepository.checkWhetherStaff(finalApplicant.getCifId())) {
                    wiValue=finalApplicant.getCifId();
                    messages = cm.assignWarn(messages, mas);
                    warn=true;
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

                //KYC Complied
                if ("WAR0015".equals(mas.getWarnCode()) &&  (finalCd.getKycComplied() == null || !finalCd.getKycComplied().equals("Y"))) {
                    cbsValue=finalCd.getKycComplied();
                    messages = cm.assignWarn(messages, mas);
                    warn=true;

                    VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
                    vehicleLoanBlock.setSlno(Long.parseLong(slno));
                    vehicleLoanBlock.setWiNum(fs.getBody().getWinum());
                    vehicleLoanBlock.setApplicantId(finalApplicant.getApplicantId().toString());
                    vehicleLoanBlock.setBlockType(VLBlockCodes.NOT_KYC_COMPLIED);
                    vehicleLoanBlock.setParticulars(finalCd.getCustId());
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
                }
                //PAN is not available in CBS
                if ("WAR002".equals(mas.getWarnCode()) && (finalCd.getPan() == null || finalCd.getPan().isBlank())) {
                    cbsValue=finalCd.getPan();
                    messages = cm.assignWarn(messages, mas);
                    warn=true;


                    VehicleLoanBlock vehicleLoanBlock = new VehicleLoanBlock();
                    vehicleLoanBlock.setSlno(Long.parseLong(slno));
                    vehicleLoanBlock.setWiNum(fs.getBody().getWinum());
                    vehicleLoanBlock.setApplicantId(finalApplicant.getApplicantId().toString());
                    vehicleLoanBlock.setBlockType(VLBlockCodes.PAN_CBS_MISSING);
                    vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
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
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(existingValue,newValue)->{
                existingValue.addAll(newValue);
                return existingValue;
            }));


            if (!validationMessages.isEmpty()) {
                String table = htmlTableGenerator.generateTable(validationMessages);
                msg.addProperty("warn",table);
                if (validationMessages.get("High")!=null && !validationMessages.get("High").isEmpty()) {
                    throw new ValidationException(ValidationError.COM001,table);
                }
            }

        }
        
        msg.addProperty("custChanged","N");
        if(custChanged.equalsIgnoreCase("Y")){
            msg.addProperty("custChanged","Y");
            applicant.setIncomeComplete("N");
            vehicleLoanProgramIntegrationService.disableProgram(applicant.getWiNum(), applicant.getApplicantId());
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

        applicant.setBasicComplete("N");
        applicant.setKycComplete("N");
        applicant.setIncomeComplete("N");

        ObjectMapper mapper = new ObjectMapper();
            masterService.saveLoan(master);
            applicant = repository.saveApplicant(applicant);
            kyc = kycrepository.findByAppId(applicant.getApplicantId());
            if (kyc == null) {
                kyc = new VehicleLoanKyc();
                kyc.setReqIpAddr(CommonUtils.getIP(fs.getReqip()));
                kyc.setSlno(Long.valueOf(slno));
                kyc.setWiNum(fs.getBody().getWinum());
                kyc.setApplicantId(applicant.getApplicantId());
                kyc.setDelFlg("N");
                kycrepository.save(kyc);
            }
            if(applicant.getBasicComplete() == null || !applicant.getBasicComplete().equals("Y")) {
                try {
                    msg.addProperty("cifView",mapper.writeValueAsString(cd));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            return new TabResponse("S",  msg.toString(), applicant.getApplicantId().toString());
        }


}
