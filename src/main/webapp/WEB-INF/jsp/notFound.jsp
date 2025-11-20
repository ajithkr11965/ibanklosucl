<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
	<meta name="_csrf" content="${_csrf.token}">
	<meta name="_csrf_header" content="${_csrf.headerName}">
	<title>404 - Page Not Found</title>

	<!-- Global stylesheets -->
	<link href="assets/fonts/inter/inter.css" rel="stylesheet" type="text/css">
	<link href="assets/icons/phosphor/styles.min.css" rel="stylesheet" type="text/css">
	<link href="assets/icons/fontawesome/styles.min.css" rel="stylesheet" type="text/css">
	<link href="assets/css/ltr/all.min.css" id="stylesheet" rel="stylesheet" type="text/css">
	<link href="assets/css/custom/dashboard.css" rel="stylesheet" type="text/css">
	<link href="assets/css/style.bundle.prefixed.css" rel="stylesheet" type="text/css"/>
	<link href="assets/plugins/global/plugins.bundle.prefixed.css" rel="stylesheet" type="text/css"/>

	<style>
		.error-container {
			text-align: center;
			padding: 100px 0;
		}
		.error-code {
			font-size: 120px;
			font-weight: bold;
			color: #dc3545;
			margin-bottom: 20px;
		}
		.error-message {
			font-size: 24px;
			margin-bottom: 30px;
		}
		#countdown {
			font-size: 18px;
			font-weight: bold;
			color: #007bff;
			margin-top: 20px;
		}
	</style>
</head>
<body>

<!-- Main navbar -->
<jsp:include page="header.jsp"/>
<!-- /main navbar -->

<!-- Page content -->
<div class="page-content pt-0">

	<!-- Main sidebar -->
	<jsp:include page="sidebar.jsp"/>
	<!-- /main sidebar -->

	<!-- Main content -->
	<div class="content-wrapper">
		<div class="content">
			<div class="error-container">
				<img src="assets/images/15.png" alt="" height="150" width="150">
				<%if(request.getAttribute("erStatus").toString().equals("NoCustName"))
				{%>
				<div class="error-message">Oops! Work Item Data Incomplete.</div>
				<%
				}
				else
				{
				%>
				<div class="error-message">Oops! Work Item not found.</div>
				<%
					}

				%>
				<p>The item you are looking for might have been removed, had its name changed, or is temporarily unavailable.</p>
				<div id="countdown">Redirecting to Dashboard in 4 seconds...</div>
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

<script>
	// Countdown timer and redirect
	let secondsLeft = 4;
	const countdownElement = document.getElementById('countdown');

	function updateCountdown() {
		if (secondsLeft > 0) {
			countdownElement.textContent = `Redirecting to Dashboard in ${secondsLeft} second${secondsLeft != 1 ? 's' : ''}...`;
			secondsLeft--;
			setTimeout(updateCountdown, 1000);
		} else {
			window.location.href = 'dashboard';
		}
	}

	// Start the countdown when the page loads
	document.addEventListener('DOMContentLoaded', updateCountdown);
</script>

</body>
</html>
