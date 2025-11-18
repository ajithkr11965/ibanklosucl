package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.model.VehicleLoanDecision;
import com.sib.ibanklosucl.repository.VehicleLoanDecisionRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class VehicleLoanDecisionService {

    @Autowired
    private VehicleLoanDecisionRepository vehicleLoanDecisionRepository;

    @Autowired
    private UserSessionData userSessionData;

    public void saveDecision(String wiNum, Long slno, String queue, String decision, HttpServletRequest request) {
        VehicleLoanDecision vehicleLoanDecision = new VehicleLoanDecision();
        vehicleLoanDecision.setWiNum(wiNum);
        vehicleLoanDecision.setSlno(slno);
        vehicleLoanDecision.setQueue(queue);
        vehicleLoanDecision.setDecision(decision);
        vehicleLoanDecision.setReqIpAddr(request.getRemoteAddr());
        vehicleLoanDecision.setDelFlg("N");
        vehicleLoanDecision.setLastModUser(userSessionData.getPPCNo());
        vehicleLoanDecision.setLastModDate(new Date());
        vehicleLoanDecision.setHomeSol(userSessionData.getEmployee().getJoinedSol());

        vehicleLoanDecisionRepository.save(vehicleLoanDecision);
    }
}
