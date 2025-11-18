package com.sib.ibanklosucl.service.esbsr;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.sib.ibanklosucl.dto.esb.EsbEncRequest;
import com.sib.ibanklosucl.dto.esb.UdaiApiErrorCode;
import com.sib.ibanklosucl.utilies.Constants;
import com.sib.ibanklosucl.utilies.ESBEncDec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class EsbApiService {
    @Value("${esb.client_id}")
    private  String  client_id;
    @Value("${esb.client_secretkey}")
    private  String  client_secretkey;

    @Autowired
    private ESBEncDec esbEncDec;
    @Autowired
    private RestTemplate restTemplate;


    private static final Gson gson = new Gson();
    public ResponseEntity<String> ApiService(String apiurl,String reqjson, String id) throws Exception{

        log.info("Request Sent to ESB URL {} : {} ~{}",apiurl,reqjson,id);
        String encryptedrequest= esbEncDec.encrypt(reqjson);
        EsbEncRequest encrypt=new EsbEncRequest();
        encrypt.setRequest(encryptedrequest);
        String encjson=gson.toJson(encrypt).toString();
        log.info("Encrypted Request Sent to ESB URL {} :  {} ~{}",apiurl,encjson,id);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("charset", "UTF-8");
        headers.set("Content-Length", Integer.toString(encjson.length()));
        headers.set("SIB-Client-Id", client_id);
        headers.set("SIB-Client-Secret", client_secretkey);
     //   headers.set("GlobalTranID", globaltranid);
        // Create an HttpEntity object with the request body and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(encjson, headers);
        // Make the request and receive the response
        ResponseEntity<String> response = restTemplate.exchange(apiurl, HttpMethod.POST, requestEntity, String.class);
        log.info("Response Received From ESB URL {} :  {}~{}",apiurl,response,id);
        return response;
    }
    public ResponseEntity<String> IntApiService(String apiurl,String reqjson, String id) throws Exception{

        log.info("Request Sent to ESB URL {} : {} ~{}",apiurl,reqjson,id);
//        String encryptedrequest= esbEncDec.encrypt(reqjson);
//        EsbEncRequest encrypt=new EsbEncRequest();
//        encrypt.setRequest(encryptedrequest);
//        String encjson=gson.toJson(encrypt).toString();
//        log.info("Encrypted Request Sent to ESB URL {} :  {} ~{}",apiurl,encjson,id);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("charset", "UTF-8");
        headers.set("Content-Length", Integer.toString(reqjson.length()));
        headers.set("SIB-Client-Id", client_id);
        headers.set("SIB-Client-Secret", client_secretkey);
       // headers.set("GlobalTranID", globaltranid);
        // Create an HttpEntity object with the request body and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(reqjson, headers);
        // Make the request and receive the response
        ResponseEntity<String> response = restTemplate.exchange(apiurl, HttpMethod.POST, requestEntity, String.class);
        log.info("Response Received From ESB URL {} :  {}~{}",apiurl,response,id);
        return response;
    }

    public JsonObject APIValidator(ResponseEntity<String> request,String apiurl,String id){
        JsonObject response=new JsonObject();
        String msg="";
        try{

            if (!request.getStatusCode().equals(HttpStatus.OK) && !request.getStatusCode().equals(HttpStatus.CREATED)) {
                msg= Constants.Messages.SERVICE_UNAVAILABLE;
                log.error("Invalid Response Code received from  ESB {} ~{}", request.getStatusCode(), id);
            } else {
                JsonObject responsejson = gson.fromJson(request.getBody(), JsonObject.class);
                if (responsejson.has("ErrorResponse")) {
                    msg= Constants.Messages.SOMETHING_ERROR;
                    log.error("Invalid Response received from  ESB @\"ErrorResponse\" ~{}", id);
                } else {
                    if (responsejson.has("Response")) {
                        JsonObject ecresponse = gson.fromJson(esbEncDec.decrypt(responsejson.get("Response").getAsString()), JsonObject.class);
                        log.info("Decrypted response received from  ESB Url {} :{} ~{}", apiurl, gson.toJson(ecresponse), id);
                        if (ecresponse.getAsJsonObject("Response").getAsJsonObject("Status").get("Code").getAsString().equals("200") || ecresponse.getAsJsonObject("Response").getAsJsonObject("Status").get("Code").getAsString().equals("201") ) {
                            msg = "OK";
                            response.add("Body", ecresponse.getAsJsonObject("Response").getAsJsonObject("Body"));
                        } else {
                            if(ecresponse.getAsJsonObject("Response").has("Body") && ecresponse.getAsJsonObject("Response").getAsJsonObject("Body").has("Description")){
                                String Desc=ecresponse.getAsJsonObject("Response").getAsJsonObject("Body").get("Description").getAsString();
                                msg = Constants.Messages.SOMETHING_ERROR;
                            }
                            else {
                                msg = Constants.Messages.SOMETHING_ERROR;
                            }
                            log.error("Invalid Status Code in  Response received from  ESB ~{}", id);
                        }
                    }
                    else{
                        msg= Constants.Messages.SOMETHING_ERROR;
                        log.error("Invalid Response received from  ESB @\"Response\"  ~{}", id);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("Error while validating response from  ESB :{} ~{}",e,id);
        }
        response.addProperty("msg",msg);
        log.info("API Validator Response : {} ~{}",gson.toJson(response), id);
        return response;
    }
    public JsonObject losAPIValidator(ResponseEntity<String> request,String apiurl,String id){
        JsonObject response=new JsonObject();
        String msg="";
        try{

            if (!request.getStatusCode().equals(HttpStatus.OK) && !request.getStatusCode().equals(HttpStatus.CREATED)) {
                msg= Constants.Messages.SERVICE_UNAVAILABLE;
                log.error("Invalid Response Code received from  ESB {} ~{}", request.getStatusCode(), id);
            } else {
                JsonObject responsejson = gson.fromJson(request.getBody(), JsonObject.class);
                if (responsejson.has("ErrorResponse")) {
                    msg= Constants.Messages.SOMETHING_ERROR;
                    log.error("Invalid Response received from  ESB @\"ErrorResponse\" ~{}", id);
                } else {
                    if (responsejson.has("Response")) {
                        JsonObject ecresponse =responsejson;// gson.fromJson(responsejson.get("Response").getAsString(), JsonObject.class);
                      //  log.info("Decrypted response received from  ESB Url {} :{} ~{}", apiurl, gson.toJson(ecresponse), id);
                        if (ecresponse.getAsJsonObject("Response").getAsJsonObject("Status").get("Code").getAsString().equals("200") || ecresponse.getAsJsonObject("Response").getAsJsonObject("Status").get("Code").getAsString().equals("201") ) {
                            msg = "OK";
                            response.add("Body", ecresponse.getAsJsonObject("Response").getAsJsonObject("Body"));
                        } else {
                            if(ecresponse.getAsJsonObject("Response").has("Body") && ecresponse.getAsJsonObject("Response").getAsJsonObject("Body").has("Description")){
                                String Desc=ecresponse.getAsJsonObject("Response").getAsJsonObject("Body").get("Description").getAsString();
                                msg = Constants.Messages.SOMETHING_ERROR;
                            }
                            else if(ecresponse.getAsJsonObject("Response").has("Body")  && ecresponse.getAsJsonObject("Response").getAsJsonObject("Body").has("errorCode")){
                                msg = UdaiApiErrorCode.fromCode(ecresponse.getAsJsonObject("Response").getAsJsonObject("Body").get("errorCode").getAsString());
                            }
                            else {
                                msg = Constants.Messages.SOMETHING_ERROR;
                            }
                            log.error("Invalid Status Code in  Response received from  ESB ~{}", id);

                        }
                    }
                    else{
                        msg= Constants.Messages.SOMETHING_ERROR;
                        log.error("Invalid Response received from  ESB @\"Response\"  ~{}", id);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("Error while validating response from  ESB :{} ~{}",e,id);
        }
        response.addProperty("msg",msg);
        log.info("API Validator Response : {} ~{}",gson.toJson(response), id);
        return response;
    }


    public JsonObject EsbAPIValidator(ResponseEntity<String> request,String apiurl) throws Exception{
        JsonObject response=new JsonObject();
        String msg="";
         if (!request.getStatusCode().equals(HttpStatus.OK) && !request.getStatusCode().equals(HttpStatus.CREATED)) {
                msg= Constants.Messages.SERVICE_UNAVAILABLE;
            }
         else {
                JsonObject responsejson = gson.fromJson(request.getBody(), JsonObject.class);
                if (responsejson.has("ErrorResponse")) {

                    msg= Constants.Messages.SOMETHING_ERROR;
                } else {
                    if (responsejson.has("Response")) {
                        JsonObject ecresponse = responsejson.get("Response").getAsJsonObject();
                        if (ecresponse.getAsJsonObject("Status").get("Code").getAsString().equals("200") || ecresponse.getAsJsonObject("Status").get("Code").getAsString().equals("201") ) {
                            msg = "OK";
                            response.add("Body", ecresponse.getAsJsonObject("Body"));
                        } else {
                            if(ecresponse.has("Body") && ecresponse.getAsJsonObject("Body").has("Description")){
                                String Desc=ecresponse.getAsJsonObject("Body").get("Description").getAsString();
                                msg = Desc;
                            }
                            else {
                                msg = Constants.Messages.SOMETHING_ERROR;
                            }
                        }
                    }
                    else{
                        msg= Constants.Messages.SOMETHING_ERROR;
                    }
                }
            }

        response.addProperty("msg",msg);
        return response;
    }


}
