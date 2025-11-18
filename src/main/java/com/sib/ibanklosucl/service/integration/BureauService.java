package com.sib.ibanklosucl.service.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.losintegrator.LosRequest;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.utilies.UserSessionData;

import com.sib.ibanklosucl.model.VehicleLoanBlock;
import com.sib.ibanklosucl.service.VLBlockCodes;
import com.sib.ibanklosucl.service.VehicleLoanBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class BureauService {
    @Value("${api.integrator}")
    private String integratorEndpoint;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private BureauCheckService bureauCheckService;
    @Autowired
    private UserSessionData usd;

    @Value("${esb.ChannelID}")
    private String ChannelID;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;

    @Autowired
    private VehicleLoanBlockService vehicleLoanBlockService;


    public void BureauCheck(JsonNode panjs, String panName,String pan, String uid,String slno,String wiNum,String appid,String res_flg) throws JsonProcessingException {

        VehicleLoanBlock vehicleLoanBlock=new VehicleLoanBlock();
        vehicleLoanBlock.setWiNum(wiNum);
        vehicleLoanBlock.setSlno(Long.parseLong(slno));
        vehicleLoanBlock.setApplicantId(appid);

        LosRequest.BureauRequestDTO requestDTO=new LosRequest.BureauRequestDTO();
        requestDTO.setUuid(UUID.randomUUID().toString());
        requestDTO.setPan(pan);
        requestDTO.setMerchantCode(merchantCode);
        requestDTO.setMerchantName(merchantName);
        LosRequest.BureauRequest req=new LosRequest.BureauRequest();
        req.setRequest(requestDTO);
        req.setMock(false);
        req.setSlno(slno);
        req.setWorkItemNumber(wiNum);
        req.setOrigin(appid);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<LosRequest.BureauRequest> entity = new HttpEntity<>(req, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                integratorEndpoint,
                HttpMethod.POST,
                entity,
                String.class
        );
        String jsonInput = response.getBody();
        JsonNode rootNode = mapper.readTree(jsonInput);
        JsonNode messageNode = rootNode.path("Response").path("Body").path("message");
        String Status = rootNode.path("Response").path("Status").path("Status").asText();
        if(!"SUCCESS".equalsIgnoreCase(Status)){
            vehicleLoanBlock.setBlockType(VLBlockCodes.BUREAU_ID_DOWN);
            vehicleLoanBlock.setParticulars(pan);
            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);

            throw new ValidationException(ValidationError.COM001,"The Bureau Api Service is Temporarily unavailable.Please Try Again Later");
        }
        String error=messageNode.path("error").path("message").asText();
        if(!error.isBlank()){

            vehicleLoanBlock.setBlockType(VLBlockCodes.BUREAU_ID_DOWN);
            vehicleLoanBlock.setParticulars(pan+"~"+error);
            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
            throw new ValidationException(ValidationError.COM001,"Service Temporarily Down ("+error+"),Please Try After Sometime");
        }



        //{"ErrorResponse":{"Header":{"Timestamp":"20241122123714649","APIName":"bureau-pan-check-api","APIVersion":"1.0.0","Interface":"iib_BureauPANCheck_app"},"Status":{"Code":"","Desc":""},"AdditionalInfo":{"msg":""}}}
        String firstName = messageNode.path("firstName").asText();
        String middleName = messageNode.path("middleName").asText();
        String lastName = messageNode.path("lastName").asText();
        String maskedAadhaar = messageNode.path("maskedAadhaar").asText();
        String name = messageNode.path("name").asText();
        String status = messageNode.path("status").asText();
        if(status.equalsIgnoreCase("NO_RECORD_FOUND"))
        {

            vehicleLoanBlock.setBlockType(VLBlockCodes.BUREAU_ID_SOMEERROR);
            vehicleLoanBlock.setParticulars(pan);
            vehicleLoanBlockService.insertBlock(vehicleLoanBlock);
            throw new ValidationException(ValidationError.COM001,"Unable to Fetch PAN Details!!");
            //return;
        }
        bureauCheckService.saveBureauCheckResponse(
            wiNum,
            Long.parseLong(slno),
            Long.parseLong(appid),
            rootNode,
           usd.getPPCNo()  // or pass actual username
        );
        List<String> nameCombinations = Arrays.asList(
                firstName + " " + middleName + " " + lastName,
                firstName + " " + lastName + " " + middleName,
                middleName + " " + firstName + " " + lastName,
                middleName + " " + lastName + " " + firstName,
                lastName + " " + firstName + " " + middleName,
                lastName + " " + middleName + " " + firstName,
                firstName + " " + middleName,
                firstName + " " + lastName,
                middleName + " " + firstName,
                middleName + " " + lastName,
                lastName + " " + firstName,
                lastName + " " + middleName,
                name
        );

        boolean isMatch =true;// nameCombinations.stream().anyMatch(panName::equalsIgnoreCase);

        if (!isMatch) {
//            throw new ValidationException(ValidationError.COM001,"PAN name does not match any combination.");
        }
        if (panjs.path("seeding_status").asText().equals("Y") && ("R".equalsIgnoreCase(res_flg) || ("N".equalsIgnoreCase(res_flg) && !isMt(uid)))){
            boolean aadhaarLinked=messageNode.path("aadhaarLinked").asBoolean();
            if(panjs.path("seeding_status").asText().equals("Y")  && !aadhaarLinked){

                vehicleLoanBlock.setBlockType(VLBlockCodes.BUREAU_LINKSTATUS_MISMATCH);
                vehicleLoanBlock.setParticulars(pan+"~"+"Aadhaar is Linked as per NSDL but Not Linked as per Bureau Api");
                vehicleLoanBlock.setActualValue(last4(uid));
                vehicleLoanBlock.setExpectedValue(maskedAadhaar);
                vehicleLoanBlockService.insertBlock(vehicleLoanBlock);

                throw new ValidationException(ValidationError.COM001,"Aadhaar is Linked as per NSDL but Not Linked as per Bureau Api");
            }
            if(!last4(uid).equalsIgnoreCase(last4(maskedAadhaar))){
                vehicleLoanBlock.setBlockType(VLBlockCodes.AAD_LAST4_DIGIT);
                vehicleLoanBlock.setParticulars(pan);
                vehicleLoanBlock.setActualValue(last4(uid));
                vehicleLoanBlock.setExpectedValue(maskedAadhaar);
                vehicleLoanBlockService.insertBlock(vehicleLoanBlock);

                throw new ValidationException(ValidationError.COM001,"Aadhaar Entered Mismatch with Aadhaar Linked in PAN");
            };
        }

    }
    private String last4(String str){
        if(str==null)
            return "";
        else if(str.length()>4){
            return str.substring(str.length()-4);
        }
        else
            return str;
    }
    private boolean isMt(String str){
        if(str==null)
            return true;
        else if(str.trim().isEmpty())
            return true;
        else
            return false;
    }

}
