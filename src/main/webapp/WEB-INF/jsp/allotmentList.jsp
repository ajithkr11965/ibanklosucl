<%--
  Created by IntelliJ IDEA.
  User: SIBL11965
  Date: 11-06-2024
  Time: 13:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanMasterDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanApplicantDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.AllotmentDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.UserSelectDTO" %>
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
<head>
	<meta charset="UTF-8">
	<title>Allotment</title>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="_csrf" content="">
	<meta name="_csrf_header" content="">
	<!-- Global stylesheets -->
	<%--		<link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">--%>
	<%--		--%>

	<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
	<script src="assets/js/jquery/jquery.min.js"></script>
	<link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
	<link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
	<link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
	<%--		<link href="assets/css/custom/dashboard.css" rel="stylesheet" type="text/css">--%>
	<!-- /global stylesheets -->
	<!-- Core JS files -->
	<link href="assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css"/>
	<link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
	<link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>

	<script src="assets/js/vendor/notifications/sweet_alert.min.js"></script>
	<script src="assets/js/custom/allotment/allotmentList.js"></script>
	<style>

		.asidecheck {
			position: fixed;
			top: 105px;
			bottom: 30px;
			left: 30px;
			z-index: 100;
			overflow: hidden;
			width: 300px;
			border-radius: 1.75rem;
		}

		.table-shadow
		{
			box-shadow: rgba(0, 0, 0, 0.05) 0px 6px 24px 0px, rgba(17, 17, 17, 0.05) 0px 0px 0px 1px !important;
		}
		.bodycolor
		{
			background-color: #fff !important;
		}

	</style>
</head>

<%
	Employee employee = new Employee();
	if (request.getAttribute("employee") != null) {
		employee = (Employee) request.getAttribute("employee");
	}
%>

<body id="kt_body" class="bodycolor">
<jsp:include page="header.jsp"/>
<div class="page-header">
	<div class="page-header-content d-lg-flex">
		<div class="kt">
			<div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-0 ms-3 mb-3 mt-1">
				<!--begin::Number-->
				<div class="d-flex align-items-center mt-2 mb-2">
					<div class="d-flex flex-center w-25px h-25px rounded-3 bg-light-primary bg-opacity-90 me-3">
						<i class="ki-duotone ki-abstract-26 text-primary fs-3x"><span class="path1"></span><span class="path2"></span></i>                </div>
					<div data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1" class="fs-8  fw-bold counted ms-2">Allotment Page</div>
				</div>
				<!--end::Number-->
				<!--begin::Label-->
				<!--end::Label-->
			</div>
		</div>

		<div class="collapse d-lg-block my-lg-auto ms-lg-auto" id="page_header1">
		</div>
	</div>
