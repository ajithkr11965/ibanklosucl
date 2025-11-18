package com.sib.ibanklosucl.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.acopn.SanctionDetailsDTO;
import com.sib.ibanklosucl.dto.bpm.BPMCreateVLResponse;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;
import com.sib.ibanklosucl.dto.mssf.MssfDocProcessResponse;
import com.sib.ibanklosucl.dto.program.bsaBankDetails;
import com.sib.ibanklosucl.dto.subqueue.LockStatusDTO;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.model.doc.VehicleLoanChargeWaiver;
import com.sib.ibanklosucl.model.doc.VehicleLoanRoiWaiver;
import com.sib.ibanklosucl.model.integrations.VLHunterDetails;
import com.sib.ibanklosucl.model.mssf.MSSFCustomerData;
import com.sib.ibanklosucl.repository.*;
import com.sib.ibanklosucl.repository.integations.ExperianHunterResponseRepository;
import com.sib.ibanklosucl.repository.program.BsaBankDetailsRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.doc.LegalityService;
import com.sib.ibanklosucl.service.doc.ManDocService;
import com.sib.ibanklosucl.service.doc.RepaymentService;
import com.sib.ibanklosucl.service.eligibility.EligibilityDetailsService;
import com.sib.ibanklosucl.service.esbsr.CIFViewService;
import com.sib.ibanklosucl.service.integration.DKScoreService;
import com.sib.ibanklosucl.service.integration.ExperianHunterService;
import com.sib.ibanklosucl.service.integration.NACHMandateService;
import com.sib.ibanklosucl.service.integration.VehicleLoanBREService;
import com.sib.ibanklosucl.service.mssf.MSSFService;
import com.sib.ibanklosucl.service.mssf.MssfDocumentService;
import com.sib.ibanklosucl.service.mssf.PDFGeneratorService;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VLWiCreateServiceImpl implements VLWiCreateService {

    @Autowired
    private VehicleLoanNeftRepository vehicleLoanNeftRepository;
    @Autowired
    private NACHMandateService nachMandateService;
    @Autowired
    private AccountOpeningRepository accountOpeningRepository;
    @Autowired
    private VehicleLoanSanModService vehicleLoanSanModService;
    @Autowired
    private VehicleLoanSubqueueTaskRepository vehicleLoanSubqueueTaskRepository;
    @Autowired
    private final VehicleLoanMasterService vlservice;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private BpmService bpmService;
    @Autowired
    private final VehicleLoanLockService vehicleLoanLockService;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;
    @Autowired
    private LosDedupeService losDedupeService;
    @Autowired
    private FinacleLosDedupeService finacleLosDedupeService;
    @Autowired
    private VLEmploymentService vlEmploymentService;

    @Autowired
    private VehicleDetailsService vehicleDetailsService;
    @Autowired
    private VehicleLoanDetailsService vehicleLoanDetailsService;
    @Autowired
    private VLCreditService vlcreditService;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;
    @Autowired
    private VehicleLoanSubqueueTaskService taskService;
    @Autowired
    private VehicleLoanBREService vehicleLoanBREService;

    @Autowired
    private BsaBankDetailsRepository bsaBankDetailsRepositoryservice;
    @Autowired
    private VehicleLoanAmberService vehicleLoanAmberService;


    @Autowired
    private VehicleLoanTatService loanTatService;
    @Autowired
    private MisrctService misrctService;
    @Autowired
    private MisprmRepository misprmRepository;
    @Autowired
    private VehicleLoanWarnService vlwarn;

    @Autowired
    private CustomerDetailsService cd;
    @Autowired
    private RepaymentService repaymentService;

    @Autowired
    private CIFViewService cfview;
    @Autowired
    private VehicleLoanFcvCpvCfrService fcvCpvCfrService;

    @Autowired
    private VehicleLoanAmberService vlamber;
    @Autowired
    private OctDetailsService octdetailsservice;
    @Autowired
    private VehicleLoanWaiverService loanWaiverService;
    @Autowired
    private LegalityService legalityService;
    @Autowired
    private ManDocService manDocService;
    @Autowired
    private VehicleLoanAmberRepository vehicleLoanAmberRepository;
    @Autowired
    private DKDataRepository dkDataRepository;
    @Autowired
    EligibilityDetailsService eligibilityDetailsService;
    @Autowired
    private DKScoreService dkScoreService;
    @Autowired
    private ExperianHunterService experianHunterService;
    @Autowired
    private ExperianHunterResponseRepository experianHunterResponseRepository;
    @Autowired
    private VehicleLoanKycRepository kycrepo;
    @Value("${app.dev-mode:true}")
    private boolean devMode;
    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    @Autowired
    private MSSFService mssfService;
    @Autowired
    private MssfDocumentService mssfDocumentService;

    @Autowired
    private MaterialListDataRepository materialListDataRepository;

    public VLWiCreateServiceImpl(VehicleLoanMasterService vlservice, VehicleLoanLockService vehicleLoanLockService) {
        this.vlservice = vlservice;
        this.vehicleLoanLockService = vehicleLoanLockService;
    }

    @Override
    public Model addEntry(HttpServletRequest request, Model model) {
        try {
            String wi_number = vlservice.getWi_Num("R");
            VehicleLoanMaster loan = new VehicleLoanMaster();
            loan.setWiNum(wi_number);
            if ("Y".equals(request.getParameter("fromMSSF"))) {
                loan.setRefNo(request.getParameter("refNo"));
                loan.setCustName(request.getParameter("customerName"));
                loan.setSolId(request.getParameter("branchId")); // assign to selected sol
            } else {
                loan.setSolId(usd.getSolid());
            }
            loan.setChannel("BRANCH MARKETING");
            loan.setQueue("BM");
            loan.setStatus("BMDRAFT");
            loan.setCmDate(new Date());
            loan.setQueueDate(new Date());
            loan.setCurrentTab("A-1");
            loan.setReqIpAddr(CommonUtils.getClientIp(request));
            loan.setCmUser(usd.getPPCNo());
            loan.setHomeSol(usd.getSolid());

            loan.setActiveFlg("Y");
            loan.setRiRcreDate(new Date());
            String trans_slno = String.valueOf(vlservice.saveLoan(loan).getSlno());
            VehicleLoanLock lk = new VehicleLoanLock();
            lk.setWiNum(wi_number);
            lk.setSlno(Long.valueOf(trans_slno));
            lk.setLockedBy(usd.getPPCNo());
            lk.setLockedOn(new Date());
            lk.setLockFlg("Y");
            lk.setQueue("BM");
            lk.setDelFlg("N");
            vehicleLoanLockService.saveLock(lk);
            loanTatService.updateTat(loan.getSlno(), usd.getPPCNo(), loan.getWiNum(), "BM");
            BPMCreateVLResponse responseDTO = null;
            try {
                responseDTO = bpmService.BpmParent(wi_number, String.valueOf(loan.getSlno()), "APPLICANT");
                if ("Y".equals(request.getParameter("fromMSSF"))) {
                    MSSFCustomerData customerData = mssfService.getMSSFDetails(loan.getRefNo());
                    byte[] pdfBytes = pdfGeneratorService.generateMSSFLeadPDF(customerData);
                    String base64PDF = Base64.getEncoder().encodeToString(pdfBytes);
                    DocumentRequest docRequest = new DocumentRequest();
                    docRequest.setCmUser(usd.getPPCNo());
                    docRequest.setSlNo(String.valueOf(loan.getSlno()));
                    docRequest.setWiNum(loan.getWiNum());

                    TabResponse dmsResponse = bpmService.BpmUpload(
                            bpmRequest(loan.getWiNum(), base64PDF, "N", "NA", "MSSF_LEAD")
                    );
                    try {
                        MssfDocProcessResponse docResponse = mssfDocumentService.processDocuments(loan.getRefNo());
                        if (docResponse != null && docResponse.getDocuments() != null) {
                            for (DOC_ARRAY doc : docResponse.getDocuments()) {
                                TabResponse docUploadResponse = bpmService.BpmUpload(
                                        bpmRequest(
                                                loan.getWiNum(),
                                                doc.getDOC_BASE64(),
                                                "Y",
                                                "APPLICANT", // childName
                                                doc.getDOC_NAME()
                                        )
                                );

                                if (!"S".equals(docUploadResponse.getStatus())) {
                                    log.error("Failed to upload document {}: {}",
                                            doc.getDOC_NAME(),
                                            docUploadResponse.getMsg());
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error processing MSSF documents for refNo {}: {}", loan.getRefNo(), e.getMessage());
                        // Consider whether to throw the exception or continue with the process
                    }

                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (responseDTO.getStatus().equals("Success")) {
                model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
            } else
                model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));

            List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
            model.addAttribute("bankDetails", bankdetails);
            model.addAttribute("vehicleLoanMaster", loan);
            model.addAttribute("slno", trans_slno);
            model.addAttribute("winum", wi_number);
            model.addAttribute("userdata", usd.getEmployee());
            model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
            model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
            model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
            model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
            model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
            model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
            model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
            model.addAttribute("sol_desc", fetchRepository.getSolName(loan.getHomeSol()));
            model.addAttribute("roname", fetchRepository.getROName(loan.getSolId()));
            //   List<Octdetails> octDetails = octdetailsservice.getOctDetails();
            model.addAttribute("octDetails", octdetailsservice.getOctDetails());
            usd.setTrans_slno(trans_slno);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }


    @Override
    public Model modifyEntry(String trans_slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(trans_slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(trans_slno));
        String lockflg = "N";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(trans_slno));
        if (lockuser == null) {
            VehicleLoanLock lk = new VehicleLoanLock();
            lk.setWiNum(vehicleLoanMaster.getWiNum());
            lk.setSlno(Long.valueOf(trans_slno));
            lk.setLockedBy(usd.getPPCNo());
            lk.setLockedOn(new Date());
            lk.setLockFlg("Y");
            //lk.setQueue("BM");
            lk.setQueue(vehicleLoanMaster.getQueue());
            lk.setDelFlg("N");
            vehicleLoanLockService.saveLock(lk);
        } else if (!lockuser.equals(usd.getPPCNo())) {
            lockflg = "Y";
        }

        //  VehicleLoanMaster loanMaster=vlservice.getMasById(Long.valueOf(trans_slno)).orElseThrow(() -> new RuntimeException("Master Not Found"));

        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));


        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        List<MaterialListData> materialListData=null;
        //VLEmployment employment=vlEmploymentService.findBySlno(vehicleLoanMaster.getSlno());
        //  VLCredit credit=vlcreditService.findBySlno(vehicleLoanMaster.getSlno());
        List<FinDedupEntity> findedup = finacleLosDedupeService.getFinDupByID(vehicleLoanMaster.getSlno());
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        //model.addAttribute("employment", employment);
        //   model.addAttribute("credit", credit);

        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        // List<Octdetails> octDetails = octdetailsservice.getOctDetails();

        model.addAttribute("octDetails", octdetailsservice.getOctDetails());
        model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        model.addAttribute("RBCM", misrctService.getCodeValuesByType("RBCM"));
        materialListData=materialListDataRepository.findBySlno(vehicleLoanMaster.getSlno());
        model.addAttribute("materialListData",materialListData);
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        return model;

    }

    @Override
    public Model fetchcheckerDetails(String trans_slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(trans_slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(trans_slno));
        String lockflg = "N";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(trans_slno));
        if (lockuser == null) {
            VehicleLoanLock lk = new VehicleLoanLock();
            lk.setWiNum(vehicleLoanMaster.getWiNum());
            lk.setSlno(Long.valueOf(trans_slno));
            lk.setLockedBy(usd.getPPCNo());
            lk.setLockedOn(new Date());
            lk.setLockFlg("Y");
            lk.setQueue("BC");
            lk.setDelFlg("N");
            vehicleLoanLockService.saveLock(lk);
        } else if (!lockuser.equals(usd.getPPCNo())) {
            lockflg = "Y";
        }
        List<VLHunterDetails> allHunterDetails = experianHunterResponseRepository
                .findAllByWiNumAndDelFlgOrderByTimestampDesc(vehicleLoanMaster.getWiNum(), "N");

        boolean hunterCheckPerformed = !allHunterDetails.isEmpty();
        model.addAttribute("hunterCheckPerformed", hunterCheckPerformed);

        boolean hunterMatchFound = false;
        if (hunterCheckPerformed) {
            hunterMatchFound = allHunterDetails.stream()
                    .anyMatch(detail -> detail.getMatches() > 0); // Assuming a score > 0 indicates a match
        }
        model.addAttribute("hunterMatchFound", hunterMatchFound);
        model.addAttribute("allHunterDetails", allHunterDetails);


        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));

        //  VLEmployment employment=vlEmploymentService.findBySlno(vehicleLoanMaster.getSlno());
        //  VLCredit credit=vlcreditService.findBySlno(vehicleLoanMaster.getSlno());
        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        VehicleLoanVehicle vehicleDetails = vehicleDetailsService.fetchExistingbyWinumandSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (vehicleDetails == null) {
            model.addAttribute("error", "No vehicle details found for the provided identifiers.");
        }
        model.addAttribute("vehicleDetails", vehicleDetails);
        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        model.addAttribute("loanDetails", loanDetails);
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (!eligibilityDetailsOpt.isPresent()) {
            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
        } else {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            model.addAttribute("eligibilityDetails", eligibilityDetails);

        }

        List<MaterialListData> materialListData=null;
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanAmberRepository.findByWiNumAndActiveFlgOrderById(vehicleLoanMaster.getWiNum(), "Y");
        List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataConsideringQueueDate(vehicleLoanMaster.getSlno());
        model.addAttribute("dkScoreData", dkScoreData);
        List<DKData> dkDataList = dkDataRepository.findAllByWinumberAndSlnoAndActiveFlg(
                vehicleLoanMaster.getWiNum(),
                String.valueOf(vehicleLoanMaster.getSlno()));

        model.addAttribute("dkDataList", dkDataList);
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        // model.addAttribute("employment", employment);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        //  model.addAttribute("credit", credit);
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("panStatusList", misrctService.getCodeValuesByType("PAN1"));
        model.addAttribute("seedingStatusList", misrctService.getCodeValuesByType("PAN2"));
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        model.addAttribute("bureauBlock", isExperianSendBack(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno()));
        model.addAttribute("octDetails", octdetailsservice.getOctDetails());
        model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
        model.addAttribute("RBCM", misrctService.getCodeValuesByType("RBCM"));
        materialListData=materialListDataRepository.findBySlno(vehicleLoanMaster.getSlno());
        model.addAttribute("materialListData",materialListData);
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        return model;
    }


    @SneakyThrows
    @Override
    public Model fetchCRTcheckerDetails(String trans_slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(trans_slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(trans_slno));
        log.error("Own");
        if (vehicleLoanMaster.getBrVUser() != null && vehicleLoanMaster.getCmUser() != null) {
            if (vehicleLoanMaster.getBrVUser().equals(usd.getPPCNo()) || vehicleLoanMaster.getCmUser().equals(usd.getPPCNo())) {
                log.error("Error");
                model.addAttribute("error", "Maker or Checker User should not be same of CRT User");
            }
        }
        //  List<Timestamp> BureauDateTimestamp = (List<Timestamp>) vlcreditService.getVLCreditDetailstoCheckBureua(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno())
        model.addAttribute("bureauBlock", isExperianSendBack(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno()));
        String lockflg = "N";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(trans_slno));
        if (lockuser == null) {
            VehicleLoanLock lk = new VehicleLoanLock();
            lk.setWiNum(vehicleLoanMaster.getWiNum());
            lk.setSlno(Long.valueOf(trans_slno));
            lk.setLockedBy(usd.getPPCNo());
            lk.setLockedOn(new Date());
            lk.setLockFlg("Y");
            lk.setQueue("CS");
            lk.setDelFlg("N");
            vehicleLoanLockService.saveLock(lk);
        } else if (!lockuser.equals(usd.getPPCNo())) {
            lockflg = "Y";
        }
        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());


        //  VLEmployment employment=vlEmploymentService.findBySlno(vehicleLoanMaster.getSlno());
        //  VLCredit credit=vlcreditService.findBySlno(vehicleLoanMaster.getSlno());
        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        VehicleLoanVehicle vehicleDetails = vehicleDetailsService.fetchExistingbyWinumandSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (vehicleDetails == null) {
            model.addAttribute("error", "No vehicle details found for the provided identifiers.");
        }
        model.addAttribute("vehicleDetails", vehicleDetails);
        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        model.addAttribute("loanDetails", loanDetails);
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (!eligibilityDetailsOpt.isPresent()) {
            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
        } else {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            model.addAttribute("eligibilityDetails", eligibilityDetails);

        }
        //List<VehicleLoanWarn> vehicleLoanWarns=new ArrayList<>();
        //vehicleLoanWarns = vlwarn.getActiveAndNotDeletedVehicleLoanWarns(vehicleLoanMaster.getSlno());

        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();

        List<Map<String, Object>> vehicleLoanWarnsWithAdditionalAttr = new ArrayList<>();
        List<VehicleLoanWarn> vehicleLoanWarns = vlwarn.getActiveAndNotDeletedVehicleLoanWarns(vehicleLoanMaster.getSlno());
        for (VehicleLoanWarn warn : vehicleLoanWarns) {
            Map<String, Object> map = new HashMap<>();
            map.put("vehicleLoanWarn", warn);
            String warncode = warn.getWarnCode();
            Long appid = warn.getApplicantId();
            VehicleLoanKyc kycdetails = kycrepo.findByApplicantIdAndDelFlg(appid, "N");
            String visa_oci_type = kycdetails.getVisaOciType();
            String custid = cd.findByAppId(appid).getCustId();
            Long slno = warn.getSlno();
            String wino = warn.getWiNum();
            CIFviewRequest requestjson = new CIFviewRequest();
            requestjson.setSlno(String.valueOf(slno));
            requestjson.setWinum(wino);
            requestjson.setAppid(String.valueOf(appid));
            requestjson.setCustID(custid);
            TabResponse respjson = cfview.getCustData(requestjson, request);
            String currentfinaclevalue = "";
            if (respjson.getStatus().equals("S")) {
                JSONObject data = new JSONObject(respjson.getMsg());
                if (warncode.equals("WAR001")) {
                    currentfinaclevalue = data.has("custDob") && !data.isNull("custDob") ? data.getString("custDob") : "";
                }
                if (warncode.equals("WAR002") || warncode.equals("WAR003")) {
                    currentfinaclevalue = data.has("pan") && !data.isNull("pan") ? data.getString("pan") : "";
                }
                if (warncode.equals("WAR004") || warncode.equals("WAR005")) {
                    currentfinaclevalue = data.has("aadhaarRefNo") && !data.isNull("aadhaarRefNo") ? data.getString("aadhaarRefNo") : "";
                }
                if (warncode.equals("WAR006") || warncode.equals("WAR007")) {
                    currentfinaclevalue = data.has("passport") && !data.isNull("passport") ? data.getString("passport") : "";
                }
                if (warncode.equals("WAR008")) {
                    currentfinaclevalue = data.has("residentialStatus") && !data.isNull("residentialStatus") ? data.getString("residentialStatus") : "";
                }
                if (warncode.equals("WAR009")) {
                    currentfinaclevalue = data.has("minorFlag") && !data.isNull("minorFlag") ? data.getString("minorFlag") : "";
                }
                if (warncode.equals("WAR0010") || warncode.equals("WAR0011")) {
                    if (visa_oci_type.equals("V")) {
                        currentfinaclevalue = data.has("visa") && !data.isNull("visa") ? data.getString("visa") : "";
                    } else if (visa_oci_type.equals("O")) {
                        currentfinaclevalue = data.has("ociCard") && !data.isNull("ociCard") ? data.getString("ociCard") : "";
                    } else if (visa_oci_type.equals("C")) {
                        currentfinaclevalue = data.has("cdnNo") && !data.isNull("cdnNo") ? data.getString("cdnNo") : "";
                    }
                }
                if (warncode.equals("WAR0012") || warncode.equals("WAR0014") || warncode.equals("WAR0015") || warncode.equals("WAR0016") || warncode.equals("WAR0020")) {
                    currentfinaclevalue = "-";
                }
                if (warncode.equals("WAR0013")) {
                    currentfinaclevalue = data.has("customerName") && !data.isNull("customerName") ? data.getString("customerName") : "";
                }
                if (warncode.equals("WAR0017") || warncode.equals("WAR0018")) {
                    currentfinaclevalue = data.has("cellPhone") && !data.isNull("cellPhone") ? data.getString("cellPhone") : "";
                }
                if (warncode.equals("WAR0019")) {
                    currentfinaclevalue = data.has("commEmail") && !data.isNull("commEmail") ? data.getString("commEmail") : "";
                }
            }
            map.put("currentcbsval", currentfinaclevalue);
            vehicleLoanWarnsWithAdditionalAttr.add(map);
        }

        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else {
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));
        }

        List<MaterialListData> materialListData=null;
        CRTAmberDataDTO amberDTO = vlamber.getamberdatasforcrt(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());

        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrService.findBySlno(vehicleLoanMaster.getSlno());
        fcvCpvCfr.ifPresent(record -> model.addAttribute("fcvCpvCfr", record));
        model.addAttribute("amberDTO", amberDTO);

        model.addAttribute("warning", vehicleLoanWarnsWithAdditionalAttr);
        List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataConsideringQueueDate(vehicleLoanMaster.getSlno());
        model.addAttribute("dkScoreData", dkScoreData);

        CRTAmberDataDTO amberdto = vehicleLoanAmberService.getamberdatasforcrt(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        model.addAttribute("ambercolor", amberdto);
        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        // model.addAttribute("employment", employment);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        //  model.addAttribute("credit", credit);
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        model.addAttribute("octDetails", octdetailsservice.getOctDetails());
        model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("panStatusList", misrctService.getCodeValuesByType("PAN1"));
        model.addAttribute("seedingStatusList", misrctService.getCodeValuesByType("PAN2"));
        model.addAttribute("RBCM", misrctService.getCodeValuesByType("RBCM"));
        materialListData=materialListDataRepository.findBySlno(vehicleLoanMaster.getSlno());
        model.addAttribute("materialListData",materialListData);
        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanBREService.getAmberColorDataForLoan(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());

        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        String previousQueue = loanTatService.getPreviousQueue(vehicleLoanMaster.getWiNum());
        model.addAttribute("previousQueue", previousQueue);

        return model;
    }

    public String isExperianSendBack(String wiNum, Long slno) {
        List<LocalDate> BureauDate = vlcreditService.getVLCreditDetailstoCheckBureua(wiNum, slno)
                .stream().map(eachscore -> eachscore.getExpFetchDate().toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate()).collect(Collectors.toList());

        //List<LocalDate>  BureauDate =BureauDateTimestamp.stream().map(timestamp ->timestamp.toLocalDateTime().toLocalDate()).collect(Collectors.toList());
        LocalDate today = LocalDate.now();
        int experianDays = Integer.parseInt(misprmRepository.findByPCODE("EXPERIAN_DAYS").getPVALUE());
        int bday = fetchRepository.getBureauDays().isPresent() ? fetchRepository.getBureauDays().get() : experianDays;
        LocalDate cutoffdate = today.minusDays(bday);
        Boolean hasOldDate = BureauDate.stream().anyMatch(bureaudate -> bureaudate.isBefore(cutoffdate));
        if (hasOldDate) {
            log.error("bureauBlock Error");
            return "Y";
        } else {
            return "N";
        }
    }

    public Model modifyRMEntry(String slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(slno));
        String lockflg = "N";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(slno));
        List<Map<String, Object>> vehicleLoanWarnsWithAdditionalAttr = new ArrayList<>();
        List<VehicleLoanWarn> vehicleLoanWarns = vlwarn.getActiveAndNotDeletedVehicleLoanWarns(vehicleLoanMaster.getSlno());
        List<MaterialListData> materialListData=null;
        for (VehicleLoanWarn warn : vehicleLoanWarns) {
            Map<String, Object> map = new HashMap<>();
            map.put("vehicleLoanWarn", warn);
            String warncode = warn.getWarnCode();
            Long appid = warn.getApplicantId();
            String custid = null;
            CustomerDetails customerDetails = cd.findByAppId(appid);
            if (customerDetails != null) {
                custid = customerDetails.getCustId();
            } else {
                continue;
            }
            String wino = warn.getWiNum();
            CIFviewRequest requestjson = new CIFviewRequest();
            requestjson.setSlno(String.valueOf(slno));
            requestjson.setWinum(wino);
            requestjson.setAppid(String.valueOf(appid));
            requestjson.setCustID(custid);
            TabResponse respjson = cfview.getCustData(requestjson, request);
            String currentfinaclevalue = "";

            if (respjson.getStatus().equals("S")) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode data = null;
                try {
                    data = objectMapper.readTree(respjson.getMsg());
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing JSON", e);
                }


                switch (warncode) {
                    case "WAR001":
                        currentfinaclevalue = data.path("custDob").asText("");
                        break;
                    case "WAR002":
                    case "WAR003":
                        currentfinaclevalue = data.path("pan").asText("");
                        break;
                    case "WAR004":
                    case "WAR005":
                        currentfinaclevalue = data.path("aadhaarRefNo").asText("");
                        break;
                    case "WAR006":
                    case "WAR007":
                        currentfinaclevalue = data.path("passport").asText("");
                        break;
                    case "WAR008":
                        currentfinaclevalue = data.path("residentialStatus").asText("");
                        break;
                    case "WAR009":
                        currentfinaclevalue = data.path("minorFlag").asText("");
                        break;
                    case "WAR0010":
                    case "WAR0011":
                        currentfinaclevalue = data.path("visa").asText("");
                        break;
                    case "WAR0013":
                        currentfinaclevalue = data.path("customerName").asText("");
                        break;
                    case "WAR0017":
                    case "WAR0018":
                        currentfinaclevalue = data.path("cellPhone").asText("");
                        break;
                    case "WAR0019":
                        currentfinaclevalue = data.path("commEmail").asText("");
                        break;
                    case "WAR0012":
                    case "WAR0014":
                    case "WAR0015":
                    case "WAR0016":
                    case "WAR0020":
                        currentfinaclevalue = "-";
                        break;
                }
            }

            map.put("currentcbsval", currentfinaclevalue);
            vehicleLoanWarnsWithAdditionalAttr.add(map);
        }
        model.addAttribute("warning", vehicleLoanWarnsWithAdditionalAttr);
        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanBREService.getAmberColorDataForLoan(vehicleLoanMaster.getWiNum(), Long.valueOf(slno));
        List<Map<String, Object>> checkerLevels = vehicleLoanAmberService.getAllActiveDeviationLevel();
        model.addAttribute("checkerLevels", checkerLevels);
        List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataConsideringQueueDate(vehicleLoanMaster.getSlno());
        model.addAttribute("dkScoreData", dkScoreData);

        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else {
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));
        }
        List<VLHunterDetails> allHunterDetails = experianHunterResponseRepository
                .findAllByWiNumAndDelFlgOrderByTimestampDesc(vehicleLoanMaster.getWiNum(), "N");

        boolean hunterCheckPerformed = !allHunterDetails.isEmpty();
        model.addAttribute("hunterCheckPerformed", hunterCheckPerformed);

        boolean hunterMatchFound = false;
        boolean hunterReviewCompleted = true;
        if (hunterCheckPerformed) {
            for (VLHunterDetails detail : allHunterDetails) {
                if (detail.getMatches() > 0) { // Assuming a MATCH > 0 indicates a match
                    hunterMatchFound = true;
                    // Check if this match has been reviewed
                    if (detail.getReviewUser() == null || detail.getReviewDate() == null) {
                        hunterReviewCompleted = false;
                        break;
                    }
                }
            }
        }

        model.addAttribute("hunterMatchFound", hunterMatchFound);
        model.addAttribute("allHunterDetails", allHunterDetails);
        model.addAttribute("hunterReviewCompleted", hunterReviewCompleted);
        model.addAttribute("bureauBlock", isExperianSendBack(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno()));
        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
        String previousQueue = loanTatService.getPreviousQueue(vehicleLoanMaster.getWiNum());
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrService.findBySlno(vehicleLoanMaster.getSlno());
        fcvCpvCfr.ifPresent(record -> model.addAttribute("fcvCpvCfr", record));
        CardRateChangeResult rateChangeResult = eligibilityDetailsService.checkCardRateChanges(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        boolean cardRateChanged = false;
        if (rateChangeResult.isHasChanged()) {
            cardRateChanged = true;
        }
        List<Map<String, Object>> groupedData = fetchRepository.findGroupedPPCsByLevel();
        try {
            model.addAttribute("ppcData", new ObjectMapper().writeValueAsString(groupedData));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("panStatusList", misrctService.getCodeValuesByType("PAN1"));
        model.addAttribute("seedingStatusList", misrctService.getCodeValuesByType("PAN2"));
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        model.addAttribute("previousQueue", previousQueue);
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        model.addAttribute("octDetails", octdetailsservice.getOctDetails());
        model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
        model.addAttribute("RBCM", misrctService.getCodeValuesByType("RBCM"));
        materialListData=materialListDataRepository.findBySlno(vehicleLoanMaster.getSlno());
        model.addAttribute("materialListData",materialListData);
        return model;
    }


    public Model insOptOut(String slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(slno));
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
         if(vehicleLoanMaster.getRoiRequested()!=null && vehicleLoanMaster.getRoiRequested() && taskService.isPending(vehicleLoanMaster.getSlno(),"ROI_WAIVER")){
             model.addAttribute("error", "Request Pending For ROI Waiver !!");
        }
        else if(vehicleLoanMaster.getChargeWaiverRequested()!=null && vehicleLoanMaster.getChargeWaiverRequested() && taskService.isPending(vehicleLoanMaster.getSlno(),"CHARGE_WAIVER")){
             model.addAttribute("error", "Request Pending For Fee/Charge Waiver !!");
        }
        if(vehicleLoanMaster.getDocMode()==null && vehicleLoanMaster.getQueue().equals("BD") && loanDetails.getInsVal().equals("Y")) {
            model.addAttribute("loanDetails", loanDetails);
            model.addAttribute("winum", vehicleLoanMaster.getWiNum());
            model.addAttribute("wiNum", vehicleLoanMaster.getWiNum());
            model.addAttribute("slno", vehicleLoanMaster.getSlno());
            model.addAttribute("userdata", usd.getEmployee());
            model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
            model.addAttribute("feeData",  loanWaiverService.chargeWaivers(vehicleLoanMaster.getSlno()));
            try {
              EligibilityDetails elig=  eligibilityDetailsService.checkProgramEligibilityWithoutInsurance(vehicleLoanMaster.getWiNum(),vehicleLoanMaster.getSlno(),false);
                model.addAttribute("insData", elig);
                model.addAttribute("feeDataNew",  loanWaiverService.calculateProcessingFee(String.valueOf(vehicleLoanMaster.getSlno()),elig.getEligibleLoanAmt(),elig.getVehicleAmt(),false,vehicleLoanMaster.getWiNum()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
            if (!eligibilityDetailsOpt.isPresent()) {
                model.addAttribute("error", "No eligibility details found for the provided identifiers.");
            } else {
                EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
                model.addAttribute("eligibilityDetails", eligibilityDetails);
            }
        }
        else{
            throw new ValidationException(ValidationError.COM001,"Cannot Modify the WI Since Documentation has been Initiated");
        }
        return model;
    }





    @Override
    public Model modifyRCEntry(String slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(slno);
        String queue = fetchRepository.getUserRole(usd.getPPCNo());
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(slno));
        if (!vehicleLoanMaster.getQueue().equals(queue)) {
            throw new RuntimeException("Request Queue Mismatch");
        }
        String lockflg = "N";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(slno));
        if (lockuser == null) {
            VehicleLoanLock lk = new VehicleLoanLock();
            lk.setWiNum(vehicleLoanMaster.getWiNum());
            lk.setSlno(Long.valueOf(slno));
            lk.setLockedBy(usd.getPPCNo());
            lk.setLockedOn(new Date());
            lk.setLockFlg("Y");
            lk.setQueue("RC");
            lk.setDelFlg("N");
            vehicleLoanLockService.saveLock(lk);
        } else if (!lockuser.equals(usd.getPPCNo())) {
            lockflg = "Y";
        }

        List<Map<String, Object>> vehicleLoanWarnsWithAdditionalAttr = new ArrayList<>();
        List<VehicleLoanWarn> vehicleLoanWarns = vlwarn.getActiveAndNotDeletedVehicleLoanWarns(vehicleLoanMaster.getSlno());

        for (VehicleLoanWarn warn : vehicleLoanWarns) {
            Map<String, Object> map = new HashMap<>();
            map.put("vehicleLoanWarn", warn);
            String warncode = warn.getWarnCode();
            Long appid = warn.getApplicantId();
            String custid = null;
            CustomerDetails customerDetails = cd.findByAppId(appid);
            if (customerDetails != null) {
                custid = customerDetails.getCustId();
            } else {
                continue;
            }
            String wino = warn.getWiNum();
            CIFviewRequest requestjson = new CIFviewRequest();
            requestjson.setSlno(String.valueOf(slno));
            requestjson.setWinum(wino);
            requestjson.setAppid(String.valueOf(appid));
            requestjson.setCustID(custid);
            TabResponse respjson = cfview.getCustData(requestjson, request);
            String currentfinaclevalue = "";

            if (respjson.getStatus().equals("S")) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode data = null;
                try {
                    data = objectMapper.readTree(respjson.getMsg());
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing JSON", e);
                }


                switch (warncode) {
                    case "WAR001":
                        currentfinaclevalue = data.path("custDob").asText("");
                        break;
                    case "WAR002":
                    case "WAR003":
                        currentfinaclevalue = data.path("pan").asText("");
                        break;
                    case "WAR004":
                    case "WAR005":
                        currentfinaclevalue = data.path("aadhaarRefNo").asText("");
                        break;
                    case "WAR006":
                    case "WAR007":
                        currentfinaclevalue = data.path("passport").asText("");
                        break;
                    case "WAR008":
                        currentfinaclevalue = data.path("residentialStatus").asText("");
                        break;
                    case "WAR009":
                        currentfinaclevalue = data.path("minorFlag").asText("");
                        break;
                    case "WAR0010":
                    case "WAR0011":
                        currentfinaclevalue = data.path("visa").asText("");
                        break;
                    case "WAR0013":
                        currentfinaclevalue = data.path("customerName").asText("");
                        break;
                    case "WAR0017":
                    case "WAR0018":
                        currentfinaclevalue = data.path("cellPhone").asText("");
                        break;
                    case "WAR0019":
                        currentfinaclevalue = data.path("commEmail").asText("");
                        break;
                    case "WAR0012":
                    case "WAR0014":
                    case "WAR0015":
                    case "WAR0016":
                    case "WAR0020":
                        currentfinaclevalue = "-";
                        break;
                }
            }

            map.put("currentcbsval", currentfinaclevalue);
            vehicleLoanWarnsWithAdditionalAttr.add(map);
        }
        model.addAttribute("warning", vehicleLoanWarnsWithAdditionalAttr);


        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanBREService.getAmberColorDataForLoan(vehicleLoanMaster.getWiNum(), Long.valueOf(slno));

        //  VehicleLoanMaster loanMaster=vlservice.getMasById(Long.valueOf(trans_slno)).orElseThrow(() -> new RuntimeException("Master Not Found"));

        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));


        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        List<MaterialListData> materialListData=null;
        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (!eligibilityDetailsOpt.isPresent()) {
            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
        } else {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            model.addAttribute("eligibilityDetails", eligibilityDetails);

        }
        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrService.findBySlno(vehicleLoanMaster.getSlno());
        fcvCpvCfr.ifPresent(record -> model.addAttribute("fcvCpvCfr", record));
        model.addAttribute("loanDetails", loanDetails);
        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("panStatusList", misrctService.getCodeValuesByType("PAN1"));
        model.addAttribute("seedingStatusList", misrctService.getCodeValuesByType("PAN2"));
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        model.addAttribute("userRole", vehicleLoanMaster.getQueue());//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("decisionParam", misrctService.getCodeValuesByTypeOrdered(vehicleLoanMaster.getQueue()));//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("octDetails", octdetailsservice.getOctDetails());
        model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataForJSP(vehicleLoanMaster.getSlno());
        model.addAttribute("dkScoreData", dkScoreData);
        model.addAttribute("bureauBlock", isExperianSendBack(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno()));
        model.addAttribute("RBCM", misrctService.getCodeValuesByType("RBCM"));
        materialListData=materialListDataRepository.findBySlno(vehicleLoanMaster.getSlno());
        model.addAttribute("materialListData",materialListData);
        return model;
    }

    @Override
    public Model modifyBDEntry(String slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(slno));
        if (!"BD".equalsIgnoreCase(vehicleLoanMaster.getQueue())) {
            throw new RuntimeException("Request Queue Mismatch");
        }
        String lockflg = "N";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(slno));
        if (lockuser == null) {
            VehicleLoanLock lk = new VehicleLoanLock();
            lk.setWiNum(vehicleLoanMaster.getWiNum());
            lk.setSlno(Long.valueOf(slno));
            lk.setLockedBy(usd.getPPCNo());
            lk.setLockedOn(new Date());
            lk.setLockFlg("Y");
            lk.setQueue("BD");
            lk.setDelFlg("N");
            vehicleLoanLockService.saveLock(lk);
        } else if (!lockuser.equals(usd.getPPCNo())) {
            lockflg = "Y";
        }
        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanBREService.getAmberColorDataForLoan(vehicleLoanMaster.getWiNum(), Long.valueOf(slno));
        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));

        try {
            Map<String, String> staffDt = fetchRepository.fetchStaff(Long.valueOf(slno));
            model.addAttribute("brHead", staffDt.get("PPC_NAME"));
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("brHead", "-");
        }
        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (!eligibilityDetailsOpt.isPresent()) {
            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
        } else {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            model.addAttribute("eligibilityDetails", eligibilityDetails);
        }
        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrService.findBySlno(vehicleLoanMaster.getSlno());
        fcvCpvCfr.ifPresent(record -> model.addAttribute("fcvCpvCfr", record));
        model.addAttribute("loanDetails", loanDetails);
        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("panStatusList", misrctService.getCodeValuesByType("PAN1"));
        model.addAttribute("seedingStatusList", misrctService.getCodeValuesByType("PAN2"));
        model.addAttribute("ModeofOper", misrctService.getCodeValuesByType("MOD"));
