<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="java.util.Optional" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.QueueCountDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="com.sib.ibanklosucl.model.user.EmployeeNotification" %>

<!DOCTYPE html>
<html lang="en" dir="ltr" class="custom-scrollbars">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="_csrf" content="${_csrf.token}">
	<meta name="_csrf_header" content="${_csrf.headerName}">
	<title>SIB-LOS Dashboard</title>

	<!-- Global stylesheets -->
	<link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">
	<link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
	<link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
	<link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
	<link href="assets/css/custom/dashboard.css" rel="stylesheet" type="text/css">
	<link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
	<link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>

	<style>
		:root {
			--primary-color: #007bff;
			--secondary-color: #6c757d;
			--success-color: #28a745;
			--warning-color: #ffc107;
			--danger-color: #dc3545;
			--light-color: #f8f9fa;
			--dark-color: #343a40;
		}
		.searchBox {
			display: flex;
			max-width: 230px;
			align-items: center;
			justify-content: space-between;
			gap: 8px;
			background: #09092d;
			border-radius: 50px;
			position: relative;
		}
		.searchButton {
			color: white;
			position: absolute;
			right: 8px;
			width: 33px;
			height: 33px;
			border-radius: 50%;
			background: var(--gradient-2, linear-gradient(90deg, #B4405C 0%, #0C529D 100%));
			border: 0;
			display: inline-block;
			transition: all 300ms cubic-bezier(.23, 1, 0.32, 1);
		}
		button:hover {
			color: #fff;
			background-color: #1A1A1A;
			box-shadow: rgba(0, 0, 0, 0.5) 0 10px 20px;
			transform: translateY(-3px);
		}
		button:active {
			box-shadow: none;
			transform: translateY(0);
		}
		.searchInput {
			border: none;
			background: none;
			outline: none;
			color: white;
			font-size: 15px;
			padding: 24px 46px 24px 26px;
		}
		body {
			background-color: var(--light-color);
			font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
		}
		.navbar {
			background-color: var(--dark-color);
		}
		.navbar-brand {
			font-weight: bold;
			color: #ffffff;
		}
		.messagescroll {
			overflow-y: auto;
			scrollbar-width: none;
			-ms-overflow-style: none;
			height: 30vh;
		}
		.marquee1 {
			position: relative;
			box-sizing: border-box;
			animation: marquee1 30s linear infinite;
			margin: auto 5px;
		}
		.status {
			text-decoration: none;
			color: inherit;
			padding: 15px;
			border-radius: 10px;
			transition: background-color 0.3s;
		}
		.status:hover {
			background-color: rgba(0, 123, 255, 0.1);
		}
		.status i {
			font-size: 2rem;
			color: var(--primary-color);
		}
		.status-count {
			font-size: 1.5rem;
			font-weight: bold;
		}
		.status-label {
			font-size: 0.875rem;
			color: var(--secondary-color);
		}
		.news-feed, .notifications {
			background-color: var(--light-color);
			border-radius: 10px;
			padding: 20px;
			box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
		}
		.news-feed h5, .notifications h5 {
			margin-bottom: 15px;
			font-weight: bold;
			color: var(--dark-color);
		}
		.news-feed ul, .notifications ul {
			list-style-type: none;
			padding-left: 0;
		}
		.news-feed li, .notifications li {
			margin-bottom: 15px;
			padding: 10px;
			border-bottom: 1px solid var(--secondary-color);
		}
		.news-feed li:last-child, .notifications li:last-child {
			border-bottom: none;
		}
		.progress-tracking {
			margin-top: 20px;
			padding: 20px;
			background-color: var(--light-color);
			border-radius: 10px;
			box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
		}
		.progress-tracking h5 {
			margin-bottom: 15px;
			font-weight: bold;
			color: var(--dark-color);
		}
		.progress-tracking .progress-bar {
			height: 20px;
			border-radius: 10px;
		}
		.dashboard .row {
			margin-bottom: 20px;
		}
		.dashboard .col-md-6, .dashboard .col-md-4, .dashboard .col-md-8 {
			margin-bottom: 20px;
		}
		.dashboard .col-md-6:last-child, .dashboard .col-md-4:last-child, .dashboard .col-md-8:last-child {
			margin-bottom: 0;
		}

		/* Fix for layout issues */
		.page-content {
			display: flex;
			width: 100%;
		}
		.sidebar-container {
			width: 15%;
			min-width: 250px;
			position: relative;
		}
		.content-wrapper {
			width: 85%;
			flex-grow: 1;
			padding-left: 15px;
		}
		.insurance-calculator-card {
			margin-bottom: 20px;
			box-shadow: rgba(9, 30, 66, 0.25) 0px 4px 8px -2px, rgba(9, 30, 66, 0.08) 0px 0px 0px 1px;
		}
		#kt_content{
			margin-left: 2%;
		}
	</style>

	<%
		Employee employee = new Employee();
		if (request.getAttribute("employee") != null) {
			employee = (Employee) request.getAttribute("employee");
		}
		int parameterCount = 0;
		QueueCountDTO queueCountDTO = null;
		if (request.getAttribute("queueCountDTO") != null) {
			queueCountDTO = (QueueCountDTO) request.getAttribute("queueCountDTO");

			Field[] fields = queueCountDTO.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (!field.isSynthetic()) {
					parameterCount++;
				}
			}
		}
	%>
