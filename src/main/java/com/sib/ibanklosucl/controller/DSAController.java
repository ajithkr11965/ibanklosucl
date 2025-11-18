package com.sib.ibanklosucl.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.*;
import com.google.gson.Gson;
import com.sib.ibanklosucl.model.VLDsaStatusView;
import com.sib.ibanklosucl.model.VehicleLoanVkyc;
import com.sib.ibanklosucl.model.integrations.ITRCallback;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.dashboard.notification.NotificationService;
import com.sib.ibanklosucl.service.vlsr.PincodeMasterService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/DSA")
@Slf4j
public class DSAController {
    private static final Gson gson = new Gson();
    @Autowired
    private DsaService dsaservice ;

    @Autowired
    public EligibilityLetterGenerator pdfService;

    @Autowired
    public CustomerVkycService customervkycservice;
/*
    @Autowired
    private  UserSessionData usd;
    */
    @Autowired
    private VehicleLoanCifService vehicleLoanCifService;
    @Autowired
    private VehicleLoanWIService vehicleLoanWIService;

    @Autowired
    private PincodeMasterService pincodeMasterService;

    @Autowired
    private NotificationService notificationService;
     @Autowired
    private ITRCallbackService itrCallbackService;

    @GetMapping("/getWorkItems")
    public ResponseEntity<String> getWorkItems(@RequestParam String userid) {
        try {
            String result = dsaservice.getWorkItems(userid);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("failure");
        }
    }
    @GetMapping("/getMobNo")
    public ResponseEntity<String> getMobNo(@RequestParam String wiNum, @RequestParam Long slno,@RequestParam String appid) {
        try {
            String result = dsaservice.getMobNo(wiNum,slno,appid);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("failure");
        }
    }

    @Autowired
    private DsaService dsaService;
    @PostMapping("/insert")
    public ResponseEntity<String> insertData(@RequestBody Map<String, String> request, HttpServletRequest httpServletRequest) {
        try {
            Map<String, String> userDetails = new ObjectMapper().readValue(request.get("UserDetails"), new TypeReference<Map<String, String>>() {});
            Map<String, String> kycDetails = new ObjectMapper().readValue(request.get("kycDetails"), new TypeReference<Map<String, String>>() {});
            Map<String, String> incomeDetails = new ObjectMapper().readValue(request.get("incomeDetails"), new TypeReference<Map<String, String>>() {});
            Map<String, String> vehicleDetails = new ObjectMapper().readValue(request.get("vehicleDetails"), new TypeReference<Map<String, String>>() {});
            Map<String, String> priceDetails = new ObjectMapper().readValue(request.get("priceDetails"), new TypeReference<Map<String, String>>() {});
            Map<String, String> loanDetails = new ObjectMapper().readValue(request.get("loanDetails"), new TypeReference<Map<String, String>>() {});
            Map<String, String> creditDetails = new ObjectMapper().readValue(request.get("creditDetails"), new TypeReference<Map<String, String>>() {});
            Map<String, String> addressDetails = new ObjectMapper().readValue(request.get("addressDetails"), new TypeReference<Map<String, String>>() {});

            String reqIpAddr = httpServletRequest.getRemoteAddr();
            String response=dsaService.insertData(incomeDetails,vehicleDetails,priceDetails,loanDetails,userDetails, kycDetails, creditDetails, addressDetails, reqIpAddr);
            //return ResponseEntity.ok("success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inserting data: " + e.getMessage());
        }
    }

    @PostMapping("/invoiceupload")
    public ResponseEntity<String> uploadInvoice(@RequestBody Map<String, String> request, HttpServletRequest httpServletRequest) {
        try {
            String wino = request.get("wino").toString();
            String slno = request.get("slno").toString();
            String invoicedate = request.get("invoicedate").toString();
            String invoiceprice = request.get("invoiceprice").toString();
            String invoicenumber = request.get("invoicenumber").toString();
            String invoice = request.get("invoice").toString();
            String invoiceext = request.get("invoiceext").toString();
            log.info("wislno: {}  ~ SLNo {}  ",wino,slno);
            String mainappid="",consent="",osc="",co1appid="",co1consent="",co1osc="",co2appid="",co2consent="",co2osc="",consentext="",oscext="",co1consentext="",co1oscext="",co2consentext="",co2oscext="";
            if(request.get("mainappid")!=null ){
                mainappid = request.get("mainappid").toString();
                consent = request.get("consent").toString();
                osc = request.get("osc").toString();
                consentext = request.get("consentext").toString();
                oscext = request.get("oscext").toString();

            }


            if(request.get("co1appid")!=null ) {
                co1appid = request.get("co1appid").toString();
                co1consent = request.get("co1consent").toString();
                co1osc = request.get("co1osc").toString();
                co1consentext = request.get("co1consentext").toString();
                co1oscext = request.get("co1oscext").toString();
            }
            if(request.get("co2appid")!=null ) {
                co2appid = request.get("co2appid").toString();
                co2consent = request.get("co2consent").toString();
                co2osc = request.get("co2osc").toString();
                co2consentext = request.get("co2consentext").toString();
                co2oscext = request.get("co2oscext").toString();
            }
            String response=dsaService.uploadInvoice(wino,slno,invoice,invoiceext,invoicedate,invoiceprice,invoicenumber,mainappid,consent,osc,co1appid,co1consent,co1osc,co2appid,co2consent,co2osc,consentext,oscext,co1consentext,co1oscext,co2consentext,co2oscext);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inserting data: " + e.getMessage());
        }
    }

