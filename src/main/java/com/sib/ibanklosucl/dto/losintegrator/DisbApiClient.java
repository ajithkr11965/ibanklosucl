package com.sib.ibanklosucl.dto.losintegrator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;



@Component
public class DisbApiClient {
    @Value("${api.integrator}")
    private String integratorEndpoint;

    public DisbResponse performDisbApi(DisbRequest disbRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DisbRequest> requestEntity = new HttpEntity<>(disbRequest, headers);
        return restTemplate.postForObject(integratorEndpoint, requestEntity, DisbResponse.class);
    }
    public DisbEnqResponse performDisbStatusEnquiryApi(DisbEnqRequest disbEnqRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DisbEnqRequest> requestEntity = new HttpEntity<>(disbEnqRequest, headers);
        return restTemplate.postForObject(integratorEndpoint, requestEntity, DisbEnqResponse.class);
    }
    public NeftResponse performNeftApi(NeftRequest neftRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<NeftRequest> requestEntity = new HttpEntity<>(neftRequest, headers);
        return restTemplate.postForObject(integratorEndpoint, requestEntity, NeftResponse.class);
    }
    public FIResponse performFI(FIRequest fiRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<FIRequest> requestEntity = new HttpEntity<>(fiRequest, headers);
        return restTemplate.postForObject(integratorEndpoint, requestEntity, FIResponse.class);
    }
}
