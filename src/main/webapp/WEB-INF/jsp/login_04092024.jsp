<%--
  Created by IntelliJ IDEA.
  User: SIBL11965
  Date: 06-05-2024
  Time: 19:11
  To change this template use File | Settings | File Templates.
--%>
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
	<meta name="_csrf" content="${_csrf.token}"/>
	<meta name="_csrf_header" content="${_csrf.headerName}"/>

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
		<div class="d-flex flex-lg-row-fluid w-lg-50 bgi-size-cover bgi-position-center  body-img "  >
			<!--begin::Content-->
			<div class="d-flex flex-column flex-center py-7 py-lg-15 px-5 px-md-15 w-100">
				<!--begin::Logo-->
				<a href="#" class="mb-0 mb-lg-12">
					<img alt="Logo" src="assets/media/sib/logo_light.png" class="h-60px h-lg-75px" />
				</a>
				<!--end::Logo-->
				<!--begin::Image-->
				<!--end::Image-->
				<!--begin::Title-->
				<h1 class="d-none d-lg-block text-white fs-2qx fw-bolder text-center mb-7">Get the loan best suited for your needs</h1>
				<!--end::Title-->
				<!--begin::Text-->
				<div class="d-none d-lg-block text-white fs-base text-center">Millions of Indians today are upwardly mobile, and on the move. Many are looking at having their own car to reach their destination on time and with comfort. Now, the years of waiting and saving are over. Our <a href="#" class="opacity-75-hover text-warning fw-bold me-1">Loan</a> will bring that dream of owning a vehicle within your reach in just a few minutes
					the blogger</a>.</div>
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
		<div class="d-flex flex-column flex-lg-row-fluid w-lg-50 p-10  z-999">
			<!--begin::Form-->
			<div class="d-flex flex-center flex-column flex-lg-row-fluid">
				<!--begin::Wrapper-->
				<div class="w-lg-500px p-10">
					<!--begin::Form-->
					<form class="form w-100" novalidate="novalidate" id="kt_sign_up_form"   method="POST" action="loginauth">
						<input type="hidden" 	name="${_csrf.parameterName}" 	value="${_csrf.token}"/>

						<!--begin::Heading-->
						<div class="text-center mb-11">
							<!--begin::Title-->
							<div class="d-none d-lg-block mb-5" >
								<img alt="Logo" src="assets/media/sib/logo.png" class="h-60px h-lg-75px" /></div>
							<h1 class="text-dark fw-bolder mb-3">Get the loan best suited for your needs</h1>
							<!---->
							<!--end::Title-->
							<!--begin::Subtitle-->
							<!--<div class="text-gray-500 fw-semibold fs-6">Get the loan best suited for your needs</div>-->
							<!--end::Subtitle=-->
						</div>
						<!--begin::Heading-->

						<div class="fv-row input-group mb-5">
									<span class="input-group-text" id="basic-addon1">
										<i class="ki-duotone ki-user  text-danger fs-1"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
									</span>
							<input type="text" class="form-control text-uppercase"  placeholder="AD Username" minlength="10" maxlength="10" name="userName" autocomplete="off" aria-describedby="basic-addon1" value=""/>
						</div>

						<div class="fv-row input-group mb-5">
									<span class="input-group-text" id="basic-addon2">
										<i class="ki-duotone ki-shield-tick   text-danger fs-1"><span class="path1"></span><span class="path2"></span><span class="path3"></span></i>
									</span>
							<input type="password" placeholder="Password" name="password" autocomplete="off" class="form-control" value="sib@1209#">
						</div>

						<!--begin::Accept-->

						<!--end::Accept-->
						<!--begin::Submit button-->
						<%if(request.getAttribute("error")!=null) {%>

						<div style="color:red;" class="p-2 m-2">
							<%=request.getAttribute("error")%>
						</div>

						<%}%>
						<div class="d-grid mb-10">

							<button type="submit" id="kt_sign_up_submit"  class="btn btn-danger   bg-sib" >
								<!--begin::Indicator label-->
								<span class="indicator-label"><i class="bi bi-check2-square fs-4 me-2s"></i>Go</span>
								<!--end::Indicator label-->
								<!--begin::Indicator progress-->
								<span class="indicator-progress">Please wait...
										<span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
								<!--end::Indicator progress-->
							</button>
						</div>
						<!--end::Submit button-->

					</form>
					<!--end::Form-->
				</div>
				<!--end::Wrapper-->
			</div>
			<!--end::Form-->
			<!--begin::Footer-->
			<div class="w-lg-500px d-flex flex-stack px-10 mx-auto justify-content-center">

				<!--begin::Links-->
				<div class="d-flex fw-semibold text-primary fs-base gap-5">
					<a href="#" class="text-sib" target="_blank">Terms</a>
					<a href="mailto:swd@sib.co.in" class="text-sib"  target="_blank">Contact Us</a>
					<%
						String nodeName = System.getenv("APP_NODE")==null?"":": "+System.getenv("APP_NODE");
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
<script>var hostUrl = "assets/";</script>
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
