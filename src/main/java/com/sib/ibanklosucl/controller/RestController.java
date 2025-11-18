package com.sib.ibanklosucl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sib.ibanklosucl.config.menuaccess.RequiresMenuAccess;
import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.acopn.LienMarkingResult;
import com.sib.ibanklosucl.dto.bpm.BPMCreateVLResponse;
import com.sib.ibanklosucl.dto.dedup.*;
import com.sib.ibanklosucl.dto.mssf.MssfDocProcessResponse;
import com.sib.ibanklosucl.dto.mssf.StoredDocument;
import com.sib.ibanklosucl.dto.ocr.OcrParsed;
import com.sib.ibanklosucl.dto.program.FDAccountRequest;
import com.sib.ibanklosucl.exception.MssfApiException;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.VehicleLoanMasterRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.eligibility.EligibilityDetailsService;
import com.sib.ibanklosucl.service.eligibility.SanctionAmtCal;
import com.sib.ibanklosucl.service.esbsr.CIFViewService;
import com.sib.ibanklosucl.service.esbsr.DedupService;
import com.sib.ibanklosucl.service.esbsr.PanNsdlService;
import com.sib.ibanklosucl.service.esbsr.UIDService;
import com.sib.ibanklosucl.service.impl.BOGSaveImpl;
import com.sib.ibanklosucl.service.impl.GeneralDtSaveImp;
import com.sib.ibanklosucl.service.integration.LienMarkingService;
import com.sib.ibanklosucl.service.integration.NACHMandateService;
import com.sib.ibanklosucl.service.mssf.MssfDocumentService;
import com.sib.ibanklosucl.service.integration.VLDedupeService;
import com.sib.ibanklosucl.service.ocrsv.OCRService;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@OpenAPIDefinition(
        info = @Info(
                title = "Ibanklos Module REST API",
                version = "1.0",
                description = "API details for the  Ibanklos application",
                contact = @Contact(
                        name = "Information bank",
                        email = "swd@sib.bank.in"
                )
        )
)
@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "Ibanklos application APIs", description = "APIs for Ibanklos")
public class RestController {

    @Autowired
    private BOGSaveImpl bogSaveImpl;

    @Autowired
    private UserSessionData usd;
    @Autowired
    private OCRService ocrService;

    @Value("${app.dev-mode:true}")
    private boolean devMode;
    @Autowired
    private PanNsdlService panNsdlService;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private UIDService uis;

    @Autowired
    private VehicleLoanMasterRepository loanMasterRepository;

    @Autowired
    private BpmService bpmService;
    private static final Gson gson = new Gson();
    @Autowired
    private VLSaveServiceFactory vlSaveServiceFactory;
    @Autowired
    private CIFViewService cifViewService;

    @Autowired
    private VehicleDetailsService vehicleDetailsService;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;

    @Autowired
    private PriceRevisionService PriceRevisionservice;
    @Autowired
    private PriceRevisionService priceRevisionService;

    @Autowired
    private VehicleLoanSubqueueTaskService taskService;
    @Autowired
    private VehicleLoanProgramService VehicleLoanProgramservice;
    @Autowired
    private VehicleLoanProgramIntegrationService loanProgramIntegrationService;

    @Autowired
    private VehicleLoanDetailsService loanDetailsService;
    @Autowired
    private EligibilityDetailsService eligibilityDetailsService;
    @Autowired
    private MisrctService misrctService;
    @Autowired
    private DedupService dedupService;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private FDAccountService fdAccountService;
    @Autowired
    private PincodeService pincodeService;
    @Autowired
    private ProgramBasedEligibilityService programBasedEligibilityService;
    @Autowired
    private CommonServiceFactory commonServiceFactory;
    @Autowired
    private VLEmploymentService vlemploymentservice;
    @Autowired
    private VLCreditService vlCreditService;
    @Autowired
    private VLEmploymentempService vlemploymentempservice;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private VehicleLoanAllotmentService vlloanallotmentservice;

    @Autowired
    private VehicleLoanQueueDetailsService queueDetailsService;

    @Autowired
    private VehicleLoanAllotmentService vlallotmentservice;
    @Autowired
    private VehicleLoanKycService vehicleLoanKycService;
    @Autowired
    private LienMarkingService lienMarkingService;


    @Autowired
    private iBankService iBankService;

    @Autowired
    private CommonUtils cm;

    @Autowired
    private SanctionAmtCal sanctionAmtCal;

    @Autowired
    private PincodeMasterService pincodeMasterService;

    @Autowired
    private VehicleLoanFcvCpvCfrService vlfcvservice;

    @Autowired
    private VehicleLoanAmberService vlamberservice;
    @Autowired
    private VehicleLoanFcvCpvCfrService fcvCpvCfrService;
    @Autowired
    private OctDetailsService octdetailsservice;
    @Autowired
    private GeneralDtSaveImp generalDtSaveService;


    @Autowired
    private VehicleLoanWaiverService loanWaiverService;

    @Autowired
    private VehicleLoanTatService vlTatService;
    @Autowired
    private VLFileUploadService vlFileUploadService;
    @Autowired
    private NACHMandateService nachMandateService;
    @Autowired
    private HelpdeskAuditService auditService;

    @Autowired
    private MssfDocumentService mssfDocumentService;
    @Autowired
    private VLDedupeService vlDedupeService;

    @GetMapping("/states")
    public List<Map<String, String>> getStates() {
        return vehicleDetailsService.getAllStates();
    }

    @GetMapping("/dealers")
    public List<Map<String, String>> getDealers() {
        return vehicleDetailsService.getAllDealers();
    }

    @GetMapping("/dealerNames/{dealerName}/{dealerCode}")
    public DealerNameResponse getDealerNames(@PathVariable String dealerName, @PathVariable String dealerCode) {
        return vehicleDetailsService.getDealerNames(dealerName, dealerCode);
    }

    @GetMapping("/dealerCodes/{dealerName}")
    public DealerCodeResponse getDealerCodes(@PathVariable String dealerName) {
        return vehicleDetailsService.getDealerCodes(dealerName);
    }

//    @GetMapping("/dealersWithCode")
//    public List<Map<String, String>> getDealersCode() {
//        return vehicleDetailsService.getAllDealersCode();
//    }

    @GetMapping("/dealerSubCodes/{dealerSubcode}/{dealerCode}/{dealerName}")
    public List<Map<String, String>> getLocations(@PathVariable String dealerSubcode,
                                                  @PathVariable String dealerCode,
                                                  @PathVariable String dealerName) {
        return vehicleDetailsService.getLocationsByDealerInfo(dealerSubcode, dealerCode, dealerName);
    }

    @GetMapping("/makesByDealer/{dealerSubcode}/{dealerCode}/{dealerName}/{cityId}")
    public List<Map<String, String>> getMakesByDealer(@PathVariable String dealerSubcode,
                                                      @PathVariable String dealerCode,
                                                      @PathVariable String dealerName,
                                                      @PathVariable String cityId) {
        return vehicleDetailsService.getMakesByDealerAndLocation(dealerSubcode, dealerCode, dealerName, cityId);
    }


    @GetMapping("/get-city/{code}")
    public List<Misrct> getCityAjax(@PathVariable String code) {
        return misrctService.getCodeLikeType("01", code);
    }

    @GetMapping("/get-state/{code}")
    public List<Misrct> getStateAjax(@PathVariable String code) {
        return misrctService.getCodeLikeType("02", code);
    }

    @GetMapping("/get-country/{code}")
    public List<Misrct> getCountryAjax(@PathVariable String code) {
        return misrctService.getCodeLikeType("03", code);
    }

    @GetMapping("/ppc-fetch/{ppcno}")
    public List<Map<String, String>> getPPC(@PathVariable String ppcno) {
        if (ppcno.matches("\\d+"))
            return fetchRepository.getPPCDetails(ppcno);
        else
            return new ArrayList<>();
    }

    @GetMapping("/ppc-rsmfetch/{ppcno}")
    public List<Map<String, String>> getRsmPPC(@PathVariable String ppcno) {
        if (ppcno.matches("\\d+"))
            return fetchRepository.getRsmPPC(ppcno);
        else
            return new ArrayList<>();
    }

    @GetMapping("/sol-rsmfetch/{sol_id}")
    public List<Map<String, String>> getRsmSol(@PathVariable String sol_id) {
        if (sol_id.matches("\\d+"))
            return fetchRepository.getRsmSol(sol_id, usd.getPPCNo());
        else
            return new ArrayList<>();
    }

    @GetMapping("/sol-lhfetch/{sol_id}")
    public List<Map<String, String>> getLHSol(@PathVariable String sol_id) {
        if (sol_id.matches("\\d+"))
            return fetchRepository.getLHSol(sol_id, usd.getPPCNo());
        else
            return new ArrayList<>();
    }

    @GetMapping("/sol-rahfetch/{sol_id}")
    public List<Map<String, String>> getRAHSol(@PathVariable String sol_id) {
        if (sol_id.matches("\\d+"))
            return fetchRepository.getRAHSol(sol_id, usd.getPPCNo());
        else
            return new ArrayList<>();
    }

    @GetMapping("/sol-mssffetch/{term}")
    public List<Map<String, String>> getMSSFSol(@PathVariable String term) {
        if (term.matches("\\d+")) {
            // Search by `sol_id`
            return fetchRepository.getMSSFSolById(term);
        } else {
            // Search by `br_name`
            return fetchRepository.getMSSFSolByName(term);
        }
    }


    @GetMapping("/cities/{stateCode}")
    public List<Map<String, String>> getCities(@PathVariable String stateCode) {
        return vehicleDetailsService.getCitiesByState(stateCode);
    }

    @GetMapping("/pincode/{pincode}")
    public PincodeMaster getDataFromPincode(@PathVariable String pincode) {
        Optional<PincodeMaster> pin = pincodeMasterService.findById(pincode);
        return pin.orElseGet(PincodeMaster::new);
    }

    @PostMapping("/los-dedupe")
    public ResponseEntity<ResponseDTO> getLosDedupeData(@RequestBody DedupRequestDTO tb) {
        return ResponseEntity.ok(dedupService.callLosDedupeApi(tb));
    }

