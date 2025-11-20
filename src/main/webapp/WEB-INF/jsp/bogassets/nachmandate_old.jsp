<%@ page import="com.sib.ibanklosucl.model.VehicleLoanApplicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanBasic" %>
<%@ page import="com.sib.ibanklosucl.model.VehicleLoanMaster" %>
<%@ page import="com.sib.ibanklosucl.model.integrations.VLHunterDetails" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sib.ibanklosucl.dto.acopn.RepayAcctDTO" %>
<%@ page import="com.sib.ibanklosucl.model.NACHMandate" %><%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 02-07-2024
  Time: 18:25
  To change this template use File | Settings | File Templates.
--%>
<script>
    $(document).ready(function () {
        //fetchInstalmentDates();
        setupEventListeners();
        //checkNACHMandateStatus();
	    $('#kt_button_resend_nach').click(function () {
            var slno = $('#slno').val();
            $.ajax({
                url: 'api/resendNachDetails',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({slno: slno}),
                dataType: 'json',
                success: function (response) {
                    if (response.status === 'success') {
                        alertmsg('NACH details resent successfully');
                    } else {
                        alertmsg('Error resending NACH details: ' + response.message);
                    }
                },
                error: function (xhr, status, error) {
                    console.error('Error resending NACH details:', error);
                    var errorMessage = '';
                    try {
                        var response = JSON.parse(xhr.responseText);
                        errorMessage = response.message || error;
                    } catch (e) {
                        errorMessage = error;
                    }
                    alertmsg('Error resending NACH details: ' + errorMessage);
                }
            });
        });


        function setupEventListeners() {
            $('#nachtableDetails').click(fetchInstalmentDates);
            $('#kt_button_nach').click(sendNachRequest);
            $('#kt_button_check_status').click(checkMandateStatus);
            $('#kt_button_cancel_mandate').click(cancelMandate);
        }

        function sendNachRequest() {
            var slno = $('#slno').val();
            $.ajax({
                url: 'api/sendNachRequest',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({slno: slno}),
                dataType: 'json',
                success: function (response) {
                    console.log('NACH Mandate request sent successfully:', response);
                    if (response.message === "Active mandate request is present") {
                        alertmsg('Active mandate request is already present');
                        disableNACHRequestButton();
                        enableCheckStatusButton();
                        // enableCancelButton();
	                    checkMandateStatus();
                    } else if (response.Response && response.Response.Status.Code === "200" || response.Response && response.Response.Status.Code === "Initiated") {
                        alertmsg('NACH Mandate request sent successfully');
                        disableNACHRequestButton();
                        enableCheckStatusButton();
                        enableCancelButton();
                    } else {
                        alertmsg('Error sending NACH Mandate request: ' + (response.Response?.Status?.Desc || 'Unknown error'));
                    }

                },
                error: function (xhr, status, error) {
                    console.error('Error sending NACH Mandate request:', error);
                    alertmsg('Error sending NACH Mandate request: ' + error);
                }
            });
        }


        function fetchInstalmentDates() {
            //var hidac_open_flg = $('#hidac_open_flg').val();
           // if (hidac_open_flg !== "") {
                var slno = $('#slno').val();
                return new Promise((resolve, reject) => {
                    $.ajax({
                        url: 'api/instalment-dates',
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify({slno: slno}),
                        dataType: 'json',
                        success: function (response) {
                            $('#inststartdate').html(response.instalmentStartDate);
                            $('#instenddate').html(response.instalmentEndDate);
                           // $('#nachemi').html(response.emi);
                            let doubleEmi = 0;
                            if (response.emi) {
                                // Convert to number and multiply by 2
                                const emiValue = parseFloat(response.emi);
                                if (!isNaN(emiValue)) {
                                    doubleEmi = (emiValue * 2).toFixed(2);
                                }
                            }
                            $('#nachemi').html(doubleEmi);
	                        $('#nachtenor').html(response.tenor);
                            $('#hidac_open_flg').val(response.ac_open_flg);
                            if (response.ac_open_flg === "") {
                                alertmsg("Account Opening is not complete !");
                            }
                            // disableNACHRequestButton();
                            enableCheckStatusButton();
                            resolve(response);
                        },
                        error: function (xhr, status, error) {
                            if (xhr.responseJSON && xhr.responseJSON.error) {
                                reject(new Error(xhr.responseJSON.error));
                            } else {
                                reject(new Error('An unknown error occurredssss'));
                            }
                        }
                    });
                });
            // } else {
            //     alertmsg("Account Opening is not complete !");
            //     $('#kt_button_nach').prop('disabled', true);
            // }
        }

        function checkNACHMandateStatus() {
            var slno = $('#slno').val();
            $.ajax({
                url: 'api/nachMandateExists',
                type: 'GET',
                data: {slno: slno},
                success: function (response) {
                    if (response.exists) {
                        disableNACHRequestButton();
                        enableCheckStatusButton();
                        enableCancelButton();
                    }
                },
                error: function (xhr, status, error) {
                    console.error('Error checking NACH Mandate status:', error);
                }
            });
        }

        function checkMandateStatus() {
            var slno = $('#slno').val();
            $.ajax({
                url: 'api/checkMandateStatus',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({slno: slno}),
                dataType: 'json',
                success: function (response) {
                    if (response.Response && response.Response.Body && response.Response.Body.statusCheck) {
                        var statusCheck = response.Response.Body.statusCheck;
                        var message = statusCheck.message;
                        var nachStatus =message.requestStatus;
                        var statusCell = $('#mandateStatusRow td');
                        var statusColorClass=getStatusColorClass(nachStatus);
                        var statusTextColorClass=getStatusTextColorClass(nachStatus);
                        var statusHtml = `
                    <div class="d-flex align-items-center `+statusColorClass+` rounded p-5 mb-7">
                        <i class="ki-duotone ki-abstract-26 `+statusTextColorClass+` fs-1 me-5">
                            <span class="path1"></span>
                            <span class="path2"></span>
                        </i>
                        <div class="flex-grow-1 me-2">
                            <a href="#" class="fw-bold text-gray-800 text-hover-primary fs-6">`+nachStatus+`</a>
                        </div>
                    </div>
                `;
                        statusCell.html(statusHtml);
                        if(nachStatus=="Authorization Request Rejected") {
                            enableNACHRequestButton();
                        } else if (nachStatus=="Progressed by User") {
                            $('#kt_button_cancel_mandate').prop('disabled', false);
                            $('#kt_button_cancel_mandate').show();
                        } else if (nachStatus!=="Progressed by User") {
                            disableCancelButton();
                        }
                        var statusInfo = '<strong>Mandate Status:</strong> ' + message.requestStatus + '<br>' +
                            '<strong>Mandate ID:</strong> ' + message.mndtId + '<br>' +
                            '<strong>Reference Number:</strong> ' + message.referenceNumber + '<br>' +
                            '<strong>Collection Amount:</strong> ' + message.colltnAmt + '<br>' +
                            '<strong>First Collection Date:</strong> ' + message.frstColltnDt + '<br>' +
                            '<strong>Final Collection Date:</strong> ' + message.fnlColltnDt + '<br>' +
                            '<strong>Reason:</strong> ' + message.reasonDesc;
                        alertmsg(statusInfo, 'Mandate Status');
                    } else {
                        alertmsg('Unable to parse mandate status response', 'Error');
                    }
                },
                error: function (xhr, status, error) {
                    console.error('Error checking mandate status:', error);
                    alertmsg('Error checking mandate status: ' + error);
                }
            });
        }



        function cancelMandate() {
            var slno = $('#slno').val();
            $.ajax({
                url: 'api/cancelMandate',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({slno: slno}),
                dataType: 'json',
                success: function (response) {
                    if (response.Response && response.Response.Body && response.Response.Body.forceIntiate) {
                        var forceInitiate = response.Response.Body.forceIntiate;
                        var statusInfo = `
                    <strong>Status Code:</strong> ${forceInitiate.status}<br>
                    <strong>Message:</strong>     ${forceInitiate.message}<br>
                    <strong>More Info:</strong> ${forceInitiate.moreInfo}<br>
                    <strong>Reference:</strong> ${forceInitiate.reference}
                `;
                        alertmsg(statusInfo, 'Mandate Cancellation Result');

                        if (forceInitiate.status === "200") {
                            checkMandateStatus();
                            disableCheckStatusButton();
                            enableNACHRequestButton();
                            disableCancelButton();
                        }
                    } else {
                        alertmsg('Unable to parse mandate cancellation response', 'Error');
                    }
                },
                error: function (xhr, status, error) {
                    console.error('Error cancelling mandate:', error);
                    alertmsg('Error cancelling mandate: ' + error);
                }
            });
        }


        function disableNACHRequestButton() {
            $('#kt_button_nach').prop('disabled', true);
        }
        function enableNACHRequestButton() {
            $('#kt_button_nach').prop('disabled', false);
        }

        function enableCheckStatusButton() {
            $('#kt_button_check_status').prop('disabled', false);
        }

        function enableCancelButton() {
            $('#kt_button_cancel_mandate').prop('disabled', false);
        }
        function disableCancelButton() {
            $('#kt_button_cancel_mandate').prop('disabled', false);
        }

        function getStatusColorClass(status) {
            switch (status) {
                case 'Authorized':
                    return 'bg-light-success';
                case 'Authorization Request Rejected':
                    return 'bg-light-danger';
                case 'Initiated':
                    return 'bg-light-warning';
                default:
                    return 'bg-light-warning';
            }
        }

        function getStatusTextColorClass(status) {
            switch (status) {
                case 'Authorized':
                    return 'text-success';
                case 'Authorization Request Rejected':
                    return 'text-danger';
                case 'Initiated':
                    return 'text-warning';
                default:
                    return 'text-warning';
            }
        }

    });
