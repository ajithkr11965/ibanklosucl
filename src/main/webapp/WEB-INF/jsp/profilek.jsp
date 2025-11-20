<%--
  Created by IntelliJ IDEA.
  User: SIBL12134
  Date: 21-06-2024
  Time: 11:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanMasterDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.VehicleLoanApplicantDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="com.sib.ibanklosucl.dto.VehicleLoanRBCPCDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.WaiverSubtaskDTO" %>
<%@ page import="com.sib.ibanklosucl.dto.Reportee" %>
<!DOCTYPE html>
<html lang="en" data-bs-theme="light">
<head>
  <meta charset="UTF-8">
  <title>Waiver Checker Queue</title>
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
		List<Reportee> reporteeList = null;
        List<String> saleSols=null;
        List<String>clusterSols=null;
        List<String> userRoles =null;
		if (request.getSession().getAttribute("employeeData") != null) {
			employee = (Employee) session.getAttribute("employeeData");
		}
		if (request.getSession().getAttribute("reportees") != null) {
			reporteeList = (List<Reportee>) session.getAttribute("reportees");
		}
        if (request.getSession().getAttribute("saleSols") != null) {
			saleSols = (List<String>) session.getAttribute("saleSols");
		}
        if (request.getSession().getAttribute("reportees") != null) {
			clusterSols = (List<String>) session.getAttribute("clusterSols");
		}
         if (request.getSession().getAttribute("userRoles") != null) {
			userRoles = (List<String>) session.getAttribute("userRoles");
		}
	%>
