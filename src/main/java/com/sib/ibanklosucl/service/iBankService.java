package com.sib.ibanklosucl.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sib.ibanklosucl.dto.SpreadDTO;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.model.misprm;
import com.sib.ibanklosucl.repository.MisprmRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class iBankService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MisprmRepository misprmRepository;

    private static final Gson gson=new Gson();

    @Value("${api.ibank.spread}")
    private String spreadUrl;

    @Value("${api.ibank.ltv}")
    private String ltvUrl;

    ObjectMapper objectMapper=new ObjectMapper();

    public SpreadDTO getSpread(String interestType, String channelId, String score, String tenure,String loanProgram) throws Exception {
        String url = spreadUrl;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("score", score)
                .queryParam("tenure", tenure);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String body = String.format("{\"channel_id\":\"%s\",\"score\":\"%s\",\"tenure\":\"%s\",\"interest_type\":\"%s\",\"program_name\":\"%s\"}",
                channelId, score, tenure, interestType,loanProgram);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, String.class);
        //SpreadDTO spreadDTO=gson.fromJson(responseEntity.getBody(),SpreadDTO.class);
        SpreadDTO spreadDTO=objectMapper.readValue(responseEntity.getBody(),SpreadDTO.class);
        if(responseEntity.getStatusCode().is2xxSuccessful())
            return spreadDTO;
        else
            throw new ValidationException(ValidationError.COM001,"Invalid Response Recieved for Spread");
    }


    @SneakyThrows
    public HashMap<String, String> getAllSpreadValues(String interestType, String channelId, String score, String tenure,String prgramName) {
        String response = String.valueOf(getSpread(interestType, channelId, score, tenure,prgramName));

        HashMap<String, String> resultSet = new HashMap<>();

        if (response == null || response.isEmpty()) {
            resultSet.put("ERRORMESSAGE", "Empty or null response");
            return resultSet;
        }

        if (!response.contains("ERROR")) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonResponse = objectMapper.readTree(response);
                if (jsonResponse.has("status") && !jsonResponse.get("status").asText().equals("SUCCESS")) {
                    resultSet.put("ERRORMESSAGE", "NO Card Rate found");
                } else {
                    Iterator<Map.Entry<String, JsonNode>> fields = jsonResponse.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> field = fields.next();
                        resultSet.put(field.getKey(), field.getValue().asText());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // Print stack trace for debugging
                resultSet.put("ERRORMESSAGE", "Invalid JSON response");
            }
        } else {
            resultSet.put("ERRORMESSAGE", response);
        }

        return resultSet;
    }



    public String getLTVDetails(String vehicleMake, String channelId, String program, String tenure, String vehicleModel, String employmentType, String amount) {
        String url = ltvUrl;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("tenure", tenure)
                .queryParam("vehicle_model", vehicleModel)
                .queryParam("employment_type", employmentType)
                .queryParam("amount", amount);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String body = String.format("{\"channel_id\":\"%s\",\"vehicle_make\":\"%s\",\"tenure\":\"%s\",\"vehicle_model\":\"%s\",\"employment_type\":\"%s\",\"program\":\"%s\",\"amount\":\"%s\"}",
                channelId, vehicleMake, tenure, vehicleModel, employmentType, program, amount);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        return String.valueOf(restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, String.class));
    }



    public String getLTVDetail(String vehicleMake, String channelId, String program, String tenure, String vehicleModel, String employmentType, String amount) throws ValidationException {
        String url = ltvUrl;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String body = String.format("{\"channel_id\":\"%s\",\"vehicle_make\":\"%s\",\"tenure\":\"%s\",\"vehicle_model\":\"%s\",\"employment_type\":\"%s\",\"program\":\"%s\",\"amount\":\"%s\"}",
                channelId, vehicleMake, tenure, vehicleModel, employmentType, program, amount);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response =restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        JsonObject resp=gson.fromJson(response.getBody(),JsonObject.class);
        if(!resp.get("status").getAsString().equals("SUCCESS")){
            throw new ValidationException(ValidationError.COM001,resp.get("REASON").getAsString());
        }
        return resp.get("ltv").getAsString() ;
    }

    public misprm getMisPRM(String pcode)
    {
        return misprmRepository.findByPCODE(pcode);
    }

}
