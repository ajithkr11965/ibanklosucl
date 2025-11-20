<%-- Created by IntelliJ IDEA. User: SIBL11965 Date: 06-05-2024 Time: 19:11 To change this template use File | Settings
    | File Templates. --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<!--begin::Head-->

<head>
	<title>SIB - LOS</title>
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	<meta property="og:locale" content="en_US" />
	<meta property="og:type" content="article" />
	<meta property="og:title" content="SIB" />
	<meta property="og:site_name" content="SIB" />
	<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />

	<!--begin::Global Stylesheets Bundle(mandatory for all pages)-->
	<link href="assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
	<link href="assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
	<link href="assets/css/custom.css" rel="stylesheet" type="text/css" />
	<!--end::Global Stylesheets Bundle-->
</head>
<!--end::Head-->
<!--begin::Body-->

<body id="kt_body" class="app-blank">
<!--begin::Root-->
<div class="d-flex flex-column flex-root" id="kt_app_root">
	<!--begin::Authentication - Sign-up -->
	<div class="d-flex flex-column flex-lg-row flex-column-fluid">

		<!--begin::Aside-->

		<div class="d-flex flex-lg-row-fluid w-lg-50 bgi-size-cover bgi-position-center  body-img ">
			<!--begin::Content-->
			<div class="d-flex flex-column flex-center py-7 py-lg-15 px-5 px-md-15 w-100">
				<div class="">
					<div class="parent" style="position: relative;top: -60px;left: 0;">
						<img class="image1" alt="excel_logo" style="height: 660px;/*! width: 760px; */position: relative;top: 0;left: 0;" src="assets/media/misc/lgmainpd.png">
						<img class="image2" id="logo" alt="excel_logo" style="height: 280px; width: 230px;position: absolute;top: 289px;left: 153px;" src="assets/media/misc/powerdrive.gif">
					</div>

				</div>

				<!--begin::Logo-->

				<!--end::Logo-->
				<!--begin::Image-->
				<!--end::Image-->

				<!--end::Text-->
				<div class="area">
					<ul class="circles">
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
					</ul>
				</div>
				<div class="area">
					<ul class="circles">
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
						<li></li>
					</ul>
				</div>

			</div>
			<!--end::Content-->
		</div>
		<!--end::Aside-->

		<!--begin::Body-->
		<div class="d-flex flex-column flex-lg-row-fluid w-lg-50 p-10  z-999  body-img-lg">
			<!--begin::Form-->
			<div class="d-flex flex-center flex-column flex-lg-row-fluid">
				<!--begin::Wrapper-->
				<div class="w-lg-500px p-10 card-login card">
					<!--begin::Form-->
					<form class="form w-100 fv-plugins-bootstrap5 fv-plugins-framework"
						  novalidate="novalidate" id="kt_sign_up_form" method="POST" action="loginauth">
						<div class="py-15">
							<!--begin::Form-->

							<!--begin::Body-->
							<div class="card-body">
								<!--begin::Heading-->
								<div class="text-start mb-10">
									<!--begin::Title-->
									<img alt="Logo" src="assets/media/sib/logo.png" class="h-60px h-lg-50px mb-4">
									<h2 data-kt-translate="sign-in-title" class="text-dark mb-3 fs-2x">SIB PowerDrive</h2>
									<!--end::Title-->
									<!--begin::Text-->
									<div class="text-gray-400 fw-semibold fs-6"
										 data-kt-translate="general-desc">Get the loan best suited for your needs</div>
									<!--end::Link-->
								</div>
								<!--begin::Heading-->

								<!--begin::Input group=-->

								<div class="fv-row mb-8 fv-plugins-icon-container">

									<!--begin::UserName-->
									<div
											class="fv-row input-group mb-5 fv-plugins-icon-container has-validation">
                                                    <span style="border: white;" class="input-group-text" id="basic-addon1">
                                    <i class="ki-duotone ki-user  text-danger fs-1" style="color: #4c346a !important;"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                                    </span>
										<input type="text" class="form-control text-uppercase" placeholder="AD Username" minlength="10" maxlength="10" name="userName" autocomplete="off" aria-describedby="basic-addon1" value="" style="background-color: #f9f9f9;border: white;">
										<div class="fv-plugins-message-container invalid-feedback"></div>
									</div>
									<!--end::UserName-->

									<div
											class="fv-plugins-message-container fv-plugins-message-container--enabled invalid-feedback">
									</div>
								</div>

								<!--end::Input group=-->

								<div class="fv-row mb-7 fv-plugins-icon-container">


									<!--begin::Password-->


									<div
											class="fv-row input-group mb-5 fv-plugins-icon-container has-validation">
                                                    <span  style="border: white;" class="input-group-text" id="basic-addon2">
										<i class="ki-duotone ki-shield-tick   text-danger fs-1" style="color: #4c346a !important;"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
                                                    </span>
										<input type="password" placeholder="Password" name="password" id="password" autocomplete="off" class="form-control" value="sib@1209#" style="background-color: #f9f9f9;border: white;">
										<div class="fv-plugins-message-container invalid-feedback"></div>
									</div>


									<!--end::Password-->

									<div
											class="fv-plugins-message-container fv-plugins-message-container--enabled invalid-feedback">
									</div>
								</div>

								<!--end::Input group=-->
								<%if(request.getAttribute("error")!=null) {%>
									<div style="color:red;" class="p-2 m-2">
										<%=request.getAttribute("error")%>
									</div>
								<%}%>
								<!--begin::Wrapper-->
								<!--end::Wrapper-->
								<!--begin::Actions-->
								<div class="d-flex justify-content-center">

									<!--begin::Submit-->
									<button id="kt_sign_up_submit" class="btn btn-primary me-2 flex-shrink-0" style="background: linear-gradient(150deg, rgb(90, 65, 115) 15%, #44315c 70%, #482462 94%);width: 50%;">

										<!--begin::Indicator label-->
										<span class="indicator-label"  data-kt-translate="sign-in-submit">Login</span>
										<!--end::Indicator label-->
										<!--begin::Indicator progress-->
										<span class="indicator-progress">
                                                <span data-kt-translate="general-progress">Please wait...</span>
                                                <span class="spinner-border spinner-border-sm align-middle ms-2"></span>
                                </span>
										<!--end::Indicator progress-->
									</button>
									<!--end::Submit-->
								</div>
								<!--end::Submit button-->
					</form>
					<!--end::Form-->
				</div>
				<!--end::Wrapper-->
			</div>
			<!--end::Form-->
			<!--begin::Footer-->
			<div class=" d-flex  justify-content-center">

				<!--begin::Links-->
				<div class=" d-flex  justify-content-center">
					<%
						String nodeName=System.getenv("APP_NODE")==null?"":": "+System.getenv(" APP_NODE");
					%>
					<div class="text-center"><code>V1.0 <%=nodeName%></code></div>

				</div>
				<!--end::Links-->
			</div>
			<!--end::Footer-->
		</div>
		<!--end::Body-->


	</div>
	<!--end::Authentication - Sign-up-->
</div>
<!--end::Root-->
<!--begin::Javascript-->
<script>
	var hostUrl = "assets/";
</script>
<!--begin::Global Javascript Bundle(mandatory for all pages)-->
<script src="assets/plugins/global/plugins.bundle.js"></script>
<script src="assets/js/scripts.bundle.js"></script>
<!--end::Global Javascript Bundle-->
<!--begin::Custom Javascript(used for this page only)-->
<script src="assets/js/custom/authentication/login.js"></script>
<script src="assets/js/custom/common/utils.js"></script>

<!--end::Custom Javascript-->
<!--end::Javascript-->


</body>
<!--end::Body-->

</html>