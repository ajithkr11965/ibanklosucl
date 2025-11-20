<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanBasic" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VLBlackList" %>
<%@ page import="java.util.*" %>
<%
	try {
	VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
	VehicleLoanBasic vehicleLoanBasic = null;
	String applicantId = "", wiNum = "", slno = "", applicantype = "", showFlg = "", accordion_style = "btn-active-light-primary";
	Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
	if (vehicleLoanMaster != null) {
		vehicleLoanApplicants = vehicleLoanMaster.getApplicants();
		for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
            applicantId = String.valueOf(applicant.getApplicantId());
			List<VLBlackList> blacklists = applicant.getVlBlackList().stream()
							.filter(fd -> "N".equals(fd.getDelFlg()))
							.toList();
			if (blacklists != null && !blacklists.isEmpty()) {
				VLBlackList blacklist = null;
				if (blacklists != null) {
					for (VLBlackList vlBlackList : blacklists) {
						if ("N".equals(vlBlackList.getDelFlg())) {
							blacklist = vlBlackList;
							showFlg = "show";
							accordion_style = "btn-active-light-success";
						}
					}
				}
			} else {
                out.println("in error - bl is empty");
			}
		}
	} else {
		out.println("in error - general is empty");
	}
%>



<div class="w-100">
	<div class="d-flex flex-stack border rounded px-7 py-3 mb-2">


			<input type="hidden" name="applicantId" class="applicantId" value="<%=applicantId%>">
			<input type="hidden" name="applicantype" class="applicantype" value="<%=applicantype%>">
			<div class="w-100">
				<div class="accordion-header d-flex  " data-bs-toggle="collapse" id="blacklistDetails" data-bs-target="#blacklisttable">
					<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start p-5 d-flex align-items-center mb-2" for="blacklisttable">
						<i class="ki-duotone ki-faceid  fs-3x me-4">
							<span class="path1"></span>
							<span class="path2"></span>
							<span class="path3"></span>
							<span class="path4"></span>
							<span class="path5"></span>
							<span class="path6"></span>
						</i>
						<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-4">Blacklist Details</span>
                        <span class="text-muted fw-semibold fs-7">
                          Check the PAN,DOB,AADHAAR are in the Blacklist group
                        </span>
                    </span>
					</label>
				</div>
				<div id="blacklisttable" class="fs-6 collapse  ps-10" data-bs-parent="#vl_rm_int">

					<div class="row">
						<div class="content d-flex flex-column flex-column-fluid">
							<div class="col-sm-12">
								<div class="mb-3 mt-3">
									<div class="row">
										<div class="col-sm-12">
											<div class="table-responsive">
												<table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
													<thead>
														<tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
															<th>Applicant Type</th>
															<th>Applicant Name</th>
															<th>DOB</th>
															<th>PAN</th>
															<th>AADHAR REFNO</th>
															<th>PASSPORT</th>
