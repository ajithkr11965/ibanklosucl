<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanKyc" %>
<%@ page import="com.sib.ibanklosucl.repository.UIDRepository" %>
<%--
Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 09-05-2024
  Time: 15:13
  To change this template use File | Settings | File Templates.
--%>


<%
    VehicleLoanApplicant applicant=null;
    if(request.getAttribute("general")!=null)
        applicant = (VehicleLoanApplicant) request.getAttribute("general");


    String apptype= request.getAttribute("apptype").toString();
    boolean completed=false,mod=false;
    String fileclass="file-input*";
    if(request.getAttribute("init")!=null)
        fileclass="file-input";


    String panNo ="",ocrPanNumber="",ocrPanDob="",ocrPanName="",panDob="",panName="",panDobNsdlValid="",aadharMode="M",aadharRefNum="",aadharDob="",aadharName="",aadharOtpValidated="",aadharOtpSent="",passportNumber="",ocrPassportNum="",passportExpiryDate="",panimg="",uidimg="",passimg="",panext="",uidext="",passext="",visaocitype="";
    String visaimg="",visaocinumber="",visaext="",passportName="",visaexp="",passportfup="false",panfup="false",uidfup="false",visafup="false";
    String photoimg="",photoext="";
    String consentimg="",consentimgext="",sibcustomer="";
    String originalSeenCertificate="",originalSeenCertificateExt="",cifmode="",custSig="",custSigExt="";
    boolean panvalidated=false,uidvalidated=false;
    String res_stat="R";
    if(applicant!=null)
    {
        fileclass="file-input";
        res_stat=applicant.getResidentFlg();
        cifmode=applicant.getCifCreationMode() != null ?applicant.getCifCreationMode() : "";
        sibcustomer=applicant.getSibCustomer();
        if(applicant.getKycapplicants()!=null){
            if(applicant.getKycComplete()!=null && applicant.getKycComplete().equals("Y"))
                completed=true;
            VehicleLoanKyc kyc=applicant.getKycapplicants();
            panNo = kyc.getPanNo() != null ? kyc.getPanNo() : "";
            ocrPanNumber = kyc.getOcrPanNumber() != null ? kyc.getOcrPanNumber() : "";
            ocrPanDob = kyc.getOcrPanDob() != null ? kyc.getOcrPanDob().toString() : "";
            ocrPanName = kyc.getOcrPanName() != null ? kyc.getOcrPanName() : "";
            panDob = kyc.getPanDob() != null ? kyc.getPanDob().toString() : "";
            panName = kyc.getPanName() != null ? kyc.getPanName() : "";
            panDobNsdlValid = kyc.getPanDobNsdlValid() != null ? kyc.getPanDobNsdlValid() : "";
            if(panDobNsdlValid.equals("Y"))panvalidated=true;
            aadharMode = kyc.getAadharMode() != null ? kyc.getAadharMode() : "";
            aadharRefNum = kyc.getAadharRefNum() != null ? kyc.getUidno() : "";
            aadharDob = kyc.getAadharYob() != null ? kyc.getAadharYob(): "";
            aadharName = kyc.getAadharName() != null ? kyc.getAadharName() : "";
            aadharOtpValidated = kyc.getAadharOtpValidated() != null ? kyc.getAadharOtpValidated() : "";
            if(aadharOtpValidated.equals("Y"))uidvalidated=true;
            aadharOtpSent = kyc.getAadharOtpSent() != null ? kyc.getAadharOtpSent() : "";
            passportNumber = kyc.getPassportNumber() != null ? kyc.getPassportNumber() : "";
            ocrPassportNum = kyc.getOcrPassportNum() != null ? kyc.getOcrPassportNum() : "";
            passportExpiryDate = kyc.getPassportExpiryDate() != null ? kyc.getPassportExpiryDate().toString() : "";
            passportName = kyc.getPassportName() != null ? kyc.getPassportName() : "";
            panimg = kyc.getPanimg() != null ? kyc.getPanimg() : "";
            uidimg = kyc.getAadharimg() != null ? kyc.getAadharimg() : "";
            passimg = kyc.getPassportimg() != null ? kyc.getPassportimg() : "";

            panext = kyc.getPanext() != null ? kyc.getPanext() : "";
            uidext = kyc.getAadharext() != null ? kyc.getAadharext() : "";
            passext = kyc.getPassportext() != null ? kyc.getPassportext() : "";

            visaocitype = kyc.getVisaOciType() != null ? kyc.getVisaOciType() : "";
            visaimg = kyc.getVisaimg() != null ? kyc.getVisaimg() : "";
            visaocinumber = kyc.getVisaOciNumber() != null ? kyc.getVisaOciNumber() : "";
            visaext = kyc.getVisaext() != null ? kyc.getVisaext() : "";
            visaexp = kyc.getVisaExpiry() != null ? kyc.getVisaExpiry().toString() : "";

            passportfup=kyc.getPassportimg()!=null?"true":"false";
            panfup=kyc.getPanimg()!=null?"true":"false";
            uidfup=kyc.getAadharimg()!=null?"true":"false";
            visafup=kyc.getVisaimg()!=null?"true":"false";

            photoimg = kyc.getPhoto() != null ? kyc.getPhoto() : "";
            photoext= kyc.getPhotoext() != null ? kyc.getPhotoext() : "";

            consentimg   = kyc.getConsentimg() != null ? kyc.getConsentimg() : "";
            consentimgext= kyc.getConsentimgext() != null ? kyc.getConsentimgext() : "";
            originalSeenCertificate   = kyc.getOriginalSeenCertificate() != null ? kyc.getOriginalSeenCertificate() : "";
            originalSeenCertificateExt= kyc.getOriginalSeenCertificateExt() != null ? kyc.getOriginalSeenCertificateExt() : "";;
            custSig   = kyc.getCustSig() != null ? kyc.getCustSig() : "";
            custSigExt= kyc.getCustSigExt() != null ? kyc.getCustSigExt() : "";

        }

    }
    String uidmanual="";//(aadharMode.equals("M") || aadharMode.isBlank())?"":"hide";
    String uidotp="hide";//aadharMode.equals("O")?"":"hide";


