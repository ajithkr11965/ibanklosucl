<%--
  Created by IntelliJ IDEA.
  User: SIBL15719
  Date: 17-07-2024
  Time: 15:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanVehicle" %>
<%@ page import="org.aspectj.asm.IRelationship" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanFcvCpvCfr" %>

<%
    VehicleLoanVehicle vlvehicle = (VehicleLoanVehicle) request.getAttribute("vehicleDetails");
    Employee userdt= (Employee) request.getAttribute("userdata");
    VehicleLoanApplicant applicant=null;
    if(request.getAttribute("general")!=null)
        applicant = (VehicleLoanApplicant) request.getAttribute("general");
    String apptype= request.getAttribute("apptype").toString();
    String sibCustomer="",custID="",residentialStatus="",appid="",rsm_sol="",rsm_solname="";
    String rsm_ppc="",rsm_ppcname="";
    String can_ppc="",can_ppcname="",relationship="";
    boolean completed=false;
    VehicleLoanFcvCpvCfr fcvCpvCfr = (VehicleLoanFcvCpvCfr) request.getAttribute("fcvCpvCfr");
    if(applicant!=null)
    {
        sibCustomer=applicant.getSibCustomer();
        custID=applicant.getCifId();
        residentialStatus=applicant.getResidentFlg();
        completed=true;
        appid= String.valueOf(applicant.getApplicantId());


        rsm_sol= applicant.getRsmsol()==null ?"":applicant.getRsmsol();
        relationship = applicant.getRelationWithApplicant()==null ?"":applicant.getRelationWithApplicant();
        rsm_solname= applicant.getRsmsolname()==null ?"":applicant.getRsmsolname();
        rsm_ppc= applicant.getRsmppc()==null ?"":applicant.getRsmppc();
        rsm_ppcname= applicant.getRsmppcname()==null ?"":applicant.getRsmppcname();
        can_ppc= applicant.getCanvassedppc()==null ?"":applicant.getCanvassedppc();
        can_ppcname= applicant.getCanvassedppcname()==null ?"":applicant.getCanvassedppcname();
    }
    String accordion_style="btn-active-light-primary";
    if(fcvCpvCfr != null){
        accordion_style="btn-active-light-success btn-light-success";
    }

    String showFlg="";
    long mainappid = vlvehicle.getApplicantId();
    String coapponly=request.getParameter("coapponly");
    String apponly=request.getParameter("apponly");
    coapponly=coapponly==null?"N":coapponly;
    apponly=apponly==null?"N":apponly;
