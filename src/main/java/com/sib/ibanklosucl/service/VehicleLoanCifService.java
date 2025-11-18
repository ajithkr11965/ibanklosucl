package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.losintegrator.cif.*;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanCIF;
import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.VehicleLoanCifRepository;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class VehicleLoanCifService {


    @Value("${cifapisendercode}")
    private String  cifapisendercode;

    @Value("${cifapisendername}")
    private String  cifapisendername;

    @Value("${cifchannelid}")
    private String cifchannelid;
    @Value("${cifapiconstcodemale}")
    private String cifapiconstcodemale;
    @Value("${cifapiconstcodefemale}")
    private String cifapiconstcodefemale;

    @Autowired
    private FetchRepository fetchRepository;
    private final VehicleLoanCifRepository vehicleLoanCifRepository;
    private final UserSessionData usd;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${esb.ChannelID}")
    private String channelID;
    private final VehicleLoanApplicantService vehicleLoanApplicantService;
    private final VehicleLoanCifApiClient vehicleLoanCifApiClient;

    private  final  VehicleLoanSubqueueTaskService vehicleLoanSubqueueTaskService;

    private final VehicleLoanWIService vehicleLoanWIService;
    @Autowired
    public VehicleLoanCifService(VehicleLoanCifRepository vehicleLoanCifRepository,
                                 UserSessionData usd, VehicleLoanApplicantService vehicleLoanApplicantService, VehicleLoanCifApiClient vehicleLoanCifApiClient, VehicleLoanSubqueueTaskService vehicleLoanSubqueueTaskService, VehicleLoanWIService vehicleLoanWIService) {
        this.vehicleLoanCifRepository = vehicleLoanCifRepository;
        this.usd=usd;
        this.vehicleLoanApplicantService = vehicleLoanApplicantService;
        this.vehicleLoanCifApiClient = vehicleLoanCifApiClient;
        this.vehicleLoanSubqueueTaskService = vehicleLoanSubqueueTaskService;
        this.vehicleLoanWIService = vehicleLoanWIService;
    }

    public VehicleLoanCIF findByApplicantIdAndDelFlag(Long applicantId){
        return vehicleLoanCifRepository.findByApplicantIdAndDelFlag(applicantId, "N")
                .orElse(null);
    }

    public void updateVLCifBlacklist(Long applicantId, String reqIp) {
        VehicleLoanApplicant a =vehicleLoanApplicantService.findByApplicantIdAndDelFlg(applicantId);
        List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks = a.getVehicleLoanSubqueueTaskList();
        VehicleLoanSubqueueTask task=null;
        for (VehicleLoanSubqueueTask t : vehicleLoanSubqueueTasks) {
            if ("CIF_CREATION".equals(t.getTaskType()) && t.getCompletedDate() == null) {//ideally only 1 record with completed date null will be available for a given applicant id
                task = t;
            }
        }
        VehicleLoanCIF existing = vehicleLoanCifRepository.findByApplicantIdAndDelFlag(applicantId, "N")
                .orElse(null);
        if(existing==null){
            VehicleLoanCIF vehicleLoanCIF = new VehicleLoanCIF();
            vehicleLoanCIF.setWiNum(a.getWiNum());
            vehicleLoanCIF.setSlno(a.getSlno());
            vehicleLoanCIF.setApplicantId(applicantId);
            vehicleLoanCIF.setDelFlag("N");
            vehicleLoanCIF.setRcreDate(new Date());
            vehicleLoanCIF.setBlFlag("Y");
            vehicleLoanCIF.setBlUser(usd.getPPCNo());
            vehicleLoanCIF.setBlDate(new Date());
            vehicleLoanCIF.setVehicleLoanSubqueueTask(task);
            vehicleLoanCIF.setIpaddress(reqIp);
            vehicleLoanCifRepository.save(vehicleLoanCIF);
        }else {
            if(task.getTaskId().equals(existing.getVehicleLoanSubqueueTask().getTaskId())){
                existing.setBlDate(new Date());
                existing.setBlUser(usd.getPPCNo());
                existing.setBlFlag("Y");
                existing.setIpaddress(reqIp);
                vehicleLoanCifRepository.save(existing);
                log.debug("Updated existing vehiclelloancif entry to deleted for applicant ID: {}", applicantId);
            }else{
                existing.setDelFlag("Y");
                vehicleLoanCifRepository.save(existing);

                VehicleLoanCIF vehicleLoanCIF = new VehicleLoanCIF();
                vehicleLoanCIF.setWiNum(a.getWiNum());
                vehicleLoanCIF.setSlno(a.getSlno());
                vehicleLoanCIF.setApplicantId(applicantId);
                vehicleLoanCIF.setDelFlag("N");
                vehicleLoanCIF.setRcreDate(new Date());
                vehicleLoanCIF.setBlFlag("Y");
                vehicleLoanCIF.setBlUser(usd.getPPCNo());
                vehicleLoanCIF.setBlDate(new Date());
                vehicleLoanCIF.setVehicleLoanSubqueueTask(task);
                vehicleLoanCIF.setIpaddress(reqIp);
                vehicleLoanCifRepository.save(vehicleLoanCIF);
            }
        }
    }



    public VehicleLoanCifResult rejectVLCifRequest(VehicleLoanCifRequest vehicleLoanCifRequest, String reqIp) {
        Long applicantId = Long.parseLong(vehicleLoanCifRequest.getApplicantId());
        VehicleLoanCIF existing = vehicleLoanCifRepository.findByApplicantIdAndDelFlag(applicantId, "N")
                .orElse(null);
        if (existing != null) {
            existing.setDecision("REJECT");
            existing.setDecisiondate(new Date());
            existing.setDecisionUser(usd.getPPCNo());
            existing.setRemarks(vehicleLoanCifRequest.getRemarks());
            existing.setRmkDate(new Date());
            existing.setRmkUser(usd.getPPCNo());
            existing.setIpaddress(reqIp);
            vehicleLoanCifRepository.save(existing);
            log.debug("Updated existing vehiclelloancif entry to rejected", applicantId);
        }else{
            VehicleLoanApplicant a =vehicleLoanApplicantService.findByApplicantIdAndDelFlg(applicantId);
            List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks = a.getVehicleLoanSubqueueTaskList();
            VehicleLoanSubqueueTask task=null;
            for (VehicleLoanSubqueueTask t : vehicleLoanSubqueueTasks) {
                if ("CIF_CREATION".equals(t.getTaskType()) && t.getCompletedDate() == null) {//ideally only 1 record with completed date null will be available for a given applicant id
                    task = t;
                }
            }
            VehicleLoanCIF vehicleLoanCIF = new VehicleLoanCIF();
            vehicleLoanCIF.setWiNum(a.getWiNum());
            vehicleLoanCIF.setSlno(a.getSlno());
            vehicleLoanCIF.setApplicantId(applicantId);
            vehicleLoanCIF.setDelFlag("N");
            vehicleLoanCIF.setRcreDate(new Date());
            vehicleLoanCIF.setDecision("REJECT");
            vehicleLoanCIF.setDecisiondate(new Date());
            vehicleLoanCIF.setDecisionUser(usd.getPPCNo());
            vehicleLoanCIF.setRemarks(vehicleLoanCifRequest.getRemarks());
            vehicleLoanCIF.setRmkDate(new Date());
            vehicleLoanCIF.setRmkUser(usd.getPPCNo());
            vehicleLoanCIF.setIpaddress(reqIp);
            //vehicleLoanCIF.setTaskId(task.getTaskId());
            vehicleLoanCIF.setVehicleLoanSubqueueTask(task);
            vehicleLoanCifRepository.save(vehicleLoanCIF);
        }
        return new VehicleLoanCifResult("200","","");
    }


    public VehicleLoanCifResult approveVLCifRequest(VehicleLoanCifRequest vehicleLoanCifRequest, String reqIp) {
        Long applicantId = Long.parseLong(vehicleLoanCifRequest.getApplicantId());
        String slno=vehicleLoanCifRequest.getSlno();
        VehicleLoanApplicant vehicleLoanApplicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(applicantId);
        String cif1=vehicleLoanApplicant.getCifId();
        if(cif1==null)cif1="";
        if(cif1!=null && cif1.length()>0){
            return new VehicleLoanCifResult("406","Applicant CIF is already present",cif1);
        }
        VehicleLoanCIF vehicleLoanCIF = vehicleLoanCifRepository.findByApplicantIdAndDelFlag(applicantId, "N")
                .orElse(null);
        String decision1=vehicleLoanCIF.getDecision();
        if(decision1==null)decision1="";
        if(decision1.trim().isEmpty() && vehicleLoanCIF!=null && (vehicleLoanCIF.getCifId()==null || vehicleLoanCIF.getCifId().trim().isEmpty())){//vehicleLoanCIF ==null ||
            //all OK
        }else{
            return new VehicleLoanCifResult("406","This action is already performed earlier",vehicleLoanCIF.getCifId());
        }
        if(vehicleLoanCIF!=null && !"Y".equals(vehicleLoanCIF.getBlFlag())){
            return new VehicleLoanCifResult("406","Please initiate partial blacklist before CIF creation",vehicleLoanCIF.getCifId());
        }
        VehicleLoanCifResult vehicleLoanCifResult = createCifId(vehicleLoanCifRequest, reqIp);
        if(vehicleLoanCifResult.getCode().equals("200")){//for dup success also, same code is coming
            //call ckyc also
             String workItemNo = vehicleLoanWIService.createWI(vehicleLoanCifRequest.getWorkItemNumber(), slno, applicantId+"",reqIp);
             if(workItemNo!=null && workItemNo.length()>0){
                 updateVLCifBpmWI(applicantId, workItemNo, false);
             }
        }
        return vehicleLoanCifResult;
    }


    public VehicleLoanCifResult createCifId(VehicleLoanCifRequest param, String reqIp) {
        Long applicantId = Long.parseLong(param.getApplicantId());
        Long slno = Long.parseLong(param.getSlno());
        VehicleLoanApplicant vehicleLoanApplicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(applicantId);

        VehicleLoanCifRequest newRequest = param;
        VehicleLoanCifRequest.Request req = new VehicleLoanCifRequest.Request();

        req.setSender_code(cifapisendercode);
        req.setSender_name(cifapisendername);
        req.setChannel_id(cifchannelid);
        req.setGender(vehicleLoanApplicant.getBasicapplicants().getGender());
        req.setSalutation(vehicleLoanApplicant.getBasicapplicants().getSalutation());
        req.setName(vehicleLoanApplicant.getBasicapplicants().getApplicantName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        req.setCust_dob(dateFormat.format(vehicleLoanApplicant.getBasicapplicants().getApplicantDob()));
        req.setCustomernreflg(vehicleLoanApplicant.getResidentFlg().equals("R")?"N":"");
        req.setBranch_code(vehicleLoanApplicant.getVehicleLoanMaster().getSolId());
        req.setPan(vehicleLoanApplicant.getKycapplicants().getPanNo());
        req.setAadhaar(vehicleLoanApplicant.getKycapplicants().getAadharRefNum());
        req.setMobile(vehicleLoanApplicant.getBasicapplicants().getMobileNo());
        req.setEmail(vehicleLoanApplicant.getBasicapplicants().getEmailId());
        String fatherName=vehicleLoanApplicant.getBasicapplicants().getFatherName();
        if(fatherName==null){
            fatherName="";
        }
        req.setFather_name(fatherName);
        String spouse_name=vehicleLoanApplicant.getBasicapplicants().getSpouseName();
        if(spouse_name==null)spouse_name="";
        req.setSpouse_name(spouse_name);
        req.setMother_name(vehicleLoanApplicant.getBasicapplicants().getMotherName());
        String maritalStatus=vehicleLoanApplicant.getBasicapplicants().getMaritalStatus();
        if("OTHERS".equalsIgnoreCase(maritalStatus)){
            maritalStatus="OTHRS";
        }
        //req.setMarital_status(vehicleLoanApplicant.getBasicapplicants().getMaritalStatus());
        req.setMarital_status(maritalStatus);

        if(vehicleLoanApplicant.getBasicapplicants().getOccupation()==null){
            throw new RuntimeException("Occupation not found in basic details");
        }
        req.setOccupation(vehicleLoanApplicant.getBasicapplicants().getOccupation());
        req.setAnnual_income(vehicleLoanApplicant.getBasicapplicants().getAnnualIncome());

        Date applicantDob=vehicleLoanApplicant.getBasicapplicants().getApplicantDob();
        String residentFlg=vehicleLoanApplicant.getResidentFlg();
        String aadharPanLinked=vehicleLoanApplicant.getKycapplicants().getPanUidLink();
        aadharPanLinked=(aadharPanLinked!=null && (aadharPanLinked.equalsIgnoreCase("Y") || aadharPanLinked.equalsIgnoreCase("NA")))?"Y":"N";

        String tdscode=fetchTdsCode(applicantDob,residentFlg,aadharPanLinked);
        if(tdscode==null || tdscode.trim().isEmpty() || tdscode.contains("|")){
            throw new RuntimeException("Unable to fetch TDS code from finacle.");
        }
        req.setTds_tbl_code(tdscode);
        String constitutionCode="";
        if(vehicleLoanApplicant.getBasicapplicants().getGender().equals("M")){
            constitutionCode=cifapiconstcodemale;
        }else if(vehicleLoanApplicant.getBasicapplicants().getGender().equals("F")){
            constitutionCode=cifapiconstcodefemale;
        }else{
            throw new RuntimeException("Cannot arrive constitutionCode");
        }
        req.setConstitution_code(constitutionCode);
        String uuid = vehicleLoanApplicant.getWiNum()+"_"+vehicleLoanApplicant.getApplicantId();
        req.setUUID(uuid);
        req.setPhone_Num_CountryCode(vehicleLoanApplicant.getBasicapplicants().getMobileCntryCode().replace("+",""));//remove '+' symbol

        String addrLine3=vehicleLoanApplicant.getBasicapplicants().getAddr3();
        if(addrLine3==null || addrLine3.trim().isEmpty()){
            addrLine3="";
        }
        String commAddrLine3=vehicleLoanApplicant.getBasicapplicants().getComAddr3();
        if(commAddrLine3 == null || commAddrLine3.trim().isEmpty()){
            commAddrLine3 = "";
        }


        String prefferedAddress=vehicleLoanApplicant.getBasicapplicants().getPreferredFlag();
        VehicleLoanCifRequest.address addPerm = new VehicleLoanCifRequest.address();
        addPerm.setAddress_line1(vehicleLoanApplicant.getBasicapplicants().getAddr1());
        addPerm.setAddress_line2(vehicleLoanApplicant.getBasicapplicants().getAddr2());
        addPerm.setAddress_line3(addrLine3);
        addPerm.setCity(vehicleLoanApplicant.getBasicapplicants().getCity());
        addPerm.setState(vehicleLoanApplicant.getBasicapplicants().getState());
        addPerm.setCountry(vehicleLoanApplicant.getBasicapplicants().getCountry());
        addPerm.setZip(vehicleLoanApplicant.getBasicapplicants().getPin());
        addPerm.setPreferred_address(prefferedAddress.equals("P")?"Y":"N");
        addPerm.setAddress_type("Home");


        VehicleLoanCifRequest.address addComm = new VehicleLoanCifRequest.address();
        addComm.setAddress_line1(vehicleLoanApplicant.getBasicapplicants().getComAddr1());
        addComm.setAddress_line2(vehicleLoanApplicant.getBasicapplicants().getComAddr2());
        addComm.setAddress_line3(commAddrLine3);
        addComm.setCity(vehicleLoanApplicant.getBasicapplicants().getComCity());
        addComm.setState(vehicleLoanApplicant.getBasicapplicants().getComState());
        addComm.setCountry(vehicleLoanApplicant.getBasicapplicants().getComCountry());
        addComm.setZip(vehicleLoanApplicant.getBasicapplicants().getComPin());
        addComm.setPreferred_address(prefferedAddress.equals("C")?"Y":"N");
        addComm.setAddress_type("Mailing");

        VehicleLoanCifRequest.address[] addressArray = new VehicleLoanCifRequest.address[2];
        addressArray[0]=addPerm;
        addressArray[1]=addComm;

        req.setAdd(addressArray);
        newRequest.setRequest(req);

        VehicleLoanCifResponse vehicleLoanCifResponse = vehicleLoanCifApiClient.performCifCreation(newRequest);//call the api

        //save to vehicle_loan_cif
        String cifId="";
        if(vehicleLoanCifResponse!=null && vehicleLoanCifResponse.getResponse()!=null && vehicleLoanCifResponse.getResponse().getStatus().getCode().equals("200")){
            cifId=vehicleLoanCifResponse.getResponse().getBody().getCustomer_id();
            VehicleLoanCIF existing = vehicleLoanCifRepository.findByApplicantIdAndDelFlag(applicantId, "N")
                    .orElse(null);
            if (existing != null) {
                existing.setCifDate(new Date());
                existing.setCifUser(usd.getPPCNo());
                existing.setCifflag("Y");
                existing.setCifId(cifId);
                existing.setIpaddress(reqIp);
                existing.setDecision("APPROVE");
                existing.setDecisiondate(new Date());
                existing.setDecisionUser(usd.getPPCNo());
                existing.setRemarks(param.getRemarks());
                existing.setRmkDate(new Date());
                existing.setRmkUser(usd.getPPCNo());
                vehicleLoanCifRepository.save(existing);
                log.debug("Updated cif id into vehicle_loan_cif for applicant ID: {}", applicantId);
            }else{//insert new record
                VehicleLoanCIF vehicleLoanCIFNew = new VehicleLoanCIF();
                vehicleLoanCIFNew.setSlno(slno);
                vehicleLoanCIFNew.setWiNum(vehicleLoanApplicant.getWiNum());
                List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks = vehicleLoanApplicant.getVehicleLoanSubqueueTaskList();
                VehicleLoanSubqueueTask task=null;
                for (VehicleLoanSubqueueTask t : vehicleLoanSubqueueTasks) {
                    if ("CIF_CREATION".equals(t.getTaskType()) && t.getCompletedDate() == null) {//ideally only 1 record with completed date null will be available for a given applicant id
                        task = t;
                    }
                }
                //vehicleLoanCIFNew.setTaskId(task.getTaskId());
                vehicleLoanCIFNew.setVehicleLoanSubqueueTask(task);
                vehicleLoanCIFNew.setApplicantId(applicantId);
                vehicleLoanCIFNew.setDecision("APPROVE");
                vehicleLoanCIFNew.setDecisiondate(new Date());
                vehicleLoanCIFNew.setDecisionUser(usd.getPPCNo());
                vehicleLoanCIFNew.setRemarks(param.getRemarks());
                vehicleLoanCIFNew.setCifId(cifId);
                vehicleLoanCIFNew.setCifUser(usd.getPPCNo());
                vehicleLoanCIFNew.setCifDate(new Date());
                vehicleLoanCIFNew.setRmkDate(new Date());
                vehicleLoanCIFNew.setRmkUser(usd.getPPCNo());
                vehicleLoanCIFNew.setIpaddress(reqIp);
                vehicleLoanCifRepository.save(vehicleLoanCIFNew);
            }

            //update cif id into vehicle_loan_applicants
            vehicleLoanApplicant.setCifId(cifId);
            vehicleLoanApplicant.setSibCustomer("Y");
            vehicleLoanApplicantService.saveApplicant(vehicleLoanApplicant);
        }
        VehicleLoanCifResult vehicleLoanCifResult = processCifCreationResponse(vehicleLoanCifResponse);
        return vehicleLoanCifResult;
    }


    private VehicleLoanCifResult processCifCreationResponse(VehicleLoanCifResponse vehicleLoanCifResponse) {
        VehicleLoanCifResult vehicleLoanCifResult = new VehicleLoanCifResult();
        String statusCode = "-1", statusDesc="";
        if(vehicleLoanCifResponse!=null && vehicleLoanCifResponse.getResponse()!=null){
            statusCode = vehicleLoanCifResponse.getResponse().getStatus().getCode();
            statusDesc = vehicleLoanCifResponse.getResponse().getStatus().getDesc();
        }

        log.debug("CIF CREATION API response status code: {}", statusCode);

        switch (statusCode) {
            case "200":
                vehicleLoanCifResult.setCode(statusCode);
                vehicleLoanCifResult.setDesc(statusDesc);
                vehicleLoanCifResult.setCifId(vehicleLoanCifResponse.getResponse().getBody().getCustomer_id());
                break;
            case "406":
                vehicleLoanCifResult.setCode(statusCode);
                vehicleLoanCifResult.setDesc(statusDesc);
                break;
            default:
                vehicleLoanCifResult.setCode(statusCode);
                vehicleLoanCifResult.setDesc(statusDesc);
                log.warn("Unexpected status code from CIF CREATION API: {}", statusCode);
        }
        return vehicleLoanCifResult;
    }




    public List<VehicleLoanCIF> findByWiNum(String wiNum){
        return vehicleLoanCifRepository.findByWiNum(wiNum).orElse(null);
    }


    public String updateVLCifBpmWI(Long applicantId, String bpmWiNum, Boolean batch) {
        String result="F";
        VehicleLoanCIF vehicleLoanCIF = vehicleLoanCifRepository.findByApplicantIdAndDelFlag(applicantId, "N")
                .orElse(null);
        if (vehicleLoanCIF != null) {
            vehicleLoanCIF=null;
            vehicleLoanCIF = vehicleLoanCifRepository.findByApplicantIdAndDelFlag(applicantId, "N")
                    .orElse(null);
            vehicleLoanCIF.setCkycDate(new Date());
            vehicleLoanCIF.setCkycflag("Y");
            if(batch){
                vehicleLoanCIF.setCkycUser("BATCH");
            }else{
                vehicleLoanCIF.setCkycUser(usd.getPPCNo());
            }

            vehicleLoanCIF.setWorkitemnumber(bpmWiNum);
            vehicleLoanCifRepository.save(vehicleLoanCIF);
            log.debug("Updated bpm Wi into vehiclelloancif: {}", applicantId);
            result="S";
        }else{
            log.debug("NOT Updated bpm Wi into vehiclelloancif: {}", applicantId);
        }
        return result;
    }

    /*
    public String fetchTdsCode(String pan, Date dob, String winum, String slno, String applicantId, String residentFlg){

        SimpleDateFormat ddmmyyyynoseparator=new SimpleDateFormat("ddMMyyyy");
        String dobStr="";
        dobStr=ddmmyyyynoseparator.format(dob);

        TdsRequest tdsRequest = new TdsRequest();
        tdsRequest.setMock(false);
        tdsRequest.setApiName("tdscodefetch");
        tdsRequest.setWorkItemNumber(winum);
        tdsRequest.setSlno(slno);
        tdsRequest.setApplicantId(applicantId);

        TdsRequest.Request innerReq=new TdsRequest.Request();
        innerReq.setUUID(winum);
        innerReq.setDOB(dobStr);//in DDMMYYYY format without separator
        innerReq.setPAN(pan.toUpperCase());
        innerReq.setCIF_ID("");//This should be passed as empty
        innerReq.setCORP_FLG("N");
        innerReq.setNRE_STATUS(residentFlg.equals("R")?"N":"Y");//

        LocalDate dob_ld = LocalDate.ofInstant(dob.toInstant(), ZoneId.systemDefault());
        LocalDate nextFyStartDate=findNextFyStartDate();
        int ageAsOnNextFyStart=calculateAgeAsOnDate(dob_ld,nextFyStartDate);
        if(ageAsOnNextFyStart>=60){

        }

        innerReq.setSNRCTZN_STAT("");//if age >=60 as on next fy start date, then Y else N
        innerReq.setAGE80_STAT("");//if age >=80 as on today, then Y else N

        TdsResponse tdsResponse = vehicleLoanCifApiClient.fetchTdsRateCode(tdsRequest);
        return tdsResponse.getResponse().getBody().getTdscode();
    }

    */

    public String fetchTdsCode(Date dob, String residentFlg, String aadhaarPanLinkFlag){
        String corp_flg="N", NRE_STATUS="",SNRCTZN_STAT="",AGE80_STAT="",PANSTAT="Y",AADH_LNK_STAT=aadhaarPanLinkFlag,
                STAT_206="N",STAT_15g="N",VALUE_15g="NOVAL",AP_PREV="N";
        if(residentFlg.equals("R")){
            NRE_STATUS="N";
        }else{
            NRE_STATUS="Y";
        }
        LocalDate dob_ld = LocalDate.ofInstant(dob.toInstant(), ZoneId.systemDefault());
        LocalDate nextFyStartDate=findNextFyStartDate();
        int ageAsOnNextFyStart=calculateAgeAsOnDate(dob_ld,nextFyStartDate);
        if(ageAsOnNextFyStart>=60){
            SNRCTZN_STAT="Y";
        }else{
            SNRCTZN_STAT="N";
        }

        LocalDate currentDate = LocalDate.now();//date without time portion
        int ageAsOnToday=calculateAgeAsOnDate(dob_ld,currentDate);
        if(ageAsOnToday>=80){
            AGE80_STAT="Y";
        }else{
            AGE80_STAT="N";
        }
        String tdscode=fetchRepository.getTdsCode(corp_flg,NRE_STATUS,SNRCTZN_STAT,AGE80_STAT,PANSTAT,AADH_LNK_STAT,STAT_206,STAT_15g,VALUE_15g,AP_PREV);
        return tdscode;

    }
    public static int calculateAgeAsOnDate(LocalDate birthDate, LocalDate asOnDate) {

        if ((birthDate != null) && (asOnDate != null) && !birthDate.isAfter(asOnDate)) {
            return java.time.Period.between(birthDate, asOnDate).getYears();
        } else {
            throw new RuntimeException("Invalid inputs in calculateAgeAsOnDate");
        }
    }

    public LocalDate findNextFyStartDate(){
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        // Create a LocalDate for April 1st of the current year
        LocalDate aprilFirstCurrentYear = LocalDate.of(currentYear, 4, 1);

        // If current date is after or on April 1st, return April 1st of the next year
        if (currentDate.isAfter(aprilFirstCurrentYear)) {
            return LocalDate.of(currentYear + 1, 4, 1);
        } else {
            // Otherwise, return April 1st of the current year
            return aprilFirstCurrentYear;
        }
    }

}
