<%@ taglib prefix="los" uri="http://www.siblos.com/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page import="java.util.Optional" %>
<%@ page import="com.sib.ibanklosucl.dto.dashboard.QueueCountDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.lang.reflect.Field" %>

<!DOCTYPE html>
<html lang="en" dir="ltr" class="custom-scrollbars">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="_csrf" content="">
	<meta name="_csrf_header" content="">
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
		/*.searchBox {*/
		/*	display: flex;*/
		/*	max-width: 230px;*/
		/*	align-items: center;*/
		/*	justify-content: space-between;*/
		/*	gap: 8px;*/
		/*	background: #09092d;*/
		/*	border-radius: 50px;*/
		/*	position: relative;*/
		/*}*/

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
		/*hover effect*/
		button:hover {
			color: #fff;
			background-color: #1A1A1A;
			box-shadow: rgba(0, 0, 0, 0.5) 0 10px 20px;
			transform: translateY(-3px);
		}
		/*button pressing effect*/
		button:active {
			box-shadow: none;
			transform: translateY(0);
		}

		/*.searchInput {*/
		/*	border: none;*/
		/*	background: none;*/
		/*	outline: none;*/
		/*	color: white;*/
		/*	font-size: 15px;*/
		/*	padding: 24px 46px 24px 26px;*/
		/*}*/
        .searchBox {
            display: flex;
            max-width: 230px;
            align-items: center;
            justify-content: space-between;
            gap: 8px;
            background: #fafaff;
            border-radius: 50px;
            position: relative;
            border: 1px solid #dedfff;
        }

        .searchInput {
            border: none;
            background: none;
            outline: none;
            color: #131313;
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
			height:30vh;
		}
		.marquee1 {
		// top: 11em;
			position: relative;
			box-sizing: border-box;
			animation: marquee1 30s linear infinite;
			margin: auto 5px;
		}
		/*.card {*/
		/*	border: none;*/
		/*	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);*/
		/*	border-radius: 10px;*/
		/*	transition: transform 0.3s ease;*/
		/*	background-color: #ffffff;*/
		/*	margin-bottom: 20px;*/
		/*}*/
		/*.card:hover {*/
		/*	transform: translateY(-5px);*/
		/*}*/
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


		.scroll-container
		{
			height:328px;
			overflow: hidden;
			position: relative;
		}
		.scroll-content
		{
			position: absolute;
			animation: marquee1 20s linear infinite;
			animation-play-state: running;

		}
		.scroll-container:hover .scroll-content
		{
			animation-play-state: paused;
		}
		@Keyframes scrollSeuqential
		{
			0%
			{
				transform:translateY(100%);
			}
			100%
			{
				transform:translateY(-100%);
			}
		}

		@keyframes marquee1 {

		0%   { -webkit-transform: translate(0, 70%); }
		100% { -webkit-transform: translate(0, -100%); }
		}


	</style>
	<script>

		// Event listener to detect 'Enter/Return' key press on the input field
		document.addEventListener("DOMContentLoaded", function() {
			const winumInput = document.getElementById("winum");
			const submitBtn = document.getElementById("submitBtn");
			const validationMessage = document.getElementById("validationMessage");
			function formatInput(input) {
				let value = input.value;

				// Check if the input is already in the correct format 'VLR_' followed by exactly 9 digits
				const formattedPattern = /^VLR_\d{9}$/;
				if (formattedPattern.test(value)) {
					return; // Do nothing if already formatted
				}

				// Remove any non-numeric characters and get the numeric part
				let numericPart = value.replace(/\D/g, '');

				// If there is a number, format it with 'VLR_' prefix and pad it to 9 digits
				if (numericPart.length > 0) {
					value = 'VLR_' + numericPart.padStart(9, '0');
				}

				// Set the formatted value back to the input field
				input.value = value;
			}


			// Function to submit the form with the input value
			function submitForm() {
				const winum = btoa(document.getElementById("winum").value);
				if (winumInput.value.trim() === "") {
					validationMessage.style.display = "block"; // Show validation message if input is empty
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



			// Listen for input change event
			winumInput.addEventListener('change', function() {
				formatInput(winumInput);
			});

			// Listen for enter key press
			winumInput.addEventListener("keydown", function(event) {
				if (event.key === "Enter") {
					event.preventDefault(); // Prevent default form submission behavior
					formatInput(winumInput);
					submitForm(); // Trigger form submission
				}
			});

			// Listen for button click event
			submitBtn.addEventListener("click", function() {
				submitForm(); // Trigger form submission on button click
			});
		});

	</script>

</head>
<body>
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
						<i class="ki-duotone ki-abstract-25 text-primary fs-3x"><span class="path1"></span><span class="path2"></span></i>                </div>
					<div class="fs-8  fw-bold counted ms-2">Dashboard</div>
				</div>
			</div>
		</div>
		<div class="input-container float-right mx-5 z-index-3">
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

	<!-- Main sidebar -->
	<jsp:include page="sidebar.jsp"/>

	<!-- /main sidebar -->

	<!-- Main content -->
	<div class="content-wrapper">

		<div class="content">

			<div class="dashboard row">

				<div class="kt">
					<div id="kt_app_content" class="app-content flex-column-fluid" style="padding-top: 0">
						<!--begin::Row-->
						<div class="row">
							<!--begin::Col-->
							<div class="col-xl-8">
								<div style="max-height: 100px !important;background-image: url('assets/images/5.jpeg') !important;" dir="ltr" class="card bgi-position-y-bottom bgi-position-x-end bgi-no-repeat bgi-size-cover min-h-150px bg-body mb-5 mb-xl-7">
									<!--begin::Body-->
									<div class="card-body d-flex flex-column justify-content-center ps-lg-10">
										<!--begin::Title-->
										<h3 class="text-white fs-2 fw-bold mb-7">My Task<br>You have <span class="position-relative d-inline-block">
										  <a href="#" id="total_task" class="link-white opacity-75-hover fw-bold d-block mb-1">${dashboardData.totalTasks} tasks</a>
										  <span class="position-absolute opacity-50 bottom-0 start-0 border-2 border-body border-bottom w-100"></span>
											</span> to complete
										</h3>
										<!--end::Title-->
										<!--begin::Action-->
										<!--begin::Action-->
									</div>
									<!--end::Body-->
								</div>
								<!--begin::Misc Widget 1-->
								<div class="row mb-5 mb-xl-8 g-5 g-xl-8">
									<c:forEach items="${accessibleMenus}" var="menu">
										<!--begin::Col-->
										<div class="col-4">
											<!--begin::Items-->
											<div class="bg-gray-100 bg-opacity-70 rounded-2 px-6 py-5" style="background: white !important;box-shadow: rgba(9, 30, 66, 0.25) 0px 4px 8px -2px, rgba(9, 30, 66, 0.08) 0px 0px 0px 1px;">
												<!--begin::Symbol-->
												<div class="d-flex flex-stack mb-3">
													<!--begin::Wrapper-->
													<div class="me-3">
														<!--begin::Icon-->
														<div class="d-flex flex-center w-30px h-30px rounded-3 bg-light-primary bg-opacity-90 mb-0">
															<i class="${menu.icon} text-primary fs-2x menu_icon"></i>
															<span class="path1"></span>
															<span class="path2"></span>
														</div>
														<!--end::Icon-->
													</div>
													<!--end::Wrapper-->
													<!--begin::Action-->
													<div class="m-0">
														<!--begin::Menu-->
														<div data-kt-countup="true" data-kt-countup-value="${queueCountDTO.getCountForMenu(menu.menuID) != null ? queueCountDTO.getCountForMenu(menu.menuID) : '0'}" data-kt-initialized="1" class="fs-5 fw-bold menu_count">
																${queueCountDTO.getCountForMenu(menu.menuID) != null ? queueCountDTO.getCountForMenu(menu.menuID) : '0'}
														</div>
														<!--end::Menu-->
													</div>
													<!--end::Action-->
												</div>
												<!--end::Symbol-->
												<!--begin::Stats-->
												<div class="d-flex flex-stack">
													<!--begin::Name-->
													<span class="text-gray-400 fw-bold">
														<a href="${menu.menuUrl}" class="text-gray-800 text-hover-primary fw-bold menu_url">${menu.menuDesc}</a>
													</span>
													<!--end::Name-->
												</div>
												<!--end::Stats-->
											</div>
											<!--end::Items-->
										</div>
										<!--end::Col-->
									</c:forEach>


									<!--end::Col-->
								</div>
								<!--end::Misc Widget 1-->
								<!--begin::List Widget 5-->
								<!--end: List Widget 5-->

							</div>
							<!--end::Col-->
							<!--begin::Col-->
							<div class="col-xl-4 ps-xl-12">
								<!--begin::Engage widget 1-->
								<div class="card card-flush mb-xl-8" style="height: 500px !important;">
									<!--begin::Header-->
									<div class="card-header py-7">
										<!--begin::Statistics-->
										<div class="m-0">
											<!--begin::Heading-->
											<div class="d-flex align-items-center mb-2">
												<!--begin::Title-->
												<!--end::Title-->
												<!--begin::Badge-->
												<span class="badge badge-light-danger text-wrap fs-base">Loan Protection Insurance premium Calculator</span>
												<!--end::Badge-->
											</div>
											<!--end::Heading-->
											<!--begin::Description-->
<%--											<span class="fs-6 fw-semibold text-gray-400">Latest Updates</span>--%>
											<!--end::Description-->
										</div>
										<!--end::Statistics-->
										<!--begin::Toolbar-->
										<!--end::Toolbar-->
									</div>
									<!--end::Header-->
									<!--begin::Body-->
									<div class="card-body pt-0">
										<!--begin::Items-->
										<div class="scroll-container" id="scroll-container">
											<div class="scroll-contents" id="scroll-content">
										<div class="mb-0">
												<!--begin::Item-->
												<c:forEach items="${newsAlerts}" var="alert">
													<!--begin::Separator-->
													<div class="separator separator-dashed my-3"></div>
													<!--end::Separator-->
													<!--begin::Item-->
													<div class="d-flex flex-stack">
														<!--begin::Section-->
														<div class="d-flex align-items-center me-5">
															<!--begin::Symbol-->
															<div class="symbol symbol-20px me-5">
                <span class="symbol-label">
                    <i class="${alert.iconClass}"></i>
                </span>
															</div>
															<!--end::Symbol-->
															<!--begin::Content-->
															<div class="me-5">
																<!--begin::Title-->
																<a href="${alert.hrefUrl}" class="text-gray-800 fw-bold text-hover-primary fs-8">${alert.title}</a>
																<!--end::Title-->
																<!--begin::Desc-->
																<span class="text-gray-400 fw-semibold fs-9 d-block text-start ps-0">${alert.subtitle}</span>
																<!--end::Desc-->
															</div>
															<!--end::Content-->
														</div>
														<!--end::Section-->
														<!--begin::Wrapper-->
														<!--end::Wrapper-->
													</div>
													<!--end::Item-->
												</c:forEach>

												<!--begin::Separator-->
												<div class="separator separator-dashed my-3"></div>
												<!--end::Separator-->
												<!--begin::Item-->
												<div class="d-flex flex-stack">
													<!--begin::Section-->
													<div class="d-flex align-items-center me-5 mb-4">
														<!--begin::Symbol-->
														<div class="symbol symbol-30px me-5">
															  <span class="symbol-label">
																<i class="ki-outline ki-abstract-22 fs-3 text-success"></i>
															  </span>
														</div>
														<!--end::Symbol-->
														<!--begin::Content-->
														<div class="me-5">
															<!--begin::Title-->
															<a   target="_blank" href="/excel/DIGIT_COMBI_PRODUCT.xlsx"  class="text-gray-800 fw-bold text-hover-primary fs-8">Digit Insurance</a>
															<!--end::Title-->
															<!--begin::Desc-->
<%--															<span class="text-gray-400 fw-semibold fs-9 d-block text-start ps-0">Download</span>--%>
															<!--end::Desc-->
														</div>
														<!--end::Content-->
													</div>

												</div>
												<!--end::Item-->

												<!--begin::Item-->
												<div class="d-flex flex-stack">
													<!--begin::Section-->
													<div class="d-flex align-items-center me-5">
														<!--begin::Symbol-->
														<div class="symbol symbol-30px me-5">
															  <span class="symbol-label">
																<i class="ki-outline ki-abstract-25 fs-3 text-success"></i>
															  </span>
														</div>
														<!--end::Symbol-->
														<!--begin::Content-->
														<div class="me-5">
															<!--begin::Title-->
															<a target="_blank" href="/excel/GCC_PA_Calculator_For_South_Indian_Bank.xls" class="text-body fw-semibold d-block">Star Health</a>
															<!--end::Title-->
															<!--begin::Desc-->
<%--															<span class="text-gray-400 fw-semibold fs-9 d-block text-start ps-0">Download</span>--%>
															<!--end::Desc-->
														</div>
														<!--end::Content-->
													</div>

												</div>
												<!--end::Item-->
                                                <!--begin::Item-->
												<div class="d-flex flex-stack">
													<!--begin::Section-->
													<div class="d-flex align-items-center me-5">
														<!--begin::Symbol-->
														<div class="symbol symbol-30px me-5">
															  <span class="symbol-label">
																<i class="ki-outline ki-abstract-25 fs-3 text-success"></i>
															  </span>
														</div>
														<!--end::Symbol-->
														<!--begin::Content-->
														<div class="me-5">
															<!--begin::Title-->
															<a  target="_blank" href="/excel/SUPERSURAKSHA_CALCULATOR.XLSX" class="text-gray-800 fw-bold text-hover-primary fs-8">Bajaj Allianz Super Suraksha</a>
															<!--end::Title-->
															<!--begin::Desc-->
<%--															<span class="text-gray-400 fw-semibold fs-9 d-block text-start ps-0">Download</span>--%>
															<!--end::Desc-->
														</div>
														<!--end::Content-->
													</div>

												</div>
												<!--end::Item-->

											<!--begin::Item-->
											<div class="d-flex flex-stack">
												<!--begin::Section-->
												<div class="d-flex align-items-center me-5">
													<!--begin::Symbol-->
													<div class="symbol symbol-30px me-5">
															  <span class="symbol-label">
																<i class="ki-outline ki-abstract-25 fs-3 text-success"></i>
															  </span>
													</div>
													<!--end::Symbol-->
													<!--begin::Content-->
													<div class="me-5">
														<!--begin::Title-->
														<a  target="_blank" href="/excel/Niva_Bupa_Calculator.xlsx" class="text-gray-800 fw-bold text-hover-primary fs-8">Niva Bupa Health Insurance </a>
														<!--end::Title-->
														<!--begin::Desc-->
														<%--															<span class="text-gray-400 fw-semibold fs-9 d-block text-start ps-0">Download</span>--%>
														<!--end::Desc-->
													</div>
													<!--end::Content-->
												</div>

											</div>
											<!--end::Item-->

											</div>
											</div>
										</div>
										<!--end::Items-->
									</div>
									<!--end::Body-->
								</div>
								<!--end::Engage widget 1-->
								<!--begin::Row-->
								<!--end::Row-->
								<!--begin::Tables Widget 5-->
								<!--end::Tables Widget 5-->
								<!--begin::Row-->
								<!--end::Row-->
							</div>
							<!--end::Col-->
						</div>
						<!--end::Row-->
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
	    <script src="assets/js/custom/performance-logging.js"></script>

</body>
</html>
