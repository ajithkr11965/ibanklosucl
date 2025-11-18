package com.sib.ibanklosucl.service.esbsr;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sib.ibanklosucl.dto.ResponseDTO;
import com.sib.ibanklosucl.dto.TabRequestDTO;
import com.sib.ibanklosucl.dto.esb.PanNsdlBody;
import com.sib.ibanklosucl.dto.losintegrator.LosRequest;
import com.sib.ibanklosucl.dto.ocr.OcrParsed;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.Constants;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PanNsdlService {
    @Value("${esb.MerchantCode}")
    private String  ChannelID;
    @Value("${esb.MerchantName}")
    private String  ChannelName;
//    @Value("${api.esb.pannsdl}")
//    private String  pannsdl;
@Value("${api.integrator}")
private String integratorurl;

    @Autowired
    private EsbRequestBuilder reqbuilder;

    @Autowired
    private EsbApiService apiService;
    @Autowired
    private UserSessionData usd;

    private static final Gson gson=new Gson();
    public  ResponseDTO PanValidator(TabRequestDTO pf, String id){
        try {
            OcrParsed.PanDoc panform=pf.getPanDoc();
            String uuidnum=CommonUtils.uidgenerate();
            PanNsdlBody panNsdlBody = new PanNsdlBody();
            panNsdlBody.setPan(panform.getPan());
            panNsdlBody.setName(panform.getName().toUpperCase());
            panNsdlBody.setDob(CommonUtils.DateFormat(panform.getDob(),"yyyy-MM-dd","dd/MM/yyyy"));
            panNsdlBody.setUUID(uuidnum);
            panNsdlBody.setMerchantName(ChannelName);
            panNsdlBody.setMerchantCode(ChannelID);

            LosRequest.PanNsdl requestjson=new LosRequest.PanNsdl();
            requestjson.setRequest(panNsdlBody);
            requestjson.setOrigin(pf.getAppid());
            requestjson.setWorkItemNumber(pf.getWinum());
            requestjson.setSlno(pf.getSlno());
            String panjson = gson.toJson(requestjson);
            ResponseEntity<String> panresponse = apiService.IntApiService(integratorurl, panjson, id);
            JsonObject pannsdljson = apiService.losAPIValidator(panresponse, integratorurl, id);
            String errormsg="";
            usd.putData(pf.getAppid(),"PAN_NSDL","");
            if(pannsdljson.get("msg").getAsString().equals("OK")){
                if(pannsdljson.getAsJsonObject("Body").get("UUID").getAsString().equals(uuidnum)) {
                    JsonObject Panjson = pannsdljson.getAsJsonObject("Body").getAsJsonArray("outputData").get(0).getAsJsonObject();
                    if (!Panjson.get("pan_status").getAsString().equals("E")) {
                        errormsg = Constants.Messages.PAN_INVALID;
                    } else if (!Panjson.get("pan").getAsString().equals(panform.getPan())) {
                        errormsg = Constants.Messages.PAN_INVALID;
                    } else if (!Panjson.get("dob").getAsString().equals("Y")) {
                        errormsg = Constants.Messages.PAN_INVALID;
                    }
//                    else if (!Panjson.get("name").getAsString().equals("Y")) {
//                        errormsg = Constants.Messages.PAN_INVALID;
//                    }
//                    else if (!Panjson.get("seeding_status").getAsString().equals("Y")) {
//                        errormsg = Constants.Messages.PAN_INVALID;
//                    }
                    else{
                        usd.putData(pf.getAppid(),"PAN_NSDL",Panjson.toString());
                        return new ResponseDTO("S","");
                    }
                }
                    else {
                        errormsg=Constants.Messages.SOMETHING_ERROR;
                    }
                }
                else{
                    errormsg=pannsdljson.get("msg").getAsString();
                }
            return new ResponseDTO("F",errormsg);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseDTO("F", Constants.Messages.SOMETHING_ERROR);
    }
}
