package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.model.integrations.ITRCallback;
import com.sib.ibanklosucl.repository.integations.ITRCallbackRepository;
import com.sib.ibanklosucl.service.integration.SMSEmailService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ITRCallbackService {

    @Autowired
    private ITRCallbackRepository itrCallbackRepository;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;

    @Autowired
    private SMSEmailService mailService;
         @Value("${app.dev-mode:true}")
    private boolean devMode;

    public ResponseDTO processITRCallback(ITRCallback itrCallback) {
        String code = "", desc = "";
        itrCallbackRepository.save(itrCallback);
        String input = itrCallback.getClientTransactionId();
        String[] parts = input.split("-");
        try {

            if (parts.length >= 2) {
                String wiNum = parts[0].toUpperCase(); // Converting to uppercase as requested
                String slno = parts[1];
                VehicleLoanMaster loanMaster = vehicleLoanMasterService.findById(Long.valueOf(slno));
                if (loanMaster == null) {
                    throw new RuntimeException("Loan application not found");
                }
                String sol_id = loanMaster.getSolId();


                // Check if the status is COMPLETED and call mailService.insertSMSEmail()
                if ("COMPLETED".equalsIgnoreCase(itrCallback.getStatus())) {
                    String content = "ITR Processing completed. You may fetch the processed ITR details using the Fetch status Button.\n";
                    SMSEmailDTO emailDTO = new SMSEmailDTO();
                    emailDTO.setSlno(Long.valueOf(1000));
                    emailDTO.setWiNum("dummy");
                    emailDTO.setSentUser("system");
                    emailDTO.setAlertId("ITRALERT -" + itrCallback.getClientTransactionId());
                    emailDTO.setReqType("E");
                    emailDTO.setEmailFrom("sibmailer@sib.bank.in");
                    if (!devMode) {
                        emailDTO.setEmailTo(String.format("br%s@sib.bank.in",sol_id));
                    } else {
                        emailDTO.setEmailTo("infobanksib@gmail.com");
                    }
                    emailDTO.setEmailBody(content);
                    emailDTO.setCustName("test applicant");
                    emailDTO.setEmailSubject("ITR Details upload for Application " + itrCallback.getClientTransactionId() + " – South Indian Bank");
                    ResponseDTO email_ = mailService.insertSMSEmail(emailDTO);
                    if (email_.getStatus().equalsIgnoreCase("F")) {
                        code = "F";
                        desc = "SMS Sent Successfully," + email_.getMsg();
                    } else {
                        code = "S";
                        desc = "SMS and Email Sent Successfully";
                    }
                    return new ResponseDTO(code, desc);
                }
            }
            return null;
        } catch (Exception ex) {
            return new ResponseDTO("F", ex.getMessage());
        }
    }
}

