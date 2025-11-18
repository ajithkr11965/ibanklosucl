package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.CRTAmberDataDTO;
import com.sib.ibanklosucl.dto.bre.AmberData;
import com.sib.ibanklosucl.dto.bre.AmberDeviationUpdateRequest;
import com.sib.ibanklosucl.model.VehicleLoanAmber;
import com.sib.ibanklosucl.model.VehicleLoanAmberSub;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.repository.VehicleLoanAmberRepository;
import com.sib.ibanklosucl.repository.VehicleLoanApplicantRepository;
import com.sib.ibanklosucl.utilies.UserSessionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VehicleLoanAmberService {
    @Autowired
    private VehicleLoanAmberRepository vehicleLoanAmberRepository;

    @Autowired
    private UserSessionData userSessionData;
    @Autowired
    private FetchRepository fetchRepository;
    @Autowired
    private VehicleLoanApplicantRepository vehicleLoanApplicantRepository;

    @Transactional
    public void updateAmberDeviations(AmberDeviationUpdateRequest request) {
        String wiNum = request.getWiNum();
        Long slno = Long.parseLong(request.getSlno());
         for (AmberData amberData : request.getAmberData()) {
            if ("CFR0001".equals(amberData.getAmberCode())) {
                updateOrInsertCfrDeviation(wiNum, slno, amberData);
            } else {
                updateOrInsertRegularDeviation(wiNum, slno, amberData);
            }
        }
    }
    @Transactional(rollbackOn = Exception.class)
    public void updateCheckerDeviations(AmberDeviationUpdateRequest request) {
        String wiNum = request.getWiNum();
        Long slno = Long.parseLong(request.getSlno());
        String userRole=fetchRepository.getUserRole(userSessionData.getPPCNo());
        List<VehicleLoanAmber> vlAmbers=getAmberDeviationsByWiNumAndSlno(wiNum,slno).stream().filter(t-> !"NA".equalsIgnoreCase(t.getApprovingAuth())).toList();
        List<AmberData> amberData=request.getAmberData();
        for (VehicleLoanAmber vlAmber :vlAmbers){
            Long userlevel= Long.valueOf(userRole.substring(userRole.length()-1));
            Long currentLevel= Long.valueOf(vlAmber.getApprovingAuth().substring(vlAmber.getApprovingAuth().length()-1));
            //Already Approved at Another Level
            boolean isApproved="APPROVE".equalsIgnoreCase(vlAmber.getApprAuthAction()) && currentLevel!=userlevel;// || userSessionData.getPPCNo().equalsIgnoreCase(vlAmber.getApprAuthUser());
            if(userlevel>=currentLevel && !isApproved) {
                Optional<AmberData> amberData1 = amberData.stream().filter(a -> a.getId().equals(vlAmber.getId())).findFirst();
                if (amberData1.isPresent()) {
                    AmberData amberData2 = amberData1.get();
                    vlAmber.setApprAuthAction(amberData2.getApproveCode());
                    vlAmber.setApprAuthRemarks(amberData2.getApproveComments());
                    vlAmber.setApprAuthDate(new Date());
                    vlAmber.setApprAuthUser(userSessionData.getPPCNo());
                    vehicleLoanAmberRepository.save(vlAmber);
                }
            }
        }

    }
    private void updateOrInsertCfrDeviation(String wiNum, Long slno, AmberData amberData) {
        Optional<VehicleLoanAmber> existingAmber = vehicleLoanAmberRepository.findActiveByWiNumAndSlnoAndAmberCode(wiNum, slno, "CFR0001");

        if (existingAmber.isPresent()) {
            VehicleLoanAmber amber = existingAmber.get();
            updateAmberRecord(amber, amberData);
        } else {
            insertNewAmberRecord(wiNum, slno, amberData);
        }
    }
    private void updateOrInsertRegularDeviation(String wiNum, Long slno, AmberData amberData) {
        if (amberData.getId() != null) {
            VehicleLoanAmber amber = vehicleLoanAmberRepository.findById(amberData.getId())
                    .orElseThrow(() -> new RuntimeException("Amber record not found"));
            updateAmberRecord(amber, amberData);
        } else {
            insertNewAmberRecord(wiNum, slno, amberData);
        }
    }

    public void updateFcvCpvCfrDeviation(String wiNum, Long slno, AmberData amberData) {
        Optional<VehicleLoanAmber> existingAmber = vehicleLoanAmberRepository.findActiveByWiNumAndSlnoAndAmberCode(wiNum, slno, amberData.getAmberCode());

        if (existingAmber.isPresent()) {
            VehicleLoanAmber amber = existingAmber.get();
            amber.setActiveFlg("N");
            amber.setDelFlg("Y");
            vehicleLoanAmberRepository.save(amber);
        }
        insertNewAmberRecord(wiNum, slno, amberData);

    }
    public void updateRaceScoreDeviation(String wiNum, Long slno, AmberData amberData) {
        Optional<VehicleLoanAmber> existingAmber = vehicleLoanAmberRepository.findActiveByWiNumAndSlnoAndAmberCode(wiNum, slno, amberData.getAmberCode());

        if (existingAmber.isPresent()) {
            VehicleLoanAmber amber = existingAmber.get();
            amber.setActiveFlg("N");
            amber.setDelFlg("Y");
            vehicleLoanAmberRepository.save(amber);
        }
        insertNewAmberRecord(wiNum, slno, amberData);

    }
    private void insertNewAmberRecord(String wiNum, Long slno, AmberData amberData) {
        VehicleLoanApplicant vehicleLoanApplicant = vehicleLoanApplicantRepository.findBySlnoAndDelFlgAndApplicantType(slno,"N","A");

        VehicleLoanAmber newAmber = new VehicleLoanAmber();
        newAmber.setWiNum(wiNum);
        newAmber.setSlno(slno);
        newAmber.setAmberCode(amberData.getAmberCode() != null ? amberData.getAmberCode() : generateNextAmberCode(wiNum, slno));
        newAmber.setAmberDesc(amberData.getAmberDesc());
        newAmber.setDeviationType("DO");
        newAmber.setApprovingAuth(amberData.getApprovingAuth());
        newAmber.setDoRemarks(amberData.getDoRemarks());
        newAmber.setLastModUser(userSessionData.getEmployee().getPpcno());
        newAmber.setLastModDate(new Date());
        newAmber.setDelFlg("N");
        newAmber.setActiveFlg("Y");
        newAmber.setHomeSol(userSessionData.getEmployee().getJoinedSol());
        newAmber.setColour(amberData.getColor()!= null ? amberData.getColor():"amber");
        newAmber.setAmberSubList(new ArrayList<>());
        log.info("Amber params and values  {} {}",amberData.getParameterRange(),amberData.getParameterValue());
        if(newAmber.getAmberCode().startsWith("RM")) {
            amberData.setApplicantId(String.valueOf(vehicleLoanApplicant.getApplicantId()));
            amberData.setApplicantName(vehicleLoanApplicant.getApplName());
            amberData.setApplicantType(vehicleLoanApplicant.getApplicantType());
        }

        // Add AmberSub if needed
        if (amberData.getParameterRange() != null && amberData.getParameterValue() != null && amberData.getApplicantId()!=null) {
            VehicleLoanAmberSub amberSub = createAmberSub(newAmber, amberData, wiNum,slno );
            newAmber.getAmberSubList().add(amberSub);
        }
        log.info("Ambersub is populated {}",newAmber.getAmberSubList().size());

        vehicleLoanAmberRepository.save(newAmber);
    }
    private VehicleLoanAmberSub createAmberSub(VehicleLoanAmber amber, AmberData amberData, String wiNum, Long slno) {
        VehicleLoanAmberSub amberSub = new VehicleLoanAmberSub();
        amberSub.setMasterValue(amberData.getParameterRange());
        amberSub.setCurrentValue(amberData.getParameterValue());
        amberSub.setWiNum(wiNum);
        amberSub.setSlno(slno);
        amberSub.setApplicantName(amberData.getApplicantName());
        amberSub.setApplicantId(Long.valueOf(amberData.getApplicantId()));
        amberSub.setApplicantType(amberData.getApplicantType());
        amberSub.setActiveFlg("Y");
        amberSub.setDelFlg("N");
        amberSub.setColour(amberData.getColor()!= null ? amberData.getColor():"amber");
        amberSub.setAmberCode(amber.getAmberCode());
        amberSub.setLastModDate(new Date());
        amberSub.setLastModUser(userSessionData.getEmployee().getPpcno());
        amberSub.setHomeSol(userSessionData.getEmployee().getJoinedSol());
        amberSub.setVehicleLoanAmber(amber);
        return amberSub;
    }
    private void updateAmberRecord(VehicleLoanAmber amber, AmberData amberData) {
        amber.setAmberDesc(amberData.getAmberDesc());
        amber.setDeviationType("DO");
        amber.setApprovingAuth(amberData.getApprovingAuth());
        amber.setDoRemarks(amberData.getDoRemarks());
        amber.setLastModUser(userSessionData.getEmployee().getPpcno());
        amber.setLastModDate(new Date());
        amber.setColour(amberData.getColor()!= null ? amberData.getColor():"amber");
        vehicleLoanAmberRepository.save(amber);
    }

    @Transactional
    public void deactivateAmberDeviation(Long amberId) {
        VehicleLoanAmber amber = vehicleLoanAmberRepository.findById(amberId)
            .orElseThrow(() -> new RuntimeException("Amber deviation not found"));

        amber.setActiveFlg("N");
        amber.setLastModDate(new Date());
        amber.setLastModUser(userSessionData.getEmployee().getPpcno());

        vehicleLoanAmberRepository.save(amber);
    }

    private String generateNextAmberCode(String wiNum, Long slno) {
        String highestCode = vehicleLoanAmberRepository.findHighestAmberCodeByWiNumAndSlno(wiNum, slno);
        if (highestCode == null) {
            return "RM0001";
        } else {
            int nextNumber = Integer.parseInt(highestCode.substring(2)) + 1;
            return String.format("RM%04d", nextNumber);
        }
    }

    public String getamberdatacolor(String wiNum, Long slno, String ambercode) {
        String ambercolor = "";
        Optional<String> amber=vehicleLoanAmberRepository.getambercolor(wiNum, slno, ambercode);
        if(amber.isPresent()){
            ambercolor = amber.get();
        }else{
            ambercolor = "failure";
        }

        return ambercolor;
    }

    public CRTAmberDataDTO getamberdatasforcrt(String winum, Long slno){
        String colorflg="";
        String racemessage="",fcvmessage="",cpvmessage="",cfrmessage="";

        int redcount=0;
        int amberdount=0;
        String racecolor = getamberdatacolor(winum,slno,"AMB006");
        if(racecolor.equals("amber")){
            racemessage="Race Score is in Amber Condition";
            amberdount=amberdount+1;
        }else if(racecolor.equals("red")){
            racemessage="Race Score is in Red Condition";
            redcount=redcount+1;
        }
        String fcvcolor = getamberdatacolor(winum,slno,"AMB009");
        if(fcvcolor.equals("amber")){
            fcvmessage="FCV Status is in Amber Condition";
            amberdount=amberdount+1;
        }else if(fcvcolor.equals("red")){
            fcvmessage="FCV Status is in Red Condition";
            redcount=redcount+1;
        }
        String cpvcolor = getamberdatacolor(winum,slno,"AMB010");
        if(cpvcolor.equals("amber")){
            cpvmessage="CPV Status is in Amber Condition";
            amberdount=amberdount+1;
        }else if(cpvcolor.equals("red")){
            cpvmessage="CPV Status is in Red Condition";
            redcount=redcount+1;
        }
        String cfrcolor = getamberdatacolor(winum,slno,"AMB011");
        if(cfrcolor.equals("amber")){
            cfrmessage="CFR Status is in Amber Condition";
            amberdount=amberdount+1;
        }else if(cfrcolor.equals("red")){
            cfrmessage="CFR Status is in Red Condition";
            redcount=redcount+1;
        }

        if(redcount>0){
            colorflg="red";
        }else if(amberdount>0){
            colorflg="amber";
        }else{
            colorflg="green";
        }

        CRTAmberDataDTO amberdto= new CRTAmberDataDTO();
        amberdto.setColor(colorflg);
        amberdto.setRacemessage(racemessage);
        amberdto.setFcvmessage(fcvmessage);
        amberdto.setCpvmessage(cpvmessage);
        amberdto.setCfrmessage(cfrmessage);
        return amberdto;

    }


    public List<VehicleLoanAmber> getAmberDeviationsByWiNumAndSlno(String wiNum, Long slno) {
       List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanAmberRepository.findActiveByWiNumAndSlnoAndColour(wiNum,slno);
       return vehicleLoanAmberList;
    }
   public List<Map<String,Object>> getAllActiveDeviationLevel() {
        return fetchRepository.findAllActiveDeviationLevel();
    }

     public List<VehicleLoanAmber> getPendingAmberDeviations(String wiNum, Long slno) {
       List<VehicleLoanAmber> vehicleLoanAmberList = vehicleLoanAmberRepository.findActiveByWiNumAndSlnoAndColourAndApprovingAuthIsEmpty(wiNum,slno,"amber");
       return vehicleLoanAmberList;
    }

}