</head>
<body>

<!-- Main navbar -->
<los:loader/>
<jsp:include page="header.jsp"/>
<!-- /main navbar -->
<%--<jsp:include page="notification.jsp"/>--%>
<!-- Page header -->
<div class="page-header">
	<div class="page-header-content d-lg-flex align-items-center justify-content-between pb-2">
		<div class="kt">
			<div class="border border-gray-300 border-dashed rounded min-w-125px py-3 px-4">
				<div class="d-flex align-items-center">
					<div class="d-flex flex-center w-25px h-25px rounded-3 bg-light-primary bg-opacity-90 me-3">
						<i class="ki-duotone ki-abstract-25 text-primary fs-3x"><span class="path1"></span><span class="path2"></span></i>
					</div>
					<div class="fs-8 fw-bold counted ms-2">Dashboard</div>
				</div>
			</div>
		</div>
		<div class="input-container float-right mx-5 ">
			<div class="col-12 d-flex justify-content-end ">
				<div id="validationMessage" class="text-danger text-center mt-2" style="display: none;">
					Please enter a WI number.
				</div>
				<div class="searchBox" style="height: 51px;">
					<input class="searchInput" id="winum" type="text" name="" placeholder="Search Work Item">
					<button class="searchButton" id="submitBtn" href="#">
						<i class="ph-magnifying-glass"></i>
					</button>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- /page header -->

