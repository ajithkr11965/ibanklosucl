package com.sib.ibanklosucl.service.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.sib.ibanklosucl.dto.DocumentRequest;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.exception.ValidationException;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.service.doc.ManDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class Docservice {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${api.docservice}")
    private String url;
    @Value("${api.docservicelegal}")
    private String legalUrl;
    @Value("${api.docserviceall}")
    private String allUrl;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FetchRepository fetchRepository;

    public String getCamPdf(DocumentRequest request) throws Exception {
        request.setDocumentType("CAM");
        return getPdf(request);
    }


    public ManDoc getAllPdf(DocumentRequest request) throws Exception {
//        request.setSlNo("714");
//        request.setWiNum("VLR_000000712");
        String url = UriComponentsBuilder.fromHttpUrl(allUrl)
                .queryParam("wiNum", request.getWiNum())
                .queryParam("slno", request.getSlNo())
                .queryParam("cmUser", request.getCmUser())
                .toUriString();
        return restTemplate.getForObject(url, ManDoc.class);
    }
    public JsonNode getLegalDoc(DocumentRequest request) throws Exception {
        request.setDocumentType("");
        return getDocPdf(request);
    }

    public String getSanctionPdf(DocumentRequest request) throws Exception {
        request.setDocumentType("SANCLTR");
    //    return fetchRepository.getDocument("206","SANCLTR");
        return getPdf(request);
    }
    public String getAgreement(DocumentRequest request) throws Exception {
        request.setDocumentType("AGMNT");
        return getPdf(request);
    }
    public String getMemorand(DocumentRequest request) throws Exception {
        request.setDocumentType("MEMORAND");
        return getPdf(request);
    }
    public String getAppForm(DocumentRequest request) throws Exception {
        request.setDocumentType("APPFORM");
        return getPdf(request);
    }

    public String getGrAgrmt(DocumentRequest request) throws Exception {
        request.setDocumentType("GRAGMNT");
        return getPdf(request);
    }
    public String getCamRbcpc(DocumentRequest request) throws Exception {
        request.setDocumentType("CAMRBCPC");
        return getPdf(request);
    }


    public String getPdf(DocumentRequest request) throws Exception {
        try {
            request.setChannel("DSA");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // Create the request entity
            HttpEntity<DocumentRequest> entity = new HttpEntity<>(request, headers);

            // Call the external API
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                     HttpMethod.POST,
                    entity,
                    String.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new ValidationException(ValidationError.COM001, response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new ValidationException(ValidationError.COM001, e.getResponseBodyAsString());
        }catch (Exception e) {
            throw new ValidationException(ValidationError.COM001, e.getMessage());
        }
    }


    public JsonNode getDocPdf(DocumentRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            // Create the request entity
            HttpEntity<DocumentRequest> entity = new HttpEntity<>(request, headers);
            // Call the external API
            //{
            //  "cmUser": "16023",
            //  "wiNum": "VLR_000000712",
            //  "channel": "DSA",
            //  "slNo": "714"
            //}
//            request.setSlNo("714");
//            request.setWiNum("VLR_000000712");

            ResponseEntity<String> response = restTemplate.exchange(
                    legalUrl,
                     HttpMethod.POST,
                    entity,
                    String.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode responses = objectMapper.readValue(response.getBody(), JsonNode.class);
                return responses;
            } else {
                throw new ValidationException(ValidationError.COM001, response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new ValidationException(ValidationError.COM001, e.getResponseBodyAsString());
        }catch (Exception e) {
            throw new ValidationException(ValidationError.COM001, e.getMessage());
        }
    }


}


