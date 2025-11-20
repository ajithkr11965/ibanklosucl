<%--
  Created by IntelliJ IDEA.
  User: SIBL16023
  Date: 10-05-2024
  Time: 11:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanDetails" %>
<%@ page import="com.sib.ibanklosucl.model.Misrct" %>
<%@ page import="java.util.List" %>
<%
	VehicleLoanDetails details = (VehicleLoanDetails) request.getAttribute("loanDetails");
	if (details == null) {
%>
<h2>No loan details available</h2>
<%
} else {
	String vehicleAmount = details.getVehicleAmt() != null ? details.getVehicleAmt().toString() : "-";
	String loanAmount = details.getLoanAmt() != null ? details.getLoanAmt().toString() : "-";
	String tenor = String.valueOf(details.getTenor());
	String roiType = details.getRoiType() != null ? details.getRoiType() : "-";
	String foirType = details.getFoirType() != null ? details.getFoirType() : "-";
    String insVal = details.getInsVal() != null ? details.getInsVal() : "-";
    String insAmt = details.getInsAmt() != null ? details.getInsAmt().toString() : "";
    String insType = details.getInsType() != null ? details.getInsType() : "";
%>
<%--<script src="assets/js/custom/WI/loandetails.js"></script>--%>

<div class="  border rounded px-7 py-3 mb-2">
	<div class="w-100">
		<div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="loanDetails" data-bs-target="#loanDetailsContent">
			<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2" for="loanDetailsContent">
				<i class="ki-duotone ki-enjin-coin  fs-3x me-4">
					<span class="path1"></span>
					<span class="path2"></span>
					<span class="path3"></span>
					<span class="path4"></span>
					<span class="path5"></span>
					<span class="path6"></span>
				</i>
				<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-4">Loan Details</span>
                        <span class="text-muted fw-semibold fs-7">
                         Enter Loan amount,tenor etc.
                        </span>
                    </span>
			</label>
		</div>

		<div id="loanDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
			<div class="row">
				<div class="col-sm-12">

					<div class="row">
						<div class="col-lg-6">
							<div class="mb-2 mt-2">
								<div class="row">
									<div class="col-md-12">
										<div class="fw-semibold">Vehicle Amount</div>
										<div class="col-lg-12 form-control-feedback form-control-feedback-start">
											<input type="number" id="vehicle-amount" class="form-control" value="<%= vehicleAmount %>" readonly disabled>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-6">
							<div class="mb-2 mt-2">
								<div class="row">
									<div class="col-md-12">
										<div class="fw-semibold">Requested Loan Amount</div>
										<div class="col-lg-12 form-control-feedback form-control-feedback-start">
											<input type="number" id="loan-amount" class="form-control" value="<%= loanAmount %>" placeholder="Enter loan amount" disabled>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="col-lg-6">
							<div class="mb-3 mt-3">
								<div class="row">
									<div class="col-md-12">
										<div class="fw-semibold">Tenor in Months</div>
										<div class="col-lg-12 form-control-feedback form-control-feedback-start">
											<input type="number" id="tenor" class="form-control" value="<%= tenor %>" placeholder="Enter tenor in months" disabled>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-3">
							<div class="mb-3 mt-3">
								<div class="row">
									<div class="col-md-12">
										<div class="fw-semibold">ROI Type</div>
										<div class="col-lg-12 form-control-feedback form-control-feedback-start">
											<div class="form-check">
												<input class="form-check-input" type="radio" name="roiType" id="roi-fixed"  value="fixed" disabled <%= "fixed".equalsIgnoreCase(roiType) ? "checked" : "" %>>
												<label class="form-check-label" for="roi-fixed">Fixed</label>
											</div>
											
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-3">
							<div class="mb-3 mt-3">
								<div class="row">
									<div class="col-md-12">
										<div class="fw-semibold">FOIR Type</div>
										<div class="col-lg-12 form-control-feedback form-control-feedback-start">
											<div class="form-check mb-2">
												<input class="form-check-input" disabled type="radio" name="foirType" id="foir-foir" value="foir" <%= "Y".equalsIgnoreCase(foirType) ? "checked" : "" %>>
												<label class="form-check-label" for="foir-foir">FOIR</label>
											</div>
											<div class="form-check">
												<input class="form-check-input" disabled type="radio" name="foirType" id="foir-non-foir" value="non-foir" <%= "N".equalsIgnoreCase(foirType) ? "checked" : "" %>>
												<label class="form-check-label" for="foir-non-foir">Non FOIR</label>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-4">
							<div class="mb-3 mt-3">
								<div class="row">
									<div class="col-md-12">
										<div class="fw-semibold">Loan Protection Insurance</div>
										<div class="col-lg-12 form-control-feedback form-control-feedback-start">
											<div class="form-check mb-2">
												<input class="form-check-input" disabled type="radio" name="insVal" id="ins-Val-Y" value="foir" <%= "Y".equalsIgnoreCase(insVal) ? "checked" : "" %>>
												<label class="form-check-label" for="foir-foir">Yes</label>
											</div>
											<div class="form-check">
												<input class="form-check-input" disabled type="radio" name="insVal" id="ins-Val-N" value="non-foir" <%= "N".equalsIgnoreCase(insVal) ? "checked" : "" %>>
												<label class="form-check-label" for="foir-non-foir">No</label>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

						<div class="col-lg-4">
							<div class="mb-2 mt-2">
								<div class="row">
									<div class="col-md-12">
										<div class="fw-semibold">Insurance Partner</div>
										<div class="col-lg-12 form-control-feedback form-control-feedback-start">
											<select class="form-control form-select insType" name="insType" id="insType" disabled>
												<option value="" >Please Select</option>
												<%
													List<Misrct> titles = (List<Misrct>) request.getAttribute("tppData");
													for (Misrct title : titles) {
														out.println("<option value=\"" + title.getCodevalue()+ "\" "+(title.getCodevalue().equals(insType) ? "selected" : "" )+" >" + title.getCodedesc() + "</option>");
													}
												%>
											</select>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-4">
							<div class="mb-2 mt-2">
								<div class="row">
									<div class="col-md-12">
										<div class="fw-semibold">Insurance Premium Amount</div>
										<div class="col-lg-12 form-control-feedback form-control-feedback-start">
											<input type="number" id="insAmt" class="form-control" value="<%= insAmt %>" placeholder="Enter Premium amount" disabled>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					</div>




			</div>

		</div>

	</div>
</div>
		<%
    }
%>
