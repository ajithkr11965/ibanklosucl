package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.dashboard.AllotmentDTO;
import com.sib.ibanklosucl.model.VehicleLoanAllotment;
import com.sib.ibanklosucl.repository.AllotmentListRepository;
import com.sib.ibanklosucl.repository.VehicleLoanAllotmentRepository;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class VehicleLoanAllotmentService {
    @Autowired
    private VehicleLoanAllotmentRepository allotmentRepository;
    @Autowired
    private AllotmentListRepository allotmentListRepository;
    @Autowired
    private UserSessionData usd;

    public boolean hasActiveAllotment(String wiNum, Long slno) {
        return allotmentRepository.findByWiNumAndSlnoAndActiveFlg(wiNum, slno, "Y").isPresent();
    }

    public List<AllotmentDTO> getAllotment(String queue) {
        return allotmentListRepository.getAllotmentList(queue);
    }

    public VehicleLoanAllotment save(String wiNum, Long slno, int doPcc, HttpServletRequest request) {
        VehicleLoanAllotment allotmentdetails= new VehicleLoanAllotment();
        VehicleLoanAllotment alloted = allotmentRepository.findBySlnoAndWiNumAndActiveFlgAndDelFlg(slno,wiNum,"Y","N");
        if(alloted !=null)
        {
            //update activeflg = N and delFlg = Y for the existing alloted rec for the same slno n winum
            alloted.setActiveFlg("N");
            alloted.setDelFlg("Y");
            allotmentRepository.save(alloted);
           // allotmentdetails.setIno(alloted.getIno());  // commented due to update of rec not needed scenerio
        }
        allotmentdetails.setSlno(slno);
        allotmentdetails.setWiNum(wiNum);
        allotmentdetails.setDoPpc(doPcc);
        allotmentdetails.setLastModDate(new Date());
        allotmentdetails.setLastModUser(usd.getPPCNo());
        allotmentdetails.setHomeSol(usd.getSolid());
        allotmentdetails.setReqIpAddr(CommonUtils.getClientIp(request));
        allotmentdetails.setDelFlg("N");
        allotmentdetails.setActiveFlg("Y");
        return allotmentRepository.save(allotmentdetails);
    }

     public boolean isAllotedToEmployee(String wiNum, Long slno, Integer employeePpcno) {
        return allotmentRepository.findByWiNumAndSlnoAndDoPpcAndActiveFlgAndDelFlg(wiNum, slno, employeePpcno, "Y","N")
                .isPresent();
    }




}