<!-- Page content -->
<div class="page-content pt-0">
	<!-- Sidebar container -->
	<div class="sidebar-container">
		<!-- Main sidebar -->
		<jsp:include page="sidebar.jsp"/>
		<!-- /main sidebar -->

		<!-- Insurance Calculator Card -->
		<div class="card insurance-calculator-card sidebar sidebar-main sidebar-expand-lg align-self-start sidebar-style">
			<!-- Card Header -->
			<div class="card-header bg-light border-bottom">
				<p class="card-title text-success fs-9">
					<i class="ph-calculator me-2 text-success"></i>
					Loan Protection Insurance Calculator
				</p>
			</div>
			<!-- Card Body -->
			<div class="card-body bg-white p-2">
				<div class="overflow-auto" style="max-height: 400px;">
					<!-- News Alerts Section -->
					<c:forEach items="${newsAlerts}" var="alert">
						<div class="d-flex align-items-center p-2 border-bottom">
							<div class="me-3">
								<div class="bg-light rounded p-1">
									<i class="${alert.iconClass} text-primary"></i>
								</div>
							</div>
							<div>
								<a href="${alert.hrefUrl}" class="text-body fw-semibold d-block">${alert.title}</a>
								<span class="text-muted fs-sm">${alert.subtitle}</span>
							</div>
						</div>
					</c:forEach>

					<!-- Insurance Links -->
					<div class="d-flex align-items-center p-2 border-bottom">
						<div class="me-3">
							<div class="bg-light rounded p-1">
								<i class="ki-outline ki-abstract-22 text-primary"></i>
							</div>
						</div>
						<div>
							<a target="_blank" href="/excel/DIGIT_COMBI_PRODUCT.xlsx" class="text-body fw-semibold d-block">Digit Insurance</a>
							<span class="text-muted fs-sm">Premium calculator</span>
						</div>
					</div>

					<div class="d-flex align-items-center p-2">
						<div class="me-3">
							<div class="bg-light rounded p-1">
								<i class="ki-outline ki-abstract-25 text-primary"></i>
							</div>
						</div>
						<div>
							<a target="_blank" href="/excel/SUPERSURAKSHA_CALCULATOR.XLSX" class="text-body fw-semibold d-block">Bajaj Allianz Super Suraksha</a>
							<span class="text-muted fs-sm">Premium calculator</span>
						</div>
					</div>

					<div class="d-flex align-items-center p-2">
                   		<div class="me-3">
                   			<div class="bg-light rounded p-1">
                   				<i class="ki-outline ki-abstract-25 text-primary"></i>
                   			</div>
                   		</div>
                   		<div>
                   			<a target="_blank" href="/excel/GCC_PA_Calculator_For_South_Indian_Bank.xls" class="text-body fw-semibold d-block">Star Health</a>
                   			<span class="text-muted fs-sm">Premium calculator</span>
                   		</div>
                   	</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Main content -->
	<div class="content-wrapper">
		<div class="content">
			<div class="dashboard row">
				<div class="kt">
					<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
						<div class="post d-flex flex-column-fluid" id="kt_post">
							<div id="kt_content_container" class="container" style="max-width: 1500px;">
								<div class="row g-5">
									<div class="col-xl-4 mb-xl-10">
										<div class="card card-flush h-xl-100">
											<div class="card-header rounded bgi-no-repeat bgi-size-cover bgi-position-y-top bgi-position-x-center align-items-start h-250px" style="background-image: url('assets/images/5img.jpeg') !important;" data-bs-theme="light">
												<h3 class="card-title align-items-start flex-column text-white pt-5">
													<span class="fw-bold fs-3 mb-3">My Tasks</span>
													<div class="fs-5 text-white">
														<span class="opacity-75">You have</span>
														<span class="position-relative d-inline-block">
                                                            <a href="#" id="total_task" class="link-white opacity-75-hover fw-bold d-block mb-1">${dashboardData.totalTasks} tasks</a>
                                                            <span class="position-absolute opacity-50 bottom-0 start-0 border-2 border-body border-bottom w-100"></span>
                                                        </span>
														<span class="opacity-75">to complete</span>
													</div>
												</h3>
											</div>
											<div class="card-body mt-n20" id="MainTaskList">
												<div class="mt-n20 position-relative">
													<div class="row g-3 g-lg-6">
														<c:forEach items="${accessibleMenus}" var="menu">
															<div class="col-12">
																<div class="bg-gray-100 bg-opacity-70 rounded-2 px-6 py-5" style="background: white !important; box-shadow: rgba(9, 30, 66, 0.25) 0px 4px 8px -2px, rgba(9, 30, 66, 0.08) 0px 0px 0px 1px;">
																	<a href="${menu.menuUrl}">
																		<div class="d-flex justify-content-between align-items-center">
																			<div class="d-flex align-items-center">
																				<i class="${menu.icon} text-success fs-2x"></i>
																			</div>
																			<div class="text-center flex-grow-1">
																				<span class="text-gray-500 fw-semibold fs-6">${menu.menuDesc}</span>
																			</div>
																			<div class="text-end">
																				<span class="text-gray-700 fw-bolder d-block fs-3 lh-1 ls-n1 mb-0" id="${menu.menuID}CountValue">${dashboardData.menuItemCounts[menu.menuID]}</span>
																			</div>
																		</div>
																	</a>
																</div>
															</div>
														</c:forEach>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="col-xl-8 mb-5 mb-xl-10" id="MainDetails">
										<div class="row g-5 h-100">
											<div class="card card-flush h-xl-100">
												<div class="card-header pt-7">
													<h3 class="card-title align-items-start flex-column">
														<span class="card-label fw-bold text-dark">Queue Details</span>
														<span class="text-gray-400 mt-1 fw-semibold fs-6">VLOS Application</span>
													</h3>
												</div>
												<div class="card-body">
													<div class="pe-6 me-n6">
														<div class="row">
															<c:forEach items="${dashboardData.dashboardItems}" var="item">
																<div class="col-4">
																	<div class="border border-dashed border-gray-300 rounded px-7 py-3 mb-3">
																		<div class="d-flex flex-stack mb-2">
																			<div class="me-3">
																				<div class="d-flex flex-center w-30px h-30px rounded-3 bg-light-yellow bg-opacity-90 mb-0">
																					<i class="${item.iconClass} text-yellow fs-2x">
																						<c:forEach begin="1" end="${item.spanCount}" var="i">
																							<span class="path${i}"></span>
																						</c:forEach>
																					</i>
																				</div>
																			</div>
																			<div class="m-0">
																				<div class="d-flex align-items-center">
																					<c:choose>
																						<c:when test="${item.countDirection == 'up'}">
																							<i class="ki-duotone ki-arrow-up fs-3 text-danger me-2">
																								<span class="path1"></span>
																								<span class="path2"></span>
																							</i>
																							<p class="text-danger fw-semibold fs-8">+${item.variation}</p>
																						</c:when>
																						<c:when test="${item.countDirection == 'down'}">
																							<i class="ki-duotone ki-arrow-down fs-3 text-success me-2">
																								<span class="path1"></span>
																								<span class="path2"></span>
																							</i>
																							<p class="text-success fw-semibold fs-8">${item.variation}</p>
																						</c:when>
																					</c:choose>
																					<div data-kt-countup="true" data-kt-countup-value="${item.count}" data-kt-countup-prefix="" data-kt-initialized="1" class="fs-4 fw-bold counted">${item.count}</div>
																				</div>
																			</div>
																		</div>
																		<div class="d-flex flex-stack">
                                                         <span class="text-gray-400 fw-bold">
                                                             <a class="text-gray-800 text-hover-primary fw-bold">${item.title}</a>
                                                         </span>
																		</div>
																	</div>
																</div>
															</c:forEach>
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
				</div>
			</div>
		</div>
	</div>
	<!-- /main content -->
