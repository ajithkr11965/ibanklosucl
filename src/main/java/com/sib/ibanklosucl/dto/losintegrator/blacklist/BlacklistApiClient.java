package com.sib.ibanklosucl.dto.losintegrator.blacklist;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BlacklistApiClient {
     @Value("${api.integrator}")
    private String integratorEndpoint;

     public BlacklistResponse performBlacklistCheck(BlacklistRequest blacklistRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<BlacklistRequest> requestEntity = new HttpEntity<>(blacklistRequest, headers);
        return restTemplate.postForObject(integratorEndpoint, requestEntity, BlacklistResponse.class);
    }
    public PartialBlacklistResponse performPartialBlacklistCheck(PartialBlacklistRequest blacklistRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PartialBlacklistRequest> requestEntity = new HttpEntity<>(blacklistRequest, headers);
        return restTemplate.postForObject(integratorEndpoint, requestEntity, PartialBlacklistResponse.class);
    }
}
