package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.CustomerVkycBasicDetailsDTO;
import com.sib.ibanklosucl.dto.VKYCDataDto;
import com.sib.ibanklosucl.dto.VkycSlnoAppidDto;
import com.sib.ibanklosucl.model.*;
import com.sib.ibanklosucl.repository.*;
import com.sib.ibanklosucl.service.vlsr.MisrctService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanBasicService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanKycService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class CustomerVkycService {
    @Autowired
    private CustomerVkycRepo vkycrepo;

    @Autowired
    private UIDRepository uidrepo;

    @Autowired
    private VehicleLoanKycService vlkycservice;

    @Autowired
    private VehicleLoanBasicService vlbasicservice;
    @Autowired
    private VLEmploymentService vlempservice;
    @Autowired
    private MisrctService misrctservice;
    @Autowired
    private VLEmploymentoccRepository vlemploymentoccrepository;
    @Autowired
    private FetchRepository fetchrepo;
    @Autowired
    private VehicleLoanMasterRepository vlmasrepo;

    public CustomerVkycBasicDetailsDTO getBasicDetails(String data) {
        VkycSlnoAppidDto slnoappi= fetchrepo.VkycGetSlnoAndAppid(data);
        CustomerVkycBasicDetailsDTO cusdto= vkycrepo.getBaseDetails(slnoappi.getSlno()!= null?slnoappi.getSlno():null,slnoappi.getAppid()!= null?slnoappi.getAppid():null);
        if(cusdto!=null) {
            String aadharrefno = cusdto.getAadharRefNum();
            cusdto.setAadharno(uidrepo.getUID(aadharrefno));
            return cusdto;
        }else
            return cusdto= new CustomerVkycBasicDetailsDTO();

    }
    public VKYCDataDto getDetails(String wiNum, Long slno, Long applicationId) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        VKYCDataDto dto = new VKYCDataDto();
        VKYCDataDto.AadhaarDetails aadhar = new VKYCDataDto.AadhaarDetails();
        VKYCDataDto.PanDetails pan = new VKYCDataDto.PanDetails();
        VKYCDataDto.CustomerDetails cust = new VKYCDataDto.CustomerDetails();

        VehicleLoanKyc vlkyc = vlkycservice.findByAppId(applicationId);
        VehicleLoanBasic vlbasic= vlbasicservice.findByAppId(applicationId);
        VLEmployment vlemp = vlempservice.findByApplicantIdAndDelFlg(applicationId);
        VLEmploymentocc vlocc = vlemploymentoccrepository.findByWiNumAndApplicantIdAndDelFlg(wiNum,applicationId,"N");
        VehicleLoanMaster vlmas = vlmasrepo.findByWiNumAndActiveFlg(wiNum,"Y").get();
        dto.setCommunicationSameAsPermanent(vlbasic.getSameAsPer().equals("Y"));

        aadhar.setAadhaarAddress("");
        aadhar.setAadhaarImage(vlkyc.getAadharimg());
        aadhar.setAadhaarPdf("");
        aadhar.setAadhaarName(vlkyc.getAadharName());
        aadhar.setAadhaarSex(vlbasic.getGender());
        aadhar.setAadhaarYob(sdf.format(vlbasic.getApplicantDob()));
        aadhar.setAadhaarRefNo(vlkyc.getAadharRefNum());

        pan.setPanCardName("");
        pan.setPanHolderTitle("");
        pan.setNameOnCard(vlkyc.getPanName());
        pan.setPanCardNum(vlkyc.getPanNo());
        pan.setAadhaarSeedingStatus("Y");

        cust.setName(vlbasic.getApplicantName());
        cust.setGender(vlbasic.getGender());
        cust.setSystemIp("");
        String occ = "";
        if(!vlemp.getEmployment_type().equals("SALARIED") && !vlemp.getEmployment_type().equals("PENSIONER") && !vlemp.getEmployment_type().equals("NONE")){
            occ = vlocc.getOccupationType();
        }else if(vlemp.getEmployment_type().equals("PENSIONER")){
            occ ="RETIRED";
        }else{
            occ="OTHERS";
        }
        cust.setOccupation(occ);
        Misrct rct = misrctservice.getByCodeValue("INC",vlbasic.getAnnualIncome());
        cust.setAnnualIncome(rct.getCodedesc());
        cust.setFatherName(vlbasic.getFatherName());
        cust.setMotherName(vlbasic.getMotherName());
        cust.setLanguage("");
        cust.setLatitude("");
        cust.setLongitude("");
        cust.setMaritalStatus(vlbasic.getMaritalStatus());
        cust.setSpouseName(vlbasic.getSpouseName());
        cust.setPermanentAddressline1(vlbasic.getAddr1());
        cust.setPermanentAddressline2(vlbasic.getAddr2());
        cust.setPermanentCity(vlbasic.getCitydesc());
        cust.setPermanentCityCode(vlbasic.getCity());
        cust.setPermanentCountry(vlbasic.getCountrydesc());
        cust.setPermanentCountryCode(vlbasic.getCountry());
        cust.setPermanentState(vlbasic.getStatedesc());
        cust.setPermanentStateCode(vlbasic.getState());
        cust.setPermanentPincode(vlbasic.getPin());
        cust.setBranch_sol_id(vlmas.getSolId());
        cust.setPermanentAddress(vlbasic.getAddr1()+", "+vlbasic.getAddr2()+", "+vlbasic.getCitydesc()+", "+vlbasic.getStatedesc()+", "+vlbasic.getCountrydesc()+", "+vlbasic.getPin());
        cust.setCommunicationAddress(vlbasic.getComAddr1()+", "+vlbasic.getComAddr2()+", "+vlbasic.getComCityedesc()+", "+vlbasic.getComStatedesc()+", "+vlbasic.getComCountrydesc()+", "+vlbasic.getComPin());
        cust.setCommunicationAddressline1(vlbasic.getComAddr1());
        cust.setCommunicationAddressline2(vlbasic.getComAddr2());
        cust.setCommunicationCity(vlbasic.getComCityedesc());
        cust.setCommunicationCityCode(vlbasic.getComCity());
        cust.setCommunicationCountry(vlbasic.getComCountrydesc());
        cust.setCommunicationCountryCode(vlbasic.getComCountry());
        cust.setCommunicationState(vlbasic.getComStatedesc());
        cust.setCommunicationStateCode(vlbasic.getComState());
        cust.setCommunicationPincode(vlbasic.getComPin());
        cust.setMobileNum(vlbasic.getMobileNo());
        cust.setPriorityStatus(true);
        cust.setApplicationDate(String.valueOf(vlbasic.getCmdate()));
        cust.setAccountScheme("SB Silver");
        cust.setEmail(vlbasic.getEmailId());

        dto.setAadhaarDetails(aadhar);
        dto.setPanDetails(pan);
        dto.setCustomerDetails(cust);

        return dto;
    }

}