</div>
<div class="page-content pt-0">
	<%request.setAttribute("TAB","ALLOT");%>
	<jsp:include page="sidebar.jsp"/>

	<!-- /main sidebar -->
	<!-- Main content -->
	<div class="content-wrapper">
		<div class="content">
			<div class="row">

				<div  id="kt_wrapper" class="kt">
					<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
						<div class="col-xl-12">
							<div class="card card-xl-stretch mb-5 mb-xl-8 table-shadow">
								<div class="card-header border-0 pt-5">
									<div class="card-title">
										<!--begin::Search-->
										<div class="d-flex align-items-center position-relative my-1">
											<i class="ki-duotone ki-magnifier fs-3 position-absolute ms-4 hover-elevate-up">
												<span class="path1"></span>
												<span class="path2"></span>
											</i>
											<input type="text" id="search_pending" data-kt-ecommerce-order-filter="search_pending" class="form-control form-control-solid w-250px ps-12" placeholder="Search Report"/>
											<input type="text" id="search_alloted" data-kt-ecommerce-order-filter="search_alloted" class="form-control form-control-solid w-250px ps-12" placeholder="Search Report"/>

										</div>
										<!--end::Search-->
										<!--begin::Export buttons-->
										<div id="kt_ecommerce_report_customer_orders_export" class="d-none"></div>
										<!--end::Export buttons-->
									</div>
									<div class="card-toolbar flex-row-fluid justify-content-end gap-5">

										<!--begin::Filter-->

										<!--end::Filter-->
										<!--begin::Export dropdown-->

										<input type="hidden" name="slno" value="" id="slno">
										<a href="#" class="btn btn-sm btn-light-primary hover-scale pending_btn" onclick="changecategory_topending(this)" style="display:none;">Show Pending Data For Allotment</a>
										<a href="#" class="btn btn-sm btn-light-success hover-scale alloted_btn" onclick="changecategory_toalloted(this)" >Show Already Alloted Data</a>

										<!--end::Export dropdown-->
									</div>
								</div>
								<div class="card-body py-3">
									<div class="table-responsive">

										<form name="wibmlist" id="wibmlist" method="post" action="wichecker">
											<input type="hidden" name="slno" value="" id="slnoInput">
											<input type="hidden" name="action" value="AQ" id="action_mod">
											<table class="table align-middle table-row-dashed fs-8 gy-3 pending_allot" id="pending_allot">

												<thead>
												<tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
													<th class="min-w-10px"></th>
													<th class="min-w-10px">Main Applicant</th>
													<th class="min-w-10px">WINum</th>
													<th class="min-w-10px">Request Loan Amount</th>
													<th class="min-w-10px">Branch Name</th>
													<th class="min-w-10px">Reg Name</th>
													<th class="min-w-10px">Queue Date</th>
													<th class="min-w-10px"></th>
												</tr>
												</thead>
												<tbody class="fw-semibold text-gray-600 ">
												<%
													List<AllotmentDTO> allotmentList = (List<AllotmentDTO>) request.getAttribute("allotmentList");
													int count = 0;
												//	out.println(allotmentList);
													for (AllotmentDTO allotment : allotmentList) {
														count++;
												%>
												<tr data-slno=<%=allotment.getSlno()%>>
													<td>
														<button type="button" name="modify-btn" value="AQ"
																class="btn btn-icon btn-bg-light btn-active-color-primary btn-sm me-1 modify-btn pulse pulse-primary"
																data-slno="<%=allotment.getSlno()%>"
														>
															<i class="ki-duotone ki-pencil fs-3">
																<span class="path1"></span>
																<span class="path2"></span>
															</i>
														</button>
													</td>
													<td>
														<!--begin::User-->
														<div class="d-flex align-items-center">
															<!--begin::Wrapper-->
															<div class="me-5 position-relative">
																<!--begin::Avatar-->
																<div class="symbol symbol-35px symbol-circle">
																	<img alt="Pic" src="assets/images/social.png">
																</div>
																<!--end::Avatar-->
															</div>
															<!--end::Wrapper-->
															<!--begin::Info-->
															<div class="d-flex flex-column justify-content-center">
																<a href="" class="fs-8  text-gray-800 text-hover-primary"><%=allotment.getCustName()%></a>
															</div>
															<!--end::Info-->
														</div>
														<!--end::User-->
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone ki-notification-status  fs-3 text-primary me-3">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getWiNum()%></div>
														</div>
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone ki-two-credit-cart fs-3 text-success me-2">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
																<span class="path5"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getLoanAmt()%></div>
														</div>
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone ki-bank  fs-3 text-primary me-3">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getBrName()%></div>
														</div>
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone bi-building  fs-3 text-primary me-3">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getRegName()%>	</div>
														</div>
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone bi-building  fs-3 text-primary me-3">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getAllotQueueDate()%>	</div>
														</div>
													</td>
													<td class="">
														<a href="#" class="btn btn-light-danger me-2 mb-2 fs-8 " onclick="allotmentsave(this)">Allot PPC</a>
													</td>
													<input type="hidden" id="wiNum_<%=allotment.getSlno()%>" value="<%=allotment.getWiNum()%>">
												</tr>
												<% } %>

												</tbody>
											</table>

											<table class="table align-middle table-row-dashed fs-8 gy-3 already_alloted" style="display:none;" id="already_alloted">

												<thead>
												<tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
													<th class="min-w-10px"></th>
													<th class="min-w-10px">Main Applicant</th>
													<th class="min-w-10px">WI Num</th>
													<th class="min-w-10px">Request Loan Amount</th>
													<th class="min-w-10px">Branch Name</th>
													<th class="min-w-10px">Reg Name</th>
													<th class="min-w-10px">Alloted User</th>
													<th class="min-w-10px">Queue Date</th>
													<th class="min-w-10px"></th>
												</tr>
												</thead>




												<tbody class="fw-semibold text-gray-600">
												<%
													List<AllotmentDTO> allotmentListAlloted = (List<AllotmentDTO>) request.getAttribute("allotmentListAlloted");
													count = 0;
													for (AllotmentDTO allotment : allotmentListAlloted) {
														count++;
												%>
												<tr data-slno=<%=allotment.getSlno()%>>
													<td>
														<button type="button" name="modify-btn" value="AQ"
																class="btn btn-icon btn-bg-light btn-active-color-primary btn-sm me-1 modify-btn pulse pulse-primary"
																data-slno="<%=allotment.getSlno()%>"
														>
															<i class="ki-duotone ki-pencil fs-3">
																<span class="path1"></span>
																<span class="path2"></span>
															</i>
														</button>
													</td>
													<td>
														<!--begin::User-->
														<div class="d-flex align-items-center">
															<!--begin::Wrapper-->
															<div class="me-5 position-relative">
																<!--begin::Avatar-->
																<div class="symbol symbol-35px symbol-circle">
																	<img alt="Pic" src="assets/images/social.png">
																</div>
																<!--end::Avatar-->
															</div>
															<!--end::Wrapper-->
															<!--begin::Info-->
															<div class="d-flex flex-column justify-content-center">
																<a href="" class="fs-8  text-gray-800 text-hover-primary"><%=allotment.getCustName()%></a>
															</div>
															<!--end::Info-->
														</div>
														<!--end::User-->
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone ki-notification-status  fs-3 text-primary me-3">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getWiNum()%></div>
														</div>
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone ki-two-credit-cart fs-3 text-success me-2">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
																<span class="path5"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getLoanAmt()%></div>
														</div>
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone ki-bank  fs-3 text-primary me-3">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getBrName()%></div>
														</div>
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone ki-bank  fs-3 text-primary me-3">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getRegName()%></div>
														</div>
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone ki-user-tick   fs-3 text-gray me-1">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getAllotedPpc()%></div>
														</div>
													</td>
													<td>
														<div class="d-flex align-items-center">
															<i class="ki-duotone ki-user-tick   fs-3 text-gray me-1">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
																<span class="path4"></span>
															</i>
															<div class="fs-8  fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1"><%=allotment.getAllotQueueDate()%></div>
														</div>
													</td>
													<td class="">
														<a href="#" class="btn btn-light-danger me-2 mb-2 fs-8 " onclick="allotmentsave(this)">Allot PPC</a>
													</td>

													<input type="hidden" id="wiNum_<%=allotment.getSlno()%>" value="<%=allotment.getWiNum()%>">
													<input type="hidden" id="allotedPpc_<%=allotment.getSlno()%>" value="<%=allotment.getAllotedPpc()%>">
												</tr>
												<% } %>

												</tbody>

											</table>
										</form>

									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

		</div>
	</div>

