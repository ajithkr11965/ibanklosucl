<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanBasic" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VLHunterDetails" %>
<%@ page import="java.text.SimpleDateFormat" %><%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 02-07-2024
  Time: 18:25
  To change this template use File | Settings | File Templates.
--%>
<%
	VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
	Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
	List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
	VehicleLoanBasic vehicleLoanBasic = null;
	VLHunterDetails vlHunterDetail = null;
	String applicantId = "", wiNum = "", slno = "", applicantype = "", showFlg = "", accordion_style = "btn-active-light-primary";
	;
	if (vehicleLoanMaster != null) {
		vehicleLoanApplicants = vehicleLoanMaster.getApplicants().stream()
							.filter(fd -> "N".equals(fd.getDelFlg()))
							.toList();
		for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
			applicantId = String.valueOf(applicant.getApplicantId());
			List<VLHunterDetails> hunterDetails = applicant.getVlHunterDetailsList().stream()
							.filter(fd -> "N".equals(fd.getDelFlg()))
							.toList();

			if (hunterDetails != null && !hunterDetails.isEmpty()) {
				for (VLHunterDetails vlHunterDetails : hunterDetails) {
					if ("N".equals(vlHunterDetails.getDelFlg())) {
						vlHunterDetail = vlHunterDetails;
						showFlg = "show";
						accordion_style = "btn-active-light-success";
					}
				}
			}

		}
	} else {

	}


%>
<!--begin::Timeline item-->
<div class="timeline-item">
	<!--begin::Timeline line-->
	<div class="timeline-line w-40px"></div>
	<!--end::Timeline line-->
	<!--begin::Timeline icon-->
	<div class="kttimeline-icon symbol symbol-circle symbol-40px me-4">
		<div class="symbol-label">
			<i class="ki-duotone ki-pointers fs-2 ">
				<span class="path1"></span>
				<span class="path2"></span>
				<span class="path3"></span>
			</i>
		</div>
	</div>
	<!--end::Timeline icon-->
	<!--begin::Timeline content-->
	<div class="timeline-content mb-10 mt-n1">
		<!--begin::Timeline heading-->
		<div class="pe-3 mb-5">
			<!--begin::Title-->
			<div class="fs-5 fw-semibold mb-2 ">Step 1</div>
			<!--end::Title-->
			<!--begin::Description-->
			<div class="d-flex align-items-center mt-1 fs-6">

			</div>
			<!--end::Description-->
		</div>
		<!--end::Timeline heading-->
		<!--begin::Timeline details-->
		<div class="" id="timeline">
			<div class="border rounded px-7 py-3">
				<div class="w-100">
					<div class="accordion-header py-3 d-flex" data-bs-toggle="collapse" id="hunterlistDetails" data-bs-target="#huntertable">
						<label class="btn btn-outline btn-outline-dashed <%=accordion_style%> <%=showFlg%> p-7 d-flex align-items-center mb-2" for="huntertable">
							<i class="ki-duotone ki-scroll fs-4x me-4">
								<span class="path1"></span>
								<span class="path2"></span>
								<span class="path3"></span>
							</i>

							<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-3">Hunter Details</span>
                        <span class="text-muted fw-semibold fs-6">
                          Run Hunter check to identify suspicious applications
                        </span>
                    </span>
						</label>
					</div>
					<div id="huntertable" class="fs-6 collapse show ps-10" data-bs-parent="#vl_checker_int">

						<div class="row">
							<div class="col-sm-12">
								<div class="mb-3 mt-3">
									<div class="row">
										<div class="col-sm-12">
											<table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
												<thead>
													<tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
														<th>Applicant Type</th>
														<th>Applicant Name</th>
														<th>Hunter</th>
														<th>Run date</th>
														<th>Score</th>
														<th>Decision</th>
														<th>Status</th>
													</tr>
												</thead>
												<tbody>
													<%
														SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                                        String applicantType="";
														for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
															applicantId = String.valueOf(applicant.getApplicantId());
                                                            if ("A".equals(applicant.getApplicantType())) {
															applicantType = "Applicant";
														} else if ("C".equals(applicant.getApplicantType())) {
															applicantType = "Co-Applicant";
														} else if ("G".equals(applicant.getApplicantType())) {
															applicantType = "Guarantor";
														}
															if (!applicant.getApplicantType().equals("G")) {
//                                                                List<VLHunterDetails> hunterDetails = applicant.getVlHunterDetailsList();
//																	for (VLHunterDetails vlHunterDetails : hunterDetails) {
//																		if ("N".equals(vlHunterDetails.getDelFlg())) {
//																			vlHunterDetail = vlHunterDetails;
//																		}
//																	}

													%>
													<tr class="table-row-gray-400">

														<td><%=applicantType%>
														</td>
														<td class="text-gray-800 text-hover-primary fs-7 fw-bold"><%=applicant.getBasicapplicants().getApplicantName()%>
														</td>
														<td>

															<button id="kt_button_hunt_<%=applicantId%>" type="button"
															        class="btn btn-sm btn-light-primary runhunterCheck applicantHunterData"
															        data-applicantId="<%=applicantId%>">
																<span class="indicator-label">
         <%= vlHunterDetail != null && vlHunterDetail.getApplicantId().equals(applicant.getApplicantId()) ? "Re-run Hunter" : "Run Hunter" %>
    </span>
																<span class="indicator-progress">
        Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
    </span>

															</button>
														</td>
														<td class="runDate"><%= vlHunterDetail != null && vlHunterDetail.getApplicantId().equals(applicant.getApplicantId()) ? dateFormat.format(vlHunterDetail.getTimestamp()) : "" %>
														</td>
														<td class="score"><%= vlHunterDetail != null && vlHunterDetail.getApplicantId().equals(applicant.getApplicantId()) ? vlHunterDetail.getScore() : "" %>
														</td>
														<td class="decision"><%= vlHunterDetail != null && vlHunterDetail.getApplicantId().equals(applicant.getApplicantId()) ? vlHunterDetail.getDecision() : "" %>
														</td>
														<td class="status"><%= vlHunterDetail != null && vlHunterDetail.getApplicantId().equals(applicant.getApplicantId()) ? (vlHunterDetail.getMatches() >0 ? "FAILED" : "PASS") : "" %>
														</td>
													</tr>
													<%
															}
														}
													%>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>

						</div>


						<div class="text-end">
							<input type="hidden" id="collapseDisable" value="<%=checker?1:0%>">
							<%if (checker) {%>
							<button type="button" id="hunterSave" class="btn btn-sm btn-primary hunterlistSave">Save<i class="ph-paper-plane-tilt ms-2"></i></button>
							<%}%>
						</div>
					</div>
				</div>


			</div>
		</div>
		<!--end::Timeline details-->
	</div>
	<!--end::Timeline content-->
</div>
<!--end::Timeline item-->