    @PostMapping("/fin-dedupe")
    public ResponseEntity<ResponseDTO> getFinacleDedupeData(@RequestBody DedupRequestDTO tb) {
        return ResponseEntity.ok(dedupService.callFinacleDedupeApi(tb));
    }


    @GetMapping("/makes")
    public List<Map<String, String>> getMakes() {
        return vehicleDetailsService.getAllMakes();
    }

    @GetMapping("/models/{makeId}/{dealerCode}/{dealerSubCode}/{dealerCityId}")
    public Map<String, List<Map<String, String>>> getModels(@PathVariable String makeId, @PathVariable String dealerCode, @PathVariable String dealerSubCode, @PathVariable String dealerCityId) {
        return vehicleDetailsService.getModelsByMake(makeId, dealerCode, dealerSubCode, dealerCityId);
    }

    @GetMapping("/variants/{modelId}")
    public List<Map<String, String>> getVariants(@PathVariable String modelId) {
        return vehicleDetailsService.getVariantsByModel(modelId);
    }

    @GetMapping("/prices/{variantId}/{cityId}")
    public Map<String, String> getPrices(@PathVariable String variantId, @PathVariable String cityId) {
        return vehicleDetailsService.getPricesByVariantAndCity(variantId, cityId);
    }

    @PostMapping("/updateChannel")
    public ResponseEntity<String> updateChannel(@RequestParam String channel, @RequestParam String winum) {
        VehicleLoanMaster vlmas = vehicleLoanMasterService.SearchByWiNum(winum);
        if (vlmas != null) {
            vlmas.setChannel(channel);
            loanMasterRepository.save(vlmas);
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.ok("failed");
        }


    }

    @PostMapping("/vehicleDetails")
    public ResponseEntity<VehicleLoanVehicle> saveVehicleLoanVehicle(@RequestBody VehicleLoanVehicle vehicleLoanVehicle, HttpServletRequest request) throws Exception {
        VehicleLoanVehicle newVehicle = vehicleDetailsService.fetchExistingbySlno(vehicleLoanVehicle.getSlno());
        if (newVehicle != null) {
            vehicleLoanVehicle.setIno(newVehicle.getIno());
        }
        vehicleLoanVehicle.setReqIpAddr(CommonUtils.getClientIp(request));
        vehicleLoanVehicle.setCmdate(new Date());
        vehicleLoanVehicle.setInvoiceDate(vehicleLoanVehicle.getInvoiceDate());
        vehicleLoanVehicle.setCmuser(usd.getPPCNo());
        vehicleLoanVehicle.setHomeSol(usd.getSolid());
        String dealerAcctdetails[] = vehicleLoanVehicle.getDealerAccount().split("\\|");
        vehicleLoanVehicle.setDealerAccount(dealerAcctdetails[0]);
        vehicleLoanVehicle.setDealerIfsc(dealerAcctdetails[1]);
        vehicleLoanVehicle.setDealerBank(dealerAcctdetails[2]);
        VehicleLoanVehicle savedVehicleLoanVehicle = vehicleDetailsService.checkAndInsertOrUpdate(vehicleLoanVehicle);
        VehicleLoanApplicant vehicleLoanApplicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(vehicleLoanVehicle.getApplicantId());
        vehicleLoanApplicant.setVehicleComplete("Y");
        vehicleLoanApplicant.setLoanComplete("N");
        vehicleLoanApplicantService.saveApplicant(vehicleLoanApplicant);
        Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsRepository.findBySlnoAndDelFlg(vehicleLoanVehicle.getSlno(), "N");
        EligibilityDetails eligibilityDetails = null;
        if (eligibilityDetails_.isPresent()) {
            eligibilityDetails = eligibilityDetails_.get();
            eligibilityDetails.setEligibilityFlg("N");
            eligibilityDetails.setProceedFlag("N");
            eligibilityDetailsRepository.save(eligibilityDetails);
        }
        if (savedVehicleLoanVehicle.getIno() != null) {
            FileDetails fileDetails = new FileDetails(
                    "VEHICLE_INVOICE",
                    "Vehicle_Invoice",
                    vehicleLoanVehicle.getInvoiceDoc(),
                    vehicleLoanVehicle.getInvoiceExt(),
                    "Vehicle_Invoice",
                    vehicleLoanVehicle.getSlno().toString()
            );

            List<FileDetails> files = Collections.singletonList(fileDetails);
            List<VLFileUpload> vlFileUploads = vlFileUploadService.findFileBySlno(vehicleLoanVehicle.getSlno());// May remove this later
            TabResponse response = vehicleDetailsService.processAndUploadFiles(vehicleLoanVehicle.getWiNum(), vehicleLoanVehicle.getSlno(), request.getRemoteAddr(), files, "N", vlFileUploads);
            if (!"S".equals(response.getStatus())) {
                throw new Exception("File upload failed: ");
            }
        }


        return ResponseEntity.ok(savedVehicleLoanVehicle);
    }


    @GetMapping("/fetchExisting")
    public ResponseEntity<VehicleLoanVehicle> fetchExisting(@RequestParam String wiNum, @RequestParam Long slno, @RequestParam Long applicantId) {
        VehicleLoanVehicle existingEntry = vehicleDetailsService.fetchExisting(wiNum, slno, applicantId);
        if (existingEntry == null) {
            existingEntry = new VehicleLoanVehicle();
            existingEntry.setWiNum("NOTFOUND");
        }
        return ResponseEntity.ok(existingEntry);
    }


    @GetMapping("/fetchLoanDetails")
    public ResponseEntity<VehicleLoanDetails> fetchLoanDetails(@RequestParam String wiNum, @RequestParam String slno, @RequestParam String applicantId) {
        VehicleLoanDetails loanDetails = loanDetailsService.findBySlnoAndDelFlg(Long.parseLong(slno));
        if (loanDetails == null) {
            loanDetails = new VehicleLoanDetails();
        }
        VehicleLoanVehicle vehicleLoanDetails = vehicleDetailsService.fetchExistingbyWinumandSlno(wiNum, Long.parseLong(slno));

        if (vehicleLoanDetails != null) {
            loanDetails.setVehicleAmt(vehicleLoanDetails.getTotalInvoicePrice() == null ? new BigDecimal(0) : new BigDecimal(vehicleLoanDetails.getTotalInvoicePrice()));
        }
        return ResponseEntity.ok(loanDetails);
    }


    @PostMapping("/saveLoanDetails")
    public ResponseEntity<?> saveLoanDetails(@RequestBody VehicleLoanDetails loanDetails) {
        VlCommonTabService vl = vlSaveServiceFactory.getTabService("LOAN");
        return vl.saveLoan(loanDetails);

    }

    @PostMapping("/getCustName")
    public ResponseEntity<?> saveLoanDetails(@RequestParam @Validated String custId) {
        String custName = fetchRepository.getCustName(custId);
        return ResponseEntity.ok(new ResponseDTO(custName != null ? "S" : "F", custName));
    }


    @PostMapping("/updatePriceRevision")

    public ResponseEntity<PriceRevision> updatePriceRevision(@RequestBody PriceRevision priceRevision) {
        priceRevision.setCmdate(new Date());
        priceRevision.setCmuser(usd.getPPCNo());
        PriceRevision savedPriceRevision = PriceRevisionservice.save(priceRevision);
        return ResponseEntity.ok(savedPriceRevision);
    }

