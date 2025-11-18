package com.sib.ibanklosucl.service;

import com.sib.ibanklosucl.dto.losintegrator.WI.WIRequest;
import com.sib.ibanklosucl.dto.losintegrator.WI.WIResponse;
import com.sib.ibanklosucl.model.VehicleLoanApplicant;
import com.sib.ibanklosucl.model.VehicleLoanBasic;
import com.sib.ibanklosucl.model.VehicleLoanKyc;
import com.sib.ibanklosucl.model.VehicleLoanMaster;
import com.sib.ibanklosucl.repository.FetchRepository;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanApplicantService;
import com.sib.ibanklosucl.service.vlsr.VehicleLoanMasterService;
import com.sib.ibanklosucl.dto.losintegrator.WI.WIApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Date;
import java.text.SimpleDateFormat;

@Service
public class VehicleLoanWIService {

    @Autowired
    private VehicleLoanMasterService vehicleLoanMasterService;
    @Autowired
    private VehicleLoanApplicantService vehicleLoanApplicantService;
    @Autowired
    private WIApiClient wiApiClient;
    @Value("${wiusername}")
    private String username;
    @Value("${wipassword}")
    private String password;


    @Autowired
    private FetchRepository fetchRepository;

    public String createWI(String wiNUm, String slno, String applicantId, String reqIp){
        WIRequest wiRequest = frameWIRequest(wiNUm,slno,applicantId,reqIp);
        WIResponse wiResponse = wiApiClient.performWiCreation(wiRequest);
        String workItemNo="";
        if(wiResponse.getResponse().getStatus().getCode().equals("200")){
            workItemNo = wiResponse.getResponse().getBody().getWorkItemNo();
        }else{
            workItemNo="";
        }
        return workItemNo;
    }
    public WIRequest frameWIRequest(String wiNUm, String slno, String applicantId, String reqIp) {
        VehicleLoanMaster vehicleLoanMaster = vehicleLoanMasterService.findBySlno(Long.parseLong(slno));
        VehicleLoanApplicant vehicleLoanApplicant = vehicleLoanApplicantService.findByApplicantIdAndDelFlg(Long.parseLong(applicantId));
        VehicleLoanBasic vehicleLoanBasic = vehicleLoanApplicant.getBasicapplicants();
        VehicleLoanKyc vehicleLoanKyc = vehicleLoanApplicant.getKycapplicants();


        WIRequest.request wiRequestRequest = new WIRequest.request();
        WIRequest.createWorkItemRequest createWIReq = new WIRequest.createWorkItemRequest();
        createWIReq.setSolId(vehicleLoanMaster.getSolId());
        createWIReq.setTypeOfCustomer("Retail");
        String setRequestDateTime=(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        createWIReq.setRequestDateTime(setRequestDateTime);
        createWIReq.setRequestFunction("Customer Creation");
        createWIReq.setInitiatedFrom("VLOS");
        String urn=System.currentTimeMillis()+"";
        createWIReq.setURN(urn);
        createWIReq.setUsername(username);
        createWIReq.setPassword(password);
        createWIReq.setProcessName("CPC");
        createWIReq.setNoOfCustomers("1");
        String solId=vehicleLoanMaster.getSolId();
        String brName=fetchRepository.getSolName(solId);
        createWIReq.setKycPlaceOfDeclaration(brName);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String kycDateOfDeclaration=simpleDateFormat.format(new Date());
        createWIReq.setKycDateOfDeclaration(kycDateOfDeclaration);
        createWIReq.setKycVerifiedBy(vehicleLoanMaster.getBrVUser());
        createWIReq.setUserID("");
        createWIReq.setTypeOfAccount("");


        WIRequest.customerDetails customerDtls = new WIRequest.customerDetails();
        customerDtls.setCid("1");
        customerDtls.setCustomerId(vehicleLoanApplicant.getCifId());
        customerDtls.setCustomerName(vehicleLoanApplicant.getApplName());
        customerDtls.setNRE("N");
        customerDtls.setMinor("N");
        customerDtls.setPoa("AadharCard");
        String cpoa=vehicleLoanBasic.getCpoa()==null?"AadharCard":vehicleLoanBasic.getCpoa();
        customerDtls.setCpoa(cpoa);
        customerDtls.setKycAttestation("01");
        customerDtls.setFatherName(vehicleLoanBasic.getFatherName()==null?"":vehicleLoanBasic.getFatherName());
        customerDtls.setMotherName(vehicleLoanBasic.getMotherName()==null?"":vehicleLoanBasic.getMotherName());
        customerDtls.setSpouseName(vehicleLoanBasic.getSpouseName()==null?"":vehicleLoanBasic.getSpouseName());
        customerDtls.setNationality("IN");
        customerDtls.setJurisdiction("");
        customerDtls.setResidentialStatus("");
        customerDtls.setCkycReq("");
        customerDtls.setPhoneContactType("");
        customerDtls.setCustomerCreation("");
        WIRequest.customerDetails[] customerDetailsArray = new WIRequest.customerDetails[1];
        customerDetailsArray[0]=customerDtls;

        WIRequest.accountDetails accountDtls = new WIRequest.accountDetails();
        accountDtls.setAccountNo("");
        accountDtls.setTypeOfAccount("");
        accountDtls.setGlCode("");
        accountDtls.setSchemeCode("");
        accountDtls.setNomineeFlag("");
        accountDtls.setAtmCard("");
        accountDtls.setCategorisation("");
        accountDtls.setUeidCode("");
        accountDtls.setUeidName("");
        accountDtls.setAccountCreation("");
        accountDtls.setModeOfOperation("");


        WIRequest.nomineeDetails nomineeDtls = new WIRequest.nomineeDetails();
        nomineeDtls.setRegNo("");
        nomineeDtls.setTitle("");
        nomineeDtls.setName("");
        nomineeDtls.setDob("");
        nomineeDtls.setRelation("");
        nomineeDtls.setNominationPercent("");
        nomineeDtls.setNomineeAddress1("");
        nomineeDtls.setNomineeAddress2("");
        nomineeDtls.setNomineeAddress3("");
        nomineeDtls.setNomineeCity("");
        nomineeDtls.setNomineeState("");
        nomineeDtls.setNomineeCountry("");
        nomineeDtls.setNomineePin("");
        nomineeDtls.setIsMinor("");
        nomineeDtls.setNomineeGuardianTitle("");
        nomineeDtls.setNomineeGuardianName("");
        nomineeDtls.setNomineeGuardianDob("");
        nomineeDtls.setNomineeGuardianCode("");
        nomineeDtls.setNomineeGuardianAddress1("");
        nomineeDtls.setNomineeGuardianAddress2("");
        nomineeDtls.setNomineeGuardianAddress3("");
        nomineeDtls.setNomineeGuardianCity("");
        nomineeDtls.setNomineeGuardianState("");
        nomineeDtls.setNomineeGuardianCountry("");
        nomineeDtls.setNomineeGuardianPin("");

        WIRequest.kycValidationDetails kycValidationDtls = new WIRequest.kycValidationDetails();
        kycValidationDtls.setPan(vehicleLoanKyc.getPanNo());
        kycValidationDtls.setPanStatus("E");
        kycValidationDtls.setPanAadharSeeded("");
        kycValidationDtls.setPanNameOnCard(vehicleLoanKyc.getPanName());
        kycValidationDtls.setPanDOB(simpleDateFormat.format(vehicleLoanKyc.getPanDob()));
        kycValidationDtls.setPanDOBMatch("");
        kycValidationDtls.setPanNameMatch("");
        WIRequest.kycValidationDetails[] kycValidationDtlsArray = new WIRequest.kycValidationDetails[1];
        kycValidationDtlsArray[0]=kycValidationDtls;

        int arraySize=5;
        if(!"AadharCard".equals(cpoa)){
            arraySize=6;
        }
        WIRequest.documents[] documentsArray=new WIRequest.documents[arraySize];


        WIRequest.documents document = null;
        String docext="";String base64="";
        int i=0;

        document = null;
        document = new WIRequest.documents();
        document.setCid("1");
        document.setDocName("AadharCard");
        docext=vehicleLoanKyc.getAadharext();
        base64=vehicleLoanKyc.getAadharimg();
        if("png".equalsIgnoreCase(docext)){
            docext="jpg";
            base64=PNGToJPGBase64Converter(base64);
        }

        document.setDocument(base64);
        document.setDocExtension(docext);
        documentsArray[i]=document;
        i++;


        document = null;
        document = new WIRequest.documents();
        document.setCid("1");
        document.setDocName("PAN");

        docext="";base64="";
        docext=vehicleLoanKyc.getPanext();
        base64=vehicleLoanKyc.getPanimg();
        if("png".equalsIgnoreCase(docext)){
            docext="jpg";
            base64=PNGToJPGBase64Converter(base64);
        }
        document.setDocument(base64);
        document.setDocExtension(docext);
        documentsArray[i]=document;
        i++;


        document = null;
        document = new WIRequest.documents();
        document.setCid("1");
        document.setDocName("Photo");

        docext="";base64="";
        docext=vehicleLoanKyc.getPhotoext();
        base64=vehicleLoanKyc.getPhoto();
        if("png".equalsIgnoreCase(docext)){
            docext="jpg";
            base64=PNGToJPGBase64Converter(base64);
        }
        document.setDocument(base64);
        document.setDocExtension(docext);
        documentsArray[i]=document;
        i++;

        document = null;
        document = new WIRequest.documents();
        document.setCid("1");
        document.setDocName("Signature");

        docext="";base64="";
        docext=vehicleLoanKyc.getCustSigExt();
        base64=vehicleLoanKyc.getCustSig();
        if("png".equalsIgnoreCase(docext)){
            docext="jpg";
            base64=PNGToJPGBase64Converter(base64);
        }
        document.setDocument(base64);
        document.setDocExtension(docext);
        documentsArray[i]=document;
        i++;

        document = null;
        document = new WIRequest.documents();
        document.setCid("1");
        document.setDocName("Loan Application");
        document.setDocument(fetchRepository.getDocumentByApplicantId("APPFORM",applicantId));
        document.setDocExtension("pdf");
        documentsArray[i]=document;
        i++;

        if(!"AadharCard".equals(cpoa)){
            //add the cpoa doc to BPM WI creation request
            document = null;
            document = new WIRequest.documents();
            document.setCid("1");
            document.setDocName(cpoa);

            base64="";docext="";
            base64=vehicleLoanBasic.getCpoaDoc();
            docext=vehicleLoanBasic.getCpoaExt();
            if("png".equalsIgnoreCase(docext)){
                docext="jpg";
                base64=PNGToJPGBase64Converter(base64);
            }
            document.setDocument(base64);
            document.setDocExtension(docext);
            documentsArray[i]=document;
            i++;
        }


        wiRequestRequest.setCreateWorkItemRequest(createWIReq);
        wiRequestRequest.setCustomerDetails(customerDetailsArray);
        wiRequestRequest.setAccountDetails(accountDtls);
        wiRequestRequest.setNomineeDetails(nomineeDtls);
        wiRequestRequest.setKycValidationDetails(kycValidationDtlsArray);
        wiRequestRequest.setDocuments(documentsArray);


        WIRequest wiRequest = new WIRequest();
        wiRequest.setMock(false);
        wiRequest.setSlno(slno);
        wiRequest.setWorkItemNumber(wiNUm);
        wiRequest.setOrigin(applicantId);
        wiRequest.setApiName("wiCreation");
        wiRequest.setRequest(wiRequestRequest);

        return wiRequest;
    }


    public String PNGToJPGBase64Converter(String base64Png){
        String base64Jpg ="";
        try {
            // Example: Base64 string retrieved from Oracle CLOB column
            //String base64Png = "BASE64_PNG_STRING_FROM_DB"; // Replace with your Base64 PNG string

            // Step 1: Decode the Base64 string to a byte array
            byte[] decodedBytes = Base64.getDecoder().decode(base64Png);

            // Step 2: Convert the byte array to a BufferedImage
            BufferedImage pngImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));

            // Step 3: Create a new BufferedImage with TYPE_INT_RGB (JPG doesn't support transparency)
            BufferedImage jpgImage = new BufferedImage(
                    pngImage.getWidth(),
                    pngImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);

            // Fill the background with white and draw the PNG image onto it
            jpgImage.createGraphics().drawImage(pngImage, 0, 0, null);

            // Step 4: Convert the BufferedImage to JPG format
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(jpgImage, "jpg", outputStream);
            byte[] jpgBytes = outputStream.toByteArray();

            // Step 5: Encode the JPG byte array to a Base64 string
            base64Jpg = Base64.getEncoder().encodeToString(jpgBytes);

            // Output the Base64 JPG string
            //System.out.println("Base64 JPG: " + base64Jpg);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64Jpg;
    }
}
