package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.VehicleLoanBlock;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.sib.ibanklosucl.repository.VehicleLoanBlockRepository;
import org.springframework.stereotype.Service;
import com.sib.ibanklosucl.repository.VehicleLoanMasterRepository;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class VehicleLoanBlockService {

    @Autowired
    private VehicleLoanBlockRepository vehicleLoanBlockRepository;
    @Autowired
    private UserSessionData usd;
    @Autowired
    private VehicleLoanMasterRepository vehicleLoanMasterRepository;
    public void insertBlock(VehicleLoanBlock vehicleLoanBlock) {
        try {
            String wiNum = vehicleLoanBlock.getWiNum();
            String blockType = vehicleLoanBlock.getBlockType();
            String applicantId = vehicleLoanBlock.getApplicantId();
            String queue = "";

            Optional<VehicleLoanMaster> vehicleLoanMaster_ = vehicleLoanMasterRepository.findByWiNumAndActiveFlg(wiNum, "Y");
            if (!vehicleLoanMaster_.isPresent()) {
                log.info("Cannot find WI in vehicleloanmaster,winum:{}", wiNum);
                //return;
            } else {
                VehicleLoanMaster vehicleLoanMaster = vehicleLoanMaster_.get();
                queue = vehicleLoanMaster.getQueue();
            }

            vehicleLoanBlock.setCmUser(usd.getPPCNo());
            vehicleLoanBlock.setCmDate(new Date());
            vehicleLoanBlock.setQueue(queue);
            vehicleLoanBlock.setActive("Y");
            vehicleLoanBlock.setDelFlag("N");

            VehicleLoanBlock vehicleLoanBlock1;
            vehicleLoanBlock1 = vehicleLoanBlockRepository.findByWiNumAndBlockTypeAndApplicantIdAndDelFlag(wiNum, blockType, applicantId, "N");
            if (vehicleLoanBlock1 != null) {
                vehicleLoanBlock1.setDelFlag("Y");
                vehicleLoanBlockRepository.save(vehicleLoanBlock1);
            }
            vehicleLoanBlockRepository.save(vehicleLoanBlock);

            /*
            switch (blockType) {
                case VLBlockCodes.NOT_KYC_COMPLIED:
                case VLBlockCodes.PAN_CBS_MISMATCH:
                case VLBlockCodes.PASSPORT_CBS_MISMATCH:
                case VLBlockCodes.AADHAAR_CBS_MISMATCH:
                case VLBlockCodes.PAN_CBS_MISSING:
                    vehicleLoanBlock1 = vehicleLoanBlockRepository.findByWiNumAndBlockTypeAndApplicantIdAndDelFlag(wiNum, blockType, applicantId, "N");
                    if (vehicleLoanBlock1 != null) {
                        vehicleLoanBlock1.setDelFlag("Y");
                        vehicleLoanBlockRepository.save(vehicleLoanBlock1);
                    }
                    vehicleLoanBlockRepository.save(vehicleLoanBlock);
                    break;


            }

             */
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception in VehicleLoanBlockService.insertBlock");
        }
    }
}
