package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.VehicleLoanKycRepository;
import com.sib.ibanklosucl.service.VehicleLoanAmberService;
import com.sib.ibanklosucl.service.VehicleLoanDecisionService;
import com.sib.ibanklosucl.service.VehicleLoanQueueDetailsService;
import com.sib.ibanklosucl.service.VlCommonTabService;
import com.sib.ibanklosucl.service.esbsr.CIFViewService;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CRTAmberSaveImpl implements VlCommonTabService {

    @Autowired
    public BOGSaveImpl bogSaveImpl;
    @Autowired
    private VehicleLoanQueueDetailsService vehicleLoanQueueDetailsService;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;

    @Autowired
    private VehicleLoanLockService vehicleLoanLockService;
    @Autowired
    private VehicleLoanTatService loanTatService;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanAmberService loanAmberService;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private MisrctService misrctService;
    @Autowired
    private VehicleLoanDecisionService vehicleLoanDecisionService;


    @Autowired
    VehicleLoanWarnService vlwarn;
    @Autowired
    private CustomerDetailsService cd;
    @Autowired
    private CIFViewService cfview;
    @Autowired
    private VehicleLoanKycRepository kycrepo;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<?> saveLoan(VehicleLoanDetails vehicleLoanDetails) {



//        vehicleLoanQueueDetailsService.createQueueEntry(winum, Long.valueOf(slno),remarks,usd.getPPCNo(),fromQueue,toQueue);
//        vehicleLoanMasterService.updateQueue(Long.valueOf(slno), toQueue, status, usd.getPPCNo());
//        vehicleLoanMasterService.updateVehicleOwner(Long.valueOf(slno), vlownerstatus,vlowner,"A-1");
//        vehicleLoanLockService.ReleaseLock(Long.valueOf(slno), usd.getPPCNo());
//        loanTatService.updateTat(Long.valueOf(slno),usd.getPPCNo(), winum,"BC",request );
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
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveCRTAmber(String slno, String winum,  String remarks, String decision,HttpServletRequest request) throws Exception {

        String fromQueue="CA",toQueue="", status="", message="";
        if(decision.equalsIgnoreCase("A")){//approve
            //add block to check warning conditions and return false if any medium level is not resolved.

            int totalwarnings=checkWarnCurrentStatus(Long.parseLong(slno), request);
            if (totalwarnings > 0) {
                return new ResponseDTO("F","Customer Entered details and finacle details mismatch, Kindly check warning section!");
            }


            toQueue = "BD";
            status="CACOMPLETE";
            message="Successfully forwarded to branch documentation queue";
        }else if(decision.equalsIgnoreCase("R")){//reject
            toQueue = "NIL";
            status="CAREJECT";
            message="The WI is rejected";
        }else if(decision.equalsIgnoreCase("S")){//send back
            toQueue = "BS";
            status="CASENDBACK";

            message="The work item is sent back to branch ";
        }

        vehicleLoanQueueDetailsService.createQueueEntry(winum, Long.valueOf(slno),remarks,usd.getPPCNo(),fromQueue,toQueue);
        vehicleLoanMasterService.updateCrtAmberQueue(Long.valueOf(slno), toQueue, status, usd.getPPCNo());
        vehicleLoanLockService.ReleaseLock(Long.valueOf(slno), usd.getPPCNo());
        loanTatService.updateTat(Long.valueOf(slno),usd.getPPCNo(), winum,toQueue );
        String s="";
        if(decision.equalsIgnoreCase("R")){
             ResponseDTO responseDTO= bogSaveImpl.sendSmsEmail(Long.valueOf(slno),winum);
             s=", "+responseDTO.getMsg();
        }
        return new ResponseDTO("S",message+s);
    }


    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveWIRecall(String wiNum, String remarks,HttpServletRequest request) throws Exception {
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findByWiNum(wiNum);
        String queue = vehicleLoanMaster.getQueue();
        String toQueue="RM";
        Long slno = vehicleLoanMaster.getSlno();
        String a = validateWIRecall(slno);
        ResponseDTO responseDTO=new ResponseDTO();
        if(a.equals("S")) {
            vehicleLoanQueueDetailsService.createQueueEntry(wiNum, Long.valueOf(slno), remarks, usd.getPPCNo(), queue, toQueue);
            vehicleLoanMasterService.updateRecallQueue(slno);
            loanTatService.updateTat(Long.valueOf(slno), usd.getPPCNo(), wiNum, toQueue);
            responseDTO.setStatus("S");
            responseDTO.setMsg("WI recalled successfully");
        }else{
            responseDTO.setStatus("F");
            responseDTO.setMsg(a);
        }
        return responseDTO;
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
    public ResponseDTO saveBOG(Long slno, String winum, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO savewism(Long slno, String winum, String remarks, String action, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO updateWaiver(WaiverDto waiverDto,HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO saveDoc(DocumentRequest documentRequest) throws Exception {
        return null;
    }


    public String validateWIRecall(Long slno){
        String returnVal="F";
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findById(slno);
        String lockuser=vehicleLoanLockService.Locked(slno);
        String queue=vehicleLoanMaster.getQueue();
        String activeFlg=vehicleLoanMaster.getActiveFlg();

        if(queue.startsWith("RCL") && activeFlg.equals("Y") && (lockuser==null || lockuser.isEmpty())){
            returnVal="S";
        }else{//failure case, set appropriate error message
            if(!queue.startsWith("RCL")){
                returnVal = "WI is not found in RBCPC Checker queue";
            } else if (!(lockuser==null || lockuser.isEmpty())) {
                returnVal = "WI is locked, please release the lock to continue";
            }else if(!activeFlg.equals("Y")){
                returnVal = "WI is not active";
            }else{
                returnVal = "WI not found";
            }
        }
        return returnVal;
    }

    public int checkWarnCurrentStatus(Long slno, HttpServletRequest request) throws JSONException {
        int totalwarnings = 0;
        List<VehicleLoanWarn> vehicleLoanWarns = vlwarn.getActiveAndNotDeletedVehicleLoanWarns(slno);
        for (VehicleLoanWarn warn : vehicleLoanWarns) {
            Map<String, Object> map = new HashMap<>();
            map.put("vehicleLoanWarn", warn);
            String warncode = warn.getWarnCode();
            Long appid = warn.getApplicantId();
            VehicleLoanKyc kycdetails = kycrepo.findByApplicantIdAndDelFlg(appid,"N");
            String visa_oci_type = kycdetails.getVisaOciType();
            CustomerDetails custdetail = cd.findByAppId(appid);
            String custid = custdetail.getCustId();
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
                    currentfinaclevalue = data.getString("custDob");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
                if (warncode.equals("WAR002")) {
                    currentfinaclevalue = data.getString("pan");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
                if (warncode.equals("WAR003")) {
                    currentfinaclevalue = data.getString("pan");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
//                if (warncode.equals("WAR004")) {
//                    currentfinaclevalue = data.getString("aadhaarRefNo");
//                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
//                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
//                            totalwarnings = totalwarnings + 1;
//                    }
//                }
//                if (warncode.equals("WAR005")) {
//                    currentfinaclevalue = data.getString("aadhaarRefNo");
//                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
//                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
//                            totalwarnings = totalwarnings + 1;
//                    }
//                }
                if (warncode.equals("WAR004") || warncode.equals("WAR005")) {
                        currentfinaclevalue = data.has("aadhaarRefNo") && !data.isNull("aadhaarRefNo") ? data.getString("aadhaarRefNo") : "";
                        if (!currentfinaclevalue.equals(warn.getWiValue())) {
                            if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                                totalwarnings = totalwarnings + 1;
                        }
                    }
                if (warncode.equals("WAR006")) {
                    currentfinaclevalue = data.getString("passport");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
                if (warncode.equals("WAR007")) {
                    currentfinaclevalue = data.getString("passport");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
                if (warncode.equals("WAR008")) {
                    currentfinaclevalue = data.getString("residentialStatus");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
                if (warncode.equals("WAR009")) {
                    currentfinaclevalue = data.getString("minorFlag");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }

                /*
                if (warncode.equals("WAR0010")) {
                    currentfinaclevalue = data.getString("visa");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
                if (warncode.equals("WAR0011")) {
                    currentfinaclevalue = data.getString("visa");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }

                 */

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
                if (warncode.equals("WAR0012")) {
                    if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                        totalwarnings = totalwarnings + 1;
                }
                if (warncode.equals("WAR0013")) {
                    currentfinaclevalue = data.getString("customerName");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
                if (warncode.equals("WAR0014")) {
                    if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                        totalwarnings = totalwarnings + 1;
                }
                if (warncode.equals("WAR0015")) {
                    if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                        totalwarnings = totalwarnings + 1;
                }
                if (warncode.equals("WAR0016")) {
                    if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                        totalwarnings = totalwarnings + 1;
                }
                if (warncode.equals("WAR0017")) {
                    currentfinaclevalue = data.getString("cellPhone");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
                if (warncode.equals("WAR0018")) {
                    currentfinaclevalue = data.getString("cellPhone");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
                if (warncode.equals("WAR0019")) {
                    currentfinaclevalue = data.getString("commEmail");
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
                if (warncode.equals("WAR0020")) {
                    if (!currentfinaclevalue.equals(warn.getWiValue())) {
                        if(warn.getSeverity().equals("High") || warn.getSeverity().equals("Medium"))
                            totalwarnings = totalwarnings + 1;
                    }
                }
            }
        }

        return totalwarnings;

    }



    public ResponseDTO wiEnquiryFetch(String wiNum) throws Exception {
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findByWiNum(wiNum);
        return null;
    }
    @Override
    public ResponseDTO acctlabelsave(AcctLabelDTO acctLabelDTO, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO performAccOpening(Long slno, String winum,  HttpServletRequest request) throws Exception {
        return null;
    }
    @Override
    public ResponseDTO disbursement(Long slno, String winum,  HttpServletRequest request) throws Exception {
        return null;
    }
    @Override
    public ResponseDTO performDisbStatusEnquiry(Long slno, String winum,  HttpServletRequest request) throws Exception {
        return null;
    }
    @Override
    public ResponseDTO performNeft(Long slno, String winum, String beneficiaryType, String dneftamt,String mneftamt,String accnum, String ifsc,
                                   String accname,  String manufMobile,String disbType, String add1, String add2, String add3, HttpServletRequest request) throws Exception {
        return null;
    }
    @Override
    public ResponseDTO bmDel(Long slno, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO performInsFiTrn(Long slno, String winum) throws Exception {
        return null;
    }
}