%>
<div class="kt">
<div class="border rounded px-7 py-1">
    <div class="accordion-header py-1 d-flex collapsed" data-bs-toggle="collapse" id="fcvcpvcfrDetails" data-bs-target="#fcvcpvcfrtable">

        <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2 <%=accordion_style%> <%=showFlg%>" for="fcvcpvcfrtable" id="fcvcpvlabel">
            <i class="ki-duotone ki-scroll fs-4x me-4">
                <span class="path1"></span>
                <span class="path2"></span>
                <span class="path3"></span>
            </i>

            <span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-3">FCV , CPV, CFR Status</span>
                        <span class="text-muted fw-semibold fs-6">
                          To Capture FCV, CPV, CFR Status
                        </span>
                    </span>
        </label>

    </div>

    <div id="fcvcpvcfrtable" class="fs-6 collapse ps-10" data-bs-parent="#vl_checker_int">
        <form id="fcvCpvCfrForm"  class="form-details fcvcpvcfr" data-code="<%=apptype%>-1" action="#" data-completed="<%=completed%>" app-id="<%=mainappid%>">

            <input type="hidden" class="bpmurl" value="<%=request.getAttribute("appurl")%>"/>
            <input type="hidden" name="appid" class="appid" value="<%=appid%>">


            <div class="row  mb-2">
                <div class="col-lg-4">
                    <div class="mb-3 mt-3">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="col-lg-12 form-control-feedback ps-5 form-control-feedback-start">
                                    FCV Status :
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="mb-3 mt-3">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                    <select id="fcvstatus" name="fcvstatus"  class="form-control select">
                                        <option value=""> Select </option>
                                        <option value="PASS" <%= fcvCpvCfr != null && "PASS".equals(fcvCpvCfr.getFcvStatus()) ? "selected" : "" %>>PASS</option>
                                        <option value="NA" <%= fcvCpvCfr != null && "NA".equals(fcvCpvCfr.getFcvStatus()) ? "selected" : "" %>>NA</option>
                                        <option value="Negative" <%= fcvCpvCfr != null && "Negative".equals(fcvCpvCfr.getFcvStatus()) ? "selected" : "" %>>Negative</option>
                                        <option value="Refer to credit" <%= fcvCpvCfr != null && "Refer to credit".equals(fcvCpvCfr.getFcvStatus()) ? "selected" : "" %>>Refer to Credit</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4 mt-3 uploadfcv">
                    <input type="file" id="fileUploadFcv" class="uploadfcv base64file file-input-overwrite file-input"  data-filedesc="Upload FCV File" name='fileUploadFcv' data-show-upload="false" data-show-remove="false"  data-applicantid="<%=appid%>" data-show-preview="false">
                    <% if (fcvCpvCfr != null && fcvCpvCfr.getFcvFileUploaded() != null && fcvCpvCfr.getFcvFileUploaded()) { %>
                    <span class="text-success">File uploaded</span>
                    <% } %>
                </div>
            </div>

            <div class="row  mb-2">
                <div class="col-lg-4">
                    <div class="mb-3 mt-3">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="col-lg-12 form-control-feedback ps-5 form-control-feedback-start">
                                    CPV Status :
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="mb-3 mt-3">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                    <select id="cpvstatus" name="cpvstatus" class="form-control select">
                                        <option value=""> Select </option>
                                        <option value="PASS" <%= fcvCpvCfr != null && "PASS".equals(fcvCpvCfr.getCpvStatus()) ? "selected" : "" %>>PASS</option>
                                        <option value="NA" <%= fcvCpvCfr != null && "NA".equals(fcvCpvCfr.getCpvStatus()) ? "selected" : "" %>>NA</option>
                                        <option value="Negative" <%= fcvCpvCfr != null && "Negative".equals(fcvCpvCfr.getCpvStatus()) ? "selected" : "" %>>Negative</option>
                                        <option value="Refer to credit" <%= fcvCpvCfr != null && "Refer to credit".equals(fcvCpvCfr.getCpvStatus()) ? "selected" : "" %>>Refer to Credit</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4 mt-3 uploadcpv">
                    <input type="file" id="fileUploadCpv" class="uploadcpv base64file file-input-overwrite  file-input"  data-filedesc="Upload CPV File" name='fileUploadCpv' data-show-upload="false" data-show-remove="false"  data-applicantid="<%=appid%>" data-show-preview="false">
                    <% if (fcvCpvCfr != null && fcvCpvCfr.getCpvFileUploaded() != null && fcvCpvCfr.getCpvFileUploaded()) { %>
                    <span class="text-success">File uploaded</span>
                    <% } %>
                </div>
            </div>
            <div class="row  mb-2">
                <div class="col-lg-4">
                    <div class="mb-3 mt-3">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="col-lg-12 form-control-feedback ps-5 form-control-feedback-start">
                                    CFR Match Found :
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="mb-3 mt-3">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="col-lg-12 form-control-feedback form-control-feedback-start">
                                    <select id="cfrstatus" name="cfrstatus" class="form-control select">
                                        <option value=""> Select </option>
                                        <option value="Yes" <%= fcvCpvCfr != null && "Yes".equals(fcvCpvCfr.getCfrStatus()) ? "selected" : "" %>>Yes</option>
                                        <option value="No" <%= fcvCpvCfr != null && "No".equals(fcvCpvCfr.getCfrStatus()) ? "selected" : "" %>>No</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4 mt-3 uploadcfr">
                    <input type="file" id="fileUploadCfr" class="fileUploadCfr base64file file-input-overwrite  file-input"  data-filedesc="Upload CFR File" name='fileUploadCfr' data-show-upload="false" data-show-remove="false"  data-applicantid="<%=appid%>" data-show-preview="false">
                    <% if (fcvCpvCfr != null && fcvCpvCfr.getCfrFileUploaded() != null && fcvCpvCfr.getCfrFileUploaded()) { %>
                    <span class="text-success">File uploaded</span>
                    <% } %>
                </div>
            </div>
            <div class="text-end">
                <button type="button" id="FcvEdit" class="btn btn-sm btn-primary FcvEdit">Edit<i class="ph-pen ms-2 me-2"></i></button>
                <button type="button" id="FcvSave" class="btn btn-sm btn-primary FcvSave" disabled>Save<i class="ph-paper-plane-tilt ms-2"></i></button>
            </div>

        </form>
    </div>
</div>
</div>


