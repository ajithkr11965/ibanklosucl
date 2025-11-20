<%--
  Created by IntelliJ IDEA.
  User: SIBL18202
  Date: 13-06-2024
  Time: 13:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.sib.ibanklosucl.model.EligibilityDetails" %><%
	EligibilityDetails details = (EligibilityDetails) request.getAttribute("eligibilityDetails");
	if (details == null) {
%>
<h2>No eligibility details available</h2>
<%
} else {
	String vehicleAmount = details.getVehicleAmt() != null ? details.getVehicleAmt().toString() : "-";
	String ltvAmount = details.getLtvAmt() != null ? details.getLtvAmt().toString() : "-";
	String loanAmount = details.getLoanAmt() != null ? details.getLoanAmt().toString() : "-";
	String tenor = String.valueOf(details.getTenor());
	String programEligibleAmount = details.getProgramEligibleAmt() != null ? details.getProgramEligibleAmt().toString() : "-";
	String eligibleLoanAmount = details.getEligibleLoanAmt() != null ? details.getEligibleLoanAmt().toString() : "-";
	String cardRate = details.getCardRate() != null ? details.getCardRate().toString() : "-";
	String emi = details.getEmi() != null ? details.getEmi().toString() : "-";
	boolean showLtvPercentGroup = "CUSTOM".equalsIgnoreCase(details.getLtvType());
	String ltvPercent = details.getLtvPer() != null ? details.getLtvPer().toString() : "-";
	String loanAmountRecommended = details.getLoanAmountRecommended()!= null ? String.valueOf(details.getLoanAmountRecommended()) :details.getEligibleLoanAmt().toString();
%>

<div class="d-flex flex-stack border rounded px-7 py-3 mb-2">
	<div class="w-100">
		<div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="ebityDetailslink" data-bs-target="#eligibilityDetailsContent">
			<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
				   for="eligibilityDetailsContent">
				<i class="ki-duotone ki-bank  fs-3x me-4">
					<span class="path1"></span>
					<span class="path2"></span>
				</i>
				<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-4">Eligiblity Details</span>
                        <span class="text-muted fw-semibold fs-7">
                         Find Loan eligiliblity,EMI etc.
                        </span>
                    </span>
			</label>
		</div>
		<div id="eligibilityDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">


			<div class="row">
				<div class="col-lg-4">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Vehicle Amount (Amount in Rs)</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="vehicle-eligible-amount" class="form-control" value="<%= vehicleAmount %>" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-4" id="ltv-percent-group" style="<%= showLtvPercentGroup ? "" : "display: none;" %>">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">LTV Percent</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number"  id="ltv-percent" class="form-control" value="<%= ltvPercent %>" placeholder="Enter LTV Percent" max="100" min="0.01" step="0.01">
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">LTV Amount (Amount in Rs)</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="ltv-amount" class="form-control" value="<%= ltvAmount %>" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Requested Loan Amount (Amount in Rs)</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="requested-loan-amount" class="form-control" value="<%= loanAmount %>" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-4">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Tenor (in Months)</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="eligible-tenor" class="form-control" value="<%= tenor %>" readonly>
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
								<div class="fw-semibold">Program Based Eligibility</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="program-eligible-amount" class="form-control" value="<%= programEligibleAmount %>" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-6">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="fw-semibold">Eligible Loan Amount  (Amount in Rs)</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="eligible-loan-amount" class="form-control" value="<%= eligibleLoanAmount %>" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-12">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-4">
								<div class="fw-semibold">Card Rate</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="card-rate" class="form-control" value="<%= cardRate %>" readonly>
								</div>
							</div>
							<div class="col-md-4">
								<div class="fw-semibold">EMI  (Amount in Rs)</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="emi" class="form-control" value="<%= emi %>" readonly>
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

