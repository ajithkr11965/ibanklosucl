<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanBasic" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VLHunterDetails" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %><%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 02-07-2024
  Time: 18:25
  To change this template use File | Settings | File Templates.
--%>
<script>
	function saveHunterReview() {
    var hunterReviews = [];
    var user ="";

    // Collect data from the table
    $('#huntertable tbody tr').each(function() {
        var row = $(this);
        var applicantId = row.find('td:first').data('applicant-id');
        var remarks = row.find('textarea[name="hunter_remarks"]').val();

        hunterReviews.push({
            applicantId: applicantId,
            remarks: remarks
        });
    });

    // Prepare the data to send
    var data = {
        slno: $('#slno').val(),
        wiNum: $('#winum').val(),
        hunterReviews: hunterReviews,
        reviewUser: $('#user_ppcno').val(), // Assuming you have access to the current user's PPC number
        reviewDate: new Date().toISOString()
    };

    // Send AJAX request
    $.ajax({
	    url: 'apicpc/saveHunterReview',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(response) {
            if (response.status === 'S') {
                alertmsg('Hunter review saved successfully. Application moved to RM queue.');
                 window.location.href = "hunterlist";
            } else {
                alert('Error: ' + response.message);
            }
        },
        error: function(xhr, status, error) {
            alert('An error occurred while saving the review: ' + error);
        }
    });
}
$(document).ready(function() {
    $('#hunterSave').click(saveHunterReview);
});

</script>
<%
	Employee userdt= (Employee) request.getAttribute("userdata");
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
	}


%>
<!--begin::Timeline item-->
<div class="w-100">
	<input type="hidden" name="user_ppcno" id="user_ppcno" value="<%=userdt.getPpcno()%>">
			<div class="d-flex flex-stack border rounded px-7 py-3 mb-2">
				<div class="w-100">
					<div class="accordion-header  d-flex show" data-bs-toggle="collapse" id="hunterlistDetails" data-bs-target="#huntertable">
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
					<div id="huntertable" class="fs-6 collapse  ps-10 show " data-bs-parent="#vl_rm_int">

						<div class="row">
							<div class="col-sm-12">
								<div class="mb-3 mt-3">
									<div class="row">
										<div class="col-sm-12">
											<table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8">
												<thead>
													<tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase">
														<th style="width: 10%;">Applicant Name</th>
														<th style="width: 10%;">Run date</th>
														<th style="width: 10%;">Decision</th>
														<th style="width: 10%;">Rule IDs</th>
														<th>Hunter Remarks</th>
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


														<td style="width: 10%;" data-applicant-id="<%= applicantId %>"><%=applicant.getBasicapplicants().getApplicantName()%>
														</td>

														<td style="width: 10%;"><%= vlHunterDetails != null && vlHunterDetails.getApplicantId().equals(applicant.getApplicantId()) ? dateFormat.format(vlHunterDetails.getTimestamp()) : "" %>
														</td >

														<td ><%= vlHunterDetails != null && vlHunterDetails.getApplicantId().equals(applicant.getApplicantId()) ? vlHunterDetails.getDecision() : "" %>
														</td>

														                                        <td class="ruleIds"><%= vlHunterDetails != null && vlHunterDetails.getRules() != null ?
                                                vlHunterDetails.getRules().stream()
                                                    .map(rule -> rule.getRuleId().trim())
                                                    .collect(Collectors.joining(","))
                                                : ""
                                            %></td>
														<td><textarea class="form-control" name="hunter_remarks" id="hunter_remarks" cols="4" rows="5"></textarea>
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
							<button type="button" id="hunterSave" class="btn btn-sm btn-primary hunterlistSave">Save<i class="ph-paper-plane-tilt ms-2"></i></button>
						</div>



					</div>
				</div>


			</div>
		</div>





