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
	String applicantId = "", wiNum = "", slno = "", applicantype = "",showFlg="",accordion_style="btn-active-light-primary";;
	if (vehicleLoanMaster != null) {
		vehicleLoanApplicants = vehicleLoanMaster.getApplicants();
		for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
			applicantId = String.valueOf(applicant.getApplicantId());
             List<VLHunterDetails> hunterDetails = applicant.getVlHunterDetailsList();
             if(hunterDetails!=null && !hunterDetails.isEmpty()) {
                 for (VLHunterDetails vlHunterDetails : hunterDetails) {
                     if ("N".equals(vlHunterDetails.getDelFlg())) {
						vlHunterDetail = vlHunterDetails;
                         showFlg="show";
                    accordion_style="btn-active-light-success";
                     }
                 }
			}

		}
	} else {
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
														for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
															applicantId = String.valueOf(applicant.getApplicantId());
															if (!applicant.getApplicantType().equals("G")) {
//                                                                List<VLHunterDetails> hunterDetails = applicant.getVlHunterDetailsList();
//																	for (VLHunterDetails vlHunterDetails : hunterDetails) {
//																		if ("N".equals(vlHunterDetails.getDelFlg())) {
//																			vlHunterDetail = vlHunterDetails;
//																		}
//																	}

													%>
													<tr>

														<td><%=applicant.getApplicantType()%>
														</td>
														<td><%=applicant.getBasicapplicants().getApplicantName()%>
														</td>
<%--														<td>--%>
<%--															<button type="button" class="btn btn-sm btn-primary runhunterCheck applicantHunterData"--%>
<%--															        data-applicantId="<%=applicantId%>">--%>
<%--																<%= vlHunterDetail != null && vlHunterDetail.getApplicantId().equals(applicant.getApplicantId()) ? "Re-run Hunter" : "Run Hunter" %>--%>
<%--															</button>--%>
<%--														</td>--%>
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



					</div>
				</div>


			</div>
		</div>





