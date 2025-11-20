<%--
  Created by IntelliJ IDEA.
  User: SIBL11965
  Date: 18-07-2024
  Time: 16:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanMasterDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
	<head>
		<meta charset="UTF-8">
		<title>RM Queue</title>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="_csrf" content="">
		<meta name="_csrf_header" content="">
		<!-- Global stylesheets -->
		<%--		<link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">--%>
		<%--		--%>
		<link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
		<link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
		<link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
		<%--		<link href="assets/css/custom/dashboard.css" rel="stylesheet" type="text/css">--%>
		<!-- /global stylesheets -->
		<!-- Core JS files -->
		<link href="assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css"/>
		<link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
		<link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
	</head>
	<%
		Employee employee = new Employee();
		if (request.getAttribute("employee") != null) {
			employee = (Employee) request.getAttribute("employee");
		}
	%>
	<body id="kt_body">
		<jsp:include page="header.jsp"/>
		<los:loader/>
		<div class="page-header">
			<div class="page-header-content d-lg-flex">
				<div class="kt">
					<div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-0 ms-3 mb-3 mt-1">
						<div class="d-flex align-items-center mt-2 mb-2">
							<div class="d-flex flex-center w-25px h-25px rounded-3 bg-light-primary bg-opacity-90 me-3">
								<i class="ki-duotone ki-abstract-32 text-primary fs-3x"><span class="path1"></span><span class="path2"></span></i></div>
							<div data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1" class="fs-8  fw-bold counted ms-2">CPC
								Maker
							</div>
						</div>
					</div>
				</div>

				<div class="collapse d-lg-block my-lg-auto ms-lg-auto" id="page_header1">
				</div>
			</div>
		</div>

		<div class="page-content pt-0">
			<%request.setAttribute("TAB", "RBCM");%>
			<jsp:include page="sidebar.jsp"/>

			<!-- /main sidebar -->
			<!-- Main content -->
			<div class="content-wrapper">
				<div class="content">
					<div class="row">


						<div class="kt" id="kt_wrapper">
							<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
								<div class="col-xl-12">
									<div class="card card-xl-stretch mb-5 mb-xl-8">
										<div class="card-header border-0 pt-5">
											<div class="card-title">
												<!--begin::Search-->
												<div class="d-flex align-items-center position-relative my-1">
													<i class="ki-duotone ki-magnifier fs-3 position-absolute ms-4 hover-elevate-up">
														<span class="path1"></span>
														<span class="path2"></span>
													</i>
													<input type="text" data-kt-ecommerce-order-filter="search" class="form-control form-control-solid w-250px ps-12"
													       placeholder="Search Report"/>
												</div>
												<!--end::Search-->
												<!--begin::Export buttons-->
												<div id="kt_ecommerce_report_customer_orders_export" class="d-none"></div>
												<!--end::Export buttons-->
											</div>
											<div class="card-toolbar flex-row-fluid justify-content-end gap-5">

											</div>
										</div>
										<div class="card-body py-3">
											<div class="table-responsive">
												<form name="wirmlist" id="wirmlist" method="post" action="wirmmodify">
													<input type="hidden" name="slno" value="" id="slnoInput">
													<input type="hidden" name="action" value="RM" id="action_mod">
													<table class="table align-middle table-row-dashed fs-8 gy-3" id="branch-maker-queue-details-table">
														<thead>
															<tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
																<th class="min-w-10px"></th>
																<th>WI Num</th>
																<th>Main Applicant</th>
																<th>Appl Date</th>
																<th>Channel</th>
																<th>Queue Date</th>
																<%--                          <th>Status</th>--%>
															</tr>
														</thead>
														<tbody class="fw-semibold text-gray-600">
															<%
																String userId = employee.getPpcno();
																List<VehicleLoanMasterDTO> rmQueue = (List<VehicleLoanMasterDTO>) request.getAttribute("rmQueue");
																int count = 1;
																for (VehicleLoanMasterDTO item : rmQueue) {
																	String lockDetails = "";
																	if (item.getVehicleLoanLock() != null && !userId.equals(item.getVehicleLoanLock().getLockedBy()) && "Y".equals(item.getVehicleLoanLock().getLockFlg())) {
																		lockDetails = "<i class='ki-duotone ki-lock-3'>" +
																				"<span class='path1'></span>" +
																				"<span class='path2'></span>" +
																				"<span class='path3'></span>" +
																				"</i> Locked By -" + item.getVehicleLoanLock().getLockedBy();
																	}
															%>
															<tr>
																<td>

																	<button type="button" name="modify-btn" value="RM"
																	        class="btn btn-icon btn-bg-light btn-active-color-primary btn-sm me-1 modify-btn pulse pulse-primary"
																	        data-slno="<%=item.getSlno()%>"
																	>
																		<i class="ki-duotone ki-pencil fs-3">
																			<span class="path1"></span>
																			<span class="path2"></span>
																		</i>
																	</button>
																</td>
																<td>
																	<div class="col">
																		<div class="d-flex align-items-center me-2">
																			<div>
																				<div class="fs-7 fw-bold me-2"><%= item.getWiNum() %>
																				</div>
																			</div>
																			<%if (item.getVehicleLoanLock() != null && !userId.equals(item.getVehicleLoanLock().getLockedBy()) && "Y".equals(item.getVehicleLoanLock().getLockFlg())) {%>
																			<i class="ki-duotone ki-lock fs-7 text-warning" data-bs-custom-class="tooltip-inverse"
																			   data-bs-toggle="tooltip"
																			   data-bs-html="true" title="<%=lockDetails%>">
																				<span class="path1"></span>
																				<span class="path2"></span>
																				<span class="path3"></span>
																			</i>
																			<%}%>

																		</div>
																	</div>
																</td>
																<td>
																	<div class="d-flex align-items-center">
																		<div class="symbol symbol-circle symbol-50px overflow-hidden me-3">
																			<div class="symbol-label fs-3 bg-light-success text-success"><%= item.getCustName().substring(0, 1) %>
																			</div>

																		</div>
																		<div class="ms-5">
																			<a href="#" class="text-gray-800 text-hover-primary fs-8 fw-bold">
																				<%= item.getCustName() %>
																			</a>
																		</div>
																	</div>
																</td>
																<td><%=item.getRiRcreDate()%>
																</td>
																<td>
																	<div class="col">
																		<div class="d-flex align-items-center me-2">
																			<!--begin::Symbol-->
																			<div class="symbol symbol-40px me-3">
																				<div class="symbol-label bg-light-danger">
																					<i class="ki-duotone ki-abstract-26 fs-7 text-danger">
																						<span class="path1"></span>
																						<span class="path2"></span>
																					</i>
																				</div>
																			</div>
																			<!--end::Symbol-->
																			<!--begin::Title-->
																			<div>
																				<div class="fs-9 text-muted fw-bold"><%=item.getChannel()%>
																				</div>
																			</div>
																			<!--end::Title-->
																		</div>
																	</div>
																</td>
																<td><%=item.getQueueDate()%>
																</td>

																</td>
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
		<script src="assets/demo/demo_configurator.js"></script>
		<script src="assets/js/bootstrap/bootstrap.bundle.min.js"></script>
		<!-- /core JS files -->
		<!-- Theme JS files -->
		<script src="assets/js/vendor/visualization/d3/d3.min.js"></script>
		<script src="assets/js/vendor/visualization/d3/d3_tooltip.js"></script>
		<script src="assets/js/jquery/jquery.min.js"></script>

		<script src="assets/js/scripts.bundle.js"></script>
		<script src="assets/plugins/global/plugins.bundle.js"></script>
		<script src="assets/plugins/custom/datatables/datatables.bundle.js"></script>
		<!--end::Vendors Javascript-->
		<!--begin::Custom Javascript(used for this page only)-->
		<script src="assets/js/custom/dashboard.js"></script>

		<script src="assets/js/app.js"></script>
		<script>
            $(document).ready(function () {
                $('.progress-info').hover(function () {
                    $(this).find('.applicant-progress').toggle();
                });
                // $('.modify-btn').click(function () {
                //     var slno = $(this).data('slno');
                //     $('#slnoInput').val(slno);
                //     $('#wibmlist').submit();
                // });
                $('#branch-maker-queue-details-table').on('click', '.modify-btn', function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    showLoader();
                    var slno = $(this).data('slno');
                    $('#slnoInput').val(slno);
                    $('#wirmlist').submit();
                });
            });
		</script>
		    <script src="assets/js/custom/performance-logging.js"></script>
	</body>
</html>