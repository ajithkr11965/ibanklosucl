package com.sib.ibanklosucl.service;
import com.sib.ibanklosucl.dto.CIFviewRequest;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.dto.TabResponse;
import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.EligibilityDetailsRepository;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.VehicleLoanKycRepository;
import com.sib.ibanklosucl.repository.VehicleLoanMasterRepository;
import com.sib.ibanklosucl.service.esbsr.CIFViewService;
import com.sib.ibanklosucl.service.integration.SMSEmailService;
import com.sib.ibanklosucl.service.integration.VehicleLoanBREService;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service
@Slf4j
public class CRTSaveService {
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private MisrctService misrctService;
    @Autowired
    private VehicleLoanAmberService vlamberservice;

    @Autowired
    private VehicleLoanAllotmentService allotmentService;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanQueueDetailsService queueDetailsService;
    @Autowired
    private VehicleLoanMasterRepository repository;
    @Autowired
    private VehicleLoanLockService vehicleLoanLockService;
    @Autowired
    private VehicleLoanTatService loanTatService;
    @Autowired
    private CIFViewService cfview;
    @Autowired
    VehicleLoanWarnService vlwarn;
    @Autowired
    private CustomerDetailsService cd;

    @Autowired
    private VehicleLoanApplicantService vlapplicant;
    @Autowired
    private VehicleLoanBREService vlbre;
    @Autowired
    private VehicleLoanKycRepository kycrepo;
    @Autowired
    private EligibilityDetailsRepository eligibilityDetailsRepository;
    @Autowired
    private FetchRepository fetchRepository;
    @Value("${helpdeskNo}")
    private String helpdeskNo;

    @Value("${app.dev-mode:true}")
    private boolean devMode;

    @Autowired
    private SMSEmailService smsEmailService;