</div>
<!-- /page content -->

<div class="btn-to-top">
	<button class="btn btn-secondary btn-icon rounded-pill" type="button"><i class="ph-arrow-up"></i></button>
</div>

<!-- Footer -->
<jsp:include page="footer.jsp"/>
<!-- /footer -->

<!-- Core JS files -->
<script src="assets/js/jquery.min.js"></script>
<script src="assets/js/bootstrap.bundle.min.js"></script>
<!-- /core JS files -->

<!-- Theme JS files -->
<script src="assets/js/app.js"></script>
<!-- /theme JS files -->

<!-- Notification System JS -->
<script src="assets/js/purify.min.js"></script>

<script>
	// Work Item search functionality
	document.addEventListener('DOMContentLoaded', function() {
		initializeWorkItemSearch();
	});

	function initializeWorkItemSearch() {
		const winumInput = document.getElementById("winum");
		const submitBtn = document.getElementById("submitBtn");
		const validationMessage = document.getElementById("validationMessage");

		function formatInput(input) {
			let value = input.value;
			const formattedPattern = /^VLR_\d{9}$/;
			if (formattedPattern.test(value)) {
				return;
			}
			let numericPart = value.replace(/\D/g, '');
			if (numericPart.length > 0) {
				value = 'VLR_' + numericPart.padStart(9, '0');
			}
			input.value = value;
		}

		function submitForm() {
			const winum = btoa(winumInput.value);
			if (winumInput.value.trim() === "") {
				validationMessage.style.display = "block";
			} else {
				validationMessage.style.display = "none";
				const form = document.createElement("form");
				form.method = "GET";
				form.action = "wisearch";
				const hiddenField = document.createElement("input");
				hiddenField.type = "hidden";
				hiddenField.name = "winum";
				hiddenField.value = winum;
				form.appendChild(hiddenField);
				document.body.appendChild(form);
				form.submit();
			}
		}

		if (winumInput) {
			winumInput.addEventListener('change', function() {
				formatInput(winumInput);
			});

			winumInput.addEventListener("keydown", function(event) {
				if (event.key === "Enter") {
					event.preventDefault();
					formatInput(winumInput);
					submitForm();
				}
			});
		}

		if (submitBtn) {
			submitBtn.addEventListener("click", function() {
				submitForm();
			});
		}
	}
</script>

<div id="congrats_modal" class="modal fade" tabindex="-1" aria-modal="true" data-bs-keyboard="false" data-bs-backdrop="static" role="dialog">
	<div class="modal-dialog modal-xl modal-dialog-centered">
		<div class="modal-content">
			<div class="modal-body">
				<!-- Modal content goes here -->
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-link" data-bs-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>
</body>
</html>