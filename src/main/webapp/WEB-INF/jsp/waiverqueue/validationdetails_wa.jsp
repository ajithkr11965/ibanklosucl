<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanFcvCpvCfr" %><%--
  Created by IntelliJ IDEA.
  User: SIBL11965
  Date: 25-07-2024
  Time: 10:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
	VehicleLoanFcvCpvCfr fcvCpvCfr = (VehicleLoanFcvCpvCfr) request.getAttribute("fcvCpvCfr");
	String previousQueue = (String) request.getAttribute("previousQueue");
    String hidfcvStatus="";
    String hidcpvStatus="";
    String hidcfrStatus="";
		    boolean hidcpvFile=false;
            boolean hidfcvFile=false;
    if(fcvCpvCfr!=null) {
        hidfcvStatus=fcvCpvCfr.getFcvStatus();
        hidcpvStatus=fcvCpvCfr.getCpvStatus();
        hidcfrStatus=fcvCpvCfr.getCfrStatus();
        hidfcvFile=fcvCpvCfr.getFcvFileUploaded();
        hidcpvFile=fcvCpvCfr.getCpvFileUploaded();

    }

%>

<div class="d-flex flex-stack border rounded px-7 py-3 mb-2">
	<div class="w-100">
		<div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="validationDetailslink" data-bs-target="#validationDetailsContent">
			<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
			       for="validationDetailsContent">
				<i class="ki-duotone ki-question fs-3x me-4">
 <span class="path1"></span>
 <span class="path2"></span>
 <span class="path3"></span>
</i>
				<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-4">FCV/CPC/CFR Details</span>
                        <span class="text-muted fw-semibold fs-7">
                         Enter FCV/CPC/CFR Details details.
                        </span>
                    </span>
			</label>
		</div>
		<div id="validationDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
			<form id="fcvCpvCfrForm" name="fcvCpvCfrForm" enctype="multipart/form-data">
				<input type="hidden" name="hidfcvStatus" id="hidfcvStatus" value="<%=hidfcvStatus!=null ?hidfcvStatus:""%>">
				<input type="hidden" name="hidcpvStatus" id="hidcpvStatus" value="<%=hidcpvStatus!=null?hidcpvStatus:""%>">
				<div class="row mb-3">
					<div class="col-md-4">
						<label for="fcvStatus" class="form-label">FCV Status:</label>
						<select id="fcvStatus" name="fcvStatus" class="form-select" disabled>
							<option value="">Select</option>
							<option value="PASS" <%= fcvCpvCfr != null && "PASS".equals(hidfcvStatus) ? "selected" : "" %>>PASS</option>
							<option value="NA" <%= fcvCpvCfr != null && "NA".equals(hidfcvStatus) ? "selected" : "" %>>NA</option>
							<option value="Negative" <%= fcvCpvCfr != null && "Negative".equals(hidfcvStatus) ? "selected" : "" %>>Negative</option>
							<option value="Refer to credit" <%= fcvCpvCfr != null && "Refer to credit".equals(hidfcvStatus) ? "selected" : "" %>>Refer to credit
							</option>

						</select>
					</div>
					<div class="col-md-4">
						<label for="cpvStatus" class="form-label">CPV Status:</label>
						<select id="cpvStatus" name="cpvStatus" class="form-select" disabled>
							<option value="">Select</option>
							<option value="PASS" <%= fcvCpvCfr != null && "PASS".equals(hidcpvStatus) ? "selected" : "" %>>PASS</option>
							<option value="NA" <%= fcvCpvCfr != null && "NA".equals(hidcpvStatus) ? "selected" : "" %>>NA</option>
							<option value="Negative" <%= fcvCpvCfr != null && "Negative".equals(hidcpvStatus) ? "selected" : "" %>>Negative</option>
							<option value="Refer to credit" <%= fcvCpvCfr != null && "Refer to credit".equals(hidcpvStatus) ? "selected" : "" %>>Refer to credit
							</option>

						</select>
					</div>
					<div class="col-md-4">
						<label for="cfrFound" class="form-label">Whether CFR Found:</label>
						<select id="cfrFound" name="cfrFound" class="form-select" disabled>
							<option value="">Select</option>
							<option value="Yes" <%= fcvCpvCfr != null && "Yes".equals(hidcfrStatus) ? "selected" : "" %>>Yes</option>
							<option value="No" <%= fcvCpvCfr != null && "No".equals(hidcfrStatus) ? "selected" : "" %>>No</option>

						</select>
					</div>
				</div>
				<div id="fileUploadFcvSection" class="row mb-3"  >
					<div class="col-md-6">
						<label for="fileUploadFcv" class="form-label">Upload FCV File:</label>
<%--						<input type="file" id="fileUploadFcv" name="fileUploadFcv" class="form-control" required>--%>
						<% if (fcvCpvCfr != null && fcvCpvCfr.getFcvFileUploaded() != null && fcvCpvCfr.getFcvFileUploaded()) { %>
						<span class="text-success">File uploaded</span>
						<% } %>
					</div>
				</div>
				<div id="fileUploadCpvSection" class="row mb-3"  >
					<div class="col-md-6">
						<label for="fileUploadCpv" class="form-label">Upload CPV File:</label>
<%--						<input type="file" id="fileUploadCpv" name="fileUploadCpv" class="form-control" required>--%>
						<% if (fcvCpvCfr != null && fcvCpvCfr.getCpvFileUploaded() != null && fcvCpvCfr.getCpvFileUploaded()) { %>
						<span class="text-success">File uploaded</span>
						<% } %>


					</div>
				</div>

			</form>
		</div>
	</div>
</div>