</script>

<%
	VehicleLoanMaster vehicleLoanMaster = (VehicleLoanMaster) request.getAttribute("vehicleLoanMaster");
	String openedAccountNo = "", ac_open_flg = "";
	Boolean checker = request.getAttribute("checker") != null ? request.getAttribute("checker").toString().equals("Y") : false;
	RepayAcctDTO vehicleLoanRepayment = (RepayAcctDTO) request.getAttribute("repaymentDetails");
    NACHMandate nachMandate = (NACHMandate) request.getAttribute("nachMandate");
	List<VehicleLoanApplicant> vehicleLoanApplicants = new ArrayList<>();
	String bank_details = "", emi_start_date = "", emi_end_date = "", repaymentAccno = "";
    String mandateStatus="";
	boolean ownbank = false,nachAllowFlag=false,nachCompletedStatus=false ,nachCancelAllow=false;


	String collect_amount = "", borrowerName = "", phone = "", mobile = "", email = "", pan = "", showFlg = "", accordion_style = "btn-active-light-primary";
	;
	if (vehicleLoanMaster != null) {
		openedAccountNo = vehicleLoanMaster.getAccNumber();
		ac_open_flg = vehicleLoanMaster.getAccOpened() != null ? vehicleLoanMaster.getAccOpened() : "";
		vehicleLoanApplicants = vehicleLoanMaster.getApplicants().stream()
				.filter(fd -> "N".equals(fd.getDelFlg()))
				.toList();
		for (VehicleLoanApplicant applicant : vehicleLoanApplicants) {
			if ("A".equals(applicant.getApplicantType())) {
				pan = applicant.getKycapplicants().getPanNo();
				email = applicant.getBasicapplicants().getEmailId();
				mobile = applicant.getBasicapplicants().getMobileNo();
			}
		}
		String loanAcctNo = vehicleLoanMaster.getAccNumber();
        if(nachMandate!=null) {
            mandateStatus=nachMandate.getStatus();
            if("Authorization Request Rejected".equals(nachMandate.getStatus())) {
	            nachAllowFlag = true;
	            nachCompletedStatus = false;
            } else if("Rejected By NPCI".equals(nachMandate.getStatus())) {
	            nachAllowFlag = true;
	            nachCompletedStatus = false;
             } else if("Progressed by User".equals(nachMandate.getStatus())) {
                nachCancelAllow=true;
            } else if ("Authorized".equals(nachMandate.getStatus())) {
                nachCompletedStatus=true;
                nachAllowFlag=false;
            }

        } else {
             nachAllowFlag=true;
             nachCompletedStatus=false;
              nachCancelAllow=false;
        }
	} else {
	}
	if (vehicleLoanRepayment != null) {
		bank_details = vehicleLoanRepayment.getBankName();
		repaymentAccno = vehicleLoanRepayment.getAccountNumber();
		borrowerName = vehicleLoanRepayment.getBorrowerName();
	}
	if ("THE SOUTH INDIAN BANK LIMITED".equalsIgnoreCase(bank_details)) {
		ownbank = true;
	}


