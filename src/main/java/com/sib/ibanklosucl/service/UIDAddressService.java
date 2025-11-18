package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.AddressForm;
import com.sib.ibanklosucl.dto.UIDResponseModel;
import com.sib.ibanklosucl.model.Misrct;
import com.sib.ibanklosucl.model.PincodeMaster;
import com.sib.ibanklosucl.service.vlsr.MisrctService;
import com.sib.ibanklosucl.service.vlsr.PincodeMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UIDAddressService {

    @Autowired
    private MisrctService misrctService;
    @Autowired
    private PincodeMasterService pincodeMasterService;

        public AddressForm createAddress(UIDResponseModel uid){
            AddressForm adr=new AddressForm();
            String adrstr1 = beautifyAddress(uid.getHouseno(), uid.getStreet(), uid.getLocality());
            String adrstr2 = beautifyAddress(uid.getVtcname(), uid.getLandmark(), "");
            String[] address = createAddressstr(adrstr1, adrstr2).split("~");
            adr.setPermanentAddress1(address[0]);
            adr.setPermanentAddress2(address[1]);
            adr.setPermanentAddress3(address[2]);
            Optional<Misrct> state_master = misrctService.getByCodedesc("02",uid.getState().trim());
            Optional<Misrct> city_master = misrctService.getByCodedesc("01",uid.getDistrict().trim());
            Optional<Misrct> country_master = misrctService.getByCodedesc("03","IN");
            Optional<PincodeMaster> pincodeMaster=pincodeMasterService.findById(uid.getPincode());

            if(state_master.isPresent()){
                adr.setPermanentState(state_master.get().getCodedesc());
                adr.setPermanentStateCode(state_master.get().getCodevalue());
            }
            else if(pincodeMaster.isPresent()) {
                adr.setPermanentState(pincodeMaster.get().getFinacleState());
                adr.setPermanentStateCode(pincodeMaster.get().getFinacleStateCode());
                adr.setPermanentCity(pincodeMaster.get().getFinacleCity());
                adr.setPermanentCityCode(pincodeMaster.get().getFinacleCityCode());
            }
            else if(city_master.isPresent()){
                adr.setPermanentCity(city_master.get().getCodedesc());
                adr.setPermanentCityCode(city_master.get().getCodevalue());
            }
            adr.setPermanentCountry(country_master.get().getCodedesc());
            adr.setPermanentCountryCode(country_master.get().getCodevalue());
            return adr;
        }

    public  String beautifyAddress(String a, String b,String c) {
        if(a==null) a="";
        if(b==null) b="";
        if(c==null) c="";
        String address=a.trim().replaceAll(",","")+" , "+b.trim().replaceAll(",","") +" , "+c.trim().replaceAll(",","");
        return address.trim().replaceAll(",$","");
    }
    public  String createAddressstr(String _address1, String _address2) {
        String address1 = _address1;
        String address2 = _address2;
        String address3 = "";
        try {
            if (address1.length() > 45) {
                String address = address1 + " " + address2;
                char[] addressCharArray = address.toCharArray();
                int index = 0;
                for (int i = 45; i > 0; i--) {
                    if (addressCharArray[i] == ' ' || addressCharArray[i] == ',') {
                        index = i;
                        break;
                    }
                }
                address1 = address.substring(0, index);
                address2 = address.substring(index + 1);
            }
            if (address2.length() > 45) {
                String address = address2;
                char[] addressCharArray = address.toCharArray();
                int index2 = 0;
                for (int i = 45; i > 0; i--) {
                    if (addressCharArray[i] == ' ' || addressCharArray[i] == ',') {
                        index2 = i;
                        break;
                    }
                }
                address2 = address.substring(0, index2);
                address3 = address.substring(index2 + 1);
            }
            if (address3.isEmpty()) {
                return address1 + "~" + address2 + "~" + address2;
            } else {
                return address1 + "~" + address2 + "~" + address3.substring(0, Math.min(address3.length(), 45));
            }
        } catch (Exception e) {
            throw e;
        }
    }

}
