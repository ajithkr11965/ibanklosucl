package com.sib.ibanklosucl.service.mssf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sib.ibanklosucl.dto.DOC_ARRAY;
import com.sib.ibanklosucl.dto.mssf.*;
import com.sib.ibanklosucl.exception.MssfApiException;
import com.sib.ibanklosucl.utilies.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MssfDocumentService {

    @Value("${api.integrator}")
    private String integratorEndpoint;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${esb.MerchantName}")
    private String merchantName;
    @Value("${esb.MerchantCode}")
    private String merchantCode;
    @Autowired
    private ObjectMapper objectMapperMock;

    @Value("${document.storage.path}")
    private String storagePath;

    public MssfDocProcessResponse processDocuments(String losId) {
        log.info("Starting document processing for losId: {}", losId);
        try {
            // First, try to fetch from API
            try {
                return processDocumentsFromAPI(losId);
            } catch (MssfApiException apiException) {
                log.warn("API fetch failed for losId: {}. Attempting local fallback. Error: {}",
                        losId, apiException.getMessage());

                // If API fails, try to get from local directory
                return processDocumentsFromLocal(losId);
            }

        } catch (Exception e) {
            log.error("Complete document processing failed for losId: {}", losId, e);
            // Log the exact location where the error occurred
            log.error("Exception in processDocuments() with cause = '{}' and exception = '{}'",
                    e.getCause() != null ? e.getCause().toString() : "NULL", e.getMessage());
            throw new MssfApiException("Document processing failed: " + e.getMessage());
        }
    }

//    public MssfDocProcessResponse processDocumentsOld(String losId) {
//        try {
//            // 1. Fetch Documents
//            MssfDocFetchResponse fetchResponse = fetchDocuments(losId);
//            validateFetchResponse(fetchResponse);
//            List<MssfDocFetchResponse.Document> documents = fetchResponse.getResponse()
//                    .getBody()
//                    .getMessage()
//                    .getDocuments();
//            if (documents == null || documents.isEmpty()) {
//                throw new MssfApiException("No documents available for processing");
//            }
//            log.info("Successfully fetched {} documents for losId: {}", documents.size(), losId);
//            // 2. Store Documents
//            storeDocuments(documents, losId);
//            List<DOC_ARRAY> docArrayList = documents.stream().map(doc -> {
//                DOC_ARRAY docArray = new DOC_ARRAY();
//                docArray.setDOC_EXT(doc.getFile_name().substring(doc.getFile_name().lastIndexOf('.') + 1));
//                String actfullname = doc.getFile_name().replaceAll("\\s+", "_");
//                String nameWithoutExt = actfullname.substring(0, actfullname.lastIndexOf('.'));
//                String finalName = nameWithoutExt.startsWith("MSSF_") ? nameWithoutExt : "MSSF_" + nameWithoutExt;
//                docArray.setDOC_NAME(finalName + CommonUtils.getCurrentTimestamp());
//                docArray.setDOC_BASE64(Base64.getEncoder().encodeToString(doc.getDoc_data()));
//                return docArray;
//            }).collect(Collectors.toList());
//
//
//            // 3. Acknowledge Documents
//            MssfDocAckResponse ackResponse = acknowledgeDocuments(losId, documents);
//            // for testing enable this  MssfDocAckResponse ackResponse = demoAcknowledgeDocuments(losId, documents);
//
//            return buildSuccessResponse(documents.size(), docArrayList);
//        } catch (MssfApiException e) {
//            log.error("MSSF API error for losId: {}. Error: {}", losId, e.getMessage());
//            throw e;
//        } catch (Exception e) {
//            log.error("Document processing failed for losId: {}", losId, e);
//            throw new MssfApiException("Document processing failed: " + e.getMessage());
//        }
//    }
    private MssfDocProcessResponse processDocumentsFromAPI(String losId) {
        try {
            log.info("Attempting to fetch documents from API for losId: {}", losId);

            // 1. Fetch Documents from API
            MssfDocFetchResponse fetchResponse = fetchDocuments(losId);
            ///validateFetchResponse(fetchResponse);

            List<MssfDocFetchResponse.Document> documents = fetchResponse.getResponse()
                    .getBody()
                    .getMessage()
                    .getDocuments();

            if (documents == null || documents.isEmpty()) {
                throw new MssfApiException("No documents available for processing from API");
            }

            log.info("Successfully fetched {} documents from API for losId: {}", documents.size(), losId);

            // 2. Store Documents
            storeDocuments(documents, losId);

            // 3. Convert to DOC_ARRAY format
            List<DOC_ARRAY> docArrayList = convertToDocArray(documents);

            // 4. Acknowledge Documents
            MssfDocAckResponse ackResponse = acknowledgeDocuments(losId, documents);

            return buildSuccessResponse(documents.size(), docArrayList, "Documents processed successfully from API");

        } catch (Exception e) {
            log.error("API processing failed for losId: {} at step: {}", losId, getCurrentProcessingStep(e));
            throw e;
        }
    }
    private MssfDocProcessResponse processDocumentsFromLocal(String losId) {
        try {
            log.info("Attempting to fetch documents from local directory for losId: {}", losId);

            List<StoredDocument> storedDocs = getStoredDocuments(losId);

            if (storedDocs.isEmpty()) {
                throw new MssfApiException("No documents found in local directory for losId: " + losId +
                        ". Both API and local fallback failed.");
            }

            log.info("Found {} documents in local directory for losId: {}", storedDocs.size(), losId);

            // Convert stored documents to DOC_ARRAY format
            List<DOC_ARRAY> docArrayList = convertStoredDocumentsToDocArray(storedDocs, losId);

            return buildSuccessResponse(storedDocs.size(), docArrayList,
                    "Documents processed successfully from local directory (API fallback)");

        } catch (Exception e) {
            log.error("Local processing failed for losId: {}", losId, e);
            throw new MssfApiException("Failed to process documents from local directory: " + e.getMessage());
        }
    }
    private List<DOC_ARRAY> convertStoredDocumentsToDocArray(List<StoredDocument> storedDocs, String losId) {
        return storedDocs.stream().map(storedDoc -> {
            try {
                DOC_ARRAY docArray = new DOC_ARRAY();

                // Extract extension from filename
                String fileName = storedDoc.getFileName();
                String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                docArray.setDOC_EXT(extension);

                // Set document name
                String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
                docArray.setDOC_NAME(nameWithoutExt + CommonUtils.getCurrentTimestamp());

                // Read file content and encode to base64
                String filePath = storagePath + "/" + losId + "/" + fileName;
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
                docArray.setDOC_BASE64(Base64.getEncoder().encodeToString(fileContent));

                return docArray;
            } catch (IOException e) {
                log.error("Failed to read stored document: {} for losId: {}", storedDoc.getFileName(), losId, e);
                throw new RuntimeException("Failed to read stored document: " + storedDoc.getFileName(), e);
            }
        }).collect(Collectors.toList());
    }
    private List<DOC_ARRAY> convertToDocArray(List<MssfDocFetchResponse.Document> documents) {
        return documents.stream().map(doc -> {
            DOC_ARRAY docArray = new DOC_ARRAY();
            docArray.setDOC_EXT(doc.getFile_name().substring(doc.getFile_name().lastIndexOf('.') + 1));
            String actfullname = doc.getFile_name().replaceAll("\\s+", "_");
            String nameWithoutExt = actfullname.substring(0, actfullname.lastIndexOf('.'));
            String finalName = nameWithoutExt.startsWith("MSSF_") ? nameWithoutExt : "MSSF_" + nameWithoutExt;
            docArray.setDOC_NAME(finalName + CommonUtils.getCurrentTimestamp());
            docArray.setDOC_BASE64(Base64.getEncoder().encodeToString(doc.getDoc_data()));
            return docArray;
        }).collect(Collectors.toList());
    }



    private void validateFetchResponse(MssfDocFetchResponse response) {
        if (response == null || response.getResponse() == null) {
            throw new MssfApiException("Unable to process your request at this moment. Please try again later.");
        }

        MssfDocFetchResponse.Status status = response.getResponse().getStatus();
        MssfDocFetchResponse.Body body = response.getResponse().getBody();

        // For logging - technical details
        StringBuilder technicalError = new StringBuilder();
        // For user - friendly message
        StringBuilder userMessage = new StringBuilder();

        if (!"200".equals(status.getCode())) {
            technicalError.append(String.format("Status Code: %s, Description: %s",
                    status.getCode(), status.getDesc()));
        }

        // Check Body Message for Additional Error Details
        if (body != null && body.getMessage() != null) {
            if (!body.getMessage().isSuccess()) {
                if (body.getMessage().getErrors() != null && !body.getMessage().getErrors().isEmpty()) {
                    MssfDocFetchResponse.Error error = body.getMessage().getErrors().get(0);

                    // Technical details for logging
                    if (technicalError.length() > 0) technicalError.append(". ");
                    technicalError.append(String.format("Error Details: Code: %d, Message: %s",
                            error.getErrorCode(), error.getErrorMessage()));

                    // Use vendor's error message directly
                    userMessage.append(error.getErrorMessage());
                }
            }
        }

        if (technicalError.length() > 0) {
            // Log the detailed technical error
            log.error("Document fetch failed: {}", technicalError.toString());

            // If no specific user message was set, use a default one
            if (userMessage.length() == 0) {
                userMessage.append("We're experiencing technical difficulties. Please try again later.");
            }

            // Use UUID and LOS ID from response body
            String uuid = body != null ? body.getUUID() : "UNKNOWN";
            String losId = (body != null && body.getMessage() != null) ? body.getMessage().getLos_id() : "UNKNOWN";

            // Log with UUID and LOS ID
            log.error("UUID: {}, LOS ID: {} - {}", uuid, losId, technicalError.toString());

            // Append UUID to user message
            userMessage.append(" (Ref: ").append(uuid).append(")");

            throw new MssfApiException(userMessage.toString());
        }
    }


    private MssfDocFetchResponse fetchDocuments(String losId) {
        log.info("Attempting to fetch documents from fetchDocuments methodd losId: {}", losId);
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        Date now = new Date();
        String timeString = formatter.format(now);
        String UUID = "MSSF" + timeString;
        MssfDocFetchRequest.Request requestBody = MssfDocFetchRequest.Request.builder()
                .merchantCode(merchantCode)
                .merchantName(merchantName)
                .reqType("docDetails")
                .doc_type_id("")
                .user_type("SINGLE_APPLICANT")
                .los_id(losId)
                .UUID(UUID)
                .build();

        MssfDocFetchRequest request = new MssfDocFetchRequest();
        request.setRequest(requestBody);
        request.setMock(false);
        request.setApiName("mssfDocFetch");


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MssfDocFetchRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<MssfDocFetchResponse> response = restTemplate.exchange(
                    integratorEndpoint,
                    HttpMethod.POST,
                    entity,
                    MssfDocFetchResponse.class
            );

//for testing enable this section and comment the  api call
//            ObjectMapper mapper = new ObjectMapper();
//            InputStream is = getClass().getClassLoader().getResourceAsStream("mock-mssf-response.json");
//            MssfDocFetchResponse responseBody = mapper.readValue(is,MssfDocFetchResponse.class);
            MssfDocFetchResponse responseBody = response.getBody();

            // Log the complete response for debugging
            log.info("Received response for losId {}: {}", losId,
                    objectMapper.writeValueAsString(responseBody));

            // Use validateFetchResponse instead of validateResponse
           //validateFetchResponse(responseBody);
            return responseBody;

        } catch (Exception e) {
            log.error("Document fetch failed - fetchDocuments fetch for losId: {}. Error: {}", losId, e.getMessage(), e);
            throw new MssfApiException("Failed to fetch documents: " + e.getMessage());
        }
    }

    private MssfDocAckResponse acknowledgeDocuments(String losId, List<MssfDocFetchResponse.Document> documents) {
        SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
        Date now = new Date();
        String timeString = formatter.format(now);
        String UUID = "MSSF" + timeString;
        List<MssfDocAckRequest.Document> ackDocs = documents.stream()
                .map(doc -> MssfDocAckRequest.Document.builder()
                        .user_type("SINGLE_APPLICANT")
                        .doc_type_id(doc.getDoc_type_id())
                        .success(true)
                        .build())
                .collect(Collectors.toList());

        MssfDocAckRequest request = MssfDocAckRequest.builder()
                .request(MssfDocAckRequest.Request.builder()
                        .merchantCode(merchantCode)
                        .merchantName(merchantName)
                        .reqType("docAck")
                        .los_id(losId)
                        .documents(ackDocs)
                        .UUID(UUID)
                        .build())
                .mock(false)
                .apiName("mssfDocAck")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MssfDocAckRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<MssfDocAckResponse> response = restTemplate.exchange(
                    integratorEndpoint,
                    HttpMethod.POST,
                    entity,
                    MssfDocAckResponse.class
            );

            validateResponse(response.getBody(), "Document acknowledgment failed");
            return response.getBody();

        } catch (Exception e) {
            log.error("Document acknowledgment failed for losId: {}", losId, e);
            throw new MssfApiException("Failed to acknowledge documents: " + e.getMessage());
        }
    }

    private void validateResponse(Object response, String errorMessage) {
        if (response == null) {
            throw new MssfApiException(errorMessage + ": Null response received");
        }

        String statusCode = null;
        String statusDesc = null;

        if (response instanceof MssfDocFetchResponse) {
            MssfDocFetchResponse.Status status = ((MssfDocFetchResponse) response).getResponse().getStatus();
            statusCode = status.getCode();
            statusDesc = status.getDesc();
        } else if (response instanceof MssfDocAckResponse) {
            MssfDocAckResponse.Status status = ((MssfDocAckResponse) response).getResponse().getStatus();
            statusCode = status.getCode();
            statusDesc = status.getDesc();
        }

        if (!"200".equals(statusCode)) {
            throw new MssfApiException(errorMessage + ": " + statusDesc);
        }
    }


    private void storeDocuments(List<MssfDocFetchResponse.Document> documents, String losId) {
        try {
            String basePath = storagePath + "/" + losId;
            Files.createDirectories(Paths.get(basePath));

            for (MssfDocFetchResponse.Document doc : documents) {
                String extension = doc.getFile_name().substring(doc.getFile_name().lastIndexOf('.'));
                String sanitizedDocTypeName = doc.getDoc_type_name().replaceAll("\\s+", "_");
                // Include doc_type_id in filename
                String fileName = String.format("MSSF_%s_%s%s",
                        doc.getDoc_type_id(),
                        sanitizedDocTypeName,
                        extension);

                Path filePath = Paths.get(basePath, fileName);
                Files.write(filePath, doc.getDoc_data());
                log.info("Stored document at: {}", filePath);
            }
        } catch (IOException e) {
            throw new MssfApiException("Failed to store documents: " + e.getMessage());
        }
    }

    public List<StoredDocument> getStoredDocuments(String losId) {
        try {
            String basePath = storagePath + "/" + losId;
            log.info("Stored documents from {} is fetched", basePath);
            if (!Files.exists(Paths.get(basePath))) {
                return Collections.emptyList();
            }

            return Files.list(Paths.get(basePath))
                    .filter(path -> path.getFileName().toString().startsWith("MSSF_"))
                    .map(path -> {

                        String fileName = path.getFileName().toString();
                        log.info("File from MSSF {} is replaced with MSSF", fileName);
                        String[] parts = fileName.replace("MSSF_", "").split("_", 2);
                        String docTypeId = parts[0];
                        String documentType = parts[1].substring(0, parts[1].lastIndexOf('.'))
                                .replace("_", " ");

                        return StoredDocument.builder()
                                .fileName(fileName)
                                .documentType(documentType)
                                .docTypeId(docTypeId)
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to get stored documents for losId: {}", losId, e);
            throw new MssfApiException("Failed to get stored documents");
        }
    }


    private MssfDocProcessResponse buildSuccessResponse(int documentCount, List<DOC_ARRAY> docArrayList) {
        return MssfDocProcessResponse.builder()
                .status("success")
                .documentCount(documentCount)
                .message("Documents processed successfully")
                .documents(docArrayList) //
                .build();
    }
    private MssfDocProcessResponse buildSuccessResponse(int documentCount, List<DOC_ARRAY> docArrayList, String message) {
        return MssfDocProcessResponse.builder()
                .status("success")
                .documentCount(documentCount)
                .message(message)
                .documents(docArrayList)
                .build();
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private MssfDocAckResponse demoAcknowledgeDocuments(String losId, List<MssfDocFetchResponse.Document> documents) {
        // Create Status
        MssfDocAckResponse.Status status = new MssfDocAckResponse.Status();
        status.setCode("200");
        status.setDesc("Success");

        // Create Header
        MssfDocAckResponse.Header header = new MssfDocAckResponse.Header();
        header.setTimestamp("202412031939");
        header.setApiName("maruti-loan-doc-api");
        header.setApiVersion("1.0.0");
        header.setInterfaceName("MarutiVehicleLoan");

        // Create Body
        MssfDocAckResponse.Message message = new MssfDocAckResponse.Message();
        message.setUnique_id("227951");
        message.setSuccess(true);

        // Populate Applicant Documents
        List<MssfDocAckResponse.ApplicantDocument> applicantDocs = new ArrayList<>();


        message.setApplicant_documents(applicantDocs);
        message.setCo_applicant_documents(Collections.emptyList());

        MssfDocAckResponse.Body body = new MssfDocAckResponse.Body();
        body.setUUID("227951");
        body.setMessage(message);

        // Assemble Final Response
        MssfDocAckResponse.Response response = new MssfDocAckResponse.Response();
        response.setHeader(header);
        response.setStatus(status);
        response.setBody(body);

        MssfDocAckResponse ackResponse = new MssfDocAckResponse();
        ackResponse.setResponse(response);

        // Log the stub response
        log.info("Stubbed acknowledgment response for losId {}: {}", losId, ackResponse);

        return ackResponse;
    }
    private String getCurrentProcessingStep(Exception e) {
        String stackTrace = Arrays.toString(e.getStackTrace());

        if (stackTrace.contains("fetchDocuments")) {
            return "API_FETCH";
        } else if (stackTrace.contains("validateFetchResponse")) {
            return "RESPONSE_VALIDATION";
        } else if (stackTrace.contains("storeDocuments")) {
            return "DOCUMENT_STORAGE";
        } else if (stackTrace.contains("acknowledgeDocuments")) {
            return "DOCUMENT_ACKNOWLEDGMENT";
        } else if (stackTrace.contains("convertToDocArray")) {
            return "DTO_MAPPING";
        } else {
            return "UNKNOWN";
        }
    }

}