%>

<div class=" border rounded px-7 py-3 mb-2">
	<input type="hidden" id="hidac_open_flg" name="hidac_open_flg" value="">
	<div class="">
		<div class="accordion-header d-flex collapsed " data-bs-toggle="collapse" id="nachtableDetails" data-bs-target="#nachtable">
			<label class="btn btn-outline btn-outline-dashed btn-active-light-primary acdlen justify-content-start  p-5 d-flex align-items-center mb-2 <%=accordion_style%> hide"
			       for="nachtable">
				<i class="ki-duotone ki-cheque fs-4x me-4">
					<span class="path1"></span>
					<span class="path2"></span>
					<span class="path3"></span>
					<span class="path4"></span>
					<span class="path5"></span>
					<span class="path6"></span>
					<span class="path7"></span>
				</i>
				<span class="d-block fw-semibold text-start">
                        <span class="text-gray-900 fw-bold d-block fs-3">NACH</span>
                        <span class="text-muted fw-semibold fs-6">
                          Send mandate request to the customer (Mandatory for Other Bank Repyment accounts)
                        </span>
                    </span>
			</label>
		</div>
		<div id="nachtable" class="fs-6 collapse ps-2" data-bs-parent="#vl_rc_int">
			<%if (!ownbank) {%>

			<div class="row">
				<div class="col-lg-6">
					<div class="m-3">
						<div class="row">
							<div class="col-sm-12">
								<table class="table table-sm align-middle table-row-dashed table-row-gray-400 fs-8 gy-3">
									<thead>

										<tr>
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Borrower Name</th>
											<td><%=borrowerName%>
											</td>
										</tr>
										<tr>
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Bank Name</th>
											<td><%=bank_details%>
											</td>
										</tr>
										<tr>
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Repayment Account</th>
											<td><%=repaymentAccno%>
											</td>
										</tr>
										<tr>
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Tenure</th>
											<td id="nachtenor"></td>
										</tr>
										<tr>
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Collection Amount</th>
											<td id="nachemi"></td>
										</tr>
										<tr>
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">EMI start date</th>
											<td id="inststartdate"></td>
										</tr>
										<tr>
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">EMI end Date</th>
											<td id="instenddate"></td>
										</tr>

										<tr>
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">PAN</th>
											<td><%=pan%>
											</td>
										</tr>
										<tr>
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Mobile</th>
											<td><%=mobile%>
											</td>
										</tr>
										<tr>
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Email</th>
											<td><%=email%>
											</td>
										</tr>
										<tr id="mandateStatusRow">
											<th class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">Mandate status</th>
											<td>
												<% String color="",textcolor="";
													if(nachCompletedStatus) {
                                                        color="bg-light-success";
                                                        textcolor="text-success";
													} else {
                                                        color="bg-light-warning";
                                                        textcolor="text-warning";
													}%>
												<div class="d-flex align-items-center <%=color%> rounded p-5 mb-7">
													<i class="ki-duotone ki-abstract-26 <%=textcolor%> fs-1 me-5">
														<span class="path1"></span>
														<span class="path2"></span>
													</i>
													<div class="flex-grow-1 me-2">
														<a href="#" class="fw-bold text-gray-800 text-hover-primary fs-6"><%=mandateStatus%></a>
													</div>
												</div>
											</td>
										</tr>

									</thead>
								</table>

								<div class="mb-1 mt-1 text-end">
									<%if(nachAllowFlag) {%>
									<button id="kt_button_nach" type="button" class="btn btn-sm btn-light-primary runnachMandate">
										<span class="indicator-label">Send NACH Mandate request</span>
										<span class="indicator-progress">Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
									</button><%}%>
									<button id="kt_button_check_status" type="button" class="btn btn-sm btn-light-info" disabled>
										<span class="indicator-label">Check Mandate Status</span>
										<span class="indicator-progress">Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
									</button>

									<button id="kt_button_resend_nach" type="button" class="btn btn-sm btn-light-linkedin">
										<span class="indicator-label">Resend NACH Details</span>
										<span class="indicator-progress">Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
									</button>
									<%if(nachCancelAllow) {%>

									<button id="kt_button_cancel_mandate" type="button" class="btn btn-sm btn-light-danger" >
										<span class="indicator-label">Cancel Mandate</span>
										<span class="indicator-progress">Please wait... <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
									</button><%}%>
								</div>

							</div>
						</div>
					</div>
				</div>
				<div class="col-lg-3">


				</div>

			</div>


			<div class="text-end">
				<input type="hidden" id="collapseDisable" value="<%=checker?1:0%>">
				<%if (checker) {%>
				<button type="button" id="nachSave" class="btn btn-sm btn-primary nachSave">Save<i class="ph-paper-plane-tilt ms-2"></i></button>
				<%}%>
			</div>
			<%} else {%>
			<span class="text-muted fw-semibold fs-6">
                          Not applicable for SIB Customers
                        </span>
			<%}%>
		</div>
	</div>


</div>
<%--		</div>--%>
<%--		<!--end::Timeline details-->--%>
<%--	</div>--%>
<%--	<!--end::Timeline content-->--%>
<%--</div>--%>
<%--<!--end::Timeline item-->--%>




