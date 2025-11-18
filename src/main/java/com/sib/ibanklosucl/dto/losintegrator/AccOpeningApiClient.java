package com.sib.ibanklosucl.dto.losintegrator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AccOpeningApiClient {
    @Value("${api.integrator}")
    private String integratorEndpoint;

    public AccOpeningResponse performAccountOpening(AccOpeningRequest accOpeningRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AccOpeningRequest> requestEntity = new HttpEntity<>(accOpeningRequest, headers);
        return restTemplate.postForObject(integratorEndpoint, requestEntity, AccOpeningResponse.class);
    }
}
