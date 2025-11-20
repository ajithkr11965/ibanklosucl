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
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
	<head>
		<meta charset="UTF-8">
		<title>Entry Queue</title>
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
		</style>
	</head>

	<body id="kt_body" >
		<jsp:include page="header.jsp"/>
		<div class="page-header">
			<div class="page-header-content d-lg-flex">
				<div class="d-flex">
					<h4 class="page-title mb-0">
						Home - <span class="fw-normal">Branch Maker Queue</span>
					</h4>

					<a href="#page_header" class="btn btn-light align-self-center collapsed d-lg-none border-transparent rounded-pill p-0 ms-auto" data-bs-toggle="collapse">
						<span class="ph-caret-down collapsible-indicator ph-sm m-1"></span>
					</a>
				</div>

				<div class="collapse d-lg-block my-lg-auto ms-lg-auto" id="page_header1">
				</div>
			</div>
		</div>
		<div class="page-content pt-0">
			<jsp:include page="sidebar.jsp"/>

			<!-- /main sidebar -->
			<!-- Main content -->
			<div class="content-wrapper">
				<div class="content">
					<div class="row">


						<div  id="kt_wrapper" class="kt">
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

												<!--begin::Filter-->

												<!--end::Filter-->
												<!--begin::Export dropdown-->
												<form name="wicreate" id="wicreate" method="post" action="wicreate">
													<input type="hidden" name="slno" value="" id="slno">
													<button type="submit" name="action" value="Add" class="btn btn-sm btn-light-primary hover-scale">
														<i class="ki-duotone ki-plus fs-4">
															<span class="path1"></span>
															<span class="path2"></span>
														</i>New Application
													</button>
												</form>

												<!--end::Export dropdown-->
											</div>
										</div>
										<div class="card-body py-3">
											<div class="table-responsive">
												<form name="wibmlist" id="wibmlist" method="post" action="wicreate">
													<input type="hidden" name="slno" value="" id="slnoInput">
													<input type="hidden" name="action" value="Modify" id="action_mod">
													<table class="table align-middle table-row-dashed fs-8 gy-3" id="branch-maker-queue-details-table">
														<thead>
															<tr class="text-start text-gray-400 fw-bold fs-8 text-uppercase gs-0">
																<th class="min-w-10px"></th>
															</tr>
														</thead>
														<tbody class="fw-semibold text-gray-600">
															<tr>
																<td>
																	<div class="card mb-5 mb-xxl-8">
																		<div class="card-body pt-9 pb-0">
																			<!--begin::Details-->
																			<div class="d-flex flex-wrap flex-sm-nowrap mb-6">
																				<!--begin::Image-->
																				<div class="d-flex flex-center flex-shrink-0 bg-light rounded w-100px h-100px w-lg-50px h-lg-50px me-4 mb-4">
																					<img src="assets/images/social.png" alt="image" class="mw-20px mw-lg-40px">
																				</div>
																				<!--end::Image-->
																				<!--begin::Wrapper-->
																				<div class="flex-grow-1">
																					<!--begin::Head-->
																					<div class="d-flex justify-content-between align-items-start flex-wrap mb-2">
																						<!--begin::Details-->
																						<div class="d-flex flex-column">
																							<!--begin::Status-->
																							<div class="d-flex align-items-center mb-1">
																								<a href="#" class="text-gray-800 text-hover-primary fs-2 fw-bold me-3">Rahul Rajagopal</a>
																								<span class="badge badge-light-success me-auto">Alloted</span>
																							</div>
																							<!--end::Status-->
																							<!--begin::Description-->
																							<div class="d-flex flex-wrap fw-semibold mb-4 fs-5 text-gray-500">
																								<i class="ki-duotone ki-notification-status fs-2 me-2 mt-1">
																									<span class="path1"></span>
																									<span class="path2"></span>
																									<span class="path3"></span>
																									<span class="path4"></span>
																								</i> VLR_000000267
																							</div>
																							<!--end::Description-->
																						</div>
																						<!--end::Details-->
																						<!--begin::Actions-->
																						<div class="d-flex mb-4" style="height: 50px;">
																							<select aria-label="Select example" class="form-select me-2">
																								<option>Any agent</option>
																								<option value="1">Grace Green</option>
																								<option value="2">Nick LOgan</option>
																								<option value="3">Carles Nilson</option>
																								<option value="1">Alice Danchik</option>
																								<option value="2">Harris Bold</option>
																								<option value="3">Carles Nilson</option>
																							</select>
																							<a href="#" class="btn btn-light-success me-2">Save</a>
																							<!--begin::Menu-->
																							<!--end::Menu-->
																							<a href="#" class="btn btn-light-danger">View</a>
																						</div>
																						<!--end::Actions-->
																					</div>
																					<!--end::Head-->
																					<!--begin::Info-->
																					<div class="d-flex flex-wrap justify-content-start">
																						<!--begin::Stats-->
																						<div class="d-flex flex-wrap">
																							<!--begin::Stat-->
																							<div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-6 mb-3">
																								<!--begin::Number-->
																								<div class="d-flex align-items-center">
																									<div class="fs-4 fw-bold">29 Jan, 2024</div>
																								</div>
																								<!--end::Number-->
																								<!--begin::Label-->
																								<div class="fw-semibold fs-6 text-gray-500">Sanction Date</div>
																								<!--end::Label-->
																							</div>
																							<!--end::Stat-->
																							<!--begin::Stat-->
																							<div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-6 mb-3">
																								<!--begin::Number-->
																								<div class="d-flex align-items-center">
																									<i class="ki-duotone ki-arrow-down fs-3 text-danger me-2">
																										<span class="path1"></span>
																										<span class="path2"></span>
																									</i>
																									<div class="fs-4 fw-bold counted" data-kt-countup="true" data-kt-countup-value="75" data-kt-initialized="1">2</div>
																								</div>
																								<!--end::Number-->
																								<!--begin::Label-->
																								<div class="fw-semibold fs-6 text-gray-500">Co Applicant</div>
																								<!--end::Label-->
																							</div>
																							<!--end::Stat-->
																							<!--begin::Stat-->
																							<div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-6 mb-3">
																								<!--begin::Number-->
																								<div class="d-flex align-items-center">
																									<i class="ki-duotone ki-arrow-up fs-3 text-success me-2">
																										<span class="path1"></span>
																										<span class="path2"></span>
																									</i>
																									<div class="fs-4 fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1">10,00,256</div>
																								</div>
																								<!--end::Number-->
																								<!--begin::Label-->
																								<div class="fw-semibold fs-6 text-gray-500">Loan Amount</div>
																								<!--end::Label-->
																							</div>
																							<!--end::Stat-->
																						</div>
																						<!--end::Stats-->
																						<!--begin::Users-->
																						<!--end::Users-->
																					</div>
																					<!--end::Info-->
																				</div>
																				<!--end::Wrapper-->
																			</div>
																			<!--end::Details-->
																			<div class="separator"></div>
																			<!--begin::Nav-->
																			<!--end::Nav-->
																		</div>
																	</div>
																</td>
															</tr>

															<tr>
																<td>
																	<div class="card mb-5 mb-xxl-8">
																		<div class="card-body pt-9 pb-0">
																			<!--begin::Details-->
																			<div class="d-flex flex-wrap flex-sm-nowrap mb-6">
																				<!--begin::Image-->
																				<div class="d-flex flex-center flex-shrink-0 bg-light rounded w-100px h-100px w-lg-50px h-lg-50px me-4 mb-4">
																					<img src="assets/images/social.png" alt="image" class="mw-20px mw-lg-40px">
																				</div>
																				<!--end::Image-->
																				<!--begin::Wrapper-->
																				<div class="flex-grow-1">
																					<!--begin::Head-->
																					<div class="d-flex justify-content-between align-items-start flex-wrap mb-2">
																						<!--begin::Details-->
																						<div class="d-flex flex-column">
																							<!--begin::Status-->
																							<div class="d-flex align-items-center mb-1">
																								<a href="#" class="text-gray-800 text-hover-primary fs-2 fw-bold me-3">Tom D Cheriya</a>
																								<span class="badge badge-light-danger me-auto">Not Alloted</span>
																							</div>
																							<!--end::Status-->
																							<!--begin::Description-->
																							<div class="d-flex flex-wrap fw-semibold mb-4 fs-5 text-gray-500">
																								<i class="ki-duotone ki-notification-status fs-2 me-2 mt-1">
																									<span class="path1"></span>
																									<span class="path2"></span>
																									<span class="path3"></span>
																									<span class="path4"></span>
																								</i> VLR_000000240
																							</div>
																							<!--end::Description-->
																						</div>
																						<!--end::Details-->
																						<!--begin::Actions-->
																						<div class="d-flex mb-4" style="height: 50px;">
																							<select aria-label="Select example" class="form-select me-2">
																								<option>Any agent</option>
																								<option value="1">Grace Green</option>
																								<option value="2">Nick LOgan</option>
																								<option value="3">Carles Nilson</option>
																								<option value="1">Alice Danchik</option>
																								<option value="2">Harris Bold</option>
																								<option value="3">Carles Nilson</option>
																							</select>
																							<a href="#" class="btn btn-light-success me-2">Save</a>
																							<!--begin::Menu-->
																							<!--end::Menu-->
																							<a href="#" class="btn btn-light-danger">View</a>
																						</div>
																						<!--end::Actions-->
																					</div>
																					<!--end::Head-->
																					<!--begin::Info-->
																					<div class="d-flex flex-wrap justify-content-start">
																						<!--begin::Stats-->
																						<div class="d-flex flex-wrap">
																							<!--begin::Stat-->
																							<div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-6 mb-3">
																								<!--begin::Number-->
																								<div class="d-flex align-items-center">
																									<div class="fs-4 fw-bold">29 Jan, 2024</div>
																								</div>
																								<!--end::Number-->
																								<!--begin::Label-->
																								<div class="fw-semibold fs-6 text-gray-500">Sanction Date</div>
																								<!--end::Label-->
																							</div>
																							<!--end::Stat-->
																							<!--begin::Stat-->
																							<div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-6 mb-3">
																								<!--begin::Number-->
																								<div class="d-flex align-items-center">
																									<i class="ki-duotone ki-arrow-down fs-3 text-danger me-2">
																										<span class="path1"></span>
																										<span class="path2"></span>
																									</i>
																									<div class="fs-4 fw-bold counted" data-kt-countup="true" data-kt-countup-value="75" data-kt-initialized="1">2</div>
																								</div>
																								<!--end::Number-->
																								<!--begin::Label-->
																								<div class="fw-semibold fs-6 text-gray-500">Co Applicant</div>
																								<!--end::Label-->
																							</div>
																							<!--end::Stat-->
																							<!--begin::Stat-->
																							<div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-6 mb-3">
																								<!--begin::Number-->
																								<div class="d-flex align-items-center">
																									<i class="ki-duotone ki-arrow-up fs-3 text-success me-2">
																										<span class="path1"></span>
																										<span class="path2"></span>
																									</i>
																									<div class="fs-4 fw-bold counted" data-kt-countup="true" data-kt-countup-value="15000" data-kt-countup-prefix="$" data-kt-initialized="1">10,00,256</div>
																								</div>
																								<!--end::Number-->
																								<!--begin::Label-->
																								<div class="fw-semibold fs-6 text-gray-500">Loan Amount</div>
																								<!--end::Label-->
																							</div>
																							<!--end::Stat-->
																						</div>
																						<!--end::Stats-->
																						<!--begin::Users-->
																						<!--end::Users-->
																					</div>
																					<!--end::Info-->
																				</div>
																				<!--end::Wrapper-->
																			</div>
																			<!--end::Details-->
																			<div class="separator"></div>
																			<!--begin::Nav-->
																			<!--end::Nav-->
																		</div>
																	</div>
																</td>
															</tr>
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
			$('#branch-maker-queue-details-table').on('click','.modify-btn',function (e) {
				e.preventDefault();
				e.stopPropagation();
                    var slno = $(this).data('slno');
                    $('#slnoInput').val(slno);
                    $('#wibmlist').submit();
                });
            });
		</script>
	</body>
</html>