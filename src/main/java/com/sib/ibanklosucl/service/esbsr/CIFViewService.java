package com.sib.ibanklosucl.service.esbsr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sib.ibanklosucl.dto.CIFviewRequest;
import com.sib.ibanklosucl.dto.TabResponse;
import com.sib.ibanklosucl.dto.esb.FetchDetailsBody;
import com.sib.ibanklosucl.dto.losintegrator.LosRequest;
import com.sib.ibanklosucl.exception.ValidationError;
import com.sib.ibanklosucl.model.CustomerDetails;
import com.sib.ibanklosucl.service.vlsr.CustomerDetailsService;
import com.sib.ibanklosucl.utilies.CommonUtils;
import com.sib.ibanklosucl.utilies.Constants;
import com.sib.ibanklosucl.utilies.UserSessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.sib.ibanklosucl.exception.ValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CIFViewService {
    @Autowired
    private RestTemplate restTemplate;
    private static final Gson gson = new Gson();


    @Value("${esb.ChannelID}")
    private String ChannelID;
    @Value("${esb.MerchantName}")
    private String ChannelName;
    @Value("${api.integrator}")
    private String urlendpoint;
    @Autowired
    private EsbApiService apiService;
    @Autowired
    private CustomerDetailsService customerDetailsService;
    @Autowired
    private UserSessionData usd;

    @Transactional
    public TabResponse getCustData(CIFviewRequest tab, HttpServletRequest request) {
        CustomerDetails cd = null, cdold = null;
        cdold = customerDetailsService.findByAppId(Long.valueOf(tab.getAppid()));

        cd = new CustomerDetails();
        cd.setSlno(Long.valueOf(tab.getSlno()));
        cd.setWiNum(tab.getWinum());
        cd.setApplicantId(Long.valueOf(tab.getAppid()));
        cd.setCustId(tab.getCustID());
        cd.setReqIpAddr(CommonUtils.getClientIp(request));
        cd.setDelFlg("N");
        cd.setCmDate(new Date());
        cd.setCmUser(usd.getPPCNo());
        cd.setHomeSol(usd.getSolid());
        try {
            cd = fetchAndProcessCustomerData(cd);
            customerDetailsService.saveCustomerDetails(cd);
            if (cdold != null) {
                cdold.setDelFlg("Y");
                customerDetailsService.saveCustomerDetails(cdold);
            }
            if (!cd.getValidFlg().equals("Y"))
                return new TabResponse("F", cd.getErrorMsg());
            else
                return new TabResponse("S", new ObjectMapper().writeValueAsString(cd));

        } catch (Exception e) {
            e.printStackTrace();
            return new TabResponse("F", Constants.Messages.CIF_ERROR);
        }

    }


    public CustomerDetails fetchAndProcessCustomerData(CustomerDetails customerDetails) throws Exception {
        FetchDetailsBody body = new FetchDetailsBody();

        body.setCustId(customerDetails.getCustId());
        body.setUuid(UUID.randomUUID().toString());
        LosRequest.CIFView cifView = new LosRequest.CIFView();
        cifView.setRequest(body);
        cifView.setMock(false);
        cifView.setOrigin(customerDetails.getApplicantId().toString());
        cifView.setWorkItemNumber(customerDetails.getWiNum());
        // Fire the API request and get the response
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        // Create the HTTP entity
        HttpEntity<LosRequest.CIFView> entity = new HttpEntity<>(cifView, headers);
        // Call the OCR API
        ResponseEntity<String> response = restTemplate.exchange(
                urlendpoint,
                HttpMethod.POST,
                entity,
                String.class
        );
        JsonObject fetchjson = apiService.EsbAPIValidator(response, urlendpoint);
        if (fetchjson.get("msg").getAsString().equals("OK")) {
            String responseBody = response.getBody();
            // Parse the JSON response using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            // Extract the relevant data from the JSON response
            JsonNode responseNode = jsonNode.get("Response");
            JsonNode bodyNode = responseNode.get("Body");

            // Convert the JSON data to Java objects
            List<Map<String, Object>> customerAddressList = objectMapper.convertValue(
                    bodyNode.get("CustomerAddress"), new TypeReference<List<Map<String, Object>>>() {
                    });
            List<Map<String, Object>> customerDocDetailsList = objectMapper.convertValue(
                    bodyNode.get("Doc_Details"), new TypeReference<List<Map<String, Object>>>() {
                    });
            List<Map<String, Object>> customerPhoneEmailDetailsList = objectMapper.convertValue(
                    bodyNode.get("Phone_EmailDetails"), new TypeReference<List<Map<String, Object>>>() {
                    });
            customerDetails.setValidFlg("Y");

            customerDetails.setErrorMsg(null);

            // Process the customer data
            customerDetails = processCustomerData(customerDetails, bodyNode, customerAddressList, customerDocDetailsList, customerPhoneEmailDetailsList);
            return customerDetails;
        } else {
            customerDetails.setValidFlg("N");
            customerDetails.setErrorMsg(fetchjson.get("msg").getAsString());
        }
        return customerDetails;
    }

    private CustomerDetails processCustomerData(CustomerDetails customerDetails, JsonNode body,
                                                List<Map<String, Object>> customerAddressList,
                                                List<Map<String, Object>> customerDocDetailsList,
                                                List<Map<String, Object>> customerPhoneEmailDetailsList) {

        if (body.get("minorFlag").asText().isBlank()) {
            throw new ValidationException(ValidationError.COM001, "Minor Flag doesnt exist in the Customer ID");
        }
        if (body.get("DOB").asText().isBlank()) {
            throw new ValidationException(ValidationError.COM001, "Date of Birth doesnt exist in the Customer ID");
        }
        customerDetails.setCustomerName(body.get("Name").asText());
        customerDetails.setUuid(body.get("UUID").asText());
        customerDetails.setCustomerName(body.get("Name").asText());
        customerDetails.setPrimarySolId(body.get("Branchid").asText());
        customerDetails.setGender(body.get("Gender").asText());
        customerDetails.setFathersName(body.get("fatherName").asText());
        customerDetails.setMothersName(body.get("motherName").asText());
        customerDetails.setCustDob(body.get("DOB").asText().substring(0, 10));
        customerDetails.setOccupation(body.get("occupation").asText());
        customerDetails.setMonthlyInc(body.get("income").asText());
        customerDetails.setMaritalStatus(body.get("maritalStatus").asText());
        customerDetails.setKycComplied(body.get("cif_kyc_complied").asText());
        customerDetails.setMinorFlag(body.get("minorFlag").asText());
        customerDetails.setResidentialStatus(body.get("residentialStatus").asText());

        customerAddressList.stream()
                .filter(address -> "Home".equalsIgnoreCase((String) address.get("AddressType")))
                .findFirst()
                .ifPresent(address -> {
                    customerDetails.setPermanentAddress1(address.get("AddressLine1").toString().replaceAll("[^A-Za-z0-9 &,./-]", ""));
                    customerDetails.setPermanentAddress2(address.get("AddressLine2").toString().replaceAll("[^A-Za-z0-9 &,./-]", ""));
                    customerDetails.setPermanentAddress3(address.get("AddressLine3").toString().replaceAll("[^A-Za-z0-9 &,./-]", ""));
                    customerDetails.setPermanentCityCode((String) address.get("City"));
                    customerDetails.setPermanentCity((String) address.get("city_desc"));
                    customerDetails.setPermanentStateCode((String) address.get("State"));
                    customerDetails.setPermanentState((String) address.get("state_desc"));
                    customerDetails.setPermanentCountryCode((String) address.get("Country"));
                    customerDetails.setPermanentCountry((String) address.get("country_desc"));
                    customerDetails.setPermanentPin((String) address.get("Zip"));
                });
        customerAddressList.stream()
                .filter(address -> "Mailing".equalsIgnoreCase((String) address.get("AddressType")))
                .findFirst()
                .ifPresent(address -> {
                    customerDetails.setCommunicationAddress1(address.get("AddressLine1").toString().replaceAll("[^A-Za-z0-9 &,./-]", ""));
                    customerDetails.setCommunicationAddress2(address.get("AddressLine2").toString().replaceAll("[^A-Za-z0-9 &,./-]", ""));
                    customerDetails.setCommunicationAddress3(address.get("AddressLine3").toString().replaceAll("[^A-Za-z0-9 &,./-]", ""));
                    customerDetails.setCommunicationCityCode((String) address.get("City"));
                    customerDetails.setCommunicationCity((String) address.get("city_desc"));
                    customerDetails.setCommunicationStateCode((String) address.get("State"));
                    customerDetails.setCommunicationState((String) address.get("state_desc"));
                    customerDetails.setCommunicationCountryCode((String) address.get("Country"));
                    customerDetails.setCommunicationCountry((String) address.get("country_desc"));
                    customerDetails.setCommunicationPin((String) address.get("Zip"));
                });

        // Processing customer documents
        customerDocDetailsList
//                .stream()
//                .filter(doc -> "PAN".equalsIgnoreCase((String) doc.get("DocumentName")) ||
//                        "AADHAAR_REF_NO".equalsIgnoreCase((String) doc.get("DocumentName")) ||
//                        "PASSPORT".equalsIgnoreCase((String) doc.get("DocumentName")))
                .forEach(doc -> {
                    String documentType = (String) doc.get("DocumentName");
                    String documentId = (String) doc.get("Reference_No");
                    if ("PAN".equalsIgnoreCase(documentType)) {
                        customerDetails.setPan(documentId);
                    } else if ("AADHAAR_REF_NO".equalsIgnoreCase(documentType)) {
                        customerDetails.setAadhaarRefNo(documentId);
                    } else if ("PASSPORT".equalsIgnoreCase(documentType)) {
                        customerDetails.setPassport(documentId);
                    } else if ("VISA".equalsIgnoreCase(documentType)) {
                        customerDetails.setVisa(documentId);
                    } else if ("DRIVING LICENCE".equalsIgnoreCase(documentType)) {
                        customerDetails.setDrivingLicence(documentId);
                    } else if ("ELECTORAL ID".equalsIgnoreCase(documentType)) {
                        customerDetails.setVoterid(documentId);
                    } else if ("OCI CARD".equalsIgnoreCase(documentType)) {
                        customerDetails.setOciCard(documentId);
//                    } else if ("OVERSEAS ID CARD".equalsIgnoreCase(documentType)) {
//                        customerDetails.setOverseasIdCard(documentId);
                    } else if ("CDC NUMBER".equalsIgnoreCase(documentType)) {
                        customerDetails.setCdnNo(documentId);
                    }
                });

        // Processing customer phone and email details
        customerPhoneEmailDetailsList.stream()
                .filter(contact -> "CELLPH".equalsIgnoreCase((String) contact.get("Phone_EmailType")) ||
                        "COMMEML".equalsIgnoreCase((String) contact.get("Phone_EmailType")))
                .forEach(contact -> {
                    String phoneEmailType = (String) contact.get("Phone_EmailType");
                    if ("CELLPH".equalsIgnoreCase(phoneEmailType)) {
                        String phone = (String) contact.get("Phone_Number");
//                        if (phone != null && phone.startsWith("+91")) {
//                            phone = phone.substring(3);
//                        }
                        if(phone!=null && phone.contains("+")){
                            phone=phone.replaceAll("\\+","").trim();
                        }
                        customerDetails.setCellPhone(phone);
                    } else if ("COMMEML".equalsIgnoreCase(phoneEmailType)) {
                        customerDetails.setCommEmail((String) contact.get("Email"));
                    }
                });

        return customerDetails;
    }


}
