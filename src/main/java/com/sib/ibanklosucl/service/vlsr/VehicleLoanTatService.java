package com.sib.ibanklosucl.service.vlsr;

import com.sib.ibanklosucl.model.VehicleLoanTat;
import com.sib.ibanklosucl.repository.VehicleLoanTatRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class VehicleLoanTatService {
    @Autowired
    private VehicleLoanTatRepository repository;
    @Autowired
    private UserSessionData usd;

    @Transactional
    public VehicleLoanTat saveTat(VehicleLoanTat tat) {
        return repository.save(tat);
    }
    public VehicleLoanTat getTatBySlno(Long slno) {
        return repository.findAllByQueueExitDateIsNullAndSlnoAndDelFlg(slno,"N");
    }
    @Transactional
    public void updateTat(Long slno, String cmuser,String winum,String queue) {

        VehicleLoanTat vehicleLoanTat=getTatBySlno(slno);
        if(vehicleLoanTat!=null){
            vehicleLoanTat.setQueueExitUser(cmuser);
            vehicleLoanTat.setQueueExitDate(new Date());
            saveTat(vehicleLoanTat);
        }
        if(!"NIL".equals(queue) && !"PD".equals(queue)) {
            VehicleLoanTat vehicleLoanTat_ = new VehicleLoanTat();
            vehicleLoanTat_.setWiNum(winum);
            vehicleLoanTat_.setSlno(slno);
            vehicleLoanTat_.setReqIpAddr(usd.getRemoteIP());
            vehicleLoanTat_.setQueue(queue);
            vehicleLoanTat_.setQueueEntryUser(cmuser);
            vehicleLoanTat_.setQueueEntryDate(new Date());
            vehicleLoanTat_.setDelFlg("N");
            saveTat(vehicleLoanTat_);
        }
    }

    public String getPreviousQueue(String wiNum) {
        List<VehicleLoanTat> previousQueueEntries = repository.findPreviousQueue(wiNum);
        if (previousQueueEntries.size() < 2) {
            return null;
        }
        return previousQueueEntries.get(1).getQueue();
    }

    public Boolean makerCheckerAreSame(Long slno, String loggedInUser) {
        VehicleLoanTat vehicleLoanTat= getTatBySlno(slno);
        return vehicleLoanTat.getQueueEntryUser().equals(loggedInUser);
    }

}