%>
<form   class="det form-details  kycdetails" data-code="<%=apptype%>-2"  data-completed="<%=completed%>"  action="#">
<%--    <div class="kt d-flex justify-content-end" style="height: 0em;">--%>
<%--        <button class="edit-button btn btn-icon  btn-bg-light btn-color-info btn-sm me-1">--%>
<%--            <i class="ki-duotone  ki-pencil fs-2"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>--%>
<%--        </button>--%>
<%--    </div>--%>
    <input type="hidden" id="modifykyc" value="<%=mod%>">
    <input type="hidden" name="panfup" class="panfup" value="<%=panfup%>">
    <input type="hidden" name="uidfup" class="uidfup" value="<%=uidfup%>">
    <input type="hidden" name="passportfup" class="passportfup" value="<%=passportfup%>">
    <input type="hidden" name="visafup" class="visafup" value="<%=visafup%>">
  <div class="row mb-3">
<%--    <div class="border-bottom pb-2 mb-2">--%>
<%--        <div class="d-flex">--%>
<%--            <span class="fw-bold">PAN Details</span>--%>
<%--            <button type="button" class="cbsfetch btn-file kyc me-2 btn btn-outline-dark ms-auto text-end me-5">--%>
<%--                <i class="ph ph-browsers  ms-2"></i>    Fetch from CBS--%>
<%--            </button>--%>
<%--        </div>--%>
<%--    </div>--%>
    <div class="row">
        <div class="col-lg-6">
	        <div class="file-placeholder">
		        <i class="fas fa-file-alt file-icon"></i>
		        <div class="file-text"><span class="checkmark"></span>File Uploaded</div>
		        <div class="file-subtext">Document added to Doc Tray</div>
	        </div>

