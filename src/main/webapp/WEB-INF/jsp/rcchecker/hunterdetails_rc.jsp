<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanBasic" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VLHunterDetails" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %><%--
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
    Map<Long, VLHunterDetails> hunterDetailsMap = new HashMap<>();
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

			if (!hunterDetails.isEmpty()) {
                hunterDetailsMap.put(applicant.getApplicantId(), hunterDetails.get(hunterDetails.size() - 1));
                showFlg = "show";
                accordion_style = "btn-active-light-success";
            }

		}
	} else {
		//System.out.println("in error - general is empty");
	}


%>
<!--begin::Timeline item-->
<div class="w-100">
			<div class="d-flex flex-stack border rounded px-7 py-3 mb-2">
				<div class="w-100">
					<div class="accordion-header  d-flex" data-bs-toggle="collapse" id="hunterlistDetails" data-bs-target="#huntertable">
						<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start p-7 d-flex align-items-center mb-2" for="huntertable">
							<i class="ki-duotone ki-scroll fs-3x me-4">
								<span class="path1"></span>
								<span class="path2"></span>
								<span class="path3"></span>
							</i>

							<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-4">Hunter Details</span>
                        <span class="text-muted fw-semibold fs-7">
                          Run Hunter check to identify suspicious applications
                        </span>
                    </span>
						</label>
					</div>
					<div id="huntertable" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">

						<div class="row">
							<div class="col-sm-12">
								<div class="mb-3 mt-3">
									<div class="row">
										<div class="col-sm-12">
											<table class="table table-sm table-bordered table-hover">
												<thead>
													<tr>
														<th>Applicant Type</th>
														<th>Applicant Name</th>
<%--														<th>Hunter</th>--%>
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
                                                                VLHunterDetails vlHunterDetails = hunterDetailsMap.get(applicant.getApplicantId());
//                                                              VLHunterDetails vlHunterDetails = hunterDetailsMap.get(applicant.getApplicantId());
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

														<td class="runDate"><%= vlHunterDetails != null && vlHunterDetails.getApplicantId().equals(applicant.getApplicantId()) ? dateFormat.format(vlHunterDetails.getTimestamp()) : "" %>
														</td>
														<td class="score"><%= vlHunterDetails != null && vlHunterDetails.getApplicantId().equals(applicant.getApplicantId()) ? vlHunterDetails.getScore() : "" %>
														</td>
														<td class="decision"><%= vlHunterDetails != null && vlHunterDetails.getApplicantId().equals(applicant.getApplicantId()) ? vlHunterDetails.getHunterUserRemarks() : "" %>
														</td>
														<td class="status"><%= vlHunterDetails != null && vlHunterDetails.getApplicantId().equals(applicant.getApplicantId()) ? (vlHunterDetails.getMatches() >0 ? "FAILED" : "PASS") : "" %>
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



					</div>
				</div>


			</div>
		</div>