<body id="kt_body">
<jsp:include page="header.jsp"/>
<div class="page-header">
  <div class="page-header-content d-lg-flex">

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


        <div id="kt_content_container" class="container-xxl kt">
								<!--begin::Navbar-->
								<div class="card mb-5 mb-xl-10">
									<div class="card-body pt-9 pb-0">
										<!--begin::Details-->
										<div class="d-flex flex-wrap flex-sm-nowrap">
											<!--begin: Pic-->
											<div class="me-7 mb-4">
												<div class="symbol symbol-100px symbol-lg-160px symbol-fixed position-relative">
													<img src="<%=employee.getImageUrl()%>" alt="image">
													<div class="position-absolute translate-middle bottom-0 start-100 mb-6 bg-success rounded-circle border border-4 border-body h-20px w-20px"></div>
												</div>
											</div>
											<!--end::Pic-->
											<!--begin::Info-->
											<div class="flex-grow-1">
												<!--begin::Title-->
												<div class="d-flex justify-content-between align-items-start flex-wrap mb-2">
													<!--begin::User-->
													<div class="d-flex flex-column">
														<!--begin::Name-->
														<div class="d-flex align-items-center mb-2">
															<a href="#" class="text-gray-900 text-hover-primary fs-2 fw-bold me-1"><%=employee.getPpcName()%></a>
															<a href="#">
																<i class="ki-duotone ki-verify fs-1 text-primary">
																	<span class="path1"></span>
																	<span class="path2"></span>
																</i>
															</a>
														</div>
														<!--end::Name-->
														<!--begin::Info-->
														<div class="d-flex flex-wrap fw-semibold fs-6 mb-4 pe-2">
															<a href="#" class="d-flex align-items-center text-gray-400 text-hover-primary me-5 mb-2">
															<i class="ki-duotone ki-profile-circle fs-4 me-1">
																<span class="path1"></span>
																<span class="path2"></span>
																<span class="path3"></span>
															</i><%=employee.getDesigDesc()%></a>
															<a href="#" class="d-flex align-items-center text-gray-400 text-hover-primary me-5 mb-2">
															<i class="ki-duotone ki-geolocation fs-4 me-1">
																<span class="path1"></span>
																<span class="path2"></span>
															</i><%=employee.getRoleName()%></a>
															<a href="#" class="d-flex align-items-center text-gray-400 text-hover-primary mb-2">
															<i class="ki-duotone ki-sms fs-4">
																<span class="path1"></span>
																<span class="path2"></span>
															</i><%=employee.getPpcno()%></a>
														</div>
														<!--end::Info-->
													</div>
													<!--end::User-->
													<!--begin::Actions-->

													<!--end::Actions-->
												</div>
												<!--end::Title-->
												<!--begin::Stats-->
												<div class="d-flex flex-wrap flex-stack">
													<!--begin::Wrapper-->
													<div class="d-flex flex-column flex-grow-1 pe-8">
														<!--begin::Stats-->
														<div class="d-flex flex-wrap">
															<!--begin::Stat-->
															<div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4 me-6 mb-3">
																<!--begin::Number-->
																<div class="d-flex align-items-center">
																	<i class="ki-duotone ki-arrow-up fs-3 text-success me-2">
																		<span class="path1"></span>
																		<span class="path2"></span>
																	</i>
																	<div class="fs-2 fw-bold counted" data-kt-countup="true" data-kt-countup-value="4500" data-kt-countup-prefix="$" data-kt-initialized="1">$4,500</div>
																</div>
																<!--end::Number-->
																<!--begin::Label-->
																<div class="fw-semibold fs-6 text-gray-400">Earnings</div>
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
																	<div class="fs-2 fw-bold counted" data-kt-countup="true" data-kt-countup-value="80" data-kt-initialized="1">80</div>
																</div>
																<!--end::Number-->
																<!--begin::Label-->
																<div class="fw-semibold fs-6 text-gray-400">Projects</div>
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
																	<div class="fs-2 fw-bold counted" data-kt-countup="true" data-kt-countup-value="60" data-kt-countup-prefix="%" data-kt-initialized="1">%60</div>
																</div>
																<!--end::Number-->
																<!--begin::Label-->
																<div class="fw-semibold fs-6 text-gray-400">Success Rate</div>
																<!--end::Label-->
															</div>
															<!--end::Stat-->
														</div>
														<!--end::Stats-->
													</div>
													<!--end::Wrapper-->
													<!--begin::Progress-->

													<!--end::Progress-->
												</div>
												<!--end::Stats-->
											</div>
											<!--end::Info-->
										</div>
										<!--end::Details-->
										<!--begin::Navs-->
										<ul class="nav nav-stretch nav-line-tabs nav-line-tabs-2x border-transparent fs-5 fw-bold">
											<!--begin::Nav item-->
											<li class="nav-item mt-2">
												<a class="nav-link text-active-primary ms-0 me-10 py-5 active" href="../../demo10/dist/account/overview.html">Overview</a>
											</li>
											<!--end::Nav item-->

										</ul>
										<!--begin::Navs-->
									</div>
								</div>
								<!--end::Navbar-->
								<!--begin::details View-->
								<div class="card mb-5 mb-xl-10" id="kt_profile_details_view">
									<!--begin::Card header-->
									<div class="card-header cursor-pointer">
										<!--begin::Card title-->
										<div class="card-title m-0">
											<h3 class="fw-bold m-0">Profile Details</h3>
										</div>
										<!--end::Card title-->
										<!--begin::Action-->
										<a href="../../demo10/dist/account/settings.html" class="btn btn-sm btn-primary align-self-center">Edit Profile</a>
										<!--end::Action-->
									</div>
									<!--begin::Card header-->
									<!--begin::Card body-->
									<div class="card-body p-9">
										<!--begin::Row-->
										<div class="row mb-7">
											<!--begin::Label-->
											<label class="col-lg-4 fw-semibold text-muted">Full Name</label>
											<!--end::Label-->
											<!--begin::Col-->
											<div class="col-lg-8">
												<span class="fw-bold fs-6 text-gray-800">Max Smith</span>
											</div>
											<!--end::Col-->
										</div>
										<!--end::Row-->
										<!--begin::Input group-->
										<div class="row mb-7">
											<!--begin::Label-->
											<label class="col-lg-4 fw-semibold text-muted">Company</label>
											<!--end::Label-->
											<!--begin::Col-->
											<div class="col-lg-8 fv-row">
												<span class="fw-semibold text-gray-800 fs-6">Keenthemes</span>
											</div>
											<!--end::Col-->
										</div>
										<!--end::Input group-->
										<!--begin::Input group-->
										<div class="row mb-7">
											<!--begin::Label-->
											<label class="col-lg-4 fw-semibold text-muted">Contact Phone
											<span class="ms-1" data-bs-toggle="tooltip" aria-label="Phone number must be active" data-bs-original-title="Phone number must be active" data-kt-initialized="1">
												<i class="ki-duotone ki-information fs-7">
													<span class="path1"></span>
													<span class="path2"></span>
													<span class="path3"></span>
												</i>
											</span></label>
											<!--end::Label-->
											<!--begin::Col-->
											<div class="col-lg-8 d-flex align-items-center">
												<span class="fw-bold fs-6 text-gray-800 me-2">044 3276 454 935</span>
												<span class="badge badge-success">Verified</span>
											</div>
											<!--end::Col-->
										</div>
										<!--end::Input group-->
										<!--begin::Input group-->
										<div class="row mb-7">
											<!--begin::Label-->
											<label class="col-lg-4 fw-semibold text-muted">Company Site</label>
											<!--end::Label-->
											<!--begin::Col-->
											<div class="col-lg-8">
												<a href="#" class="fw-semibold fs-6 text-gray-800 text-hover-primary">keenthemes.com</a>
											</div>
											<!--end::Col-->
										</div>
										<!--end::Input group-->
										<!--begin::Input group-->
										<div class="row mb-7">
											<!--begin::Label-->
											<label class="col-lg-4 fw-semibold text-muted">Country
											<span class="ms-1" data-bs-toggle="tooltip" aria-label="Country of origination" data-bs-original-title="Country of origination" data-kt-initialized="1">
												<i class="ki-duotone ki-information fs-7">
													<span class="path1"></span>
													<span class="path2"></span>
													<span class="path3"></span>
												</i>
											</span></label>
											<!--end::Label-->
											<!--begin::Col-->
											<div class="col-lg-8">
												<span class="fw-bold fs-6 text-gray-800">Germany</span>
											</div>
											<!--end::Col-->
										</div>
										<!--end::Input group-->
										<!--begin::Input group-->
										<div class="row mb-7">
											<!--begin::Label-->
											<label class="col-lg-4 fw-semibold text-muted">Communication</label>
											<!--end::Label-->
											<!--begin::Col-->
											<div class="col-lg-8">
												<span class="fw-bold fs-6 text-gray-800">Email, Phone</span>
											</div>
											<!--end::Col-->
										</div>
										<!--end::Input group-->
										<!--begin::Input group-->
										<div class="row mb-10">
											<!--begin::Label-->
											<label class="col-lg-4 fw-semibold text-muted">Allow Changes</label>
											<!--begin::Label-->
											<!--begin::Label-->
											<div class="col-lg-8">
												<span class="fw-semibold fs-6 text-gray-800">Yes</span>
											</div>
											<!--begin::Label-->
										</div>
										<!--end::Input group-->
										<!--begin::Notice-->
										<div class="notice d-flex bg-light-warning rounded border-warning border border-dashed p-6">
											<!--begin::Icon-->
											<i class="ki-duotone ki-information fs-2tx text-warning me-4">
												<span class="path1"></span>
												<span class="path2"></span>
												<span class="path3"></span>
											</i>
											<!--end::Icon-->
											<!--begin::Wrapper-->
											<div class="d-flex flex-stack flex-grow-1">
												<!--begin::Content-->

												<!--end::Content-->
											</div>
											<!--end::Wrapper-->
										</div>
										<!--end::Notice-->
									</div>
									<!--end::Card body-->
								</div>
								<!--end::details View-->
								<!--begin::Row-->
								<div class="row gy-5 g-xl-10">
									<!--begin::Col-->
									<div class="col-xl-8 mb-xl-10">
										<!--begin::Chart widget 5-->
										<div class="card card-flush h-lg-100">
											<!--begin::Header-->
											<div class="card-header flex-nowrap pt-5">
												<!--begin::Title-->
												<h3 class="card-title align-items-start flex-column">
													<span class="card-label fw-bold text-dark">Top Selling Categories</span>
													<span class="text-gray-400 pt-2 fw-semibold fs-6">8k social visitors</span>
												</h3>
												<!--end::Title-->
												<!--begin::Toolbar-->
												<div class="card-toolbar">
													<!--begin::Menu-->
													<button class="btn btn-icon btn-color-gray-400 btn-active-color-primary justify-content-end" data-kt-menu-trigger="click" data-kt-menu-placement="bottom-end" data-kt-menu-overflow="true">
														<i class="ki-duotone ki-dots-square fs-1 text-gray-400 me-n1">
															<span class="path1"></span>
															<span class="path2"></span>
															<span class="path3"></span>
															<span class="path4"></span>
														</i>
													</button>
													<!--begin::Menu 2-->
													<div class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-800 menu-state-bg-light-primary fw-semibold w-200px" data-kt-menu="true">
														<!--begin::Menu item-->
														<div class="menu-item px-3">
															<div class="menu-content fs-6 text-dark fw-bold px-3 py-4">Quick Actions</div>
														</div>
														<!--end::Menu item-->
														<!--begin::Menu separator-->
														<div class="separator mb-3 opacity-75"></div>
														<!--end::Menu separator-->
														<!--begin::Menu item-->
														<div class="menu-item px-3">
															<a href="#" class="menu-link px-3">New Ticket</a>
														</div>
														<!--end::Menu item-->
														<!--begin::Menu item-->
														<div class="menu-item px-3">
															<a href="#" class="menu-link px-3">New Customer</a>
														</div>
														<!--end::Menu item-->
														<!--begin::Menu item-->
														<div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="right-start">
															<!--begin::Menu item-->
															<a href="#" class="menu-link px-3">
																<span class="menu-title">New Group</span>
																<span class="menu-arrow"></span>
															</a>
															<!--end::Menu item-->
															<!--begin::Menu sub-->
															<div class="menu-sub menu-sub-dropdown w-175px py-4">
																<!--begin::Menu item-->
																<div class="menu-item px-3">
																	<a href="#" class="menu-link px-3">Admin Group</a>
																</div>
																<!--end::Menu item-->
																<!--begin::Menu item-->
																<div class="menu-item px-3">
																	<a href="#" class="menu-link px-3">Staff Group</a>
																</div>
																<!--end::Menu item-->
																<!--begin::Menu item-->
																<div class="menu-item px-3">
																	<a href="#" class="menu-link px-3">Member Group</a>
																</div>
																<!--end::Menu item-->
															</div>
															<!--end::Menu sub-->
														</div>
														<!--end::Menu item-->
														<!--begin::Menu item-->
														<div class="menu-item px-3">
															<a href="#" class="menu-link px-3">New Contact</a>
														</div>
														<!--end::Menu item-->
														<!--begin::Menu separator-->
														<div class="separator mt-3 opacity-75"></div>
														<!--end::Menu separator-->
														<!--begin::Menu item-->
														<div class="menu-item px-3">
															<div class="menu-content px-3 py-3">
																<a class="btn btn-primary btn-sm px-4" href="#">Generate Reports</a>
															</div>
														</div>
														<!--end::Menu item-->
													</div>
													<!--end::Menu 2-->
													<!--end::Menu-->
												</div>
												<!--end::Toolbar-->
											</div>
											<!--end::Header-->
											<!--begin::Body-->
											<div class="card-body pt-5 ps-6">
												<div id="kt_charts_widget_5" class="min-h-auto" style="min-height: 365px;"><div id="apexchartsclmwu2nk" class="apexcharts-canvas apexchartsclmwu2nk apexcharts-theme-light" style="width: 778px; height: 350px;"><svg id="SvgjsSvg1098" width="778" height="350" xmlns="http://www.w3.org/2000/svg" version="1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:svgjs="http://svgjs.dev" class="apexcharts-svg" xmlns:data="ApexChartsNS" transform="translate(0, 0)" style="background: transparent;"><foreignObject x="0" y="0" width="778" height="350"><div class="apexcharts-legend" xmlns="http://www.w3.org/1999/xhtml" style="max-height: 175px;"></div></foreignObject><g id="SvgjsG1100" class="apexcharts-inner apexcharts-graphical" transform="translate(110, 30)"><defs id="SvgjsDefs1099"><linearGradient id="SvgjsLinearGradient1103" x1="0" y1="0" x2="0" y2="1"><stop id="SvgjsStop1104" stop-opacity="0.4" stop-color="rgba(216,227,240,0.4)" offset="0"></stop><stop id="SvgjsStop1105" stop-opacity="0.5" stop-color="rgba(190,209,230,0.5)" offset="1"></stop><stop id="SvgjsStop1106" stop-opacity="0.5" stop-color="rgba(190,209,230,0.5)" offset="1"></stop></linearGradient><clipPath id="gridRectMaskclmwu2nk"><rect id="SvgjsRect1108" width="645.5" height="279.11199999999997" x="-2" y="0" rx="0" ry="0" opacity="1" stroke-width="0" stroke="none" stroke-dasharray="0" fill="#fff"></rect></clipPath><clipPath id="forecastMaskclmwu2nk"></clipPath><clipPath id="nonForecastMaskclmwu2nk"></clipPath><clipPath id="gridRectMarkerMaskclmwu2nk"><rect id="SvgjsRect1109" width="645.5" height="283.11199999999997" x="-2" y="-2" rx="0" ry="0" opacity="1" stroke-width="0" stroke="none" stroke-dasharray="0" fill="#fff"></rect></clipPath></defs><rect id="SvgjsRect1107" width="0" height="279.11199999999997" x="0" y="0" rx="0" ry="0" opacity="1" stroke-width="0" stroke-dasharray="3" fill="url(#SvgjsLinearGradient1103)" class="apexcharts-xcrosshairs" y2="279.11199999999997" filter="none" fill-opacity="0.9"></rect><line id="SvgjsLine1134" x1="0" y1="280.11199999999997" x2="0" y2="286.11199999999997" stroke="#e0e0e0" stroke-dasharray="0" stroke-linecap="butt" class="apexcharts-xaxis-tick"></line><line id="SvgjsLine1136" x1="160.675" y1="280.11199999999997" x2="160.675" y2="286.11199999999997" stroke="#e0e0e0" stroke-dasharray="0" stroke-linecap="butt" class="apexcharts-xaxis-tick"></line><line id="SvgjsLine1138" x1="321.35" y1="280.11199999999997" x2="321.35" y2="286.11199999999997" stroke="#e0e0e0" stroke-dasharray="0" stroke-linecap="butt" class="apexcharts-xaxis-tick"></line><line id="SvgjsLine1140" x1="482.02500000000003" y1="280.11199999999997" x2="482.02500000000003" y2="286.11199999999997" stroke="#e0e0e0" stroke-dasharray="0" stroke-linecap="butt" class="apexcharts-xaxis-tick"></line><line id="SvgjsLine1142" x1="642.7" y1="280.11199999999997" x2="642.7" y2="286.11199999999997" stroke="#e0e0e0" stroke-dasharray="0" stroke-linecap="butt" class="apexcharts-xaxis-tick"></line><g id="SvgjsG1129" class="apexcharts-grid"><g id="SvgjsG1130" class="apexcharts-gridlines-horizontal"></g><g id="SvgjsG1131" class="apexcharts-gridlines-vertical"><line id="SvgjsLine1135" x1="160.675" y1="0" x2="160.675" y2="279.11199999999997" stroke="#dbdfe9" stroke-dasharray="4" stroke-linecap="butt" class="apexcharts-gridline"></line><line id="SvgjsLine1137" x1="321.35" y1="0" x2="321.35" y2="279.11199999999997" stroke="#dbdfe9" stroke-dasharray="4" stroke-linecap="butt" class="apexcharts-gridline"></line><line id="SvgjsLine1139" x1="482.02500000000003" y1="0" x2="482.02500000000003" y2="279.11199999999997" stroke="#dbdfe9" stroke-dasharray="4" stroke-linecap="butt" class="apexcharts-gridline"></line></g><line id="SvgjsLine1144" x1="0" y1="279.11199999999997" x2="641.5" y2="279.11199999999997" stroke="transparent" stroke-dasharray="0" stroke-linecap="butt"></line><line id="SvgjsLine1143" x1="0" y1="1" x2="0" y2="279.11199999999997" stroke="transparent" stroke-dasharray="0" stroke-linecap="butt"></line></g><g id="SvgjsG1110" class="apexcharts-bar-series apexcharts-plot-series"><g id="SvgjsG1111" class="apexcharts-series" rel="1" seriesName="series-1" data:realIndex="0"><path id="SvgjsPath1116" d="M 4.101 8.436571428571426 L 597.50725 8.436571428571426 C 599.50725 8.436571428571426 601.50725 10.436571428571426 601.50725 12.436571428571426 L 601.50725 27.436571428571426 C 601.50725 29.436571428571426 599.50725 31.436571428571426 597.50725 31.436571428571426 L 4.101 31.436571428571426 C 2.101 31.436571428571426 0.101 29.436571428571426 0.101 27.436571428571426 L 0.101 12.436571428571426 C 0.101 10.436571428571426 2.101 8.436571428571426 4.101 8.436571428571426 Z " fill="rgba(62,151,255,0.85)" fill-opacity="1" stroke-opacity="1" stroke-linecap="round" stroke-width="0" stroke-dasharray="0" class="apexcharts-bar-area" index="0" clip-path="url(#gridRectMaskclmwu2nk)" pathTo="M 4.101 8.436571428571426 L 597.50725 8.436571428571426 C 599.50725 8.436571428571426 601.50725 10.436571428571426 601.50725 12.436571428571426 L 601.50725 27.436571428571426 C 601.50725 29.436571428571426 599.50725 31.436571428571426 597.50725 31.436571428571426 L 4.101 31.436571428571426 C 2.101 31.436571428571426 0.101 29.436571428571426 0.101 27.436571428571426 L 0.101 12.436571428571426 C 0.101 10.436571428571426 2.101 8.436571428571426 4.101 8.436571428571426 Z " pathFrom="M 0.101 8.436571428571426 L 0.101 8.436571428571426 L 0.101 31.436571428571426 L 0.101 31.436571428571426 L 0.101 31.436571428571426 L 0.101 31.436571428571426 L 0.101 31.436571428571426 L 0.101 8.436571428571426 Z" cy="48.30971428571428" cx="601.50625" j="0" val="15" barHeight="23" barWidth="601.40625"></path><path id="SvgjsPath1118" d="M 4.101 48.30971428571428 L 477.226 48.30971428571428 C 479.226 48.30971428571428 481.226 50.30971428571428 481.226 52.30971428571428 L 481.226 67.30971428571428 C 481.226 69.30971428571428 479.226 71.30971428571428 477.226 71.30971428571428 L 4.101 71.30971428571428 C 2.101 71.30971428571428 0.101 69.30971428571428 0.101 67.30971428571428 L 0.101 52.30971428571428 C 0.101 50.30971428571428 2.101 48.30971428571428 4.101 48.30971428571428 Z " fill="rgba(241,65,108,0.85)" fill-opacity="1" stroke-opacity="1" stroke-linecap="round" stroke-width="0" stroke-dasharray="0" class="apexcharts-bar-area" index="0" clip-path="url(#gridRectMaskclmwu2nk)" pathTo="M 4.101 48.30971428571428 L 477.226 48.30971428571428 C 479.226 48.30971428571428 481.226 50.30971428571428 481.226 52.30971428571428 L 481.226 67.30971428571428 C 481.226 69.30971428571428 479.226 71.30971428571428 477.226 71.30971428571428 L 4.101 71.30971428571428 C 2.101 71.30971428571428 0.101 69.30971428571428 0.101 67.30971428571428 L 0.101 52.30971428571428 C 0.101 50.30971428571428 2.101 48.30971428571428 4.101 48.30971428571428 Z " pathFrom="M 0.101 48.30971428571428 L 0.101 48.30971428571428 L 0.101 71.30971428571428 L 0.101 71.30971428571428 L 0.101 71.30971428571428 L 0.101 71.30971428571428 L 0.101 71.30971428571428 L 0.101 48.30971428571428 Z" cy="88.18285714285713" cx="481.225" j="1" val="12" barHeight="23" barWidth="481.125"></path><path id="SvgjsPath1120" d="M 4.101 88.18285714285713 L 397.0385 88.18285714285713 C 399.0385 88.18285714285713 401.0385 90.18285714285713 401.0385 92.18285714285713 L 401.0385 107.18285714285713 C 401.0385 109.18285714285713 399.0385 111.18285714285713 397.0385 111.18285714285713 L 4.101 111.18285714285713 C 2.101 111.18285714285713 0.101 109.18285714285713 0.101 107.18285714285713 L 0.101 92.18285714285713 C 0.101 90.18285714285713 2.101 88.18285714285713 4.101 88.18285714285713 Z " fill="rgba(80,205,137,0.85)" fill-opacity="1" stroke-opacity="1" stroke-linecap="round" stroke-width="0" stroke-dasharray="0" class="apexcharts-bar-area" index="0" clip-path="url(#gridRectMaskclmwu2nk)" pathTo="M 4.101 88.18285714285713 L 397.0385 88.18285714285713 C 399.0385 88.18285714285713 401.0385 90.18285714285713 401.0385 92.18285714285713 L 401.0385 107.18285714285713 C 401.0385 109.18285714285713 399.0385 111.18285714285713 397.0385 111.18285714285713 L 4.101 111.18285714285713 C 2.101 111.18285714285713 0.101 109.18285714285713 0.101 107.18285714285713 L 0.101 92.18285714285713 C 0.101 90.18285714285713 2.101 88.18285714285713 4.101 88.18285714285713 Z " pathFrom="M 0.101 88.18285714285713 L 0.101 88.18285714285713 L 0.101 111.18285714285713 L 0.101 111.18285714285713 L 0.101 111.18285714285713 L 0.101 111.18285714285713 L 0.101 111.18285714285713 L 0.101 88.18285714285713 Z" cy="128.05599999999998" cx="401.0375" j="2" val="10" barHeight="23" barWidth="400.9375"></path><path id="SvgjsPath1122" d="M 4.101 128.05599999999998 L 316.851 128.05599999999998 C 318.851 128.05599999999998 320.851 130.05599999999998 320.851 132.05599999999998 L 320.851 147.05599999999998 C 320.851 149.05599999999998 318.851 151.05599999999998 316.851 151.05599999999998 L 4.101 151.05599999999998 C 2.101 151.05599999999998 0.101 149.05599999999998 0.101 147.05599999999998 L 0.101 132.05599999999998 C 0.101 130.05599999999998 2.101 128.05599999999998 4.101 128.05599999999998 Z " fill="rgba(255,199,0,0.85)" fill-opacity="1" stroke-opacity="1" stroke-linecap="round" stroke-width="0" stroke-dasharray="0" class="apexcharts-bar-area" index="0" clip-path="url(#gridRectMaskclmwu2nk)" pathTo="M 4.101 128.05599999999998 L 316.851 128.05599999999998 C 318.851 128.05599999999998 320.851 130.05599999999998 320.851 132.05599999999998 L 320.851 147.05599999999998 C 320.851 149.05599999999998 318.851 151.05599999999998 316.851 151.05599999999998 L 4.101 151.05599999999998 C 2.101 151.05599999999998 0.101 149.05599999999998 0.101 147.05599999999998 L 0.101 132.05599999999998 C 0.101 130.05599999999998 2.101 128.05599999999998 4.101 128.05599999999998 Z " pathFrom="M 0.101 128.05599999999998 L 0.101 128.05599999999998 L 0.101 151.05599999999998 L 0.101 151.05599999999998 L 0.101 151.05599999999998 L 0.101 151.05599999999998 L 0.101 151.05599999999998 L 0.101 128.05599999999998 Z" cy="167.92914285714284" cx="320.85" j="3" val="8" barHeight="23" barWidth="320.75"></path><path id="SvgjsPath1124" d="M 4.101 167.92914285714284 L 276.75725 167.92914285714284 C 278.75725 167.92914285714284 280.75725 169.92914285714284 280.75725 171.92914285714284 L 280.75725 186.92914285714284 C 280.75725 188.92914285714284 278.75725 190.92914285714284 276.75725 190.92914285714284 L 4.101 190.92914285714284 C 2.101 190.92914285714284 0.101 188.92914285714284 0.101 186.92914285714284 L 0.101 171.92914285714284 C 0.101 169.92914285714284 2.101 167.92914285714284 4.101 167.92914285714284 Z " fill="rgba(114,57,234,0.85)" fill-opacity="1" stroke-opacity="1" stroke-linecap="round" stroke-width="0" stroke-dasharray="0" class="apexcharts-bar-area" index="0" clip-path="url(#gridRectMaskclmwu2nk)" pathTo="M 4.101 167.92914285714284 L 276.75725 167.92914285714284 C 278.75725 167.92914285714284 280.75725 169.92914285714284 280.75725 171.92914285714284 L 280.75725 186.92914285714284 C 280.75725 188.92914285714284 278.75725 190.92914285714284 276.75725 190.92914285714284 L 4.101 190.92914285714284 C 2.101 190.92914285714284 0.101 188.92914285714284 0.101 186.92914285714284 L 0.101 171.92914285714284 C 0.101 169.92914285714284 2.101 167.92914285714284 4.101 167.92914285714284 Z " pathFrom="M 0.101 167.92914285714284 L 0.101 167.92914285714284 L 0.101 190.92914285714284 L 0.101 190.92914285714284 L 0.101 190.92914285714284 L 0.101 190.92914285714284 L 0.101 190.92914285714284 L 0.101 167.92914285714284 Z" cy="207.8022857142857" cx="280.75625" j="4" val="7" barHeight="23" barWidth="280.65625"></path><path id="SvgjsPath1126" d="M 4.101 207.8022857142857 L 156.476 207.8022857142857 C 158.476 207.8022857142857 160.476 209.8022857142857 160.476 211.8022857142857 L 160.476 226.8022857142857 C 160.476 228.8022857142857 158.476 230.8022857142857 156.476 230.8022857142857 L 4.101 230.8022857142857 C 2.101 230.8022857142857 0.101 228.8022857142857 0.101 226.8022857142857 L 0.101 211.8022857142857 C 0.101 209.8022857142857 2.101 207.8022857142857 4.101 207.8022857142857 Z " fill="rgba(80,205,205,0.85)" fill-opacity="1" stroke-opacity="1" stroke-linecap="round" stroke-width="0" stroke-dasharray="0" class="apexcharts-bar-area" index="0" clip-path="url(#gridRectMaskclmwu2nk)" pathTo="M 4.101 207.8022857142857 L 156.476 207.8022857142857 C 158.476 207.8022857142857 160.476 209.8022857142857 160.476 211.8022857142857 L 160.476 226.8022857142857 C 160.476 228.8022857142857 158.476 230.8022857142857 156.476 230.8022857142857 L 4.101 230.8022857142857 C 2.101 230.8022857142857 0.101 228.8022857142857 0.101 226.8022857142857 L 0.101 211.8022857142857 C 0.101 209.8022857142857 2.101 207.8022857142857 4.101 207.8022857142857 Z " pathFrom="M 0.101 207.8022857142857 L 0.101 207.8022857142857 L 0.101 230.8022857142857 L 0.101 230.8022857142857 L 0.101 230.8022857142857 L 0.101 230.8022857142857 L 0.101 230.8022857142857 L 0.101 207.8022857142857 Z" cy="247.67542857142854" cx="160.475" j="5" val="4" barHeight="23" barWidth="160.375"></path><path id="SvgjsPath1128" d="M 4.101 247.67542857142854 L 116.38225 247.67542857142854 C 118.38225 247.67542857142854 120.38225 249.67542857142854 120.38225 251.67542857142854 L 120.38225 266.67542857142854 C 120.38225 268.67542857142854 118.38225 270.67542857142854 116.38225 270.67542857142854 L 4.101 270.67542857142854 C 2.101 270.67542857142854 0.101 268.67542857142854 0.101 266.67542857142854 L 0.101 251.67542857142854 C 0.101 249.67542857142854 2.101 247.67542857142854 4.101 247.67542857142854 Z " fill="rgba(63,66,84,0.85)" fill-opacity="1" stroke-opacity="1" stroke-linecap="round" stroke-width="0" stroke-dasharray="0" class="apexcharts-bar-area" index="0" clip-path="url(#gridRectMaskclmwu2nk)" pathTo="M 4.101 247.67542857142854 L 116.38225 247.67542857142854 C 118.38225 247.67542857142854 120.38225 249.67542857142854 120.38225 251.67542857142854 L 120.38225 266.67542857142854 C 120.38225 268.67542857142854 118.38225 270.67542857142854 116.38225 270.67542857142854 L 4.101 270.67542857142854 C 2.101 270.67542857142854 0.101 268.67542857142854 0.101 266.67542857142854 L 0.101 251.67542857142854 C 0.101 249.67542857142854 2.101 247.67542857142854 4.101 247.67542857142854 Z " pathFrom="M 0.101 247.67542857142854 L 0.101 247.67542857142854 L 0.101 270.67542857142854 L 0.101 270.67542857142854 L 0.101 270.67542857142854 L 0.101 270.67542857142854 L 0.101 270.67542857142854 L 0.101 247.67542857142854 Z" cy="287.5485714285714" cx="120.38125" j="6" val="3" barHeight="23" barWidth="120.28125"></path><g id="SvgjsG1113" class="apexcharts-bar-goals-markers" style="pointer-events: none"><g id="SvgjsG1115" className="apexcharts-bar-goals-groups" class="apexcharts-hidden-element-shown" clip-path="url(#gridRectMarkerMaskclmwu2nk)"></g><g id="SvgjsG1117" className="apexcharts-bar-goals-groups" class="apexcharts-hidden-element-shown" clip-path="url(#gridRectMarkerMaskclmwu2nk)"></g><g id="SvgjsG1119" className="apexcharts-bar-goals-groups" class="apexcharts-hidden-element-shown" clip-path="url(#gridRectMarkerMaskclmwu2nk)"></g><g id="SvgjsG1121" className="apexcharts-bar-goals-groups" class="apexcharts-hidden-element-shown" clip-path="url(#gridRectMarkerMaskclmwu2nk)"></g><g id="SvgjsG1123" className="apexcharts-bar-goals-groups" class="apexcharts-hidden-element-shown" clip-path="url(#gridRectMarkerMaskclmwu2nk)"></g><g id="SvgjsG1125" className="apexcharts-bar-goals-groups" class="apexcharts-hidden-element-shown" clip-path="url(#gridRectMarkerMaskclmwu2nk)"></g><g id="SvgjsG1127" className="apexcharts-bar-goals-groups" class="apexcharts-hidden-element-shown" clip-path="url(#gridRectMarkerMaskclmwu2nk)"></g></g><g id="SvgjsG1114" class="apexcharts-bar-shadows apexcharts-hidden-element-shown" style="pointer-events: none"></g></g><g id="SvgjsG1112" class="apexcharts-datalabels apexcharts-hidden-element-shown" data:realIndex="0"></g></g><g id="SvgjsG1132" class="apexcharts-grid-borders"><line id="SvgjsLine1133" x1="0" y1="0" x2="0" y2="279.11199999999997" stroke="#dbdfe9" stroke-dasharray="4" stroke-linecap="butt" class="apexcharts-gridline"></line><line id="SvgjsLine1141" x1="642.7" y1="0" x2="642.7" y2="279.11199999999997" stroke="#dbdfe9" stroke-dasharray="4" stroke-linecap="butt" class="apexcharts-gridline"></line></g><line id="SvgjsLine1145" x1="0" y1="0" x2="641.5" y2="0" stroke="#b6b6b6" stroke-dasharray="0" stroke-width="1" stroke-linecap="butt" class="apexcharts-ycrosshairs"></line><line id="SvgjsLine1146" x1="0" y1="0" x2="641.5" y2="0" stroke-dasharray="0" stroke-width="0" stroke-linecap="butt" class="apexcharts-ycrosshairs-hidden"></line><g id="SvgjsG1164" class="apexcharts-yaxis apexcharts-xaxis-inversed" rel="0"><g id="SvgjsG1165" class="apexcharts-yaxis-texts-g apexcharts-xaxis-inversed-texts-g" transform="translate(-90, 0)"><text id="SvgjsText1167" font-family="Helvetica, Arial, sans-serif" x="0" y="23.748987012987012" text-anchor="start" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#252f4a" class="apexcharts-text apexcharts-yaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1168">Phones</tspan><title>Phones</title></text><text id="SvgjsText1170" font-family="Helvetica, Arial, sans-serif" x="0" y="63.62212987012987" text-anchor="start" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#252f4a" class="apexcharts-text apexcharts-yaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1171">Laptops</tspan><title>Laptops</title></text><text id="SvgjsText1173" font-family="Helvetica, Arial, sans-serif" x="0" y="103.49527272727272" text-anchor="start" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#252f4a" class="apexcharts-text apexcharts-yaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1174">Headsets</tspan><title>Headsets</title></text><text id="SvgjsText1176" font-family="Helvetica, Arial, sans-serif" x="0" y="143.36841558441557" text-anchor="start" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#252f4a" class="apexcharts-text apexcharts-yaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1177">Games</tspan><title>Games</title></text><text id="SvgjsText1179" font-family="Helvetica, Arial, sans-serif" x="0" y="183.24155844155842" text-anchor="start" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#252f4a" class="apexcharts-text apexcharts-yaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1180">Keyboardsy</tspan><title>Keyboardsy</title></text><text id="SvgjsText1182" font-family="Helvetica, Arial, sans-serif" x="0" y="223.11470129870128" text-anchor="start" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#252f4a" class="apexcharts-text apexcharts-yaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1183">Monitors</tspan><title>Monitors</title></text><text id="SvgjsText1185" font-family="Helvetica, Arial, sans-serif" x="0" y="262.98784415584413" text-anchor="start" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#252f4a" class="apexcharts-text apexcharts-yaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1186">Speakers</tspan><title>Speakers</title></text></g></g><g id="SvgjsG1147" class="apexcharts-xaxis apexcharts-yaxis-inversed"><g id="SvgjsG1148" class="apexcharts-xaxis-texts-g" transform="translate(0, -9.333333333333334)"><text id="SvgjsText1149" font-family="Helvetica, Arial, sans-serif" x="641.5" y="309.11199999999997" text-anchor="middle" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#b5b5c3" class="apexcharts-text apexcharts-xaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1151">16K</tspan><title>16K</title></text><text id="SvgjsText1152" font-family="Helvetica, Arial, sans-serif" x="481.025" y="309.11199999999997" text-anchor="middle" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#b5b5c3" class="apexcharts-text apexcharts-xaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1154">12K</tspan><title>12K</title></text><text id="SvgjsText1155" font-family="Helvetica, Arial, sans-serif" x="320.55000000000007" y="309.11199999999997" text-anchor="middle" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#b5b5c3" class="apexcharts-text apexcharts-xaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1157">8K</tspan><title>8K</title></text><text id="SvgjsText1158" font-family="Helvetica, Arial, sans-serif" x="160.07500000000005" y="309.11199999999997" text-anchor="middle" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#b5b5c3" class="apexcharts-text apexcharts-xaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1160">4K</tspan><title>4K</title></text><text id="SvgjsText1161" font-family="Helvetica, Arial, sans-serif" x="-0.39999999999997726" y="309.11199999999997" text-anchor="middle" dominant-baseline="auto" font-size="14px" font-weight="600" fill="#b5b5c3" class="apexcharts-text apexcharts-xaxis-label " style="font-family: Helvetica, Arial, sans-serif;"><tspan id="SvgjsTspan1163">0K</tspan><title>0K</title></text></g></g><g id="SvgjsG1187" class="apexcharts-yaxis-annotations"></g><g id="SvgjsG1188" class="apexcharts-xaxis-annotations"></g><g id="SvgjsG1189" class="apexcharts-point-annotations"></g></g></svg><div class="apexcharts-tooltip apexcharts-theme-light"><div class="apexcharts-tooltip-title" style="font-family: Helvetica, Arial, sans-serif; font-size: 12px;"></div><div class="apexcharts-tooltip-series-group" style="order: 1;"><span class="apexcharts-tooltip-marker" style="background-color: rgb(62, 151, 255);"></span><div class="apexcharts-tooltip-text" style="font-family: Helvetica, Arial, sans-serif; font-size: 12px;"><div class="apexcharts-tooltip-y-group"><span class="apexcharts-tooltip-text-y-label"></span><span class="apexcharts-tooltip-text-y-value"></span></div><div class="apexcharts-tooltip-goals-group"><span class="apexcharts-tooltip-text-goals-label"></span><span class="apexcharts-tooltip-text-goals-value"></span></div><div class="apexcharts-tooltip-z-group"><span class="apexcharts-tooltip-text-z-label"></span><span class="apexcharts-tooltip-text-z-value"></span></div></div></div></div><div class="apexcharts-yaxistooltip apexcharts-yaxistooltip-0 apexcharts-yaxistooltip-left apexcharts-theme-light"><div class="apexcharts-yaxistooltip-text"></div></div></div></div>
											</div>
											<!--end::Body-->
										</div>
										<!--end::Chart widget 5-->
									</div>
									<!--end::Col-->
									<!--begin::Col-->
									<div class="col-xl-4 mb-5 mb-xl-10">
										<!--begin::Engage widget 1-->
										<div class="card h-md-100" dir="ltr">
											<!--begin::Body-->
											<div class="card-body d-flex flex-column flex-center">
												<!--begin::Heading-->
												<div class="mb-2">
													<!--begin::Title-->
													<h1 class="fw-semibold text-gray-800 text-center lh-lg">Have you tried
													<br>new
													<span class="fw-bolder">Mobile Application ?</span></h1>
													<!--end::Title-->
													<!--begin::Illustration-->
													<div class="py-10 text-center">
														<img src="assets/media/svg/illustrations/easy/1.svg" class="theme-light-show w-200px" alt="">
														<img src="assets/media/svg/illustrations/easy/1-dark.svg" class="theme-dark-show w-200px" alt="">
													</div>
													<!--end::Illustration-->
												</div>
												<!--end::Heading-->
												<!--begin::Links-->
												<div class="text-center mb-1">
													<!--begin::Link-->
													<a class="btn btn-sm btn-primary me-2" data-bs-target="#kt_modal_create_app" data-bs-toggle="modal">Try now</a>
													<!--end::Link-->
													<!--begin::Link-->
													<a class="btn btn-sm btn-light" href="../../demo10/dist/apps/invoices/view/invoice-1.html">Learn more</a>
													<!--end::Link-->
												</div>
												<!--end::Links-->
											</div>
											<!--end::Body-->
										</div>
										<!--end::Engage widget 1-->
									</div>
									<!--end::Col-->
								</div>
								<!--end::Row-->
								<!--begin::Row-->
								<div class="row gy-5 g-xl-10">
									<!--begin::Col-->
									<div class="col-xl-4">
										<!--begin::List widget 5-->
										<div class="card card-flush h-xl-100">
											<!--begin::Header-->
											<div class="card-header pt-7">
												<!--begin::Title-->
												<h3 class="card-title align-items-start flex-column">
													<span class="card-label fw-bold text-dark">Product Delivery</span>
													<span class="text-gray-400 mt-1 fw-semibold fs-6">1M Products Shipped so far</span>
												</h3>
												<!--end::Title-->
												<!--begin::Toolbar-->
												<div class="card-toolbar">
													<a href="../../demo10/dist/apps/ecommerce/sales/details.html" class="btn btn-sm btn-light">Order Details</a>
												</div>
												<!--end::Toolbar-->
											</div>
											<!--end::Header-->
											<!--begin::Body-->
											<div class="card-body">
												<!--begin::Scroll-->
												<div class="hover-scroll-overlay-y pe-6 me-n6" style="height: 415px">
													<!--begin::Item-->
													<div class="border border-dashed border-gray-300 rounded px-7 py-3 mb-6">
														<!--begin::Info-->
														<div class="d-flex flex-stack mb-3">
															<!--begin::Wrapper-->
															<div class="me-3">
																<!--begin::Icon-->
																<img src="assets/media/stock/ecommerce/210.png" class="w-50px ms-n1 me-1" alt="">
																<!--end::Icon-->
																<!--begin::Title-->
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-gray-800 text-hover-primary fw-bold">Elephant 1802</a>
																<!--end::Title-->
															</div>
															<!--end::Wrapper-->
															<!--begin::Action-->
															<div class="m-0">
																<!--begin::Menu-->
																<button class="btn btn-icon btn-color-gray-400 btn-active-color-primary justify-content-end" data-kt-menu-trigger="click" data-kt-menu-placement="bottom-end" data-kt-menu-overflow="true">
																	<i class="ki-duotone ki-dots-square fs-1">
																		<span class="path1"></span>
																		<span class="path2"></span>
																		<span class="path3"></span>
																		<span class="path4"></span>
																	</i>
																</button>
																<!--begin::Menu 2-->
																<div class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-800 menu-state-bg-light-primary fw-semibold w-200px" data-kt-menu="true">
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content fs-6 text-dark fw-bold px-3 py-4">Quick Actions</div>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mb-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Ticket</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Customer</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="right-start">
																		<!--begin::Menu item-->
																		<a href="#" class="menu-link px-3">
																			<span class="menu-title">New Group</span>
																			<span class="menu-arrow"></span>
																		</a>
																		<!--end::Menu item-->
																		<!--begin::Menu sub-->
																		<div class="menu-sub menu-sub-dropdown w-175px py-4">
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Admin Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Staff Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Member Group</a>
																			</div>
																			<!--end::Menu item-->
																		</div>
																		<!--end::Menu sub-->
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Contact</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mt-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content px-3 py-3">
																			<a class="btn btn-primary btn-sm px-4" href="#">Generate Reports</a>
																		</div>
																	</div>
																	<!--end::Menu item-->
																</div>
																<!--end::Menu 2-->
																<!--end::Menu-->
															</div>
															<!--end::Action-->
														</div>
														<!--end::Info-->
														<!--begin::Customer-->
														<div class="d-flex flex-stack">
															<!--begin::Name-->
															<span class="text-gray-400 fw-bold">To:
															<a href="../../demo10/dist/apps/ecommerce/sales/details.html" class="text-gray-800 text-hover-primary fw-bold">Jason Bourne</a></span>
															<!--end::Name-->
															<!--begin::Label-->
															<span class="badge badge-light-success">Delivered</span>
															<!--end::Label-->
														</div>
														<!--end::Customer-->
													</div>
													<!--end::Item-->
													<!--begin::Item-->
													<div class="border border-dashed border-gray-300 rounded px-7 py-3 mb-6">
														<!--begin::Info-->
														<div class="d-flex flex-stack mb-3">
															<!--begin::Wrapper-->
															<div class="me-3">
																<!--begin::Icon-->
																<img src="assets/media/stock/ecommerce/209.png" class="w-50px ms-n1 me-1" alt="">
																<!--end::Icon-->
																<!--begin::Title-->
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-gray-800 text-hover-primary fw-bold">RiseUP</a>
																<!--end::Title-->
															</div>
															<!--end::Wrapper-->
															<!--begin::Action-->
															<div class="m-0">
																<!--begin::Menu-->
																<button class="btn btn-icon btn-color-gray-400 btn-active-color-primary justify-content-end" data-kt-menu-trigger="click" data-kt-menu-placement="bottom-end" data-kt-menu-overflow="true">
																	<i class="ki-duotone ki-dots-square fs-1">
																		<span class="path1"></span>
																		<span class="path2"></span>
																		<span class="path3"></span>
																		<span class="path4"></span>
																	</i>
																</button>
																<!--begin::Menu 2-->
																<div class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-800 menu-state-bg-light-primary fw-semibold w-200px" data-kt-menu="true">
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content fs-6 text-dark fw-bold px-3 py-4">Quick Actions</div>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mb-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Ticket</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Customer</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="right-start">
																		<!--begin::Menu item-->
																		<a href="#" class="menu-link px-3">
																			<span class="menu-title">New Group</span>
																			<span class="menu-arrow"></span>
																		</a>
																		<!--end::Menu item-->
																		<!--begin::Menu sub-->
																		<div class="menu-sub menu-sub-dropdown w-175px py-4">
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Admin Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Staff Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Member Group</a>
																			</div>
																			<!--end::Menu item-->
																		</div>
																		<!--end::Menu sub-->
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Contact</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mt-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content px-3 py-3">
																			<a class="btn btn-primary btn-sm px-4" href="#">Generate Reports</a>
																		</div>
																	</div>
																	<!--end::Menu item-->
																</div>
																<!--end::Menu 2-->
																<!--end::Menu-->
															</div>
															<!--end::Action-->
														</div>
														<!--end::Info-->
														<!--begin::Customer-->
														<div class="d-flex flex-stack">
															<!--begin::Name-->
															<span class="text-gray-400 fw-bold">To:
															<a href="../../demo10/dist/apps/ecommerce/sales/details.html" class="text-gray-800 text-hover-primary fw-bold">Marie Durant</a></span>
															<!--end::Name-->
															<!--begin::Label-->
															<span class="badge badge-light-primary">Shipping</span>
															<!--end::Label-->
														</div>
														<!--end::Customer-->
													</div>
													<!--end::Item-->
													<!--begin::Item-->
													<div class="border border-dashed border-gray-300 rounded px-7 py-3 mb-6">
														<!--begin::Info-->
														<div class="d-flex flex-stack mb-3">
															<!--begin::Wrapper-->
															<div class="me-3">
																<!--begin::Icon-->
																<img src="assets/media/stock/ecommerce/214.png" class="w-50px ms-n1 me-1" alt="">
																<!--end::Icon-->
																<!--begin::Title-->
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-gray-800 text-hover-primary fw-bold">Yellow Stone</a>
																<!--end::Title-->
															</div>
															<!--end::Wrapper-->
															<!--begin::Action-->
															<div class="m-0">
																<!--begin::Menu-->
																<button class="btn btn-icon btn-color-gray-400 btn-active-color-primary justify-content-end" data-kt-menu-trigger="click" data-kt-menu-placement="bottom-end" data-kt-menu-overflow="true">
																	<i class="ki-duotone ki-dots-square fs-1">
																		<span class="path1"></span>
																		<span class="path2"></span>
																		<span class="path3"></span>
																		<span class="path4"></span>
																	</i>
																</button>
																<!--begin::Menu 2-->
																<div class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-800 menu-state-bg-light-primary fw-semibold w-200px" data-kt-menu="true">
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content fs-6 text-dark fw-bold px-3 py-4">Quick Actions</div>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mb-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Ticket</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Customer</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="right-start">
																		<!--begin::Menu item-->
																		<a href="#" class="menu-link px-3">
																			<span class="menu-title">New Group</span>
																			<span class="menu-arrow"></span>
																		</a>
																		<!--end::Menu item-->
																		<!--begin::Menu sub-->
																		<div class="menu-sub menu-sub-dropdown w-175px py-4">
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Admin Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Staff Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Member Group</a>
																			</div>
																			<!--end::Menu item-->
																		</div>
																		<!--end::Menu sub-->
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Contact</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mt-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content px-3 py-3">
																			<a class="btn btn-primary btn-sm px-4" href="#">Generate Reports</a>
																		</div>
																	</div>
																	<!--end::Menu item-->
																</div>
																<!--end::Menu 2-->
																<!--end::Menu-->
															</div>
															<!--end::Action-->
														</div>
														<!--end::Info-->
														<!--begin::Customer-->
														<div class="d-flex flex-stack">
															<!--begin::Name-->
															<span class="text-gray-400 fw-bold">To:
															<a href="../../demo10/dist/apps/ecommerce/sales/details.html" class="text-gray-800 text-hover-primary fw-bold">Dan Wilson</a></span>
															<!--end::Name-->
															<!--begin::Label-->
															<span class="badge badge-light-danger">Confirmed</span>
															<!--end::Label-->
														</div>
														<!--end::Customer-->
													</div>
													<!--end::Item-->
													<!--begin::Item-->
													<div class="border border-dashed border-gray-300 rounded px-7 py-3 mb-6">
														<!--begin::Info-->
														<div class="d-flex flex-stack mb-3">
															<!--begin::Wrapper-->
															<div class="me-3">
																<!--begin::Icon-->
																<img src="assets/media/stock/ecommerce/211.png" class="w-50px ms-n1 me-1" alt="">
																<!--end::Icon-->
																<!--begin::Title-->
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-gray-800 text-hover-primary fw-bold">Elephant 1802</a>
																<!--end::Title-->
															</div>
															<!--end::Wrapper-->
															<!--begin::Action-->
															<div class="m-0">
																<!--begin::Menu-->
																<button class="btn btn-icon btn-color-gray-400 btn-active-color-primary justify-content-end" data-kt-menu-trigger="click" data-kt-menu-placement="bottom-end" data-kt-menu-overflow="true">
																	<i class="ki-duotone ki-dots-square fs-1">
																		<span class="path1"></span>
																		<span class="path2"></span>
																		<span class="path3"></span>
																		<span class="path4"></span>
																	</i>
																</button>
																<!--begin::Menu 2-->
																<div class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-800 menu-state-bg-light-primary fw-semibold w-200px" data-kt-menu="true">
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content fs-6 text-dark fw-bold px-3 py-4">Quick Actions</div>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mb-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Ticket</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Customer</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="right-start">
																		<!--begin::Menu item-->
																		<a href="#" class="menu-link px-3">
																			<span class="menu-title">New Group</span>
																			<span class="menu-arrow"></span>
																		</a>
																		<!--end::Menu item-->
																		<!--begin::Menu sub-->
																		<div class="menu-sub menu-sub-dropdown w-175px py-4">
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Admin Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Staff Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Member Group</a>
																			</div>
																			<!--end::Menu item-->
																		</div>
																		<!--end::Menu sub-->
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Contact</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mt-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content px-3 py-3">
																			<a class="btn btn-primary btn-sm px-4" href="#">Generate Reports</a>
																		</div>
																	</div>
																	<!--end::Menu item-->
																</div>
																<!--end::Menu 2-->
																<!--end::Menu-->
															</div>
															<!--end::Action-->
														</div>
														<!--end::Info-->
														<!--begin::Customer-->
														<div class="d-flex flex-stack">
															<!--begin::Name-->
															<span class="text-gray-400 fw-bold">To:
															<a href="../../demo10/dist/apps/ecommerce/sales/details.html" class="text-gray-800 text-hover-primary fw-bold">Lebron Wayde</a></span>
															<!--end::Name-->
															<!--begin::Label-->
															<span class="badge badge-light-success">Delivered</span>
															<!--end::Label-->
														</div>
														<!--end::Customer-->
													</div>
													<!--end::Item-->
													<!--begin::Item-->
													<div class="border border-dashed border-gray-300 rounded px-7 py-3 mb-6">
														<!--begin::Info-->
														<div class="d-flex flex-stack mb-3">
															<!--begin::Wrapper-->
															<div class="me-3">
																<!--begin::Icon-->
																<img src="assets/media/stock/ecommerce/215.png" class="w-50px ms-n1 me-1" alt="">
																<!--end::Icon-->
																<!--begin::Title-->
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-gray-800 text-hover-primary fw-bold">RiseUP</a>
																<!--end::Title-->
															</div>
															<!--end::Wrapper-->
															<!--begin::Action-->
															<div class="m-0">
																<!--begin::Menu-->
																<button class="btn btn-icon btn-color-gray-400 btn-active-color-primary justify-content-end" data-kt-menu-trigger="click" data-kt-menu-placement="bottom-end" data-kt-menu-overflow="true">
																	<i class="ki-duotone ki-dots-square fs-1">
																		<span class="path1"></span>
																		<span class="path2"></span>
																		<span class="path3"></span>
																		<span class="path4"></span>
																	</i>
																</button>
																<!--begin::Menu 2-->
																<div class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-800 menu-state-bg-light-primary fw-semibold w-200px" data-kt-menu="true">
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content fs-6 text-dark fw-bold px-3 py-4">Quick Actions</div>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mb-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Ticket</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Customer</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="right-start">
																		<!--begin::Menu item-->
																		<a href="#" class="menu-link px-3">
																			<span class="menu-title">New Group</span>
																			<span class="menu-arrow"></span>
																		</a>
																		<!--end::Menu item-->
																		<!--begin::Menu sub-->
																		<div class="menu-sub menu-sub-dropdown w-175px py-4">
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Admin Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Staff Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Member Group</a>
																			</div>
																			<!--end::Menu item-->
																		</div>
																		<!--end::Menu sub-->
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Contact</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mt-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content px-3 py-3">
																			<a class="btn btn-primary btn-sm px-4" href="#">Generate Reports</a>
																		</div>
																	</div>
																	<!--end::Menu item-->
																</div>
																<!--end::Menu 2-->
																<!--end::Menu-->
															</div>
															<!--end::Action-->
														</div>
														<!--end::Info-->
														<!--begin::Customer-->
														<div class="d-flex flex-stack">
															<!--begin::Name-->
															<span class="text-gray-400 fw-bold">To:
															<a href="../../demo10/dist/apps/ecommerce/sales/details.html" class="text-gray-800 text-hover-primary fw-bold">Ana Simmons</a></span>
															<!--end::Name-->
															<!--begin::Label-->
															<span class="badge badge-light-primary">Shipping</span>
															<!--end::Label-->
														</div>
														<!--end::Customer-->
													</div>
													<!--end::Item-->
													<!--begin::Item-->
													<div class="border border-dashed border-gray-300 rounded px-7 py-3">
														<!--begin::Info-->
														<div class="d-flex flex-stack mb-3">
															<!--begin::Wrapper-->
															<div class="me-3">
																<!--begin::Icon-->
																<img src="assets/media/stock/ecommerce/192.png" class="w-50px ms-n1 me-1" alt="">
																<!--end::Icon-->
																<!--begin::Title-->
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-gray-800 text-hover-primary fw-bold">Yellow Stone</a>
																<!--end::Title-->
															</div>
															<!--end::Wrapper-->
															<!--begin::Action-->
															<div class="m-0">
																<!--begin::Menu-->
																<button class="btn btn-icon btn-color-gray-400 btn-active-color-primary justify-content-end" data-kt-menu-trigger="click" data-kt-menu-placement="bottom-end" data-kt-menu-overflow="true">
																	<i class="ki-duotone ki-dots-square fs-1">
																		<span class="path1"></span>
																		<span class="path2"></span>
																		<span class="path3"></span>
																		<span class="path4"></span>
																	</i>
																</button>
																<!--begin::Menu 2-->
																<div class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-800 menu-state-bg-light-primary fw-semibold w-200px" data-kt-menu="true">
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content fs-6 text-dark fw-bold px-3 py-4">Quick Actions</div>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mb-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Ticket</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Customer</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="right-start">
																		<!--begin::Menu item-->
																		<a href="#" class="menu-link px-3">
																			<span class="menu-title">New Group</span>
																			<span class="menu-arrow"></span>
																		</a>
																		<!--end::Menu item-->
																		<!--begin::Menu sub-->
																		<div class="menu-sub menu-sub-dropdown w-175px py-4">
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Admin Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Staff Group</a>
																			</div>
																			<!--end::Menu item-->
																			<!--begin::Menu item-->
																			<div class="menu-item px-3">
																				<a href="#" class="menu-link px-3">Member Group</a>
																			</div>
																			<!--end::Menu item-->
																		</div>
																		<!--end::Menu sub-->
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<a href="#" class="menu-link px-3">New Contact</a>
																	</div>
																	<!--end::Menu item-->
																	<!--begin::Menu separator-->
																	<div class="separator mt-3 opacity-75"></div>
																	<!--end::Menu separator-->
																	<!--begin::Menu item-->
																	<div class="menu-item px-3">
																		<div class="menu-content px-3 py-3">
																			<a class="btn btn-primary btn-sm px-4" href="#">Generate Reports</a>
																		</div>
																	</div>
																	<!--end::Menu item-->
																</div>
																<!--end::Menu 2-->
																<!--end::Menu-->
															</div>
															<!--end::Action-->
														</div>
														<!--end::Info-->
														<!--begin::Customer-->
														<div class="d-flex flex-stack">
															<!--begin::Name-->
															<span class="text-gray-400 fw-bold">To:
															<a href="../../demo10/dist/apps/ecommerce/sales/details.html" class="text-gray-800 text-hover-primary fw-bold">Kevin Leonard</a></span>
															<!--end::Name-->
															<!--begin::Label-->
															<span class="badge badge-light-danger">Confirmed</span>
															<!--end::Label-->
														</div>
														<!--end::Customer-->
													</div>
													<!--end::Item-->
												</div>
												<!--end::Scroll-->
											</div>
											<!--end::Body-->
										</div>
										<!--end::List widget 5-->
									</div>
									<!--end::Col-->
									<!--begin::Col-->
									<div class="col-xl-8">
										<!--begin::Table Widget 5-->
										<div class="card card-flush h-xl-100">
											<!--begin::Card header-->
											<div class="card-header pt-7">
												<!--begin::Title-->
												<h3 class="card-title align-items-start flex-column">
													<span class="card-label fw-bold text-dark">Stock Report</span>
													<span class="text-gray-400 mt-1 fw-semibold fs-6">Total 2,356 Items in the Stock</span>
												</h3>
												<!--end::Title-->
												<!--begin::Actions-->
												<div class="card-toolbar">
													<!--begin::Filters-->
													<div class="d-flex flex-stack flex-wrap gap-4">
														<!--begin::Destination-->
														<div class="d-flex align-items-center fw-bold">
															<!--begin::Label-->
															<div class="text-muted fs-7 me-2">Cateogry</div>
															<!--end::Label-->
															<!--begin::Select-->
															<select class="form-select form-select-transparent text-dark fs-7 lh-1 fw-bold py-0 ps-3 w-auto select2-hidden-accessible" data-control="select2" data-hide-search="true" data-dropdown-css-class="w-150px" data-placeholder="Select an option" data-select2-id="select2-data-9-ujh6" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
																<option></option>
																<option value="Show All" selected="selected" data-select2-id="select2-data-11-g083">Show All</option>
																<option value="a">Category A</option>
																<option value="b">Category B</option>
															</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-10-bz5v" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-transparent text-dark fs-7 lh-1 fw-bold py-0 ps-3 w-auto" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-nzke-container" aria-controls="select2-nzke-container"><span class="select2-selection__rendered" id="select2-nzke-container" role="textbox" aria-readonly="true" title="Show All">Show All</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span>
															<!--end::Select-->
														</div>
														<!--end::Destination-->
														<!--begin::Status-->
														<div class="d-flex align-items-center fw-bold">
															<!--begin::Label-->
															<div class="text-muted fs-7 me-2">Status</div>
															<!--end::Label-->
															<!--begin::Select-->
															<select class="form-select form-select-transparent text-dark fs-7 lh-1 fw-bold py-0 ps-3 w-auto select2-hidden-accessible" data-control="select2" data-hide-search="true" data-dropdown-css-class="w-150px" data-placeholder="Select an option" data-kt-table-widget-5="filter_status" data-select2-id="select2-data-12-hl7t" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
																<option></option>
																<option value="Show All" selected="selected" data-select2-id="select2-data-14-efz8">Show All</option>
																<option value="In Stock">In Stock</option>
																<option value="Out of Stock">Out of Stock</option>
																<option value="Low Stock">Low Stock</option>
															</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-13-w275" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-transparent text-dark fs-7 lh-1 fw-bold py-0 ps-3 w-auto" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-9eve-container" aria-controls="select2-9eve-container"><span class="select2-selection__rendered" id="select2-9eve-container" role="textbox" aria-readonly="true" title="Show All">Show All</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span>
															<!--end::Select-->
														</div>
														<!--end::Status-->
														<!--begin::Search-->
														<a href="../../demo10/dist/apps/ecommerce/catalog/products.html" class="btn btn-light btn-sm">View Stock</a>
														<!--end::Search-->
													</div>
													<!--begin::Filters-->
												</div>
												<!--end::Actions-->
											</div>
											<!--end::Card header-->
											<!--begin::Card body-->
											<div class="card-body">
												<!--begin::Table-->
												<div id="kt_table_widget_5_table_wrapper" class="dataTables_wrapper dt-bootstrap4 no-footer"><div class="table-responsive"><table class="table align-middle table-row-dashed fs-6 gy-3 dataTable no-footer" id="kt_table_widget_5_table">
													<!--begin::Table head-->
													<thead>
														<!--begin::Table row-->
														<tr class="text-start text-gray-400 fw-bold fs-7 text-uppercase gs-0"><th class="min-w-150px sorting" tabindex="0" aria-controls="kt_table_widget_5_table" rowspan="1" colspan="1" style="width: 151.167px;" aria-label="Item: activate to sort column ascending">Item</th><th class="text-end pe-3 min-w-100px sorting_disabled" rowspan="1" colspan="1" style="width: 100.933px;" aria-label="Product ID">Product ID</th><th class="text-end pe-3 min-w-150px sorting" tabindex="0" aria-controls="kt_table_widget_5_table" rowspan="1" colspan="1" style="width: 151.317px;" aria-label="Date Added: activate to sort column ascending">Date Added</th><th class="text-end pe-3 min-w-100px sorting" tabindex="0" aria-controls="kt_table_widget_5_table" rowspan="1" colspan="1" style="width: 100.917px;" aria-label="Price: activate to sort column ascending">Price</th><th class="text-end pe-3 min-w-100px sorting" tabindex="0" aria-controls="kt_table_widget_5_table" rowspan="1" colspan="1" style="width: 100.933px;" aria-label="Status: activate to sort column ascending">Status</th><th class="text-end pe-0 min-w-75px sorting" tabindex="0" aria-controls="kt_table_widget_5_table" rowspan="1" colspan="1" style="width: 75.65px;" aria-label="Qty: activate to sort column ascending">Qty</th></tr>
														<!--end::Table row-->
													</thead>
													<!--end::Table head-->
													<!--begin::Table body-->
													<tbody class="fw-bold text-gray-600">







													<tr class="odd">
															<!--begin::Item-->
															<td>
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-dark text-hover-primary">Macbook Air M1</a>
															</td>
															<!--end::Item-->
															<!--begin::Product ID-->
															<td class="text-end">#XGY-356</td>
															<!--end::Product ID-->
															<!--begin::Date added-->
															<td class="text-end" data-order="2023-04-20T00:00:00+05:30">02 Apr, 2023</td>
															<!--end::Date added-->
															<!--begin::Price-->
															<td class="text-end">$1,230</td>
															<!--end::Price-->
															<!--begin::Status-->
															<td class="text-end">
																<span class="badge py-3 px-4 fs-7 badge-light-primary">In Stock</span>
															</td>
															<!--end::Status-->
															<!--begin::Qty-->
															<td class="text-end" data-order="58">
																<span class="text-dark fw-bold">58 PCS</span>
															</td>
															<!--end::Qty-->
														</tr><tr class="even">
															<!--begin::Item-->
															<td>
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-dark text-hover-primary">Surface Laptop 4</a>
															</td>
															<!--end::Item-->
															<!--begin::Product ID-->
															<td class="text-end">#YHD-047</td>
															<!--end::Product ID-->
															<!--begin::Date added-->
															<td class="text-end" data-order="2023-04-20T00:00:00+05:30">01 Apr, 2023</td>
															<!--end::Date added-->
															<!--begin::Price-->
															<td class="text-end">$1,060</td>
															<!--end::Price-->
															<!--begin::Status-->
															<td class="text-end">
																<span class="badge py-3 px-4 fs-7 badge-light-danger">Out of Stock</span>
															</td>
															<!--end::Status-->
															<!--begin::Qty-->
															<td class="text-end" data-order="0">
																<span class="text-dark fw-bold">0 PCS</span>
															</td>
															<!--end::Qty-->
														</tr><tr class="odd">
															<!--begin::Item-->
															<td>
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-dark text-hover-primary">Logitech MX 250</a>
															</td>
															<!--end::Item-->
															<!--begin::Product ID-->
															<td class="text-end">#SRR-678</td>
															<!--end::Product ID-->
															<!--begin::Date added-->
															<td class="text-end" data-order="2023-03-20T00:00:00+05:30">24 Mar, 2023</td>
															<!--end::Date added-->
															<!--begin::Price-->
															<td class="text-end">$64</td>
															<!--end::Price-->
															<!--begin::Status-->
															<td class="text-end">
																<span class="badge py-3 px-4 fs-7 badge-light-primary">In Stock</span>
															</td>
															<!--end::Status-->
															<!--begin::Qty-->
															<td class="text-end" data-order="290">
																<span class="text-dark fw-bold">290 PCS</span>
															</td>
															<!--end::Qty-->
														</tr><tr class="even">
															<!--begin::Item-->
															<td>
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-dark text-hover-primary">AudioEngine HD3</a>
															</td>
															<!--end::Item-->
															<!--begin::Product ID-->
															<td class="text-end">#PXF-578</td>
															<!--end::Product ID-->
															<!--begin::Date added-->
															<td class="text-end" data-order="2023-03-20T00:00:00+05:30">24 Mar, 2023</td>
															<!--end::Date added-->
															<!--begin::Price-->
															<td class="text-end">$1,060</td>
															<!--end::Price-->
															<!--begin::Status-->
															<td class="text-end">
																<span class="badge py-3 px-4 fs-7 badge-light-danger">Out of Stock</span>
															</td>
															<!--end::Status-->
															<!--begin::Qty-->
															<td class="text-end" data-order="46">
																<span class="text-dark fw-bold">46 PCS</span>
															</td>
															<!--end::Qty-->
														</tr><tr class="odd">
															<!--begin::Item-->
															<td>
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-dark text-hover-primary">HP Hyper LTR</a>
															</td>
															<!--end::Item-->
															<!--begin::Product ID-->
															<td class="text-end">#PXF-778</td>
															<!--end::Product ID-->
															<!--begin::Date added-->
															<td class="text-end" data-order="2023-01-20T00:00:00+05:30">16 Jan, 2023</td>
															<!--end::Date added-->
															<!--begin::Price-->
															<td class="text-end">$4500</td>
															<!--end::Price-->
															<!--begin::Status-->
															<td class="text-end">
																<span class="badge py-3 px-4 fs-7 badge-light-primary">In Stock</span>
															</td>
															<!--end::Status-->
															<!--begin::Qty-->
															<td class="text-end" data-order="78">
																<span class="text-dark fw-bold">78 PCS</span>
															</td>
															<!--end::Qty-->
														</tr><tr class="even">
															<!--begin::Item-->
															<td>
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-dark text-hover-primary">Dell 32 UltraSharp</a>
															</td>
															<!--end::Item-->
															<!--begin::Product ID-->
															<td class="text-end">#XGY-356</td>
															<!--end::Product ID-->
															<!--begin::Date added-->
															<td class="text-end" data-order="2023-12-20T00:00:00+05:30">22 Dec, 2023</td>
															<!--end::Date added-->
															<!--begin::Price-->
															<td class="text-end">$1,060</td>
															<!--end::Price-->
															<!--begin::Status-->
															<td class="text-end">
																<span class="badge py-3 px-4 fs-7 badge-light-warning">Low Stock</span>
															</td>
															<!--end::Status-->
															<!--begin::Qty-->
															<td class="text-end" data-order="8">
																<span class="text-dark fw-bold">8 PCS</span>
															</td>
															<!--end::Qty-->
														</tr><tr class="odd">
															<!--begin::Item-->
															<td>
																<a href="../../demo10/dist/apps/ecommerce/catalog/edit-product.html" class="text-dark text-hover-primary">Google Pixel 6 Pro</a>
															</td>
															<!--end::Item-->
															<!--begin::Product ID-->
															<td class="text-end">#XVR-425</td>
															<!--end::Product ID-->
															<!--begin::Date added-->
															<td class="text-end" data-order="2023-12-20T00:00:00+05:30">27 Dec, 2023</td>
															<!--end::Date added-->
															<!--begin::Price-->
															<td class="text-end">$1,060</td>
															<!--end::Price-->
															<!--begin::Status-->
															<td class="text-end">
																<span class="badge py-3 px-4 fs-7 badge-light-primary">In Stock</span>
															</td>
															<!--end::Status-->
															<!--begin::Qty-->
															<td class="text-end" data-order="124">
																<span class="text-dark fw-bold">124 PCS</span>
															</td>
															<!--end::Qty-->
														</tr></tbody>
													<!--end::Table body-->
												</table></div><div class="row"><div class="col-sm-12 col-md-5 d-flex align-items-center justify-content-center justify-content-md-start"></div><div class="col-sm-12 col-md-7 d-flex align-items-center justify-content-center justify-content-md-end"></div></div></div>
												<!--end::Table-->
											</div>
											<!--end::Card body-->
										</div>
										<!--end::Table Widget 5-->
									</div>
									<!--end::Col-->
								</div>
								<!--end::Row-->
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