<%--          <input type="file" class="<%=fileclass%> base64file panfile" name='panfile' data-show-upload="false" data-show-remove="false" data-filedesc="UPLOAD PAN" data-filebase64="<%=panimg%>" data-filebase64ext="<%=panext%>">--%>

        </div>
        <div class="col-lg-6">
            <div class="mb-2">
              <div class="form-check-horizontal">
               <!--   <button type="button" class="btn btn-light btn-file panocr" title="Details will be autofilled">
                      <i class="ph-check-circle me-2"></i>
                      OCR Mode
                  </button>-->
              </div>
            </div>
            <div class="mb-2">
              <label class="form-label">PAN</label>
              <div class="form-control-feedback form-control-feedback-start">
                <input type="text" class="form-control pan" placeholder="" minlength="10" maxlength="10" name="pan" value="<%=panNo%>">
                <input type="hidden" class="form-control ocr_pan" placeholder="" name="ocr_pan">
                <input type="hidden" class="form-control ocr_pandob" placeholder="" name="ocr_pandob">
                <input type="hidden" class="form-control ocr_panname" placeholder="" name="ocr_panname">

                <div class="form-control-feedback-icon">
                  <i class="ph-identification-card text-muted"></i>
                </div>
              </div>
            </div>
            <div class="mb-2">
              <label class="form-label">DOB</label>
              <div class="form-control-feedback form-control-feedback-start">
                <input type="date" class="form-control pandob" placeholder="DD-MM-YYYY" name="pandob" value="<%=panDob%>">
                <div class="form-control-feedback-icon">
                  <i class="ph-calendar-blank text-muted"></i>
                </div>
              </div>
            </div>
            <div class="mb-2">
              <label class="form-label">Name as on PAN</label>
              <div class="form-control-feedback form-control-feedback-start">
                <input type="text" class="form-control panname" placeholder=""  name="panname" value="<%=panName%>">
                <div class="form-control-feedback-icon">
                  <i class="ph-user-circle text-muted"></i>
                </div>
              </div>
            </div>
            <div class="mb-2 text-center">
                <input type="hidden" class="pan-validated valid-lib" name="pan-validated" value="<%=panvalidated%>">
<%--              <button type="submit"  class="btn btn-teal btn-file pan-validate">Validate<i class="ph-vault ms-2"></i></button>--%>
            </div>
        </div>
    </div>
  </div>
  <div class="row mb-3  ">
    <div class="border-bottom pb-2 mb-2">
      <span class="fw-bold">Aadhaar Details</span>
    </div>
    <div class="row">
        <div class="col-lg-6 uidmanual <%=uidmanual%>">
	         <div class="file-placeholder">
		        <i class="fas fa-file-alt file-icon"></i>
		        <div class="file-text"><span class="checkmark"></span>File Uploaded</div>
		        <div class="file-subtext">Document added to Doc Tray</div>
	        </div>
<%--          <input type="file" class="<%=fileclass%>  base64file uidfile uidmanual <%=uidmanual%>"  data-filedesc="UPLOAD AADHAAR" name='uidfile' data-show-upload="false" data-show-remove="false" data-show-cancel="false" data-filebase64="<%=uidimg%>" data-filebase64ext="<%=uidext%>">--%>
        </div>
        <div class="col-lg-6">
            <div class="mb-2">
              <div class="form-check-horizontal">
<%--                  <button type="button" class="btn btn-light uidocr btn-file uidmanual <%=uidmanual%>" title="Details will be autofilled">--%>
<%--                      <i class="ph-check-circle me-2"></i>--%>
<%--                      OCR Mode--%>
<%--                  </button>--%>
                  <input type="hidden" class="uidmode" name="uidmode" value="M">
<%--                  <label class="form-check form-check-inline">--%>
<%--                      <input type="radio" class="form-check-input uidmode" name="uidmode" value="M"  required="" <%= ("M".equals(aadharMode) || aadharMode.isBlank()) ? "checked" : "" %> >--%>
<%--                      <span class="form-check-label">Manual</span>--%>
<%--                  </label>--%>
<%--                  <label class="form-check form-check-inline">--%>
<%--                      <input type="radio" class="form-check-input uidmode" name="uidmode" value="O" required=""  <%= "O".equals(aadharMode) ? "checked" : "" %>>--%>
<%--                      <span class="form-check-label" for="validationFormCheck3">OTP</span>--%>
<%--                  </label>--%>
              </div>
            </div>
            <div class="mb-2">
              <label class="form-label">Aadhaar</label>
              <div class="form-control-feedback form-control-feedback-start">
                <input type="text" class="form-control uid" placeholder="" minlength="12" maxlength="12" name="uid"  value="<%=aadharRefNum%>">
                <div class="form-control-feedback-icon">
                  <i class="ph-identification-card text-muted"></i>
                </div>
              </div>
            </div>
<%--            <div class="mb-2 uidmanual <%=uidmanual%>">--%>
<%--              <label class="form-label">Year of Birth</label>--%>
<%--              <div class="form-control-feedback form-control-feedback-start">--%>
<%--                <input type="text" class="form-control uiddob uidmanual  " minlength="4" maxlength="4" placeholder="YYYY" name="uiddob"  value="<%=aadharDob%>">--%>
<%--                <div class="form-control-feedback-icon">--%>
<%--                  <i class="ph-calendar-blank text-muted"></i>--%>
<%--                </div>--%>
<%--              </div>--%>
<%--            </div>--%>
            <div class="mb-2 uidmanual <%=uidmanual%>">
              <label class="form-label">Name as on Aadhaar</label>
              <div class="form-control-feedback form-control-feedback-start">
                <input type="text" class="form-control uidname" placeholder=""  name="uidname"  value="<%=aadharName%>">
                <div class="form-control-feedback-icon">
                  <i class="ph-user-circle text-muted"></i>
                </div>
              </div>
            </div>
