package com.sib.ibanklosucl.dto.losintegrator.WI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class WIApiClient {
    @Value("${api.integrator}")
    private String integratorEndpoint;
    public WIResponse performWiCreation(WIRequest wiRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WIRequest> requestEntity = new HttpEntity<>(wiRequest, headers);
        return restTemplate.postForObject(integratorEndpoint, requestEntity, WIResponse.class);
    }
}
