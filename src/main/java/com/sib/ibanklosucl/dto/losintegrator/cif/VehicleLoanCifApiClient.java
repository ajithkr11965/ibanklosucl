package com.sib.ibanklosucl.dto.losintegrator.cif;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class VehicleLoanCifApiClient {
    @Value("${api.integrator}")
    private String integratorEndpoint;
    public VehicleLoanCifResponse performCifCreation(VehicleLoanCifRequest vehicleLoanCifRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<VehicleLoanCifRequest> requestEntity = new HttpEntity<>(vehicleLoanCifRequest, headers);
        return restTemplate.postForObject(integratorEndpoint, requestEntity, VehicleLoanCifResponse.class);
    }
    /*
    public TdsResponse fetchTdsRateCode(TdsRequest tdsRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TdsRequest> requestEntity = new HttpEntity<>(tdsRequest, headers);
        return restTemplate.postForObject(integratorEndpoint, requestEntity, TdsResponse.class);
    }

     */
}