<%--															<th>Action</th>--%>
															<th>Run date</th>
															<th>Status</th>
														</tr>
													</thead>
													<tbody>
														<%
															for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
																String runDate = "";
																String status = "";
																String applicantType = "";
																boolean mismatch = false;
																String mismatchFields = "";

																List<VLBlackList> blacklists = applicant.getVlBlackList();
																VLBlackList blacklist = null;
																if (blacklists != null) {
																	for (VLBlackList vlBlackList : blacklists) {
																		if ("N".equals(vlBlackList.getDelFlg())) {
																			blacklist = vlBlackList;
																		}
																	}
																}
																Date dob = applicant.getKycapplicants().getPanDob();
																SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
																String formattedDob = simpleDateFormat.format(dob);
																String passport = (applicant.getKycapplicants() != null) ? StringUtils.defaultString(applicant.getKycapplicants().getPassportNumber()) : "";
																applicantId = (applicant.getApplicantId() != null) ? StringUtils.defaultString(String.valueOf(applicant.getApplicantId())) : "";
																if ("A".equals(applicant.getApplicantType())) {
																	applicantType = "Applicant";
																} else if ("C".equals(applicant.getApplicantType())) {
																	applicantType = "Co-Applicant";
																} else if ("G".equals(applicant.getApplicantType())) {
																	applicantType = "Guarantor";
																}

																if (blacklist != null) {
																	runDate = new SimpleDateFormat("dd/MM/yyyy").format(blacklist.getBlCheckDate());
																	status = blacklist.getBlCheckResult();
																	if (!applicant.getKycapplicants().getPanNo().equals(blacklist.getPan())) {
																		mismatch = true;
																		mismatchFields += "PAN, ";
																	}
																	String applicantDob = dateFormat.format(applicant.getBasicapplicants().getApplicantDob());
																	String blacklistDob = dateFormat.format(dateFormat.parse(blacklist.getDob()));

																	if (!applicantDob.equals(blacklistDob)) {
																		mismatch = true;
																		mismatchFields += "DOB, ";
																	}

//														if (!applicant.getKycapplicants().getPassportNumber().equals(blacklist.getPassport())) {
//															mismatch = true;
//															mismatchFields += "Passport, ";
//														}
																	if (mismatchFields.endsWith(", ")) {
																		mismatchFields = mismatchFields.substring(0, mismatchFields.length() - 2);
																	}
																} else {
                                                                    out.print("vlblack is null");
																}


														%>

														<tr class="table-row-gray-400">
															<td><%=applicantType%>
															</td>
															<td class="text-gray-800 text-hover-primary fs-7 fw-bold"><%=applicant.getBasicapplicants().getApplicantName()%>
															</td>
															<td class="text-gray-800 text-hover-primary fs-8 fw-bold"><%=formattedDob%>
															</td>
															<td class="text-gray-800 text-hover-primary fs-8 fw-bold"><%=applicant.getKycapplicants().getPanNo()%>
															</td>
															<td class="text-gray-800 text-hover-primary fs-8 fw-bold"><%=applicant.getKycapplicants().getAadharRefNum()%>
															</td>
															<td><%=passport%>
															</td>
<%--															<td>--%>
<%--																<button id="kt_button_<%=applicantId%>" type="button data-bl-applicantId="<%= blacklist != null ? "Re-run Blacklist" : "Run blacklist" %></span>--%>
<%--																<span class="indicator-progress">--%>
<%--        Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>--%>
<%--    </span>--%>
<%--																</button>--%>
<%--																<input type="hidden" class="applicantData" data-applicantId="<%=applicantId%>" data-dob="<%=formattedDob%>"--%>
<%--																       data-pan="<%=applicant.getKycapplicants().getPanNo()%>" data-passport="<%=passport%>">--%>
<%--															</td>--%>
															<td><%=runDate%>
															</td>
															<td>
																<%if ("Not Blacklisted".equals(status)) {%>
																<span class="badge badge-light-success mb-1"><%=status%></span>
																<%} else {%>
																<span class="badge bg-danger mb-1"><%=status%></span>
																<%}%>
																<% if (mismatch) { %>
																<div class="alert alert-dismissible bg-light-warning d-flex flex-column flex-sm-row p-1 mb-2">
																	<i class="ki-duotone ki-notification-bing fs-2hx text-warning me-1 mb-2 mb-sm-0  animation-blink"><span
																			class="path1"></span><span class="path2"></span><span class="path3"></span></i>
																	<div class="d-flex flex-column pe-0 pe-sm-10">
																		<h6 class="fw-semibold">KYC Details changed</h6>
																		<span>Details updated: <%=mismatchFields%>.
        </span>
																	</div>
																	<button type="button"
																	        class="position-absolute position-sm-relative m-2 m-sm-0 top-0 end-0 btn btn-icon ms-sm-auto"
																	        data-bs-dismiss="alert">
																		<i class="ki-duotone ki-cross fs-1 text-warning"><span class="path1"></span><span class="path2"></span></i>
																	</button>
																</div>
																<% } %>

															</td>
														</tr>
														<%}%>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

					</div>
					<div class="text-end">
						<input type="hidden" id="collapseDisable" value="<%=checker?1:0%>">
						<%if (checker) {%>

						<button type="button" id="blacklistSave" class="btn btn-sm btn-primary blacklistSave">Proceed
						</button>
						<%}%>
					</div>

				</div>
			</div>



	</div>
</div>

<%} catch (Exception e) {
	//System.out.println("error"+e.getMessage());
}%>