//        model.addAttribute("sancLetter", fetchRepository.getDocument("206", "SANCLTR"));
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        model.addAttribute("repaymentDetails", repaymentService.getRepaymentDetails(Long.valueOf(slno)));
        model.addAttribute("userRole", vehicleLoanMaster.getQueue());//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("decisionParam", misrctService.getCodeValuesByTypeOrdered(vehicleLoanMaster.getQueue()));//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("bankName", fetchRepository.getBankCode());
        model.addAttribute("roiLevels", fetchRepository.findRoiLevel());
        model.addAttribute("feeLevels", fetchRepository.findProceessLevel());
        model.addAttribute("subQueueData", taskService.getBySlno(Long.valueOf(slno)));
        model.addAttribute("processFeeMast", fetchRepository.fetchProccessingFee(slno));
        model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
        model.addAttribute("octDetails", octdetailsservice.getOctDetails());
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataForJSP(vehicleLoanMaster.getSlno());
        model.addAttribute("dkScoreData", dkScoreData);
        List<VehicleLoanSubqueueTask> tasks = taskService.getSubQueueTasks(Long.valueOf(slno), "ROI_WAIVER");
        Optional<VehicleLoanSubqueueTask> compRoi_ = tasks.stream().filter(t -> t.getStatus().equalsIgnoreCase("COMPLETED")).findFirst();
        String initalROI = "";
        if (compRoi_.isPresent()) {
            VehicleLoanRoiWaiver roi = compRoi_.get().getRoiWaiver();
            initalROI = roi.getInitialRoi() != null ? roi.getInitialRoi().toString() : "";
        }
        model.addAttribute("initalROI", initalROI);
        if (vehicleLoanMaster.getDocMode() == null) {
        } else if (vehicleLoanMaster.getDocMode().equals("D")) {
            model.addAttribute("legalDoc", legalityService.findAllBySlno(Long.valueOf(slno)));
        } else if (vehicleLoanMaster.getDocMode().equals("M")) {
            model.addAttribute("manDoc", manDocService.findAllBySlno(Long.valueOf(slno)));
        }

        return model;
    }

    @Override
    public Model fetchWiBog(String slno, HttpServletRequest request, Model model) {

        usd.setTrans_slno(slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(slno));
        String lockflg = "N";
        String lockuser = "";
        List<VehicleLoanSubqueueTask> tasks = vehicleLoanSubqueueTaskRepository.findPendingTasksByWiNum(vehicleLoanMaster.getWiNum());
        for (VehicleLoanSubqueueTask task : tasks) {
            if (task.getTaskType().equals("CIF_CREATION") && task.getStatus().equals("PENDING") && task.getLockFlg() != null &&
                    task.getLockFlg().equals("Y")) {
                lockuser = task.getLockedBy();
                break;
            }
        }
        //String lockuser = vehicleLoanLockService.Locked(Long.valueOf(slno));
        //System.out.println("lockuserrrrrrrrrrrrrrrrr:"+lockuser);
        if (lockuser == null || lockuser.trim().isEmpty()) {
            loanWaiverService.lockPendingSubTasks(vehicleLoanMaster.getWiNum(), usd.getPPCNo(), Long.parseLong(slno), "CIF_CREATION");
        } else if (!lockuser.equals(usd.getPPCNo())) {
            lockflg = "Y";
        }
        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanBREService.getAmberColorDataForLoan(vehicleLoanMaster.getWiNum(), Long.valueOf(slno));

        //  VehicleLoanMaster loanMaster=vlservice.getMasById(Long.valueOf(trans_slno)).orElseThrow(() -> new RuntimeException("Master Not Found"));

        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));

        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        model.addAttribute("loanDetails", loanDetails);

        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (!eligibilityDetailsOpt.isPresent()) {
            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
        } else {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            model.addAttribute("eligibilityDetails", eligibilityDetails);

        }
        List<MaterialListData> materialListData=null;
        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        List<Misrct> singleDedupeRelations = losDedupeService.getSingleDedupeRelations("RLDEP");
        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
        model.addAttribute("singleDedupeRelations", singleDedupeRelations);
        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("panStatusList", misrctService.getCodeValuesByType("PAN1"));
        model.addAttribute("seedingStatusList", misrctService.getCodeValuesByType("PAN2"));
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("userRole", vehicleLoanMaster.getQueue());//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("decisionParam", misrctService.getCodeValuesByTypeOrdered(vehicleLoanMaster.getQueue()));//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("octDetails", octdetailsservice.getOctDetails());
        model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
        model.addAttribute("RBCM", misrctService.getCodeValuesByType("RBCM"));
        materialListData=materialListDataRepository.findBySlno(vehicleLoanMaster.getSlno());
        model.addAttribute("materialListData",materialListData);

        List<Map<String, Object>> vehicleLoanWarnsWithAdditionalAttr = new ArrayList<>();
        List<VehicleLoanWarn> vehicleLoanWarns = vlwarn.getActiveAndNotDeletedVehicleLoanWarns(vehicleLoanMaster.getSlno());
        for (VehicleLoanWarn warn : vehicleLoanWarns) {
            Map<String, Object> map = new HashMap<>();
            map.put("vehicleLoanWarn", warn);
            String warncode = warn.getWarnCode();
            Long appid = warn.getApplicantId();
            String custid = cd.findByAppId(appid).getCustId();
            //Long slno = warn.getSlno();
            String wino = warn.getWiNum();
            CIFviewRequest requestjson = new CIFviewRequest();
            requestjson.setSlno(String.valueOf(warn.getSlno()));
            requestjson.setWinum(wino);
            requestjson.setAppid(String.valueOf(appid));
            requestjson.setCustID(custid);
            TabResponse respjson = cfview.getCustData(requestjson, request);
            String currentfinaclevalue = "";
            if (respjson.getStatus().equals("S")) {
                JSONObject data = new JSONObject(respjson.getMsg());
                if (warncode.equals("WAR001")) {
                    currentfinaclevalue = data.has("custDob") && !data.isNull("custDob") ? data.getString("custDob") : "";
                }
                if (warncode.equals("WAR002") || warncode.equals("WAR003")) {
                    currentfinaclevalue = data.has("pan") && !data.isNull("pan") ? data.getString("pan") : "";
                }
                if (warncode.equals("WAR004") || warncode.equals("WAR005")) {
                    currentfinaclevalue = data.has("aadhaarRefNo") && !data.isNull("aadhaarRefNo") ? data.getString("aadhaarRefNo") : "";
                }
                if (warncode.equals("WAR006") || warncode.equals("WAR007")) {
                    currentfinaclevalue = data.has("passport") && !data.isNull("passport") ? data.getString("passport") : "";
                }
                if (warncode.equals("WAR008")) {
                    currentfinaclevalue = data.has("residentialStatus") && !data.isNull("residentialStatus") ? data.getString("residentialStatus") : "";
                }
                if (warncode.equals("WAR009")) {
                    currentfinaclevalue = data.has("minorFlag") && !data.isNull("minorFlag") ? data.getString("minorFlag") : "";
                }
                if (warncode.equals("WAR0010") || warncode.equals("WAR0011")) {
                    currentfinaclevalue = data.has("visa") && !data.isNull("visa") ? data.getString("visa") : "";
                }
                if (warncode.equals("WAR0012") || warncode.equals("WAR0014") || warncode.equals("WAR0015") || warncode.equals("WAR0016") || warncode.equals("WAR0020")) {
                    currentfinaclevalue = "-";
                }
                if (warncode.equals("WAR0013")) {
                    currentfinaclevalue = data.has("customerName") && !data.isNull("customerName") ? data.getString("customerName") : "";
                }
                if (warncode.equals("WAR0017") || warncode.equals("WAR0018")) {
                    currentfinaclevalue = data.has("cellPhone") && !data.isNull("cellPhone") ? data.getString("cellPhone") : "";
                }
                if (warncode.equals("WAR0019")) {
                    currentfinaclevalue = data.has("commEmail") && !data.isNull("commEmail") ? data.getString("commEmail") : "";
                }
            }
            map.put("currentcbsval", currentfinaclevalue);
            vehicleLoanWarnsWithAdditionalAttr.add(map);
        }
        model.addAttribute("warning", vehicleLoanWarnsWithAdditionalAttr);
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        List<Map<String, Object>> bureauDetails = fetchRepository.getBureauDetailsByApplicant(vehicleLoanMaster.getWiNum());
        model.addAttribute("bureauDetails", bureauDetails);
        //getBureauDetailsByApplicant
        List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataForJSP(vehicleLoanMaster.getSlno());
        model.addAttribute("dkScoreData", dkScoreData);
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrService.findBySlno(vehicleLoanMaster.getSlno());
        fcvCpvCfr.ifPresent(record -> model.addAttribute("fcvCpvCfr", record));
        VehicleLoanVehicle vehicleDetails = vehicleDetailsService.fetchExistingbyWinumandSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (vehicleDetails == null) {
            model.addAttribute("error", "No vehicle details found for the provided identifiers.");
        }
        model.addAttribute("vehicleDetails", vehicleDetails);
        return model;
    }


    @Override
    public Model wiSearch(String slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findAppBySlno(Long.valueOf(slno));
        String lockflg = "N";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(slno));
        List<MaterialListData> materialListData=null;
        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanBREService.getAmberColorDataForLoan(vehicleLoanMaster.getWiNum(), Long.valueOf(slno));
        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));

        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        model.addAttribute("lockuser", lockuser);
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (!eligibilityDetailsOpt.isPresent()) {
            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
        } else {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            model.addAttribute("eligibilityDetails", eligibilityDetails);
        }

        VehicleLoanVehicle vehicleDetails = vehicleDetailsService.fetchExistingbyWinumandSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (vehicleDetails == null) {
            model.addAttribute("error", "No vehicle details found for the provided identifiers.");
        }
        model.addAttribute("vehicleDetails", vehicleDetails);
        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrService.findBySlno(vehicleLoanMaster.getSlno());
        fcvCpvCfr.ifPresent(record -> model.addAttribute("fcvCpvCfr", record));
        model.addAttribute("loanDetails", loanDetails);
        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        model.addAttribute("lockflg", lockflg);
        if(vehicleLoanMaster.getRbcpcCheckerUser()!=null) {
            List<Map<String, String>> ppcDetails = fetchRepository.getPPCDetails(vehicleLoanMaster.getRbcpcCheckerUser());
            model.addAttribute("ppcDetails", ppcDetails);
        }


        String QueueName = "";
        try {
            QueueName = misrctService.getByCodeValue("QT", vehicleLoanMaster.getQueue()).getCodedesc();
        } catch (Exception e) {
            QueueName = "";
        }
        model.addAttribute("queueName", QueueName);
