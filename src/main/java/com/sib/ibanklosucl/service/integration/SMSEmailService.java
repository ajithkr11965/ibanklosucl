package com.sib.ibanklosucl.service.integration;

import com.sib.ibanklosucl.dto.MailRequest;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.dto.doc.SMSEmailDTO;
import com.sib.ibanklosucl.model.integrations.SMSEmail;
import com.sib.ibanklosucl.repository.integations.SMSEmailRepository;
import com.sib.ibanklosucl.service.email.WarningMailService;
import com.sib.ibanklosucl.service.esbsr.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class SMSEmailService {
    @Value("${app.mailSmsSend:false}")
    private boolean mailSmsSend;
    @Autowired
    private SMSEmailRepository smsEmailRepository;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MailService mailService;
    @Autowired
    private WarningMailService warningMailService;

    public boolean countEntitiesWithin30Minutes(Long slno, String reqType, String alertId) {
        Date endTime = new Date(); // Current date and time

        Calendar cal = Calendar.getInstance();
        cal.setTime(endTime);
        cal.add(Calendar.MINUTE, -30); // Subtract 30 minutes from the current time
        Date startTime = cal.getTime();

        return smsEmailRepository.countBySlnoAndSendateWithin30Minutes(slno, reqType, startTime, endTime, alertId) > 0;
    }


    @Transactional
    public ResponseDTO insertSMSEmail(SMSEmailDTO smsEmailDTO) {
        try {
            if ("S".equalsIgnoreCase(smsEmailDTO.getReqType())) {
                if (countEntitiesWithin30Minutes(smsEmailDTO.getSlno(), "S", smsEmailDTO.getAlertId()))
                    return new ResponseDTO("F", "A Sms Has Already Been Sent within 30 minutes");
            } else {
                // FIXED LOGIC: Exclude MSSF alerts from 30-minute check
                boolean isMSSFAlert = smsEmailDTO.getAlertId() != null &&
                        smsEmailDTO.getAlertId().startsWith("MSSF_");
                boolean isNachAlert = "NACHALERT".equals(smsEmailDTO.getAlertId());

                // Only check 30-minute rule if it's NOT an MSSF alert and NOT a NACH alert
                if (!isMSSFAlert && !isNachAlert) {
                    if (countEntitiesWithin30Minutes(smsEmailDTO.getSlno(), "E", smsEmailDTO.getAlertId())) {
                        return new ResponseDTO("F", "An Email Has Already Been Sent within 30 minutes");
                    }
                }
            }

            SMSEmail smsEmail = new SMSEmail();
            smsEmail.setSlno(smsEmailDTO.getSlno());
            smsEmail.setWiNum(smsEmailDTO.getWiNum());
            smsEmail.setReqType(smsEmailDTO.getReqType());
            smsEmail.setAlertId(smsEmailDTO.getAlertId());
            smsEmail.setMessage(smsEmailDTO.getMessage());
            smsEmail.setForacid(null);
            smsEmail.setMobile(smsEmailDTO.getMobile());
            smsEmail.setEmailFrom(smsEmailDTO.getEmailFrom());
            smsEmail.setEmailTo(smsEmailDTO.getEmailTo());
            smsEmail.setEmailBody(smsEmailDTO.getEmailBody());
            smsEmail.setEmailSubject(smsEmailDTO.getEmailSubject());
            smsEmail.setSentDate(new Date());
            smsEmail.setSentUser(smsEmailDTO.getSentUser());
            smsEmail.setHashCode(smsEmailDTO.getHashCode());
            smsEmail.setAppid(smsEmailDTO.getAppid());
            smsEmail.setEmailCc(smsEmailDTO.getEmailCc());
            smsEmailRepository.save(smsEmail);

            // Conditional insertion for reqType = "S"
            if ("S".equalsIgnoreCase(smsEmailDTO.getReqType())) {
                insertIntoSibptt(smsEmailDTO);
            } else {
                MailRequest mailRequest = new MailRequest();
                mailRequest.setFrom(smsEmailDTO.getEmailFrom());
                mailRequest.setSubject(smsEmailDTO.getEmailSubject());
                boolean isMSSFAlert = smsEmailDTO.getAlertId() != null &&
                        smsEmailDTO.getAlertId().startsWith("MSSF_");

                if (isMSSFAlert) {
                    mailRequest.addToMailArray("rsmalgroup@sib.bank.in", mailRequest);
                    mailRequest.addToMailArray("albgcentralteam@sib.bank.in", mailRequest);
                    mailRequest.setContent(smsEmailDTO.getEmailBody());
                    List<String> images = new ArrayList<>();
                    images.add("static/images/siblogo.png");
                    mailRequest.setImagePaths(images);
                } else {
                    mailRequest.setContent(warningMailService.generateCustEmailBody(smsEmailDTO.getEmailBody(), smsEmailDTO.getCustName()));
                    mailRequest.setImagePaths(mailRequest.getImages());
                }

                mailRequest.addToMailArray(smsEmailDTO.getEmailTo(), mailRequest);
                if ("NACHALERT".equals(smsEmailDTO.getAlertId())) {
                    mailRequest.addCcMailArray(smsEmailDTO.getEmailCc(), mailRequest);
                } else if ("DSA_WI_ALERT".equals(smsEmailDTO.getAlertId())) {
                    String[] mailarr = smsEmailDTO.getEmailCc().split("|");
                    for (String mails : mailarr) {
                        if (mails != null && !mails.trim().isEmpty() && !mails.trim().equals("-"))
                            mailRequest.addCcMailArray(mails.trim(), mailRequest);
                    }

                }

                if (mailSmsSend) {
                    mailRequest.addToMailArray("ajithkr@sib.bank.in", mailRequest);
                    mailRequest.addToMailArray("antonyraj@sib.bank.in", mailRequest);
                }
                mailService.sendMail(mailRequest);
            }

            return new ResponseDTO("S", "Data inserted successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseDTO("F", "Failed to insert data: " + e.getMessage());
        }
    }

    private void insertIntoSibptt(SMSEmailDTO smsEmailDTO) {
        try {
            String sql = "INSERT INTO sibptt@smsdb (alertid, message, foracid, mobileid) VALUES (:alertid, :message, :foracid, :mobileid)";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("alertid", smsEmailDTO.getAlertId());
            query.setParameter("message", smsEmailDTO.getMessage());
            query.setParameter("foracid", "0000000000000000");
            query.setParameter("mobileid", smsEmailDTO.getMobile());
            query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert into PTT: " + e.getMessage());
        }
    }
}