    @SneakyThrows
    public String crtFinalSave(String wiNum, Long slno, String remarks, String action, HttpServletRequest request) {
        VehicleLoanMaster loanMaster = repository.findBySlnoWithApplicants(slno);
        // Find the highest existing amber code for this loan
        int ambercount = 0;
        int redcount = 0;
        if (remarks.equals("")) {
            return "Remarks is mandatory.";
        } else if (action.equals("approve")) {

            int totalwarnings = 0;
            List<VehicleLoanWarn> vehicleLoanWarns = vlwarn.getActiveAndNotDeletedVehicleLoanWarns(slno);
            for (VehicleLoanWarn warn : vehicleLoanWarns) {
                Map<String, Object> map = new HashMap<>();
                map.put("vehicleLoanWarn", warn);
                String warncode = warn.getWarnCode();
                Long appid = warn.getApplicantId();
                CustomerDetails custdetail = cd.findByAppId(appid);
                String custid = custdetail.getCustId();
                String wino = warn.getWiNum();
                CIFviewRequest requestjson = new CIFviewRequest();
                requestjson.setSlno(String.valueOf(slno));
                requestjson.setWinum(wino);
                requestjson.setAppid(String.valueOf(appid));
                requestjson.setCustID(custid);
                VehicleLoanKyc kycdetails = kycrepo.findByApplicantIdAndDelFlg(appid,"N");
                String visa_oci_type = kycdetails.getVisaOciType();
                TabResponse respjson = cfview.getCustData(requestjson, request);
                String currentfinaclevalue = "";
                if (respjson.getStatus().equals("S")) {
                    JSONObject data = new JSONObject(respjson.getMsg());
                    if (warncode.equals("WAR001")) {
                        currentfinaclevalue = data.has("custDob") && !data.isNull("custDob") ?data.getString("custDob"):"";
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                        }
                    }
                    if (warncode.equals("WAR002")||warncode.equals("WAR003")) {
                        currentfinaclevalue = data.has("pan") && !data.isNull("pan") ? data.getString("pan") : "";
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                        }
                    }
                    if (warncode.equals("WAR004") || warncode.equals("WAR005")) {
                        currentfinaclevalue = data.has("aadhaarRefNo") && !data.isNull("aadhaarRefNo") ? data.getString("aadhaarRefNo") : "";
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                                totalwarnings = totalwarnings + 1;
                        }
                    }
                    if (warncode.equals("WAR006") || warncode.equals("WAR007")) {
                        currentfinaclevalue = data.has("passport") && !data.isNull("passport") ? data.getString("passport") : "";
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                        }
                    }
                    if (warncode.equals("WAR008")) {
                        currentfinaclevalue = data.has("residentialStatus") && !data.isNull("residentialStatus") ? data.getString("residentialStatus") : "";
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                        }
                    }
                    if (warncode.equals("WAR009")) {
                        currentfinaclevalue = data.has("minorFlag") && !data.isNull("minorFlag") ? data.getString("minorFlag") : "";
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                        }
                    }
                    if (warncode.equals("WAR0010") || warncode.equals("WAR0011")) {
                        if(visa_oci_type.equals("V")){
                            currentfinaclevalue = data.has("visa") && !data.isNull("visa") ? data.getString("visa") : "";
                        }else if(visa_oci_type.equals("O")){
                            currentfinaclevalue = data.has("ociCard") && !data.isNull("ociCard") ? data.getString("ociCard") : "";
                        }else if(visa_oci_type.equals("C")) {
                            currentfinaclevalue = data.has("cdnNo") && !data.isNull("cdnNo") ? data.getString("cdnNo") : "";
                        }
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                        }
                    }
                    if (warncode.equals("WAR0012") || warncode.equals("WAR0014") || warncode.equals("WAR0015") || warncode.equals("WAR0016") || warncode.equals("WAR0020")) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                        totalwarnings = totalwarnings + 1;
                    }
                    if (warncode.equals("WAR0013")) {
                        currentfinaclevalue = data.has("customerName") && !data.isNull("customerName") ? data.getString("customerName") : "";
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                        }
                    }
                    if (warncode.equals("WAR0017") || warncode.equals("WAR0018")) {
                        currentfinaclevalue = data.has("cellPhone") && !data.isNull("cellPhone") ? data.getString("cellPhone") : "";
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                        }
                    }
                    if (warncode.equals("WAR0019")) {
                        currentfinaclevalue = data.has("commEmail") && !data.isNull("commEmail") ? data.getString("commEmail") : "";
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                        }
                    }
                }
            }

            if (totalwarnings > 0) {
                return "Customer Entered details and finacle details mismatch, Kindly check warning section!";
            }

            String racecolor = vlamberservice.getamberdatacolor(wiNum, slno, "AMB006");
            if (racecolor.equals("failure")) {
                return "Race score is mandatory";
            } else {
                if (racecolor.equals("green")) {

                } else if (racecolor.equals("amber")) {
                    ambercount = ambercount + 1;
                } else if (racecolor.equals("red")) {
                    redcount = redcount + 1;
                }
            }
            if (redcount == 0) {
                String fcvcolor = vlamberservice.getamberdatacolor(wiNum, slno, "AMB009");
                if (fcvcolor.equals("failure")) {
                    return "FCV data is mandatory";
                } else {
                    if (fcvcolor.equals("green")) {

                    } else if (fcvcolor.equals("amber")) {
                        ambercount = ambercount + 1;
                    } else if (fcvcolor.equals("red")) {
                        redcount = redcount + 1;
                    }
                }
                String cpvcolor = vlamberservice.getamberdatacolor(wiNum, slno, "AMB010");
                if (cpvcolor.equals("failure")) {
                    return "CPV data is mandatory";
                } else {
                    if (cpvcolor.equals("green")) {

                    } else if (cpvcolor.equals("amber")) {
                        ambercount = ambercount + 1;
                    } else if (cpvcolor.equals("red")) {
                        redcount = redcount + 1;
                    }
                }
                String cfrcolor = vlamberservice.getamberdatacolor(wiNum, slno, "AMB011");
                if (cfrcolor.equals("failure")) {
                    return "CRF data is mandatory";
                } else {
                    if (cfrcolor.equals("green")) {

                    } else if (cfrcolor.equals("amber")) {
                        ambercount = ambercount + 1;
                    } else if (cfrcolor.equals("red")) {
                        redcount = redcount + 1;
                    }
                }
                if (redcount == 0) {
                    String queue = "", stp = "", status = "";
                    if (ambercount > 0) {
                        status = "CRTCOMPLETE";
                        stp = "NONSTP";
                        queue = "";
                        if (allotmentService.hasActiveAllotment(wiNum, slno)) {
                            queue = "RM";
                        } else {
                            queue = "RA";
                        }
                    } else {
                        status = "CRTCOMPLETE";
                        stp = "STP";
                        queue = "BD";
                    }


                    queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), loanMaster.getQueue(), queue);

                    vehicleLoanLockService.ReleaseLock(slno, usd.getPPCNo());

                    loanTatService.updateTat(Long.valueOf(slno), usd.getPPCNo(), wiNum, queue);
                    // Update VehicleLoanMaster
                    loanMaster.setQueue(queue);
                    loanMaster.setQueueDate(new Date());
                    loanMaster.setCrtCmUser(usd.getEmployee().getPpcno());
                    loanMaster.setCrtCmDate(new Date());
                    loanMaster.setStatus(status);
                    loanMaster.setStp(stp);
                    if(!queue.equals("BD")){
                        loanMaster.setSanDate(null);
                        loanMaster.setSanFlg("N");
                        loanMaster.setSanUser(null);
                    }
                    repository.save(loanMaster);


                    Optional<EligibilityDetails> eligibilityDetailsOpt = eligibilityDetailsRepository.findByWiNumAndSlno(wiNum, slno);
                    VehicleLoanMaster vmas = vehicleLoanMasterService.findById(slno);
                    List<VehicleLoanApplicant> vlapp = vmas.getApplicants().stream().filter(t -> t.getDelFlg().equals("N") && t.getApplicantType().equalsIgnoreCase("A")).toList();
                    for (VehicleLoanApplicant vp : vlapp) {
                        String mobile = vp.getBasicapplicants().getMobileCntryCode() + vp.getBasicapplicants().getMobileNo();
                        String email = vp.getBasicapplicants().getEmailId();
                        String salutation = vp.getBasicapplicants().getSalutation();
                        salutation = misrctService.getByCodeValue("TIT", salutation).getCodedesc();
                        String custName = vp.getApplName();
                        String sancAmt = eligibilityDetailsOpt.get().getSancAmountRecommended().toString();
                        String brName = fetchRepository.getSolName(vmas.getSolId());
                        String helpdesk = helpdeskNo;
                        SMSEmailDTO smdDto = new SMSEmailDTO();
                        smdDto.setAlertId("LOSAPPR");
                        smdDto.setSlno(slno);
                        smdDto.setWiNum(wiNum);
                        smdDto.setSentUser(usd.getPPCNo());
                        smdDto.setReqType("S");
                        if(!devMode) {
                            smdDto.setMobile(mobile);
                        }
                        else{
                            smdDto.setMobile("918547016003");
                        }
                        //smdDto.setMobile(mobile);
                        smdDto.setMessage(salutation + "|" + custName + "|Rs." + sancAmt + "/-|" + smdDto.getWiNum() + "|" + brName + "|" + helpdesk+"|");
                        ResponseDTO sms = smsEmailService.insertSMSEmail(smdDto);
                        /*if (sms.getStatus().equalsIgnoreCase("F")) return sms;*/
                        SMSEmailDTO emailDTO = new SMSEmailDTO();
                        emailDTO.setSlno(slno);
                        emailDTO.setWiNum(wiNum);
                        emailDTO.setSentUser(usd.getPPCNo());
                        emailDTO.setAlertId("LOSAPPR");
                        emailDTO.setReqType("E");
                        emailDTO.setEmailFrom("sibmailer@sib.bank.in");
                        if(!devMode) {
                            emailDTO.setEmailTo(email);
                        }
                        else {
                            emailDTO.setEmailTo("antonyraj@sib.bank.in");
                            //     emailDTO.setEmailTo("vigneshpadmanabhan@sib.bank.in");
                        }
                        emailDTO.setEmailBody("Congratulations! Your Vehicle loan of request of Rs." + sancAmt + "/- vide Appl No-" + wiNum + " has been approved. Kindly contact the branch " + brName + " to complete the formalities");
                        emailDTO.setCustName(salutation + " " + custName);
                        emailDTO.setEmailSubject("Your Vehicle Loan Is Approved – South Indian Bank");
                        ResponseDTO email_ = smsEmailService.insertSMSEmail(emailDTO);
                        /*if (email_.getStatus().equalsIgnoreCase("F"))
                            return new ResponseDTO("F", "SMS Sent Successfully ,Email  Failed");*/


                    }


                    return "success~" + queue;
                } else {
                     return "FCV/CPV/CFR is not in eligibile range, Kindly proceed with Send Back or Reject Option";
                }
            } else {
                return "Race score is not in eligibile range";
            }
        } else if (action.equals("sendback")) {
            String queue = "BS";
            String status = "CRTSENDBACK";

            queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), loanMaster.getQueue(), queue);

            vehicleLoanLockService.ReleaseLock(slno, usd.getPPCNo());

            loanTatService.updateTat(Long.valueOf(slno), usd.getPPCNo(), wiNum, queue);
            // Update VehicleLoanMaster
            loanMaster.setQueue(queue);
            loanMaster.setQueueDate(new Date());
            loanMaster.setCrtCmUser(usd.getEmployee().getPpcno());
            loanMaster.setCrtCmDate(new Date());
            loanMaster.setStatus(status);
            loanMaster.setSanDate(null);
            loanMaster.setSanFlg("N");
            loanMaster.setSanUser(null);
            repository.save(loanMaster);
            return "success~" + queue;

        } else if (action.equals("reject")) {
            String queue = "NIL";
            String status = "CRTREJECT";

            queueDetailsService.createQueueEntry(wiNum, slno, remarks, usd.getEmployee().getPpcno(), loanMaster.getQueue(), queue);

            vehicleLoanLockService.ReleaseLock(slno, usd.getPPCNo());

            loanTatService.updateTat(Long.valueOf(slno), usd.getPPCNo(), wiNum, queue);
            // Update VehicleLoanMaster
            loanMaster.setQueue(queue);
            loanMaster.setQueueDate(new Date());
            loanMaster.setCrtCmUser(usd.getEmployee().getPpcno());
            loanMaster.setCrtCmDate(new Date());
            loanMaster.setRejUser(usd.getEmployee().getPpcno());
            loanMaster.setRejDate(new Date());
            loanMaster.setRejFlg("Y");
            loanMaster.setRejQueue("CS");
            loanMaster.setStatus(status);
            repository.save(loanMaster);


            VehicleLoanMaster vmas=vehicleLoanMasterService.findById(slno);
            List<VehicleLoanApplicant> vlapp =vmas.getApplicants().stream().filter(t->t.getDelFlg().equals("N") && t.getApplicantType().equalsIgnoreCase("A")).toList();
            for (VehicleLoanApplicant vp:vlapp){
                String mobile=vp.getBasicapplicants().getMobileCntryCode()+vp.getBasicapplicants().getMobileNo();
                String email=vp.getBasicapplicants().getEmailId();
                String salutation=vp.getBasicapplicants().getSalutation();
                salutation=misrctService.getByCodeValue("TIT",salutation).getCodedesc();
                String custName=vp.getApplName();
                String brName=fetchRepository.getSolName(vmas.getSolId());
                String helpdesk=helpdeskNo;
                SMSEmailDTO smdDto = new SMSEmailDTO();
                smdDto.setAlertId("LOSDECN");
                smdDto.setSlno(slno);
                smdDto.setWiNum(wiNum);
                smdDto.setSentUser(usd.getPPCNo());
                smdDto.setReqType("S");
                if(!devMode) {
                    smdDto.setMobile(mobile);
                }
                else{
                    smdDto.setMobile("918547016003");
                }
                smdDto.setMessage(salutation+"|"+custName  +"|"+ smdDto.getWiNum() +  "|" + helpdesk+"|");
                ResponseDTO sms = smsEmailService.insertSMSEmail(smdDto);
                /*if (sms.getStatus().equalsIgnoreCase("F")) return sms;*/
                SMSEmailDTO emailDTO = new SMSEmailDTO();
                emailDTO.setSlno(slno);
                emailDTO.setWiNum(wiNum);
                emailDTO.setSentUser(usd.getPPCNo());
                emailDTO.setAlertId("LOSDECN");
                emailDTO.setReqType("E");
                emailDTO.setEmailFrom("sibmailer@sib.bank.in");
                if(!devMode) {
                    emailDTO.setEmailTo(email);
                }
                else {
                    emailDTO.setEmailTo("antonyraj@sib.bank.in");
                    //  emailDTO.setEmailTo("vigneshpadmanabhan@sib.bank.in");
                }
                emailDTO.setEmailBody("We regret to inform you that your application "+wiNum+" for vehicle loan has been declined. For further details, please contact our customer care  "+helpdesk+" or visit the nearest branch. We appreciate your interest in SIB and look forward to serving you in future");
                emailDTO.setCustName(salutation+" "+custName);
                emailDTO.setEmailSubject("Vehicle Loan Application Rejected – South Indian Bank");
                ResponseDTO email_ = smsEmailService.insertSMSEmail(emailDTO);
                /*if (email_.getStatus().equalsIgnoreCase("F"))
                    return new ResponseDTO("F", "SMS Sent Successfully ,Email  Failed");*/


            }

            return "success~" + queue;
        } else {
            return "failure - invalid option";
        }
    }

    @SneakyThrows
    public String crtRaceScoreFinalSave(String requestData, HttpServletRequest request) {
        JSONObject reqdata = new JSONObject(requestData);
        String winum = reqdata.getString("wiNum");
        String slno = reqdata.getString("slno");
        List<VehicleLoanApplicant> vlapp = vlapplicant.findBySlnoAndDelFlg(Long.parseLong(slno));
        String color = "";
        org.json.JSONObject racejson = new org.json.JSONObject();
        JSONArray tenorjsonarray = new JSONArray();
        List<String> amberData = new ArrayList();
        List<String> mainamberData = new ArrayList();
        org.json.JSONObject returnJson = new org.json.JSONObject();
        for (VehicleLoanApplicant applicant : vlapp) {
            String racescore = applicant.getRaceScore();

            if (racescore == null) {
                return "Run race score for all applicant before saving";
            } else {
                if (Long.parseLong(racescore) > 750) {
                    color = "green";
                } else {
                    color = "amber";
                }
                org.json.JSONObject tenordata = new org.json.JSONObject();
                tenordata.put("currentValue", racescore);
                tenordata.put("color", color);
                tenordata.put("masterValue", "750");
                tenordata.put("applicantId", applicant.getApplicantId());
                tenordata.put("applicantType", applicant.getApplicantType());
                tenordata.put("applicantName", applicant.getApplName());
                amberData.add(tenordata.getString("color"));
                tenorjsonarray.put(tenordata);
            }


        }
        racejson.put("breCode", "AMB006");
        racejson.put("breDesc", "Race score is outside STP Range");
        racejson.put("color", getMainColor(amberData));
        racejson.put("generic", "N");
        racejson.put("breSub", tenorjsonarray);
        mainamberData.add(getMainColor(amberData));
        amberData.clear();

        returnJson.put("AMB006", racejson);

        org.json.JSONObject resp = new org.json.JSONObject();
        resp.put("status", "SUCCESS");
        resp.put("eligibilityFlag", "green");
        resp.put("breFlag", getMainColor(mainamberData));
        resp.put("breData", returnJson);
        vlbre.processAmberData(resp.toString(),winum,slno, CommonUtils.getClientIp(request));
        return resp.toString();
    }

    public String getMainColor(List<String> colordata) throws Exception {
        return colordata.contains("amber") ? "amber" : "green";
    }
}