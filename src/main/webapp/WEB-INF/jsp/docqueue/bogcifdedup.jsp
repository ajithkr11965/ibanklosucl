<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="java.util.*" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanSingleDedupe" %>
<%@ page import="com.sib.ibanklosucl.model.Misrct" %>

<%!
	private String formatDate(String dateStr) {
		try {
			if (dateStr == null || dateStr.trim().isEmpty()) return "-";
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date date = inputFormat.parse(dateStr);
			return outputFormat.format(date);
		} catch (Exception e) {
			return dateStr.split(" ")[0];
		}
	}
%>

<%
	VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
	String applicantId = "", wiNum = "", slno = "", applicantype = "", showFlg = "", accordion_style = "btn-active-light-primary";
	Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
	wiNum = request.getParameter("wiNum") != null ? request.getParameter("wiNum") : "";
	slno = request.getParameter("slNo") != null ? request.getParameter("slNo") : "";

	if (vehicleLoanMaster != null) {
		vehicleLoanApplicants = vehicleLoanMaster.getApplicants().stream()
				.filter(fd -> "N".equals(fd.getDelFlg()))
				.toList();
	}
%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Dedupe Check</title>

		<!-- CSS Dependencies -->


		<style>
            .rejected-record {
    background-color: #fff5f8 !important;
}

.rejected-record td {
    color: #999999 !important;
    text-decoration: line-through;
}

.rejected-record .reject-record {
    display: none;
}

