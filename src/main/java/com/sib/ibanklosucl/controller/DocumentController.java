package com.sib.ibanklosucl.controller;

import com.sib.ibanklosucl.config.menuaccess.RequiresMenuAccess;

import com.sib.ibanklosucl.dto.*;
import com.sib.ibanklosucl.dto.doc.RepaymentDTO;
import com.sib.ibanklosucl.dto.doc.WaiverDto;
import com.sib.ibanklosucl.dto.subqueue.LockStatusDTO;
import com.sib.ibanklosucl.dto.subqueue.WaiverHistoryDTO;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.service.*;
import com.sib.ibanklosucl.service.eligibility.EligibilityHelperService;
import com.sib.ibanklosucl.service.integration.Docservice;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.utilies.UserSessionData;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

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
@RestController
@RequestMapping("/doc")
@Slf4j
@Tag(name = "Ibanklos application APIs", description = "APIs for Ibanklos")
public class DocumentController {
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanMasterService vlmassr;
    @Autowired
    private VLSaveServiceFactory vlSaveServiceFactory;
    @Autowired
    private  EligibilityHelperService eligibilityHelperService;


    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private Docservice docservice;
    @Autowired
    private CommonServiceFactory commonServiceFactory;

    @Autowired
    private VehicleLoanWaiverService loanWaiverService;
    @PostMapping("/sancStatus")
    @RequiresMenuAccess(menuIds = {"BD"})
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<ResponseDTO> getFinacleDedupeData(@RequestParam("slno") @Validated  Long slno,@RequestParam("action") @Validated String action) {
        VehicleLoanMaster master=vlmassr.findById(slno);
        if(master.getDocMode()!=null && action.equals("Y")){
            return ResponseEntity.ok(new ResponseDTO("F","The WI is Already Submitted for Documentation"));
        }
        master.setSancModRequired(action.equals("Y"));
        vlmassr.saveLoan(master);
        return ResponseEntity.ok(new ResponseDTO("S",""));
    }
    @PostMapping("/updateRepaymentDetails")
    @RequiresMenuAccess(menuIds = {"BD"})
    public ResponseEntity<ResponseDTO> updateRepayment(@RequestBody RepaymentDTO repaymentDTO) {
        VlCommonTabService vl = vlSaveServiceFactory.getTabService("REPAYMENT");
        try {
            ResponseDTO dto=vl.saveRepayment(repaymentDTO);
            return new  ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Some exception",e);
            return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/updateWaiver")
    @RequiresMenuAccess(menuIds = {"BD"})
    public ResponseEntity<ResponseDTO> updateWaiver(@RequestBody WaiverDto waiverDto, HttpServletRequest request) {
        VlCommonTabService vl = vlSaveServiceFactory.getTabService("WAIVER");
        try {
            ResponseDTO dto=vl.saveWaiver(waiverDto,request);
            return new  ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Some exception",e);
            return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/updateWaiverChecker")
/*
    @RequiresMenuAccess(menuIds = {"WAIVE"})
*/
    public ResponseEntity<ResponseDTO> updateWaiverChecker(@RequestBody WaiverDto waiverDto,HttpServletRequest request) {
        VlCommonTabService vl = vlSaveServiceFactory.getTabService("WAIVER");
        String taskType ="";
        if(waiverDto.getRoidto()!=null) {
            taskType ="ROI_WAIVER";
        } else {
            taskType ="CHARGE_WAIVER";
        }
        Long slno = waiverDto.getWiSlno();
        try {
            ResponseDTO dto=vl.updateWaiver(waiverDto,request );
            return new  ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Some exception",e);
            return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        } finally {
            loanWaiverService.releaseSubQueueLocksByTask(slno, usd.getPPCNo(),taskType);
        }
    }
    @PostMapping("/getRoi")
    @RequiresMenuAccess(menuIds = {"BD","WAIVE"})
    public ResponseEntity<?> calculateEmi(@RequestBody WaiverDto.ROIWaiverDto roiWaiverDto) {
        try {
          // BigDecimal modifiedRoi=roiWaiverDto.getOperationalCost().add(roiWaiverDto.getCrp()).add(roiWaiverDto.getEbr()).add(roiWaiverDto.getBaseSpread());
           BigDecimal modifiedRoi=roiWaiverDto.getEbr().add(roiWaiverDto.getBaseSpread());
            BigDecimal modifiedemi=eligibilityHelperService.calculateEmi(modifiedRoi,roiWaiverDto.getSancAmt(),roiWaiverDto.getSanctenor());
            roiWaiverDto.setRevisedRoi(modifiedRoi);
            roiWaiverDto.setRevisedEmi(modifiedemi);
            roiWaiverDto.setStatus("S");
            return new  ResponseEntity<>(roiWaiverDto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Some exception",e);
            return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/getRevEmiBogAssets")
    @RequiresMenuAccess(menuIds = {"ACOPN"})
    public ResponseEntity<?> calculateEmi(@RequestBody WaiverDto.BOGAssetRevisedDto bogAssetsRevisedDto) {
        try {

            String result=eligibilityHelperService.checkLoanTenor(bogAssetsRevisedDto.getRevisedTenor(),bogAssetsRevisedDto.getSlno());
            if(!result.equals("S")){
                result=result.split("\\|")[1];
                bogAssetsRevisedDto.setStatus("F");
                bogAssetsRevisedDto.setMsg(result);
                return new  ResponseEntity<>(bogAssetsRevisedDto, HttpStatus.OK);
            }
            VehicleLoanApplicant vehicleLoanApplicant = vlmassr.findById(Long.parseLong(bogAssetsRevisedDto.getSlno())).getApplicants().stream().filter(program -> !"NONE".equals(program.getVlProgram())).toList().get(0);
            Boolean validAmount =eligibilityHelperService.validateLoanAmount2(bogAssetsRevisedDto.getWiNum(),Long.parseLong(bogAssetsRevisedDto.getSlno()),vehicleLoanApplicant.getVlProgram().getLoanProgram(),bogAssetsRevisedDto.getRevisedAmount().toString(),"ALL");
            if(!validAmount){
                bogAssetsRevisedDto.setStatus("F");
                bogAssetsRevisedDto.setMsg("Revised sanction amount is invalid");
                return new  ResponseEntity<>(bogAssetsRevisedDto, HttpStatus.OK);
            }

            BigDecimal modifiedemi=eligibilityHelperService.calculateEmi(bogAssetsRevisedDto.getRevisedRoi(),bogAssetsRevisedDto.getRevisedAmount(),bogAssetsRevisedDto.getRevisedTenor());
            bogAssetsRevisedDto.setRevisedEmi(modifiedemi);
            bogAssetsRevisedDto.setStatus("S");
            return new  ResponseEntity<>(bogAssetsRevisedDto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Some exception",e);
            return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/checkWaiverLocks")
    public ResponseEntity<LockStatusDTO> checkWaiverLocks(@RequestParam("wiNum") String wiNum) {
        LockStatusDTO lockStatus = loanWaiverService.checkWaiverLocks(wiNum, usd.getPPCNo());
        return new ResponseEntity<>(lockStatus, HttpStatus.OK);
    }
     @PostMapping("/waiver-history")
    public ResponseEntity<?> getWaiverHistory(@RequestParam("slno") Long slno,@RequestParam("waiverType") String waiverType) {
        try {
            List<WaiverHistoryDTO> history = loanWaiverService.getWaiverHistory(slno, waiverType);
            return new ResponseEntity<>(history, HttpStatus.OK);
        } catch (Exception e) {
             return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("/updateLegality")
    @RequiresMenuAccess(menuIds = {"BD"})
    public ResponseEntity<ResponseDTO> updateWaiver(@RequestBody DocumentRequest requestDto) {
        VlCommonTabService vl = vlSaveServiceFactory.getTabService("DOC");
        try {
            ResponseDTO dto=vl.saveDoc(requestDto);
            return new  ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/getSancLetter")
    @RequiresMenuAccess(menuIds = {"BD"})
    public ResponseEntity<String> getSancLetter(@RequestBody DocumentRequest requestDto) {
        try {
            requestDto.setCmUser(usd.getPPCNo());
            return new  ResponseEntity<>(docservice.getSanctionPdf(requestDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("/manDocuploadsave")
    @RequiresMenuAccess(menuIds = {"BD"})
    public ResponseDTO handleFileUpload(@ModelAttribute FileUploadForm form) {
        form.setReqip(usd.getRemoteIP());
        form.setCmUser(usd.getPPCNo());
        CommonService commonService = commonServiceFactory.getService("BMUPLOAD");
        return commonService.processDataDoc(form);

    }


    @PostMapping("/createSanModRequest")
    @RequiresMenuAccess(menuIds = {"ACOPN"})
    public ResponseEntity<ResponseDTO> createSanModRequest(@RequestBody WaiverDto waiverDto, HttpServletRequest request) {
        VlCommonTabService vl = vlSaveServiceFactory.getTabService("WAIVER");
        try {
            ResponseDTO dto=vl.saveWaiver(waiverDto,request);
            return new  ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Some exception",e);
            return new ResponseEntity<>(new ResponseDTO("F", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


}