    @GetMapping("/getInPrincData")
    public ResponseEntity<String> getInPrincData(@RequestParam String wiNum, @RequestParam Long slno) {
        log.info("getInPrincData Req  {}  ~ SLNO {} ",wiNum,slno);
        return ResponseEntity.ok(pdfService.generatePDF(wiNum,slno));
    }

    @GetMapping("/getVkycBasicData")
    public CustomerVkycBasicDetailsDTO getVkycBasicData(@RequestParam String data) {
        return customervkycservice.getBasicDetails(data);
    }

    @GetMapping("/testNotification")
    public String testNotif(@RequestParam("solId") String solId)
    {
        notificationService.createNotification(solId,"Entry Submitted","Branch Maker Submitted Successfully","bmlist","Branch","Medium");
        return "okey";
    }


    @GetMapping("/getCustomerData")
    public VKYCDataDto getCustomerData(@RequestParam String wiNum, @RequestParam Long slno, @RequestParam Long applicationId) {
        return customervkycservice.getDetails(wiNum,slno,applicationId);
    }

    @GetMapping("/getStatus")
    public List<VLDsaStatusView> getAllVLDsaStatus(@RequestParam String cmuser) {
        return dsaservice.getAllVLDsaStatus(cmuser);
    }


    @PostMapping("/createBpmWI")
    public ResponseEntity<ResponseDTO> createBpmWI(@RequestBody Map<String, String> requestjson, HttpServletRequest request) {
        try {
            String msg="", status="F";
            Boolean batch=true;
            String winum=requestjson.get("winum");
            String slno=requestjson.get("slno");
            String applicantId=requestjson.get("applicantId");
            String bpmWiNUm=vehicleLoanWIService.createWI(winum,slno,applicantId, CommonUtils.getClientIp(request));
            String a="";
            if(bpmWiNUm!=null && !bpmWiNUm.trim().isEmpty()){
                msg="WI "+bpmWiNUm+" created successfully";
                a = vehicleLoanCifService.updateVLCifBpmWI(Long.parseLong(applicantId), bpmWiNUm, batch);
                if(a.equals("S")){
                    msg+=", Vehicle_loan_cif updated successfully";
                    status="S";
                }else{
                    msg+=", Vehicle_loan_cif NOT updated";
                }
            }else{
                msg="BPM WI creation failed";
            }
            return ResponseEntity.ok(new ResponseDTO(status,msg));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseDTO("ERROR", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @Autowired
    private VehicleLoanVkycService losVkycService;
    @PostMapping("/vkycCallBack")
    public VKYCResponse updateVkyc(@RequestBody String requestBody) {
        try {
            log.info("vkycCallBack Req {} ",requestBody);
            JSONObject jsonObjectRequest = new JSONObject(requestBody);
            String uniqueid = jsonObjectRequest.getString("unique_id");
            if (uniqueid == null || uniqueid.trim().isEmpty()) {
                log.info("vkycCallBack failed. invalid unique id ");
                return new VKYCResponse("failed","Invalid uniqueid for request",uniqueid);
            }
            VehicleLoanVkyc losVkyc = new VehicleLoanVkyc();
            losVkyc.setAadhaarRefKey(jsonObjectRequest.getString("aadhaar_ref_key"));
            losVkyc.setCustName(jsonObjectRequest.getString("name"));
            losVkyc.setVkycGender(jsonObjectRequest.getString("gender"));
            losVkyc.setVkycDob(jsonObjectRequest.getString("dob"));
            losVkyc.setVkycVcipStatus(jsonObjectRequest.getString("vcip_status"));
            losVkyc.setVkycStatusDecr(jsonObjectRequest.getString("status_description"));
            losVkyc.setVkycLatitude(jsonObjectRequest.getString("latitude"));
            losVkyc.setVkycLongitude(jsonObjectRequest.getString("longitude"));
            losVkyc.setVkycPhotoMatch(jsonObjectRequest.getString("photo_match"));
            losVkyc.setVkycFailureReason(jsonObjectRequest.getString("failure_reason"));
            losVkyc.setCustId(jsonObjectRequest.getString("customer_id"));
            losVkyc.setVkycPhoto(jsonObjectRequest.getString("photo"));
            losVkyc.setVkycUniqueId(uniqueid);
            log.info("losVkyc callbaclk request: "+gson.toJson(losVkyc));
            String result = losVkycService.updateVkycDetails(losVkyc, uniqueid);
            if ("Success".equals(result)) {
                log.info("vkycCallBack success  {} ",uniqueid);
                return new VKYCResponse("success","Customer Id Successfully Updated",uniqueid);
            } else {
                log.info("vkycCallBack Failed  {} ",uniqueid);
                return new VKYCResponse("failed","Customer Id Update Failed!",uniqueid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("vkycCallBack failed Exception {} ",e);
            return new VKYCResponse("failed","Internal Server Error",null);
        }
    }

    @PostMapping("/getExperianScoreDsa")
    public DSAExperianReponseDTO getExperianDKData(@RequestBody DSAExperianRequestDTO experianreq) {

        return dsaservice.processDataDSA(experianreq);

    }

    @GetMapping("/pincode")
    public ExperianPincodeMasterDTO getDataFromPincodeDSA(@RequestParam String pincode) {
        Optional<ExperianPincodeMasterDTO> pin = pincodeMasterService.getexperianaddressdata(pincode);
        return pin.orElseGet(ExperianPincodeMasterDTO::new);
    }
    @PostMapping("/itr-callback")
    public ResponseEntity<String> handleITRCallback(@RequestBody ITRCallback itrCallback) {
        itrCallbackService.processITRCallback(itrCallback);
        return ResponseEntity.ok("Callback processed successfully");
    }
}
