package com.sib.ibanklosucl.service.ocrsv;

import com.google.gson.Gson;
import com.sib.ibanklosucl.dto.TabRequestDTO;
import com.sib.ibanklosucl.dto.losintegrator.LosRequest;
import com.sib.ibanklosucl.dto.ocr.FileRequest;
import com.sib.ibanklosucl.dto.ocr.OCRResponse;
import com.sib.ibanklosucl.dto.ocr.OcrParsed;
import com.sib.ibanklosucl.model.VehicleLoanKyc;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanKycService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OCRService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private VehicleLoanKycService vehicleLoanKycService;
    private static final Gson gson=new Gson();
    @Value("${api.integrator}")
    private String ocrendpoint;


    @Value("${app.dev-mode:true}")
    private boolean devMode;


    public OcrParsed processFile(TabRequestDTO fr) {
        // Prepare the headers
        FileRequest fileRequest=fr.getFileRequest();
        fileRequest.getDocumentMeta().setApplicationType("LOS");
        LosRequest.OCR ocr=new LosRequest.OCR();
//        if(devMode) {
//                fileRequest.getDocumentMeta().setApplicationType("TEST");
//        }
        ocr.setRequest(fileRequest);
        ocr.setMock(false);
        ocr.setOrigin(fr.getAppid());
        ocr.setWorkItemNumber(fr.getWinum());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        // Create the HTTP entity
        HttpEntity<LosRequest.OCR> entity = new HttpEntity<>(ocr, headers);

        // Call the OCR API
        ResponseEntity<String> response = restTemplate.exchange(
                ocrendpoint,
                HttpMethod.POST,
                entity,
                String.class
        );

        OCRResponse response1=gson.fromJson(response.getBody(),OCRResponse.class);
        return parseResponse(response1, fileRequest.getFileType(),fr.getAppid());
    }

    private OcrParsed parseResponse(OCRResponse response,String type,String appid){
        if(!response.getStatus().equalsIgnoreCase("SUCCESS"))
            return Convert(response);
        switch (type){
            case "PAN": return PanParser(response,appid);
            case "AADHAAR": return UIDParser(response,appid);
            case "PASSPORT": return PassportParser(response);
            default: return Convert(response);
        }
    }

    private OcrParsed PanParser(OCRResponse response,String appid){
        Optional<OCRResponse.Document> panDocument = response.getDocuments().stream()
                .filter(document -> "PAN".equalsIgnoreCase(document.getDocumentType()))
                .findFirst();
        OcrParsed parsed=Convert(response);
        OcrParsed.PanDoc parsedResponse = new OcrParsed.PanDoc();
        panDocument.ifPresent(document -> {
            parsedResponse.setName(document.getOcrData().getName());
            parsedResponse.setDob(CommonUtils.DateFormat(document.getOcrData().getDob(),"dd/MM/yyyy","yyyy-MM-dd"));
            parsedResponse.setPan(document.getOcrData().getPan());
        });
        parsed.setPan(parsedResponse);

        return parsed;
    }
    private OcrParsed UIDParser(OCRResponse response,String appid){
        List<OCRResponse.Document> uiddoc = response.getDocuments().stream()
                .filter(document -> "AADHAAR".equalsIgnoreCase(document.getDocumentType())).collect(Collectors.toList());
        OcrParsed parsed=Convert(response);
        OcrParsed.Uid parsedResponse = new OcrParsed.Uid();
        parsedResponse.setDocumentProcessed(response.getDocumentProcessed());
        uiddoc.forEach(document -> {
            if(document.getOcrData().getName()!=null)
                parsedResponse.setName(document.getOcrData().getName());
            if(document.getOcrData().getDob()!=null)
                parsedResponse.setDob(document.getOcrData().getDob());
            if(document.getOcrData().getYob()!=null)
                 parsedResponse.setDob(document.getOcrData().getYob());
            parsedResponse.setAadhaar(document.getOcrData().getAadhaar());
        });
        parsed.setUid(parsedResponse);
        VehicleLoanKyc kyc=vehicleLoanKycService.findByAppId(Long.valueOf(appid));
        if(response.getStatus().equals("SUCCESS") && (response.getDocumentProcessed()==null || response.getDocumentProcessed().isBlank())){
            return new OcrParsed("FAILURE","Aadhaar Masked Image Missing!!");
        }
        kyc.setAadharimg(response.getDocumentProcessed());
        kyc.setAadharext("pdf");
        vehicleLoanKycService.save(kyc);
        return parsed;
    }
    private OcrParsed PassportParser(OCRResponse response){

        List<OCRResponse.Document> doc = response.getDocuments().stream()
                .filter(document -> "PASSPORT".equalsIgnoreCase(document.getDocumentType())).collect(Collectors.toList());
        OcrParsed parsed=Convert(response);
        OcrParsed.Passport parsedResponse = new OcrParsed.Passport();

        doc.forEach(document -> {
            if(document.getOcrData().getPassportNumber()!=null)
                parsedResponse.setPassportNumber(document.getOcrData().getPassportNumber());
            if(document.getOcrData().getGivenName()!=null)
                parsedResponse.setGivenName(document.getOcrData().getGivenName());
            if(document.getOcrData().getDoe()!=null)
                 parsedResponse.setDoe(CommonUtils.DateFormat(document.getOcrData().getDoe(),"dd/MM/yyyy","yyyy-MM-dd"));
        });
        parsed.setPassport(parsedResponse);
        return parsed;
    }

    private OcrParsed Convert(OCRResponse response){
        OcrParsed parsed=new OcrParsed();
        parsed.setStatus(response.getStatus());
        parsed.setErrorMessage(response.getErrorMessage());
        return parsed;
    }

}
