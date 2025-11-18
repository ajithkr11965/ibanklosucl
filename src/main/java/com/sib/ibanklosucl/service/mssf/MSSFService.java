package com.sib.ibanklosucl.service.mssf;

import com.sib.ibanklosucl.dto.mssf.*;
import com.sib.ibanklosucl.model.mssf.MSSFCustomerData;
import com.sib.ibanklosucl.model.mssf.MSSFLock;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.mssf.MSSFCustomerRepository;
import com.sib.ibanklosucl.repository.mssf.MSSFDealerSolRepository;
import com.sib.ibanklosucl.repository.mssf.MSSFLockRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MSSFService {

    private final MSSFCustomerRepository mssfCustomerRepository;
    private final MSSFLockRepository mssfLockRepository;
    private final MSSFDealerSolRepository mssfDealerSolRepository;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Value("${api.integrator}")
    private String integratorEndpoint;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private UserSessionData usd;

    //    public List<MSSFCustomerDTO> getMSSFQueue(String solId) {
//        List<Object[]> mssfQueue = mssfCustomerRepository.findPendingApplicationsBySol(solId);
//        return mssfQueue.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
    public List<MSSFCustomerDTO> getMSSFQueue(String solId) {
        try {
             List<String> solid=new ArrayList<>();
            if (fetchRepository.isLMPPC(usd.getPPCNo())) {
                solid = fetchRepository.getLHSols(usd.getPPCNo());
            } else if (usd.getSolid().equalsIgnoreCase("8032")) {
                solid = fetchRepository.getRsmSols(usd.getPPCNo());
            } else {
                solid.add(solId);
            }
            log.info("Fetching MSSF queue for solId: {}", solId);
            List<Object[]> mssfQueue = mssfCustomerRepository.findPendingApplicationsBySol(solid);
            log.info("Found {} records", mssfQueue.size());

            return mssfQueue.stream()
                    .peek(row -> log.info("Processing row: {}", Arrays.toString(row)))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error in getMSSFQueue for solId: " + solId, e);
            throw e;
        }
    }


    private MSSFCustomerDTO convertToDTO(Object[] result) {
        MSSFCustomerData mssfData = (MSSFCustomerData) result[0];
        String lockFlag = (String) result[1];
        String lockedBy = (String) result[2];

        MSSFCustomerDTO dto = new MSSFCustomerDTO();
        dto.setRefNo(mssfData.getRefNo());
        dto.setCustomerName(mssfData.getPdFirstName() + " " +
                (mssfData.getPdMiddleName() != null ? mssfData.getPdMiddleName() + " " : "") +
                mssfData.getPdLastName());
        dto.setMobile(mssfData.getPdMobile());
        dto.setEmail(mssfData.getPdEmail());
        dto.setDealerCode(mssfData.getDlrCode());
        dto.setLoanAmount(mssfData.getLaLoanAmt());
        dto.setCreatedDate(mssfData.getCreatedDate());

        if ("Y".equals(lockFlag)) {
            MSSFLockDTO lockDTO = new MSSFLockDTO();
            lockDTO.setLockedBy(lockedBy);
            lockDTO.setLockFlag("Y");
            dto.setLockDetails(lockDTO);
        }

        return dto;
    }

    @Transactional
    public MSSFCustomerData getMSSFDetails(String refNo) {
        return mssfCustomerRepository.findByRefNo(refNo)
                .orElseThrow(() -> new RuntimeException("MSSF application not found"));
    }

    @Transactional
    public void lockApplication(String refNo, String userId) {
        MSSFLock lock = mssfLockRepository.findByRefNo(refNo)
                .orElse(new MSSFLock());

        lock.setRefNo(refNo);
        lock.setLockedBy(userId);
        lock.setLockFlg("Y");
        lock.setLockedOn(LocalDateTime.now());

        mssfLockRepository.save(lock);
    }

    @Transactional
    public void unlockApplication(String refNo) {
        mssfLockRepository.findByRefNo(refNo)
                .ifPresent(lock -> {
                    lock.setLockFlg("N");
                    lock.setLockedBy(null);
                    lock.setLockedOn(null);
                    mssfLockRepository.save(lock);
                });
    }

    @Transactional
    public void createVehicleLoan(String refNo, String userId) {
        MSSFCustomerData mssfData = getMSSFDetails(refNo);
        // Logic to create vehicle loan application
        // Update MSSF status
        mssfData.setStatus("PROCESSED");
        mssfData.setModifiedBy(userId);
        mssfData.setModifiedDate(LocalDateTime.now());
        mssfCustomerRepository.save(mssfData);
    }

    public MssfCustomerDetailsDTO getMssfDetails(String refNo) {
        MSSFCustomerData customerData = mssfCustomerRepository.findByRefNo(refNo)
                .orElseThrow(() -> new RuntimeException("MSSF application not found: " + refNo));

        MssfCustomerDetailsDTO dto = new MssfCustomerDetailsDTO();
        BeanUtils.copyProperties(customerData, dto);
        return dto;
    }

    public void updateApplication(String refNo, String status, String subStatus) {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        Date now = new Date();
        String timeString = formatter.format(now);
        String UUID = "MSSF" + timeString;
        MssfStatusUpdateRequest.Request requestBody = MssfStatusUpdateRequest.Request.builder()
                .merchantCode(merchantCode)
                .merchantName(merchantName)
                .reqType("docDetails")
                .reference_id("")
                .application_status(status)
                .application_sub_status(subStatus)
                .last_updated_timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .los_id(refNo)
                .UUID(UUID)
                .build();
        mssfCustomerRepository.updateMSSFStatusToActive(refNo);
    }


}
