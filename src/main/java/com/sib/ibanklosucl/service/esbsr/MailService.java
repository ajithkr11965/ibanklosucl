package com.sib.ibanklosucl.service.esbsr;

import com.google.gson.Gson;
import com.sib.ibanklosucl.dto.MailRequest;
import com.sib.ibanklosucl.dto.MailResponseDto;
import com.sib.ibanklosucl.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MailService {
    private static final Gson gson=new Gson();
    @Autowired
    private RestTemplate restTemplate;
    @Value("${api.mailservice}")
    private String endpoint;

    public ResponseDTO sendMail(MailRequest mailRequest) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        // Create the HTTP entity
        HttpEntity<MailRequest> entity = new HttpEntity<>(mailRequest, headers);
        // Call the OCR API
        ResponseEntity<String> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                entity,
                String.class
        );

        MailResponseDto response1=gson.fromJson(response.getBody(), MailResponseDto.class);
        if(response1.getStatus().equalsIgnoreCase("SUCCESS")){
            return   new ResponseDTO("S",response1.getMessage());
        }
        else{
         return  new ResponseDTO("F",response1.getMessage());
        }
    }

}
