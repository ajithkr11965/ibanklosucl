package com.sib.ibanklosucl.service.impl;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.bpm.BPMFileUpload;
import com.sib.ibanklosucl.dto.bpm.BpmRequest;

import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanDetails;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.VehicleLoanTat;
import com.sib.ibanklosucl.service.VehicleLoanQueueDetailsService;
import com.sib.ibanklosucl.service.VlCommonTabService;
import com.sib.ibanklosucl.service.bpmsr.BpmService;
import com.sib.ibanklosucl.service.dashboard.notification.NotificationService;
import com.sib.ibanklosucl.service.integration.Docservice;
import com.sib.ibanklosucl.service.vlsr.*;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BrMakerSaveImpl implements VlCommonTabService {
    @Autowired
    private BOGSaveImpl bogSaveImpl;
    @Autowired
    private VehicleLoanWarnService vehicleLoanWarnService;
    @Autowired
    private VehicleLoanQueueDetailsService vehicleLoanQueueDetailsService;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;

    @Autowired
    private VehicleLoanLockService vehicleLoanLockService;
    @Autowired
    private VehicleLoanTatService loanTatService;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private Docservice docservice;
    @Autowired
    private BpmService bpmService;

    @Autowired
    private VehicleLoanTatService vehicleLoanTatService;
    @Autowired
    private NotificationService notificationService;


    @Override
    public ResponseEntity<?> saveLoan(VehicleLoanDetails vehicleLoanDetails) {
        return null;
    }
    @Override
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO saveMaker(String slno, String winum, String vlowner, String vlownerstatus, String remarks, HttpServletRequest request) throws Exception {

        ResponseDTO responseDTO=vehicleLoanWarnService.sendWarnEmail(Long.valueOf(slno));
        if(!responseDTO.getStatus().equals("S"))
            return responseDTO;


        String fromQueue="",toQueue="", status="";
        VehicleLoanTat vehicleLoanTat=vehicleLoanTatService.getTatBySlno(Long.valueOf(slno));
        String currentQueue=vehicleLoanTat.getQueue();
         switch (currentQueue)  {
             case "BS":
                 fromQueue="BS";
                 toQueue="BC";
                 status="BSCOMPLETE";
                 break;
             case "BM":
                 fromQueue="BM";
                 toQueue="BC";
                 status="BMCOMPLETE";
                 break;
             default:
                 return new ResponseDTO("F", "Response Already Saved!!");

         }
//        RBCPCCheckerSave rbs=new RBCPCCheckerSave();
//        rbs.setSlno(slno);
//        rbs.setWinum(winum);
    //    waiverService.calculateProcessingFee(rbs);
        vehicleLoanQueueDetailsService.createQueueEntry(winum, Long.valueOf(slno),remarks,usd.getPPCNo(),fromQueue,toQueue);
        vehicleLoanMasterService.updateQueue(Long.valueOf(slno), toQueue, status, usd.getPPCNo());
        return brMakerSaves(slno,winum,vlowner,vlownerstatus,toQueue,status,request);

    }

    @Override
    public ResponseDTO saveChecker(RBCPCMakerSave rbs, HttpServletRequest request) throws Exception {
        return null;
    }

    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO brMakerSaves(String slno, String winum, String vlowner, String vlownerstatus, String toQueue,String status,HttpServletRequest request ) throws Exception{
        DocumentRequest documentRequest=new DocumentRequest();
        documentRequest.setCmUser(usd.getPPCNo());
        documentRequest.setSlNo(slno);
        documentRequest.setWiNum(winum);
        String pdf=docservice.getCamPdf(documentRequest);
        TabResponse tb=bpmService.BpmUpload(bpmRequest(winum,pdf,"N","NA","CAM"));
        if(!"S".equalsIgnoreCase(tb.getStatus())){
            return new ResponseDTO(tb.getStatus(), tb.getMsg());
        }
        List<VehicleLoanApplicant> vehicleLoanApplicant=vehicleLoanApplicantService.findBySlnoAndDelFlg(Long.valueOf(slno));
        for(VehicleLoanApplicant vl_app:vehicleLoanApplicant){
            documentRequest.setAppId(String.valueOf(vl_app.getApplicantId()));
            String aof=docservice.getAppForm(documentRequest);
            TabResponse tb1=bpmService.BpmUpload(bpmRequest(winum,aof,"Y",vl_app.getBpmFolderName(),"APPLICATION_FORM"));
            if(!"S".equalsIgnoreCase(tb1.getStatus())){
                return new ResponseDTO(tb1.getStatus(), tb1.getMsg());
            }
        }

        vehicleLoanMasterService.updateVehicleOwner(Long.valueOf(slno), vlownerstatus,vlowner,"A-1");
        vehicleLoanLockService.ReleaseLock(Long.valueOf(slno), usd.getPPCNo());
        loanTatService.updateTat(Long.valueOf(slno),usd.getPPCNo(), winum,"BC" );
        notificationService.createNotification(usd.getSolid(),"Entry Submitted","Branch Maker Submitted Successfully","bclist","Branch","Medium");
        return new ResponseDTO("S","Maker Saved SuccessFully");
    }

    public  BpmRequest bpmRequest(String winum, String pdf, String child, String childName, String docName){
        BPMFileUpload bpmFileUpload = new BPMFileUpload();
        bpmFileUpload.setWI_NAME(winum);
        bpmFileUpload.setCHILD(child);
        bpmFileUpload.setCHILD_FOLDER(childName);
        bpmFileUpload.setSystemIP(usd.getRemoteIP());
        List<DOC_ARRAY> docArrayList = new ArrayList<>();
        DOC_ARRAY docArray = new DOC_ARRAY();
        docArray.setDOC_NAME(docName+ CommonUtils.getCurrentTimestamp());
        docArray.setDOC_EXT("pdf");
        docArray.setDOC_BASE64(pdf);
        docArrayList.add(docArray);
        bpmFileUpload.setDOC_ARRAY(docArrayList);
        BpmRequest bpmRequest = new BpmRequest();
        bpmRequest.setRequest(bpmFileUpload);
        return bpmRequest;
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
    public ResponseDTO saveCRTAmber(String slno, String winum, String declaration, String remarks, HttpServletRequest request) {
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
    public ResponseDTO saveBOG(Long slno, String winum, String remarks, HttpServletRequest request) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO savewism(Long slno, String winum, String remarks, String action, HttpServletRequest request) throws Exception {
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
    @Transactional(rollbackOn = Exception.class)
    public ResponseDTO bmDel(Long slno, HttpServletRequest request) throws Exception {

        VehicleLoanMaster loanMaster = vehicleLoanMasterService.findById(slno);
        if (loanMaster == null) {
            return new ResponseDTO("F","Loan application not found");
        }
        String lockuser=vehicleLoanLockService.Locked(slno);
        if (!(lockuser==null || lockuser.isEmpty())) {
            return new ResponseDTO("F","WI is locked, please release the lock to continue");
        }
        String wiNum= loanMaster.getWiNum();
        String currentQueue = loanMaster.getQueue();
        String newQueue = "NIL";
        loanMaster.setQueue(newQueue);
        loanMaster.setRejDate(new Date());
        loanMaster.setRejUser(usd.getEmployee().getPpcno());
        loanMaster.setRejQueue(currentQueue);
        vehicleLoanMasterService.saveLoan(loanMaster);
        vehicleLoanQueueDetailsService.createQueueEntry(loanMaster.getWiNum(), slno, "BR MAKER REJECT", usd.getEmployee().getPpcno(), currentQueue, newQueue);
        vehicleLoanTatService.updateTat(slno, usd.getEmployee().getPpcno(), wiNum, newQueue);
        vehicleLoanLockService.ReleaseLock(slno, usd.getPPCNo());
        String s="";
        if(loanMaster.getChannel().equals("MARKET DSA PORTAL")||loanMaster.getChannel().equals("DEALER PORTAL")||loanMaster.getChannel().equals("DST PORTAL")){
            ResponseDTO temp = bogSaveImpl.sendSmsEmail(slno,wiNum);
            s = ", " + temp.getMsg();
        }
        return new ResponseDTO("S","The Wi is deleted"+s);
    }

    @Override
    public ResponseDTO performInsFiTrn(Long slno, String winum) throws Exception {
        return null;
    }

}