<%--            <div class="mb-2 text-center">--%>
<%--              <button type="submit"  class="btn btn-file btn-teal uid-validate uidocr uidmanual <%=uidmanual%>">Validate<i class="ph-vault ms-2"></i></button>--%>
<%--              <button type="submit"  class="btn btn-file btn-teal uid-otp uidotp <%=uidotp%>">Send Otp<i class="ph-align-right ms-2"></i></button>--%>
<%--                <input type="hidden" class="uid-validated valid-lib" name="uid-validated"  value="<%=uidvalidated%>">--%>
<%--            </div>--%>
        </div>
    </div>
  </div>

    <div class="row mb-3">

        <div class="row">
            <div class="col-lg-6">
                <div class="border-bottom pb-2 mb-2">
                    <span class="fw-bold">Customer Photo*</span>
                </div>
	            <div class="file-placeholder">
		            <i class="fas fa-file-alt file-icon"></i>
		            <div class="file-text"><span class="checkmark"></span>File Uploaded</div>
		            <div class="file-subtext">Document added to Doc Tray</div>
	            </div>
<%--                <input type="file" class="<%=fileclass%> base64file photofile" name='photofile'  data-filedesc="UPLOAD PHOTO" data-show-upload="false" data-show-remove="false" data-filebase64="<%=photoimg%>" data-filebase64ext="<%=photoext%>">--%>
            </div>
            <div class="col-lg-6">
                <div class="border-bottom pb-2 mb-2">
                    <span class="fw-bold">Consent Form*</span>
                </div>
	            <div class="file-placeholder">
		            <i class="fas fa-file-alt file-icon"></i>
		            <div class="file-text"><span class="checkmark"></span>File Uploaded</div>
		            <div class="file-subtext">Document added to Doc Tray</div>
	            </div>
<%--                <input type="file" class="<%=fileclass%> base64file consentfile" name='consentfile'  data-filedesc="UPLOAD Consent Form" data-show-upload="false" data-show-remove="false" data-filebase64="<%=consentimg%>" data-filebase64ext="<%=consentimgext%>">--%>
            </div>
            </div>
</div>
    <div class="row mb-3">

        <div class="row">
            <div class="col-lg-6 justify-content-center cifmode  <%=sibcustomer.equals("N")?"d-flex":"hide"%> ">
                <div class="border-bottom pb-2 mb-2">
                    <b class="form-label">CIF ID Creation Mode</b>
                    <div class="form-check-horizontal">
                        <select class="form-control form-select cif_mode" name="cif_mode" >
                            <option value="">Select</option>
                            <option value="M" <%=cifmode.equals("M")?"selected":""%>>Manual</option>
                            <option value="V" <%=cifmode.equals("V")?"selected":""%>>Video KYC</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="col-lg-6">
                <div class="border-bottom pb-2 mb-2">
                    <span class="fw-bold">Original Seen And Verified <code>(Mandatory for Non sib customers with Cif Creation Mode Manual)</code></span>
                </div>
	            <div class="file-placeholder">
		            <i class="fas fa-file-alt file-icon"></i>
		            <div class="file-text"><span class="checkmark"></span>File Uploaded</div>
		            <div class="file-subtext">Document added to Doc Tray</div>
	            </div>
<%--                <input type="file" class="<%=fileclass%> base64file originalfile" name='originalfile'  data-filedesc="UPLOAD Original Seen And Verified Form" data-show-upload="false" data-show-remove="false" data-filebase64="<%=originalSeenCertificate%>" data-filebase64ext="<%=originalSeenCertificateExt%>">--%>
            </div>
            <div class="col-lg-6  custSig <%=sibcustomer.equals("N")?"":"hide"%> ">
                <div class="border-bottom pb-2 mb-2">
                    <span class="fw-bold">Customer Signature <code>*(Mandatory for Non Sib customers)</code></span>
                </div>