//        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("sancLetter", fetchRepository.getDocument(slno, "SANCLTR"));
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        model.addAttribute("repaymentDetails", fetchRepository.getRepaymentAcctDetails(Long.valueOf(slno), false));
        model.addAttribute("userRole", vehicleLoanMaster.getQueue());//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("decisionParam", misrctService.getCodeValuesByTypeOrdered(vehicleLoanMaster.getQueue()));//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("bankName", fetchRepository.getBankCode());
        model.addAttribute("doPPC", fetchRepository.getDODetails(vehicleLoanMaster.getWiNum()));
        List<Map<String, String>> cifCreationDetails = fetchRepository.getCIFCreationDetails(vehicleLoanMaster.getWiNum());
        model.addAttribute("subqueCIFPendingInfoList", cifCreationDetails);
        Map<String, String> disbursementDetails = fetchRepository.getDisbursementDetails(vehicleLoanMaster.getWiNum());
        model.addAttribute("disbursementDetails", disbursementDetails);
        List<Map<String, String>> subquePendingInfoList = fetchRepository.getSubquePendingInfo(vehicleLoanMaster.getWiNum());
        String wiProgram = fetchRepository.getWIProgram(Long.valueOf(slno));
        SanctionDetailsDTO sanctionDetails = fetchRepository.fetchFinalSanctionDetails(Long.valueOf(slno));
        model.addAttribute("sanctionDetails", sanctionDetails);
        model.addAttribute("subquePendingInfoList", subquePendingInfoList);
        model.addAttribute("wiProgram", wiProgram);
        model.addAttribute("roiLevels", fetchRepository.findRoiLevel());
        model.addAttribute("subQueueData", taskService.getBySlno(Long.valueOf(slno)));
        model.addAttribute("processFeeFinal", fetchRepository.fetchProccessingFeeAcOpen(slno));
        model.addAttribute("sanctionDetailsFinal", fetchRepository.fetchSanctionDetailsFinal(Long.valueOf(slno), false));
        model.addAttribute("acctlabels", misrctService.getCodeValuesByType("LB"));
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));


        VehicleLoanSubqueueTask vehicleLoanSubqueueTask = taskService.getSubTaskByTypeAndStatus(Long.valueOf(slno), "SAN_MOD", "PENDING");
        if (vehicleLoanSubqueueTask != null) {
            VehicleLoanSanMod vehicleLoanSanMod = vehicleLoanSanModService.findByTaskId(vehicleLoanSubqueueTask.getTaskId());
            model.addAttribute("vehicleLoanSanMod", vehicleLoanSanMod);
        }

        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(Long.valueOf(slno), "N");
        model.addAttribute("vehicleLoanAccount", vehicleLoanAccount);
        model.addAttribute("dealeraccifsc", fetchDealerAccNumberAndIfsc(Long.parseLong(slno), vehicleLoanMaster.getWiNum(), true));
        model.addAttribute("octDetails", octdetailsservice.getOctDetails());
        model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("panStatusList", misrctService.getCodeValuesByType("PAN1"));
        model.addAttribute("seedingStatusList", misrctService.getCodeValuesByType("PAN2"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        model.addAttribute("RBCM", misrctService.getCodeValuesByType("RBCM"));
        materialListData=materialListDataRepository.findBySlno(vehicleLoanMaster.getSlno());
        model.addAttribute("materialListData",materialListData);
        String currentActiveTab = "";//acc label,acc open, nach, neft, disb, else san letter, remakrs
        Boolean nachflag = false;
        Optional<NACHMandate> op = nachMandateService.getNACHMandateBySlno(Long.parseLong(slno));
        if (op.isPresent()) {
            NACHMandate nachMandate = op.get();
            nachflag = true;
            model.addAttribute("nachMandate", nachMandate);
        }

        if (vehicleLoanAccount != null) {
            if (("Y".equals(vehicleLoanAccount.getNeftflagdealer()) || "Y".equals(vehicleLoanAccount.getFiflag_dealer())) && ("Y".equals(vehicleLoanAccount.getNeftflagmanuf()) || "Y".equals(vehicleLoanAccount.getFiflag_manu()))) {
                //neft tab is completed
                currentActiveTab = "rmk";
            } else if (("Y".equals(vehicleLoanAccount.getNeftflagdealer()) || "Y".equals(vehicleLoanAccount.getFiflag_dealer())) || ("Y".equals(vehicleLoanAccount.getNeftflagmanuf()) || "Y".equals(vehicleLoanAccount.getFiflag_manu()))) {
                //neft tab is in progress
                currentActiveTab = "neft";
            } else if ("Y".equals(vehicleLoanAccount.getDisbflag())) {//ie disburseemnt to pool account is done
                currentActiveTab = "disb";
            } else if (nachflag) {
                currentActiveTab = "nach";
            } else {
                currentActiveTab = "acopn";
            }
        } else {
            List<VehicleLoanAcctLabels> vehicleLoanAcctLabels = vehicleLoanMaster.getVehicleLoanAcctLabels();
            if (vehicleLoanAcctLabels != null && vehicleLoanAcctLabels.size() > 0) {
                currentActiveTab = "label";
            } else {
                currentActiveTab = "sanletter";
            }
        }
        model.addAttribute("activeTab", currentActiveTab);
        List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataForJSP(vehicleLoanMaster.getSlno());
        model.addAttribute("dkScoreData", dkScoreData);
        model.addAttribute("remarks", fetchRepository.findLatestRemarks(vehicleLoanMaster.getWiNum()));
        return model;
    }

    @Override
    public Model fetchBOGAssetDetails(String slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(slno));
        String lockflg = "N";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(slno));
        if (lockuser == null) {
            VehicleLoanLock lk = new VehicleLoanLock();
            lk.setWiNum(vehicleLoanMaster.getWiNum());
            lk.setSlno(Long.valueOf(slno));
            lk.setLockedBy(usd.getPPCNo());
            lk.setLockedOn(new Date());
            lk.setLockFlg("Y");
            lk.setQueue("ACOPN");
            lk.setDelFlg("N");
            vehicleLoanLockService.saveLock(lk);
        } else if (!lockuser.equals(usd.getPPCNo())) {
            lockflg = "Y";
        }
        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanBREService.getAmberColorDataForLoan(vehicleLoanMaster.getWiNum(), Long.valueOf(slno));
        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));

        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (!eligibilityDetailsOpt.isPresent()) {
            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
        } else {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            model.addAttribute("eligibilityDetails", eligibilityDetails);
        }

        VehicleLoanVehicle vehicleDetails = vehicleDetailsService.fetchExistingbyWinumandSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (vehicleDetails == null) {
            model.addAttribute("error", "No vehicle details found for the provided identifiers.");
        }
        model.addAttribute("vehicleDetails", vehicleDetails);
        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrService.findBySlno(vehicleLoanMaster.getSlno());
        fcvCpvCfr.ifPresent(record -> model.addAttribute("fcvCpvCfr", record));
        model.addAttribute("loanDetails", loanDetails);
        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("sancLetter", fetchRepository.getDocument(slno, "SANCLTR"));
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        model.addAttribute("repaymentDetails", fetchRepository.getRepaymentAcctDetails(Long.valueOf(slno)));
        model.addAttribute("userRole", vehicleLoanMaster.getQueue());//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("decisionParam", misrctService.getCodeValuesByTypeOrdered(vehicleLoanMaster.getQueue()));//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("bankName", fetchRepository.getBankCode());
        model.addAttribute("roiLevels", fetchRepository.findRoiLevel());
        model.addAttribute("subQueueData", taskService.getBySlno(Long.valueOf(slno)));
        model.addAttribute("processFeeFinal", fetchRepository.fetchProccessingFeeAcOpen(slno));
        model.addAttribute("sanctionDetailsFinal", fetchRepository.fetchSanctionDetailsFinal(Long.valueOf(slno)));
        model.addAttribute("acctlabels", misrctService.getCodeValuesByType("LB"));
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));


        VehicleLoanSubqueueTask vehicleLoanSubqueueTask = taskService.getSubTaskByTypeAndStatus(Long.valueOf(slno), "SAN_MOD", "PENDING");
        if (vehicleLoanSubqueueTask != null) {
            VehicleLoanSanMod vehicleLoanSanMod = vehicleLoanSanModService.findByTaskId(vehicleLoanSubqueueTask.getTaskId());
            model.addAttribute("vehicleLoanSanMod", vehicleLoanSanMod);
        }

        VehicleLoanAccount vehicleLoanAccount = accountOpeningRepository.findBySlnoAndDelflag(Long.valueOf(slno), "N");
        model.addAttribute("vehicleLoanAccount", vehicleLoanAccount);
        model.addAttribute("dealeraccifsc", fetchDealerAccNumberAndIfsc(Long.parseLong(slno), vehicleLoanMaster.getWiNum()));
        model.addAttribute("octDetails", octdetailsservice.getOctDetails());
        model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("panStatusList", misrctService.getCodeValuesByType("PAN1"));
        model.addAttribute("seedingStatusList", misrctService.getCodeValuesByType("PAN2"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        String currentActiveTab = "";//acc label,acc open, nach, neft, disb, else san letter, remakrs
        Boolean nachflag = false;
        Optional<NACHMandate> op = nachMandateService.getNACHMandateBySlno(Long.parseLong(slno));
        if (op.isPresent()) {
            NACHMandate nachMandate = op.get();
            nachflag = true;
            model.addAttribute("nachMandate", nachMandate);
        }
        if (vehicleLoanAccount != null) {
            if (("SUCCESS".equals(vehicleLoanAccount.getNeftflagdealer()) || "SUCCESS".equals(vehicleLoanAccount.getFiflag_dealer())) && ("SUCCESS".equals(vehicleLoanAccount.getNeftflagmanuf()) || "SUCCESS".equals(vehicleLoanAccount.getFiflag_manu()))) {
                //neft tab is completed
                currentActiveTab = "rmk";
            } else if (("SUCCESS".equals(vehicleLoanAccount.getNeftflagdealer()) || "SUCCESS".equals(vehicleLoanAccount.getFiflag_dealer())) || ("SUCCESS".equals(vehicleLoanAccount.getNeftflagmanuf()) || "SUCCESS".equals(vehicleLoanAccount.getFiflag_manu()))) {
                //neft tab is in progress
                currentActiveTab = "neft";
            } else if ("SUCCESS".equals(vehicleLoanAccount.getDisbflag())) {//ie disburseemnt to pool account is done
                currentActiveTab = "disb";
            } else if (nachflag) {
                currentActiveTab = "nach";
            } else {
                currentActiveTab = "acopn";
            }
        } else {
            List<VehicleLoanAcctLabels> vehicleLoanAcctLabels = vehicleLoanMaster.getVehicleLoanAcctLabels();
            if (vehicleLoanAcctLabels != null && vehicleLoanAcctLabels.size() > 0) {
                currentActiveTab = "label";
            } else {
                currentActiveTab = "sanletter";
            }
        }
        model.addAttribute("activeTab", currentActiveTab);
        List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataForJSP(vehicleLoanMaster.getSlno());
        model.addAttribute("dkScoreData", dkScoreData);
        VehicleLoanNeftInputs vehicleLoanNeftInputs = vehicleLoanNeftRepository.findBySlnoAndDelflag(Long.parseLong(slno), "N");
        model.addAttribute("vehicleLoanNeftInputs", vehicleLoanNeftInputs);
        return model;
    }

    public String fetchDealerAccNumberAndIfsc(Long slno, String winum) {
        VehicleLoanVehicle vehicle = vehicleDetailsService.fetchExistingbyWinumandSlno(winum, slno);
        String dealerCode = "", dealerSubcode = "", cityId = "", cityName = "", oemid = "";
        if (vehicle != null) {
            dealerCode = vehicle.getDealerCode() == null ? "" : vehicle.getDealerCode();
            dealerSubcode = vehicle.getDealerSubCode();
            cityId = vehicle.getDealerCityId();
            cityName = vehicle.getDealerCityName();
            oemid = vehicle.getMakeId();
        } else
            return null;

        // String r = vehicle.getDealerAccount()+"-"+vehicle.getDealerIfsc();// fetchRepository.fetchDealerAcctNoAndIfsc(dealerCode, dealerSubcode, cityId, cityName, oemid);
        String r = fetchRepository.fetchDealerAcctNoAndIfsc(dealerCode, dealerSubcode, cityId, cityName, oemid, slno.toString());
        //r="0587054000000224-SIBL0000587";

        if (r == null || r.trim().isEmpty() || !r.contains("-")) {
            throw new RuntimeException("Dealer account not found");
        }
        return r;
    }

    public String fetchDealerAccNumberAndIfsc(Long slno, String winum, Boolean validator) {
        VehicleLoanVehicle vehicle = vehicleDetailsService.fetchExistingbyWinumandSlno(winum, slno);
        String dealerCode = "", dealerSubcode = "", cityId = "", cityName = "", oemid = "";
        if (vehicle != null) {
            dealerCode = vehicle.getDealerCode() == null ? "" : vehicle.getDealerCode();
            dealerSubcode = vehicle.getDealerSubCode();
            cityId = vehicle.getDealerCityId();
            cityName = vehicle.getDealerCityName();
            oemid = vehicle.getMakeId();
        } else
            return null;
        //String r = vehicle.getDealerAccount()+"-"+vehicle.getDealerIfsc();//fetchRepository.fetchDealerAcctNoAndIfsc(dealerCode, dealerSubcode, cityId, cityName, oemid);
        String r = fetchRepository.fetchDealerAcctNoAndIfsc(dealerCode, dealerSubcode, cityId, cityName, oemid, slno.toString());
        //r="0587054000000224-SIBL0000587";
        if ((r == null || r.trim().isEmpty() || !r.contains("-")) && validator) {
            return null;
        }
        return r;
    }

    @Override
    public Model modifyCRTAmber(String slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(slno));
        String lockflg = "N";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(slno));
        if (lockuser == null) {
            VehicleLoanLock lk = new VehicleLoanLock();
            lk.setWiNum(vehicleLoanMaster.getWiNum());
            lk.setSlno(Long.valueOf(slno));
            lk.setLockedBy(usd.getPPCNo());
            lk.setLockedOn(new Date());
            lk.setLockFlg("Y");
            lk.setQueue("CA");
            lk.setDelFlg("N");
            vehicleLoanLockService.saveLock(lk);
        } else if (!lockuser.equals(usd.getPPCNo())) {
            lockflg = "Y";
        }
        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanBREService.getAmberColorDataForLoan(vehicleLoanMaster.getWiNum(), Long.valueOf(slno));

        //  VehicleLoanMaster loanMaster=vlservice.getMasById(Long.valueOf(trans_slno)).orElseThrow(() -> new RuntimeException("Master Not Found"));

        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));


        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        model.addAttribute("loanDetails", loanDetails);

        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (!eligibilityDetailsOpt.isPresent()) {
            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
        } else {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            model.addAttribute("eligibilityDetails", eligibilityDetails);

        }

        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());

        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();

        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("panStatusList", misrctService.getCodeValuesByType("PAN1"));
        model.addAttribute("seedingStatusList", misrctService.getCodeValuesByType("PAN2"));
        //model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("userRole", vehicleLoanMaster.getQueue());//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("decisionParam", misrctService.getCodeValuesByTypeOrdered(vehicleLoanMaster.getQueue()));//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        List<Map<String, Object>> vehicleLoanWarnsWithAdditionalAttr = new ArrayList<>();
        List<VehicleLoanWarn> vehicleLoanWarns = vlwarn.getActiveAndNotDeletedVehicleLoanWarns(vehicleLoanMaster.getSlno());
        for (VehicleLoanWarn warn : vehicleLoanWarns) {
            Map<String, Object> map = new HashMap<>();
            map.put("vehicleLoanWarn", warn);
            String warncode = warn.getWarnCode();
            Long appid = warn.getApplicantId();
            VehicleLoanKyc kycdetails = kycrepo.findByApplicantIdAndDelFlg(appid, "N");
            String visa_oci_type = kycdetails.getVisaOciType();
            String custid = cd.findByAppId(appid).getCustId();
            //Long slno = warn.getSlno();
            String wino = warn.getWiNum();
            CIFviewRequest requestjson = new CIFviewRequest();
            requestjson.setSlno(String.valueOf(warn.getSlno()));
            requestjson.setWinum(wino);
            requestjson.setAppid(String.valueOf(appid));
            requestjson.setCustID(custid);
            TabResponse respjson = cfview.getCustData(requestjson, request);
            String currentfinaclevalue = "";
            if (respjson.getStatus().equals("S")) {
                JSONObject data = new JSONObject(respjson.getMsg());
                if (warncode.equals("WAR001")) {
                    currentfinaclevalue = data.isNull("custDob") ? "" : data.getString("custDob");
                }
                if (warncode.equals("WAR002")) {
                    currentfinaclevalue = data.isNull("pan") ? "" : data.getString("pan");
                }
                if (warncode.equals("WAR003")) {
                    currentfinaclevalue = data.isNull("pan") ? "" : data.getString("pan");
                }
                if (warncode.equals("WAR004")) {
                    currentfinaclevalue = data.isNull("aadhaarRefNo") ? "" : data.getString("aadhaarRefNo");
                }
                if (warncode.equals("WAR005")) {
                    currentfinaclevalue = data.isNull("aadhaarRefNo") ? "" : data.getString("aadhaarRefNo");
                }
                if (warncode.equals("WAR006")) {
                    currentfinaclevalue = data.isNull("passport") ? "" : data.getString("passport");
                }
                if (warncode.equals("WAR007")) {
                    currentfinaclevalue = data.isNull("passport") ? "" : data.getString("passport");
                }
                if (warncode.equals("WAR008")) {
                    currentfinaclevalue = data.isNull("residentialStatus") ? "" : data.getString("residentialStatus");
                }
                if (warncode.equals("WAR009")) {
                    currentfinaclevalue = data.isNull("minorFlag") ? "" : data.getString("minorFlag");
                }
                /*
                if (warncode.equals("WAR0010")) {
                    currentfinaclevalue = data.isNull("visa") ? "" : data.getString("visa");
                }
                if (warncode.equals("WAR0011")) {
                    currentfinaclevalue = data.isNull("visa") ? "" : data.getString("visa");
                }

                 */
                if (warncode.equals("WAR0010") || warncode.equals("WAR0011")) {
                    if (visa_oci_type.equals("V")) {
                        currentfinaclevalue = data.has("visa") && !data.isNull("visa") ? data.getString("visa") : "";
                    } else if (visa_oci_type.equals("O")) {
                        currentfinaclevalue = data.has("ociCard") && !data.isNull("ociCard") ? data.getString("ociCard") : "";
                    } else if (visa_oci_type.equals("C")) {
                        currentfinaclevalue = data.has("cdnNo") && !data.isNull("cdnNo") ? data.getString("cdnNo") : "";
                    }
                }
                if (warncode.equals("WAR0012")) {
                    currentfinaclevalue = "-";
                }
                if (warncode.equals("WAR0013")) {
                    currentfinaclevalue = data.isNull("customerName") ? "" : data.getString("customerName");
                }
                if (warncode.equals("WAR0014")) {
                    currentfinaclevalue = "-";
                }
                if (warncode.equals("WAR0015")) {
                    currentfinaclevalue = "-";
                }
                if (warncode.equals("WAR0016")) {
                    currentfinaclevalue = "-";
                }
                if (warncode.equals("WAR0017")) {
                    currentfinaclevalue = data.isNull("cellPhone") ? "" : data.getString("cellPhone");
                }
                if (warncode.equals("WAR0018")) {
                    currentfinaclevalue = data.isNull("cellPhone") ? "" : data.getString("cellPhone");
                }
                if (warncode.equals("WAR0019")) {
                    currentfinaclevalue = data.isNull("commEmail") ? "" : data.getString("commEmail");
                }
                if (warncode.equals("WAR0020")) {
                    currentfinaclevalue = "-";
                }
            }
            map.put("currentcbsval", currentfinaclevalue);
            vehicleLoanWarnsWithAdditionalAttr.add(map);
        }
        List<MaterialListData> materialListData=null;
        model.addAttribute("warning", vehicleLoanWarnsWithAdditionalAttr);
        List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataConsideringQueueDate(vehicleLoanMaster.getSlno());
        model.addAttribute("dkScoreData", dkScoreData);
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrService.findBySlno(vehicleLoanMaster.getSlno());
        fcvCpvCfr.ifPresent(record -> model.addAttribute("fcvCpvCfr", record));
        model.addAttribute("octDetails", octdetailsservice.getOctDetails());
        model.addAttribute("tppData",  misrctService.getCodeValuesByType("II"));
        model.addAttribute("commProof", misrctService.getCodeValuesByType("CP"));
        model.addAttribute("panStatusList", misrctService.getCodeValuesByType("PAN1"));
        model.addAttribute("seedingStatusList", misrctService.getCodeValuesByType("PAN2"));
        model.addAttribute("annualIncome", misrctService.getCodeValuesByType("INC"));
        model.addAttribute("RBCM", misrctService.getCodeValuesByType("RBCM"));
        materialListData=materialListDataRepository.findBySlno(vehicleLoanMaster.getSlno());
        model.addAttribute("materialListData",materialListData);
        return model;
    }

    @Override
    public Model modifyWaiverEntry(String slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(slno));
        String lockflg = "N";
        String roiLockflg = "N", roiLockedBy = "";
        String chargeLockflg = "N", chargeLockedBy = "";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(slno));
        LockStatusDTO lockStatus = loanWaiverService.checkWaiverLocks(vehicleLoanMaster.getWiNum(), usd.getPPCNo());
        WaiverAccessDTO userAccess = vlservice.findWaiverAccess(usd.getPPCNo());

        model.addAttribute("userAccess", userAccess);
        if (lockStatus.isAnyLocked()) {
            if (lockStatus.isRoiLocked()) {
                roiLockflg = "Y";
                roiLockedBy = lockStatus.getRoiLockedBy();
            }
            if (lockStatus.isChargeLocked()) {
                chargeLockflg = "Y";
                chargeLockedBy = lockStatus.getChargeLockedBy();
            }
        } else {
            log.info("No locks found for the waiver tasks. set lock if user has access {} {} {}", vehicleLoanMaster.getWiNum(), usd.getPPCNo(), vehicleLoanMaster.getSlno());
        }
        log.info("No locks found for the waiver tasks. set lock if user has access {} {} {}", vehicleLoanMaster.getWiNum(), usd.getPPCNo(), vehicleLoanMaster.getSlno());
        lockStatus = loanWaiverService.lockWaiverTasks(vehicleLoanMaster.getWiNum(), usd.getPPCNo(), vehicleLoanMaster.getSlno());
        model.addAttribute("lockStatus", lockStatus);

        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanBREService.getAmberColorDataForLoan(vehicleLoanMaster.getWiNum(), Long.valueOf(slno));
        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));

        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (!eligibilityDetailsOpt.isPresent()) {
            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
        } else {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            model.addAttribute("eligibilityDetails", eligibilityDetails);
        }
        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrService.findBySlno(vehicleLoanMaster.getSlno());
        fcvCpvCfr.ifPresent(record -> model.addAttribute("fcvCpvCfr", record));
        model.addAttribute("loanDetails", loanDetails);
        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("roiLockflg", roiLockflg);
        model.addAttribute("chargeLockflg", chargeLockflg);
        model.addAttribute("roiLockedBy", roiLockedBy);
        model.addAttribute("chargeLockedBy", chargeLockedBy);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("sancLetter", fetchRepository.getDocument(slno, "SANCLTR"));
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        model.addAttribute("repaymentDetails", repaymentService.getRepaymentDetails(Long.valueOf(slno)));
        model.addAttribute("userRole", vehicleLoanMaster.getQueue());//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("decisionParam", misrctService.getCodeValuesByTypeOrdered(vehicleLoanMaster.getQueue()));//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("bankName", fetchRepository.getBankCode());
        model.addAttribute("roiLevels", fetchRepository.findRoiLevel());
        model.addAttribute("subQueueData", taskService.getBySlno(Long.valueOf(slno)));
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));
        model.addAttribute("roname", fetchRepository.getROName(vehicleLoanMaster.getSolId()));
        List<Map<String, Object>> dkScoreData = dkScoreService.fetchDKScoreDataForJSP(vehicleLoanMaster.getSlno());
        model.addAttribute("dkScoreData", dkScoreData);
        List<VehicleLoanSubqueueTask> subqueueWaiverTasks = taskService.getSubQueueTasks(Long.valueOf(slno), "CHARGE_WAIVER");
        List<VehicleLoanSubqueueTask> subqueueROITasks = taskService.getSubQueueTasks(Long.valueOf(slno), "ROI_WAIVER");
        if (!subqueueWaiverTasks.isEmpty()) {
            VehicleLoanSubqueueTask latestWaiverTask = subqueueWaiverTasks.get(0);
            model.addAttribute("latestWaiverTask", latestWaiverTask);
            if ("COMPLETED".equals(latestWaiverTask.getStatus())) {
                VehicleLoanChargeWaiver latestChargeWaiver = taskService.getLatestCompletedChargeWaiver(latestWaiverTask.getTaskId());
                model.addAttribute("latestChargeWaiver", latestChargeWaiver);
            }
        }
        if (!subqueueROITasks.isEmpty()) {
            VehicleLoanSubqueueTask latestROITask = subqueueWaiverTasks.get(0);
            model.addAttribute("latestROITask", latestROITask);
            if ("COMPLETED".equals(latestROITask.getStatus())) {
                VehicleLoanRoiWaiver latestROIWaiver = taskService.getLatestCompletedRoiWaiver(latestROITask.getTaskId());
                model.addAttribute("latestROIWaiver", latestROIWaiver);
            }
        }
        // model.addAttribute("completedChgWaiver", taskService.getLatestCompletedChargeWaiver(Long.valueOf(slno)));
        model.addAttribute("processFeeMast", fetchRepository.fetchProccessingFee(slno));
        model.addAttribute("feeLevels", fetchRepository.findProceessLevel());
        return model;
    }


    @Override
    public Model fetchWiSm(String slno, HttpServletRequest request, Model model) {
        usd.setTrans_slno(slno);
        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(slno));
        /*
        String lockflg = "N";
        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(slno));
        if (lockuser == null) {
            VehicleLoanLock lk = new VehicleLoanLock();
            lk.setWiNum(vehicleLoanMaster.getWiNum());
            lk.setSlno(Long.valueOf(slno));
            lk.setLockedBy(usd.getPPCNo());
            lk.setLockedOn(new Date());
            lk.setLockFlg("Y");
            lk.setQueue("BD");
            lk.setDelFlg("N");
            vehicleLoanLockService.saveLock(lk);
        } else if (!lockuser.equals(usd.getPPCNo())) {
            lockflg = "Y";
        }

         */

        String lockflg = "N";
        String lockuser = "";
        List<VehicleLoanSubqueueTask> tasks = vehicleLoanSubqueueTaskRepository.findPendingTasksByWiNum(vehicleLoanMaster.getWiNum());
        for (VehicleLoanSubqueueTask task : tasks) {
            if (task.getTaskType().equals("SAN_MOD") && task.getStatus().equals("PENDING") && task.getLockFlg() != null &&
                    task.getLockFlg().equals("Y")) {
                lockuser = task.getLockedBy();
                break;
            }
        }

        //System.out.println("lockuserrrrrrrrrrrrrrrrr:"+lockuser);
        if (lockuser == null || lockuser.trim().isEmpty()) {
            loanWaiverService.lockPendingSubTasks(vehicleLoanMaster.getWiNum(), usd.getPPCNo(), Long.parseLong(slno), "SAN_MOD");
        } else if (!lockuser.equals(usd.getPPCNo())) {
            lockflg = "Y";
        }
        List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanBREService.getAmberColorDataForLoan(vehicleLoanMaster.getWiNum(), Long.valueOf(slno));
        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(vehicleLoanMaster.getWiNum(), String.valueOf(vehicleLoanMaster.getSlno()), "APPLICANT");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success")) {
            model.addAttribute("bpm", new ResponseDTO("S", responseDTO.getChildUrl()));
        } else
            model.addAttribute("bpm", new ResponseDTO("F", responseDTO.getStatus()));

        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
        model.addAttribute("findedup", findedup);
        model.addAttribute("losdedup", los);
        List<bsaBankDetails> bankdetails = bsaBankDetailsRepositoryservice.getBankDetails();
        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (!eligibilityDetailsOpt.isPresent()) {
            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
        } else {
            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
            model.addAttribute("eligibilityDetails", eligibilityDetails);
        }

        VehicleLoanVehicle vehicleDetails = vehicleDetailsService.fetchExistingbyWinumandSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        if (vehicleDetails == null) {
            model.addAttribute("error", "No vehicle details found for the provided identifiers.");
        }
        model.addAttribute("vehicleDetails", vehicleDetails);
        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
        if (loanDetails == null) {
            model.addAttribute("error", "No loan details found for the provided identifiers.");
        }
        Optional<VehicleLoanFcvCpvCfr> fcvCpvCfr = fcvCpvCfrService.findBySlno(vehicleLoanMaster.getSlno());
        fcvCpvCfr.ifPresent(record -> model.addAttribute("fcvCpvCfr", record));
        model.addAttribute("loanDetails", loanDetails);
        model.addAttribute("bankDetails", bankdetails);
        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
        model.addAttribute("slno", vehicleLoanMaster.getSlno());
        model.addAttribute("lockflg", lockflg);
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("userdata", usd.getEmployee());
        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
        model.addAttribute("employmentTypes", misrctService.getCodeValuesByType("EMP"));
        model.addAttribute("sancLetter", fetchRepository.getDocument(slno, "SANCLTR"));
        model.addAttribute("mobCodes", fetchRepository.getMobCodeMaster());
        model.addAttribute("vehicleLoanAmberList", vehicleLoanAmberList);
        model.addAttribute("repaymentDetails", fetchRepository.getRepaymentAcctDetails(Long.valueOf(slno)));
        model.addAttribute("userRole", vehicleLoanMaster.getQueue());//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("decisionParam", misrctService.getCodeValuesByTypeOrdered(vehicleLoanMaster.getQueue()));//fetchRepository.getUserRole(usd.getPPCNo())
        model.addAttribute("bankName", fetchRepository.getBankCode());
        model.addAttribute("roiLevels", fetchRepository.findRoiLevel());
        model.addAttribute("subQueueData", taskService.getBySlno(Long.valueOf(slno)));
        model.addAttribute("processFeeFinal", fetchRepository.fetchProccessingFeeAcOpen(slno));
        model.addAttribute("sanctionDetailsFinal", fetchRepository.fetchSanctionDetailsFinal(Long.valueOf(slno)));
        model.addAttribute("lockuser", lockuser);
        model.addAttribute("sol_desc", fetchRepository.getSolName(vehicleLoanMaster.getSolId()));

        VehicleLoanSubqueueTask vehicleLoanSubqueueTask = taskService.getSubTaskByTypeAndStatus(Long.valueOf(slno), "SAN_MOD", "PENDING");
        if (vehicleLoanSubqueueTask != null) {
            VehicleLoanSanMod vehicleLoanSanMod = vehicleLoanSanModService.findByTaskId(vehicleLoanSubqueueTask.getTaskId());
            model.addAttribute("vehicleLoanSanMod", vehicleLoanSanMod);
            if (vehicleLoanSanMod.getCmUser().equals(usd.getPPCNo())) {
                model.addAttribute("makerCheckerSame", "Y");
            }
        }
        return model;
    }


    //    @Override
