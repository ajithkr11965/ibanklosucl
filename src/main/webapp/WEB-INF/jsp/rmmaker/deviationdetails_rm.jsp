<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.sib.ibanklosucl.model.*" %>
<%@ page import="java.util.Optional" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="assets/plugins/custom/formrepeater/formrepeater.bundle.js"></script>
<script>

    function initializeDeviationDetails() {
        $('#kt_docs_repeater_basic').repeater({
            initEmpty: false,
            defaultValues: {
                'text-input': 'foo'
            },
            show: function () {
                $(this).slideDown();
                applyEditabilityRules($(this));
            },
            hide: function (deleteElement) {
                $(this).slideUp(deleteElement);
            }
        });
        $(document).on('click', '.delete-amber-row', function (e) {
            e.preventDefault();
            var row = $(this).closest('.form-group.row');
            var amberId = row.data('amber-id');

            if (amberId) {
                // Existing row
                $.ajax({
                    url: 'apicpc/deactivate-amber-deviation',
                    type: 'POST',
                    data: JSON.stringify({amberId: amberId}),
                    contentType: 'application/json',
                    success: function (response) {
                        if (response.success) {
                            row.remove();
                        } else {
                            alertmsg('Error deactivating deviation: ' + response.message);
                        }
                    },
                    error: function () {
                        alertmsg('Error communicating with the server');
                    }
                });
            } else {
                // New row added by repeater
                row.remove();
            }
        });

        $('#submitBtn').click(function (e) {
            console.log("saving the devations");
            e.preventDefault();
            if (validateForm()) {
                submitAmberDeviations();
            }
            console.log("saving the devations completed");
        });

        function submitAmberDeviations() {
            console.log("----in submitAmberDeviations");
            var winum = $('#winum').val();
            var slno = $('#slno').val();
            var formData = {
                wiNum: winum,
                slno: slno,
                reqIpAddr: "", // Assuming you want to send the client's IP address
                amberData: []
            };
            $('#preloadedData .form-group.row').each(function () {
                var row = $(this);
                var amberCode = row.data('amber-code');
                var isRM = amberCode.startsWith('RM');
                var amberData = {
                    id: row.data('amber-id'),
                    amberCode: amberCode,
                    amberDesc: isRM ? row.find('textarea[name="amberDesc"]').val().trim() : row.find('.amber-desc-display').text().trim(),
                    approvingAuth: row.find('select[name="allotted"]').val(),
                    doRemarks: row.find('input[name="doComments"]').val().trim(),
                    parameterRange: isRM ? row.find('input[name="parameterRange"]').val().trim() : row.find('.parameter-range-display').text().trim(),
                    parameterValue: isRM ? row.find('input[name="parameterValue"]').val().trim() : row.find('.parameter-value-display').text().trim()
                };
                formData.amberData.push(amberData);
            });

            console.log("before repeat=====" + JSON.stringify(formData));

            // Collect data from new rows
            $('[data-repeater-item]').each(function (index) {
                var row = $(this);
                var newAmberData = {
                    amberDesc: row.find('textarea[name^="group-a"][name$="[deviationParameter]"]').val(),
                    approvingAuth: row.find('select[name^="group-a"][name$="[allotted]"]').val(),
                    doRemarks: row.find('input[name^="group-a"][name$="[doComments]"]').val(),
                    parameterRange: row.find('input[name^="group-a"][name$="[parameterRange]"]').val(),
                    parameterValue: row.find('input[name^="group-a"][name$="[parameterValue]"]').val()

                };
                // Only add if at least one field is filled
                if (Object.values(newAmberData).some(value => value && value.trim() !== '')) {
                    formData.amberData.push(newAmberData);
                }
            });

            console.log("after repeat=====" + JSON.stringify(formData));
            // Send AJAX request
            $.ajax({
                url: 'apicpc/update-amber-deviations',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(formData),
                success: function (response) {
                    updateAccordionStyle('deviationDetailslink', true);
                    alertmsg('Deviations updated successfully');
                    console.log(response);
                    // You can add more user feedback here, such as updating the UI or refreshing the data
                },
                error: function (xhr, status, error) {
                    alertmsg('Error updating deviations: ' + error);
                    // You can add more error handling here, such as displaying specific error messages
                }
            });
        }

        $('#preloadedData .form-group.row').each(function () {
            applyEditabilityRules($(this));
        });
    }

    function applyEditabilityRules(row) {
        var amberCode = row.data('amber-code');
        if (amberCode && (amberCode.startsWith('AMB') || amberCode.startsWith('CFR'))) {
            row.find('input, textarea, select').not('select[name="allotted"], input[name="doComments"]').prop('disabled', true);
            row.find('.display-field').show();
            row.find('.edit-field').hide();
        } else if (amberCode && amberCode.startsWith('RM')) {
            row.find('input, textarea, select').prop('disabled', false);
            row.find('.display-field').hide();
            row.find('.edit-field').show();
        }
    }
        function validateForm() {
        var isValid = true;

        // Validate preloaded data
        $('#preloadedData .form-group.row').each(function() {
            var row = $(this);
            var amberCode = row.data('amber-code');

            if (!amberCode || (!amberCode.startsWith('AMB') && !amberCode.startsWith('CFR'))) {
                // Validate all fields for RM codes
                row.find('input, textarea, select').not(':disabled').each(function() {
                    if ($(this).val().trim() === '') {
                        isValid = false;
                        $(this).addClass('is-invalid');
                    } else {
                        $(this).removeClass('is-invalid');
                    }
                });
            } else {
                // Validate only editable fields for AMB and CFR codes
                row.find('select[name="allotted"], input[name="doComments"]').each(function() {
                    if ($(this).val().trim() === '') {
                        isValid = false;
                        $(this).addClass('is-invalid');
                    } else {
                        $(this).removeClass('is-invalid');
                    }
                });
            }
        });

        // Validate new rows
        $('[data-repeater-item]').each(function() {
            var row = $(this);
            row.find('input, textarea, select').not(':disabled').each(function() {
                if ($(this).val().trim() === '') {
                    isValid = false;
                    $(this).addClass('is-invalid');
                } else {
                    $(this).removeClass('is-invalid');
                }
            });
        });

        if (!isValid) {
            alertmsg('Please fill in all required fields.');
        }

        return isValid;
    }


    $(document).ready(function () {
        initializeDeviationDetails();
    });
