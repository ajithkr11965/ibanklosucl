
<%--
  Created by IntelliJ IDEA.
  User: SIBL18202
  Date: 13-06-2024
  Time: 13:58
  To change this template use File | Settings | File Templates.
--%>
<%
	Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
%>
<script src="assets/js/custom/WI/eligibilityDetails.js"></script>
<div class="d-flex flex-stack border rounded px-7 py-3 mb-2">
	<div class="">
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


			<!-- Eligibility Details -->
			<div class="row">
				<div class="col-lg-3">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="col-form-label">Vehicle Amount</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="vehicle-eligible-amount" class="form-control" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-3" id="ltv-percent-group" style="display: none;">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="col-form-label">LTV Percent</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="ltv-percent" class="form-control" placeholder="Enter LTV Percent" max="100" step="0.01">
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-3">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="col-form-label">LTV Amount</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="ltv-amount" class="form-control" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-3">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="col-form-label">Additional LTV Amount</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="add-ltv-amount" class="form-control" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-3">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="col-form-label">Requested Loan Amount</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="requested-loan-amount" class="form-control" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-3">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="col-form-label">Tenor</div>
								<div class="col-lg-12 form-control-feedback form-control-feedback-start">
									<input type="number" id="eligible-tenor" class="form-control" readonly>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-12">
					<div class="mb-3 mt-3">
						<div class="row">
							<div class="col-md-12">
								<div class="text-center mt-3" <%= checker ? "style='display:none'" : "" %>>
<%--									<button type="button" class="btn btn-sm btn-primary check-eligibility-button">Run Eligibility</button>--%>
									<button type="button" class="btn btn-sm btn-primary check-eligibility-button" id="check-eligibility-button">
															<span class="indicator-label">
															Run Eligibility</span>
															<span class="indicator-progress">
																Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
															</button>
								</div>
								<%--					<button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#kt_modal_1">--%>
								<%--						Launch demo modal--%>
								<%--					</button>--%>
							</div>
						</div>
					</div>
				</div>
				<div class="separator separator-content border-info-subtle my-5">
					<i class="ki-duotone ki-chart-pie-4 fs-3 text-info-emphasis"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
				</div>
				<div id="proceedSection" class="row">
					<div class="col-lg-3">
						<div class="mb-2 mt-2">
							<div class="row">
								<div class="col-md-12">
									<div class="col-form-label">Eligible Loan amount</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<input type="number" id="eligible-loan-amount" class="form-control" readonly>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-2">
						<div class="mb-2 mt-2">
							<div class="row">
								<div class="col-md-12">
									<div class="col-form-label">Card Rate</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<input type="number" id="card-rate" class="form-control" readonly>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-2">
						<div class="mb-2 mt-2">
							<div class="row">
								<div class="col-md-12">
									<div class="col-form-label">Program based Eligiblity</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<input type="number" id="program-eligible-amount" class="form-control" readonly>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-2">
						<div class="mb-2 mt-2">
							<div class="row">
								<div class="col-md-12">
									<div class="col-form-label">LTV Percentage</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<input type="number" id="ltv-percentage" class="form-control" readonly>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-3">
						<div class="mb-2 mt-2">
							<div class="row">
								<div class="col-md-12">
									<div class="col-form-label">EMI</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<input type="number" id="emi" class="form-control" readonly>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-6">
						<div class="mb-3 mt-3">
							<div class="row">
								<div class="col-md-12">
									<div class="fw-semibold">Amount Transferred To Dealer</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<input type="number" min="" id="dealter-loan-amount" class="form-control" readonly>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-lg-6">
						<div class="mb-3 mt-3">
							<div class="row">
								<div class="col-md-12">
									<div class="fw-semibold">Amount Transferred To Insurance</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<input type="number" min="" id="ins-loan-amount" class="form-control" readonly>
									</div>
								</div>
							</div>
						</div>
					</div>

					<div class="col-lg-4">
						<div class="mb-2 mt-2">
							<div class="row">
								<div class="col-md-12">
									<div class="col-form-label">Recommended Loan Amount</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<div class="input-group">
											<input type="number" id="rm-recommend-loan-amount" name="rmRecommendLoanAmount" class="form-control" readonly>
											<a class="input-group-text btn btn-sm btn-info" id="edit-amount-icon" style="display:none;"><i class="ki-duotone ki-pencil"><span class="path1"></span><span class="path2"></span></i></a>
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
									<div class="col-form-label">Recommended ROI</div>
									<div class="col-lg-12 form-control-feedback form-control-feedback-start">
										<div class="input-group">
											<input type="number" id="rm-recommended-roi" name="rmRecommendROI" class="form-control" readonly>
											<a class="input-group-text btn btn-sm btn-info" id="edit-roi-icon" style="display:none;"><i class="ki-duotone ki-pencil hover-elevate-up"><span class="path1"></span><span class="path2"></span></i></a>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="col-lg-12">
						<div class="mb-2 mt-2">
							<div class="row">
								<div class="col-md-12 justify-content-center">
									<%--					<div class="text-center mt-3" <%= checker ? "style='display:none'" : "" %>>--%>
									<%--						<button type="button" class="btn btn-sm btn-primary check-eligibility-button">Check Eligibility</button>--%>
									<%--					</div>--%>
									<button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal" data-bs-target="#kt_modal_1">
										Calculate
									</button>
									<button type="button" id="reset-button" class="btn btn-sm btn-secondary">
										Reset
									</button>
								</div>
							</div>
						</div>
					</div>
					</div>
				</div>
				<div class="text-end">
					 <button type="button" class="btn btn-primary proceed-button proceed-button-rm" id="proceed-eligibility">Proceed</button>
				</div>
			</div>
		</div>
	</div>
