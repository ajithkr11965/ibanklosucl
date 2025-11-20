<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.EligibilityDetails" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="javax.persistence.Column" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanDetails" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanSubqueueTask" %>
<%@ page import="com.sib.ibanklosucl.model.doc.VehicleLoanRoiWaiver" %>
<%@ page import="java.util.Optional" %>
<%@ page import="com.sib.ibanklosucl.dto.subqueue.LockStatusDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.WaiverAccessDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f0f4f8;
        margin: 0;
        padding: 0;
    }

    .container {
        margin-top: 20px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .header h2 {
        margin: 0;
        font-size: 24px;
    }

    .interaction-history {
        margin-top: 20px;
    }

    .history-table th, .history-table td {
        padding: 10px;
        text-align: left;
    }

    .status-resolved {
        color: green;
    }

    .status-open {
        color: red;
    }

    .status-inwork {
        color: orange;
    }

    .expandable-content {
        display: none;
        background-color: #f9f9f9;
    }

    .expandable-content td {
        padding: 15px;
    }

    .card-body {
        padding: 10px;
        background-color: #f7f7f7;
        border-radius: 8px;
        border: 1px solid #ddd;
    }

    .card-body p {
        margin: 0;
        font-size: 14px;
    }

    .details-icon {
        color: #007bff;
        margin-right: 5px;
    }

    .remarks {
        margin-top: 10px;
        font-size: 14px;
        background-color: #e9ecef;
        padding: 10px;
        border-radius: 4px;
        border-left: 4px solid #007bff;
    }

	.link-td {
		color: red !important;/* Text color */
		text-decoration: underline; /* Makes it look like a link */
		cursor: pointer; /* Changes cursor to pointer */
	}

	.link-td:hover {
		color: green !important; /* Darker color on hover */
		text-decoration: none; /* Remove underline on hover */
	}
</style>
<script>
    function isReq() {
        return true;

    }


    $(document).ready(function () {
        scrolltoId('roiWaiverDet');



        var hidtaskStatus = $('#taskStatus').val();
        console.log("-----" + hidtaskStatus);
        if (hidtaskStatus === "COMPLETED") {
            $('#roisaveBtn').hide();
        }


        // Initialize form validation
        $("#roiDetailsForm").validate({
            rules: {
                'roiRequired': {
                    required: true
                },
                roisancAmt: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },
                roiSancRoi: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },
                roiSancTenor: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },
                roiSancemi: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },
                roiebr: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },
                roioperationalCost: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },
                roicrp: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },
                roispread: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },
                sancBaseSpread: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },
                revisedRoi: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },

                revisedEmi: {
                    required: function () {
                        return isReq();
                    },
                    twoDecimalPlaces: true
                },
                roidecision: {
                    required: function () {
                        return isReq();
                    }
                },
                roiwaiverSancRemarks: {
                    required: function () {
                        return isReq();
                    },
                    maxlength: 150
                }
            },
            messages: {},
            ignore: 'input[type=hidden], .select2-search__field',
            highlight: function (element, errorClass) {
                $(element).removeClass('is-invalid').removeClass('is-valid').removeClass('validation-invalid-label');
            },
            unhighlight: function (element, errorClass) {
                $(element).removeClass('validation-invalid-label').removeClass('is-invalid').removeClass('is-valid');
            },
            errorPlacement: function (error, element) {
                element.removeClass('is-valid').addClass('is-invalid');
                error.css({
                    'padding-left': '23px',
                    'margin-right': '20px',
                    'padding-bottom': '2px',
                    'color': 'red',
                    'font-size': 'small'
                });
                error.addClass("validation-invalid-label");
                log(element);
                if (element.hasClass('checkbox-input')) {
                    element.parent().parent().parent().append(error);
                } else {
                    error.insertAfter(element);
                }
            }
        });
        $.validator.addMethod("regex", function (value, element, regexpr) {
            return regexpr.test(value);
        }, "Invalid format");
        jQuery.validator.addMethod("twoDecimalPlaces", function (value, element) {
            return this.optional(element) || /^\d+(\.\d{1,2})?$/.test(value);
        }, "Please enter a valid number with up to two decimal places.");


        $("#hisviewBtn").on('click', function (e) {
            var slno = $('#slno').val();
            var waiverType = "ROI";
            e.preventDefault();
            $.ajax({
                url: 'doc/waiver-history',
                type: 'POST',
                data: {slno: slno, waiverType: waiverType},
                success: function (response) {
                    var tableHeader = $('#roiwaiverHistoryTableHeader');
                    var tableBody = $('#roiwaiverHistoryTableBody');
                    tableHeader.empty();
                    tableBody.empty();

                    var headerRow = $('<tr>');
                    if (waiverType === 'ROI') {
                        headerRow.append('<th>Decision</th><th>Requested Spread</th><th>Sanctioned Spread</th><th>Last Modified</th><th>Modified By</th><th>Status</th>');
                    } else if (waiverType === 'CHARGE') {
                        headerRow.append('<th>Decision</th><th>Fee Code</th><th>Fee Name</th><th>Original Fee</th><th>Recommended Fee</th><th>Sanctioned Fee</th><th>Final Fee</th><th>Waiver Flag</th><th>Remarks</th><th>Sanction Remarks</th><th>Last Modified</th><th>Modified By</th>');
                    }
                    tableHeader.append(headerRow);

                    response.forEach(function (waiver, index) {
                        var row = $('<tr onclick="toggleExpand(this)">');
                        if (waiverType === 'ROI') {
                            row.append($('<td>').text(waiver.decision));
                            row.append($('<td>').text(waiver.requestedSpread + '%'));
                            row.append($('<td>').text(waiver.sanctionedSpread + '%'));
                            row.append($('<td>').text(new Date(waiver.lastModDate).toLocaleString()));
                            row.append($('<td>').text(waiver.lastModUser));
                            row.append($('<td class="link-td">').text(waiver.taskStatus));
                        } else if (waiverType === 'CHARGE') {
                            row.append($('<td>').text(waiver.decision));
                            row.append($('<td>').text(waiver.feeCode));
                            row.append($('<td>').text(waiver.feeName));
                            row.append($('<td>').text('₹' + waiver.feeValue.toFixed(2)));
                            row.append($('<td>').text('₹' + waiver.feeValueRec.toFixed(2)));
                            row.append($('<td>').text('₹' + waiver.feeSancValue.toFixed(2)));
                            row.append($('<td>').text('₹' + waiver.finalFee.toFixed(2)));
                            row.append($('<td>').text(waiver.waiverFlag));
                            row.append($('<td>').text(waiver.remarks));
                            row.append($('<td>').text(waiver.sanctionRemarks));
                            row.append($('<td>').text(new Date(waiver.lastModDate).toLocaleString()));
                            row.append($('<td>').text(waiver.lastModUser));
                        }
                        tableBody.append(row);

                        // Create an expandable row with icons and parallel layout
                        var expandableRow = $('<tr class="expandable-content">');
                        var expandableCell = $('<td colspan="12">');
                        var cardBody = $('<div class="card card-body">');

                        var detailsRow = $('<div class="row">');
                        detailsRow.append('<div class="col-md-4"><p><i class="fas fa-user-tie details-icon"></i><strong>Decision:</strong> ' + waiver.decision + '</p><p><i class="fas fa-calendar-alt details-icon"></i><strong>Modified Date:</strong> ' + new Date(waiver.lastModDate).toLocaleString() + '</p></div>');
                        detailsRow.append('<div class="col-md-4"><p><i class="fas fa-user-tie details-icon"></i><strong>Decision:</strong> ' + waiver.decision + '</p><p><i class="fas fa-calendar-alt details-icon"></i><strong>Sanctioned User:</strong> ' + waiver.completedUser + '</p></div>');
                        detailsRow.append('<div class="col-md-4"><p><i class="fas fa-percentage details-icon"></i><strong>Requested Spread:</strong> ' + waiver.requestedSpread + '%</p><p><i class="fas fa-percentage details-icon"></i><strong>Sanctioned Spread:</strong> ' + waiver.sanctionedSpread + '%</p></div>');
                        detailsRow.append('<div class="col-md-4"><p><i class="fas fa-money-bill-wave details-icon"></i><strong>Revised ROI:</strong> ' + waiver.revisedRoi + '%</p><p><i class="fas fa-money-bill-wave details-icon"></i><strong>Revised EMI:</strong> ₹' + waiver.revisedEmi.toFixed(2) + '</p></div>');

                        cardBody.append(detailsRow);
                        cardBody.append('<div class="remarks"><strong>Branch Remarks:</strong> ' + waiver.remarks + '</div>');
                        cardBody.append('<div class="remarks"><strong>Sanction Remarks:</strong> ' + waiver.sanctionRemarks + '</div>');

                        expandableCell.append(cardBody);
                        expandableRow.append(expandableCell);
                        tableBody.append(expandableRow);
                    });

                    $('#roiwaiverHistoryModalLabel').text(waiverType + ' Waiver History');
                    $('#roiwaiverHistoryModal').modal('show');
                },
                error: function (xhr, status, error) {
                    console.error('Error fetching waiver history:', error);
                    alert('Failed to fetch waiver history. Please try again.');
                }
            });

        });

        // Handle save button click
        $("#roisaveBtn").on('click', function (e) {
            e.preventDefault();
            var showMsg="";
            var decision= $("#roidecision").val();
            if ($("#roiDetailsForm").valid()) {
                showLoader();
                var dto = {
                    wiSlno: $('#slno').val(),
                    waiverType: "ROI",
                    roidto: {
                        sancAmt: $("#roisancAmt").val(),
                        sancRoi: $("#roiSancRoi").val(),
                        sanctenor: $("#roiSancTenor").val(),
                        sancemi: $("#roiSancemi").val(),
                        ebr: $("#roiebr").val(),
                        operationalCost: $("#roioperationalCost").val(),
                        crp: $("#roicrp").val(),
                        spread: $("#roispread").val(),
                        baseSpread: $("#roibrbaseSpread").val(),
                        sancBaseSpread: $("#sancBaseSpread").val(),
                        revisedRoi: $("#revisedRoi").val(),
                        revisedEmi: $("#revisedEmi").val(),
                        decision: $("#roidecision").val(),
                        stp: $("#roiSTP").val(),
                        roiType: $("#roiRoitype").val(),
                        roiwaiveRequired: $('[name="roiRequired"]:checked').val(),
                        slno: $('#slno').val(),
                        wiNum: $('#winum').val(),
	                    initialRoi:$('#initailROI').val(),
                        roiwaiverSancRemarks: $('#roiwaiverSancRemarks').val()
                    }
                };
                $.ajax({
                    url: 'doc/updateWaiverChecker', // Update with your API endpoint
                    type: 'POST',
                    data: JSON.stringify(dto),
                    contentType: 'application/json',
                    success: function (response) {
                        hideLoader();
                        if(decision.startsWith("RI")) {
                            showMsg="ROI Waiver request forwarded";
                        } else if (decision==="SANCTION") {
                            showMsg="ROI Waiver request sanctioned";
                        } else if (decision==="REJECT") {
							showMsg="ROI Waiver request rejected";
                        }
                        if (response.status === 'S') {
                                $('#roisaveBtn').hide();
                            notyalt(showMsg);
                            updateCheckerAccordionStyle("roiLink",true);
                            checkWaiverLocksAndRedirect($('#winum').val());
                        } else {
                            alertmsg('Failed: ' + response.msg);
                        }
                    },
                    error: function (xhr, status, error) {
                        hideLoader();
                        var err_data = xhr.responseJSON;
                        if (err_data.msg) {
                            alertmsgvert(err_data.msg);
                        } else {
                            alertmsg('An error occurred: ' + error);
                        }
                    }
                });
            }
        });


        $('#sancBaseSpread').on('change', function (e) {
            e.preventDefault();
            e.stopPropagation();
            var spread = $(this);
            if (spread.val().length >= 0) {
                showLoader();
                $.ajax({
                    url: 'doc/getRoi',
                    type: 'POST',
                    data: JSON.stringify({
                        ebr: $("#roiebr").val(),
                        operationalCost: $("#roioperationalCost").val(),
                        crp: $("#roicrp").val(),
                        spread: $("#roispread").val(),
                        baseSpread: $("#sancBaseSpread").val(),
                        sanctenor: $("#roiSancTenor").val(),
                        sancAmt: $("#roisancAmt").val()
                    }),
                    contentType: 'application/json',
                    success: function (response) {
                        if (response.status === 'S') {
                            $("#revisedRoi").val(response.revisedRoi)
                            $("#revisedEmi").val(response.revisedEmi)
                        } else {
                            alertmsg(response.msg);
                        }
                        hideLoader();
                    },
                    error: function (xhr, status, error) {
                        hideLoader();
                        var err_data = xhr.responseJSON;
                        if (err_data.msg) {
                            alertmsgvert(err_data.msg);
                        } else {
                            alertmsg('An error occurred: ' + error);
                        }
                    }
                });
            }
        });


        $('input[type=radio][name=roiRequired]').change(function () {
            if (this.value == 'Y') {
                $('.roiwaiveRequired').removeClass('hide');
            } else if (this.value == 'N') {
                $('.roiwaiveRequired').addClass('hide');
            }
        });
    });
