package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.dto.program.FDAccountDetails;
import com.sib.ibanklosucl.dto.program.FDAccountRequest;
import com.sib.ibanklosucl.dto.program.FDAccountResponse;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanFD;
import com.sib.ibanklosucl.model.VehicleLoanProgram;
import com.sib.ibanklosucl.repository.program.FDAccountRepository;
import com.sib.ibanklosucl.repository.program.VehicleLoanFDRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FDAccountService {

    @Autowired
    private VehicleLoanFDRepository vehicleLoanFDRepository;

    private final FDAccountRepository accountRepository;
    private List<FDAccountDetails> unusedAccounts;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;

    public FDAccountService(FDAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public String deleteFDAccount(Long ino, String delUser, String delHomeSol, String delIpAddr) {
        Optional<VehicleLoanFD> optionalVehicleLoanFD = vehicleLoanFDRepository.findById(ino);
        if (optionalVehicleLoanFD.isPresent()) {
            VehicleLoanFD vehicleLoanFD = optionalVehicleLoanFD.get();
            vehicleLoanFD.setDelFlg("Y");
            vehicleLoanFD.setDelUser(delUser);
            vehicleLoanFD.setDelDate(new Date());
            vehicleLoanFD.setDelHomeSol(delHomeSol);
            vehicleLoanFDRepository.save(vehicleLoanFD);
            recalculateEligibility(vehicleLoanFD.getWiNum());
            return "success";
        } else {
            return "error";
        }
    }
    public Map<String, Object> getAccountDetails(Long applicantId, String wiNum,Long slno) {
        List<VehicleLoanFD> activeFDs = vehicleLoanFDRepository.findByApplicantIdAndWiNumAndDelFlg(applicantId, wiNum, "N");
        List<FDAccountResponse> responseList = new ArrayList<>();
         Set<String> allCifIds = new HashSet<>();

        for (VehicleLoanFD fd : activeFDs) {
            FDAccountResponse response = new FDAccountResponse();
            response.setVehicleLoanFD(fd);
            response.setEligible(fd.isEligible());
            responseList.add(response);
            allCifIds.addAll(Arrays.asList(fd.getCifid().split(",")));
        }
        List<VehicleLoanApplicant> existingApplicants = vehicleLoanApplicantService.findBySlnoAndDelFlg(slno);
        Set<String> existingCifIds = existingApplicants.stream()
                .map(VehicleLoanApplicant::getCifId)
                .collect(Collectors.toSet());
        Set<String> missingCifIds = new HashSet<>(allCifIds);
        missingCifIds.removeAll(existingCifIds);
        Map<String, Object> result = new HashMap<>();
        result.put("fdAccounts", responseList);
        result.put("missingCifIds", missingCifIds);
        return result;

    }
    public void updateVehicleLoanFDWithProgram(Long applicantId, VehicleLoanProgram vehicleLoanProgram) {
        List<VehicleLoanFD> vehicleLoanFDList = vehicleLoanFDRepository.findByApplicantIdAndWiNumAndDelFlg(applicantId, vehicleLoanProgram.getWiNum(), "N");
        for (VehicleLoanFD vehicleLoanFD : vehicleLoanFDList) {
            vehicleLoanFD.setVlfd(vehicleLoanProgram);
        }
        vehicleLoanFDRepository.saveAll(vehicleLoanFDList);
    }

    @Transactional
    public void deleteAndSaveVehicleLoanFD(Long applicantId, String wiNum, List<VehicleLoanFD> vehicleLoanFDList) {
        try {
            if (vehicleLoanFDList == null || vehicleLoanFDList.isEmpty()) {
                log.warn("Attempt to save empty or null VehicleLoanFD list for applicantId: {} and wiNum: {}", applicantId, wiNum);
                return;
            }

            log.info("Deleting existing VehicleLoanFD records for applicantId: {} and wiNum: {}", applicantId, wiNum);
            vehicleLoanFDRepository.deleteByApplicantIdAndWiNum(applicantId, wiNum);

            log.info("Saving {} new VehicleLoanFD records", vehicleLoanFDList.size());
            vehicleLoanFDRepository.saveAll(vehicleLoanFDList);

            log.info("Successfully updated VehicleLoanFD records for applicantId: {} and wiNum: {}", applicantId, wiNum);
        } catch (Exception e) {
            log.error("Error occurred while updating VehicleLoanFD records for applicantId: {} and wiNum: {}", applicantId, wiNum, e);
            throw new RuntimeException("Failed to update VehicleLoanFD records", e);
        }
    }

    public Map<String, Object> fetchAccountDetailsByCifNew(FDAccountRequest fdAccountRequest) {
        Long applicantId = Long.valueOf(fdAccountRequest.getApplicantId());
        String wiNum = fdAccountRequest.getWiNum();
        String customerId = fdAccountRequest.getCifId();
        List<VehicleLoanFD> existingRecords = vehicleLoanFDRepository.findByApplicantIdAndWiNumAndDelFlg(applicantId, wiNum, "N");
        for (VehicleLoanFD existingRecord : existingRecords) {
            existingRecord.setDelFlg("Y");
            existingRecord.setDelUser(fdAccountRequest.getLastModUser());
            existingRecord.setDelDate(new Date());
            vehicleLoanFDRepository.save(existingRecord);
        }
        List<FDAccountDetails> accountDetailsList = accountRepository.findAccountDetails(customerId);
        List<FDAccountResponse> responseList = new ArrayList<>();
        Set<String> allCifIds = new HashSet<>();
        if (accountDetailsList != null && !accountDetailsList.isEmpty()) {
            for (FDAccountDetails accountDetails : accountDetailsList) {
                String fdAccountNumber = accountDetails.getFdAccNo();

                // Check if the account already exists for this applicant and wiNum
                boolean isDuplicate = vehicleLoanFDRepository.existsByApplicantIdAndWiNumAndFdaccnumAndDelFlg(applicantId, wiNum, fdAccountNumber, "N");
                boolean isEligible = calculateEligibility(wiNum);

                if (!isDuplicate) {
                    VehicleLoanFD currentApplicantFD = vehicleLoanFDRepository
                            .findByFdaccnumAndApplicantIdAndWiNumAndDelFlg(fdAccountNumber, applicantId, wiNum, "N")
                            .orElseGet(() -> createAndSaveVehicleLoanFD(fdAccountRequest, accountDetails, fdAccountNumber, isEligible));

                    // Create the FDAccountResponse object
                    FDAccountResponse response = new FDAccountResponse();
                    response.setVehicleLoanFD(currentApplicantFD);
                    response.setEligible(currentApplicantFD.isEligible());
                    responseList.add(response);
                    allCifIds.addAll(Arrays.asList(currentApplicantFD.getCifid().split(",")));
                }
            }

        }
        List<VehicleLoanApplicant> existingApplicants = vehicleLoanApplicantService.findBySlnoAndDelFlg(Long.valueOf(fdAccountRequest.getSlno()));
        Set<String> existingCifIds = existingApplicants.stream()
                .map(VehicleLoanApplicant::getCifId)
                .collect(Collectors.toSet());
        Set<String> missingCifIds = new HashSet<>(allCifIds);
        missingCifIds.removeAll(existingCifIds);
        Map<String, Object> result = new HashMap<>();
        result.put("fdAccounts", responseList);
        result.put("missingCifIds", missingCifIds);
        return result;
    }

    private long calculateDistinctJointHoldersCount(List<VehicleLoanFD> fds) {
        return fds.stream()
                .flatMap(fd -> Arrays.stream(fd.getCifid().split(",")))
                .distinct()
                .count();
    }
    private VehicleLoanFD createAndSaveVehicleLoanFD(FDAccountRequest fdAccountRequest, FDAccountDetails accountDetails, String fdAccountNumber, boolean isEligible) {
        VehicleLoanFD vehicleLoanFD = new VehicleLoanFD();
        vehicleLoanFD.setWiNum(fdAccountRequest.getWiNum());
        vehicleLoanFD.setSlno(Long.valueOf(fdAccountRequest.getSlno()));
        vehicleLoanFD.setApplicantId(Long.valueOf(fdAccountRequest.getApplicantId()));
        vehicleLoanFD.setFdaccnum(fdAccountNumber);
        vehicleLoanFD.setAvailbalance(accountDetails.getEligFDAmount());
        vehicleLoanFD.setFdaccname(accountDetails.getAccountName());
        vehicleLoanFD.setCifid(accountDetails.getCifIds());
        vehicleLoanFD.setAcid(accountDetails.getAcid());
        vehicleLoanFD.setSingleJoint(accountDetails.getAccountType());
        vehicleLoanFD.setLastModUser(fdAccountRequest.getLastModUser());
        vehicleLoanFD.setLastModDate(new Date());
        vehicleLoanFD.setAccountOpenDate(accountDetails.getAccountOpenDate());
        vehicleLoanFD.setMaturityDate(accountDetails.getMaturityDate());
        vehicleLoanFD.setMaturityAmount(accountDetails.getMaturityAmount());
        vehicleLoanFD.setDepositAmount(accountDetails.getDepositAmount());
        vehicleLoanFD.setReqIpAddr(fdAccountRequest.getReqIpAddr());
        vehicleLoanFD.setHomeSol(fdAccountRequest.getHomeSol());
        vehicleLoanFD.setFdStatus(accountDetails.getFdStatus());
        vehicleLoanFD.setFsldAdjAmount(accountDetails.getFsldAdjAmount());
        vehicleLoanFD.setFdBalAmount(accountDetails.getFdAmount());
        vehicleLoanFD.setDelFlg("N");
        boolean existingEligibility = vehicleLoanFDRepository
                .findByFdaccnumAndWiNumAndDelFlg(fdAccountNumber, fdAccountRequest.getWiNum(), "N").stream()
                .anyMatch(VehicleLoanFD::isEligible);
        vehicleLoanFD.setEligible(!existingEligibility && isEligible);

        return vehicleLoanFDRepository.save(vehicleLoanFD);
    }
//    @Transactional
//    public void recalculateEligibility(String wiNum) {
//        List<VehicleLoanFD> allFDs = vehicleLoanFDRepository.findByWiNumAndDelFlg(wiNum, "N");
//        boolean isEligible = calculateEligibility(wiNum);
//        Map<String, List<VehicleLoanFD>> fdGroups = allFDs.stream()
//                .collect(Collectors.groupingBy(VehicleLoanFD::getFdaccnum));
//        for (List<VehicleLoanFD> fdGroup : fdGroups.values()) {
//            boolean existingEligibility = fdGroup.stream().anyMatch(VehicleLoanFD::isEligible);
//            if (!existingEligibility && isEligible) {
//                fdGroup.get(0).setEligible(true);
//            } else {
//                fdGroup.forEach(fd -> fd.setEligible(isEligible && fd.isEligible()));
//            }
//        }
//        vehicleLoanFDRepository.saveAll(allFDs);
//    }
    @Transactional
    public void recalculateEligibility(String wiNum) {
        List<VehicleLoanFD> allFDs = vehicleLoanFDRepository.findByWiNumAndDelFlg(wiNum, "N");
        boolean isEligible = calculateEligibility(wiNum);

        for (VehicleLoanFD fd : allFDs) {
            fd.setEligible(isEligible);
        }
        vehicleLoanFDRepository.saveAll(allFDs);
    }

    private boolean calculateEligibility(String wiNum) {
        List<VehicleLoanFD> allFDs = vehicleLoanFDRepository.findByWiNumAndDelFlg(wiNum, "N");
        Set<String> distinctJointHolders = allFDs.stream()
                .flatMap(fd -> Arrays.stream(fd.getCifid().split(",")))
                .collect(Collectors.toSet());
        return distinctJointHolders.size() <= 4;
    }
    public List<String> getMissingCifIds(String wiNum) {
        List<VehicleLoanFD> allFDs = vehicleLoanFDRepository.findByWiNumAndDelFlg(wiNum, "N");
        if(allFDs.isEmpty())
            return new ArrayList();
        Set<String> allCifIds = allFDs.stream()
                .flatMap(fd -> Arrays.stream(fd.getCifid().split(",")))
                .collect(Collectors.toSet());

        List<VehicleLoanApplicant> existingApplicants = vehicleLoanApplicantService.findBySlnoAndDelFlgAndApplicantType(allFDs.get(0).getSlno());
        Set<String> existingCifIds = existingApplicants.stream()
                .map(VehicleLoanApplicant::getCifId)
                .collect(Collectors.toSet());

        Set<String> missingCifIds = new HashSet<>(allCifIds);
        missingCifIds.removeAll(existingCifIds);

        return new ArrayList<>(missingCifIds);
    }
        public BigDecimal calculateTotalEligibleBalance(Long applicantId, String wiNum) {
        List<VehicleLoanFD> eligibleFDs = vehicleLoanFDRepository.findByApplicantIdAndWiNumAndEligibleAndDelFlg(applicantId, wiNum, true, "N");

        return eligibleFDs.stream()
                .map(VehicleLoanFD::getAvailbalance)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    @Transactional
    public boolean validateCifId(Long applicantId, String wiNum, String cifId) {
        List<VehicleLoanFD> activeFDs = vehicleLoanFDRepository.findByApplicantIdAndWiNumAndDelFlg(applicantId, wiNum, "N");

        Set<String> savedCifIds = activeFDs.stream()
            .flatMap(fd -> Set.of(fd.getCifid().split(",")).stream())
            .collect(Collectors.toSet());

        if (!savedCifIds.contains(cifId)) {
            for (VehicleLoanFD fd : activeFDs) {
                fd.setDelFlg("Y");
            }
            vehicleLoanFDRepository.saveAll(activeFDs);
            return false;  // Indicates that CIF ID was not found
        }
        return true;  // Indicates that CIF ID was found
    }


}