</div>



<div class="modal fade" tabindex="-1" id="kt_modal_1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Eligibility Details</h3>
                <div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
                    <i class="ki-duotone ki-cross fs-1"><span class="path1"></span><span class="path2"></span></i>
                </div>
            </div>

            <div class="modal-body">
                <div class="row mb-4">
                    <div class="col-md-6">
                        <h5 class="mb-3">Loan Details</h5>
                        <div class="mb-2">
                            <label for="modal-ltv-amount" class="form-label">LTV Amount</label>
                            <input type="number" class="form-control" id="modal-ltv-amount" readonly>
                        </div>
                        <div class="mb-2">
                            <label for="modal-ltv-amount" class="form-label">Additional LTV Amount</label>
                            <input type="number" class="form-control" id="add-modal-ltv-amount" readonly>
                        </div>
                        <div>
                            <label for="modal-requested-loan-amount" class="form-label">Requested Loan Amount</label>
                            <input type="number" class="form-control" id="modal-requested-loan-amount" readonly>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <h5 class="mb-3">Eligibility</h5>
                        <div class="mb-2">
                            <label for="modal-program-eligibility" class="form-label">Program Based Eligibility</label>
                            <input type="number" class="form-control" id="modal-program-eligibility" readonly>
                        </div>
	                     <div class="mb-2">
                            <label for="modal-card-rate" class="form-label">Card Rate</label>
                            <input type="number" class="form-control" id="modal-card-rate" readonly>
                        </div>

                    </div>
                </div>

                <div class="row mb-4">
	                <div class="separator separator-content border-dark my-15"><span class="w-250px fw-bold">CPC Recommendations</span></div>
                    <div class="col-md-6" id="roimodalrow">

                        <div>
                            <label for="modal-recommended-roi" class="form-label">Recommended ROI</label>
                            <input type="number" class="form-control" id="modal-recommended-roi">
                        </div>
                    </div>
                    <div class="col-md-6" id="amtmodalrow">
                        <div>
                            <label for="modal-recommended-amount" class="form-label">Recommended Amount</label>
                            <input type="number" class="form-control" id="modal-recommended-amount">
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6">
                        <div class="mb-3">
                            <label for="modal-emi" class="form-label">EMI</label>
                            <input type="number" class="form-control" id="modal-emi" readonly>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="mb-3">
                            <label for="modal-emi" class="form-label">Amount Transferred To Dealer</label>
                            <input type="number" class="form-control" id="modal-dealAmt" readonly>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="mb-3">
                            <label for="modal-emi" class="form-label">Amount Transferred To Insurance</label>
                            <input type="number" class="form-control" id="modal-insAmt" readonly>
                        </div>
                    </div>
                    <div class="col-md-6 mt-8 align-items-end">
                        <button type="button" class="btn btn-sm btn-primary me-2" id="fetch-emi-button">Fetch EMI</button>

                    </div>
                </div>
            </div>

            <div class="modal-footer">
	             <button type="button" class="btn btn-sm btn-success" id="confirm-button">Confirm</button>
                <button type="button" class="btn btn-sm btn-light" id="cancel-button" data-bs-dismiss="modal">Cancel</button>
            </div>
        </div>
    </div>
</div>


