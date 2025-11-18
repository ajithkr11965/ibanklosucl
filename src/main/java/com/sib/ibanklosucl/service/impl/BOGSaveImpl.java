package com.sib.ibanklosucl.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.dto.losintegrator.*;
import com.sib.ibanklosucl.dto.mssf.MSSFStatusRequest;
import com.sib.ibanklosucl.dto.mssf.MSSFStatusResponse;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.model.doc.VehicleLoanRepayment;
import com.sib.ibanklosucl.repository.VehicleLoanAcctLabelRepository;
import com.sib.ibanklosucl.repository.VehicleLoanNeftRepository;
import com.sib.ibanklosucl.repository.VehicleLoanCifRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.mssf.MSSFCustomerRepository;
import com.sib.ibanklosucl.repository.program.VehicleLoanFDRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.doc.RepaymentService;
import com.sib.ibanklosucl.service.integration.NACHMandateService;
import com.sib.ibanklosucl.service.integration.SMSEmailService;
import com.sib.ibanklosucl.service.vlsr.MisrctService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanTatService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import com.sib.ibanklosucl.service.eligibility.EligibilityDetailsService;
import com.sib.ibanklosucl.repository.AccountOpeningRepository;
import com.sib.ibanklosucl.repository.VehicleLoanMasterRepository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
@Slf4j
public class BOGSaveImpl implements VlCommonTabService {

    @Autowired
    private VehicleLoanNeftRepository vehicleLoanNeftRepository;
    @Autowired
    private VehicleLoanProgramService VehicleLoanProgramservice;
    @Autowired
    private VehicleLoanFDRepository vehicleLoanFDRepository;
    @Autowired
    private SMSEmailService smsEmailService;

    @Autowired
    private ObjectMapper mapper;
    @Value("${helpdeskNo}")
    private String helpdeskNo;

    @Value("${app.dev-mode:true}")
    private boolean devMode;
    @Value("${neft.merchantCode}")
    private String neftmerchantCode;
    @Value("${neft.merchantName}")
    private String neftmerchantName;
    @Value("${BogPoolAcc}")
    private String BogPoolAcc;
    @Value("${fi.merchantname}")
    private String fimerchantname;
    @Value("${fi.merchantcode}")
    private String fimerchantcode;

    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${esb.ChannelID}")
    private String channelID;

    @Value("${accappend}")
    private String append;//="_AO"

    @Autowired
    private NACHMandateService nachMandateService;
    @Autowired
    private VehicleLoanTatService loanTatService;

    @Autowired
    private VehicleDetailsService vehicleDetailsService;
    @Autowired
    private DisbApiClient disbApiClient;
    @Autowired
    private MisrctService misrctService;
    @Autowired
    private VehicleLoanMasterRepository vehicleLoanMasterRepo;

    @Autowired
    private AccountOpeningRepository accountOpeningRepository;
    @Autowired
    private RepaymentService repaymentService;
    @Autowired
    private VehicleLoanDetailsService loanDetailsService;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private AccOpeningApiClient accOpeningApiClient;
    @Autowired
    private EligibilityDetailsService eligibilityDetailsService;
    @Autowired
    private VehicleLoanSanModService vehicleLoanSanModService;
    @Autowired
    private VehicleLoanWaiverService loanWaiverService;
    @Autowired
    private VehicleLoanQueueDetailsService vehicleLoanQueueDetailsService;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;
    @Autowired
    private VehicleLoanCifService vehicleLoanCifService;

    @Autowired
    private UserSessionData usd;

    @Autowired
    private VehicleLoanCifRepository vehicleLoanCifRepository;

    @Autowired
    private VehicleLoanAcctLabelRepository vehicleLoanAcctLabelRepository;

    @Autowired
    private VehicleLoanSubqueueTaskService vehicleLoanSubqueueTaskService;

    @Autowired
    private VehicleLoanSubqueueTatService vehicleLoanSubqueueTatService;
    @Autowired
    private MSSFCustomerRepository mssfCustomerRepository;
    ;

    @Override
    public ResponseEntity<?> saveLoan(VehicleLoanDetails vehicleLoanDetails) {
        return null;
    }