.rejected-badge {
    display: inline-block;
    padding: 0.25rem 0.5rem;
    font-size: 0.85rem;
    background-color: #f1416c;
    color: #ffffff;
    border-radius: 0.475rem;
    margin-left: 0.5rem;
}
            .dedupe-results {
                background-color: #f8f9fa;
            }

            .dedupe-results .table {
                margin-bottom: 0;
            }

            .dedupe-results td {
                padding: 0.5rem;
            }

            .relation-select {
                min-width: 150px;
            }

            .action-buttons {
                white-space: nowrap;
            }

            .spinner-overlay {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(255, 255, 255, 0.8);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 1000;
            }
		</style>
	</head>
	<body>

		<div class="timeline-content mb-10 mt-n2">
        <div class="pe-3">
            <div class="d-flex flex-stack border rounded px-7 py-3">
                <form class="form-details dedupeDetails" data-code="D-1" action="#">
                    <div class="">
                        <div class="accordion-header d-flex" data-bs-toggle="collapse" id="dedupeDetails" data-bs-target="#dedupetable">
                            <label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start p-5 d-flex align-items-center mb-2 <%=accordion_style%> <%=showFlg%>">
                                <i class="ki-duotone ki-verify fs-4x me-4">
                                    <span class="path1"></span>
                                    <span class="path2"></span>
                                </i>
                                <span class="d-block fw-semibold text-start">
                                    <span class="text-gray-900 fw-bold d-block fs-3">Dedupe Check</span>
                                    <span class="text-muted fw-semibold fs-6">Verify phone number and email matches</span>
                                </span>
                            </label>
                        </div>

                        <div id="dedupetable" class="fs-6 collapse ps-10 <%=showFlg%>" data-bs-parent="#dedupe_checker" style="transform: scale(0.70);transform-origin: top left;">
                            <div class="row">
                                <div class="col-sm-12">
                                    <div class="table-responsive">
                                        <table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
                                            <thead>
							<tr class="fw-bold fs-6 text-gray-800">
								<th>Applicant Type</th>
								<th>Name</th>
								<th>Mobile</th>
								<th>Email</th>
								<th>Action</th>
								<th>Status</th>
							</tr>
						</thead>
						<tbody>
							<% for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
								String applicantType = "";
								if ("A".equals(applicant.getApplicantType())) {
									applicantType = "Applicant";
								} else if ("C".equals(applicant.getApplicantType())) {
									applicantType = "Co-Applicant";
								} else if ("G".equals(applicant.getApplicantType())) {
									applicantType = "Guarantor";
								}

								List<VehicleLoanSingleDedupe> dedupes = applicant.getVlDedupe().stream()
										.filter(fd -> "N".equals(fd.getDelFlg()))
										.toList();
								VehicleLoanSingleDedupe dedupe = dedupes.isEmpty() ? null : dedupes.get(0);
								String status = dedupe != null ? dedupe.getCheckResult() : "";
                                boolean isRejected = dedupe != null && "Rejected".equals(dedupe.getCheckResult());
                                String rowClass = isRejected ? "rejected-record" : "";
							%>
							<!-- Main Applicant Row -->
							<tr class="align-middle">
								<td><%=applicantType%>
								</td>
								<td><%=applicant.getBasicapplicants().getApplicantName()%>
								</td>
								<td><%=applicant.getBasicapplicants().getMobileNo()%>
								</td>
								<td><%=applicant.getBasicapplicants().getEmailId()%>
								</td>
								<td>
									<button type="button"
									        class="btn btn-sm btn-light-primary runDedupeCheck"
									        data-applicant-id="<%=applicant.getApplicantId()%>"
									        data-mobile="<%=applicant.getBasicapplicants().getMobileNo()%>"
									        data-email="<%=applicant.getBasicapplicants().getEmailId()%>">
                                    <span class="indicator-label">
                                        <%= dedupe != null ? "Re-run Dedupe" : "Run Dedupe" %>
                                    </span>
										<span class="indicator-progress">
                                        Processing... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                                    </span>
									</button>
								</td>
								<td class="status-cell">
									<% if (dedupe != null) { %>
									<span class="badge badge-light-<%= "No Match".equals(status) ? "success" : "warning" %>">
                                        <%=status%>
                                    </span>
									<% } %>
								</td>
							</tr>

							<!-- Dedupe Results Row -->
							<% if (dedupe != null && dedupe.getFetchResponse() != null) { %>
							<tr class="dedupe-results-row">
								<td colspan="6">
									<div class="table-responsive mt-3">
										<table class="table table-row-bordered table-striped gs-7">
											<thead>
												<tr class="fw-bold fs-6 text-gray-800">
													<th>Phone</th>
													<th>Email ID</th>
													<th>Customer ID</th>
													<th>DOB</th>
													<th>PAN</th>
													<th>Name</th>
													<th>Voter ID</th>
													<th>Aadhar Ref No</th>
													<th>Passport</th>
													<th>Relation</th>
													<th>Action</th>
												</tr>
											</thead>
											<tbody id="dedupe-results">
												<%
													JSONObject responseObj = new JSONObject(dedupe.getFetchResponse());
													JSONObject responseBody = responseObj.getJSONObject("Response").getJSONObject("Body");

													// Check if "customer" is a JSONArray
													if (!responseBody.isNull("customer") && responseBody.get("customer") instanceof JSONArray) {
														JSONArray customers = responseBody.getJSONArray("customer");

														for (int i = 0; i < customers.length(); i++) {
															JSONObject customer = customers.getJSONObject(i);
												%>
												<tr class="<%= rowClass %>">
													<td><%= customer.optString("mobilephone", "-") %>
                                                        <% if (isRejected) { %>
                                                            <span class="rejected-badge">Rejected</span>
                                                        <% } %>
													</td>
													<td><%= customer.optString("emailid", "-") %>
													</td>
													<td><%= customer.optString("customerid", "-") %>
													</td>
													<td><%= formatDate(customer.optString("dob", "-")) %>
													</td>
													<td><%= customer.optString("pan", "-") %>
													</td>
													<td><%= customer.optString("name", "-") %>
													</td>
													<td><%= customer.optString("voterid", "-") %>
													</td>
													<td><%= customer.optString("aadhar_ref_no", "-") %>
													</td>
													<td><%= customer.optString("passportno", "-") %>
													</td>
													<td>
														<select class="form-select form-select-sm relation-select"
														        data-applicant-id="<%= applicant.getApplicantId() %>"
														        data-customer-id="<%= customer.optString("customerid", "") %>"
																<%= dedupe.getRelation() != null ? "disabled" : "" %>>
															<option value="">Select Relation</option>
															<%
																for(Misrct relation:(List<Misrct>) request.getAttribute("singleDedupeRelations"))
															{%>
															<option value="<%=relation.getCodevalue()%>"><%=relation.getCodedesc()%></option>
															<%}%>
														</select>
													</td>
													<td class="action-buttons">
														<% if (!isRejected) { %>
                                                            <button type="button"
                                                                    class="btn btn-sm btn-danger reject-record"
                                                                    data-applicant-id="<%= applicant.getApplicantId() %>"
                                                                    data-customer-id="<%= customer.optString("customerid", "") %>">
                                                                <i class="ki-duotone ki-cross-circle fs-6">
                                                                    <span class="path1"></span>
                                                                    <span class="path2"></span>
                                                                </i>
                                                                Reject
                                                            </button>
                                                        <% } %>
													</td>
												</tr>
												<%
													}
												} else {
												%>
												<tr>
													<td colspan="10" class="text-center text-muted">No duplicates found.</td>
												</tr>
												<%
													}
												%>

											</tbody>
										</table>
									</div>
								</td>
							</tr>
							<% } %>
							<% } %>
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

		<!-- Hidden Fields -->
		<input type="hidden" id="wiNum" value="<%=wiNum%>">
		<input type="hidden" id="slNo" value="<%=slno%>">

		<!-- Reject Modal -->
		<div class="modal fade" id="rejectModal" tabindex="-1" aria-hidden="true">
			<div class="modal-dialog modal-dialog-centered">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title">Reject Record</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
					</div>
					<div class="modal-body">
						<div class="mb-3">
							<label class="form-label required">Rejection Remarks</label>
							<textarea class="form-control" id="rejectRemarks" rows="3" required></textarea>
							<div class="invalid-feedback">Please enter rejection remarks</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
						<button type="button" class="btn btn-danger" id="confirmReject">
							<span class="indicator-label">Reject</span>
							<span class="indicator-progress">
                        Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                    </span>
						</button>
					</div>
				</div>
			</div>
		</div>

		<!-- JavaScript Dependencies -->


		<!-- Custom JavaScript -->
		<script>
            $(document).ready(function () {
                // Initialize variables
                let selectedApplicantId = null;
                let selectedCustomerId = null;
                $('.runDedupeCheck').on('click', function () {
                    const btn = $(this);
                    const applicantId = btn.data('applicant-id');
                    const mobile = btn.data('mobile');
                    const email = btn.data('email');

                    // Show loading state
                    btn.attr('data-kt-indicator', 'on').prop('disabled', true);

                    // Call dedupe check API
                    $.ajax({
                        url: 'api/dedupe/check',
                        method: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({
                            origin: applicantId,
                            workItemNumber: $('#winum').val(),
                            slno: $('#slno').val(),
                            mock: false,
                            apiName: 'singleDedupe',
                            encFlag: false,
                            request: {
                                mobileNumber: mobile,
                                email: email,
                                merchantCode: 'INFM',
                                merchantName: 'Information_Bank',
                            },
                        }),
                        success: function (response) {
    const {Body} = response.Response;
    const customers = Body?.customer || [];
    const applicantId = btn.data('applicant-id');
    const statusCell = btn.closest('tr').find('.status-cell');
    const currentRow = btn.closest('tr');

    // Remove existing results row if it exists
    if (currentRow.next('.dedupe-results-row').length) {
        currentRow.next('.dedupe-results-row').remove();
    }

    if (customers.length === 0) {
        // Handle No Match case
        statusCell.html(
            '<span class="badge badge-light-success">No Match</span>' +
            '<div class="mt-2 text-muted small">No duplicates found</div>'
        );
    } else {
        // Handle Match Found case
        statusCell.html(
            '<span class="badge badge-light-warning">Match Found</span>' +
            '<div class="mt-2 text-muted small">' + customers.length + ' match(es) found</div>'
        );

        // Create results row with table
        const resultsRow = $(
            '<tr class="dedupe-results-row">' +
                '<td colspan="6">' +
                    '<div class="table-responsive mt-3">' +
                        '<table class="table table-row-bordered table-striped gs-7">' +
                            '<thead>' +
                                '<tr class="fw-bold fs-6 text-gray-800">' +
                                    '<th>Phone</th>' +
                                    '<th>Email ID</th>' +
                                    '<th>Customer ID</th>' +
                                    '<th>DOB</th>' +
                                    '<th>PAN</th>' +
                                    '<th>Name</th>' +
                                    '<th>Voter ID</th>' +
                                    '<th>Aadhar Ref No</th>' +
                                    '<th>Passport</th>' +
                                    '<th>Relation</th>' +
                                    '<th>Action</th>' +
                                '</tr>' +
                            '</thead>' +
                            '<tbody>' +
                            '</tbody>' +
                        '</table>' +
                    '</div>' +
                '</td>' +
            '</tr>'
        );

        // Add customer rows
        const tbody = resultsRow.find('tbody');
        customers.forEach(customer => {
            const row = $(
                '<tr>' +
                    '<td>' + (customer.mobilephone || '-') + '</td>' +
                    '<td>' + (customer.emailid || '-') + '</td>' +
                    '<td>' + (customer.customerid || '-') + '</td>' +
                    '<td>' + (formatDate(customer.dob) || '-') + '</td>' +
                    '<td>' + (customer.pan || '-') + '</td>' +
                    '<td>' + (customer.name || '-') + '</td>' +
                    '<td>' + (customer.voterid || '-') + '</td>' +
                    '<td>' + (customer.aadhar_ref_no || '-') + '</td>' +
                    '<td>' + (customer.passportno || '-') + '</td>' +
                    '<td>' +
                        '<select class="form-select form-select-sm relation-select" ' +
                                'data-applicant-id="' + applicantId + '" ' +
                                'data-customer-id="' + customer.customerid + '">' +
                            '<option value="">Select Relation</option>' +
                            '<option value="WIFE">WIFE</option>' +
                            '<option value="HUSBAND">HUSBAND</option>' +
                            '<option value="FATHER">FATHER</option>' +
                            '<option value="MOTHER">MOTHER</option>' +
                            '<option value="SON">SON</option>' +
                            '<option value="DAUGHTER">DAUGHTER</option>' +
                            '<option value="DIRECTOR">DIRECTOR</option>' +
                            '<option value="NATURAL GUARDIAN">NATURAL GUARDIAN</option>' +
                            '<option value="LEGAL GUARDIAN">LEGAL GUARDIAN</option>' +
                            '<option value="PARTNER">PARTNER</option>' +
                            '<option value="PROPRIETOR">PROPRIETOR</option>' +
                            '<option value="TRUSTEE">TRUSTEE</option>' +
                            '<option value="INDIVIDUAL MEMBER OF SHG">INDIVIDUAL MEMBER OF SHG</option>' +
                            '<option value="AUTHORIZED SIGNATORY">AUTHORIZED SIGNATORY</option>' +
                            '<option value="GOVERNING BODY MEMBER">GOVERNING BODY MEMBER</option>' +
                            '<option value="BENEFICIAL OWNER">BENEFICIAL OWNER</option>' +
                        '</select>' +
                    '</td>' +
                    '<td class="action-buttons">' +
                        '<button type="button" ' +
                                'class="btn btn-sm btn-danger reject-record" ' +
                                'data-applicant-id="' + applicantId + '" ' +
                                'data-customer-id="' + customer.customerid + '">' +
                            '<i class="ki-duotone ki-cross-circle fs-6">' +
                                '<span class="path1"></span>' +
                                '<span class="path2"></span>' +
                            '</i>' +
                            'Reject' +
                        '</button>' +
                    '</td>' +
                '</tr>'
            );
            tbody.append(row);
        });

        // Insert results row after current row
        currentRow.after(resultsRow);

        // Reinitialize event handlers
        initializeEventHandlers();
    }
},

                        error: function (xhr, status, error) {
                            $('#dedupe-results').html('<p>Error fetching dedupe details. Please try again later.</p>');
                        },
                        complete: function () {
                            btn.attr('data-kt-indicator', 'off').prop('disabled', false);
                        },
                    });
                });

                // Run Dedupe Check
                $('.runDedupeCheckoldy').on('click', function () {
                    const btn = $(this);
                    const applicantId = btn.data('applicant-id');
                    const mobile = btn.data('mobile');
                    const email = btn.data('email');

                    // Show loading state
                    btn.attr('data-kt-indicator', 'on')
                        .prop('disabled', true);

                    // Call dedupe check API
                    $.ajax({
                        url: 'api/dedupe/check',
                        method: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({
                            origin: applicantId,
                            workItemNumber: $('#winum').val(),
                            slno: $('#slno').val(),
                            mock: false,
                            apiName: 'singleDedupe',
                            encFlag: false,
                            request: {
                                mobileNumber: mobile,
                                email: email,
                                merchantCode: 'INFM',
                                merchantName: 'Information_Bank'
                            }
                        }),
                        success: function (response) {
                            // Handle successful response
                            if (response.Response?.Status?.Code === 201) {
                                showSuccess('Dedupe check completed successfully');
                                setTimeout(() => location.reload(), 1500);
                            } else {
                                handleError(response);
                            }
                        },
                        error: function (xhr, status, error) {
                            handleApiError(xhr);
                        },
                        complete: function () {
                            btn.attr('data-kt-indicator', 'off')
                                .prop('disabled', false);
                        }
                    });
                });

                // Handle relation selection
                $('.relation-select').on('change', function () {
                    const select = $(this);
                    const applicantId = select.data('applicant-id');
                    const customerId = select.data('customer-id');
                    const relation = select.val();

                    if (!relation) return;

                    // Show loading state
                    select.prop('disabled', true);
                    const loadingSpinner = $('<div class="spinner-border spinner-border-sm text-primary ms-2"></div>');
                    select.after(loadingSpinner);

                    // Update relation via API
                    $.ajax({
                        url: 'api/dedupe/updateRelation',
                        method: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({
                            applicantId: applicantId,
                            customerId: customerId,
                            relation: relation
                        }),
                        success: function (response) {
                            showSuccess('Relation updated successfully');
                            updateSubmitButtonState();
                        },
                        error: function (xhr) {
                            select.prop('disabled', false);
                            handleApiError(xhr);
                        },
                        complete: function () {
                            loadingSpinner.remove();
                        }
                    });
                });

                // Handle reject button click
                $('.reject-record').on('click', function () {
                    selectedApplicantId = $(this).data('applicant-id');
                    selectedCustomerId = $(this).data('customer-id');
                    $('#rejectRemarks').val('').removeClass('is-invalid');
                    $('#rejectModal').modal('show');
                });

                $('#confirmRejectoldy').on('click', function () {
                    const btn = $(this);
                    const remarks = $('#rejectRemarks').val().trim();

                    if (!remarks) {
                        $('#rejectRemarks').addClass('is-invalid');
                        return;
                    }

                    // Show loading state
                    btn.attr('data-kt-indicator', 'on')
                        .prop('disabled', true);

                    // Call reject API
                    $.ajax({
                        url: 'api/dedupe/reject',
                        method: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({
                            applicantId: selectedApplicantId,
                            customerId: selectedCustomerId,
                            remarks: remarks
                        }),
                        success: function (response) {
                            $('#rejectModal').modal('hide');
                            showSuccess('Record rejected successfully');

                            // Instead of page reload, update the UI directly
                            const row = $(`[data-customer-id="${selectedCustomerId}"]`).closest('tr');
                            row.fadeOut(400, function () {
                                $(this).remove();
                                // If no more records, update the parent status
                                const resultsTable = $('.dedupe-results');
                                if (resultsTable.find('tbody tr').length === 0) {
                                    resultsTable.closest('tr').remove();
                                    // Update status to "No Match"
                                    const statusCell = $('.status-cell');
                                    statusCell.html(`
                        <span class="badge badge-light-success">No Match</span>
                        <div class="mt-2 text-muted small">No additional matching records found</div>
                    `);
                                }
                            });
                        },
                        error: function (xhr) {
                            handleApiError(xhr);
                        },
                        complete: function () {
                            btn.attr('data-kt-indicator', 'off')
                                .prop('disabled', false);
                        }
                    });
                });


                // Handle confirm reject in modal
                $('#confirmReject').on('click', function () {
    const btn = $(this);
    const remarks = $('#rejectRemarks').val().trim();

    if (!remarks) {
        $('#rejectRemarks').addClass('is-invalid');
        return;
    }

    btn.attr('data-kt-indicator', 'on')
        .prop('disabled', true);

    $.ajax({
        url: 'api/dedupe/reject',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            applicantId: selectedApplicantId,
            customerId: selectedCustomerId,
            remarks: remarks
        }),
        success: function (response) {
            $('#rejectModal').modal('hide');
            showSuccess('Record rejected successfully');

            // Update the UI to show rejected state
            const row = $('[data-customer-id="' + selectedCustomerId + '"]').closest('tr');
            row.addClass('rejected-record');

            // Add rejected badge to first column
            const firstCell = row.find('td:first');
            firstCell.append('<span class="rejected-badge">Rejected</span>');

            // Remove reject button
            row.find('.reject-record').remove();

            // Update the results count if needed
            updateResultsCount(row.closest('.dedupe-results-row'));
        },
        error: function (xhr) {
            handleApiError(xhr);
        },
        complete: function () {
            btn.attr('data-kt-indicator', 'off')
                .prop('disabled', false);
        }
    });
});