//    public Model fetchCRTcheckerDetails(String trans_slno, HttpServletRequest request, Model model) {
//        usd.setTrans_slno(trans_slno);
//        VehicleLoanMaster vehicleLoanMaster = vlservice.findBySlno(Long.valueOf(trans_slno));
//        String lockflg = "N";
//        String lockuser = vehicleLoanLockService.Locked(Long.valueOf(trans_slno));
//        if (lockuser == null) {
//            VehicleLoanLock lk = new VehicleLoanLock();
//            lk.setWiNum(vehicleLoanMaster.getWiNum());
//            lk.setSlno(Long.valueOf(trans_slno));
//            lk.setLockedBy(usd.getPPCNo());
//            lk.setLockedOn(new Date());
//            lk.setLockFlg("Y");
//            lk.setQueue("BM");
//            lk.setDelFlg("N");
//            vehicleLoanLockService.saveLock(lk);
//        } else if (!lockuser.equals(usd.getPPCNo())) {
//            lockflg = "Y";
//        }
//        List<LosDedupeEntity> los = losDedupeService.getLosByID(vehicleLoanMaster.getSlno());
//
//        //  VLEmployment employment=vlEmploymentService.findBySlno(vehicleLoanMaster.getSlno());
//        //  VLCredit credit=vlcreditService.findBySlno(vehicleLoanMaster.getSlno());
//        List<FinDedupEntity> findedup = finacleLosDedupeService.getLosByID(vehicleLoanMaster.getSlno());
//        VehicleLoanVehicle vehicleDetails = vehicleDetailsService.fetchExistingbyWinumandSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
//        if (vehicleDetails == null) {
//            model.addAttribute("error", "No vehicle details found for the provided identifiers.");
//        }
//        model.addAttribute("vehicleDetails", vehicleDetails);
//        List<VehicleLoanDetails> loanDetailsList = vehicleLoanDetailsService.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
//        VehicleLoanDetails loanDetails = loanDetailsList.isEmpty() ? null : loanDetailsList.get(0);
//        if (loanDetails == null) {
//            model.addAttribute("error", "No loan details found for the provided identifiers.");
//        }
//        model.addAttribute("loanDetails", loanDetails);
//        Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(vehicleLoanMaster.getWiNum(), vehicleLoanMaster.getSlno());
//        if (!eligibilityDetailsOpt.isPresent()) {
//            model.addAttribute("error", "No eligibility details found for the provided identifiers.");
//        } else {
//            EligibilityDetails eligibilityDetails = eligibilityDetailsOpt.get();
//            model.addAttribute("eligibilityDetails", eligibilityDetails);
//
//        }
//        model.addAttribute("findedup", findedup);
//        model.addAttribute("losdedup", los);
//        // model.addAttribute("employment", employment);
//        model.addAttribute("vehicleLoanMaster", vehicleLoanMaster);
//        model.addAttribute("winum", vehicleLoanMaster.getWiNum());
//        model.addAttribute("slno", vehicleLoanMaster.getSlno());
//        //  model.addAttribute("credit", credit);
//        model.addAttribute("lockflg", lockflg);
//        model.addAttribute("userdata", usd.getEmployee());
//        model.addAttribute("residenceTypes", misrctService.getCodeValuesByType("RES"));
//        model.addAttribute("titles", misrctService.getCodeValuesByType("TIT"));
//        return model;
//    }
    public BpmRequest bpmRequest(String winum, String pdf, String child, String childName, String docName) {
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
    private String formatDate(Object dateObj) {
    if (dateObj == null) return null;

    try {
        if (dateObj instanceof java.sql.Timestamp) {
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            return outputFormat.format(new Date(((java.sql.Timestamp) dateObj).getTime()));
        } else if (dateObj instanceof String) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = inputFormat.parse((String) dateObj);
            return outputFormat.format(date);
        } else {
            log.warn("Unexpected date format: {}", dateObj.getClass());
            return dateObj.toString();
        }
    } catch (Exception e) {
        log.error("Error formatting date: {}", dateObj, e);
        return dateObj.toString();
    }
}


}