function checkWaiverLocksAndRedirect(wiNum) {
            var emp_ppcno = $('#emp_ppcno').val();
            $.ajax({
                url: 'doc/checkWaiverLocks', // Endpoint to check the waiver locks
                type: 'GET',
                data: {wiNum: wiNum},
                success: function (lockStatus) {
                    // Check if the current user has locks on both ROI_WAIVER and CHARGE_WAIVER
                    if (lockStatus.roiLockedBy === emp_ppcno || lockStatus.chargeLockedBy === emp_ppcno) {
                        if (lockStatus.roiLocked || lockStatus.chargeLocked) {
                            notyalt('Record Saved Successfully');
                        } else {
                            window.location.href = 'waiverlist'; // Redirect to the waiver list
                        }
                    } else {
                        window.location.href = 'waiverlist'; // Redirect to the waiver list
                    }
                },
                error: function (xhr, status, error) {
                    alertmsg('Error checking waiver locks: ' + error);
                }
            });
        }

</script>

<div id="roiWaiverDet" class="flex-stack border rounded sancApprove hide px-7 py-3 mb-2">
	<div class="w-100">
		<div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="roiLink" data-bs-target="#roiContent">
			<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2"
			       for="roiContent">
				<i class="ki-duotone ki-ranking fs-3x me-4">
					<span class="path1"></span>
					<span class="path2"></span>
				</i>
				<span class="d-block fw-semibold text-start">
                    <span class="text-gray-900 fw-bold d-block fs-4">ROI Waiver</span>
                    <span class="text-muted fw-semibold fs-7">
                     Send WI for ROI Waiver
                    </span>
                </span>
			</label>
		</div>
		<div id="roiContent" class="fs-6 collapse  ps-10 " data-bs-parent="#vl_rm_int">
		<%
			Employee userdt = (Employee) request.getAttribute("userdata");
			VehicleLoanMaster master = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
			EligibilityDetails eligibilityDetails = (EligibilityDetails) request.getAttribute("eligibilityDetails");
			VehicleLoanDetails loanDetails = (VehicleLoanDetails) request.getAttribute("loanDetails");
			LockStatusDTO lockStatus = (LockStatusDTO) request.getAttribute("lockStatus");
			WaiverAccessDTO userAccess = (WaiverAccessDTO) request.getAttribute("userAccess");
			String taskStatus = "";
			boolean roiLocked = false;
			String roiSancRoi = "", roiSanctenor = "", roiSancemi = "", enableClass = "", roiwaiverSancRemarks = "";
			String revisedRoi = "", revisedEmi = "", roidecision = "", roiRemarks = "",initailROI="";
			BigDecimal operationalCost = BigDecimal.ZERO, crp = BigDecimal.ZERO, spread = BigDecimal.ZERO, ebr = BigDecimal.ZERO, baseSpread = BigDecimal.ZERO, roisancAmt = BigDecimal.ZERO, sancBaseSpread = BigDecimal.ZERO;
			boolean isFixed = false, isStp = false;
			switch (master.getStp()) {
				case "STP":
					isStp = true;
					break;
				case "NONSTP":
					isStp = false;
					break;
				default:
					throw new RuntimeException("STP Not Found");
			}
			roiSancRoi = eligibilityDetails.getSancCardRate().toString();
			roiSanctenor = String.valueOf(eligibilityDetails.getSancTenor());
			roiSancemi = eligibilityDetails.getSancEmi().toString();
			ebr = eligibilityDetails.getEbr();
			operationalCost = eligibilityDetails.getOpCost();
			spread = eligibilityDetails.getSpread();
			crp = eligibilityDetails.getCrp();
			roisancAmt = eligibilityDetails.getSancAmountRecommended();

			switch (loanDetails.getRoiType()) {
				case "FIXED":
					isFixed = true;
					baseSpread = spread.add(operationalCost).add(crp);
					break;
				case "FLOATING":
					baseSpread = spread;
					isFixed = false;
					break;
				default:
					throw new RuntimeException("ROI Type Not Found");
			}
			List<Map<String, Object>> roiLevels = (List<Map<String, Object>>) request.getAttribute("roiLevels");


			List<VehicleLoanSubqueueTask> subqueueTask = (List<VehicleLoanSubqueueTask>) request.getAttribute("subQueueData");
			if (subqueueTask != null) {
				Optional<VehicleLoanSubqueueTask> roi_ = subqueueTask.stream().filter(t -> t.getTaskType().equalsIgnoreCase("ROI_WAIVER")).findFirst();
				if (roi_.isPresent()) {
					VehicleLoanSubqueueTask roiTask = roi_.get();
					VehicleLoanRoiWaiver roi = roi_.get().getRoiWaiver();
					taskStatus = roiTask.getStatus();
					if (roi != null) {
						// spread=roi.getSpread();
						baseSpread = roi.getBaseSpread();
						revisedEmi = roi.getRevisedEmi().toString();
						revisedRoi = roi.getRevisedRoi().toString();
						roidecision = roiTask.getDecision();
						roiRemarks = roi.getRoiwaiverRemarks();
						sancBaseSpread = roi.getSancBaseSpread() != null ? roi.getSancBaseSpread() : roi.getBaseSpread();
                        initailROI = roi.getInitialRoi()!=null?roi.getInitialRoi().toString():roiSancRoi;
					}


					if (!"COMPLETED".equals(taskStatus)) {
						enableClass = "";
					} else {
						enableClass = "disabled";
					}


		%>
		<%
			if (lockStatus != null && lockStatus.isAnyLocked()) {
				if (userAccess.isHasRoiAccess() && lockStatus.isRoiLocked()) {
					if (!lockStatus.getRoiLockedBy().equals(userdt.getPpcno())) {
						enableClass = "disabled";
						roiLocked = true;
					}
				} else if (!userAccess.isHasRoiAccess()) {
					enableClass = "disabled";
				}
			} else if (!userAccess.isHasRoiAccess()) {
				enableClass = "disabled";
			}
//         if (userAccess.isHasChargeAccess() && lockStatus.isChargeLocked()) {
//            if(!lockStatus.getChargeLockedBy().equals(userdt.getPpcno())) {
//                 enableClass="disabled";
//            }
//         }
		%>



			<div class="row">
				<div class="col-sm-12">
					<!--begin::Repeater-->
					<div id="kt_docs_repeater_basic">

						<form id="roiDetailsForm" name="roiDetailsForm" method="POST">
							<input type="hidden" class="form-control" id="hidroidecision" name="hidroidecision" value="<%=roidecision%>" readonly/>
							<input type="hidden" class="form-control" id="taskStatus" name="taskStatus" value="<%=taskStatus%>" readonly/>
							<input type="hidden" class="form-control" id="initailROI" name="initailROI" value="<%=initailROI%>" readonly/>
							<div class="roiwaiveRequired <%=master.getRoiRequested()!=null && master.getRoiRequested()?"":"hide"%>">
								<div class="mb-3">
									<label for="roisancAmt" class="form-label">Sanctioned Amount by <%=isStp ? "BRANCH" : "DO"%>:</label>
									<input type="text" class="form-control" id="roisancAmt" name="roisancAmt" required value="<%=roisancAmt%>" readonly/>
								</div>
								<div class="mb-3">
									<label for="roisancAmt" class="form-label">Sanctioned ROI by <%=isStp ? "BRANCH" : "DO"%>:</label>
									<input type="text" class="form-control" id="roiSancRoi" name="roiSancRoi" required value="<%=roiSancRoi%>" readonly/>
								</div>
								<div class="mb-3">
									<label for="roiSancTenor" class="form-label">Sanctioned Tenor:</label>
									<input type="text" class="form-control" id="roiSancTenor" name="roiSancTenor" required value="<%=roiSanctenor%>" readonly/>
									<input type="hidden" class="form-control" id="roiRoitype" name="roiRoitype" required value="<%=loanDetails.getRoiType()%>"/>
									<input type="hidden" class="form-control" id="roiSTP" name="roiSTP" required value="<%=master.getStp()%>"/>
								</div>
								<div class="mb-3">
									<label for="roiSancemi" class="form-label">Sanctioned EMI:</label>
									<input type="text" class="form-control" id="roiSancemi" name="roiSancemi" required value="<%=roiSancemi%>" readonly/>
								</div>
								<div class="col-sm-12 mb-3">
									<table class="table table-sm table-bordered table-hover">
										<thead>
											<tr>
												<th>EBR</th>
												<%if (!isFixed) {%>
												<th>Operational Cost</th>
												<th>CRP</th>
												<th>Requested Spread</th>
												<th>Sanctioned Spread</th>
												<%} else {%>
												<th>Requested Spread</th>
												<th>Sanctioned Spread</th>
												<%}%>
											</tr>
										</thead>
										<tbody>
											<tr>

												<td><input type="text" class="form-control" id="roiebr" name="roiebr" required value="<%=ebr.toString()%>" readonly/></td>
												<%if (!isFixed) {%>
												<td><input type="text" class="form-control" id="roioperationalCost" name="roioperationalCost" required
												           value="<%=operationalCost.toString()%>" readonly/></td>
												<td><input type="text" class="form-control" id="roicrp" name="roicrp" required value="<%=crp.toString()%>" readonly/></td>
												<td><input type="text" class="form-control" id="brspread" name="brspread" required value="<%=baseSpread.toString()%>" readonly/>
												</td>
												<td><input type="text" class="form-control" <%=enableClass%> id="sancBaseSpread2" name="sancBaseSpread2" required
												           value="<%=sancBaseSpread.toString()%>"/>
													<input type="hidden" class="form-control" id="roispread" name="roispread" required value="<%=baseSpread.toString()%>"/></td>

												<%} else {%>
												<td>
													<input type="hidden" class="form-control" id="roioperationalCost" name="roioperationalCost" required
													       value="<%=operationalCost.toString()%>"/>
													<input type="hidden" class="form-control" id="roicrp" name="roicrp" required value="<%=crp.toString()%>"/>
													<input type="hidden" class="form-control" id="roispread" name="roispread" required value="<%=spread.toString()%>"/>
													<input type="hidden" class="form-control" id="roispread" name="roispread" required value="<%=spread.toString()%>"/>
													<input type="text" class="form-control" id="roibrbaseSpread" name="roibrbaseSpread" required
													       value="<%=baseSpread==null?" ":baseSpread.toString()%>"  readonly />
												</td>
												<td><input type="text" class="form-control" id="sancBaseSpread" name="sancBaseSpread" required
													        value="<%=sancBaseSpread.toString()%>" <%=enableClass%>
													       /></td>
												<%}%>
											</tr>
										</tbody>
									</table>
								</div>


								<div class="mb-3">
									<label for="revisedRoi" class="form-label">Revised ROI:</label>
									<input type="text" class="form-control" id="revisedRoi" name="revisedRoi" readonly required value="<%=revisedRoi%>"/>
								</div>
								<div class="mb-3">
									<label for="revisedEmi" class="form-label">Revised EMI:</label>
									<input type="text" class="form-control" id="revisedEmi" name="revisedEmi" readonly required value="<%=revisedEmi%>"/>
								</div>
								<div class="mb-3">
									<label for="roidecision" class="form-label">Decision:</label>
									<select type="text" id="roidecision" name="roidecision" required value="<%=roidecision%>" <%=enableClass%> class="form-select" required>
										<option value="" selected>Select Decision</option>
										<%

											for (Map<String, Object> level : roiLevels) {
												String level_val = (String) level.get("LEVEL_NAME");
												String level_desc = (String) level.get("DISPLAY_NAME");
										%>
										<option value="<%= level_val %>" <%= level_val.equals(roidecision) ? "selected" : "" %>><%= level_desc %>
										</option>
										<%
											}

										%>
										<option value="SANCTION" <%= "SANCTION".equals(roidecision) ? "selected" : "" %>>Sanctioned
										<option value="REJECT" <%= "REJECT".equals(roidecision) ? "selected" : "" %>>Rejected
									</select>
								</div>
								<div class="mb-3">
									<label for="roiwaiverbrRemarks" class="form-label">Branch Remarks:</label>
									<textarea type="text" id="roiwaiverbrRemarks" name="roiwaiverbrRemarks" disabled class="form-control" required><%=roiRemarks%></textarea>

								</div>
								<div class="mb-3">
									<label for="roiwaiverSancRemarks" class="form-label">Remarks:</label>
									<textarea type="text" id="roiwaiverSancRemarks" name="roiwaiverSancRemarks" <%=enableClass%> class="form-control"
									          required><%=roiwaiverSancRemarks%></textarea>
								</div>
							</div>

						</form>
						<div class="text-end pt-5">

							<%
								if (!"COMPLETED".equals(taskStatus) && !roiLocked && userAccess.isHasRoiAccess()) {%>
							<button type="button" id="roisaveBtn" class="btn btn-sm btn-primary" data-kt-stepper-action="next">Save
								<i class="ki-duotone ki-double-right ">
									<i class="path1"></i>
									<i class="path2"></i>
								</i></button>
							<%} else if (roiLocked) {%>
							<div class="col-lg-10">
								<span class="badge bg-warning mt-2">ROI waiver in progress . Locked by - <%=lockStatus.getRoiLockedBy()%></span>
							</div>
							<%} else if ("COMPLETED".equals(taskStatus)) {%>
							<div class="col-lg-10">
								<span class="badge bg-success mt-2">ROI waiver sanctioned</span>
							</div>
							<%} else if (!userAccess.isHasRoiAccess()) {%>
							<div class="col-lg-10">
								<span class="badge bg-warning mt-2">ROI waiver privileges are not present for the user</span>
							</div>
							<%}%>
						</div>

					</div>
					<!--end::Repeater-->
				</div>
			</div>
			<a href="#" id="hisviewBtn" class="btn btn-lg btn-flex btn-link btn-color-warning">
						ROI waiver History
						<i class="ki-duotone ki-arrow-right ms-2 fs-3"><span class="path1"></span><span class="path2"></span></i> </a>
			<%} else {%>
			<div class="col-sm-12 mb-2 justify-content-center">
				<div class="bg-light bg-opacity-50 rounded-3 p-10 mx-md-5 h-md-100">

					<div class="d-flex flex-center w-60px h-60px rounded-3 bg-light-warning bg-opacity-90 mb-10">
						<i class="ki-duotone ki-abstract-25 text-warning fs-3x"><span class="path1"></span><span class="path2"></span></i></div>

					<h4 class="mb-5">No Pending ROI waiver request found</h4>
					<a href="#" id="hisviewBtn" class="btn btn-lg btn-flex btn-link btn-color-warning">
						ROI waiver History
						<i class="ki-duotone ki-arrow-right ms-2 fs-3"><span class="path1"></span><span class="path2"></span></i> </a>
				</div>
			</div>
			<%
					}
				}
			%>
		</div>

	</div>

</div>

<div class="modal fade" id="roiwaiverHistoryModal" tabindex="-1" role="dialog" aria-labelledby="roiwaiverHistoryModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-xl" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="roiwaiverHistoryModalLabel">Waiver History</h5>
				<div class="btn btn-icon btn-sm btn-active-light-primary ms-2" data-bs-dismiss="modal" aria-label="Close">
					<i class="ki-duotone ki-cross fs-1"><span class="path1"></span><span class="path2"></span></i>
				</div>
			</div>
			<div class="modal-body">
				<table class="table table-striped table-responsive">
					<thead id="roiwaiverHistoryTableHeader">

					</thead>
					<tbody id="roiwaiverHistoryTableBody">
						<!-- Data will be populated here -->
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
