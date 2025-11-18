package com.sib.ibanklosucl.dto.dedup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DedupeApiClient {

    private final RestTemplate restTemplate;
    private final String dedupeApiUrl;

    public DedupeApiClient(
            RestTemplate restTemplate,
            @Value("${api.integrator}") String dedupeApiUrl) {
        this.restTemplate = restTemplate;
        this.dedupeApiUrl = dedupeApiUrl;
    }

    public DedupeResponse performDedupeCheck(DedupeRequest request) {
        return restTemplate.postForObject(dedupeApiUrl, request, DedupeResponse.class);
    }
}