    @GetMapping("/fetchPriceRevision")
    public ResponseEntity<PriceRevision> fetchPriceRevision(@RequestParam String wiNum, @RequestParam Long slno) {
        Optional<PriceRevision> priceRevision = priceRevisionService.findByWiNumAndSlno(wiNum, slno);
        return priceRevision.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/getLoanEligibilityDetails/{wiNum}/{slNo}")
    public ResponseEntity<?> getLoanDetails(@PathVariable String wiNum, @PathVariable Long slNo) {
        return eligibilityDetailsService.getLoanEligibilityDetails(slNo);
    }

    @PostMapping("/ins-opt-out")
    public ResponseEntity<?> insOptOut(@RequestBody TabRequestDTO Doc) {
        try {

            VehicleLoanMaster loanMaster = vehicleLoanMasterService.findById(Long.valueOf(Doc.getSlno()));
                  if(loanMaster.getRoiRequested()!=null && loanMaster.getRoiRequested() && taskService.isPending(loanMaster.getSlno(),"ROI_WAIVER")){
                throw new ValidationException(ValidationError.COM001,"Request Pending For ROI Waiver !!");
            }
            else if(loanMaster.getChargeWaiverRequested()!=null && loanMaster.getChargeWaiverRequested() && taskService.isPending(loanMaster.getSlno(),"CHARGE_WAIVER")){
                throw new ValidationException(ValidationError.COM001,"Request Pending For Fee/Charge Waiver !!");
            }
            if(loanMaster.getDocMode()==null && loanMaster.getQueue().equals("BD") ) {
                EligibilityDetails elig=  eligibilityDetailsService.checkProgramEligibilityWithoutInsurance(loanMaster.getWiNum(),loanMaster.getSlno(),true);
                 loanWaiverService.calculateProcessingFee(String.valueOf(loanMaster.getSlno()),elig.getEligibleLoanAmt(),elig.getVehicleAmt(),true,loanMaster.getWiNum());
                return ResponseEntity.ok(new TabResponse("S","Updated Successfully"));
            }
            throw new ValidationException(ValidationError.COM001,"Cannot Modify the WI Since Documentation has been Initiated");
        } catch (Exception e) {
            return new ResponseEntity<>(new TabResponse("F", e.getMessage()), HttpStatus.OK);
        }

    }
    @PostMapping("/checkProgramEligibility")
    public ResponseEntity<?> checkProgramEligibility(@RequestBody CheckEligibilityRequest request) {
        try {
            VehicleLoanMaster loanMaster = vehicleLoanMasterService.findById(request.getSlno());
            String currentQueue = loanMaster.getQueue();
            ProgramEligibilityResponse response = eligibilityDetailsService.checkProgramEligibility(request, currentQueue);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(new TabResponse("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/saveEligibilityRecommendation")
    public ResponseEntity<?> saveEligibilityRecommendation(@Valid @RequestBody EligibilityRecommendationRequest request) {
        try {
            eligibilityDetailsService.saveEligibilityRecommendation(request);
            return ResponseEntity.ok(new TabResponse("SUCCESS", "Recommendation saved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new TabResponse("ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new TabResponse("ERROR", "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/checkEligibilityFlag")
    public ResponseEntity<?> checkEligibilityFlag(@RequestBody Map<String, Long> request) {
        Long slno = request.get("slno");
        String eligibilityFlag = eligibilityDetailsService.getEligibilityFlag(slno);
        return ResponseEntity.ok(Map.of("eligibilityFlag", eligibilityFlag));
    }


    @GetMapping("/getEligibilityDetails/{wiNum}/{slNo}")
    public ResponseEntity<?> getEligibilityDetails(@PathVariable String wiNum, @PathVariable Long slNo) {
        try {
            EligibilityDetails details = eligibilityDetailsService.getEligibilityDetailsCPC(wiNum, slNo);
            if (details != null) {
                return ResponseEntity.ok(details);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new TabResponse("ERROR", e.getMessage()));
        }
    }


    @PostMapping("/saveEligibilityDetails")
    public ResponseEntity<?> saveEligibilityDetails(@RequestBody EligibilityDetails eligibilityDetails, HttpServletRequest request) {
        try {
            if (programBasedEligibilityService.checkEligibilityValidations(eligibilityDetails.getWiNum(), eligibilityDetails.getSlno())) {
                Optional<EligibilityDetails> eligibilityDetailsdata = eligibilityDetailsService.findBySlno(eligibilityDetails.getSlno());
                EligibilityDetails eligibilityDetails1 = null;
                if (eligibilityDetailsdata.isPresent()) {
                    eligibilityDetails1 = eligibilityDetailsdata.get();
                    if ("Y".equalsIgnoreCase(eligibilityDetails1.getEligibilityFlg())) {
                        eligibilityDetails1.setEligibilityDate(new Date());
                        eligibilityDetails1.setCmuser(usd.getPPCNo());
                        eligibilityDetails1.setReqIpAddr(request.getRemoteAddr());
                        eligibilityDetails1.setHomeSol(usd.getSolid());
                        eligibilityDetails1.setProceedFlag("Y");
                        EligibilityDetails saved = eligibilityDetailsService.save(eligibilityDetails1);
                        return new ResponseEntity<>(saved, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(new ErrorResponse("F", "Kindly Click Check Eligibility Button to Proceed"), HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return new ResponseEntity<>(new ErrorResponse("F", "Kindly Check Eligibility"), HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(new ErrorResponse("F", "Loan Not Eligible"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(null, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/applicant-details")
    public ResponseEntity<?> getVehicleLoanKycDetails(@RequestParam Long applicantId,
                                                      @RequestParam String wiNum,
                                                      @RequestParam Long slno,
                                                      Model model) {
        Map<String, Object> details = vehicleLoanApplicantService.getApplicantDetails(slno, wiNum, applicantId);
        if (details != null) {
            Map<String, String> response = new HashMap<>();
            response.put("panNo", (String) details.get("panNo"));
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            String formattedDate = details.get("panDob") != null ? formatter.format((Date) details.get("panDob")) : null;
            response.put("panDob", formattedDate);
            response.put("mobileCntryCode", (String) details.get("mobileCntryCode"));
            response.put("mobileNo", (String) details.get("mobileNo"));
            response.put("applicantType", (String) details.get("applicantType"));
            response.put("residentFlg", (String) details.get("residentFlg"));
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }

    }


//    @GetMapping("/getProgramBasedEligibility")
//    public ResponseEntity<Map<String, String>> getProgramBasedEligibility(
//            @RequestParam String wiNum, @RequestParam Long slno, @RequestParam int tenor, @RequestParam String cardRate, @RequestParam String loanAmt, @RequestParam String ltvAmt) {
//        String validation = programBasedEligibilityService.checkEligibilityValidations(wiNum, slno);
//        Map<String, String> response = new HashMap<>();
//        if (!validation.equals("OK")) {
//            response.put("eligibility", "ERROR");
//            response.put("ErrorMessage", validation);
//            return ResponseEntity.ok(response);
//        }
//        String eligibility = programBasedEligibilityService.getProgramBasedEligibility(wiNum, slno, tenor, cardRate);
//
//        if (eligibility.equals("0.00")) {
//            response.put("eligibility", "ERROR");
//            response.put("ErrorMessage", "Applicant is not eligible");
//            return ResponseEntity.ok(response);
//        }
//        if (eligibility.contains("Error")) {
//            response.put("eligibility", "ERROR");
//            response.put("ErrorMessage", eligibility);
//            return ResponseEntity.ok(response);
//        }
//        BigDecimal principal = BigDecimal.valueOf(Math.min(Math.min(Double.parseDouble(loanAmt), Double.parseDouble(ltvAmt)), Double.parseDouble(eligibility)));
//        String emi = programBasedEligibilityService.getEligibleEmi(new BigDecimal(cardRate), principal, tenor);
//
//        response.put("eligibility", eligibility);
//        response.put("emi", emi);
//
//        return ResponseEntity.ok(response);
//    }


//    @GetMapping("/GetSpread/{interestType}/{channelId}")
//    public ResponseEntity<String> getSpread(
//            @PathVariable String interestType,
//            @PathVariable String channelId,
//            @RequestParam String score,
//            @RequestParam String tenure) {
//        return iBankService.getSpread(interestType, channelId, score, tenure);
//    }


//    @PostMapping("/updateEligibilityDetails")
//    public ResponseEntity<?> updateEligibilityDetails(@RequestBody EligibilityDetails eligibilityDetails) {
//        Optional<EligibilityDetails> existingDetails = eligibilityDetailsService.findById(eligibilityDetails.getIno());
//        if (existingDetails.isPresent()) {
//            String validation = programBasedEligibilityService.checkEligibilityValidations(existingDetails.get().getWiNum(),existingDetails.get().getSlno());
//            validation="OK";
//            if(!validation.equals("OK"))
//            {
//                Map<String, String> response = new HashMap<>();
//                response.put("eligibility", "ERROR");
//                response.put("ErrorMessage", validation);
//                return ResponseEntity.ok(response);
//            }
//            EligibilityDetails updatedDetails = existingDetails.get();
//            updatedDetails.setProgramEligibleAmt(eligibilityDetails.getProgramEligibleAmt());
//            updatedDetails.setEligibleLoanAmt(eligibilityDetails.getEligibleLoanAmt());
//            updatedDetails.setCardRate(eligibilityDetails.getCardRate());
//            updatedDetails.setHomeSol(usd.getSolid());
//            eligibilityDetailsService.save(updatedDetails);
//            return ResponseEntity.ok(updatedDetails);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }


    @GetMapping("/checkLoanAmount")
    public boolean checkLoanAmount(@RequestParam String programName, @RequestParam String employmentName, @RequestParam Float loanAmount) {
        return loanDetailsService.checkLoanAmount(programName, employmentName, loanAmount);
    }


    @GetMapping("/checkLoanTenor")
    public boolean checkLoanTenor(@RequestParam String programName, @RequestParam String employmentName, @RequestParam int tenor) {
        return loanDetailsService.checkLoanTenor(programName, employmentName, tenor);
    }

    @GetMapping("/isCreditComplete")
    public boolean isCreditComplete(@RequestParam String wiNum, @RequestParam String slno) {
        return !eligibilityDetailsService.isCreditComplete(wiNum, slno);
    }

    @PostMapping("/insertVehicleLoanProgram")
    public ResponseEntity<VehicleLoanProgram> insertVehicleLoanProgram(@RequestBody VehicleLoanProgram vehicleLoanProgram) {
        VehicleLoanProgram createdProgram = VehicleLoanProgramservice.insertVehicleLoanProgram(vehicleLoanProgram);
        return ResponseEntity.ok(createdProgram);
    }

    @PutMapping("/updateVehicleLoanProgram/{ApplicantID}")
    public ResponseEntity<VehicleLoanProgram> updateVehicleLoanProgram(
            @PathVariable String ApplicantID, @RequestBody VehicleLoanProgram vehicleLoanProgram) {
        VehicleLoanProgram updatedProgram = VehicleLoanProgramservice.updateVehicleLoanProgram(ApplicantID, vehicleLoanProgram);
        return ResponseEntity.ok(updatedProgram);
    }

    @GetMapping("/getVehicleLoanProgram/{wiNum}/{slNo}")
    public ResponseEntity<?> getVehicleLoanProgram(@PathVariable String wiNum, @PathVariable Long slNo) {
        List<VehicleLoanProgram> vps = VehicleLoanProgramservice.getVehicleLoanProgram(wiNum, slNo);
        vps = vps.stream().filter(program -> !"NONE".equals(program.getLoanProgram())).collect(Collectors.toList());
        if (vps.size() > 0)
            return ResponseEntity.ok(vps.get(0).getLoanProgram());
        else
            return ResponseEntity.ok(null);
    }

    @GetMapping("/fetchCredit")
    public ResponseEntity<VLCredit> fetchScore(@RequestParam String wiNum, @RequestParam Long slNo) {
        VLCredit vlCredit = vlCreditService.getVLCreditDetails(wiNum, slNo);
        if (vlCredit != null) {
            return ResponseEntity.ok(vlCredit);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


//    @GetMapping("/fetchAnnualIncome")
//    public ResponseEntity<AnnualIncomeAndBankBalance> fetchAnnualIncome(@RequestParam String wiNum, @RequestParam Long slNo) {
//        AnnualIncomeAndBankBalance result = VehicleLoanProgramservice.getAnnualIncomeAndBankBalance(wiNum, slNo);
//        if (result != null) {
//            return ResponseEntity.ok(result);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }


    @GetMapping("/isProgramSet")
    public boolean isProgramSet(@RequestParam String wiNum, @RequestParam String slno) {
        return loanDetailsService.isProgramSet(wiNum, slno);
    }

    @GetMapping("/getViewData")
    public String getViewData(
            @RequestParam String programName,
            @RequestParam String employmentName,
            @RequestParam Float loanAmount) {
        return loanDetailsService.getViewData(programName, employmentName, loanAmount);
    }

    @GetMapping("/getViewTenor")
    public String getViewTenor(
            @RequestParam String programName,
            @RequestParam String employmentName,
            @RequestParam int tenor) {
        if (loanDetailsService.checkLoanTenor(programName, employmentName, tenor))
            return loanDetailsService.getViewDataTenor(programName, employmentName, tenor);
        else {
            return "OK";
        }
    }


    @PostMapping("/losocr")
    public ResponseEntity<OcrParsed> OcrController(@RequestBody TabRequestDTO fr) {
        fr.getFileRequest().getDocumentMeta().setCmUser(usd.getPPCNo());
        OcrParsed ocrResponse = ocrService.processFile(fr);
        return ResponseEntity.ok(ocrResponse);
    }

    @PostMapping("/pan-validate")
    public ResponseEntity<ResponseDTO> PanController(@RequestBody TabRequestDTO tb) {

        // return   ResponseEntity.ok(new ResponseDTO("S",""));
        return ResponseEntity.ok(panNsdlService.PanValidator(tb, usd.getTrace_id()));
    }

    @PostMapping("/uid-demo-validate")
    public ResponseEntity<ResponseDTO> DemographicController(@RequestBody TabRequestDTO Doc) {
        if (cm.isEmpty(Doc.getUidDoc().getUid()))
            return ResponseEntity.ok(new ResponseDTO("F", "Enter Aadhaar Number"));
        if (cm.isEmpty(Doc.getUidDoc().getName()))
            return ResponseEntity.ok(new ResponseDTO("F", "Enter Name as on Aadhaar Number"));
        ResponseDTO rdto = null;
        if (devMode) {
            rdto = new ResponseDTO();
            rdto.setStatus("S");
        } else {
            rdto = uis.UidDemoValidator(Doc, usd.getTrace_id());
        }
        return ResponseEntity.ok(rdto);
    }

    @PostMapping("/uid-otp-sent")
    public ResponseEntity<ResponseDTO> UIDOtpSender(@RequestBody TabRequestDTO uidDoc) {
        return ResponseEntity.ok(uis.UidOtpSend(uidDoc, usd.getTrace_id()));
    }

    @PostMapping("/uid-otp-validate")
    public ResponseEntity<ResponseDTO> UIDOtpValidate(@RequestBody TabRequestDTO Doc) {
        Doc.getFileRequest().getDocumentMeta().setCmUser(usd.getPPCNo());
        return ResponseEntity.ok(uis.UidOtpValid(Doc, usd.getTrace_id()));
    }

    @PostMapping("/fetch-cbs")
    public ResponseEntity<TabResponse> getCBSData(@Validated @RequestBody CIFviewRequest formData, HttpServletRequest request) {
        return ResponseEntity.ok(cifViewService.getCustData(formData, request));
    }


    @PostMapping("/co-app-bpm")
    public ResponseEntity<TabResponse> CreateCoApp(@Validated @RequestBody FormData formData, HttpServletRequest request) {
        TypeCount tc = CommonUtils.parseString(formData.getReqtype());
        BPMCreateVLResponse responseDTO = null;
        try {
            if (vehicleLoanApplicantService.countCoApplicants(Long.valueOf(formData.getSlno())) >= 3 && tc.getType().contains("C")) {
                return ResponseEntity.ok(new TabResponse("F", "Only max. 3 Co-Applicants can be added for a WI."));
            }
            responseDTO = bpmService.BpmParent(formData.getWinum(), formData.getSlno(), CommonUtils.expandReq(tc));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success"))
            return ResponseEntity.ok(new TabResponse("S", responseDTO.getChildUrl()));
        else
            return ResponseEntity.ok(new TabResponse("F", responseDTO.getStatus()));

    }

    @PostMapping("/getParentBpm")
    public ResponseEntity<TabResponse> getParentBOM(@Validated @RequestBody FormData formData, HttpServletRequest request) {
        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(formData.getWinum(), formData.getSlno(), "NA");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success"))
            return ResponseEntity.ok(new TabResponse("S", responseDTO.getParentUrl()));
        else
            return ResponseEntity.ok(new TabResponse("F", responseDTO.getStatus()));

    }

    @PostMapping("/bpm-parent")
    public ResponseEntity<TabResponse> getBpmParentUrl(@Validated @RequestBody FormData formData, HttpServletRequest request) {
        TypeCount tc = CommonUtils.parseString(formData.getReqtype());
        BPMCreateVLResponse responseDTO = null;
        try {
            responseDTO = bpmService.BpmParent(formData.getWinum(), formData.getSlno(), CommonUtils.expandReq(tc));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (responseDTO.getStatus().equals("Success"))
            return ResponseEntity.ok(new TabResponse("S", responseDTO.getParentUrl()));
        else
            return ResponseEntity.ok(new TabResponse("F", responseDTO.getStatus()));

    }




    @PostMapping("/del-coapp")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<TabResponse> DeleteApplicant(@RequestBody TabRequestDTO Doc) {
        if (Doc.getAppid() != null && !Doc.getAppid().isBlank()) {
            VehicleLoanApplicant vlapp = vehicleLoanApplicantService.getById(Long.valueOf(Doc.getAppid()));
            vlapp.setDelFlg("Y");
            if (vlapp.getVlProgram() != null)
                vlapp.getVlProgram().setDelFlg("Y");
            if (vlapp.getKycapplicants() != null)
                vlapp.getKycapplicants().setDelFlg("Y");
            if (vlapp.getVlEmployment() != null)
                vlapp.getVlEmployment().setDelFlg("Y");
            if (vlapp.getVlcredit() != null)
                vlapp.getVlcredit().setDelFlg("Y");
            if (vlapp.getBasicapplicants() != null)
                vlapp.getBasicapplicants().setDelFlg("Y");
            vehicleLoanApplicantService.saveApplicant(vlapp);
        }
        Optional<EligibilityDetails> eligibilityDetails_ = eligibilityDetailsRepository.findBySlnoAndDelFlg(Long.valueOf(Doc.getSlno()), "N");
        EligibilityDetails eligibilityDetails = null;
        if (eligibilityDetails_.isPresent()) {
            eligibilityDetails = eligibilityDetails_.get();
            eligibilityDetails.setEligibilityFlg("N");
            eligibilityDetails.setProceedFlag("N");
            eligibilityDetailsRepository.save(eligibilityDetails);
        }
        vehicleLoanApplicantService.resetLoanFlg(Long.valueOf(Doc.getSlno()));
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(Long.valueOf(Doc.getSlno()));
        vehicleLoanMaster.setCurrentTab("A-1");
        vehicleLoanMasterService.saveLoan(vehicleLoanMaster);
        return ResponseEntity.ok(new TabResponse("S", ""));
    }


    @PostMapping("/save-data")
    public ResponseEntity<TabResponse> submitFormData(@Validated @RequestBody FormData formData, HttpServletRequest request) {
        try {
            TypeCount tc = CommonUtils.parseString(formData.getReqtype());
            FormBody fb = new FormBody();
            if (formData.getDOC_ARRAY() != null && formData.getDOC_ARRAY().size() > 0) {
                TabResponse rs = bpmService.BpmChildUpload(formData, tc, request, usd.getTrace_id());
                fb.setFileFlag("Y");
                fb.setDOC_ARRAY(formData.getDOC_ARRAY());
                if (!rs.getStatus().equals("S")) {
                    return ResponseEntity.ok(rs);
                }
            }
            FormSave fs = new FormSave();
            fs.setTabType(formData.getId());
            fb.setSlno(formData.getSlno());
            fb.setCurrenttab(formData.getReqtype());
            fb.setReqcount(String.valueOf(tc.getCount()));
            fb.setReqtype(tc.getType());
            fb.setData(formData.getData());
            fb.setAppid(formData.getAppid());
            fb.setWinum(formData.getWinum());
            fb.setTc(tc);
            fs.setBody(fb);
            fs.setReqip(CommonUtils.getClientIp(request));
            log.info(gson.toJson(fs));
            VlSaveService service = vlSaveServiceFactory.getService(formData.getId());
            return ResponseEntity.ok(service.executeSave(fs));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new TabResponse("F", e.getMessage()));
        }
    }

    @PostMapping("/SaveFCV-data")
    public String uploadFCVData(@RequestParam String fcvStatus,
                                @RequestParam String cpvStatus,
                                @RequestParam String cfrFound,
                                @RequestParam String wiNum,
                                @RequestParam Long slno,
                                @RequestParam(value = "fileUploadFcv", required = false) MultipartFile fcvFile,
                                @RequestParam(value = "fileUploadCpv", required = false) MultipartFile cpvFile,
                                @RequestParam(value = "fileInputCfr", required = false) MultipartFile cfrFile,
                                HttpServletRequest request) throws JsonProcessingException {
        try {
            VehicleLoanFcvCpvCfr details = new VehicleLoanFcvCpvCfr();
            details.setFcvStatus(fcvStatus);
            details.setCpvStatus(cpvStatus);
            details.setCfrStatus(cfrFound);
            details.setWiNum(wiNum);
            details.setSlno(slno);
            VehicleLoanFcvCpvCfr savedDetails = fcvCpvCfrService.save(details, request, fcvFile, cpvFile, cfrFile);
            if (savedDetails.getSlno() != null) {
                return "success";
            }
            return "failure";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "An error occurred while saving the details: " + e.getMessage();
        }
    }

    @PostMapping("/FetchCRTAmberData")
    public CRTAmberDataDTO fetchamberdatas(@RequestParam("winum") String winum, @RequestParam("slno") Long slno) {
        CRTAmberDataDTO amberdto = vlamberservice.getamberdatasforcrt(winum, slno);
        return amberdto;
    }

    @PostMapping("/fetch-program-details")
    public ResponseEntity<?> fetchProgramDetails(@RequestBody Map<String, String> requestBody) {
        String applicantId = requestBody.get("applicantId");
        String wiNum = requestBody.get("wiNum");
        String slno = requestBody.get("slno");

        try {
            // Fetch the applicant
            VehicleLoanApplicant applicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(Long.valueOf(applicantId));
            if (applicant == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "E", "message", "Applicant not found"));
            }

            // Get the existing program
            VehicleLoanProgram program = VehicleLoanProgramservice.findVehicleLoanProgrambyAppID(Long.valueOf(applicantId));

            // Fetch program details using the service
            Map<String, Object> programDetails = VehicleLoanProgramservice.getProgramDetails(program, Long.valueOf(slno));

            if (programDetails == null) {
                return ResponseEntity.ok(Map.of(
                        "status", "S",
                        "message", "No program found for this applicant.",
                        "programDetails", null
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "status", "S",
                    "message", "Program details fetched successfully",
                    "programDetails", programDetails
            ));
        } catch (Exception e) {
            log.error("Error fetching program details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "E", "message", "Error fetching program details: " + e.getMessage()));
        }
    }

    @PostMapping("/check-active-itr")
    public ResponseEntity<?> checkActiveITR(@RequestBody Map<String, String> request) {
        Long applicantId = Long.valueOf(request.get("applicantId"));
        String wiNum = request.get("wiNum");

        boolean hasActiveProcess = loanProgramIntegrationService.hasActiveITRProcess(applicantId, wiNum);

        return ResponseEntity.ok(Collections.singletonMap("hasActiveProcess", hasActiveProcess));
    }

    @PostMapping("/reset-active-itr")
    public ResponseEntity<?> resetActiveITR(@RequestBody Map<String, String> request) {
        Long applicantId = Long.valueOf(request.get("applicantId"));
        String wiNum = request.get("wiNum");

        loanProgramIntegrationService.resetActiveITRProcess(applicantId, wiNum);

        return ResponseEntity.ok(Collections.singletonMap("status", "success"));
    }

    @PostMapping("/check-active-bsa")
    public ResponseEntity<?> checkActiveBSA(@RequestBody Map<String, String> request) {
        Long applicantId = Long.valueOf(request.get("applicantId"));
        String wiNum = request.get("wiNum");

        boolean hasActiveProcess = loanProgramIntegrationService.hasActiveBSAProcess(applicantId, wiNum);

        return ResponseEntity.ok(Collections.singletonMap("hasActiveProcess", hasActiveProcess));
    }

    @PostMapping("/reset-active-bsa")
    public ResponseEntity<?> resetActiveBSA(@RequestBody Map<String, String> request) {
        Long applicantId = Long.valueOf(request.get("applicantId"));
        String wiNum = request.get("wiNum");

        loanProgramIntegrationService.resetActiveBSAProcess(applicantId, wiNum);

        return ResponseEntity.ok(Collections.singletonMap("status", "success"));
    }

    @PostMapping("/fetchITR")
    public String submitFormData(@RequestBody String requestData) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String itrSMSMobileNo = requestMap.get("itrSMSMobileNo");
            String itrPan = requestMap.get("itrPan");
            String itrDOB = requestMap.get("itrDOB");
            String itrMode = requestMap.get("itrMode");
            String applicantId = requestMap.get("applicantId");
            String wiNum = requestMap.get("wiNum");
            String slno = requestMap.get("slno");

            return loanProgramIntegrationService.handleITRRequest(itrSMSMobileNo, itrPan, itrDOB, itrMode, applicantId, wiNum, slno);
        } catch (Exception exception) {
            return "{\"error\":\"An error occurred while processing the request.\"}";
        }
    }

    @PostMapping("/fetchITRUpload")
    public String submitITRUploadFormData(@RequestBody String requestData) {
        try {
            Map<String, Object> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String itrSMSMobileNo = (String) requestMap.get("itrSMSMobileNo");
            String itrPan = (String) requestMap.get("itrPan");
            String itrDOB = (String) requestMap.get("itrDOB");
            String itrMode = (String) requestMap.get("itrMode");
            String applicantId = (String) requestMap.get("applicantId");
            String wiNum = (String) requestMap.get("wiNum");
            String slno = (String) requestMap.get("slno");
            List<String> itrYearsList = (List<String>) requestMap.get("itrYearsList");
            List<String> form26asYearsList = (List<String>) requestMap.get("form26asYearsList");
            List<String> form16YearsList = (List<String>) requestMap.get("form16YearsList");

            return loanProgramIntegrationService.handleITRUploadRequest(itrSMSMobileNo, itrPan, itrDOB, itrMode, applicantId, wiNum, slno, itrYearsList, form26asYearsList, form16YearsList);
        } catch (Exception exception) {
            return "{\"error\":\"An error occurred while processing the ITR upload request.\"}";
        }
    }

    @PostMapping("/fetchBSA")
    public String submitBSAFormData(@RequestBody String requestData, HttpServletRequest request) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String txnId = requestMap.get("txnId");
            String institutionId = requestMap.get("institutionId");
            String yearMonthFrom = requestMap.get("yearMonthFrom");
            String yearMonthTo = requestMap.get("yearMonthTo");
            String loanType = "Vehicle";
            String applicantId = requestMap.get("applicantId");
            String wiNum = requestMap.get("wiNum");
            String slno = requestMap.get("slno");

            return loanProgramIntegrationService.handleBSARequest(txnId, institutionId, yearMonthFrom, yearMonthTo, loanType, applicantId, wiNum, slno);
        } catch (Exception exception) {
            return "{\"error\":\"An error occurred while processing the request.\"}";
        }
    }

    @PostMapping("/hasITREntries")
    public boolean hasITREntries(@RequestBody String requestData) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String applicantId = requestMap.get("applicantId");
            String wiNum = requestMap.get("wiNum");
            return loanProgramIntegrationService.hasITREntries(applicantId, wiNum);
        } catch (Exception exception) {
            return false;
        }
    }

    @PostMapping("/getLatestCompletedITRTransactionId")
    public String getLatestCompletedITRTransactionId(@RequestBody String requestData) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String applicantId = requestMap.get("applicantId");
            String wiNum = requestMap.get("wiNum");
            return loanProgramIntegrationService.fetchLatestCompletedITRTransactionId(applicantId, wiNum);
        } catch (Exception e) {
            return "{\"error\":\"An error occurred while processing the request.\"}";
        }
    }

    @PostMapping("/fetchITRReport")
    public String fetchITRReport(@RequestBody Map<String, String> requestMap, HttpServletRequest request) {
        String perfiosTransactionId = requestMap.get("perfiosTransactionId");
        String applicantId = requestMap.get("applicantId");
        String wiNum = requestMap.get("wiNum");
        return loanProgramIntegrationService.fetchITRReport(perfiosTransactionId, applicantId, wiNum);
    }

    @GetMapping("/itr-status")
    public ResponseEntity<?> getITRStatus(@RequestParam Long applicantId, @RequestParam String wiNum) {
        VehicleLoanITR latestITR = loanProgramIntegrationService.getLatestITRStatus(applicantId, wiNum);
        if (latestITR != null) {
            Map<String, Object> status = new HashMap<>();
            status.put("itrMode", latestITR.getItrMode());
            status.put("timestamp", latestITR.getTimestamp());
            status.put("perfiosTransactionId", latestITR.getPerfiosTransactionId());
            status.put("generateLinkId", latestITR.getGenerateLinkId());
            status.put("url", latestITR.getUrl());
            status.put("delFlg", latestITR.getDelFlg());
            status.put("perfiosStatus", latestITR.getPerfiosStatus());
            status.put("perfiosMessage", latestITR.getMessage());
            status.put("updated", latestITR.getUpdated());
            return ResponseEntity.ok(status);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/hasBSAEntries")
    public boolean hasBSAEntries(@RequestBody String requestData) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String applicantId = requestMap.get("applicantId");
            String wiNum = requestMap.get("wiNum");
            return loanProgramIntegrationService.hasBSAEntries(applicantId, wiNum);
        } catch (Exception exception) {
            return false;
        }
    }

    @GetMapping("/bsa-status")
    public ResponseEntity<?> getBSAStatus(@RequestParam Long applicantId, @RequestParam String wiNum) {
        VehicleLoanBSA latestBSA = loanProgramIntegrationService.getLatestBSAStatus(applicantId, wiNum);
        if (latestBSA != null) {
            Map<String, Object> status = new HashMap<>();
            status.put("timestamp", latestBSA.getTimestamp());
            status.put("perfiosTransactionId", latestBSA.getPerfiosTransactionId());
            status.put("txnId", latestBSA.getTxnId());
            status.put("url", latestBSA.getUrl());
            status.put("delFlg", latestBSA.getDelFlg());
            status.put("perfiosStatus", latestBSA.getPerfiosStatus());
            status.put("perfiosMessage", latestBSA.getMessage());
            status.put("updated", latestBSA.getUpdated());
            status.put("errorCode", latestBSA.getErrorCode());
            return ResponseEntity.ok(status);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/getLatestCompletedBSATransactionId")
    public String getLatestCompletedBSATransactionId(@RequestBody String requestData) {
        try {
            Map<String, String> requestMap = new ObjectMapper().readValue(requestData, Map.class);
            String applicantId = requestMap.get("applicantId");
            String wiNum = requestMap.get("wiNum");
            return loanProgramIntegrationService.fetchLatestCompletedBSATransactionId(applicantId, wiNum);
        } catch (Exception e) {
            return "{\"error\":\"An error occurred while processing the request.\"}";
        }
    }

    @PostMapping("/fetchBSAReport")
    public String fetchBSAReport(@RequestBody Map<String, String> requestMap) {
        String perfiosTransactionId = requestMap.get("perfiosTransactionId");
        String applicantId = requestMap.get("applicantId");
        String wiNum = requestMap.get("wiNum");
        return loanProgramIntegrationService.fetchBSAReport(perfiosTransactionId, applicantId, wiNum);
    }

    //    @PostMapping("/getFDAccountDetails")
//    public ResponseEntity getAccountDetails(@RequestBody FDAccountRequest fdAccountRequest, HttpServletRequest request) {
//        fdAccountRequest.setLastModUser(usd.getPPCNo());
//        fdAccountRequest.setHomeSol(usd.getSolid());
//        fdAccountRequest.setReqIpAddr(CommonUtils.getClientIp(request));
//        List<VehicleLoanFD> vehicleLoanFDList = fdAccountService.getAccountDetails(fdAccountRequest);
//        if (vehicleLoanFDList.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        } else {
//            return ResponseEntity.ok(vehicleLoanFDList);
//        }
//
//    }
//    @PostMapping("/getFDAccountDetails")
//    public ResponseEntity getAccountDetails(@RequestBody FDAccountRequest fdAccountRequest, HttpServletRequest request) {
//        fdAccountRequest.setLastModUser(usd.getPPCNo());
//        fdAccountRequest.setHomeSol(usd.getSolid());
//        fdAccountRequest.setReqIpAddr(CommonUtils.getClientIp(request));
//        Object response = fdAccountService.fetchAccountDetails(fdAccountRequest);
//
//        if (response instanceof String) {
//            String message = (String) response;
//            if (message.startsWith("Error:")) {
//                return ResponseEntity.badRequest().body(response);
//            } else {
//                return ResponseEntity.ok().body(message);
//            }
//        } else {
//            List<VehicleLoanFD> vehicleLoanFDList = (List<VehicleLoanFD>) response;
//            return ResponseEntity.ok(vehicleLoanFDList);
//        }
//    }
    @PostMapping("/getFDAccountDetailsbycif")
    public ResponseEntity<?> getFDAccountDetails(@RequestBody FDAccountRequest fdAccountRequest) {
        try {
            Map<String, Object> result = fdAccountService.fetchAccountDetailsByCifNew(fdAccountRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching FD account details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching FD account details");
        }
    }

    @PostMapping("/getFDAccountDetailsbycifV2")
    public ResponseEntity<?> getFDAccountDetailsV2(@RequestBody FDAccountRequest fdAccountRequest, HttpServletRequest request) {
        try {
            fdAccountRequest.setLastModUser(usd.getPPCNo());
            fdAccountRequest.setHomeSol(usd.getSolid());
            fdAccountRequest.setReqIpAddr(CommonUtils.getClientIp(request));
            Map<String, Object> result = fdAccountService.fetchAccountDetailsByCifNew(fdAccountRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching FD account details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching FD account details");
        }
    }

    @PostMapping("/deleteFDAccount")
    public ResponseEntity<Void> deleteFDAccount(@RequestBody Map<String, Long> requestBody, HttpServletRequest request) {
        Long ino = requestBody.get("ino");
        String delUser = usd.getPPCNo();
        String delHomeSol = usd.getSolid();
        String delIpAddr = CommonUtils.getClientIp(request);

        String result = fdAccountService.deleteFDAccount(ino, delUser, delHomeSol, delIpAddr);

        if (result.equals("success")) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/getAccountDetails")
    public ResponseEntity<Map<String, Object>> getAccountDetails(@RequestParam Long applicantId, @RequestParam String wiNum, Long slno) {
        Map<String, Object> vehicleLoanFDList = fdAccountService.getAccountDetails(applicantId, wiNum, slno);
        return ResponseEntity.ok(vehicleLoanFDList);
    }


    @PostMapping("/saveVLProgram")
    public ResponseEntity<VehicleLoanProgram> saveVLProgram(@RequestBody VehicleLoanProgram vehicleLoanProgram, HttpServletRequest request) {
        vehicleLoanProgram.setCmUser(usd.getPPCNo());
        vehicleLoanProgram.setCmDate(new Date());
        vehicleLoanProgram.setReqIpAddr(CommonUtils.getClientIp(request));
        VehicleLoanProgramservice.insertVehicleLoanProgram(vehicleLoanProgram);
        return ResponseEntity.ok(vehicleLoanProgram);
    }

    @PostMapping("/saveVLEmployment")
    public ResponseEntity<VLEmployment> saveVLEmployment(@RequestBody VLEmployment vlemploymentdetails, HttpServletRequest request) {
        vlemploymentdetails.setCmdate(new Date());
        vlemploymentdetails.setCmuser(usd.getPPCNo());
        vlemploymentdetails.setReqIpAddr(CommonUtils.getClientIp(request));
        VLEmployment savedVLEmploymentdetails = vlemploymentservice.save(vlemploymentdetails);
        return ResponseEntity.ok(savedVLEmploymentdetails);
    }

    @GetMapping("/fetchEmpDetails")
    public ResponseEntity<List<VLEmployment>> fetchEmpDetails(@RequestParam String wiNum, @RequestParam String slNo) {
        List<VLEmployment> vlempdetails = vlemploymentservice.findBySlno(Long.parseLong(slNo));
        return ResponseEntity.ok(vlempdetails);
    }


    @GetMapping("/getOctDetails")
    public ResponseEntity<List<Octdetails>> getOctDetails() {
        List<Octdetails> octdetails = octdetailsservice.getOctDetails();
        return ResponseEntity.ok(octdetails);
    }


    @PostMapping("/bmdocuploadsave")
    public TabResponse handleFileUpload(@ModelAttribute FileUploadForm form, HttpServletRequest request) {
        form.setReqip(CommonUtils.getClientIp(request));
        CommonDTO commonDTO = new CommonDTO();
        commonDTO.setFileUploadForm(form);
        CommonService commonService = commonServiceFactory.getService("BMUPLOAD");
        return commonService.processData(commonDTO);

    }

    @PostMapping("/getExperianScore")
    public TabResponse handleFileUpload(@RequestBody CommonDTO exp) {
        try {
            CommonService commonService = commonServiceFactory.getService("EXPERIAN");
            return commonService.processData(exp);
        } catch (Exception e) {
            return new TabResponse("F", e.getMessage());
        }

    }


    @PostMapping(value = "/saveAllotment")
    @Transactional
    public ResponseDTO saveAllotment(@RequestParam("slno") @Validated String slno, @RequestParam("wiNum") @Validated String wiNum, @RequestParam("doPpc") @Validated int doPpc, @RequestParam("remarks") @Validated String remarks, HttpServletRequest request) {
        try {
            VehicleLoanAllotment savedAllotmentDetails = vlloanallotmentservice.save(wiNum, Long.valueOf(slno), doPpc, request);
            VehicleLoanMaster loanMaster = loanMasterRepository.findBySlnoWithApplicants(Long.valueOf(slno));
            if (loanMaster == null) {
                throw new RuntimeException("Loan application not found");
            }
            queueDetailsService.createQueueWithAssignUserEntry(wiNum, Long.valueOf(slno), remarks, usd.getEmployee().getPpcno(), loanMaster.getQueue(), "RM", String.valueOf(doPpc));
            //queueDetailsService.createQueueEntry(wiNum, Long.valueOf(slno), remarks, usd.getEmployee().getPpcno(), loanMaster.getQueue(), "RM");
            vehicleLoanMasterService.updateQueue(Long.valueOf(slno), "RM", "RACOMPLETE", usd.getPPCNo());
            vlTatService.updateTat(Long.valueOf(slno), usd.getPPCNo(), wiNum, "RM");
            return new ResponseDTO("S", "");
        } catch (Exception e) {
            return new ResponseDTO("F", e.getMessage());
        }
    }


    @RequestMapping("/bmsave")
    public ResponseDTO BMSave(@RequestParam("slno") @Validated String slno, @RequestParam("winum") @Validated String winum, @RequestParam("vlowner") @Validated String vlowner, @RequestParam("vlownerstatus") @Validated String vlownerstatus, @RequestParam("remarks") @Validated String remarks, HttpServletRequest request) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("BRMAKER");
            return vl.saveMaker(slno, winum, vlowner, vlownerstatus, remarks, request);
        } catch (Exception e) {
            return new ResponseDTO("F", e.getMessage());
        }
    }


    @PostMapping("/wicrtambersave")
    @Transactional
    public ResponseDTO wiCrtAmberSave(@RequestParam("slno") @Validated String slno, @RequestParam("winum") @Validated String winum, @RequestParam("remarks") @Validated String remarks, @RequestParam("declaration") @Validated String declaration, @RequestParam("decision") @Validated String decision, HttpServletRequest request) {
        try {
            if (decision.equals("A") && (declaration == null || declaration.isEmpty())) {
                return new ResponseDTO("F", "Kindly accept the declaration to proceed");
            }
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("CRTAMBER");
            return vl.saveCRTAmber(slno, winum, remarks, decision, request);
        } catch (Exception e) {
            return new ResponseDTO("F", e.getMessage());
        }
    }

    @PostMapping("/recallwisave")
    @Transactional
    public ResponseDTO recallWISave(@RequestParam("winum") @Validated String winum, @RequestParam("remarks") @Validated String remarks, HttpServletRequest request) {
        try {
            if (winum == null || winum.isEmpty() || remarks == null || remarks.isEmpty()) {
                return new ResponseDTO("F", "Please provide all inputs");
            }
            winum = winum.toUpperCase();
            if (!winum.startsWith("VLR")) {
                //wiNum=wiNum+"VLR"+StringUtils.leftPad(wiNum, 9, '0');
                String temp = "000000000" + winum;
                temp = temp.substring(temp.length() - 9);
                winum = "VLR_" + temp;
            }
            if (winum.length() != 13) {
                return new ResponseDTO("F", "Invalid WI number");
            }
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("CRTAMBER");
            return vl.saveWIRecall(winum, remarks, request);

        } catch (Exception e) {
            return new ResponseDTO("F", e.getMessage());
        }
    }


    @GetMapping("/wienquiryfetch")
    public ResponseEntity<ResponseDTO> wiEnquiryfetch(@RequestParam("winum") @Validated String wiNum, HttpServletRequest request) {
        try {
            if (wiNum == null || wiNum.trim().isEmpty()) {
                return ResponseEntity.ok(new ResponseDTO("F", "WI number is required"));
            }
            wiNum = wiNum.toUpperCase();
            if (!wiNum.startsWith("VLR")) {
                //wiNum=wiNum+"VLR"+StringUtils.leftPad(wiNum, 9, '0');
                String temp = "000000000" + wiNum;
                temp = temp.substring(temp.length() - 9);
                wiNum = "VLR_" + temp;
            }
            if (wiNum.length() != 13) {
                return ResponseEntity.ok(new ResponseDTO("F", "Invalid WI number"));
            }
            VehicleLoanMaster loanMaster = vehicleLoanMasterService.findByWiNum(wiNum);
            if (loanMaster == null || loanMaster.getCustName() == null || loanMaster.getCustName().trim().isEmpty()) {
                return ResponseEntity.ok(new ResponseDTO("F", "WI number not found"));
            }
            String slno = loanMaster.getSlno().toString();
            return ResponseEntity.ok(new ResponseDTO("S", slno));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO("F", "Please check the WI number"));
        }
    }

    @PostMapping("/wibogsave")
    public ResponseEntity<ResponseDTO> wiBogSave(@RequestParam("slno") @Validated String slno, @RequestParam("winum") @Validated String winum, @RequestParam("remarks") @Validated String remarks, HttpServletRequest request) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("BOG");
            ResponseDTO responseDTO = vl.saveBOG(Long.parseLong(slno), winum, remarks, request);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseDTO("ERROR", "An unexpected error occurred: " + e.getMessage()));
        }
    }


    @PostMapping("/wismsave")
    public ResponseEntity<ResponseDTO> wismsave(@RequestParam("slno") @Validated String slno, @RequestParam("winum") @Validated String winum, @RequestParam("remarks") @Validated String remarks, @RequestParam("action") @Validated String action, HttpServletRequest request) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("BOG");
            ResponseDTO responseDTO = vl.savewism(Long.parseLong(slno), winum, remarks, action, request);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseDTO("ERROR", "An unexpected error occurred: " + e.getMessage()));
        }
    }


    @PostMapping("/acctlabelsave")
    public ResponseEntity<ResponseDTO> acctlabelsave(@RequestBody AcctLabelDTO acctLabelDTO, HttpServletRequest request) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("BOG");
            ResponseDTO responseDTO = vl.acctlabelsave(acctLabelDTO, request);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseDTO("ERROR", "An unexpected error occurred: " + e.getMessage()));
        }
    }


    @PostMapping("/accopening")
    public ResponseEntity<ResponseDTO> accopening(@RequestParam("slno") @Validated Long slno, @RequestParam("winum") @Validated String winum, HttpServletRequest request) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("BOG");

            ResponseDTO responseDTO = vl.performAccOpening(slno, winum, request);
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseDTO("ERROR", "An unexpected error occurred: " + e.getMessage()));
        }
    }


    @PostMapping("/disbursement")
    @RequiresMenuAccess(menuIds = {"ACOPN"})
    public ResponseEntity<ResponseDTO> disbursement(@RequestParam("slno") @Validated Long slno, @RequestParam("winum") @Validated String winum, HttpServletRequest request) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("BOG");

            ResponseDTO responseDTO = vl.disbursement(slno, winum, request);
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseDTO("ERROR", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/disbursementenq")
    @RequiresMenuAccess(menuIds = {"ACOPN"})
    public ResponseEntity<ResponseDTO> disbursementenq(@RequestParam("slno") @Validated Long slno, @RequestParam("winum") @Validated String winum, HttpServletRequest request) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("BOG");

            ResponseDTO responseDTO = vl.performDisbStatusEnquiry(slno, winum, request);
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseDTO("ERROR", "" + e.getMessage()));//An unexpected error occurred:
        }
    }

    @PostMapping("/instalment-dates")
    public ResponseEntity<?> getInstalmentDates(@RequestBody Map<String, Long> request) {
        Long slno = request.get("slno");
        if (slno == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "slno is required"));
        }
        try {
            Map<String, String> instalmentDates = fetchRepository.getInstalmentDates(slno);
            return ResponseEntity.ok(instalmentDates);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sendNachRequest")
    public ResponseEntity<Map<String, Object>> sendNachRequest(@RequestBody Map<String, Object> request) {
        Long slno = Long.valueOf(request.get("slno").toString());
        String mode = (String) request.get("mode");
        Map<String, Object> response = nachMandateService.sendNachMandate(slno, mode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createManualNach")
    public ResponseEntity<?> createManualNach(@RequestBody Map<String, Object> request) {
        Long slno = Long.valueOf(request.get("slno").toString());
        String mode = (String) request.get("mode");

        try {
            Map<String, Object> response = nachMandateService.sendNachMandate(slno, mode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating manual NACH", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/nachMandateExists")
    public ResponseEntity<Map<String, Boolean>> checkNACHMandateStatus(@RequestParam Long slno) {
        boolean exists = nachMandateService.nachMandateExists(slno);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @PostMapping("/lienMarking")
    public ResponseEntity<?> markLien(@RequestParam String wiNum) {
        try {
            List<LienMarkingResult> results = lienMarkingService.markLiens(wiNum);
            return ResponseEntity.ok(Map.of("status", "success", "results", results));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/performneft")
    @RequiresMenuAccess(menuIds = {"ACOPN"})
    public ResponseEntity<ResponseDTO> performneft(@RequestParam("slno") @Validated Long slno, @RequestParam("winum") @Validated String winum,
                                                   @RequestParam("beneficiaryType") @Validated String beneficiaryType,
                                                   @RequestParam("dneftamt") @Validated String dneftamt,
                                                   @RequestParam("mneftamt") @Validated String mneftamt,
                                                   @RequestParam("accnum") String accnum,
                                                   @RequestParam("ifsc") String ifsc,
                                                   @RequestParam("accname") String accname,
                                                   @RequestParam("manufmobile") String manufmobile,
                                                   @RequestParam("disbType") @Validated String disbType,
                                                   @RequestParam("add1") String add1,
                                                   @RequestParam("add2") String add2,
                                                   @RequestParam("add3") String add3,


                                                   @Validated @RequestParam("dealername") String dealername,
                                                   @Validated @RequestParam("dealernamermk") String dealernamermk,
                                                   @Validated @RequestParam("dstcode") String dstcode,
                                                   @Validated @RequestParam("dsacode") String dsacode,
                                                   @Validated @RequestParam("dealercode") String dealercode,
                                                   @Validated @RequestParam("dealersubcode") String dealersubcode,
                                                   @Validated @RequestParam("dealeracc") String dealeracc,
                                                   @Validated @RequestParam("dealerifsc") String dealerifsc,
                                                   @Validated @RequestParam("manmake") String manmake,
                                                   HttpServletRequest request) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("BOG");

            bogSaveImpl.beforePerformNeft(slno, winum, beneficiaryType, dneftamt, mneftamt, accnum, ifsc, accname, manufmobile, disbType, add1, add2,
                    add3, request, dealername, dealernamermk, dstcode, dsacode, dealercode, dealersubcode, dealeracc, dealerifsc, manmake
            );
            ResponseDTO responseDTO = vl.performNeft(slno, winum, beneficiaryType, dneftamt, mneftamt, accnum, ifsc, accname, manufmobile, disbType, add1, add2, add3, request);
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO("ERROR", e.getMessage()));
        }
    }

    @PostMapping("/performInsFi")
    public  ResponseEntity<ResponseDTO> performInsFiTrn(@RequestParam("slno") @Validated Long slno, @RequestParam("winum") @Validated String winum) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("BOG");
            ResponseDTO responseDTO = vl.performInsFiTrn(slno, winum);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO("ERROR", e.getMessage()));
        }
    }
    @PostMapping("/cancelMandate")
    public ResponseEntity<Map<String, Object>> cancelMandate(@RequestBody Map<String, Long> request) {
        Long slno = request.get("slno");
        Map<String, Object> response = nachMandateService.cancelMandate(slno);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/resendNachDetails")
    public ResponseEntity<?> resendNachDetails(@RequestBody Map<String, Long> request) {
        Long slno = request.get("slno");
        try {
            ResponseDTO response = nachMandateService.resendNachDetails(slno);
            if ("S".equals(response.getStatus())) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "NACH details resent successfully"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "error",
                        "message", response.getMsg()
                ));
            }
        } catch (Exception e) {
            log.error("Error resending NACH details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    ));
        }
    }

    @PostMapping("/checkMandateStatus")
    public ResponseEntity<Map<String, Object>> checkMandateStatus(@RequestBody Map<String, Long> request) {
        Long slno = request.get("slno");
        Map<String, Object> response = nachMandateService.checkMandateStatus(slno);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/searchppc")
    public List<Map<String, String>> searchByText(@RequestParam("searchText") String searchText) {
        return fetchRepository.findMatches(searchText);
    }


    @PostMapping("/bmdel")
    @Transactional
    public ResponseEntity<ResponseDTO> wiCrtAmberSave(@RequestParam("slno") @Validated Long slno, HttpServletRequest request) {
        try {
            VlCommonTabService vl = vlSaveServiceFactory.getTabService("BRMAKER");
            return ResponseEntity.ok(vl.bmDel(slno, request));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO("F", e.getMessage()));
        }
    }

    @GetMapping("/wifolders")
    public ResponseEntity<ResponseDTO> wiFolderfetch(@RequestParam("winum") @Validated String wiNum, HttpServletRequest request) {
        try {
            if (wiNum == null || wiNum.trim().isEmpty()) {
                return ResponseEntity.ok(new ResponseDTO("F", "WI number is required"));
            }
            wiNum = wiNum.toUpperCase();
            if (!wiNum.startsWith("VLR")) {
                //wiNum=wiNum+"VLR"+StringUtils.leftPad(wiNum, 9, '0');
                String temp = "000000000" + wiNum;
                temp = temp.substring(temp.length() - 9);
                wiNum = "VLR_" + temp;
            }
            if (wiNum.length() != 13) {
                return ResponseEntity.ok(new ResponseDTO("F", "Invalid WI number"));
            }
            VehicleLoanMaster loanMaster = vehicleLoanMasterService.SearchByWiNum(wiNum);
            if (loanMaster == null || loanMaster.getCustName() == null || loanMaster.getCustName().trim().isEmpty()) {
                return ResponseEntity.ok(new ResponseDTO("F", "WI number not found"));
            }
            if (loanMaster.getQueue().equals("NIL")) {//|| loanMaster.getQueue().equals("PD")
                return ResponseEntity.ok(new ResponseDTO("F", "WI is Disbursed/Rejected"));
            }
            List<VehicleLoanApplicant> applicants = loanMaster.getApplicants();
            // Convert to a list of maps with only field1 and field2
            List<Map<String, Object>> folderNames = applicants.stream().filter(t -> "N".equalsIgnoreCase(t.getDelFlg()))
                    .map(dto -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("appName", dto.getApplName());
                        map.put("folderName", dto.getBpmFolderName());
                        return map;
                    })
                    .collect(Collectors.toList());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(folderNames);

            return ResponseEntity.ok(new ResponseDTO("S", jsonString));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO("F", "Please check the WI number"));
        }
    }

