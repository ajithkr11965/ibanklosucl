<%@ page import="com.sib.ibanklosucl.dto.Employee" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	String path=request.getAttribute("path")==null?"": request.getAttribute("path").toString();
	Employee employee = new Employee();

	boolean isSuperUser=false;

	if (request.getAttribute("employee") != null) {
		employee = (Employee) request.getAttribute("employee");
		isSuperUser=employee.isSuperUser();
	}

%>
<style>
	.header_bg {
		background-image: url('<%=path%>assets/images/5img.jpeg') !important;
		background-size: cover;
		height: 5rem;
	}
	.bredcrum-shadow {
		box-shadow: rgba(0, 0, 0, 0.1) 0px 4px 6px -1px, rgba(0, 0, 0, 0.06) 0px 2px 4px -1px !important;
	}
	.cssbuttons-io-button {
  color: white;
  font-family: inherit;
  padding: 0.35em;
  padding-left: 1.2em;
  font-size: 13px;
  border-radius: 10em;
  border: none;
  letter-spacing: 0.05em;
  display: flex;
  align-items: center;
  overflow: hidden;
  position: relative;
  height: 2.8em;
  padding-right: 3.3em;
  cursor: pointer;
  text-transform: uppercase;
  font-weight: 500;
  box-shadow: 0 0 1.6em rgba(26, 52, 113, 0.3),0 0 1.6em hsla(191.2, 98.2%, 56.1%, 0);
  transition: all 0.6s cubic-bezier(0.23, 1, 0.320, 1);
  background: linear-gradient(135deg, #0e0e38 0%, #734262 100%);
  font-weight: normal;
}

.cssbuttons-io-button .icon_mel {
  background: white;
  margin-left: 1em;
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 2.2em;
  width: 2.2em;
  border-radius: 10em;
  right: 0.3em;
  transition: all 0.6s cubic-bezier(0.23, 1, 0.320, 1);
}

.cssbuttons-io-button:hover .icon_mel {
  width: calc(100% - 0.6em);
}

.cssbuttons-io-button .icon_mel svg {
  width: 1.1em;
  transition: transform 0.3s;
  color: #FF2020;
}

.cssbuttons-io-button:hover .icon_mel svg {
  transform: translateX(0.1em);
}

.cssbuttons-io-button:active .icon_mel {
  transform: scale(0.9);
}
</style>

<div class="navbar navbar-dark navbar-expand-lg navbar-static header_bg">
	<div class="container-fluid">
		<div class="d-flex d-lg-none me-2">
			<button type="button" class="navbar-toggler sidebar-mobile-main-toggle rounded-pill">
				<span class="ph-list"></span>
			</button>
		</div>
		<div class="navbar-brand flex-1 flex-lg-0">
			<a href="index.html" class="d-inline-flex align-items-center">
				<img src="assets/images/logo_icon.svg" alt="">
			</a>
		</div>
		<ul class="nav flex-row justify-content-end order-1 order-lg-2">

			<%if(isSuperUser){
				%>
			<li class="nav-item nav-item-dropdown-lg dropdown">
				<a href="switch" class="navbar-nav-link navbar-nav-link-icon rounded-pill">
					<i class="ph-detective "></i>
				</a>
			</li>
				<%

			}%>


<%--			<li class="nav-item ms-lg-2">--%>
<%--				<a href="#" class="navbar-nav-link navbar-nav-link-icon rounded-pill" id="allDashBoard" >--%>
<%--					<span class="ph-house-simple"></span>--%>
<%--				</a>--%>
<%--			</li>--%>
			<li class="nav-item ms-lg-2">
				<a href="#" class="navbar-nav-link navbar-nav-link-icon rounded-pill" data-bs-toggle="offcanvas" data-bs-target="#notifications">
					<span class="ph-bell"></span>
					<span id="notif_count" class="badge bg-yellow text-black position-absolute top-0 end-0 translate-middle-top zindex-1 rounded-pill mt-1 me-1">0</span>
				</a>
			</li>

			<li class="nav-item nav-item-dropdown-lg dropdown ms-lg-2">
				<a href="#" class="navbar-nav-link align-items-center rounded-pill p-1" data-bs-toggle="dropdown" aria-expanded="false">
					<div class="status-indicator-container">
						<img src="<%=employee.getImageUrl()%>" class="w-32px h-32px rounded-pill" alt="">
						<span class="status-indicator bg-success"></span>
					</div>
					<span class="d-none d-lg-inline-block mx-lg-2"><%=employee.getPpcName()%></span>
				</a>

				<div class="dropdown-menu dropdown-menu-end">
					<a href="<%=path%>profile" class="dropdown-item">
						<span class="ph-user-circle me-2"></span>
						My profile
					</a>

					<div class="dropdown-divider"></div>

					<a href="<%=path%>logout" class="dropdown-item">
						<span class="ph-sign-out me-2"></span>
						Logout
					</a>
				</div>
			</li>
		</ul>
	</div>
</div>

<div class="page-header page-header-light shadow mb-3 bredcrum-shadow">
	<div class="page-header-content d-lg-flex">
		<div class="d-flex">
			<div class="breadcrumb py-2">
				<a href="#" class="breadcrumb-item"><span class="ph-house"></span></a>
				<a href="dashboard" class="breadcrumb-item">Home</a>
<%--				<span class="breadcrumb-item active">Dashboard</span>--%>
			</div>

			<a href="#breadcrumb_elements" class="btn btn-light align-self-center collapsed d-lg-none border-transparent rounded-pill p-0 ms-auto"
			   data-bs-toggle="collapse">
				<span class="ph-caret-down collapsible-indicator ph-sm m-1"></span>
			</a>
		</div>

		<div class="collapse d-lg-block ms-lg-auto" id="breadcrumb_elements">
			<div class="d-lg-flex mb-2 mb-lg-0">
				<a href="#" class="d-flex align-items-center text-body py-2">
					<span class="ph-lifebuoy me-2"></span>
					<%
						//String appNode = System.getenv("APP_NODE");
						String nodeName="";
						if(System.getenv("APP_NODE")!=null && !System.getenv("APP_NODE").isEmpty()){
							nodeName=System.getenv("APP_NODE");
						}

					%>
					<div class="text-center"><code><%=nodeName%></code></div>
				</a>
				<button id="allDashBoard" class="ms-4 mt-2 mb-2 cssbuttons-io-button">Los Platform<div class="icon_mel">
    <svg height="24" width="24" viewBox="0 0 24 24" xmlns=http://www.w3.org/2000/svg><path d="M0 0h24v24H0z" fill="none"></path><path d="M16.172 11l-5.364-5.364 1.414-1.414L20 12l-7.778 7.778-1.414-1.414L16.172 13H4v-2z" fill="currentColor"></path></svg>
  </div>
</button>

			</div>
		</div>
	</div>
</div>

<script>
	document.getElementById("allDashBoard").addEventListener("click", function (e) {
		e.preventDefault(); // Prevent default anchor behavior
		e.stopPropagation();
		showLoader();
		var timer = setTimeout(function () {
			fetch("api/generateToken", {
				method: "POST"
			})
					.then(response => response.json())
					.then(response => {
						clearTimeout(timer);
						window.location.href = response.redirectUrl + "?token=" + encodeURIComponent(response.token);
					})
					.catch(error => {
						clearTimeout(timer);
						hideLoader();
						alert("Error accessing application. Please try again.");
					});
		}, 300);
	});
</script>

<!-- Include JavaScript for WebSocket connection and updating notification count -->
<%--<script src="assets/js/sockjs.min.js"></script>--%>
<%--<script src="assets/js/stomp.min.js"></script>--%>
<%--<script type="text/javascript">--%>
<%--	var ppc = '<%= employee.getPpcno() %>';  // Use the employee's PPC name as the identifier--%>

<%--	var socket = new SockJS('ws');--%>
<%--	var stompClient = Stomp.over(socket);--%>

<%--	stompClient.connect({}, function (frame) {--%>
<%--		stompClient.subscribe('/user/' + ppc + '/queue/notifications', function (message) {--%>
<%--			var notification = JSON.parse(message.body);--%>
<%--			updateNotificationCount();--%>
<%--		});--%>
<%--	});--%>

<%--	function updateNotificationCount() {--%>
<%--		var ppc = '<%= employee.getPpcno() %>';--%>

<%--		fetch(`notifications/count?ppc=`+ppc, {--%>
<%--			headers: {--%>
<%--				'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')--%>
<%--			}--%>
<%--		})--%>
<%--				.then(response => response.json())--%>
<%--				.then(count => {--%>
<%--					document.getElementById("notif_count").textContent = count;--%>
<%--				})--%>
<%--				.catch(error => {--%>
<%--					console.error('Error fetching notification count:', error);--%>
<%--				});--%>
<%--	}--%>


<%--	// Optionally, load the initial notification count from the server when the page loads--%>
<%--	function loadInitialNotificationCount() {--%>
<%--		fetch('notifications/count?ppc=' + ppc)--%>
<%--				.then(response => response.json())--%>
<%--				.then(count => {--%>
<%--					document.getElementById("notif_count").textContent = count;--%>
<%--				});--%>
<%--	}--%>

<%--	loadInitialNotificationCount();  // Load the initial count when the page loads--%>
<%--</script>--%>