    @Override
    public ResponseDTO saveMaker(String slno, String winum, String vlowner, String vlownerstatus, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveChecker(RBCPCMakerSave rbs, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveRBCChecker(RBCPCCheckerSave rbs, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveRBCMaker(RBCPCMakerSave rbs, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveCRTAmber(String slno, String winum, String remarks, String decision, HttpServletRequest request) throws Exception {
        return null;
    }


    @Override
    public ResponseDTO saveWIRecall(String wiNum, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveRepayment(RepaymentDTO repaymentDTO) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveWaiver(WaiverDto waiverDto, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO updateWaiver(WaiverDto waiverDto, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveDoc(DocumentRequest documentRequest) throws Exception {
        return null;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveBOG(Long slno, String winum, String remarks, HttpServletRequest request) throws Exception {

        String fromQueue = "BD", toQueue = "BD", status = "", message = "";
        ResponseDTO responseDTO = validateInputs(slno, winum, remarks, request);
        if (responseDTO.getStatus().equals("F")) {
            return responseDTO;
        }

        //update subqueue table
        int a = updateSubtaskAndTat(slno, winum, remarks, request, "CIF_CREATION", "CIF_COMPLETE");
        if (a <= 0) {
            throw new RuntimeException("F");
        }

        vehicleLoanQueueDetailsService.createQueueEntry(winum, slno, remarks, usd.getPPCNo(), fromQueue, toQueue);
        loanWaiverService.releaseSubQueueLocks(slno, usd.getPPCNo());
        return new ResponseDTO("S", message);
    }

//    @Override
//    public ResponseDTO saveCustDoc(String winum, String foldername, String filename, String remarks, HttpServletRequest request) throws Exception {
//        return null;
//    }

    public ResponseDTO validateInputs(Long slno, String winum, String remarks, HttpServletRequest request) {
        List<VehicleLoanCIF> vehicleLoanCIFList = vehicleLoanCifService.findByWiNum(winum);
        if (vehicleLoanCIFList.size() <= 0) {
            return new ResponseDTO("F", "No data found under CIF Creation tab");
        }
        int totalVlCif = 0;
        for (VehicleLoanCIF vehicleLoanCIF : vehicleLoanCIFList) {
            if ("Y".equals(vehicleLoanCIF.getDelFlag())) {
                continue;
            }
            if (vehicleLoanCIF.getDecision() == null || vehicleLoanCIF.getDecision().isEmpty()) {
                return new ResponseDTO("F", "Please complete all rows under CIF Creation tab");
            } else {
                totalVlCif++;
            }
        }
        int totalTasks = 0;
        List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks = vehicleLoanSubqueueTaskService.getBySlno(slno);
        for (VehicleLoanSubqueueTask task : vehicleLoanSubqueueTasks) {
            if (task.getTaskType().equals("CIF_CREATION") && task.getCompletedUser() == null) {
                totalTasks++;
            }
        }
        if (totalTasks != totalVlCif) {
            //return new ResponseDTO("F","Please complete all rows under CIF Creation tab.");
        }
        if (totalVlCif < totalTasks) {
            return new ResponseDTO("F", "Please complete all rows under CIF Creation tab.");
        }

        return new ResponseDTO("S", "");
    }

    public int updateSubtaskAndTat(Long slno, String winum, String remarks, HttpServletRequest request, String taskType, String action) {
        int a = 0;
        List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks = vehicleLoanSubqueueTaskService.getBySlno(slno);

        for (VehicleLoanSubqueueTask vehicleLoanSubqueueTask : vehicleLoanSubqueueTasks) {
            if (vehicleLoanSubqueueTask.getTaskType().equalsIgnoreCase(taskType) && vehicleLoanSubqueueTask.getCompletedDate() == null) {
                a++;
                //VehicleLoanSubqueueTask vehicleLoanSubqueueTask1 = vehicleLoanSubqueueTask;
                vehicleLoanSubqueueTask.setCompletedDate(new Date());
                vehicleLoanSubqueueTask.setCompletedUser(usd.getPPCNo());
                vehicleLoanSubqueueTask.setStatus("COMPLETED");

                vehicleLoanSubqueueTaskService.saveSubTask(vehicleLoanSubqueueTask);
                vehicleLoanSubqueueTatService.updateTat(vehicleLoanSubqueueTask.getTaskId(), usd.getPPCNo(), winum, taskType, action, "", request);
            }
        }
        return a;
    }

    public ResponseDTO updateWaiver(WaiverDto waiverDto) throws Exception {
        return null;
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO savewism(Long slno, String winum, String remarks, String action, HttpServletRequest request) throws Exception {

        String fromQueue = "ACOPN", toQueue = "ACOPN", status = "", message = "";
        /*
        ResponseDTO responseDTO = validateInputs(slno,winum,remarks,request);
        if(responseDTO.getStatus().equals("F")){
            return responseDTO;
        }
        */
        //update subqueue table
        verifySanMod(slno, winum, remarks, action, request);
        if (action.equalsIgnoreCase("APPROVE")) {
            setEligibilitySm(slno);
        }
        int a = updateSubtaskAndTat(slno, winum, remarks, request, "SAN_MOD", action);
        if (a <= 0) {
            throw new RuntimeException("F");
        }
        vehicleLoanQueueDetailsService.createQueueEntry(winum, slno, remarks, usd.getPPCNo(), fromQueue, toQueue);
        loanWaiverService.releaseSubQueueLocks(slno, usd.getPPCNo());
        return new ResponseDTO("S", message);
    }

    public void verifySanMod(Long slno, String winum, String remarks, String action, HttpServletRequest request) {
        List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks = vehicleLoanSubqueueTaskService.getSubTaskByTypeAndStatus2(slno, "SAN_MOD", "PENDING");
        VehicleLoanSubqueueTask vehicleLoanSubqueueTask = vehicleLoanSubqueueTasks.get(0);//get the latest task id
        VehicleLoanSanMod vehicleLoanSanMod = vehicleLoanSanModService.findByTaskId(Long.parseLong(vehicleLoanSubqueueTask.getTaskId().toString()));
        vehicleLoanSanMod.setVDate(new Date());
        vehicleLoanSanMod.setVUser(usd.getPPCNo());
        vehicleLoanSanMod.setVRemarks(remarks);
        vehicleLoanSanMod.setDecision(action);
        vehicleLoanSanModService.save(vehicleLoanSanMod);
    }

    public void setEligibilitySm(long slno) {
        Optional<EligibilityDetails> eligibilityDetailsdata = eligibilityDetailsService.findBySlno(slno);

        List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks = vehicleLoanSubqueueTaskService.getSubTaskByTypeAndStatus2(slno, "SAN_MOD", "PENDING");
        VehicleLoanSubqueueTask vehicleLoanSubqueueTask = vehicleLoanSubqueueTasks.get(0);//get the latest task id
        VehicleLoanSanMod vehicleLoanSanMod = vehicleLoanSanModService.findByTaskId(Long.parseLong(vehicleLoanSubqueueTask.getTaskId().toString()));

        if (eligibilityDetailsdata.isPresent()) {
            EligibilityDetails eligibilityDetails = eligibilityDetailsdata.get();
            eligibilityDetails.setSmCardRate(eligibilityDetails.getSancCardRate());
            eligibilityDetails.setSmEmi(vehicleLoanSanMod.getRevisedEmi());
            eligibilityDetails.setSmTenor(Integer.parseInt(vehicleLoanSanMod.getRevisedTenor().toString()));
            eligibilityDetails.setSmSancAmount(vehicleLoanSanMod.getRevisedSanAmt());
            eligibilityDetailsService.save(eligibilityDetails);
        } else {
            log.error("Existing eligibility record not found in setEligibilitysm");
            throw new RuntimeException("F");
        }
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO acctlabelsave(AcctLabelDTO acctLabelDTO, HttpServletRequest request) throws Exception {

        acctLabelDTO.makeLabelTextUppercase();
        List<String> list = new ArrayList<>();

        String wiNum = acctLabelDTO.getWiNum();
        Long slno = acctLabelDTO.getSlno();
        if (wiNum == null || slno <= 0) {
            return new ResponseDTO("F", "Invalid inputs");
        }
        VehicleLoanMaster master = vehicleLoanMasterService.findById(slno);
        if (master.getAccNumber() != null) {
            return new ResponseDTO("F", "Not allowed after account opening");
        }
        vehicleLoanAcctLabelRepository.deleteBySlno(slno);
        for (AcctLabelDTO.AcctLabel acctLabel : acctLabelDTO.getAcctLabels()) {
            if (list.contains(acctLabel.getAcctLabel()) || acctLabel.getLabelText() == null || acctLabel.getLabelText().trim().isEmpty()) {
                log.error("Invalid label details found");
                throw new RuntimeException("F");
            }
            if(acctLabel.getAcctLabel()!=null ) {
                VehicleLoanAcctLabels vehicleLoanAcctLabel = new VehicleLoanAcctLabels();
                vehicleLoanAcctLabel.setSlno(slno);
                vehicleLoanAcctLabel.setWiNum(wiNum);
                vehicleLoanAcctLabel.setAcctLabel(acctLabel.getAcctLabel());
                vehicleLoanAcctLabel.setLabeltext(acctLabel.getLabelText());
                vehicleLoanAcctLabel.setCmDate(new Date());
                vehicleLoanAcctLabel.setCmUser(usd.getPPCNo());
                vehicleLoanAcctLabelRepository.save(vehicleLoanAcctLabel);
            }
        }
        return new ResponseDTO("S", "Labels are saved successfully");
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO performAccOpening(Long slno, String winum, HttpServletRequest request) throws Exception {
        log.info("Performing Account opening for: {}", winum);
        validateAccOpeningRequest(slno, winum);

        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(slno, "N");
        if (vehicleLoanAccount != null) {
                /*
                vehicleLoanAccount.setDelflag("Y");
                accountOpeningRepository.save(vehicleLoanAccount);
                 */
            return new ResponseDTO("F", "Already attempted earlier");
        }
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findById(slno);
        VehicleLoanApplicant applicant = vehicleLoanMaster.getApplicants().stream().filter(i -> "A".equals(i.getApplicantType())).collect(Collectors.toList()).get(0);
        String s = fetchRepository.getAccountNumAndOpenDate(applicant.getCifId(), winum + append);
        if (s != null && !s.equals("0.00")) {//account is already present in gam with our winum
            String foracid = s.split("\\|")[0];
            String acctopendate = s.split("\\|")[1];
            if (foracid == null || foracid.trim().length() != 16) {
                return new ResponseDTO("F", "Unable to fetch account number from gam");
            }
            VehicleLoanAccount vehicleLoanAccount_ = new VehicleLoanAccount();
            vehicleLoanAccount_.setSlno(slno);
            vehicleLoanAccount_.setWiNum(winum);
            vehicleLoanAccount_.setCmDate(new Date());
            vehicleLoanAccount_.setCmUser(usd.getPPCNo());
            vehicleLoanAccount_.setDelflag("N");
            vehicleLoanAccount_.setAccopenapiuuid(winum + append);
            vehicleLoanAccount_.setIpaddress(CommonUtils.getClientIp(request));
            vehicleLoanAccount_.setVehicleLoanMaster(vehicleLoanMaster);
            accountOpeningRepository.save(vehicleLoanAccount_);

            DateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            vehicleLoanMaster.setAccOpened("Y");
            vehicleLoanMaster.setAccOpenDate(format.parse(acctopendate));
            vehicleLoanMaster.setAccNumber(foracid);
            vehicleLoanMasterRepo.save(vehicleLoanMaster);
            return new ResponseDTO("S", foracid);
        }

        AccOpeningRequest accOpeningRequest = createAccOpeningReqObject(slno, winum);
        AccOpeningResult accOpeningResult = callAccOpeningApi(accOpeningRequest, CommonUtils.getClientIp(request));
        if ("200".equals(accOpeningResult.getCode())) {

            VehicleLoanAccount vehicleLoanAccount_ = new VehicleLoanAccount();
            vehicleLoanAccount_.setSlno(slno);
            vehicleLoanAccount_.setWiNum(winum);
            vehicleLoanAccount_.setCmDate(new Date());
            vehicleLoanAccount_.setCmUser(usd.getPPCNo());
            vehicleLoanAccount_.setDelflag("N");
            vehicleLoanAccount_.setAccopenapiuuid(accOpeningRequest.getRequest().getUUID());
            vehicleLoanAccount_.setIpaddress(CommonUtils.getClientIp(request));
            vehicleLoanAccount_.setVehicleLoanMaster(vehicleLoanMaster);
            accountOpeningRepository.save(vehicleLoanAccount_);

            vehicleLoanMaster.setAccOpened("Y");
            vehicleLoanMaster.setAccOpenDate(new Date());
            vehicleLoanMaster.setAccNumber(accOpeningResult.getAccNo());
            vehicleLoanMasterRepo.save(vehicleLoanMaster);
            return new ResponseDTO("S", accOpeningResult.getAccNo());
        } else {
            String apiErrMsg = accOpeningResult.getDesc();
            return new ResponseDTO("F", apiErrMsg);
        }

    }

    public AccOpeningRequest createAccOpeningReqObject(Long slno, String winum) {
        String sysdateDDMMYYYY = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        sysdateDDMMYYYY = formatter.format(new Date());
        String uuid = winum + append;
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(slno);
        VehicleLoanApplicant applicant = vehicleLoanMaster.getApplicants().stream().filter(i -> "A".equals(i.getApplicantType())).collect(Collectors.toList()).get(0);
        EligibilityDetails eligibilityDetails = new EligibilityDetails();
        Optional<EligibilityDetails> eligibilityDetailsdata = eligibilityDetailsService.findBySlno(slno);

        if (!eligibilityDetailsdata.isPresent()) {
            log.error("Existing eligibility record not found in createAccOpeningReqObject");
            throw new RuntimeException("Existing eligibility record not found in createAccOpeningReqObject");
        }

        eligibilityDetails = eligibilityDetailsdata.get();


        String program = vehicleLoanMaster.getApplicants().stream().filter(i -> !"NONE".equals(i.getVlProgram().getLoanProgram())).collect(Collectors.toList()).get(0).getVlProgram().getLoanProgram();

        VehicleLoanDetails loanDetails = loanDetailsService.findBySlnoAndDelFlg(slno);
        String foirType = loanDetails.getFoirType();//Y - foir, N- Non foir
        String foirTypeMybank = ("Y".equals(foirType) ? "F" : "N");//F-Foir, N-Non-foir


        Map<String, String> vlaccountmas_mybank = fetchRepository.fetchVlaccountmas(program, foirTypeMybank, applicant.getResidentFlg());
        if (vlaccountmas_mybank == null) {
            log.error("VL account master is not entered for the given combination");
            throw new RuntimeException("Please enter VL account master for the given combination");//,"VL account master is not entered for the given combination");
        }
        AccOpeningRequest accOpeningRequest = new AccOpeningRequest();
        accOpeningRequest.setSlno(slno.toString());
        accOpeningRequest.setWorkItemNumber(winum);
        accOpeningRequest.setApiName("accCreation");
        accOpeningRequest.setMock(false);

        AccOpeningRequest.Request request = new AccOpeningRequest.Request();
        request.setMerchant_code(merchantCode);
        request.setMerchant_name(merchantName);
        request.setSchm_code(vlaccountmas_mybank.get("schm_code"));
        String actualSancAmount = ((eligibilityDetails.getSmSancAmount() == null || eligibilityDetails.getSmSancAmount().equals(BigDecimal.ZERO)) ?
                eligibilityDetails.getSancAmountRecommended() : eligibilityDetails.getSmSancAmount()).toString();
        String actualRoi = eligibilityDetails.getSancCardRate().toString();
        String actualRoi_part1 = actualRoi, actualRoi_part2 = "";
        if (actualRoi.contains(".")) {
            actualRoi_part1 = actualRoi.split("\\.")[0];
            actualRoi_part2 = "0." + actualRoi.split("\\.")[1];
        }
        request.setAmount(actualSancAmount);
        request.setEqInstallFlg("Y");
        String inttblcode = "K" + actualRoi_part1 + "00";
        if (actualRoi_part1.length() == 1) {
            inttblcode = "K0" + actualRoi_part1 + "00";
        }

        request.setIntTblCode(inttblcode);
        request.setLoanPeriodDays("0");
        int tenor = eligibilityDetails.getSancTenor();
        if (eligibilityDetails.getSmTenor() != null && eligibilityDetails.getSmTenor() > 0) {
            tenor = eligibilityDetails.getSmTenor();
        }
        request.setLoanPeriodMonths(tenor + "");//convert to string
        request.setGl_sub_head_code(vlaccountmas_mybank.get("gl_code"));
        request.setAcctDrPrefPcnt(actualRoi_part2);
        request.setCif_id(applicant.getCifId());
        request.setIntCollFlg("Y");
        request.setSol_id(vehicleLoanMaster.getSolId());
        request.setUUID(uuid);
        request.setCurrencyCode("INR");

        AccOpeningRequest.RepmtRec repmtRec = new AccOpeningRequest.RepmtRec();
        String interestStartDt = fetchRepository.next5th();
        repmtRec.setIntStartDt(interestStartDt);
        repmtRec.setIntereststartDate(interestStartDt.split("-")[0]);
        String instalmentstartDt = fetchRepository.instalmentstartdate(sysdateDDMMYYYY);
        repmtRec.setInstallstartDate(instalmentstartDt.split("-")[0]);
        repmtRec.setInstallStartDt(instalmentstartDt);
        repmtRec.setFrequency("M");
        repmtRec.setNoOfInstall(tenor + "");
        repmtRec.setInstallmentId("EIDEM");

        AccOpeningRequest.RepmtRec[] a = new AccOpeningRequest.RepmtRec[1];
        a[0] = repmtRec;
        request.setRepmtRec(a);


        List<VehicleLoanApplicant> applicants = vehicleLoanMaster.getApplicants();
        List<AccOpeningRequest.RelPartyRec> relPartyRecList = new ArrayList<AccOpeningRequest.RelPartyRec>();
        int numRelatedParties = 0;
        for (VehicleLoanApplicant vehicleLoanApplicant : applicants) {
            if (vehicleLoanApplicant.getApplicantType().equals("A") || "Y".equals(vehicleLoanApplicant.getDelFlg())) {
                continue;
            }
            String relPartyType = "", relPartyCode = "", custid = "", name = "", titleprefix = "";
            if (vehicleLoanApplicant.getApplicantType().equals("C")) {
                relPartyType = "J";
            } else {
                relPartyType = "G";
            }
            relPartyCode = vehicleLoanApplicant.getRelationWithApplicant();
            custid = vehicleLoanApplicant.getCifId();
            name = vehicleLoanApplicant.getApplName();
            VehicleLoanBasic vehicleLoanBasic = vehicleLoanApplicant.getBasicapplicants();
            titleprefix = vehicleLoanBasic.getSalutation();

            AccOpeningRequest.RelPartyRec relPartyRec = new AccOpeningRequest.RelPartyRec();
            relPartyRec.setRelPartyType(relPartyType);
            relPartyRec.setRelPartyCode(relPartyCode);
            relPartyRec.setCustId(custid);
            relPartyRec.setName(name);
            relPartyRec.setTitlePrefix(titleprefix);
            AccOpeningRequest.RelPartyContactInfo relPartyContactInfo = new AccOpeningRequest.RelPartyContactInfo();
            relPartyContactInfo.setEmailAddr("");
            relPartyContactInfo.setCity("");
            relPartyContactInfo.setAddr1("");
            relPartyContactInfo.setAddr2("");
            relPartyContactInfo.setStateProv("");
            relPartyContactInfo.setPostalCode("");
            relPartyContactInfo.setAddrType("");
            relPartyRec.setRelPartyContactInfo(relPartyContactInfo);

            relPartyRecList.add(relPartyRec);
            numRelatedParties++;
        }
        AccOpeningRequest.RelPartyRec[] relPartyRecArray = relPartyRecList.toArray(new AccOpeningRequest.RelPartyRec[]{});
        request.setRelPartyRec(relPartyRecArray);

        AccOpeningRequest.LoanAcctAdd_CustomData customData = new AccOpeningRequest.LoanAcctAdd_CustomData();
        customData.setFree_text5("N");
        customData.setFree_text3("");
        customData.setFree_text3("N");
        customData.setFree_text2("VL");
        customData.setFree_text1(vlaccountmas_mybank.get("com_real_est_code"));

        customData.setFree_text13(sysdateDDMMYYYY);
        customData.setFree_text14(vlaccountmas_mybank.get("acct_rate_score"));
        customData.setDrawing_power_ind("E");
        customData.setGuard_cover_code(vlaccountmas_mybank.get("guarantee_cover_code"));
        customData.setPurpose_of_advn(vlaccountmas_mybank.get("risk_category_code") == null ? "" : vlaccountmas_mybank.get("risk_category_code"));
        customData.setFree_text4("");

        VehicleLoanRepayment vehicleLoanRepayment = repaymentService.getRepaymentDetails(slno);
        if ("SIBL".equals(vehicleLoanRepayment.getBankName())) {//sib is selected
            String foracid = vehicleLoanRepayment.getAccountNumber();
            int nre = fetchRepository.accountIsNre(foracid);
            customData.setRepay_method("E");
            customData.setRepay_oper_acct(foracid);
            if (nre == 1) {//NRE account
                customData.setRepay_holdin_oper_acct_flg("N");
            } else {
                customData.setRepay_holdin_oper_acct_flg("Y");
            }
        }
        customData.setLimit_value(actualSancAmount);
        customData.setLimit_currency("INR");
        customData.setFree_text12("");
        customData.setOccupation_code(vlaccountmas_mybank.get("occupation_code"));
        customData.setBorrower_category_code(vlaccountmas_mybank.get("borrower_category") == null ? "" : vlaccountmas_mybank.get("borrower_category"));
        customData.setFree_text15("");
        customData.setDrawing_power_currency("INR");
        customData.setLimit_sanct_expiry_date("");

        String limSancExpDate = fetchRepository.limSancExpDate(instalmentstartDt, tenor);
        customData.setLimit_sanct_expiry_date(limSancExpDate);
        customData.setLimit_currency("INR");
        customData.setLimit_pen_days("");
        customData.setLimit_desc("");
        customData.setAcct_limit_entered("1");
        customData.setFree_code10(vlaccountmas_mybank.get("oth_cr_fclty"));
        customData.setReview_date("");
        customData.setMode_of_oper_code(vehicleLoanMaster.getModeOper());
        customData.setLimit_prefix("");
        customData.setLimit_suffix("");

        String sanUser = vehicleLoanMaster.getSanUser();
        String sanDate = formatter.format(vehicleLoanMaster.getSanDate());
        String sanDesigAsOnSanDate = fetchRepository.getDesig(sanUser, sanDate);
        String sanCode = fetchRepository.getSanCode(sanDesigAsOnSanDate);
        if (sanCode == null || sanCode.trim().isEmpty()) {
            sanCode = "2399";//OTHERS
        }
        customData.setLimit_sanct_code(sanCode);
        customData.setChannel_id(channelID);
        customData.setMode_advn(vlaccountmas_mybank.get("mode_advance") == null ? "" : vlaccountmas_mybank.get("mode_advance"));
        customData.setLimit_sanct_ref_num(winum + append);
        customData.setIndustry_type(vlaccountmas_mybank.get("industry_type"));
        customData.setSanct_date(sanDate);
        customData.setEmployee_ppc(applicant.getCanvassedppc() == null ? "" : applicant.getCanvassedppc());
        String limSanAuthCode = "";
        String stp = vehicleLoanMaster.getStp();
        if ("STP".equals(stp)) {
            limSanAuthCode = "004";
        } else {
            limSanAuthCode = "008";
        }
        customData.setLimit_sanct_auth_code(limSanAuthCode);

        customData.setSub_sector_code(vlaccountmas_mybank.get("sub_sector_code") == null ? "" : vlaccountmas_mybank.get("sub_sector_code"));
        customData.setLimit_document_date(formatter.format(vehicleLoanMaster.getDocCompDate()));
        customData.setSector_code(vlaccountmas_mybank.get("sector_code") == null ? "" : vlaccountmas_mybank.get("sector_code"));
        customData.setMis_entered("1");
        customData.setLimit_pen_months("");
        customData.setType_advn(vlaccountmas_mybank.get("advance_type") == null ? "" : vlaccountmas_mybank.get("advance_type"));
        customData.setLedger_num("");
        customData.setFree_code9(vlaccountmas_mybank.get("spl_cls_code"));
        customData.setFree_code4(vlaccountmas_mybank.get("nabard_code"));
        customData.setAcct_mgr_userid("AUD2011");
        customData.setFree_code5(vlaccountmas_mybank.get("ssi_code"));
        customData.setFree_code6(vlaccountmas_mybank.get("agri_code"));
        customData.setFree_code7(vlaccountmas_mybank.get("occupation_sub_code") == null ? "" : vlaccountmas_mybank.get("occupation_sub_code"));
        customData.setFree_code1(vlaccountmas_mybank.get("banking_arngmt_code"));
        customData.setFree_code2(vlaccountmas_mybank.get("dri_scheme_code"));
        customData.setRemarks(winum);
        customData.setNature_advn(vlaccountmas_mybank.get("nature_advance") == null ? "" : vlaccountmas_mybank.get("nature_advance"));
        customData.setFree_code3(vlaccountmas_mybank.get("ac_relation_code"));


        List<VehicleLoanAcctLabels> vehicleLoanAcctLabels = vehicleLoanMaster.getVehicleLoanAcctLabels();
        int labelCount = vehicleLoanAcctLabels.size();
        AccOpeningRequest.acct_label[] labelarray = new AccOpeningRequest.acct_label[labelCount];
        int i = 0;
        List<Misrct> misrctList = misrctService.getCodeValuesByType("LB");
        for (VehicleLoanAcctLabels label : vehicleLoanAcctLabels) {
            AccOpeningRequest.acct_label label_ = new AccOpeningRequest.acct_label();
            String labelcode = label.getAcctLabel();
            Misrct misrct=null;
            if("LOS_SOURCE".equalsIgnoreCase(labelcode)){
                misrct= new Misrct();
                misrct.setCodedesc("LOS_SOURCE");
                misrct.setCodevalue("POWER DRIVE");
            }else {
                misrct = misrctList.stream().filter(rct -> labelcode.equals(rct.getCodevalue()) && "N".equals(rct.getDelflag())).collect(Collectors.toList()).get(0);
            }
            label_.setLabel_name(misrct.getCodedesc());
            label_.setLabel_value(label.getLabeltext());
            labelarray[i] = label_;
            i++;
        }
        customData.setAcct_label(labelarray);

        request.setLoanAcctAddCustomData(customData);
        accOpeningRequest.setRequest(request);
        return accOpeningRequest;
    }

    @Transactional
    public AccOpeningResult callAccOpeningApi(AccOpeningRequest accOpeningRequest, String reqIP) {

        log.info("Performing Account opening api call for: {}", accOpeningRequest.getWorkItemNumber());

        try {
            AccOpeningResponse accOpeningResponse = accOpeningApiClient.performAccountOpening(accOpeningRequest);
            String foracid = "", temp = accOpeningResponse.getResponse().getBody().getLoan_Account_Number();
            if ("200".equals(accOpeningResponse.getResponse().getStatus().getCode()) && temp != null && temp.length() == 16) {
                foracid = accOpeningResponse.getResponse().getBody().getLoan_Account_Number();
            } else if ("200".equals(accOpeningResponse.getResponse().getStatus().getCode()) &&
                    "Duplicate UUID Success".equalsIgnoreCase(accOpeningResponse.getResponse().getBody().getStatus()) &&
                    (accOpeningResponse.getResponse().getBody().getMessage().contains("Loan Account Already Created Using UUID"))
            ) {
                //foracid=StringUtils.right(accOpeningResponse.getResponse().getBody().getMessage(),16);
                String t = accOpeningResponse.getResponse().getBody().getMessage();
                foracid = t.substring(t.length() - 16);
            }
            AccOpeningResult accOpeningResult = new AccOpeningResult();
            if (foracid != null && foracid.trim().length() == 16) {
                accOpeningResult.setCode("200");
                accOpeningResult.setAccNo(foracid);
            } else {
                accOpeningResult.setCode(accOpeningResponse.getResponse().getStatus().getCode());
                accOpeningResult.setDesc(accOpeningResponse.getResponse().getStatus().getDesc());
            }
            return accOpeningResult;
        } catch (Exception e) {
            log.error("exception acc opening api ", e);
            throw new RuntimeException("Error performing acc opening api check", e);
        }
    }

    private void validateAccOpeningRequest(Long slno, String winum) {

        if (slno == null ||
                winum == null) {
            log.error("Invalid winum or slno: {}", slno);
            throw new IllegalArgumentException("All parameters are required for Account opening API");
        }

        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(slno);
        if (vehicleLoanMaster.getAccNumber() != null) {
            String msg = "Account " + vehicleLoanMaster.getAccNumber() + " is already opened for this WI";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        /*
        List<VehicleLoanSubqueueTask> pendingvehicleLoanSubqueueTasks = vehicleLoanSubqueueTaskService.getSubTaskByTypeAndStatus2(slno,"SAN_MOD","PENDING");
        if(pendingvehicleLoanSubqueueTasks!=null && pendingvehicleLoanSubqueueTasks.size()>0){
            String msg="Account opening is not possible since pending sanction modification records exist";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        List<VehicleLoanSubqueueTask> vehicleLoanSubqueueTasks = vehicleLoanSubqueueTaskService.getSubTaskByTypeAndStatus2(slno,"SAN_MOD","COMPLETED");
        if(vehicleLoanSubqueueTasks==null || vehicleLoanSubqueueTasks.size()==0){
            String msg="Account opening is not possible since Sanction Modification section is incomplete";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

         */
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO disbursement(Long slno, String winum, HttpServletRequest request) throws Exception {
        VehicleLoanMaster master = vehicleLoanMasterService.findById(slno);
        String reqIp = CommonUtils.getClientIp(request);
        if (master.getAccNumber() == null) {
            String msg = "Account is not opened";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(slno, "N");
        if ("SUCCESS".equals(vehicleLoanAccount.getDisbflag())) {
            String msg = "Disbursement has already happened";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if ("INITIATED".equals(vehicleLoanAccount.getDisbflag())) {
            String msg = "Disbursement is already initiated";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        /*
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String accOpenDtDDMMYYYY=format.format(master.getAccOpenDate());
        Map<String,String> cbs =  checkdtdhtd(BogPoolAcc,winum+"_1","C",accOpenDtDDMMYYYY);
        if(cbs!=null){
            String msg="Disbursement is found in cbs, tran id:"+cbs.get("tran_id")+", tran date:"+cbs.get("tran_date")+", amount:"+cbs.get("tran_amt");
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
         */

        if (fetchRepository.loanIsDisbursed(master.getAccNumber()) == 1) {
            String msg = "The loan account is already disbursed, please check ";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }


        VehicleLoanRepayment vehicleLoanRepayment = repaymentService.getRepaymentDetails(Long.valueOf(slno));
        Optional<NACHMandate> op = nachMandateService.getNACHMandateBySlno(slno);
        NACHMandate nachMandate = null;
        if (op.isPresent()) {
            nachMandate = op.get();
        }
//        if(!vehicleLoanRepayment.getIfscCode().startsWith("SIBL") && (nachMandate == null || !"Authorized".equalsIgnoreCase(nachMandate.getStatus()))){
//            String msg="Please set Nach mandate before disbursement";
//            log.error(msg);
//            throw new RuntimeException(msg);
//        }
        // change made to accomodate the Manual Nach
        if (!vehicleLoanRepayment.getIfscCode().startsWith("SIBL") &&
                (nachMandate == null ||
                        !("Authorized".equalsIgnoreCase(nachMandate.getStatus()) && "DIGITAL".equals(nachMandate.getMandateMode()) ||
                                "Manual Authorized".equalsIgnoreCase(nachMandate.getStatus()) && "MANUAL".equals(nachMandate.getMandateMode())))) {
            String msg = "Please set Nach mandate before disbursement";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        String program = getProgram(winum, slno);
        if (program.equals("LOANFD")) {
            List<VehicleLoanFD> allFDs = vehicleLoanFDRepository.findByWiNumAndDelFlg(winum, "N");
            for (VehicleLoanFD fd : allFDs) {
                if (!"MARKED".equals(fd.getLienStatus())) {
                    String msg = "Please mark Lien before disbursement";
                    log.error(msg);
                    throw new IllegalArgumentException(msg);
                }
            }
        }


        DisbRequest disbRequest = createDisbrequest(slno, winum, reqIp);
        DisbResult disbResult = callDisbAPI(disbRequest, reqIp);
        if (disbResult != null && "200".equals(disbResult.getCode())) {
            vehicleLoanAccount.setDisbflag("INITIATED");
            vehicleLoanAccount.setDisbapiuuid(disbRequest.getRequest().getUUID());//(winum+"_1");
            vehicleLoanAccount.setDisbcmuser(usd.getPPCNo());
            vehicleLoanAccount.setDisbcmdate(new Date());
            accountOpeningRepository.save(vehicleLoanAccount);
            return new ResponseDTO("S", "");
        } else {
            return new ResponseDTO("F", "");
        }
    }

    public String getProgram(String wiNum, Long slNo) {
        List<VehicleLoanProgram> vps = VehicleLoanProgramservice.getVehicleLoanProgram(wiNum, slNo);
        vps = vps.stream().filter(program -> !"NONE".equals(program.getLoanProgram())).collect(Collectors.toList());
        if (vps.size() > 0)
            return vps.get(0).getLoanProgram();
        else
            throw new RuntimeException("Unable to fetch loan program");
    }

    public DisbResult callDisbAPI(DisbRequest disbRequest, String reqIP) {

        log.info("Performing callDisbAPI for: {}", disbRequest.getWorkItemNumber());

        try {
            DisbResponse disbResponse = disbApiClient.performDisbApi(disbRequest);
            String foracid = "";
            DisbResult disbResult = new DisbResult();
            if (disbResponse != null && disbResponse.getResponse() != null && disbResponse.getResponse().getStatus() != null && "200".equals(disbResponse.getResponse().getStatus().getCode())) {
                disbResult.setCode("200");
            } else {
                disbResult.setCode(disbResponse.getResponse().getStatus().getCode());
                disbResult.setDesc(disbResponse.getResponse().getStatus().getDesc());
            }
            return disbResult;
        } catch (Exception e) {
            log.error("exception callDisbAPI ", e);
            throw new RuntimeException("Error callDisbAPI", e);
        }
    }

    public DisbRequest createDisbrequest(Long slno, String winum, String reqIP) {
        DisbRequest disbRequest = new DisbRequest();
        disbRequest.setSlno(slno.toString());
        disbRequest.setMock(false);
        disbRequest.setApiName("disbursement");
        disbRequest.setWorkItemNumber(winum);

        VehicleLoanMaster master = vehicleLoanMasterService.findBySlno(slno);
        VehicleLoanVehicle vehicleLoanDetails = vehicleDetailsService.fetchExistingbyWinumandSlno(winum, slno);

        Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsService.findBySlno(slno);
        EligibilityDetails eligibility = new EligibilityDetails();
        if (eligibilityDetails_ != null) {
            eligibility = eligibilityDetails_.get();
        }

        BigDecimal upfrontFee = BigDecimal.ZERO;
        String upfrontFeestr = "";
        upfrontFee = fetchRepository.getTotalProcessingCharge(String.valueOf(slno), fetchRepository.getWIProgram(slno));
        if (upfrontFee != null) {
            upfrontFeestr = upfrontFee.toString();
        }
        String custominsuramt = "";
        if (vehicleLoanDetails.getCustomInsurance()) {
            custominsuramt = vehicleLoanDetails.getCustomInsuranceAmount();
        }


        DisbRequest.chargeData chargeData = new DisbRequest.chargeData();
        chargeData.setProcess_fee("");
        chargeData.setUpfront_fee(upfrontFeestr);
        chargeData.setProp_val_chrg(custominsuramt);
        chargeData.setExpert_val_chrg("");
        chargeData.setDevn_chrg("");
        String stampAmt = "";
        if (master.getStampAmt() != null && "D".equals(master.getDocMode())) {
            stampAmt = master.getStampAmt().toString();
        }
        chargeData.setDocu_chrg(stampAmt);
        chargeData.setCersai_chrg("");
        chargeData.setCic_chrg("");
        chargeData.setNesl_chrg("");
        chargeData.setProc_fee_arrear("");
        chargeData.setQly_insp_chrg("");
        chargeData.setYly_insp_chrg("");

        VehicleLoanApplicant applicant = master.getApplicants().stream().filter(i -> "A".equals(i.getApplicantType())).collect(Collectors.toList()).get(0);
        DisbRequest.intrabank_data intrabank_data = new DisbRequest.intrabank_data();
        intrabank_data.setAdditionalRemarks("");
        intrabank_data.setCustomer_account_number(BogPoolAcc);

        intrabank_data.setCustomer_name(vehicleLoanDetails.getDealerName());
        intrabank_data.setRemarks("");
        intrabank_data.setTran_particulars(vehicleLoanDetails.getDealerName().substring(0, Math.min(vehicleLoanDetails.getDealerName().length(), 45)));


        DisbRequest.Request innerReq = new DisbRequest.Request();
        //innerReq.setUUID(winum+"_1");

        innerReq.setUUID(String.valueOf(System.currentTimeMillis()));
        innerReq.setChargeFlag("Y");
        innerReq.setChargeData(chargeData);
        innerReq.setChecker_user(usd.getPPCNo());
        innerReq.setFinalDisbFlg("N");
        innerReq.setDisbursementMode("S");
        innerReq.setFirstDisbFlg("N");
        innerReq.setGrossNetDisbt("N");
        innerReq.setIntrabank_data(intrabank_data);
        innerReq.setLoan_acct_num(master.getAccNumber());
        innerReq.setLoan_disb_amt(String.valueOf(eligibility.getSmSancAmount() == null || eligibility.getSmSancAmount().equals(BigDecimal.ZERO) ? eligibility.getSancAmountRecommended() : eligibility.getSmSancAmount()));

        innerReq.setMerchantCode(merchantCode);
        innerReq.setMerchantName(merchantName);
        innerReq.setMaker_user(master.getBrDocCmUser());
        innerReq.setSolId(master.getSolId());

        disbRequest.setRequest(innerReq);
        return disbRequest;

    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO performDisbStatusEnquiry(Long slno, String winum, HttpServletRequest request) throws Exception {

        VehicleLoanMaster master = vehicleLoanMasterService.findById(slno);
        String reqIp = CommonUtils.getClientIp(request);
        if (master.getAccNumber() == null) {
            String msg = "Account is not opened";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(slno, "N");
        if (vehicleLoanAccount.getDisbflag() == null) {
            String msg = "Disbursement is not initiated";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        /*
        long duration  = (new Date()).getTime() - vehicleLoanAccount.getDisbcmdate().getTime();
        System.out.println("vehicleLoanAccount.getDisbcmdate().getTime()="+vehicleLoanAccount.getDisbcmdate().getTime()+",(new Date()).getTime()="+(new Date()).getTime());
        long diffInMinutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(duration);
        System.out.println("diffInMinutes="+diffInMinutes);
        if(diffInMinutes<2){
            String msg="After clicking the disburse button, please wait for 2 minutes before proceeding with status enquiry";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        */


        DisbEnqRequest disbEnqRequest = createDisbEnqRequest(slno, winum, reqIp);
        DisbEnqResponse disbEnqResponse = disbApiClient.performDisbStatusEnquiryApi(disbEnqRequest);


        if (disbEnqResponse != null && disbEnqResponse.getResponse() != null && disbEnqResponse.getResponse().getStatus() != null
                && "200".equals(disbEnqResponse.getResponse().getStatus().getCode())
                && "SUCCESS".equalsIgnoreCase(disbEnqResponse.getResponse().getBody().getMessage())
        ) {//&& "INITIATED".equals(disbEnqResponse.getResponse().getBody().getDisbursementStatus())
            String tranRefNo = "", cbsRefNo = "", processedTime = "";
            tranRefNo = disbEnqResponse.getResponse().getBody().getTranRefNo();
            cbsRefNo = disbEnqResponse.getResponse().getBody().getCbsRefNo();
            processedTime = disbEnqResponse.getResponse().getBody().getProcessedTime();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date processedTimeDt = format.parse(processedTime);

            vehicleLoanAccount.setDisbflag("SUCCESS");
            vehicleLoanAccount.setDisbdate(processedTimeDt);
            vehicleLoanAccount.setTranRefNo(tranRefNo);
            vehicleLoanAccount.setCbsRefNo(cbsRefNo);
            vehicleLoanAccount.setProcessedTime(processedTimeDt);
            vehicleLoanAccount.setDisbenqcmuser(usd.getPPCNo());
            vehicleLoanAccount.setDisbenqcmdate(new Date());

            String dateportion = processedTime.split(" ")[0];
            BigDecimal tran_amt = fetchRepository.getDisbursedAmountFromCBS(tranRefNo, dateportion);
            if (tran_amt.equals(BigDecimal.ZERO)) {
                Date datePortionDt = new SimpleDateFormat("yyyy-MM-dd").parse(dateportion);
                LocalDateTime ldt = LocalDateTime.ofInstant(datePortionDt.toInstant(), ZoneId.systemDefault());
                ldt = ldt.minusDays(1);
                datePortionDt = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                String datePortionMinus1Day = new SimpleDateFormat("yyyy-MM-dd").format(datePortionDt);
                tran_amt = fetchRepository.getDisbursedAmountFromCBS(tranRefNo, datePortionMinus1Day);
            }
            if (tran_amt.equals(BigDecimal.ZERO)) {
                String msg = "Unable to fetch disbursed amount from CBS";
                log.error(msg);
                throw new IllegalArgumentException(msg);
            }
            vehicleLoanAccount.setDisbursedAmount(tran_amt);
            accountOpeningRepository.save(vehicleLoanAccount);

            Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsService.findBySlno(slno);
            EligibilityDetails eligibilityDetails = new EligibilityDetails();
            if (eligibilityDetails_ != null) {
                eligibilityDetails = eligibilityDetails_.get();
            }
            String actualSancAmount = ((eligibilityDetails.getSmSancAmount() == null || eligibilityDetails.getSmSancAmount().equals(BigDecimal.ZERO)) ?
                    eligibilityDetails.getSancAmountRecommended() : eligibilityDetails.getSmSancAmount()).toString();
            master.setDisbAmt(actualSancAmount);
            master.setDisbDate(processedTimeDt);
            master.setDisbFlg("SUCCESS");
            vehicleLoanMasterService.saveLoan(master);
            return new ResponseDTO("S", tranRefNo + "," + tran_amt);
        } else if ("406".equals(disbEnqResponse.getResponse().getStatus().getCode())) {//failure

            vehicleLoanAccount.setDisbenqcmuser(usd.getPPCNo());
            vehicleLoanAccount.setDisbenqcmdate(new Date());
            vehicleLoanAccount.setDisbflag("FAILED");
            accountOpeningRepository.save(vehicleLoanAccount);
            String disbursementStatus = disbEnqResponse.getResponse().getBody().getDisbursementStatus();
            if (disbursementStatus == null) {
                disbursementStatus = "Failed";
            }
            return new ResponseDTO("F", disbursementStatus);

        } else if ("202".equals(disbEnqResponse.getResponse().getStatus().getCode())) {//pending

            vehicleLoanAccount.setDisbenqcmuser(usd.getPPCNo());
            vehicleLoanAccount.setDisbenqcmdate(new Date());
            accountOpeningRepository.save(vehicleLoanAccount);
            String disbursementStatus = "Loan disbursement is under progress";//disbEnqResponse.getResponse().getBody().getDisbursementStatus();//this is not returned properly in all cases
            if (disbursementStatus == null) {
                disbursementStatus = "Pending";
            }
            return new ResponseDTO("P", disbursementStatus);

        } else {//we assume it is still pending, or, no proper response received from esb
            vehicleLoanAccount.setDisbenqcmuser(usd.getPPCNo());
            vehicleLoanAccount.setDisbenqcmdate(new Date());
            accountOpeningRepository.save(vehicleLoanAccount);
            return new ResponseDTO("P", "Please try again later");
        }
    }

    public DisbEnqRequest createDisbEnqRequest(Long slno, String winum, String reqIP) {
        DisbEnqRequest disbEnqRequest = new DisbEnqRequest();
        disbEnqRequest.setMock(false);
        disbEnqRequest.setApiName("disbursementenq");
        disbEnqRequest.setSlno(String.valueOf(slno));
        disbEnqRequest.setWorkItemNumber(winum);
        DisbEnqRequest.Request innerReq = new DisbEnqRequest.Request();
        String uuid = "";
        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(slno, "N");
        uuid = vehicleLoanAccount.getDisbapiuuid();//pass same uuid of the disbursement request
        innerReq.setUUID(uuid);
        //innerReq.setUUID(winum+"_1");
        innerReq.setApiName("loan-disbursement-module-api");

        innerReq.setMerchantCode(merchantCode);
        innerReq.setMerchantName(merchantName);

        disbEnqRequest.setRequest(innerReq);
        return disbEnqRequest;

    }

    public void beforePerformNeft(Long slno, String winum, String beneficiaryType, String dneftamt, String mneftamt, String accnum, String ifsc,
                                  String accname, String manufMobile, String disbType, String add1, String add2, String add3, HttpServletRequest request,
                                  String dealername, String dealernamermk, String dstcode, String dsacode, String dealercode, String dealersubcode,
                                  String dealeracc, String dealerifsc, String manmake)
            throws Exception {
        validateNeftInputs(slno, winum, beneficiaryType, dneftamt, mneftamt, accnum, ifsc, accname, manufMobile, disbType, add1, add2, add3);
        VehicleLoanNeftInputs vehicleLoanNeftInputs = vehicleLoanNeftRepository.findBySlnoAndDelflag(slno, "N");
        if (vehicleLoanNeftInputs != null) {
            //it means details are already updated, no need to update again
        } else {
            VehicleLoanNeftInputs vehicleLoanNeftInputs_ = new VehicleLoanNeftInputs();
            vehicleLoanNeftInputs_.setWiNum(winum);
            vehicleLoanNeftInputs_.setSlno(slno);
            vehicleLoanNeftInputs_.setDisbType(disbType);


            vehicleLoanNeftInputs_.setDealerName(dealername);
            vehicleLoanNeftInputs_.setDealerNameRmk(dealernamermk);
            vehicleLoanNeftInputs_.setDstCode(dstcode);
            vehicleLoanNeftInputs_.setDsaCode(dsacode);
            vehicleLoanNeftInputs_.setDealerCode(dealercode);
            vehicleLoanNeftInputs_.setDealerSubCode(dealersubcode);
            vehicleLoanNeftInputs_.setDealerAcc(dealeracc);
            vehicleLoanNeftInputs_.setDealerIfsc(dealerifsc);
            vehicleLoanNeftInputs_.setDealerAmount(new BigDecimal(dneftamt));

            vehicleLoanNeftInputs_.setManMake(manmake);
            vehicleLoanNeftInputs_.setManMobile(manufMobile);
            vehicleLoanNeftInputs_.setManAcc(accnum);
            vehicleLoanNeftInputs_.setManConfirmAcc(accnum);
            vehicleLoanNeftInputs_.setManIfsc(ifsc);
            vehicleLoanNeftInputs_.setManAccName(accname);
            vehicleLoanNeftInputs_.setAdd1(add1);
            vehicleLoanNeftInputs_.setAdd2(add2);
            vehicleLoanNeftInputs_.setAdd3(add3);
            vehicleLoanNeftInputs_.setManAmount(new BigDecimal(mneftamt));

            vehicleLoanNeftInputs_.setCmDate(new Date());
            vehicleLoanNeftInputs_.setCmUser(usd.getPPCNo());
            vehicleLoanNeftInputs_.setIpaddress(CommonUtils.getClientIp(request));
            vehicleLoanNeftInputs_.setDelflag("N");
            vehicleLoanNeftRepository.save(vehicleLoanNeftInputs_);
        }
    }


    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO performNeft(Long slno, String winum, String beneficiaryType, String dneftamt, String mneftamt, String accnum, String ifsc,
                                   String accname, String manufMobile, String disbType, String add1, String add2, String add3, HttpServletRequest request
    ) throws Exception {

        if (ifsc != null) {
            ifsc = ifsc.toUpperCase();
        }
        validateNeftInputs(slno, winum, beneficiaryType, dneftamt, mneftamt, accnum, ifsc, accname, manufMobile, disbType, add1, add2, add3);
        NeftRequest neftRequest = frameNeftRequest(slno, winum, beneficiaryType, dneftamt, mneftamt, accnum, ifsc, accname, manufMobile, add1, add2, add3);
        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(slno, "N");


        if (beneficiaryType.equals("D")) {
            VehicleLoanVehicle vehicle = vehicleDetailsService.fetchExistingbyWinumandSlno(winum, slno);
            String dealerCode = vehicle.getDealerCode();
            String dealerSubcode = vehicle.getDealerSubCode();
            String cityId = vehicle.getDealerCityId();
            String cityName = vehicle.getDealerCityName();
            String oemid = vehicle.getMakeId();
            if (fetchRepository.checkDealerBlocked(dealerCode, dealerSubcode, cityId, cityName, oemid) <= 0) {
                return new ResponseDTO("F", "This dealer has been blocked");
            }
        }


        if ("D".equals(beneficiaryType) && ("SUCCESS".equals(vehicleLoanAccount.getFiflag_dealer()) || "SUCCESS".equals(vehicleLoanAccount.getNeftflagdealer())) || "M".equals(beneficiaryType) && ("SUCCESS".equals(vehicleLoanAccount.getFiflag_manu()) || "SUCCESS".equals(vehicleLoanAccount.getNeftflagmanuf()))) {
            return new ResponseDTO("F", "Tran is already done");
        }


        //ensure that FI tran has not alredy happened in dtd/htd
        String sysdateDDMMYYYY = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        sysdateDDMMYYYY = formatter.format(vehicleLoanAccount.getCmDate());
        Map<String, String> fidetails = checkdtdhtd(BogPoolAcc, winum + "_" + beneficiaryType, "D", sysdateDDMMYYYY);
        if (fidetails != null) {//tran already present
            String tranId = (String) fidetails.get("tran_id");
            String tranDate = (String) fidetails.get("tran_date");
            String tranAmt = (String) fidetails.get("tran_amt");
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            // Date tranDateDt = format.parse(tranDate);
            LocalDateTime tranDateDt = null;
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
            tranDateDt = LocalDateTime.parse(tranDate + " 00:00:00.000", dateTimeFormatter);

            if (beneficiaryType.equals("D")) {
                if (new BigDecimal(tranAmt).equals(new BigDecimal(dneftamt))) {
                    //ok
                } else {
                    throw new RuntimeException("DTD Tran amount does not match the given amount");
                }
                vehicleLoanAccount.setFiflag_dealer("SUCCESS");
                vehicleLoanAccount.setFicmuser_dealer(usd.getPPCNo());
                vehicleLoanAccount.setFicmdate_dealer(new Date());
                vehicleLoanAccount.setFitranid_dealer(tranId);
                vehicleLoanAccount.setFitrandate_dealer(tranDateDt);
                vehicleLoanAccount.setNeftamountdealer(new BigDecimal(tranAmt));
                vehicleLoanAccount.setDealeraccnum(neftRequest.getRequest().getBeneficiaryAccNo());
                vehicleLoanAccount.setDealerifsc(neftRequest.getRequest().getBeneficiaryIFSC());
                vehicleLoanAccount.setDealermob(neftRequest.getRequest().getBeneficiaryMob());
                vehicleLoanAccount.setDealername(neftRequest.getRequest().getBeneficiaryAccName());
                vehicleLoanAccount.setDisbType(disbType);
                accountOpeningRepository.save(vehicleLoanAccount);
                return new ResponseDTO("S", tranId);
            } else {
                if (new BigDecimal(tranAmt).equals(new BigDecimal(mneftamt))) {
                    //ok
                } else {
                    throw new RuntimeException("DTD Tran amount does not match the given amount");
                }
                vehicleLoanAccount.setFiflag_manu("SUCCESS");
                vehicleLoanAccount.setFicmuser_manu(usd.getPPCNo());
                vehicleLoanAccount.setFicmdate_manu(new Date());
                vehicleLoanAccount.setFitranid_manu(tranId);
                vehicleLoanAccount.setFitrandate_manu(tranDateDt);
                vehicleLoanAccount.setNeftamountmanuf(new BigDecimal(tranAmt));
                vehicleLoanAccount.setManufacc(neftRequest.getRequest().getBeneficiaryAccNo());
                vehicleLoanAccount.setManufifsc(neftRequest.getRequest().getBeneficiaryIFSC());
                vehicleLoanAccount.setManumob(neftRequest.getRequest().getBeneficiaryMob());
                vehicleLoanAccount.setManuname(neftRequest.getRequest().getBeneficiaryAccName());
                vehicleLoanAccount.setDisbType(disbType);
                vehicleLoanAccount.setAdd1(add1);
                vehicleLoanAccount.setAdd2(add2);
                vehicleLoanAccount.setAdd3(add3);
                accountOpeningRepository.save(vehicleLoanAccount);
                return new ResponseDTO("S", tranId);
            }
        }


        if (neftRequest.getRequest().getBeneficiaryIFSC().startsWith("SIBL")) {//do fi instead of neft


            FIResponse fiResponse = performFi(slno, winum, beneficiaryType, dneftamt, mneftamt, neftRequest.getRequest().getBeneficiaryAccNo(), ifsc, accname, request);
            //if(fiResponse!=null && fiResponse.getResponse().getStatus().equals("200") && fiResponse.getResponse().getBody().getTran_id()!=null){
            String resp = parseFIResponse(fiResponse);
            if (resp != null && resp.contains("|") && resp.length() > 1) {//success

                String tranId = resp.split("\\|")[0];//fiResponse.getResponse().getBody().getTran_id();
                //String tranDate=resp.split("\\|")[1];//fiResponse.getResponse().getBody().getTran_date();
                String tranDate = "";
                LocalDateTime tranDateDt = null;
                //Date tranDateDt=null;
                if (resp.endsWith("|")) {
                    tranDate = "";
                } else {
                    tranDate = resp.split("\\|")[1];
                    //tranDate = tranDate.substring(0, Math.min(tranDate.length(), 10));
                    //DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    //tranDateDt = format.parse(tranDate);
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    tranDateDt = LocalDateTime.parse(tranDate, dateTimeFormatter);
                }
                if (beneficiaryType.equals("D")) {
                    vehicleLoanAccount.setFiflag_dealer("SUCCESS");
                    vehicleLoanAccount.setFicmuser_dealer(usd.getPPCNo());
                    vehicleLoanAccount.setFicmdate_dealer(new Date());
                    vehicleLoanAccount.setFitranid_dealer(tranId);
                    vehicleLoanAccount.setFitrandate_dealer(tranDateDt);
                    vehicleLoanAccount.setNeftamountdealer(new BigDecimal(dneftamt));
                    vehicleLoanAccount.setDealeraccnum(neftRequest.getRequest().getBeneficiaryAccNo());
                    vehicleLoanAccount.setDealerifsc(neftRequest.getRequest().getBeneficiaryIFSC());
                    vehicleLoanAccount.setDealermob(neftRequest.getRequest().getBeneficiaryMob());
                    vehicleLoanAccount.setDealername(neftRequest.getRequest().getBeneficiaryAccName());
                    vehicleLoanAccount.setDisbType(disbType);
                    accountOpeningRepository.save(vehicleLoanAccount);
                    return new ResponseDTO("S", tranId);
                } else {
                    vehicleLoanAccount.setFiflag_manu("SUCCESS");
                    vehicleLoanAccount.setFicmuser_manu(usd.getPPCNo());
                    vehicleLoanAccount.setFicmdate_manu(new Date());
                    vehicleLoanAccount.setFitranid_manu(tranId);
                    vehicleLoanAccount.setFitrandate_manu(tranDateDt);
                    vehicleLoanAccount.setNeftamountmanuf(new BigDecimal(mneftamt));
                    vehicleLoanAccount.setManufacc(neftRequest.getRequest().getBeneficiaryAccNo());
                    vehicleLoanAccount.setManufifsc(neftRequest.getRequest().getBeneficiaryIFSC());
                    vehicleLoanAccount.setManumob(neftRequest.getRequest().getBeneficiaryMob());
                    vehicleLoanAccount.setManuname(neftRequest.getRequest().getBeneficiaryAccName());
                    vehicleLoanAccount.setDisbType(disbType);
                    accountOpeningRepository.save(vehicleLoanAccount);
                    return new ResponseDTO("S", tranId);
                }
            } else {
                return new ResponseDTO("F", resp);
            }

        } else {//initiate neft

            if (beneficiaryType.equals("D") && "SUCCESS".equals(vehicleLoanAccount.getNeftflagdealer()) ||
                    beneficiaryType.equals("M") && "SUCCESS".equals(vehicleLoanAccount.getNeftflagmanuf())) {
                return new ResponseDTO("F", "NEFT is already initiated for this beneficiary");
            }
            NeftResponse neftResponse = disbApiClient.performNeftApi(neftRequest);
            String utrNo = parseNeftResponse(neftResponse);
            if (!utrNo.isEmpty()) {//success case
                if (beneficiaryType.equals("D")) {
                    vehicleLoanAccount.setNeftcmuserdealer(usd.getPPCNo());
                    vehicleLoanAccount.setNeftcmdatedealer(new Date());
                    vehicleLoanAccount.setUtrnodealer(utrNo);
                    vehicleLoanAccount.setNeftflagdealer("SUCCESS");
                    vehicleLoanAccount.setNeftamountdealer(new BigDecimal(neftRequest.getRequest().getTransactionAmount()));
                    vehicleLoanAccount.setDealeraccnum(neftRequest.getRequest().getBeneficiaryAccNo());
                    vehicleLoanAccount.setDealerifsc(neftRequest.getRequest().getBeneficiaryIFSC());
                    vehicleLoanAccount.setDealermob(neftRequest.getRequest().getBeneficiaryMob());
                    vehicleLoanAccount.setDealername(neftRequest.getRequest().getBeneficiaryAccName());
                    vehicleLoanAccount.setDisbType(disbType);
                    accountOpeningRepository.save(vehicleLoanAccount);
                    return new ResponseDTO("S", utrNo);
                } else {
                    vehicleLoanAccount.setNeftcmusermanuf(usd.getPPCNo());
                    vehicleLoanAccount.setNeftcmdatemanuf(new Date());
                    vehicleLoanAccount.setUtrnomanuf(utrNo);
                    vehicleLoanAccount.setNeftflagmanuf("SUCCESS");
                    vehicleLoanAccount.setNeftamountmanuf(new BigDecimal(neftRequest.getRequest().getTransactionAmount()));
                    vehicleLoanAccount.setManufacc(neftRequest.getRequest().getBeneficiaryAccNo());
                    vehicleLoanAccount.setManufifsc(neftRequest.getRequest().getBeneficiaryIFSC());
                    vehicleLoanAccount.setManumob(neftRequest.getRequest().getBeneficiaryMob());
                    vehicleLoanAccount.setManuname(neftRequest.getRequest().getBeneficiaryAccName());
                    vehicleLoanAccount.setDisbType(disbType);
                    vehicleLoanAccount.setAdd1(add1);
                    vehicleLoanAccount.setAdd2(add2);
                    vehicleLoanAccount.setAdd3(add3);
                    accountOpeningRepository.save(vehicleLoanAccount);
                    return new ResponseDTO("S", utrNo);
                }
            } else {
                String errMessage = "";
                if (neftResponse.getResponse().getBody() != null) {
                    errMessage = neftResponse.getResponse().getBody().getErrorMessage();
                    if (errMessage == null || errMessage.isEmpty()) {
                        errMessage = neftResponse.getResponse().getBody().getDescription();
                    }
                } else {
                    errMessage = neftResponse.getResponse().getStatus().getDesc();
                }
                return new ResponseDTO("F", errMessage);
            }
        }
    }

    public String fetchDealerAccNumberAndIfsc(Long slno, String winum) {
        VehicleLoanVehicle vehicle = vehicleDetailsService.fetchExistingbyWinumandSlno(winum, slno);
        String dealerCode = vehicle.getDealerCode();
        String dealerSubcode = vehicle.getDealerSubCode();
        String cityId = vehicle.getDealerCityId();
        String cityName = vehicle.getDealerCityName();
        String oemid = vehicle.getMakeId();

        //String r =vehicle.getDealerAccount()+"-"+vehicle.getDealerIfsc();
        String r = fetchRepository.fetchDealerAcctNoAndIfsc(dealerCode, dealerSubcode, cityId, cityName, oemid, slno.toString());
        if (r == null || r.trim().isEmpty() || !r.contains("-")) {
            throw new RuntimeException("Dealer account not found");
        }
        return r;
    }

    public NeftRequest frameNeftRequest(Long slno, String winum, String beneficiaryType, String dneftamt, String mneftamt, String accnum, String ifsc, String accname,
                                        String manufmobile, String add1, String add2, String add3) {
        NeftRequest neftRequest = new NeftRequest();
        neftRequest.setMock(false);
        neftRequest.setSlno(String.valueOf(slno));
        neftRequest.setWorkItemNumber(winum);
        neftRequest.setApiName("neft");

        String tranAmt = "0", beneficiaryAccNum = "", beneficiaryifsc = "", beneficiaryAccName = "", beneficiaryMob = "";
        if (beneficiaryType.equals("D")) {
            VehicleLoanVehicle vehicle = vehicleDetailsService.fetchExistingbyWinumandSlno(winum, slno);
            String r = fetchDealerAccNumberAndIfsc(slno, winum);
            beneficiaryAccNum = r.split("-")[0];
            beneficiaryifsc = r.split("-")[1];
//            beneficiaryAccNum=vehicle.getDealerAccount().trim();
//            beneficiaryifsc=vehicle.getDealerIfsc().trim();
            tranAmt = dneftamt;
            beneficiaryAccName = vehicle.getDealerName();


            String dealerCode = vehicle.getDealerCode();
            String dealerSubcode = vehicle.getDealerSubCode();
            beneficiaryMob = fetchRepository.fetchDealerMobile(dealerCode, dealerSubcode);
            if (beneficiaryMob == null || beneficiaryMob.trim().isEmpty()) {
                log.error("Dealer mobile  number is not updated");
                throw new RuntimeException("Dealer mobile  number is not updated");
            }
            add1 = vehicle.getDealerName();
            String cityId = vehicle.getDealerCityId();
            String cityName = vehicle.getDealerCityName();
            String oemid = vehicle.getMakeId();
            Map<String, String> m = fetchRepository.getDealerCityLocation(dealerCode, dealerSubcode, cityId, cityName, oemid);
            if (m == null || m.get("city_name") == null || m.get("state") == null) {
                log.error("Unable to fetch dealer city and state");
                throw new RuntimeException("Unable to fetch dealer city and state");
            }
            add2 = m.get("city_name");
            add3 = m.get("state");

        } else {
            tranAmt = mneftamt;
            beneficiaryAccNum = accnum;
            beneficiaryifsc = ifsc;
            beneficiaryAccName = accname;
            beneficiaryMob = manufmobile;
        }

        NeftRequest.Request innerReq = new NeftRequest.Request();
        innerReq.setUUID(winum + beneficiaryType);
        innerReq.setMerchantCode(neftmerchantCode);
        innerReq.setMerchantName(neftmerchantName);
        innerReq.setTypeofPayment("NEFT");
        innerReq.setTransactionAmount(tranAmt);//amount limit
        if (add1 == null || add1.trim().isEmpty() || add2 == null || add2.trim().isEmpty()) {
            log.error("Unable to fetch address line 1 and 2");
            throw new RuntimeException("Unable to fetch address line 1 and 2");
        }
        add1 = add1.substring(0, Math.min(add1.length(), 35));
        add2 = add2.substring(0, Math.min(add2.length(), 35));
        innerReq.setBeneficiaryAccAdd1(add1);
        innerReq.setBeneficiaryAccAdd2(add2);
        innerReq.setBeneficiaryAccAdd3(add3);
        innerReq.setRemitterAccNo(BogPoolAcc);
        innerReq.setBeneficiaryIFSC(beneficiaryifsc);
        innerReq.setBeneficiaryAccNo(beneficiaryAccNum);
        if (beneficiaryAccName == null || beneficiaryAccName.trim().isEmpty()) {
            log.error("Unable to fetch beneficiaryAccName");
            throw new RuntimeException("Unable to fetch beneficiaryAccName");
        }
        beneficiaryAccName = beneficiaryAccName.substring(0, Math.min(beneficiaryAccName.length(), 50));
        innerReq.setBeneficiaryAccName(beneficiaryAccName);
        innerReq.setBeneficiaryMob(beneficiaryMob);
        innerReq.setBeneficiaryemail("");
        innerReq.setReportCode("");//pass as blank.

        neftRequest.setRequest(innerReq);
        return neftRequest;

    }

    public void validateNeftInputs(Long slno, String winum, String beneficiaryType, String dneftamt, String mneftamt, String accnum, String ifsc, String accname,
                                   String manufmobile, String disbType, String add1, String add2, String add3) {


        //Insurance

        VehicleLoanDetails loanDetails = loanDetailsService.findBySlnoAndDelFlg(slno);
        BigDecimal insAmt = new BigDecimal(0);
        if (loanDetails.getInsVal().equals("Y")) {
            insAmt = loanDetails.getInsAmt();
            if (loanDetails.getInsTranID() == null || loanDetails.getInsTranID().isBlank()) {
                String msg = "Kindly Complete Insurance Disbursement !!";
                log.error(msg);
                throw new IllegalArgumentException(msg);
            }
        }

 if (disbType == null || (!disbType.equals("S") && !disbType.equals("M")) || slno == null || winum == null || beneficiaryType == null || (!"D".equals(beneficiaryType) && !"M".equals(beneficiaryType)) ||
                ("D".equals(beneficiaryType) && (dneftamt == null || new BigDecimal(dneftamt).compareTo(BigDecimal.ZERO) <= 0)) ||
                "M".equals(beneficiaryType) && ((mneftamt == null || new BigDecimal(mneftamt).compareTo(BigDecimal.ZERO) <= 0) ||
                        (dneftamt == null || new BigDecimal(dneftamt).compareTo(BigDecimal.ZERO) <= 0) || accnum == null || accnum.trim().isEmpty() ||
                        ifsc == null || ifsc.trim().isEmpty() || accname == null || accname.trim().isEmpty() || accname.trim().length() > 50 ||
                        manufmobile == null || manufmobile.trim().isEmpty() || manufmobile.length() != 10 ||
                        add1 == null || add1.trim().isEmpty() || add1.length() > 35 ||
                        add2 == null || add2.trim().isEmpty() || add2.length() > 35 ||
                        add3 == null || add3.trim().isEmpty() || add3.length() > 35
                )
        ) {
            log.error("Invalid inputs in validateNeftInputs");
            throw new IllegalArgumentException("Invalid parameters");
        }
        if (disbType.equals("M")) {
            if ((dneftamt == null || new BigDecimal(dneftamt).compareTo(BigDecimal.ZERO) <= 0) || (mneftamt == null || new BigDecimal(mneftamt).compareTo(BigDecimal.ZERO) <= 0) ||
                    (dneftamt == null || new BigDecimal(dneftamt).compareTo(BigDecimal.ZERO) <= 0) || accnum == null || accnum.trim().isEmpty() ||
                    ifsc == null || ifsc.trim().isEmpty() || accname == null || accname.trim().isEmpty() || accname.trim().length() > 50 ||
                    manufmobile == null || manufmobile.trim().isEmpty() || manufmobile.length() != 10 ||
                    add1 == null || add1.trim().isEmpty() || add1.length() > 35 ||
                    add2 == null || add2.trim().isEmpty() || add2.length() > 35 ||
                    add3 == null || add3.trim().isEmpty() || add3.length() > 35
            ) {
                log.error("Invalid inputs in validateNeftInputs for disbursement type:Multiple");
                throw new IllegalArgumentException("Invalid parameters for disbursement type:Multiple");
            }
        }
        if (disbType.equals("S")) {
            if (dneftamt == null || new BigDecimal(dneftamt).compareTo(BigDecimal.ZERO) <= 0) {
                log.error("Invalid inputs in validateNeftInputs for disbursement type:Single");
                throw new IllegalArgumentException("Invalid parameters for disbursement type:Single");
            }
        }
        BigDecimal dneftamt_bd = new BigDecimal(dneftamt);
        BigDecimal mneftamt_bd = new BigDecimal(mneftamt);

        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(slno, "N");
        if (disbType.equals("M") && (vehicleLoanAccount.getDisbursedAmount().subtract(insAmt)).compareTo(dneftamt_bd.add(mneftamt_bd)) == 0 ||
                disbType.equals("S") && (vehicleLoanAccount.getDisbursedAmount().subtract(insAmt)).compareTo(dneftamt_bd) == 0
        ) {
            //valid
        } else {
            String msg = "Neft amount does not add up";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        VehicleLoanMaster master = vehicleLoanMasterService.findById(slno);
        if (master == null || master.getAccNumber() == null) {
            String msg = "Account is not opened";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (!"SUCCESS".equals(vehicleLoanAccount.getDisbflag())) {
            String msg = "Disbursement is not completed";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (fetchRepository.loanIsDisbursed(master.getAccNumber()) != 1) {
            String msg = "The loan account is not disbursed in lam, please check ";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }


    }


    public FIResponse performFi(Long slno, String winum, String beneficiaryType, String dneftamt, String mneftamt, String accnum, String ifsc, String accname, HttpServletRequest request) throws Exception {
        String tranAmt = "D".equals(beneficiaryType) ? dneftamt : mneftamt;
        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(slno, "N");
        FIRequest fiRequest = frameFIRequest(slno, winum, beneficiaryType, tranAmt, accnum);
        FIResponse fiResponse = disbApiClient.performFI(fiRequest);
        return fiResponse;
    }

    public FIRequest frameFIRequest(Long slno, String winum, String beneficiaryType, String tranamount, String beneficiaryAccNum) {
        FIRequest fiRequest = new FIRequest();
        fiRequest.setMock(false);
        fiRequest.setApiName("fi");
        fiRequest.setSlno(String.valueOf(slno));
        fiRequest.setWorkItemNumber(winum);

        FIRequest.Request innerReq = new FIRequest.Request();
        innerReq.setUUID(winum + beneficiaryType);
        innerReq.setMerchantName(fimerchantname);
        innerReq.setMerchantCode(fimerchantcode);

        String tranParticulars = "", custname = "", br_name = "";
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(slno);
        custname = vehicleLoanMaster.getCustName();
        custname = custname.trim();
        custname = custname.substring(0, Math.min(custname.length(), 25));

        br_name = fetchRepository.getSolName(vehicleLoanMaster.getSolId());
        br_name = br_name.trim();
        br_name = br_name.substring(0, Math.min(br_name.length(), 24));

        tranParticulars = custname + "-" + br_name;
        tranParticulars = tranParticulars.toUpperCase();

        FIRequest.PartTrnRec partTrnRecDebit = new FIRequest.PartTrnRec();
        partTrnRecDebit.setAcctId(BogPoolAcc);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime currentdateTime = LocalDateTime.now();
        String valueDateStr = currentdateTime.format(dateTimeFormatter);
        partTrnRecDebit.setValueDt(valueDateStr);//"2024-08-31T11:14:17.324"
        partTrnRecDebit.setExpiry_date("");
        partTrnRecDebit.setTod_user_id("");
        partTrnRecDebit.setTrnAmt(tranamount);
        partTrnRecDebit.setDiscrete_advn_catgr("");
        partTrnRecDebit.setTod_event_type("");
        //partTrnRecDebit.setTrnParticulars("VLOS FI "+winum+beneficiaryType);
        partTrnRecDebit.setTrnParticulars(tranParticulars);
        partTrnRecDebit.setTod_lev_int_flg("");
        partTrnRecDebit.setGrant_tod_flg("");
        partTrnRecDebit.setGrant_date("");
        partTrnRecDebit.setRefnum(winum + "_" + beneficiaryType);
        partTrnRecDebit.setPartTrnRmks("VLOS");
        partTrnRecDebit.setTran_header_remarks("");
        partTrnRecDebit.setTod_entity_type("");
        partTrnRecDebit.setTod_amt_grntd_crncy("");
        partTrnRecDebit.setRemarks2("");
        partTrnRecDebit.setDiscrete_advn_type("");
        partTrnRecDebit.setTod_amt_grntd("");
        partTrnRecDebit.setCurrencyCode("INR");
        partTrnRecDebit.setPenal_date("");
        partTrnRecDebit.setCreditDebitFlg("D");

        FIRequest.PartTrnRec partTrnRecCredit = new FIRequest.PartTrnRec();
        partTrnRecCredit.setAcctId(beneficiaryAccNum);
        partTrnRecCredit.setValueDt(valueDateStr);
        partTrnRecCredit.setExpiry_date("");
        partTrnRecCredit.setTod_user_id("");
        partTrnRecCredit.setTrnAmt(tranamount);
        partTrnRecCredit.setDiscrete_advn_catgr("");
        partTrnRecCredit.setTod_event_type("");
        //partTrnRecCredit.setTrnParticulars("VLOS FI "+winum);
        partTrnRecCredit.setTrnParticulars(tranParticulars);
        partTrnRecCredit.setTod_lev_int_flg("");
        partTrnRecCredit.setGrant_tod_flg("");
        partTrnRecCredit.setGrant_date("");
        partTrnRecCredit.setRefnum(winum + "_" + beneficiaryType);
        partTrnRecCredit.setPartTrnRmks("VLOS");
        partTrnRecCredit.setTran_header_remarks("");
        partTrnRecCredit.setTod_entity_type("");
        partTrnRecCredit.setTod_amt_grntd_crncy("");
        partTrnRecCredit.setRemarks2("");
        partTrnRecCredit.setDiscrete_advn_type("");
        partTrnRecCredit.setTod_amt_grntd("");
        partTrnRecCredit.setCurrencyCode("INR");
        partTrnRecCredit.setPenal_date("");
        partTrnRecCredit.setCreditDebitFlg("C");


        FIRequest.PartTrnRec[] PartTrnRec = new FIRequest.PartTrnRec[2];
        PartTrnRec[0] = partTrnRecDebit;
        PartTrnRec[1] = partTrnRecCredit;

        innerReq.setPartTrnRec(PartTrnRec);
        fiRequest.setRequest(innerReq);

        return fiRequest;
    }

    public ResponseDTO performInsFiTrn(Long slno, String winum) throws Exception {

        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(slno);
        VehicleLoanDetails loanDetails = loanDetailsService.findBySlnoAndDelFlg(slno);
        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(slno, "N");
        String creditAcc = vehicleLoanMaster.getSolId() + "256000000006";
        //ensure that FI tran has not alredy happened in dtd/htd
        String sysdateDDMMYYYY = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        sysdateDDMMYYYY = formatter.format(vehicleLoanAccount.getCmDate());
        Map<String, String> fidetails = checkdtdhtd(BogPoolAcc, "INS_" + winum, "D", sysdateDDMMYYYY);
        if (fidetails != null) {//tran already present
            String tranId = (String) fidetails.get("tran_id");
            String tranDate = (String) fidetails.get("tran_date");
            LocalDateTime tranDateDt = null;
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
            tranDateDt = LocalDateTime.parse(tranDate + " 00:00:00.000", dateTimeFormatter);
            loanDetails.setFicmuser(usd.getPPCNo());
            loanDetails.setFicmdate(new Date());
            loanDetails.setInsTranID(tranId);
            loanDetails.setInsTranDate(tranDateDt);
            loanDetails.setInsCreditAcc(creditAcc);
            loanDetailsService.save(loanDetails);
            return new ResponseDTO("S", tranId);
        }
        if (vehicleLoanMaster == null || vehicleLoanMaster.getAccNumber() == null) {
            String msg = "Account is not opened";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else if (!"SUCCESS".equals(vehicleLoanAccount.getDisbflag())) {
            String msg = "Disbursement is not completed";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else if (fetchRepository.loanIsDisbursed(vehicleLoanMaster.getAccNumber()) != 1) {
            String msg = "The loan account is not disbursed in lam, please check ";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else if (loanDetails.getInsTranID() != null) {
            String msg = "The Insurance Amount is already Disbursed, please check ";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        } else if (loanDetails.getInsVal().equals("Y")) {
            FIResponse fiResponse = performInsFi(slno, winum, loanDetails, vehicleLoanMaster, creditAcc);
            //if(fiResponse!=null && fiResponse.getResponse().getStatus().equals("200") && fiResponse.getResponse().getBody().getTran_id()!=null){
            log.info("parseFIResponse-->{}", mapper.writeValueAsString(fiResponse));
            String resp = parseFIResponse(fiResponse);
            if (resp != null && resp.contains("|") && resp.length() > 1) {//success

                String tranId = resp.split("\\|")[0];//fiResponse.getResponse().getBody().getTran_id();
                //String tranDate=resp.split("\\|")[1];//fiResponse.getResponse().getBody().getTran_date();
                String tranDate = "";
                LocalDateTime tranDateDt = null;
                //Date tranDateDt=null;
                if (resp.endsWith("|")) {
                    tranDate = "";
                } else {
                    tranDate = resp.split("\\|")[1];
                    //tranDate = tranDate.substring(0, Math.min(tranDate.length(), 10));
                    //DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    //tranDateDt = format.parse(tranDate);
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    tranDateDt = LocalDateTime.parse(tranDate, dateTimeFormatter);
                }
                loanDetails.setFicmuser(usd.getPPCNo());
                loanDetails.setFicmdate(new Date());
                loanDetails.setInsTranID(tranId);
                loanDetails.setInsTranDate(tranDateDt);
                loanDetails.setInsCreditAcc(creditAcc);
                loanDetailsService.save(loanDetails);
                return new ResponseDTO("S", tranId);
            } else {
                return new ResponseDTO("F", resp);
            }
        }
        return new ResponseDTO("F", "Insurance not required for the WI");
    }

    public FIResponse performInsFi(Long slno, String winum, VehicleLoanDetails loanDetails, VehicleLoanMaster vehicleLoanMaster, String creditAcc) throws Exception {
        if (loanDetails.getInsVal().equals("Y")) {
            FIRequest fiRequest = frameInsFIRequest(slno, winum, loanDetails.getInsAmt().toString(), vehicleLoanMaster, creditAcc);
            FIResponse fiResponse = disbApiClient.performFI(fiRequest);
            return fiResponse;
        }
        throw new ValidationException(ValidationError.COM001, "Insurance not required for the WI");
    }

    public FIRequest frameInsFIRequest(Long slno, String winum, String tranamount, VehicleLoanMaster vehicleLoanMaster, String creditAcc) {
        FIRequest fiRequest = new FIRequest();
        fiRequest.setMock(false);
        fiRequest.setApiName("fi");
        fiRequest.setSlno(String.valueOf(slno));
        fiRequest.setWorkItemNumber(winum);

        FIRequest.Request innerReq = new FIRequest.Request();
        innerReq.setUUID("INS_" + winum);
        innerReq.setMerchantName(fimerchantname);
        innerReq.setMerchantCode(fimerchantcode);

        String tranParticulars = "", custname = "", br_name = "";
        custname = vehicleLoanMaster.getCustName();
        custname = custname.trim();
        custname = custname.substring(0, Math.min(custname.length(), 25));


        tranParticulars = custname + "/INSURANCE/" + winum;
        tranParticulars = tranParticulars.toUpperCase();

        FIRequest.PartTrnRec partTrnRecDebit = new FIRequest.PartTrnRec();
        partTrnRecDebit.setAcctId(BogPoolAcc);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime currentdateTime = LocalDateTime.now();
        String valueDateStr = currentdateTime.format(dateTimeFormatter);
        partTrnRecDebit.setValueDt(valueDateStr);//"2024-08-31T11:14:17.324"
        partTrnRecDebit.setExpiry_date("");
        partTrnRecDebit.setTod_user_id("");
        partTrnRecDebit.setTrnAmt(tranamount);
        partTrnRecDebit.setDiscrete_advn_catgr("");
        partTrnRecDebit.setTod_event_type("");
        //partTrnRecDebit.setTrnParticulars("VLOS FI "+winum+beneficiaryType);
        partTrnRecDebit.setTrnParticulars(tranParticulars);
        partTrnRecDebit.setTod_lev_int_flg("");
        partTrnRecDebit.setGrant_tod_flg("");
        partTrnRecDebit.setGrant_date("");
        partTrnRecDebit.setRefnum("INS_" + winum);
        partTrnRecDebit.setPartTrnRmks("VLOS");
        partTrnRecDebit.setTran_header_remarks("");
        partTrnRecDebit.setTod_entity_type("");
        partTrnRecDebit.setTod_amt_grntd_crncy("");
        partTrnRecDebit.setRemarks2("");
        partTrnRecDebit.setDiscrete_advn_type("");
        partTrnRecDebit.setTod_amt_grntd("");
        partTrnRecDebit.setCurrencyCode("INR");
        partTrnRecDebit.setPenal_date("");
        partTrnRecDebit.setCreditDebitFlg("D");

        FIRequest.PartTrnRec partTrnRecCredit = new FIRequest.PartTrnRec();
        partTrnRecCredit.setAcctId(creditAcc);
        partTrnRecCredit.setValueDt(valueDateStr);
        partTrnRecCredit.setExpiry_date("");
        partTrnRecCredit.setTod_user_id("");
        partTrnRecCredit.setTrnAmt(tranamount);
        partTrnRecCredit.setDiscrete_advn_catgr("");
        partTrnRecCredit.setTod_event_type("");
        //partTrnRecCredit.setTrnParticulars("VLOS FI "+winum);
        partTrnRecCredit.setTrnParticulars(tranParticulars);
        partTrnRecCredit.setTod_lev_int_flg("");
        partTrnRecCredit.setGrant_tod_flg("");
        partTrnRecCredit.setGrant_date("");
        partTrnRecCredit.setRefnum("INS_" + winum);
        partTrnRecCredit.setPartTrnRmks("VLOS");
        partTrnRecCredit.setTran_header_remarks("");
        partTrnRecCredit.setTod_entity_type("");
        partTrnRecCredit.setTod_amt_grntd_crncy("");
        partTrnRecCredit.setRemarks2("");
        partTrnRecCredit.setDiscrete_advn_type("");
        partTrnRecCredit.setTod_amt_grntd("");
        partTrnRecCredit.setCurrencyCode("INR");
        partTrnRecCredit.setPenal_date("");
        partTrnRecCredit.setCreditDebitFlg("C");


        FIRequest.PartTrnRec[] PartTrnRec = new FIRequest.PartTrnRec[2];
        PartTrnRec[0] = partTrnRecDebit;
        PartTrnRec[1] = partTrnRecCredit;

        innerReq.setPartTrnRec(PartTrnRec);
        fiRequest.setRequest(innerReq);

        return fiRequest;
    }

    public Map<String, String> checkdtdhtd(String accNum, String refNUm, String partTranType, String startDateDDMMYYYY) {
        String inputDate = startDateDDMMYYYY; // Replace with your input date

        Map<String, String> mm = fetchRepository.getDTD(accNum, refNUm, partTranType);
        if (mm != null) {
            return mm;
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Parse the input string to a LocalDate
        LocalDate startDate = LocalDate.parse(inputDate, formatter);

        // Get today's date
        LocalDate endDate = LocalDate.now();

        // Initialize a list to hold all the dates (optional)
        List<LocalDate> dateList = new ArrayList<>();

        // Iterate from startDate to endDate
        while (!startDate.isAfter(endDate)) {
            // Add the current date to the list (optional)
            //dateList.add(startDate);

            // Print the current date
            //System.out.println(startDate);
            String datestr = formatter.format(startDate);

            Map<String, String> nn = fetchRepository.getHTD(accNum, refNUm, partTranType, datestr);
            if (nn != null) {
                return nn;
            }

            // Move to the next date
            startDate = startDate.plusDays(1);
        }
        return null;
    }

    public String parseNeftResponse(NeftResponse neftResponse) {
        String utrNo = "";
        if (neftResponse != null && neftResponse.getResponse().getStatus().getCode().equals("200") && neftResponse.getResponse().getBody().getUTRNo() != null) {
            utrNo = neftResponse.getResponse().getBody().getUTRNo();
        } else if (neftResponse.getResponse().getStatus().getCode().equals("406")
                && "Duplicate Success Transaction".equals(neftResponse.getResponse().getBody().getDescription())) {
            utrNo = neftResponse.getResponse().getBody().getErrorMessage().split("UTR:")[1];
        } else {
            utrNo = "";
        }
        return utrNo;
    }

    public String parseFIResponse(FIResponse fiResponse) {
        String tranId = "", tranDate = "", responsestr = "";
        if (fiResponse != null && fiResponse.getResponse().getStatus().getCode().equals("200") && fiResponse.getResponse().getBody().getTran_id() != null) {
            tranId = fiResponse.getResponse().getBody().getTran_id();
            tranDate = fiResponse.getResponse().getBody().getTran_date();
            responsestr = tranId + "|" + tranDate;
        } else if (fiResponse.getResponse().getStatus().getCode().equals("406")
                && fiResponse.getResponse().getStatus().getDesc().equalsIgnoreCase("Duplicate Success Transaction")) {
            tranId = fiResponse.getResponse().getBody().getDescription().split("transaction id:")[1];
            responsestr = tranId + "|" + tranDate;
        } else {
            responsestr = fiResponse.getResponse().getStatus().getDesc();
        }
        return responsestr;
    }


    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO wiBogAssetRej(String winum, Long slno, String remarks, HttpServletRequest request) throws Exception {

        VehicleLoanMaster loanMaster = vehicleLoanMasterService.findById(slno);
        if (loanMaster == null) {
            throw new RuntimeException("Loan application not found");
        }
        if (!winum.equals(loanMaster.getWiNum())) {
            throw new RuntimeException("Invalid inputs");
        }

        Optional<NACHMandate> op = nachMandateService.getNACHMandateBySlno(slno);
        if (op.isPresent()) {
            NACHMandate nachMandate = op.get();
            if ("Authorized".equalsIgnoreCase(nachMandate.getStatus()) || "Progressed by User".equalsIgnoreCase(nachMandate.getStatus())) {
                throw new RuntimeException("Cannot reject the WI since NACH mandate is set/awaiting approval");
            }
        }

        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(slno, "N");
        boolean fundtransferred = false;
        if (vehicleLoanAccount != null) {
            String disbType = vehicleLoanAccount.getDisbType();
            if (disbType == null) disbType = "";
            if (disbType.equals("S")) {
                if ("SUCCESS".equals(vehicleLoanAccount.getFiflag_dealer()) || "SUCCESS".equals(vehicleLoanAccount.getNeftflagdealer())) {
                    fundtransferred = true;
                }
            } else if (disbType.equals("M")) {
                if (("SUCCESS".equals(vehicleLoanAccount.getFiflag_dealer()) || "SUCCESS".equals(vehicleLoanAccount.getNeftflagdealer()))
                        || ("SUCCESS".equals(vehicleLoanAccount.getFiflag_manu()) || "SUCCESS".equals(vehicleLoanAccount.getNeftflagmanuf()))) {
                    fundtransferred = true;
                }
            }
        }
        if (fundtransferred) {
            throw new RuntimeException("Cannot reject the WI since fund is already transferred");
        }


        loanMaster.setQueue("NIL");
        loanMaster.setRejDate(new Date());
        loanMaster.setRejUser(usd.getPPCNo());
        loanMaster.setRejFlg("Y");
        loanMaster.setRejQueue("ACOPN");
        vehicleLoanMasterService.saveLoan(loanMaster);
        vehicleLoanQueueDetailsService.createQueueEntry(winum, Long.valueOf(slno), remarks, usd.getPPCNo(), "ACOPN", "NIL");
        loanTatService.updateTat(slno, usd.getPPCNo(), winum, "NIL");

        ResponseDTO smsemail = sendSmsEmail(slno, winum);
        return new ResponseDTO("S", "The record is rejected, " + smsemail.getMsg());
    }


    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO wibogassetPostDisb(String winum, Long slno, String remarks, HttpServletRequest request) throws Exception {

        VehicleLoanMaster loanMaster = vehicleLoanMasterService.findById(slno);
        if (loanMaster == null) {
            throw new RuntimeException("Loan application not found");
        }
        if (!winum.equals(loanMaster.getWiNum())) {
            throw new RuntimeException("Invalid inputs");
        }

        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(slno, "N");
        boolean fundtransferred = false;
        if (vehicleLoanAccount != null) {
            String disbType = vehicleLoanAccount.getDisbType();
            if (disbType == null) disbType = "";
            if (disbType.equals("S")) {
                if ("SUCCESS".equals(vehicleLoanAccount.getFiflag_dealer()) || "SUCCESS".equals(vehicleLoanAccount.getNeftflagdealer())) {
                    fundtransferred = true;
                }
            } else if (disbType.equals("M")) {
                if (("SUCCESS".equals(vehicleLoanAccount.getFiflag_dealer()) || "SUCCESS".equals(vehicleLoanAccount.getNeftflagdealer()))
                        && ("SUCCESS".equals(vehicleLoanAccount.getFiflag_manu()) || "SUCCESS".equals(vehicleLoanAccount.getNeftflagmanuf()))) {
                    fundtransferred = true;
                }
            }
        }
        if (!fundtransferred) {
            throw new RuntimeException("Please complete all sections before post disbursement");
        }

        loanMaster.setQueue("PD");
        loanMaster.setPdUser(usd.getPPCNo());
        loanMaster.setPdDate(new Date());
        vehicleLoanMasterService.saveLoan(loanMaster);
        vehicleLoanQueueDetailsService.createQueueEntry(winum, Long.valueOf(slno), remarks, usd.getPPCNo(), "ACOPN", "PD");
        loanTatService.updateTat(slno, usd.getPPCNo(), winum, "PD");
        updateMSSFStatus(loanMaster);
        return new ResponseDTO("S", "Saved successfully, Kindly enter post disbursement details in Information bank.");
    }

    public ResponseDTO sendSmsEmail(Long slno, String wiNum) {

        String testmobnum = "8086748300";
        String code = "", desc = "";
        try {
            VehicleLoanMaster vmas = vehicleLoanMasterService.findById(slno);
            List<VehicleLoanApplicant> vlapp = vmas.getApplicants().stream().filter(t -> t.getDelFlg().equals("N") && t.getApplicantType().equalsIgnoreCase("A")).toList();
            for (VehicleLoanApplicant vp : vlapp) {
                String mobile = vp.getBasicapplicants().getMobileCntryCode() + vp.getBasicapplicants().getMobileNo();
                String email = vp.getBasicapplicants().getEmailId();
                String salutation = vp.getBasicapplicants().getSalutation();
                salutation = misrctService.getByCodeValue("TIT", salutation).getCodedesc();
                String custName = vp.getApplName();
                String brName = fetchRepository.getSolName(vmas.getSolId());
                String helpdesk = helpdeskNo;
                SMSEmailDTO smdDto = new SMSEmailDTO();
                smdDto.setAlertId("LOSDECN");
                smdDto.setSlno(slno);
                smdDto.setWiNum(wiNum);
                smdDto.setSentUser(usd.getPPCNo());
                smdDto.setReqType("S");
                smdDto.setMobile(testmobnum);
                if (!devMode) {
                    smdDto.setMobile(mobile);
                } else {
                    smdDto.setMobile(testmobnum);
                }
                smdDto.setMessage(salutation + "|" + custName + "|" + smdDto.getWiNum() + "|" + helpdesk + "|");
                ResponseDTO sms = smsEmailService.insertSMSEmail(smdDto);
                if (sms.getStatus().equalsIgnoreCase("F")) return sms;
                SMSEmailDTO emailDTO = new SMSEmailDTO();
                emailDTO.setSlno(slno);
                emailDTO.setWiNum(wiNum);
                emailDTO.setSentUser(usd.getPPCNo());
                emailDTO.setAlertId("LOSDECN");
                emailDTO.setReqType("E");
                emailDTO.setEmailFrom("sibmailer@sib.bank.in");
                if (!devMode) {
                    emailDTO.setEmailTo(email);
                } else {
                    emailDTO.setEmailTo("antonyraj@sib.bank.in");
                    //emailDTO.setEmailTo("vigneshpadmanabhan@sib.bank.in");
                    emailDTO.setEmailTo("shrutid@sib.bank.in");
                }
                emailDTO.setEmailBody("We regret to inform you that your application " + wiNum + " for vehicle loan has been declined. For further details, please contact our customer care  " + helpdesk + " or visit the nearest branch. We appreciate your interest in SIB and look forward to serving you in future");
                emailDTO.setCustName(salutation + " " + custName);
                emailDTO.setEmailSubject("Vehicle Loan Application Rejected – South Indian Bank");
                ResponseDTO email_ = smsEmailService.insertSMSEmail(emailDTO);
                if (email_.getStatus().equalsIgnoreCase("F")) {
                    code = "F";
                    desc = "SMS Sent Successfully," + email_.getMsg();
                } else {
                    code = "S";
                    desc = "SMS and Email Sent Successfully";
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            code = "F";
            desc = e.getMessage();
        }
        return new ResponseDTO(code, desc);
    }

    @Override
    public ResponseDTO bmDel(Long slno, HttpServletRequest request) throws Exception {
        return null;
    }

    public void updateMSSFStatus(VehicleLoanMaster loanMaster) {
        // Check if refNo is not null before making API call
        if (loanMaster.getRefNo() == null) {
            log.info("RefNo is null for wiNum: {}, skipping MSSF status update", loanMaster.getWiNum());
            return;
        }

        try {
            // Create the request payload
            MSSFStatusRequest request = createMSSFStatusRequest(loanMaster);

            // Make the API call
            boolean apiSuccess = callMSSFStatusUpdateAPI(request);

            // If API call is successful, update the database status
            if (apiSuccess) {
                updateMSSFCustomerStatus(loanMaster.getRefNo());
                log.info("MSSF status update successful for wiNum: {}, refNo: {} - Both API and DB updated",
                        loanMaster.getWiNum(), loanMaster.getRefNo());
            } else {
                log.warn("MSSF API call failed for wiNum: {}, refNo: {} - Database not updated",
                        loanMaster.getWiNum(), loanMaster.getRefNo());
            }

        } catch (Exception e) {
            // Log the error but don't throw it to prevent breaking existing flow
            log.error("MSSF status update failed for wiNum: {}, refNo: {}. Error: {}",
                    loanMaster.getWiNum(), loanMaster.getRefNo(), e.getMessage(), e);
        }
    }

    private MSSFStatusRequest createMSSFStatusRequest(VehicleLoanMaster loanMaster) {
        MSSFStatusRequest mssfRequest = new MSSFStatusRequest();
        mssfRequest.setMock(false);
        mssfRequest.setApiName("mssfStatusUpdate");

        MSSFStatusRequest.Request request = new MSSFStatusRequest.Request();
        request.setNewQueue("PD");
        request.setWiNum(loanMaster.getWiNum());
        request.setLosId(loanMaster.getRefNo());

        mssfRequest.setRequest(request);
        return mssfRequest;
    }

    private boolean callMSSFStatusUpdateAPI(MSSFStatusRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://10.21.107.48:8185/losintegrator/api/v1/call";

        try {
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Cookie", "JSESSIONID=FA729BD5069E23E6CF30D944A9D22E56");

            // Create HTTP entity with request body and headers
            HttpEntity<MSSFStatusRequest> httpEntity = new HttpEntity<>(request, headers);

            // Make the API call
            ResponseEntity<MSSFStatusResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    MSSFStatusResponse.class
            );

            // Check if response is successful
            if (response.getStatusCode() == HttpStatus.OK) {
                MSSFStatusResponse responseBody = response.getBody();
                if (responseBody != null && responseBody.getResponse() != null
                        && responseBody.getResponse().getStatus() != null) {

                    String statusCode = responseBody.getResponse().getStatus().getCode();
                    if ("200".equals(statusCode)) {
                        log.info("MSSF API call successful. UUID: {}",
                                responseBody.getResponse().getBody().getUUID());
                        return true;
                    } else {
                        log.warn("MSSF API returned non-200 status: {}", statusCode);
                        return false;
                    }
                }
            } else {
                log.warn("MSSF API call returned HTTP status: {}", response.getStatusCode());
                return false;
            }

        } catch (RestClientException e) {
            log.error("RestClientException during MSSF API call: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during MSSF API call: {}", e.getMessage());
            throw e;
        }

        return false;
    }

    private void updateMSSFCustomerStatus(String refNo) {
        try {
            mssfCustomerRepository.updateMSSFStatusToComplete(refNo, usd.getPPCNo());
            log.info("Successfully updated MSSF Customer Data status to 'C' for refNo: {} by user: {}",
                    refNo, usd.getPPCNo());

        } catch (Exception e) {
            log.error("Failed to update MSSF Customer Data status for refNo: {}. Error: {}",
                    refNo, e.getMessage(), e);
        }
    }


}