    @PostMapping("/customuploadsave")
    public ResponseEntity<TabResponse> customuploadsave(@RequestBody CommonDTO commonDTO) {
        try {
            CommonService vl = commonServiceFactory.getService("CUSTDOC");
            TabResponse responseDTO = vl.processData(commonDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new TabResponse("F", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/saveGeneralDetails")
    @ResponseBody
    public TabResponse saveGeneralDetails(@RequestBody FormSave formSave,
                                          HttpServletRequest request) {
        TypeCount tc = CommonUtils.parseString("A-1");
        FormBody fb = new FormBody();
        FormData formData = new FormData();
        formData.setAppid(formSave.getBody().getAppid());
        formData.setWinum(formSave.getBody().getWinum());
        formData.setSlno(formSave.getBody().getSlno());
        formData.setData(formSave.getBody().getData());
        formData.setDOC_ARRAY(formSave.getBody().getDOC_ARRAY());
        formData.setId("GEN");
        formData.setReqtype("A");

        if (formSave.getBody().getDOC_ARRAY() != null && formSave.getBody().getDOC_ARRAY().size() > 0) {
            TabResponse rs = bpmService.BpmChildUpload(formData, tc, request, usd.getTrace_id());
            fb.setFileFlag("Y");
            fb.setDOC_ARRAY(formData.getDOC_ARRAY());
        }
        formSave.getBody().setTc(tc);

        formSave.setReqip(request.getRemoteAddr());
        return generalDtSaveService.executeSave(formSave);
    }


    @GetMapping("/pincodes/{stateCode}")
    public Map<String, Object> getAllLocations(@PathVariable String stateCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PincodeMaster> locations = pincodeService.getAllLocations(stateCode).stream()
                    .collect(Collectors.toMap(PincodeMaster::getFinacleCityCode,
                            city -> city,
                            (existing, replacement) -> existing
                    )).values().stream().collect(Collectors.toList());

            response.put("success", true);
            response.put("message", "Locations fetched successfully");
            response.put("data", locations);
        } catch (Exception e) {
            log.error("Error fetching locations", e);
            response.put("success", false);
            response.put("message", "Error fetching locations");
        }
        return response;
    }

    @GetMapping("/pincodes/{stateCode}/{cityCode}")
    public Map<String, Object> getPincodes(@PathVariable String stateCode,
                                           @PathVariable String cityCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<PincodeDTO> pincodes = pincodeService.getPincodesByStateAndCity(stateCode, cityCode);
            response.put("success", true);
            response.put("message", "Pincodes fetched successfully");
            response.put("data", pincodes);
        } catch (Exception e) {
            log.error("Error fetching pincodes", e);
            response.put("success", false);
            response.put("message", "Error fetching pincodes");
        }
        return response;
    }

    @GetMapping("/api-logs")
    @ResponseBody
    public Page<Map<String, Object>> getApiLogs(
            @RequestParam String wiNum,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Convert to 0-based page index
        return auditService.apiLogs(wiNum, page - 1, size);
    }


    @PostMapping("/pincode/create")
    public ResponseEntity<Map<String, Object>> createPincode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            validateRequest(request);
            pincodeService.createPincode(request.get("pincode"), request.get("stateCode"), request.get("cityCode"));
            response.put("success", true);
            response.put("message", "Pincode created successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid pincode request", e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (EntityNotFoundException e) {
            log.error("Entity not found", e);
            response.put("success", false);
            response.put("message", "Entry not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Error creating pincode", e);
            response.put("success", false);
            response.put("message", "Error creating pincode");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/process-documents/{losId}")
    public ResponseEntity<?> processDocuments(@PathVariable String losId) {
        try {
            MssfDocProcessResponse response = mssfDocumentService.processDocuments(losId);
            return ResponseEntity.ok(response);

        } catch (MssfApiException e) {
            log.error("Document processing failed for losId: {}", losId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Unexpected error for losId: {}", losId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "An unexpected error occurred"
                    ));
        }
    }

    @GetMapping("/document-status/{losId}")
    public ResponseEntity<?> getDocumentStatus(@PathVariable String losId) {
        try {
            List<StoredDocument> storedDocs = mssfDocumentService.getStoredDocuments(losId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "documents", storedDocs
            ));
        } catch (Exception e) {
            log.error("Failed to get document status for losId: {}", losId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Failed to get document status"
                    ));
        }
    }

    private void validateRequest(Map<String, String> request) {
        if (!request.containsKey("pincode")) {
            throw new RuntimeException("Missing required fields");
        }
        if (!request.get("pincode").matches("^[1-9][0-9]{5}$")) {
            throw new RuntimeException("Invalid pincode format");
        }
    }

    @PostMapping("/dedupe/check")
    public ResponseEntity<?> performDedupeCheck(@RequestBody DedupeRequest request,
                                                HttpServletRequest httpRequest) {
        try {
            log.info("Received dedupe check request for applicant ID: {}", request.getOrigin());
            DedupeResponse result = vlDedupeService.performDedupeCheck(request, httpRequest.getRemoteAddr());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error performing dedupe check for applicant ID: {}", request.getOrigin(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error performing dedupe check: " + e.getMessage()));
        }
    }

    @PostMapping("/dedupe/updateRelation")
    public ResponseEntity<?> updateDedupeRelation(@RequestBody DedupeRelationRequest request,
                                                  HttpServletRequest httpRequest) {
        try {
            log.info("Updating dedupe relation for applicant ID: {}", request.getApplicantId());
            vlDedupeService.updateDedupeRelation(request.getApplicantId(),
                    request.getRelation(),
                    httpRequest.getRemoteAddr());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating dedupe relation for applicant ID: {}", request.getApplicantId(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error updating relation: " + e.getMessage()));
        }
    }

    @PostMapping("/dedupe/reject")
    public ResponseEntity<?> rejectDedupeMatch(@RequestBody DedupeRejectRequest request,
                                               HttpServletRequest httpRequest) {
        try {
            log.info("Rejecting dedupe match for applicant ID: {}", request.getApplicantId());
            vlDedupeService.rejectDedupeMatch(request.getApplicantId(),
                    request.getRemarks(),
                    httpRequest.getRemoteAddr());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error rejecting dedupe match for applicant ID: {}", request.getApplicantId(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error rejecting match: " + e.getMessage()));
        }
    }

    @PostMapping("/dedupe/submit")
    public ResponseEntity<?> submitDedupeResults(@RequestBody DedupeSubmitRequest request,
                                                 HttpServletRequest httpRequest) {
        try {
            log.info("Submitting dedupe results for {} applicants", request.getResults().size());
            vlDedupeService.submitDedupeResults(request.getResults(), httpRequest.getRemoteAddr());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error submitting dedupe results", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error submitting results: " + e.getMessage()));
        }
    }

    @GetMapping("/dedupe/checkStatus")
    public ResponseEntity<?> checkDedupeStatus(@RequestParam String wiNum, @RequestParam String slNo) {
        try {
            List<Map<String, Object>> pendingRelations = vlDedupeService.getPendingDedupeRelations(wiNum, slNo);
            return ResponseEntity.ok(pendingRelations);
        } catch (Exception e) {
            log.error("Error checking dedupe status for WI: {} and SlNo: {}", wiNum, slNo, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error checking dedupe status: " + e.getMessage()));
        }
    }

    @GetMapping("/endpoints")
    public ResponseEntity<List<Map<String, Object>>> getApiEndpoints() {
        try {
            List<Map<String, Object>> endpoints = auditService.getApiEndpoints();
            return ResponseEntity.ok(endpoints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/decode-base64")
    public ResponseEntity<Map<String, String>> decodeBase64(
            @RequestBody Map<String, String> requestBody) {
        String content = requestBody.get("content");
        Map<String, String> result = new HashMap<>();

        try {
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Content cannot be empty");
            }

            byte[] decodedBytes = Base64.getDecoder().decode(content);
            String fileType = detectFileType(decodedBytes);

            result.put("type", fileType);
            result.put("success", "true");
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            result.put("success", "false");
            result.put("error", "Invalid Base64 content");
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            result.put("success", "false");
            result.put("error", "Error processing content: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }


    private String detectFileType(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return "application/octet-stream";
        }

        // Check for PDF signature (%PDF)
        if (bytes[0] == 0x25 && bytes[1] == 0x50 && bytes[2] == 0x44 && bytes[3] == 0x46) {
            return "application/pdf";
        }

        // Check for JPEG signature (FF D8 FF)
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) {
            return "image/jpeg";
        }

        // Check for PNG signature (89 50 4E 47)
        if (bytes[0] == (byte) 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) {
            return "image/png";
        }

        // Try to detect if it's text by checking if it's valid UTF-8
        try {
            new String(bytes, StandardCharsets.UTF_8);
            return "text/plain";
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }

    @PostMapping("/generateToken")
    public TokenResponse generateToken() throws Exception {
        String username=usd.getUserName();
        String targetApp="LOS";
        log.info("Generating token for user {} targeting {}", username, targetApp);
        // Generate JWT token for specified application
        String accessToken = tokenService.generateToken(username, targetApp);
        // Get application URL based on target app ID
        String appUrl = tokenService.getAppUrlById(targetApp);
        log.info("Access Token {}",accessToken);
        return new TokenResponse(accessToken, "", appUrl + "sso/validate");
    }


}