<%--                <input type="file" class="<%=fileclass%> base64file custsig" name='custsig'  data-filedesc="UPLOAD Customer Signature" data-show-upload="false" data-show-remove="false" data-filebase64="<%=custSig%>" data-filebase64ext="<%=custSigExt%>">--%>
            </div>
            </div>
</div>






    <div class="row mb-3 nresdiv <%=res_stat.equals("N")?"":"hide"%> ">
        <div class="border-bottom pb-2 mb-2">
            <span class="fw-bold">PASSPORT Details</span>
        </div>
        <div class="row">
            <div class="col-lg-6">
                <input type="file" class="<%=fileclass%> base64file passportfile" name='passportfile'  data-filedesc="UPLOAD PASSPORT" data-show-upload="false" data-show-remove="false" data-filebase64="<%=passimg%>" data-filebase64ext="<%=passext%>">
            </div>
            <div class="col-lg-6">
                <div class="mb-2">
<%--                    <div class="form-check-horizontal">--%>
<%--                        <button type="button" class="btn btn-file btn-light passportocr" title="Details will be autofilled">--%>
<%--                            <i class="ph-check-circle me-2"></i>--%>
<%--                            OCR Mode--%>
<%--                        </button>--%>
<%--                    </div>--%>
                </div>
                <div class="mb-2">
                    <label class="form-label">Passport number</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="text" class="form-control passport" placeholder="" name="passport"  value="<%=passportNumber%>">
                        <input type="hidden" class="form-control ocr_passport" placeholder="" name="ocr_passport"  >
                        <div class="form-control-feedback-icon">
                            <i class="ph-identification-card text-muted"></i>
                        </div>
                    </div>
                </div>
                <div class="mb-2">
                    <label class="form-label">Passport Name</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="text" class="form-control passportname" placeholder="" name="passportname"  value="<%=passportName%>">
                        <input type="hidden" class="form-control ocr_passport" placeholder="" name="ocr_passport"  >
                        <div class="form-control-feedback-icon">
                            <i class="ph-identification-card text-muted"></i>
                        </div>
                    </div>
                </div>
                <div class="mb-2">
                    <label class="form-label">Expiry Date</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="date" class="form-control passportexp" placeholder="DD-MM-YYYY" name="passportexp"  value="<%=passportExpiryDate%>">
                        <input type="hidden" class="form-control ocr_passportexp" placeholder="" name="ocr_passportexp">
                        <div class="form-control-feedback-icon">
                            <i class="ph-calendar-blank text-muted"></i>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>

    <div class="row mb-3 nresdiv <%=res_stat.equals("N")?"":"hide"%> ">
        <div class="border-bottom pb-2 mb-2">
            <span class="fw-bold">VISA Details</span>
        </div>
        <div class="row">
            <div class="col-lg-6">
                <input type="file" class="<%=fileclass%> base64file visafile"  data-filedesc="UPLOAD FILE SELECTED" name='visafile' data-show-upload="false" data-show-remove="false" data-filebase64="<%=visaimg%>" data-filebase64ext="<%=visaext%>">
            </div>
            <div class="col-lg-6">
                <div class="mb-2">
                    <label class="form-label">Document Type</label>
                    <div class="form-check-horizontal">
                       <select class="form-control form-select visaocimode" name="visaocimode" value="<%=visaocitype%>">
                           <option value="">Select</option>
                           <option value="V" <%=visaocitype.equals("V")?"selected":""%>>VISA</option>
                           <option value="O" <%=visaocitype.equals("O")?"selected":""%>>OCI</option>
                           <option value="C" <%=visaocitype.equals("C")?"selected":""%>>CDC Number</option>
                       </select>
                    </div>
                </div>
                <div class="mb-2">
                    <label class="form-label">Document number</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="text" class="form-control visa" placeholder="" name="visa"  value="<%=visaocinumber%>">
                        <div class="form-control-feedback-icon">
                            <i class="ph-identification-card text-muted"></i>
                        </div>
                    </div>
                </div>
                <div class="mb-2">
                    <label class="form-label">Visa Expiry Date</label>
                    <div class="form-control-feedback form-control-feedback-start">
                        <input type="date" class="form-control visa_exp" placeholder="DD-MM-YYYY" name="visa_exp"   value="<%=visaexp%>">
                        <input type="hidden" class="form-control ocr_visa_exp" placeholder="" name="ocr_visa_exp">
                        <div class="form-control-feedback-icon">
                            <i class="ph-calendar-blank text-muted"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>


</form>