</div>

<jsp:include page="footer.jsp"/>
<div id="modal_default" class="modal fade" tabindex="-1">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title">Allot PPC</h5>
				<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
			</div>

			<div class="modal-body">
				<div class="row mb-3">
					<label class="col-form-label col-sm-3">Allotment PPC</label>
					<div class="col-sm-9">
						<select class="form-control select select2-initialisation select2-initialisation-allot fs-8 " aria-label="Select example" id="doPpc">
							<option value="">Select</option>
							<%
								List<UserSelectDTO> userdetailsallot = (List<UserSelectDTO>) request.getAttribute("userdetailsallot");
								for (UserSelectDTO userdetails : userdetailsallot) {
									out.print("<option value=\"" + userdetails.getPpcno() + "\"     >" + "(" + userdetails.getPpcno() + ") - " + userdetails.getPpcName() + "</option>");

								}%>
						</select>
					</div>
				</div>

				<div class="row mb-3">
					<label class="col-form-label col-sm-3">Remarks</label>
					<div class="col-sm-9">
						<input type="text" id="remarks" placeholder="Enter Remarks" class="form-control">
					</div>
				</div>
			</div>

			<div class="modal-footer">
				<button type="button"  class="btn btn-flat-primary border-transparent" data-bs-dismiss="modal">Close</button>
				<button type="button" id="modalsave" class="btn btn-flat-success border-transparent">Save changes</button>
			</div>
		</div>
	</div>
</div>


<%--<script src="assets/demo/demo_configurator.js"></script>--%>
<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
<!-- /core JS files -->
<!-- Theme JS files -->
<script src="assets/js/vendor/visualization/d3/d3.min.js"></script>
<script src="assets/js/vendor/visualization/d3/d3_tooltip.js"></script>


<script src="assets/js/scripts.bundle.js"></script>
<script src="assets/plugins/global/plugins.bundle.js"></script>
<script src="assets/plugins/custom/datatables/datatables.bundle.js"></script>
<!--end::Vendors Javascript-->
<!--begin::Custom Javascript(used for this page only)-->
<%--<script src="assets/js/custom/dashboard.js"></script>--%>
<%--<script src="assets/js/custom/allotment_pending.js"></script>--%>
<script src="assets/js/custom/allotment_alloted.js"></script>
<script src="assets/js/vendor/forms/selects/select2.min.js"></script>
<script src="assets/demo/pages/form_select2.js"></script>

<script src="assets/js/app.js"></script>
<script>
	$(document).ready(function () {
		$('.progress-info').hover(function () {
			$(this).find('.applicant-progress').toggle();
		});
		$('.modify-btn').click(function () {
			var slno = $(this).data('slno');
			console.log(slno+ " : slno");
			$('#slnoInput').val(slno);
			$('#wibmlist').submit();
		});
		//$('#branch-maker-queue-details-table').on('click','.modify-btn',function (e) {
		// 	e.preventDefault();
		// 	e.stopPropagation();
		//         var slno = $(this).data('slno');
		//         $('#slnoInput').val(slno);
		//         $('#wibmlist').submit();
		//     });

		$('.select2-initialisation-allot').select2({
			templateResult: formatState,
			templateSelection: formatState

		});

		$('.pending_allot').show();
		$('.already_alloted').hide();

		$('.pending_btn').hide();
		$('.alloted_btn').show();


		$('#search_pending').show();
		$('#search_alloted').hide();

		$('#already_alloted_length').hide();
		$('#already_alloted_paginate').hide();

		$('#pending_allot_length').hide();
		$('#pending_allot_padginate').hide();

	});

	function formatState(state)
	{
		if(!state.id)
		{
			return state.text;
		}
		var $state=$('<span>'+state.text+'</span>');
		return $state;
	}

</script>
</body>
</html>