function updateResultsCount(resultsRow) {
    const activeRows = resultsRow.find('tr:not(.rejected-record)').length;
    if (activeRows === 0) {
        // If no active records remain, update status to "No Match"
        const statusCell = resultsRow.prev('tr').find('.status-cell');
        statusCell.html(
            '<span class="badge badge-light-success">No Match</span>' +
            '<div class="mt-2 text-muted small">No active matching records</div>'
        );
    } else {
        // Update match count
        const statusCell = resultsRow.prev('tr').find('.status-cell');
        statusCell.find('.text-muted').text(activeRows + ' active match(es) found');
    }
}

                // Submit button handler
                $('.dedupeSave').on('click', function () {
                    if (!validateRelations()) {
                        showError('Please select relation for all records before submitting');
                        return;
                    }

                    // Confirm submission
                    Swal.fire({
                        title: 'Confirm Submission',
                        text: 'Are you sure you want to proceed with the dedupe check results?',
                        icon: 'question',
                        showCancelButton: true,
                        confirmButtonText: 'Yes, submit',
                        cancelButtonText: 'Cancel',
                        buttonsStyling: false,
                        customClass: {
                            confirmButton: 'btn btn-primary',
                            cancelButton: 'btn btn-light'
                        }
                    }).then((result) => {
                        if (result.isConfirmed) {
                            submitDedupeResults();
                        }
                    });
                });

                // Function to submit final results
                function submitDedupeResults() {
                    const btn = $('.dedupeSave');

                    // Show loading state
                    btn.attr('data-kt-indicator', 'on')
                        .prop('disabled', true);

                    // Collect all relations
                    const results = [];
                    $('.relation-select:disabled').each(function () {
                        const select = $(this);
                        results.push({
                            applicantId: select.data('applicant-id'),
                            customerId: select.data('customer-id'),
                            relation: select.val()
                        });
                    });

                    // Submit to API
                    $.ajax({
                        url: 'api/dedupe/submit',
                        method: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({results: results}),
                        success: function (response) {
                            showSuccess('Dedupe results submitted successfully');
                            setTimeout(() => location.reload(), 1500);
                        },
                        error: function (xhr) {
                            handleApiError(xhr);
                        },
                        complete: function () {
                            btn.attr('data-kt-indicator', 'off')
                                .prop('disabled', false);
                        }
                    });
                }

                // Validation function for relations
                function validateRelations() {
                    let valid = true;
                    $('.relation-select:visible:not(:disabled)').each(function () {
                        if (!$(this).val()) {
                            valid = false;
                            return false;
                        }
                    });
                    return valid;
                }

                // Function to update submit button state
                function updateSubmitButtonState() {
                    const submitBtn = $('.dedupeSave');
                    const pendingRelations = $('.relation-select:visible:not(:disabled)').length;
                    submitBtn.prop('disabled', pendingRelations > 0);
                }

                // Function to handle API errors
                function handleApiError(xhr) {
                    let errorMessage = 'An error occurred. Please try again.';

                    if (xhr.status === 403) {
                        errorMessage = 'Session expired. Please login again.';
                        setTimeout(() => {
                            window.location.href = 'login.jsp';
                        }, 2000);
                    } else if (xhr.status === 500) {
                        errorMessage = 'Internal server error. Please try again later.';
                    } else if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMessage = xhr.responseJSON.message;
                    }

                    showError(errorMessage);
                }

                // Function to show success message
                function showSuccess(message) {
                    Swal.fire({
                        text: message,
                        icon: 'success',
                        buttonsStyling: false,
                        confirmButtonText: 'Ok, got it!',
                        customClass: {
                            confirmButton: 'btn btn-primary'
                        }
                    });
                }

                // Function to show error message
                function showError(message) {
                    Swal.fire({
                        text: message,
                        icon: 'error',
                        buttonsStyling: false,
                        confirmButtonText: 'Ok, got it!',
                        customClass: {
                            confirmButton: 'btn btn-danger'
                        }
                    });
                }

                // Handle modal events
                $('#rejectModal').on('hidden.bs.modal', function () {
                    selectedApplicantId = null;
                    selectedCustomerId = null;
                    $('#rejectRemarks').val('').removeClass('is-invalid');
                    $('#confirmReject').attr('data-kt-indicator', 'off')
                        .prop('disabled', false);
                });

                // Initialize tooltips and button state
                $('[data-bs-toggle="tooltip"]').tooltip();
                updateSubmitButtonState();
                function formatDate(dateStr) {
    if (!dateStr) return '-';
    try {
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return dateStr;
        return date.toLocaleDateString('en-IN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    } catch (e) {
        return dateStr;
    }
}

function initializeEventHandlers() {
    // Reinitialize relation select handlers
    $('.relation-select').off('change').on('change', function () {
        const select = $(this);
        const applicantId = select.data('applicant-id');
        const customerId = select.data('customer-id');
        const relation = select.val();

        if (!relation) return;

        select.prop('disabled', true);
        const loadingSpinner = $('<div class="spinner-border spinner-border-sm text-primary ms-2"></div>');
        select.after(loadingSpinner);

        $.ajax({
            url: 'api/dedupe/updateRelation',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                applicantId: applicantId,
                customerId: customerId,
                relation: relation
            }),
            success: function (response) {
                showSuccess('Relation updated successfully');
                updateSubmitButtonState();
            },
            error: function (xhr) {
                select.prop('disabled', false);
                handleApiError(xhr);
            },
            complete: function () {
                loadingSpinner.remove();
            }
        });
    });

    // Reinitialize reject button handlers
    $('.reject-record').off('click').on('click', function () {
        selectedApplicantId = $(this).data('applicant-id');
        selectedCustomerId = $(this).data('customer-id');
        $('#rejectRemarks').val('').removeClass('is-invalid');
        $('#rejectModal').modal('show');
    });
}

            });
		</script>
	</body>
</html>