package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.dto.MailRequest;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.ValidationRepository;
import com.sib.ibanklosucl.repository.VehicleLoanWarnDataRepository;
import com.sib.ibanklosucl.repository.VehicleLoanWarnMasterRepository;
import com.sib.ibanklosucl.repository.VehicleLoanWarnRepository;
import com.sib.ibanklosucl.service.email.WarningMailService;
import com.sib.ibanklosucl.service.esbsr.MailService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class VehicleLoanWarnService {

    private static final String fromMail="sibmailer@sib.bank.in";
    @Autowired
    private VehicleLoanWarnRepository vehicleLoanWarnRepository;
    @Autowired
    private VehicleLoanWarnDataRepository vehicleLoanWarnDataRepository;
    @Autowired
    private VehicleLoanWarnMasterRepository vehicleLoanWarnMasterRepository;
    @Autowired
    private UserSessionData usd;

    @Autowired
    private VehicleLoanMasterService masterService;
    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    private MisrctService misrctService;
    @Autowired
    private WarningMailService warningMailService;
    @Autowired
    private MailService mailService;

    @Autowired
    private CommonUtils cm;
    @Autowired
    private ValidationRepository validationRepository;



    @Value("${app.dev-mode:true}")
    private boolean devMode;
    @Transactional
    public VehicleLoanWarn createVehicleLoanWarn(VehicleLoanWarn vehicleLoanWarn) {
        return vehicleLoanWarnRepository.save(vehicleLoanWarn);
    }

    @Transactional
    public List<VehicleLoanWarn> createVehicleLoanWarns(List<VehicleLoanWarn> vehicleLoanWarns) {
        return vehicleLoanWarnRepository.saveAll(vehicleLoanWarns);
    }
    @Transactional
    public VehicleLoanWarnData saveWarn(VehicleLoanWarnData vehicleLoanWarn) {
        return vehicleLoanWarnDataRepository.save(vehicleLoanWarn);
    }


    public List<VehicleLoanWarn> getActiveAndNotDeletedVehicleLoanWarns(Long Slno) {
        return vehicleLoanWarnRepository.findActiveAndNotDeleted(Slno);
    }
    public Long countOfWarn(Long Slno) {
        return vehicleLoanWarnRepository.countBySlnoAndActiveFlgAndDelFlg(Slno,"Y","N");
    }

    public List<VehicleLoanWarnMaster> getWarnMaster() {
        return vehicleLoanWarnMasterRepository.findAllByDelFlg("N");
    }

    @Transactional
    public void updateActiveFlgToN(List<VehicleLoanWarn> vehicleLoanWarns) {
        vehicleLoanWarnRepository.updateActiveFlgToN(vehicleLoanWarns);
    }

    @Transactional
    public ResponseDTO sendWarnEmail(Long slno) throws  Exception{
        VehicleLoanMaster vlmas=masterService.findAppBySlno(slno);

        List<VehicleLoanApplicant> applicants=vlmas.getApplicants().stream().filter(t->"Y".equals(t.getSibCustomer()) && "N".equals(t.getDelFlg())).toList();
        List<VehicleLoanWarn> vehicleLoanWarns=new ArrayList<>();
        if(applicants.size()>0){
            vehicleLoanWarns=getActiveAndNotDeletedVehicleLoanWarns(slno);
            updateActiveFlgToN(vehicleLoanWarns);
        }
        List<VehicleLoanWarn> warnList=new ArrayList<>();
        SimpleDateFormat sm=new SimpleDateFormat("yyyy-MM-dd");
        applicants.forEach(
            a->{
                CustomerDetails cd =customerDetailsService.findByAppId(a.getApplicantId());
                if(cd==null){
                    throw new ValidationException(ValidationError.COM001,"Kindly click Fetch From CBS to proceed!!");
                }
                VehicleLoanBasic vlBasic=a.getBasicapplicants();
                //WARNING VALIDATIONS
                List<VehicleLoanWarnMaster> loanWarnMaster = getWarnMaster();
                VehicleLoanApplicant finalApplicant = a;
                VehicleLoanKyc finalKyc = a.getKycapplicants();
                String finalDob = sm.format(a.getApplDob());
                String cbsValue=null,wiValue=null;
                //DOB mismatch between application vs CBS
                if (cd.getCustDob() == null || !cd.getCustDob().equalsIgnoreCase(finalDob)) {
                    cbsValue=cd.getCustDob();
                    wiValue=finalDob;
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR001");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //PAN is not available in CBS
                if ((cd.getPan() == null || cd.getPan().isBlank())) {
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    cbsValue=cd.getPan();
                    wiValue="-";
                    v1.setWarnCode("WAR002");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //PAN Mismatch
                if ( !cd.getPan().equalsIgnoreCase(finalKyc.getPanNo())) {
                    cbsValue=cd.getPan();
                    wiValue=finalKyc.getPanNo();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR003");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //Aadhaar is not available in CBS
                if ( ( !cm.isEmpty(finalKyc.getAadharRefNum()) && cm.isEmpty(cd.getAadhaarRefNo()))) {

                    cbsValue=cd.getAadhaarRefNo();
                    wiValue=finalKyc.getAadharRefNum();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR004");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    if(cm.isEmpty(cd.getPassport()) || cm.isEmpty(cd.getVoterid()) || cm.isEmpty(cd.getDrivingLicence())){
                      v1.setSeverity("Low");
                    }
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //Aadhaar Mismatch
                if ( (!cm.isEmpty(finalKyc.getAadharRefNum()) && !cm.isEmpty(cd.getAadhaarRefNo()) && !cd.getAadhaarRefNo().equalsIgnoreCase(finalKyc.getAadharRefNum()))) {
                    cbsValue=cd.getAadhaarRefNo();
                    wiValue=finalKyc.getAadharRefNum();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR005");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }

                //Passport is not available in CBS for NRI
                if ("N".equals(finalApplicant.getResidentFlg()) && cm.isEmpty(cd.getPassport())) {
                    cbsValue=cd.getPassport();
                    wiValue="";
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR006");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //Passport number mismatch between application vs CBS for NRI
                if ("N".equals(finalApplicant.getResidentFlg())  && !cm.isEmpty(cd.getPassport())  && !cd.getPassport().equalsIgnoreCase(finalKyc.getPassportNumber())){
                    cbsValue=cd.getPassport();
                    wiValue=finalKyc.getPassportNumber();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR007");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }

                //Resident status mismatch between application vs CBS
                if (cd.getResidentialStatus() == null || !cd.getResidentialStatus().equals(finalApplicant.getResidentFlg().equals("N")?"N":"Y")) {
                    cbsValue=cd.getResidentialStatus();
                    wiValue=finalApplicant.getResidentFlg();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR008");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //CIF is minor in CBS
                if   (cd.getMinorFlag() == null || cd.getMinorFlag().equals("Y")) {
                    cbsValue=cd.getMinorFlag();
                    wiValue=null;
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR009");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //Visa is not available in CBS
                if (("N".equals(finalApplicant.getResidentFlg())  && "V".equals(finalKyc.getVisaOciType()) && cm.isEmpty(cd.getVisa()))) {
                    cbsValue=cd.getVisa()==null?"":cd.getVisa();
                    wiValue=finalKyc.getVisaOciNumber();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0011");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                if (("N".equals(finalApplicant.getResidentFlg())  && "C".equals(finalKyc.getVisaOciType()) && cm.isEmpty(cd.getCdnNo()))) {
                    cbsValue=cd.getCdnNo()==null?"":cd.getCdnNo();
                    wiValue=finalKyc.getVisaOciNumber();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0011");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                if (("N".equals(finalApplicant.getResidentFlg())  && "O".equals(finalKyc.getVisaOciType()) && cm.isEmpty(cd.getOciCard()))) {
                    cbsValue=cd.getOciCard()==null?"":cd.getOciCard();
                    wiValue=finalKyc.getVisaOciNumber();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0011");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //Visa mismatch between application vs CBS
                if ("N".equals(finalApplicant.getResidentFlg()) && "V".equals(finalKyc.getVisaOciType()) && (cm.isEmpty(cd.getVisa()) || !cd.getVisa().equalsIgnoreCase(finalKyc.getVisaOciNumber()))) {
                    cbsValue=cd.getVisa();
                    wiValue= finalKyc.getVisaOciNumber();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0010");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                if ("N".equals(finalApplicant.getResidentFlg()) && "C".equals(finalKyc.getVisaOciType()) && (cm.isEmpty(cd.getCdnNo()) || !cd.getCdnNo().equalsIgnoreCase(finalKyc.getVisaOciNumber()))) {
                    cbsValue=cd.getCdnNo();
                    wiValue= finalKyc.getVisaOciNumber();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0010");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                if ("N".equals(finalApplicant.getResidentFlg()) && "O".equals(finalKyc.getVisaOciType()) && (cm.isEmpty(cd.getOciCard()) || !cd.getOciCard().equalsIgnoreCase(finalKyc.getVisaOciNumber()))) {
                    cbsValue=cd.getOciCard();
                    wiValue= finalKyc.getVisaOciNumber();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0010");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }

                //PAN vs Aadhaar name mismatch
                if (!cm.isEmpty(finalKyc.getAadharName()) && !finalKyc.getPanName().equalsIgnoreCase(finalKyc.getAadharName())) {
                    cbsValue="-";
                    wiValue= finalKyc.getPanName()+"|"+finalKyc.getAadharName();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0012");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //Name mismatch between application vs CBS
                if ( !finalApplicant.getApplName().trim().equalsIgnoreCase(cd.getCustomerName().trim())) {
                    cbsValue=cd.getCustomerName();
                    wiValue= finalApplicant.getApplName();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0013");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }

                //KYC Complied
                if ((cd.getKycComplied() == null || !cd.getKycComplied().equals("Y"))) {
                    cbsValue=cd.getKycComplied();
                    wiValue="-";
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0015");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //Phone number mismatch between application vs CBS
                if (!cm.isEmpty(cd.getCellPhone()) && !(vlBasic.getMobileCntryCode()+vlBasic.getMobileNo()).equalsIgnoreCase(cd.getCellPhone())) {
                    cbsValue=cd.getCellPhone();
                    wiValue= vlBasic.getMobileCntryCode()+vlBasic.getMobileNo();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0017");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
                //Phone number is not available in CBS`
                if (cm.isEmpty(cd.getCellPhone())) {
                    cbsValue=cd.getCellPhone();
                    wiValue=  vlBasic.getMobileCntryCode()+vlBasic.getMobileNo();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0018");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }

                //Email mismatch between application vs CBS
                if (!cm.isEmpty(cd.getCommEmail()) && !vlBasic.getEmailId().equalsIgnoreCase(cd.getCommEmail())) {
                    cbsValue=cd.getCommEmail();
                    wiValue= vlBasic.getEmailId();
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0019");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }

                //Customer ID of STAFF is  not Allowed.
                if (validationRepository.checkWhetherStaff(finalApplicant.getCifId())) {
                    wiValue=finalApplicant.getCifId();
                    cbsValue="-";
                    VehicleLoanWarn v1=createVlwarn(cd,vlmas.getSolId(),a.getApplName());
                    v1.setWarnCode("WAR0020");
                    v1.setWarnDesc(getWarnCode(loanWarnMaster,v1.getWarnCode()));
                    v1.setSeverity(getSeverity(loanWarnMaster,v1.getWarnCode()));
                    v1.setCbsValue(cbsValue);
                    v1.setWiValue(wiValue);
                    warnList.add(v1);
                }
            });
        createVehicleLoanWarns(warnList);
        if(warnList.size()>0) {
            MailRequest mailRequest=new MailRequest();
            mailRequest.setFrom(warnList.get(0).getMailIdFrom());
            if(!devMode){
                mailRequest.addToMailArray(warnList.get(0).getMailIdTo(), mailRequest);
            }
            else {
                //mailRequest.addToMailArray("aruntgeorge@sib.bank.in", mailRequest);
                //mailRequest.addToMailArray("antonyraj@sib.bank.in", mailRequest);
                mailRequest.addToMailArray("antonyraj@sib.bank.in", mailRequest);
               // mailRequest.addToMailArray("vigneshpadmanabhan@sib.bank.in", mailRequest);
            }
            mailRequest.setContent(warningMailService.generateEmailBody(warnList,warnList.get(0).getWiNum()));
            mailRequest.setSubject("Warning Conditions of "+warnList.get(0).getWiNum());
            return mailService.sendMail(mailRequest);
        }

        return new ResponseDTO("S","No mail sent");
    }

private String getWarnCode(List<VehicleLoanWarnMaster> loanWarnMaster,String warncode){
        return loanWarnMaster.stream().filter(t->warncode.equalsIgnoreCase(t.getWarnCode())).toList().get(0).getWarnDesc();
}
private String getSeverity(List<VehicleLoanWarnMaster> loanWarnMaster,String warncode){
        return loanWarnMaster.stream().filter(t->warncode.equalsIgnoreCase(t.getWarnCode())).toList().get(0).getSeverity();
}

    private  VehicleLoanWarn createVlwarn(CustomerDetails cust,String sol_id,String app_type){
        VehicleLoanWarn v1=new VehicleLoanWarn();
        v1.setWiNum(cust.getWiNum());
        v1.setSlno(cust.getSlno());
        v1.setApplicantId(cust.getApplicantId());
        v1.setActiveFlg("Y");
        v1.setDelFlg("N");
        v1.setQueue("BM");
        v1.setReqIpAddr(usd.getRemoteIP());
        v1.setMailDate(new Date());
        v1.setMailSentFlg("Y");
        v1.setMailIdFrom(fromMail);
        v1.setMailIdTo(String.format("br%s@sib.bank.in",sol_id));
        v1.setLastModDate(new Date());
        v1.setLastModUser(usd.getPPCNo());
        v1.setHomeSol(usd.getSolid());
        v1.setApplicantType(app_type);
        return v1;
    }



}
