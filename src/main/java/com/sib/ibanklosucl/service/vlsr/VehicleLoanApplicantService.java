package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.integrations.VLBlackList;
import com.sib.ibanklosucl.model.integrations.VLHunterDetails;
import com.sib.ibanklosucl.repository.VehicleLoanApplicantRepository;
import com.sib.ibanklosucl.utilies.Constants;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VehicleLoanApplicantService {

    @Autowired
    private VehicleLoanApplicantRepository repository;
    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private UserSessionData usd;

    //    public VehicleLoanApplicant getById(Long applicant_id) {
//        return repository.findByapplicant_id(applicant_id);
//    }
    public VehicleLoanApplicant getById(Long applicant_id) {
        return repository.findById(applicant_id).orElseThrow(() -> new RuntimeException("Not Found"));
    }

    public Long countCoApplicants(Long slno){
        return repository.countAllBySlnoAndDelFlgAndApplicantType(slno,"N","C");
    }

    public VehicleLoanApplicant findByApplicantIdAndDelFlg(Long applicant_id) {
        return repository.findByApplicantIdAndDelFlg(applicant_id, "N");
    }
    public void resetLoanFlg(Long slno) {
         repository.updateLoanFlg(slno);
    }

    public List<VehicleLoanApplicant> findBySlnoAndDelFlg(Long slno) {
        return repository.findBySlnoAndDelFlg(slno, "N");
    }
    public List<VehicleLoanApplicant> findBySlnoAndDelFlgAndApplicantType(Long slno) {
        List<String> applicantTypes= Arrays.asList("A","C");
        return repository.findBySlnoAndDelFlgAndApplicantTypes(slno, "N",applicantTypes);
    }
    public Long getGuaranatorId(Long slno,String id) {
        Optional<VehicleLoanApplicant> vehicleLoanApplicant= repository.findAllBySlnoAndApplicantTypeAndDelFlg(slno,id, "N");
        if(vehicleLoanApplicant.isPresent()){
            return  vehicleLoanApplicant.get().getApplicantId();
        }
        return -1L;
    }

    @Transactional
    public VehicleLoanApplicant saveApplicant(VehicleLoanApplicant applicant) {
        return repository.save(applicant);
    }

    @Transactional
    public void updateApplicantRaceScore(Long applicantId, String score) {
        repository.updateApplicantRaceScore(applicantId,score);
    }

    @Transactional
    public ResponseDTO updateApplicantIntegrationDetails(String wiNum, String slno, String identifier, String updValue) {
        List<VehicleLoanApplicant> vehicleLoanApplicants = findBySlnoAndDelFlg(Long.valueOf(slno));
        String message = "error";
        for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
            if ("BLACKLIST".equals(identifier)) {
                boolean allNotBlacklisted = true;
                List<VLBlackList> vlBlackLists = applicant.getVlBlackList();
                if(vlBlackLists!=null && !vlBlackLists.isEmpty()) {
                    for (VLBlackList blackList : vlBlackLists) {
                        if (blackList.getBlCheckResult().equals("Blacklisted") && blackList.getDelFlg().equals("N")) {
                             allNotBlacklisted = false;
                            return new ResponseDTO("F", "Blacklisted Applicant present");
                        }
                    }
                } else {
                    return new ResponseDTO("F", "Blacklist check is not completed");
                }
                if (allNotBlacklisted) {
                    applicant.setBlacklistCheck(updValue);
                    vehicleLoanMasterService.updateStatus(Long.valueOf(slno),"BCDRAFT");
                } else {
                    return new ResponseDTO("F", "All applicants are not 'Not Blacklisted'");
                }
            } else if ("HUNTER".equals(identifier)) {
                if (!"G".equals(applicant.getApplicantType())) {
                    boolean allPassedHunter = true;
                    List<VLHunterDetails> vlHunterDetails = applicant.getVlHunterDetailsList();
                    if (vlHunterDetails != null && !vlHunterDetails.isEmpty()) {
                        for (VLHunterDetails vlHunter : vlHunterDetails) {
                            if (vlHunter.getMatches()>0 && vlHunter.getDelFlg().equals("N")) {
                                allPassedHunter = true;
                                //return new ResponseDTO("F", "Hunter check failed Applicant present");
                            } else {
                                allPassedHunter = true;
                            }
                        }
                    } else {
                        return new ResponseDTO("F", "Hunter check is not completed");
                    }
                    if (allPassedHunter) {
                        vehicleLoanMasterService.updateStatus(Long.valueOf(slno),"BCDRAFT");
                        applicant.setBlacklistCheck(updValue);
                    } else {
                        return new ResponseDTO("F", "All applicants are not 'Not Blacklisted'");
                    }
                    applicant.setHunterCheck(updValue);
                }
            }
        }
        repository.saveAll(vehicleLoanApplicants);
        message = "SUCCESS";
        if (message.equals("SUCCESS")) {
            return new ResponseDTO("Y", message);
        } else {
            return new ResponseDTO("F", Constants.Messages.SOMETHING_ERROR);
        }
    }

    public Map<String, Object> getApplicantDetails(Long slno, String wiNum, Long applicantId) {
        return repository.findApplicantDetails(slno, wiNum, applicantId);
    }


    public VehicleLoanApplicant findByApplicantIdAndDelFlag(Long applicant_id) {
        return repository.findByApplicantIdAndDelFlg(applicant_id, "N");
    }
}
