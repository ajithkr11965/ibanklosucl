package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanSubqueueTask;
import com.sib.ibanklosucl.model.VehicleLoanVkyc;
import com.sib.ibanklosucl.repository.VehicleLoanApplicantRepository;
import com.sib.ibanklosucl.repository.VehicleLoanSubqueueTaskRepository;
import com.sib.ibanklosucl.repository.VehicleLoanVkycRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Slf4j
public class VehicleLoanVkycService {
    @Autowired
    private VehicleLoanVkycRepository repository;

    @Autowired
    private VehicleLoanApplicantRepository apprepo;

    @Autowired
    private VehicleLoanSubqueueTaskRepository taskRepository;

    @Transactional
    public String updateVkycDetails(VehicleLoanVkyc vkycDetails,String uniqueId) {
        String msg="";
        try {
            VehicleLoanVkyc existingVkyc = repository.findByVkycUniqueId(uniqueId);
            if (existingVkyc != null) {
                log.info("los vkyc existing data fetched ");
                if (existingVkyc != null) {
                    VehicleLoanApplicant vlapplicant = apprepo.findByApplicantIdAndDelFlg(existingVkyc.getAppId(), "N");
                    if (vlapplicant != null) {
                        VehicleLoanSubqueueTask vltask = taskRepository.findBySlnoAndTaskTypeAndApplicantAndStatusNot(vlapplicant.getSlno(), "VKYC",vlapplicant, "COMPLETED");
                        if (vltask != null) {
                            existingVkyc.setAadhaarRefKey(vkycDetails.getAadhaarRefKey());
                            existingVkyc.setCustName(vkycDetails.getCustName());
                            existingVkyc.setVkycGender(vkycDetails.getVkycGender());
                            existingVkyc.setVkycDob(vkycDetails.getVkycDob());
                            existingVkyc.setVkycVcipStatus(vkycDetails.getVkycVcipStatus());
                            existingVkyc.setVkycStatusDecr(vkycDetails.getVkycStatusDecr());
                            existingVkyc.setVkycLatitude(vkycDetails.getVkycLatitude());
                            existingVkyc.setVkycLongitude(vkycDetails.getVkycLongitude());
                            existingVkyc.setVkycPhotoMatch(vkycDetails.getVkycPhotoMatch());
                            existingVkyc.setVkycFailureReason(vkycDetails.getVkycFailureReason());
                            existingVkyc.setCustId(vkycDetails.getCustId());
                            existingVkyc.setVkycPhoto(vkycDetails.getVkycPhoto());
                            existingVkyc.setVkycTime(new Date());
                            repository.save(existingVkyc);
                            log.info("vlvkyc db updated ");

                            if(!vkycDetails.getVkycVcipStatus().equals("Rejected") && vkycDetails.getCustId()!=null && !vkycDetails.getCustId().equals("") && !vkycDetails.getCustId().isEmpty() && !vkycDetails.getCustId().isBlank()){
                                vlapplicant.setCifId(vkycDetails.getCustId());
                                vlapplicant.setSibCustomer("Y");
                                apprepo.save(vlapplicant);
                                log.info("vlapplicants db updated ");

                                vltask.setStatus("COMPLETED");
                                vltask.setCompletedDate(new Date());
                                vltask.setCompletedUser("VLKYC");
                                taskRepository.save(vltask);
                                log.info("vltask db updated - success");
                            }else{
                                vltask.setStatus("REJECTED");
                                vltask.setRemarks(vkycDetails.getVkycVcipStatus());
                                taskRepository.save(vltask);
                            }



                            msg= "Success";
                        } else {
                            log.info("vltask - no records");
                            msg= "Failed";
                        }
                    } else {
                        log.info("vlapplicants - no records");
                        msg= "Failed";
                    }
                } else {
                    log.info("vlvkyc - no records");
                    msg= "Failed";
                }

            } else {
                log.info("los vkyc existing data fetch failed");
                msg= "Failed";
            }

        } catch (Exception e) {
            log.info("Exception :", e);
            e.printStackTrace();
            msg= "Failed";
        }finally {
            return msg;
        }
    }
}