</script>
<%
	VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
	List<VehicleLoanAmber> vehicleLoanAmberList = new ArrayList<>();
	VehicleLoanBasic vehicleLoanBasic = null;
	String applicantId = "", wiNum = "", slno = "", applicantype = "", showFlg = "", accordion_style = "btn-active-light-primary";
	Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
	if (vehicleLoanMaster != null) {
		vehicleLoanApplicants = vehicleLoanMaster.getApplicants();
		vehicleLoanAmberList = (List<VehicleLoanAmber>) request.getAttribute("vehicleLoanAmberList");
		wiNum = vehicleLoanMaster.getWiNum();
		slno = vehicleLoanMaster.getSlno().toString();
	} else {
	}
    List<Map<String, Object>> checkerLevels = (List<Map<String, Object>>) request.getAttribute("checkerLevels");
%>
<div class="flex-stack border rounded px-7 py-3 mb-2">
	<div class="">
		<div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="deviationDetailslink" data-bs-target="#deviationDetailsContent">
			<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2 <%=accordion_style%> <%=showFlg%>"
			       for="deviationDetailsContent">
				<i class="ki-duotone ki-element-8 fs-3x me-4">
					<span class="path1"></span>
					<span class="path2"></span>
				</i>
				<span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">Deviation Details</span>
                    <span class="text-muted fw-semibold fs-7">
                     Enter Deviation details.
                    </span>
                </span>
			</label>
		</div>
		<div id="deviationDetailsContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int" th:fragment="deviationDetailsContent">

			<div class="row">
				<div class="col-sm-12">
					<!--begin::Repeater-->
					<div id="kt_docs_repeater_basic">
						<form id="dynamicForm" class="form">
							<!-- Labels row -->
							<div class="form-group row">
								<div class="col-sm-2">
									<label class="col-form-label">Deviation</label>
								</div>
								<div class="col-sm-1">
									<label class="col-form-label"> Range</label>
								</div>
								<div class="col-sm-2">
									<label class="col-form-label">Allotted</label>
								</div>
								<div class="col-sm-2">
									<label class="col-form-label"> Value</label>
								</div>
								<div class="col-sm-2">
									<label class="col-form-label">DO comments</label>
								</div>
								<div class="col-sm-2 approver-only">
									<label class="col-form-label">Approver comments</label>
								</div>
							</div>
							<div class="separator separator-dashed border-gray-500 my-1"></div>

							<!-- Preloaded data container -->
							<div id="preloadedData">
								<% for (VehicleLoanAmber vehicleLoanAmber : vehicleLoanAmberList) { %>
								<div class="form-group row mb-1" data-amber-id="<%=vehicleLoanAmber.getId()%>" data-amber-code="<%=vehicleLoanAmber.getAmberCode()%>">
									<div class="col-sm-2">
										<span class="devamberdesc amber-desc-display display-field "><%= vehicleLoanAmber.getAmberDesc() %></span>
										<textarea class="form-control amber-desc-edit edit-field" name="amberDesc"
										          style="display:none;"><%= vehicleLoanAmber.getAmberDesc() %></textarea>
									</div>
									<div class="col-sm-1">
            <span class="parameter-range-display display-field devdesc">
                <%= vehicleLoanAmber.getAmberSubList().isEmpty() ? "" : vehicleLoanAmber.getAmberSubList().get(0).getMasterValue() != null ? vehicleLoanAmber.getAmberSubList().get(0).getMasterValue() : "-" %>
            </span>
										<input type="text" class="form-control parameter-range-edit edit-field" name="parameterRange" style="display:none;"
										       value="<%= vehicleLoanAmber.getAmberSubList().isEmpty() ? "" : vehicleLoanAmber.getAmberSubList().get(0).getMasterValue() != null ? vehicleLoanAmber.getAmberSubList().get(0).getMasterValue() : "" %>">
									</div>

									<div class="col-sm-2">
										<select name="allotted" class="form-select" required>
											<option value="" selected>Select</option>
											<option value="NA" <%= "NA".equals(vehicleLoanAmber.getApprovingAuth()) ? "selected" : "" %>>NA</option>
											<%
																						if (checkerLevels != null) {
																							for (Map<String, Object> level : checkerLevels) {
																								String level_val = (String) level.get("LEVEL_NAME");
																								String level_desc = (String) level.get("DISPLAY_NAME");
																					%>
																					<option value="<%= level_val %>" <%= level_val.equals(vehicleLoanAmber.getApprovingAuth()) ? "selected" : "" %>><%= level_desc %>
																					</option>
																					<%
																							}
																						}
																					%>
<%--											<option value="RCL1" <%= "RCL1".equals(vehicleLoanAmber.getApprovingAuth()) ? "selected" : "" %>>L1</option>--%>
<%--											<option value="RCL2" <%= "RCL2".equals(vehicleLoanAmber.getApprovingAuth()) ? "selected" : "" %>>L2</option>--%>
<%--											<option value="RCL3" <%= "RCL3".equals(vehicleLoanAmber.getApprovingAuth()) ? "selected" : "" %>>L3</option>--%>
										</select>
									</div>
									<div class="col-sm-2">
										<% for (VehicleLoanAmberSub amberSub : vehicleLoanAmber.getAmberSubList()) { %>
										<div class="parameter-value-display display-field devdesc">
											<%= amberSub.getApplicantName() != null ? amberSub.getApplicantName() : "" %>(
											<%= amberSub.getApplicantType() != null ? amberSub.getApplicantType() : "" %>)
											-:
											<%= amberSub.getCurrentValue() != null ? amberSub.getCurrentValue() : "" %>
										</div>
										<% } %>
										            <input type="text" class="form-control parameter-value-edit edit-field" name="parameterValue" style="display:none;"
                   value="<%= !vehicleLoanAmber.getAmberSubList().isEmpty() && vehicleLoanAmber.getAmberSubList().get(0).getCurrentValue() != null ? vehicleLoanAmber.getAmberSubList().get(0).getCurrentValue() : "" %>">

									</div>
									<div class="col-sm-2">
										<input type="text" name="doComments" class="form-control"
										       value="<%= vehicleLoanAmber.getDoRemarks() != null ? vehicleLoanAmber.getDoRemarks() : "" %>" placeholder="DO Comments">
									</div>
									<div class="col-sm-2 approver-only">
										<%= vehicleLoanAmber.getApprAuthRemarks() != null ? vehicleLoanAmber.getApprAuthRemarks() : "" %>
									</div>
									<%if (vehicleLoanAmber.getAmberCode().startsWith("RM")) {%>
									<div class="col-sm-1">
										<a href="javascript:;" data-repeater-delete class="btn btn-sm btn-light-danger mb-1 delete-amber-row">
											<i class="ki-duotone ki-trash fs-1"><span class="path1"></span><span class="path2"></span><span class="path3"></span><span
													class="path4"></span><span class="path5"></span></i>
										</a>
									</div>
									<%}%>
								</div>
								<div class="separator separator-dashed border-gray-400 my-1"></div>
								<% } %>
							</div>

							<!-- Repeater container for new rows -->
							<div data-repeater-list="group-a">
								<div data-repeater-item>
									<div class="form-group row mb-1">
										<div class="col-sm-2">
											<textarea name="deviationParameter" rows="1" class="form-control" required placeholder=" Deviation Parameter "></textarea>
										</div>
										<div class="col-sm-1">
											<input type="text" name="parameterRange" class="form-control" required placeholder="Range">
										</div>
										<div class="col-sm-2">
											<select name="allotted" class="form-select" required>
												<option value="">Select</option>
											<%
																						if (checkerLevels != null) {
																							for (Map<String, Object> level : checkerLevels) {
																								String level_val = (String) level.get("LEVEL_NAME");
																								String level_desc = (String) level.get("DISPLAY_NAME");
																					%>
																					<option value="<%= level_val %>"><%= level_desc %>
																					</option>
																					<%
																							}
																						}
																					%>
											</select>
										</div>
										<div class="col-sm-2">
											<input type="text" name="parameterValue" class="form-control devdesc" placeholder="value" required>
										</div>
										<div class="col-sm-2">
											<input type="text" name="doComments" class="form-control devdesc" placeholder="DO Comments">
										</div>
										<div class="col-sm-2 approver-only">

										</div>
										<div class="col-sm-1">
											<a href="javascript:;" data-repeater-delete class="btn btn-sm btn-light-danger mb-1">
												<i class="ki-duotone ki-trash fs-2"><span class="path1"></span><span class="path2"></span><span class="path3"></span><span
														class="path4"></span><span class="path5"></span></i>
											</a>
										</div>
									</div>
								</div>
							</div>

							<div class="form-group mt-5">
								<a href="javascript:;" data-repeater-create class="btn btn-sm btn-light-success">
									<i class="ki-duotone ki-plus fs-2"></i> Add
								</a>
							</div>


							<button type="submit" id="submitBtn" class="btn btn-primary mt-5">Submit</button>
						</form>
					</div>
					<!--end::Repeater-->
				</div>
			</div>
		</div>
	</div>
</div>